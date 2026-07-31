package com.kurekurecredential.web.progress;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurekurecredential.domain.certification.Certification;
import com.kurekurecredential.repository.CertificationRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
class ProgressSummaryControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CertificationRepository certificationRepository;

	@Test
	void summaryRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/api/certification-goals/{goalId}/summary", 1))
				.andExpect(status().isForbidden());
	}

	@Test
	void ownerCanGetAggregatedProgressSummary() throws Exception {
		String accessToken = registerAndLogin("progress-summary@example.com");
		GoalContext goal = createGoal(accessToken);
		createStudyPlan(accessToken, goal.id());
		long firstTaskId = createTask(accessToken, goal.id(), "基礎学習");
		createTask(accessToken, goal.id(), "問題演習");
		mockMvc.perform(patch("/api/study-tasks/{taskId}/complete", firstTaskId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk());
		createStudyLog(accessToken, goal.id(), "2026-08-10", 120, "基礎学習");
		createStudyLog(accessToken, goal.id(), "2026-08-11", 180, "問題演習");
		createMockExamResult(
				accessToken,
				goal.id(),
				"2026-08-15",
				"模擬試験 1回目",
				650);
		long latestResultId = createMockExamResult(
				accessToken,
				goal.id(),
				"2026-09-15",
				"模擬試験 2回目",
				750);
		long expectedDays = ChronoUnit.DAYS.between(
				LocalDate.now(),
				LocalDate.of(2027, 2, 28));

		mockMvc.perform(get(
						"/api/certification-goals/{goalId}/summary",
						goal.id())
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.goalId").value(goal.id()))
				.andExpect(jsonPath("$.certificationName")
						.value(goal.certificationName()))
				.andExpect(jsonPath("$.targetExamDate").value("2027-02-28"))
				.andExpect(jsonPath("$.daysUntilExam").value(expectedDays))
				.andExpect(jsonPath("$.plannedStudyMinutes").value(600))
				.andExpect(jsonPath("$.actualStudyMinutes").value(300))
				.andExpect(jsonPath("$.studyProgressRate").value(50.0))
				.andExpect(jsonPath("$.taskSummary.total").value(2))
				.andExpect(jsonPath("$.taskSummary.done").value(1))
				.andExpect(jsonPath("$.taskSummary.todo").value(1))
				.andExpect(jsonPath("$.taskSummary.completionRate").value(50.0))
				.andExpect(jsonPath("$.latestMockExamResult.id")
						.value(latestResultId))
				.andExpect(jsonPath("$.latestMockExamResult.examDate")
						.value("2026-09-15"))
				.andExpect(jsonPath("$.latestMockExamResult.examName")
						.value("模擬試験 2回目"))
				.andExpect(jsonPath("$.latestMockExamResult.score").value(750))
				.andExpect(jsonPath("$.latestMockExamResult.passingScore")
						.value(720))
				.andExpect(jsonPath("$.latestMockExamResult.scoreGap").value(30));
	}

	@Test
	void summaryReturnsZeroValuesWhenNoProgressDataExists() throws Exception {
		String accessToken = registerAndLogin("progress-empty@example.com");
		GoalContext goal = createGoal(accessToken);

		mockMvc.perform(get(
						"/api/certification-goals/{goalId}/summary",
						goal.id())
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.plannedStudyMinutes").value(0))
				.andExpect(jsonPath("$.actualStudyMinutes").value(0))
				.andExpect(jsonPath("$.studyProgressRate").value(0.0))
				.andExpect(jsonPath("$.taskSummary.total").value(0))
				.andExpect(jsonPath("$.taskSummary.done").value(0))
				.andExpect(jsonPath("$.taskSummary.todo").value(0))
				.andExpect(jsonPath("$.taskSummary.completionRate").value(0.0))
				.andExpect(jsonPath("$.latestMockExamResult").doesNotExist());
	}

	@Test
	void otherUserCannotGetSummaryAndMissingGoalReturnsNotFound()
			throws Exception {
		String ownerToken = registerAndLogin("progress-owner@example.com");
		String otherToken = registerAndLogin("progress-other@example.com");
		GoalContext ownerGoal = createGoal(ownerToken);

		mockMvc.perform(get(
						"/api/certification-goals/{goalId}/summary",
						ownerGoal.id())
						.header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));

		mockMvc.perform(get(
						"/api/certification-goals/{goalId}/summary",
						Long.MAX_VALUE)
						.header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message")
						.value("資格目標が見つかりません。id=" + Long.MAX_VALUE));
	}

	private GoalContext createGoal(String accessToken) throws Exception {
		Certification certification = certificationRepository.findAll().get(0);
		String request = """
				{
				  "certificationId": %d,
				  "targetExamDate": "2027-02-28",
				  "weeklyStudyHours": 8,
				  "currentLevel": "BEGINNER",
				  "studyStartDate": "2026-08-01",
				  "status": "IN_PROGRESS"
				}
				""".formatted(certification.getId());
		MvcResult result = mockMvc.perform(post("/api/certification-goals")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isCreated())
				.andReturn();
		long goalId = objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id")
				.asLong();
		return new GoalContext(goalId, certification.getName());
	}

	private void createStudyPlan(String accessToken, long goalId)
			throws Exception {
		String request = """
				{
				  "title": "10時間学習計画",
				  "startDate": "2026-08-01",
				  "endDate": "2026-10-31",
				  "totalPlannedHours": 10,
				  "memo": "進捗集計テスト",
				  "items": [
				    {
				      "weekNumber": 1,
				      "phase": "BASIC_UNDERSTANDING",
				      "title": "基礎学習",
				      "description": "基礎を理解する",
				      "plannedHours": 4,
				      "mockExamRecommended": false
				    },
				    {
				      "weekNumber": 2,
				      "phase": "QUESTION_PRACTICE",
				      "title": "問題演習",
				      "description": "問題を解く",
				      "plannedHours": 6,
				      "mockExamRecommended": false
				    }
				  ]
				}
				""";
		mockMvc.perform(post(
						"/api/certification-goals/{goalId}/study-plans",
						goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isCreated());
	}

	private long createTask(
			String accessToken,
			long goalId,
			String title) throws Exception {
		String request = """
				{
				  "studyPlanItemId": null,
				  "title": "%s",
				  "description": "進捗集計テスト",
				  "dueDate": "2026-08-31",
				  "estimatedMinutes": 60,
				  "priority": "MEDIUM"
				}
				""".formatted(title);
		MvcResult result = mockMvc.perform(post(
						"/api/certification-goals/{goalId}/study-tasks",
						goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isCreated())
				.andReturn();
		return objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id")
				.asLong();
	}

	private void createStudyLog(
			String accessToken,
			long goalId,
			String studiedDate,
			int studyMinutes,
			String title) throws Exception {
		String request = """
				{
				  "taskId": null,
				  "resourceId": null,
				  "studiedDate": "%s",
				  "studyMinutes": %d,
				  "title": "%s",
				  "content": "進捗集計テスト",
				  "reflection": null,
				  "understandingLevel": "MEDIUM"
				}
				""".formatted(studiedDate, studyMinutes, title);
		mockMvc.perform(post(
						"/api/certification-goals/{goalId}/study-logs",
						goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isCreated());
	}

	private long createMockExamResult(
			String accessToken,
			long goalId,
			String examDate,
			String examName,
			int score) throws Exception {
		String request = """
				{
				  "examDate": "%s",
				  "examName": "%s",
				  "score": %d,
				  "maxScore": 1000,
				  "passingScore": 720,
				  "correctAnswerRate": %s,
				  "weakAreas": null,
				  "memo": null
				}
				""".formatted(examDate, examName, score, score / 10.0);
		MvcResult result = mockMvc.perform(post(
						"/api/certification-goals/{goalId}/mock-exam-results",
						goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isCreated())
				.andReturn();
		return objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id")
				.asLong();
	}

	private String registerAndLogin(String email) throws Exception {
		String registerRequest = """
				{
				  "name": "進捗サマリーAPIテストユーザー",
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

	private record GoalContext(long id, String certificationName) {
	}
}
