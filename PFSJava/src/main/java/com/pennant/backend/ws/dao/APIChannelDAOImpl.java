package com.pennant.backend.ws.dao;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.channeldetails.APIChannel;
import com.pennant.backend.model.channeldetails.APIChannelIP;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.ws.exception.APIException;

public class APIChannelDAOImpl extends BasisNextidDaoImpl<APIChannel> implements APIChannelDAO {
	private static Logger logger = Logger.getLogger(APIChannelDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new ChannelDetails
	 * 
	 * @return ChannelDetails
	 */
	@Override
	public APIChannel getChannelDetails() {
		logger.debug("Entering ");
		APIChannel aPIChannel = new APIChannel();
		logger.debug("Leaving ");
		return aPIChannel;
	}

	/**
	 * This method get the module from method getChannelDetails() and set the
	 * new record flag as true and return ChannelDetails()
	 * 
	 * @return ChannelDetails
	 */
	@Override
	public APIChannel getNewChannelDetails() {
		logger.debug("Entering ");
		APIChannel aPIChannel = getChannelDetails();
		aPIChannel.setNewRecord(true);
		logger.debug("Leaving ");
		return aPIChannel;
	}

	/**
	 * Fetch the Record ChannelDetails details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ChannelDetails
	 */
	@Override
	public APIChannel getChannelDetailsById(final long id, String type) {
		logger.debug("Entering ");

		MapSqlParameterSource source = null;
		RowMapper<APIChannel> typeRowMapper = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT Id, Code, Description, Active,LastMntBy, LastMntOn,");
		sql.append(" Version,  RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  API_CHANNEL_DETAILS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  Where  Id = :Id");

		logger.debug("selectSql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Id", id);

		typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(APIChannel.class);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return null;
		} finally {
			logger.debug("Leaving ");
			source = null;
			typeRowMapper = null;
		}
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the ChannelDetails or
	 * ChannelDetails_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete ChannelDetails by key
	 * CcyCode
	 * 
	 * @param APIChannel
	 *            (ChannelDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void deleteChannelDetails(APIChannel aPIChannel, String type) {
		logger.debug("Entering ");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From API_CHANNEL_DETAILS");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Id = :Id ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aPIChannel);

		try {
			if (this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters) <= 0) {
				ErrorDetails errorDetails = getError("41003", aPIChannel.getCode(), aPIChannel.getUserDetails()
						.getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006", aPIChannel.getCode(), aPIChannel.getUserDetails()
					.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method insert new Records into ChannelDetails or
	 * ChannelDetails_Temp.
	 * 
	 * save ChannelDetails
	 * 
	 * @param ChannelDetails
	 *            (ChannelDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return String
	 * 
	 */
	@Override
	public long save(APIChannel aPIChannel, String type) {
		logger.debug("Entering ");

		if (aPIChannel.getId() == Long.MIN_VALUE) {
			aPIChannel.setId(getNextId("SeqAPI_CHANNEL_DETAILS"));
			logger.debug("get NextID:" + aPIChannel.getId());
		}
		StringBuilder insertSql = new StringBuilder("Insert Into API_CHANNEL_DETAILS");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Id,Code,Description,Active,LastMntBy, LastMntOn,");
		insertSql.append(" Version ,  RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:Id, :Code,:Description,:Active,:LastMntBy, :LastMntOn, ");
		insertSql.append(" :Version ,  :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aPIChannel);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return aPIChannel.getId();
	}

	/**
	 * This method updates the Record ChannelDetails or ChannelDetails_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update ChannelDetails by key CcyCode and Version
	 * 
	 * @param ChannelDetails
	 *            (ChannelDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(APIChannel aPIChannel, String type) {
		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update API_CHANNEL_DETAILS");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Code = :Code, Description = :Description, Active = :Active,");
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Id = :Id ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aPIChannel);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", aPIChannel.getCode(), aPIChannel.getUserDetails()
					.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving ");
	}

	@Override
	public APIChannelIP getChannelAuthDetails() {
		logger.debug("Entering ");
		APIChannelIP aPIChannelIP = new APIChannelIP();
		logger.debug("Leaving ");
		return aPIChannelIP;
	}

	/**
	 * This method get the module from method getChannelAuthDetails() and set
	 * the new record flag as true and return ChannelAuthDetails()
	 * 
	 * @return ChannelAuthDetails
	 */
	@Override
	public APIChannelIP getNewChannelAuthDetails() {
		logger.debug("Entering ");
		APIChannelIP aPIChannelIP = getChannelAuthDetails();
		aPIChannelIP.setNewRecord(true);
		logger.debug("Leaving ");
		return aPIChannelIP;
	}

	/**
	 * Fetch the Record ChannelAuthDetails details by chaneelId,id
	 * 
	 * @param id
	 *            (long)
	 * @param ChannelId
	 *            (long)
	 * @return APIChannelIP(object)
	 */

	@Override
	public APIChannelIP getChannelIpDetail(long channelId, long id) {
		logger.debug("Entering ");

		MapSqlParameterSource source = null;
		StringBuilder selectSql = null;

		selectSql = new StringBuilder();
		selectSql.append(" SELECT Id, ChannelId, IP,  Active, Version, ");
		selectSql.append("  LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  API_CHANNEL_IP_DETAILS");
		selectSql.append("  Where  Id = :Id AND ChannelId = :ChannelId ");

		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Id", id);
		source.addValue("ChannelId", channelId);

		RowMapper<APIChannelIP> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(APIChannelIP.class);

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			return null;
		} finally {
			logger.debug("Leaving ");
			source = null;
			selectSql = null;
		}
	}

	/**
	 * This method Deletes the Record from the ChannelAuthDetails or
	 * ChannelAuthDetails_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete ChannelAuthDetails by key
	 * CcyCode
	 * 
	 * @param APIChannelIP
	 *            (ChannelAuthDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(APIChannelIP aPIChannelIP, String type) {
		logger.debug("Entering ");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From API_CHANNEL_IP_DETAILS");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  Id = :Id ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aPIChannelIP);
		try {
			if (this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters) <= 0) {
				ErrorDetails errorDetails = getError("41003", aPIChannelIP.getiP(), aPIChannelIP.getUserDetails()
						.getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006", aPIChannelIP.getiP(), aPIChannelIP.getUserDetails()
					.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method insert new Records into ChannelAuthDetails or
	 * ChannelAuthDetails_Temp.
	 * 
	 * save ChannelAuthDetails
	 * 
	 * @param APIChannelIP
	 *            (ChannelAuthDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return String
	 * 
	 */
	@Override
	public long save(APIChannelIP aPIChannelIP, String type) {
		logger.debug("Entering ");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into API_CHANNEL_IP_DETAILS");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" ( Id, ChannelId, IP,  Active,  LastMntBy, LastMntOn,");
		insertSql.append(" ,Version , RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:Id, :ChannelId, :IP, :Active, :LastMntBy, :LastMntOn, ");
		insertSql.append(" ,:Version , :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aPIChannelIP);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return aPIChannelIP.getId();
	}

	/**
	 * This method updates the Record ChannelAuthDetails or
	 * ChannelAuthDetails_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update ChannelAuthDetails by key
	 * CcyCode and Version
	 * 
	 * @param APIChannelIP
	 *            (ChannelAuthDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(APIChannelIP aPIChannelIP, String type) {
		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update API_CHANNEL_IP_DETAILS");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set IP = :IP, Active= :Active,");
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Id = :Id AND ChannelId = :ChannelId");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aPIChannelIP);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", aPIChannelIP.getiP(), aPIChannelIP.getUserDetails()
					.getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving ");
	}

	@SuppressWarnings("serial")
	@Override
	public void deleteChannelAuthDetails(long id, String type) {
		logger.debug("Entering ");
		MapSqlParameterSource source = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From API_CHANNEL_IP_DETAILS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ChannelId = :ChannelId ");

		logger.debug("deleteSql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("ChannelId", id);
		try {
			this.namedParameterJdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails = getError("41006", String.valueOf(id), "EN");
			throw new DataAccessException(errorDetails.getError()) {
			};
		} finally {
			logger.debug("Leaving");
			source = null;
		}
	}

	@Override
	public List<APIChannelIP> getChannelAuthDetailsByChannelId(long id, String type) {
		logger.debug("Entering ");

		MapSqlParameterSource source = null;

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Id, ChannelId, IP, Active, LastMntBy, LastMntOn, ");
		selectSql.append(" Version,  RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  API_CHANNEL_IP_DETAILS");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append("  Where ChannelId = :ChannelId");

		logger.debug("selectSql: " + selectSql.toString());
		source = new MapSqlParameterSource();
		source.addValue("ChannelId", id);
		RowMapper<APIChannelIP> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(APIChannelIP.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	private ErrorDetails getError(String errorId, String reportID, String userLanguage) {
		String[][] parms = new String[2][1];

		parms[1][0] = reportID;
		parms[0][0] = PennantJavaUtil.getLabel("label_ReportID") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0], parms[1]),
				userLanguage);
	}

	@Override
	public long getChannelId(String channelCode, String channelIp) throws APIException {
		logger.debug("Entering ");
		MapSqlParameterSource source = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select CD.Id from API_CHANNEL_DETAILS CD");
		sql.append(" INNER JOIN  API_CHANNEL_IP_DETAILS CID ON CID.ChannelID = CD.ID");
		sql.append(" Where CD.Code = :Code AND CID.IP = :IP");

		logger.debug("Query: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Code", channelCode);
		source.addValue("IP", channelIp);

		try {
			return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			throw new APIException("99003");
		} finally {
			logger.debug("Leaving");
			source = null;
		}
	}
}
