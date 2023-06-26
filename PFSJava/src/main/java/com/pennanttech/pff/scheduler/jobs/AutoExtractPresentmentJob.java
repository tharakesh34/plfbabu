package com.pennanttech.pff.scheduler.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.financemanagement.impl.PresentmentJobService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoExtractPresentmentJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getKey().getName();

		logger.debug("JOB: {}", jobName);

		List<String> mandateTypes = getMandateTypeList();
		Date appDate = SysParamUtil.getAppDate();
		Date toDate = null;
		Date fromDate = null;
		int nachDateFreq = SysParamUtil.getValueAsInt(SMTParameterConstants.PRESENTMENT_NACH_DATE_FREQUENCY);
		int pdcDateFreq = SysParamUtil.getValueAsInt(SMTParameterConstants.PRESENTMENT_PDC_DATE_FREQUENCY);

		List<String> entities = getExtractService(context).getEntityCodes();

		if (CollectionUtils.isEmpty(entities)) {
			return;
		}

		for (String entityCode : entities) {
			List<FinanceType> loanTypes = getExtractService(context).getFinanceTypeList(entityCode);
			for (FinanceType loanType : loanTypes) {
				for (String mandateType : mandateTypes) {
					PresentmentHeader presentmentHeader = new PresentmentHeader();
					if (InstrumentType.isNACH(mandateType)) {
						toDate = DateUtil.addDays(appDate, nachDateFreq);
						fromDate = DateUtil.addDays(appDate, nachDateFreq);
					} else if (InstrumentType.isPDC(mandateType)) {
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
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (PresentmentJobService) jobDataMap.get("presentmentJobService");
	}

	public List<String> getMandateTypeList() {
		List<String> mandateTypes = new ArrayList<String>();
		mandateTypes.add(InstrumentType.NACH.name());
		mandateTypes.add(InstrumentType.PDC.name());
		return mandateTypes;
	}

}
