package com.pennant.pff.batch.job;

import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomSerializer extends Jackson2ExecutionContextStringSerializer {
	public CustomSerializer() {
		super(new String[] { "java.util.ArrayList"

				, "java.util.Arrays$ArrayList"

				, "java.util.LinkedList"

				, "java.util.Collections$EmptyList"

				, "java.util.Collections$EmptyMap"

				, "java.util.Collections$EmptySet"

				, "java.util.Collections$UnmodifiableRandomAccessList"

				, "java.util.Collections$UnmodifiableList"

				, "java.util.Collections$UnmodifiableMap"

				, "java.util.Collections$UnmodifiableSet"

				, "java.util.Collections$SingletonList"

				, "java.util.Collections$SingletonMap"

				, "java.util.Collections$SingletonSet"

				, "java.util.Date"

				, "java.time.Instant"

				, "java.time.Duration"

				, "java.time.LocalDate"

				, "java.time.LocalTime"

				, "java.time.LocalDateTime"

				, "java.sql.Date"

				, "java.sql.Timestamp"

				, "java.net.URL"

				, "java.util.TreeMap"

				, "java.util.HashMap"

				, "java.util.LinkedHashMap"

				, "java.util.TreeSet"

				, "java.util.HashSet"

				, "java.util.LinkedHashSet"

				, "java.lang.Boolean"

				, "java.lang.Byte"

				, "java.lang.Short"

				, "java.lang.Integer"

				, "java.lang.Long"

				, "java.lang.Double"

				, "java.lang.Float"

				, "java.math.BigDecimal"

				, "java.math.BigInteger"

				, "java.lang.String"

				, "java.lang.Character"

				, "java.lang.CharSequence"

				, "java.util.Properties"

				, "[Ljava.util.Properties"

				, "org.springframework.batch.core.JobParameter"

				, "org.springframework.batch.core.JobParameters"

				, "org.springframework.batch.core.jsr.partition.JsrPartitionHandler$PartitionPlanState"

				, "com.pennant.backend.model.eventproperties.EventProperties"

				, "com.pennanttech.dataengine.model.DataEngineStatus" });
		// setObjectMapper(objectMapper());
	}

	@SuppressWarnings("deprecation")
	private ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		objectMapper.enableDefaultTyping();
		return objectMapper;
	}
}
