package vip.stqr.STShortURL.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * TODO
 *
 * @author 曙天
 * @since 2022-09-24 12:58
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.fillStrategy(metaObject, "createTime", new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.fillStrategy(metaObject, "updateTime", new Date());
    }
}
