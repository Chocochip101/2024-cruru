package com.cruru.dashboard.controller;

import static com.cruru.util.fixture.ClubFixture.createClub;

import com.cruru.choice.controller.dto.ChoiceCreateRequest;
import com.cruru.club.domain.Club;
import com.cruru.club.domain.repository.ClubRepository;
import com.cruru.dashboard.controller.dto.DashboardCreateRequest;
import com.cruru.question.controller.dto.QuestionCreateRequest;
import com.cruru.util.ControllerTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("대시보드 컨트롤러 테스트")
class DashboardControllerTest extends ControllerTest {

    @Autowired
    private ClubRepository clubRepository;

    private Club club;

    @BeforeEach
    void setUp() {
        club = clubRepository.save(createClub());
    }

    @DisplayName("대시보드 생성 성공 시, 201을 응답한다.")
    @Test
    void read() {
        // given
        List<ChoiceCreateRequest> choiceCreateRequests = List.of(new ChoiceCreateRequest("선택지1", 1));
        List<QuestionCreateRequest> questionCreateRequests = List.of(
                new QuestionCreateRequest("DROPDOWN", "객관식질문1", "하나를 선택한다.", choiceCreateRequests, 1));
        DashboardCreateRequest request = new DashboardCreateRequest(
                "크루루대시보드",
                "# 공고 내용",
                questionCreateRequests,
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2999, 12, 31, 23, 59)
        );
        String url = String.format("/v1/dashboards?club_id=%d", club.getId());

        // when&then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post(url)
                .then().log().all().statusCode(201);
    }
}
