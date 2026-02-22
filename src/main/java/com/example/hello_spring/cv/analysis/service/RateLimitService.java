package com.example.hello_spring.cv.analysis.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import com.example.hello_spring.cv.exception.RateLimitExceededException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {

        // 5 analyses per minute
        Bandwidth minuteLimit = Bandwidth.classic(
                5,
                Refill.intervally(5, Duration.ofMinutes(1))
        );

        // 30 analyses per day
        Bandwidth dailyLimit = Bandwidth.classic(
                30,
                Refill.intervally(30, Duration.ofDays(1))
        );

        return Bucket.builder()
                .addLimit(minuteLimit)
                .addLimit(dailyLimit)
                .build();
    }

    public void checkAnalysisLimit(String username) {

        Bucket bucket = userBuckets.computeIfAbsent(
                username,
                k -> createNewBucket()
        );

        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException(
                    "Too many analysis requests. Please try again later."
            );
        }
    }
}
