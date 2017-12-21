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
 * FileName    		:  FinAssetEvaluationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAssetEvaluation;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.finance.FinAssetEvaluationService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/FinAssetEvaluation/finAssetEvaluationDialog.zul file.
 */
public class FinAssetEvaluationDialogCtrl extends GFCBaseCtrl<FinAssetEvaluation> {
	private static final long serialVersionUID = 3141943554064485540L;
	private static final Logger logger = Logger.getLogger(FinAssetEvaluationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_FinAssetEvaluationDialog; // autowired
	protected Groupbox 		gb_basicDetails; 			     // autowired
	protected Groupbox 		gp_REUEvaluation; 			     // autowired
	protected Textbox 		finReference; 				     // autowired
	protected Textbox 		custRepreName; 		             // autowired
	protected Textbox 		remarks; 		            	 // autowired
	protected Textbox 		panelFirm; 			             // autowired
	protected Textbox 		reuReference; 			         // autowired
	protected Textbox    	propertyDesc; 		             // autowired
	protected Combobox 		status; 	                     // autowired
	protected Textbox 		valuationComments; 			     // autowired
	protected CurrencyBox 	totalRevenue; 			         // autowired
	protected CurrencyBox 	marketValueAED; 			     // autowired
	protected CurrencyBox 	valuerFee; 			             // autowired
	protected CurrencyBox 	customerFee; 			         // autowired
	protected CurrencyBox 	expRentalIncome; 			     // autowired
	protected Combobox 		typeofValuation; 	             // autowired
	protected ExtendedCombobox	vendorValuer; 	                 // autowired
	protected Combobox 		propertyStatus; 	             // autowired
	protected Combobox 		reuDecision; 	                 // autowired
	protected Checkbox      custAwareVisit; 	             // autowired
	protected Checkbox      tenantAwareVisit; 	             // autowired
	protected Checkbox      propIsRented; 	                 // autowired
	protected Checkbox      illegalDivAlteration; 	         // autowired
	protected Checkbox      nocReqDevMunicipality; 	         // autowired
	protected Decimalbox    unitVillaSize;                   // autowired
	protected Checkbox      leased; 	                     // autowired
	protected Decimalbox    percWorkCompletion; 	         // autowired
	protected Textbox 		contactNumCountryCode; 		     // autowired
	protected Textbox 		contactNumAreaCode; 		     // autowired
	protected Textbox 		contactPhoneNumber; 		     // autowired
	protected Datebox       vendorInstructedDate; 			 // autowired
	protected Datebox       reportDeliveredDate; 			 // autowired
	protected Datebox       inspectionDate; 				 // autowired
	protected Datebox       finalReportDate; 				 // autowired
	protected Datebox       valuationDate; 					 // autowired
	
	// not auto wired vars
	private FinAssetEvaluation finAssetEvaluation; 							 // overhanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient FinAssetEvaluationService finAssetEvaluationService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	//For Dynamically calling of this Controller
	private Div toolbar;
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private Grid grid_basicDetails;
	private Grid grid_Reference2;

	private transient boolean recSave = false;
	private transient boolean   newFinance;
	public transient int   ccyFormatter = 0;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;

	/**
	 * default constructor.<br>
	 */
	public FinAssetEvaluationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinAssetEvaluationDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinAssetEvaluation object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinAssetEvaluationDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinAssetEvaluationDialog);

		if(event.getTarget().getParent() != null){
			parent = event.getTarget().getParent();
		}

		// READ OVERHANDED params !
		if (arguments.containsKey("finAssetEvaluation")) {
			this.finAssetEvaluation = (FinAssetEvaluation) arguments.get("finAssetEvaluation");
			FinAssetEvaluation befImage = new FinAssetEvaluation();
			BeanUtils.copyProperties(this.finAssetEvaluation, befImage);
			this.finAssetEvaluation.setBefImage(befImage);
			setFinAssetEvaluation(this.finAssetEvaluation);
		} else {
			setFinAssetEvaluation(null);
		}

		if (arguments.containsKey("financeDetail")) {
			setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
			if (getFinancedetail()!=null) {
				setFinAssetEvaluation(getFinancedetail().getFinAssetEvaluation());
			}
		}
		
