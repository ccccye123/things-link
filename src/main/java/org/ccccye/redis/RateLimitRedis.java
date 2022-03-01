package org.ccccye.redis;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RateLimitRedis {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> rateScript;

    static {
        rateScript = new DefaultRedisScript<>();
        rateScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("/lua/rate.lua")));
        rateScript.setResultType(Long.class);
    }

    /**
     * @param key  限流键
     * @param max  桶大小
     * @param rate 桶恢复速度
     * @Author chenf
     * @Description 限流
     * @Date 15:43 2022/3/1
     * @Return boolean
     */
    public boolean rateLimit(String key, int max, int rate) {
        List<String> keyList = new ArrayList<>(1);
        keyList.add(key);
        Long ret = stringRedisTemplate
                .execute(rateScript,
                        keyList,
                        Integer.toString(max),
                        Integer.toString(rate),
                        Long.toString(System.currentTimeMillis()));
        return Long.valueOf(1).equals(ret);
    }


}
