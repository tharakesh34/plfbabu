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
 * FileName    		:  CustomerBankInfoDialogCtrl.java                                                   * 	  
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
 * 18-04-2018       Vinay                    0.2          As per Profectus document added 
 * 														  some fields                       * 
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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerBankInfo/customerBankInfoDialog.zul file.
 */
public class CustomerBankInfoDialogCtrl extends GFCBaseCtrl<CustomerBankInfo> {
	private static final long serialVersionUID = -7522534300621535097L;
	private static final Logger logger = Logger.getLogger(CustomerBankInfoDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerBankInfoDialog; 		

	protected Longbox 	custID; 						
	protected Textbox 	custCIF;						
	protected Label   	custShrtName;					
	protected ExtendedCombobox 	bankName; 				
	protected Textbox 	accountNumber; 					
	protected ExtendedCombobox 	accountType; 			
	protected Checkbox 	salaryAccount; 		
	protected Row row_salaryAccount;
	
	//####_0.2
	protected Intbox creditTranNo;
	protected CurrencyBox creditTranAmt;
	protected CurrencyBox creditTranAvg;
	protected Intbox debitTranNo;
	protected CurrencyBox debitTranAmt;
	protected Intbox cashDepositNo;
	protected CurrencyBox cashDepositAmt;
	protected Intbox cashWithdrawalNo;
	protected CurrencyBox cashWithdrawalAmt;
	protected Intbox chqDepositNo;
	protected CurrencyBox chqDepositAmt;
	protected Intbox chqIssueNo;
	protected CurrencyBox chqIssueAmt;
	protected Intbox inwardChqBounceNo;
	protected Intbox outwardChqBounceNo;
	protected CurrencyBox eodBalMin;
	protected CurrencyBox eodBalMax;
	protected CurrencyBox eodBalAvg;
	
	private int finFormatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	
	// not auto wired variables
	private CustomerBankInfo customerBankInfo; // overHanded per parameter
	private transient boolean validationOn;
	protected Button btnSearchPRCustid; 
	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerBankInfo> CustomerBankInfoList;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private String moduleType="";
	private String userRole="";
	private boolean isFinanceProcess = false;
	protected int	accNoLength;
	private transient BankDetailService	 bankDetailService;

	/**
	 * default constructor.<br>
	 */
	public CustomerBankInfoDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerBankInfoDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerBankInfo object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerBankInfoDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerBankInfoDialog);

