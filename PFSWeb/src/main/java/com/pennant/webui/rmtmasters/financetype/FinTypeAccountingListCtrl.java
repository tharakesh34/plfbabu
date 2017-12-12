/**
Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinTypeAccountingListCtrl.java                                       * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2017    														*
 *                                                                  						*
 * Modified Date    :  21-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2017       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.financetype;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
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
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinTypeAccountingListCtrl  extends GFCBaseCtrl<FinTypeAccounting> {
	
	private static final long serialVersionUID = 4521079241535245640L;

	private static final Logger logger = Logger.getLogger(FinTypeAccountingListCtrl.class);

	protected Window window_FinTypeAccountingList;
	
	private Component parent = null;
	private Tab parentTab = null;
	
	private List<FinTypeAccounting>			finTypeAccountingList	= new ArrayList<FinTypeAccounting>();
	private Map<String,FinTypeAccounting> 	finTypeAccEventMap = new LinkedHashMap<String,FinTypeAccounting>();

	private Listbox							listBoxAccountingDetails;
	
	private String roleCode = "";
	private String finType = "";
	protected boolean isOverdraft = false;
	private boolean isCompReadonly = false;
	private boolean allowRIAInvestment = false;
	private boolean validate = false;
	
	private Object mainController;
	protected int moduleId;
	
	private List<AccountEngineEvent> accEventStaticList = new ArrayList<AccountEngineEvent>();
	
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
	public void onCreate$window_FinTypeAccountingList(Event event) throws Exception {
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
				moduleId =  (int) arguments.get("moduleId");
			}
			
			if (arguments.containsKey("allowRIAInvestment")) {
				this.allowRIAInvestment = (boolean) arguments.get("allowRIAInvestment");
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
				this.isOverdraft =  (Boolean)arguments.get("isOverdraft");
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

	private void doShowDialog() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		logger.debug("Entering");
		
		if (this.isOverdraft) {
			accEventStaticList = PennantAppUtil.getOverdraftAccountingEvents();
		} else {
			accEventStaticList =  PennantAppUtil.getAccountingEvents();
		}
		
		doFillFinTypeAccountingList(this.finTypeAccountingList);
		
		if (parent != null) {
			this.window_FinTypeAccountingList.setHeight(borderLayoutHeight - 75 + "px");
			parent.appendChild(this.window_FinTypeAccountingList);
		}
		
		try {
			getMainController().getClass().getMethod("setFinTypeAccountingListCtrl", this.getClass()).invoke(mainController, this);
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

		if(ImplementationConstants.ALLOW_ADDDBSF) {
			if (isOverdraft) {
				setAccountingMandStyle(AccountEventConstants.ACCEVENT_ADDDBSF, false);
			} else {
				setAccountingMandStyle(AccountEventConstants.ACCEVENT_ADDDBSF, true);
			}
		}
		setAccountingMandStyle(AccountEventConstants.ACCEVENT_ADDDBSN, true);
		if (isOverdraft) {
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_AMZPD, true);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_CMTDISB, true);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_RATCHG, false);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_SCDCHG, false);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_EMIHOLIDAY, false);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_REAGING, false);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_EARLYPAY, false);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_EARLYSTL, false);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_AMENDMENT, false);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_SEGMENT, false);
		} else {
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_RATCHG, true);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_SCDCHG, true);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_EMIHOLIDAY, true);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_REAGING, true);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_EARLYPAY, true);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_EARLYSTL, true);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_AMENDMENT, true);
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_SEGMENT, true);
		}
		
		setAccountingMandStyle(AccountEventConstants.ACCEVENT_CANCELFIN, true);
		setAccountingMandStyle(AccountEventConstants.ACCEVENT_DISBINS, true);
		
		if (financeTypeDialogCtrl.finDepreciationReq.isChecked() && !isOverdraft) {
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_DPRCIATE, true);
		}
		
		boolean vasFlag = false;
		if (StringUtils.isNotBlank(financeTypeDialogCtrl.alwdVasProduct.getValue())) {
			vasFlag = true;
		}
		setAccountingMandStyle(AccountEventConstants.ACCEVENT_VAS_ACCRUAL, vasFlag);
		setAccountingMandStyle(AccountEventConstants.ACCEVENT_VAS_FEE, vasFlag);
		
		if (financeTypeDialogCtrl.alwPlanDeferment.isChecked()) {
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_DEFFRQ, true);
		}
		
		if (financeTypeDialogCtrl.finIsAlwDifferment.isChecked()) {
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_DEFRPY, true);
		}
	
		if (financeTypeDialogCtrl.finIsIntCpz.isChecked() || financeTypeDialogCtrl.finGrcIsIntCpz.isChecked()) {
			if (!isCompReadonly) {
				setAccountingMandStyle(AccountEventConstants.ACCEVENT_COMPOUND, true);
			}
		} else {
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_COMPOUND, false);
		}
		
		if (StringUtils.equals(getComboboxValue(financeTypeDialogCtrl.cbfinProductType),
				FinanceConstants.PRODUCT_ISTISNA)) {
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_PRGCLAIM, true);
		} else {
			setAccountingMandStyle(AccountEventConstants.ACCEVENT_PRGCLAIM, false);
		}

		logger.debug("leaving");
	}

	public void setAccountingMandStyle(String eventCode, boolean mandatory){
		if(this.listBoxAccountingDetails.getFellowIfAny(eventCode) != null){
			ExtendedCombobox exCombobox = (ExtendedCombobox) this.listBoxAccountingDetails.getFellowIfAny(eventCode);
			if(exCombobox.isReadonly()){
				exCombobox.setMandatoryStyle(false);
			}else{
				exCombobox.setMandatoryStyle(mandatory);
			}
		}
	}
	
	public List<FinTypeAccounting> doSave () {
		logger.debug("Entering");
		
		List<FinTypeAccounting> finTypeAccountingList = processAccountingDetails();
		
		logger.debug("leaving");
		
		return finTypeAccountingList;
	}
	
	public void doFillFinTypeAccountingList(List<FinTypeAccounting> finTypeAccountingList) {
		logger.debug("Entering");

		for (AccountEngineEvent accountEngineEvent : accEventStaticList) {
			FinTypeAccounting finTypeAcc = fetchExistingFinTypeAcc(finTypeAccountingList, accountEngineEvent.getAEEventCode());

			if (finTypeAcc == null) {
				FinTypeAccounting finTypeAccNew = getNewFinTypeAccounting();
				finTypeAccNew.setEvent(accountEngineEvent.getAEEventCode());
				finTypeAccNew.setEventDesc(accountEngineEvent.getAEEventCodeDesc());
				finTypeAccNew.setMandatory(accountEngineEvent.isMandatory());
				finTypeAccEventMap.put(accountEngineEvent.getAEEventCode(), finTypeAccNew);
			} else {
				FinTypeAccounting befImage = new FinTypeAccounting();
				BeanUtils.copyProperties(finTypeAcc, befImage);
				finTypeAcc.setBefImage(befImage);
				finTypeAcc.setEventDesc(accountEngineEvent.getAEEventCodeDesc());
				finTypeAcc.setMandatory(accountEngineEvent.isMandatory());
				finTypeAccEventMap.put(accountEngineEvent.getAEEventCode(), finTypeAcc);
			}
		}

		this.listBoxAccountingDetails.getItems().clear();
		Listitem item = null;
		ExtendedCombobox extCombobox = null;
		boolean newRowReq = true;
		int itemCount = 0;
		for (FinTypeAccounting finTypeAcc : finTypeAccEventMap.values()) {
			itemCount++;

			if (newRowReq) {
				item = new Listitem();
			}
			Listcell lc;
			lc = new Listcell(getEventDesc(finTypeAcc.getEvent()));
			lc.setStyle("line-height:12px!important;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("line-height:12px!important;");
			extCombobox = getExtendedCombobox(finTypeAcc.getEvent());
			extCombobox.setId(finTypeAcc.getEvent());
			if (isCompReadonly) {
				extCombobox.setMandatoryStyle(false);
			} else {
				extCombobox.setMandatoryStyle(finTypeAcc.isMandatory());
			}
			extCombobox.setReadonly(isCompReadonly);
			extCombobox.setValue(finTypeAcc.getLovDescEventAccountingName());
			extCombobox.setDescription(finTypeAcc.getLovDescAccountingName());
			lc.appendChild(extCombobox);
			lc.setParent(item);
			if (!newRowReq || itemCount == finTypeAccEventMap.size()) {
				this.listBoxAccountingDetails.appendChild(item);
				newRowReq = true;
			} else {
				newRowReq = false;
			}
		}
		logger.debug("Leaving");
	}
	
	private FinTypeAccounting fetchExistingFinTypeAcc(List<FinTypeAccounting> finTypeAccountingList, String eventCode) {
		for (FinTypeAccounting finTypeAcc : finTypeAccountingList) {
			if (StringUtils.equals(finTypeAcc.getEvent(), eventCode)) {
				return finTypeAcc;
			}
		}

		return null;
	}
	
	private FinTypeAccounting getNewFinTypeAccounting(){
		FinTypeAccounting finTypeAccNew = new FinTypeAccounting();
		finTypeAccNew.setNewRecord(true);
		finTypeAccNew.setFinType(this.finType);
		finTypeAccNew.setModuleId(moduleId);
		boolean isNew = finTypeAccNew.isNew();
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

	private String getEventDesc(String value) {
		for (int i = 0; i < accEventStaticList.size(); i++) {
			if (accEventStaticList.get(i).getAEEventCode().equalsIgnoreCase(value)) {
				return accEventStaticList.get(i).getAEEventCodeDesc();
			}
		}
		return "";
	}
	
	private ExtendedCombobox getExtendedCombobox(String eventCode){	
		ExtendedCombobox extendedCombobox = new ExtendedCombobox();
		extendedCombobox.setMaxlength(8);
		extendedCombobox.setModuleName("AccountingSet");
		extendedCombobox.setValueColumn("AccountSetCode");
		extendedCombobox.setDescColumn("AccountSetCodeName");
		extendedCombobox.setValidateColumns(new String[] { "AccountSetCode" });
		extendedCombobox.setTextBoxWidth(80);
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("EventCode", eventCode, Filter.OP_EQUAL);
		filters[1] = new Filter("EntryByInvestment", this.allowRIAInvestment ? 1 : 0, Filter.OP_EQUAL);
		extendedCombobox.setFilters(filters);
		return extendedCombobox;
	}

	public List<FinTypeAccounting> processAccountingDetails() {
		if (this.listBoxAccountingDetails.getItems() != null && !this.listBoxAccountingDetails.getItems().isEmpty()) {
			ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
			
			for (Listitem listitem : this.listBoxAccountingDetails.getItems()) {
				List<Listcell> listCells = listitem.getChildren();
				boolean isEventCode = true;
				
				for (Listcell listcell : listCells) {
					if (!isEventCode) {
						ExtendedCombobox extCombobox = (ExtendedCombobox) listcell.getFirstChild();
						String eventCode = extCombobox.getId();
						FinTypeAccounting finAccounting = finTypeAccEventMap.get(eventCode);
						if (validate && extCombobox.isMandatory() && !extCombobox.isReadonly()) {
							extCombobox.setConstraint(new PTStringValidator(finAccounting.getEventDesc(), null, true, true));
							try {
								extCombobox.getValidatedValue();
							} catch (WrongValueException we) {
								wve.add(we);
							}
							extCombobox.setConstraint("");
						}

						if (extCombobox.getObject() == null) {
							if (StringUtils.isEmpty(extCombobox.getValue())
									&& finAccounting.getAccountSetID() != Long.MIN_VALUE) {
								finAccounting.setAccountSetID(Long.MIN_VALUE);
								finAccounting.setLovDescEventAccountingName("");
								finAccounting.setLovDescAccountingName("");
								finAccounting.setRecordStatus(this.recordStatus.getValue());
								finTypeAccEventMap.put(eventCode, finAccounting);
							}
						} else {
							if (extCombobox.getObject() instanceof String) {
								finAccounting.setAccountSetID(Long.MIN_VALUE);
								finAccounting.setLovDescEventAccountingName("");
								finAccounting.setLovDescAccountingName("");
							} else {
								AccountingSet accountingSet = (AccountingSet) extCombobox.getObject();
								finAccounting.setAccountSetID(accountingSet.getAccountSetid());
								finAccounting.setLovDescEventAccountingName(accountingSet.getAccountSetCode());
								finAccounting.setLovDescAccountingName(accountingSet.getAccountSetCodeName());
							}
							finAccounting.setRecordStatus(this.recordStatus.getValue());
							finTypeAccEventMap.put(eventCode, finAccounting);
						}
					}
					isEventCode = !isEventCode;
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
		}
		
		return processWorkflowDetails();
	}


	private List<FinTypeAccounting> processWorkflowDetails(){
		List<FinTypeAccounting> finTypeAccList = new ArrayList<FinTypeAccounting>();
		for (FinTypeAccounting finAccounting : finTypeAccEventMap.values()) {
			FinTypeAccounting finTypeAccBefImg = finAccounting.getBefImage();
			if(finTypeAccBefImg == null){
				if(finAccounting.getAccountSetID() != Long.MIN_VALUE){
					finTypeAccList.add(finAccounting);
				}
			}else{
				if(finTypeAccBefImg.getAccountSetID() != Long.MIN_VALUE && finAccounting.getAccountSetID() == Long.MIN_VALUE){
					if (StringUtils.isBlank(finAccounting.getRecordType())) {
						finAccounting.setVersion(finAccounting.getVersion() + 1);
						finAccounting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						finAccounting.setNewRecord(true);
					} else if (StringUtils.trimToEmpty(finAccounting.getRecordType()).equals(PennantConstants.RCD_UPD)) {
						finAccounting.setVersion(finAccounting.getVersion() + 1);
						finAccounting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					}else if (StringUtils.trimToEmpty(finAccounting.getRecordType()).equals(PennantConstants.RECORD_TYPE_UPD)) {
						finAccounting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					}else if (finAccounting.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						finAccounting.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					}
				}else if(finTypeAccBefImg.getAccountSetID() == Long.MIN_VALUE &&
						finAccounting.getAccountSetID() != Long.MIN_VALUE){
					finAccounting.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}else if(finTypeAccBefImg.getAccountSetID() != Long.MIN_VALUE && 
						finAccounting.getAccountSetID() != Long.MIN_VALUE && 
						finTypeAccBefImg.getAccountSetID() != finAccounting.getAccountSetID()){
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
	
	public void setRIAAccountingProps(String eventCode, boolean allowRIAInvestment){
		if(this.listBoxAccountingDetails.getFellowIfAny(eventCode) != null){
			Filter[] filters = new Filter[2];
			filters[0] = new Filter("EventCode", eventCode, Filter.OP_EQUAL);
			filters[1] = new Filter("EntryByInvestment", allowRIAInvestment ? 1 : 0, Filter.OP_EQUAL);
			ExtendedCombobox extCombobox = (ExtendedCombobox) this.listBoxAccountingDetails.getFellowIfAny(eventCode);
			extCombobox.setValue("", "");
			extCombobox.setObject("");
			extCombobox.setFilters(filters);
		}
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

	public boolean isAllowRIAInvestment() {
		return allowRIAInvestment;
	}

	public void setAllowRIAInvestment(boolean allowRIAInvestment) {
		this.allowRIAInvestment = allowRIAInvestment;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}
}
