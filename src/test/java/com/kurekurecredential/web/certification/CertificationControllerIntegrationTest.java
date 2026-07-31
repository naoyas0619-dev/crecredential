package com.kurekurecredential.web.certification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class CertificationControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void certificationsRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/certifications"))
				.andExpect(status().isForbidden());
	}

	@Test
	void authenticatedUserCanGetCertificationList() throws Exception {
		String accessToken = registerAndLogin("certification-list@example.com");

		mockMvc.perform(get("/api/certifications")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(6))
				.andExpect(jsonPath("$.total").value(6))
				.andExpect(jsonPath("$.items[0].id").isNumber())
				.andExpect(jsonPath("$.items[0].name").isString())
				.andExpect(jsonPath("$.items[0].provider").isString())
				.andExpect(jsonPath("$.items[0].difficulty").isString())
				.andExpect(jsonPath("$.items[0].recommendedStudyHours").isNumber())
				.andExpect(jsonPath("$.items[0].description").doesNotExist());
	}

	@Test
	void keywordFiltersCertificationNameAndProvider() throws Exception {
		String accessToken = registerAndLogin("certification-search@example.com");

		mockMvc.perform(get("/api/certifications")
						.queryParam("keyword", " AWS ")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.total").value(2))
				.andExpect(jsonPath("$.items[0].provider").value("AWS"))
				.andExpect(jsonPath("$.items[1].provider").value("AWS"));
	}

	@Test
	void authenticatedUserCanGetCertificationDetail() throws Exception {
		String accessToken = registerAndLogin("certification-detail@example.com");
		long certificationId = findCertificationId(accessToken, "Solutions Architect");

		mockMvc.perform(get("/api/certifications/{certificationId}", certificationId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(certificationId))
				.andExpect(jsonPath("$.name")
						.value("AWS Certified Solutions Architect - Associate"))
				.andExpect(jsonPath("$.provider").value("AWS"))
				.andExpect(jsonPath("$.difficulty").value("ASSOCIATE"))
				.andExpect(jsonPath("$.description").isString())
				.andExpect(jsonPath("$.recommendedStudyHours").value(120))
				.andExpect(jsonPath("$.examFormat").isString())
				.andExpect(jsonPath("$.passingScore").value(720))
				.andExpect(jsonPath("$.officialUrl").isString())
				.andExpect(jsonPath("$.validityPeriod").value("3年"));
	}

	@Test
	void missingCertificationReturnsNotFound() throws Exception {
		String accessToken = registerAndLogin("certification-not-found@example.com");

		mockMvc.perform(get("/api/certifications/{certificationId}", Long.MAX_VALUE)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("NOT_FOUND"))
				.andExpect(jsonPath("$.message").value(
						"資格が見つかりません。id=" + Long.MAX_VALUE));
	}

	private String registerAndLogin(String email) throws Exception {
		String registerRequest = """
				{
				  "name": "資格APIテストユーザー",
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

	private long findCertificationId(String accessToken, String keyword) throws Exception {
		MvcResult result = mockMvc.perform(get("/api/certifications")
						.queryParam("keyword", keyword)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn();

		return objectMapper.readTree(result.getResponse().getContentAsString())
				.get("items")
				.get(0)
				.get("id")
				.asLong();
	}
}
