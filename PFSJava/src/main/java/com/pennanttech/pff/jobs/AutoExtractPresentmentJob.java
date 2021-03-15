package com.pennanttech.pff.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.financemanagement.impl.PresentmentJobService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoExtractPresentmentJob implements Job, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AutoExtractPresentmentJob.class);

	public static final String JOB_ENABLED = SMTParameterConstants.PRESENTMENT_AUTO_EXTRACT_JOB_ENABLED;
	public static final String JOB_KEY = "PRESENTMENT_AUTO_EXTRACT_JOB_ENABLED";
	public static final String JOB_KEY_DESCRIPTION = "Presentment Auto Extract Job Enabled";
	public static final String JOB_TRIGGER = "PRESENTMENT_EXTRACT_JOB_TRIGGER";
	private static final String DEFAULT_JOB_FREQUENCY = "0 0/5 * 1/1 * ? *";
	public static final String JOB_FREQUENCY = SMTParameterConstants.PRESENTMENT_AUTO_EXTRACT_JOB_FREQUENCY;

	private PresentmentJobService jobService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getKey().getName();
		logger.debug("JOB: {}", jobName);
		List<String> mandateTypes = getMandateTypeList();
		Date appDate = SysParamUtil.getAppDate();
		Date toDate = null;
		Date fromDate = null;
		int nachDateFreq = SysParamUtil.getValueAsInt(SMTParameterConstants.PRESENTMENT_NACH_DATE_FREQUENCY);
		int pdcDateFreq = SysParamUtil.getValueAsInt(SMTParameterConstants.PRESENTMENT_PDC_DATE_FREQUENCY);

		if (!SysParamUtil.isAllowed(JOB_ENABLED) || (nachDateFreq < 0 && pdcDateFreq < 0)) {
			logger.warn("{} Presentment Auto Download Job is Disabled", JOB_ENABLED);
			return;
		}

		List<String> entities = getExtractService(context).getEntityCodes();

		if (CollectionUtils.isEmpty(entities)) {
			return;
		}

		for (String entityCode : entities) {
			List<FinanceType> loanTypes = getExtractService(context).getFinanceTypeList(entityCode);
			for (FinanceType loanType : loanTypes) {
				for (String mandateType : mandateTypes) {
					PresentmentHeader presentmentHeader = new PresentmentHeader();
					if (mandateType.equals(MandateConstants.TYPE_NACH)) {
						toDate = DateUtil.addDays(appDate, nachDateFreq);
						fromDate = DateUtil.addDays(appDate, nachDateFreq);
					} else if (mandateType.equals(MandateConstants.TYPE_PDC)) {
						toDate = DateUtil.addDays(appDate, pdcDateFreq);
						fromDate = DateUtil.addDays(appDate, pdcDateFreq);
						presentmentHeader.setLoanType(loanType.getFinType());
					}
					presentmentHeader.setToDate(toDate);
					presentmentHeader.setFromDate(fromDate);
					presentmentHeader.setPresentmentType("P");
					presentmentHeader.setMandateType(mandateType);
					presentmentHeader.setEntityCode(entityCode);

					try {
						getExtractService(context).extractPresentment(presentmentHeader);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
					}
				}
			}
		}
	}

	private PresentmentJobService getExtractService(JobExecutionContext context) {
		if (jobService == null) {
			jobService = (PresentmentJobService) context.getJobDetail().getJobDataMap().get("presentmentJobService");
		}
		return jobService;
	}

	public static String getCronExpression() {
		String cronExpression = SysParamUtil.getValueAsString(JOB_FREQUENCY);

		if (StringUtils.isEmpty(cronExpression)) {
			cronExpression = DEFAULT_JOB_FREQUENCY;
		}

		if (!CronExpression.isValidExpression(cronExpression)) {
			throw new AppException(String.format("The cron expression %s for presentment auto extraction is not valid.",
					cronExpression));
		}

		return cronExpression;
	}

	public List<String> getMandateTypeList() {
		List<String> mandateTypes = new ArrayList<String>();
		mandateTypes.add(MandateConstants.TYPE_NACH);
		mandateTypes.add(MandateConstants.TYPE_PDC);
		return mandateTypes;
	}

}
