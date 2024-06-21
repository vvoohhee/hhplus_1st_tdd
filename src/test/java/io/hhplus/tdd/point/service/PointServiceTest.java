package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PointServiceTest {

    @Mock
    UserPointRepository userPointRepository;

    @Mock
    PointHistoryRepository pointHistoryRepository;

    @Mock
    PointValidator pointValidator;

    @InjectMocks
    PointService pointService;

    @BeforeEach
    void setUp() {
        // 목 객체를 초기화하기 위해 사용
        MockitoAnnotations.openMocks(this);
        pointValidator = new PointValidator();
    }

    @Test
    @DisplayName("포인트_조회_성공_테스트")
    void pointTest_성공() {
        // given
        long id = 1L;
        UserPoint userPoint = new UserPoint(id, 1000L, System.currentTimeMillis());

        // when
        when(userPointRepository.selectById(id)).thenReturn(userPoint);
        UserPoint result = pointService.point(id);

        // then : 결과는 Null이면 안되고, 스텁(userPoint)과 서비스 메서드의 결과인 result는 같아야 한다.
        assertNotNull(result);
        assertEquals(userPoint, result);
    }

    @Test
    @DisplayName("포인트_히스토리_조회_성공_테스트_유저와_히스토리가_모두_존재하여_히스토리_조회")
    void historyTest_유저와_히스토리가_모두_존재하여_히스토리_조회() {
        // given
        long id = 1L;
        List<PointHistory> pointHistoryList = List.of(
                new PointHistory(1L, 1L, 1000L, TransactionType.CHARGE, System.currentTimeMillis())
        );

        // when
        when(pointHistoryRepository.selectAllByUserId(id)).thenReturn(pointHistoryList);
        List<PointHistory> result = pointService.history(id);

        // then
        assertNotNull(result);
        assertIterableEquals(pointHistoryList, result);
    }

    @Test
    @DisplayName("포인트_충전_성공_테스트")
    void chargeTest_성공() {
        // given
        long id = 1L;
        long chargeAmount = 1000L;
        UserPoint updateUserPoint = new UserPoint(id, 1000L, 0L);

        // when
        when(userPointRepository.insertOrUpdate(id, chargeAmount)).thenReturn(updateUserPoint);
        UserPoint result = pointService.charge(id, chargeAmount);

        // then
        assertNotNull(result);
        assertEquals(result.id(), 1L);
        assertEquals(result.point(), 1000L);
    }

    @Test
    @DisplayName("포인트_사용_성공_테스트")
    void useTest_성공() {

        // given
        long id = 1L;
        long useAmount = 3000L;
        UserPoint currentUserPoint = new UserPoint(1L, 10000L, System.currentTimeMillis());
        UserPoint remainUserPoint = new UserPoint(1L, 7000L, System.currentTimeMillis());

        // when
        when(userPointRepository.selectById(id)).thenReturn(currentUserPoint);
        when(userPointRepository.insertOrUpdate(currentUserPoint.id(), currentUserPoint.point() - useAmount)).thenReturn(remainUserPoint);
        UserPoint result = pointService.use(id, useAmount);

        // then
        assertNotNull(result);
        assertEquals(remainUserPoint, result);
    }

}
