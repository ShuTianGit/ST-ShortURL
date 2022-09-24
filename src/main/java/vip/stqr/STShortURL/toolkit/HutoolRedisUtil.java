package vip.stqr.STShortURL.toolkit;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import cn.hutool.setting.Setting;

import java.io.File;

/**
 * Description: redis工具类
 *<a href="https://blog.csdn.net/csdncjh/article/details/119825746">文章参考地址</a>
 * @author 曙天
 * @since 2022-09-13 18:40
 */
public class HutoolRedisUtil {
    private static Setting setting;
    {
        //自定义数据库Setting，更多实用请参阅Hutool-Setting章节
        setting = new Setting(new File("redis.setting"), CharsetUtil.CHARSET_UTF_8,false);
    }
    public static RedisDS getRedisDS(String groupName){
        return RedisDS.create(setting,groupName);
    }

}
