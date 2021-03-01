package com.pennant.backend.dao.lenderupload.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.lenderupload.LenderDataUploadDAO;
import com.pennant.backend.model.lenderdataupload.LenderDataUpload;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class LenderDataUploadDAOImpl extends BasicDao<LenderDataUpload> implements LenderDataUploadDAO {

	private static Logger logger = LogManager.getLogger(LenderDataUploadDAOImpl.class);

	@Override
	public void save(LenderDataUpload lenderDataUpload) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder("Insert Into LenderDataUpload");
		insertSql.append(" (UploadHeaderId,LenderId,FinReference,");
		insertSql.append("Status,Reason)");
		insertSql.append("values(:UploadHeaderId,:LenderId,:FinReference,");
		insertSql.append(":Status, :Reason)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(lenderDataUpload);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}
