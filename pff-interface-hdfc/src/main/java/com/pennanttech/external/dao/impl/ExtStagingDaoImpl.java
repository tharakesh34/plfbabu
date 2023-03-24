package com.pennanttech.external.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.external.dao.ExtStagingDao;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtStagingDaoImpl implements ExtStagingDao {
	private static final Logger logger = LogManager.getLogger(ExtStagingDaoImpl.class);

	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	@Override
	public List<ExtPresentmentFile> getStagingPresentment(String flag) {
		logger.debug(Literal.ENTERING);
		String queryStr;

		List<ExtPresentmentFile> presentmentFilesList = new ArrayList<ExtPresentmentFile>();

		StringBuilder query = new StringBuilder();
		query.append(" SELECT FINNONE_BATCHID,AGREEMENTID,CHEQUESNO,");
		query.append(" CHEQUEDATE,CHEQUESTATUS,BOUNCE_REASON ");
		query.append(" FROM PDC_BATCH_D_STG ");
		query.append(" WHERE  PICK_FINNONE = ?");
		queryStr = query.toString();

		extNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
			ps.setString(1, flag);// FIXME
		}, rs -> {
			ExtPresentmentFile details = new ExtPresentmentFile();
			details.setClusterId(StringUtils.trimToEmpty(rs.getString("FINNONE_BATCHID")));
			details.setFinReference(rs.getString("AGREEMENTID"));
			details.setChequeSerialNo(rs.getString("CHEQUESNO"));
			details.setChequeDate(rs.getDate("CHEQUEDATE"));
			details.setStatus(rs.getString("CHEQUESTATUS"));
			details.setBounceReason(rs.getString("BOUNCE_REASON"));
			presentmentFilesList.add(details);
		});

		logger.debug(Literal.LEAVING);
		return presentmentFilesList;
	}

	@Override
	public void updatePickupStatus(String pickFlag, long agreementId, String chequeSno) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder("UPDATE PDC_BATCH_D_STG");
		sql.append(" SET PICK_FINNONE = ?, FINNONE_PICK_DATE = ? WHERE AGREEMENTID= ? AND CHEQUESNO=?");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, pickFlag);
			ps.setTimestamp(index++, curTimeStamp);
			ps.setLong(index++, agreementId);
			ps.setString(index, chequeSno);
		});
	}

	@Override
	public void updateErrorDetails(long agreementId, String chequeSno, String errorFlag, String errorDesc) {
		StringBuilder sql = new StringBuilder("UPDATE PDC_BATCH_D_STG");
		sql.append(" SET EXCEPTION_FLAG = ?, EXCEPTION_DESC = ? WHERE AGREEMENTID= ? AND CHEQUESNO=?");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, errorFlag);
			ps.setString(index++, errorDesc);
			ps.setLong(index++, agreementId);
			ps.setString(index, chequeSno);
		});
	}

	public void setStagingDataSource(DataSource stagingDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(stagingDataSource);
	}
}
