package com.pennanttech.pff.external.presentment;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public interface PresentmentCustomeExtractor {

	public MapSqlParameterSource readData(Row row);

}
