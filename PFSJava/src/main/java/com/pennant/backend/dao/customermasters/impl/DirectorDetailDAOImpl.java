/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : DirectorDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-12-2011 * *
 * Modified Date : 01-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters.impl;

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

import com.pennant.backend.dao.customermasters.DirectorDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>DirectorDetail model</b> class.<br>
 * 
 */
public class DirectorDetailDAOImpl extends SequenceDao<DirectorDetail> implements DirectorDetailDAO {
	private static Logger logger = LogManager.getLogger(DirectorDetailDAOImpl.class);

	public DirectorDetailDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new DirectorDetail
	 * 
	 * @return DirectorDetail
	 */
	@Override
	public DirectorDetail getDirectorDetail() {
		logger.debug(Literal.ENTERING);
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("DirectorDetail");
		DirectorDetail directorDetail = new DirectorDetail();
		if (workFlowDetails != null) {
			directorDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		directorDetail.setId(getNextValue("SeqCustomerDirectorDetail"));
		logger.debug("get NextID:" + directorDetail.getId());
		logger.debug(Literal.LEAVING);
		return directorDetail;
	}

	/**
	 * This method get the module from method getDirectorDetail() and set the new record flag as true and return
	 * DirectorDetail()
	 * 
	 * @return DirectorDetail
	 */
	@Override
	public DirectorDetail getNewDirectorDetail() {
		logger.debug(Literal.ENTERING);
		DirectorDetail directorDetail = getDirectorDetail();
		directorDetail.setNewRecord(true);
		logger.debug(Literal.LEAVING);
		return directorDetail;
	}

	/**
	 * Fetch the Record Director Detail details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return DirectorDetail
	 */
	@Override
	public DirectorDetail getDirectorDetailById(final long id, final long custID, String type) {
		logger.debug(Literal.ENTERING);
		DirectorDetail directorDetail = new DirectorDetail();

		directorDetail.setId(id);
		directorDetail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder("Select DirectorId, CustID, ShareHolderCustID, FirstName,");
		selectSql.append(
				" MiddleName, LastName, ShortName, CustGenderCode, CustSalutationCode,SharePerc,Shareholder,ShareholderCustomer, Director,Designation, ");
		selectSql.append(" CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustAddrLine1, CustAddrLine2,");
		selectSql.append(" CustPOBox, CustAddrCity, CustAddrProvince, CustAddrCountry, CustAddrZIP,");
		selectSql.append(" CustAddrPhone, CustAddrFrom, IdType, IdReference, Nationality, Dob,");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" lovDescShareHolderCustCIF,lovDescCustGenderCodeName,lovDescCustSalutationCodeName,");
			selectSql.append(" lovDescCustAddrCityName,lovDescCustAddrProvinceName,");
			selectSql.append(
					" lovDescCustAddrCountryName, lovDescCustRecordType , lovDescCustShrtName, lovDescDesignationName,");
			selectSql.append(
					" lovDescNationalityName,lovDescCustDocCategoryName,IDReferenceMand, lovShareHolderCustShrtName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From CustomerDirectorDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DirectorId =:DirectorId AND CustID =:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		RowMapper<DirectorDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(DirectorDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record Director Detail List by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return List<DirectorDetail>
	 */
	@Override
	public List<DirectorDetail> getCustomerDirectorByCustomer(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DirectorId, CustID, ShareHolderCustID, FirstName, MiddleName, LastName, ShortName");
		sql.append(", CustGenderCode, CustSalutationCode, SharePerc, Shareholder, ShareholderCustomer");
		sql.append(", Director, Designation, CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustAddrLine1");
		sql.append(", CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince, CustAddrCountry");
		sql.append(", CustAddrZIP, CustAddrPhone, CustAddrFrom, IdType, IdReference, Nationality, Dob");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescShareHolderCustCIF,LovDescCustGenderCodeName, LovDescCustSalutationCodeName");
			sql.append(", LovDescCustAddrCityName, LovDescCustAddrProvinceName, LovDescCustAddrCountryName");
			sql.append(", LovDescDesignationName, LovDescNationalityName, LovDescCustDocCategoryName");
			sql.append(", IdReferenceMand, lovShareHolderCustShrtName");
		}

		sql.append(" from CustomerDirectorDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, id);
		}, (rs, rowNum) -> {
			DirectorDetail cd = new DirectorDetail();
			cd.setDirectorId(rs.getLong("DirectorId"));
			cd.setCustID(rs.getLong("CustID"));
			cd.setShareHoldercustID(rs.getLong("ShareHolderCustID"));
			cd.setFirstName(rs.getString("FirstName"));
			cd.setMiddleName(rs.getString("MiddleName"));
			cd.setLastName(rs.getString("LastName"));
			cd.setShortName(rs.getString("ShortName"));
			cd.setCustGenderCode(rs.getString("CustGenderCode"));
			cd.setCustSalutationCode(rs.getString("CustSalutationCode"));
			cd.setSharePerc(rs.getBigDecimal("SharePerc"));
			cd.setShareholder(rs.getBoolean("Shareholder"));
			cd.setShareholderCustomer(rs.getBoolean("ShareholderCustomer"));
			cd.setDirector(rs.getBoolean("Director"));
			cd.setDesignation(rs.getString("Designation"));
			cd.setCustAddrHNbr(rs.getString("CustAddrHNbr"));
			cd.setCustFlatNbr(rs.getString("CustFlatNbr"));
			cd.setCustAddrStreet(rs.getString("CustAddrStreet"));
			cd.setCustAddrLine1(rs.getString("CustAddrLine1"));
			cd.setCustAddrLine2(rs.getString("CustAddrLine2"));
			cd.setCustPOBox(rs.getString("CustPOBox"));
			cd.setCustAddrCity(rs.getString("CustAddrCity"));
			cd.setCustAddrProvince(rs.getString("CustAddrProvince"));
			cd.setCustAddrCountry(rs.getString("CustAddrCountry"));
			cd.setCustAddrZIP(rs.getString("CustAddrZIP"));
			cd.setCustAddrPhone(rs.getString("CustAddrPhone"));
			cd.setCustAddrFrom(rs.getTimestamp("CustAddrFrom"));
			cd.setIdType(rs.getString("IdType"));
			cd.setIdReference(rs.getString("IdReference"));
			cd.setNationality(rs.getString("Nationality"));
			cd.setDob(rs.getTimestamp("Dob"));
			cd.setVersion(rs.getInt("Version"));
			cd.setLastMntBy(rs.getLong("LastMntBy"));
			cd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cd.setRecordStatus(rs.getString("RecordStatus"));
			cd.setRoleCode(rs.getString("RoleCode"));
			cd.setNextRoleCode(rs.getString("NextRoleCode"));
			cd.setTaskId(rs.getString("TaskId"));
			cd.setNextTaskId(rs.getString("NextTaskId"));
			cd.setRecordType(rs.getString("RecordType"));
			cd.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				cd.setLovDescShareHolderCustCIF(rs.getString("LovDescShareHolderCustCIF"));
				cd.setLovDescCustGenderCodeName(rs.getString("LovDescCustGenderCodeName"));
				cd.setLovDescCustSalutationCodeName(rs.getString("LovDescCustSalutationCodeName"));
				cd.setLovDescCustSalutationCodeName(rs.getString("lovShareHolderCustShrtName"));
				cd.setLovDescCustAddrCityName(rs.getString("LovDescCustAddrCityName"));
				cd.setLovDescCustAddrProvinceName(rs.getString("LovDescCustAddrProvinceName"));
				cd.setLovDescCustAddrCountryName(rs.getString("LovDescCustAddrCountryName"));
				cd.setLovDescDesignationName(rs.getString("LovDescDesignationName"));
				cd.setLovDescNationalityName(rs.getString("LovDescNationalityName"));
				cd.setLovDescCustDocCategoryName(rs.getString("LovDescCustDocCategoryName"));
				cd.setIdReferenceMand(rs.getBoolean("IdReferenceMand"));
				cd.setLovShareHolderCustShrtName(rs.getString("lovShareHolderCustShrtName"));
			}

			return cd;
		});

	}

	/**
	 * This method Deletes the Record from the CustomerDirectorDetail or CustomerDirectorDetail_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Director Detail by key DirectorId
	 * 
	 * @param Director Detail (directorDetail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(DirectorDetail directorDetail, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From CustomerDirectorDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DirectorId =:DirectorId AND CustID = :CustID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method Deletes the Records from the CustomerDirectorDetail or CustomerDirectorDetail_Temp. depend on
	 * CustomerID
	 * 
	 * @param int(customerId)
	 * @param type            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(final long customerId, String type) {
		logger.debug(Literal.ENTERING);

		DirectorDetail directorDetail = new DirectorDetail();
		directorDetail.setCustID(customerId);

		StringBuilder deleteSql = new StringBuilder("Delete From CustomerDirectorDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		logger.debug(Literal.LEAVING);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

	}

	/**
	 * This method insert new Records into CustomerDirectorDetail or CustomerDirectorDetail_Temp. it fetches the
	 * available Sequence form SeqCustomerDirectorDetail by using getNextValue() method.
	 *
	 * save Director Detail
	 * 
	 * @param Director Detail (directorDetail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(DirectorDetail directorDetail, String type) {
		logger.debug(Literal.ENTERING);
		if (directorDetail.getId() == Long.MIN_VALUE) {
			directorDetail.setId(getNextValue("SeqCustomerDirectorDetail"));
			logger.debug("get NextID:" + directorDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into CustomerDirectorDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (DirectorId, CustID, ShareHolderCustID, FirstName, MiddleName, LastName, ShortName,");
		insertSql.append(" CustGenderCode, CustSalutationCode,SharePerc, CustAddrHNbr, CustFlatNbr, CustAddrStreet,");
		insertSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince,");
		insertSql.append(" CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom ,");
		insertSql.append(
				" Shareholder, ShareholderCustomer, Director, Designation, IdType, IdReference, Nationality, Dob,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:DirectorId, :CustID, :ShareHolderCustID, :FirstName, :MiddleName, :LastName,");
		insertSql.append(" :ShortName, :CustGenderCode, :CustSalutationCode, :SharePerc, :CustAddrHNbr,");
		insertSql.append(" :CustFlatNbr, :CustAddrStreet, :CustAddrLine1, :CustAddrLine2, :CustPOBox,");
		insertSql.append(" :CustAddrCity, :CustAddrProvince, :CustAddrCountry, :CustAddrZIP,");
		insertSql
				.append(" :CustAddrPhone, :CustAddrFrom, :Shareholder, :ShareholderCustomer, :Director, :Designation,");
		insertSql.append(" :IdType, :IdReference, :Nationality, :Dob,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return directorDetail.getId();
	}

	/**
	 * This method updates the Record CustomerDirectorDetail or CustomerDirectorDetail_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update Director Detail by key DirectorId and Version
	 * 
	 * @param Director Detail (directorDetail)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(DirectorDetail directorDetail, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update CustomerDirectorDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set ShareHolderCustID =:ShareHolderCustID, FirstName = :FirstName,");
		sql.append(" MiddleName = :MiddleName, LastName = :LastName, ShortName = :ShortName,");
		sql.append(" CustGenderCode = :CustGenderCode, CustSalutationCode = :CustSalutationCode,");
		sql.append(" SharePerc = :SharePerc, CustAddrHNbr = :CustAddrHNbr, CustFlatNbr = :CustFlatNbr,");
		sql.append(" CustAddrStreet = :CustAddrStreet, CustAddrLine1 = :CustAddrLine1,");
		sql.append(" CustAddrLine2 = :CustAddrLine2, CustPOBox = :CustPOBox,");
		sql.append(" CustAddrCity = :CustAddrCity, CustAddrProvince = :CustAddrProvince,");
		sql.append(" CustAddrCountry = :CustAddrCountry, CustAddrZIP = :CustAddrZIP,");
		sql.append(" CustAddrPhone = :CustAddrPhone, CustAddrFrom = :CustAddrFrom,");
		sql.append(
				" Shareholder =  :Shareholder, ShareholderCustomer = :ShareholderCustomer, Director = :Director, Designation = :Designation,");
		sql.append(" IdType =  :IdType, IdReference = :IdReference, Nationality = :Nationality, Dob = :Dob,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where DirectorId =:DirectorId AND CustID = :CustID");

		if (!type.endsWith("_Temp")) {
			sql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(directorDetail);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public DirectorDetail getDirectorDetailByDirectorId(long directorId, long custId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select DirectorId, CustID, ShareHolderCustID, FirstName");
		sql.append(", MiddleName, LastName, ShortName, CustGenderCode, CustSalutationCode, SharePerc, Shareholder");
		sql.append(
				", ShareholderCustomer, Director, Designation, CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustAddrLine1");
		sql.append(", CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince, CustAddrCountry, CustAddrZIP");
		sql.append(", CustAddrPhone, CustAddrFrom, IdType, IdReference, Nationality, Dob");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescShareHolderCustCIF,LovDescCustGenderCodeName, LovDescCustSalutationCodeName ");
			sql.append(", LovDescCustAddrCityName, LovDescCustAddrProvinceName");
			sql.append(", LovDescCustAddrCountryName, LovDescDesignationName");
			sql.append(
					", LovDescNationalityName, LovDescCustDocCategoryName, IDReferenceMand, lovShareHolderCustShrtName");
		}

		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CustomerDirectorDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DirectorId = :DirectorId and CustID = :CustID");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DirectorId", directorId);
		source.addValue("CustID", custId);

		RowMapper<DirectorDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(DirectorDetail.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getVersion(long custID, long directorId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustId", custID);
		source.addValue("DirectorId", directorId);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT Version FROM CustomerDirectorDetail");
		sql.append(" WHERE CustId = :CustId AND DirectorId = :DirectorId");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}
}