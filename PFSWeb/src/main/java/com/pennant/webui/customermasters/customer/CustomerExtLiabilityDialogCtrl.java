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
 * FileName    		:  CustomerExtLiabilityDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 19-04-2018       Vinay					 0.2        As per Profectus documnet 			*
 * 														below fields are added 				* 	
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerExtLiability/customerExtLiabilityDialog.zul file.
 */
public class CustomerExtLiabilityDialogCtrl extends GFCBaseCtrl<CustomerExtLiability> {
	private static final long serialVersionUID = -7522534300621535097L;
	private static final Logger logger = Logger.getLogger(CustomerExtLiabilityDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerExtLiabilityDialog; 		

	protected Longbox 	custID; 					
	protected Textbox 	custCIF;					
	protected Label   	custShrtName;				
	protected Datebox 	finDate; 					
	protected ExtendedCombobox 	finType; 		    
	protected ExtendedCombobox 	finStatus; 		    
	protected ExtendedCombobox 	bankName; 			
	protected CurrencyBox 	originalAmount; 		
	protected CurrencyBox 	installmentAmount; 		
	protected CurrencyBox 	outStandingBal; 		
	protected Intbox liabilitySeq;          	    
	
	//###_0.2
	protected CurrencyBox 	roi; 		
	protected Intbox totalTenure;          	    
	protected Intbox balanceTenure;          	    
	protected Intbox noOfBounces;          	    
	protected CurrencyBox 	pos; 		
	protected CurrencyBox 	overdue; 		
	protected Checkbox 	emiFoir; 		
	protected Combobox 	source; 		
	protected Combobox 	checkedBy; 		
	protected Textbox 	securityDetail; 		
	protected ExtendedCombobox 	endUseOfFunds; 			
	protected ExtendedCombobox 	repayFrom; 			
	private final List<ValueLabel>					sourceInfoList				= PennantStaticListUtil.getSourceInfoList();
	private final List<ValueLabel>					trackCheckList				= PennantStaticListUtil.getTrackCheckList();
	// not auto wired variables
	private CustomerExtLiability customerExtLiability; // overHanded per parameter

	private transient boolean validationOn;
	
	protected Button btnSearchPRCustid; 

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerExtLiability> CustomerExtLiabilityList;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private String moduleType="";
	private String userRole="";
	private int finFormatter;
	private boolean isFinanceProcess = false;
	Date appDate = DateUtility.getAppDate();
	Date appStartDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE);
	

