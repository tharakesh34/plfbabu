package com.pennant.backend.dao.collateral.impl;

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

import com.pennant.backend.dao.collateral.CoOwnerDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.collateral.CoOwnerDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;

public class CoOwnerDetailDAOImpl extends BasisNextidDaoImpl<CoOwnerDetail> implements CoOwnerDetailDAO {
	private static Logger	logger	= Logger.getLogger(CoOwnerDetailDAOImpl.class);

	public CoOwnerDetailDAOImpl() {
		super();
	}

	private NamedParameterJdbcTemplate	jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the CoOwnerDetail or CoOwnerDetail_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Guarantor Details by key CollateralRef
	 * and CoOwnerId
	 * 
	 * @param CoOwnerDetail
	 *            Details (coOwnerDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
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
	 * @param CoOwnerDetail
	 *            Details (coOwnerDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
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
	 * This method Deletes the Record from the CoOwnerDetail or CoOwnerDetail_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete CoOwnerDetail Details by key CollateralRef
	 * and CoOwnerId
	 * 
	 * @param CoOwnerDetail
	 *            Details (coOwnerDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
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
		sql.append(" CoOwnerIDNumber = :CoOwnerIDNumber, CoOwnerCIFName = :CoOwnerCIFName, CoOwnerPercentage = :CoOwnerPercentage, ");
		sql.append(" MobileNo = :MobileNo, EmailId = :EmailId, CoOwnerProofName = :CoOwnerProofName,");
		sql.append(" Remarks = :Remarks, AddrHNbr = :AddrHNbr, FlatNbr = :FlatNbr, AddrStreet = :AddrStreet, AddrLine1 = :AddrLine1, ");
		sql.append(" AddrLine2 = :AddrLine2, POBox = :POBox, AddrCity = :AddrCity, AddrProvince = :AddrProvince, AddrCountry = :AddrCountry,");
		sql.append(" AddrZIP = :AddrZIP, CoOwnerProof = :CoOwnerProof, Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
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
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append("Select CoOwnerId, CollateralRef, BankCustomer, CustomerId, CoOwnerIDType,");
		sql.append("CoOwnerIDNumber,CoOwnerCIFName, CoOwnerPercentage, MobileNo, EmailId,");
		sql.append("CoOwnerProofName, Remarks, AddrHNbr, FlatNbr, AddrStreet, AddrLine1, ");
		sql.append("AddrLine2, POBox, AddrCity, AddrProvince, AddrCountry, AddrZIP, ");
		sql.append("CoOwnerProof, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		sql.append("NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" From CollateralCoOwners");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("selectSql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		RowMapper<CoOwnerDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CoOwnerDetail.class);
		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}
		return null;
	}

	@Override
	public CoOwnerDetail getCoOwnerDetailByRef(String collateralReference, int coOwnerId, String type) {
		logger.debug("Entering");
		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
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

		source = new MapSqlParameterSource();

		source.addValue("CollateralRef", collateralReference);
		source.addValue("CoOwnerId", coOwnerId);

		RowMapper<CoOwnerDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CoOwnerDetail.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception :", e);
		} finally {
			source = null;
		}
		logger.debug("Leaving");
		return null;
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

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CollateralCoOwners ");
		selectSql.append(" WHERE CollateralRef = :CollateralRef");

		logger.debug("selectSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.info(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * This method Deletes the Record from the CoOwnerDetail or CoOwnerDetail_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete CoOwnerDetail Details by key CollateralRef
	 * 
	 * @param CoOwnerDetail
	 *            Details (coOwnerDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteList(CoOwnerDetail coOwnerDetail, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From CollateralCoOwners");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(coOwnerDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}
}
