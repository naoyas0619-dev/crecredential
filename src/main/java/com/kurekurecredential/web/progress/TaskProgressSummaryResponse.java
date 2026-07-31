package com.kurekurecredential.web.progress;

import java.math.BigDecimal;

public record TaskProgressSummaryResponse(
		long total,
		long done,
		long todo,
		BigDecimal completionRate) {
}
