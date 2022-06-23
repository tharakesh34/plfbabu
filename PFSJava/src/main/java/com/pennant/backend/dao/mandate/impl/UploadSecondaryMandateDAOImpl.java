package com.pennant.backend.dao.mandate.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.mandate.UploadSecondaryMandateDAO;
import com.pennant.backend.model.mandate.UploadSecondaryMandate;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class UploadSecondaryMandateDAOImpl extends BasicDao<UploadSecondaryMandate>
		implements UploadSecondaryMandateDAO {
	private static Logger logger = LogManager.getLogger(UploadSecondaryMandateDAOImpl.class);

	/**
	 * 
	 * save SecondaryMandateStatus
	 * 
	 * @param SecondaryMandateStatus (mandateStatus)
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
		insertSql.append(
				" Values(:UploadId, :MandateID, :MandateType, :AccNumber, :AccHolderName, :BarCodeNumber, :AccType, ");
		insertSql.append(":MICR, :BankCode, :Status, :Reason)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatus);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public boolean fileIsExists(String name) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FileName", name);
		StringBuilder sql = new StringBuilder("SELECT Count(*) From UploadSecondaryMandate");
		sql.append(" Where FileName = :FileName");
		logger.debug("selectSql: " + sql.toString());

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
	}

	@Override
	public List<UploadSecondaryMandate> getReportData(long uploadId, long userId, String module) {
		UploadSecondaryMandate secondaryMandateStatus = new UploadSecondaryMandate();
		secondaryMandateStatus.setUploadId(uploadId);
		secondaryMandateStatus.setLastMntBy(userId);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("select FILENAME,MANDATEID,MANDATETYPE,MICR,BARCODENUMBER,ACCNUMBER,ACCTYPE,BANKCODE,");
		selectSql.append("ACCHOLDERNAME,STATUS,REASON,trasactiondate,lastmntby userid from UploadHeader T Inner Join");
		selectSql.append(" UploadSecondaryMandate T1 on T.uploadid=T1.uploadid");
		selectSql.append(" Where uploadId =:uploadId AND LastMntBy =:LastMntBy AND Module =:Module");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secondaryMandateStatus);
		RowMapper<UploadSecondaryMandate> typeRowMapper = BeanPropertyRowMapper
				.newInstance(UploadSecondaryMandate.class);

		return jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
}