		try {

			if (arguments.containsKey("customerBankInfo")) {
				this.customerBankInfo = (CustomerBankInfo) arguments
						.get("customerBankInfo");
				CustomerBankInfo befImage = new CustomerBankInfo();
				BeanUtils.copyProperties(this.customerBankInfo, befImage);
				this.customerBankInfo.setBefImage(befImage);
				setCustomerBankInfo(this.customerBankInfo);
			} else {
				setCustomerBankInfo(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}
			if (arguments.containsKey("retailCustomer")) {
				boolean isRetailCust = (boolean) arguments.get("retailCustomer");
				if(!isRetailCust){
					this.row_salaryAccount.setVisible(false);
				}
			}

			if (getCustomerBankInfo().isNewRecord()) {
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
				this.customerBankInfo.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole,
							"CustomerBankInfoDialog");
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
				this.customerBankInfo.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerBankInfoDialog");
				}
			}
			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}
			if (arguments.containsKey("CustomerBankInfoList")) {
				CustomerBankInfoList = (List<CustomerBankInfo>) arguments.get("CustomerBankInfoList");
			}
			doLoadWorkFlow(this.customerBankInfo.isWorkflow(),
					this.customerBankInfo.getWorkflowId(),
					this.customerBankInfo.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"CustomerBankInfoDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCustomerBankInfo());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerBankInfoDialog.onClose();
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
		this.bankName.setTextBoxWidth(117);
		this.bankName.setModuleName("BankDetail");
		this.bankName.setValueColumn("BankCode");
		this.bankName.setDescColumn("BankName");
		this.bankName.setValidateColumns(new String[] { "BankCode" });
		
		this.accountType.setMaxlength(8);
		this.accountType.setMandatoryStyle(true);
		this.accountType.setTextBoxWidth(110);
		this.accountType.setModuleName("LovFieldDetail");
		this.accountType.setValueColumn("FieldCodeValue");
		this.accountType.setDescColumn("ValueDesc");
		this.accountType.setValidateColumns(new String[] { "FieldCodeValue" });

		Filter filter[] = new Filter[1];
		filter[0] = new Filter("FieldCode", "ACC_TYPE", Filter.OP_EQUAL);
		this.accountType.setFilters(filter);
		
		this.accountType.setMaxlength(8);
		
		if (StringUtils.isNotBlank(this.customerBankInfo.getBankCode())) {
			accNoLength = bankDetailService.getAccNoLengthByCode(this.customerBankInfo.getBankCode());
		}
		
		if (accNoLength!=0) {
			this.accountNumber.setMaxlength(accNoLength);
		}else{
			this.accountNumber.setMaxlength(LengthConstants.LEN_ACCOUNT);
		}

		//###_0.2
		this.creditTranAmt.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.creditTranAmt.setScale(finFormatter);
		
		this.creditTranAvg.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.creditTranAvg.setScale(finFormatter);
		
		this.debitTranAmt.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.debitTranAmt.setScale(finFormatter);
		
		this.cashDepositAmt.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.cashDepositAmt.setScale(finFormatter);
		
		this.cashWithdrawalAmt.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.cashWithdrawalAmt.setScale(finFormatter);
	
		this.chqDepositAmt.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.chqDepositAmt.setScale(finFormatter);
	
		this.chqIssueAmt.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.chqIssueAmt.setScale(finFormatter);
		
		this.eodBalMin.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.eodBalMin.setScale(finFormatter);
		
		this.eodBalMax.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.eodBalMax.setScale(finFormatter);
		
		this.eodBalAvg.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.eodBalAvg.setScale(finFormatter);
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
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
		getUserWorkspace().allocateAuthorities("CustomerBankInfoDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerBankInfoDialog);
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
		doWriteBeanToComponents(this.customerBankInfo.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerBankInfo
	 *            CustomerBankInfo
	 */
	public void doWriteBeanToComponents(CustomerBankInfo aCustomerBankInfo) {
		logger.debug("Entering");

		if(aCustomerBankInfo.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerBankInfo.getCustID());
		}
		this.bankName.setValue(aCustomerBankInfo.getBankName());
		this.bankName.setDescription(StringUtils.trimToEmpty(aCustomerBankInfo.getLovDescBankName()));
		this.accountNumber.setValue(aCustomerBankInfo.getAccountNumber());
		this.accountType.setValue(aCustomerBankInfo.getAccountType());
		this.accountType.setDescription(StringUtils.trimToEmpty(aCustomerBankInfo.getLovDescAccountType()));
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomerBankInfo.getLovDescCustCIF()));
		this.custShrtName.setValue(StringUtils.trimToEmpty(aCustomerBankInfo.getLovDescCustShrtName()));
		this.salaryAccount.setChecked(aCustomerBankInfo.isSalaryAccount());
		if(CustomerBankInfoList != null){
			for(CustomerBankInfo customerBankInfo : CustomerBankInfoList){
				if(customerBankInfo.isSalaryAccount()){
					this.salaryAccount.setDisabled(true);
				}
			}
		}
		if (aCustomerBankInfo.isSalaryAccount()) {
			this.salaryAccount.setDisabled(false);
		}
		
		this.creditTranNo.setValue(aCustomerBankInfo.getCreditTranNo());
		this.creditTranAmt.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getCreditTranAmt(),finFormatter));
		this.creditTranAvg.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getCreditTranAvg(),finFormatter));
		this.debitTranNo.setValue(aCustomerBankInfo.getDebitTranNo());
		this.debitTranAmt.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getDebitTranAmt(),finFormatter));
		this.cashDepositNo.setValue(aCustomerBankInfo.getCashDepositNo());
		this.cashDepositAmt.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getCashDepositAmt(),finFormatter));
		this.cashWithdrawalNo.setValue(aCustomerBankInfo.getCashWithdrawalNo());
		this.cashWithdrawalAmt.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getCashWithdrawalAmt(),finFormatter));
		this.chqDepositNo.setValue(aCustomerBankInfo.getChqDepositNo());
		this.chqDepositAmt.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getChqDepositAmt(),finFormatter));
		this.chqIssueNo.setValue(aCustomerBankInfo.getChqIssueNo());
		this.chqIssueAmt.setValue(aCustomerBankInfo.getChqIssueAmt());
		this.inwardChqBounceNo.setValue(aCustomerBankInfo.getInwardChqBounceNo());
		this.outwardChqBounceNo.setValue(aCustomerBankInfo.getOutwardChqBounceNo());
		this.eodBalMin.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getEodBalMin(),finFormatter));
		this.eodBalMax.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getEodBalMax(),finFormatter));
		this.eodBalAvg.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getEodBalAvg(),finFormatter));
		
		this.recordStatus.setValue(aCustomerBankInfo.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerBankInfo
	 */
	public void doWriteComponentsToBean(CustomerBankInfo aCustomerBankInfo) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerBankInfo.setLovDescCustCIF(this.custCIF.getValue());
			aCustomerBankInfo.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		/*
		try {
			aCustomerBankInfo.setBankId(Long.valueOf(this.bankName.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}*/
		
		try {
			aCustomerBankInfo.setLovDescBankName(this.bankName.getDescription());
			aCustomerBankInfo.setBankName(this.bankName.getValidatedValue());//TODO: change it to name and increase the size in db
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerBankInfo.setAccountNumber(PennantApplicationUtil.unFormatAccountNumber(this.accountNumber.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomerBankInfo.setLovDescAccountType(this.accountType.getDescription());
			aCustomerBankInfo.setAccountType(this.accountType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setSalaryAccount(this.salaryAccount.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerBankInfo.setCreditTranNo(this.creditTranNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCreditTranAmt(PennantAppUtil.unFormateAmount(this.creditTranAmt.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCreditTranAvg(PennantAppUtil.unFormateAmount(this.creditTranAvg.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setDebitTranNo(this.debitTranNo.intValue());
		} catch (WrongValueException we) {
			
		}
		try {
			aCustomerBankInfo.setDebitTranAmt(PennantAppUtil.unFormateAmount(this.debitTranAmt.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCashDepositNo(this.cashDepositNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCashDepositAmt(PennantAppUtil.unFormateAmount(this.cashDepositAmt.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCashWithdrawalNo(this.cashWithdrawalNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCashWithdrawalAmt(PennantAppUtil.unFormateAmount(this.cashWithdrawalAmt.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setChqDepositNo(this.chqDepositNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setChqDepositAmt(PennantAppUtil.unFormateAmount(this.chqDepositAmt.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setChqIssueNo(this.chqIssueNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setChqIssueAmt(PennantAppUtil.unFormateAmount(this.chqIssueAmt.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setInwardChqBounceNo(this.inwardChqBounceNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setOutwardChqBounceNo(this.outwardChqBounceNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setEodBalMin(PennantAppUtil.unFormateAmount(this.eodBalMin.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setEodBalMax(PennantAppUtil.unFormateAmount(this.eodBalMax.getValidateValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setEodBalAvg(PennantAppUtil.unFormateAmount(this.eodBalAvg.getValidateValue(),finFormatter));
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

		aCustomerBankInfo.setRecordStatus(this.recordStatus.getValue());
		setCustomerBankInfo(aCustomerBankInfo);
		logger.debug("Leaving");
	}
	
	/**
	 * when clicks on button "CommitmentRef"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void onFulfill$bankName(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		Object dataObject = this.bankName.getObject();

		if (dataObject instanceof String) {
			this.bankName.setValue(dataObject.toString(), "");
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankName.setValue(details.getBankCode(),
						details.getBankName());
				if (StringUtils.isNotBlank(details.getBankCode())) {
					accNoLength = details.getAccNoLength();
				}
				
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerBankInfo
	 * @throws Exception
	 */
	public void doShowDialog(CustomerBankInfo aCustomerBankInfo) throws Exception {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custID.focus();
		} else {
			this.bankName.focus();
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
			doWriteBeanToComponents(aCustomerBankInfo);

            doCheckEnquiry();
            if(this.accountNumber.isReadonly()){
            	this.accountNumber.setTooltiptext(this.accountNumber.getValue());
            }
            if(isNewCustomer()){
				this.window_CustomerBankInfoDialog.setHeight("55%");
				this.window_CustomerBankInfoDialog.setWidth("60%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerBankInfoDialog.doModal() ;
			}else{
				this.window_CustomerBankInfoDialog.setWidth("100%");
				this.window_CustomerBankInfoDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerBankInfoDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if(PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)){
			this.custID.setReadonly(true);
			this.custCIF.setReadonly(true);
			this.bankName.setReadonly(true);
			this.accountType.setReadonly(true);
			this.accountNumber.setReadonly(true);
			
			this.creditTranNo.setReadonly(true);
			this.creditTranAmt.setReadonly(true);
			this.creditTranAvg.setReadonly(true);
			this.debitTranNo.setReadonly(true);
			this.debitTranAmt.setReadonly(true);
			this.cashDepositNo.setReadonly(true);
			this.cashDepositAmt.setReadonly(true);
			this.cashWithdrawalNo.setReadonly(true);
			this.cashWithdrawalAmt.setReadonly(true);
			this.chqDepositNo.setReadonly(true);
			this.chqDepositAmt.setReadonly(true);
			this.chqIssueNo.setReadonly(true);
			this.chqIssueAmt.setReadonly(true);
			this.inwardChqBounceNo.setReadonly(true);
			this.outwardChqBounceNo.setReadonly(true);
			this.eodBalMin.setReadonly(true);
			this.eodBalMax.setReadonly(true);
			this.eodBalAvg.setReadonly(true);
			
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.accountNumber.isReadonly()) {
			this.accountNumber
					.setConstraint(new PTStringValidator(
							Labels.getLabel("label_CustomerBankInfoDialog_AccountNumber.value"),
							PennantRegularExpressions.REGEX_ACCOUNTNUMBER,
							true));
			
		}
		
		if(!this.creditTranNo.isDisabled()){
			this.creditTranNo.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerBankInfoDialog_CreditTranNo.value"), false, false));
		}
		if(!this.creditTranAmt.isDisabled()){
			this.creditTranAmt.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerBankInfoDialog_CreditTranAmt.value"), 0, false, false));
		}
		if(!this.creditTranAvg.isDisabled()){
			this.creditTranAvg.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerBankInfoDialog_CreditTranAvg.value"), 0, false, false));
		}
		if(!this.debitTranNo.isDisabled()){
			this.debitTranNo.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerBankInfoDialog_DebitTranNo.value"), false, false));
		}
		if(!this.debitTranAmt.isDisabled()){
			this.debitTranAmt.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerBankInfoDialog_DebitTranAmt.value"), 0, false, false));
		}
		if(!this.cashDepositNo.isDisabled()){
			this.cashDepositNo.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerBankInfoDialog_CashDepositNo.value"), false, false));
		}
		if(!this.cashDepositAmt.isDisabled()){
			this.cashDepositAmt.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerBankInfoDialog_CashDepositAmt.value"), 0, false, false));
		}
		if(!this.cashWithdrawalNo.isDisabled()){
			this.cashWithdrawalNo.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerBankInfoDialog_CashWithdrawalNo.value"), false, false));
		}
		if(!this.cashWithdrawalAmt.isDisabled()){
			this.cashWithdrawalAmt.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerBankInfoDialog_CashWithdrawalAmt.value"), 0, false, false));
		}
		if(!this.chqDepositNo.isDisabled()){
			this.chqDepositNo.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerBankInfoDialog_ChqDepositNo.value"), false, false));
		}
		if(!this.chqDepositAmt.isDisabled()){
			this.chqDepositAmt.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerBankInfoDialog_ChqDepositAmt.value"), 0, false, false));
		}
		if(!this.chqIssueNo.isDisabled()){
			this.chqIssueNo.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerBankInfoDialog_ChqIssueNo.value"), false, false));
		}
		if(!this.chqIssueAmt.isDisabled()){
			this.chqIssueAmt.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerBankInfoDialog_ChqIssueAmt.value"), 0, false, false));
		}
		if(!this.inwardChqBounceNo.isDisabled()){
			this.inwardChqBounceNo.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerBankInfoDialog_InwardChqBounceNo.value"), false, false));
		}
		if(!this.outwardChqBounceNo.isDisabled()){
			this.outwardChqBounceNo.setConstraint(new PTNumberValidator(Labels.getLabel("label_CustomerBankInfoDialog_OutwardChqBounceNo.value"), false, false));
		}
		if(!this.eodBalMin.isDisabled()){
			this.eodBalMin.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerBankInfoDialog_EodBalMin.value"), 0, false, false));
		}
		if(!this.eodBalMax.isDisabled()){
			this.eodBalMax.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerBankInfoDialog_EodBalMax.value"), 0, false, false));
		}
		if(!this.eodBalAvg.isDisabled()){
			this.eodBalAvg.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerBankInfoDialog_EodBalAvg.value"), 0, false, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.accountNumber.setConstraint("");

		this.creditTranNo.setConstraint("");
		this.creditTranAmt.setConstraint("");
		this.creditTranAvg.setConstraint("");
		this.debitTranNo.setConstraint("");
		this.debitTranAmt.setConstraint("");
		this.cashDepositNo.setConstraint("");
		this.cashDepositAmt.setConstraint("");
		this.cashWithdrawalNo.setConstraint("");
		this.cashWithdrawalAmt.setConstraint("");
		this.chqDepositNo.setConstraint("");
		this.chqDepositAmt.setConstraint("");
		this.chqIssueNo.setConstraint("");
		this.chqIssueAmt.setConstraint("");
		this.inwardChqBounceNo.setConstraint("");
		this.outwardChqBounceNo.setConstraint("");
		this.eodBalMin.setConstraint("");
		this.eodBalMax.setConstraint("");
		this.eodBalAvg.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.bankName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerBankInfoDialog_BankName.value"),null,true,true));
		this.accountType.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerBankInfoDialog_AccountType.value"),null,true,true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.bankName.setConstraint("");
		this.accountType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.accountNumber.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.accountType.setErrorMessage("");

		this.creditTranNo.setErrorMessage("");
		this.creditTranAmt.setErrorMessage("");
		this.creditTranAvg.setErrorMessage("");
		this.debitTranNo.setErrorMessage("");
		this.debitTranAmt.setErrorMessage("");
		this.cashDepositNo.setErrorMessage("");
		this.cashDepositAmt.setErrorMessage("");
		this.cashWithdrawalNo.setErrorMessage("");
		this.cashWithdrawalAmt.setErrorMessage("");
		this.chqDepositNo.setErrorMessage("");
		this.chqDepositAmt.setErrorMessage("");
		this.chqIssueNo.setErrorMessage("");
		this.chqIssueAmt.setErrorMessage("");
		this.inwardChqBounceNo.setErrorMessage("");
		this.outwardChqBounceNo.setErrorMessage("");
		this.eodBalMin.setErrorMessage("");
		this.eodBalMax.setErrorMessage("");
		this.eodBalAvg.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a CustomerBankInfo object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerBankInfo aCustomerBankInfo = new CustomerBankInfo();
		BeanUtils.copyProperties(getCustomerBankInfo(), aCustomerBankInfo);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_CustomerBankInfoDialog_BankName.value")+" : "+aCustomerBankInfo.getBankName()+","+ 
				Labels.getLabel("label_CustomerBankInfoDialog_AccountNumber.value")+" : "+aCustomerBankInfo.getAccountNumber(); 
		
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerBankInfo.getRecordType())) {
				aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion() + 1);
				aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(!isFinanceProcess && getCustomerDialogCtrl() != null &&  
						getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
					aCustomerBankInfo.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()) {
					aCustomerBankInfo.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerBankInfo, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerBankInfoDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getCustomerDialogCtrl().doFillCustomerBankInfoDetails(this.CustomerBankInfoList);
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
			this.bankName.setReadonly(isReadOnly("CustomerBankInfoDialog_BankName"));
			this.bankName.setMandatoryStyle(!isReadOnly("CustomerBankInfoDialog_BankName"));
			this.accountNumber.setReadonly(isReadOnly("CustomerBankInfoDialog_AccountNumber"));
			
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.bankName.setReadonly(true);
			this.accountNumber.setReadonly(true);
			
		}
		this.salaryAccount.setDisabled(isReadOnly("CustomerBankInfoDialog_SalaryAccount"));
		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.accountType.setReadonly(isReadOnly("CustomerBankInfoDialog_AccountType"));

		this.creditTranNo.setReadonly(isReadOnly("CustomerBankInfoDialog_CreditTranNo"));
		this.creditTranAmt.setReadonly(isReadOnly("CustomerBankInfoDialog_CreditTranAmt"));
		this.creditTranAvg.setReadonly(isReadOnly("CustomerBankInfoDialog_CreditTranAvg"));
		this.debitTranNo.setReadonly(isReadOnly("CustomerBankInfoDialog_DebitTranNo"));
		this.debitTranAmt.setReadonly(isReadOnly("CustomerBankInfoDialog_DebitTranAmt"));
		this.cashDepositNo.setReadonly(isReadOnly("CustomerBankInfoDialog_CashDepositNo"));
		this.cashDepositAmt.setReadonly(isReadOnly("CustomerBankInfoDialog_CashDepositAmt"));
		this.cashWithdrawalNo.setReadonly(isReadOnly("CustomerBankInfoDialog_CashWithdrawalNo"));
		this.cashWithdrawalAmt.setReadonly(isReadOnly("CustomerBankInfoDialog_CashWithdrawalAmt"));
		this.chqDepositNo.setReadonly(isReadOnly("CustomerBankInfoDialog_ChqDepositNo"));
		this.chqDepositAmt.setReadonly(isReadOnly("CustomerBankInfoDialog_ChqDepositAmt"));
		this.chqIssueNo.setReadonly(isReadOnly("CustomerBankInfoDialog_ChqIssueNo"));
		this.chqIssueAmt.setReadonly(isReadOnly("CustomerBankInfoDialog_ChqIssueAmt"));
		this.inwardChqBounceNo.setReadonly(isReadOnly("CustomerBankInfoDialog_InwardChqBounceNo"));
		this.outwardChqBounceNo.setReadonly(isReadOnly("CustomerBankInfoDialog_OutwardChqBounceNo"));
		this.eodBalMin.setReadonly(isReadOnly("CustomerBankInfoDialog_EodBalMin"));
		this.eodBalMax.setReadonly(isReadOnly("CustomerBankInfoDialog_EodBalMax"));
		this.eodBalAvg.setReadonly(isReadOnly("CustomerBankInfoDialog_EodBalAvg"));


		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerBankInfo.isNewRecord()) {
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
		this.accountType.setReadonly(true);
		this.accountNumber.setReadonly(true);

		this.creditTranNo.setReadonly(true);
		this.creditTranAmt.setReadonly(true);
		this.creditTranAvg.setReadonly(true);
		this.debitTranNo.setReadonly(true);
		this.debitTranAmt.setReadonly(true);
		this.cashDepositNo.setReadonly(true);
		this.cashDepositAmt.setReadonly(true);
		this.cashWithdrawalNo.setReadonly(true);
		this.cashWithdrawalAmt.setReadonly(true);
		this.chqDepositNo.setReadonly(true);
		this.chqDepositAmt.setReadonly(true);
		this.chqIssueNo.setReadonly(true);
		this.chqIssueAmt.setReadonly(true);
		this.inwardChqBounceNo.setReadonly(true);
		this.outwardChqBounceNo.setReadonly(true);
		this.eodBalMin.setReadonly(true);
		this.eodBalMax.setReadonly(true);
		this.eodBalAvg.setReadonly(true);
		

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
		this.accountType.setValue("");
		this.accountType.setDescription("");
		this.accountNumber.setValue("");

		this.creditTranNo.setValue(0);
		this.creditTranAmt.setValue("");
		this.creditTranAvg.setValue("");
		this.debitTranNo.setValue(0);
		this.debitTranAmt.setValue("");
		this.cashDepositNo.setValue(0);
		this.cashDepositAmt.setValue("");
		this.cashWithdrawalNo.setValue(0);
		this.cashWithdrawalAmt.setValue("");
		this.chqDepositNo.setValue(0);
		this.chqDepositAmt.setValue("");
		this.chqIssueNo.setValue(0);
		this.chqIssueAmt.setValue("");
		this.inwardChqBounceNo.setValue(0);
		this.outwardChqBounceNo.setValue(0);
		this.eodBalMin.setValue("");
		this.eodBalMax.setValue("");
		this.eodBalAvg.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerBankInfo aCustomerBankInfo = new CustomerBankInfo();
		BeanUtils.copyProperties(getCustomerBankInfo(), aCustomerBankInfo);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doClearMessage();
		doSetValidation();
		// fill the CustomerBankInfo object with the components data
		doWriteComponentsToBean(aCustomerBankInfo);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerBankInfo.isNew();
		String tranType = "";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerBankInfo.getRecordType())){
				aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion()+1);
				if(isNew){
					aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerBankInfo.setNewRecord(true);
				}
			}
		}else{

			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerBankInfo.setVersion(1);
					aCustomerBankInfo.setRecordType(PennantConstants.RCD_ADD);					
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isBlank(aCustomerBankInfo.getRecordType())){
					aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion()+1);
					aCustomerBankInfo.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerBankInfo, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerBankInfoDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getCustomerDialogCtrl().doFillCustomerBankInfoDetails(this.CustomerBankInfoList);
				closeDialog();
			}
		
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	
	private AuditHeader newFinanceCustomerProcess(CustomerBankInfo aCustomerBankInfo,String tranType){
		logger.debug("Entering");
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aCustomerBankInfo, tranType);
		CustomerBankInfoList = new ArrayList<CustomerBankInfo>();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		
		valueParm[0] = String.valueOf(aCustomerBankInfo.getLovDescCustCIF());
		valueParm[1] = aCustomerBankInfo.getAccountNumber();
		
		errParm[0] = PennantJavaUtil.getLabel("label_CustomerBankInfoDialog_CustID.value") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("listheader_CustomerBankInfo_AccountNumber.label")+ ":" + valueParm[1];
		
		if(getCustomerDialogCtrl().getCustomerBankInfoDetailList()!=null && getCustomerDialogCtrl().getCustomerBankInfoDetailList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getCustomerBankInfoDetailList().size(); i++) {
				CustomerBankInfo customerBankInfo = getCustomerDialogCtrl().getCustomerBankInfoDetailList().get(i);
				
				
				if(aCustomerBankInfo.getAccountNumber().equals(customerBankInfo.getAccountNumber())
						&& aCustomerBankInfo.getBankName().equals(customerBankInfo.getBankName())){ 
					// Both Current and Existing list rating same
					
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,"41001",
										errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							CustomerBankInfoList.add(aCustomerBankInfo);
						}else if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							CustomerBankInfoList.add(aCustomerBankInfo);
						}else if(aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerBankInfoList().size(); j++) {
								CustomerBankInfo email =  getCustomerDialogCtrl().getCustomerDetails().getCustomerBankInfoList().get(j);
								if(email.getCustID() == aCustomerBankInfo.getCustID() && 
										email.getBankName().equals(aCustomerBankInfo.getBankName())){
									CustomerBankInfoList.add(email);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							CustomerBankInfoList.add(customerBankInfo);
						}
					}
				} else {
					    CustomerBankInfoList.add(customerBankInfo);
				}
			}
		}
		
		if (!recordAdded) {
			CustomerBankInfoList.add(aCustomerBankInfo);
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
	 * @param aCustomerBankInfo
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerBankInfo aCustomerBankInfo, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,aCustomerBankInfo.getBefImage(), aCustomerBankInfo);
		return new AuditHeader(getReference(), String.valueOf(aCustomerBankInfo.getCustID()), null, null, 
				auditDetail, aCustomerBankInfo.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerBankInfoDialog, auditHeader);
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
		doShowNotes(this.customerBankInfo);
	}
	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustomerBankInfo().getCustID()+ PennantConstants.KEY_SEPERATOR
		+ getCustomerBankInfo().getBankName();
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

	public CustomerBankInfo getCustomerBankInfo() {
		return this.customerBankInfo;
	}
	public void setCustomerBankInfo(CustomerBankInfo customerBankInfo) {
		this.customerBankInfo = customerBankInfo;
	}

	public void setCustomerEmails(List<CustomerBankInfo> customerEmails) {
		this.CustomerBankInfoList = customerEmails;
	}
	public List<CustomerBankInfo> getCustomerEmails() {
		return CustomerBankInfoList;
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
	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public CustomerViewDialogCtrl getCustomerViewDialogCtrl() {
		return customerViewDialogCtrl;
	}

	public void setCustomerViewDialogCtrl(CustomerViewDialogCtrl customerViewDialogCtrl) {
		this.customerViewDialogCtrl = customerViewDialogCtrl;
	}
}
