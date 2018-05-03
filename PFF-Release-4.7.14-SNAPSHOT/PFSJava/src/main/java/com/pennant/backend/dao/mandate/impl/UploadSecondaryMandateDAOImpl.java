package com.pennant.backend.dao.mandate.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.mandate.UploadSecondaryMandateDAO;
import com.pennant.backend.model.mandate.UploadSecondaryMandate;
import com.pennanttech.pennapps.core.resource.Literal;

public class UploadSecondaryMandateDAOImpl implements UploadSecondaryMandateDAO {
	
	private static Logger				logger	= Logger.getLogger(UploadSecondaryMandateDAOImpl.class);
	// Spring Named JDBC Template
		private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;
		
		/**
		 * To Set dataSource
		 * 
		 * @param dataSource
		 */

		public void setDataSource(DataSource dataSource) {
			this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		}

		/**
		 * 
		 * save SecondaryMandateStatus
		 * 
		 * @param SecondaryMandateStatus
		 *            (mandateStatus)
		 * @return void
		 * @throws DataAccessException
		 * 
		 */

		@Override
		public void save(UploadSecondaryMandate mandateStatus) {
			logger.debug("Entering");
			StringBuilder insertSql = new StringBuilder("Insert Into UploadSecondaryMandate");
			insertSql.append(" (UploadId,MandateID,MandateType,AccNumber, AccHolderName,BarCodeNumber,AccType,");
			insertSql.append(" MICR,BankCode, Status,Reason)");
			insertSql.append(" Values(:UploadId, :MandateID, :MandateType, :AccNumber, :AccHolderName, :BarCodeNumber, :AccType, ");
			insertSql.append(":MICR, :BankCode, :Status, :Reason)");

			logger.debug("insertSql: " + insertSql.toString());

			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatus);
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
			logger.debug("Leaving");
		}

		@Override
	public boolean fileIsExists(String name) {
		logger.debug("Entering");

		int count = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FileName", name);
		StringBuilder sql = new StringBuilder("SELECT Count(*) From UploadSecondaryMandate");
		sql.append(" Where FileName = :FileName");
		logger.debug("selectSql: " + sql.toString());

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			count = 0;
		}
		boolean exists = false;
		if (count > 0) {
			exists = true;
		}
		return exists;
	}

		@Override
	public List<UploadSecondaryMandate> getReportData(long uploadId, long userId, String module) {

		UploadSecondaryMandate secondaryMandateStatus = new UploadSecondaryMandate();
		secondaryMandateStatus.setUploadId(uploadId);
		secondaryMandateStatus.setLastMntBy(userId);

		List<UploadSecondaryMandate> list;

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("select FILENAME,MANDATEID,MANDATETYPE,MICR,BARCODENUMBER,ACCNUMBER,ACCTYPE,BANKCODE,");
		selectSql.append("ACCHOLDERNAME,STATUS,REASON,trasactiondate,lastmntby userid from UploadHeader T Inner Join");
		selectSql.append(" UploadSecondaryMandate T1 on T.uploadid=T1.uploadid");
		selectSql.append(" Where uploadId =:uploadId AND LastMntBy =:LastMntBy AND Module =:Module");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secondaryMandateStatus);
		RowMapper<UploadSecondaryMandate> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(UploadSecondaryMandate.class);

		try {
			list = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			list = new ArrayList<>();
		}

		logger.debug(Literal.LEAVING);

		return list;

	}

}
