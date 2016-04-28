package com.redis;

import com.redis.config.RedisConfig;
import com.redis.service.RedisService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Title: MessageProducerTest
 * @Description:
 * @author: xian jie
 * @date: 2016-4-27 17:43
 * 杭州尚尚签网络科技有限公司
 * @version: 2.0
 */
public class MessageProducerTest {

    /**
     * set value
     */
    @Test
    public void setString() throws InterruptedException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(RedisConfig.class);
        RedisService redisService = ctx.getBean(RedisService.class);
        // 设置 key value 不带超时
        redisService.setString("key", "hello world!", null);

        System.out.println("key value: " + redisService.getString("key"));

        redisService.setString("key1", "key1", 5L);

        // 休眠6秒让当前key过期
        Thread.sleep(1000 * 6);

        System.out.println("key1 value: " + redisService.getString("key1"));
    }


    /**
     * 批量放入 string
     */
    @Test
    public void batchSetString() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(RedisConfig.class);
        RedisService redisService = ctx.getBean(RedisService.class);

        Map<String, String> keyValueMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            keyValueMap.put("key" + i, "value" + i);
        }

        redisService.setString(keyValueMap, null);
    }

    /**
     * 通过key获取单个
     */
    @Test
    public void getString() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(RedisConfig.class);
        RedisService redisService = ctx.getBean(RedisService.class);

        redisService.setString("key100", "key100", null);
        System.out.println("message: " + redisService.getString("key100"));
    }
}
