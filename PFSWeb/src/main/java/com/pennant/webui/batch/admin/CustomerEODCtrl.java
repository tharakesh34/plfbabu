package com.pennant.webui.batch.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.job.process.EodTrigger;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SessionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.dao.EodDetailDAO;
import com.pennant.eod.model.EodDetail;
import com.pennant.policy.model.UserImpl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /PFSWeb/WebContent/WEB-INF/pages/Batch/CustomerEOD.zul file.
 */
public class CustomerEODCtrl extends GFCBaseCtrl<Object> implements ApplicationContextAware {
	private static final long	serialVersionUID	= 4309463490869641570L;
	private final static Logger	logger				= Logger.getLogger(CustomerEODCtrl.class);

	protected Window			window_BatchAdmin;
	protected Textbox			lable_LastBusiness_Date;
	protected Textbox			lable_NextBusiness_Date;
	protected Textbox			lable_Value_Date;
	protected Textbox			startTime;
	protected Textbox			completedTime;

	protected Textbox			batchStatus;
	protected Button			btnStartJob;
	protected Borderlayout		borderLayoutBatchAdmin;

	private ApplicationContext	applicationContext;

	public CustomerEODCtrl() {
		super();
	}

	public void onCreate$window_BatchAdmin(Event event) throws Exception {

		lable_Value_Date.setValue(DateUtility.getValueDate(DateFormat.LONG_DATE));
		lable_NextBusiness_Date.setValue(DateUtility.formatToLongDate(SysParamUtil
				.getValueAsDate(PennantConstants.APP_DATE_NEXT)));
		lable_LastBusiness_Date.setValue(DateUtility.formatToLongDate(SysParamUtil
				.getValueAsDate(PennantConstants.APP_DATE_LAST)));
		EodDetailDAO eodDetailsDAO = applicationContext.getBean(EodDetailDAO.class);

		EodDetail eodDetail = eodDetailsDAO.getEodDetailById(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_VALUE));
		
		if (eodDetail==null) {
			eodDetail = eodDetailsDAO.getEodDetailById(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_LAST));
		}
		if (eodDetail != null) {
			this.startTime.setValue(DateUtility.format(eodDetail.getStatTime(), DateFormat.LONG_TIME));
			this.completedTime.setValue(DateUtility.format(eodDetail.getEndTime(), DateFormat.LONG_TIME));
			this.batchStatus.setValue(eodDetail.getStatus());
		}

		this.borderLayoutBatchAdmin.setHeight(getBorderLayoutHeight());

	}

	public void onClick$btnStartJob(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		MultiLineMessageBox.doSetTemplate();
		int conf = 0;
		String loggedInUsers = getLoggedInUsers();
		if (StringUtils.isNotEmpty(loggedInUsers)) {
			loggedInUsers = "\n" + loggedInUsers;
			Clients.showNotification(Labels.getLabel("label_current_logged_users", new String[] { loggedInUsers }),
					"info", null, null, -1);
			return;
		}

		String msg = Labels.getLabel("labe_start_job", new String[] { DateUtility.formatToShortDate(SysParamUtil
				.getValueAsDate(PennantConstants.APP_DATE_NEXT)) });

		conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Information"), MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			closeOtherTabs();
			this.btnStartJob.setDisabled(true);

			try {
				EodTrigger eodTrigger = (EodTrigger) applicationContext.getBean(EodTrigger.class);
				setFileds(eodTrigger);
				eodTrigger.run();

			} catch (Exception e) {
				MessageUtil.showErrorMessage(e.getMessage());
				logger.error("Exception: ", e);
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	private void setFileds(EodTrigger eodTrigger) {
		eodTrigger.map.put(1, lable_Value_Date);
		eodTrigger.map.put(2, lable_NextBusiness_Date);
		eodTrigger.map.put(3, lable_LastBusiness_Date);
		eodTrigger.map.put(4, btnStartJob);
		eodTrigger.map.put(5, startTime);
		eodTrigger.map.put(6, completedTime);
		eodTrigger.map.put(7, batchStatus);
	}

	public void onClickError(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		StepExecution stepExecution = (StepExecution) batchStatus.getAttribute("data");

		if (stepExecution != null) {
			Filedownload.save(stepExecution.getExitStatus().getExitDescription(), "text/plain",
					stepExecution.getStepName());
		}

		logger.debug("Leacing" + event.toString());
	}

	private String getLoggedInUsers() {
		StringBuilder builder = new StringBuilder();
		List<UserImpl> users = SessionUtil.getLoggedInUsers();
		SecurityUser secUser = null;
		if (!users.isEmpty()) {
			for (UserImpl user : users) {
				if (user.getUserId() != getUserWorkspace().getLoggedInUser().getLoginUsrID()) {
					if (builder.length() > 0) {
						builder.append("</br>");
					}
					secUser = user.getSecurityUser();
					builder.append("&bull;")
							.append("&nbsp;")
							.append(user.getUserId())
							.append("&ndash;")
							.append(secUser.getUsrFName() + " " + StringUtils.trimToEmpty(secUser.getUsrMName()) + " "
									+ secUser.getUsrLName());
				}
			}
		}
		return builder.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	private void closeOtherTabs() {

		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
				.getFellow("tabBoxIndexCenter");
		Tabs tabs = tabbox.getTabs();
		List<Component> childs = new ArrayList<Component>(tabs.getChildren());

		for (Component component : childs) {
			if (component instanceof Tab) {
				Tab tab = (Tab) component;
				if ("tab_Home".equals(tab.getId()) || tab.getId().equals(tabbox.getSelectedTab().getId())) {
					continue;
				}

				tab.close();
			}

		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

}
