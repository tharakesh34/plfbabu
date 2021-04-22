/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  OfflineUserMessagesBackupDAOImpl.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-03-2012    														*
 *                                                                  						*
 * Modified Date    :  20-03-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 **/
package com.pennant.backend.dao.messages.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.pennant.backend.dao.messages.OfflineUserMessagesBackupDAO;
import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * @author s057
 *
 */
public class OfflineUserMessagesBackupDAOImpl extends BasicDao<OfflineUsersMessagesBackup>
		implements OfflineUserMessagesBackupDAO {
	private static Logger logger = LogManager.getLogger(OfflineUserMessagesBackupDAOImpl.class);

	public OfflineUserMessagesBackupDAOImpl() {
		super();
	}

	/**
	 * This Method selects the records from UserRoles_AView table with UsrID condition
	 * 
	 * @param secuser(SecUser)
	 * @return List<OfflineUsersMessagesBackup>
	 */
	@Override
	public List<OfflineUsersMessagesBackup> getMessagesBackupByUserId(String toUsrId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ToUsrID, FromUsrID, SendTime, Message");
		sql.append(" FROM OFFLINEUSRMESSAGESBACKUP");
		sql.append(" Where ToUsrID = ?");

		logger.trace(Literal.SQL + sql.toString());

		List<OfflineUsersMessagesBackup> msgList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, toUsrId);
		}, (rs, rowNum) -> {
			OfflineUsersMessagesBackup msgBkp = new OfflineUsersMessagesBackup();

			msgBkp.setFromUsrID(rs.getString("FromUsrID"));
			msgBkp.setSendTime(rs.getDate("SendTime"));
			msgBkp.setMessage(rs.getString("Message"));
			msgBkp.setToUsrID(rs.getString("ToUsrID"));

			return msgBkp;
		});

		return sortBySendTime(msgList);
	}

	private List<OfflineUsersMessagesBackup> sortBySendTime(List<OfflineUsersMessagesBackup> msgList) {
		return msgList.stream().sorted((msg1, msg2) -> DateUtil.compare(msg1.getSendTime(), msg2.getSendTime()))
				.collect(Collectors.toList());
	}

	@Override
	public void save(List<OfflineUsersMessagesBackup> msgBkp) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" OFFLINEUSRMESSAGESBACKUP");
		sql.append(" (TOUSRID, FROMUSRID, SENDTIME, MESSAGE");
		sql.append(") values(");
		sql.append("?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL, sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				OfflineUsersMessagesBackup offUsrMsgBkp = msgBkp.get(i);

				int index = 1;
				ps.setString(index++, offUsrMsgBkp.getToUsrID());
				ps.setString(index++, offUsrMsgBkp.getFromUsrID());
				ps.setDate(index++, JdbcUtil.getDate(offUsrMsgBkp.getSendTime()));
				ps.setString(index++, offUsrMsgBkp.getMessage());
			}

			@Override
			public int getBatchSize() {
				return msgBkp.size();
			}
		});
	}

	@Override
	public void delete(String toUsrId) {
		logger.debug("Entering");
		Map<String, String> namedParamters = Collections.singletonMap("TOUSRID", toUsrId);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From OFFLINEUSRMESSAGESBACKUP");
		deleteSql.append(" Where TOUSRID =:TOUSRID ");
		logger.debug("deleteSql: " + deleteSql.toString());

		this.jdbcTemplate.update(deleteSql.toString(), namedParamters);

		logger.debug("Leaving");
	}

	@Override
	public void deleteByFromUsrId(String fromUsrId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From");
		sql.append(" OFFLINEUSRMESSAGESBACKUP");
		sql.append(" Where FromUsrId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, fromUsrId);
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

}
