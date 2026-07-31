package com.kurekurecredential.web.task;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
class StudyTaskControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CertificationRepository certificationRepository;

	@Test
	void studyTasksRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/study-tasks"))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-tasks", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(createTaskRequest(null, "未認証タスク", "2026-08-10")))
				.andExpect(status().isForbidden());
	}

	@Test
	void ownerCanCreateTaskLinkedToStudyPlanItem() throws Exception {
		String accessToken = registerAndLogin("task-create@example.com");
		GoalWithPlanItem context = createGoalWithPlanItem(accessToken);

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-tasks", context.goalId())
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createTaskRequest(
								context.planItemId(),
								"IAMの基本を学習する",
								"2026-08-10")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.goalId").value(context.goalId()))
				.andExpect(jsonPath("$.studyPlanItemId").value(context.planItemId()))
				.andExpect(jsonPath("$.title").value("IAMの基本を学習する"))
				.andExpect(jsonPath("$.description").value("結合テスト用タスク"))
				.andExpect(jsonPath("$.dueDate").value("2026-08-10"))
				.andExpect(jsonPath("$.estimatedMinutes").value(120))
				.andExpect(jsonPath("$.priority").value("HIGH"))
				.andExpect(jsonPath("$.status").value("TODO"))
				.andExpect(jsonPath("$.completedAt").value(blankOrNullString()));
	}

	@Test
	void createValidatesDueDateAndPlanItemRelationship() throws Exception {
		String accessToken = registerAndLogin("task-validation@example.com");
		GoalWithPlanItem firstContext = createGoalWithPlanItem(accessToken);
		GoalWithPlanItem secondContext = createGoalWithPlanItem(accessToken);

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-tasks", firstContext.goalId())
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createTaskRequest(
								null,
								"期間外タスク",
								"2026-07-31")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("タスク期限は資格目標の学習期間内に設定してください。"));

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-tasks", firstContext.goalId())
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createTaskRequest(
								secondContext.planItemId(),
								"別目標の項目",
								"2026-08-10")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("学習計画項目は指定した資格目標に属していません。"));
	}

	@Test
	void listReturnsOnlyCurrentUsersTasksAndSupportsFilters() throws Exception {
		String firstUserToken = registerAndLogin("task-list-first@example.com");
		String secondUserToken = registerAndLogin("task-list-second@example.com");
		GoalWithPlanItem firstContext = createGoalWithPlanItem(firstUserToken);
		GoalWithPlanItem secondContext = createGoalWithPlanItem(secondUserToken);

		long laterTaskId = createTask(
				firstUserToken,
				firstContext.goalId(),
				null,
				"後のタスク",
				"2026-08-20");
		long earlierTaskId = createTask(
				firstUserToken,
				firstContext.goalId(),
				firstContext.planItemId(),
				"先のタスク",
				"2026-08-10");
		createTask(
				secondUserToken,
				secondContext.goalId(),
				null,
				"別ユーザーのタスク",
				"2026-08-05");
		completeTask(firstUserToken, earlierTaskId);

		mockMvc.perform(get("/api/study-tasks")
						.header(HttpHeaders.AUTHORIZATION, bearer(firstUserToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.total").value(2))
				.andExpect(jsonPath("$.items[0].id").value(earlierTaskId))
				.andExpect(jsonPath("$.items[1].id").value(laterTaskId));

		mockMvc.perform(get("/api/study-tasks")
						.queryParam("goalId", String.valueOf(firstContext.goalId()))
						.queryParam("status", "DONE")
						.queryParam("dueFrom", "2026-08-01")
						.queryParam("dueTo", "2026-08-15")
						.header(HttpHeaders.AUTHORIZATION, bearer(firstUserToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.total").value(1))
				.andExpect(jsonPath("$.items[0].id").value(earlierTaskId))
				.andExpect(jsonPath("$.items[0].status").value("DONE"));
	}

	@Test
	void ownerCanGetUpdateAndCompleteTask() throws Exception {
		String accessToken = registerAndLogin("task-update@example.com");
		GoalWithPlanItem context = createGoalWithPlanItem(accessToken);
		long taskId = createTask(
				accessToken,
				context.goalId(),
				null,
				"更新前タスク",
				"2026-08-10");

		mockMvc.perform(get("/api/study-tasks/{taskId}", taskId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(taskId))
				.andExpect(jsonPath("$.status").value("TODO"));

		String updateRequest = """
				{
				  "studyPlanItemId": %d,
				  "title": "更新後タスク",
				  "description": "内容も更新",
				  "dueDate": "2026-08-15",
				  "estimatedMinutes": 180,
				  "priority": "MEDIUM",
				  "status": "DONE"
				}
				""".formatted(context.planItemId());

		mockMvc.perform(put("/api/study-tasks/{taskId}", taskId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateRequest))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.studyPlanItemId").value(context.planItemId()))
				.andExpect(jsonPath("$.title").value("更新後タスク"))
				.andExpect(jsonPath("$.estimatedMinutes").value(180))
				.andExpect(jsonPath("$.priority").value("MEDIUM"))
				.andExpect(jsonPath("$.status").value("DONE"))
				.andExpect(jsonPath("$.completedAt", not(blankOrNullString())));

		String reopenRequest = """
				{
				  "studyPlanItemId": null,
				  "title": "再開タスク",
				  "description": "再開する",
				  "dueDate": "2026-08-16",
				  "estimatedMinutes": 60,
				  "priority": "HIGH",
				  "status": "TODO"
				}
				""";

		mockMvc.perform(put("/api/study-tasks/{taskId}", taskId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(reopenRequest))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.studyPlanItemId").value(blankOrNullString()))
				.andExpect(jsonPath("$.status").value("TODO"))
				.andExpect(jsonPath("$.completedAt").value(blankOrNullString()));

		mockMvc.perform(patch("/api/study-tasks/{taskId}/complete", taskId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("DONE"))
				.andExpect(jsonPath("$.completedAt", not(blankOrNullString())));
	}

	@Test
	void otherUserCannotAccessTaskOrPlanItem() throws Exception {
		String ownerToken = registerAndLogin("task-owner@example.com");
		String otherUserToken = registerAndLogin("task-other@example.com");
		GoalWithPlanItem ownerContext = createGoalWithPlanItem(ownerToken);
		GoalWithPlanItem otherContext = createGoalWithPlanItem(otherUserToken);
		long taskId = createTask(
				ownerToken,
				ownerContext.goalId(),
				ownerContext.planItemId(),
				"所有者タスク",
				"2026-08-10");

		mockMvc.perform(get("/api/study-tasks/{taskId}", taskId)
						.header(HttpHeaders.AUTHORIZATION, bearer(otherUserToken)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));

		mockMvc.perform(patch("/api/study-tasks/{taskId}/complete", taskId)
						.header(HttpHeaders.AUTHORIZATION, bearer(otherUserToken)))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-tasks",
						otherContext.goalId())
						.header(HttpHeaders.AUTHORIZATION, bearer(otherUserToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createTaskRequest(
								ownerContext.planItemId(),
								"他人の計画項目",
								"2026-08-10")))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));
	}

	@Test
	void invalidSearchAndMissingTaskReturnErrors() throws Exception {
		String accessToken = registerAndLogin("task-error@example.com");

		mockMvc.perform(get("/api/study-tasks")
						.queryParam("dueFrom", "2026-08-20")
						.queryParam("dueTo", "2026-08-01")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("期限終了日は期限開始日以降にしてください。"));

		mockMvc.perform(get("/api/study-tasks")
						.queryParam("status", "UNKNOWN")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"));

		mockMvc.perform(get("/api/study-tasks/{taskId}", Long.MAX_VALUE)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message")
						.value("学習タスクが見つかりません。id=" + Long.MAX_VALUE));
	}

	private GoalWithPlanItem createGoalWithPlanItem(String accessToken) throws Exception {
		long certificationId = certificationRepository.findAllByOrderByNameAsc()
				.get(0)
				.getId();
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

		String planRequest = """
				{
				  "title": "タスク用学習計画",
				  "startDate": "2026-08-01",
				  "endDate": "2026-08-31",
				  "totalPlannedHours": 8,
				  "items": [
				    {
				      "weekNumber": 1,
				      "phase": "BASIC_UNDERSTANDING",
				      "title": "基礎理解",
				      "plannedHours": 8,
				      "mockExamRecommended": false,
				      "recommendedStartDate": "2026-08-01",
				      "recommendedEndDate": "2026-08-07"
				    }
				  ]
				}
				""";

		MvcResult planResult = mockMvc.perform(
						post("/api/certification-goals/{goalId}/study-plans", goalId)
								.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
								.contentType(MediaType.APPLICATION_JSON)
								.content(planRequest))
				.andExpect(status().isCreated())
				.andReturn();
		long planItemId = objectMapper.readTree(planResult.getResponse().getContentAsString())
				.get("items")
				.get(0)
				.get("id")
				.asLong();
		return new GoalWithPlanItem(goalId, planItemId);
	}

	private long createTask(
			String accessToken,
			long goalId,
			Long planItemId,
			String title,
			String dueDate) throws Exception {
		MvcResult result = mockMvc.perform(
						post("/api/certification-goals/{goalId}/study-tasks", goalId)
								.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
								.contentType(MediaType.APPLICATION_JSON)
								.content(createTaskRequest(planItemId, title, dueDate)))
				.andExpect(status().isCreated())
				.andReturn();
		return objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id")
				.asLong();
	}

	private void completeTask(String accessToken, long taskId) throws Exception {
		mockMvc.perform(patch("/api/study-tasks/{taskId}/complete", taskId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk());
	}

	private String createTaskRequest(
			Long planItemId,
			String title,
			String dueDate) {
		String planItemValue = planItemId == null ? "null" : planItemId.toString();
		return """
				{
				  "studyPlanItemId": %s,
				  "title": "%s",
				  "description": "結合テスト用タスク",
				  "dueDate": "%s",
				  "estimatedMinutes": 120,
				  "priority": "HIGH"
				}
				""".formatted(planItemValue, title, dueDate);
	}

	private String registerAndLogin(String email) throws Exception {
		String registerRequest = """
				{
				  "name": "学習タスクAPIテストユーザー",
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

	private record GoalWithPlanItem(long goalId, long planItemId) {
	}
}
