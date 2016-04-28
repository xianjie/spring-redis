package com.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Title: RedisService
 * @Description:
 * @author: xian jie
 * @date: 2016-4-27 17:18
 * 杭州尚尚签网络科技有限公司
 * @version: 2.0
 */
@Service
public class RedisService {


    /**
     * redis.opsForValue()  封装操作strings
     * redis.opsForList()   封装操作list
     * redis.opsForSet() 封装操作sets
     * redis.opsForZSet() 封装操作sorted sets
     * redis.opsForHash() 封装操作hash
     */

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ---------------------------------------String-----------------------------------------

    /**
     * 新增String ----setNX 不存在则增加 ------------------------------
     *
     * @param key     键
     * @param value   值
     * @param timeout 超时(秒)
     * @return true 操作成功，false 已存在值
     */
    public boolean setString(final String key, final String value, final Long timeout) {
        /*boolean result = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            Boolean result1 = connection.setNX(key.getBytes(), value.getBytes());
            if (result1 == false)
                return result1;
            if (timeout != null && timeout > 0)
                connection.expire(key.getBytes(), timeout);
            return result1;
        });*/


        boolean result = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            // 序列化
            RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
            byte[] byteKey = redisSerializer.serialize(key);
            byte[] byteValue = redisSerializer.serialize(value);
            Boolean r1 = connection.setNX(byteKey, byteValue);
            if (r1 == false)
                return r1;
            if (timeout != null && timeout > 0)
                connection.expire(byteKey, timeout);
            return r1;
        });
        return result;
    }

    /**
     * 批量新增String---setNx 不存在则增加
     *
     * @param keyValueList 键值对的map
     * @param timeout      超时处理
     * @return
     */
    public boolean setString(final Map<String, String> keyValueList, final Long timeout) {
        boolean result = redisTemplate.execute(connection -> {
            for (String key : keyValueList.keySet()) {
                RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
                byte[] byteKey = redisSerializer.serialize(key);
                byte[] byteValue = redisSerializer.serialize(keyValueList.get(key));
                connection.setNX(byteKey, byteValue);
                if (timeout != null && timeout > 0)
                    connection.expire(key.getBytes(), timeout);
            }
            return true;
        }, false, true);
        return result;
    }

    /**
     * 通过key获取单个
     *
     * @param key
     * @return
     */
    public String getString(final String key) {
        String value = redisTemplate.execute((RedisCallback<String>) connection -> {
            RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
            byte[] byteKey = redisSerializer.serialize(key);
            byte[] result = connection.get(byteKey);
            if (result != null && result.length > 0) {
                // 反序列化
                redisSerializer.deserialize(result);
                return new String(result);
            }
            return null;
        });
        return value;
    }


    // ---------------------------------------List-----------------------------------------

    /**
     * 新增Hash ----setNX 不存在则增加 ------------------------------
     *
     * @param key     键
     * @param value   值
     * @param timeout 超时(秒)
     * @return true 操作成功，false 已存在值
     */
    public boolean addHash(final String key, final String field, final String value, final Long timeout) {
        boolean result = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            Boolean result1 = connection.hSetNX(key.getBytes(), field.getBytes(), value.getBytes());
            if (result1 == false)
                return result1;
            if (timeout != null && timeout > 0)
                connection.expire(key.getBytes(), timeout);
            return result1;
        });
        return result;
    }

    /**
     * 批量新增Hash ----setNX 不存在则增加 ------------------------------
     *
     * @param key            键
     * @param fieldValueList 值
     * @param timeout        超时(秒)
     * @return true 操作成功，false 已存在值
     */
    public boolean addHash(final String key, final Map<String, String> fieldValueList, final Long timeout) {
        boolean result = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            for (String hashKey : fieldValueList.keySet()) {
                connection.hSetNX(key.getBytes(), hashKey.getBytes(), fieldValueList.get(hashKey).getBytes());
                if (timeout != null && timeout > 0)
                    connection.expire(key.getBytes(), timeout);
            }
            return true;
        });
        return result;
    }

    /**
     * 通过key获取单个
     *
     * @param key
     * @return
     */
    public Object getHashField(final String key, final String field) {
        String value = redisTemplate.execute((RedisCallback<String>) connection -> new String(connection.hGet(key.getBytes(), field.getBytes())));
        return value;
    }

    /**
     * 通过key获取整个Hash
     *
     * @param key
     * @return
     */
    public Map<byte[], byte[]> getHashAll(final String key) {
        Map<byte[], byte[]> value = redisTemplate.execute((RedisCallback<Map<byte[], byte[]>>) connection -> connection.hGetAll(key.getBytes()));
        return value;
    }

    //---------------------------------------------------通用删除-------------------------------------------------

    /**
     * 删除单个
     *
     * @param key
     */
    public void delete(final String key) {
        redisTemplate.execute((RedisCallback<Long>) connection -> {
            return connection.del(key.getBytes());
        });
    }


    //----------------------------------------------------队列操作--------------------------------------------------

    /**
     * 压栈
     *
     * @param key
     * @param value
     * @return
     */
    public Long push(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 出栈
     *
     * @param key
     * @return
     */
    public String pop(String key) {
        return (String) redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 入队
     *
     * @param key
     * @param value
     * @return
     */
    public Long in(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 出队
     *
     * @param key
     * @return
     */
    public String out(String key) {
        return (String) redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 栈/队列长
     *
     * @param key
     * @return
     */
    public Long length(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 范围检索
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> range(String key, int start, int end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 移除
     *
     * @param key
     * @param i
     * @param value
     */
    public void remove(String key, long i, String value) {
        redisTemplate.opsForList().remove(key, i, value);
    }

    /**
     * 检索
     *
     * @param key
     * @param index
     * @return
     */
    public String index(String key, long index) {
        return (String) redisTemplate.opsForList().index(key, index);
    }

    /**
     * 置值
     *
     * @param key
     * @param index
     * @param value
     */
    public void set(String key, long index, String value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 裁剪
     *
     * @param key
     * @param start
     * @param end
     */
    public void trim(String key, long start, int end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    //---------------------------------------------------SET-----------------------------------------------

    /**
     * 新增Set ----setNX 不存在则增加 ------------------------------
     *
     * @param key     键
     * @param value   值
     * @param timeout 超时(秒)
     * @return true 操作成功，false 已存在值
     */
    public Long addSet(final String key, final String value, final Long timeout) {
        Long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
            Long result1 = connection.sAdd(key.getBytes(), value.getBytes());
            if (result1 == 0)
                return result1;
            if (timeout != null && timeout > 0)
                connection.expire(key.getBytes(), timeout);
            return result1;
        });
        return result;
    }


    /**
     * 通过key获取单个Set
     *
     * @param key
     * @return
     */
    public Set<byte[]> getSet(final String key) {
        Set<byte[]> value = redisTemplate.execute((RedisCallback<Set<byte[]>>) connection -> connection.sMembers(key.getBytes()));
        return value;
    }


}
