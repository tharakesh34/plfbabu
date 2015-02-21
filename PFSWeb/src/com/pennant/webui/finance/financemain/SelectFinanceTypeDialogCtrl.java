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
 * FileName    		:  FinanceMainQDEDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-11-2011    														*
 *                                                                  						*
 * Modified Date    :  16-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-11-2011       Pennant	                 0.1                                            * 
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.impl.PFFCustomerPreparation;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceMain/ SelectFinanceTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SelectFinanceTypeDialogCtrl extends GFCBaseCtrl implements Serializable{


	private static final long serialVersionUID = 8556168885363682933L;
	private final static Logger logger = Logger.getLogger(SelectFinanceTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       			window_SelectFinanceTypeDialog;            // autoWired
	protected ExtendedCombobox      finType;                                   // autoWired
	protected ExtendedCombobox      wIfFinaceRef;                              // autoWired
	protected ExtendedCombobox      promotionCode;
	protected Button       			btnProceed;                                // autoWired
	protected Textbox				custCIF;
	protected Radio					newCust;
	protected Radio					existingCust;
	protected Row					customerRow;
	protected Row					row_selectCustomer;
	
	protected FinanceMainListCtrl               financeMainListCtrl;  //over handed parameter
	protected transient FinanceWorkFlow         financeWorkFlow;
	private transient   WorkFlowDetails         workFlowDetails=null;
	private FinanceDetail financeDetail =null;
	private FinanceType financeType =null;
	private List<String> userRoleCodeList = new ArrayList<String>();
	private String loanType = "";
	
	private transient 	FinanceTypeService      financeTypeService;
	private transient   FinanceWorkFlowService  financeWorkFlowService;
	private transient   FinanceDetailService    financeDetailService;   
	private transient   CustomerDetailsService  customerDetailsService;   
	private transient 	PFFCustomerPreparation 	pffCustomerPreparation;
	private transient StepPolicyService stepPolicyService;

    private String menuItemRightName= null;	
    
	/**
	 * default constructor.<br>
	 */
	public SelectFinanceTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //


	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMain object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectFinanceTypeDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(this.financeDetail, befImage);
			this.financeDetail.setBefImage(befImage);

			setFinanceDetail(this.financeDetail);
		} else {
			setFinanceDetail(null);
		}
		if (args.containsKey("financeMainListCtrl")) {
			this.financeMainListCtrl = (FinanceMainListCtrl) args.get("financeMainListCtrl");
			setFinanceMainListCtrl(this.financeMainListCtrl);
		} else {
			setFinanceMainListCtrl(null);;
		}
		
		if (args.containsKey("loanType")) {
			this.loanType = (String) args.get("loanType");
		}
		if (args.containsKey("menuItemRightName")) {
			this.menuItemRightName = (String) args.get("menuItemRightName");
		}
		
		if (args.containsKey("role")) {
			userRoleCodeList = (ArrayList<String>) args.get("role");
		}
		doSetFieldProperties();
		showSelectFinanceTypeDialog();
		logger.debug("Leaving " + event.toString());
	}

	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceWorkFlow");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("LovDescFinTypeName");
		this.finType.setValidateColumns(new String[] { "FinType" });
		Filter[] filters = new Filter[3];
		filters[0]= new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		filters[1]= new Filter("WorkFlowType", "TSR_FIN_PROCESS", Filter.OP_NOT_EQUAL);
		filters[2]= Filter.in("lovDescFirstTaskOwner", userRoleCodeList);
		this.finType.setFilters(filters);
		
		this.promotionCode.setMaxlength(8);
		this.promotionCode.setModuleName("PromotionWorkFlow");
		this.promotionCode.setValueColumn("FinType");
		this.promotionCode.setDescColumn("FinTypeDesc");
		this.promotionCode.setValidateColumns(new String[]{"FinType"});
		this.promotionCode.setReadonly(true);
		
		this.wIfFinaceRef.setModuleName("WIFFinanceMain");
		this.wIfFinaceRef.setValueColumn("FinReference");
		this.wIfFinaceRef.setDescColumn("FinAmount");
		this.wIfFinaceRef.setValidateColumns(new String[] { "FinReference" });
		this.wIfFinaceRef.setReadonly(true);
		this.wIfFinaceRef.setTextBoxWidth(120);
  
		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * When user clicks on button "SearchFinType" button
	 * @param event
	 */
	
	public void onFulfill$finType(Event event){
		logger.debug("Entering " + event.toString());
		Object dataObject = this.finType.getObject();
		if (dataObject instanceof String){
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
			CheckScreenCode("");
		}else{
			FinanceWorkFlow details= (FinanceWorkFlow) dataObject;
			/*Set FinanceWorkFloe object*/
			setFinanceWorkFlow(details);
			if (details != null) {
				this.loanType = details.getLovDescProductCodeName();
				this.finType.setValue(details.getFinType());
				this.finType.setDescription(details.getLovDescFinTypeName());
				CheckScreenCode(details.getScreenCode());
				this.promotionCode.setReadonly(false);
			}
		}
		this.promotionCode.setValue("","");
		this.wIfFinaceRef.setValue("");
		this.promotionCode.setReadonly(true);
		this.wIfFinaceRef.setReadonly(true);
		
		Filter[] filters1;
		if(!this.finType.getValue().trim().equals("")){
			if(!StringUtils.trimToEmpty(this.loanType).equals("")){
				filters1 = new Filter[4] ;
			}else{
				filters1 = new Filter[3] ;
			}
			filters1[0]= new Filter("LovDescProductName", this.finType.getValue(), Filter.OP_EQUAL);
			filters1[1]= new Filter("FinIsActive", 1, Filter.OP_EQUAL);
			filters1[2]= Filter.in("lovDescFirstTaskOwner", userRoleCodeList);
			if(!StringUtils.trimToEmpty(this.loanType).equals("")){
				filters1[3]= new Filter("lovDescProductCodeName", this.loanType, Filter.OP_EQUAL);
			}
			this.promotionCode.setFilters(filters1);
			this.promotionCode.setReadonly(false);
		}
		
		Filter[] filters;
		if(!this.finType.getValue().trim().equals("")){
			if(!StringUtils.trimToEmpty(this.loanType).equals("")){
				filters = new Filter[3] ;
			}else{
				filters = new Filter[2] ;
			}
			filters[0]= new Filter("FinType", this.finType.getValue(), Filter.OP_EQUAL);
			filters[1]= Filter.in("lovDescFirstTaskOwner", userRoleCodeList);
			if(!StringUtils.trimToEmpty(this.loanType).equals("")){
				filters[2]= new Filter("lovDescProductCodeName", this.loanType, Filter.OP_EQUAL);
			}
			this.wIfFinaceRef.setFilters(filters);
			this.wIfFinaceRef.setReadonly(false);
		}
		logger.debug("Leaving " + event.toString());
	}
	
	public void onFulfill$promotionCode(Event event){
		logger.debug("Entering " + event.toString());
		
		Object dataObject = this.promotionCode.getObject();
		if (dataObject instanceof String){
			this.promotionCode.setValue(dataObject.toString());
			this.promotionCode.setDescription("");
			CheckScreenCode("");
			this.wIfFinaceRef.setReadonly(false);
		}else{
			FinanceWorkFlow details= (FinanceWorkFlow) dataObject;
			/*Set FinanceWorkFloe object*/
			setFinanceWorkFlow(details);
			if (details != null) {
				
				this.loanType = details.getLovDescProductCodeName();
				this.promotionCode.setValue(details.getFinType());
				this.promotionCode.setDescription(details.getLovDescFinTypeName());
				CheckScreenCode(details.getScreenCode());
				this.wIfFinaceRef.setReadonly(true);
				this.wIfFinaceRef.setValue("");
			}
		}
		
		this.wIfFinaceRef.setValue("");
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Method for Checking Screen Code Object to avail Customer 
	 */
	private void CheckScreenCode(String screenCode){
		
		if(screenCode.equals("") || screenCode.equals("DDE")){
			this.row_selectCustomer.setVisible(true);
			if(this.existingCust.isChecked()){
				this.customerRow.setVisible(true);
				this.custCIF.setDisabled(false);
			}else{
				this.newCust.setSelected(true);
				this.customerRow.setVisible(false);
				this.custCIF.setDisabled(true);
			}
		}else if(screenCode.equals("QDE")){
			this.row_selectCustomer.setVisible(false);
			this.existingCust.setSelected(true);
			this.customerRow.setVisible(false);
			this.custCIF.setDisabled(true);
			this.custCIF.setValue("");
		}
	}
	
	/**
	 * When user clicks on button "btnSearchWIFFinaceRef" button
	 * @param event
	 */
	public void onFulfill$wIfFinaceRef(Event event) {
		logger.debug("Entering " + event.toString());
		
		Object dataObject = this.wIfFinaceRef.getObject();
		if (dataObject instanceof String){
			this.wIfFinaceRef.setValue(dataObject.toString());
			this.wIfFinaceRef.setDescription("");
			this.promotionCode.setReadonly(false);
		}else{
			FinanceMain details= (FinanceMain) dataObject;
			if (details != null) {
				
				this.loanType = details.getLovDescProductCodeName();
				this.wIfFinaceRef.setValue(details.getFinReference(),"");
				FinanceWorkFlow financeWorkFlow=getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(details.getFinType());
				setFinanceWorkFlow(financeWorkFlow);
				CheckScreenCode(financeWorkFlow.getScreenCode());
				this.promotionCode.setReadonly(true);
				this.promotionCode.setValue("", "");
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * When user clicks on button "btnProceed" button
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		doFieldValidation();
		this.window_SelectFinanceTypeDialog.onClose();
		
		if(!this.wIfFinaceRef.getValue().trim().equals("")){
			financeDetail = getFinanceDetailService().getFinanceDetailById(this.wIfFinaceRef.getValue().trim(), true,"",false);
			financeDetail.getFinScheduleData().getFinanceMain().setCurDisbursementAmt(
					financeDetail.getFinScheduleData().getFinanceMain().getFinAmount());
			financeDetail.getFinScheduleData().getFinanceMain().setNewRecord(true);
			financeDetail.getFinScheduleData().getFinanceMain().setRecordType("");
			financeDetail.getFinScheduleData().getFinanceMain().setVersion(0);
			
			//overdue Penalty Details
			if(financeDetail.getFinScheduleData().getFinODPenaltyRate() == null){
				financeDetail.getFinScheduleData().setFinODPenaltyRate(new FinODPenaltyRate());
			}
			
			financeType = financeDetail.getFinScheduleData().getFinanceType();
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setApplyODPenalty(financeType.isApplyODPenalty());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODIncGrcDays(financeType.isODIncGrcDays());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODChargeCalOn(financeType.getODChargeCalOn());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODGraceDays(financeType.getODGraceDays());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODChargeType(financeType.getODChargeType());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODChargeAmtOrPerc(financeType.getODChargeAmtOrPerc());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODAllowWaiver(financeType.isODAllowWaiver());
			financeDetail.getFinScheduleData().getFinODPenaltyRate().setODMaxWaiverPerc(financeType.getODMaxWaiverPerc());

		}else{
			if(!StringUtils.trimToEmpty(this.finType.getValue()).equals("")){
				financeType = getFinanceTypeService().getApprovedFinanceTypeById(this.finType.getValue().trim());
			} else if(!StringUtils.trimToEmpty(this.promotionCode.getValue()).equals("")){
				financeType = getFinanceTypeService().getApprovedFinanceTypeById(this.promotionCode.getValue().trim());
			}
			financeDetail.getFinScheduleData().setFinanceMain(new FinanceMain(), financeType);
			financeDetail.getFinScheduleData().setFinanceType(financeType);
			
			//Step Policy Details
			if(financeType.isStepFinance()){
				List<StepPolicyDetail> stepPolicyList = getStepPolicyService().getStepPolicyDetailsById(financeType.getDftStepPolicy());
				this.financeDetail.getFinScheduleData().resetStepPolicyDetails(stepPolicyList);
			}
		}
		
		try {
			//Fetch & set Default statuses f
			if (financeDetail.getFinScheduleData().getFinanceMain() != null) {
				financeDetail.getFinScheduleData().getFinanceMain().setFinStsReason(PennantConstants.FINSTSRSN_SYSTEM);
				financeDetail.getFinScheduleData().getFinanceMain().setFinStatus(getFinanceDetailService().getCustStatusByMinDueDays());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		
		//Workflow Details Setup
		
		if(getFinanceWorkFlow()!=null){
			workFlowDetails = WorkFlowUtil.getDetailsByType(getFinanceWorkFlow().getWorkFlowType());
		}
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
			financeDetail.setWorkflowId(0);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
			financeDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
			financeDetail.getFinScheduleData().getFinanceMain().setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		//Fetching Finance Reference Detail
		financeDetail = getFinanceDetailService().getFinanceReferenceDetails(financeDetail, 
				getFinanceWorkFlow().getLovDescFirstTaskOwner(),getFinanceWorkFlow().getScreenCode(),"");
		financeDetail.setNewRecord(true);

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(getFinanceWorkFlow()!=null){
			
			//Customer Data Fetching
			if (!getFinanceWorkFlow().getScreenCode().trim().equals("QDE")) {
				financeDetail.setCustomerDetails(fetchCustomerData());
			}
			
			StringBuilder fileLocaation = new StringBuilder("/WEB-INF/pages/Finance/FinanceMain/");
			/*
			 * if screen code is quick data entry (QDE) navigate to QDE screen
			 * otherwise navigate to Detail data entry screen
			 */
			if (getFinanceWorkFlow().getScreenCode().trim().equals("QDE")) {
				fileLocaation.append("FinanceMainQDEDialog.zul");
			} else {
				String productType = StringUtils.trimToEmpty(this.loanType);

				if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_IJARAH)) {
					fileLocaation.append("IjarahFinanceMainDialog.zul");
				} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_ISTISNA)) {
					fileLocaation.append("IstisnaFinanceMainDialog.zul");
				} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_MUDARABA)) {
					fileLocaation.append("MudarabaFinanceMainDialog.zul");
				} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_MURABAHA)) {
					fileLocaation.append("MurabahaFinanceMainDialog.zul");
				} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_MUSHARAKA)) {
					fileLocaation.append("MusharakFinanceMainDialog.zul");
				} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_TAWARRUQ)) {
					fileLocaation.append("TawarruqFinanceMainDialog.zul");
				} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_SUKUK)) {
					fileLocaation.append("SukukFinanceMainDialog.zul");
				} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_SUKUKNRM)) {
					fileLocaation.append("SukuknrmFinanceMainDialog.zul");
				} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_ISTNORM)) {
					fileLocaation.append("IstnormFinanceMainDialog.zul"); 
				} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_CONVENTIONAL)) {
					fileLocaation.append("ConvFinanceMainDialog.zul"); 
				} else {
					PTMessageUtils.showErrorMessage(Labels.getLabel("message.error.productNotFound", 
							new String[]{productType}));	
					return;
				}
			}

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeDetail", financeDetail);
			map.put("financeMainListCtrl", 	this.financeMainListCtrl);
			map.put("financeType", financeType);
			map.put("menuItemRightName", menuItemRightName);
			
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(fileLocaation.toString(),null,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}else{
			logger.error("work flow not found");
			PTMessageUtils.showErrorMessage(Labels.getLabel("Workflow_Not_Found")+getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType());
		}

		logger.debug("Leaving " + event.toString());
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ GUI Process++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showSelectFinanceTypeDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SelectFinanceTypeDialog.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doFieldValidation() {
		logger.debug("Entering ");
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if(StringUtils.trimToEmpty(this.finType.getValue()).equals("")){
				throw new WrongValueException(this.finType,Labels.getLabel("CHECK_NO_EMPTY"
						,new String[]{Labels.getLabel("label_SelectFinanceTypeDialog_FinType.value")})); 
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}
		
		try {
			if (!getFinanceWorkFlow().getScreenCode().trim().equals("QDE")) {
				if (this.existingCust.isChecked()){
					if (this.custCIF.getValue().equals("")) {
						throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_SelectFinanceTypeDialog_CustCIF.value") }));
					}
				}
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}
		if(wve.size() > 0){
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	
		logger.debug("Leaving ");
	}
	
	public void onCheck$existingCust(Event event){
		logger.debug("Entering" + event.toString());
		this.custCIF.setDisabled(false);
		this.customerRow.setVisible(true);
		logger.debug("Leaving" + event.toString());
	}
	
	public void onCheck$newCust(Event event){
		logger.debug("Entering" + event.toString());
		this.custCIF.setValue("");
		this.custCIF.setDisabled(true);
		this.customerRow.setVisible(false);
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public CustomerDetails fetchCustomerData() {
		logger.debug("Entering");
		
		CustomerDetails customerDetails = new CustomerDetails();
		// Get the data of Customer from Core Banking Customer
		try {
			this.custCIF.setConstraint("");
			this.custCIF.setErrorMessage("");
			this.custCIF.clearErrorMessage();
			String cif = StringUtils.trimToEmpty(this.custCIF.getValue());
			//If  customer exist is checked 
			if (this.existingCust.isChecked()){
				Customer customer = null;
				if (cif.equals("")) {
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel("label_CustomerDialog_CoreCustID.value") }));
				}else{
					
					//check Customer Data in LOCAL PFF system
					customer = getCustomerDetailsService().getCheckCustomerByCIF(cif);
				}
				
				//Interface Core Banking System call
				if (customer == null) {
					customerDetails.setNewRecord(true);
					customerDetails = getPffCustomerPreparation().getCustomerByInterface(cif, "");
					if (customerDetails == null) {
						throw new CustomerNotFoundException();
					}
				}
				
				if (customer != null) {
					customerDetails = getCustomerDetailsService().getCustomerById(customer.getId());
				}
				
			}else if (this.newCust.isChecked()){
				customerDetails = getNewCustomerDetail();
			}
			
		} catch (CustomerNotFoundException e) {
			logger.error(e);
			MultiLineMessageBox.show(Labels.getLabel("Cust_NotFound_NewCustomer"), Labels.getLabel("message.Information"), 
					MultiLineMessageBox.OK, MultiLineMessageBox.INFORMATION);
			
			customerDetails = getNewCustomerDetail();
		}
		logger.debug("Leaving");
		return customerDetails;
	}

	
	private CustomerDetails getNewCustomerDetail(){
		
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setNewRecord(true);
		Customer customer = new Customer();
		
		customer.setLovDescCustCtgType(PennantConstants.CUST_CAT_INDIVIDUAL);
		customer.setCustCtgCode(PennantConstants.PFF_CUSTCTG_INDIV);
		customer.setLovDescCustCtgCodeName(PennantConstants.PFF_CUSTCTG_INDIV);
		customer.setCustCIF(getCustomerDetailsService().getNewProspectCustomerCIF());
		customer.setCustCRCPR("");
		customer.setCustCOB(SystemParameterDetails.getSystemParameterValue("CURR_SYSTEM_COUNTRY").toString());
		customer.setCustBaseCcy(SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").toString());
		customer.setCustLng(SystemParameterDetails.getSystemParameterValue("APP_LNG").toString());
		customer.setCustParentCountry(SystemParameterDetails.getSystemParameterValue("APP_DFT_NATION").toString());
		customer.setCustResdCountry(SystemParameterDetails.getSystemParameterValue("CURR_SYSTEM_COUNTRY").toString());
		customer.setCustRiskCountry(SystemParameterDetails.getSystemParameterValue("APP_DFT_NATION").toString());
		customer.setCustNationality(SystemParameterDetails.getSystemParameterValue("APP_DFT_NATION").toString());
		
		CustEmployeeDetail detail = new CustEmployeeDetail();
		detail.setNewRecord(true);
		customerDetails.setCustEmployeeDetail(detail);
		
		customerDetails.setCustomer(customer);
		return customerDetails;
		
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Getters and Setters ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public FinanceMainListCtrl getFinanceMainListCtrl() {
		return financeMainListCtrl;
	}
	public void setFinanceMainListCtrl(FinanceMainListCtrl financeMainListCtrl) {
		this.financeMainListCtrl = financeMainListCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}
	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}
	
	public FinanceWorkFlow getFinanceWorkFlow() {
		return financeWorkFlow;
	}
	public void setFinanceWorkFlow(FinanceWorkFlow financeWorkFlow) {
		this.financeWorkFlow = financeWorkFlow;
	}
	
	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}
	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public StepPolicyService getStepPolicyService() {
		return stepPolicyService;
	}
	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public PFFCustomerPreparation getPffCustomerPreparation() {
		return pffCustomerPreparation;
	}

	public void setPffCustomerPreparation(PFFCustomerPreparation pffCustomerPreparation) {
		this.pffCustomerPreparation = pffCustomerPreparation;
	}
}
