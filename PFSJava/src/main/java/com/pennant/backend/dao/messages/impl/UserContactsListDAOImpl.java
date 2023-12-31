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
 * * FileName : DataImportDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-03-2012 * * Modified
 * Date : 20-03-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 **/
package com.pennant.backend.dao.messages.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.messages.UserContactsListDAO;
import com.pennant.backend.model.messages.UserContactsList;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * @author s057
 *
 */
public class UserContactsListDAOImpl extends BasicDao<UserContactsList> implements UserContactsListDAO {
	Logger logger = LogManager.getLogger(UserContactsListDAOImpl.class);

	public UserContactsListDAOImpl() {
		super();
	}

	/**
	 * This Method selects the records from UserRoles_AView table with UsrID condition
	 * 
	 * @param secuser(SecUser)
	 * @return UserContactsList
	 */
	@Override
	public UserContactsList getUserContactsList(String usrID, String type) {
		logger.debug("Entering ");
		UserContactsList userContactsList = new UserContactsList();
		userContactsList.setUsrID(usrID);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT  UsrID,Type,ContactsList,GroupName");
		selectSql.append("  FROM USERCONTACTSLIST  where UsrID=:UsrID order by UsrID Asc ");
		logger.debug("selectSql : " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userContactsList);
		RowMapper<UserContactsList> typeRowMapper = BeanPropertyRowMapper.newInstance(UserContactsList.class);
		logger.debug("Leaving ");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void save(UserContactsList userContactsList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into USERCONTACTSLIST");
		insertSql.append(" (UsrID, Type, ContactsList, GroupName)");
		insertSql.append(" Values(:UsrID, :Type, :ContactsList, :GroupName)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userContactsList);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	@Override
	public void update(UserContactsList userContactsList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update SecRoles");
		updateSql.append(" Set UsrID = :UsrID, Type = :Type, ContactsList = :ContactsList, GroupName = :GroupName");

		logger.debug("updateSql:" + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userContactsList);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	@Override
	public void delete(String usrID, String type) {
		logger.debug("Entering");
		UserContactsList userContactsList = new UserContactsList();
		userContactsList.setUsrID(usrID);
		userContactsList.setType(type);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From USERCONTACTSLIST");
		deleteSql.append(" Where UsrID =:UsrID and Type = :Type");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(userContactsList);

		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
}
