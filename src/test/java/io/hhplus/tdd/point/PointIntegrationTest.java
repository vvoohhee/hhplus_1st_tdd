package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PointIntegrationTest {
    @Autowired
    PointService pointService;

    @Test
    @DisplayName("포인트_충전과_포인트_사용_동시성_테스트")
    void pointServiceAsyncTest_포인트_충전과_포인트_사용_동시성_테스트() {
        // given
        long userId = 1L;
        pointService.charge(userId, 10000);

        // when
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    pointService.use(userId, 1000);
                }),
                CompletableFuture.runAsync(() -> {
                    pointService.charge(userId, 4000);
                }),
                CompletableFuture.runAsync(() -> {
                    pointService.use(userId, 100);
                })
        ).join();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        UserPoint userPoint = pointService.point(userId);

        assertNotNull(userPoint);
        assertEquals(10000 - 1000 + 4000 - 100, userPoint.point());
    }
}