		if(arguments.containsKey("financeMainDialogCtrl")){
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
			try {
					financeMainDialogCtrl.getClass().getMethod("setFinAssetEvaluationDialogCtrl", this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
			setNewFinance(true);
			this.window_FinAssetEvaluationDialog.setTitle("");
		}

		if(arguments.containsKey("roleCode")){
			setRole((String) arguments.get("roleCode"));
			getUserWorkspace().allocateRoleAuthorities(getRole(), "FinAssetEvaluationDialog");
		}

		if(arguments.containsKey("ccyFormatter")){
			this.ccyFormatter = (Integer)arguments.get("ccyFormatter");
		}		
		
		if (arguments.containsKey("parentTab")) {
			parentTab = (Tab) arguments.get("parentTab");
		}
		
		if (isWorkFlowEnabled() && !isNewFinance()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "FinAssetEvaluationDialog");
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinAssetEvaluation());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.custRepreName.setWidth("171px");
		this.totalRevenue.setWidth("171px");
		this.marketValueAED.setWidth("171px");
		this.valuerFee.setWidth("171px");
		this.customerFee.setWidth("171px");
		this.expRentalIncome.setWidth("171px");
		this.remarks.setWidth("270px");
		this.panelFirm.setWidth("171px");
		this.propertyDesc.setWidth("270px");
		this.reuReference.setWidth("210px");
		this.valuationComments.setWidth("270px");
		this.status.setWidth("171px");
		this.typeofValuation.setWidth("171px");
		this.propertyStatus.setWidth("171px");
		this.reuDecision.setWidth("171px");
		
		this.vendorValuer.setInputAllowed(false);
		this.vendorValuer.setDisplayStyle(3);
		this.vendorValuer.setModuleName("VendorValuator");
		this.vendorValuer.setValueColumn("FieldCodeValue");
		this.vendorValuer.setDescColumn("ValueDesc");
		this.vendorValuer.setValidateColumns(new String[] { "FieldCodeValue" });
		this.vendorValuer.setTextBoxWidth(151);
		
		this.contactNumCountryCode.setMaxlength(3);
		this.contactNumCountryCode.setWidth("50px");
		this.contactNumAreaCode.setMaxlength(3);
		this.contactNumAreaCode.setWidth("50px");
		this.contactPhoneNumber.setMaxlength(8);
		this.contactPhoneNumber.setWidth("100px");
		
		this.custRepreName.setMaxlength(200);
		this.remarks.setMaxlength(500);
		this.panelFirm.setMaxlength(100);
		this.percWorkCompletion.setMaxlength(6);
		this.propertyDesc.setMaxlength(500);
		this.reuReference.setMaxlength(50);
		this.valuationComments.setMaxlength(500);
	
		this.totalRevenue.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.totalRevenue.setScale(ccyFormatter);
		this.totalRevenue.setTextBoxWidth(171);
	
		this.marketValueAED.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.marketValueAED.setScale(ccyFormatter);
		this.marketValueAED.setTextBoxWidth(171);
		
		this.valuerFee.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.valuerFee.setScale(ccyFormatter);
		this.valuerFee.setTextBoxWidth(171);
		
		this.customerFee.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.customerFee.setScale(ccyFormatter);
		this.customerFee.setTextBoxWidth(171);
		
		this.expRentalIncome.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.expRentalIncome.setScale(ccyFormatter);
		this.expRentalIncome.setTextBoxWidth(171);
		
		this.vendorInstructedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.reportDeliveredDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.inspectionDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finalReportDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valuationDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("FinAssetEvaluationDialog", getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinAssetEvaluationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinAssetEvaluationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinAssetEvaluationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinAssetEvaluationDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}


	public void doSave_FinAssetEvaluation(FinanceDetail financeDetail,Tab etihadTab,boolean recSave) throws InterruptedException{
		logger.debug("Entering");

		doClearMessage();
		if(!recSave){
			doSetValidation();	
			doSetLOVValidation();
		}
		FinAssetEvaluation finAssetEvaluation = getFinAssetEvaluation();
		doWriteComponentsToBean(finAssetEvaluation,etihadTab);
		if(StringUtils.isBlank(getFinAssetEvaluation().getRecordType())){
			finAssetEvaluation.setVersion(getFinAssetEvaluation().getVersion() + 1);
			finAssetEvaluation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			finAssetEvaluation.setNewRecord(true);
		}
		finAssetEvaluation.setFinReference(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
		finAssetEvaluation.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		finAssetEvaluation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		finAssetEvaluation.setUserDetails(getUserWorkspace().getLoggedInUser());
		financeDetail.setFinAssetEvaluation(finAssetEvaluation);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_FinAssetEvaluationDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.finAssetEvaluation.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinAssetEvaluation
	 *            FinAssetEvaluation
	 */
	public void doWriteBeanToComponents(FinAssetEvaluation aFinAssetEvaluation) {
		logger.debug("Entering");
		this.finReference.setValue(aFinAssetEvaluation.getFinReference());
		this.custRepreName.setValue(aFinAssetEvaluation.getCustRepreName());
		fillComboBox(this.typeofValuation, aFinAssetEvaluation.getTypeofValuation(), PennantStaticListUtil.getTypeOfValuations(), "");
		fillComboBox(this.propertyStatus, aFinAssetEvaluation.getPropertyStatus(), PennantStaticListUtil.getPropertyStatus(), "");
		fillComboBox(this.reuDecision, aFinAssetEvaluation.getReuDecision(), PennantStaticListUtil.getREUDecisionTypes(), "");
		fillComboBox(this.status, aFinAssetEvaluation.getStatus(), PennantStaticListUtil.getEvaluationStatus(), "");
		this.vendorValuer.setValue(String.valueOf(aFinAssetEvaluation.getVendorValuer()));
		this.vendorValuer.setDescription(StringUtils.trimToEmpty(aFinAssetEvaluation.getVendorValuerDesc()));
		this.remarks.setValue(aFinAssetEvaluation.getRemarks());
		this.panelFirm.setValue(aFinAssetEvaluation.getPanelFirm());
		this.percWorkCompletion.setValue(aFinAssetEvaluation.getPercWorkCompletion());
		this.propertyDesc.setValue(aFinAssetEvaluation.getPropertyDesc());
		this.reuReference.setValue(aFinAssetEvaluation.getReuReference());
		if(StringUtils.isNotEmpty(aFinAssetEvaluation.getTenantContactNum())){
			String[] contactNumber = PennantApplicationUtil.unFormatPhoneNumber(aFinAssetEvaluation.getTenantContactNum());
			if(contactNumber.length==3){
				this.contactNumCountryCode.setValue(contactNumber[0]);
				this.contactNumAreaCode.setValue(contactNumber[1]);
				this.contactPhoneNumber.setValue(contactNumber[2]);
			}
		}
		this.valuationComments.setValue(aFinAssetEvaluation.getValuationComments());
		this.totalRevenue.setValue(PennantAppUtil.formateAmount(aFinAssetEvaluation.getTotalRevenue(), ccyFormatter));
		this.marketValueAED.setValue(PennantAppUtil.formateAmount(aFinAssetEvaluation.getMarketValueAED(), ccyFormatter));
		this.valuerFee.setValue(PennantAppUtil.formateAmount(aFinAssetEvaluation.getValuerFee(), ccyFormatter));
		this.customerFee.setValue(PennantAppUtil.formateAmount(aFinAssetEvaluation.getCustomerFee(), ccyFormatter));
		this.expRentalIncome.setValue(PennantAppUtil.formateAmount(aFinAssetEvaluation.getExpRentalIncome(), ccyFormatter));
		this.custAwareVisit.setChecked(aFinAssetEvaluation.isCustAwareVisit());
		this.tenantAwareVisit.setChecked(aFinAssetEvaluation.isTenantAwareVisit());
		this.propIsRented.setChecked(aFinAssetEvaluation.isPropIsRented());
		this.nocReqDevMunicipality.setChecked(aFinAssetEvaluation.isNocReqDevMunicipality());
		this.illegalDivAlteration.setChecked(aFinAssetEvaluation.isIllegalDivAlteration());
		this.unitVillaSize.setValue(aFinAssetEvaluation.getUnitVillaSize());
		this.leased.setChecked(aFinAssetEvaluation.isLeased());
		this.vendorInstructedDate.setValue(aFinAssetEvaluation.getVendorInstructedDate());
		this.reportDeliveredDate.setValue(aFinAssetEvaluation.getReportDeliveredDate());
		this.inspectionDate.setValue(aFinAssetEvaluation.getInspectionDate());
		this.finalReportDate.setValue(aFinAssetEvaluation.getFinalReportDate());
		this.valuationDate.setValue(aFinAssetEvaluation.getValuationDate());
		this.recordStatus.setValue(aFinAssetEvaluation.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinAssetEvaluation
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(FinAssetEvaluation aFinAssetEvaluation,Tab etihadTab) throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinAssetEvaluation.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinAssetEvaluation.setCustRepreName(this.custRepreName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(StringUtils.equals(getComboboxValue(this.typeofValuation), "#")) {
				aFinAssetEvaluation.setTypeofValuation(null);
			}else{
				aFinAssetEvaluation.setTypeofValuation(getComboboxValue(this.typeofValuation));
			}
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setVendorValuer(Long.parseLong(this.vendorValuer.getValue()));
			aFinAssetEvaluation.setVendorValuerDesc(this.vendorValuer.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(StringUtils.equals(getComboboxValue(this.status), "#")){
				aFinAssetEvaluation.setStatus(null);
			}else{
				aFinAssetEvaluation.setStatus(getComboboxValue(this.status));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(StringUtils.equals(getComboboxValue(this.propertyStatus), "#")){
				aFinAssetEvaluation.setPropertyStatus(null);
			}else{
				aFinAssetEvaluation.setPropertyStatus(getComboboxValue(this.propertyStatus));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(StringUtils.equals(getComboboxValue(this.reuDecision), "#")){
				aFinAssetEvaluation.setReuDecision(null);
			}else{
				aFinAssetEvaluation.setReuDecision(getComboboxValue(this.reuDecision));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setPanelFirm(this.panelFirm.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setPercWorkCompletion(this.percWorkCompletion.intValue() == 0 ? BigDecimal.ZERO : 
				new BigDecimal(PennantApplicationUtil.formatRate(this.percWorkCompletion.getValue().doubleValue(),2)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinAssetEvaluation.setPropertyDesc(this.propertyDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setReuReference(this.reuReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		if(StringUtils.isNotEmpty(this.contactNumCountryCode.getValue()) || 
				StringUtils.isNotEmpty(this.contactNumAreaCode.getValue()) ||
				StringUtils.isNotEmpty(this.contactPhoneNumber.getValue())){
			try {
				if(StringUtils.isEmpty(this.contactNumCountryCode.getValue()) && !this.contactNumCountryCode.isReadonly()){
					throw new WrongValueException(this.contactNumCountryCode,Labels.getLabel("label_TenantContactNum_Validation_CountryCode"));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if(StringUtils.isEmpty(this.contactNumAreaCode.getValue()) && !this.contactNumAreaCode.isReadonly()){
					throw new WrongValueException(this.contactNumAreaCode,Labels.getLabel("label_TenantContactNum_Validation_AreaCode"));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if(StringUtils.isEmpty(this.contactPhoneNumber.getValue()) && !this.contactPhoneNumber.isReadonly()){
					throw new WrongValueException(this.contactPhoneNumber,Labels.getLabel("label_TenantContactNum_Validation_PhoneNum"));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		try {
			aFinAssetEvaluation.setTenantContactNum(PennantApplicationUtil.formatPhoneNumber(this.contactNumCountryCode.getValue(),this.contactNumAreaCode.getValue(),this.contactPhoneNumber.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setTotalRevenue(PennantAppUtil.unFormateAmount(this.totalRevenue.getValidateValue(), ccyFormatter));			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setMarketValueAED(PennantAppUtil.unFormateAmount(this.marketValueAED.getValidateValue(), ccyFormatter));			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setValuerFee(PennantAppUtil.unFormateAmount(this.valuerFee.getValidateValue(), ccyFormatter));			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setCustomerFee(PennantAppUtil.unFormateAmount(this.customerFee.getValidateValue(), ccyFormatter));			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setExpRentalIncome(PennantAppUtil.unFormateAmount(this.expRentalIncome.getValidateValue(), ccyFormatter));			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setCustAwareVisit(this.custAwareVisit.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setTenantAwareVisit(this.tenantAwareVisit.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setPropIsRented(this.propIsRented.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setNocReqDevMunicipality(this.nocReqDevMunicipality.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setIllegalDivAlteration(this.illegalDivAlteration.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.unitVillaSize.getValue() != null) {
				aFinAssetEvaluation.setUnitVillaSize(this.unitVillaSize.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setLeased(this.leased.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setValuationComments(this.valuationComments.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setVendorInstructedDate(this.vendorInstructedDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setReportDeliveredDate(this.reportDeliveredDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setInspectionDate(this.inspectionDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setFinalReportDate(this.finalReportDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAssetEvaluation.setValuationDate(this.valuationDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve, etihadTab);
		aFinAssetEvaluation.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab etihadTab) {
		logger.debug("Entering");
		doRemoveValidation();
		doRemoveLOVValidation();
		if(!recSave){
			if (wve.size() > 0) {
				logger.debug("Throwing occured Errors By using WrongValueException");
				if(parentTab != null){
					parentTab.setSelected(true);
				}
				if(etihadTab != null){
					etihadTab.setSelected(true);
				}
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}
		}
		logger.debug("Leaving");
	}
	
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinAssetEvaluation
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinAssetEvaluation aFinAssetEvaluation) throws InterruptedException {
		logger.debug("Entering");
		getBorderLayoutHeight();
		// append finance basic details 
		appendFinBasicDetails();

		if (aFinAssetEvaluation == null) {
			aFinAssetEvaluation = new FinAssetEvaluation();
			aFinAssetEvaluation.setNewRecord(true);
			setFinAssetEvaluation(aFinAssetEvaluation);
		} 
		
		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinAssetEvaluation.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit(); 
			// setFocus
			this.custRepreName.focus();
		} else {
			this.custRepreName.focus();
			if(isNewFinance()){
				doEdit(); 
			}else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinAssetEvaluation);

			this.gp_REUEvaluation.setVisible(isReadOnly("FinanceMainDialog_invisibleREUValuation"));
			
			if(parent != null){
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.gb_basicDetails.setHeight("100%");
				int visibleRows = grid_basicDetails.getRows().getVisibleItemCount() + grid_Reference2.getRows().getVisibleItemCount();
				this.window_FinAssetEvaluationDialog.setHeight(visibleRows*22+350+"px");
				parent.appendChild(this.window_FinAssetEvaluationDialog);
			}else{
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.custRepreName.isReadonly()) {
			this.custRepreName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_CustRepreName.value"), 
					null, false));
		}
		if (!this.typeofValuation.isDisabled()) {
			this.typeofValuation.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_TypeofValuation.value"), null, false));
		}
		if (!this.vendorValuer.isReadonly()) {
			this.vendorValuer.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_VendorValuer.value"),null,false,true));
		}
		if (!this.status.isReadonly()) {
			this.status.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_Status.value"),null, false));
		}
		if (!this.propertyStatus.isDisabled()) {
			this.propertyStatus.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_PropertyStatus.value"),null,false));
		}
		if (!this.reuDecision.isDisabled()) {
			this.reuDecision.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_REUDecision.value"),null,false));
		}
		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_Remarks.value"), 
					null, false));
		}
		if (!this.panelFirm.isReadonly()) {
			this.panelFirm.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_PanelFirm.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.propertyDesc.isReadonly()) {
			this.propertyDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_PropertyDesc.value"), 
					PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.reuReference.isReadonly()) {
			this.reuReference.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_REUReference.value"), 
					PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if(!this.contactNumCountryCode.isReadonly()){
			this.contactNumCountryCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_FinAssetEvaluationDialog_ContactNumCountryCode.value"),false,1));
		}
		if(!this.contactNumAreaCode.isReadonly()){
			this.contactNumAreaCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_FinAssetEvaluationDialog_ContactNumAreaCode.value"),false,2));
		}
		if (!this.contactPhoneNumber.isReadonly()) {
			this.contactPhoneNumber.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_FinAssetEvaluationDialog_ContactPhoneNumber.value"),false,3));
		}
		if (!this.valuationComments.isReadonly()) {
			this.valuationComments.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAssetEvaluationDialog_ValuationComments.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.totalRevenue.isReadonly()) {
			this.totalRevenue.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_FinAssetEvaluationDialog_TotalRevenue.value"), ccyFormatter, false, false));
		}
		if (!this.marketValueAED.isReadonly()) {
			this.marketValueAED.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_FinAssetEvaluationDialog_MarketValueAED.value"), ccyFormatter, false, false));
		}
		if (!this.valuerFee.isReadonly()) {
			this.valuerFee.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_FinAssetEvaluationDialog_ValuerFee.value"), ccyFormatter, false, false));
		}
		if (!this.customerFee.isReadonly()) {
			this.customerFee.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_FinAssetEvaluationDialog_CustomerFee.value"), ccyFormatter, false, false));
		}
		if (!this.expRentalIncome.isReadonly()) {
			this.expRentalIncome.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_FinAssetEvaluationDialog_ExpRentalIncome.value"), ccyFormatter, false, false));
		}
		if (!this.vendorInstructedDate.isDisabled()) {
			this.vendorInstructedDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinAssetEvaluationDialog_VendorInstructedDate.value"),false));
		}
		if (!this.reportDeliveredDate.isDisabled()) {
			this.reportDeliveredDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinAssetEvaluationDialog_ReportDeliveredDate.value"),false));
		}
		if (!this.inspectionDate.isDisabled()) {
			this.inspectionDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinAssetEvaluationDialog_InspectionDate.value"),false));
		}
		if (!this.finalReportDate.isDisabled()) {
			this.finalReportDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinAssetEvaluationDialog_FinalReportDate.value"),false));
		}
		if (!this.valuationDate.isDisabled()) {
			this.valuationDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinAssetEvaluationDialog_ValuationDate.value"),false));
		}
		if (!this.unitVillaSize.isReadonly()) {
			this.unitVillaSize.setConstraint(new PTDecimalValidator(
			Labels.getLabel("label_FinAssetEvaluationDialog_UnitVillaSize.value"), ccyFormatter, false, false,999999));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
		this.custRepreName.setConstraint("");
		this.typeofValuation.setConstraint("");
		this.vendorValuer.setConstraint("");
		this.propertyStatus.setConstraint("");
		this.reuDecision.setConstraint("");
		this.totalRevenue.setConstraint("");
		this.marketValueAED.setConstraint("");
		this.valuerFee.setConstraint("");
		this.customerFee.setConstraint("");
		this.expRentalIncome.setConstraint("");
		this.remarks.setConstraint("");
		this.panelFirm.setConstraint("");
		this.reuReference.setConstraint("");
		this.propertyDesc.setConstraint("");
		this.valuationComments.setConstraint("");
		this.status.setConstraint("");
		this.percWorkCompletion.setConstraint("");
		this.contactNumCountryCode.setConstraint("");
		this.contactNumAreaCode.setConstraint("");
		this.contactPhoneNumber.setConstraint("");
		this.vendorInstructedDate.setConstraint("");
		this.reportDeliveredDate.setConstraint("");
		this.inspectionDate.setConstraint("");
		this.finalReportDate.setConstraint("");
		this.valuationDate.setConstraint("");
		this.unitVillaSize.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Set the constraints to LOV fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");


		logger.debug("Leaving");
	}

	/**
	 * Method for remove constraints to LOV fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear the error Messages 
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		this.custRepreName.setErrorMessage("");
		this.totalRevenue.setErrorMessage("");
		this.marketValueAED.setErrorMessage("");
		this.valuerFee.setErrorMessage("");
		this.customerFee.setErrorMessage("");
		this.expRentalIncome.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.panelFirm.setErrorMessage("");
		this.reuReference.setErrorMessage("");
		this.propertyDesc.setErrorMessage("");
		this.valuationComments.setErrorMessage("");
		this.status.setErrorMessage("");
		this.typeofValuation.setErrorMessage("");
		this.vendorValuer.setErrorMessage("");
		this.propertyStatus.setErrorMessage("");
		this.reuDecision.setErrorMessage("");
		this.percWorkCompletion.setErrorMessage("");
		this.contactNumCountryCode.setErrorMessage("");
		this.contactNumAreaCode.setErrorMessage("");
		this.contactPhoneNumber.setErrorMessage("");
		this.vendorInstructedDate.setErrorMessage("");
		this.reportDeliveredDate.setErrorMessage("");
		this.inspectionDate.setErrorMessage("");
		this.finalReportDate.setErrorMessage("");
		this.valuationDate.setErrorMessage("");
		this.unitVillaSize.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		logger.debug("Entering");
		
		logger.debug("Leaving");
	}
	
	// CRUD operations

	/**
	 * Deletes a FinAssetEvaluation object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinAssetEvaluation aFinAssetEvaluation = new FinAssetEvaluation();
		BeanUtils.copyProperties(getFinAssetEvaluation(), aFinAssetEvaluation);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aFinAssetEvaluation.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinAssetEvaluation.getRecordType())) {
				aFinAssetEvaluation.setVersion(aFinAssetEvaluation.getVersion() + 1);
				aFinAssetEvaluation.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFinAssetEvaluation.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aFinAssetEvaluation, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFinAssetEvaluation().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.finReference.setReadonly(true);
		this.custRepreName.setReadonly(isReadOnly("FinAssetEvaluationDialog_custRepreName"));
		this.vendorValuer.setReadonly(isReadOnly("FinAssetEvaluationDialog_vendorValuer"));
		this.propertyStatus.setDisabled(isReadOnly("FinAssetEvaluationDialog_propertyStatus"));
		this.reuDecision.setDisabled(isReadOnly("FinAssetEvaluationDialog_reuDecision"));
		this.typeofValuation.setDisabled(isReadOnly("FinAssetEvaluationDialog_typeofValuation"));
		this.remarks.setReadonly(isReadOnly("FinAssetEvaluationDialog_remarks"));
		this.panelFirm.setReadonly(isReadOnly("FinAssetEvaluationDialog_panelFirm"));
		this.propertyDesc.setReadonly(isReadOnly("FinAssetEvaluationDialog_propertyDesc"));
		this.reuReference.setReadonly(isReadOnly("FinAssetEvaluationDialog_reuReference"));
		this.status.setDisabled(isReadOnly("FinAssetEvaluationDialog_status"));
		this.totalRevenue.setReadonly(isReadOnly("FinAssetEvaluationDialog_totalRevenue"));
		this.marketValueAED.setReadonly(isReadOnly("FinAssetEvaluationDialog_marketValueAED"));
		this.valuerFee.setReadonly(isReadOnly("FinAssetEvaluationDialog_valuerFee"));
		this.customerFee.setReadonly(isReadOnly("FinAssetEvaluationDialog_customerFee"));
		this.expRentalIncome.setReadonly(isReadOnly("FinAssetEvaluationDialog_expRentalIncome"));
		this.valuationComments.setReadonly(isReadOnly("FinAssetEvaluationDialog_valuationComments"));
		this.percWorkCompletion.setDisabled(isReadOnly("FinAssetEvaluationDialog_percWorkCompletion"));
		this.custAwareVisit.setDisabled(isReadOnly("FinAssetEvaluationDialog_custAwareVisit"));
		this.tenantAwareVisit.setDisabled(isReadOnly("FinAssetEvaluationDialog_tenantAwareVisit"));
		this.propIsRented.setDisabled(isReadOnly("FinAssetEvaluationDialog_propIsRented"));
		this.nocReqDevMunicipality.setDisabled(isReadOnly("FinAssetEvaluationDialog_nocReqDevMunicipality"));
		this.illegalDivAlteration.setDisabled(isReadOnly("FinAssetEvaluationDialog_illegalDivAlteration"));
		this.unitVillaSize.setReadonly(isReadOnly("FinAssetEvaluationDialog_unitVillaSize"));
		this.leased.setDisabled(isReadOnly("FinAssetEvaluationDialog_leased"));
		this.contactNumCountryCode.setReadonly(isReadOnly("FinAssetEvaluationDialog_tenantContactCountryCode"));
		this.contactNumAreaCode.setReadonly(isReadOnly("FinAssetEvaluationDialog_tenantContactAreaCode"));
		this.contactPhoneNumber.setReadonly(isReadOnly("FinAssetEvaluationDialog_tenantContactPhoneNumber"));
		this.vendorInstructedDate.setDisabled(isReadOnly("FinAssetEvaluationDialog_vendorInstructedDate"));
		this.reportDeliveredDate.setDisabled(isReadOnly("FinAssetEvaluationDialog_reportDeliveredDate"));
		this.inspectionDate.setDisabled(isReadOnly("FinAssetEvaluationDialog_inspectionDate"));
		this.finalReportDate.setDisabled(isReadOnly("FinAssetEvaluationDialog_finalReportDate"));
		this.valuationDate.setDisabled(isReadOnly("FinAssetEvaluationDialog_valuationDate"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.finAssetEvaluation.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewFinance()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
		this.custRepreName.setReadonly(true);
		this.totalRevenue.setReadonly(true);
		this.marketValueAED.setReadonly(true);
		this.valuerFee.setReadonly(true);
		this.customerFee.setReadonly(true);
		this.expRentalIncome.setReadonly(true);
		this.remarks.setReadonly(true);
		this.panelFirm.setReadonly(true);
		this.reuReference.setReadonly(true);
		this.propertyDesc.setReadonly(true);
		this.valuationComments.setReadonly(true);
		this.status.setDisabled(true);
		this.typeofValuation.setDisabled(true);
		this.vendorValuer.setReadonly(true);
		this.propertyStatus.setDisabled(true);
		this.reuDecision.setDisabled(true);
		this.percWorkCompletion.setDisabled(true);
		this.custAwareVisit.setDisabled(true);
		this.tenantAwareVisit.setDisabled(true);
		this.propIsRented.setDisabled(true);
		this.nocReqDevMunicipality.setDisabled(true);
		this.illegalDivAlteration.setDisabled(true);
		this.leased.setDisabled(true);
		this.contactNumCountryCode.setDisabled(true);
		this.contactNumAreaCode.setDisabled(true);
		this.contactPhoneNumber.setDisabled(true);
		this.vendorInstructedDate.setDisabled(true);
		this.reportDeliveredDate.setDisabled(true);
		this.inspectionDate.setDisabled(true);
		this.finalReportDate.setDisabled(true);
		this.valuationDate.setDisabled(true);
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.finReference.setText("");
		this.custRepreName.setValue("");
		this.totalRevenue.setValue("");
		this.marketValueAED.setValue("");
		this.valuerFee.setValue("");
		this.customerFee.setValue("");
		this.expRentalIncome.setValue("");
		this.remarks.setValue("");
		this.panelFirm.setValue("");
		this.reuReference.setValue("");
		this.propertyDesc.setValue("");
		this.valuationComments.setValue("");
		this.status.setValue("");
		this.typeofValuation.setValue("");
		this.vendorValuer.setValue("");
		this.propertyStatus.setValue("");
		this.reuDecision.setValue("");
		this.percWorkCompletion.setValue("");
		this.contactNumCountryCode.setValue("");
		this.contactNumAreaCode.setValue("");
		this.contactPhoneNumber.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinAssetEvaluation aFinAssetEvaluation = new FinAssetEvaluation();
		BeanUtils.copyProperties(getFinAssetEvaluation(), aFinAssetEvaluation);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		doSetLOVValidation();
		// fill the FinAssetEvaluation object with the components data
		doWriteComponentsToBean(aFinAssetEvaluation,null);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aFinAssetEvaluation.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinAssetEvaluation.getRecordType())) {
				aFinAssetEvaluation.setVersion(aFinAssetEvaluation.getVersion() + 1);
				if (isNew) {
					aFinAssetEvaluation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinAssetEvaluation.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinAssetEvaluation.setNewRecord(true);
				}
			}
		} else {
			aFinAssetEvaluation.setVersion(aFinAssetEvaluation.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aFinAssetEvaluation, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFinAssetEvaluation (FinAssetEvaluation)
	 * @param tranType (String)
	 * 
	 * @return boolean
	 */
	private boolean doProcess(FinAssetEvaluation aFinAssetEvaluation, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinAssetEvaluation.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinAssetEvaluation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinAssetEvaluation.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinAssetEvaluation.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinAssetEvaluation.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinAssetEvaluation);
				}

				if (isNotesMandatory(taskId, aFinAssetEvaluation)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aFinAssetEvaluation.setTaskId(taskId);
			aFinAssetEvaluation.setNextTaskId(nextTaskId);
			aFinAssetEvaluation.setRoleCode(getRole());
			aFinAssetEvaluation.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinAssetEvaluation, tranType);

			String operationRefs = getServiceOperations(taskId, aFinAssetEvaluation);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinAssetEvaluation, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinAssetEvaluation, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * @param method (String)
	 * 
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinAssetEvaluation aFinAssetEvaluation = (FinAssetEvaluation) auditHeader.getAuditDetail().getModelData();

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinAssetEvaluationService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getFinAssetEvaluationService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getFinAssetEvaluationService().doApprove(auditHeader);

						if (aFinAssetEvaluation.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinAssetEvaluationService().doReject(auditHeader);
						if (aFinAssetEvaluation.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_FinAssetEvaluationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_FinAssetEvaluationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(finAssetEvaluation), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}


	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFinAssetEvaluation
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinAssetEvaluation aFinAssetEvaluation,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aFinAssetEvaluation.getBefImage(), aFinAssetEvaluation);
		return new AuditHeader(String.valueOf(aFinAssetEvaluation.getId()),
				null, null, null, auditDetail,
				aFinAssetEvaluation.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinAssetEvaluationDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.finAssetEvaluation);
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.finAssetEvaluation.getId());
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
	

	public void onFulfill$vendorValuer(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = vendorValuer.getObject();
		if (dataObject instanceof String) {
			this.vendorValuer.setValue("0");
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.vendorValuer.setValue(String.valueOf(details.getFieldCodeId()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public FinAssetEvaluation getFinAssetEvaluation() {
		return this.finAssetEvaluation;
	}
	public void setFinAssetEvaluation(FinAssetEvaluation finAssetEvaluation) {
		this.finAssetEvaluation = finAssetEvaluation;
	}

	public void setFinAssetEvaluationService(
			FinAssetEvaluationService finAssetEvaluationService) {
		this.finAssetEvaluationService = finAssetEvaluationService;
	}
	public FinAssetEvaluationService getFinAssetEvaluationService() {
		return this.finAssetEvaluationService;
	}

	public void setOverideMap(
			HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
	public boolean isNewFinance() {
		return newFinance;
	}
	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}
	
	public FinanceDetail getFinancedetail() {
		return financedetail;
	}
	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}
	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

}
