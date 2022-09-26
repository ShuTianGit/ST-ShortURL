package vip.stqr.STShortURL.service.impl;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import vip.stqr.STShortURL.entity.UrlMap;
import vip.stqr.STShortURL.mapper.UrlMapMapper;
import vip.stqr.STShortURL.service.UrlService;
import vip.stqr.STShortURL.toolkit.HashUtils;
import vip.stqr.STShortURL.toolkit.HutoolRedisUtil;

import java.util.Objects;

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
    private static final String DUPLICATE = "ST";

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
                .select(UrlMap::getLongUrl)
                .eq(UrlMap::getShortUrl, shortURL)).getLongUrl();
        if (longURL != null) {
            //数据库有此短链接，添加缓存
            redisDS.setStr(shortURL,longURL);
            jedis.expire(shortURL, TIMEOUT);
        }
        return longURL;
    }

    @Override
    public String saveUrlMap(String longUrl, String createBy) {
        String originalUrl = longUrl;
        longUrl = longUrl + createBy;
        String shortUrl = HashUtils.hashToBase62(longUrl);

        //保留长度为1的短链接
        if (shortUrl.length() == 1) {
            createBy += IdUtil.simpleUUID();;
            saveUrlMap(originalUrl,createBy);
        }

        UrlMap dbUrlMap = urlMapMapper.selectOne(Wrappers.<UrlMap>lambdaQuery()
                .select(UrlMap::getShortUrl,UrlMap::getLongUrl,UrlMap::getCreateBy)
                .eq(UrlMap::getShortUrl, shortUrl));

        // 数据库中没有该短链接,直接生成
        if(dbUrlMap == null) {
            UrlMap urlMap = new UrlMap();
            urlMap.setLongUrl(originalUrl);
            urlMap.setShortUrl(shortUrl);
            urlMap.setCreateBy(createBy);
            urlMapMapper.insert(urlMap);
            return shortUrl;
        }

        // 短链接相同,长链接不同,说明不同长链接Hash到了一样的值,需要重新Hash
        if (Objects.equals(dbUrlMap.getShortUrl(), shortUrl) && !Objects.equals(dbUrlMap.getLongUrl(), originalUrl)){
            createBy += IdUtil.simpleUUID();;
            saveUrlMap(originalUrl,createBy);
        }

        // 短链接相同,长链接相同,不同创建人,需要重新Hash
        if(Objects.equals(dbUrlMap.getShortUrl(), shortUrl) && Objects.equals(dbUrlMap.getLongUrl(), originalUrl) && !Objects.equals(dbUrlMap.getCreateBy(), createBy)) {
            createBy += createBy + IdUtil.simpleUUID();;
            saveUrlMap(originalUrl,createBy);
        }

        return dbUrlMap.getShortUrl();
    }

    @Override
    public void updateUrlViews(String shortURL) {
        // urlMapMapper.update();
    }
}
