package vip.stqr.STShortURL.service.impl;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import vip.stqr.STShortURL.entity.UrlMap;
import vip.stqr.STShortURL.mapper.UrlMapMapper;
import vip.stqr.STShortURL.service.UrlService;
import vip.stqr.STShortURL.toolkit.HashUtils;
import vip.stqr.STShortURL.toolkit.HutoolRedisUtil;

/**
 * TODO
 *
 * @author 曙天
 * @since 2022-09-24 11:59
 */
@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    private UrlMapMapper urlMapMapper;
    /**
     * 最近使用的短链接缓存过期时间(秒)
     */
    private static final long TIMEOUT = 10 * 60;
    /**
     * 自定义长链接防重复字符串
     */
    private static final String DUPLICATE = "*";

    /**
     * 创建布隆过滤器
     */
    private static final BitMapBloomFilter FILTER = BloomFilterUtil.createBitMap(10);
    @Override
    public String getLongUrlByShortUrl(String shortURL) {
        //查找Redis中是否有缓存
        RedisDS redisDS = HutoolRedisUtil.getRedisDS("local");
        Jedis jedis = redisDS.getJedis();
        String longURL = redisDS.getStr(shortURL);
        if (longURL != null) {
            //有缓存，延迟缓存时间
            jedis.expire(shortURL, TIMEOUT);
            return longURL;
        }
        //Redis没有缓存，从数据库查找
        longURL = urlMapMapper.selectOne(Wrappers.<UrlMap>lambdaQuery()
                .select(UrlMap::getShortUrl)
                .eq(UrlMap::getShortUrl, shortURL)).getShortUrl();
        if (longURL != null) {
            //数据库有此短链接，添加缓存
            redisDS.setStr(shortURL,longURL);
            jedis.expire(shortURL, TIMEOUT);
        }
        return longURL;
    }

    @Override
    public String saveUrlMap(String shortURL, String longURL, String originalURL) {

        //保留长度为1的短链接
        if (shortURL.length() == 1) {
            longURL += DUPLICATE;
            shortURL = saveUrlMap(HashUtils.hashToBase62(longURL), longURL, originalURL);
        }

        RedisDS redisDS = HutoolRedisUtil.getRedisDS("local");
        Jedis jedis = redisDS.getJedis();
        if (FILTER.contains(shortURL)) {
            //存在，从Redis中查找是否有缓存
            String redisLongUrl = redisDS.getStr(shortURL);
            if (originalURL.equals(redisLongUrl)) {
                //Redis有缓存，重置过期时间
                jedis.expire(shortURL, TIMEOUT);
                return shortURL;
            }
            //没有缓存，在长链接后加上指定字符串，重新hash
            longURL += DUPLICATE;
            shortURL = saveUrlMap(HashUtils.hashToBase62(longURL), longURL, originalURL);
        }else {
            //不存在，直接存入数据库
            try {
                UrlMap urlMap = new UrlMap();
                urlMap.setLongUrl(originalURL);
                urlMap.setShortUrl(shortURL);
                urlMapMapper.insert(urlMap);
                FILTER.add(shortURL);
                //添加缓存
                redisDS.setStr(shortURL,originalURL);
                jedis.expire(shortURL, TIMEOUT);
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException) {
                    //数据库已经存在此短链接，则可能是布隆过滤器误判，在长链接后加上指定字符串，重新hash
                    longURL += DUPLICATE;
                    shortURL = saveUrlMap(HashUtils.hashToBase62(longURL), longURL, originalURL);
                } else {
                    throw e;
                }
            }
        }
        return shortURL;
    }

    @Override
    public String saveUrlMap2(String longUrl, String createBy) {
        String originalUrl = longUrl;
        longUrl = longUrl + createBy;
        String shortUrl = HashUtils.hashToBase62(longUrl);

        //保留长度为1的短链接
        if (shortUrl.length() == 1) {
            longUrl += DUPLICATE;
            shortUrl = saveUrlMap2(originalUrl,createBy);
        }

        RedisDS redisDS = HutoolRedisUtil.getRedisDS("local");
        Jedis jedis = redisDS.getJedis();

        String redisLongUrl = redisDS.getStr(shortUrl);
        if (originalUrl.equals(redisLongUrl)) {
            //Redis有缓存，重置过期时间
            jedis.expire(shortUrl, TIMEOUT);
            return shortUrl;
        }



        return null;
    }

    @Override
    public void updateUrlViews(String shortURL) {
        // urlMapMapper.update();
    }
}
