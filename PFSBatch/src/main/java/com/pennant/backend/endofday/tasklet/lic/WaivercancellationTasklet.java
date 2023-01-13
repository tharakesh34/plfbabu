package com.pennant.backend.endofday.tasklet.lic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.service.finance.FeeWaiverCancelService;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.npa.eod.tasklet.EffAssetClassificationTaskLet;

public class WaivercancellationTasklet implements Tasklet {

	private Logger logger = LogManager.getLogger(EffAssetClassificationTaskLet.class);

	private DataSource dataSource;
	private FeeWaiverCancelService feeWaiverCancelService;
	private static final String SUCCESS_MSG = "Effective Asset Classification completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "Effective Asset Classification failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_LOG = "Cause {}\nMessage {}\nLocalizedMessage {}\nStackTrace {}";

	private static final String QUEUE_QUERY = "select FINREFERENCE from FEEWAIVERHEADER where WAIVERFULLFILLDATE = ?";

	public WaivercancellationTasklet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		// if (SysParamUtil.getValue(SMTParameterConstants.ALW_WAIVERCANCEL_ON_EOD).equals("Y")) {
		Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

		Date appDate = SysParamUtil.getAppDate();

		JdbcCursorItemReader<String> itemReader = new JdbcCursorItemReader<>();
		itemReader.setSql(QUEUE_QUERY);
		itemReader.setDataSource(dataSource);

		itemReader.setRowMapper((rs, rowNum) -> {
			return rs.getString("FinReference");
		});

		itemReader.setPreparedStatementSetter(ps -> {
			ps.setInt(1, EodConstants.PROGRESS_WAIT);
		});

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		List<Exception> exceptions = new ArrayList<>(1);

		try {
			feeWaiverCancelService.processConditionalWaiver(appDate);
		} catch (Exception e) {
			logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

			exceptions.add(e);
		}

		itemReader.close();

		if (!exceptions.isEmpty()) {
			String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
			logger.info(EXCEPTION_MSG, sysDate);

			throw exceptions.get(0);
		}

		String sysDate = DateUtil.getSysDate(DateFormat.FULL_DATE_TIME);
		logger.info(SUCCESS_MSG, sysDate);
		// }
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setFeeWaiverCancelService(FeeWaiverCancelService feeWaiverCancelService) {
		this.feeWaiverCancelService = feeWaiverCancelService;
	}

}
