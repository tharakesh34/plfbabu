package com.pennant.backend.dao.limit.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

public class LimitHeaderDAOImpl extends BasisNextidDaoImpl<LimitHeader> implements LimitHeaderDAO {
	private static Logger logger = Logger.getLogger(LimitHeaderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new LimitDetail 
	 * @return LimitDetail
	 */

	@Override
	public LimitHeader getLimitHeader() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("LimitHeader");
		LimitHeader limitHeader= new LimitHeader();
		if (workFlowDetails!=null){
			limitHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return limitHeader;
	}

	@Override
	public LimitHeader getNewLimitHeader() {
		logger.debug("Entering");
		LimitHeader limitHeader = getLimitHeader();
		limitHeader.setNewRecord(true);
		logger.debug("Leaving");
		return limitHeader;
	}


	/**
	 * To Set  dataSource
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}



	/**
	 * Fetch the Record  Limit Header details by Customer ID
	 * 
	 * @param Customerid (String )
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitHeader
	 */

	@Override
	public LimitHeader getLimitHeaderByCustomerId(final long customerId, String type) {

		logger.debug("Entering");
		LimitHeader limitHeader = getLimitHeader();

		limitHeader.setCustomerId(customerId);

		StringBuilder selectSql = new StringBuilder("Select HeaderId,  CustomerId, ResponsibleBranch, LimitCcy, LimitExpiryDate, LimitRvwDate, LimitStructureCode, LimitSetupRemarks,Active,Rebuild");
		selectSql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" , ShowLimitsIn,QueryDesc,GroupName,CustGrpRO1,ResponsibleBranchName,StructureName,CustShrtName,custCoreBank ,custDftBranch ,CustDftBranchName ,custSalutationCode, ");
			selectSql.append(" CustCIF,CustFName,CustMName, CustFullName, CustGrpCode, GroupName ");
		}
		selectSql.append(" From LimitHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustomerId =:CustomerId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		RowMapper<LimitHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitHeader.class);

		try{
			limitHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			limitHeader = null;
		}
		logger.debug("Leaving");
		return limitHeader;

	}

	/**
	 * Fetch the Record  Limit Header details by Customer Group Code
	 * 
	 * @param GroupCode (String )
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitHeader
	 */
	@Override
	public LimitHeader getLimitHeaderByCustomerGroupCode(final long groupCode, String type) {

		logger.debug("Entering");
		LimitHeader limitHeader = getLimitHeader();

		limitHeader.setCustomerGroup(groupCode);

		StringBuilder selectSql = new StringBuilder("Select HeaderId,  CustomerGroup, ResponsibleBranch, LimitCcy, ");
		selectSql.append(" LimitExpiryDate, LimitRvwDate, LimitStructureCode, LimitSetupRemarks,Active,Rebuild, ");
		selectSql.append("RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" , ShowLimitsIn, QueryDesc, CustGrpRO1, ResponsibleBranchName, StructureName, ");
			selectSql.append(" CustCIF,CustFName,CustMName, CustFullName, CustGrpCode, GroupName ");
		}
		selectSql.append(" From LimitHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustomerGroup =:CustomerGroup");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		RowMapper<LimitHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitHeader.class);

