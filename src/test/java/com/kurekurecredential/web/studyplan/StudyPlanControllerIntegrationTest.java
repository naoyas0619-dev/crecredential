package com.kurekurecredential.web.studyplan;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class StudyPlanControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CertificationRepository certificationRepository;

	@Test
	void studyPlansRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/certification-goals/{goalId}/study-plans", 1))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-plans", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(singleItemPlanRequest(
								"未認証テスト",
								"2026-08-01",
								"2026-08-14")))
				.andExpect(status().isForbidden());
	}

	@Test
	void ownerCanCreateStudyPlanWithSortedItems() throws Exception {
		String accessToken = registerAndLogin("study-plan-create@example.com");
		long goalId = createGoal(accessToken);

		String request = """
				{
				  "title": "AWS 2週間学習計画",
				  "startDate": "2026-08-01",
				  "endDate": "2026-08-14",
				  "totalPlannedHours": 16,
				  "memo": "基礎から順番に進める。",
				  "items": [
				    {
				      "weekNumber": 2,
				      "phase": "PRACTICAL_EXERCISE",
				      "title": "構成演習",
				      "description": "AWS上で構成を試す。",
				      "plannedHours": 8,
				      "mockExamRecommended": false,
				      "recommendedStartDate": "2026-08-08",
				      "recommendedEndDate": "2026-08-14"
				    },
				    {
				      "weekNumber": 1,
				      "phase": "BASIC_UNDERSTANDING",
				      "title": "基礎理解",
				      "description": "主要サービスを学ぶ。",
				      "plannedHours": 8,
				      "mockExamRecommended": false,
				      "recommendedStartDate": "2026-08-01",
				      "recommendedEndDate": "2026-08-07"
				    }
				  ]
				}
				""";

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-plans", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.goalId").value(goalId))
				.andExpect(jsonPath("$.title").value("AWS 2週間学習計画"))
				.andExpect(jsonPath("$.startDate").value("2026-08-01"))
				.andExpect(jsonPath("$.endDate").value("2026-08-14"))
				.andExpect(jsonPath("$.totalPlannedHours").value(16))
				.andExpect(jsonPath("$.memo").value("基礎から順番に進める。"))
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.items[0].weekNumber").value(1))
				.andExpect(jsonPath("$.items[0].phase").value("BASIC_UNDERSTANDING"))
				.andExpect(jsonPath("$.items[1].weekNumber").value(2))
				.andExpect(jsonPath("$.items[1].phase").value("PRACTICAL_EXERCISE"));
	}

	@Test
	void createRejectsInvalidPlanAndItemDates() throws Exception {
		String accessToken = registerAndLogin("study-plan-dates@example.com");
		long goalId = createGoal(accessToken);

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-plans", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(singleItemPlanRequest(
								"日付逆転",
								"2026-08-14",
								"2026-08-01")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.message")
						.value("学習計画の終了日は開始日以降にしてください。"));

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-plans", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(singleItemPlanRequest(
								"目標期間外",
								"2026-07-01",
								"2026-07-14")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("学習計画は資格目標の学習期間内に設定してください。"));

		String itemOutsidePlan = singleItemPlanRequest(
				"項目期間外",
				"2026-08-01",
				"2026-08-14")
				.replace(
						"\"recommendedEndDate\": \"2026-08-01\"",
						"\"recommendedEndDate\": \"2026-08-20\"");

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-plans", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(itemOutsidePlan))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("学習計画項目の推奨期間は学習計画の期間内に設定してください。weekNumber=1"));
	}

	@Test
	void createRejectsEmptyItemsAndDuplicateWeekNumbers() throws Exception {
		String accessToken = registerAndLogin("study-plan-items@example.com");
		long goalId = createGoal(accessToken);

		String emptyItemsRequest = """
				{
				  "title": "項目なし",
				  "startDate": "2026-08-01",
				  "endDate": "2026-08-14",
				  "totalPlannedHours": 0,
				  "items": []
				}
				""";

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-plans", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(emptyItemsRequest))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

		String duplicateWeeksRequest = """
				{
				  "title": "週番号重複",
				  "startDate": "2026-08-01",
				  "endDate": "2026-08-14",
				  "totalPlannedHours": 16,
				  "items": [
				    {
				      "weekNumber": 1,
				      "phase": "BASIC_UNDERSTANDING",
				      "title": "基礎1",
				      "plannedHours": 8,
				      "mockExamRecommended": false
				    },
				    {
				      "weekNumber": 1,
				      "phase": "PRACTICAL_EXERCISE",
				      "title": "基礎2",
				      "plannedHours": 8,
				      "mockExamRecommended": false
				    }
				  ]
				}
				""";

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-plans", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(duplicateWeeksRequest))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("学習計画項目の週番号が重複しています。weekNumber=1"));
	}

	@Test
	void ownerCanListAndGetStudyPlans() throws Exception {
		String accessToken = registerAndLogin("study-plan-list@example.com");
		long goalId = createGoal(accessToken);

		long laterPlanId = createStudyPlan(
				accessToken,
				goalId,
				"後の計画",
				"2026-09-01",
				"2026-09-14");
		long earlierPlanId = createStudyPlan(
				accessToken,
				goalId,
				"先の計画",
				"2026-08-01",
				"2026-08-14");

		mockMvc.perform(get("/api/certification-goals/{goalId}/study-plans", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.total").value(2))
				.andExpect(jsonPath("$.items[0].id").value(earlierPlanId))
				.andExpect(jsonPath("$.items[0].title").value("先の計画"))
				.andExpect(jsonPath("$.items[1].id").value(laterPlanId));

		mockMvc.perform(get("/api/study-plans/{studyPlanId}", earlierPlanId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(earlierPlanId))
				.andExpect(jsonPath("$.goalId").value(goalId))
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].weekNumber").value(1));
	}

	@Test
	void otherUserCannotCreateListOrGetStudyPlan() throws Exception {
		String ownerToken = registerAndLogin("study-plan-owner@example.com");
		String otherUserToken = registerAndLogin("study-plan-other@example.com");
		long ownerGoalId = createGoal(ownerToken);
		long studyPlanId = createStudyPlan(
				ownerToken,
				ownerGoalId,
				"所有者の計画",
				"2026-08-01",
				"2026-08-14");

		mockMvc.perform(post("/api/certification-goals/{goalId}/study-plans", ownerGoalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(otherUserToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(singleItemPlanRequest(
								"不正作成",
								"2026-09-01",
								"2026-09-14")))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));

		mockMvc.perform(get("/api/certification-goals/{goalId}/study-plans", ownerGoalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(otherUserToken)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));

		mockMvc.perform(get("/api/study-plans/{studyPlanId}", studyPlanId)
						.header(HttpHeaders.AUTHORIZATION, bearer(otherUserToken)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));
	}

	@Test
	void missingGoalAndStudyPlanReturnNotFound() throws Exception {
		String accessToken = registerAndLogin("study-plan-not-found@example.com");

		mockMvc.perform(get("/api/certification-goals/{goalId}/study-plans", Long.MAX_VALUE)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("NOT_FOUND"));

		mockMvc.perform(get("/api/study-plans/{studyPlanId}", Long.MAX_VALUE)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("NOT_FOUND"))
				.andExpect(jsonPath("$.message")
						.value("学習計画が見つかりません。id=" + Long.MAX_VALUE));
	}

	private long createGoal(String accessToken) throws Exception {
		long certificationId = certificationRepository.findAllByOrderByNameAsc()
				.get(0)
				.getId();
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

	private long createStudyPlan(
			String accessToken,
			long goalId,
			String title,
			String startDate,
			String endDate) throws Exception {
		MvcResult result = mockMvc.perform(
						post("/api/certification-goals/{goalId}/study-plans", goalId)
								.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
								.contentType(MediaType.APPLICATION_JSON)
								.content(singleItemPlanRequest(title, startDate, endDate)))
				.andExpect(status().isCreated())
				.andReturn();

		return objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id")
				.asLong();
	}

	private String singleItemPlanRequest(
			String title,
			String startDate,
			String endDate) {
		return """
				{
				  "title": "%s",
				  "startDate": "%s",
				  "endDate": "%s",
				  "totalPlannedHours": 8,
				  "memo": "結合テスト用",
				  "items": [
				    {
				      "weekNumber": 1,
				      "phase": "BASIC_UNDERSTANDING",
				      "title": "基礎理解",
				      "description": "主要分野を学習する。",
				      "plannedHours": 8,
				      "mockExamRecommended": false,
				      "recommendedStartDate": "%s",
				      "recommendedEndDate": "%s"
				    }
				  ]
				}
				""".formatted(title, startDate, endDate, startDate, startDate);
	}

	private String registerAndLogin(String email) throws Exception {
		String registerRequest = """
				{
				  "name": "学習計画APIテストユーザー",
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
