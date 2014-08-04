package com.pennant.backend.dao.approvalstatusenquiry.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.approvalstatusenquiry.ApprovalStatusEnquiryDAO;
import com.pennant.backend.dao.impl.BasisDAO;
import com.pennant.backend.model.finance.AuditTransaction;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.util.PennantConstants;

public class ApprovalStatusEnquiryDAOImpl extends BasisDAO<CustomerFinanceDetail>  implements ApprovalStatusEnquiryDAO {

	private final static Logger logger = Logger.getLogger(ApprovalStatusEnquiryDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/*
	 * (non-Javadoc)
	 * @see com.pennant.backend.dao.finance.ApprovaStatusEnquiryDAO#getCustomerFinanceMainById(java.lang.String, java.lang.String)
	 */
	@Override
    public CustomerFinanceDetail getCustomerFinanceMainById(String id, String type,boolean facility) {

		logger.debug("Entering");
		CustomerFinanceDetail customerFinanceDetail = new CustomerFinanceDetail();
		customerFinanceDetail.setFinReference(id); 
 		
		StringBuilder selectSql = new StringBuilder(" SELECT TOP 1 FinReference, FinBranch, CustID, CustCIF, CustShrtName," );
		selectSql.append(" RoleCode, NextRoleCode, RecordType, DeptDesc, RoleDesc, NextRoleDesc, ");
		selectSql.append(" FinType, FinAmount, FinStartDate, LastMntBy,ccyFormat,lastMntByUser ,FinCcy,FinTypeDesc ");
		if (facility) {
	        selectSql.append(" from CustomerFacilityDetails");
        }else{
        	selectSql.append(" from CustomerFinanceDetails");
        	
        }
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		 
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerFinanceDetail);
		RowMapper<CustomerFinanceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerFinanceDetail.class);
		
		try {
			customerFinanceDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			customerFinanceDetail = null;
		}
		logger.debug("Leaving");
		return customerFinanceDetail;
	
    }

	
	/*
	 * (non-Javadoc)
	 * @see com.pennant.backend.dao.finance.ApprovaStatusEnquiryDAO#getFinTransactionsList(java.lang.String, boolean)
	 */
	@Override
    public List<AuditTransaction> getFinTransactionsList(String id, boolean approvedFinance,boolean facility) {

		logger.debug("Entering");
		CustomerFinanceDetail customerFinanceDetail = new CustomerFinanceDetail();
		customerFinanceDetail.setFinReference(id); 
		customerFinanceDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerFinanceDetail.setRecordStatus("Saved");
		StringBuilder selectSql = new StringBuilder(" ");
		selectSql.append("SELECT AuditReference, AuditDate, RoleCode, RoleDesc, LastMntBy, RecordStatus, RecordType, UsrName "); 
 		if (facility) {
	        selectSql.append(" from FacilityStatusApprovalInquiry_View ");
        }else {
        	selectSql.append(" from FinanceStatusApprovalInquiry_View ");
			
		}
		selectSql.append(" Where AuditReference =:FinReference and RecordType = :RecordType ");
 		if(approvedFinance){
 			selectSql.append(" and RecordStatus <> :RecordStatus");
 		}
 		selectSql.append(" Order by AuditDate ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerFinanceDetail);
		RowMapper<AuditTransaction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AuditTransaction.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
   
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
}