		try {
			limitHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			limitHeader = null;
		}
		logger.debug("Leaving");
		return limitHeader;

	}

	@Override
	public LimitHeader getLimitHeaderByRule(final String ruleCode,final String ruleValue, String type) {

		logger.debug("Entering");
		LimitHeader limitHeader = getLimitHeader();

		limitHeader.setRuleCode(ruleCode);

		StringBuilder selectSql = new StringBuilder("Select HeaderId,  RuleCode,RuleValue, ResponsibleBranch, LimitCcy, LimitExpiryDate , LimitRvwDate, LimitStructureCode, LimitSetupRemarks,Active,Rebuild");
		selectSql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",ShowLimitsIn,QueryDesc,ResponsibleBranchName,StructureName");
		}
		selectSql.append(" From LimitHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleCode =:RuleCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		RowMapper<LimitHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitHeader.class);

		try{
			limitHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			limitHeader = null;
		}
		logger.debug("Leaving");
		return limitHeader;

	}

	/**
	 * This method insert new Records into LimitHeader or LimitHeader_Temp.
	 * it fetches the available Sequence form SeqLimitHeader by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Limit Header 
	 * 
	 * @param Limit Header (LimitDetails)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	public long save(LimitHeader limitHeader,String type) {
		logger.debug("Entering");
		if (limitHeader.getId()==Long.MIN_VALUE){
			limitHeader.setId(getNextidviewDAO().getNextId("SeqLimitHeader"));
			logger.debug("get NextID:"+limitHeader.getId());
		}

		StringBuilder insertSql =new StringBuilder("Insert Into LimitHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HeaderId, RuleCode, RuleValue, CustomerGroup, CustomerId, ResponsibleBranch, LimitCcy, LimitExpiryDate, LimitRvwDate, LimitStructureCode, LimitSetupRemarks,Active,Rebuild");
		insertSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:HeaderId, :RuleCode, :RuleValue, :CustomerGroup, :CustomerId, :ResponsibleBranch, :LimitCcy, :LimitExpiryDate, :LimitRvwDate, :LimitStructureCode, :LimitSetupRemarks,:Active,:Rebuild");
		insertSql.append(", :Version ,:CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return limitHeader.getId();
	}

	/**
	 * This method updates the Record LimitHeader or LimitHeader_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Limit Header by key HeaderId and Version
	 * 
	 * @param Limit Header (limitHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(LimitHeader limitHeader,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update LimitHeader");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set RuleCode = :RuleCode, RuleValue = :RuleValue, CustomerGroup = :CustomerGroup, CustomerId = :CustomerId, ResponsibleBranch = :ResponsibleBranch, LimitCcy = :LimitCcy, LimitExpiryDate = :LimitExpiryDate, LimitRvwDate = :LimitRvwDate, LimitStructureCode = :LimitStructureCode, LimitSetupRemarks = :LimitSetupRemarks ,Active =:Active,Rebuild =:Rebuild");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where HeaderId =:HeaderId");

		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}


	/**
	 * This method Deletes the Record from the LimitHeader or LimitHeader_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Limit Header by key HeaderId
	 * 
	 * @param Limit Header (LimitHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(LimitHeader limitHeader ,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From LimitHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where HeaderId =:HeaderId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader );
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);

		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record  Limit Header details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitDetails
	 */
	@Override
	public LimitHeader getLimitHeaderById(final long id, String type) {
		logger.debug("Entering");
		LimitHeader limitHeader = getLimitHeader();

		limitHeader.setId(id);

		StringBuilder selectSql = new StringBuilder("Select HeaderId, RuleCode, RuleValue, CustomerGroup, CustomerId, ResponsibleBranch, LimitCcy, LimitExpiryDate, LimitRvwDate, LimitStructureCode, LimitSetupRemarks,Active");
		selectSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,ShowLimitsIn,QueryDesc,CustCIF,CustGrpCode ,GroupName,ResponsibleBranchName,StructureName,CustFName,CustMName,CustFullName ,CustShrtName,custCoreBank ,custDftBranch ,CustDftBranchName ,custSalutationCode");
		}
		selectSql.append(" From LimitHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where HeaderId =:HeaderId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		RowMapper<LimitHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitHeader.class);

		try{
			limitHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			limitHeader = null;
		}
		logger.debug("Leaving");
		return limitHeader;
	}
	@Override
	public List<LimitHeader> getLimitHeaderByStructureCode(String code, String type) {
		logger.debug("Entering");
		LimitHeader limitHeader = getLimitHeader();

		limitHeader.setLimitStructureCode(code);

		StringBuilder selectSql = new StringBuilder("Select HeaderId, RuleCode, RuleValue, CustomerGroup, CustomerId, ResponsibleBranch, LimitCcy, LimitExpiryDate, LimitRvwDate, LimitStructureCode, LimitSetupRemarks,Active");
		selectSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){			
		}
		selectSql.append(" From LimitHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitStructureCode =:LimitStructureCode ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);
		RowMapper<LimitHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitHeader.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);	
	
	}



	@SuppressWarnings("deprecation")
	@Override
	public boolean isCustomerExists(long customerId, String type) {
		logger.debug("Entering");
		LimitHeader limitHeader = getLimitHeader();
		long count = 0;
		limitHeader.setCustomerId(customerId);

		StringBuilder selectSql = new StringBuilder("Select count(CustomerId) ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,QueryDesc,CustCIF,CustGrpCode ,GroupName,ResponsibleBranchName,StructureName,CustFName,CustMName,CustFullName ,CustShrtName,custCoreBank ,custDftBranch ,CustDftBranchName ,custSalutationCode");
		}
		selectSql.append(" From LimitHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustomerId =:CustomerId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitHeader);

		try{
			count = this.namedParameterJdbcTemplate.queryForLong(selectSql.toString(), beanParameters);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			limitHeader = null;
		}

		logger.debug("Leaving");
		if(count<=0){
			return false;
		}else{
			return true;
		}		
	}

	/**
	 * @param headerId
	 * @param rebuild
	 * @param type
	 */
	@Override
	public void updateRebuild(long headerId,boolean rebuild,String type) {
		logger.debug("Entering");
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);
		source.addValue("Rebuild", rebuild);
		
		StringBuilder	updateSql =new StringBuilder("Update LimitHeader");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set Rebuild = :Rebuild ");
		updateSql.append(" Where HeaderId =:HeaderId");
		logger.debug("updateSql: " + updateSql.toString());

		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
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
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);
		source.addValue("Active", 1);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From LimitHeader");
		selectSql.append(" Where HeaderId = :HeaderId AND Active = :Active ");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			return 0;
		}
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
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);
		source.addValue("CustomerID", CustID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From LimitHeader");
		selectSql.append(" Where HeaderId = :HeaderId AND CustomerID = :CustomerID");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			return 0;
		}
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
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				"Select HeaderId, RuleCode, RuleValue, CustomerGroup, CustomerId, ResponsibleBranch, LimitCcy, LimitExpiryDate,");
		selectSql.append(" LimitRvwDate, LimitStructureCode, LimitSetupRemarks, Active");

		selectSql.append(" From LimitHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleCode IS NOT NULL");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new LimitHeader());
		RowMapper<LimitHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitHeader.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);
		source.addValue("CustomerGroup", CustGrpID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From LimitHeader");
		selectSql.append(" Where HeaderId = :HeaderId AND CustomerGroup = :CustomerGroup");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			return 0;
		}
	}
}
