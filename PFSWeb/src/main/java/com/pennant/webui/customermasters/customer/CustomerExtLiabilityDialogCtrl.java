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
 * * FileName : CustomerExtLiabilityDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 *
 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * 19-04-2018 Vinay 0.2 As per Profectus documnet * below fields are added * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.sampling.SamplingDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerExtLiability/custdosaveomerExtLiabilityDialog.zul file.
 */
public class CustomerExtLiabilityDialogCtrl extends GFCBaseCtrl<CustomerExtLiability> {
	private static final long serialVersionUID = -7522534300621535097L;
	private static final Logger logger = LogManager.getLogger(CustomerExtLiabilityDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerExtLiabilityDialog;

	protected Row row_custType;
	protected Combobox custType;
	protected Longbox custID;
	protected Textbox custCIF;
	protected Label custShrtName;
	protected Datebox finDate;
	protected ExtendedCombobox finType;
	protected ExtendedCombobox finStatus;
	protected ExtendedCombobox bankName;
	protected CurrencyBox originalAmount;
	protected CurrencyBox installmentAmount;
	protected CurrencyBox outStandingBal;
	protected Intbox liabilitySeq;

	// ###_0.2
	protected CurrencyBox roi;
	protected Intbox totalTenure;
	protected Intbox balanceTenure;
	protected Intbox noOfBounces;
	protected CurrencyBox pos;
	protected CurrencyBox overdue;
	protected Checkbox emiFoir;
	protected Combobox source;
	protected Combobox checkedBy;
	protected Textbox securityDetail;
	protected ExtendedCombobox endUseOfFunds;
	protected ExtendedCombobox repayFrom;
	protected Textbox otherFinInstitute;
	protected Space space_Other;
	private final List<ValueLabel> sourceInfoList = PennantStaticListUtil.getSourceInfoList();
	private final List<ValueLabel> trackCheckList = PennantStaticListUtil.getTrackCheckList();
	// not auto wired variables
	private CustomerExtLiability externalLiability; // overHanded per parameter

	private transient boolean validationOn;

	protected Button btnSearchPRCustid;

	private boolean newRecord = false;
	private boolean newCustomer = false;
	private List<CustomerExtLiability> externalLiabilities;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	private SamplingDialogCtrl samplingDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType = "";
	private String userRole = "";
	private int finFormatter;
	private boolean isFinanceProcess = false;
	Date appDate = SysParamUtil.getAppDate();
	Date appStartDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE);
	private String inputSource = "customer";
	private Set<String> coApplicants;

	protected Intbox noOfInstallmentMonths;
	private Listbox listBoxInstallmentDetails;
	private List<ExtLiabilityPaymentdetails> extLiabilitiesPaymentdetails = new ArrayList<>();

	protected CurrencyBox imputedEmi;
	protected Textbox ownerShip;
	protected Checkbox lastTwentyFourMonths;
	protected Checkbox lastSixMonths;
	protected Checkbox lastThreeMonths;
	protected CurrencyBox currentOverDue;
	protected Textbox repayFromAccNo;
	protected Textbox remarks;
	protected Intbox noOfBouncesInSixMonths;
	protected Intbox noOfBouncesInTwelveMonths;
	protected Checkbox consideredBasedOnRTR;
	protected Intbox mob;

