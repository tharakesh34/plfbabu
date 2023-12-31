package com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditApplicationReviewDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevType;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class CreditApplicationReviewDAOImpl extends SequenceDao<FinCreditReviewDetails>
		implements CreditApplicationReviewDAO {
	private static Logger logger = LogManager.getLogger(CreditApplicationReviewDAOImpl.class);

	public CreditApplicationReviewDAOImpl() {
		super();
	}

	/**
	 * Method for get the CreditRevCategory Details
	 */
	public List<FinCreditRevCategory> getCreditRevCategoryByCreditRevCode(String creditRevCode) {
		logger.debug("Entering");
		FinCreditRevCategory finCreditRevCategory = new FinCreditRevCategory();
		finCreditRevCategory.setCreditRevCode(creditRevCode);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CategoryId,CategorySeque,CreditRevCode,CategoryDesc,Remarks,NoOfyears,changedsply,");
		selectSql.append(" brkdowndsply,Version,LastMntBy,");
		selectSql.append(" LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId");
		selectSql.append(" FROM FinCreditRevCategory Where CreditRevCode= :CreditRevCode ");

		List<FinCreditRevCategory> creditRevCategories;
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevCategory);
		RowMapper<FinCreditRevCategory> typeRowMapper = BeanPropertyRowMapper.newInstance(FinCreditRevCategory.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		creditRevCategories = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		if (creditRevCategories != null) {
			return creditRevCategories;
		} else {
			return creditRevCategories = new ArrayList<>();
		}
	}

	/**
	 * Method for get the CreditRevCategory Details
	 */
	public List<FinCreditRevCategory> getCreditRevCategoryByCreditRevCodeAndEligibilityIds(String creditRevCode,
			List<Long> eligibilityIds) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" select * from (");
		sql.append(" select CategoryId, CategorySeque, CreditRevCode, CategoryDesc, Remarks, NoOfyears, changedsply, ");
		sql.append(
				" brkdowndsply, Version, LastMntBy, LastMntOn,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(
				" from FinCreditRevCategory where CreditRevCode = :CreditRevCode and EligibilityId in (:EligibilityId)");
		sql.append(" union all ");
		sql.append(" select CategoryId, CategorySeque, CreditRevCode, CategoryDesc, Remarks, NoOfyears, changedsply, ");
		sql.append(
				" brkdowndsply, Version, LastMntBy, LastMntOn,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from FinCreditRevCategory where CreditRevCode = :CreditRevCode and EligibilityId");
		sql.append(
				" in (select fieldcodeid from rmtlovfielddetail where fieldcode = :fieldcode and fieldcodevalue in (:fieldcodevalue))) T");
		sql.append(" order by CategoryId");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CreditRevCode", creditRevCode);
		source.addValue("EligibilityId", eligibilityIds);
		source.addValue("fieldcode", "ELGMETHOD");
		source.addValue("fieldcodevalue", Arrays.asList(new String[] { "PL", "BL", "RT", "ES" })); // FIXME
																									// make
																									// me
																									// as
																									// constants

		RowMapper<FinCreditRevCategory> typeRowMapper = BeanPropertyRowMapper.newInstance(FinCreditRevCategory.class);

		logger.trace(Literal.SQL + sql.toString());
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId) {
		logger.debug("Entering");
		FinCreditRevSubCategory finCreditRevSubCategory = new FinCreditRevSubCategory();
		finCreditRevSubCategory.setCategoryId(categoryId);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT SubCategoryCode,SubCategorySeque,CategoryId,SubCategoryDesc,SubCategoryItemType,");
		selectSql.append(
				" MainSubCategoryCode,ItemsToCal,ItemRule,isCreditCCY,Version,LastMntBy,LastMntOn,RecordStatus,format, percentCategory, grand,");
		selectSql.append(" RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId");
		selectSql.append(" FROM FinCreditRevSubCategory Where CategoryId= :CategoryId order by CalcSeque asc"); // change
																												// Calseq
																												// -
																												// SubCategorySeque

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevSubCategory);
		RowMapper<FinCreditRevSubCategory> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditRevSubCategory.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId,
			String subCategoryItemType) {
		logger.debug("Entering");
		FinCreditRevSubCategory finCreditRevSubCategory = new FinCreditRevSubCategory();
		finCreditRevSubCategory.setCategoryId(categoryId);
		finCreditRevSubCategory.setSubCategoryItemType(subCategoryItemType);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT SubCategoryCode,SubCategorySeque,CategoryId,SubCategoryDesc,SubCategoryItemType,");
		selectSql.append(
				" MainSubCategoryCode,ItemsToCal,ItemRule,isCreditCCY,Version,LastMntBy,LastMntOn,RecordStatus,format, percentCategory, grand,");
		selectSql.append(" RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId");
		selectSql.append(
				" FROM FinCreditRevSubCategory Where CategoryId= :CategoryId and SubCategoryItemType= :SubCategoryItemType order by CalcSeque"); // 2nd
																																					// Time

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevSubCategory);
		RowMapper<FinCreditRevSubCategory> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditRevSubCategory.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(long categoryId) {
		logger.debug("Entering");
		FinCreditRevSubCategory finCreditRevSubCategory = new FinCreditRevSubCategory();
		finCreditRevSubCategory.setCategoryId(categoryId);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT T1.Remarks, T2.SubCategoryCode,T2.SubCategorySeque,T2.CalcSeque, T2.CategoryId,T2.SubCategoryDesc,T2.SubCategoryItemType,");
		selectSql.append(" T2.MainSubCategoryCode,T2.ItemsToCal,T2.ItemRule,T2.isCreditCCY,");
		selectSql.append(
				" T2.Version,T2.LastMntBy,T2.LastMntOn,T2.RecordStatus,T2.format, T2.percentCategory, T2.grand,");
		selectSql.append(" T2.RoleCode,T2.NextRoleCode,T2.TaskId,T2.NextTaskId,T2.RecordType,T2.WorkflowId");
		selectSql.append(" FROM FinCreditRevCategory T1");
		selectSql.append(" inner join FinCreditRevSubCategory_View T2 On T1.CategoryId = T2.CategoryId ");
		if (categoryId > 0) {
			selectSql.append(" Where T1.CategoryId= :CategoryId ");
		}
		selectSql.append(" order by CalcSeque");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevSubCategory);
		RowMapper<FinCreditRevSubCategory> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditRevSubCategory.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByMainCategory(String category) {
		logger.debug("Entering");
		FinCreditRevSubCategory finCreditRevSubCategory = new FinCreditRevSubCategory();
		finCreditRevSubCategory.setSubCategoryCode(category);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT T1.Remarks, T2.SubCategoryCode,T2.SubCategorySeque,T2.CalcSeque, T2.CategoryId,T2.SubCategoryDesc,T2.SubCategoryItemType,");
		selectSql.append(" T2.MainSubCategoryCode,T2.ItemsToCal,T2.ItemRule,T2.isCreditCCY,");
		selectSql.append(
				" T2.Version,T2.LastMntBy,T2.LastMntOn,T2.RecordStatus,T2.format, T2.percentCategory, T2.grand,");
		selectSql.append(" T2.RoleCode,T2.NextRoleCode,T2.TaskId,T2.NextTaskId,T2.RecordType,T2.WorkflowId");
		selectSql.append(" FROM FinCreditRevCategory T1");
		selectSql.append(" inner join FinCreditRevSubCategory_View T2 On T1.CategoryId = T2.CategoryId ");
		selectSql.append(" Where T1.CreditRevCode = :SubCategoryCode ");
		selectSql.append(" order by CalcSeque");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevSubCategory);
		RowMapper<FinCreditRevSubCategory> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditRevSubCategory.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinCreditReviewDetails
	 * 
	 * @return FinCreditReviewDetails
	 */
	@Override
	public FinCreditReviewDetails getCreditReviewDetails() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinCreditReviewDetails");
		FinCreditReviewDetails creditReviewDetails = new FinCreditReviewDetails();
		if (workFlowDetails != null) {
			creditReviewDetails.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return creditReviewDetails;
	}

	/**
	 * This method get the module from method getCreditReviewDetails() and set the new record flag as true and return
	 * FinCreditReviewDetails()
	 * 
	 * @return FinCreditReviewDetails
	 */
	@Override
	public FinCreditReviewDetails getNewCreditReviewDetails() {
		logger.debug("Entering");
		FinCreditReviewDetails creditReviewDetails = getCreditReviewDetails();
		creditReviewDetails.setNewRecord(true);
		logger.debug("Leaving");
		return creditReviewDetails;
	}

	/**
	 * Fetch the Record FinCreditReviewDetails by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return FinCreditReviewDetails
	 */
	@Override
	public FinCreditReviewDetails getCreditReviewDetailsById(final long id, String type) {
		logger.debug("Entering");
		FinCreditReviewDetails creditReviewDetails = new FinCreditReviewDetails();

		creditReviewDetails.setDetailId(id);

		StringBuilder selectSql = new StringBuilder("SELECT DetailId,CreditRevCode,CustomerId,AuditYear,BankName,");
		selectSql.append(" Auditors,Consolidated,Location,ConversionRate,AuditedDate,NoOfShares,MarketPrice,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(
				" TaskId, NextTaskId, RecordType, WorkflowId, AuditPeriod, AuditType, Qualified, Currency, Division");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescCustCIF,lovDescCustCtgCode,lovDescCustShrtName,lovDescCcyEditField");
		}
		selectSql.append(" From FinCreditReviewDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DetailId =:DetailId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		RowMapper<FinCreditReviewDetails> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewDetails.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/*
	 * 
	 */
	@Override
	public FinCreditReviewDetails getCreditReviewDetailsByCustIdAndYear(final long customerId, String auditYear,
			String type) {
		logger.debug("Entering");
		FinCreditReviewDetails creditReviewDetails = new FinCreditReviewDetails();

		creditReviewDetails.setCustomerId(customerId);
		creditReviewDetails.setAuditYear(auditYear);

		StringBuilder selectSql = new StringBuilder("SELECT DetailId,CreditRevCode,CustomerId,AuditYear,BankName,");
		selectSql.append(" Auditors,Consolidated,Location,ConversionRate,AuditedDate,NoOfShares,MarketPrice,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(
				" TaskId, NextTaskId, RecordType, WorkflowId, AuditPeriod, AuditType, Qualified, Currency, Division");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescCustCIF,lovDescCustCtgCode,lovDescCustShrtName,lovDescCcyEditField");
		}
		selectSql.append(" From FinCreditReviewDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustomerId =:customerId and auditYear = :auditYear");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		RowMapper<FinCreditReviewDetails> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewDetails.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public int getCreditReviewAuditPeriodByAuditYear(final long customerId, final String auditYear, int auditPeriod,
			boolean isEnquiry, String type) {
		logger.debug("Entering");

		FinCreditReviewDetails creditReviewDetails = new FinCreditReviewDetails();
		creditReviewDetails.setCustomerId(customerId);
		creditReviewDetails.setAuditYear(auditYear);

		StringBuilder selectSql = new StringBuilder();
		SqlParameterSource beanParameters;

		if (isEnquiry) {
			selectSql.append("SELECT  COALESCE(MAX(AuditPeriod),0) ");
			selectSql.append(" From FinCreditReviewDetails");
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" Where CustomerId =:customerId and AuditYear = :auditYear");
			beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);

			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} else {
			return getAuditPeriod(customerId, auditYear, auditPeriod, type);
		}
	}

	/**
	 * This method Deletes the Record from the CreditReviewDetails or CreditReviewDetails_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete FinCreditReviewDetails by key detailId
	 * 
	 * @param FinCreditReviewDetails (creditReviewDetails)
	 * @param type                   (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinCreditReviewDetails creditReviewDetails, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinCreditReviewDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DetailId =:DetailId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);

		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CreditReviewDetails or CreditReviewDetails_Temp. it fetches the available
	 * Sequence form SeqCreditReviewDetails by using getNextidviewDAO().getNextId() method.
	 *
	 * save FinCreditReviewDetails
	 * 
	 * @param FinCreditReviewDetails (creditReviewDetails)
	 * @param type                   (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(FinCreditReviewDetails creditReviewDetails, String type) {
		logger.debug("Entering");
		if (creditReviewDetails.getDetailId() == Long.MIN_VALUE) {
			creditReviewDetails.setDetailId(getNextValue("SeqFinCreditReviewDetails"));
		}
		StringBuilder insertSql = new StringBuilder("Insert Into FinCreditReviewDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (DetailId,CreditRevCode,CustomerId,AuditYear,BankName,Auditors,Consolidated,Location,");
		insertSql.append(
				"  ConversionRate,AuditedDate,NoOfShares,MarketPrice,Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,");
		insertSql.append(
				" TaskId,NextTaskId,RecordType,WorkflowId, AuditPeriod, AuditType, Qualified, Currency, Division)");
		insertSql.append(
				" Values ( :DetailId, :CreditRevCode, :CustomerId, :AuditYear, :BankName, :Auditors, :Consolidated, ");
		insertSql.append(
				" :Location, :ConversionRate, :AuditedDate, :NoOfShares, :MarketPrice, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(
				" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :AuditPeriod, :AuditType, :Qualified, :Currency, :Division)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return creditReviewDetails.getDetailId();
	}

	/**
	 * This method updates the Record CreditReviewDetails or CreditReviewDetails_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update FinCreditReviewDetails by key detailId and Version
	 * 
	 * @param FinCreditReviewDetails(creditReviewDetails)
	 * @param type                                        (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinCreditReviewDetails creditReviewDetails, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinCreditReviewDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CreditRevCode = :CreditRevCode,CustomerId = :CustomerId,");
		updateSql.append(
				" AuditYear = :AuditYear,BankName = :BankName,Auditors = :Auditors,Consolidated = :Consolidated,");
		updateSql.append(" Location = :Location,ConversionRate = :ConversionRate,AuditedDate = :AuditedDate,");
		updateSql.append(" NoOfShares = :NoOfShares,MarketPrice = :MarketPrice,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, ");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, ");
		updateSql.append(
				" AuditPeriod = :AuditPeriod, AuditType = :AuditType, Qualified = :Qualified, Currency = :Currency");
		updateSql.append(" Where DetailId =:DetailId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * This method for getting the creditreviewdetails by creditrevcode
	 * 
	 * @param creditRevCode
	 */
	public FinCreditRevType getFinCreditRevByRevCode(String creditRevCode) {
		logger.debug("Entering");
		FinCreditRevType finCreditRev = new FinCreditRevType();
		finCreditRev.setCreditRevCode(creditRevCode);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CreditRevCode,CreditRevDesc,CreditCCY,EntryCCY,Version,LastMntBy,LastMntOn,");
		selectSql.append(" RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId");
		selectSql.append("  FROM FinCreditRevType Where CreditRevCode= :CreditRevCode ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRev);
		RowMapper<FinCreditRevType> typeRowMapper = BeanPropertyRowMapper.newInstance(FinCreditRevType.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method for checking whether record is already existed with the customer id and audited year.
	 * 
	 * @param custID
	 * @param auditYear
	 * @return int
	 */
	public int isCreditSummaryExists(long custID, String auditYear, int auditPeriod) {
		logger.debug("Entering");

		FinCreditReviewDetails creditReviewDetails = new FinCreditReviewDetails();
		creditReviewDetails.setCustomerId(custID);
		creditReviewDetails.setAuditYear(auditYear);
		creditReviewDetails.setAuditPeriod(auditPeriod);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT count(*) ");
		selectSql.append(
				"  FROM FinCreditReviewDetails_View Where CustomerId= :CustomerId  and AuditYear  = :AuditYear and AuditPeriod = :AuditPeriod");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);

	}

	public int getAuditPeriod(long customerId, String auditYear, int auditPeriod, String type) {
		logger.debug("Entering");
		FinCreditReviewDetails creditReviewDetails = new FinCreditReviewDetails();
		creditReviewDetails.setCustomerId(customerId);
		creditReviewDetails.setAuditYear(auditYear);

		StringBuilder selectSql = new StringBuilder("Select CASE WHEN T2.AuditPeriod IS null THEN MAXAuditPeriod ");
		selectSql.append(" ELSE T2.AuditPeriod  END AuditPeriod FROM (");
		selectSql.append(" Select T1.Audityear ,T1.Customerid,MAX(T1.AuditPeriod) MAXAuditPeriod");
		selectSql.append(" from FinCreditReviewDetails");
		selectSql.append(type);
		selectSql.append(" T1 GROUP BY T1.Audityear ,T1.Customerid) T1 Left Join FinCreditReviewDetails");
		selectSql.append(type);
		selectSql.append(" T2 ON");

		selectSql.append(
				" T1.AuditYear =T2.AuditYear and T1.CustomerId =T2.CustomerId and T2.AuditPeriod= :auditPeriod ");
		selectSql.append(" Where T1.AuditYear = :auditYear and T1.CustomerId = :customerId ");

		logger.debug("selectSql: " + selectSql.toString());
		creditReviewDetails.setAuditPeriod(auditPeriod);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public String getMaxAuditYearByCustomerId(long customerId, String type) {
		FinCreditReviewDetails creditReviewDetails = new FinCreditReviewDetails();
		creditReviewDetails.setCustomerId(customerId);
		int maxAuditYear = 0;
		SqlParameterSource beanParameters;

		StringBuilder sql = new StringBuilder("SELECT COALESCE(Max(AuditYear), '0') FROM  FinCreditReviewDetails_View");
		sql.append(" where CustomerId = :CustomerId");

		logger.trace(Literal.SQL + sql.toString());

		beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);

		maxAuditYear = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);

		return String.valueOf(maxAuditYear);
	}

	@Override
	public List<FinCreditReviewDetails> getFinCreditRevDetailsByCustomerId(final long customerId, String type) {
		logger.debug("Entering");
		FinCreditReviewDetails finCreditReviewDetails = new FinCreditReviewDetails();
		finCreditReviewDetails.setCustomerId(customerId);

		StringBuilder selectSql = new StringBuilder("SELECT DetailId,CreditRevCode,CustomerId,AuditYear,BankName,");
		selectSql.append(" Auditors,Consolidated,Location,ConversionRate,AuditedDate,NoOfShares,MarketPrice,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(
				" TaskId, NextTaskId, RecordType, WorkflowId, AuditPeriod, AuditType, Qualified, Currency, Division");
		selectSql.append(" FROM FinCreditReviewDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustomerId= :customerId ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditReviewDetails);
		RowMapper<FinCreditReviewDetails> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewDetails.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinCreditReviewDetails> getAuditYearsByCustId(Set<Long> custId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustomerId", custId);

		StringBuilder selectSql = new StringBuilder("Select ");
		selectSql.append(" LovDescCustCIF, CustomerId, AuditYear");
		selectSql.append(" From finCreditReviewDetails_AView");
		selectSql.append(" Where CustomerId in (:CustomerId)");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<FinCreditReviewDetails> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewDetails.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCustCtg(String custCtgCode, String categorydesc) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("creditrevcode", custCtgCode);
		source.addValue("categorydesc", categorydesc);

		StringBuilder selectSql = new StringBuilder("Select * ");
		selectSql.append(
				" from fincreditrevsubcategory where categoryid = (select categoryid from fincreditrevcategory ");
		selectSql.append(" Where creditrevcode = :creditrevcode and categorydesc = :categorydesc)");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<FinCreditRevSubCategory> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditRevSubCategory.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public List<FinCreditReviewDetails> getFinCreditRevDetailIds(long customerId) {
		FinCreditReviewDetails finCreditReviewDetails = new FinCreditReviewDetails();
		finCreditReviewDetails.setCustomerId(customerId);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select DetailId, AuditYear");
		selectSql.append(" FROM FinCreditReviewDetails_view Where CustomerId= :CustomerId ");

		List<FinCreditReviewDetails> details;
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditReviewDetails);
		RowMapper<FinCreditReviewDetails> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewDetails.class);

		details = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		if (details != null) {
			return details;
		} else {
			return details = new ArrayList<>();
		}

	}

	@Override
	public Map<String, Object> getFinCreditRevSummaryDetails(long detailid) {
		Map<String, Object> map = new HashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append("select SubcategoryCode, ItemValue from Fincreditreviewsummary_View");
		sql.append(" Where DetailId = :DetailId");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DetailId", detailid);

		this.jdbcTemplate.query(sql.toString(), source, new RowMapper<Map<String, Object>>() {

			@Override
			public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
				map.put(rs.getString("SubcategoryCode"), rs.getBigDecimal("ItemValue"));
				// return map;
				return map;
			}
		});

		return map;
	}
}
