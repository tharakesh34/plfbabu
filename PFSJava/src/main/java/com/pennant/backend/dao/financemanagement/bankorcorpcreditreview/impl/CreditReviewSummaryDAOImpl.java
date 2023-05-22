package com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditReviewSummaryDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class CreditReviewSummaryDAOImpl extends SequenceDao<FinCreditReviewSummary> implements CreditReviewSummaryDAO {
	private static Logger logger = LogManager.getLogger(CreditReviewSummaryDAOImpl.class);

	public CreditReviewSummaryDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinCreditReviewSummary
	 * 
	 * @return FinCreditReviewSummary
	 */
	@Override
	public FinCreditReviewSummary getCreditReviewSummary() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinCreditReviewSummary");
		FinCreditReviewSummary creditReviewSummary = new FinCreditReviewSummary();
		if (workFlowDetails != null) {
			creditReviewSummary.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return creditReviewSummary;
	}

	/**
	 * This method get the module from method getFinCreditReviewSummary() and set the new record flag as true and return
	 * FinCreditReviewSummary()
	 * 
	 * @return FinCreditReviewSummary
	 */
	@Override
	public FinCreditReviewSummary getNewCreditReviewSummary() {
		logger.debug("Entering");
		FinCreditReviewSummary creditReviewSummary = getCreditReviewSummary();
		creditReviewSummary.setNewRecord(true);
		logger.debug("Leaving");
		return creditReviewSummary;
	}

	/**
	 * Fetch the Record CreditReviewSummary by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return FinCreditReviewSummary
	 */
	@Override
	public FinCreditReviewSummary getCreditReviewSummaryById(final long summaryId, long detailId, String type) {
		logger.debug("Entering");
		FinCreditReviewSummary creditReviewSummary = new FinCreditReviewSummary();
		creditReviewSummary.setDetailId(detailId);
		creditReviewSummary.setSummaryId(summaryId);

		StringBuilder selectSql = new StringBuilder("Select SummaryId,DetailId,SubCategoryCode,ItemValue,");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From FinCreditReviewSummary");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DetailId =:DetailId and SummaryId = :SummaryId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		RowMapper<FinCreditReviewSummary> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewSummary.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record FinCreditReviewSummary details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return FinCreditReviewSummary
	 */
	@Override
	public List<FinCreditReviewSummary> getListCreditReviewSummaryById(final long id, String type,
			boolean postingsProcess) {
		logger.debug("Entering");

		FinCreditReviewSummary creditReviewSummary = new FinCreditReviewSummary();
		creditReviewSummary.setDetailId(id);

		StringBuilder selectSql = new StringBuilder(" Select SummaryId,DetailId,SubCategoryCode,ItemValue,");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		if (!postingsProcess) {
			selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
			selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		}
		selectSql.append(" From FinCreditReviewSummary");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DetailId =:DetailId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		RowMapper<FinCreditReviewSummary> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewSummary.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method Deletes the Record from the FinCreditReviewSummary or FinCreditReviewSummary_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete FinCreditReviewSummary by key detailid and
	 * summary id
	 * 
	 * @param FinCreditReviewSummary (creditReviewSummary)
	 * @param type                   (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinCreditReviewSummary creditReviewSummary, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinCreditReviewSummary");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DetailId =:DetailId and SummaryId = :SummaryId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
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
	 * This method for deleting the summary details of the perticular detail item id
	 */
	@Override
	public void deleteByDetailId(long detailId, String type) {
		logger.debug("Entering");
		FinCreditReviewSummary creditReviewSummary = new FinCreditReviewSummary();
		creditReviewSummary.setDetailId(detailId);
		StringBuilder deleteSql = new StringBuilder("Delete From FinCreditReviewSummary");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DetailId =:DetailId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinCreditReviewSummary or TransactionEntry_Temp. it fetches the available
	 * Sequence form SeqFinCreditReviewSummary by using getNextidviewDAO().getNextId() method.
	 * 
	 * save FinCreditReviewSummary Entry
	 * 
	 * @param FinCreditReviewSummary Entry (creditReviewSummary)
	 * @param type                   (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(FinCreditReviewSummary creditReviewSummary, String type) {
		logger.debug("Entering");

		if (creditReviewSummary.getSummaryId() == Long.MIN_VALUE) {
			creditReviewSummary.setSummaryId(getNextValue("SeqFinCreditReviewSummary"));
		}

		logger.debug("get NextID:" + creditReviewSummary.getSummaryId());
		StringBuilder insertSql = new StringBuilder("Insert Into FinCreditReviewSummary");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SummaryId,DetailId,SubCategoryCode,ItemValue,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId) ");
		insertSql.append(" Values(:SummaryId, :DetailId, :SubCategoryCode, :ItemValue,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return creditReviewSummary.getId();
	}

	/**
	 * This method updates the Record FinCreditReviewSummary or FinCreditReviewSummary. if Record not updated then
	 * throws DataAccessException with error 41004. update FinCreditReviewSummary Entry by key DetailId and Version
	 * 
	 * @param FinCreditReviewSummary (creditReviewSummary)
	 * @param type                   (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinCreditReviewSummary creditReviewSummary, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinCreditReviewSummary");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SubCategoryCode = :SubCategoryCode,ItemValue = :ItemValue,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where DetailId =:DetailId and SummaryId = :SummaryId");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(creditReviewSummary);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method for getting the list of summary details based on the customer id and year.<br>
	 * 
	 */
	@Override
	public List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId(long customerId, String year,
			String type) {
		logger.debug("Entering");
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("CustomerId", customerId);
		namedParameterMap.put("Audityear", year);

		StringBuilder selectSql = new StringBuilder(
				" select T1.SummaryId,T1.DetailId,T1.SubCategoryCode,T1.ItemValue,");
		selectSql.append(
				" T2.ConversionRate LovDescConversionRate,T2.bankName LovDescBankName ,T2.noOfShares lovDescNoOfShares,");
		selectSql.append(" T2.marketPrice lovDescMarketPrice, T3.CcyEditField lovDescCcyEditField ");
		selectSql.append(" from FinCreditReviewSummary");
		selectSql.append(type);
		selectSql.append(" T1 ");
		selectSql.append(" INNER JOIN finCreditReviewDetails");
		selectSql.append(type);
		selectSql.append(" T2 on T1.detailId = T2.detailId ");
		selectSql.append(" INNER JOIN RMTCurrencies T3 ON T2.Currency = T3.CcyCode ");
		selectSql.append(" where T2.CustomerId = :CustomerId and T2.Audityear = :Audityear");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinCreditReviewSummary> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewSummary.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), namedParameterMap, typeRowMapper);
	}

	/**
	 * This method for getting the list of summary details based on the customer id and year.<br>
	 * 
	 */
	@Override
	public FinCreditReviewDetails getCreditReviewDetailsByYearAndCustId(long customerId, String year, String type) {
		logger.debug("Entering");
		FinCreditReviewDetails creditReviewDetails = new FinCreditReviewDetails();
		creditReviewDetails.setCustomerId(customerId);
		creditReviewDetails.setAuditYear(year);

		StringBuilder selectSql = new StringBuilder("SELECT DetailId,CreditRevCode,CustomerId,AuditYear,BankName,");
		selectSql.append(" Auditors,Consolidated,Location,ConversionRate,AuditedDate,NoOfShares,MarketPrice,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(
				" TaskId, NextTaskId, RecordType, WorkflowId, AuditPeriod, AuditType, Qualified, Currency, Division ");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescCustCIF,lovDescCustCtgCode,lovDescCustShrtName,lovDescCcyEditField");
		}
		selectSql.append(" From FinCreditReviewDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustomerId =:CustomerId AND AuditYear =:AuditYear");

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

	@Override
	public List<FinCreditReviewSummary> getLatestCreditReviewSummaryByYearAndCustId(long id) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder(" SELECT T1.SummaryId,T1.DetailId,T1.SubCategoryCode, T1.ItemValue, ");
		sql.append(" T2.ConversionRate LovDescConversionRate,T2.bankName LovDescBankName, ");
		sql.append(" T2.noOfShares lovDescNoOfShares,  T2.marketPrice lovDescMarketPrice, ");
		sql.append(" T2.AuditYear AuditYear, T2.CreditRevCode CreditRevCode ");
		sql.append(" From FinCreditReviewSummary T1 inner join finCreditReviewDetails T2 on T1.detailId = T2.detailId");
		sql.append(" WHERE T1.DetailID = (SELECT DetailId FROM (select DetailId, ");
		sql.append(" row_number() over (ORDER BY AuditYear DESC) row_num ");
		sql.append(" FROM FinCreditReviewDetails WHERE CustomerID = :Customerid )T where row_num <= 1 ) ");
		logger.debug("selectSql: " + sql.toString());

		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("Customerid", id);

		RowMapper<FinCreditReviewSummary> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewSummary.class);

		return this.jdbcTemplate.query(sql.toString(), namedParameterMap, typeRowMapper);
	}

	/**
	 * This method for getting the list of summary details based on the customer id and year.<br>
	 * 
	 */
	@Override
	public List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId2(long id, String year,
			String category, String type) {
		logger.debug("Entering");
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("Customerid", id);
		namedParameterMap.put("Audityear", year);
		namedParameterMap.put("Category", category);

		StringBuilder selectSql = new StringBuilder(
				" select T1.SummaryId,T1.DetailId,T1.SubCategoryCode,T1.ItemValue, ");
		selectSql.append(
				" T2.ConversionRate LovDescConversionRate,T2.bankName LovDescBankName ,T2.noOfShares lovDescNoOfShares,");
		selectSql.append(" T2.marketPrice lovDescMarketPrice, T4.CreditRevCode lovDescCreditRevCode, ");
		selectSql.append(" T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode,");
		selectSql.append(" T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId,");
		selectSql.append(
				" T4.CategoryID lovDescCategoryId, T4.CategoryDesc lovDescCategoryDesc, T3.SubCategoryDesc lovDescSubCategoryDesc ");
		selectSql.append(" from FinCreditReviewSummary");
		selectSql.append(type);
		selectSql.append(" T1 ");
		selectSql.append(" inner join finCreditReviewDetails");
		selectSql.append(type);
		selectSql.append(" T2 on T1.detailId = T2.detailId ");
		selectSql.append(" inner join FinCreditRevSubCategory T3 on T1.SubCategoryCode = t3.SubCategoryCode ");
		selectSql.append(" inner join FinCreditRevCategory T4 on T3.CategoryId = T4.CategoryId ");

		selectSql.append(" Where T2.Customerid = :Customerid and T2.Audityear = :Audityear");
		selectSql.append(" and T4.CreditRevCode = :Category order by T4.CategoryID, T3.SubCategoryCode");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinCreditReviewSummary> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewSummary.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), namedParameterMap, typeRowMapper);
	}

	/**
	 * This method for getting the list of summary details based on the customer id and year.<br>
	 * 
	 */
	@Override
	public List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId2(long customerId, String year,
			String category, int auditPeriod, boolean isCurrentYear, String type) {
		logger.debug("Entering");
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("Customerid", customerId);
		namedParameterMap.put("Audityear", year);
		namedParameterMap.put("Category", category);
		namedParameterMap.put("AuditPeriod", auditPeriod);

		StringBuilder selectSql = new StringBuilder(
				" select T1.SummaryId,T1.DetailId,T1.SubCategoryCode,T1.ItemValue, ");
		selectSql.append(
				" T2.ConversionRate LovDescConversionRate,T2.bankName LovDescBankName ,T2.noOfShares lovDescNoOfShares,");
		selectSql.append(" T2.marketPrice lovDescMarketPrice, T4.CreditRevCode lovDescCreditRevCode, ");
		selectSql.append(" T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode,");
		selectSql.append(" T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId,");
		selectSql.append(
				" T4.CategoryID lovDescCategoryId, T4.CategoryDesc lovDescCategoryDesc, T3.SubCategoryDesc lovDescSubCategoryDesc ");

		selectSql.append(" from FinCreditReviewSummary");
		selectSql.append(type);
		selectSql.append(" T1 ");
		selectSql.append(" inner join finCreditReviewDetails");
		selectSql.append(type);
		selectSql.append(" T2 on T1.detailId = T2.detailId ");
		selectSql.append(" inner join FinCreditRevSubCategory T3 on T1.SubCategoryCode = t3.SubCategoryCode ");
		selectSql.append(" inner join FinCreditRevCategory T4 on T3.CategoryId = T4.CategoryId ");
		selectSql.append(" Where T2.Customerid = :Customerid and T2.Audityear = :Audityear");
		selectSql.append(" and T2.AuditPeriod =");
		if (isCurrentYear) {
			selectSql.append(" :AuditPeriod");
		} else {
			StringBuilder selectSql2 = new StringBuilder(
					"Select CASE WHEN T2.AuditPeriod IS null THEN MAXAuditPeriod ");
			selectSql2.append(" ELSE T2.AuditPeriod  END AuditPeriod FROM (");
			selectSql2.append(" Select T1.Audityear ,T1.Customerid,MAX(T1.AuditPeriod) MAXAuditPeriod");
			selectSql2.append(" from FinCreditReviewDetails" + type
					+ " T1 GROUP BY T1.Audityear ,T1.Customerid) T1 Left Join FinCreditReviewDetails T2 ON");
			selectSql2.append(
					" T1.AuditYear =T2.AuditYear and T1.CustomerId =T2.CustomerId and	T2.AuditPeriod= :AuditPeriod ");
			selectSql2.append(" Where T1.AuditYear = :Audityear and T1.CustomerId = :Customerid ");
			selectSql.append(" ( " + selectSql2.toString() + ")");
		}

		selectSql.append(" and T4.CreditRevCode = :Category order by T4.CategoryID, T3.SubCategoryCode");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinCreditReviewSummary> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditReviewSummary.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), namedParameterMap, typeRowMapper);
	}

	/**
	 * This Method for getting CurrencySpotRate
	 */
	public BigDecimal getCcySpotRate(String ccyCode) {
		logger.debug("Entering");

		Currency currency = new Currency();
		currency.setCcyCode(ccyCode);
		StringBuilder selectSql = new StringBuilder("Select CcySpotRate from RMTCurrencies where CcyCode = :CcyCode");
		logger.debug("selectSql: " + selectSql.toString());
		BeanPropertySqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		RowMapper<Currency> typeRowMapper = BeanPropertyRowMapper.newInstance(Currency.class);

		currency = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper).get(0);

		logger.debug("Leaving");
		return currency.getCcySpotRate();
	}

	@Override
	public List<String> getAuditYearsbyCustdId(long custId) {
		MapSqlParameterSource parmSrc = new MapSqlParameterSource();
		parmSrc.addValue("custId", custId);
		StringBuilder selectSql = new StringBuilder(
				"Select audityear from FinCreditReviewDetails_View where customerId =:custId");

		return this.jdbcTemplate.query(selectSql.toString(), parmSrc, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});
	}

	@Override
	public LinkedHashMap<String, Object> getFinCreditRevSummaryByCustIdAndAdtYr(long customerId, long auditYear) {
		logger.debug(Literal.ENTERING);
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append("select SubcategoryCode, ItemValue from FinCreditReviewSummary_view where detailId =");
		sql.append(
				" (Select detailId FROM FinCreditReviewDetails_view Where customerId =:customerId and auditYear =:auditYear)  order by summaryId asc");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("customerId", customerId);
		source.addValue("auditYear", auditYear);

		this.jdbcTemplate.query(sql.toString(), source, new RowMapper<Map<String, Object>>() {

			@Override
			public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
				map.put(rs.getString("SubcategoryCode"), rs.getBigDecimal("ItemValue"));
				return map;
			}
		});

		logger.debug(Literal.LEAVING);
		return map;
	}

}
