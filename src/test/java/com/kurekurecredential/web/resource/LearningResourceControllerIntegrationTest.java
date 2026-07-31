package com.kurekurecredential.web.resource;

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
class LearningResourceControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CertificationRepository certificationRepository;

	@Test
	void learningResourcesRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/learning-resources"))
				.andExpect(status().isForbidden());
	}

	@Test
	void authenticatedUserCanGetInitialLearningResources() throws Exception {
		String accessToken = registerAndLogin("resource-list@example.com");

		mockMvc.perform(get("/api/learning-resources")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(12))
				.andExpect(jsonPath("$.total").value(12))
				.andExpect(jsonPath("$.items[0].id").isNumber())
				.andExpect(jsonPath("$.items[0].certificationId").isNumber())
				.andExpect(jsonPath("$.items[0].title").isString())
				.andExpect(jsonPath("$.items[0].url").isString())
				.andExpect(jsonPath("$.items[0].resourceType").isString())
				.andExpect(jsonPath("$.items[0].recommendationScore").value(5))
				.andExpect(jsonPath("$.items[0].official").value(true))
				.andExpect(jsonPath("$.items[0].memo").doesNotExist());
	}

	@Test
	void listFiltersByCertificationTypeAndTargetLevel() throws Exception {
		String accessToken = registerAndLogin("resource-filter@example.com");
		long certificationId = certificationRepository.findAllByOrderByNameAsc()
				.stream()
				.filter(certification -> certification.getName()
						.equals("AWS Certified Solutions Architect - Associate"))
				.findFirst()
				.orElseThrow()
				.getId();

		mockMvc.perform(get("/api/learning-resources")
						.queryParam("certificationId", String.valueOf(certificationId))
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.total").value(2))
				.andExpect(jsonPath("$.items[0].certificationId").value(certificationId))
				.andExpect(jsonPath("$.items[1].certificationId").value(certificationId));

		mockMvc.perform(get("/api/learning-resources")
						.queryParam("certificationId", String.valueOf(certificationId))
						.queryParam("resourceType", "OFFICIAL_DOCUMENT")
						.queryParam("targetLevel", "BEGINNER")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.total").value(1))
				.andExpect(jsonPath("$.items[0].resourceType")
						.value("OFFICIAL_DOCUMENT"))
				.andExpect(jsonPath("$.items[0].targetLevel").value("BEGINNER"));
	}

	@Test
	void authenticatedUserCanGetLearningResourceDetail() throws Exception {
		String accessToken = registerAndLogin("resource-detail@example.com");

		MvcResult listResult = mockMvc.perform(get("/api/learning-resources")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andReturn();
		long resourceId = objectMapper
				.readTree(listResult.getResponse().getContentAsString())
				.get("items")
				.get(0)
				.get("id")
				.asLong();

		mockMvc.perform(get("/api/learning-resources/{resourceId}", resourceId)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(resourceId))
				.andExpect(jsonPath("$.certificationId").isNumber())
				.andExpect(jsonPath("$.certificationName").isString())
				.andExpect(jsonPath("$.title").isString())
				.andExpect(jsonPath("$.author").isString())
				.andExpect(jsonPath("$.recommendationScore").value(5))
				.andExpect(jsonPath("$.paid").value(false))
				.andExpect(jsonPath("$.official").value(true))
				.andExpect(jsonPath("$.memo").isString());
	}

	@Test
	void invalidFiltersAndMissingResourcesReturnErrors() throws Exception {
		String accessToken = registerAndLogin("resource-error@example.com");

		mockMvc.perform(get("/api/learning-resources")
						.queryParam("certificationId", String.valueOf(Long.MAX_VALUE))
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("NOT_FOUND"));

		mockMvc.perform(get("/api/learning-resources")
						.queryParam("resourceType", "UNKNOWN")
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"));

		mockMvc.perform(get("/api/learning-resources/{resourceId}", Long.MAX_VALUE)
						.header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("NOT_FOUND"))
				.andExpect(jsonPath("$.message")
						.value("教材が見つかりません。id=" + Long.MAX_VALUE));
	}

	private String registerAndLogin(String email) throws Exception {
		String registerRequest = """
				{
				  "name": "教材APIテストユーザー",
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