	/**
	 * default constructor.<br>
	 */
	public CustomerExtLiabilityDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerExtLiabilityDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerExtLiability object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerExtLiabilityDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerExtLiabilityDialog);

		try {
			if (arguments.containsKey("customerExtLiability")) {
				this.customerExtLiability = (CustomerExtLiability) arguments
						.get("customerExtLiability");
				CustomerExtLiability befImage = new CustomerExtLiability();
				BeanUtils.copyProperties(this.customerExtLiability, befImage);
				this.customerExtLiability.setBefImage(befImage);
				setCustomerExtLiability(this.customerExtLiability);
			} else {
				setCustomerExtLiability(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (arguments.containsKey("finFormatter")) {
				this.finFormatter = (Integer) arguments.get("finFormatter");
			}

			if (getCustomerExtLiability().isNewRecord()) {
				setNewRecord(true);
			}

			if (arguments.containsKey("customerDialogCtrl")) {
				setCustomerDialogCtrl((CustomerDialogCtrl) arguments
						.get("customerDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.customerExtLiability.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole,
							"CustomerExtLiabilityDialog");
				}
			}
			if (arguments.containsKey("customerViewDialogCtrl")) {
				setCustomerViewDialogCtrl((CustomerViewDialogCtrl) arguments.get("customerViewDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.customerExtLiability.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerExtLiabilityDialog");
				}
			}
			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}
			doLoadWorkFlow(this.customerExtLiability.isWorkflow(),
					this.customerExtLiability.getWorkflowId(),
					this.customerExtLiability.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"CustomerExtLiabilityDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCustomerExtLiability());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerExtLiabilityDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.bankName.setMaxlength(8);
		this.bankName.setMandatoryStyle(true);
		this.bankName.setTextBoxWidth(116);
		this.bankName.setModuleName("BankDetail");
		this.bankName.setValueColumn("BankCode");
		this.bankName.setDescColumn("BankName");
		this.bankName.setValidateColumns(new String[] { "BankCode" });
		
		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		this.finType.setTextBoxWidth(116);
		this.finType.setModuleName("OtherBankFinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });
		
		this.finStatus.setMaxlength(8);
		this.finStatus.setMandatoryStyle(true);
		this.finStatus.setTextBoxWidth(116);
		this.finStatus.setModuleName("CustomerStatusCode");
		this.finStatus.setValueColumn("CustStsCode");
		this.finStatus.setDescColumn("CustStsDescription");
		this.finStatus.setValidateColumns(new String[] { "CustStsCode" });
		
		this.finDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.originalAmount.setMandatory(false);
		this.originalAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.originalAmount.setScale(finFormatter);
		
		this.installmentAmount.setMandatory(true);
		this.installmentAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.installmentAmount.setScale(finFormatter);
		
		this.outStandingBal.setMandatory(true);
		this.outStandingBal.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.outStandingBal.setScale(finFormatter);
		
		this.roi.setMandatory(true);
		this.roi.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.roi.setScale(finFormatter);
		
		this.pos.setMandatory(true);
		this.pos.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.pos.setScale(finFormatter);
		
		this.overdue.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.overdue.setScale(finFormatter);
		
		this.endUseOfFunds.setMaxlength(8);
		this.endUseOfFunds.setMandatoryStyle(true);
		this.endUseOfFunds.setTextBoxWidth(116);
		this.endUseOfFunds.setModuleName("LoanPurpose");
		this.endUseOfFunds.setValueColumn("LoanPurposeCode");
		this.endUseOfFunds.setDescColumn("LoanPurposeDesc");
		this.endUseOfFunds.setValidateColumns(new String[] { "LoanPurposeCode" });
		
		this.repayFrom.setMaxlength(8);
		this.repayFrom.setMandatoryStyle(true);
		this.repayFrom.setTextBoxWidth(116);
		this.repayFrom.setModuleName("BankDetail");
		this.repayFrom.setValueColumn("BankCode");
		this.repayFrom.setDescColumn("BankName");
		this.repayFrom.setValidateColumns(new String[] { "BankCode" });
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
		}
		logger.debug("Leaving");
	}
	
	/**
	 * ON fulfill FinType
	 * 
	 * @param event
	 * 
	 */
	public void onFulfill$finType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = finType.getObject();
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
		} else {
			OtherBankFinanceType details = (OtherBankFinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.finType.setDescription(details.getFinTypeDesc());
			}
		}
		doSetInstAmountMandProp();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * 
	 */
	private void doSetInstAmountMandProp(){
		if(StringUtils.equals(this.finType.getValue(), "CC")){
			this.installmentAmount.setMandatory(false);
		}else{
			this.installmentAmount.setMandatory(true);
		}
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
		getUserWorkspace().allocateAuthorities("CustomerExtLiabilityDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
		MessageUtil.showHelpWindow(event, window_CustomerExtLiabilityDialog);
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
		doWriteBeanToComponents(this.customerExtLiability.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerExtLiability
	 *            CustomerExtLiability
	 */
	public void doWriteBeanToComponents(CustomerExtLiability aCustomerExtLiability) {
		logger.debug("Entering");

		if(aCustomerExtLiability.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerExtLiability.getCustID());
		}
		this.liabilitySeq.setValue(aCustomerExtLiability.getLiabilitySeq());
		this.finDate.setValue(aCustomerExtLiability.getFinDate());
		this.bankName.setValue(aCustomerExtLiability.getBankName());
		this.bankName.setDescription(StringUtils.trimToEmpty(aCustomerExtLiability.getLovDescBankName()));
		this.finType.setValue(aCustomerExtLiability.getFinType());
		this.finType.setDescription(StringUtils.trimToEmpty(aCustomerExtLiability.getLovDescFinType()));
		this.finStatus.setValue(aCustomerExtLiability.getFinStatus());
		this.finStatus.setDescription(StringUtils.trimToEmpty(aCustomerExtLiability.getLovDescFinStatus()));
		this.originalAmount.setValue(PennantAppUtil.formateAmount(aCustomerExtLiability.getOriginalAmount(),finFormatter));
		this.installmentAmount.setValue(PennantAppUtil.formateAmount(aCustomerExtLiability.getInstalmentAmount(),finFormatter));
		this.outStandingBal.setValue(PennantAppUtil.formateAmount(aCustomerExtLiability.getOutStandingBal(),finFormatter));
		
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomerExtLiability.getLovDescCustCIF()));
		this.custShrtName.setValue(StringUtils.trimToEmpty(aCustomerExtLiability.getLovDescCustShrtName()));

		//###_0.2
		this.roi.setValue(PennantAppUtil.formateAmount(aCustomerExtLiability.getRoi(),finFormatter));
		this.pos.setValue(PennantAppUtil.formateAmount(aCustomerExtLiability.getPos(),finFormatter));
		this.overdue.setValue(PennantAppUtil.formateAmount(aCustomerExtLiability.getOverdue(),finFormatter));
		this.totalTenure.setValue(aCustomerExtLiability.getTenure());
		this.balanceTenure.setValue(aCustomerExtLiability.getTenureBal());
		this.noOfBounces.setValue(aCustomerExtLiability.getBounceNo());
		this.emiFoir.setChecked(aCustomerExtLiability.isEmiCnsdrForFOIR());
		this.securityDetail.setValue(aCustomerExtLiability.getSecurityDetail());
		this.endUseOfFunds.setValue(aCustomerExtLiability.getEndUseOfFunds());
		this.endUseOfFunds.setDescription(aCustomerExtLiability.getLoanpurposedesc());
		this.repayFrom.setValue(aCustomerExtLiability.getRepayFrom());
		this.repayFrom.setDescription(aCustomerExtLiability.getLovdescrepayfrom());
		fillComboBox(this.source, aCustomerExtLiability.getSource(), sourceInfoList, "");
		fillComboBox(this.checkedBy, aCustomerExtLiability.getCheckedBy(), trackCheckList, "");
		
		this.recordStatus.setValue(aCustomerExtLiability.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerExtLiability
	 */
	public void doWriteComponentsToBean(CustomerExtLiability aCustomerExtLiability) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerExtLiability.setLovDescCustCIF(this.custCIF.getValue());
			aCustomerExtLiability.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setLiabilitySeq(this.liabilitySeq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setFinDate(this.finDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setLovDescBankName(this.bankName.getDescription());
			aCustomerExtLiability.setBankName(this.bankName.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setLovDescFinType(this.finType.getDescription());
			aCustomerExtLiability.setFinType(this.finType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}


		try {
			aCustomerExtLiability.setOriginalAmount(PennantAppUtil.unFormateAmount(this.originalAmount.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		
		try {
			aCustomerExtLiability.setInstalmentAmount(PennantAppUtil.unFormateAmount(this.installmentAmount.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setOutStandingBal(PennantAppUtil.unFormateAmount(this.outStandingBal.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setFinStatus(this.finStatus.getValidatedValue());
			aCustomerExtLiability.setLovDescFinStatus(this.finStatus.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//###_0.2
		try {
			aCustomerExtLiability.setRoi(PennantAppUtil.unFormateAmount(this.roi.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setPos(PennantAppUtil.unFormateAmount(this.pos.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerExtLiability.setOverdue(PennantAppUtil.unFormateAmount(this.overdue.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerExtLiability.setTenure(this.totalTenure.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerExtLiability.setTenureBal(this.balanceTenure.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerExtLiability.setBounceNo(this.noOfBounces.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerExtLiability.setEmiCnsdrForFOIR(this.emiFoir.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (getComboboxValue(this.source).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.source, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerExtLiabilityDialog_Source.value") }));
			}
			aCustomerExtLiability.setSource(this.source.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (getComboboxValue(this.checkedBy).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.checkedBy, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerExtLiabilityDialog_CheckedBy.value") }));
			}
			aCustomerExtLiability.setCheckedBy(this.checkedBy.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerExtLiability.setSecurityDetail(this.securityDetail.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerExtLiability.setEndUseOfFunds(this.endUseOfFunds.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerExtLiability.setRepayFrom(this.repayFrom.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();

		boolean focus = false;
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
				Component component = wve.get(i).getComponent();
				if(!focus){
					focus = setComponentFocus(component);
				}
			}
			throw new WrongValuesException(wvea);
		}

		aCustomerExtLiability.setRecordStatus(this.recordStatus.getValue());
		setCustomerExtLiability(aCustomerExtLiability);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerExtLiability
	 * @throws Exception
	 */
	public void doShowDialog(CustomerExtLiability aCustomerExtLiability) throws Exception {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			this.finType.focus();
			if (isNewCustomer()){
				doEdit();
			}else  if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerExtLiability);

            doCheckEnquiry();
            doSetInstAmountMandProp();
            
			if(isNewCustomer()){
				this.window_CustomerExtLiabilityDialog.setHeight("55%");
				this.window_CustomerExtLiabilityDialog.setWidth("60%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerExtLiabilityDialog.doModal() ;
			}else{
				this.window_CustomerExtLiabilityDialog.setWidth("100%");
				this.window_CustomerExtLiabilityDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerExtLiabilityDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if(PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)){
			this.bankName.setReadonly(true);
			this.finStatus.setReadonly(true);
			this.finType.setReadonly(true);
			this.outStandingBal.setReadonly(true);
			this.installmentAmount.setReadonly(true);
			this.originalAmount.setReadonly(true);
			this.liabilitySeq.setReadonly(true);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
			
			this.roi.setReadonly(true);	
			this.totalTenure.setReadonly(true);          	    
			this.balanceTenure.setReadonly(true);          	    
			this.noOfBounces.setReadonly(true);          	    
			this.pos.setReadonly(true); 		
			this.overdue.setReadonly(true); 		
			this.emiFoir.setDisabled(true); 		
			this.source.setReadonly(true); 		
			this.checkedBy.setReadonly(true); 		
			this.securityDetail.setReadonly(true); 		
			this.endUseOfFunds.setReadonly(true); 			
			this.repayFrom.setReadonly(true); 
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.finDate.isReadonly()) {
			this.finDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_FinDate.value"), true,appStartDate,appDate,true));
		}
		if (!this.finStatus.isReadonly()) {
			this.finStatus.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_FinStatus.value"), null, true, true));
		}
		if (!this.originalAmount.isDisabled()) {
			this.originalAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_OriginalAmount.value"), 0, true, false));
		}
		if (!this.installmentAmount.isDisabled()) {
			this.installmentAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_InstallmentAmount.value"),
					0, this.installmentAmount.isMandatory(), false));
		}
		if (!this.outStandingBal.isDisabled()) {
			this.outStandingBal.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_OutStandingBal.value"), 0, true, false));
		}
		//###_0.2
		if (!this.pos.isDisabled()) {
			this.pos.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_POS.value"), 0, true, false));
		}
		if (!this.roi.isDisabled()) {
			this.roi.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_ROI.value"), 0, true, false));
		}
		if (!this.overdue.isDisabled()) {
			this.overdue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_Overdue.value"), 0, false, false));
		}
		if (!this.totalTenure.isDisabled()) {
			this.totalTenure.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_TotalTenure.value"), true, false));
		}
		if (!this.balanceTenure.isDisabled()) {
			this.balanceTenure.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_BalanceTenure.value"), true, false));
		}
		if (!this.noOfBounces.isDisabled()) {
			this.noOfBounces.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_NoOfBounces.value"), true, false));
		}
		if (!this.source.isReadonly()) {
			this.source.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_Source.value"),null,true));
		}
		if (!this.checkedBy.isReadonly()) {
			this.checkedBy.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_CheckedBy.value"),null,true));
		}
		if (!this.securityDetail.isReadonly()) {
			this.securityDetail.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_SecurityDetail.value"), null, false, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finStatus.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		if (!this.bankName.isReadonly()) {
			this.bankName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_BankName.value"),null,true,true));
		}
		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_finType.value"),null,true,true));
		}
		if (!this.endUseOfFunds.isReadonly()) {
			this.endUseOfFunds.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_EndUseOfFunds.value"), null, true, true));
		}
		if (!this.repayFrom.isReadonly()) {
			this.repayFrom.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_RepayFrom.value"), null, true, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.bankName.setConstraint("");
		this.finType.setConstraint("");
		this.originalAmount.setConstraint("");
		this.installmentAmount.setConstraint("");
		this.outStandingBal.setConstraint("");
		
		this.roi.setConstraint("");
		this.totalTenure.setConstraint("");    	    
		this.balanceTenure.setConstraint("");    	    
		this.noOfBounces.setConstraint("");    	    
		this.pos.setConstraint(""); 		
		this.overdue.setConstraint(""); 		
		this.source.setConstraint(""); 		
		this.checkedBy.setConstraint(""); 		
		this.securityDetail.setConstraint(""); 		
		this.endUseOfFunds.setConstraint(""); 			
		this.repayFrom.setConstraint(""); 	
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finStatus.setErrorMessage("");
		this.finDate.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.originalAmount.setErrorMessage("");
		this.installmentAmount.setErrorMessage("");
		this.outStandingBal.setErrorMessage("");
		
		this.roi.setErrorMessage("");
		this.totalTenure.setErrorMessage("");    	    
		this.balanceTenure.setErrorMessage("");    	    
		this.noOfBounces.setErrorMessage("");    	    
		this.pos.setErrorMessage(""); 		
		this.overdue.setErrorMessage(""); 		
		this.source.setErrorMessage(""); 		
		this.checkedBy.setErrorMessage(""); 		
		this.securityDetail.setErrorMessage(""); 		
		this.endUseOfFunds.setErrorMessage(""); 			
		this.repayFrom.setErrorMessage(""); 	
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a CustomerExtLiability object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerExtLiability aCustomerExtLiability = new CustomerExtLiability();
		BeanUtils.copyProperties(getCustomerExtLiability(), aCustomerExtLiability);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_CustomerExtLiabilityDialog_LiabilitySeq.value")+" : "+aCustomerExtLiability.getLiabilitySeq();
		
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerExtLiability.getRecordType())) {
				aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion() + 1);
				aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(!isFinanceProcess && getCustomerDialogCtrl() != null &&  
						getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
					aCustomerExtLiability.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()) {
					aCustomerExtLiability.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerExtLiability, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerExtLiabilityDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getCustomerDialogCtrl().doFillCustomerExtLiabilityDetails(this.CustomerExtLiabilityList);
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()){
			if(isNewCustomer()){
				this.btnCancel.setVisible(false);	
				this.btnSearchPRCustid.setVisible(false);
			}else{
				this.btnSearchPRCustid.setVisible(true);
			}
			
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
		}
		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.bankName.setReadonly(isReadOnly("CustomerExtLiabilityDialog_BankName"));
		this.finDate.setDisabled(isReadOnly("CustomerExtLiabilityDialog_finDate"));
		this.finStatus.setReadonly(isReadOnly("CustomerExtLiabilityDialog_finStatus"));
		this.finType.setReadonly(isReadOnly("CustomerExtLiabilityDialog_finType"));
		this.originalAmount.setReadonly(isReadOnly("CustomerExtLiabilityDialog_originalAmount"));
		this.installmentAmount.setReadonly(isReadOnly("CustomerExtLiabilityDialog_installmentAmount"));
		this.outStandingBal.setReadonly(isReadOnly("CustomerExtLiabilityDialog_outStandingBal"));

		this.roi.setReadonly(isReadOnly("CustomerExtLiabilityDialog_ROI"));	
		this.totalTenure.setReadonly(isReadOnly("CustomerExtLiabilityDialog_TotalTenure"));          	    
		this.balanceTenure.setReadonly(isReadOnly("CustomerExtLiabilityDialog_BalanceTenure"));          	    
		this.noOfBounces.setReadonly(isReadOnly("CustomerExtLiabilityDialog_NoOfBounces"));          	    
		this.pos.setReadonly(isReadOnly("CustomerExtLiabilityDialog_POS")); 		
		this.overdue.setReadonly(isReadOnly("CustomerExtLiabilityDialog_Overdue")); 		
		this.emiFoir.setDisabled(isReadOnly("CustomerExtLiabilityDialog_EMIFoir")); 		
		this.source.setReadonly(isReadOnly("CustomerExtLiabilityDialog_Source")); 	
		this.source.setDisabled(isReadOnly("CustomerExtLiabilityDialog_Source")); 	
		this.checkedBy.setReadonly(isReadOnly("CustomerExtLiabilityDialog_CheckedBy")); 		
		this.checkedBy.setDisabled(isReadOnly("CustomerExtLiabilityDialog_CheckedBy")); 		
		this.securityDetail.setReadonly(isReadOnly("CustomerExtLiabilityDialog_SecurityDetail")); 		
		this.endUseOfFunds.setReadonly(isReadOnly("CustomerExtLiabilityDialog_EndUseOfFunds")); 			
		this.repayFrom.setReadonly(isReadOnly("CustomerExtLiabilityDialog_RepayFrom")); 

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerExtLiability.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if(newCustomer){
				if(PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)){
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				}else if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		boolean isCustomerWorkflow = false;
		if(getCustomerDialogCtrl() != null){
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.bankName.setReadonly(true);
		this.finStatus.setReadonly(true);
		this.finType.setReadonly(true);
		this.outStandingBal.setReadonly(true);
		this.installmentAmount.setReadonly(true);
		this.originalAmount.setReadonly(true);
		this.liabilitySeq.setReadonly(true);
		
		this.roi.setReadonly(true);	
		this.totalTenure.setReadonly(true);          	    
		this.balanceTenure.setReadonly(true);          	    
		this.noOfBounces.setReadonly(true);          	    
		this.pos.setReadonly(true); 		
		this.overdue.setReadonly(true); 		
		this.emiFoir.setDisabled(true); 		
		this.source.setReadonly(true); 		
		this.checkedBy.setReadonly(true); 		
		this.securityDetail.setReadonly(true); 		
		this.endUseOfFunds.setReadonly(true); 			
		this.repayFrom.setReadonly(true); 	
		
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
		this.bankName.setValue("");
		this.bankName.setDescription("");
		this.finStatus.setValue("");
		this.finType.setValue("");
		this.originalAmount.setValue("");
		this.installmentAmount.setValue("");
		this.outStandingBal.setValue("");
		this.finType.setDescription("");
		
		this.roi.setValue("");	
		this.totalTenure.setValue(0);          	    
		this.balanceTenure.setValue(0);          	    
		this.noOfBounces.setValue(0);          	    
		this.pos.setValue(""); 		
		this.overdue.setValue(""); 		
		this.source.setValue(""); 		
		this.checkedBy.setValue(""); 		
		this.securityDetail.setValue(""); 		
		this.endUseOfFunds.setValue(""); 			
		this.repayFrom.setValue(""); 
		
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerExtLiability aCustomerExtLiability = new CustomerExtLiability();
		BeanUtils.copyProperties(getCustomerExtLiability(), aCustomerExtLiability);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerExtLiability object with the components data
		doWriteComponentsToBean(aCustomerExtLiability);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerExtLiability.isNew();
		String tranType = "";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerExtLiability.getRecordType())){
				aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion()+1);
				if(isNew){
					aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerExtLiability.setNewRecord(true);
				}
			}
		}else{

			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerExtLiability.setVersion(1);
					aCustomerExtLiability.setRecordType(PennantConstants.RCD_ADD);					
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isBlank(aCustomerExtLiability.getRecordType())){
					aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion()+1);
					aCustomerExtLiability.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerExtLiability, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerExtLiabilityDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getCustomerDialogCtrl().doFillCustomerExtLiabilityDetails(this.CustomerExtLiabilityList);
				closeDialog();
			}
		
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	
	private AuditHeader newFinanceCustomerProcess(CustomerExtLiability aCustomerExtLiability,String tranType){
		logger.debug("Entering");
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aCustomerExtLiability, tranType);
		CustomerExtLiabilityList = new ArrayList<CustomerExtLiability>();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		
		valueParm[0] = String.valueOf(aCustomerExtLiability.getId());
		valueParm[1] = String.valueOf(aCustomerExtLiability.getLiabilitySeq());
		
		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_LiabilitySeq")+ ":" + valueParm[1];
		
		if(getCustomerDialogCtrl().getCustomerExtLiabilityDetailList()!=null && getCustomerDialogCtrl().getCustomerExtLiabilityDetailList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getCustomerExtLiabilityDetailList().size(); i++) {
				CustomerExtLiability customerExtLiability = getCustomerDialogCtrl().getCustomerExtLiabilityDetailList().get(i);
				
				
				if(aCustomerExtLiability.getLiabilitySeq()==customerExtLiability.getLiabilitySeq()){ // Both Current and Existing list rating same
					
					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,"41001",
										errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							CustomerExtLiabilityList.add(aCustomerExtLiability);
						}else if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							CustomerExtLiabilityList.add(aCustomerExtLiability);
						}else if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerExtLiabilityList().size(); j++) {
								CustomerExtLiability email =  getCustomerDialogCtrl().getCustomerDetails().getCustomerExtLiabilityList().get(j);
								if(email.getCustID() == aCustomerExtLiability.getCustID() && 
										email.getLiabilitySeq()==aCustomerExtLiability.getLiabilitySeq()){
									CustomerExtLiabilityList.add(email);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							CustomerExtLiabilityList.add(customerExtLiability);
						}
					}
				} else {
					CustomerExtLiabilityList.add(customerExtLiability);
				}
			}
		}
		
		if(!recordAdded){
			CustomerExtLiabilityList.add(aCustomerExtLiability);
		}
		logger.debug("Leaving");
		return auditHeader;
	} 


	// Search Button Component Events

	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerExtLiability
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerExtLiability aCustomerExtLiability, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,aCustomerExtLiability.getBefImage(), aCustomerExtLiability);
		return new AuditHeader(getReference(), String.valueOf(aCustomerExtLiability.getCustID()), null, null, 
				auditDetail, aCustomerExtLiability.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerExtLiabilityDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.customerExtLiability);
	}
	
	
	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustomerExtLiability().getCustID()+ PennantConstants.KEY_SEPERATOR
		+ getCustomerExtLiability().getBankName();
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

	public CustomerExtLiability getCustomerExtLiability() {
		return this.customerExtLiability;
	}
	public void setCustomerExtLiability(CustomerExtLiability customerExtLiability) {
		this.customerExtLiability = customerExtLiability;
	}

	public void setCustomerEmails(List<CustomerExtLiability> customerEmails) {
		this.CustomerExtLiabilityList = customerEmails;
	}
	public List<CustomerExtLiability> getCustomerEmails() {
		return CustomerExtLiabilityList;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}
	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public CustomerViewDialogCtrl getCustomerViewDialogCtrl() {
		return customerViewDialogCtrl;
	}

	public void setCustomerViewDialogCtrl(CustomerViewDialogCtrl customerViewDialogCtrl) {
		this.customerViewDialogCtrl = customerViewDialogCtrl;
	}
	
}
