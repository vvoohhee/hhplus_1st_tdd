package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointHistoryRepository pointHistoryRepository;
    private final UserPointRepository userPointRepository;
    private final PointValidator pointValidator;

    public UserPoint point(long id) {
        // id가 일치하는 유저의 포인트 조회 후 리턴
        return userPointRepository.selectById(id);
    }

    public List<PointHistory> history(long id) {
        // id가 일치하는 유저의 포인트 내역 조회 후 리턴
        return pointHistoryRepository.selectAllByUserId(id);
    }

    public UserPoint charge(long id, long amount) {
        // amount는 0보다 커야함 (음수 또는 0이면 안됨)
        pointValidator.validateAmount(amount);

        // 포인트 충전
        UserPoint userPoint = userPointRepository.insertOrUpdate(id, amount);

        // 포인트 충전내역 생성
        pointHistoryRepository.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

        return userPoint;
    }

    public UserPoint use(long id, long amount) {
        // amount는 0보다 커야함 (음수 또는 0이면 안됨)
        pointValidator.validateAmount(amount);

        // amount 만큼 사용할 수 있는지 확인하기 위해 UserPoint 정보를 조회
        UserPoint userPoint = userPointRepository.selectById(id);

        // amount는 유저가 가지고 있는 포인트보다 크면 안됨
        pointValidator.validateAmountAgainstPoint(amount, userPoint.point());

        // 테이블에 포인트 사용 후의 금액을 업데이트
        UserPoint afterUserPoint = userPointRepository.insertOrUpdate(userPoint.id(), userPoint.point() - amount);

        // 포인트 이용내역 생성
        pointHistoryRepository.insert(id, amount, TransactionType.USE, System.currentTimeMillis());

        return afterUserPoint;
    }
}