	private boolean isCustomer360 = false;
	private transient BankDetailService bankDetailService;
	protected BankDetail bankDetails;
	protected int maxAccNoLength;
	protected int minAccNoLength;

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
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerExtLiability object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CustomerExtLiabilityDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerExtLiabilityDialog);

		try {
			if (arguments.containsKey("externalLiability")) {
				this.externalLiability = (CustomerExtLiability) arguments.get("externalLiability");
				CustomerExtLiability befImage = new CustomerExtLiability();
				BeanUtils.copyProperties(this.externalLiability, befImage);
				this.externalLiability.setBefImage(befImage);
				setExternalLiability(this.externalLiability);
			} else {
				setExternalLiability(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (arguments.containsKey("finFormatter")) {
				this.finFormatter = (Integer) arguments.get("finFormatter");
			}

			if (getExternalLiability().isNewRecord()) {
				setNewRecord(true);
			}

			if (arguments.containsKey("customerDialogCtrl")) {
				setCustomerDialogCtrl((CustomerDialogCtrl) arguments.get("customerDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.externalLiability.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerExtLiabilityDialog");
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
				this.externalLiability.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerExtLiabilityDialog");
				}
			}
			if (arguments.containsKey("financialSummaryDialogCtrl")) {
				setNewCustomer(true);
			}

			if (arguments.containsKey("samplingDialogCtrl")) {
				row_custType.setVisible(true);
				inputSource = "sampling";
				setSamplingDialogCtrl((SamplingDialogCtrl) arguments.get("samplingDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				if (arguments.containsKey("coApplicants")) {
					coApplicants = (Set<String>) arguments.get("coApplicants");
				}

				this.externalLiability.setWorkflowId(0);
				if (arguments.containsKey("roleCode") && !enqiryModule) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerExtLiabilityDialog");
				}
			}

			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}

			if (arguments.containsKey("customer360")) {
				isCustomer360 = (boolean) arguments.get("customer360");
			}

			doLoadWorkFlow(this.externalLiability.isWorkflow(), this.externalLiability.getWorkflowId(),
					this.externalLiability.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerExtLiabilityDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getExternalLiability());
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

		this.otherFinInstitute.setMaxlength(50);
		if (StringUtils.isNotBlank(externalLiability.getLoanBank())
				&& externalLiability.getLoanBank().equals(PennantConstants.OTHER_BANK)) {
			this.space_Other.setClass(PennantConstants.mandateSclass);
		}

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

		this.originalAmount.setMandatory(true);
		this.originalAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.originalAmount.setScale(finFormatter);

		this.installmentAmount.setMandatory(true);
		this.installmentAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.installmentAmount.setScale(finFormatter);

		if (ImplementationConstants.CUSTOM_EXT_LIABILITIES) {
			this.outStandingBal.setMandatory(false);
		} else {
			this.outStandingBal.setMandatory(true);
		}
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

		this.noOfInstallmentMonths.setValue(0);

		this.imputedEmi.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.imputedEmi.setScale(finFormatter);

		this.currentOverDue.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.currentOverDue.setScale(finFormatter);

		this.ownerShip.setMaxlength(50);
		this.remarks.setMaxlength(1000);

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
	private void doSetInstAmountMandProp() {
		if (StringUtils.equals(this.finType.getValue(), "CC")) {
			this.installmentAmount.setMandatory(false);
		} else {
			this.installmentAmount.setMandatory(true);
		}
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Method for Calling list Of existed Customers
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */

	private void onload() throws SuspendNotAllowedException, InterruptedException {
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
	 * @param event An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.externalLiability.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerExtLiability CustomerExtLiability
	 */
	public void doWriteBeanToComponents(CustomerExtLiability liability) {
		logger.debug("Entering");

		if (liability.getCustId() != Long.MIN_VALUE) {
			this.custID.setValue(liability.getCustId());
		}

		List<ValueLabel> customerTypes = new ArrayList<>();
		if (row_custType.isVisible()) {
			customerTypes.add(new ValueLabel("1", "Primary Customer"));
			customerTypes.add(new ValueLabel("2", "Co-Applicant Customer"));
			fillComboBox(this.custType, liability.getCustType() == 0 ? customerTypes.get(0).getValue()
					: String.valueOf(liability.getCustType()), customerTypes, "");
		}

		if (row_custType.isVisible() && coApplicants.contains(liability.getCustCif())) {
			this.custType.setValue(customerTypes.get(1).getLabel());
		}

		this.liabilitySeq.setValue(liability.getSeqNo());
		this.finDate.setValue(liability.getFinDate());
		this.bankName.setValue(liability.getLoanBank());
		this.bankName.setDescription(StringUtils.trimToEmpty(liability.getLoanBankName()));
		this.finType.setValue(liability.getFinType());
		this.finType.setDescription(StringUtils.trimToEmpty(liability.getFinTypeDesc()));
		this.finStatus.setValue(liability.getFinStatus());
		this.finStatus.setDescription(StringUtils.trimToEmpty(liability.getCustStatusDesc()));
		this.originalAmount.setValue(PennantApplicationUtil.formateAmount(liability.getOriginalAmount(), finFormatter));
		this.installmentAmount
				.setValue(PennantApplicationUtil.formateAmount(liability.getInstalmentAmount(), finFormatter));
		this.outStandingBal
				.setValue(PennantApplicationUtil.formateAmount(liability.getOutstandingBalance(), finFormatter));

		this.custCIF.setValue(StringUtils.trimToEmpty(liability.getCustCif()));
		this.custShrtName.setValue(StringUtils.trimToEmpty(liability.getCustShrtName()));

		// ###_0.2
		this.roi.setValue(PennantApplicationUtil.formateAmount(liability.getRateOfInterest(), finFormatter));
		this.pos.setValue(PennantApplicationUtil.formateAmount(liability.getPrincipalOutstanding(), finFormatter));
		this.overdue.setValue(PennantApplicationUtil.formateAmount(liability.getOverdueAmount(), finFormatter));
		this.totalTenure.setValue(liability.getTenure());
		this.balanceTenure.setValue(liability.getBalanceTenure());
		this.noOfBounces.setValue(liability.getBounceInstalments());
		this.emiFoir.setChecked(liability.isFoir());
		this.securityDetail.setValue(liability.getSecurityDetails());
		this.otherFinInstitute.setValue(liability.getOtherFinInstitute());
		this.endUseOfFunds.setValue(liability.getLoanPurpose());
		this.endUseOfFunds.setDescription(liability.getLoanPurpose());
		this.repayFrom.setValue(liability.getRepayBank());
		this.repayFrom.setDescription(liability.getRepayBankName());
		if (ImplementationConstants.CUSTOM_EXT_LIABILITIES) {
			fillComboBox(this.source, String.valueOf(liability.getSource()), sourceInfoList, ",3,");
			fillComboBox(this.checkedBy, String.valueOf(liability.getCheckedBy()), trackCheckList, ",3,");
		} else {
			fillComboBox(this.source, String.valueOf(liability.getSource()), sourceInfoList, "");
			fillComboBox(this.checkedBy, String.valueOf(liability.getCheckedBy()), trackCheckList, "");
		}
		this.imputedEmi.setValue(PennantApplicationUtil.formateAmount(liability.getImputedEmi(), finFormatter));
		this.currentOverDue.setValue(PennantApplicationUtil.formateAmount(liability.getCurrentOverDue(), finFormatter));
		this.ownerShip.setValue(liability.getOwnerShip());
		this.lastTwentyFourMonths.setChecked(liability.isLastTwentyFourMonths());
		this.lastSixMonths.setChecked(liability.isLastSixMonths());
		this.lastThreeMonths.setChecked(liability.isLastThreeMonths());
		this.repayFromAccNo.setValue(liability.getRepayFromAccNo());
		this.remarks.setValue(liability.getRemarks());
		this.noOfBouncesInSixMonths.setValue(liability.getNoOfBouncesInSixMonths());
		this.noOfBouncesInTwelveMonths.setValue(liability.getNoOfBouncesInTwelveMonths());
		this.consideredBasedOnRTR.setChecked(liability.isConsideredBasedOnRTR());
		this.mob.setValue(liability.getMob());

		if (liability.getExtLiabilitiesPayments().size() > 0) {
			CustomerExtLiability detail = (CustomerExtLiability) ObjectUtil.clone(liability);
			List<ExtLiabilityPaymentdetails> extPaymentsData = detail.getExtLiabilitiesPayments();
			for (int i = 0; i < extPaymentsData.size(); i++) {
				if (extPaymentsData.get(i).getKeyValue() == 0) {
					extPaymentsData.get(i).setKeyValue(i + 1);
				}
			}
			setExtLiabilitiesPaymentdetails(extPaymentsData);
			doFillInstallmentDetails(false);
		} else {
			onChangeInstallmentList();
		}

		if (StringUtils.isNotBlank(liability.getRepayBank())) {
			bankDetails = getBankDetailService().getAccNoLengthByCode(liability.getRepayBank());
			minAccNoLength = bankDetails.getMinAccNoLength();
			maxAccNoLength = bankDetails.getAccNoLength();
		}
		this.repayFromAccNo.setMaxlength(maxAccNoLength);

		// to calculate derived emi
		if (!liability.isNewRecord()) {
			deriveEmi();
		}
		this.recordStatus.setValue(liability.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerExtLiability
	 */
	public void doWriteComponentsToBean(CustomerExtLiability aLiability) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aLiability.setCustShrtName(this.custShrtName.getValue());
			aLiability.setCustCif(this.custCIF.getValue());
			aLiability.setCustId(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLiability.setSeqNo(this.liabilitySeq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLiability.setFinDate(this.finDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLiability.setLoanBankName(this.bankName.getDescription());
			aLiability.setLoanBank(this.bankName.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLiability.setFinType(this.finType.getValidatedValue());
			aLiability.setFinTypeDesc(this.finType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setOtherFinInstitute(this.otherFinInstitute.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLiability.setOriginalAmount(
					PennantApplicationUtil.unFormateAmount(this.originalAmount.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLiability.setInstalmentAmount(
					PennantApplicationUtil.unFormateAmount(this.installmentAmount.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLiability.setOutstandingBalance(
					PennantApplicationUtil.unFormateAmount(this.outStandingBal.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLiability.setFinStatus(this.finStatus.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// ###_0.2
		try {
			aLiability.setRateOfInterest(
					PennantApplicationUtil.unFormateAmount(this.roi.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLiability.setPrincipalOutstanding(
					PennantApplicationUtil.unFormateAmount(this.pos.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setOverdueAmount(
					PennantApplicationUtil.unFormateAmount(this.overdue.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setTenure(this.totalTenure.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setBalanceTenure(this.balanceTenure.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setBounceInstalments(this.noOfBounces.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setFoir(this.emiFoir.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (getComboboxValue(this.source).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.source, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerExtLiabilityDialog_Source.value") }));
			}
			aLiability.setSource(Integer.parseInt(this.source.getSelectedItem().getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (getComboboxValue(this.checkedBy).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.checkedBy, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerExtLiabilityDialog_CheckedBy.value") }));
			}
			aLiability.setCheckedBy(Integer.parseInt(this.checkedBy.getSelectedItem().getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setSecurityDetails(this.securityDetail.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setLoanPurpose(this.endUseOfFunds.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setRepayBank(this.repayFrom.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setInputSource(inputSource);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setImputedEmi(
					PennantApplicationUtil.unFormateAmount(this.imputedEmi.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setCurrentOverDue(
					PennantApplicationUtil.unFormateAmount(this.currentOverDue.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setLastTwentyFourMonths(this.lastTwentyFourMonths.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setLastSixMonths(this.lastSixMonths.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setLastThreeMonths(this.lastThreeMonths.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setOwnerShip(this.ownerShip.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setRepayFromAccNo(this.repayFromAccNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setNoOfBouncesInSixMonths(this.noOfBouncesInSixMonths.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setNoOfBouncesInTwelveMonths(this.noOfBouncesInTwelveMonths.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLiability.setConsideredBasedOnRTR(this.consideredBasedOnRTR.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			int mobVal = this.mob.intValue();
			if (mobVal < 0) {
				throw new WrongValueException(this.mob, "MOB value should be greaterthan 0");
			}
			aLiability.setMob(mobVal);
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
				if (!focus) {
					focus = setComponentFocus(component);
				}
			}
			throw new WrongValuesException(wvea);
		}

		aLiability.setRecordStatus(this.recordStatus.getValue());
		setExternalLiability(aLiability);
		logger.debug("Leaving");
	}

	public void onChange$custType(Event event) {
		logger.debug(Literal.ENTERING);
		// this.reason.setErrorMessage("");
		String type = this.custType.getSelectedItem().getValue();
		if (!type.equals("#")) {
			visibleComponent(Integer.parseInt(type));
		}
		logger.debug(Literal.LEAVING);
	}

	public void setLiabilitySeq() {
		logger.debug(Literal.ENTERING);
		if (getSamplingDialogCtrl() != null && getSamplingDialogCtrl().getCustomerExtLiabilityDetailList() != null
				&& getSamplingDialogCtrl().getCustomerExtLiabilityDetailList().size() > 0) {

			List<CustomerExtLiability> custExtLiabilityList = getSamplingDialogCtrl()
					.getCustomerExtLiabilityDetailList();
			int idNumber = 0;
			for (CustomerExtLiability customerExtLiability : custExtLiabilityList) {
				if (customerExtLiability.getCustCif().equals(this.custCIF.getValue())) {
					int tempId = customerExtLiability.getSeqNo();
					if (tempId > idNumber) {
						idNumber = tempId;
					}
				}
			}
			this.liabilitySeq.setValue(++idNumber);
		}
		logger.debug(Literal.LEAVING);
	}

	private void visibleComponent(Integer type) {
		CustomerExtLiability extLiability = this.externalLiability.getBefImage();
		if (type == 2) {
			this.custCIF.setValue("");
			this.custShrtName.setValue("");
			this.btnSearchPRCustid.setVisible(true);
			this.liabilitySeq.setValue(0);
		} else {
			this.custCIF.setValue(extLiability.getCustCif() == null ? "" : extLiability.getCustCif().trim());
			this.custShrtName
					.setValue(extLiability.getCustShrtName() == null ? "" : extLiability.getCustShrtName().trim());
			this.btnSearchPRCustid.setVisible(false);
			this.liabilitySeq.setValue(this.externalLiability.getSeqNo());
		}
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerExtLiability
	 */
	public void doShowDialog(CustomerExtLiability aliability) {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			this.finType.focus();
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
			doWriteBeanToComponents(aliability);

			doCheckEnquiry();
			doSetInstAmountMandProp();

			if (isNewCustomer()) {
				this.window_CustomerExtLiabilityDialog.setHeight("90%");
				this.window_CustomerExtLiabilityDialog.setWidth("90%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerExtLiabilityDialog.doModal();
			} else {
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
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.bankName.setReadonly(true);
			this.finDate.setDisabled(true);
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
			this.source.setDisabled(true);
			this.checkedBy.setDisabled(true);
			this.securityDetail.setReadonly(true);
			this.otherFinInstitute.setReadonly(true);
			this.endUseOfFunds.setReadonly(true);
			this.repayFrom.setReadonly(true);
			this.repayFromAccNo.setReadonly(true);
			this.remarks.setReadonly(true);
			this.noOfBouncesInSixMonths.setReadonly(true);
			this.noOfBouncesInTwelveMonths.setReadonly(true);
			this.consideredBasedOnRTR.setDisabled(true);
			this.mob.setReadonly(true);
			this.noOfInstallmentMonths.setReadonly(isCustomer360);
			this.imputedEmi.setReadonly(true);
			this.ownerShip.setReadonly(true);
			this.currentOverDue.setReadonly(true);
			this.lastSixMonths.setDisabled(true);
			this.lastThreeMonths.setDisabled(true);
			this.lastTwentyFourMonths.setDisabled(true);
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.finDate.isDisabled()) {
			this.finDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_FinDate.value"), true,
							appStartDate, appDate, true));
		}
		if (!this.finStatus.isReadonly()) {
			this.finStatus.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_FinStatus.value"), null, true, true));
		}
		if (!this.originalAmount.isDisabled()) {
			this.originalAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_OriginalAmount.value"), 0, true, false));
		}
		if (!this.installmentAmount.isDisabled()) {
			this.installmentAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_InstallmentAmount.value"),
							0, this.installmentAmount.isMandatory(), false));
		}
		if (!this.outStandingBal.isReadonly() && ImplementationConstants.CUSTOM_EXT_LIABILITIES) {
			this.outStandingBal.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_OutStandingBal.value"), 0, false, false));
		} else if (!this.outStandingBal.isReadonly()) {
			this.outStandingBal.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_OutStandingBal.value"), 0, true, false));
		}
		// ###_0.2
		if (!this.pos.isReadonly()) {
			this.pos.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_POS.value"),
					0, true, false));
		}
		if (!this.roi.isDisabled()) {
			this.roi.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_ROI.value"),
					0, true, false));
		}
		if (!this.overdue.isDisabled()) {
			this.overdue.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_Overdue.value"), 0, false, false));
		}
		if (!this.totalTenure.isReadonly() && ImplementationConstants.CUSTOM_EXT_LIABILITIES) {
			this.totalTenure.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_TotalTenure.value"), false, false));
		} else if (!this.totalTenure.isReadonly()) {
			this.totalTenure.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_TotalTenure.value"), true, false));
		}
		if (!this.balanceTenure.isReadonly() && ImplementationConstants.CUSTOM_EXT_LIABILITIES) {
			this.balanceTenure.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_BalanceTenure.value"), false, false));
		} else if (!this.balanceTenure.isReadonly()) {
			this.balanceTenure.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_BalanceTenure.value"), true, false));
		}
		if (!this.noOfBounces.isDisabled()) {
			this.noOfBounces.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_NoOfBounces.value"), false, false));
		}
		if (!this.source.isReadonly()) {
			this.source.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_Source.value"), null, true));
		}
		if (!this.checkedBy.isReadonly()) {
			this.checkedBy.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_CheckedBy.value"), null, true));
		}
		if (!this.securityDetail.isReadonly()) {
			this.securityDetail.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_SecurityDetail.value"), null, false, true));
		}
		if (!this.otherFinInstitute.isReadonly()) {
			if (StringUtils.trimToEmpty(this.bankName.getValue()).equals(PennantConstants.OTHER_BANK)) {
				this.otherFinInstitute.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CustomerExtLiabilityDialog_otherFinInstitute.value"), null, true));
			}
		}
		if (!this.noOfBouncesInSixMonths.isDisabled()) {
			this.noOfBouncesInSixMonths.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_NoOfBouncesInSixMonths.value"), false, false));
		}
		if (!this.noOfBouncesInTwelveMonths.isDisabled()) {
			this.noOfBouncesInTwelveMonths.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_NoOfBouncesInTwelveMonths.value"), false, false));
		}

		if (!this.repayFromAccNo.isReadonly() && !StringUtils.isEmpty(this.repayFromAccNo.getValue())) {
			if (this.repayFromAccNo.getMaxlength() != this.repayFromAccNo.getValue().length()) {
				this.repayFromAccNo.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_RepayFromAccNo.value"),
								null, true, minAccNoLength, maxAccNoLength));
			}
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
			this.bankName.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_BankName.value"), null, true, true));
		}
		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_finType.value"), null, true, true));
		}
		if (!this.endUseOfFunds.isReadonly()) {
			this.endUseOfFunds.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_EndUseOfFunds.value"), null, true, true));
		}
		if (!this.repayFrom.isReadonly()) {
			this.repayFrom.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerExtLiabilityDialog_RepayFrom.value"), null, true, true));
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
		this.repayFromAccNo.setConstraint("");
		this.remarks.setConstraint("");
		this.noOfBouncesInSixMonths.setConstraint("");
		this.noOfBouncesInTwelveMonths.setConstraint("");
		this.mob.setConstraint("");
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
		this.otherFinInstitute.setErrorMessage("");
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
		this.repayFromAccNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.noOfBouncesInSixMonths.setErrorMessage("");
		this.noOfBouncesInTwelveMonths.setErrorMessage("");
		this.mob.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CustomerExtLiability aCustomerExtLiability = new CustomerExtLiability();
		BeanUtils.copyProperties(getExternalLiability(), aCustomerExtLiability);

		final String keyReference = Labels.getLabel("label_CustomerExtLiabilityDialog_LiabilitySeq.value") + " : "
				+ aCustomerExtLiability.getSeqNo();

		doDelete(keyReference, aCustomerExtLiability);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final CustomerExtLiability aCustomerExtLiability) {
		String tranType = PennantConstants.TRAN_WF;

		if (StringUtils.isBlank(aCustomerExtLiability.getRecordType())) {
			aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion() + 1);
			aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if (!isFinanceProcess && getCustomerDialogCtrl() != null
					&& getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()) {
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
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newFinanceCustomerProcess(aCustomerExtLiability, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerExtLiabilityDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getCustomerDialogCtrl() != null) {
					getCustomerDialogCtrl().doFillCustomerExtLiabilityDetails(this.externalLiabilities);
				} else if (getSamplingDialogCtrl() != null) {
					getSamplingDialogCtrl().doFillCustomerExtLiabilityDetails(this.externalLiabilities);
				}
				closeDialog();
			}
		} catch (DataAccessException e) {
			MessageUtil.showError(e);
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
				// this.emiFoir.setVisible(!isReadOnly("CustomerExtLiabilityDialog_EMIFoir"));
			} else {
				this.btnSearchPRCustid.setVisible(true);
			}
			this.custType.setDisabled(isReadOnly("CustomerExtLiabilityDialog_BankName"));
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.custType.setDisabled(true);
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
		this.source.setReadonly(isReadOnly("CustomerExtLiabilityDialog_Source"));
		this.source.setDisabled(isReadOnly("CustomerExtLiabilityDialog_Source"));
		this.checkedBy.setReadonly(isReadOnly("CustomerExtLiabilityDialog_CheckedBy"));
		this.checkedBy.setDisabled(isReadOnly("CustomerExtLiabilityDialog_CheckedBy"));
		this.securityDetail.setReadonly(isReadOnly("CustomerExtLiabilityDialog_SecurityDetail"));
		this.endUseOfFunds.setReadonly(isReadOnly("CustomerExtLiabilityDialog_EndUseOfFunds"));
		this.repayFrom.setReadonly(isReadOnly("CustomerExtLiabilityDialog_RepayFrom"));
		this.imputedEmi.setReadonly(isReadOnly("CustomerExtLiabilityDialog_ImputedEmi"));
		this.ownerShip.setReadonly(isReadOnly("CustomerExtLiabilityDialog_Ownership"));
		this.lastTwentyFourMonths.setDisabled(isReadOnly("CustomerExtLiabilityDialog_LastInTwentyFourMths"));
		this.lastSixMonths.setDisabled(isReadOnly("CustomerExtLiabilityDialog_LastInSixMths"));
		this.lastThreeMonths.setDisabled(isReadOnly("CustomerExtLiabilityDialog_LastInThreeMths"));
		this.currentOverDue.setDisabled(isReadOnly("CustomerExtLiabilityDialog_CurrentOverdue"));
		this.repayFromAccNo.setReadonly(isReadOnly("CustomerExtLiabilityDialog_RepayFromAccNo"));
		this.remarks.setReadonly(isReadOnly("CustomerExtLiabilityDialog_Remarks"));
		this.noOfBouncesInSixMonths.setReadonly(isReadOnly("CustomerExtLiabilityDialog_NoOfBouncesInSixMonths"));
		this.noOfBouncesInTwelveMonths.setReadonly(isReadOnly("CustomerExtLiabilityDialog_NoOfBouncesInTwelveMonths"));
		this.consideredBasedOnRTR.setDisabled(isReadOnly("CustomerExtLiabilityDialog_consideredBasedOnRTR"));
		this.mob.setReadonly(isReadOnly("CustomerExtLiabilityDialog_MOB"));
		this.emiFoir.setDisabled(isReadOnly("CustomerExtLiabilityDialog_EMIFoir"));
		this.imputedEmi.setReadonly(false);
		this.ownerShip.setReadonly(false);
		this.lastTwentyFourMonths.setDisabled(false);
		this.lastSixMonths.setDisabled(false);
		this.lastThreeMonths.setDisabled(false);
		this.currentOverDue.setDisabled(false);

		if (externalLiability.getLoanBank() != null) {
			if (!externalLiability.getLoanBank().equals(PennantConstants.OTHER_BANK)) {
				this.otherFinInstitute.setReadonly(true);
			} else {
				this.otherFinInstitute.setReadonly(isReadOnly("CustomerExtLiabilityDialog_OtherName"));
			}
		} else {
			this.otherFinInstitute.setReadonly(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.externalLiability.isNewRecord()) {
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
		if (isWorkFlowEnabled() || isCustomerWorkflow || isFinanceProcess) {
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
		this.otherFinInstitute.setReadonly(true);
		this.endUseOfFunds.setReadonly(true);
		this.repayFrom.setReadonly(true);
		this.imputedEmi.setReadonly(true);
		this.ownerShip.setReadonly(true);
		this.lastTwentyFourMonths.setDisabled(true);
		this.lastSixMonths.setDisabled(true);
		this.lastThreeMonths.setDisabled(true);
		this.currentOverDue.setReadonly(true);
		this.repayFromAccNo.setReadonly(true);
		this.remarks.setReadonly(true);
		this.noOfBouncesInSixMonths.setReadonly(true);
		this.noOfBouncesInTwelveMonths.setReadonly(true);
		this.consideredBasedOnRTR.setDisabled(true);
		this.mob.setReadonly(true);

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
		this.repayFromAccNo.setValue("");
		this.remarks.setValue("");
		this.noOfBouncesInSixMonths.setValue(0);
		this.noOfBouncesInTwelveMonths.setValue(0);
		this.mob.setValue(0);

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
		BeanUtils.copyProperties(getExternalLiability(), aCustomerExtLiability);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerExtLiability object with the components data
		doWriteComponentsToBean(aCustomerExtLiability);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		if (!saveInstallmentInfoList(aCustomerExtLiability)) {
			return;
		}

		isNew = aCustomerExtLiability.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerExtLiability.getRecordType())) {
				aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion() + 1);
				if (isNew) {
					aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerExtLiability.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aCustomerExtLiability.setVersion(1);
					aCustomerExtLiability.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aCustomerExtLiability.getRecordType())) {
					aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion() + 1);
					aCustomerExtLiability.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aCustomerExtLiability.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newFinanceCustomerProcess(aCustomerExtLiability, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerExtLiabilityDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getCustomerDialogCtrl() != null) {
					getCustomerDialogCtrl().doFillCustomerExtLiabilityDetails(this.externalLiabilities);
				} else if (getSamplingDialogCtrl() != null) {
					getSamplingDialogCtrl().doFillCustomerExtLiabilityDetails(this.externalLiabilities);
				}
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFinanceCustomerProcess(CustomerExtLiability aCustomerExtLiability, String tranType) {
		logger.debug("Entering");
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCustomerExtLiability, tranType);
		externalLiabilities = new ArrayList<>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustomerExtLiability.getCustId());
		valueParm[1] = String.valueOf(aCustomerExtLiability.getSeqNo());

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_LiabilitySeq") + ":" + valueParm[1];

		List<CustomerExtLiability> custExtLiabilityList = null;

		if (getCustomerDialogCtrl() != null && getCustomerDialogCtrl().getCustomerExtLiabilityDetailList() != null
				&& getCustomerDialogCtrl().getCustomerExtLiabilityDetailList().size() > 0) {
			custExtLiabilityList = getCustomerDialogCtrl().getCustomerExtLiabilityDetailList();

		} else if (getSamplingDialogCtrl() != null
				&& getSamplingDialogCtrl().getCustomerExtLiabilityDetailList() != null
				&& getSamplingDialogCtrl().getCustomerExtLiabilityDetailList().size() > 0) {
			custExtLiabilityList = getSamplingDialogCtrl().getCustomerExtLiabilityDetailList();
		}

		if (custExtLiabilityList != null && custExtLiabilityList.size() > 0) {
			for (int i = 0; i < custExtLiabilityList.size(); i++) {
				CustomerExtLiability customerExtLiability = custExtLiabilityList.get(i);

				if (aCustomerExtLiability.getSeqNo() == customerExtLiability.getSeqNo()
						&& aCustomerExtLiability.getCustId() == customerExtLiability.getCustId()) {
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							externalLiabilities.add(aCustomerExtLiability);
						} else if (aCustomerExtLiability.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							externalLiabilities.add(aCustomerExtLiability);
						} else if (aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails()
									.getCustomerExtLiabilityList().size(); j++) {
								CustomerExtLiability custExtLiability = getCustomerDialogCtrl().getCustomerDetails()
										.getCustomerExtLiabilityList().get(j);
								if (custExtLiability.getCustId() == aCustomerExtLiability.getCustId()
										&& custExtLiability.getSeqNo() == aCustomerExtLiability.getSeqNo()) {
									externalLiabilities.add(custExtLiability);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							externalLiabilities.add(customerExtLiability);
						}
					}
				} else {
					externalLiabilities.add(customerExtLiability);
				}
			}
		}

		if (!recordAdded) {
			externalLiabilities.add(aCustomerExtLiability);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	public void onFulfill$bankName(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = bankName.getObject();

		if (dataObject == null || dataObject instanceof String) {
			this.bankName.setValue("");
			this.bankName.setDescription("");
			this.otherFinInstitute.setValue("");
			this.otherFinInstitute.setReadonly(true);
			this.space_Other.setClass("");
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details.getBankCode().equals(PennantConstants.OTHER_BANK)) {
				this.space_Other.setClass(PennantConstants.mandateSclass);
				this.otherFinInstitute.setReadonly(isReadOnly("CustomerExtLiabilityDialog_OtherName"));
			} else {
				this.space_Other.setClass("");
				this.otherFinInstitute.setValue("");
				this.otherFinInstitute.setReadonly(true);
			}

			if (StringUtils.isNotBlank(details.getBankCode())) {
				bankDetails = getBankDetailService().getAccNoLengthByCode(details.getBankCode());
				minAccNoLength = bankDetails.getMinAccNoLength();
				maxAccNoLength = bankDetails.getAccNoLength();
			}

			this.repayFromAccNo.setMaxlength(maxAccNoLength);

		}
		logger.debug(Literal.LEAVING);
	}

	// Search Button Component Events

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
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
		setLiabilitySeq();
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
	private AuditHeader getAuditHeader(CustomerExtLiability externalLiability, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, externalLiability.getBefImage(), externalLiability);
		return new AuditHeader(getReference(), String.valueOf(externalLiability.getCustId()), null, null, auditDetail,
				externalLiability.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.externalLiability);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getExternalLiability().getCustId() + PennantConstants.KEY_SEPERATOR
				+ getExternalLiability().getLoanBank();
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

	public CustomerExtLiability getExternalLiability() {
		return this.externalLiability;
	}

	public void setExternalLiability(CustomerExtLiability externalLiability) {
		this.externalLiability = externalLiability;
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

	public void onChange$totalTenure(Event event) {
		logger.debug(Literal.ENTERING);
		deriveEmi();
		onChangeInstallmentList();
		logger.debug(Literal.LEAVING);
	}

	public void onChange$noOfInstallmentMonths(Event event) {
		logger.debug(Literal.ENTERING);
		// onChangeInstallmentList();
		doFillInstallmentDetails(true);
		logger.debug(Literal.LEAVING);
	}

	public void onChangeInstallmentList() {
		try {
			if (this.noOfInstallmentMonths.getValue() != null) {
				listBoxInstallmentDetails.getItems().clear();
				int noOfmonths = 0;

				Integer tenure = this.totalTenure.getValue();
				if (tenure == null) {
					tenure = 0;
				}

				noOfmonths = this.noOfInstallmentMonths.getValue() == 0 ? tenure
						: this.noOfInstallmentMonths.getValue();
				Date date = SysParamUtil.getAppDate();
				int emiList = DateUtil.getMonthsBetween(finDate.getValue(), appDate);
				List<ExtLiabilityPaymentdetails> paymentDetails = getPaymentDetails(date, noOfmonths, emiList);

				ExtLiabilityPaymentdetails installmentDetails = new ExtLiabilityPaymentdetails();
				installmentDetails.setNewRecord(true);

				if (paymentDetails.size() > 0) {
					Collections.reverse(paymentDetails);
					List<ExtLiabilityPaymentdetails> extPaymentsData = paymentDetails;
					for (int i = 0; i < extPaymentsData.size(); i++) {
						extPaymentsData.get(i).setKeyValue(i + 1);
					}
					// displaying the latest emi's first
					Collections.reverse(paymentDetails);
					setExtLiabilitiesPaymentdetails(extPaymentsData);
					doFillInstallmentDetails(false);
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}

	public void doFillInstallmentDetails(boolean fromRTR) {
		listBoxInstallmentDetails.getItems().clear();
		List<ExtLiabilityPaymentdetails> paymentDetails = getExtLiabilitiesPaymentdetails();
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			isCustomer360 = true;
		}
		int totalInstallments = paymentDetails.size();
		// Following Scenario is when, the number of installments for RTR is greater than the given loan tenure.
		if (fromRTR && noOfInstallmentMonths.intValue() <= totalInstallments) {
			totalInstallments = noOfInstallmentMonths.intValue();
		}
		for (int i = 0; i < totalInstallments; i++) {
			ExtLiabilityPaymentdetails installmentDetails = paymentDetails.get(i);
			Listitem listitem = new Listitem();
			listitem.setAttribute("data", installmentDetails);
			Listcell listcell;
			Listcell clearedDateCell = new Listcell();
			listcell = new Listcell(installmentDetails.getEmiType());
			listcell.setParent(listitem);

			Combobox emiClearanceCombobox = new Combobox();
			emiClearanceCombobox.setReadonly(true);
			emiClearanceCombobox.setWidth("100px");
			listcell = new Listcell();
			listcell.setId("emiClearance".concat(String.valueOf(installmentDetails.getKeyValue())));
			fillComboBox(emiClearanceCombobox, installmentDetails.getEmiClearance(),
					PennantStaticListUtil.getEmiClearance(), "");
			Object[] clearedDateData = new Object[1];
			clearedDateData[0] = clearedDateCell;
			emiClearanceCombobox.addForward("onChange", self, "onChangeInstallmentCleared", clearedDateData);
			listcell.appendChild(emiClearanceCombobox);
			listcell.setParent(listitem);

			// New field added for EMI Cleared Date
			Intbox clearedDate = new Intbox();
			clearedDate.setValue(installmentDetails.getEmiClearedDay());
			clearedDate.setDisabled(isCustomer360);
			clearedDateCell.setId("clearedDay".concat(String.valueOf(installmentDetails.getKeyValue())));
			clearedDateCell.appendChild(clearedDate);
			clearedDate.setMaxlength(2);

			if (isCustomer360) {
				emiClearanceCombobox.setDisabled(isCustomer360);
			} else {
				emiClearanceCombobox.setDisabled(isReadOnly("CustomerExtLiabilityDialog_EmiClearance"));
			}
			if (emiClearanceCombobox.isDisabled()) {
				clearedDate.setReadonly(true);
			} else {
				String emiCleared = getComboboxValue(emiClearanceCombobox);
				clearedDate.setReadonly(!StringUtils.equals(emiCleared, PennantConstants.CLEARED));
			}
			clearedDateCell.setParent(listitem);
			// for customer 360 it should be disable
			listitem.setDisabled(isCustomer360);

			emiClearanceCombobox.setDisabled(isReadOnly("CustomerExtLiabilityDialog_EmiClearance"));

			listBoxInstallmentDetails.appendChild(listitem);
		}
		if (!fromRTR) {
			this.noOfInstallmentMonths.setText(String.valueOf(paymentDetails.size()));
		}
	}

	public void onChangeInstallmentCleared(ForwardEvent event) {
		logger.debug(Literal.ENTERING + event.toString());
		Combobox comboBox = (Combobox) event.getOrigin().getTarget();
		Object[] data = (Object[]) event.getData();
		Listcell clearedDateCell = (Listcell) data[0];
		Intbox intbox = (Intbox) clearedDateCell.getChildren().get(0);
		if (PennantConstants.CLEARED.equals(getComboboxValue(comboBox))) {
			intbox.setReadonly(false);
		} else {
			intbox.setValue(0);
			intbox.setReadonly(true);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public List<ExtLiabilityPaymentdetails> getPaymentDetails(Date startDate, int noOfMonths, int emiList) {
		Date dtStartDate = startDate;
		Date dtEndDate = DateUtil.addMonths(dtStartDate, -noOfMonths);
		List<ExtLiabilityPaymentdetails> months = getFrequency(dtStartDate, dtEndDate, noOfMonths);
		return months;
	}

	private List<ExtLiabilityPaymentdetails> getFrequency(final Date startDate, final Date endDate, int noOfMonths) {
		List<ExtLiabilityPaymentdetails> list = new ArrayList<>();
		if (startDate == null || endDate == null) {
			return list;
		}

		Date tempStartDate = (Date) startDate.clone();
		Date tempEndDate = (Date) endDate.clone();

		while (DateUtil.compare(tempStartDate, tempEndDate) > 0) {
			ExtLiabilityPaymentdetails temp = new ExtLiabilityPaymentdetails();
			String key = DateUtil.format(tempStartDate, DateFormat.LONG_MONTH);
			temp.setEmiType(key);
			tempStartDate = DateUtil.addMonths(tempStartDate, -1);
			list.add(temp);
		}
		return list;
	}

	public boolean saveInstallmentInfoList(CustomerExtLiability customerExtLiabilityData) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		List<ExtLiabilityPaymentdetails> data = new ArrayList<>();
		for (Listitem listitem : listBoxInstallmentDetails.getItems()) {

			ExtLiabilityPaymentdetails installmentDetails = (ExtLiabilityPaymentdetails) listitem.getAttribute("data");

			Combobox emiClearance = (Combobox) getComponent(listitem, "emiClearance");
			installmentDetails.setEmiClearance(emiClearance.getSelectedItem().getValue());
			Intbox emiClearedDay = (Intbox) getComponent(listitem, "clearedDay");
			String emiCleared = getComboboxValue(emiClearance);
			if (PennantConstants.CLEARED.equalsIgnoreCase(emiCleared)) {
				if (emiClearedDay.getValue() <= 0 || emiClearedDay.getValue() > 31) {
					throw new WrongValueException(emiClearedDay,
							Labels.getLabel("label_ExternelLiabilities_ClearedDate.value"));
				}
			}
			installmentDetails.setEmiClearedDay(emiClearedDay.intValue());
			boolean isNew = false;
			isNew = installmentDetails.isNewRecord();
			String tranType = "";

			if (installmentDetails.isNewRecord()) {
				installmentDetails.setVersion(1);
				installmentDetails.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(installmentDetails.getRecordType())) {
				installmentDetails.setVersion(installmentDetails.getVersion() + 1);
				installmentDetails.setRecordType(PennantConstants.RCD_UPD);
			}

			if (installmentDetails.getRecordType().equals(PennantConstants.RCD_ADD)
					&& installmentDetails.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (installmentDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			} else {
				installmentDetails.setVersion(installmentDetails.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
			data.add(installmentDetails);
		}

		customerExtLiabilityData.setExtLiabilitiesPayments(data);
		logger.debug(Literal.LEAVING);
		return true;
	}

	private Component getComponent(Listitem listitem, String listcellId) {
		List<Listcell> listcels = listitem.getChildren();

		for (Listcell listcell : listcels) {
			String id = StringUtils.trimToNull(listcell.getId());

			if (id == null) {
				continue;
			}

			id = id.replaceAll("\\d", "");
			if (StringUtils.equals(id, listcellId)) {
				return listcell.getFirstChild();
			}
		}
		return null;
	}

	public void onCheckInstallmentCleared(ForwardEvent event) {
		logger.debug(Literal.ENTERING + event.toString());
		Checkbox checkbox = (Checkbox) event.getOrigin().getTarget();
		Object[] data = (Object[]) event.getData();
		Listcell clearedDateCell = (Listcell) data[0];
		Intbox intbox = (Intbox) clearedDateCell.getChildren().get(0);
		if (checkbox.isChecked()) {
			intbox.setReadonly(false);
		} else {
			intbox.setReadonly(true);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onFulfill$repayFrom(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = repayFrom.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.repayFrom.setValue("");
			this.repayFrom.setDescription("");
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (StringUtils.isNotBlank(details.getBankCode())) {
				bankDetails = getBankDetailService().getAccNoLengthByCode(details.getBankCode());
				minAccNoLength = bankDetails.getMinAccNoLength();
				maxAccNoLength = bankDetails.getAccNoLength();
			}

			this.repayFromAccNo.setMaxlength(maxAccNoLength);

		}
		logger.debug(Literal.LEAVING);
	}

	private void deriveEmi() {
		try {
			if (ImplementationConstants.DERIVED_EMI_REQ) {
				BigDecimal rate = this.roi.getValidateValue() == null ? BigDecimal.ZERO : this.roi.getValidateValue();
				BigDecimal originalAmount = this.originalAmount.getValidateValue();
				double totTenure = this.totalTenure.intValue();
				double prin = originalAmount.doubleValue();
				double drate = rate.doubleValue();
				double caculatedROI = drate / (12 * 100);
				// (principal*rate*Math.pow(1+rate,time))/(Math.pow(1+rate,time)-1)
				double emi = (prin * caculatedROI * Math.pow(1 + caculatedROI, totTenure))
						/ (Math.pow(1 + caculatedROI, totTenure) - 1);
				this.imputedEmi.setValue(new BigDecimal(emi));
			}
		} catch (Exception e) {
			this.imputedEmi.setValue(BigDecimal.ZERO);
		}
	}

	public void onValueChange$originalAmount(Event event) {
		logger.debug(Literal.ENTERING);
		deriveEmi();
		logger.debug(Literal.LEAVING);
	}

	public void onValueChange$roi(Event event) {
		logger.debug(Literal.ENTERING);
		deriveEmi();
		logger.debug(Literal.LEAVING);
	}

	public List<ExtLiabilityPaymentdetails> getExtLiabilitiesPaymentdetails() {
		return extLiabilitiesPaymentdetails;
	}

	public void setExtLiabilitiesPaymentdetails(List<ExtLiabilityPaymentdetails> extLiabilitiesPaymentdetails) {
		this.extLiabilitiesPaymentdetails = extLiabilitiesPaymentdetails;
	}

	public BankDetailService getBankDetailService() {
		return bankDetailService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

}
