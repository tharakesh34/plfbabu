package com.pennant.backend.dao.collateral.impl;

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

import com.pennant.backend.dao.collateral.CoOwnerDetailDAO;
import com.pennant.backend.model.collateral.CoOwnerDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class CoOwnerDetailDAOImpl extends BasicDao<CoOwnerDetail> implements CoOwnerDetailDAO {
	private static Logger logger = LogManager.getLogger(CoOwnerDetailDAOImpl.class);

	public CoOwnerDetailDAOImpl() {
		super();
	}

	/**
	 * This method Deletes the Record from the CoOwnerDetail or CoOwnerDetail_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Guarantor Details by key CollateralRef and CoOwnerId
	 * 
	 * @param CoOwnerDetail Details (coOwnerDetail)
	 * @param type          (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(CoOwnerDetail coOwnerDetail, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From CollateralCoOwners");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CollateralRef = :CollateralRef AND CoOwnerId = :CoOwnerId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(coOwnerDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into coOwnerDetail or coOwnerDetail_Temp. it fetches the available
	 * 
	 * save CoOwnerDetail Details
	 * 
	 * @param CoOwnerDetail Details (coOwnerDetail)
	 * @param type          (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(CoOwnerDetail coOwnerDetail, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into CollateralCoOwners");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (CoOwnerId, CollateralRef, CustomerId, BankCustomer, CoOwnerIDType,");
		sql.append(" CoOwnerIDNumber,CoOwnerCIFName, CoOwnerPercentage, MobileNo, EmailId,");
		sql.append(" CoOwnerProofName, Remarks, AddrHNbr, FlatNbr, AddrStreet, AddrLine1, ");
		sql.append(" AddrLine2, POBox, AddrCity, AddrProvince, AddrCountry, AddrZIP, ");
		sql.append(" CoOwnerProof, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		sql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId) ");
		sql.append(" Values (:CoOwnerId, :CollateralRef, :CustomerId, :BankCustomer, :CoOwnerIDType,");
		sql.append(" :CoOwnerIDNumber, :CoOwnerCIFName, :CoOwnerPercentage, :MobileNo, :EmailId,");
		sql.append(" :CoOwnerProofName, :Remarks, :AddrHNbr, :FlatNbr, :AddrStreet, :AddrLine1, ");
		sql.append(" :AddrLine2, :POBox, :AddrCity, :AddrProvince, :AddrCountry, :AddrZIP, ");
		sql.append(" :CoOwnerProof, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId) ");
		logger.debug("insertSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(coOwnerDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return coOwnerDetail.getId();
	}

	/**
	 * This method Deletes the Record from the CoOwnerDetail or CoOwnerDetail_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete CoOwnerDetail Details by key CollateralRef and CoOwnerId
	 * 
	 * @param CoOwnerDetail Details (coOwnerDetail)
	 * @param type          (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CoOwnerDetail coOwnerDetail, String type) {
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();

		sql.append("Update CollateralCoOwners");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CustomerId = :CustomerId, BankCustomer = :BankCustomer, CoOwnerIDType = :CoOwnerIDType,");
		sql.append(
				" CoOwnerIDNumber = :CoOwnerIDNumber, CoOwnerCIFName = :CoOwnerCIFName, CoOwnerPercentage = :CoOwnerPercentage, ");
		sql.append(" MobileNo = :MobileNo, EmailId = :EmailId, CoOwnerProofName = :CoOwnerProofName,");
		sql.append(
				" Remarks = :Remarks, AddrHNbr = :AddrHNbr, FlatNbr = :FlatNbr, AddrStreet = :AddrStreet, AddrLine1 = :AddrLine1, ");
		sql.append(
				" AddrLine2 = :AddrLine2, POBox = :POBox, AddrCity = :AddrCity, AddrProvince = :AddrProvince, AddrCountry = :AddrCountry,");
		sql.append(
				" AddrZIP = :AddrZIP, CoOwnerProof = :CoOwnerProof, Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(
				" RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where CollateralRef = :CollateralRef AND CoOwnerId = :CoOwnerId ");
		logger.debug("updateSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(coOwnerDetail);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<CoOwnerDetail> getCoOwnerDetailByRef(String collateralRef, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CoOwnerId, CollateralRef, BankCustomer, CustomerId, CoOwnerIDType, CoOwnerIDNumber");
		sql.append(", CoOwnerCIFName, CoOwnerPercentage, MobileNo, EmailId, CoOwnerProofName, Remarks");
		sql.append(", AddrHNbr, FlatNbr, AddrStreet, AddrLine1, AddrLine2, POBox, AddrCity, AddrProvince");
		sql.append(", AddrCountry, AddrZIP, CoOwnerProof, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (type.contains("View")) {
			sql.append(", CoOwnerCIF");
		}

		sql.append(" From CollateralCoOwners");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, collateralRef);
		}, (rs, rowNum) -> {
			CoOwnerDetail cc = new CoOwnerDetail();

			cc.setCoOwnerId(rs.getInt("CoOwnerId"));
			cc.setCollateralRef(rs.getString("CollateralRef"));
			cc.setBankCustomer(rs.getBoolean("BankCustomer"));
			cc.setCustomerId(rs.getLong("CustomerId"));
			cc.setCoOwnerIDType(rs.getString("CoOwnerIDType"));
			cc.setCoOwnerIDNumber(rs.getString("CoOwnerIDNumber"));
			cc.setCoOwnerCIFName(rs.getString("CoOwnerCIFName"));
			cc.setCoOwnerPercentage(rs.getBigDecimal("CoOwnerPercentage"));
			cc.setMobileNo(rs.getString("MobileNo"));
			cc.setEmailId(rs.getString("EmailId"));
			cc.setCoOwnerProofName(rs.getString("CoOwnerProofName"));
			cc.setRemarks(rs.getString("Remarks"));
			cc.setAddrHNbr(rs.getString("AddrHNbr"));
			cc.setFlatNbr(rs.getString("FlatNbr"));
			cc.setAddrStreet(rs.getString("AddrStreet"));
			cc.setAddrLine1(rs.getString("AddrLine1"));
			cc.setAddrLine2(rs.getString("AddrLine2"));
			cc.setPOBox(rs.getString("POBox"));
			cc.setAddrCity(rs.getString("AddrCity"));
			cc.setAddrProvince(rs.getString("AddrProvince"));
			cc.setAddrCountry(rs.getString("AddrCountry"));
			cc.setAddrZIP(rs.getString("AddrZIP"));
			cc.setCoOwnerProof(rs.getBytes("CoOwnerProof"));
			cc.setVersion(rs.getInt("Version"));
			cc.setLastMntBy(rs.getLong("LastMntBy"));
			cc.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cc.setRecordStatus(rs.getString("RecordStatus"));
			cc.setRoleCode(rs.getString("RoleCode"));
			cc.setNextRoleCode(rs.getString("NextRoleCode"));
			cc.setTaskId(rs.getString("TaskId"));
			cc.setNextTaskId(rs.getString("NextTaskId"));
			cc.setRecordType(rs.getString("RecordType"));
			cc.setWorkflowId(rs.getLong("WorkflowId"));

			if (type.contains("View")) {
				cc.setCoOwnerCIF(rs.getString("CoOwnerCIF"));
			}

			return cc;
		});

	}

	@Override
	public CoOwnerDetail getCoOwnerDetailByRef(String collateralReference, int coOwnerId, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append("Select CoOwnerId, CollateralRef, BankCustomer, CustomerId, CoOwnerIDType,");
		sql.append("CoOwnerIDNumber,CoOwnerCIFName, CoOwnerPercentage, MobileNo, EmailId,");
		sql.append("CoOwnerProofName, Remarks, AddrHNbr, FlatNbr, AddrStreet, AddrLine1, ");
		sql.append("AddrLine2, POBox, AddrCity, AddrProvince, AddrCountry, AddrZIP, ");
		sql.append("CoOwnerProof, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		sql.append("NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(",coOwnerIDTypeName");
		}
		sql.append(" From CollateralCoOwners");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef AND CoOwnerId = :CoOwnerId");
		logger.debug("selectSql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralReference);
		source.addValue("CoOwnerId", coOwnerId);

		RowMapper<CoOwnerDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(CoOwnerDetail.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Get version of co-Owner details
	 * 
	 * @param collateralRef
	 * @param tableType
	 * @return Integer
	 */
	@Override
	public int getVersion(String collateralRef, String tableType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Version FROM CollateralCoOwners ");
		selectSql.append(" WHERE CollateralRef = :CollateralRef");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	/**
	 * This method Deletes the Record from the CoOwnerDetail or CoOwnerDetail_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete CoOwnerDetail Details by key CollateralRef
	 * 
	 * @param CoOwnerDetail Details (coOwnerDetail)
	 * @param type          (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteList(String collateralRef, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder("Delete From CollateralCoOwners");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("deleteSql: " + sql.toString());
		source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug("Leaving");
	}
}
