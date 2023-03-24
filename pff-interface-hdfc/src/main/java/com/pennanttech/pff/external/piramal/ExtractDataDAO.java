package com.pennanttech.pff.external.piramal;

import java.sql.Timestamp;

public interface ExtractDataDAO {
	boolean extractDetails(Timestamp currentTime, Class<?> beanType, String insertQuery);
}
