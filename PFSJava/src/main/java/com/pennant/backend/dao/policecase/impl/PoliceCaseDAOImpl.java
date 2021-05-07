package com.pennant.backend.dao.policecase.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.policecase.PoliceCaseDAO;
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class PoliceCaseDAOImpl extends BasicDao<PoliceCaseDetail> implements PoliceCaseDAO {
	private static Logger logger = LogManager.getLogger(PoliceCaseDAOImpl.class);

	public PoliceCaseDAOImpl() {
		super();
	}

	public void saveList(List<PoliceCase> policeCase, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinPoliceCaseDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				"(FinReference, CustCIF, CustFName, CustLName , CustDOB, CustCRCPR, CustPassportNo ,MobileNumber, ");
		insertSql.append(" CustNationality, CustProduct, PoliceCaseRule, Override, OverrideUser ) ");
		insertSql.append(
				" Values(:FinReference, :CustCIF , :CustFName , :CustLName , :CustDOB ,  :CustCRCPR , :CustPassportNo ,");
		insertSql.append(
				" :MobileNumber , :CustNationality , :CustProduct , :PoliceCaseRule, :Override , :OverrideUser)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(policeCase.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	@Override
	public List<PoliceCase> fetchPoliceCase(String finReference, String queryCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, CustCIF, CustFName, CustLName, CustDOB, CustCRCPR");
		sql.append(", CustPassportNo, MobileNumber, CustNationality");
		sql.append(", CustProduct, PoliceCaseRule, Override, OverrideUser");
		sql.append(" From FinPoliceCaseDetail");
		sql.append(" Where FinReference = ? and PoliceCaseRule LIKE (?)");

		logger.trace(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
			ps.setString(2, "%" + queryCode + "%");
		}, (rs, i) -> {
			PoliceCase pc = new PoliceCase();

			pc.setFinReference(rs.getString("FinReference"));
			pc.setCustCIF(rs.getString("CustCIF"));
			pc.setCustFName(rs.getString("CustFName"));
			pc.setCustLName(rs.getString("CustLName"));
			pc.setCustDOB(rs.getTimestamp("CustDOB"));
			pc.setCustCRCPR(rs.getString("CustCRCPR"));
			pc.setCustPassportNo(rs.getString("CustPassportNo"));
			pc.setMobileNumber(rs.getString("MobileNumber"));
			pc.setCustNationality(rs.getString("CustNationality"));
			pc.setCustProduct(rs.getString("CustProduct"));
			pc.setPoliceCaseRule(rs.getString("PoliceCaseRule"));
			pc.setOverride(rs.getBoolean("Override"));
			pc.setOverrideUser(rs.getString("OverrideUser"));

			return pc;
		});

	}

	@Override
	public List<PoliceCase> fetchFinPoliceCase(String finReference) {
		logger.debug("Entering");

		PoliceCase policeCase = new PoliceCase();
		policeCase.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" Select FinReference , CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ");
		selectSql.append(" CustProduct , PoliceCaseRule , Override , OverrideUser ");
		selectSql.append(" From FinPoliceCaseDetail");
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCase);
		RowMapper<PoliceCase> typeRowMapper = BeanPropertyRowMapper.newInstance(PoliceCase.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<PoliceCaseDetail> fetchCorePolice(PoliceCaseDetail policecase, String sqlQuery) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select * FROM PoliceCaseCustomers ");
		sql.append(StringUtils.trimToEmpty(sqlQuery));

		logger.trace(Literal.SQL + sql);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policecase);
		BeanPropertyRowMapper<PoliceCaseDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(PoliceCaseDetail.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in PoliceCaseCustomers for the specified Rule >> {}", sqlQuery);
		}

		return new ArrayList<>();
	}

	@Override
	public void deleteList(String finReference) {
		logger.debug("Entering");

		PoliceCaseDetail policeCase = new PoliceCaseDetail();
		policeCase.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FinPoliceCaseDetail");
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCase);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void updatePoliceCaseList(List<PoliceCase> policeCase) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPoliceCaseDetail");
		updateSql.append(" Set CustFName=:CustFName , CustLName=:CustLName , ");
		updateSql.append(
				" CustDOB=:CustDOB , CustCRCPR=:CustCRCPR , CustPassportNo=:CustPassportNo , MobileNumber=:MobileNumber , ");
		updateSql.append(
				" CustNationality=:CustNationality , CustProduct=:CustProduct , PoliceCaseRule=:PoliceCaseRule ,Override=:Override, ");
		updateSql.append(" OverrideUser=:OverrideUser ");
		updateSql.append(" WHERE FinReference=:FinReference AND CustCIF=:CustCIF ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(policeCase.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public PoliceCaseDetail getPoliceCaseDetailById(String id, String type) {
		logger.debug("Entering");

		PoliceCaseDetail policeCaseDetail = new PoliceCaseDetail();
		policeCaseDetail.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality ,CustProduct, ");
		selectSql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if ("_View".equals(type)) {
			selectSql.append(", lovDescNationalityDesc ");
		}
		selectSql.append(" From PoliceCaseCustomers" + StringUtils.trimToEmpty(type));

		selectSql.append(" Where CustCIF =:CustCIF ");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(policeCaseDetail);
		RowMapper<PoliceCaseDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(PoliceCaseDetail.class);

		try {
			policeCaseDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			policeCaseDetail = null;
		}
		logger.debug("Leaving");
		return policeCaseDetail;
	}

	@Override
	public boolean isDuplicateKey(String custCIF, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "CustCIF = :custCIF";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PoliceCaseCustomers", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PoliceCaseCustomers_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PoliceCaseCustomers_Temp", "PoliceCaseCustomers" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("custCIF", custCIF);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(PoliceCaseDetail policeCaseDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into PoliceCaseCustomers");
		sql.append(tableType.getSuffix());
		sql.append(
				"(CustCIF, CustFName, CustLName , CustDOB, CustCRCPR, CustPassportNo ,MobileNumber,CustNationality, CustProduct,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(
				" values(:CustCIF , :CustFName , :CustLName , :CustDOB ,  :CustCRCPR , :CustPassportNo ,:MobileNumber , :CustNationality ,:CustProduct, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		sql.append(" :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(policeCaseDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return policeCaseDetail.getId();
	}

	@Override
	public void update(PoliceCaseDetail policeCaseDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder();
		sql.append("update PoliceCaseCustomers");
		sql.append(tableType.getSuffix());
		sql.append(" set CustFName=:CustFName , CustLName=:CustLName , ");
		sql.append(
				" CustDOB=:CustDOB , CustCRCPR=:CustCRCPR , CustPassportNo=:CustPassportNo , MobileNumber=:MobileNumber , CustNationality=:CustNationality, CustProduct=:CustProduct, ");
		sql.append(
				" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		sql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" wHERE CustCIF=:CustCIF ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(policeCaseDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PoliceCaseDetail policeCaseDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL
		StringBuilder sql = new StringBuilder();
		sql.append("delete from PoliceCaseCustomers");
		sql.append(tableType.getSuffix());
		sql.append(" where CustCIF =:CustCIF");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(policeCaseDetail);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void moveData(String finReference, String suffix) {

		logger.debug(" Entering ");
		try {
			if (StringUtils.isBlank(suffix)) {
				return;
			}

			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue("FinReference", finReference);

			StringBuilder selectSql = new StringBuilder();
			selectSql.append(" SELECT * FROM FinPoliceCaseDetail");
			selectSql.append(" WHERE FinReference = :FinReference ");

			RowMapper<PoliceCase> typeRowMapper = BeanPropertyRowMapper.newInstance(PoliceCase.class);
			List<PoliceCase> list = this.jdbcTemplate.query(selectSql.toString(), map, typeRowMapper);

			if (list != null && !list.isEmpty()) {
				saveList(list, suffix);
			}

		} catch (DataAccessException e) {
			logger.debug(e);
		}
		logger.debug(" Leaving ");

	}
}
