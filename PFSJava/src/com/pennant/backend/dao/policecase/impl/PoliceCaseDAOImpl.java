package com.pennant.backend.dao.policecase.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;



import com.pennant.backend.dao.policecase.PoliceCaseDAO;
import com.pennant.backend.model.policecase.PoliceCase;

public class PoliceCaseDAOImpl  implements PoliceCaseDAO {
	private static Logger logger = Logger.getLogger(PoliceCaseDAOImpl.class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	@Override
	public void saveList(List<PoliceCase> policeCase) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder("Insert Into FinPoliceCaseDetail ");
		insertSql.append("(FinReference, CustCIF, CustFName, CustLName , CustDOB, CustCRCPR, CustPassPort ,CustMobileNumber, ");
		insertSql.append(" CustNationality, CustProduct, PoliceCaseRule, Override, OverrideUser ) ");
		insertSql.append(" Values(:FinReference, :CustCIF , :CustFName , :CustLName , :CustDOB ,  :CustCRCPR , :CustPassPort ,");
		insertSql.append(" :CustMobileNumber , :CustNationality , :CustProduct , :PoliceCaseRule, :Override , :OverrideUser)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(policeCase.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public List<PoliceCase> fetchPoliceCase(String finReference, String queryCode) {
		logger.debug("Entering");
		
		PoliceCase policeCase = new PoliceCase();
		policeCase.setFinReference(finReference);
		policeCase.setPoliceCaseRule(queryCode);

		StringBuilder selectSql = new StringBuilder(" Select FinReference , CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustDOB , CustCRCPR ,CustPassPort , CustMobileNumber , CustNationality , ");
		selectSql.append(" CustProduct , PoliceCaseRule , Override , OverrideUser ");
		selectSql.append(" From FinPoliceCaseDetail");
		selectSql.append(" Where FinReference =:FinReference AND PoliceCaseRule=:PoliceCaseRule ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCase);
		RowMapper<PoliceCase> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PoliceCase.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

}
