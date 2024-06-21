package io.hhplus.tdd.point.service;

import io.hhplus.tdd.common.exception.CustomException;
import org.springframework.stereotype.Component;

@Component
public class PointValidator {
    /**
     * 요청한 포인트 금액(amount)이 양수인지 검증
     * @param amount
     */
    public void validateAmount(long amount) {
        if (amount <= 0) throw new CustomException("0보다 큰 포인트 금액을 입력해주세요.");
    }

    /**
     * 사용하려는 포인트 금액(amount)이 currentPoint보다 작은지 검증
     * @param amount 사용자가 요청한 포인트 금액
     * @param currentPoint 사용자가 보유하고 있는 포인트 잔액
     */
    public void validateAmountAgainstPoint(long amount, long currentPoint) {
        if(amount > currentPoint) throw new CustomException("요청한 금액이 현재 포인트 잔액을 초과합니다.");
    }
}
