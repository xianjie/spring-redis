package com.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Title: RedisConfig
 * @Description:
 * @author: xian jie
 * @date: 2016-4-27 17:15
 * 杭州尚尚签网络科技有限公司
 * @version: 2.0
 */

@Configuration
@ComponentScan("com.redis")
@PropertySource("classpath:/redis.properties")
public class RedisConfig {

    private @Value("${redis.host}") String redisHost;
    private @Value("${redis.port}") String redisPort;

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(5);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        JedisConnectionFactory ob = new JedisConnectionFactory(poolConfig);
        ob.setUsePool(true);

        //System.out.println(redisHost);
        //System.out.println(redisPort);

        ob.setHostName("192.168.30.172");
        ob.setPort(6379);
        return ob;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }
}
