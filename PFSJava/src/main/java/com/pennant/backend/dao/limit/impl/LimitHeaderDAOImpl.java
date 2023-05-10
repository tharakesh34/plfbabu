package com.pennant.backend.dao.limit.impl;

import java.sql.ResultSet;
import java.util.List;
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

import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class LimitHeaderDAOImpl extends SequenceDao<LimitHeader> implements LimitHeaderDAO {
	private static Logger logger = LogManager.getLogger(LimitHeaderDAOImpl.class);

	/**
	 * This method set the Work Flow id based on the module name and return the new LimitDetail
	 * 
	 * @return LimitDetail
	 */

	@Override
	public LimitHeader getLimitHeader() {
		logger.debug(Literal.ENTERING);
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LimitHeader");
		LimitHeader limitHeader = new LimitHeader();
		if (workFlowDetails != null) {
			limitHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug(Literal.LEAVING);
		return limitHeader;
	}

	@Override
	public LimitHeader getNewLimitHeader() {
		logger.debug(Literal.ENTERING);
		LimitHeader limitHeader = getLimitHeader();
		limitHeader.setNewRecord(true);
		logger.debug(Literal.LEAVING);
		return limitHeader;
	}

	/**
	 * Fetch the Record Limit Header details by Customer ID
	 * 
	 * @param Customerid (String )
	 * @param type       (String) ""/_Temp/_View
	 * @return LimitHeader
	 */

	@Override
	public LimitHeader getLimitHeaderByCustomerId(final long custId, String type) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, CustomerId, ResponsibleBranch, LimitCcy, LimitExpiryDate, LimitRvwDate");
		sql.append(", LimitStructureCode, LimitSetupRemarks, Active, Rebuild, ValidateMaturityDate, BLOCKLIMIT");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", ShowLimitsIn, QueryDesc, GroupName, ResponsibleBranchName, StructureName, CustShrtName");
			sql.append(", CustCoreBank, CustDftBranch, CustDftBranchName, CustSalutationCode, CustCIF");
			sql.append(", CustFName, CustMName, CustFullName, CustGrpCode, GroupName, CustGrpRO1");
		}

		sql.append(" from LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where CustomerId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (ResultSet rs, int rowNum) -> {
				LimitHeader lh = new LimitHeader();

				lh.setHeaderId(rs.getLong("HeaderId"));
				lh.setCustomerId(rs.getLong("CustomerId"));
				lh.setResponsibleBranch(rs.getString("ResponsibleBranch"));
				lh.setLimitCcy(rs.getString("LimitCcy"));
				lh.setLimitExpiryDate(rs.getTimestamp("LimitExpiryDate"));
				lh.setLimitRvwDate(rs.getTimestamp("LimitRvwDate"));
				lh.setLimitStructureCode(rs.getString("LimitStructureCode"));
				lh.setLimitSetupRemarks(rs.getString("LimitSetupRemarks"));
				lh.setActive(rs.getBoolean("Active"));
				lh.setRebuild(rs.getBoolean("Rebuild"));
				lh.setValidateMaturityDate(rs.getBoolean("ValidateMaturityDate"));
				lh.setRecordStatus(rs.getString("RecordStatus"));
				lh.setRoleCode(rs.getString("RoleCode"));
				lh.setNextRoleCode(rs.getString("NextRoleCode"));
				lh.setTaskId(rs.getString("TaskId"));
				lh.setNextTaskId(rs.getString("NextTaskId"));
				lh.setRecordType(rs.getString("RecordType"));
				lh.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					lh.setShowLimitsIn(rs.getString("ShowLimitsIn"));
					lh.setQueryDesc(rs.getString("QueryDesc"));
					lh.setGroupName(rs.getString("GroupName"));
					lh.setResponsibleBranchName(rs.getString("ResponsibleBranchName"));
					lh.setStructureName(rs.getString("StructureName"));
					lh.setCustShrtName(rs.getString("CustShrtName"));
					lh.setCustCoreBank(rs.getString("CustCoreBank"));
					lh.setCustDftBranch(rs.getString("CustDftBranch"));
					lh.setCustDftBranchName(rs.getString("CustDftBranchName"));
					lh.setCustSalutationCode(rs.getString("CustSalutationCode"));
					lh.setCustCIF(rs.getString("CustCIF"));
					lh.setCustFName(rs.getString("CustFName"));
					lh.setCustMName(rs.getString("CustMName"));
					lh.setCustFullName(rs.getString("CustFullName"));
					lh.setCustGrpCode(rs.getString("CustGrpCode"));
					// lh.setCustGrpRO1(rs.getString("CustGrpRO1"));
				}
				return lh;
			}, custId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	/**
	 * Fetch the Record Limit Header details by Customer Group Code
	 * 
	 * @param GroupCode (String )
	 * @param type      (String) ""/_Temp/_View
	 * @return LimitHeader
	 */
	@Override
	public LimitHeader getLimitHeaderByCustomerGroupCode(final long groupCode, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, CustomerGroup, ResponsibleBranch, LimitCcy, LimitExpiryDate, LimitRvwDate");
		sql.append(", LimitStructureCode, LimitSetupRemarks, Active, Rebuild, ValidateMaturityDate");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", ShowLimitsIn, QueryDesc, ResponsibleBranchName, StructureName, CustCIF, CustFName");
			sql.append(", CustMName, CustFullName, CustGrpCode, GroupName, CustGrpRO1");
		}

		sql.append(" From LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustomerGroup = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				LimitHeader lh = new LimitHeader();

				lh.setHeaderId(rs.getLong("HeaderId"));
				lh.setCustomerGroup(rs.getLong("CustomerGroup"));
				lh.setResponsibleBranch(rs.getString("ResponsibleBranch"));
				lh.setLimitCcy(rs.getString("LimitCcy"));
				lh.setLimitExpiryDate(rs.getTimestamp("LimitExpiryDate"));
				lh.setLimitRvwDate(rs.getTimestamp("LimitRvwDate"));
				lh.setLimitStructureCode(rs.getString("LimitStructureCode"));
				lh.setLimitSetupRemarks(rs.getString("LimitSetupRemarks"));
				lh.setActive(rs.getBoolean("Active"));
				lh.setRebuild(rs.getBoolean("Rebuild"));
				lh.setValidateMaturityDate(rs.getBoolean("ValidateMaturityDate"));
				lh.setRecordStatus(rs.getString("RecordStatus"));
				lh.setRoleCode(rs.getString("RoleCode"));
				lh.setNextRoleCode(rs.getString("NextRoleCode"));
				lh.setTaskId(rs.getString("TaskId"));
				lh.setNextTaskId(rs.getString("NextTaskId"));
				lh.setRecordType(rs.getString("RecordType"));
				lh.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					lh.setShowLimitsIn(rs.getString("ShowLimitsIn"));
					lh.setQueryDesc(rs.getString("QueryDesc"));
					lh.setResponsibleBranchName(rs.getString("ResponsibleBranchName"));
					lh.setStructureName(rs.getString("StructureName"));
					lh.setCustCIF(rs.getString("CustCIF"));
					lh.setCustFName(rs.getString("CustFName"));
					lh.setCustMName(rs.getString("CustMName"));
					lh.setCustFullName(rs.getString("CustFullName"));
					lh.setCustGrpCode(rs.getString("CustGrpCode"));
					lh.setGroupName(rs.getString("GroupName"));
					// lh.setCustGrpRO1(rs.getString("CustGrpRO1"));
				}

				return lh;
			}, groupCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in LimitHeader{} for the specified CustomerGroup >> {}", type, groupCode);
		}

		return null;
	}

	@Override
	public LimitHeader getLimitHeaderByRule(final String ruleCode, final String ruleValue, String type) {

		logger.debug(Literal.ENTERING);
		LimitHeader limitHeader = getLimitHeader();

		limitHeader.setRuleCode(ruleCode);

		StringBuilder sql = new StringBuilder(
				"select HeaderId,  RuleCode,RuleValue, ResponsibleBranch, LimitCcy, LimitExpiryDate , LimitRvwDate, LimitStructureCode, LimitSetupRemarks,Active,Rebuild");
		sql.append(
				",ValidateMaturityDate, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(",ShowLimitsIn, QueryDesc, ResponsibleBranchName, StructureName");
		}
		sql.append(" From LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where RuleCode = :RuleCode");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		RowMapper<LimitHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitHeader.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method insert new Records into LimitHeader or LimitHeader_Temp. it fetches the available Sequence form
	 * SeqLimitHeader by using getNextidviewDAO().getNextId() method.
	 *
	 * save Limit Header
	 * 
	 * @param Limit Header (LimitDetails)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	public long save(LimitHeader limitHeader, String type) {
		logger.debug(Literal.ENTERING);
		if (limitHeader.getId() == Long.MIN_VALUE) {
			limitHeader.setId(getNextValue("SeqLimitHeader"));
			logger.debug("get NextValue:" + limitHeader.getId());
		}

		StringBuilder sql = new StringBuilder("Insert Into LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(
				" (HeaderId, RuleCode, RuleValue, CustomerGroup, CustomerId, ResponsibleBranch, LimitCcy, LimitExpiryDate, LimitRvwDate, LimitStructureCode, LimitSetupRemarks,Active,Rebuild,ValidateMaturityDate");
		sql.append(
				", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(
				" Values(:HeaderId, :RuleCode, :RuleValue, :CustomerGroup, :CustomerId, :ResponsibleBranch, :LimitCcy, :LimitExpiryDate, :LimitRvwDate, :LimitStructureCode, :LimitSetupRemarks,:Active,:Rebuild,:validateMaturityDate");
		sql.append(
				", :Version ,:CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return limitHeader.getId();
	}

	/**
	 * This method updates the Record LimitHeader or LimitHeader_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Limit Header by key HeaderId and Version
	 * 
	 * @param Limit Header (limitHeader)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * 
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(LimitHeader limitHeader, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(
				" Set RuleCode = :RuleCode, RuleValue = :RuleValue, CustomerGroup = :CustomerGroup, CustomerId = :CustomerId, ResponsibleBranch = :ResponsibleBranch, LimitCcy = :LimitCcy, LimitExpiryDate = :LimitExpiryDate, LimitRvwDate = :LimitRvwDate, LimitStructureCode = :LimitStructureCode, LimitSetupRemarks = :LimitSetupRemarks ,Active =:Active,Rebuild =:Rebuild");
		sql.append(
				",ValidateMaturityDate = :ValidateMaturityDate, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where HeaderId = :HeaderId");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version= :Version-1");
		}

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method Deletes the Record from the LimitHeader or LimitHeader_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Limit Header by key HeaderId
	 * 
	 * @param Limit Header (LimitHeader)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(LimitHeader limitHeader, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From LimitHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where HeaderId =:HeaderId");

		logger.trace(Literal.SQL + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fetch the Record Limit Header details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return LimitDetails
	 */
	@Override
	public LimitHeader getLimitHeaderById(final long id, String type) {
		logger.debug(Literal.ENTERING);

		LimitHeader limitHeader = getLimitHeader();
		limitHeader.setId(id);
		StringBuilder sql = new StringBuilder(
				"select HeaderId, RuleCode, RuleValue, CustomerGroup, CustomerId, ResponsibleBranch, LimitCcy, LimitExpiryDate, LimitRvwDate, LimitStructureCode, LimitSetupRemarks,Active,ValidateMaturityDate");
		sql.append(
				", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(
					" ,ShowLimitsIn,QueryDesc,CustCIF,CustGrpCode ,GroupName,ResponsibleBranchName,StructureName,CustFName,CustMName,CustFullName ,CustShrtName,custCoreBank ,custDftBranch ,CustDftBranchName ,custSalutationCode");
		}
		sql.append(" From LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId = :HeaderId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		RowMapper<LimitHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitHeader.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<LimitHeader> getLimitHeaderByStructureCode(String code, String type) {
		logger.debug(Literal.ENTERING);

		LimitHeader limitHeader = getLimitHeader();
		limitHeader.setLimitStructureCode(code);

		StringBuilder sql = new StringBuilder(
				"Select HeaderId, RuleCode, RuleValue, CustomerGroup, CustomerId, ResponsibleBranch, LimitCcy, LimitExpiryDate, LimitRvwDate, LimitStructureCode, LimitSetupRemarks,Active,ValidateMaturityDate");
		sql.append(
				", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
		}
		sql.append(" From LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LimitStructureCode =:LimitStructureCode ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		RowMapper<LimitHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitHeader.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

	}

	@Override
	public boolean isCustomerExists(long customerId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select count(CustomerId) ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(
					" ,QueryDesc,CustCIF,CustGrpCode ,GroupName,ResponsibleBranchName,StructureName,CustFName,CustMName,CustFullName ,CustShrtName,custCoreBank ,custDftBranch ,CustDftBranchName ,custSalutationCode");
		}
		sql.append(" From LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustomerId = :CustomerId");
		LimitHeader limitHeader = getLimitHeader();
		long count = 0;
		limitHeader.setCustomerId(customerId);

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);

		try {
			count = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		logger.debug(Literal.LEAVING);
		if (count <= 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @param headerId
	 * @param rebuild
	 * @param type
	 */
	@Override
	public void updateRebuild(long headerId, boolean rebuild, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Rebuild = :Rebuild ");
		sql.append(" Where HeaderId =:HeaderId");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);
		source.addValue("Rebuild", rebuild);

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for fetch number of records from limitHeader
	 * 
	 * @param headerId
	 * @param type
	 * 
	 * @return Integer
	 */
	@Override
	public int getLimitHeaderCountById(long headerId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT COUNT(*) From LimitHeader");
		sql.append(" where HeaderId = :HeaderId AND Active = :Active ");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);
		source.addValue("Active", 1);

		logger.trace(Literal.SQL + sql.toString());
		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	/**
	 * Method for fetch number of records from limitHeader
	 * 
	 * @param headerId
	 * @param CustID
	 * 
	 * @return Integer
	 */
	@Override
	public int getLimitHeaderAndCustCountById(long headerId, long CustID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT COUNT(*) From LimitHeader");
		sql.append(" where HeaderId = :HeaderId and CustomerID = :CustomerID");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);
		source.addValue("CustomerID", CustID);

		logger.trace(Literal.SQL + sql.toString());
		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	/**
	 * Method for fetch limitHeaders
	 * 
	 * @param type
	 * 
	 * @return List<LimitHeader>
	 */
	@Override
	public List<LimitHeader> getLimitHeaders(String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select HeaderId, RuleCode, RuleValue, CustomerGroup, CustomerId, ResponsibleBranch, LimitCcy");
		sql.append(", LimitExpiryDate, LimitRvwDate, LimitStructureCode, LimitSetupRemarks, Active");
		sql.append(", ValidateMaturityDate");
		sql.append(" From LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where RuleCode IS NOT NULL");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new LimitHeader());
		RowMapper<LimitHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitHeader.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for fetch number of records from limitHeader
	 * 
	 * @param headerId
	 * @param customer group
	 * 
	 * @return Integer
	 */
	@Override
	public int getLimitHeaderAndCustGrpCountById(long headerId, long CustGrpID) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);
		source.addValue("CustomerGroup", CustGrpID);

		StringBuilder sql = new StringBuilder("SELECT COUNT(*) From LimitHeader");
		sql.append(" Where HeaderId = :HeaderId and CustomerGroup = :CustomerGroup");

		logger.trace(Literal.SQL + sql.toString());
		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	@Override
	public long isLimitBlock(long custID, String type, boolean limitBlock) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select count(*)");
		sql.append(" From LimitHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where CustomerId = :CustomerId and Blocklimit = :Blocklimit");

		LimitHeader limitHeader = getLimitHeader();
		limitHeader.setCustomerId(custID);
		limitHeader.setBlocklimit(limitBlock);

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);

		return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Long.class);
	}

	@Override
	public int updateBlockLimit(long custId, long headerId, boolean blockLimit) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update LimitHeader");
		sql.append(" Set Blocklimit =:blocklimit ");
		sql.append(" Where HeaderId =:HeaderId AND CustomerId =:CustomerId");

		int count = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);
		source.addValue("CustomerId", custId);
		source.addValue("blocklimit", blockLimit);

		logger.trace(Literal.SQL + sql.toString());

		count = this.jdbcTemplate.update(sql.toString(), source);
		if (count <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
		return count;

	}

	public List<String> getLimitRuleFields() {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("RBModule", "LMTLINE");

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct RBFldName from RBFieldDetails");
		sql.append(" where RBModule = :RBModule");

		return jdbcTemplate.queryForList(sql.toString(), parameterSource, String.class);
	}

	@Override
	public FinanceType getLimitFieldsByFinTpe(String finType, Set<String> ruleFields) {
		StringBuilder sql = new StringBuilder("select ");
		sql.append(ruleFields.toString().replace("[", "").replace("]", ""));
		sql.append(" from RMTFinanceTypes");
		sql.append(" where FinType = :FinType");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinType", finType);

		RowMapper<FinanceType> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceType.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Customer getLimitFieldsByCustId(long custId, Set<String> ruleFields) {
		StringBuilder sql = new StringBuilder("select ");
		sql.append(ruleFields.toString().replace("[", "").replace("]", ""));
		sql.append(" from Customers");
		sql.append(" where CustID = :CustID");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("CustID", custId);

		RowMapper<Customer> typeRowMapper = BeanPropertyRowMapper.newInstance(Customer.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinanceMain> getLimitFieldsByCustId(long custId, Set<String> ruleFields, boolean orgination) {
		StringBuilder sql = new StringBuilder("select ");
		sql.append(ruleFields.toString().replace("[", "").replace("]", ""));

		if (orgination) {
			sql.append(", 1 LimitValid");
		}
		sql.append(" from FinanceMain");
		if (orgination) {
			sql.append(TableType.TEMP_TAB.getSuffix());
		}
		sql.append(" where CustID= :CustID");
		if (orgination) {
			if (App.DATABASE == Database.ORACLE) {
				sql.append(" and RcdMaintainSts IS NULL ");
			} else {
				sql.append(" and RcdMaintainSts = '' ");
			}
		}

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("CustID", custId);

		RowMapper<FinanceMain> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMain.class);

		return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
	}

	@Override
	public List<FinanceMain> getInstitutionLimitFields(Set<String> ruleFields, String whereClause, boolean orgination) {
		StringBuilder sql = new StringBuilder("Select ");

		for (String field : ruleFields) {
			sql.append(" fm.").append(field).append(",");
		}

		sql.deleteCharAt(sql.length() - 1);

		if (orgination) {
			sql.append(", 1 LimitValid");
		}
		sql.append(" from FinanceMain");

		if (orgination) {
			sql.append(TableType.TEMP_TAB.getSuffix());
		}

		sql.append(" fm");
		sql.append(" Inner Join Customers ct on ct.CustID = fm.CustID");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = fm.FinType ");

		sql.append(whereClause);

		if (orgination) {
			if (App.DATABASE == Database.ORACLE) {
				sql.append(" and RcdMaintainSts IS NULL ");
			} else {
				sql.append(" and RcdMaintainSts = '' ");
			}
		}

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();

		RowMapper<FinanceMain> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMain.class);

		return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
	}

	@Override
	public boolean isDuplicateKey(String ruleCode, String limitStructureCode, TableType tableType) {
		String sql;
		String whereClause = "RuleCode = ? and LimitStructureCode = ?";

		Object[] obj = new Object[] { ruleCode, limitStructureCode };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("LimitHeader", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("LimitHeader_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "LimitHeader_Temp", "LimitHeader" }, whereClause);

			obj = new Object[] { ruleCode, limitStructureCode, ruleCode, limitStructureCode };
			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}
}
