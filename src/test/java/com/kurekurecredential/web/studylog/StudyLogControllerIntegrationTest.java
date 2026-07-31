package com.kurekurecredential.web.studylog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurekurecredential.domain.study.LearningResource;
import com.kurekurecredential.repository.LearningResourceRepository;
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
class StudyLogControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private LearningResourceRepository learningResourceRepository;

	@Test
	void studyLogsRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/study-logs"))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-logs", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(studyLogRequest(null, null, "2026-08-10", 60, "未認証")))
				.andExpect(status().isForbidden());
	}

	@Test
	void ownerCanCreateStudyLogWithTaskAndResource() throws Exception {
		String accessToken = registerAndLogin("study-log-create@example.com");
		LogContext context = createContext(accessToken);

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-logs",
						context.goalId())
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(studyLogRequest(
								context.taskId(),
								context.resourceId(),
								"2026-08-10",
								90,
								"IAMの基本学習")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.goalId").value(context.goalId()))
				.andExpect(jsonPath("$.taskId").value(context.taskId()))
				.andExpect(jsonPath("$.resourceId").value(context.resourceId()))
				.andExpect(jsonPath("$.studiedDate").value("2026-08-10"))
				.andExpect(jsonPath("$.studyMinutes").value(90))
				.andExpect(jsonPath("$.title").value("IAMの基本学習"))
				.andExpect(jsonPath("$.content").value("学習内容"))
				.andExpect(jsonPath("$.reflection").value("振り返り"))
				.andExpect(jsonPath("$.understandingLevel").value("MEDIUM"));
	}

	@Test
	void createValidatesDateMinutesTaskAndResource() throws Exception {
		String accessToken = registerAndLogin("study-log-validation@example.com");
		LogContext firstContext = createContext(accessToken);
		LogContext secondContext = createContext(accessToken);
		long differentResourceId = findResourceForDifferentCertification(
				firstContext.certificationId());

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-logs",
						firstContext.goalId())
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(studyLogRequest(null, null, "2026-07-31", 60, "期間外")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("学習日は資格目標の学習期間内に設定してください。"));

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-logs",
						firstContext.goalId())
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(studyLogRequest(null, null, "2026-08-10", 0, "時間不足")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-logs",
						firstContext.goalId())
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(studyLogRequest(
								secondContext.taskId(),
								null,
								"2026-08-10",
								60,
								"別目標タスク")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("学習タスクは指定した資格目標に属していません。"));

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-logs",
						firstContext.goalId())
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(studyLogRequest(
								null,
								differentResourceId,
								"2026-08-10",
								60,
								"別資格教材")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("教材は資格目標と同じ資格に紐づくものを指定してください。"));
	}

	@Test
	void listReturnsOnlyCurrentUsersLogsAndSupportsDateFilters() throws Exception {
		String firstUserToken = registerAndLogin("study-log-list-first@example.com");
		String secondUserToken = registerAndLogin("study-log-list-second@example.com");
		LogContext firstContext = createContext(firstUserToken);
		LogContext secondContext = createContext(secondUserToken);

		long earlierLogId = createStudyLog(
				firstUserToken,
				firstContext,
				"2026-08-05",
				"先のログ");
		long laterLogId = createStudyLog(
				firstUserToken,
				firstContext,
				"2026-08-20",
				"後のログ");
		createStudyLog(secondUserToken, secondContext, "2026-08-25", "別ユーザーログ");

		mockMvc.perform(get("/api/study-logs")
						.header(HttpHeaders.AUTHORIZATION, bearer(firstUserToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.total").value(2))
				.andExpect(jsonPath("$.items[0].id").value(laterLogId))
				.andExpect(jsonPath("$.items[1].id").value(earlierLogId));

		mockMvc.perform(get("/api/study-logs")
						.queryParam("goalId", String.valueOf(firstContext.goalId()))
						.queryParam("studiedFrom", "2026-08-01")
						.queryParam("studiedTo", "2026-08-10")
						.header(HttpHeaders.AUTHORIZATION, bearer(firstUserToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].id").value(earlierLogId));
	}

	@Test
	void ownerCanGetAndUpdateStudyLog() throws Exception {
		String accessToken = registerAndLogin("study-log-update@example.com");
		LogContext context = createContext(accessToken);
		long logId = createStudyLog(
				accessToken,
				context,
				"2026-08-10",
				"更新前ログ");

		mockMvc.perform(get("/api/study-logs/{logId}", logId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(logId))
				.andExpect(jsonPath("$.content").value("学習内容"));

		mockMvc.perform(put("/api/study-logs/{logId}", logId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(studyLogRequest(
								null,
								null,
								"2026-08-11",
								120,
								"更新後ログ")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.taskId").doesNotExist())
				.andExpect(jsonPath("$.resourceId").doesNotExist())
				.andExpect(jsonPath("$.studiedDate").value("2026-08-11"))
				.andExpect(jsonPath("$.studyMinutes").value(120))
				.andExpect(jsonPath("$.title").value("更新後ログ"));
	}

	@Test
	void otherUserCannotAccessStudyLogOrTask() throws Exception {
		String ownerToken = registerAndLogin("study-log-owner@example.com");
		String otherUserToken = registerAndLogin("study-log-other@example.com");
		LogContext ownerContext = createContext(ownerToken);
		LogContext otherContext = createContext(otherUserToken);
		long logId = createStudyLog(ownerToken, ownerContext, "2026-08-10", "所有者ログ");

		mockMvc.perform(get("/api/study-logs/{logId}", logId)
						.header(HttpHeaders.AUTHORIZATION, bearer(otherUserToken)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-logs",
						otherContext.goalId())
						.header(HttpHeaders.AUTHORIZATION, bearer(otherUserToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(studyLogRequest(
								ownerContext.taskId(),
								null,
								"2026-08-10",
								60,
								"他人のタスク")))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));
	}

	@Test
	void invalidSearchAndMissingLogReturnErrors() throws Exception {
		String accessToken = registerAndLogin("study-log-error@example.com");

		mockMvc.perform(get("/api/study-logs")
						.queryParam("studiedFrom", "2026-08-20")
						.queryParam("studiedTo", "2026-08-01")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("学習日終了日は学習日開始日以降にしてください。"));

		mockMvc.perform(get("/api/study-logs/{logId}", Long.MAX_VALUE)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message")
						.value("学習ログが見つかりません。id=" + Long.MAX_VALUE));
	}

	private LogContext createContext(String accessToken) throws Exception {
		LearningResource resource = learningResourceRepository
				.search(null, null, null)
				.get(0);
		long certificationId = resource.getCertification().getId();
		String goalRequest = """
				{
				  "certificationId": %d,
				  "targetExamDate": "2027-02-28",
				  "weeklyStudyHours": 8,
				  "currentLevel": "BEGINNER",
				  "studyStartDate": "2026-08-01",
				  "status": "IN_PROGRESS"
				}
				""".formatted(certificationId);

		MvcResult goalResult = mockMvc.perform(post("/api/certification-goals")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(goalRequest))
				.andExpect(status().isCreated())
				.andReturn();
		long goalId = objectMapper.readTree(goalResult.getResponse().getContentAsString())
				.get("id")
				.asLong();

		String taskRequest = """
				{
				  "studyPlanItemId": null,
				  "title": "ログ用タスク",
				  "description": "学習ログ結合テスト用",
				  "dueDate": "2026-08-31",
				  "estimatedMinutes": 60,
				  "priority": "MEDIUM"
				}
				""";
		MvcResult taskResult = mockMvc.perform(
						post("/api/certification-goals/{goalId}/study-tasks", goalId)
								.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
								.contentType(MediaType.APPLICATION_JSON)
								.content(taskRequest))
				.andExpect(status().isCreated())
				.andReturn();
		long taskId = objectMapper.readTree(taskResult.getResponse().getContentAsString())
				.get("id")
				.asLong();
		return new LogContext(goalId, taskId, resource.getId(), certificationId);
	}

	private long createStudyLog(
			String accessToken,
			LogContext context,
			String studiedDate,
			String title) throws Exception {
		MvcResult result = mockMvc.perform(
						post("/api/certification-goals/{goalId}/study-logs", context.goalId())
								.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
								.contentType(MediaType.APPLICATION_JSON)
								.content(studyLogRequest(
										context.taskId(),
										context.resourceId(),
										studiedDate,
										60,
										title)))
				.andExpect(status().isCreated())
				.andReturn();
		return objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id")
				.asLong();
	}

	private long findResourceForDifferentCertification(long certificationId) {
		return learningResourceRepository.search(null, null, null)
				.stream()
				.filter(resource -> !resource.getCertification().getId().equals(certificationId))
				.findFirst()
				.orElseThrow()
				.getId();
	}

	private String studyLogRequest(
			Long taskId,
			Long resourceId,
			String studiedDate,
			int studyMinutes,
			String title) {
		String taskValue = taskId == null ? "null" : taskId.toString();
		String resourceValue = resourceId == null ? "null" : resourceId.toString();
		return """
				{
				  "taskId": %s,
				  "resourceId": %s,
				  "studiedDate": "%s",
				  "studyMinutes": %d,
				  "title": "%s",
				  "content": "学習内容",
				  "reflection": "振り返り",
				  "understandingLevel": "MEDIUM"
				}
				""".formatted(taskValue, resourceValue, studiedDate, studyMinutes, title);
	}

	private String registerAndLogin(String email) throws Exception {
		String registerRequest = """
				{
				  "name": "学習ログAPIテストユーザー",
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

	private record LogContext(
			long goalId,
			long taskId,
			long resourceId,
			long certificationId) {
	}
}
