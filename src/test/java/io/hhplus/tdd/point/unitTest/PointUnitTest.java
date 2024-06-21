package io.hhplus.tdd.point.unitTest;

import io.hhplus.tdd.common.exception.CustomException;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class PointUnitTest {
    @Mock
    UserPointRepository userPointRepository;

    @Mock
    PointValidator pointValidator;

    @BeforeEach
    void setUp() {
        // 목 객체를 초기화하기 위해 사용
        MockitoAnnotations.openMocks(this);
        pointValidator = new PointValidator();
    }

    @Test
    @DisplayName("포인트_충전_실패_테스트_충전_금액이_0이라_실패")
    void chargeTest_테스트_충전_금액이_0이라_실패() {
        // given
        long id = 1L;
        long chargeAmount = 0L;

        // when - then
        assertThrows(CustomException.class, () -> pointValidator.validateAmount(chargeAmount));
    }

    @Test
    @DisplayName("포인트_충전_실패_테스트_충전_금액이_0보다_작아_실패")
    void chargeTest_테스트_충전_금액이_0보다_작아_실패() {
        // given
        long id = 1L;
        long chargeAmount = -3000L;

        // when - then
        assertThrows(CustomException.class, () -> pointValidator.validateAmount(chargeAmount));
    }

    @Test
    @DisplayName("포인트_사용_실패_테스트_사용하려는_포인트가_잔액보다_커서_실패")
    void useTest_포인트_사용_실패_테스트_사용하려는_포인트가_잔액보다_커서_실패() {
        // given
        long id = 1L;
        long amount = 20000L;
        UserPoint currentUserPoint = new UserPoint(1L, 10000L, 0L);

        // when
        when(userPointRepository.selectById(id)).thenReturn(currentUserPoint);

        // then
        assertThrows(CustomException.class, () -> {
            pointValidator.validateAmountAgainstPoint(amount, currentUserPoint.point());
        });
    }

}
