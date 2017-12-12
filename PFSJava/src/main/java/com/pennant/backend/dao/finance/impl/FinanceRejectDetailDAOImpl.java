package com.pennant.backend.dao.finance.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinanceRejectDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceRejectDetail;

public class FinanceRejectDetailDAOImpl  extends BasisNextidDaoImpl<FinanceRejectDetail> implements FinanceRejectDetailDAO {

	private static Logger logger = Logger.getLogger(FinanceRejectDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinanceRejectDetailDAOImpl() {
		super();
	} 

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void saveFinanceRejectedDetailsLog(FinanceMain financeMain) {
		logger.debug("Entering");
		FinanceRejectDetail financeRejectDetail = new FinanceRejectDetail();
		if (financeRejectDetail.getId()==Long.MIN_VALUE){
			financeRejectDetail.setId(getNextidviewDAO().getNextId("SeqFinanceRejectDetail"));
			logger.debug("get NextID:"+financeRejectDetail.getId());
		}
		financeRejectDetail.setFinReference(financeMain.getFinReference());
		financeRejectDetail.setRejectStatus(financeMain.getRejectStatus());
		financeRejectDetail.setRejectReason(financeMain.getRejectReason());
		financeRejectDetail.setRoleCode(financeMain.getRoleCode());
		financeRejectDetail.setRejectedUser(financeMain.getUserDetails().getUserId());
		financeRejectDetail.setRejectedDate(financeMain.getLastMntOn());
		
		StringBuilder insertSql = new StringBuilder("Insert Into  FinanceRejectDetail ");
		insertSql.append(" (RejectId,FinReference, RejectStatus , RejectReason, RoleCode, RejectedUser, RejectedDate)");
		insertSql.append(" Values( :RejectId, :FinReference, :RejectStatus , :RejectReason, :RoleCode, :RejectedUser, :RejectedDate )");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRejectDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
}


