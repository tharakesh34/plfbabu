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
 * * FileName : CustomerIncomeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customerincome;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.customermasters.customer.CustomerViewDialogCtrl;
import com.pennant.webui.sampling.SamplingDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerIncome/customerIncomeDialog.zul file.
 */
public class CustomerIncomeDialogCtrl extends GFCBaseCtrl<CustomerIncome> {
	private static final long serialVersionUID = 7152044545249791558L;
	private static final Logger logger = LogManager.getLogger(CustomerIncomeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerIncomeDialog; // autoWired

	IncomeType incomeType = null;
	protected Longbox custID; // autoWired
	protected ExtendedCombobox custIncomeType; // autoWired
	protected CurrencyBox custIncome; // autoWired
	protected Textbox custCIF; // autoWired
	protected Label custShrtName; // autoWired
	protected Checkbox jointCust;
	protected Decimalbox margin;
	protected Row row_isJoint; // autoWired
	protected Row row_custType;
	protected Combobox custType;

	// not auto wired variables
	private CustomerIncome customerIncome; // overHanded per parameter
	private transient CustomerIncomeListCtrl customerIncomeListCtrl; // overHanded per parameter

	private transient boolean validationOn;

	protected Button btnSearchPRCustid; // autoWire

	// ServiceDAOs / Domain Classes
	private transient CustomerIncomeService customerIncomeService;
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord = false;
	private boolean newCustomer = false;
	private List<CustomerIncome> customerIncomes;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	private SamplingDialogCtrl samplingDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private int ccyFormatter = 0;
	private String moduleType = "";
	private boolean custIsJointCust = false;
	private String userRole = "";
	private String inputSource = "customer";
	private Set<String> coApplicants;
	private boolean isFinanceProcess = false;

	/**
	 * default constructor.<br>
	 */
	public CustomerIncomeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerIncomeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerIncome object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CustomerIncomeDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerIncomeDialog);

