package com.pennant.app.job.process;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.MailUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.TATDetailDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.maillog.MailLogDAO;
import com.pennant.backend.dao.notifications.NotificationsDAO;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.finance.TATNotificationCode;
import com.pennant.backend.model.finance.TATNotificationLog;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.core.EventManager;
import com.pennant.core.EventManager.Notify;

public class TatNotificationProcess extends QuartzJobBean implements StatefulJob, Serializable {
	private static final long serialVersionUID = -336577018042634131L;
	private static final Logger logger = Logger.getLogger(TatNotificationProcess.class);

	public TatNotificationProcess() {
		super();
	}

	private transient FinanceMainDAO financeMainDAO;
	private transient NotificationsDAO notificationsDAO;
	private transient DedupParmDAO dedupParmDAO;
	private transient MailLogDAO mailLogDAO;
	private transient MailUtil mailUtil;
	private transient TATDetailDAO tatDetailDAO;
	private transient SecurityUserDAO securityUserDAO;
	private boolean newRecord;
	private EventManager eventManager;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Entering");
		
		//Getting alert count from smt parameters.
		final int alertCount = SysParamUtil.getValueAsInt(NotificationConstants.TAT_ALT_CNT);
		//Fetching all the TAT records which are not submitted
		List<TATDetail> tatList = getTatDetailDAO().getAllTATDetail();
		if (tatList != null && tatList.size() > 0) {
			for (TATDetail tatDetail : tatList) {
				//Checking how many times alert sent
				TATNotificationLog notificationLog = getTatDetailDAO().getLogDetails(tatDetail);

				if (notificationLog == null) {
					newRecord = true;
					processTat(tatDetail, newRecord, 0);
				} else if (notificationLog.getCount() < alertCount) {
					newRecord = false;
					processTat(tatDetail, newRecord, notificationLog.getCount());
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * TAT Implementation Process
	 * 
	 * @param tatDetail
	 * @param newRecord
	 * @param alertCount
	 */
	private void processTat(TATDetail tatDetail, boolean newRecord, int alertCount) {
		logger.debug("Entering");
		try {
			FinanceReferenceDetail financeRefDetail = new FinanceReferenceDetail();
			financeRefDetail.setMandInputInStage(tatDetail.getRoleCode() + ",");
			financeRefDetail.setFinType(tatDetail.getFinType());
			//Getting the TAT Details
			List<FinanceReferenceDetail> refList = getDedupParmDAO().getQueryCodeList(financeRefDetail, "_TATView");
			if (refList != null && refList.size() > 0) {
				for (FinanceReferenceDetail financeReferenceDetail : refList) {
					//Getting the notification mechanism and time 
					TATNotificationCode notificationCode = getTatDetailDAO().getNotificationdetail(
							financeReferenceDetail.getLovDescNamelov());
					long waitTime = Long.parseLong(notificationCode.getTime()) * 60000;
					// Calculating the time difference
					long difftime = calculateTime(tatDetail.gettATStartTime(), tatDetail);

					if (difftime > waitTime) {
						tatDetail.setTriggerTime(new Timestamp(System.currentTimeMillis()));
						getTatDetailDAO().update(tatDetail);
						FinanceMain financeMain = getFinanceMainDAO().getFinanceMainByRef(
						        tatDetail.getReference(), "_Temp",false);


						if ("Internal".equals(financeReferenceDetail.getAlertType())) {
							//Internal Alert
							if (financeMain.getNextUserId() != null) {
								SecurityUser user = getSecurityUserDAO().getSecurityUserById(
										Long.parseLong(financeMain.getNextUserId()), "");
								Notify notify = Notify.valueOf("USER");
								String[] to = user.getUsrLogin().split(",");
								if (StringUtils.isNotEmpty(financeMain.getFinReference())) {

									String reference = financeMain.getFinReference();
									getEventManager()
											.publish(
													Labels.getLabel("Internal_TAT_Msg") 
															+ reference, notify, to);
								} else {
									getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE"), notify, to);
								}
							}
						} else {
							//Mail Sending
							getMailUtil().sendNotifications(NotificationConstants.TEMPLATE_FOR_TAT, financeMain);
						}

						//Log Writing
						TATNotificationLog tatNotificationLog = new TATNotificationLog();
						tatNotificationLog.setModule(tatDetail.getModule());
						tatNotificationLog.setReference(tatDetail.getReference());
						tatNotificationLog.setRoleCode(tatDetail.getRoleCode());
						if (newRecord) {
							tatNotificationLog.setCount(1);
							getTatDetailDAO().saveLogDetail(tatNotificationLog);
						} else {
							tatNotificationLog.setCount(alertCount + 1);
							getTatDetailDAO().updateLogDetail(tatNotificationLog);
						}
					}
				}

			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Calculating the time difference
	 * 
	 * @param eventTime
	 * @param tatDetail
	 * @return
	 */
	private long calculateTime(Timestamp eventTime, TATDetail tatDetail) {
		logger.debug("Entering");
		long difftme = 0;
		String edTime = null;
		SimpleDateFormat format = new SimpleDateFormat(PennantConstants.dateTimeFormat);
		Date stdd = null;
		long currenTime = System.currentTimeMillis();
		try {
			if (tatDetail.getTriggerTime() == null) {
				String stTime = format.format(new Timestamp(currenTime));
				stdd = format.parse(stTime);
				edTime = format.format(eventTime);
				Date etdd = format.parse(edTime);
				difftme = stdd.getTime() - etdd.getTime();
			} else {
				stdd = tatDetail.getTriggerTime();
				edTime = format.format(new Timestamp(currenTime));
				Date etdd = format.parse(edTime);
				difftme = etdd.getTime() - stdd.getTime();
			}

		} catch (ParseException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return difftme;
	}

	// Setters and Getters

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public NotificationsDAO getNotificationsDAO() {
		return notificationsDAO;
	}

	public void setNotificationsDAO(NotificationsDAO notificationsDAO) {
		this.notificationsDAO = notificationsDAO;
	}

	public MailLogDAO getMailLogDAO() {
		return mailLogDAO;
	}

	public void setMailLogDAO(MailLogDAO mailLogDAO) {
		this.mailLogDAO = mailLogDAO;
	}

	public MailUtil getMailUtil() {
		return mailUtil;
	}

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public TATDetailDAO getTatDetailDAO() {
		return tatDetailDAO;
	}

	public void setTatDetailDAO(TATDetailDAO tatDetailDAO) {
		this.tatDetailDAO = tatDetailDAO;
	}

	public DedupParmDAO getDedupParmDAO() {
		return dedupParmDAO;
	}

	public void setDedupParmDAO(DedupParmDAO dedupParmDAO) {
		this.dedupParmDAO = dedupParmDAO;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public SecurityUserDAO getSecurityUserDAO() {
		return securityUserDAO;
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

}
