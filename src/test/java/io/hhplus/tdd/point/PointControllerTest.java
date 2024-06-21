package io.hhplus.tdd.point;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("포인트 조회 컨트롤러 테스트")
    public void pointTest() throws Exception {
        long id = 0;

        MvcResult mvcResult = mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        UserPoint actual = objectMapper.readValue(jsonResponse, UserPoint.class);

        assertThat(actual).isEqualTo(new UserPoint(0, 0, 0));
    }

    @Test
    @DisplayName("포인트 히스토리 컨트롤러 조회 테스트 ")
    public void historyTest() throws Exception {
        long id = 0;
        MvcResult mvcResult = mockMvc.perform(get("/point/{id}/histories", id))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<PointHistory> actual = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertThat(actual).isEqualTo(List.of());
    }

    @Test
    @DisplayName("포인트 충전 컨트롤러 테스트")
    public void chargeTest() throws Exception {
        long id = 0;
        long amount = 0;

        MvcResult mvcResult = mockMvc.perform(patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        UserPoint actual = objectMapper.readValue(jsonResponse, UserPoint.class);

        assertThat(actual).isEqualTo(new UserPoint(0, 0, 0));
    }

    @Test
    @DisplayName("포인트 사용 컨트롤러 테스트")
    public void useTest() throws Exception {
        long id = 0;
        long amount = 0;

        MvcResult mvcResult = mockMvc.perform(patch("/point/{id}/use", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        UserPoint actual = objectMapper.readValue(jsonResponse, UserPoint.class);
        assertThat(actual).isEqualTo(new UserPoint(0, 0, 0));
    }

}
