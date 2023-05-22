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
 * * FileName : FinTypeAccountingListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-03-2017 * *
 * Modified Date : 21-03-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-03-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.financetype;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.service.rmtmasters.FinTypeAccountingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;

public class FinTypeAccountingListCtrl extends GFCBaseCtrl<FinTypeAccounting> {

	private static final long serialVersionUID = 4521079241535245640L;

	private static final Logger logger = LogManager.getLogger(FinTypeAccountingListCtrl.class);

	protected Window window_FinTypeAccountingList;

	private Component parent = null;
	private Tab parentTab = null;

	private List<FinTypeAccounting> finTypeAccountingList = new ArrayList<FinTypeAccounting>();
	private Map<String, FinTypeAccounting> finTypeAccEventMap = new LinkedHashMap<String, FinTypeAccounting>();

	private Listbox listBoxAccountingDetails;

	private String roleCode = "";
	private String finType = "";
	protected boolean isOverdraft = false;
	protected boolean consumerDurable = false;
	private boolean isCompReadonly = false;
	private boolean validate = false;

	private Object mainController;
	protected int moduleId;

	private FinTypeAccountingService finTypeAccountingService;

	/**
	 * default constructor.<br>
	 */
	public FinTypeAccountingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypeAccountingList";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_FinTypeAccountingList(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTypeAccountingList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("parentTab")) {
				parentTab = (Tab) arguments.get("parentTab");
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "FinTypeAccountingList");
			}

			if (arguments.containsKey("finType")) {
				finType = (String) arguments.get("finType");
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

			if (arguments.containsKey("finTypeAccountingList")) {
				this.finTypeAccountingList = (List<FinTypeAccounting>) arguments.get("finTypeAccountingList");
			}

			if (arguments.containsKey("isOverdraft")) {
				this.isOverdraft = (Boolean) arguments.get("isOverdraft");
			}
			if (arguments.containsKey("consumerDurable")) {
				this.consumerDurable = (Boolean) arguments.get("consumerDurable");
			}

			this.listBoxAccountingDetails.setHeight(this.borderLayoutHeight - 120 + "px");

			doEdit();
			doCheckRights();
			doSetFieldProperties();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinTypeAccountingList.onClose();
		}

		logger.debug("Leaving");
	}

	private void doEdit() {

	}

	private void doSetFieldProperties() {

	}

	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("FinTypeInsuranceList", roleCode);

		logger.debug("leaving");
	}

	private void doShowDialog()
			throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		logger.debug("Entering");

		doFillFinTypeAccountingList(this.finTypeAccountingList);

		if (parent != null) {
			this.window_FinTypeAccountingList.setHeight(borderLayoutHeight - 75 + "px");
			parent.appendChild(this.window_FinTypeAccountingList);
		}

		try {
			getMainController().getClass().getMethod("setFinTypeAccountingListCtrl", this.getClass())
					.invoke(mainController, this);
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
		}

		doSetAccountingMandatory();

		logger.debug("leaving");
	}

	private void doSetAccountingMandatory() {
		logger.debug("Entering");

		FinanceTypeDialogCtrl financeTypeDialogCtrl = null;

		if (this.mainController instanceof FinanceTypeDialogCtrl) {
			financeTypeDialogCtrl = (FinanceTypeDialogCtrl) this.mainController;
		} else {
			return;
		}

		if (ImplementationConstants.ALLOW_ADDDBSF) {
			if (isOverdraft) {
				setAccountingMandStyle(AccountingEvent.ADDDBSF, false);
			} else {
				setAccountingMandStyle(AccountingEvent.ADDDBSF, true);
			}
		}
		setAccountingMandStyle(AccountingEvent.ADDDBSN, true);
		if (isOverdraft) {
			setAccountingMandStyle(AccountingEvent.ADDDBSP, false);
			setAccountingMandStyle(AccountingEvent.INSTDATE, false);
			setAccountingMandStyle(AccountingEvent.AMZ, false);
			setAccountingMandStyle(AccountingEvent.PROVSN, false);
			setAccountingMandStyle(AccountingEvent.WRITEOFF, false);
			setAccountingMandStyle(AccountingEvent.AMZPD, true);
			setAccountingMandStyle(AccountingEvent.CMTDISB, true);
			setAccountingMandStyle(AccountingEvent.RATCHG, false);
			setAccountingMandStyle(AccountingEvent.SCDCHG, false);
			setAccountingMandStyle(AccountingEvent.EMIHOLIDAY, false);
			setAccountingMandStyle(AccountingEvent.REAGING, false);
			setAccountingMandStyle(AccountingEvent.EARLYPAY, true);
			setAccountingMandStyle(AccountingEvent.REPAY, true);
			setAccountingMandStyle(AccountingEvent.EARLYSTL, false);
			setAccountingMandStyle(AccountingEvent.AMENDMENT, false);
			setAccountingMandStyle(AccountingEvent.SEGMENT, false);
		} else {
			setAccountingMandStyle(AccountingEvent.RATCHG, true);
			setAccountingMandStyle(AccountingEvent.SCDCHG, true);
			setAccountingMandStyle(AccountingEvent.EMIHOLIDAY, true);
			setAccountingMandStyle(AccountingEvent.REAGING, true);
			setAccountingMandStyle(AccountingEvent.EARLYPAY, true);
			setAccountingMandStyle(AccountingEvent.EARLYSTL, true);
			setAccountingMandStyle(AccountingEvent.AMENDMENT, true);
			setAccountingMandStyle(AccountingEvent.SEGMENT, true);
		}

		setAccountingMandStyle(AccountingEvent.CANCELFIN, true);
		setAccountingMandStyle(AccountingEvent.DISBINS, true);

		boolean vasFlag = false;
		if (StringUtils.isNotBlank(financeTypeDialogCtrl.alwdVasProduct.getValue())) {
			vasFlag = true;
		}
		setAccountingMandStyle(AccountingEvent.VAS_ACCRUAL, vasFlag);
		setAccountingMandStyle(AccountingEvent.VAS_FEE, vasFlag);

		if (financeTypeDialogCtrl.alwPlanDeferment.isChecked()) {
			setAccountingMandStyle(AccountingEvent.DEFFRQ, true);
		}

		if (financeTypeDialogCtrl.finIsAlwDifferment.isChecked()) {
			setAccountingMandStyle(AccountingEvent.DEFRPY, true);
		}

		if (financeTypeDialogCtrl.finIsIntCpz.isChecked() || financeTypeDialogCtrl.finGrcIsIntCpz.isChecked()) {
			if (!isCompReadonly) {
				setAccountingMandStyle(AccountingEvent.COMPOUND, true);
			}
		} else {
			setAccountingMandStyle(AccountingEvent.COMPOUND, false);
		}

		logger.debug("leaving");
	}

	public void setAccountingMandStyle(String eventCode, boolean mandatory) {
		if (this.listBoxAccountingDetails.getFellowIfAny(eventCode) != null) {
			ExtendedCombobox exCombobox = (ExtendedCombobox) this.listBoxAccountingDetails.getFellowIfAny(eventCode);

			if (exCombobox.isReadonly()) {
				exCombobox.setMandatoryStyle(false);
			} else {
				exCombobox.setMandatoryStyle(mandatory);

				ExtendedCombobox settlementexCombobox = (ExtendedCombobox) this.listBoxAccountingDetails
						.getFellowIfAny(eventCode + "_S");
				settlementexCombobox.setMandatoryStyle(mandatory);

				ExtendedCombobox npaexCombobox = (ExtendedCombobox) this.listBoxAccountingDetails
						.getFellowIfAny(eventCode + "_N");
				npaexCombobox.setMandatoryStyle(mandatory);

				ExtendedCombobox writeoffexCombobox = (ExtendedCombobox) this.listBoxAccountingDetails
						.getFellowIfAny(eventCode + "_W");
				writeoffexCombobox.setMandatoryStyle(mandatory);
			}
		}
	}

	public List<FinTypeAccounting> onSave() {
		logger.debug("Entering");

		List<FinTypeAccounting> finTypeAccountingList = processAccountingDetails();

		logger.debug("leaving");

		return finTypeAccountingList;
	}

	private List<AccountEngineEvent> getRegularEvents(List<AccountEngineEvent> list) {
		return list.stream()

				.filter(ae -> !ae.getAEEventCode().endsWith("_S"))

				.filter(ae -> !ae.getAEEventCode().endsWith("_N"))

				.filter(ae -> !ae.getAEEventCode().endsWith("_W"))

				.collect(Collectors.toList());
	}

	private boolean isEventExists(String eventCode, List<AccountEngineEvent> list) {
		return list.stream().anyMatch(ae -> ae.getAEEventCode().contains(eventCode));
	}

	public void doFillFinTypeAccountingList(List<FinTypeAccounting> finTypeAccountingList) {
		logger.debug("Entering");

		String categoryCode = AccountingEvent.EVENTCTG_FINANCE;
		if (this.isOverdraft) {
			categoryCode = AccountingEvent.EVENTCTG_OVERDRAFT;
		} else if (consumerDurable) {
			categoryCode = AccountingEvent.EVENTCTG_CD;
		}

		List<AccountEngineEvent> allEvents = finTypeAccountingService.getAccountEngineEvents(categoryCode);

		for (AccountEngineEvent ae : getRegularEvents(allEvents)) {
			FinTypeAccounting finTypeAcc = fetchExistingFinTypeAcc(finTypeAccountingList, ae.getAEEventCode());

			if (finTypeAcc == null) {
				finTypeAcc = getNewFinTypeAccounting();
				finTypeAcc.setEvent(ae.getAEEventCode());
			} else {
				FinTypeAccounting befImage = new FinTypeAccounting();
				BeanUtils.copyProperties(finTypeAcc, befImage);
				finTypeAcc.setBefImage(befImage);
			}

			finTypeAcc.setEventDesc(ae.getAEEventCodeDesc());
			finTypeAcc.setMandatory(ae.isMandatory());

			finTypeAccEventMap.put(ae.getAEEventCode(), finTypeAcc);
		}

		this.listBoxAccountingDetails.getItems().clear();

		List<FinTypeAccounting> faList = finTypeAccEventMap.values().stream().collect(Collectors.toList());

		for (FinTypeAccounting finTypeAcc : faList) {
			Listitem item = new Listitem();

			String eventCode = finTypeAcc.getEvent();
			String eventDesc = finTypeAcc.getEventDesc();

			Listcell lc = new Listcell(eventDesc);
			lc.setStyle("line-height:12px!important;");
			lc.setParent(item);

			/* Regular */
			lc = new Listcell();
			lc.setStyle("line-height:12px!important;");
			lc.appendChild(getExtendedCombobox(allEvents, finTypeAcc));
			lc.setParent(item);

			/* Settlement */
			eventCode = finTypeAcc.getEvent() + "_S";

			FinTypeAccounting settlement = fetchExistingFinTypeAcc(finTypeAccountingList, eventCode);

			if (settlement == null) {
				settlement = getNewFinTypeAccounting();
				settlement.setEvent(eventCode);
			} else {
				FinTypeAccounting befImage = new FinTypeAccounting();
				BeanUtils.copyProperties(settlement, befImage);
				settlement.setBefImage(befImage);
			}
			settlement.setEventDesc(eventDesc);

			finTypeAccEventMap.put(settlement.getEvent(), settlement);

			lc = new Listcell();
			lc.setStyle("line-height:12px!important;");
			lc.appendChild(getExtendedCombobox(allEvents, settlement));
			lc.setParent(item);

			/* NPA */
			eventCode = finTypeAcc.getEvent() + "_N";
			FinTypeAccounting npa = fetchExistingFinTypeAcc(finTypeAccountingList, eventCode);

			if (npa == null) {
				npa = getNewFinTypeAccounting();
				npa.setEvent(eventCode);
			} else {
				FinTypeAccounting befImage = new FinTypeAccounting();
				BeanUtils.copyProperties(npa, befImage);
				npa.setBefImage(befImage);
			}

			npa.setEventDesc(eventDesc);
			finTypeAccEventMap.put(npa.getEvent(), npa);

			lc = new Listcell();
			lc.setStyle("line-height:12px!important;");
			lc.appendChild(getExtendedCombobox(allEvents, npa));
			lc.setParent(item);

			/* Write-off */
			eventCode = finTypeAcc.getEvent() + "_W";
			FinTypeAccounting writeOff = fetchExistingFinTypeAcc(finTypeAccountingList, eventCode);

			if (writeOff == null) {
				writeOff = getNewFinTypeAccounting();
				writeOff.setEvent(eventCode);
			} else {
				FinTypeAccounting befImage = new FinTypeAccounting();
				BeanUtils.copyProperties(writeOff, befImage);
				writeOff.setBefImage(befImage);
			}

			writeOff.setEventDesc(eventDesc);
			finTypeAccEventMap.put(writeOff.getEvent(), writeOff);

			lc = new Listcell();
			lc.setStyle("line-height:12px!important;");
			lc.appendChild(getExtendedCombobox(allEvents, writeOff));
			lc.setParent(item);

			this.listBoxAccountingDetails.appendChild(item);

		}
		logger.debug("Leaving");
	}

	private ExtendedCombobox getExtendedCombobox(List<AccountEngineEvent> list, FinTypeAccounting finTypeAcc) {
		String eventCode = finTypeAcc.getEvent();
		ExtendedCombobox extCombobox = getExtendedCombobox(eventCode);

		extCombobox.setId(eventCode);
		if (isCompReadonly) {
			extCombobox.setMandatoryStyle(false);
		} else {
			extCombobox.setMandatoryStyle(finTypeAcc.isMandatory());
		}
		extCombobox.setReadonly(isCompReadonly);
		extCombobox.setValue(finTypeAcc.getLovDescEventAccountingName());
		// extCombobox.setDescription(finTypeAcc.getLovDescAccountingName());

		if (!isEventExists(eventCode, list)) {
			extCombobox.setValue("", "");
			extCombobox.setButtonDisabled(true);
		}

		return extCombobox;
	}

	private FinTypeAccounting fetchExistingFinTypeAcc(List<FinTypeAccounting> finTypeAccountingList, String eventCode) {
		for (FinTypeAccounting finTypeAcc : finTypeAccountingList) {
			if (StringUtils.equals(finTypeAcc.getEvent(), eventCode)) {
				return finTypeAcc;
			}
		}

		return null;
	}

	private FinTypeAccounting getNewFinTypeAccounting() {
		FinTypeAccounting finTypeAccNew = new FinTypeAccounting();
		finTypeAccNew.setNewRecord(true);
		finTypeAccNew.setFinType(this.finType);
		finTypeAccNew.setModuleId(moduleId);
		boolean isNew = finTypeAccNew.isNewRecord();
		if (isWorkFlowEnabled()) {
			if (StringUtils.isBlank(finTypeAccNew.getRecordType())) {
				finTypeAccNew.setVersion(finTypeAccNew.getVersion() + 1);
				if (isNew) {
					finTypeAccNew.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					finTypeAccNew.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					finTypeAccNew.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				finTypeAccNew.setVersion(1);
				finTypeAccNew.setRecordType(PennantConstants.RCD_ADD);
			}
			if (StringUtils.isBlank(finTypeAccNew.getRecordType())) {
				finTypeAccNew.setVersion(finTypeAccNew.getVersion() + 1);
				finTypeAccNew.setRecordType(PennantConstants.RCD_UPD);
			}
		}
		return finTypeAccNew;
	}

	private ExtendedCombobox getExtendedCombobox(String eventCode) {
		ExtendedCombobox extendedCombobox = new ExtendedCombobox();
		extendedCombobox.setMaxlength(10);
		extendedCombobox.setModuleName("AccountingSet");
		extendedCombobox.setValueColumn("AccountSetCode");
		// extendedCombobox.setDescColumn("AccountSetCodeName");
		extendedCombobox.setValidateColumns(new String[] { "AccountSetCode" });
		extendedCombobox.setTextBoxWidth(100);

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("EventCode", eventCode, Filter.OP_EQUAL);
		filters[1] = new Filter("EntryByInvestment", 0, Filter.OP_EQUAL);

		extendedCombobox.setFilters(filters);

		return extendedCombobox;
	}

	public List<FinTypeAccounting> processAccountingDetails() {
		if (CollectionUtils.isEmpty(this.listBoxAccountingDetails.getItems())) {
			return processWorkflowDetails();
		}

		List<WrongValueException> wve = new ArrayList<WrongValueException>();

		for (Listitem listitem : this.listBoxAccountingDetails.getItems()) {
			List<Listcell> listCells = listitem.getChildren();

			for (Listcell listcell : listCells) {
				Component component = listcell.getFirstChild();

				if (!(component instanceof ExtendedCombobox)) {
					continue;
				}

				ExtendedCombobox extCombobox = (ExtendedCombobox) component;
				String eventCode = extCombobox.getId();

				FinTypeAccounting fa = finTypeAccEventMap.get(eventCode);

				if (validate && extCombobox.isMandatory() && !extCombobox.isReadonly()) {
					extCombobox.setConstraint(new PTStringValidator(fa.getEventDesc(), null, true, true));
					try {
						extCombobox.getValidatedValue();
					} catch (WrongValueException we) {
						wve.add(we);
					}
					extCombobox.setConstraint("");
				}

				if (extCombobox.getObject() == null) {
					if (StringUtils.isEmpty(extCombobox.getValue()) && fa.getAccountSetID() != Long.MIN_VALUE) {
						fa.setAccountSetID(Long.MIN_VALUE);
						fa.setLovDescEventAccountingName("");
						fa.setLovDescAccountingName("");
						fa.setRecordStatus(this.recordStatus.getValue());
						finTypeAccEventMap.put(eventCode, fa);
					}
				} else {
					if (extCombobox.getObject() instanceof String) {
						fa.setAccountSetID(Long.MIN_VALUE);
						fa.setLovDescEventAccountingName("");
						fa.setLovDescAccountingName("");
					} else {
						AccountingSet accountingSet = (AccountingSet) extCombobox.getObject();
						fa.setAccountSetID(accountingSet.getAccountSetid());
						fa.setLovDescEventAccountingName(accountingSet.getAccountSetCode());
						fa.setLovDescAccountingName(accountingSet.getAccountSetCodeName());
					}

					fa.setRecordStatus(this.recordStatus.getValue());
					finTypeAccEventMap.put(eventCode, fa);
				}

			}
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
			}

			this.parentTab.setSelected(true);

			throw new WrongValuesException(wvea);
		}

		return processWorkflowDetails();
	}

	private List<FinTypeAccounting> processWorkflowDetails() {
		List<FinTypeAccounting> finTypeAccList = new ArrayList<>();

		for (FinTypeAccounting finAccounting : finTypeAccEventMap.values()) {
			FinTypeAccounting finTypeAccBefImg = finAccounting.getBefImage();
			if (finTypeAccBefImg == null) {
				if (finAccounting.getAccountSetID() != Long.MIN_VALUE) {
					finTypeAccList.add(finAccounting);
				}
			} else {
				if (finTypeAccBefImg.getAccountSetID() != Long.MIN_VALUE
						&& finAccounting.getAccountSetID() == Long.MIN_VALUE) {
					if (StringUtils.isBlank(finAccounting.getRecordType())) {
						finAccounting.setVersion(finAccounting.getVersion() + 1);
						finAccounting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						finAccounting.setNewRecord(true);
					} else if (StringUtils.trimToEmpty(finAccounting.getRecordType())
							.equals(PennantConstants.RCD_UPD)) {
						finAccounting.setVersion(finAccounting.getVersion() + 1);
						finAccounting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (StringUtils.trimToEmpty(finAccounting.getRecordType())
							.equals(PennantConstants.RECORD_TYPE_UPD)) {
						finAccounting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (finAccounting.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						finAccounting.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					}
				} else if (finTypeAccBefImg.getAccountSetID() == Long.MIN_VALUE
						&& finAccounting.getAccountSetID() != Long.MIN_VALUE) {
					finAccounting.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				} else if (finTypeAccBefImg.getAccountSetID() != Long.MIN_VALUE
						&& finAccounting.getAccountSetID() != Long.MIN_VALUE
						&& finTypeAccBefImg.getAccountSetID() != finAccounting.getAccountSetID()) {
					if (StringUtils.isBlank(finAccounting.getRecordType())) {
						finAccounting.setVersion(finAccounting.getVersion() + 1);
						finAccounting.setRecordType(PennantConstants.RCD_UPD);
					}
				}
				finTypeAccList.add(finAccounting);
			}
		}

		return finTypeAccList;
	}

	public List<FinTypeAccounting> getFinTypeAccountingList() {
		return finTypeAccountingList;
	}

	public void setFinTypeAccountingList(List<FinTypeAccounting> finTypeAccountingList) {
		this.finTypeAccountingList = finTypeAccountingList;
	}

	public Object getMainController() {
		return mainController;
	}

	public void setMainController(Object mainController) {
		this.mainController = mainController;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	@Autowired
	public void setFinTypeAccountingService(FinTypeAccountingService finTypeAccountingService) {
		this.finTypeAccountingService = finTypeAccountingService;
	}

}
