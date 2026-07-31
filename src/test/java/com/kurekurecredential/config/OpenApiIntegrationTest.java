package com.kurekurecredential.config;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void openApiDocumentIsPublicAndContainsJwtSecurityDefinition()
			throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(
						MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.info.title")
						.value("クレクレデンシャル API"))
				.andExpect(jsonPath("$.info.version").value("v1"))
				.andExpect(jsonPath(
						"$.components.securitySchemes.bearerAuth.type")
						.value("http"))
				.andExpect(jsonPath(
						"$.components.securitySchemes.bearerAuth.scheme")
						.value("bearer"))
				.andExpect(jsonPath("$.security[0].bearerAuth").isArray())
				.andExpect(jsonPath(
						"$.paths['/api/certification-goals/{goalId}/summary'].get")
						.exists())
				.andExpect(jsonPath(
						"$.paths['/api/auth/login'].post.security")
						.isEmpty())
				.andExpect(jsonPath(
						"$.paths['/api/auth/register'].post.security")
						.isEmpty());
	}

	@Test
	void swaggerUiIsPublic() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string(
						"Location",
						containsString("/swagger-ui/index.html")));

		mockMvc.perform(get("/swagger-ui/index.html"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(
						MediaType.TEXT_HTML));
	}
}
