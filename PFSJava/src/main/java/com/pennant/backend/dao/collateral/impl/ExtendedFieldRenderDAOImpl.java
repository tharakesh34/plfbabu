package com.pennant.backend.dao.collateral.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtendedFieldRenderDAOImpl extends BasicDao<ExtendedFieldRender> implements ExtendedFieldRenderDAO {
	private static Logger logger = LogManager.getLogger(ExtendedFieldRenderDAOImpl.class);

	public ExtendedFieldRenderDAOImpl() {
		super();
	}

	@Override
	public ExtendedFieldRender getExtendedFieldDetails(String reference, int seqNo, String tableName, String type) {
		logger.debug("Entering");

		ExtendedFieldRender fieldRender = null;

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("SeqNo", seqNo);

		StringBuilder selectSql = new StringBuilder("Select Reference, SeqNo, ");
		selectSql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId from ");
		selectSql.append(tableName);
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where  Reference =:Reference AND SeqNo = :SeqNo ");

		RowMapper<ExtendedFieldRender> typeRowMapper = BeanPropertyRowMapper.newInstance(ExtendedFieldRender.class);
		logger.debug("selectSql: " + selectSql.toString());
		try {
			fieldRender = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records are not found in {} for the specified Reference >> {} and Seq No >> {}", tableName,
					reference, seqNo);
			fieldRender = null;
		}

		logger.debug("Leaving");
		return fieldRender;
	}

	/**
	 * Get Extended field details Maps by Reference
	 */
	@Override
	public List<Map<String, Object>> getExtendedFieldMap(String reference, String tableName, String type) {
		List<Map<String, Object>> renderMap = null;

		type = StringUtils.trimToEmpty(type).toLowerCase();

		StringBuilder sql = new StringBuilder();
		if (StringUtils.startsWith(type, "_view")) {
			sql.append("select * from (select * from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" t1  union all select * from ");
			sql.append(tableName);
			sql.append(" t1  where not exists (select 1 from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" where reference = t1.reference and seqno = t1.seqno)) t where t.reference = :reference ");
		} else {
			sql.append("select * from ");
			sql.append(tableName);
			sql.append(StringUtils.trimToEmpty(type));
			sql.append(" where reference = :reference order by seqno");
		}

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", reference);
		try {
			renderMap = this.jdbcTemplate.queryForList(sql.toString(), source);
		} catch (Exception e) {
			logger.warn("Records not found in {}{} for the reference : {}", tableName, type, reference);
			renderMap = new ArrayList<>();
		}

		return renderMap;
	}

	/**
	 * Get Extended field details Maps by verificationId
	 */
	@Override
	public List<Map<String, Object>> getExtendedFieldMapByVerificationId(long verificationId, String tableName) {
		logger.debug("Entering");

		List<Map<String, Object>> renderMap = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("verificationId", verificationId);

		StringBuilder selectSql = new StringBuilder("Select * from ");
		selectSql.append(tableName);
		selectSql.append(" where  verificationId = :verificationId order by seqno");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			renderMap = this.jdbcTemplate.queryForList(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exceprtion ", e);
			renderMap = new ArrayList<>();
		}

		logger.debug("Leaving");
		return renderMap;
	}

	/**
	 * Get Extended field details Maps by Reference
	 */
	@Override
	public Map<String, Object> getExtendedField(String reference, String tableName, String type) {
		logger.debug("Entering");

		Map<String, Object> renderMap = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		StringBuilder selectSql = null;
		if (StringUtils.startsWithIgnoreCase(type, "_View")) {
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
			renderMap = this.jdbcTemplate.queryForMap(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records are not found in {}{} for the specified Reference >> {}", tableName, type, reference);
			renderMap = null;
		}

		logger.debug("Leaving");
		return renderMap;
	}

	/**
	 * Get Extended field details Maps by Reference
	 */
	@Override
	public Map<String, Object> getExtendedField(String reference, int seqNo, String tableName, String type) {
		logger.debug("Entering");

		Map<String, Object> renderMap = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("SeqNo", seqNo);

		StringBuilder selectSql = null;
		if (StringUtils.startsWithIgnoreCase(type, "_View")) {
			selectSql = new StringBuilder("Select * from (Select * from ");
			selectSql.append(tableName);
			selectSql.append("_Temp");
			selectSql.append(" T1  UNION ALL  Select * from ");
			selectSql.append(tableName);
			selectSql.append(" T1  WHERE NOT EXISTS (SELECT 1 FROM ");
			selectSql.append(tableName);
			selectSql.append("_Temp");
			selectSql.append(" where  Reference =T1.Reference)) T WHERE T.Reference = :Reference and SeqNo= :SeqNo ");
		} else {
			selectSql = new StringBuilder("Select * from ");
			selectSql.append(tableName);
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" where  Reference = :Reference and SeqNo= :SeqNo ");
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
		} catch (DataIntegrityViolationException e) {
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
	public int validateMasterData(String tableName, String column, String filterColumn, Object fieldValue) {
		logger.debug("Entering");

		boolean tempFix = false;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ColumnName", column);
		source.addValue("Filter", filterColumn);
		source.addValue("Value", fieldValue);
		source.addValue("active", true);
		//FIXME: Need to change the method implementation
		if (column.equals("CustGrpID") || column.equals("EmpName")) {
			source.addValue("Value", fieldValue);
			column = "EmployerId";
			source.addValue("ColumnName", "EmployerId");
		} else if (column.equals("DealerName")) {
			tempFix = true;
			source.addValue("Value", fieldValue);
		}

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM ");
		selectSql.append(tableName);
		selectSql.append(" WHERE ");
		if (tempFix) {
			selectSql.append("DealerId");
			selectSql.append("= :Value");
		} else {
			selectSql.append(column);
			selectSql.append("= :Value");
			if (StringUtils.isNotBlank(filterColumn)) {
				selectSql.append(" AND " + filterColumn + "=:active");
			}
		}
		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (Exception dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}
		logger.debug("Leaving");

		return recordCount;
	}

	/**
	 * 
	 * @param tableName
	 * @param lovField
	 * @param filters
	 * @param fieldValue
	 * @return
	 */
	@Override
	public int validateExtendedComboBoxData(String tableName, String lovField, Object[][] filters, String fieldValue) {
		logger.debug("Entering");

		String valueColumn = lovField;
		String filterColumn = filters[0][0].toString();
		String filterColumnValue = filters[0][2].toString();

		MapSqlParameterSource source = new MapSqlParameterSource();

		if (!filterColumn.contains("IsActive")) {
			source.addValue("filterColumnValue", filterColumnValue);
		} else {
			try {
				source.addValue("filterColumnValue", Integer.parseInt(filterColumnValue));
			} catch (NumberFormatException e) {
				logger.debug("Exception: ", e);
				source.addValue("filterColumnValue", filterColumnValue);
			}
		}
		source.addValue("Value", fieldValue);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM ");
		selectSql.append(tableName);
		selectSql.append(" WHERE ");
		selectSql.append(valueColumn);
		selectSql.append("= :Value");
		if (StringUtils.isNotBlank(filterColumn)) {
			selectSql.append(" AND " + filterColumn);
			selectSql.append("= :filterColumnValue");
		}
		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}

		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * Method for check the ExtendedFields with the given reference and seqNo is available or not
	 * 
	 * @param reference
	 * @param seqNo
	 * @param tableName
	 */
	@Override
	public boolean isExists(String reference, int seqNo, String tableName) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder(" Select count(*) from ");
		sql.append(tableName);
		sql.append(" where Reference = :Reference AND SeqNo = :SeqNo ");

		source.addValue("Reference", reference);
		source.addValue("SeqNo", seqNo);

		int count = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		if (count > 0) {
			return true;
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	@Override
	public List<Map<String, Object>> getExtendedFieldMap(long VerificationId, String tableName, String type) {
		logger.debug("Entering");

		List<Map<String, Object>> renderMap = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("VerificationId", VerificationId);

		StringBuilder sql = null;
		sql = new StringBuilder("Select * from ");
		sql.append(tableName);
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where  VerificationId = :VerificationId ");

		logger.debug("selectSql: " + sql.toString());
		try {
			renderMap = this.jdbcTemplate.queryForList(sql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exceprtion ", e);
			renderMap = null;
		}

		logger.debug("Leaving");
		return renderMap;
	}

	@Override
	public String getCategory(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select fincategory from financemain_view where finreference = :finreference");
			source = new MapSqlParameterSource();
			source.addValue("finreference", finReference);
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

		return null;
	}

	@Override
	public Map<String, Object> getCollateralMap(String reference, String tableName, String type) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> map = new HashMap<>();

		type = type.toLowerCase();

		StringBuilder sql = new StringBuilder();
		if (StringUtils.startsWith(type, "_view")) {
			sql.append("select * from (select * from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" t1  union all select * from ");
			sql.append(tableName);
			sql.append(" t1  where not exists (select 1 from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" where reference = t1.reference)) t ");
		} else {
			sql.append("select * from ");
			sql.append(tableName);
			sql.append(StringUtils.trimToEmpty(type));
		}
		sql.append(" where reference = :reference");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", reference);
		try {
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.queryForMap(sql.toString(), source);
		} catch (DataAccessException e) {
			logger.error(Literal.ENTERING, e);
			return map;
		}
	}

	@Override
	public Map<String, Object> getCollateralMap(String reference, int seqNo, String tableName, String type) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> map = new HashMap<>();

		type = type.toLowerCase();

		StringBuilder sql = new StringBuilder();
		if (StringUtils.startsWith(type, "_view")) {
			sql.append("select * from (select * from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" t1  union all select * from ");
			sql.append(tableName);
			sql.append(" t1  where not exists (select 1 from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" where reference = t1.reference)) t ");
		} else {
			sql.append("select * from ");
			sql.append(tableName);
			sql.append(StringUtils.trimToEmpty(type));
		}
		sql.append(" where reference = :reference and seqno = :seqno");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", reference);
		source.addValue("seqno", seqNo);
		try {
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.queryForMap(sql.toString(), source);
		} catch (DataAccessException e) {
			logger.error(Literal.ENTERING, e);
			return map;
		}
	}

	@Override
	public Map<String, String> getAllExtendedFieldMap(String tableName, String type) {
		logger.debug(Literal.ENTERING);

		Map<String, String> renderMap = null;

		type = StringUtils.trimToEmpty(type);

		type = type.toLowerCase();

		StringBuilder sql = new StringBuilder();
		if (StringUtils.startsWith(type, "_view")) {
			sql.append("select * from (select Reference,UpdateCpID from ");
			sql.append(tableName);
			sql.append("_TEMP");
			sql.append(" t1  union all select Reference,UpdateCpID from ");
			sql.append(tableName);
			sql.append(" t1  where not exists (select 1 from ");
			sql.append(tableName);
			sql.append("_TEMP");
			sql.append(" where reference = t1.reference and seqno = t1.seqno)) t where t.reference = :reference ");
		} else {
			sql.append("select Reference,UpdateCpID  from ");
			sql.append(tableName);
			sql.append(StringUtils.trimToEmpty(type));
		}
		logger.trace(Literal.SQL + sql.toString());
		try {
			renderMap = this.jdbcTemplate.query(sql.toString(), new ResultSetExtractor<Map<String, String>>() {
				@Override
				public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Map<String, String> mapRet = new HashMap<String, String>();
					while (rs.next()) {
						if (rs.getString("UpdateCpID") == null) {
							mapRet.put(rs.getString("Reference"), rs.getString("UpdateCpID"));
						}
					}
					return mapRet;
				}
			});
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			renderMap = new HashMap<String, String>();
		}

		logger.debug(Literal.LEAVING);
		return renderMap;
	}

	@Override
	public Map<String, String> getAllExtendedFieldMapForUpdateCpid(String tableName, String type) {
		logger.debug(Literal.ENTERING);

		Map<String, String> renderMap = null;

		type = StringUtils.trimToEmpty(type);

		type = type.toLowerCase();

		StringBuilder sql = new StringBuilder();
		if (StringUtils.startsWith(type, "_view")) {
			sql.append("select * from (select Reference,UpdateCpID from ");
			sql.append(tableName);
			sql.append("_TEMP");
			sql.append(" t1  union all select Reference,UpdateCpID from ");
			sql.append(tableName);
			sql.append(" t1  where not exists (select 1 from ");
			sql.append(tableName);
			sql.append("_TEMP");
			sql.append(" where reference = t1.reference and seqno = t1.seqno)) t where t.reference = :reference ");
		} else {
			sql.append("select Reference,UpdateCpID  from ");
			sql.append(tableName);
			sql.append(StringUtils.trimToEmpty(type));
		}
		logger.trace(Literal.SQL + sql.toString());
		try {
			renderMap = this.jdbcTemplate.query(sql.toString(), new ResultSetExtractor<Map<String, String>>() {
				@Override
				public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Map<String, String> mapRet = new HashMap<String, String>();
					while (rs.next()) {
						if (rs.getString("UpdateCpID") != null) {
							mapRet.put(rs.getString("Reference"), rs.getString("UpdateCpID"));
						}
					}
					return mapRet;
				}
			});
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			renderMap = new HashMap<String, String>();
		}

		logger.debug(Literal.LEAVING);
		return renderMap;
	}

}
