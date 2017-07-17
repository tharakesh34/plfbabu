package com.pennant.backend.dao.limit.impl;

import java.util.ArrayList;
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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.limit.LimitGroupLinesDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.limit.LimitGroupLines;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

public class LimitGroupLinesDAOImpl extends BasisCodeDAO<LimitGroupLines> implements LimitGroupLinesDAO {

	private static Logger logger = Logger.getLogger(LimitGroupLinesDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public LimitGroupLinesDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new LimitGroupLines 
	 * @return LimitGroupLines
	 */

	@Override
	public LimitGroupLines getLimitGroupLines() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LimitGroup");
		LimitGroupLines limitGroupItemsItems= new LimitGroupLines();
		if (workFlowDetails!=null){
			limitGroupItemsItems.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return limitGroupItemsItems;
	}


	/**
	 * This method get the module from method getLimitGroupLines() and set the new record flag as true and return LimitGroupLines()   
	 * @return LimitGroupLines
	 */


	@Override
	public LimitGroupLines getNewLimitGroupLines() {
		logger.debug("Entering");
		LimitGroupLines limitGroupItemsItems = getLimitGroupLines();
		limitGroupItemsItems.setNewRecord(true);
		logger.debug("Leaving");
		return limitGroupItemsItems;
	}

	/**
	 * Fetch the Record  Limit Group details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitGroupLines
	 */
	@Override
	public List<LimitGroupLines> getLimitGroupLinesById(final String id, String type) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = null;
		
		StringBuilder selectSql = new StringBuilder("Select LimitGroupCode, GroupCode, LimitLine, LimitLines,ItemSeq");
		selectSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
		}
		selectSql.append(" From LimitGroupLines");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitGroupCode = :LimitGroupCode");
		selectSql.append("  order by ItemSeq");
		
		logger.debug("selectSql: " + selectSql.toString());
		source = new MapSqlParameterSource();
		source.addValue("LimitGroupCode", id);
		
