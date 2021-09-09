package com.pennant.backend.dao.custdedup.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerDedupDAOImpl extends BasicDao<CustomerDedup> implements CustomerDedupDAO {
	private static Logger logger = LogManager.getLogger(CustomerDedupDAOImpl.class);

	public CustomerDedupDAOImpl() {
		super();
	}

	@Override
	public void saveList(List<CustomerDedup> cdList, String type) {
		StringBuilder sql = new StringBuilder("Insert Into CustomerDedupDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("( FinID, FinReference, CustCIF, CustFName, CustLName, CustShrtName, CustDOB, CustCRCPR");
		sql.append(", CustPassportNo, MobileNumber, CustNationality, DedupRule, Override, OverrideUser");
		sql.append(", Module");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;

				CustomerDedup cd = cdList.get(i);

				ps.setLong(index++, cd.getFinID());
				ps.setString(index++, cd.getFinReference());
				ps.setString(index++, cd.getCustCIF());
				ps.setString(index++, cd.getCustFName());
				ps.setString(index++, cd.getCustLName());
				ps.setString(index++, cd.getCustShrtName());
				ps.setDate(index++, JdbcUtil.getDate(cd.getCustDOB()));
				ps.setString(index++, cd.getCustCRCPR());
				ps.setString(index++, cd.getCustPassportNo());
				ps.setString(index++, cd.getMobileNumber());
				ps.setString(index++, cd.getCustNationality());
				ps.setString(index++, cd.getDedupRule());
				ps.setBoolean(index++, cd.isOverride());
				ps.setString(index++, cd.getOverrideUser());
				ps.setString(index++, cd.getModule());
			}

			@Override
			public int getBatchSize() {
				return cdList.size();
			}
		});
	}

	@Override
	public void updateList(List<CustomerDedup> cdList) {
		StringBuilder sql = new StringBuilder("Update CustomerDedupDetail");
		sql.append(" Set CustFName = ?, CustLName = ?, CustShrtName = ?, CustDOB = ?");
		sql.append(", CustCRCPR= ?, CustPassportNo = ?, MobileNumber = ?, CustNationality = ?");
		sql.append(", DedupRule = ?, Override = ?, OverrideUser = ?, Module = ?");
		sql.append(" Where FinID = ? and CustCIF = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;

				CustomerDedup cd = cdList.get(i);

				ps.setString(index++, cd.getCustFName());
				ps.setString(index++, cd.getCustLName());
				ps.setString(index++, cd.getCustShrtName());
				ps.setDate(index++, JdbcUtil.getDate(cd.getCustDOB()));
				ps.setString(index++, cd.getCustCRCPR());
				ps.setString(index++, cd.getCustPassportNo());
				ps.setString(index++, cd.getMobileNumber());
				ps.setString(index++, cd.getCustNationality());
				ps.setString(index++, cd.getDedupRule());
				ps.setBoolean(index++, cd.isOverride());
				ps.setString(index++, cd.getOverrideUser());
				ps.setString(index++, cd.getModule());

				ps.setLong(index++, cd.getFinID());
				ps.setString(index++, cd.getCustCIF());
			}

			@Override
			public int getBatchSize() {
				return cdList.size();
			}
		});
	}

	@Override
	public List<CustomerDedup> fetchOverrideCustDedupData(String finReference, String queryCode, String module) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, CustCIF, CustFName, CustLName, CustShrtName");
		sql.append(", CustDOB, CustCRCPR, CustPassportNo, MobileNumber, CustNationality");
		sql.append(", DedupRule , Override , OverrideUser, Module");
		sql.append(" From CustomerDedupDetail");
		sql.append(" Where FinReference = ? and DedupRule like(?) and Module= ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
			ps.setString(2, "%" + queryCode + "%");
			ps.setString(3, module);
		}, (rs, i) -> {
			CustomerDedup cd = new CustomerDedup();

			cd.setFinID(rs.getLong("FinID"));
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
	}

	@Override
	public void moveData(String finReference, String suffix) {
		/* FIXME : change to FinID Pre-approved(_PA) tables need to remove */
		if (StringUtils.isBlank(suffix)) {
			return;
		}

		try {
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
			//
		}

	}

}
