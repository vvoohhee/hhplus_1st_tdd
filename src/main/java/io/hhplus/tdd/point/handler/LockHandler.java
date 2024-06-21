package io.hhplus.tdd.point.handler;

import io.hhplus.tdd.common.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Component
public class LockHandler {
    private final ConcurrentHashMap<Long, Lock> userMap = new ConcurrentHashMap<>();

    public <T> T executeOnLock(Long userId, Supplier<T> block) {
        // userId와 일치하는 key가 없으면 생성
        Lock lock = userMap.computeIfAbsent(userId, k -> new ReentrantLock());

        try {
            boolean acquired = false;

            // 락을 3초동안 기다려보고, 3초 후에도 얻지 못하면 타임아웃 예외처리
            acquired = lock.tryLock(3000, TimeUnit.MILLISECONDS);

            if (!acquired) {
                throw new CustomException("Lock 획득 실패, TimeOut 발생");
            }

            // 락을 얻었을 경우, block을 실행시키고 제네릭 T 타입의 결과 반환
            return block.get();

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("락 얻는 중에 예외 발생");

        } finally {
            // 결과 반환 후 락 해제
            lock.unlock();
        }
    }
}
