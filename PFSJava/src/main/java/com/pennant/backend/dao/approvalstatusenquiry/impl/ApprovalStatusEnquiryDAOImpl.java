package com.pennant.backend.dao.approvalstatusenquiry.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.approvalstatusenquiry.ApprovalStatusEnquiryDAO;
import com.pennant.backend.model.finance.AuditTransaction;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ApprovalStatusEnquiryDAOImpl extends BasicDao<CustomerFinanceDetail> implements ApprovalStatusEnquiryDAO {
	private static final Logger logger = LogManager.getLogger(ApprovalStatusEnquiryDAOImpl.class);

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
		RowMapper<CustomerFinanceDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerFinanceDetail.class);

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

	private String commaJoin(List<String> finReferences) {
		return finReferences.stream().map(e -> "?").collect(Collectors.joining(","));
	}

	@Override
	public List<AuditTransaction> getFinTransactionsList(List<String> finReferences, boolean approvedFinance,
			boolean facility, String auditEvent) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select AuditReference, AuditDate, RoleCode, RoleDesc, LastMntBy");
		sql.append(", RecordStatus, RecordType, UsrName ");
		if (facility) {
			sql.append(" from FacilityStsAprvlInquiry_View");
			sql.append(" Where AuditReference In (");
			sql.append(commaJoin(finReferences));
			sql.append(")");
		} else {
			sql.append(" from FinStsAprvlInquiry_View");
			sql.append(" Where AuditReference In(");
			sql.append(commaJoin(finReferences));
			sql.append(")");
			sql.append(" and AUDITTRANTYPE = ?");
		}

		if (StringUtils.isNotEmpty(auditEvent)) {
			sql.append(" and AuditEvent = ?");
		}

		if (approvedFinance) {
			sql.append(" and RecordStatus <> ?");
		}
		sql.append(" Order by AuditDate ");

		logger.debug(Literal.SQL + sql.toString());

		return auditJdbcTemplate.getJdbcOperations().query(sql.toString(), ps -> {
			int index = 1;
			if (facility) {
				for (String finReference : finReferences) {
					ps.setString(index++, finReference);
				}
			} else {
				for (String finReference : finReferences) {
					ps.setString(index++, finReference);
				}
				ps.setString(index++, "W");
			}

			if (StringUtils.isNotEmpty(auditEvent)) {
				ps.setString(index++, auditEvent);
			}

			if (approvedFinance) {
				ps.setString(index++, "Saved");
			}

		}, (rs, rowNum) -> {
			AuditTransaction auditTxn = new AuditTransaction();
			auditTxn.setAuditReference(rs.getString("AuditReference"));
			auditTxn.setAuditDate(rs.getTimestamp("AuditDate"));
			auditTxn.setRoleCode(rs.getString("RoleCode"));
			auditTxn.setRoleDesc(rs.getString("RoleDesc"));
			auditTxn.setLastMntBy(rs.getLong("LastMntBy"));
			auditTxn.setRecordStatus(rs.getString("RecordStatus"));
			auditTxn.setRecordType(rs.getString("RecordType"));
			auditTxn.setUsrName(rs.getString("UsrName"));

			return auditTxn;
		});

	}

	@Override
	public List<CustomerFinanceDetail> getListOfCustomerFinanceDetailById(long custID, String type, boolean facility) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinBranch, CustId, CustCIF, CustShrtName, FinReference, RoleCode");
		sql.append(", NextRoleCode, RecordType, DeptDesc, PrvRoleDesc, NextRoleDesc, FinType, FinAmount");
		sql.append(", FinStartDate, LastMntBy, UsrFName, LastMntByUser, FinCcy, FinTypeDesc, LovDescFinDivision");

		if (facility) {
			sql.append(" from CustomerFacilityDetails");
		} else {
			sql.append(", FeeChargeAmt, NumberOfTerms, FirstRepay, ProductCategory");
			sql.append(" from CustomerFinanceDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where custId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, custID);
		}, (rs, rowNum) -> {
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

			if (!facility) {
				cfd.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
				cfd.setFirstRepay(rs.getBigDecimal("FirstRepay"));
				cfd.setNumberOfTerms(rs.getInt("NumberOfTerms"));
				cfd.setProductCategory(rs.getString("ProductCategory"));
			}

			return cfd;
		});

	}

	@Override
	public List<AuditTransaction> getFinTransactions(String id) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select AuditReference, AuditDate, RoleCode, RoleDesc, LastMntBy, RecordStatus, ");
		sql.append(" RecordType, UsrName from FinStsAprvlInquiry_View ");
		sql.append(" Where AuditReference =:FinReference  ");
		sql.append(" Order by AuditDate ");

		logger.debug(Literal.SQL + sql.toString());

		CustomerFinanceDetail customerFinanceDetail = new CustomerFinanceDetail();
		customerFinanceDetail.setFinReference(id);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerFinanceDetail);
		RowMapper<AuditTransaction> typeRowMapper = BeanPropertyRowMapper.newInstance(AuditTransaction.class);
		logger.debug(Literal.LEAVING);

		return this.auditJdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	public void setAuditDataSource(DataSource auditDataSource) {
		this.auditJdbcTemplate = new NamedParameterJdbcTemplate(auditDataSource);
	}

}
