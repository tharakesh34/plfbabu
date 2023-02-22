package com.pennant.backend.endofday.tasklet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.settlement.model.FinSettlementHeader;
import com.pennant.pff.settlement.service.SettlementService;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class OTSTasklet implements Tasklet {

	private Logger logger = LogManager.getLogger(OTSTasklet.class);

	private PlatformTransactionManager transactionManager;
	private DataSource dataSource;
	private SettlementService settlementService;
	private static final String START_MSG = "OTS Process started at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String FAILED_MSG = "OTS Process failed on {} for the FinReference {}";
	private static final String SUCCESS_MSG = "OTS Process completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "OTS Process failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\nLocalizedMessage {}\nStackTrace {}";

	private static final String QUEUE_QUERY = "select FinId, ID, SettlementAmount, OtsDate, FinReference, NoOfGraceDays, SETTLEMENTENDAFTERGRACE, ENDDATE from FIN_SETTLEMENT_HEADER where SETTLEMENTSTATUS='I'";

	public OTSTasklet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if (!"Y".equals(SysParamUtil.getValue(SMTParameterConstants.ALW_OTS_ON_EOD))) {
			return RepeatStatus.FINISHED;
		}

		Date appDate = SysParamUtil.getAppDate();

		String strSysDate = DateUtil.format(appDate, DateFormat.LONG_DATE_TIME);
		String strAppDate = DateUtil.formatToLongDate(appDate);
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		TransactionStatus txStatus = null;

		List<Exception> exceptions = new ArrayList<>(1);

		logger.info(START_MSG, strSysDate, strAppDate);

		JdbcCursorItemReader<FinSettlementHeader> itemReader = new JdbcCursorItemReader<>();
		itemReader.setSql(QUEUE_QUERY);
		itemReader.setDataSource(dataSource);
		itemReader.setRowMapper((rs, rowNum) -> {

			FinSettlementHeader header = new FinSettlementHeader();

			long finId = rs.getLong("FinId");
			long settlementHeaderID = rs.getLong("ID");
			BigDecimal settlementAmount = rs.getBigDecimal("SettlementAmount");
			Date otsDate = rs.getDate("OtsDate");
			String finReference = rs.getString("FinReference");
			long noOfGraceDays = rs.getLong("NoOfGraceDays");
			Date settlementEndAfterGrace = rs.getDate("SettlementEndAfterGrace");
			Date endDate = rs.getDate("EndDate");

			header.setFinID(finId);
			header.setId(settlementHeaderID);
			header.setSettlementAmount(settlementAmount);
			header.setOtsDate(otsDate);
			header.setFinReference(finReference);
			header.setNoOfGraceDays(noOfGraceDays);
			header.setSettlementEndAfterGrace(settlementEndAfterGrace);
			header.setEndDate(endDate);

			return header;
		});

		itemReader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());

		FinSettlementHeader fsh = null;
		while ((fsh = itemReader.read()) != null) {

			BigDecimal balanceAmt = settlementService.getSettlementAountReceived(fsh.getFinID());
			balanceAmt = balanceAmt == null ? BigDecimal.ZERO : balanceAmt;
			boolean processSettlementCancellation = false;

			try {
				txStatus = transactionManager.getTransaction(txDef);
				if (balanceAmt.compareTo(fsh.getSettlementAmount()) >= 0) {
					settlementService.processSettlement(fsh.getId(), fsh.getOtsDate());
					continue;
				}

				if (fsh.getNoOfGraceDays() == 0 && appDate.compareTo(fsh.getEndDate()) == 0) {
					processSettlementCancellation = true;
				}

				if (fsh.getNoOfGraceDays() > 0 && appDate.compareTo(fsh.getEndDate()) == 0) {
					processSettlementCancellation = true;
				}

				if (processSettlementCancellation) {
					settlementService.processSettlementCancellation(fsh.getFinID(), fsh.getOtsDate());
				}

				transactionManager.commit(txStatus);

			} catch (Exception e) {
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				exceptions.add(e);
				strSysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}

				logger.info(FAILED_MSG, strSysDate, fsh.getFinID());
			}
		}

		itemReader.close();

		if (!exceptions.isEmpty()) {
			String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
			logger.info(EXCEPTION_MSG, sysDate, strAppDate);

			throw exceptions.get(0);
		}

		String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		logger.info(SUCCESS_MSG, sysDate, strAppDate);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Autowired
	public void setSettlementService(SettlementService settlementService) {
		this.settlementService = settlementService;
	}

}
