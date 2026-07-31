package com.kurekurecredential.web.mockexam;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurekurecredential.repository.CertificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class MockExamResultControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CertificationRepository certificationRepository;

	@Test
	void mockExamResultsRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/mock-exam-results"))
				.andExpect(status().isForbidden());

		mockMvc.perform(post(
						"/api/certification-goals/{goalId}/mock-exam-results",
						1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(resultRequest(
								"2026-08-15",
								"認証なし",
								650,
								1000,
								720,
								"65.00")))
				.andExpect(status().isForbidden());
	}

	@Test
	void ownerCanCreateMockExamResult() throws Exception {
		String accessToken = registerAndLogin("mock-exam-create@example.com");
		long goalId = createGoal(accessToken);

		mockMvc.perform(post(
						"/api/certification-goals/{goalId}/mock-exam-results",
						goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(resultRequest(
								"2026-08-15",
								"AWS SAA 模擬試験 1回目",
								650,
								1000,
								720,
								"65.00")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.goalId").value(goalId))
				.andExpect(jsonPath("$.examDate").value("2026-08-15"))
				.andExpect(jsonPath("$.examName").value("AWS SAA 模擬試験 1回目"))
				.andExpect(jsonPath("$.score").value(650))
				.andExpect(jsonPath("$.maxScore").value(1000))
				.andExpect(jsonPath("$.passingScore").value(720))
				.andExpect(jsonPath("$.scoreGap").value(-70))
				.andExpect(jsonPath("$.correctAnswerRate").value(65.0))
				.andExpect(jsonPath("$.weakAreas").value("VPC, IAM"))
				.andExpect(jsonPath("$.memo").value("弱点を復習する。"));
	}

	@Test
	void createValidatesExamDateAndScores() throws Exception {
		String accessToken = registerAndLogin("mock-exam-validation@example.com");
		long goalId = createGoal(accessToken);

		mockMvc.perform(post(
						"/api/certification-goals/{goalId}/mock-exam-results",
						goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(resultRequest(
								"2026-07-31",
								"期間外",
								650,
								1000,
								720,
								"65.00")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("受験日は資格目標の学習期間内に設定してください。"));

		mockMvc.perform(post(
						"/api/certification-goals/{goalId}/mock-exam-results",
						goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(resultRequest(
								"2026-08-15",
								"得点超過",
								1001,
								1000,
								720,
								"65.00")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("得点は満点以下にしてください。"));

		mockMvc.perform(post(
						"/api/certification-goals/{goalId}/mock-exam-results",
						goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(resultRequest(
								"2026-08-15",
								"合格ライン超過",
								650,
								1000,
								1001,
								"65.00")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("合格ラインは満点以下にしてください。"));

		mockMvc.perform(post(
						"/api/certification-goals/{goalId}/mock-exam-results",
						goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(resultRequest(
								"2026-08-15",
								"正答率超過",
								650,
								1000,
								720,
								"100.01")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	@Test
	void listReturnsOnlyCurrentUsersResultsAndSupportsFilters() throws Exception {
		String firstToken = registerAndLogin("mock-exam-list-first@example.com");
		String secondToken = registerAndLogin("mock-exam-list-second@example.com");
		long firstGoalId = createGoal(firstToken);
		long secondGoalId = createGoal(secondToken);

		long earlierId = createResult(
				firstToken,
				firstGoalId,
				"2026-08-05",
				"1回目",
				600);
		long laterId = createResult(
				firstToken,
				firstGoalId,
				"2026-08-20",
				"2回目",
				750);
		createResult(secondToken, secondGoalId, "2026-08-25", "別ユーザー", 800);

		mockMvc.perform(get("/api/mock-exam-results")
						.header(HttpHeaders.AUTHORIZATION, bearer(firstToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.total").value(2))
				.andExpect(jsonPath("$.items[0].id").value(laterId))
				.andExpect(jsonPath("$.items[1].id").value(earlierId));

		mockMvc.perform(get("/api/mock-exam-results")
						.queryParam("goalId", String.valueOf(firstGoalId))
						.queryParam("examFrom", "2026-08-01")
						.queryParam("examTo", "2026-08-10")
						.header(HttpHeaders.AUTHORIZATION, bearer(firstToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].id").value(earlierId));
	}

	@Test
	void ownerCanGetAndUpdateMockExamResult() throws Exception {
		String accessToken = registerAndLogin("mock-exam-update@example.com");
		long goalId = createGoal(accessToken);
		long resultId = createResult(
				accessToken,
				goalId,
				"2026-08-15",
				"更新前",
				650);

		mockMvc.perform(get("/api/mock-exam-results/{resultId}", resultId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(resultId))
				.andExpect(jsonPath("$.scoreGap").value(-70));

		mockMvc.perform(put("/api/mock-exam-results/{resultId}", resultId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(resultRequest(
								"2026-08-22",
								"更新後",
								780,
								1000,
								720,
								"78.00")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.examDate").value("2026-08-22"))
				.andExpect(jsonPath("$.examName").value("更新後"))
				.andExpect(jsonPath("$.score").value(780))
				.andExpect(jsonPath("$.scoreGap").value(60));
	}

	@Test
	void otherUserCannotAccessResultAndInvalidSearchReturnsError()
			throws Exception {
		String ownerToken = registerAndLogin("mock-exam-owner@example.com");
		String otherToken = registerAndLogin("mock-exam-other@example.com");
		long ownerGoalId = createGoal(ownerToken);
		long resultId = createResult(
				ownerToken,
				ownerGoalId,
				"2026-08-15",
				"所有者の結果",
				650);

		mockMvc.perform(get("/api/mock-exam-results/{resultId}", resultId)
						.header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));

		mockMvc.perform(get("/api/mock-exam-results")
						.queryParam("examFrom", "2026-08-20")
						.queryParam("examTo", "2026-08-01")
						.header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("受験日終了は受験日開始以降にしてください。"));

		mockMvc.perform(get("/api/mock-exam-results/{resultId}", Long.MAX_VALUE)
						.header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message")
						.value("模擬試験結果が見つかりません。id=" + Long.MAX_VALUE));
	}

	private long createGoal(String accessToken) throws Exception {
		long certificationId = certificationRepository.findAll().get(0).getId();
		String request = """
				{
				  "certificationId": %d,
				  "targetExamDate": "2027-02-28",
				  "weeklyStudyHours": 8,
				  "currentLevel": "BEGINNER",
				  "studyStartDate": "2026-08-01",
				  "status": "IN_PROGRESS"
				}
				""".formatted(certificationId);
		MvcResult result = mockMvc.perform(post("/api/certification-goals")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isCreated())
				.andReturn();
		return objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id")
				.asLong();
	}

	private long createResult(
			String accessToken,
			long goalId,
			String examDate,
			String examName,
			int score) throws Exception {
		MvcResult result = mockMvc.perform(post(
						"/api/certification-goals/{goalId}/mock-exam-results",
						goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(resultRequest(
								examDate,
								examName,
								score,
								1000,
								720,
								String.valueOf(score / 10.0))))
				.andExpect(status().isCreated())
				.andReturn();
		return objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id")
				.asLong();
	}

	private String resultRequest(
			String examDate,
			String examName,
			int score,
			int maxScore,
			int passingScore,
			String correctAnswerRate) {
		return """
				{
				  "examDate": "%s",
				  "examName": "%s",
				  "score": %d,
				  "maxScore": %d,
				  "passingScore": %d,
				  "correctAnswerRate": %s,
				  "weakAreas": "VPC, IAM",
				  "memo": "弱点を復習する。"
				}
				""".formatted(
				examDate,
				examName,
				score,
				maxScore,
				passingScore,
				correctAnswerRate);
	}

	private String registerAndLogin(String email) throws Exception {
		String registerRequest = """
				{
				  "name": "模擬試験APIテストユーザー",
				  "email": "%s",
				  "password": "password123"
				}
				""".formatted(email);
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(registerRequest))
				.andExpect(status().isCreated());

		String loginRequest = """
				{
				  "email": "%s",
				  "password": "password123"
				}
				""".formatted(email);
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginRequest))
				.andExpect(status().isOk())
				.andReturn();
		return objectMapper.readTree(loginResult.getResponse().getContentAsString())
				.get("accessToken")
				.asText();
	}

	private String bearer(String accessToken) {
		return "Bearer " + accessToken;
	}
}
