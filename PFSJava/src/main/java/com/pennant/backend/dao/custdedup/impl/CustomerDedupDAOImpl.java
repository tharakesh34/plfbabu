package com.pennant.backend.dao.custdedup.impl;

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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerDedupDAOImpl extends BasicDao<CustomerDedup> implements CustomerDedupDAO {
	private static Logger logger = LogManager.getLogger(CustomerDedupDAOImpl.class);

	public CustomerDedupDAOImpl() {
		super();
	}

	@Override
	public void saveList(List<CustomerDedup> insertList, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into CustomerDedupDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference , CustCIF , CustFName , CustLName , ");
		insertSql.append(" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ");
		insertSql.append(" DedupRule , Override , OverrideUser ,Module )");
		insertSql.append(" Values(:FinReference , :CustCIF , :CustFName , :CustLName , ");
		insertSql.append(
				" :CustShrtName , :CustDOB , :CustCRCPR ,:CustPassportNo , :MobileNumber , :CustNationality , ");
		insertSql.append(" :DedupRule , :Override , :OverrideUser, :Module )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(insertList.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void updateList(List<CustomerDedup> updateList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerDedupDetail Set CustFName = :CustFName,");
		updateSql.append(" CustLName = :CustLName , CustShrtName = :CustShrtName, CustDOB = :CustDOB, ");
		updateSql.append(
				" CustCRCPR= :CustCRCPR, CustPassportNo = :CustPassportNo,MobileNumber = :MobileNumber, CustNationality = :CustNationality,");
		updateSql
				.append(" DedupRule = :DedupRule, Override = :Override, OverrideUser = :OverrideUser, Module=:Module ");
		updateSql.append(" Where FinReference =:FinReference  AND CustCIF =:CustCIF");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<CustomerDedup> fetchOverrideCustDedupData(String finReference, String queryCode, String module) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, CustCIF, CustFName, CustLName, CustShrtName");
		sql.append(", CustDOB, CustCRCPR, CustPassportNo, MobileNumber, CustNationality");
		sql.append(", DedupRule , Override , OverrideUser,Module");
		sql.append(" From CustomerDedupDetail");
		sql.append(" Where FinReference = ? and DedupRule like(?) and Module= ?");

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
			ps.setString(2, "%" + queryCode + "%");
			ps.setString(3, module);
		}, (rs, i) -> {
			CustomerDedup cd = new CustomerDedup();

			cd.setFinReference(rs.getString("FinReference"));
			cd.setCustCIF(rs.getString("CustCIF"));
			cd.setCustFName(rs.getString("CustFName"));
			cd.setCustLName(rs.getString("CustLName"));
			cd.setCustShrtName(rs.getString("CustShrtName"));
			cd.setCustDOB(rs.getTimestamp("CustDOB"));
			cd.setCustCRCPR(rs.getString("CustCRCPR"));
			cd.setCustPassportNo(rs.getString("CustPassportNo"));
			cd.setMobileNumber(rs.getString("MobileNumber"));
			cd.setCustNationality(rs.getString("CustNationality"));
			cd.setDedupRule(rs.getString("DedupRule"));
			cd.setOverride(rs.getBoolean("Override"));
			cd.setOverrideUser(rs.getString("OverrideUser"));
			cd.setModule(rs.getString("Module"));

			return cd;
		});
	}

	/**
	 * Fetched the Dedup Fields if Dedup Exist for a Customer
	 *
	 */
	public List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup dedup, String sqlQuery) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustId, CustCIF, CustFName, CustLName, CustCRCPR, CustPassportNo, CustShrtName");
		sql.append(", CustDOB, CustNationality, MobileNumber, CustCtgCode, CustDftBranch, CustSector");
		sql.append(", CustSubSector, AadharNumber, PanNumber, CustEMail");
		/* Below columns are not available in Bean */
		sql.append(", CustTypeCode, SubCategory, CasteId, ReligionId, CasteCode");
		sql.append(", CasteDesc, ReligionCode, ReligionDesc, lovdescCustCtgType");
		sql.append(" from CustomersDedup_View ");

		if (!StringUtils.isBlank(sqlQuery)) {
			sql.append(StringUtils.trimToEmpty(sqlQuery));
			sql.append(" and ");
		} else {
			sql.append(" Where");
		}
		sql.append(" CustId != :CustId");

		logger.debug(Literal.SQL + sql);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, (rs, rowNum) -> {
				CustomerDedup cd = new CustomerDedup();

				cd.setCustId(rs.getLong("CustId"));
				cd.setCustCIF(rs.getString("CustCIF"));
				cd.setCustFName(rs.getString("CustFName"));
				cd.setCustLName(rs.getString("CustLName"));
				cd.setCustCRCPR(rs.getString("CustCRCPR"));
				cd.setCustPassportNo(rs.getString("CustPassportNo"));
				cd.setCustShrtName(rs.getString("CustShrtName"));
				cd.setCustDOB(rs.getTimestamp("CustDOB"));
				cd.setCustNationality(rs.getString("CustNationality"));
				cd.setMobileNumber(rs.getString("MobileNumber"));
				cd.setCustCtgCode(rs.getString("CustCtgCode"));
				cd.setCustDftBranch(rs.getString("CustDftBranch"));
				cd.setCustSector(rs.getString("CustSector"));
				cd.setCustSubSector(rs.getString("CustSubSector"));
				cd.setAadharNumber(rs.getString("AadharNumber"));
				cd.setPanNumber(rs.getString("PanNumber"));
				cd.setCustEMail(rs.getString("CustEMail"));

				return cd;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records are not available in CustomersDedup_View");
		}

		return new ArrayList<>();
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
			selectSql.append(" SELECT * FROM CustomerDedupDetail");
			selectSql.append(" WHERE FinReference = :FinReference ");

			RowMapper<CustomerDedup> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerDedup.class);
			List<CustomerDedup> list = this.jdbcTemplate.query(selectSql.toString(), map, typeRowMapper);

			if (list != null && !list.isEmpty()) {
				saveList(list, suffix);
			}

		} catch (DataAccessException e) {
			logger.debug(e);
		}
		logger.debug(" Leaving ");

	}

}
