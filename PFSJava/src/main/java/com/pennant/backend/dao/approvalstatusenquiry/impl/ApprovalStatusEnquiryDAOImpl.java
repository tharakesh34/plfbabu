package com.pennant.backend.dao.approvalstatusenquiry.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.approvalstatusenquiry.ApprovalStatusEnquiryDAO;
import com.pennant.backend.model.finance.AuditTransaction;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ApprovalStatusEnquiryDAOImpl extends BasicDao<CustomerFinanceDetail> implements ApprovalStatusEnquiryDAO {
	private static final Logger logger = Logger.getLogger(ApprovalStatusEnquiryDAOImpl.class);

	private NamedParameterJdbcTemplate auditJdbcTemplate;

	public ApprovalStatusEnquiryDAOImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pennant.backend.dao.finance.ApprovaStatusEnquiryDAO#getCustomerFinanceMainById(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public CustomerFinanceDetail getCustomerFinanceMainById(String id, String type, boolean facility) {

		logger.debug("Entering");
		CustomerFinanceDetail customerFinanceDetail = new CustomerFinanceDetail();
		customerFinanceDetail.setFinReference(id);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference, FinBranch, CustID, CustCIF, CustShrtName,");
		selectSql.append(" RoleCode, NextRoleCode, RecordType, DeptDesc, PrvRoleDesc, NextRoleDesc, ");
		selectSql.append(" FinType, FinAmount, FinStartDate, LastMntBy,UsrFName,lastMntByUser ,FinCcy,FinTypeDesc ");
		if (facility) {
			selectSql.append(" from CustomerFacilityDetails");
		} else {
			selectSql.append(",feeChargeAmt from CustomerFinanceDetails");

		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerFinanceDetail);
		RowMapper<CustomerFinanceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerFinanceDetail.class);

		try {
			customerFinanceDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerFinanceDetail = null;
		}
		logger.debug("Leaving");
		return customerFinanceDetail;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pennant.backend.dao.finance.ApprovaStatusEnquiryDAO#getFinTransactionsList(java.lang.String, boolean)
	 */
	@Override
	public List<AuditTransaction> getFinTransactionsList(String id, boolean approvedFinance, boolean facility,
			String auditEvent) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT AuditReference, AuditDate, RoleCode, RoleDesc, LastMntBy, RecordStatus, RecordType, UsrName ");
		if (facility) {
			sql.append(" from FacilityStsAprvlInquiry_View ");
			sql.append(" Where AuditReference =:FinReference  ");
		} else {
			sql.append(" from FinStsAprvlInquiry_View ");
			sql.append(" Where AuditReference =:FinReference and AUDITTRANTYPE='W' ");
		}
		if (StringUtils.isNotEmpty(auditEvent)) {
			sql.append(" and AuditEvent = :FinEvent ");
		}
		if (approvedFinance) {
			sql.append(" and RecordStatus <> :RecordStatus");
		}
		sql.append(" Order by AuditDate ");

		logger.trace(Literal.SQL + sql.toString());

		CustomerFinanceDetail customerFinanceDetail = new CustomerFinanceDetail();
		customerFinanceDetail.setFinReference(id);
		customerFinanceDetail.setFinEvent(auditEvent);
		customerFinanceDetail.setRecordStatus("Saved");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerFinanceDetail);
		RowMapper<AuditTransaction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AuditTransaction.class);
		logger.debug(Literal.LEAVING);

		return this.auditJdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<CustomerFinanceDetail> getListOfCustomerFinanceDetailById(long custID, String type, boolean facility) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinBranch, CustId, CustCIF, CustShrtName, FinReference, RoleCode");
		sql.append(", NextRoleCode, RecordType, DeptDesc, PrvRoleDesc, NextRoleDesc, FinType, FinAmount");
		sql.append(", FinStartDate, LastMntBy, UsrFName, LastMntByUser, FinCcy, FinTypeDesc, LovDescFinDivision"); // LovDescFinDivision not available in CustomerFacilityDetails_View 

		if (facility) {
			sql.append(" from CustomerFacilityDetails");
		} else {
			sql.append(", FeeChargeAmt, NumberOfTerms, FirstRepay");
			sql.append(" from CustomerFinanceDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where custId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, custID);
				}
			}, new RowMapper<CustomerFinanceDetail>() {
				@Override
				public CustomerFinanceDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					CustomerFinanceDetail cfd = new CustomerFinanceDetail();

					cfd.setFinReference(rs.getString("FinReference"));
					cfd.setFinBranch(rs.getString("FinBranch"));
					cfd.setCustId(rs.getLong("CustId"));
					cfd.setCustCIF(rs.getString("CustCIF"));
					cfd.setCustShrtName(rs.getString("CustShrtName"));
					cfd.setRoleCode(rs.getString("RoleCode"));
					cfd.setNextRoleCode(rs.getString("NextRoleCode"));
					cfd.setRecordType(rs.getString("RecordType"));
					cfd.setDeptDesc(rs.getString("DeptDesc"));
					cfd.setPrvRoleDesc(rs.getString("PrvRoleDesc"));
					cfd.setNextRoleDesc(rs.getString("NextRoleDesc"));
					cfd.setFinType(rs.getString("FinType"));
					cfd.setFinAmount(rs.getBigDecimal("FinAmount"));
					cfd.setFinStartDate(rs.getTimestamp("FinStartDate"));
					cfd.setLastMntBy(rs.getLong("LastMntBy"));
					cfd.setUsrFName(rs.getString("UsrFName"));
					cfd.setLastMntByUser(rs.getString("LastMntByUser"));
					cfd.setFinCcy(rs.getString("FinCcy"));
					cfd.setFinTypeDesc(rs.getString("FinTypeDesc"));
					//	cfd.setLovDescFinDivision(rs.getString("LovDescFinDivision")); (In bean its not available)

					if (!facility) {
						cfd.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
						cfd.setFirstRepay(rs.getBigDecimal("FirstRepay"));
						cfd.setNumberOfTerms(rs.getInt("NumberOfTerms"));
					}

					return cfd;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	public void setAuditDataSource(DataSource auditDataSource) {
		this.auditJdbcTemplate = new NamedParameterJdbcTemplate(auditDataSource);
	}

}
