package com.kurekurecredential.web.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

final class JsonTestHelper {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private JsonTestHelper() {
	}

	static String extractString(MvcResult result, String fieldName) throws Exception {
		JsonNode jsonNode = OBJECT_MAPPER.readTree(result.getResponse().getContentAsString());
		return jsonNode.get(fieldName).asText();
	}
}
