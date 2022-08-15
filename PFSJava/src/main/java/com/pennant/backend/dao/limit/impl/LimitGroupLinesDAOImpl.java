package com.pennant.backend.dao.limit.impl;

import java.util.ArrayList;
import java.util.List;

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

import com.pennant.backend.dao.limit.LimitGroupLinesDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.limit.LimitGroupLines;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class LimitGroupLinesDAOImpl extends BasicDao<LimitGroupLines> implements LimitGroupLinesDAO {
	private static Logger logger = LogManager.getLogger(LimitGroupLinesDAOImpl.class);

	public LimitGroupLinesDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new LimitGroupLines
	 * 
	 * @return LimitGroupLines
	 */

	@Override
	public LimitGroupLines getLimitGroupLines() {
		logger.debug(Literal.ENTERING);
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LimitGroup");
		LimitGroupLines limitGroupItemsItems = new LimitGroupLines();
		if (workFlowDetails != null) {
			limitGroupItemsItems.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug(Literal.LEAVING);
		return limitGroupItemsItems;
	}

	/**
	 * This method get the module from method getLimitGroupLines() and set the new record flag as true and return
	 * LimitGroupLines()
	 * 
	 * @return LimitGroupLines
	 */

	@Override
	public LimitGroupLines getNewLimitGroupLines() {
		logger.debug(Literal.ENTERING);
		LimitGroupLines limitGroupItemsItems = getLimitGroupLines();
		limitGroupItemsItems.setNewRecord(true);
		logger.debug(Literal.LEAVING);
		return limitGroupItemsItems;
	}

	/**
	 * Fetch the Record Limit Group details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return LimitGroupLines
	 */
	@Override
	public List<LimitGroupLines> getLimitGroupLinesById(final String id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select LimitGroupCode, GroupCode, LimitLine, LimitLines, ItemSeq");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LimitGroupLines");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LimitGroupCode = :LimitGroupCode");
		sql.append(" order by ItemSeq");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitGroupCode", id);

		RowMapper<LimitGroupLines> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitGroupLines.class);
		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Limit group lines not availabe for the limit group code {}", id);
		}
		return null;
	}

	/**
	 * This method Deletes the Record from the LimitGroupLines or LimitGroupLines_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Limit Group by key GroupCode
	 * 
	 * @param Limit Group (limitGroupItems)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(String limitGroupCode, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From LimitGroupLines");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LimitGroupCode = :LimitGroupCode");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitGroupCode", limitGroupCode);

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into LimitGroupLines or LimitGroupLines_Temp.
	 *
	 * save Limit Group
	 * 
	 * @param Limit Group (limitGroupItems)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(LimitGroupLines limitGroupItems, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into LimitGroupLines");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (LimitGroupCode, GroupCode, LimitLine, LimitLines,ItemSeq");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:LimitGroupCode, :GroupCode, :LimitLine, :LimitLines, :ItemSeq");
		sql.append(", :Version ,:CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus");
		sql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return limitGroupItems.getId();
	}

	/**
	 * This method updates the Record LimitGroupLines or LimitGroupLines_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Limit Group by key GroupCode and Version
	 * 
	 * @param Limit Group (limitGroupItems)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(LimitGroupLines limitGroupItems, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update LimitGroupLines");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set GroupCode = :GroupCode, LimitLine = :LimitLine, LimitLines = :LimitLines ,ItemSeq =:ItemSeq");
		sql.append(", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");

		if (limitGroupItems.getGroupCode() != null) {
			sql.append(" Where LimitGroupCode = :LimitGroupCode AND GroupCode = :GroupCode");
		} else {
			sql.append(" Where LimitGroupCode = :LimitGroupCode AND LimitLine = :LimitLine");
		}
		if (!type.endsWith("_Temp")) {
			sql.append("  and Version= :Version-1");
		}

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteLimitGroupLines(LimitGroupLines limitGroupItems, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From LimitGroupLines");
		sql.append(StringUtils.trimToEmpty(type));
		if (limitGroupItems.getGroupCode() != null) {
			sql.append(" Where LimitGroupCode = :LimitGroupCode AND GroupCode = :GroupCode");
		} else {
			sql.append(" Where LimitGroupCode = :LimitGroupCode AND LimitLine = :LimitLine");
		}
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
		try {
			if (this.jdbcTemplate.update(sql.toString(), beanParameters) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public String getLimitLines(LimitGroupLines lmtGrpItems, String limitGroupCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql;

		if (lmtGrpItems.getLimitLine() == null) {
			sql = new StringBuilder("SELECT STUFF((SELECT '|' + CAST(LimitLines as varchar)");
		} else {
			sql = new StringBuilder("SELECT STUFF((SELECT '|' + CAST(LimitLine as varchar)");
		}

		sql.append(" FROM LimitGroupLines");

		sql.append(" Where LimitGroupCode = :LimitGroupCode");
		sql.append(" FOR XML PATH(''), TYPE).value('.', 'varchar(max)'), 1, 1, '');");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitGroupCode", limitGroupCode);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Limit line not available for limit group code {}", limitGroupCode);
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
		return new String();
	}

	@Override
	public List<LimitGroupLines> getLimitGroupItemById(final String id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select LimitGroupCode, GroupCode, LimitLine, LimitLines,ItemSeq");
		sql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From LimitGroupLines");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LimitGroupCode = :LimitGroupCode");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("LimitGroupCode", id);

		RowMapper<LimitGroupLines> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitGroupLines.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);

	}

	@Override
	public int validationCheck(String limitGroup, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder("Select Count(*) From LimitGroupLines");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GroupCode = :GroupCode");

		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GroupCode", limitGroup);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public int limitLineCheck(String limitLine, String limitCategory, String type) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("Select Count(*) From LimitGroupLines");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitLine = :LimitLine AND LimitCategory = :LimitCategory ");
		source.addValue("LimitLine", limitLine);
		source.addValue("LimitCategory", limitCategory);

		logger.debug("selectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	@Override
	public String getGroupcodes(String code, boolean limitLine, String type) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Code", code);

		StringBuilder selectSql = new StringBuilder("Select LimitGroupCode  ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
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

		RowMapper<LimitGroupLines> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitGroupLines.class);
		logger.debug(Literal.LEAVING);

		record = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		for (LimitGroupLines gCode : record) {
			if (groupCode == null) {
				groupCode = "'" + gCode.getLimitGroupCode() + "'";
			} else {
				groupCode = groupCode.concat(",'" + gCode.getLimitGroupCode() + "'");
			}
		}

		return groupCode;
	}

	@Override
	public List<LimitGroupLines> getGroupCodesByLimitGroup(String code, boolean limitLine, String type) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Code", code);

		StringBuilder selectSql = new StringBuilder("Select LimitGroupCode,GroupCode,LimitLine,LimitLines ,ItemSeq");
		selectSql.append(
				", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From LimitGroupLines");
		selectSql.append(StringUtils.trimToEmpty(type));

		if (limitLine) {
			selectSql.append(" where LimitLine in ( :Code) ");
		} else {
			selectSql.append(" where GroupCode in ( :Code) ");
		}
		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<LimitGroupLines> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitGroupLines.class);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public String getGroupByLineAndHeader(String limitLine, long headerID) {
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

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitLine", limitLine);
		source.addValue("HeaderID", headerID);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("");
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

		logger.debug(Literal.LEAVING);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<LimitGroupLines> getAllLimitLinesByGroup(final String group, String type) {
		logger.debug(Literal.ENTERING);

		List<LimitGroupLines> groupLines = new ArrayList<LimitGroupLines>();
		getLimitLinesByGroup(group, type, groupLines);

		logger.debug(" Entering ");
		return groupLines;
	}

	private void getLimitLinesByGroup(final String group, String type, List<LimitGroupLines> groupLines) {
		List<LimitGroupLines> list = getLimitGroupLinesById(group, type);
		for (LimitGroupLines limitGroupLines : list) {
			groupLines.add(limitGroupLines);
			if (limitGroupLines.getGroupCode() != null) {
				getLimitLinesByGroup(limitGroupLines.getGroupCode(), type, groupLines);
			}
		}
	}

	@Override
	public int getLimitLinesByRuleCode(String ruleCode, String type) {
		logger.debug(Literal.ENTERING);
		LimitGroupLines limitGroupItemsItems = new LimitGroupLines();
		limitGroupItemsItems.setLimitLine(ruleCode);

		StringBuilder sql = new StringBuilder("SELECT COUNT(*)");
		sql.append(" From LimitGroupLines");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where LimitLine =:LimitLine");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItemsItems);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);
	}

}