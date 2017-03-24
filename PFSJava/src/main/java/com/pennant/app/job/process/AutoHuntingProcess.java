package com.pennant.app.job.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pennant.Interface.service.PostingsInterfaceService;
import com.pennant.app.core.ServiceUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.SysParamUtil.Param;
import com.pennant.backend.dao.ext.ExtTablesDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.ExtTable;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.ddapayments.impl.DDARepresentmentService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;

public class AutoHuntingProcess extends QuartzJobBean implements StatefulJob, Serializable {

	private static final long			serialVersionUID	= 3054694495821370902L;
	private static final Logger			logger				= Logger.getLogger(AutoHuntingProcess.class);

	private ExtTablesDAO				extTablesDAO;
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private FinanceMainDAO				financeMainDAO;

	private ServiceUtil					serviceUtil;
	private DDARepresentmentService		ddaRepresentmentService;
	private PostingsInterfaceService	postingsInterfaceService;

	public AutoHuntingProcess() {
		super();

	}

	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		try {
			String status = SysParamUtil.getValueAsString(Param.AUTOHUNTING.getCode());

			if (PennantConstants.AUTOHUNT_BATCH.equals(status)) {
				SysParamUtil.updateParamDetails(Param.AUTOHUNTING.getCode(), PennantConstants.AUTOHUNT_STOPPED);
				return;
			}

			if (!PennantConstants.AUTOHUNT_RUNNING.equals(status)) {
				return;
			}

			List<ExtTable> pdDetails = getExtTablesDAO().getPDDetails();
			logger.debug("Hunting");

			if (pdDetails != null && pdDetails.size() > 0) {
				// update the flag as process i.e. 1
				getExtTablesDAO().updateBatch(pdDetails);
				logger.debug("Process Started");

				for (ExtTable extTable : pdDetails) {
					String[] accDetails = StringUtils.trimToEmpty(extTable.getAccountBalance()).split(",");

					String accountNumber = accDetails[0];
					String accountBalance = accDetails[1];
					BigDecimal accBalance = new BigDecimal(accountBalance);

					if (accBalance.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					/*
					 * Since account and finance currency are same format the
					 * amount based on finance currency
					 */
					String finccy = getFinanceMainDAO().getCurrencyByAccountNo(accountNumber);
					BigDecimal running = PennantApplicationUtil.unFormateAmount(accBalance, CurrencyUtil.getFormat(finccy));
					List<FinanceScheduleDetail> prischDetails = getFinanceScheduleDetailDAO().getFinSchDetlsByPrimary(accountNumber);
					List<FinanceScheduleDetail> scschDetails = getFinanceScheduleDetailDAO().getFinSchDetlsBySecondary(accountNumber);

					if (scschDetails != null && !scschDetails.isEmpty()) {
						prischDetails.addAll(scschDetails);
					}

					for (FinanceScheduleDetail scheduleDetail : prischDetails) {

						BigDecimal totalDue = getServiceUtil().getTotDueBySchedule(scheduleDetail);

						if (running.compareTo(totalDue) <= 0) {
							totalDue = running;
						}

						processAutoHunting(DateUtility.getValueDate(),scheduleDetail, totalDue);
						running = running.subtract(totalDue);

						if (running.compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
					}
				}
				logger.debug("Process Completed");
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
		}

	}

	/**
	 * @param scheduleDetail
	 * @param amoutPaid
	 * @throws Exception
	 */
	public void processAutoHunting(Date date,FinanceScheduleDetail scheduleDetail, BigDecimal amoutPaid) throws Exception {

		if (amoutPaid.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		List<ReturnDataSet> list = getServiceUtil().processAutoHunting(date,scheduleDetail, amoutPaid);
		if (!list.isEmpty()) {
			getPostingsInterfaceService().doFillPostingDetails(list, list.get(0).getFinBranch(), Long.MIN_VALUE, "Y");
		}
		// send the past due cleared details to DDS
		getDdaRepresentmentService().doDDARepresentment(scheduleDetail.getSchDate(), scheduleDetail.getFinReference());

	}

	// Getters and Setters

	public ExtTablesDAO getExtTablesDAO() {
		return extTablesDAO;
	}

	public void setExtTablesDAO(ExtTablesDAO extTablesDAO) {
		this.extTablesDAO = extTablesDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public DDARepresentmentService getDdaRepresentmentService() {
		return ddaRepresentmentService;
	}

	public void setDdaRepresentmentService(DDARepresentmentService ddaRepresentmentService) {
		this.ddaRepresentmentService = ddaRepresentmentService;
	}

	public PostingsInterfaceService getPostingsInterfaceService() {
		return postingsInterfaceService;
	}

	public void setPostingsInterfaceService(PostingsInterfaceService postingsInterfaceService) {
		this.postingsInterfaceService = postingsInterfaceService;
	}

	public ServiceUtil getServiceUtil() {
		return serviceUtil;
	}

	public void setServiceUtil(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
}
