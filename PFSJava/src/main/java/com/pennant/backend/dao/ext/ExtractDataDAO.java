package com.pennant.backend.dao.ext;

import java.sql.Timestamp;

public interface ExtractDataDAO {

	boolean extractDetails(Timestamp currentTime, Class<?> beanType, String insertQuery);

}
