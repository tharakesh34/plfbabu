/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : VehicleDealerListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-09-2011 * *
 * Modified Date : 29-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-09-2011 Pennant 0.1 * * 01-05-2018 Vinay 0.2 Module code added for individual module * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.alerts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.dao.finance.putcall.FinOptionDAO;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.service.finance.covenant.impl.CovenantAlerts;
import com.pennant.backend.service.finance.covenant.impl.PutCallAlerts;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Alerts/AlertList.zul file.
 */
public class AlertListCtrl extends GFCBaseListCtrl<Object> {
	private static final long serialVersionUID = 259921702952389829L;

	protected Window window;
	protected Borderlayout borderLayout;
	protected Paging paging;
	protected Listbox covenantListbox;
	protected Listbox putCallListbox;
	protected Button btnSendAlerts;

	private String module;

	@Autowired
	private CovenantAlerts covenantAlerts;
	@Autowired
	private PutCallAlerts putCallAlerts;

	private PagedListWrapper<Covenant> covenantListWrapper;
	private PagedListWrapper<FinOption> putCallListWrapper;
	private CovenantsDAO covenantsDAO;
	private FinOptionDAO finOptionDAO;

	List<Covenant> covenants = new ArrayList<>();
	List<FinOption> finOptions = new ArrayList<>();

	/**
	 * default constructor.<br>
	 */
	public AlertListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		this.module = getArgument("module");

		if (module.equals("COVENANT")) {
			super.moduleCode = "Covenant";
			super.pageRightName = "CovenantAlerts";
			this.covenantListbox.setVisible(true);

		} else if (module.equals("PUTCALL")) {
			super.moduleCode = "FinOptionType";
			this.putCallListbox.setVisible(true);
		}

	}

	@Override
	protected void doAddFilters() {

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window(Event event) {
		Date appDate = DateUtil.getDatePart(SysParamUtil.getAppDate());
		if (module.equals("COVENANT")) {
			setPageComponents(window, borderLayout, covenantListbox, paging);

			doRenderPage();

			List<Covenant> covList = new ArrayList<>();
			covenants = covenantsDAO.getCovenantsAlertList();

			for (Covenant covenant : covenants) {
				Date lastAlertSentOn = covenant.getAlertsentOn();
				int alertDays = covenant.getAlertDays();
				if (lastAlertSentOn != null && DateUtil.getDaysBetween(appDate, lastAlertSentOn) <= alertDays) {
					continue;
				}

				covList.add(covenant);
			}

			if (CollectionUtils.isNotEmpty(covList)) {
				getCovenantListWrapper().initList(covList, covenantListbox, paging);
				this.covenantListbox.setItemRenderer(new CovenantAlertsModelItemRenderer());
			}
		} else if (module.equals("PUTCALL")) {
			setPageComponents(window, borderLayout, putCallListbox, paging);

			doRenderPage();

			List<FinOption> finList = new ArrayList<>();
			finOptions = finOptionDAO.getPutCallAlertList();

			for (FinOption finOption : finOptions) {
				Date lastAlertSentOn = finOption.getAlertsentOn();
				int alertDays = finOption.getAlertDays();
				if (lastAlertSentOn != null && DateUtil.getDaysBetween(appDate, lastAlertSentOn) <= alertDays) {
					continue;
				}

				finList.add(finOption);
			}

			if (CollectionUtils.isNotEmpty(finList)) {
				getPutCallListWrapper().initList(finList, putCallListbox, paging);
				this.putCallListbox.setItemRenderer(new PutCallAlertslItemRenderer());
			}
		}
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSendAlerts(Event event) {
		logger.debug(Literal.ENTERING);
		if (module.equals("COVENANT")) {
			CovenantAlertsThred covenantAlertsThred = new CovenantAlertsThred();
			Thread thread = new Thread(covenantAlertsThred);
			thread.start();
		} else if (module.equals("PUTCALL")) {
			PutCallAlertsThread putCallAlertsThread = new PutCallAlertsThread();
			Thread thread = new Thread(putCallAlertsThread);
			thread.start();
		}

		logger.debug(Literal.LEAVING);

	}

	public class CovenantAlertsThred implements Runnable {
		@Override
		public void run() {
			covenantAlerts.sendAlerts();
		}
	}

	public class PutCallAlertsThread implements Runnable {
		@Override
		public void run() {
			putCallAlerts.sendAlerts();
		}
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<Covenant> getCovenantListWrapper() {
		if (this.covenantListWrapper == null) {
			this.covenantListWrapper = (PagedListWrapper<Covenant>) SpringUtil.getBean("pagedListWrapper");
		}
		return covenantListWrapper;
	}

	@SuppressWarnings("unchecked")
	public PagedListWrapper<FinOption> getPutCallListWrapper() {
		if (this.putCallListWrapper == null) {
			this.putCallListWrapper = (PagedListWrapper<FinOption>) SpringUtil.getBean("pagedListWrapper");
		}
		return putCallListWrapper;
	}

	public CovenantsDAO getCovenantsDAO() {
		return covenantsDAO;
	}

	public void setCovenantsDAO(CovenantsDAO covenantsDAO) {
		this.covenantsDAO = covenantsDAO;
	}

	public void setFinOptionDAO(FinOptionDAO finOptionDAO) {
		this.finOptionDAO = finOptionDAO;
	}
}