package org.ccccye.redis;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@SpringBootTest
public class RateLimitRedisTest {
    @Autowired
    private RateLimitRedis redisManager;

    @Test
    public void rateLimitTest() throws InterruptedException {
        String key = "test_rateLimit_key";
        int max = 10;  //令牌桶大小
        int rate = 10; //令牌每秒恢复速度
        AtomicInteger successCount = new AtomicInteger(0);
        Executor executor = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(30);
        for (int i = 0; i < 30; i++) {
            executor.execute(() -> {
                boolean isAllow = redisManager.rateLimit(key, max, rate);
                if (isAllow) {
                    successCount.addAndGet(1);
                }
                log.info(Boolean.toString(isAllow));
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        log.info("请求成功{}次", successCount.get());
    }
}