		if (arguments.containsKey("customerIncome")) {
			this.customerIncome = (CustomerIncome) arguments.get("customerIncome");
			CustomerIncome befImage = new CustomerIncome();
			BeanUtils.copyProperties(this.customerIncome, befImage);
			this.customerIncome.setBefImage(befImage);
			setCustomerIncome(this.customerIncome);
		} else {
			setCustomerIncome(null);
		}

		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}
		if (arguments.containsKey("jointCust")) {
			this.custIsJointCust = (Boolean) arguments.get("jointCust");
		}

		if (getCustomerIncome().isNewRecord()) {
			setNewRecord(true);
		}

		if (arguments.containsKey("customerDialogCtrl")) {

			setCustomerDialogCtrl((CustomerDialogCtrl) arguments.get("customerDialogCtrl"));
			setNewCustomer(true);

			if (arguments.containsKey("ccyFormatter")) {
				ccyFormatter = (Integer) arguments.get("ccyFormatter");
			}

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.customerIncome.setWorkflowId(0);
			if (arguments.containsKey("roleCode") && !enqiryModule) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerIncomeDialog");
			}
		}
		if (arguments.containsKey("customerViewDialogCtrl")) {

			setCustomerViewDialogCtrl((CustomerViewDialogCtrl) arguments.get("customerViewDialogCtrl"));
			setNewCustomer(true);

			if (arguments.containsKey("ccyFormatter")) {
				ccyFormatter = (Integer) arguments.get("ccyFormatter");
			}

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.customerIncome.setWorkflowId(0);
			if (arguments.containsKey("roleCode") && !enqiryModule) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerIncomeDialog");
			}
		}

		if (arguments.containsKey("samplingDialogCtrl")) {
			row_custType.setVisible(true);
			inputSource = "sampling";
			setSamplingDialogCtrl((SamplingDialogCtrl) arguments.get("samplingDialogCtrl"));
			setNewCustomer(true);

			if (arguments.containsKey("ccyFormatter")) {
				ccyFormatter = (Integer) arguments.get("ccyFormatter");
			}
			if (arguments.containsKey("coApplicants")) {
				coApplicants = (Set<String>) arguments.get("coApplicants");
			}

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.customerIncome.setWorkflowId(0);
			if (arguments.containsKey("roleCode") && !enqiryModule) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, pageRightName);
			}
		}

		if (arguments.containsKey("isFinanceProcess")) {
			isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
		}

		doLoadWorkFlow(this.customerIncome.isWorkflow(), this.customerIncome.getWorkflowId(),
				this.customerIncome.getNextTaskId());

		if (isWorkFlowEnabled() && !enqiryModule) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), pageRightName);
		}

		// READ OVERHANDED parameters !
		// we get the customerIncomeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerIncome here.
		if (arguments.containsKey("customerIncomeListCtrl")) {
			setCustomerIncomeListCtrl((CustomerIncomeListCtrl) arguments.get("customerIncomeListCtrl"));
		} else {
			setCustomerIncomeListCtrl(null);
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerIncome());

		// Calling SelectCtrl For proper selection of Customer
		if (isNewRecord() && !isNewCustomer()) {
			onload();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.custIncomeType.setMaxlength(20);
		this.custIncomeType.getTextbox().setMaxlength(50);
		this.custIncomeType.setMandatoryStyle(true);
		this.custIncomeType.setTextBoxWidth(110);
		this.custIncomeType.setModuleName("IncomeExpense");
		this.custIncomeType.setValueColumn("IncomeExpenseCode");
		this.custIncomeType.setDescColumn("IncomeTypeDesc");
		this.custIncomeType.setValidateColumns(new String[] { "IncomeExpenseCode" });

		if (this.samplingDialogCtrl != null) {
			Filter incomeTypeFilter[] = new Filter[1];
			incomeTypeFilter[0] = new Filter("IncomeExpense", PennantConstants.INCOME, Filter.OP_EQUAL);
			custIncomeType.setFilters(incomeTypeFilter);
		}

		this.custIncome.setMandatory(true);
		this.custIncome.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.custIncome.setScale(ccyFormatter);

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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		// getUserWorkspace().allocateAuthorities("CustomerIncomeDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerIncomeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerIncomeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerIncomeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerIncomeDialog_btnSave"));
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
		doSave();
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		MessageUtil.showHelpWindow(event, window_CustomerIncomeDialog);
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		doDelete();
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	@Override
	public void closeDialog() {
		if (isNewCustomer()) {
			closeWindow();
			return;
		}

		super.closeDialog();
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.customerIncome.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerIncome CustomerIncome
	 */
	public void doWriteBeanToComponents(CustomerIncome aCustomerIncome) {
		logger.debug("Entering");

		if (aCustomerIncome.getCustId() != Long.MIN_VALUE) {
			this.custID.setValue(aCustomerIncome.getCustId());
		}

		List<ValueLabel> customerTypes = new ArrayList<>();
		if (row_custType.isVisible()) {
			customerTypes.add(new ValueLabel("1", "Primary Customer"));
			customerTypes.add(new ValueLabel("2", "Co-Applicant Customer"));
			fillComboBox(this.custType, aCustomerIncome.getCustType() == 0 ? customerTypes.get(0).getValue()
					: String.valueOf(aCustomerIncome.getCustType()), customerTypes, "");
		}

		if (row_custType.isVisible() && coApplicants.contains(aCustomerIncome.getCustCif())) {
			this.custType.setValue(customerTypes.get(1).getLabel());
		}

		this.custIncomeType.setValue(aCustomerIncome.getIncomeType() == null ? "" : aCustomerIncome.getIncomeType());
		this.custIncome.setValue(CurrencyUtil.parse(aCustomerIncome.getIncome(), ccyFormatter));
		this.custCIF.setValue(aCustomerIncome.getCustCif() == null ? "" : aCustomerIncome.getCustCif().trim());
		this.custShrtName
				.setValue(aCustomerIncome.getCustShrtName() == null ? "" : aCustomerIncome.getCustShrtName().trim());
		this.jointCust.setChecked(aCustomerIncome.isJointCust());
		this.margin.setValue(CurrencyUtil.parse(aCustomerIncome.getMargin(), 2));

		if (isNewRecord()) {
			this.custIncomeType.setDescription("");
		} else {
			this.custIncomeType.setDescription(aCustomerIncome.getIncomeTypeDesc());
		}
		this.recordStatus.setValue(aCustomerIncome.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerIncome
	 */
	public void doWriteComponentsToBean(CustomerIncome aCustomerIncome) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aCustomerIncome.setCustShrtName(this.custShrtName.getValue());
			aCustomerIncome.setCustId(this.custID.getValue());
			aCustomerIncome.setCustCif(this.custCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerIncome.setMargin(CurrencyUtil.unFormat(this.margin.getValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.custIncomeType.getDescription();
			aCustomerIncome.setIncomeType(this.custIncomeType.getValue().trim());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomerIncome.setIncome(CurrencyUtil.unFormat(this.custIncome.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aCustomerIncome.setJointCust(this.jointCust.isChecked());

		aCustomerIncome.setInputSource(inputSource);

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		IncomeType type = getIncomeType(this.custIncomeType.getValue().trim());
		if (type != null && type.getMargin() != null) {
			aCustomerIncome.setMarginDeviation(!type.getMargin().equals(this.margin.getValue()));
		}

		aCustomerIncome.setRecordStatus(this.recordStatus.getValue());
		setCustomerIncome(aCustomerIncome);
		logger.debug("Leaving");
	}

	public void onChange$custType(Event event) {
		logger.debug(Literal.ENTERING);
		String type = this.custType.getSelectedItem().getValue();
		if (!type.equals("#")) {
			visibleComponent(Integer.parseInt(type));
		}
		logger.debug(Literal.LEAVING);
	}

	private void visibleComponent(Integer type) {
		CustomerIncome customerIncome = this.customerIncome.getBefImage();

		if (type == 2) {
			this.custCIF.setValue("");
			this.custShrtName.setValue("");
			this.btnSearchPRCustid.setVisible(true);
		} else {
			this.custCIF.setValue(customerIncome.getCustCif() == null ? "" : customerIncome.getCustCif().trim());
			this.custShrtName
					.setValue(customerIncome.getCustShrtName() == null ? "" : customerIncome.getCustShrtName().trim());
			this.btnSearchPRCustid.setVisible(false);
		}
	}

	private IncomeType getIncomeType(String incomeType) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<IncomeType> searchObject = new JdbcSearchObject<IncomeType>(IncomeType.class);
		searchObject.addFilterEqual("IncomeTypeCode", incomeType);
		searchObject.addTabelName("BMTIncomeTypes_AView");
		List<IncomeType> incomeList = pagedListService.getBySearchObject(searchObject);
		if (incomeList != null && !incomeList.isEmpty()) {
			return incomeList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerIncome
	 */
	public void doShowDialog(CustomerIncome aCustomerIncome) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custIncome.focus();
			if (isNewCustomer()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(aCustomerIncome);
			doCheckEnquiry();
			if (!custIsJointCust) {
				this.row_isJoint.setVisible(false);
			}

			if (isNewCustomer()) {
				this.window_CustomerIncomeDialog.setHeight("228px");
				this.window_CustomerIncomeDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_CustomerIncomeDialog.doModal();
			} else {
				this.window_CustomerIncomeDialog.setWidth("100%");
				this.window_CustomerIncomeDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
			doReadOnly();
		}

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (this.btnSearchPRCustid.isVisible()) {
			this.custCIF.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerIncomeDialog_CustID.value"), null, true));
		}

		if (!this.custIncome.isReadonly()) {
			this.custIncome.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerIncomeDialog_CustIncome.value"), ccyFormatter, true, false));
		}
		if (!this.margin.isReadonly()) {
			this.margin.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerIncomeDialog_Margin.value"),
					2, false, false, 100));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custCIF.setConstraint("");
		this.custIncome.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.custIncomeType.setConstraint(new PTStringValidator(
				Labels.getLabel("label_CustomerIncomeDialog_CustIncomeType.value"), null, true, true));

		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custIncomeType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custIncome.setErrorMessage("");
		this.custIncomeType.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getCustomerIncomeListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CustomerIncome aCustomerIncome = new CustomerIncome();
		BeanUtils.copyProperties(getCustomerIncome(), aCustomerIncome);

		final String keyReference = Labels.getLabel("label_CustomerIncomeDialog_CustIncomeType.value") + " : "
				+ aCustomerIncome.getIncomeType();

		doDelete(keyReference, aCustomerIncome);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final CustomerIncome aCustomerIncome) {
		String tranType = PennantConstants.TRAN_WF;

		if (StringUtils.isBlank(aCustomerIncome.getRecordType())) {
			aCustomerIncome.setVersion(aCustomerIncome.getVersion() + 1);
			aCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if (!isFinanceProcess && getCustomerDialogCtrl() != null
					&& getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()) {
				aCustomerIncome.setNewRecord(true);
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
			/*
			 * if (isWorkFlowEnabled()) { aCustomerIncome.setNewRecord(true); tranType = PennantConstants.TRAN_WF; }
			 * else { tranType = PennantConstants.TRAN_DEL; }
			 */ }

		try {
			if (isNewCustomer()) {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newCustomerProcess(aCustomerIncome, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerIncomeDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getCustomerDialogCtrl() != null) {
						getCustomerDialogCtrl().doFillCustomerIncome(this.customerIncomes);
					} else if (getSamplingDialogCtrl() != null) {
						getSamplingDialogCtrl().doFillCustomerIncome(this.customerIncomes);
					}
					closeDialog();
				}
			} else if (doProcess(aCustomerIncome, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			showMessage(e);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()) {

			if (isNewCustomer()) {
				this.btnCancel.setVisible(false);
				this.btnSearchPRCustid.setVisible(false);
			} else {
				this.btnSearchPRCustid.setVisible(true);
			}
			this.custIncomeType.setReadonly(isReadOnly("CustomerIncomeDialog_custIncomeType"));
			this.jointCust.setDisabled(isReadOnly("CustomerIncomeDialog_custIncomeType"));
			this.custType.setDisabled(isReadOnly("CustomerIncomeDialog_custIncome"));
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.custIncomeType.setReadonly(true);
			this.jointCust.setDisabled(true);
			this.custType.setDisabled(true);
		}

		this.custCIF.setReadonly(true);
		this.custID.setReadonly(isReadOnly("CustomerIncomeDialog_custID"));
		this.custIncome.setReadonly(isReadOnly("CustomerIncomeDialog_custIncome"));
		this.margin.setReadonly(isReadOnly("CustomerIncomeDialog_custIncome"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerIncome.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newCustomer) {
				if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {

		if (enqiryModule) {
			return true;
		}

		boolean isCustomerWorkflow = false;
		if (getCustomerDialogCtrl() != null) {
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		} else if (getSamplingDialogCtrl() != null) {
			isCustomerWorkflow = getSamplingDialogCtrl().getSampling().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.custCIF.setReadonly(true);
		this.custIncomeType.setReadonly(true);
		this.custIncome.setReadonly(true);
		this.jointCust.setDisabled(true);
		this.margin.setReadonly(true);
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
		this.custCIF.setValue("");
		this.custShrtName.setValue("");
		this.custIncomeType.setValue("");
		this.custIncomeType.setDescription("");
		this.custIncome.setValue("");
		this.jointCust.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerIncome aCustomerIncome = new CustomerIncome();
		BeanUtils.copyProperties(getCustomerIncome(), aCustomerIncome);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aCustomerIncome);

		isNew = aCustomerIncome.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerIncome.getRecordType())) {
				aCustomerIncome.setVersion(aCustomerIncome.getVersion() + 1);
				if (isNew) {
					aCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerIncome.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aCustomerIncome.setVersion(1);
					aCustomerIncome.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aCustomerIncome.getRecordType())) {
					aCustomerIncome.setVersion(aCustomerIncome.getVersion() + 1);
					aCustomerIncome.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aCustomerIncome.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aCustomerIncome.setVersion(aCustomerIncome.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		/*
		 * IncomeType type = null; IncomeTypeServiceImpl income; if
		 * (this.margin.getValue().compareTo(type.getMargin())==0) { boolean Flag = false; if(Flag=false){
		 * 
		 * } } else {
		 * 
		 * }
		 */
		// save it to database
		try {
			if (isNewCustomer()) {
				AuditHeader auditHeader = newCustomerProcess(aCustomerIncome, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerIncomeDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getCustomerDialogCtrl() != null) {
						getCustomerDialogCtrl().doFillCustomerIncome(this.customerIncomes);
					} else if (getSamplingDialogCtrl() != null) {
						getSamplingDialogCtrl().doFillCustomerIncome(this.customerIncomes);
					}
					// send the data back to customer
					closeDialog();
				}
			} else if (doProcess(aCustomerIncome, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newCustomerProcess(CustomerIncome aCustomerIncome, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCustomerIncome, tranType);
		customerIncomes = new ArrayList<>();

		String[] valueParm = new String[4];
		String[] errParm = new String[4];

		valueParm[0] = String.valueOf(aCustomerIncome.getCustCif());
		valueParm[1] = aCustomerIncome.getIncomeType();
		valueParm[2] = String.valueOf(aCustomerIncome.isJointCust());
		valueParm[3] = aCustomerIncome.getCategory();

		errParm[0] = PennantJavaUtil.getLabel("label_CustomerIncomeDialog_CustIncomeCIF.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustIncomeType") + ":" + valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_JointCust") + ":" + valueParm[3];
		errParm[3] = PennantJavaUtil.getLabel("label_CustIncomeCountry") + ":" + valueParm[2];

		List<CustomerIncome> custIncomeList = null;

		if (getCustomerDialogCtrl() != null && getCustomerDialogCtrl().getIncomeList() != null
				&& getCustomerDialogCtrl().getIncomeList().size() > 0) {
			custIncomeList = getCustomerDialogCtrl().getIncomeList();
		} else if (getSamplingDialogCtrl() != null && getSamplingDialogCtrl().getIncomeList() != null
				&& getSamplingDialogCtrl().getIncomeList().size() > 0) {
			custIncomeList = getSamplingDialogCtrl().getIncomeList();
		}

		if (custIncomeList != null && custIncomeList.size() > 0) {
			for (int i = 0; i < custIncomeList.size(); i++) {
				CustomerIncome customerIncome = custIncomeList.get(i);

				if ((aCustomerIncome.getCustId() == customerIncome.getCustId())
						&& (aCustomerIncome.getIncomeType().equals(customerIncome.getIncomeType()))
						&& (aCustomerIncome.getCategory().equals(customerIncome.getCategory()))
						&& (aCustomerIncome.isJointCust() == customerIncome.isJointCust())
						&& (aCustomerIncome.getIncomeExpense().equals(customerIncome.getIncomeExpense()))) { // Both
																												// Current
																												// and
																												// Existing
																												// list
																												// rating
																												// same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							customerIncomes.add(aCustomerIncome);
						} else if (aCustomerIncome.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							customerIncomes.add(aCustomerIncome);
						} else if (aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerIncomeList()
									.size(); j++) {
								CustomerIncome income = getCustomerDialogCtrl().getCustomerDetails()
										.getCustomerIncomeList().get(j);
								if (income.getCustId() == aCustomerIncome.getCustId()
										&& income.getIncomeType().equals(aCustomerIncome.getIncomeType())
										&& income.getCategory().equals(aCustomerIncome.getCategory())
										&& income.getIncomeExpense().equals(aCustomerIncome.getIncomeExpense())) {
									customerIncomes.add(income);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							customerIncomes.add(customerIncome);
						}
					}
				} else {
					customerIncomes.add(customerIncome);
				}
			}
		}

		if (!recordAdded) {
			customerIncomes.add(aCustomerIncome);
		}
		return auditHeader;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerIncome (CustomerIncome)
	 * 
	 * @param tranType        (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(CustomerIncome aCustomerIncome, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerIncome.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomerIncome.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerIncome.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerIncome.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerIncome.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerIncome);
				}

				if (isNotesMandatory(taskId, aCustomerIncome)) {
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

			aCustomerIncome.setTaskId(taskId);
			aCustomerIncome.setNextTaskId(nextTaskId);
			aCustomerIncome.setRoleCode(getRole());
			aCustomerIncome.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerIncome, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomerIncome);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerIncome, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aCustomerIncome, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerIncome aCustomerIncome = (CustomerIncome) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getCustomerIncomeService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getCustomerIncomeService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getCustomerIncomeService().doApprove(auditHeader);

					if (aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getCustomerIncomeService().doReject(auditHeader);
					if (aCustomerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CustomerIncomeDialog, auditHeader);
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_CustomerIncomeDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.customerIncome), true);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	// Search Button Component Events

	public void onFulfill$custIncomeType(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = custIncomeType.getObject();
		if (dataObject instanceof String) {
			this.custIncomeType.setValue(dataObject.toString());
			this.custIncomeType.setDescription("");
		} else {
			IncomeType details = (IncomeType) dataObject;
			if (details != null) {
				this.custIncomeType.setValue(details.getIncomeTypeCode().trim());
				this.custIncomeType.setDescription(details.getIncomeTypeDesc());
				getCustomerIncome().setIncomeExpense(details.getIncomeExpense().trim());
				getCustomerIncome().setIncomeTypeDesc(details.getIncomeTypeDesc());
				getCustomerIncome().setCategory(details.getCategory().trim());
				getCustomerIncome().setCategoryDesc(details.getLovDescCategoryName());
				getCustomerIncome().setMargin(details.getMargin());
				this.margin.setValue(CurrencyUtil.parse(details.getMargin(), 2));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Calling list Of existed Customers
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);
		onload();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * To load the customerSelect filter dialog
	 */
	private void onload() {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();
		List<Filter> filtersList = new ArrayList<Filter>();
		Filter filter = null;
		if (arguments.containsKey("samplingDialogCtrl")) {
			if (!coApplicants.isEmpty()) {
				filter = new Filter("custcif", coApplicants.toArray(new String[0]), Filter.OP_IN);
			} else {
				filter = new Filter("custcif", "", Filter.OP_EQUAL);
			}
		} else {
			filter = new Filter("lovDescCustCtgType", PennantConstants.PFF_CUSTCTG_INDIV, Filter.OP_EQUAL);
		}

		filtersList.add(filter);
		map.put("DialogCtrl", this);
		map.put("filtersList", filtersList);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		final Customer aCustomer = (Customer) nCustomer;
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerIncome
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerIncome aCustomerIncome, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerIncome.getBefImage(), aCustomerIncome);

		return new AuditHeader(getReference(), String.valueOf(aCustomerIncome.getCustId()), null, null, auditDetail,
				aCustomerIncome.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerIncomeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.customerIncome);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustomerIncome().getCustId() + PennantConstants.KEY_SEPERATOR + getCustomerIncome().getIncomeType()
				+ PennantConstants.KEY_SEPERATOR + getCustomerIncome().getCategory() + PennantConstants.KEY_SEPERATOR
				+ getCustomerIncome().getIncomeExpense();
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

	public CustomerIncome getCustomerIncome() {
		return this.customerIncome;
	}

	public void setCustomerIncome(CustomerIncome customerIncome) {
		this.customerIncome = customerIncome;
	}

	public void setCustomerIncomeService(CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}

	public CustomerIncomeService getCustomerIncomeService() {
		return this.customerIncomeService;
	}

	public void setCustomerIncomeListCtrl(CustomerIncomeListCtrl customerIncomeListCtrl) {
		this.customerIncomeListCtrl = customerIncomeListCtrl;
	}

	public CustomerIncomeListCtrl getCustomerIncomeListCtrl() {
		return this.customerIncomeListCtrl;
	}

	public void setCustomerIncomes(List<CustomerIncome> customerIncomes) {
		this.customerIncomes = customerIncomes;
	}

	public List<CustomerIncome> getCustomerIncomes() {
		return customerIncomes;
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}

	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
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

	public SamplingDialogCtrl getSamplingDialogCtrl() {
		return samplingDialogCtrl;
	}

	public void setSamplingDialogCtrl(SamplingDialogCtrl samplingDialogCtrl) {
		this.samplingDialogCtrl = samplingDialogCtrl;
	}

}
