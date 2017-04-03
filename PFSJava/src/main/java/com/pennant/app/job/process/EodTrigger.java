package com.pennant.app.job.process;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.Eod;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.model.EodDetail;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class EodTrigger extends QuartzJobBean implements StatefulJob, Runnable, Serializable {

	private static final long				serialVersionUID	= -3196188690942217990L;
	private static final Logger				logger				= Logger.getLogger(EodTrigger.class);

	private Eod								eod;

	public volatile Map<Integer, Component>	map					= new HashMap<Integer, Component>();

	public EodTrigger() {
		super();

	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Entering");

		startEODProcess();

		logger.debug("Leaving");
	}

	@Override
	public void run() {
		startEODProcess();

	}

	public void setEod(Eod eod) {
		this.eod = eod;
	}

	private void startEODProcess() {
		// Fetch the active customer details and process the EOD activities 
		try {
			//DateUtility.getDate(DateUtility.getSysDate(PennantConstants.DBDateFormat),PennantConstants.DBDateFormat);
			Date date = DateUtility.getAppDate();
			EodDetail eodDetail = eod.getEodDetailById(date);
			boolean startEod = false;

			if (eodDetail == null) {
				eodDetail = new EodDetail();
				eodDetail.setProcessDate(date);
				eodDetail.setStatTime(DateUtility.getSysDate());
				eodDetail.setStatus(EodConstants.STATUS_STARTED);
				eod.save(eodDetail);
				startEod = true;
				((Textbox) map.get(5)).setValue(DateUtility.format(eodDetail.getStatTime(), DateFormat.LONG_TIME));
				((Textbox) map.get(7)).setValue(EodConstants.STATUS_STARTED);
			} else {
				startEod = false;
				//Restart
				if (StringUtils.trimToEmpty(eodDetail.getStatus()).equals(EodConstants.STATUS_FAILED)) {
					startEod = true;
				}

			}

			if (startEod) {
				logger.debug("Eod for the day has been started for the date: " + eodDetail.getStatTime());
				eod.getThreadPoolTaskExecutor().initialize();
				eod.doProcess();
			}

			if (eodDetail.getEndTime() != null) {
				logger.debug("Eod for the day has been completed for the date: " + eodDetail.getEndTime());
				return;
			}

			while (true) {
				//logger.debug("Waiting..............");

				((Textbox) map.get(7)).setValue(EodConstants.STATUS_RUNNING);

				if (eod.getThreadPoolTaskExecutor().getActiveCount() == 0) {

					long count = eod.getCountbyProgress(DateUtility.getAppDate(), EodConstants.PROGRESS_START);
					if (count == 0) {
						long stauscount = eod.getCountByStatus(DateUtility.getAppDate(), EodConstants.STATUS_FAILED);
						if (stauscount > 0) {
							((Textbox) map.get(7)).setValue(EodConstants.STATUS_FAILED);
							((Button) map.get(4)).setDisabled(false);
							eodDetail.setEndTime(DateUtility.getSysDate());
							eodDetail.setStatus(EodConstants.STATUS_FAILED);
							eod.getEodDetailDAO().update(eodDetail);

						} else {

							eod.getPostEodService().doProcess();
							eodDetail.setEndTime(DateUtility.getSysDate());
							eodDetail.setStatus(EodConstants.STATUS_COMPLETED);
							eod.getEodDetailDAO().update(eodDetail);
							((Textbox) map.get(6)).setValue(DateUtility.format(eodDetail.getEndTime(),
									DateFormat.LONG_TIME));
							updateCompleteEODStatus();
						}

						eod.getThreadPoolTaskExecutor().destroy();

						logger.debug("Eod for the day has been completed for the date: " + eodDetail.getEndTime());
						break;
					}

				}

			}

		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void updateCompleteEODStatus() {
		((Textbox) map.get(1)).setValue(DateUtility.getValueDate(DateFormat.LONG_DATE));
		((Textbox) map.get(2)).setValue(DateUtility.formatToLongDate(SysParamUtil
				.getValueAsDate(PennantConstants.APP_DATE_NEXT)));
		((Textbox) map.get(3)).setValue(DateUtility.formatToLongDate(SysParamUtil
				.getValueAsDate(PennantConstants.APP_DATE_LAST)));
		((Button) map.get(4)).setDisabled(false);
		((Textbox) map.get(7)).setValue(EodConstants.STATUS_COMPLETED);
	}

}
