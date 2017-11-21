package com.pennant.backend.dao.collateral.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.model.extendedfields.ExtendedFieldRender;

public class ExtendedFieldRenderDAOImpl implements ExtendedFieldRenderDAO {
	private static Logger	logger	= Logger.getLogger(ExtendedFieldRenderDAOImpl.class);

	public ExtendedFieldRenderDAOImpl() {
		super();
	}

	private NamedParameterJdbcTemplate	jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public ExtendedFieldRender getExtendedFieldDetails(String reference, int seqNo, String tableName, String type) {
		logger.debug("Entering");

		ExtendedFieldRender fieldRender = null;
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("SeqNo", seqNo);
		
		StringBuilder selectSql = new StringBuilder("Select Reference, SeqNo, ");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId from ");
		selectSql.append(tableName);
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where  Reference =:Reference AND SeqNo = :SeqNo ");

		RowMapper<ExtendedFieldRender> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExtendedFieldRender.class);
		logger.debug("selectSql: " + selectSql.toString());
		try {
			fieldRender = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.info("Exceprtion ", e);
			fieldRender = null;
		}

		logger.debug("Leaving");
		return fieldRender;
	}

	/**
	 * Get Extended field details Maps by Reference
	 */
	@Override
	public List<Map<String,Object>> getExtendedFieldMap(String reference, String tableName, String type) {
		logger.debug("Entering");

		List<Map<String, Object>> renderMap = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		
		StringBuilder selectSql = null;
		if (StringUtils.startsWith(type, "_View")) {
			selectSql = new StringBuilder("Select * from (Select * from ");
			selectSql.append(tableName);
			selectSql.append("_Temp");
			selectSql.append(" T1  UNION ALL  Select * from ");
			selectSql.append(tableName);
			selectSql.append(" T1  WHERE NOT EXISTS (SELECT 1 FROM ");
			selectSql.append(tableName);
			selectSql.append("_Temp");
			selectSql.append(" where  Reference =T1.Reference)) T WHERE T.Reference = :Reference ");
		} else {
			selectSql = new StringBuilder("Select * from ");
			selectSql.append(tableName);
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" where  Reference = :Reference ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		try {
			renderMap = this.jdbcTemplate.queryForList(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exceprtion ", e);
			renderMap = null;
		}

		logger.debug("Leaving");
		return renderMap;
	}
	
	/**
	 * Get Extended field details Maps by Reference
	 */
	@Override
	public Map<String,Object> getExtendedField(String reference, String tableName, String type) {
		logger.debug("Entering");
		
		Map<String, Object> renderMap = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		
		StringBuilder selectSql =  null;
		if(StringUtils.startsWith(type, "_View")){
			selectSql = new StringBuilder("Select * from (Select * from ");
			selectSql.append(tableName);
			selectSql.append("_Temp");
			selectSql.append(" T1  UNION ALL  Select * from ");
			selectSql.append(tableName);
			selectSql.append(" T1  WHERE NOT EXISTS (SELECT 1 FROM ");
			selectSql.append(tableName);
			selectSql.append("_Temp");
			selectSql.append(" where  Reference =T1.Reference)) T WHERE T.Reference = :Reference ");
		}else{
			selectSql = new StringBuilder("Select * from ");
			selectSql.append(tableName);
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" where  Reference = :Reference ");
		}
		
		logger.debug("selectSql: " + selectSql.toString());
		try {
			renderMap = this.jdbcTemplate.queryForMap(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exceprtion ", e);
			renderMap = null;
		}
		
		logger.debug("Leaving");
		return renderMap;
	}
	
	@Override
	public void update(String reference, int seqNo, Map<String, Object> mappedValues, String type, String tableName) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder(" UPDATE ");
		insertSql.append(tableName);
		insertSql.append(StringUtils.trimToEmpty(type));
		List<String> list = new ArrayList<String>(mappedValues.keySet());
		StringBuilder query = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				query.append(" set ").append(list.get(i)).append("=:").append(list.get(i));
			} else {
				query.append(",").append(list.get(i)).append("=:").append(list.get(i));
			}
		}
		insertSql.append(query);
		insertSql.append(" where Reference ='").append(reference).append("' AND SeqNo = '").append(seqNo).append("'");
		
		logger.debug("insertSql: " + insertSql.toString());
		this.jdbcTemplate.update(insertSql.toString(), mappedValues);
		logger.debug("Leaving");
	}

	@Override
	public void delete(String reference, int seqNo, String type, String tableName) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("SeqNo", seqNo);
		
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(tableName);
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where Reference = :Reference AND  SeqNo = :SeqNo");

		logger.debug("deleteSql: " + deleteSql.toString());
		this.jdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public void save(Map<String, Object> mappedValues, String type, String tableName) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder(" INSERT INTO ");
		insertSql.append(tableName);
		insertSql.append(StringUtils.trimToEmpty(type));

		List<String> list = new ArrayList<String>(mappedValues.keySet());
		String columnames = "";
		String columnValues = "";
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1) {
				columnames = columnames.concat(list.get(i)).concat(" , ");
				columnValues = columnValues.concat(":").concat(list.get(i)).concat(" , ");
			} else {
				columnames = columnames.concat(list.get(i));
				columnValues = columnValues.concat(":").concat(list.get(i));
			}
		}
		insertSql.append(" (" + columnames + ") values (" + columnValues + ")");
		logger.debug("insertSql: " + insertSql.toString());
		try {
			this.jdbcTemplate.update(insertSql.toString(), mappedValues);
		} catch(DataIntegrityViolationException e) {
			logger.error("Exception", e);
			throw e;
		}
		logger.debug("Leaving");

	}

	/**
	 * Fetch max seqNo from Extended table
	 * 
	 * @param reference
	 * @param tableName
	 * @return Integer
	 */
	@Override
	public int getMaxSeqNoByRef(String reference, String tableName) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		
		StringBuilder selectSql = new StringBuilder("Select  Max(SeqNo) from ");
		selectSql.append(tableName);
		selectSql.append(" where  Reference =:Reference ");

		logger.debug("selectSql: " + selectSql.toString());
		
		int maxSeqNo = 0;
		try {
			maxSeqNo = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception ", e);
			maxSeqNo = 0;
		}

		logger.debug("Leaving");
		return maxSeqNo;
	}

	

	/**
	 * This method Deletes the list of extendedfiled Records.
	 * 
	 * @param collateralRef
	 * @param tableNmae
	 * @param tableType
	 *            (String) ""/_Temp/
	 * @return void
	 * 
	 */
	@Override
	public void deleteList(String reference, String tableNmae, String tableType) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;
		try {
			sql = new StringBuilder();
			sql.append(" Delete From  ");
			sql.append(tableNmae);
			sql.append(StringUtils.trimToEmpty(tableType));
			sql.append(" Where Reference = :Reference");
			logger.debug("deleteSql: " + sql.toString());

			source = new MapSqlParameterSource();
			source.addValue("Reference", reference);

			this.jdbcTemplate.update(sql.toString(), source);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
	}

	@Override
	public int validateMasterData(String tableName, String column, String filterColumn, String fieldValue) {
		logger.debug("Entering");
		
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("ColumnName", column);
		source.addValue("Filter", filterColumn);
		source.addValue("Value", fieldValue);
		if(StringUtils.equals("CustGrpID", column)) {//FIXME:DDP
			source.addValue("Value", Integer.parseInt(fieldValue));
		}

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM ");
		selectSql.append(tableName);
		selectSql.append(" WHERE ");
		selectSql.append(column);
		selectSql.append("= :Value");
		if(StringUtils.isNotBlank(filterColumn)){
			selectSql.append(" AND "+filterColumn+"= 1");
		}
		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);	
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		
		return recordCount;
	}
}
