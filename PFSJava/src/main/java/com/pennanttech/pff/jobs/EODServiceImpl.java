package com.pennanttech.pff.jobs;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.zkoss.zul.Timer;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.eod.EODConfigDAO;
import com.pennant.backend.endofday.main.PFSBatchAdmin;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.batch.backend.service.BatchProcessStatusService;
import com.pennanttech.pff.batch.model.BatchProcessStatus;
import com.pennanttech.pff.eod.EODService;

public class EODServiceImpl implements EODService {
	private static final Logger logger = LogManager.getLogger(EODServiceImpl.class);
	private static final String DEFAULT_JOB_FREQUENCY = "0 0/3 * 1/1 * ? *";

	private EODConfigDAO eODConfigDAO;
	private BatchProcessStatusService bpsService;

	protected Timer timer;
	String[] args = new String[1];

	@Override
	public void startEOD() {
		logger.debug(Literal.ENTERING);

		BatchProcessStatus bps = new BatchProcessStatus();

		if (!eODConfigDAO.isAutoEODEnabled()) {
			logger.info("Auto EOD job not enabled.");
			return;
		}

		bps.setName("PLF_EOD");
		bps = bpsService.getBatchStatus(bps);

		if (bps != null) {
			int days = DateUtil.getDaysBetween(DateUtil.getSysDate(), bps.getEndTime());

			if (days == 0) {
				logger.debug("EOD is already processed.");
				return;
			}
		}

		if (bps == null) {
			bps = new BatchProcessStatus();
			bps.setName("PLF_EOD");
			bps.setStartTime(DateUtil.getSysDate());
			bps.setStatus("I");
			bps.setValueDate(SysParamUtil.getAppValueDate());
			bpsService.saveBatchStatus(bps);
		}

		String[] args = new String[1];
		args[0] = DateUtil.formatToShortDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT));

		PFSBatchAdmin.getInstance();

		PFSBatchAdmin.setArgs(args);

		PFSBatchAdmin.setRunType("START");

		try {
			Thread thread = new Thread(new EODJob());
			thread.start();
			Thread.sleep(1000);
		} catch (Exception e) {
			timer.stop();
			MessageUtil.showError(e);
		}
	}

	@Override
	public void stopEOD() {

	}

	public class EODJob implements Runnable {

		public EODJob() {
			super();
		}

		@Override
		public void run() {
			PFSBatchAdmin.startJob();
		}

	}

	@Override
	public String getCronExpression() {
		logger.debug(Literal.ENTERING);

		String cronExpression = eODConfigDAO.getFrequency();

		if (StringUtils.isEmpty(cronExpression)) {
			cronExpression = DEFAULT_JOB_FREQUENCY;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(
					String.format("The cron expression %s for mandate process not valid.", cronExpression));
		}

		logger.debug(Literal.LEAVING);
		return cronExpression;
	}

	@Override
	public boolean isAutoRequired() {
		logger.debug(Literal.ENTERING);
		return eODConfigDAO.isAutoRequired();
	}

	public void setEODConfigDAO(EODConfigDAO eODConfigDAO) {
		this.eODConfigDAO = eODConfigDAO;
	}

	public BatchProcessStatusService getBpsService() {
		return bpsService;
	}

	public void setBpsService(BatchProcessStatusService bpsService) {
		this.bpsService = bpsService;
	}
}
