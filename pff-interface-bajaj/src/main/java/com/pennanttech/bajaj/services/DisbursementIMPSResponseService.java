package com.pennanttech.bajaj.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.bajaj.process.DisbursemenIMPSResponseProcess;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.DisbursementResponseService;

public class DisbursementIMPSResponseService extends BajajService {
	private static final Logger logger = Logger.getLogger(DisbursementIMPSResponseService.class);

	@Autowired
	private DisbursementResponseService disbursementResponse;

	public DisbursementIMPSResponseService() {
		super();
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		
		DisbursemenIMPSResponseProcess disbRespProcess  = null;
		
		List<Long> disbursements = getDisbursements();
		if (disbursements == null || disbursements.isEmpty()) {
			return;
		}
		disbRespProcess = new DisbursemenIMPSResponseProcess(dataSource, new Long(1000), getValueDate(), disbursements);
		disbRespProcess.process("DISB_IMPS_RESPONSE");
		DataEngineStatus status = disbRespProcess.getDataEngineStatus();

		try {
			disbursementResponse.receiveResponse(status.getId());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private List<Long> getDisbursements() {
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		sql.append(" SELECT CHANNELPARTNERREFNO FROM INT_DSBIMPS_RESPONSE ");
		sql.append(" WHERE  PROCESSFLAG = :PROCESSFLAG ");

		paramMap.addValue("PROCESSFLAG", "N");

		try {
			return namedJdbcTemplate.queryForList(sql.toString(), paramMap, Long.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			paramMap = null;
			sql = null;
		}
		return null;
	}

}