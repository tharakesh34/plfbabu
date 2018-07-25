package com.pennant.backend.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.TATDetailDAO;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.finance.TATNotificationCode;
import com.pennant.backend.model.finance.TATNotificationLog;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class TATDetailDAOImpl extends SequenceDao<TATDetail> implements TATDetailDAO {
    private static Logger logger = Logger.getLogger(TATDetailDAOImpl.class);


	public TATDetailDAOImpl() {
		super();
	}

	

	/**
	 * Get TAT Detail
	 */
	@Override
	public TATDetail getTATDetail(String reference, String rolecode) {
		logger.debug("Entering");
		TATDetail detail = new TATDetail();
		try {
			detail.setReference(reference);
			detail.setRoleCode(rolecode);

			StringBuilder selectSql = new StringBuilder(
					"Select T1.Reference,  T1.RoleCode ,T1.SerialNo,T1.TATStartTime from TATDetails T1 Inner join ");
			selectSql.append("(SELECT Reference, MAX(SerialNo) SerialNo, RoleCode ");
			selectSql.append("From TATDetails Where Reference= :Reference AND RoleCode=:RoleCode ");
			selectSql.append("group by Reference,RoleCode)T2 ON T1.Reference =T2.Reference ");
			selectSql.append("and T1.RoleCode =T2.RoleCode and T1.SerialNo =T2.SerialNo");

			RowMapper<TATDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(TATDetail.class);
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
			logger.debug("selectSql: " + selectSql.toString());

			detail = this.jdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return null;
		}
		return detail;
	}

	/**
	 * Get TAT Detail
	 */
	@Override
	public List<TATDetail> getAllTATDetail() {
		logger.debug("Entering");
		TATDetail tatDetail = new TATDetail();

		StringBuilder selectSql = new StringBuilder(
				"SELECT module, reference , serialNo, TATStartTime, TATEndTime,TriggerTime, RoleCode, FinType From TATDetails");
		selectSql.append(" Where TATEndTime IS NULL ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(tatDetail);
		RowMapper<TATDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TATDetail.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Starting the turnaround time
	 * 
	 */
	@Override
	public void save(TATDetail tatDetail) {
		logger.debug("Entering");
		tatDetail.setSerialNo(getNextId("SeqTATDetails"));

		StringBuilder insertSql = new StringBuilder(" INSERT INTO TATDetails ");
		insertSql.append(" (Module, Reference, SerialNo, RoleCode, TATStartTime, TATEndTime, FinType)");
		insertSql.append(" Values( :Module, :Reference, :SerialNo, :RoleCode, :TATStartTime, :TATEndTime, :FinType)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(tatDetail);
		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			logger.debug("Exception: ", e);
		}
		logger.debug("Leaving");

	}

	/**
	 * Updating turnaround time with end time
	 * 
	 */
	public void update(TATDetail aTatDetail) {
		logger.debug("Entering");
		try {
			TATDetail tatDetail = getTATDetail(aTatDetail.getReference(), aTatDetail.getRoleCode());
			tatDetail.settATEndTime(aTatDetail.gettATEndTime());
			if (tatDetail.gettATStartTime() != null) {
				tatDetail.settATStartTime(tatDetail.gettATStartTime());
			} else {
				tatDetail.settATStartTime(aTatDetail.gettATStartTime());
			}
			tatDetail.setTriggerTime(aTatDetail.getTriggerTime());
			StringBuilder updateSql = new StringBuilder(" UPDATE TATDetails");
			updateSql.append(" SET TatStartTime =:TATStartTime, TATEndTime=:TATEndTime, TriggerTime =:TriggerTime");
			updateSql.append(" WHERE Reference=:Reference AND SerialNo=:SerialNo");

			logger.debug("updateSql: " + updateSql.toString());

			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(tatDetail);

			this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.debug("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public TATNotificationLog getLogDetails(TATDetail tatDetail) {
		logger.debug("Entering");

		TATNotificationLog notificationLog = new TATNotificationLog();
		notificationLog.setModule(tatDetail.getModule());
		notificationLog.setReference(tatDetail.getReference());
		notificationLog.setRoleCode(tatDetail.getRoleCode());

		StringBuilder selectSql = new StringBuilder("SELECT module, reference, count, RoleCode From TATNotificationLog");
		selectSql.append(" Where module = :Module and Reference=:Reference AND RoleCode=:RoleCode");

		RowMapper<TATNotificationLog> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(TATNotificationLog.class);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notificationLog);
		logger.debug("selectSql: " + selectSql.toString());
		try {
			notificationLog = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return null;
		}

		return notificationLog;

	}

	@Override
	public TATNotificationCode getNotificationdetail(String code) {
		logger.debug("Entering");

		TATNotificationCode notificationCode = new TATNotificationCode();
		notificationCode.setTatNotificationCode(code);

		StringBuilder selectSql = new StringBuilder(
				"SELECT TATNotificationId, TATNotificationDesc, Time From TATNotificationCodes");
		selectSql.append(" Where TATNotificationCode =:TatNotificationCode ");

		RowMapper<TATNotificationCode> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(TATNotificationCode.class);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notificationCode);
		logger.debug("selectSql: " + selectSql.toString());
		try {
			notificationCode = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return null;
		}

		return notificationCode;
	}

	@Override
	public void saveLogDetail(TATNotificationLog notificationLog) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder(" INSERT INTO TATNotificationLog ");
		insertSql.append(" (Module, Reference, RoleCode, Count)");
		insertSql.append(" Values( :Module, :Reference, :RoleCode, :Count)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notificationLog);
		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			logger.debug("Exception: ", e);
		}
		logger.debug("Leaving");

	}

	@Override
	public void updateLogDetail(TATNotificationLog notificationLog) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder(" UPDATE TATNotificationLog");
		updateSql.append(" SET Count =:Count ");
		updateSql.append(" WHERE Reference=:Reference AND RoleCode =:RoleCode");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notificationLog);
		try {
			this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			logger.debug("Exception: ", e);
		}
		logger.debug("Leaving");

	}

}
