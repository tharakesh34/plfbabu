package com.pennant.backend.endofday.tasklet.bajaj;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.services.ControlDumpRequestService;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class ControlDump implements Tasklet {
	private Logger logger = Logger.getLogger(ControlDump.class);

	private DataSource dataSource;
	@Autowired
	private EODConfigDAO eodConfigDAO;

	@Autowired
	private ControlDumpRequestService controlDumpRequestService;

	public EODConfig getEodConfig() {
		try {
			List<EODConfig> list = eodConfigDAO.getEODConfig();
			if (!list.isEmpty()) {
				return list.get(0);
			}

		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return null;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Data Extract Preparation On : " + valueDate);

		try {
			
			boolean monthEnd = false;
			int amzPostingEvent = SysParamUtil.getValueAsInt(AccountConstants.AMZ_POSTING_EVENT);
			if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_MTH_END) {
				if (valueDate.compareTo(DateUtility.getMonthEnd(valueDate)) == 0) {
					monthEnd = true;
				}
			} else if (amzPostingEvent == AccountConstants.AMZ_POSTING_APP_EXT_MTH_END) {
				if (getEodConfig() != null && getEodConfig().isInExtMnth()) {
					if (getEodConfig().getMnthExtTo().compareTo(valueDate) == 0) {
						monthEnd = true;
					}
				}

			}
			// if month end then only it should run
			if (monthEnd) {

			}
			
			new ControlDumpProcessThread(new Long(1000), controlDumpRequestService).start();
			DataEngineStatus status = BajajInterfaceConstants.CONTROL_DUMP_REQUEST_STATUS;
			status.setStatus("I");
			
			while("I".equals(status.getStatus())) {
				BatchUtil.setExecution(context, "TOTAL", String.valueOf(status.getTotalRecords()));
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(status.getProcessedRecords()));
				
				if ("F".equals(status.getStatus())) {
					throw new Exception("Unable to process the ALM.");
				}
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}

		logger.debug("COMPLETE: Data Extract Preparation On :" + valueDate);
		return RepeatStatus.FINISHED;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	
	public class ControlDumpProcessThread extends Thread {
		private long userId;
		private ControlDumpRequestService controlDumpRequestService;

		public ControlDumpProcessThread(long userId, ControlDumpRequestService controlDumpRequestService) {
			this.userId = userId;
			this.controlDumpRequestService = controlDumpRequestService;
		}

		public void run() {
			try {
				logger.debug("Control Dump Request Service started...");
				sleep(1000);
				this.controlDumpRequestService.sendReqest(userId, DateUtility.getAppValueDate(),
						DateUtility.getAppDate(), DateUtility.getMonthStartDate(DateUtility.getAppValueDate()), DateUtility.getMonthEnd(DateUtility.getAppValueDate()));

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
}
