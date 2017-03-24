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
 * FileName    		:  MandateDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.mandate.mandate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/mandateDialog.zul file. <br>
 * ************************************************************<br>
 */
public class MandateDialogCtrl extends GFCBaseCtrl<Mandate> {

	private static final long						serialVersionUID		= 1L;
	private final static Logger						logger					= Logger.getLogger(MandateDialogCtrl.class);

	protected Window								window_MandateDialog;
	protected ExtendedCombobox						custID;
	protected ExtendedCombobox						mandateRef;
	protected Combobox								mandateType;
	protected ExtendedCombobox						bankBranchID;
	protected Textbox								bank;
	protected Textbox								city;
	protected Textbox								micr;
	protected Textbox								ifsc;
	protected Textbox								accNumber;
	protected Textbox								accHolderName;
	protected Datebox								inputDate;
	protected Textbox								jointAccHolderName;
	protected Combobox								accType;
	protected Checkbox								openMandate;
	protected Datebox								startDate;
	protected Datebox								expiryDate;
	protected CurrencyBox							maxLimit;
	protected FrequencyBox							periodicity;
	protected Textbox								phoneCountryCode;
	protected Textbox								phoneAreaCode;
	protected Textbox								phoneNumber;
	protected Combobox								status;
	protected Textbox								approvalID;
	protected Groupbox								gb_basicDetails;
	protected Groupbox								gb_enquiry;
	protected Checkbox								useExisting;
	protected Checkbox								active;
	protected Textbox								reason;
	protected Space									space_Reason;
	protected Space									space_Expirydate;

	protected Row									mandateRow;

	private boolean									enqModule				= false;
	private boolean									flag					= false;

	// not auto wired vars
	private Mandate									mandate;
	private transient MandateListCtrl				mandateListCtrl;
	private transient MandateRegistrationListCtrl	mandateRegistrationListCtrl;

	private boolean									notes_Entered			= false;

	// Button controller for the CRUD buttons
	private transient final String					btnCtroller_ClassPrefix	= "button_MandateDialog_";
	protected Button								btnHelp;
	protected Button								btnProcess;
	protected Button								btnView;

	// ServiceDAOs / Domain Classes
	private transient MandateService				mandateService;

	private final List<ValueLabel>					mandateTypeList			= PennantStaticListUtil
																					.getMandateTypeList();
	private final List<ValueLabel>					accTypeList				= PennantStaticListUtil.getAccTypeList();
	private final List<ValueLabel>					statusTypeList			= PennantStaticListUtil.getStatusTypeList();

	public transient int							ccyFormatter			= 0;
	private boolean									registration			= false;
	private boolean									maintain				= false;
	/* loan related declrations */
	private boolean									fromLoan				= false;
	protected North									north_mandate;
	protected Groupbox								finBasicdetails;
	private FinBasicDetailsCtrl						finBasicDetailsCtrl;
	private Object									financeMainDialogCtrl	= null;
	FinanceDetail									financeDetail;
	private FinanceMain								financemain;
	Tab												parenttab				= null;
//	long											mandateID				= 0;
	protected Row									rowStatus;
	protected int									accNoLength;
	private transient BankDetailService				bankDetailService;


