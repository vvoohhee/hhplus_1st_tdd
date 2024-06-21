package io.hhplus.tdd.point.handler;

import io.hhplus.tdd.common.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Function;

@Component
public class CompletableFutureHandler {
    // userId를 key로 하는 ConcurrentHashMap 생성
    private final ConcurrentHashMap<Long, CompletableFuture<Void>> userMap = new ConcurrentHashMap<>();

    /**
     * 같은 사용자가 요청하는 경우 CompletableFuture로 동시성 처리를 하기 위한 메서드
     *
     * @param userId 사용자 아이디
     * @param task   비동기로 수행할 작업
     * @return task 작업의 리턴 값
     */
    public <T> T executeOnFuture(Long userId, Function<Long, T> task) {
        // userId와 일치하는 key가 존재하지 않는 경우 맵에 추가
        CompletableFuture<Void> future = userMap.computeIfAbsent(userId, k -> new CompletableFuture<>());
        System.out.println("userLock key : " + userId);

        // CompletableFuture 사용하여 비동기로 task를 실행시키도록 함
        System.out.println("비동기 처리 시작");
        CompletableFuture<T> result = future.supplyAsync(() -> task.apply(userId));
        /*
        CompletableFuture<T> result = future.thenCompose(lock ->
                CompletableFuture.supplyAsync(() -> task.apply(userId))
        );
         */

        // CompletableFuture를 완료시킴
        result.whenComplete((response, throwable) -> {
            System.out.println("비동기 처리 끝");
            future.complete(null);
        });

        try {
            // 결과를 서비스로 리턴
            T resultValue = result.get();
            System.out.println("결과 : " + resultValue);
            return resultValue;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new CustomException("동시성 처리 실패");
        }
    }
}
