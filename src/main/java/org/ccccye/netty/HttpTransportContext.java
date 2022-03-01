package org.ccccye.netty;

import lombok.Getter;
import org.ccccye.redis.RateLimitRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class HttpTransportContext {
    @Getter
    @Autowired
    private RateLimitRedis rateLimitRedis;

    @Getter
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Getter
    @Value("${rate.entry.max}")
    private int entryBucketMax;

    @Getter
    @Value("${rate.entry.rate}")
    private int entryRateLimit;

    @Getter
    @Value("${rate.device.max}")
    private int deviceBucketMax;

    @Getter
    @Value("${rate.device.rate}")
    private int deviceRateLimit;
}
