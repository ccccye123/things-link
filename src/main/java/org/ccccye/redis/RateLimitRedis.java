package org.ccccye.redis;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RateLimitRedis {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean rateLimit(String key, int max, int rate) {
        List<String> keyList = new ArrayList<>(1);
        keyList.add(key);
        Long ret = stringRedisTemplate
                .execute(new RedisReteLimitScript(), keyList, Integer.toString(max), Integer.toString(rate),
                        Long.toString(System.currentTimeMillis()));
        return new Long(1).equals(ret);
    }



    class RedisReteLimitScript implements RedisScript<Long> {
//        private static final String SCRIPT =
//                "local ratelimit_info = redis.pcall('HMGET',KEYS[1],'last_time','current_token') local last_time = ratelimit_info[1] local current_token = tonumber(ratelimit_info[2]) local max_token = tonumber(ARGV[1]) local token_rate = tonumber(ARGV[2]) local current_time = tonumber(ARGV[3]) local reverse_time = 1000/token_rate if current_token == nil then current_token = max_token last_time = current_time else local past_time = current_time-last_time; local reverse_token = math.floor(past_time/reverse_time) current_token = current_token+reverse_token; last_time = reverse_time*reverse_token+last_time if current_token>max_token then current_token = max_token end end local result = '0' if(current_token>0) then result = '1' current_token = current_token-1 end redis.call('HMSET',KEYS[1],'last_time',last_time,'current_token',current_toke  redis.call('pexpire',KEYS[1],math.ceil(reverse_time*(max_tokencurrent_token)+(current_time-last_time))) return result";

        private static final String SCRIPT = "local ratelimit_info = redis.pcall('HMGET',KEYS[1],'last_time','current_token')\n" +
                "local last_time = ratelimit_info[1]\n" +
                "local current_token = tonumber(ratelimit_info[2])\n" +
                "local max_token = tonumber(ARGV[1])\n" +
                "local token_rate = tonumber(ARGV[2])\n" +
                "local current_time = tonumber(ARGV[3])\n" +
                "local reverse_time = 1000/token_rate\n" +
                "if current_token == nil then\n" +
                "  current_token = max_token\n" +
                "  last_time = current_time\n" +
                "else\n" +
                "  local past_time = current_time-last_time\n" +
                "  local reverse_token = math.floor(past_time/reverse_time)\n" +
                "  current_token = current_token+reverse_token\n" +
                "  last_time = reverse_time*reverse_token+last_time\n" +
                "  if current_token>max_token then\n" +
                "    current_token = max_token\n" +
                "  end\n" +
                "end\n" +
                "local result = 0\n" +
                "if(current_token>0) then\n" +
                "  result = 1\n" +
                "  current_token = current_token-1\n" +
                "end\n" +
                "redis.call('HMSET',KEYS[1],'last_time',last_time,'current_token',current_token)\n" +
                "redis.call('pexpire',KEYS[1],math.ceil(reverse_time*(max_token-current_token)+(current_time-last_time)))\n" +
                "return result\n";

        @Override
        public String getSha1() {
            return DigestUtils.sha1DigestAsHex(SCRIPT);
        }

        @Override
        public Class<Long> getResultType() {
            return Long.class;
        }

        @Override
        public String getScriptAsString() {
            return SCRIPT;
        }
    }
}
