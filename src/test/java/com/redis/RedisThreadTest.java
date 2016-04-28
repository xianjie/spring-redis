package com.redis;

import com.redis.config.RedisConfig;
import com.redis.service.RedisService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Title: RedisThreadTest
 * @Description:
 * @author: xian jie
 * @date: 2016-4-27 21:23
 * 杭州尚尚签网络科技有限公司
 * @version: 2.0
 */
public class RedisThreadTest {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(RedisConfig.class);
        RedisService redisService = ctx.getBean(RedisService.class);


        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        long beginTime = System.currentTimeMillis();

        // 写入
        /*for (int i = 0; i < 10000; i++) {
            executor.execute(new Thread(new SetString("key" + i, "value" + i, redisService)));
        }*/

        // 读取 并发读取同一个 key
        /*for (int i = 0; i < 10; i++) {
            executor.execute(new Thread(new GetString("key1", redisService)));
        }*/


        // 读取 并发读取不同的 key
        for (int i = 0; i < 1000; i++) {
            executor.execute(new Thread(new GetString("key" + i, redisService)));
        }


        executor.shutdown();

        while (true) {
            if (executor.isTerminated()) {
                System.out.println("所有线程执行完成");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Done");
        System.out.println("全部线程执行总时间=" + (endTime - beginTime) + "毫秒");
    }
}


class SetString implements Runnable {

    private String key;
    private String value;
    private RedisService redisService;

    public SetString(String key, String value, RedisService redisService) {
        this.key = key;
        this.value = value;
        this.redisService = redisService;
    }

    @Override
    public void run() {
        boolean b = redisService.setString(key, value, null);
        System.out.println("key=" + key + "&" + b);
    }
}


class GetString implements Runnable {

    private String key;
    private RedisService redisService;

    public GetString(String key, RedisService redisService) {
        this.key = key;
        this.redisService = redisService;
    }

    @Override
    public void run() {
        System.out.println("key=" + key + "&value=" + redisService.getString(key));
    }
}
