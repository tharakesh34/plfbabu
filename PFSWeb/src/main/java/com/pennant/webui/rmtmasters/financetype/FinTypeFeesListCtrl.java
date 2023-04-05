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
 * * FileName : FinTypeFeesListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-03-2017 * * Modified
 * Date : 21-03-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-03-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.financetype;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.service.rmtmasters.FinTypeAccountingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.constants.AccountingEvent;

public class FinTypeFeesListCtrl extends GFCBaseCtrl<FinTypeFees> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinTypeFeesListCtrl.class);

	protected Window window_FinTypeFeesList;

	private Component parent = null;
	// private Tab parentTab = null;
	protected Listbox listBoxFinTypeFeesListOrigination;
	protected Listbox listBoxFinTypeFeesListServicing;

	protected Button btnNew_FinTypeFeesList_Origination;
	protected Button btnNew_FinTypeFeesList_Servicing;

	// ### START SFA_20210405 -->
	protected Listheader listheader_FinTypeFeesList_Org_InclForAssigment;
	protected Listheader listheader_FinTypeFeesList_Serv_InclForAssigment;
	// ### END SFA_20210405 <--

	private List<FinTypeFees> finTypeFeesList = new ArrayList<FinTypeFees>();
	private Map<String, String> eventDetailMap = new HashMap<String, String>();
	private List<FinTypeFees> finTypeFeesOriginationList = new ArrayList<FinTypeFees>();
	private List<FinTypeFees> finTypeFeesServicingList = new ArrayList<FinTypeFees>();

	private Object mainController;

	private String roleCode = "";
	private String finCcy = "";
	private String finType = "";
	protected int moduleId = 0;
	protected boolean isOverdraft = false;
	protected boolean consumerDurable = false;
	private boolean isCompReadonly = false;
	private boolean excludeAppFeeCodes = false;

	private FinTypeAccountingService finTypeAccountingService;

	/**
	 * default constructor.<br>
	 */
	public FinTypeFeesListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypeFeesList";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_FinTypeFeesList(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTypeFeesList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			// if (arguments.containsKey("parentTab")) {
			// parentTab = (Tab) arguments.get("parentTab");
			// }

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), super.pageRightName);
			}

			if (arguments.containsKey("finType")) {
				finType = (String) arguments.get("finType");
			}

			if (arguments.containsKey("finCcy")) {
				finCcy = (String) arguments.get("finCcy");
			}

			if (arguments.containsKey("moduleId")) {
				moduleId = (int) arguments.get("moduleId");
			}

			if (arguments.containsKey("mainController")) {
				this.mainController = (Object) arguments.get("mainController");
			}

			if (arguments.containsKey("isCompReadonly")) {
				this.isCompReadonly = (boolean) arguments.get("isCompReadonly");
			}

			if (arguments.containsKey("finTypeFeesList")) {
				this.finTypeFeesList = (List<FinTypeFees>) arguments.get("finTypeFeesList");
			}
			if (arguments.containsKey("isOverdraft")) {
				this.isOverdraft = (Boolean) arguments.get("isOverdraft");
			}
			if (arguments.containsKey("consumerDurable")) {
				this.consumerDurable = (Boolean) arguments.get("consumerDurable");
			}

			if (arguments.containsKey("excludeAppFeeCodes")) {
				this.excludeAppFeeCodes = (Boolean) arguments.get("excludeAppFeeCodes");
			}

			doEdit();
			doCheckRights();
			doSetFieldProperties();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_FinTypeFeesList.onClose();
		}

		logger.debug("Leaving");
	}

	private void doSetFieldProperties() {
		/*
		 * if (ImplementationConstants.ALLOW_SINGLE_FEE_CONFIG) {
		 * this.listheader_FinTypeFeesList_Org_InclForAssigment.setVisible(true);
		 * this.listheader_FinTypeFeesList_Serv_InclForAssigment.setVisible(true); }
		 */
	}

	private void doCheckRights() {
		logger.debug("Entering");

		// getUserWorkspace().allocateAuthorities(super.pageRightName, roleCode);
		this.btnNew_FinTypeFeesList_Origination.setVisible(!isCompReadonly);
		this.btnNew_FinTypeFeesList_Servicing.setVisible(!isCompReadonly);

		logger.debug("leaving");
	}

	private void doShowDialog()
			throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		logger.debug("Entering");

		doStoreEventDetails();
		doFillFinTypeFeesOrigination(getFinTypeFeesByModule(this.finTypeFeesList, true));
		doFillFinTypeFeesServicing(getFinTypeFeesByModule(this.finTypeFeesList, false));

		if (parent != null) {
			this.window_FinTypeFeesList.setHeight(borderLayoutHeight - 75 + "px");
			parent.appendChild(this.window_FinTypeFeesList);
			this.listBoxFinTypeFeesListOrigination.setHeight(this.borderLayoutHeight - 145 + "px");
			this.listBoxFinTypeFeesListServicing.setHeight(this.borderLayoutHeight - 145 + "px");

			try {
				getMainController().getClass().getMethod("setFinTypeFeesListCtrl", this.getClass())
						.invoke(mainController, this);
			} catch (InvocationTargetException e) {
				logger.error("Exception: ", e);
			}
		}

		logger.debug("leaving");
	}

	private void doStoreEventDetails() {
		List<AccountEngineEvent> accEventsList = getAccountingEvents();
		this.eventDetailMap.clear();
		for (AccountEngineEvent accountEngineEvent : accEventsList) {
			this.eventDetailMap.put(accountEngineEvent.getAEEventCode(), accountEngineEvent.getAEEventCodeDesc());
		}
	}

	private List<AccountEngineEvent> getAccountingEvents() {
		String categoryCode = AccountingEvent.EVENTCTG_FINANCE;
		if (this.isOverdraft) {
			categoryCode = AccountingEvent.EVENTCTG_OVERDRAFT;
		} else if (this.consumerDurable) {
			categoryCode = AccountingEvent.EVENTCTG_GOLD;
		}

		return finTypeAccountingService.getAccountEngineEvents(categoryCode);
	}

	private List<FinTypeFees> getFinTypeFeesByModule(List<FinTypeFees> finTypeFeesList, boolean isOriginationFees) {
		List<FinTypeFees> feesList = new ArrayList<FinTypeFees>();
		if (finTypeFeesList != null && !finTypeFeesList.isEmpty()) {
			for (FinTypeFees finTypeFee : finTypeFeesList) {
				if ((finTypeFee.isOriginationFee() == isOriginationFees)
						|| (!finTypeFee.isOriginationFee() == !isOriginationFees)) {
					feesList.add(finTypeFee);
				}
			}
		}
		return feesList;
	}

	public void doFillFinTypeFeesOrigination(List<FinTypeFees> finTypeFeesList) {
		logger.debug("Entering");
		try {

			if (CollectionUtils.isEmpty(finTypeFeesList)) {
				return;
			}

			finTypeFeesList = sortFeesByFeeOrder(finTypeFeesList);
			setFinTypeFeesOriginationList(finTypeFeesList);
			this.listBoxFinTypeFeesListOrigination.getItems().clear();
			String listGroupEvent = "";
			Listgroup group;
			Listcell lc;
			List<ValueLabel> remFeeSchList = PennantStaticListUtil.getRemFeeSchdMethods();
			List<ValueLabel> feeCalTypeList = PennantStaticListUtil.getRemFeeSchdMethods();

			for (FinTypeFees finTypeFee : finTypeFeesList) {
				if (!finTypeFee.isOriginationFee()) {
					continue;
				}
				if (!StringUtils.equals(finTypeFee.getFinEvent(), listGroupEvent)) {
					group = new Listgroup();
					if (this.eventDetailMap.get(finTypeFee.getFinEvent()) == null) {
						lc = new Listcell(finTypeFee.getFinEvent());
					} else {
						lc = new Listcell(
								finTypeFee.getFinEvent() + "-" + this.eventDetailMap.get(finTypeFee.getFinEvent()));
					}
					lc.setParent(group);
					this.listBoxFinTypeFeesListOrigination.appendChild(group);
				}
				listGroupEvent = finTypeFee.getFinEvent();
				Listitem item = new Listitem();
				lc = new Listcell(String.valueOf(finTypeFee.getFeeOrder()));
				lc.setParent(item);
				lc = new Listcell(finTypeFee.getFeeTypeCode());
				lc.setParent(item);
				lc = new Listcell(PennantStaticListUtil.getlabelDesc(finTypeFee.getCalculationType(), feeCalTypeList));
				lc.setParent(item);
				lc = new Listcell(PennantStaticListUtil.getlabelDesc(finTypeFee.getFeeScheduleMethod(), remFeeSchList));
				lc.setParent(item);
				lc = new Listcell(String.valueOf(finTypeFee.getMaxWaiverPerc()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell();
				Checkbox modifyFeeCB = new Checkbox();
				modifyFeeCB.setChecked(finTypeFee.isAlwModifyFee());
				modifyFeeCB.setDisabled(true);
				modifyFeeCB.setParent(lc);
				lc.setParent(item);
				lc = new Listcell();
				Checkbox alwDeviationCB = new Checkbox();
				alwDeviationCB.setChecked(finTypeFee.isAlwDeviation());
				alwDeviationCB.setDisabled(true);
				alwDeviationCB.setParent(lc);
				lc.setParent(item);

				// ### START SFA_20210405 -->
				// if (ImplementationConstants.ALLOW_SINGLE_FEE_CONFIG) {
				// lc = new Listcell();
				// Checkbox inclForAssignment = new Checkbox();
				// inclForAssignment.setChecked(finTypeFee.isInclForAssignment());
				// inclForAssignment.setDisabled(true);
				// inclForAssignment.setParent(lc);
				// lc.setParent(item);
				// }
				// ### END SFA_20210405 <--

				lc = new Listcell(finTypeFee.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(finTypeFee.getRecordType());
				lc.setParent(item);
				item.setAttribute("data", finTypeFee);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinTypeFeesOrgItemDoubleClicked");
				this.listBoxFinTypeFeesListOrigination.appendChild(item);
			}

		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	private List<FinTypeFees> sortFeesByFeeOrder(List<FinTypeFees> finTypeFeesList) {
		List<FinTypeFees> sortedList = new ArrayList<FinTypeFees>();
		Map<String, List<FinTypeFees>> feesMap = new HashMap<String, List<FinTypeFees>>();

		if (finTypeFeesList != null && !finTypeFeesList.isEmpty()) {
			for (FinTypeFees finTypeFee : finTypeFeesList) {
				if (!feesMap.containsKey(finTypeFee.getFinEvent())) {
					feesMap.put(finTypeFee.getFinEvent(), new ArrayList<FinTypeFees>());
				}
				feesMap.get(finTypeFee.getFinEvent()).add(finTypeFee);
			}
		}

		for (String eventCode : feesMap.keySet()) {
			List<FinTypeFees> eventList = feesMap.get(eventCode);
			Comparator<FinTypeFees> beanComp = new BeanComparator<FinTypeFees>("feeOrder");
			Collections.sort(eventList, beanComp);
			sortedList.addAll(eventList);
		}

		return sortedList;
	}

	public void onClick$btnNew_FinTypeFeesList_Origination(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Clients.clearWrongValue(this.listBoxFinTypeFeesListOrigination);

		final FinTypeFees finTypeFees = new FinTypeFees("");
		finTypeFees.setFinType(this.finType);
		finTypeFees.setOriginationFee(true);
		finTypeFees.setActive(true);
		finTypeFees.setNewRecord(true);
		finTypeFees.setModuleId(moduleId);

		showDetailFinTypeFeesView(finTypeFees);

		logger.debug("Leaving" + event.toString());
	}

	public void onFinTypeFeesOrgItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Listitem item = (Listitem) event.getOrigin().getTarget();
		FinTypeFees finTypeFees = (FinTypeFees) item.getAttribute("data");

		if (!StringUtils.trimToEmpty(finTypeFees.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			finTypeFees.setNewRecord(false);
			finTypeFees.setOriginationFee(true);

			showDetailFinTypeFeesView(finTypeFees);
		}

		logger.debug("Leaving" + event.toString());
	}

	private void showDetailFinTypeFeesView(FinTypeFees finTypeFees) throws InterruptedException {

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finTypeFees", finTypeFees);
		map.put("finTypeFeesListCtrl", this);
		map.put("role", roleCode);
		map.put("ccyFormat", CurrencyUtil.getFormat(this.finCcy));
		map.put("excludeAppFeeCodes", this.excludeAppFeeCodes);

		String feeTypeCode = finTypeFees.getFeeTypeCode();
		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;

		if ((AdvanceRuleCode.ADVINT.name().equals(feeTypeCode) || AdvanceRuleCode.ADVEMI.name().equals(feeTypeCode)
				|| AdvanceRuleCode.CASHCLT.name().equals(feeTypeCode) || AdvanceRuleCode.DSF.name().equals(feeTypeCode)
				|| subventionFeeCode.equals(feeTypeCode) || StringUtils.equals(subventionFeeCode, feeTypeCode))) {
			map.put("enqiryModule", enqiryModule);
		}
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeFeesDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void onClick$btnNew_FinTypeFeesList_Servicing(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Clients.clearWrongValue(this.listBoxFinTypeFeesListServicing);

		// create a new IncomeExpenseDetail object, We GET it from the backEnd.
		final FinTypeFees finTypeFees = new FinTypeFees("");
		finTypeFees.setFinType(this.finType);
		finTypeFees.setOriginationFee(false);
		finTypeFees.setActive(true);
		finTypeFees.setNewRecord(true);
		finTypeFees.setModuleId(moduleId);

		showDetailFinTypeFeesView(finTypeFees);

		logger.debug("Leaving" + event.toString());
	}

	public void onFinTypeFeesServicingItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Listitem item = (Listitem) event.getOrigin().getTarget();
		FinTypeFees finTypeFees = (FinTypeFees) item.getAttribute("data");

		if (!StringUtils.trimToEmpty(finTypeFees.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			finTypeFees.setNewRecord(false);
			finTypeFees.setOriginationFee(false);

			showDetailFinTypeFeesView(finTypeFees);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doFillFinTypeFeesServicing(List<FinTypeFees> finTypeFees) {
		logger.debug("Entering");

		try {
			if (finTypeFees != null) {
				finTypeFees = sortFeesByFeeOrder(finTypeFees);
				setFinTypeFeesServicingList(finTypeFees);
				this.listBoxFinTypeFeesListServicing.getItems().clear();
				String listGroupEvent = "";

				Listgroup group;
				Listcell lc;
				for (FinTypeFees finTypeFee : finTypeFees) {
					if (finTypeFee.isOriginationFee()) {
						continue;
					}

					if (!StringUtils.equals(finTypeFee.getFinEvent(), listGroupEvent)) {
						group = new Listgroup();
						if (this.eventDetailMap.get(finTypeFee.getFinEvent()) == null) {
							lc = new Listcell(finTypeFee.getFinEvent());
						} else {
							lc = new Listcell(
									finTypeFee.getFinEvent() + "-" + this.eventDetailMap.get(finTypeFee.getFinEvent()));
						}
						lc.setParent(group);
						this.listBoxFinTypeFeesListServicing.appendChild(group);
					}

					listGroupEvent = finTypeFee.getFinEvent();
					Listitem item = new Listitem();
					lc = new Listcell(String.valueOf(finTypeFee.getFeeOrder()));
					lc.setParent(item);
					lc = new Listcell(finTypeFee.getFeeTypeCode());
					lc.setParent(item);
					lc = new Listcell(PennantStaticListUtil.getlabelDesc(finTypeFee.getCalculationType(),
							PennantStaticListUtil.getFeeCalculationTypes()));
					lc.setParent(item);
					lc = new Listcell(String.valueOf(finTypeFee.getMaxWaiverPerc()));
					lc.setParent(item);
					lc = new Listcell();
					Checkbox modifyFeeCB = new Checkbox();
					modifyFeeCB.setChecked(finTypeFee.isAlwModifyFee());
					modifyFeeCB.setDisabled(true);
					modifyFeeCB.setParent(lc);
					lc.setParent(item);
					lc = new Listcell();
					Checkbox alwDeviationCB = new Checkbox();
					alwDeviationCB.setChecked(finTypeFee.isAlwDeviation());
					alwDeviationCB.setDisabled(true);
					alwDeviationCB.setParent(lc);
					lc.setParent(item);

					// ### START SFA_20210405 -->
					// if (ImplementationConstants.ALLOW_SINGLE_FEE_CONFIG) {
					// lc = new Listcell();
					// Checkbox inclForAssignment = new Checkbox();
					// inclForAssignment.setChecked(finTypeFee.isInclForAssignment());
					// inclForAssignment.setDisabled(true);
					// inclForAssignment.setParent(lc);
					// lc.setParent(item);
					// }
					// ### END SFA_20210405 <--

					lc = new Listcell(finTypeFee.getRecordStatus());
					lc.setParent(item);
					lc = new Listcell(finTypeFee.getRecordType());
					lc.setParent(item);
					item.setAttribute("data", finTypeFee);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onFinTypeFeesServicingItemDoubleClicked");
					this.listBoxFinTypeFeesListServicing.appendChild(item);
				}
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	public List<FinTypeFees> onSave() {
		logger.debug("Entering");

		List<FinTypeFees> finTypeFeesList = new ArrayList<FinTypeFees>();
		finTypeFeesList.addAll(getFinTypeFeesOriginationList());
		finTypeFeesList.addAll(getFinTypeFeesServicingList());

		logger.debug("Leaving");

		return finTypeFeesList;
	}

	private void doEdit() {

	}

	public List<FinTypeFees> getFinTypeFeesList() {
		return finTypeFeesList;
	}

	public void setFinTypeFeesList(List<FinTypeFees> finTypeFeesList) {
		this.finTypeFeesList = finTypeFeesList;
	}

	public List<FinTypeFees> getFinTypeFeesOriginationList() {
		return finTypeFeesOriginationList;
	}

	public void setFinTypeFeesOriginationList(List<FinTypeFees> finTypeFeesOriginationList) {
		this.finTypeFeesOriginationList = finTypeFeesOriginationList;
	}

	public List<FinTypeFees> getFinTypeFeesServicingList() {
		return finTypeFeesServicingList;
	}

	public void setFinTypeFeesServicingList(List<FinTypeFees> finTypeFeesServicingList) {
		this.finTypeFeesServicingList = finTypeFeesServicingList;
	}

	public Object getMainController() {
		return mainController;
	}

	public void setMainController(Object mainController) {
		this.mainController = mainController;
	}

	@Autowired
	public void setFinTypeAccountingService(FinTypeAccountingService finTypeAccountingService) {
		this.finTypeAccountingService = finTypeAccountingService;
	}

}