		RowMapper<LimitGroupLines> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitGroupLines.class);
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		return null;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the LimitGroupLines or LimitGroupLines_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Limit Group by key GroupCode
	 * 
	 * @param Limit Group (limitGroupItems)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(String limitGroupCode,String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		
		StringBuilder deleteSql = new StringBuilder("Delete From LimitGroupLines");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LimitGroupCode = :LimitGroupCode");
		logger.debug("deleteSql: " + deleteSql.toString());
		
		source = new MapSqlParameterSource();
		source.addValue("LimitGroupCode", limitGroupCode);

		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into LimitGroupLines or LimitGroupLines_Temp.
	 *
	 * save Limit Group 
	 * 
	 * @param Limit Group (limitGroupItems)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(LimitGroupLines limitGroupItems,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into LimitGroupLines");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LimitGroupCode, GroupCode, LimitLine, LimitLines,ItemSeq");
		insertSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LimitGroupCode, :GroupCode, :LimitLine, :LimitLines,:ItemSeq");
		insertSql.append(", :Version ,:CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return limitGroupItems.getId();
	}
	
	/**
	 * This method updates the Record LimitGroupLines or LimitGroupLines_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Limit Group by key GroupCode and Version
	 * 
	 * @param Limit Group (limitGroupItems)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(LimitGroupLines limitGroupItems,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update LimitGroupLines");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set GroupCode = :GroupCode, LimitLine = :LimitLine , LimitLines = :LimitLines ,ItemSeq =:ItemSeq");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		if(limitGroupItems.getGroupCode() != null) {
			updateSql.append(" Where LimitGroupCode = :LimitGroupCode AND GroupCode = :GroupCode");
		} else {
			updateSql.append(" Where LimitGroupCode = :LimitGroupCode AND LimitLine = :LimitLine");
		}
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	

	@Override
	public void deleteLimitGroupLines(LimitGroupLines limitGroupItems, String type) {
		
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From LimitGroupLines");
		deleteSql.append(StringUtils.trimToEmpty(type));
		if(limitGroupItems.getGroupCode() != null) {
			deleteSql.append(" Where LimitGroupCode = :LimitGroupCode AND GroupCode = :GroupCode");
		} else {
			deleteSql.append(" Where LimitGroupCode = :LimitGroupCode AND LimitLine = :LimitLine");
		}
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
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


	@Override
	public String getLimitLines(LimitGroupLines lmtGrpItems, String limitGroupCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder selectSql;
		
		if(lmtGrpItems.getLimitLine() == null) {
			selectSql = new StringBuilder("SELECT STUFF((SELECT '|' + CAST(LimitLines as varchar)");
		} else {
			selectSql = new StringBuilder("SELECT STUFF((SELECT '|' + CAST(LimitLine as varchar)");
		}
		
		selectSql.append(" FROM LimitGroupLines");
		
		selectSql.append(" Where LimitGroupCode = :LimitGroupCode");
		selectSql.append(" FOR XML PATH(''), TYPE).value('.', 'varchar(max)'),1,1,'');");
				
		logger.debug("selectSql: " + selectSql.toString());
		source = new MapSqlParameterSource();
		source.addValue("LimitGroupCode", limitGroupCode);
		
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		return new String();
	}
	
	@Override
	public List<LimitGroupLines> getLimitGroupItemById(final String id, String type) {
		logger.debug("Entering");
		LimitGroupLines limitGroupItems = getLimitGroupLines();
		
		limitGroupItems.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select LimitGroupCode, GroupCode, LimitLine, LimitLines,ItemSeq");
		selectSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From LimitGroupLines");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitGroupCode = :LimitGroupCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
		RowMapper<LimitGroupLines> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitGroupLines.class);
		
		logger.debug("Leaving");
			return  this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int validationCheck(String limitGroup, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder	selectSql = new StringBuilder("Select Count(*) From LimitGroupLines");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GroupCode = :GroupCode");
		source.addValue("GroupCode", limitGroup);
		
		logger.debug("selectSql: " + selectSql.toString());
		
		try {
			
			recordCount = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		
		return recordCount;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int limitLineCheck(String limitLine,String limitCategory, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder	selectSql = new StringBuilder("Select Count(*) From LimitGroupLines");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitLine = :LimitLine AND LimitCategory = :LimitCategory ");
		source.addValue("LimitLine", limitLine);
		source.addValue("LimitCategory", limitCategory);

		
		logger.debug("selectSql: " + selectSql.toString());
		
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		
		return recordCount;
	}
	
	@Override
	public String getGroupcodes(String code, boolean limitLine, String type) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Code", code);


		StringBuilder	selectSql = new StringBuilder("Select LimitGroupCode  ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From LimitGroupLines");
		selectSql.append(StringUtils.trimToEmpty(type));
	
		if (limitLine) {
			selectSql.append(" Where LimitLine in (:Code) ");
		} else {
			selectSql.append(" Where GroupCode in (:Code) ");
		}
		List<LimitGroupLines> record;
		String groupCode = null;
		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<LimitGroupLines> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitGroupLines.class);
		logger.debug("Leaving");
		try {
			record = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
			for (LimitGroupLines gCode : record) {
				if(groupCode==null){
					groupCode ="'" + gCode.getLimitGroupCode() + "'";
				}else{
					groupCode  = groupCode.concat(",'" + gCode.getLimitGroupCode() + "'");
				}
			}

		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		} finally {
			record = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		return groupCode;

	}
	
	
	
	@Override
	public List<LimitGroupLines> getGroupCodesByLimitGroup(String code, boolean limitLine, String type) {
		MapSqlParameterSource source= new MapSqlParameterSource();
		source.addValue("Code", code);
		
		StringBuilder	selectSql = new StringBuilder("Select LimitGroupCode,GroupCode,LimitLine,LimitLines ,ItemSeq");
		selectSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From LimitGroupLines");
		selectSql.append(StringUtils.trimToEmpty(type));

		if(limitLine){		
			selectSql.append(" where LimitLine in ( :Code) ");
		}else{
			selectSql.append(" where GroupCode in ( :Code) ");
		}
		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<LimitGroupLines> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitGroupLines.class);
		logger.debug("Leaving");
		try {
			return	 this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);	
			

		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		} finally {
			selectSql = null;
			logger.debug("Leaving");
		}
		return new ArrayList<LimitGroupLines>();		

	}
	
	@Override
	public String getGroupByLineAndHeader(String limitLine, long headerID) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitLine", limitLine);
		source.addValue("HeaderID", headerID);

		StringBuilder selectSql = new StringBuilder(" select lgl.LimitGroupCode ");
		selectSql.append(" from (select lsd.LimitLine from LimitDetails ld  ");
		selectSql.append(" inner join LimitStructureDetails lsd on  ");
		selectSql.append(" lsd.LimitStructureDetailsID= ld.LimitStructureDetailsID ");
		selectSql.append(" where LimitHeaderId=:HeaderID and lsd.LimitLine is not null) cs ");
		selectSql.append(" inner join LimitGroupLines lgl ");
		selectSql.append(" on  lgl.LimitLine=cs.LimitLine where cs.LimitLine=:LimitLine And");
		selectSql.append(" Lgl.LIMITGROUPCODE in ( select LSD.GROUPCODE from LIMITDETAILS LD ");
		selectSql.append(" inner join LIMITSTRUCTUREDETAILS LSD on   ");
		selectSql.append(" LSD.LIMITSTRUCTUREDETAILSID= LD.LIMITSTRUCTUREDETAILSID  where  ");
		selectSql.append("  LIMITHEADERID = :HeaderID  and LSD.GroupCode is not null)");
		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);

		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		} finally {
			selectSql = null;
			logger.debug("Leaving");
		}
		return null;
	}
	
	@Override
	public String getGroupByGroupAndHeader(String groupCode, long headerID) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GroupCode", groupCode);
		source.addValue("HeaderID", headerID);

		StringBuilder selectSql = new StringBuilder(" select lgl.LimitGroupCode ");
		selectSql.append(" from (select lsd.GroupCode from LimitDetails ld  ");
		selectSql.append(" inner join LimitStructureDetails lsd on   ");
		selectSql.append(" lsd.LimitStructureDetailsID= ld.LimitStructureDetailsID ");
		selectSql.append(" where LimitHeaderId = :HeaderID and lsd.GroupCode is not null) cs ");
		selectSql.append(" inner join LimitGroupLines lgl ");
		selectSql.append(" on lgl.GroupCode = cs.GroupCode where cs.GroupCode = :GroupCode And");
		selectSql.append(" Lgl.LIMITGROUPCODE in ( select LSD.GROUPCODE from LIMITDETAILS LD ");
		selectSql.append(" inner join LIMITSTRUCTUREDETAILS LSD on   ");
		selectSql.append(" LSD.LIMITSTRUCTUREDETAILSID= LD.LIMITSTRUCTUREDETAILSID  where  ");
		selectSql.append(" LIMITHEADERID = :HeaderID  and LSD.GroupCode is not null)");
		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);

		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		} finally {
			selectSql = null;
			logger.debug("Leaving");
		}
		return null;
	}
	
	@Override
	public List<LimitGroupLines> getAllLimitLinesByGroup(final String group, String type) {
		logger.debug("Entering");
		
		List<LimitGroupLines> groupLines=new ArrayList<LimitGroupLines>();
		getLimitLinesByGroup(group, type, groupLines);
		
		logger.debug(" Entering ");
		return groupLines;
	}
	
	private void getLimitLinesByGroup(final String group, String type,List<LimitGroupLines> groupLines) {
		List<LimitGroupLines> list = getLimitGroupLinesById(group, type);
		for (LimitGroupLines limitGroupLines : list) {
			groupLines.add(limitGroupLines);
			if (limitGroupLines.getGroupCode() !=null) {
				getLimitLinesByGroup(limitGroupLines.getGroupCode(), type, groupLines);
			}
		}
	}
	
	@Override
	public int getLimitLinesByRuleCode(String ruleCode, String type) {
		logger.debug("Entering");
		LimitGroupLines limitGroupItemsItems= new LimitGroupLines();
		limitGroupItemsItems.setLimitLine(ruleCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From LimitGroupLines");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitLine =:LimitLine");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItemsItems);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	
}