	/**
	 * default constructor.<br>
	 */
	public MandateDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MandateDialog";
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Mandate object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_MandateDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_MandateDialog);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			}

			if (arguments.containsKey("fromLoan")) {
				fromLoan = (Boolean) arguments.get("fromLoan");
			}

			if (arguments.containsKey("registration")) {
				registration = (Boolean) arguments.get("registration");
			}
			if (arguments.containsKey("maintain")) {
				maintain = (Boolean) arguments.get("maintain");
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("mandate")) {
				this.mandate = (Mandate) arguments.get("mandate");
				Mandate befImage = new Mandate();
				BeanUtils.copyProperties(this.mandate, befImage);
				this.mandate.setBefImage(befImage);
				setMandate(this.mandate);
			} else {
				setMandate(null);
			}

			// ***** Loan Origination ********//
			this.mandateRow.setVisible(fromLoan);
			if (fromLoan) {
				if (arguments.containsKey("financeDetail")) {
					financeDetail = (FinanceDetail) arguments.get("financeDetail");
					financemain = financeDetail.getFinScheduleData().getFinanceMain();
					if (financeDetail.getMandate() != null) {
						this.mandate = financeDetail.getMandate();

					} else {
						this.mandate = new Mandate();
						mandate.setNewRecord(true);
						mandate.setCustID(financemain.getCustID());
						mandate.setCustCIF(financemain.getLovDescCustCIF());
						mandate.setMandateType(financemain.getFinRepayMethod());
					}
					this.mandate.setWorkflowId(0);
				}

				if (arguments.containsKey("roleCode")) {
					getUserWorkspace().allocateRoleAuthorities(arguments.get("roleCode").toString(), "MandateDialog");
				}

				if (arguments.containsKey("tab")) {
					parenttab = (Tab) arguments.get("tab");
				}

				if (arguments.containsKey("finHeaderList")) {
					appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
				} else {
					appendFinBasicDetails(null);
				}

				if (arguments.containsKey("financeMainDialogCtrl")) {
					setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
				}
			}
			// *****************************//

			doLoadWorkFlow(this.mandate.isWorkflow(), this.mandate.getWorkflowId(), this.mandate.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "MandateDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the flagListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete flag here.
			if (arguments.containsKey("mandateListCtrl")) {
				setMandateListCtrl((MandateListCtrl) arguments.get("mandateListCtrl"));
			} else if (arguments.containsKey("mandateRegistrationListCtrl")) {
				setMandateRegistrationListCtrl((MandateRegistrationListCtrl) arguments
						.get("mandateRegistrationListCtrl"));
			} else {
				setMandateRegistrationListCtrl(null);
			}
			if (getMandate() != null) {
				ccyFormatter = CurrencyUtil.getFormat(getMandate().getMandateCcy());
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMandate());
		} catch (Exception e) {
			createException(window_MandateDialog, e);
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.accNumber.setMaxlength(50);
		this.accHolderName.setMaxlength(50);
		this.jointAccHolderName.setMaxlength(50);
		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.expiryDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.inputDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.maxLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.maxLimit.setScale(ccyFormatter);
		this.maxLimit.setTextBoxWidth(171);
		this.maxLimit.setMandatory(true);

		this.periodicity.setMandatoryStyle(true);
		this.phoneCountryCode.setMaxlength(3);
		this.phoneCountryCode.setWidth("50px");
		this.phoneAreaCode.setMaxlength(3);
		this.phoneAreaCode.setWidth("50px");
		this.phoneNumber.setMaxlength(8);
		this.phoneNumber.setWidth("100px");
		this.approvalID.setMaxlength(50);

		this.custID.setModuleName("Customer");
		this.custID.setMandatoryStyle(true);
		this.custID.setValueColumn("CustCIF");
		this.custID.setDescColumn("CustShrtName");
		this.custID.setDisplayStyle(2);
		this.custID.setValidateColumns(new String[] { "CustCIF" });

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("BranchCode");
		this.bankBranchID.setDescColumn("BranchDesc");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "BranchCode" });

		this.mandateRef.setModuleName("Mandate");
		this.mandateRef.setMandatoryStyle(true);
		this.mandateRef.setValueColumn("MandateID");
		this.mandateRef.setDescColumn("MandateRef");
		this.mandateRef.setDisplayStyle(2);
		this.mandateRef.setValidateColumns(new String[] { "MandateID" });
		addMandateFiletrs(null);
		this.active.setChecked(true);
		this.reason.setMaxlength(60);
		
		if(StringUtils.isNotBlank(this.mandate.getBankCode())){
			accNoLength = getBankDetailService().getAccNoLengthByCode(this.mandate.getBankCode());
		}
		setStatusDetails();
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnSave"));
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		logger.debug("Leaving");
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_MandateDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnProcess(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "View" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnReason(Event event) {
		logger.debug("Entering");
		HashMap<String, Object> arg = new HashMap<String, Object>();
		arg.put("mandateId", mandate.getMandateID());

		Executions.createComponents("/WEB-INF/pages/Mandate/MandateStatusList.zul", null, arg);
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		doClose(this.btnSave.isVisible());
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
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(
					getNotes("Mandate", String.valueOf(getMandate().getMandateID()), getMandate().getVersion()), this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e);
			MessageUtil.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());

	}

	public void onCheck$useExisting(Event event) {
		logger.debug("Entering" + event.toString());
		doClearMessage();
		useExisting();

		if (!this.useExisting.isChecked() && financeDetail != null) {
			Mandate man = financeDetail.getMandate();
			if (man != null && !man.isUseExisting()) {
				doWriteData(man);
			} else{
				this.mandateRef.setAttribute("mandateID", Long.MIN_VALUE);
				this.periodicity.setValue(MandateConstants.MANDATE_DEFAULT_FRQ);
				this.startDate.setValue(DateUtility.getAppDate());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$openMandate(Event event) {
		logger.debug("Entering" + event.toString());
		checkOpenMandate();
		logger.debug("Leaving" + event.toString());
	}

	private void checkOpenMandate() {
		if (this.openMandate.isChecked()) {
			readOnlyComponent(true, this.expiryDate);
			this.expiryDate.setValue(null);
			this.space_Expirydate.setSclass("");
		} else {
			if (this.useExisting.isChecked()) {
				readOnlyComponent(true, this.expiryDate);
			} else {
				readOnlyComponent(isReadOnly("MandateDialog_ExpiryDate"), this.expiryDate);
				this.space_Expirydate.setSclass("mandatory");
			}
		}
	}

	private void useExisting() {
		boolean checked = this.useExisting.isChecked();
		if (checked) {
			readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.mandateRef);
			readOnlyComponent(true, this.bankBranchID);
			readOnlyComponent(true, this.accNumber);
			readOnlyComponent(true, this.accHolderName);
			readOnlyComponent(true, this.jointAccHolderName);
			readOnlyComponent(true, this.accType);
			readOnlyComponent(true, this.maxLimit);
			readOnlyComponent(true, this.periodicity);
			readOnlyComponent(true, this.phoneCountryCode);
			readOnlyComponent(true, this.phoneAreaCode);
			readOnlyComponent(true, this.phoneNumber);
			readOnlyComponent(true, this.startDate);
			readOnlyComponent(true, this.expiryDate);
			readOnlyComponent(true, this.openMandate);
			readOnlyComponent(true, this.approvalID);
		} else {
			readOnlyComponent(true, this.mandateRef);
			readOnlyComponent(isReadOnly("MandateDialog_BankBranchID"), this.bankBranchID);
			readOnlyComponent(isReadOnly("MandateDialog_AccNumber"), this.accNumber);
			readOnlyComponent(isReadOnly("MandateDialog_AccHolderName"), this.accHolderName);
			readOnlyComponent(isReadOnly("MandateDialog_JointAccHolderName"), this.jointAccHolderName);
			readOnlyComponent(isReadOnly("MandateDialog_AccType"), this.accType);
			readOnlyComponent(isReadOnly("MandateDialog_MaxLimit"), this.maxLimit);
			readOnlyComponent(isReadOnly("MandateDialog_Periodicity"), this.periodicity);
			readOnlyComponent(isReadOnly("MandateDialog_PhoneCountryCode"), this.phoneCountryCode);
			readOnlyComponent(isReadOnly("MandateDialog_PhoneCountryCode"), this.phoneCountryCode);
			readOnlyComponent(isReadOnly("MandateDialog_PhoneAreaCode"), this.phoneAreaCode);
			readOnlyComponent(isReadOnly("MandateDialog_PhoneNumber"), this.phoneNumber);
			readOnlyComponent(isReadOnly("MandateDialog_StartDate"), this.startDate);
			readOnlyComponent(isReadOnly("MandateDialog_ExpiryDate"), this.expiryDate);
			readOnlyComponent(isReadOnly("MandateDialog_OpenMandate"), this.openMandate);
			readOnlyComponent(true, this.approvalID);
			this.maxLimit.setMandatory(true);
		}
		clearMandatedata();
	}

	private void clearMandatedata() {
		this.mandateRef.setValue("", "");
		this.bankBranchID.setValue("", "");
		this.accNumber.setValue("");
		this.accHolderName.setValue("");
		this.city.setValue("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.jointAccHolderName.setValue("");
		this.startDate.setText("");
		this.expiryDate.setText("");
		this.accType.setValue("");
		this.maxLimit.setValue(BigDecimal.ZERO);
		this.periodicity.setValue("");
		this.phoneCountryCode.setValue("");
		this.phoneAreaCode.setValue("");
		this.phoneNumber.setValue("");
		//Frequency
		this.approvalID.setValue("");
	}

	public void onChange$mandateType(Event event) {
		String str = this.mandateType.getSelectedItem().getValue().toString();
		this.bankBranchID.setValue("");
		this.bankBranchID.setDescription("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.city.setValue("");
		Filter filter[] = new Filter[1];
		switch (str) {
		case MandateConstants.TYPE_ECS:
			filter[0] = new Filter("ECS", "1", Filter.OP_EQUAL);
			this.bankBranchID.setFilters(filter);
			break;
		case MandateConstants.TYPE_DDM:
			filter[0] = new Filter("DDA", "1", Filter.OP_EQUAL);
			this.bankBranchID.setFilters(filter);
			break;
		case MandateConstants.TYPE_NACH:
			filter[0] = new Filter("NACH", "1", Filter.OP_EQUAL);
			this.bankBranchID.setFilters(filter);
			break;

		default:
			break;
		}

	}

	// ****************************************************************+
	// ************************ GUI operations ************************+
	// ****************************************************************+

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aMandate
	 * @throws InterruptedException
	 */
	@SuppressWarnings("rawtypes")
	public void doShowDialog(Mandate aMandate) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aMandate.isNew()) {
			this.btnCtrl.setInitNew();
			this.inputDate.setValue(DateUtility.getAppDate());
			doEdit();
			// setFocus
			this.custID.focus();
		} else {
			this.custID.setReadonly(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aMandate.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				if (fromLoan && !enqModule) {
					doEdit();
				}
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aMandate);
			doDesignByStatus(aMandate);
			doDesignByMode();
			if (fromLoan) {
				try {
					Class[] paramType = { this.getClass() };
					Object[] stringParameter = { this };
					getFinanceMainDialogCtrl().getClass().getMethod("setMandateDialogCtrl", paramType)
							.invoke(getFinanceMainDialogCtrl(), stringParameter);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				if (parenttab != null) {
					checkTabDisplay(aMandate.getMandateType(), false);
				}

			} else {
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
		}
		logger.debug("Leaving");
	}

	private void doDesignByStatus(Mandate aMandate) {
		String satsu = StringUtils.trimToEmpty(aMandate.getStatus());

		if (satsu.equals("") || satsu.equals(PennantConstants.List_Select)) {
			this.rowStatus.setVisible(false);
		} else {
			this.rowStatus.setVisible(true);
		}

		if (satsu.equals(MandateConstants.STATUS_REJECTED)) {
			readOnlyComponent(true, status);
			readOnlyComponent(true, reason);
		}

		if (satsu.equals(MandateConstants.STATUS_NEW)) {
			readOnlyComponent(true, status);
			readOnlyComponent(true, reason);
			this.reason.setValue("");
			this.rowStatus.setVisible(false);
		}

	}

	private void doDesignByMode() {
		if (enqModule) {
			this.btnCancel.setVisible(false);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
			this.gb_enquiry.setVisible(true);
			this.btnNotes.setVisible(false);
			return;
		}

		if (registration) {
			this.rowStatus.setVisible(true);
			readOnlyComponent(false, this.status);
			readOnlyComponent(false, this.reason);
			this.btnCancel.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnSave.setVisible(false);
			this.btnNotes.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnProcess.setVisible(true);
		}

		if (fromLoan) {
			this.north_mandate.setVisible(false);
			readOnlyComponent(true, this.custID);
			readOnlyComponent(true, this.mandateType);
			this.rowStatus.setVisible(false);

		}

		if (StringUtils.isNotEmpty(getMandate().getOrgReference()) && !StringUtils.equals(getMandate().getStatus(),MandateConstants.STATUS_FIN)) {
			readOnlyComponent(true, this.openMandate);
		}
		
		
		if (StringUtils.isEmpty(getMandate().getRecordType()) && StringUtils.isNotEmpty(getMandate().getRecordStatus())) {
			readOnlyComponent(true, this.mandateType);
		}

	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getMandate().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.custID.setReadonly(false);
		} else {
			this.btnCancel.setVisible(true);
			this.custID.setReadonly(true);
		}

		readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.useExisting);
		readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.mandateRef);
		readOnlyComponent(isReadOnly("MandateDialog_MandateType"), this.mandateType);
		readOnlyComponent(isReadOnly("MandateDialog_BankBranchID"), this.bankBranchID);
		readOnlyComponent(isReadOnly("MandateDialog_AccNumber"), this.accNumber);
		readOnlyComponent(isReadOnly("MandateDialog_AccHolderName"), this.accHolderName);
		readOnlyComponent(isReadOnly("MandateDialog_JointAccHolderName"), this.jointAccHolderName);
		readOnlyComponent(isReadOnly("MandateDialog_AccType"), this.accType);
		readOnlyComponent(isReadOnly("MandateDialog_OpenMandate"), this.openMandate);
		readOnlyComponent(isReadOnly("MandateDialog_StartDate"), this.startDate);
		readOnlyComponent(isReadOnly("MandateDialog_ExpiryDate"), this.expiryDate);
		readOnlyComponent(isReadOnly("MandateDialog_MaxLimit"), this.maxLimit);
		readOnlyComponent(isReadOnly("MandateDialog_Periodicity"), this.periodicity);
		readOnlyComponent(isReadOnly("MandateDialog_PhoneCountryCode"), this.phoneCountryCode);
		readOnlyComponent(isReadOnly("MandateDialog_PhoneAreaCode"), this.phoneCountryCode);
		readOnlyComponent(isReadOnly("MandateDialog_PhoneCountryCode"), this.phoneAreaCode);
		readOnlyComponent(isReadOnly("MandateDialog_PhoneNumber"), this.phoneNumber);
		readOnlyComponent(isReadOnly("MandateDialog_ApprovalID"), this.approvalID);
		readOnlyComponent(isReadOnly("MandateDialog_Status"), this.status);
		readOnlyComponent(isReadOnly("MandateDialog_Status"), this.reason);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.mandate.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving ");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || fromLoan) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");

		doWriteBeanToComponents(this.mandate.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		readOnlyComponent(true, this.custID);
		readOnlyComponent(true, this.mandateRef);
		readOnlyComponent(true, this.inputDate);
		readOnlyComponent(true, this.mandateType);
		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.accNumber);
		readOnlyComponent(true, this.accHolderName);
		readOnlyComponent(true, this.jointAccHolderName);
		readOnlyComponent(true, this.reason);
		readOnlyComponent(true, this.accType);
		readOnlyComponent(true, this.openMandate);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.expiryDate);
		readOnlyComponent(true, this.maxLimit);
		readOnlyComponent(true, this.periodicity);
		readOnlyComponent(true, this.phoneCountryCode);
		readOnlyComponent(true, this.phoneAreaCode);
		readOnlyComponent(true, this.phoneNumber);
		readOnlyComponent(true, this.status);
		readOnlyComponent(true, this.approvalID);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aMandate
	 *            Mandate
	 * @param tab
	 */
	public void doWriteBeanToComponents(Mandate aMandate) {
		logger.debug("Entering");
		this.useExisting.setChecked(aMandate.isUseExisting());
		if (fromLoan) {
			useExisting();
		} else {
			readOnlyComponent(true, this.mandateRef);
		}
		if (aMandate.getCustID() != Long.MIN_VALUE && aMandate.getCustID() != 0) {
			this.custID.setAttribute("custID", aMandate.getCustID());
			this.custID.setValue(aMandate.getCustCIF(), aMandate.getCustShrtName());
		}

		fillComboBox(this.mandateType, aMandate.getMandateType(), mandateTypeList, "");

		List<String> excludeList = new ArrayList<String>();
		if (registration) {
			excludeList.add(MandateConstants.STATUS_FIN);
			excludeList.add(MandateConstants.STATUS_NEW);
			excludeList.add(MandateConstants.STATUS_APPROVED);
			excludeList.add(MandateConstants.STATUS_AWAITCON);
			excludeList.add(MandateConstants.STATUS_HOLD);
			excludeList.add(MandateConstants.STATUS_RELEASE);
		} else if (maintain) {
			excludeList.add(MandateConstants.STATUS_FIN);
			excludeList.add(MandateConstants.STATUS_NEW);
			excludeList.add(MandateConstants.STATUS_APPROVED);
			excludeList.add(MandateConstants.STATUS_AWAITCON);
			excludeList.add(MandateConstants.STATUS_REJECTED);

			// get previous mandate status from main
			Mandate oldMnadate = getMandateService().getApprovedMandateById(aMandate.getMandateID());
			if (StringUtils.trimToEmpty(oldMnadate.getStatus()).equals(MandateConstants.STATUS_HOLD)) {
				excludeList.add(MandateConstants.STATUS_HOLD);
			} else {
				excludeList.add(MandateConstants.STATUS_RELEASE);
			}
		}
		fillComboBox(this.status, aMandate.getStatus(), statusTypeList, excludeList);
		this.reason.setValue(aMandate.getReason());
		doWriteData(aMandate);

		this.approvalID.setValue(aMandate.getApprovalID());
		this.recordStatus.setValue(aMandate.getRecordStatus());
		if (aMandate.isNew()) {
			this.inputDate.setValue(DateUtility.getAppDate());
			this.active.setChecked(true);
		} else {
			this.inputDate.setValue(aMandate.getInputDate());
		}

		logger.debug("Leaving");
	}

	private void doWriteData(Mandate aMandate) {

		ccyFormatter = CurrencyUtil.getFormat(aMandate.getMandateCcy());

		if (aMandate.isNewRecord()) {
			if (StringUtils.isEmpty(aMandate.getPeriodicity())) {
				aMandate.setPeriodicity(MandateConstants.MANDATE_DEFAULT_FRQ);
			}
			if (aMandate.getStartDate() == null) {
				aMandate.setStartDate(DateUtility.getAppDate());
			}
		}

		if (aMandate.getMandateID() != 0 && aMandate.getMandateID() != Long.MIN_VALUE) {
			this.mandateRef.setAttribute("mandateID", aMandate.getMandateID());
			this.mandateRef.setValue(String.valueOf(aMandate.getMandateID()),
					StringUtils.trimToEmpty(aMandate.getMandateRef()));
		}

		if (aMandate.getBankBranchID() != Long.MIN_VALUE && aMandate.getBankBranchID() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", aMandate.getBankBranchID());
			this.bankBranchID.setValue(StringUtils.trimToEmpty(aMandate.getBranchCode()),
					StringUtils.trimToEmpty(aMandate.getBranchDesc()));
		}
		this.city.setValue(StringUtils.trimToEmpty(aMandate.getCity()));
		this.bank.setValue(StringUtils.trimToEmpty(aMandate.getBankName()));
		this.micr.setValue(aMandate.getMICR());
		this.ifsc.setValue(aMandate.getIFSC());
		this.accNumber.setValue(aMandate.getAccNumber());
		this.accHolderName.setValue(aMandate.getAccHolderName());
		this.jointAccHolderName.setValue(aMandate.getJointAccHolderName());
		fillComboBox(this.accType, aMandate.getAccType(), accTypeList, "");
		this.openMandate.setChecked(aMandate.isOpenMandate());
		this.startDate.setValue(aMandate.getStartDate());
		this.expiryDate.setValue(aMandate.getExpiryDate());
		this.maxLimit.setValue(PennantAppUtil.formateAmount(aMandate.getMaxLimit(), ccyFormatter));
		this.periodicity.setValue(aMandate.getPeriodicity());
		this.phoneCountryCode.setValue(aMandate.getPhoneCountryCode());
		this.phoneAreaCode.setValue(aMandate.getPhoneAreaCode());
		this.phoneNumber.setValue(aMandate.getPhoneNumber());
		this.reason.setValue(aMandate.getReason());

		if (!enqModule && !registration) {
			checkOpenMandate();
		}

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aMandate
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean(Mandate aMandate, Tab tab) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		Object mandateID = this.mandateRef.getAttribute("mandateID");
		if (mandateID!=null) {
			aMandate.setMandateID((long) mandateID);
		}else{
			aMandate.setMandateID(Long.MIN_VALUE);
		}
	
		aMandate.setUseExisting(this.useExisting.isChecked());
		// Customer ID
		try {
			this.custID.getValidatedValue();
			Object obj = this.custID.getAttribute("custID");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aMandate.setCustID(Long.valueOf((obj.toString())));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Mandate Reference
		try {

			String ref = this.mandateRef.getValidatedValue();
			if (fromLoan && this.useExisting.isChecked() && StringUtils.isEmpty(ref)) {
				throw new WrongValueException(this.mandateRef.getTextbox(), Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_MandateDialog_MandateRef.value") }));
			}

			Object obj = this.mandateRef.getAttribute("mandateRef");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aMandate.setMandateRef(obj.toString());
				}
			} else {
				aMandate.setMandateRef("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Mandate Type
		try {
			aMandate.setMandateType(this.mandateType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Bank Branch ID
		try {
			this.bankBranchID.getValidatedValue();
			Object obj = this.bankBranchID.getAttribute("bankBranchID");
			if (obj != null) {
				aMandate.setBankBranchID(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Account Number
		try {
			aMandate.setAccNumber(PennantApplicationUtil.unFormatAccountNumber(this.accNumber.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Account Holder Name
		try {
			aMandate.setAccHolderName(this.accHolderName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Joint Account Holder Name
		try {
			aMandate.setJointAccHolderName(this.jointAccHolderName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Account Type
		try {
			this.accType.getValue();
			if (this.accType.getSelectedItem() != null && this.accType.getSelectedItem().getValue() != null) {
				aMandate.setAccType(this.accType.getSelectedItem().getValue().toString());
			} else {
				aMandate.setAccType("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Open Mandate
		try {
			aMandate.setOpenMandate(this.openMandate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Start Date
		try {
			aMandate.setStartDate(this.startDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Expiry Date
		try {
			aMandate.setExpiryDate(this.expiryDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Max Limit
		try {
			aMandate.setMaxLimit(PennantAppUtil.unFormateAmount(this.maxLimit.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Periodicity
		try {
			if (this.periodicity.isValidComboValue()) {
				aMandate.setPeriodicity(this.periodicity.getValue() == null ? "" : this.periodicity.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Phone Country Code
		try {
			aMandate.setPhoneCountryCode(this.phoneCountryCode.getValue());
			aMandate.setPhoneAreaCode(this.phoneAreaCode.getValue());
			aMandate.setPhoneNumber(this.phoneNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Status
		try {

			if (registration) {
				this.status.setConstraint(new StaticListValidator(statusTypeList, Labels
						.getLabel("label_MandateDialog_Status.value")));
			}
			aMandate.setStatus(this.status.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// InputDate
		try {
			aMandate.setInputDate(DateUtility.getAppDate());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Open Mandate
		try {
			aMandate.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Reason
		try {
			aMandate.setReason(this.reason.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// MandateCcy
		try {
			aMandate.setMandateCcy(SysParamUtil.getAppCurrency());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		logger.debug("Leaving");
		return wve;
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");
		doRemoveValidation();
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (parenttab != null) {
				parenttab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation(boolean validate) {
		logger.debug("Entering");
		// Customer ID
		if (!this.custID.isReadonly()) {
			this.custID.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_CustID.value"), null,
					validate, true));
		}
		// Mandate Type
		if (!this.mandateType.isDisabled()) {
			this.mandateType.setConstraint(new StaticListValidator(mandateTypeList, Labels
					.getLabel("label_MandateDialog_MandateType.value")));
		}
		// Bank Branch ID
		if (!this.bankBranchID.isReadonly()) {
			this.bankBranchID.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MandateDialog_BankBranchID.value"), null, validate));
		}
		// Account Number
		if (!this.accNumber.isReadonly()) {
			this.accNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_AccNumber.value"),
					PennantRegularExpressions.REGEX_ACCOUNTNUMBER, validate, accNoLength,accNoLength));
		}
		// Account Holder Name
		if (!this.accHolderName.isReadonly()) {
			this.accHolderName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MandateDialog_AccHolderName.value"), PennantRegularExpressions.REGEX_NAME,
					validate));
		}
		// Joint Account Holder Name
		if (!this.jointAccHolderName.isReadonly()) {
			this.jointAccHolderName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MandateDialog_JointAccHolderName.value"), PennantRegularExpressions.REGEX_NAME,
					false));
		}
		// Account Type
		if (!this.accType.isDisabled() && validate) {
			this.accType.setConstraint(new StaticListValidator(accTypeList, Labels
					.getLabel("label_MandateDialog_AccType.value")));
		}
		// Start Date
		Date appStartDate = DateUtility.getAppDate();
		Date appExpiryDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");

		if (!this.startDate.isDisabled() && this.expiryDate.isButtonVisible()) {
			this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_MandateDialog_StartDate.value"),
					validate, appStartDate, appExpiryDate, true));
		}
		// Expiry Date
		if (!this.expiryDate.isDisabled() && this.expiryDate.isButtonVisible()) {
			try {
				this.expiryDate.setConstraint(new PTDateValidator(Labels
						.getLabel("label_MandateDialog_ExpiryDate.value"), validate, this.startDate.getValue(),
						appExpiryDate, this.openMandate.isChecked()));
			} catch (WrongValueException we) {
				this.expiryDate.setConstraint(new PTDateValidator(Labels
						.getLabel("label_MandateDialog_ExpiryDate.value"), validate, true, null, false));
			}
		}
		// Max Limit
		if (!this.maxLimit.isReadonly()) {
			this.maxLimit.setConstraint(new PTDecimalValidator(Labels.getLabel("label_MandateDialog_MaxLimit.value"),
					ccyFormatter, validate, false));
		}
		// Periodicity
		if (!this.phoneCountryCode.isReadonly()) {
			this.phoneCountryCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_MandateDialog_PhoneCountryCode.value"), false, 1));
		}
		if (!this.phoneAreaCode.isReadonly()) {
			this.phoneAreaCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_MandateDialog_PhoneAreaCode.value"), false, 2));
		}
		if (!this.phoneNumber.isReadonly()) {
			this.phoneNumber.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_MandateDialog_PhoneNumber.value"), false, 3));
		}
		// Reason
		if (!this.reason.isReadonly()) {
			this.reason.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateStatusDialog_Reason.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, flag));
		}

		// Status
		//		if (!this.status.isDisabled()) {
		//			this.status.setConstraint(new StaticListValidator(statusTypeList, Labels.getLabel("label_MandateDialog_Status.value")));
		//		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custID.setConstraint("");
		this.mandateRef.setConstraint("");
		this.inputDate.setConstraint("");
		this.mandateType.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.accNumber.setConstraint("");
		this.accHolderName.setConstraint("");
		this.jointAccHolderName.setConstraint("");
		this.accType.setConstraint("");
		this.startDate.setConstraint("");
		this.expiryDate.setConstraint("");
		this.maxLimit.setConstraint("");
		this.phoneCountryCode.setConstraint("");
		this.phoneAreaCode.setConstraint("");
		this.phoneNumber.setConstraint("");
		this.status.setConstraint("");
		this.approvalID.setConstraint("");
		this.reason.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custID.setErrorMessage("");
		this.mandateRef.setErrorMessage("");
		this.mandateRef.clearErrorMessage();
		this.mandateType.setErrorMessage("");
		this.bankBranchID.setErrorMessage("");
		this.accNumber.setErrorMessage("");
		this.accHolderName.setErrorMessage("");
		this.jointAccHolderName.setErrorMessage("");
		this.accType.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.expiryDate.setErrorMessage("");
		this.maxLimit.setErrorMessage("");
		this.phoneCountryCode.setErrorMessage("");
		this.phoneAreaCode.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.status.setErrorMessage("");
		this.approvalID.setErrorMessage("");
		this.reason.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		if (registration) {
			getMandateRegistrationListCtrl().search();
		} else {
			getMandateListCtrl().search();
		}
	}

	public void onSelect$status(Event event) {
		logger.debug("Entering" + event.toString());
		this.space_Reason.setSclass("mandatory");
		flag = true;
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = this.bankBranchID.getObject();

		if (dataObject == null || dataObject instanceof String) {
			this.bank.setValue("");
			this.city.setValue("");
			this.micr.setValue("");
			this.ifsc.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bank.setValue(details.getBankName());
				this.micr.setValue(details.getMICR());
				this.ifsc.setValue(details.getIFSC());
				this.city.setValue(details.getCity());
				if(StringUtils.isNotBlank(details.getBankCode())){
					accNoLength = getBankDetailService().getAccNoLengthByCode(details.getBankCode());
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$custID(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = custID.getObject();

		if (dataObject instanceof String) {
			this.custID.setValue(dataObject.toString());
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custID.setAttribute("custID", details.getCustID());

			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$mandateRef(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = mandateRef.getObject();

		if (dataObject instanceof String) {
			this.mandateRef.setValue(dataObject.toString());
			this.mandateRef.setAttribute("mandateID", Long.MIN_VALUE);
			clearMandatedata();
		} else {
			Mandate details = (Mandate) dataObject;
			if (details != null) {
				this.mandateRef.setAttribute("mandateID", details.getMandateID());
				doWriteData(details);
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	// *****************************************************************
	// ************************+ crud operations ***********************
	// *****************************************************************

	/**
	 * Deletes a Mandate object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Mandate aMandate = new Mandate();
		BeanUtils.copyProperties(getMandate(), aMandate);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aMandate.getMandateID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aMandate.getRecordType()).equals("")) {
				aMandate.setVersion(aMandate.getVersion() + 1);
				aMandate.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aMandate.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aMandate.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aMandate.getNextTaskId(), aMandate);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aMandate, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showErrorMessage(this.window_MandateDialog, e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.custID.setValue("");
		this.mandateRef.setValue("");
		this.inputDate.setValue(null);
		this.mandateType.setValue("");
		this.bankBranchID.setValue("");
		this.accNumber.setValue("");
		this.accHolderName.setValue("");
		this.jointAccHolderName.setValue("");
		this.accType.setValue("");
		this.openMandate.setChecked(false);
		this.startDate.setValue(null);
		this.expiryDate.setValue(null);
		this.maxLimit.setValue(BigDecimal.ZERO);
		this.periodicity.setValue("");
		this.phoneCountryCode.setValue("");
		this.phoneAreaCode.setValue("");
		this.phoneNumber.setValue("");
		this.status.setValue("");
		this.approvalID.setValue("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.city.setValue("");
		this.reason.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Mandate aMandate = new Mandate();
		BeanUtils.copyProperties(getMandate(), aMandate);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aMandate.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aMandate.getNextTaskId(), aMandate);
		}

		// *************************************************************
		// force validation, if on, than execute by component.getValue()
		// *************************************************************
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aMandate.getRecordType()) && isValidation()) {
			doSetValidation(true);
			// fill the Mandate object with the components data
			ArrayList<WrongValueException> wve = doWriteComponentsToBean(aMandate, null);
			showErrorDetails(wve);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aMandate.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aMandate.getRecordType()).equals("")) {
				aMandate.setVersion(aMandate.getVersion() + 1);
				if (isNew) {
					aMandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aMandate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aMandate.setNewRecord(true);
				}
			}
		} else {
			aMandate.setVersion(aMandate.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (registration) {
				aMandate.setModule(MandateConstants.MODULE_REGISTRATION);
			}

			if (doProcess(aMandate, tranType)) {
				// doWriteBeanToComponents(aMandate);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_MandateDialog, e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(Mandate aMandate, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aMandate.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aMandate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aMandate.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					try {
						if (!notesEntered) {
							MessageUtil.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error("Exception: ", e);
					}
				}
			}

			aMandate.setTaskId(getTaskId());
			aMandate.setNextTaskId(getNextTaskId());
			aMandate.setRoleCode(getRole());
			aMandate.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aMandate, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aMandate, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aMandate, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		Mandate aMandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getMandateService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getMandateService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getMandateService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aMandate.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getMandateService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aMandate.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_MandateDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_MandateDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(
								getNotes("Mandate", String.valueOf(aMandate.getMandateID()), aMandate.getVersion()),
								true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ******************************************************//
	// ***************** WorkFlow Components*****************//
	// ******************************************************//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Mandate aMandate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMandate.getBefImage(), aMandate);
		return new AuditHeader(String.valueOf(aMandate.getMandateID()), null, null, null, auditDetail,
				aMandate.getUserDetails(), getOverideMap());
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			if (finHeaderList != null) {
				map.put("finHeaderList", finHeaderList);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	public void checkTabDisplay(String mandateType, boolean onchange) {
		this.parenttab.setVisible(false);
		String val = StringUtils.trimToEmpty(mandateType);
		for (ValueLabel valueLabel : mandateTypeList) {
			if (val.equals(valueLabel.getValue())) {
				this.parenttab.setVisible(true);
				fillComboBox(this.mandateType, mandateType, mandateTypeList, "");
				break;
			}
		}

		addMandateFiletrs(mandateType);
		if (this.useExisting.isChecked() && onchange) {
			clearMandatedata();
		}
	}

	private void addMandateFiletrs(String repaymethod) {
		if (financemain != null) {
			if (StringUtils.isEmpty(repaymethod)) {
				repaymethod = financemain.getFinRepayMethod();
			}
			Filter[] filters = new Filter[3];
			filters[0] = new Filter("CustID", financemain.getCustID(), Filter.OP_EQUAL);
			filters[1] = new Filter("MandateType", repaymethod, Filter.OP_EQUAL);
			filters[2] = new Filter("Active", 1, Filter.OP_EQUAL);
			this.mandateRef.setFilters(filters);
			this.mandateRef.setWhereClause("(OpenMandate = 1 or OrgReference is null)");
		}
	}

	public void doSave_Mandate(FinanceDetail financeDetail, Tab tab, boolean recSave) throws InterruptedException {
		logger.debug("Entering");

		doClearMessage();
		boolean validate=false;
		if (!recSave && !isReadOnly("MandateDialog_validate")) {
			validate=true;
		}
		doSetValidation(validate);

		ArrayList<WrongValueException> wve = doWriteComponentsToBean(getMandate(), tab);
		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}
		showErrorDetails(wve);

		if (StringUtils.isBlank(getMandate().getRecordType())) {
			getMandate().setVersion(getMandate().getVersion() + 1);
			getMandate().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getMandate().setNewRecord(true);
		}
		getMandate().setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		getMandate().setLastMntOn(new Timestamp(System.currentTimeMillis()));
		getMandate().setUserDetails(getUserWorkspace().getLoggedInUser());
		financeDetail.setMandate(getMandate());
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Mandate getMandate() {
		return this.mandate;
	}

	public void setMandate(Mandate mandate) {
		this.mandate = mandate;
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public MandateService getMandateService() {
		return this.mandateService;
	}

	public void setMandateListCtrl(MandateListCtrl mandateListCtrl) {
		this.mandateListCtrl = mandateListCtrl;
	}

	public MandateListCtrl getMandateListCtrl() {
		return this.mandateListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setMandateRegistrationListCtrl(MandateRegistrationListCtrl mandateRegistrationListCtrl) {
		this.mandateRegistrationListCtrl = mandateRegistrationListCtrl;
	}

	public MandateRegistrationListCtrl getMandateRegistrationListCtrl() {
		return this.mandateRegistrationListCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public BankDetailService getBankDetailService() {
		return bankDetailService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

}
