/**
 * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  FinCovenantTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeList.zul file.
 */
public class FinCovenantTypeListCtrl extends GFCBaseCtrl<FinanceDetail> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = Logger.getLogger(FinCovenantTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinCovenantTypeList;
	
	protected Button btnNew_NewFinCovenantType;

	protected Listbox listBoxFinCovenantType;
	
	// For Dynamically calling of this Controller
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private List<FinCovenantType> finCovenantTypesDetailList = new ArrayList<FinCovenantType>();
	private int ccyFormat=0;
	private transient boolean recSave = false;
	private String roleCode = "";
	private boolean isEnquiry = false;
	private transient boolean newFinance;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;
	private String allowedRoles;
	/**
	 * default constructor.<br>
	 */
	public FinCovenantTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinCovenantTypeList";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected CovenantType object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinCovenantTypeList(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinCovenantTypeList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
				//parent.setStyle("overflow:auto;");
			}

		if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				this.window_FinCovenantTypeList.setTitle("");
				setNewFinance(true);
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "FinCovenantTypeList");
			}
			
			if (arguments.containsKey("ccyFormatter")) {
				ccyFormat=Integer.parseInt(arguments.get("ccyFormatter").toString());
			}
			
			if (arguments.containsKey("parentTab")) {
				parentTab = (Tab) arguments.get("parentTab");
			}
			
			if (arguments.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) arguments.get("isEnquiry");
			}
			
			if (arguments.containsKey("financeDetail")) {
				setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
				if (getFinancedetail()!=null) {
					if(getFinancedetail().getCovenantTypeList() != null){
						setFinCovenantTypeDetailList(getFinancedetail().getCovenantTypeList());
					}
				}
			}
			
			if (arguments.containsKey("allowedRoles")) {
				allowedRoles=(String) arguments.get("allowedRoles");
			}

			if(arguments.containsKey("financeMainDialogCtrl")){
				this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
				try {
						financeMainDialogCtrl.getClass().getMethod("setFinCovenantTypeListCtrl", this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.window_FinCovenantTypeList.setTitle("");
			}
			
			doEdit();
			doCheckRights();
			doSetFieldProperties();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		logger.debug("Leaving");
	}
		
	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}
	
	
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinCovenantTypeList",roleCode);
		this.btnNew_NewFinCovenantType.setVisible(getUserWorkspace().isAllowed("FinCovenantTypeList_NewFinCovenantTypeDetail"));
		logger.debug("leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}
	
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
	
		try {
			appendFinBasicDetails();
			doCheckEnquiry();
			doWriteBeanToComponents();

			this.listBoxFinCovenantType.setHeight(borderLayoutHeight - 226 +"px");
			if (parent != null) {
				this.window_FinCovenantTypeList.setHeight(borderLayoutHeight-75+"px");
				parent.appendChild(this.window_FinCovenantTypeList);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param commodityHeader
	 *            
	 */
	public void doWriteBeanToComponents() {
		logger.debug("Entering ");
		
		doFillFinCovenantTypeDetails(getFinCovenantTypeDetailList());
		
		logger.debug("Leaving ");
	}
	
	private void doCheckEnquiry() {
		if(isEnquiry){
			this.btnNew_NewFinCovenantType.setVisible(false);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onCovenantTypeValidation(Event event){
		logger.debug("Entering" + event.toString());

		String userAction = "";
		FinanceDetail finDetail = null;
		Map<String,Object> map = new HashMap<String,Object>();
		if(event.getData() != null){
			map = (Map<String, Object>) event.getData();
		}

		if(map.containsKey("userAction")){
			userAction = (String) map.get("userAction");
		}
		
		if(map.containsKey("financeDetail")){
			finDetail = (FinanceDetail) map.get("financeDetail");
		}
		
		recSave = false;
		if("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction)
				|| "Reject".equalsIgnoreCase(userAction) || "Resubmit".equalsIgnoreCase(userAction)){
			recSave = true;
		}
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if(!recSave){
				FinanceMain main = null;
				if (getFinanceMainDialogCtrl() != null) {
					try {
						if (financeMainDialogCtrl.getClass().getMethod("getFinanceMain") != null) {
							Object object = financeMainDialogCtrl.getClass().getMethod("getFinanceMain").invoke(financeMainDialogCtrl);
							if (object != null) {
								main = (FinanceMain) object;
							}
						}
					} catch (Exception e) {
						logger.error("Exception: ", e);
					}
				}
				if (this.listBoxFinCovenantType.getItems() != null && !this.listBoxFinCovenantType.getItems().isEmpty()) {
					if (main != null && main.getFinAmount() != null) {
						
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve);
		
		if(finDetail !=null){
			finDetail.setCovenantTypeList(finCovenantTypesDetailList);
		}
		logger.debug("Leaving" + event.toString());
	}

	
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");

			if(parentTab != null){
				parentTab.setSelected(true);
			}

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnNew_NewFinCovenantType(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewFinCovenantType);
		
		final FinCovenantType aFinCovenantType = new FinCovenantType();
		aFinCovenantType.setFinReference(financedetail.getFinScheduleData().getFinReference());
		aFinCovenantType.setNewRecord(true);
		aFinCovenantType.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finCovenantTypes", aFinCovenantType);
		map.put("ccyFormatter", ccyFormat);
		map.put("finCovenantTypesListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
		map.put("allowedRoles", allowedRoles);
		map.put("financeDetail", getFinancedetail());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFinCovenantTypeItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewFinCovenantType);

		Listitem listitem = this.listBoxFinCovenantType.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinCovenantType aFinCovenantType = (FinCovenantType) listitem.getAttribute("data");
			if (isDeleteRecord(aFinCovenantType.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			}else{
				aFinCovenantType.setNewRecord(false);

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finCovenantTypes", aFinCovenantType);
				map.put("ccyFormatter", ccyFormat);
				map.put("finCovenantTypesListCtrl", this);
				map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
				map.put("roleCode", roleCode);
				map.put("enqModule", isEnquiry);
				map.put("allowedRoles", allowedRoles);
				map.put("financeDetail", getFinancedetail());

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void doFillFinCovenantTypeDetails(List<FinCovenantType> finAdvancePayDetails) {
		logger.debug("Entering");
		this.listBoxFinCovenantType.getItems().clear();
		setFinCovenantTypeDetailList(finAdvancePayDetails);
		if (finAdvancePayDetails != null && !finAdvancePayDetails.isEmpty()) {
			for (FinCovenantType detail : finAdvancePayDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(detail.getCovenantTypeDesc());
				lc.setParent(item);
				lc = new Listcell(detail.getMandRoleDesc());
				lc.setParent(item);
				
				Checkbox cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwWaiver());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);
				
				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwPostpone());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);
				
				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwOtc());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);
				
				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinCovenantTypeItemDoubleClicked");
				this.listBoxFinCovenantType.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	
	
	
	private boolean isDeleteRecord(String rcdType){
		if(StringUtils.equals(PennantConstants.RECORD_TYPE_CAN,rcdType) || 
				StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, rcdType)){
			return true;
		}
		return false;
	}
	
	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this );
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
		} catch (Exception e) {
			logger.debug(e);
		}
		
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//


	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public List<FinCovenantType> getFinCovenantTypeDetailList() {
		return finCovenantTypesDetailList;
	}
	public void setFinCovenantTypeDetailList(
			List<FinCovenantType> finCovenantTypesDetailList) {
		this.finCovenantTypesDetailList = finCovenantTypesDetailList;
	}

	public boolean isNewFinance() {
		return newFinance;
	}
	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}
	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}
	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

}
