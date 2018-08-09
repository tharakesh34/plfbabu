package com.pennant.app.job.process;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MailLog;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.maillog.MailLogDAO;
import com.pennant.backend.dao.notifications.NotificationsDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.NotificationConstants;

public class PriorityNotificationProcess extends QuartzJobBean implements StatefulJob, Serializable {
	private static final long serialVersionUID = 4480912264156310688L;
	private static final Logger logger = Logger.getLogger(PriorityNotificationProcess.class);

	public PriorityNotificationProcess() {
		super();
	}

	private FinanceMainDAO 		financeMainDAO;
	private NotificationsDAO 	notificationsDAO;
	private MailLogDAO 			mailLogDAO;
	private MailUtil 			mailUtil;

	Date appDate = DateUtility.getAppDate();

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Entering");

		final int maxAllowedDays = SysParamUtil.getValueAsInt("QP_NOTIFY_DAYS");
		//get List of References
		List<FinanceMain> referenceList = getFinanceMainDAO().getFinanceRefByPriority();

		if(maxAllowedDays != -1) {
			List<FinanceMain> pendingRefList = getFinanceMainDAO().getFinanceRefByValueDate(appDate,maxAllowedDays);
			referenceList.addAll(pendingRefList);
		}

		try {
			for(FinanceMain finMain : referenceList) {
				boolean isMailSent;
				//send mail
				getMailUtil().sendNotifications(NotificationConstants.TEMPLATE_FOR_QP, finMain);

				/*
				 * if(isMailSent){ MailLog mailLog = prepareMailLog(finMain); getMailLogDAO().saveMailLog(mailLog); }
				 */
			}
		} catch(Exception e){
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for prepare MailLog object
	 */
	private MailLog prepareMailLog(FinanceMain financeMain) {
		logger.debug("Entering");

		MailLog mailLog = new MailLog();
		mailLog.setMailReference(getMailLogDAO().getMailReference() + 1);
		mailLog.setModule(NotificationConstants.MAIL_MODULE_FIN);
		mailLog.setReference(financeMain.getFinReference());
		mailLog.setMailType(NotificationConstants.TEMPLATE_FOR_QP);
		mailLog.setValueDate(appDate);
		mailLog.setReqUser(financeMain.getLastMntBy());
		mailLog.setReqUserRole(financeMain.getRoleCode());
		mailLog.setUniqueRef(financeMain.getPriority());

		logger.debug("Leaving");
		return mailLog;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public MailUtil getMailUtil() {
		return mailUtil;
	}
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public NotificationsDAO getNotificationsDAO() {
		return notificationsDAO;
	}
	public void setNotificationsDAO(NotificationsDAO notificationsDAO) {
		this.notificationsDAO = notificationsDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public MailLogDAO getMailLogDAO() {
		return mailLogDAO;
	}

	public void setMailLogDAO(MailLogDAO mailLogDAO) {
		this.mailLogDAO = mailLogDAO;
	}

}
