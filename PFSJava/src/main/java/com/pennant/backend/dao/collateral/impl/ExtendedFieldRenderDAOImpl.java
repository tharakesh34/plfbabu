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
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class ExtendedFieldRenderDAOImpl extends BasicDao<ExtendedFieldRender> implements ExtendedFieldRenderDAO {
	private static Logger logger = LogManager.getLogger(ExtendedFieldRenderDAOImpl.class);

	public ExtendedFieldRenderDAOImpl() {
		super();
	}

	@Override
	public ExtendedFieldRender getExtendedFieldDetails(String reference, int seqNo, String tableName, String type) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("SeqNo", seqNo);

		StringBuilder sql = new StringBuilder("select Reference, SeqNo, Version, LastMntOn, LastMntBy, ");
		sql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId from ");
		sql.append(tableName);
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where  Reference = :Reference AND SeqNo = :SeqNo ");

		RowMapper<ExtendedFieldRender> typeRowMapper = BeanPropertyRowMapper.newInstance(ExtendedFieldRender.class);
		logger.debug(Literal.SQL + sql.toString());
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Get Extended field details Maps by Reference
	 */
	@Override
	public List<Map<String, Object>> getExtendedFieldMap(String reference, String tableName, String type) {
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
			sql.append(
					" where reference = t1.reference and seqno = t1.seqno)) t where t.reference = :reference order by seqno");
		} else if (StringUtils.startsWith(type, "_mview")) {
			sql.append("Select * from (select * from ");
			sql.append(tableName);
			sql.append(" t1 Where Not Exists (Select 1 From ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" Where reference = t1.reference And seqno = t1.seqno) Union All select * from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" t1  Where Exists (Select 1 from ");
			sql.append(tableName);
			sql.append(" Where reference = t1.reference And seqno = t1.seqno)) t");
			sql.append(" Where t.reference = :reference Order By seqno");
		} else {
			sql.append("select * from ");
			sql.append(tableName);
			sql.append(StringUtils.trimToEmpty(type));
			sql.append(" where reference = :reference order by seqno");
		}

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", reference);

		return this.jdbcTemplate.queryForList(sql.toString(), source);
	}

	/**
	 * Get Extended field details Maps by verificationId
	 */
	@Override
	public List<Map<String, Object>> getExtendedFieldMapByVerificationId(long verificationId, String tableName) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("verificationId", verificationId);

		StringBuilder selectSql = new StringBuilder("Select * from ");
		selectSql.append(tableName);
		selectSql.append(" where  verificationId = :verificationId order by seqno");

		logger.debug(Literal.SQL + selectSql.toString());

		return this.jdbcTemplate.queryForList(selectSql.toString(), source);
	}

	/**
	 * Get Extended field details Maps by Reference
	 */
	@Override
	public Map<String, Object> getExtendedField(String reference, String tableName, String type) {
		logger.debug("Entering");

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

		logger.debug(Literal.SQL + selectSql.toString());
		try {
			return this.jdbcTemplate.queryForMap(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Get Extended field details Maps by Reference
	 */
	@Override
	public Map<String, Object> getExtendedField(String reference, int seqNo, String tableName, String type) {
		logger.debug("Entering");

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

		logger.debug(Literal.SQL + selectSql.toString());
		try {
			return this.jdbcTemplate.queryForMap(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
				query.append(" set ").append(list.get(i)).append(" = :").append(list.get(i));
			} else {
				query.append(",").append(list.get(i)).append(" = :").append(list.get(i));
			}
		}
		insertSql.append(query);
		insertSql.append(" where Reference = :Reference AND SeqNo = :SeqNo");

		MapSqlParameterSource source = new MapSqlParameterSource(mappedValues);
		source.addValue("Reference", reference);
		source.addValue("SeqNo", seqNo);

		logger.debug(Literal.SQL + insertSql.toString());
		this.jdbcTemplate.update(insertSql.toString(), source);
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

		logger.debug(Literal.SQL + deleteSql.toString());
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
		logger.debug(Literal.SQL + insertSql.toString());
		try {
			this.jdbcTemplate.update(insertSql.toString(), mappedValues);
		} catch (DataIntegrityViolationException e) {
			throw new ConcurrencyException(e);
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

		StringBuilder selectSql = new StringBuilder("Select  COALESCE(Max(SeqNo), 0) from ");
		selectSql.append(tableName);
		selectSql.append(" where  Reference = :Reference ");

		logger.debug(Literal.SQL + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	/**
	 * This method Deletes the list of extendedfiled Records.
	 * 
	 * @param collateralRef
	 * @param tableNmae
	 * @param tableType     (String) ""/_Temp/
	 * @return void
	 * 
	 */
	@Override
	public void deleteList(String reference, String tableNmae, String tableType) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From  ");
		sql.append(tableNmae);
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where Reference = :Reference");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
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
		// FIXME: Need to change the method implementation
		if (column.equals("CustGrpID") || column.equals("EmpName")) {
			source.addValue("Value", fieldValue);
			column = "EmployerId";
			source.addValue("ColumnName", "EmployerId");
		} else if (column.equals("DealerName")) {
			tempFix = true;
			source.addValue("Value", fieldValue);
		}

		StringBuilder selectSql = new StringBuilder();
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
		logger.debug(Literal.SQL + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
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

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM ");
		selectSql.append(tableName);
		selectSql.append(" WHERE ");
		selectSql.append(valueColumn);
		selectSql.append("= :Value");
		if (StringUtils.isNotBlank(filterColumn)) {
			selectSql.append(" AND " + filterColumn);
			selectSql.append("= :filterColumnValue");
		}
		logger.debug(Literal.SQL + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
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
	public List<Map<String, Object>> getExtendedFieldMap(long verificationId, String tableName, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("VerificationId", verificationId);

		StringBuilder sql = null;
		sql = new StringBuilder("Select * from ");
		sql.append(tableName);
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where  VerificationId = :VerificationId ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcTemplate.queryForList(sql.toString(), source);
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
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Map<String, Object> getCollateralMap(String reference, String tableName, String type) {
		logger.debug(Literal.ENTERING);

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
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return new HashMap<String, Object>();
		}
	}

	@Override
	public Map<String, Object> getCollateralMap(String reference, int seqNo, String tableName, String type) {
		logger.debug(Literal.ENTERING);

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
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return new HashMap<String, Object>();
		}
	}

	@Override
	public Map<String, String> getAllExtendedFieldMap(String tableName, String type) {
		logger.debug(Literal.ENTERING);

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

		return this.jdbcTemplate.query(sql.toString(), new ResultSetExtractor<Map<String, String>>() {
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
	}

	@Override
	public Map<String, String> getAllExtendedFieldMapForUpdateCpid(String tableName, String type) {
		logger.debug(Literal.ENTERING);

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
		return this.jdbcTemplate.query(sql.toString(), new ResultSetExtractor<Map<String, String>>() {
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
	}

	@Override
	public int getMaxSeq(String reference, String tableName, String tableType) {

		StringBuilder sql = new StringBuilder();
		if (StringUtils.startsWithIgnoreCase(tableType, "_View")) {
			sql.append("select COALESCE(max(seqno),0) from (select seqno,reference from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" t1  union all select seqno,reference from ");
			sql.append(tableName);
			sql.append(" t1  where not exists (select 1 from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" where reference = t1.reference AND SeqNo = t1.SeqNo)) t where t.reference = :reference ");
		} else {
			sql.append("select COALESCE(max(seqno),0) from ");
			sql.append(tableName);
			sql.append(StringUtils.trimToEmpty(tableType));
			sql.append(" where reference = :reference");
		}
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", reference);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 1;
		}
	}

	/**
	 * Get Extended field details Maps by Reference
	 */
	@Override
	public Map<String, Object> getExtendedField(String reference, long instructionUID, String tableName, String type) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("InstructionUID", instructionUID);

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
			selectSql.append(" Where  Reference =T1.Reference And SeqNo = T1.SeqNo)) T");
			selectSql.append(" WHERE T.Reference = :Reference and T.InstructionUID = :InstructionUID");
		} else {
			selectSql = new StringBuilder("Select * from ");
			selectSql.append(tableName);
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" where  Reference = :Reference and InstructionUID = :InstructionUID");
		}

		logger.debug(Literal.SQL + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForMap(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void deleteList(String reference, int seqNo, String tableName, String tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From  ");
		sql.append(tableName);
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where Reference = :Reference And SeqNo = :SeqNo");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("SeqNo", seqNo);

		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public String getUCICNumber(String tablename, Object ucic) {
		StringBuilder sql = new StringBuilder("Select Reference from");
		sql.append(" CUSTOMER_");
		sql.append(tablename);
		sql.append("_ED");
		sql.append(" where UCIC = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, ucic);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
