package com.kurekurecredential.web.goal;

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
class CertificationGoalControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CertificationRepository certificationRepository;

	@Test
	void certificationGoalsRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/certification-goals"))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/api/certification-goals")
						.contentType(MediaType.APPLICATION_JSON)
						.content(createRequestBody(firstCertificationId(), "IN_PROGRESS")))
				.andExpect(status().isForbidden());
	}

	@Test
	void authenticatedUserCanCreateCertificationGoal() throws Exception {
		String accessToken = registerAndLogin("goal-create@example.com");
		long certificationId = firstCertificationId();

		mockMvc.perform(post("/api/certification-goals")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createRequestBody(certificationId, "IN_PROGRESS")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.certification.id").value(certificationId))
				.andExpect(jsonPath("$.certification.name").isString())
				.andExpect(jsonPath("$.targetExamDate").value("2027-02-28"))
				.andExpect(jsonPath("$.weeklyStudyHours").value(8))
				.andExpect(jsonPath("$.currentLevel").value("BEGINNER"))
				.andExpect(jsonPath("$.studyStartDate").value("2026-08-01"))
				.andExpect(jsonPath("$.status").value("IN_PROGRESS"));
	}

	@Test
	void createRejectsInvalidValues() throws Exception {
		String accessToken = registerAndLogin("goal-validation@example.com");
		long certificationId = firstCertificationId();

		String invalidHoursRequest = """
				{
				  "certificationId": %d,
				  "targetExamDate": "2027-02-28",
				  "weeklyStudyHours": 0,
				  "currentLevel": "BEGINNER",
				  "studyStartDate": "2026-08-01",
				  "status": "IN_PROGRESS"
				}
				""".formatted(certificationId);

		mockMvc.perform(post("/api/certification-goals")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(invalidHoursRequest))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.details[0].field").value("weeklyStudyHours"));

		String invalidDateRequest = """
				{
				  "certificationId": %d,
				  "targetExamDate": "2026-07-31",
				  "weeklyStudyHours": 8,
				  "currentLevel": "BEGINNER",
				  "studyStartDate": "2026-08-01",
				  "status": "IN_PROGRESS"
				}
				""".formatted(certificationId);

		mockMvc.perform(post("/api/certification-goals")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(invalidDateRequest))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.message")
						.value("目標試験日は学習開始日以降の日付にしてください。"));
	}

	@Test
	void createRejectsMissingCertificationAndInvalidEnum() throws Exception {
		String accessToken = registerAndLogin("goal-invalid-reference@example.com");

		mockMvc.perform(post("/api/certification-goals")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createRequestBody(Long.MAX_VALUE, "IN_PROGRESS")))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("NOT_FOUND"));

		mockMvc.perform(post("/api/certification-goals")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createRequestBody(firstCertificationId(), "UNKNOWN")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"));
	}

	@Test
	void listReturnsOnlyCurrentUsersGoalsAndFiltersByStatus() throws Exception {
		String firstUserToken = registerAndLogin("goal-list-first@example.com");
		String secondUserToken = registerAndLogin("goal-list-second@example.com");
		long certificationId = firstCertificationId();

		createGoal(firstUserToken, certificationId, "IN_PROGRESS");
		createGoal(firstUserToken, certificationId, "PAUSED");
		createGoal(secondUserToken, certificationId, "IN_PROGRESS");

		mockMvc.perform(get("/api/certification-goals")
						.header(HttpHeaders.AUTHORIZATION, bearer(firstUserToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.total").value(2));

		mockMvc.perform(get("/api/certification-goals")
						.queryParam("status", "PAUSED")
						.header(HttpHeaders.AUTHORIZATION, bearer(firstUserToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.total").value(1))
				.andExpect(jsonPath("$.items[0].status").value("PAUSED"));

		mockMvc.perform(get("/api/certification-goals")
						.queryParam("status", "UNKNOWN")
						.header(HttpHeaders.AUTHORIZATION, bearer(firstUserToken)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"));
	}

	@Test
	void ownerCanGetAndUpdateCertificationGoal() throws Exception {
		String accessToken = registerAndLogin("goal-update@example.com");
		long goalId = createGoal(accessToken, firstCertificationId(), "IN_PROGRESS");

		mockMvc.perform(get("/api/certification-goals/{goalId}", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(goalId))
				.andExpect(jsonPath("$.status").value("IN_PROGRESS"));

		String updateRequest = """
				{
				  "targetExamDate": "2027-03-31",
				  "weeklyStudyHours": 12,
				  "currentLevel": "BASIC",
				  "studyStartDate": "2026-08-15",
				  "status": "PAUSED"
				}
				""";

		mockMvc.perform(put("/api/certification-goals/{goalId}", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateRequest))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(goalId))
				.andExpect(jsonPath("$.targetExamDate").value("2027-03-31"))
				.andExpect(jsonPath("$.weeklyStudyHours").value(12))
				.andExpect(jsonPath("$.currentLevel").value("BASIC"))
				.andExpect(jsonPath("$.studyStartDate").value("2026-08-15"))
				.andExpect(jsonPath("$.status").value("PAUSED"));
	}

	@Test
	void otherUserCannotGetOrUpdateCertificationGoal() throws Exception {
		String ownerToken = registerAndLogin("goal-owner@example.com");
		String otherUserToken = registerAndLogin("goal-other-user@example.com");
		long goalId = createGoal(ownerToken, firstCertificationId(), "IN_PROGRESS");

		mockMvc.perform(get("/api/certification-goals/{goalId}", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(otherUserToken)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));

		String updateRequest = """
				{
				  "targetExamDate": "2027-03-31",
				  "weeklyStudyHours": 12,
				  "currentLevel": "BASIC",
				  "studyStartDate": "2026-08-15",
				  "status": "CANCELED"
				}
				""";

		mockMvc.perform(put("/api/certification-goals/{goalId}", goalId)
						.header(HttpHeaders.AUTHORIZATION, bearer(otherUserToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateRequest))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));
	}

	@Test
	void missingCertificationGoalReturnsNotFound() throws Exception {
		String accessToken = registerAndLogin("goal-not-found@example.com");

		mockMvc.perform(get("/api/certification-goals/{goalId}", Long.MAX_VALUE)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("NOT_FOUND"))
				.andExpect(jsonPath("$.message")
						.value("資格目標が見つかりません。id=" + Long.MAX_VALUE));
	}

	private long firstCertificationId() {
		return certificationRepository.findAllByOrderByNameAsc().get(0).getId();
	}

	private long createGoal(
			String accessToken,
			long certificationId,
			String statusValue) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/certification-goals")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createRequestBody(certificationId, statusValue)))
				.andExpect(status().isCreated())
				.andReturn();

		return objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id")
				.asLong();
	}

	private String createRequestBody(long certificationId, String statusValue) {
		return """
				{
				  "certificationId": %d,
				  "targetExamDate": "2027-02-28",
				  "weeklyStudyHours": 8,
				  "currentLevel": "BEGINNER",
				  "studyStartDate": "2026-08-01",
				  "status": "%s"
				}
				""".formatted(certificationId, statusValue);
	}

	private String registerAndLogin(String email) throws Exception {
		String registerRequest = """
				{
				  "name": "資格目標APIテストユーザー",
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
