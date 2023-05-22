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
 * * FileName : CustomerBankInfoDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * 18-04-2018 Vinay 0.2 As per Profectus document added some fields * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.MasterDefUtil;
import com.pennant.app.util.MasterDefUtil.AccountType;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.ExternalDocument;
import com.pennant.backend.model.perfios.PerfiosHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.PerfiousService;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerBankInfo/customerBankInfoDialog.zul file.
 */
public class CustomerBankInfoDialogCtrl extends GFCBaseCtrl<CustomerBankInfo> {
	private static final long serialVersionUID = -7522534300621535097L;
	private static final Logger logger = LogManager.getLogger(CustomerBankInfoDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerBankInfoDialog;

	protected Longbox custID;
	protected Textbox custCIF;
	protected Label custShrtName;
	protected ExtendedCombobox bankName;
	protected Textbox accountNumber;
	protected ExtendedCombobox accountType;
	protected Checkbox salaryAccount;
	protected Checkbox addToBenficiary;

	protected Row row6;
	protected Row row7;
	protected Row row8;
	protected Row row9;
	protected Row row10;
	protected Row row11;
	protected Row row12;
	protected Row row13;

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
	protected Textbox bankBranch;
	protected Datebox fromDate;
	protected Datebox toDate;
	protected Combobox repaymentFrom;
	protected Intbox NoOfMonthsBanking;
	protected Textbox lwowRatio;
	protected CurrencyBox ccLimit;
	protected Combobox typeOfBanks;
	protected Datebox accountOpeningDate;
	protected Textbox phoneNumber; // autowired

	// BHFL
	protected Button button_CustomerBankInfoDialog_btnAccBehaviour;
	protected Toolbar toolBar_AccBehaviour;
	protected Listbox listBoxAccBehaviour;
	protected Listheader listHead_AccBehaviour;
	protected Button btnSearchPRCustid;
	protected Label label_CustomerBankInfoDialog_SalaryAccount;
	protected ExtendedCombobox bankBranchID;
	protected Textbox accountHolderName;
	protected Space spAccountHolderName;
	protected Listheader lRistheader_BankBalance;
	protected Label label_CustomerBankInfoDialog_CreditTranNo;
	protected Groupbox accountBehaviourSumary;

	protected JdbcSearchObject<Customer> newSearchObject;

	private int finFormatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	private CustomerBankInfo customerBankInfo;
	private transient boolean validationOn;
	private boolean newRecord = false;
	private boolean newCustomer = false;
	private List<CustomerBankInfo> CustomerBankInfoList;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	private String moduleType = "";
	private String userRole = "";
	private boolean isFinanceProcess = false;
	private int maxAccNoLength;
	private int minAccNoLength;
	private transient BankDetailService bankDetailService;
	private List<BankInfoDetail> bankInfoDetails = new ArrayList<>();
	private List<BankInfoSubDetail> bankInfoSubDetails = new ArrayList<>();
	private String monthlyIncome = SysParamUtil.getValueAsString(SMTParameterConstants.MONTHLY_INCOME_REQ);
	private String configDay = SysParamUtil.getValueAsString(SMTParameterConstants.BANKINFO_DAYS);
	private boolean isCustomer360 = false;
	private BankDetail bankDetail;

	private boolean fromLoan = false;
	private String empType = null;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private int tenor;
	private String finReference = null;
	private Groupbox gb_perfios;
	private Button button_CustomerBankInfoDialog_btnInitiateperfios;
	private Button button_CustomerBankInfoDialog_btnPerfiosDocUpload;
	private List<ExternalDocument> externalDocumentsList = new ArrayList<>();
	protected PerfiousService perfiosService;
	protected CustomerDetailsService customerDetailsService;
	private DMSService dmsService;

	protected Listbox listBoxDocuments;

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
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerBankInfo object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerBankInfoDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerBankInfoDialog);

		try {

			if (arguments.containsKey("customerBankInfo")) {
				this.customerBankInfo = (CustomerBankInfo) arguments.get("customerBankInfo");
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
				if (!isRetailCust) {
					this.label_CustomerBankInfoDialog_SalaryAccount.setVisible(false);
					this.salaryAccount.setVisible(false);
				}
			}

			if (getCustomerBankInfo().isNewRecord()) {
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
				this.customerBankInfo.setWorkflowId(0);

				if (arguments.containsKey("fromLoan")) {
					fromLoan = (Boolean) arguments.get("fromLoan");
				}

				if (arguments.containsKey("empType")) {
					empType = (String) arguments.get("empType");
				}

				if (arguments.containsKey("finAmount")) {
					finAmount = (BigDecimal) arguments.get("finAmount");
				}
				if (arguments.containsKey("tenor")) {
					tenor = (int) arguments.get("tenor");
				}
				if (arguments.containsKey("finReference")) {
					finReference = (String) arguments.get("finReference");
				}

				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerBankInfoDialog");
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
			doLoadWorkFlow(this.customerBankInfo.isWorkflow(), this.customerBankInfo.getWorkflowId(),
					this.customerBankInfo.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerBankInfoDialog");
			}

			if (arguments.containsKey("customer360")) {
				isCustomer360 = (boolean) arguments.get("customer360");
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

		if (this.addToBenficiary.isChecked()) {
			this.bankBranchID.setButtonDisabled(false);
		} else {
			this.bankBranchID.setButtonDisabled(true);
		}

		if (fromLoan) {
			this.gb_perfios.setVisible(true);
		}

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

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setValueColumn("IFSC");
		this.bankBranchID.setDescColumn("");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "IFSC" });

		fillComboBox(repaymentFrom, "", PennantStaticListUtil.getYesNo(), "");
		fillComboBox(typeOfBanks, "", PennantStaticListUtil.getTypeOfBanks(), "");
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("FieldCode", "ACC_TYPE", Filter.OP_EQUAL);
		this.accountType.setFilters(filter);

		this.accountType.setMaxlength(8);
		if (isNewRecord()) {
			this.addToBenficiary.setDisabled(false);
		} else {
			this.addToBenficiary.setDisabled(true);
		}

		if (StringUtils.isNotBlank(this.bankName.getValue())) {
			bankDetail = bankDetailService.getAccNoLengthByCode(this.bankName.getValue());
			maxAccNoLength = bankDetail.getAccNoLength();
			minAccNoLength = bankDetail.getMinAccNoLength();
		}
		if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {
			lRistheader_BankBalance.setWidth("450px");
		}
		this.accountNumber.setMaxlength(maxAccNoLength);

		// ###_0.2
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

		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.accountOpeningDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
		}
		this.ccLimit.setMandatory(false);
		this.ccLimit.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.ccLimit.setScale(finFormatter);

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
		getUserWorkspace().allocateAuthorities("CustomerBankInfoDialog", userRole);

		/*
		 * this.button_CustomerBankInfoDialog_btnPerfiosDocUpload
		 * .setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnPerfiosDocUpload"));
		 */
		/*
		 * this.button_CustomerBankInfoDialog_btnInitiateperfios
		 * .setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnInitiateperfios"));
		 */
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnSave"));
		this.btnCancel.setVisible(false);
		if (perfiosService == null) {
			this.button_CustomerBankInfoDialog_btnInitiateperfios.setVisible(false);
		} else {
			this.button_CustomerBankInfoDialog_btnInitiateperfios.setVisible(true);
		}
		logger.debug("Leaving");
	}

	public void onClick$button_CustomerBankInfoDialog_btnAccBehaviour(Event event) {
		logger.debug(Literal.ENTERING);
		BankInfoDetail bankInfoDetail = new BankInfoDetail();
		bankInfoDetail.setNewRecord(true);
		int keyValue = 0;

		List<Listitem> bankInfoList = listBoxAccBehaviour.getItems();
		if (bankInfoList != null && !bankInfoList.isEmpty()) {
			for (Listitem detail : bankInfoList) {
				BankInfoDetail bankInfo = (BankInfoDetail) detail.getAttribute("data");
				if (bankInfo != null && bankInfo.getKeyValue() > keyValue) {
					keyValue = bankInfo.getKeyValue();
				}
			}
		}
		bankInfoDetail.setKeyValue(keyValue + 1);

		renderItem(bankInfoDetail);
		renderSummary();
		logger.debug(Literal.LEAVING);

	}

	private void doFillBankInfoDetails() {
		logger.debug(Literal.ENTERING);

		this.listBoxAccBehaviour.getItems().clear();
		int size = getBankInfoDetails().size();
		for (int i = 0; i < size; i++) {
			if (!StringUtils.equals(getBankInfoDetails().get(i).getRecordType(), PennantConstants.RECORD_TYPE_DEL)
					&& !StringUtils.equals(getBankInfoDetails().get(i).getRecordType(),
							PennantConstants.RECORD_TYPE_CAN)) {
				renderItem(getBankInfoDetails().get(i));
			}
		}
		renderSummary();
		logger.debug(Literal.LEAVING);
	}

	private void renderSummary() {

		logger.debug(Literal.ENTERING);

		try {
			// Monthly Balance Amount
			BigDecimal balanceSum = BigDecimal.ZERO;
			BigDecimal balanceAvg = BigDecimal.ZERO;

			int dataItems = 0;

			int count = 0;
			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {

				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {
					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					dataItems++;

					List<Listcell> listcels = listItem.getChildren();

					for (Listcell listcell : listcels) {
						String id = StringUtils.trimToNull(listcell.getId());

						if (id == null) {
							continue;
						}

						id = id.replaceAll("\\d", "");
						if (SysParamUtil
								.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {
							if (StringUtils.equals(id, "balance")) {

								String[] array = configDay.split(",");

								for (int i = 1; i <= array.length; i++) {
									CurrencyBox balanceValue = (CurrencyBox) listcell.getFellowIfAny(
											"balance_currency".concat(String.valueOf(bankInfoDetail.getKeyValue()))
													.concat(String.valueOf(i)));
									if (this.accountType.getValue() != null
											&& (StringUtils.equals(this.accountType.getValue(), "CC")
													|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
										balanceValue.setAllowNagativeValues(true);
									}
									Clients.clearWrongValue(balanceValue);
									if (balanceValue.getValidateValue() != null) {
										balanceSum = balanceSum.add(balanceValue.getValidateValue());
										count++;
									}
								}
							}
						} else {
							if (StringUtils.equals(id, "balance")) {
								Hbox hbox = (Hbox) getComponent(listItem, "balance");
								CurrencyBox avgAmtAmount = (CurrencyBox) hbox.getLastChild();
								Clients.clearWrongValue(avgAmtAmount);
								if (this.accountType.getValue() != null
										&& (StringUtils.equals(this.accountType.getValue(), "CC")
												|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
									avgAmtAmount.setAllowNagativeValues(true);
								}
								if (avgAmtAmount.getValidateValue() != null) {
									balanceSum = balanceSum.add(avgAmtAmount.getValidateValue());
								}
							}
						}
					}
				}
			}

			if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {

				if (balanceSum.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal divider = BigDecimal.valueOf(count, 0);
					balanceAvg = PennantApplicationUtil.unFormateAmount(balanceSum, PennantConstants.defaultCCYDecPos)
							.divide(divider, 0, RoundingMode.HALF_DOWN);
				}
			} else {
				if (balanceSum.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
					balanceAvg = PennantApplicationUtil.unFormateAmount(balanceSum, PennantConstants.defaultCCYDecPos)
							.divide(divider, 0, RoundingMode.HALF_DOWN);
				} else if (balanceSum.compareTo(BigDecimal.ZERO) < 0) {
					BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
					balanceAvg = PennantApplicationUtil.unFormateAmount(balanceSum, PennantConstants.defaultCCYDecPos)
							.divide(divider, 0, RoundingMode.HALF_DOWN);
				}

			}

			// Debit Transaction Count
			BigDecimal dbtTransactionCountSum = BigDecimal.ZERO;
			BigDecimal debitTransactionCountAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {
					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "debitNo");
					Intbox dbtTransactionCountAmount = (Intbox) hbox.getLastChild();
					Clients.clearWrongValue(dbtTransactionCountAmount);
					if (dbtTransactionCountAmount.getValue() != null) {
						dbtTransactionCountSum = dbtTransactionCountSum
								.add(new BigDecimal(dbtTransactionCountAmount.getValue()));
					}
				}
			}

			if (dbtTransactionCountSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				debitTransactionCountAvg = PennantApplicationUtil
						.unFormateAmount(dbtTransactionCountSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}

			// Debit Amount
			BigDecimal debitAmtSum = BigDecimal.ZERO;
			BigDecimal debitAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {
					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "debitAmt");
					CurrencyBox debitAmount = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(debitAmount);
					if (debitAmount.getValidateValue() != null) {
						debitAmtSum = debitAmtSum.add(debitAmount.getValidateValue());
					}
				}
			}

			if (debitAmtSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				debitAvg = PennantApplicationUtil.unFormateAmount(debitAmtSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}

			// credit Transaction Count
			BigDecimal creditTransactionCountSum = BigDecimal.ZERO;
			BigDecimal creditTransactionCountAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "creditNo");
					Intbox creditTransactionCountAmount = (Intbox) hbox.getLastChild();
					Clients.clearWrongValue(creditTransactionCountAmount);
					if (creditTransactionCountAmount.getValue() != null) {
						creditTransactionCountSum = creditTransactionCountSum
								.add(new BigDecimal(creditTransactionCountAmount.getValue()));
					}
				}
			}

			if (creditTransactionCountSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				creditTransactionCountAvg = PennantApplicationUtil
						.unFormateAmount(creditTransactionCountSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}

			// Credit Amount
			BigDecimal creditAmtSum = BigDecimal.ZERO;
			BigDecimal creditAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "creditAmt");
					CurrencyBox creditAmount = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(creditAmount);
					if (creditAmount.getValidateValue() != null) {
						creditAmtSum = creditAmtSum.add(creditAmount.getValidateValue());
					}
				}
			}

			if (creditAmtSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				creditAvg = PennantApplicationUtil.unFormateAmount(creditAmtSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}

			// Inward Amount
			BigDecimal bounceInwardSum = BigDecimal.ZERO;
			BigDecimal inwardAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "bounceInWard");
					CurrencyBox bounceInwardAmount = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(bounceInwardAmount);
					if (bounceInwardAmount.getValidateValue() != null) {
						bounceInwardSum = bounceInwardSum.add(bounceInwardAmount.getValidateValue());
					}
				}
			}

			if (bounceInwardSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				inwardAvg = PennantApplicationUtil.unFormateAmount(bounceInwardSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}

			// Outward Amount
			BigDecimal bounceOutwardSum = BigDecimal.ZERO;
			BigDecimal outwardAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "bounceOutWard");
					CurrencyBox bounceOutwardAmount = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(bounceOutwardAmount);
					if (bounceOutwardAmount.getValidateValue() != null) {
						bounceOutwardSum = bounceOutwardSum.add(bounceOutwardAmount.getValidateValue());
					}
				}
			}

			if (bounceOutwardSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				outwardAvg = PennantApplicationUtil.unFormateAmount(bounceOutwardSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}

			// Closing Balance
			BigDecimal clsBalSum = BigDecimal.ZERO;
			BigDecimal clsBalAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "closingBal");
					CurrencyBox clsBal = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(clsBal);
					if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
							|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
						clsBal.setAllowNagativeValues(true);
					}
					if (clsBal.getValidateValue() != null) {
						clsBalSum = clsBalSum.add(clsBal.getValidateValue());
					}
				}
			}

			if (dataItems > 0) {
				BigDecimal clsBalDivider = BigDecimal.valueOf(dataItems, 0);
				clsBalAvg = PennantApplicationUtil.unFormateAmount(clsBalSum, PennantConstants.defaultCCYDecPos)
						.divide(clsBalDivider, 0, RoundingMode.HALF_DOWN);
			}
			// Sanction Limit
			BigDecimal sanctionLimitSum = BigDecimal.ZERO;
			BigDecimal sanctionLimitAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "sanctionLimit");
					CurrencyBox sanctionLimitAmount = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(sanctionLimitAmount);
					if (sanctionLimitAmount.getValidateValue() != null) {
						sanctionLimitSum = sanctionLimitSum.add(sanctionLimitAmount.getValidateValue());
					}
				}
			}

			if (sanctionLimitSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				sanctionLimitAvg = PennantApplicationUtil
						.unFormateAmount(sanctionLimitSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}
			// Average Utilization
			BigDecimal avgUtilizationSum = BigDecimal.ZERO;
			BigDecimal avgUtilizationAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "avgUtilization");
					CurrencyBox avgUtilizationPercentage = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(avgUtilizationPercentage);
					if (avgUtilizationPercentage.getValidateValue() != null) {
						avgUtilizationSum = avgUtilizationSum.add(avgUtilizationPercentage.getValidateValue());
					}
				}
			}

			if (avgUtilizationSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				avgUtilizationAvg = PennantApplicationUtil
						.unFormateAmount(avgUtilizationSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}

			// Peak Utilization Level
			BigDecimal peakUtilizationLevelSum = BigDecimal.ZERO;
			BigDecimal peakUtilizationLevelAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "peakUtilizationLevel");
					CurrencyBox peakUtilizationLevelPercentage = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(peakUtilizationLevelPercentage);
					if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
							|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
						peakUtilizationLevelPercentage.setAllowNagativeValues(true);
					}
					if (peakUtilizationLevelPercentage.getValidateValue() != null) {
						peakUtilizationLevelSum = peakUtilizationLevelSum
								.add(peakUtilizationLevelPercentage.getValidateValue());
					}
				}
			}

			if (peakUtilizationLevelSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				peakUtilizationLevelAvg = PennantApplicationUtil
						.unFormateAmount(peakUtilizationLevelSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			} else if (peakUtilizationLevelSum.compareTo(BigDecimal.ZERO) < 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				peakUtilizationLevelAvg = PennantApplicationUtil
						.unFormateAmount(peakUtilizationLevelSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}
			// Settlement No
			BigDecimal settlementNoSum = BigDecimal.ZERO;
			BigDecimal settlementNoAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "settlementNo");
					Intbox settlementNoAmount = (Intbox) hbox.getLastChild();
					Clients.clearWrongValue(settlementNoAmount);
					if (settlementNoAmount.getValue() != null) {
						settlementNoSum = settlementNoSum.add(new BigDecimal(settlementNoAmount.getValue()));
					}
				}
			}

			if (settlementNoSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				settlementNoAvg = PennantApplicationUtil
						.unFormateAmount(settlementNoSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}
			// Settlement Credits
			BigDecimal settlementCreditsSum = BigDecimal.ZERO;
			BigDecimal settlementCreditsAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "settlementCredits");
					CurrencyBox settlementCredits = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(settlementCredits);
					if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
							|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
						settlementCredits.setAllowNagativeValues(true);
					}
					if (settlementCredits.getValidateValue() != null) {
						settlementCreditsSum = settlementCreditsSum.add(settlementCredits.getValidateValue());
					}
				}
			}

			if (settlementCreditsSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				settlementCreditsAvg = PennantApplicationUtil
						.unFormateAmount(settlementCreditsSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			} else if (settlementCreditsSum.compareTo(BigDecimal.ZERO) < 0) {
				BigDecimal divider = BigDecimal.valueOf(dataItems, 0);
				settlementCreditsAvg = PennantApplicationUtil
						.unFormateAmount(settlementCreditsSum, PennantConstants.defaultCCYDecPos)
						.divide(divider, 0, RoundingMode.HALF_DOWN);
			}

			// add summary list item
			BigDecimal odCCSum = BigDecimal.ZERO;
			BigDecimal odCCAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}

					Hbox hbox = (Hbox) getComponent(listItem, "odCCLimit");
					CurrencyBox odccValue = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(odccValue);
					if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
							|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
						odccValue.setAllowNagativeValues(true);
					}
					if (odccValue.getValidateValue() != null) {
						odCCSum = odCCSum.add(odccValue.getValidateValue());
					}
				}
			}

			if (odCCSum.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal odCCSumDivider = BigDecimal.valueOf(dataItems, 0);
				odCCAvg = PennantApplicationUtil.unFormateAmount(odCCSum, PennantConstants.defaultCCYDecPos)
						.divide(odCCSumDivider, 0, RoundingMode.HALF_DOWN);
			} else if (odCCSum.compareTo(BigDecimal.ZERO) < 0) {
				BigDecimal odCCSumDivider = BigDecimal.valueOf(dataItems, 0);
				odCCAvg = PennantApplicationUtil.unFormateAmount(odCCSum, PennantConstants.defaultCCYDecPos)
						.divide(odCCSumDivider, 0, RoundingMode.HALF_DOWN);
			}

			// Interest Sum and Summary
			BigDecimal interestSum = BigDecimal.ZERO;
			BigDecimal interestAvg = BigDecimal.ZERO;

			// TRF Sum and Summary
			BigDecimal trfSum = BigDecimal.ZERO;
			BigDecimal trfAvg = BigDecimal.ZERO;
			BigDecimal totalEmi = BigDecimal.ZERO;
			BigDecimal totalEmiAvg = BigDecimal.ZERO;
			BigDecimal totalSalary = BigDecimal.ZERO;
			BigDecimal totalSalaryAvg = BigDecimal.ZERO;
			int emiBounceNo = 0;
			BigDecimal emiBounceAvg = BigDecimal.ZERO;

			if (this.listBoxAccBehaviour.getItems() != null && !this.listBoxAccBehaviour.getItems().isEmpty()) {
				for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {

					BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

					if (bankInfoDetail == null) {
						continue;
					}
					// getting Interest
					Hbox hbox = (Hbox) getComponent(listItem, "interest");
					CurrencyBox interestValue = (CurrencyBox) hbox.getLastChild();
					Clients.clearWrongValue(interestValue);
					if (interestValue.getValidateValue() != null) {
						interestSum = interestSum.add(interestValue.getValidateValue());
					}
					// getting TRF
					Hbox trf = (Hbox) getComponent(listItem, "trf");
					CurrencyBox trfValue = (CurrencyBox) trf.getLastChild();
					Clients.clearWrongValue(trfValue);
					if (trfValue.getValidateValue() != null) {
						trfSum = trfSum.add(trfValue.getValidateValue());
					}

					// Total EMI
					Hbox emi = (Hbox) getComponent(listItem, "totalEmi");
					CurrencyBox emiComp = (CurrencyBox) emi.getLastChild();
					Clients.clearWrongValue(emiComp);
					if (emiComp.getValidateValue() != null) {
						totalEmi = totalEmi.add(emiComp.getValidateValue());
					}

					// Total Salary
					Hbox salary = (Hbox) getComponent(listItem, "totalSalary");
					CurrencyBox salaryComp = (CurrencyBox) salary.getLastChild();
					Clients.clearWrongValue(salaryComp);
					if (salaryComp.getValidateValue() != null) {
						totalSalary = totalSalary.add(salaryComp.getValidateValue());
					}

					// EMI Bounce No
					Hbox emiBounce = (Hbox) getComponent(listItem, "emiBounce");
					Intbox emiBounceComp = (Intbox) emiBounce.getLastChild();
					Clients.clearWrongValue(emiBounceComp);
					if (emiBounceComp != null) {
						emiBounceNo = emiBounceNo + (emiBounceComp.intValue());
					}

				}
			}

			BigDecimal interestDivider = BigDecimal.valueOf(dataItems, 0);
			if (interestSum.compareTo(BigDecimal.ZERO) > 0) {
				interestAvg = PennantApplicationUtil.unFormateAmount(interestSum, PennantConstants.defaultCCYDecPos)
						.divide(interestDivider, 0, RoundingMode.HALF_DOWN);
			}

			if (trfSum.compareTo(BigDecimal.ZERO) > 0) {
				trfAvg = PennantApplicationUtil.unFormateAmount(trfSum, PennantConstants.defaultCCYDecPos)
						.divide(interestDivider, 0, RoundingMode.HALF_DOWN);
			}

			if (totalEmi.compareTo(BigDecimal.ZERO) > 0) {
				totalEmiAvg = PennantApplicationUtil.unFormateAmount(totalEmi, PennantConstants.defaultCCYDecPos)
						.divide(interestDivider, 0, RoundingMode.HALF_DOWN);
			}
			if (totalSalary.compareTo(BigDecimal.ZERO) > 0) {
				totalSalaryAvg = PennantApplicationUtil.unFormateAmount(totalSalary, PennantConstants.defaultCCYDecPos)
						.divide(interestDivider, 0, RoundingMode.HALF_DOWN);
			}
			if (emiBounceNo > 0) {
				emiBounceAvg = new BigDecimal(emiBounceNo).divide(interestDivider, 2, RoundingMode.HALF_DOWN);
			}

			if (this.listBoxAccBehaviour.getFellowIfAny("item_Sum") != null) {
				Listitem item = (Listitem) this.listBoxAccBehaviour.getFellow("item_Sum");
				listBoxAccBehaviour.removeItemAt(item.getIndex());
			}
			if (this.listBoxAccBehaviour.getFellowIfAny("item_Avg") != null) {
				Listitem item = (Listitem) this.listBoxAccBehaviour.getFellow("item_Avg");
				listBoxAccBehaviour.removeItemAt(item.getIndex());
			}

			Listcell listcell;
			Listitem item;

			// Sum
			item = new Listitem();
			item.setId("item_Sum");
			listcell = new Listcell(Labels.getLabel("label_CustomerBankInfoDialog_Sum.value"));
			listcell.setStyle("font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(balanceSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(dbtTransactionCountSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(debitAmtSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(creditTransactionCountSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(creditAmtSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(bounceInwardSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(bounceOutwardSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(clsBalSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(sanctionLimitSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(avgUtilizationSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(peakUtilizationLevelSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(settlementNoSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(settlementCreditsSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(String.valueOf(odCCSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);
			// Interest
			listcell = new Listcell(String.valueOf(interestSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);
			// TRF
			listcell = new Listcell(String.valueOf(trfSum));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			// Total EMI
			listcell = new Listcell(String.valueOf(totalEmi));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			// Total Salary
			listcell = new Listcell(String.valueOf(totalSalary));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			// EMI Bounces
			listcell = new Listcell(String.valueOf(emiBounceNo));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			this.listBoxAccBehaviour.appendChild(item);

			// Average
			item = new Listitem();
			item.setId("item_Avg");
			listcell = new Listcell(Labels.getLabel("label_CustomerBankInfoDialog_Average.value"));
			listcell.setStyle("font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(
					PennantApplicationUtil.amountFormate(balanceAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(
					PennantApplicationUtil.amountFormate(debitTransactionCountAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(PennantApplicationUtil.amountFormate(debitAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(
					PennantApplicationUtil.amountFormate(creditTransactionCountAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(PennantApplicationUtil.amountFormate(creditAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(PennantApplicationUtil.amountFormate(inwardAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(
					PennantApplicationUtil.amountFormate(outwardAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(PennantApplicationUtil.amountFormate(clsBalAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(
					PennantApplicationUtil.amountFormate(sanctionLimitAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(
					PennantApplicationUtil.amountFormate(avgUtilizationAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(
					PennantApplicationUtil.amountFormate(peakUtilizationLevelAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(
					PennantApplicationUtil.amountFormate(settlementNoAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(
					PennantApplicationUtil.amountFormate(settlementCreditsAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell(PennantApplicationUtil.amountFormate(odCCAvg, PennantConstants.defaultCCYDecPos));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);
			// Interest
			listcell = new Listcell(String
					.valueOf(PennantApplicationUtil.amountFormate(interestAvg, PennantConstants.defaultCCYDecPos)));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);
			// TRF
			listcell = new Listcell(
					String.valueOf(PennantApplicationUtil.amountFormate(trfAvg, PennantConstants.defaultCCYDecPos)));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			// Total EMI
			listcell = new Listcell(String
					.valueOf(PennantApplicationUtil.amountFormate(totalEmiAvg, PennantConstants.defaultCCYDecPos)));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			// Total Salary
			listcell = new Listcell(String
					.valueOf(PennantApplicationUtil.amountFormate(totalSalaryAvg, PennantConstants.defaultCCYDecPos)));
			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			// EMI Bounces
			listcell = new Listcell(String.valueOf(emiBounceAvg));

			listcell.setStyle("text-align:right;font-weight:bold");
			listcell.setParent(item);

			listcell = new Listcell("");
			listcell.setParent(item);

			this.listBoxAccBehaviour.appendChild(item);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void renderItem(BankInfoDetail bankInfoDetail) {
		Listitem listItem = new Listitem();
		Listcell listCell;
		Hbox hbox;
		Space space;
		boolean isReadOnly = isReadOnly("CustomerBankInfoDialog_AccountType");
		// for customer 360 it should be readonly
		if (isCustomer360) {
			isReadOnly = true;
		}

		// Month Year
		listCell = new Listcell();
		Datebox monthYear = new Datebox();
		monthYear.setFormat(PennantConstants.monthYearFormat);
		if (bankInfoDetail.isNewRecord() && StringUtils.isEmpty(bankInfoDetail.getRecordType())) {
			readOnlyComponent(false, monthYear);
		} else {
			readOnlyComponent(true, monthYear);
		}
		monthYear.setReadonly(true); // Intentionally we put as read-only here
		monthYear.setValue(bankInfoDetail.getMonthYear());
		listCell.setId("monthYear".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		monthYear.setWidth("130px");
		hbox.appendChild(space);
		hbox.appendChild(monthYear);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {

			// Bank Balance
			listCell = new Listcell();
			listCell.setId("balance".concat(String.valueOf(bankInfoDetail.getKeyValue())));
			Grid grid = new Grid();
			Columns columns = new Columns();
			Column column1 = new Column();
			Column column2 = new Column();
			column1.setParent(columns);
			column2.setParent(columns);
			Rows rows = new Rows();
			Row row;
			CurrencyBox box;
			Label label;

			int balCount = 0;
			int count = 0;
			for (String day : configDay.split(",")) {

				int j = 0;
				try {
					j = Integer.parseInt(StringUtils.trim(day));
				} catch (NumberFormatException e) {
					continue;
				}

				balCount++;
				row = new Row();
				// Day
				label = new Label();

				if (j == 1) {
					label.setValue(j + "st");
				} else if (j == 2) {
					label.setValue(j + "nd");
				} else if (j == 3) {
					label.setValue(j + "rd");
				} else {
					label.setValue(j + "th");
				}

				hbox = new Hbox();
				space = new Space();
				space.setSpacing("2px");
				space.setSclass("mandatory");
				// Balance
				box = new CurrencyBox();
				if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
						|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
					box.setAllowNagativeValues(true);
				}
				box.setId("balance_currency".concat(String.valueOf(bankInfoDetail.getKeyValue()))
						.concat(String.valueOf(balCount)));
				box.setBalUnvisible(true);
				box.setFormat(PennantApplicationUtil.getAmountFormate(2));
				box.setScale(2);
				if (bankInfoDetail.getBankInfoSubDetails().size() > 0) {
					box.setValue(PennantApplicationUtil.formateAmount(
							bankInfoDetail.getBankInfoSubDetails().get(count).getBalance(),
							PennantConstants.defaultCCYDecPos));
					count++;
				} else {
					box.setValue(BigDecimal.ZERO);
				}
				box.addForward("onFulfill", self, "onChangeConfigDay", box);
				box.setReadonly(isReadOnly);
				space.setParent(hbox);
				box.setParent(hbox);
				label.setParent(row);
				hbox.setParent(row);
				row.setParent(rows);
			}
			rows.setParent(grid);
			grid.setParent(listCell);
			listCell.setParent(listItem);
		} else {
			// Average
			listCell = new Listcell();
			hbox = new Hbox();
			space = new Space();
			space.setSpacing("2px");
			CurrencyBox balanceAmt = new CurrencyBox();
			if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
					|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
				balanceAmt.setAllowNagativeValues(true);
			}
			balanceAmt.setBalUnvisible(true);
			listCell.setId("balance".concat(String.valueOf(bankInfoDetail.getKeyValue())));
			space.setSclass("mandatory");
			balanceAmt.setFormat(PennantApplicationUtil.getAmountFormate(2));
			balanceAmt.setScale(2);
			if (bankInfoDetail.getBankInfoSubDetails().size() > 0) {
				balanceAmt.setValue(PennantApplicationUtil
						.formateAmount(bankInfoDetail.getBankInfoSubDetails().get(0).getBalance(), 2));
			} else {
				balanceAmt.setValue(BigDecimal.ZERO);
			}
			balanceAmt.addForward("onFulfill", self, "onChangeConfigDay", balanceAmt);
			balanceAmt.setReadonly(isReadOnly);
			hbox.appendChild(space);
			hbox.appendChild(balanceAmt);
			listCell.appendChild(hbox);
			listCell.setParent(listItem);
		}

		// Debit No
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Intbox debitNo = new Intbox();
		debitNo.setMaxlength(4);
		listCell.setId("debitNo".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		space.setSclass("mandatory");
		debitNo.setReadonly(isReadOnly);
		debitNo.setValue(bankInfoDetail.getDebitNo());
		hbox.appendChild(space);
		hbox.appendChild(debitNo);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Debit Amount
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox debitAmt = new CurrencyBox();
		debitAmt.setBalUnvisible(true);
		listCell.setId("debitAmt".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		space.setSclass("mandatory");
		debitAmt.setFormat(PennantApplicationUtil.getAmountFormate(2));
		debitAmt.setScale(2);
		debitAmt.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getDebitAmt(), 2));
		debitAmt.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(debitAmt);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Credit No
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Intbox creditNo = new Intbox();
		creditNo.setMaxlength(4);
		listCell.setId("creditNo".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		space.setSclass("mandatory");
		creditNo.setReadonly(isReadOnly);
		creditNo.setValue(bankInfoDetail.getCreditNo());
		hbox.appendChild(space);
		hbox.appendChild(creditNo);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Credit Amount
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox creditAmt = new CurrencyBox();
		creditAmt.setBalUnvisible(true);
		listCell.setId("creditAmt".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		space.setSclass("mandatory");
		creditAmt.setFormat(PennantApplicationUtil.getAmountFormate(2));
		creditAmt.setScale(2);
		creditAmt.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getCreditAmt(), 2));
		creditAmt.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(creditAmt);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Bounce In Ward
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox bounceInWard = new CurrencyBox();
		bounceInWard.setBalUnvisible(true);
		listCell.setId("bounceInWard".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		space.setSclass("mandatory");
		bounceInWard.setFormat(PennantApplicationUtil.getAmountFormate(2));
		bounceInWard.setScale(2);
		bounceInWard.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getBounceIn(), 2));
		bounceInWard.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(bounceInWard);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Bounce Out Ward
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox bounceOutWard = new CurrencyBox();
		bounceOutWard.setBalUnvisible(true);
		listCell.setId("bounceOutWard".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		space.setSclass("mandatory");
		bounceOutWard.setFormat(PennantApplicationUtil.getAmountFormate(2));
		bounceOutWard.setScale(2);
		bounceOutWard.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getBounceOut(), 2));
		bounceOutWard.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(bounceOutWard);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Closing Balance
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox closingBal = new CurrencyBox();
		if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
				|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
			closingBal.setAllowNagativeValues(true);
		}
		closingBal.setBalUnvisible(true);
		listCell.setId("closingBal".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		closingBal.setFormat(PennantApplicationUtil.getAmountFormate(2));
		closingBal.setScale(2);
		closingBal.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getClosingBal(), 2));
		closingBal.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(closingBal);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Sanction Limit
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox sanctionLimit = new CurrencyBox();
		sanctionLimit.setBalUnvisible(true);
		listCell.setId("sanctionLimit".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		sanctionLimit.setFormat(PennantApplicationUtil.getAmountFormate(2));
		sanctionLimit.setScale(2);
		sanctionLimit.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getSanctionLimit(), 2));
		sanctionLimit.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(sanctionLimit);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Average Utilization
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox avgUtilization = new CurrencyBox();
		avgUtilization.setBalUnvisible(true);
		listCell.setId("avgUtilization".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		avgUtilization.setFormat(PennantApplicationUtil.getAmountFormate(2));
		avgUtilization.setScale(2);
		avgUtilization.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getAvgUtilization(), 2));
		avgUtilization.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(avgUtilization);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Average Utilization
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox peakUtilizationLevel = new CurrencyBox();
		if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
				|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
			peakUtilizationLevel.setAllowNagativeValues(true);
		}
		peakUtilizationLevel.setBalUnvisible(true);
		listCell.setId("peakUtilizationLevel".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		peakUtilizationLevel.setFormat(PennantApplicationUtil.getAmountFormate(2));
		peakUtilizationLevel.setScale(2);
		peakUtilizationLevel
				.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getPeakUtilizationLevel(), 2));
		peakUtilizationLevel.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(peakUtilizationLevel);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Settlement No
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Intbox settlementNo = new Intbox();
		settlementNo.setMaxlength(4);
		listCell.setId("settlementNo".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		settlementNo.setReadonly(isReadOnly);
		if (bankInfoDetail.getSettlementNo() == null) {
			settlementNo.setValue(0);
		} else {
			settlementNo.setValue(bankInfoDetail.getSettlementNo());
		}
		hbox.appendChild(space);
		hbox.appendChild(settlementNo);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Settlement Credits
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox settlementCredits = new CurrencyBox();
		if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
				|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
			settlementCredits.setAllowNagativeValues(true);
		}
		settlementCredits.setBalUnvisible(true);
		listCell.setId("settlementCredits".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		settlementCredits.setFormat(PennantApplicationUtil.getAmountFormate(2));
		settlementCredits.setScale(2);
		settlementCredits.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getSettlementCredits(), 2));
		settlementCredits.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(settlementCredits);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// ODCC Limit
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox odCCLimit = new CurrencyBox();
		if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
				|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
			odCCLimit.setAllowNagativeValues(true);
		}
		odCCLimit.setBalUnvisible(true);
		listCell.setId("odCCLimit".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		odCCLimit.setFormat(PennantApplicationUtil.getAmountFormate(2));
		odCCLimit.setScale(2);
		odCCLimit.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getoDCCLimit(), 2));
		odCCLimit.setReadonly(true);
		hbox.appendChild(space);
		hbox.appendChild(odCCLimit);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Interest
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox interest = new CurrencyBox();
		interest.setBalUnvisible(true);
		listCell.setId("interest".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		interest.setFormat(PennantApplicationUtil.getAmountFormate(2));
		interest.setScale(2);
		interest.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getInterest(), 2));
		interest.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(interest);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// TRF
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox trf = new CurrencyBox();
		trf.setBalUnvisible(true);
		listCell.setId("trf".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		trf.setFormat(PennantApplicationUtil.getAmountFormate(2));
		trf.setScale(2);
		trf.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getTrf(), 2));
		trf.setReadonly(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(trf);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Total EMI or Loan
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox totalEmi = new CurrencyBox();
		totalEmi.setBalUnvisible(true);
		listCell.setId("totalEmi".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		totalEmi.setFormat(PennantApplicationUtil.getAmountFormate(2));
		totalEmi.setScale(2);
		totalEmi.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getTrf(), 2));
		totalEmi.setReadonly(isReadOnly);
		totalEmi.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getTotalEmi(), 2));
		hbox.appendChild(space);
		hbox.appendChild(totalEmi);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Total Salary
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox totalSalary = new CurrencyBox();
		totalSalary.setBalUnvisible(true);
		listCell.setId("totalSalary".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		totalSalary.setFormat(PennantApplicationUtil.getAmountFormate(2));
		totalSalary.setScale(2);
		totalSalary.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getTrf(), 2));
		totalSalary.setReadonly(isReadOnly);
		totalSalary.setValue(PennantApplicationUtil.formateAmount(bankInfoDetail.getTotalSalary(), 2));
		hbox.appendChild(space);
		hbox.appendChild(totalSalary);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// EMI Out Bounces
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		listCell.setId("emiBounce".concat(String.valueOf(bankInfoDetail.getKeyValue())));
		Intbox emiBounces = new Intbox();
		emiBounces.setValue(bankInfoDetail.getEmiBounceNo());
		hbox.appendChild(space);
		hbox.appendChild(emiBounces);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Delete action
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel("Delete");
		button.setVisible(!isReadOnly);
		button.addForward("onClick", self, "onClickAccBehaviourButtonDelete", listItem);
		hbox.appendChild(space);
		listCell.appendChild(button);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		listItem.setAttribute("data", bankInfoDetail);
		// for customer 360 it should be disable
		listItem.setDisabled(isCustomer360);
		this.listBoxAccBehaviour.appendChild(listItem);
	}

	public void onChangeConfigDay(Event event) {
		logger.debug(Literal.ENTERING);

		BigDecimal balanceSum = BigDecimal.ZERO;

		if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {

			for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {
				int count = 0;
				balanceSum = BigDecimal.ZERO;
				BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");
				List<Listcell> listcels = listItem.getChildren();
				String[] array = configDay.split(",");

				for (Listcell listcell : listcels) {
					String id = StringUtils.trimToNull(listcell.getId());

					if (id == null) {
						continue;
					}

					id = id.replaceAll("\\d", "");

					if (StringUtils.equals(id, "balance")) {
						for (int i = 1; i <= array.length; i++) {
							CurrencyBox balanceValue = (CurrencyBox) listcell.getFellowIfAny("balance_currency"
									.concat(String.valueOf(bankInfoDetail.getKeyValue())).concat(String.valueOf(i)));
							if (this.accountType.getValue() != null
									&& (StringUtils.equals(this.accountType.getValue(), "CC")
											|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
								balanceValue.setAllowNagativeValues(true);
							}
							Clients.clearWrongValue(balanceValue);
							if (balanceValue.getValidateValue() != null) {
								balanceSum = balanceSum.add(balanceValue.getValidateValue());
								count++;
							}
						}

					}
				}

				Hbox hbox1 = (Hbox) getComponent(listItem, "odCCLimit");
				if (hbox1 != null) {
					CurrencyBox db = (CurrencyBox) hbox1.getLastChild();
					if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
							|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
						db.setAllowNagativeValues(true);
					}
					db.setValue(balanceSum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
				}
			}
		} else {
			for (Listitem listItem : this.listBoxAccBehaviour.getItems()) {
				balanceSum = BigDecimal.ZERO;
				BankInfoDetail bankInfoDetail = (BankInfoDetail) listItem.getAttribute("data");

				if (bankInfoDetail == null) {
					continue;
				}

				Hbox hbox = (Hbox) getComponent(listItem, "balance");
				CurrencyBox avgAmtAmount = (CurrencyBox) hbox.getLastChild();

				if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
						|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
					avgAmtAmount.setAllowNagativeValues(true);
				}
				Clients.clearWrongValue(avgAmtAmount);
				if (avgAmtAmount.getValidateValue() != null) {
					balanceSum = balanceSum.add(avgAmtAmount.getValidateValue());
				}
				Hbox hbox1 = (Hbox) getComponent(listItem, "odCCLimit");
				if (hbox1 != null) {
					CurrencyBox db = (CurrencyBox) hbox1.getLastChild();
					if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
							|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
						db.setAllowNagativeValues(true);
					}
					if (db.getValidateValue() != null) {
						db.setValue(balanceSum);
					}

				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClickAccBehaviourButtonDelete(ForwardEvent event) {
		Listitem item = (Listitem) event.getData();
		listBoxAccBehaviour.removeItemAt(item.getIndex());
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
		MessageUtil.showHelpWindow(event, window_CustomerBankInfoDialog);
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

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		doWriteBeanToComponents(this.customerBankInfo.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param bankInfo CustomerBankInfo
	 */
	public void doWriteBeanToComponents(CustomerBankInfo bankInfo) {
		logger.debug("Entering");

		if (bankInfo.getCustID() != Long.MIN_VALUE) {
			this.custID.setValue(bankInfo.getCustID());
		}

		this.bankName.setValue(bankInfo.getBankName());
		this.bankName.setDescription(StringUtils.trimToEmpty(bankInfo.getLovDescBankName()));
		this.accountNumber.setValue(bankInfo.getAccountNumber());
		this.accountType.setValue(bankInfo.getAccountType());
		this.accountType.setDescription(StringUtils.trimToEmpty(bankInfo.getLovDescAccountType()));
		this.custCIF.setValue(StringUtils.trimToEmpty(bankInfo.getLovDescCustCIF()));
		this.custShrtName.setValue(StringUtils.trimToEmpty(bankInfo.getLovDescCustShrtName()));
		this.salaryAccount.setChecked(bankInfo.isSalaryAccount());
		if (CustomerBankInfoList != null) {
			for (CustomerBankInfo customerBankInfo : CustomerBankInfoList) {
				if (customerBankInfo.isSalaryAccount()) {
					this.salaryAccount.setDisabled(true);
				}
			}
		}
		if (bankInfo.isSalaryAccount()) {
			this.salaryAccount.setDisabled(false);
		}

		this.creditTranNo.setValue(bankInfo.getCreditTranNo());
		this.creditTranAmt.setValue(PennantApplicationUtil.formateAmount(bankInfo.getCreditTranAmt(), finFormatter));
		this.creditTranAvg.setValue(PennantApplicationUtil.formateAmount(bankInfo.getCreditTranAvg(), finFormatter));
		this.debitTranNo.setValue(bankInfo.getDebitTranNo());
		this.debitTranAmt.setValue(PennantApplicationUtil.formateAmount(bankInfo.getDebitTranAmt(), finFormatter));
		this.cashDepositNo.setValue(bankInfo.getCashDepositNo());
		this.cashDepositAmt.setValue(PennantApplicationUtil.formateAmount(bankInfo.getCashDepositAmt(), finFormatter));
		this.cashWithdrawalNo.setValue(bankInfo.getCashWithdrawalNo());
		this.cashWithdrawalAmt
				.setValue(PennantApplicationUtil.formateAmount(bankInfo.getCashWithdrawalAmt(), finFormatter));
		this.chqDepositNo.setValue(bankInfo.getChqDepositNo());
		this.chqDepositAmt.setValue(PennantApplicationUtil.formateAmount(bankInfo.getChqDepositAmt(), finFormatter));
		this.chqIssueNo.setValue(bankInfo.getChqIssueNo());
		this.chqIssueAmt.setValue(PennantApplicationUtil.formateAmount(bankInfo.getChqIssueAmt(), finFormatter));
		this.inwardChqBounceNo.setValue(bankInfo.getInwardChqBounceNo());
		this.outwardChqBounceNo.setValue(bankInfo.getOutwardChqBounceNo());
		this.eodBalMin.setValue(PennantApplicationUtil.formateAmount(bankInfo.getEodBalMin(), finFormatter));
		this.eodBalMax.setValue(PennantApplicationUtil.formateAmount(bankInfo.getEodBalMax(), finFormatter));
		this.eodBalAvg.setValue(PennantApplicationUtil.formateAmount(bankInfo.getEodBalAvg(), finFormatter));
		this.bankBranch.setValue(bankInfo.getBankBranch());
		this.accountHolderName.setValue(bankInfo.getAccountHolderName());
		this.phoneNumber.setValue(bankInfo.getPhoneNumber());
		this.fromDate.setValue(bankInfo.getFromDate());
		this.accountOpeningDate.setValue(bankInfo.getAccountOpeningDate());
		this.toDate.setValue(bankInfo.getToDate());
		fillComboBox(this.repaymentFrom, bankInfo.getRepaymentFrom(), PennantStaticListUtil.getYesNo(), "");
		this.NoOfMonthsBanking.setValue(bankInfo.getNoOfMonthsBanking());
		this.lwowRatio.setValue(bankInfo.getLwowRatio());
		this.ccLimit.setValue(PennantApplicationUtil.formateAmount(bankInfo.getCcLimit(), finFormatter));
		if (StringUtils.equals(MasterDefUtil.getAccountTypeCode(AccountType.OD), bankInfo.getAccountType())
				|| StringUtils.equals(MasterDefUtil.getAccountTypeCode(AccountType.CC), bankInfo.getAccountType())) {
			this.ccLimit.setMandatory(true);
		}
		this.typeOfBanks.setValue(bankInfo.getTypeOfBanks());

		this.recordStatus.setValue(bankInfo.getRecordStatus());
		this.addToBenficiary.setChecked(bankInfo.isAddToBenficiary());

		if (bankInfo.getBankBranchID() != null && bankInfo.getBankBranchID() != Long.MIN_VALUE
				&& bankInfo.getBankBranchID() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", bankInfo.getBankBranchID());
			this.bankBranchID.setValue(StringUtils.trimToEmpty(bankInfo.getiFSC()));
		}

		if (bankInfo.getBankInfoDetails().size() > 0) {
			List<BankInfoDetail> bankInfoDetails = bankInfo.getBankInfoDetails();
			for (int i = 0; i < bankInfoDetails.size(); i++) {
				bankInfoDetails.get(i).setKeyValue(i + 1);
			}

			setBankInfoDetails(bankInfoDetails);
			doFillBankInfoDetails();
		}

		if (CollectionUtils.isNotEmpty(bankInfo.getExternalDocuments())) {
			doFillExternalDocuments(bankInfo.getExternalDocuments());
		}

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

		try {
			aCustomerBankInfo.setLovDescBankName(this.bankName.getDescription());
			aCustomerBankInfo.setBankName(this.bankName.getValidatedValue());// FIXME: change it to name and increase
																				// the size in db
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomerBankInfo
					.setAccountNumber(PennantApplicationUtil.unFormatAccountNumber(this.accountNumber.getValue()));
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
			aCustomerBankInfo.setCreditTranAmt(
					PennantApplicationUtil.unFormateAmount(this.creditTranAmt.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCreditTranAvg(
					PennantApplicationUtil.unFormateAmount(this.creditTranAvg.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setDebitTranNo(this.debitTranNo.intValue());
		} catch (WrongValueException we) {

		}
		try {
			aCustomerBankInfo.setDebitTranAmt(
					PennantApplicationUtil.unFormateAmount(this.debitTranAmt.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCashDepositNo(this.cashDepositNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCashDepositAmt(
					PennantApplicationUtil.unFormateAmount(this.cashDepositAmt.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCashWithdrawalNo(this.cashWithdrawalNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCashWithdrawalAmt(
					PennantApplicationUtil.unFormateAmount(this.cashWithdrawalAmt.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setChqDepositNo(this.chqDepositNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setChqDepositAmt(
					PennantApplicationUtil.unFormateAmount(this.chqDepositAmt.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setChqIssueNo(this.chqIssueNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setChqIssueAmt(
					PennantApplicationUtil.unFormateAmount(this.chqIssueAmt.getValidateValue(), finFormatter));
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
			aCustomerBankInfo.setEodBalMin(
					PennantApplicationUtil.unFormateAmount(this.eodBalMin.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setEodBalMax(
					PennantApplicationUtil.unFormateAmount(this.eodBalMax.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setEodBalAvg(
					PennantApplicationUtil.unFormateAmount(this.eodBalAvg.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setBankBranch((this.bankBranch.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setAccountHolderName((this.accountHolderName.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setPhoneNumber(this.phoneNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setFromDate((this.fromDate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			if (DateUtil.compare(this.accountOpeningDate.getValue(), SysParamUtil.getAppDate()) > 0) {
				throw new WrongValueException(this.accountOpeningDate, Labels.getLabel("const_NO_FUTURE",
						new String[] { Labels.getLabel("label_CustomerBankInfoDialog_AccountOpeningDate.value") }));
			}

			aCustomerBankInfo.setAccountOpeningDate((this.accountOpeningDate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setToDate((this.toDate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setRepaymentFrom(getComboboxValue(this.repaymentFrom));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setNoOfMonthsBanking((this.NoOfMonthsBanking.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setLwowRatio((this.lwowRatio.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo
					.setCcLimit(PennantApplicationUtil.unFormateAmount(this.ccLimit.getActualValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setTypeOfBanks((this.typeOfBanks.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setAddToBenficiary(this.addToBenficiary.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Bank Branch ID
		try {
			this.bankBranchID.getValidatedValue();
			Object obj = this.bankBranchID.getAttribute("bankBranchID");

			aCustomerBankInfo.setiFSC(this.bankBranchID.getValue());
			if (obj != null) {
				aCustomerBankInfo.setBankBranchID(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aCustomerBankInfo.setExternalDocuments(getExternalDocumentsList());
		doRemoveValidation();
		doRemoveLOVValidation();

		boolean focus = false;
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				Component component = wve.get(i).getComponent();
				if (!focus) {
					focus = setComponentFocus(component);
				}
			}
			throw new WrongValuesException(wvea);
		}

		aCustomerBankInfo.setRecordStatus(this.recordStatus.getValue());
		setCustomerBankInfo(aCustomerBankInfo);
		logger.debug("Leaving");
	}

	public boolean saveBankInfoList(CustomerBankInfo customerBankInfo) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Map<Date, BankInfoDetail> hashMap = new HashMap<>();

		List<BankInfoDetail> infoList = customerBankInfo.getBankInfoDetails();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		Set<Date> dateValidatioinSet = new HashSet<>();

		for (Listitem listitem : listBoxAccBehaviour.getItems()) {

			BankInfoDetail bankInfoDetail = (BankInfoDetail) listitem.getAttribute("data");

			if (bankInfoDetail != null) {
				try {
					getCompValuetoBean(listitem, "monthYear");

					if (bankInfoDetail != null) {
						Hbox hbox1 = (Hbox) getComponent(listitem, "monthYear");
						Datebox monthYear = (Datebox) hbox1.getLastChild();

						if (dateValidatioinSet.contains(bankInfoDetail.getMonthYear())) {
							if (!PennantConstants.RECORD_TYPE_DEL.equals(bankInfoDetail.getRecordType())
									&& !PennantConstants.RECORD_TYPE_CAN.equals(bankInfoDetail.getRecordType())) {
								throw new WrongValueException(monthYear,
										Labels.getLabel("listheader_MonthYear.label") + " combination already exist.");
							}
						} else {
							dateValidatioinSet.add(bankInfoDetail.getMonthYear());
						}
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					getCompValuetoBean(listitem, "balance");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					getCompValuetoBean(listitem, "debitNo");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					getCompValuetoBean(listitem, "debitAmt");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					getCompValuetoBean(listitem, "creditNo");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					getCompValuetoBean(listitem, "creditAmt");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					getCompValuetoBean(listitem, "bounceInWard");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					getCompValuetoBean(listitem, "bounceOutWard");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					getCompValuetoBean(listitem, "closingBal");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					getCompValuetoBean(listitem, "sanctionLimit");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					getCompValuetoBean(listitem, "avgutilization");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					getCompValuetoBean(listitem, "peakUtilizationLevel");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					getCompValuetoBean(listitem, "settlementNo");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					getCompValuetoBean(listitem, "settlementCredits");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					getCompValuetoBean(listitem, "odCCLimit");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				// Interest
				try {
					getCompValuetoBean(listitem, "interest");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				// TRF
				try {
					getCompValuetoBean(listitem, "trf");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				// totalEmi
				try {
					getCompValuetoBean(listitem, "totalEmi");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				// totalSalary
				try {
					getCompValuetoBean(listitem, "totalSalary");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				// emiBounce
				try {
					getCompValuetoBean(listitem, "emiBounce");
				} catch (WrongValueException we) {
					wve.add(we);
				}

				showErrorDetails(wve);

				boolean isNew = false;
				isNew = bankInfoDetail.isNewRecord();
				String tranType = "";

				if (bankInfoDetail.isNewRecord()) {
					bankInfoDetail.setVersion(1);
					bankInfoDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(bankInfoDetail.getRecordType())) {
					bankInfoDetail.setVersion(bankInfoDetail.getVersion() + 1);
					if (CollectionUtils.isNotEmpty(bankInfoDetail.getBankInfoSubDetails())) {
						for (BankInfoSubDetail bankInfoSubDetail : bankInfoDetail.getBankInfoSubDetails()) {
							if (isFinanceProcess) {
								bankInfoSubDetail.setVersion(bankInfoSubDetail.getVersion() + 1);
							}
						}
					}
					bankInfoDetail.setRecordType(PennantConstants.RCD_UPD);
					if (!isFinanceProcess && getCustomerDialogCtrl() != null
							&& getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()) {
						bankInfoDetail.setNewRecord(true);
					}
				}

				if (bankInfoDetail.getRecordType().equals(PennantConstants.RCD_ADD) && bankInfoDetail.isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (bankInfoDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				} else {
					if (isNew) {
						tranType = PennantConstants.TRAN_ADD;
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}
				}
				try {
					AuditHeader auditHeader = newBankInfoDetailProcess(bankInfoDetail, infoList, tranType);
					if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
						auditHeader = ErrorControl.showErrorDetails(this.window_CustomerBankInfoDialog, auditHeader);
						setBankInfoDetails(customerBankInfo.getBankInfoDetails());
						return false;
					}
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						infoList = bankInfoDetails;
					}
				} catch (final DataAccessException e) {
					logger.error(Literal.EXCEPTION, e);
					showMessage(e);
				}
				hashMap.put(bankInfoDetail.getMonthYear(), bankInfoDetail);
			}
		}

		//
		for (BankInfoDetail detail : bankInfoDetails) {
			if (!hashMap.containsKey(detail.getMonthYear())) {
				if (StringUtils.isBlank(detail.getRecordType())) {
					detail.setNewRecord(true);
					detail.setVersion(detail.getVersion() + 1);
					detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else {
					if (!StringUtils.equals(detail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
						detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					}
				}
			}
		}

		setBankInfoDetails(bankInfoDetails);
		logger.debug(Literal.LEAVING);
		return true;
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);

		boolean focus = false;
		if (wve.size() > 0) {
			setBankInfoDetails(customerBankInfo.getBankInfoDetails());
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (!focus) {
						focus = setComponentFocus(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	private AuditHeader newBankInfoDetailProcess(BankInfoDetail detail, List<BankInfoDetail> infoList,
			String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = new AuditHeader();
		String[] valueParm = new String[1];
		String[] errParm = new String[1];
		valueParm[0] = String.valueOf(DateUtil.format(detail.getMonthYear(), PennantConstants.monthYearFormat));
		errParm[0] = "Monthyear" + ":" + valueParm[0];

		bankInfoDetails = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(infoList)) {
			for (int i = 0; i < infoList.size(); i++) {
				if (DateUtil.compare(detail.getMonthYear(), infoList.get(i).getMonthYear()) == 0) {
					if (detail.isNewRecord() && StringUtils.isEmpty(detail.getRecordType())) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (detail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.bankInfoDetails.add(detail);
						} else if (detail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (detail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.bankInfoDetails.add(detail);
						} else if (detail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < customerBankInfo.getBankInfoDetails().size(); j++) {
								BankInfoDetail infoDetail = customerBankInfo.getBankInfoDetails().get(j);
								if (infoDetail.getMonthYear().equals(detail.getMonthYear())) {
									this.bankInfoDetails.add(infoDetail);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.bankInfoDetails.add(infoList.get(i));
							if (detail.getRecordType().equals(PennantConstants.RCD_ADD)) {
								recordAdded = true;
							}
						}
					}
				} else {
					this.bankInfoDetails.add(infoList.get(i));
				}
			}
		}

		if (!recordAdded) {
			this.bankInfoDetails.add(detail);
		}
		return auditHeader;
	}

	@SuppressWarnings({ "deprecation" })
	private void getCompValuetoBean(Listitem listitem, String comonentId) {
		BankInfoDetail bankInfoDetail = null;

		bankInfoDetail = (BankInfoDetail) listitem.getAttribute("data");

		switch (comonentId) {
		case "monthYear":
			Hbox hbox1 = (Hbox) getComponent(listitem, "monthYear");
			if (hbox1 != null) {
				Datebox monthYear = (Datebox) hbox1.getLastChild();
				Clients.clearWrongValue(monthYear);
				Date monthYearValue = monthYear.getValue();
				if (!monthYear.isDisabled()) {
					if (monthYearValue == null) {
						throw new WrongValueException(monthYear,
								Labels.getLabel("FIELD_IS_MAND", new String[] { "Month Year" }));
					} else {
						monthYearValue.setDate(1);
						if (DateUtil.compare(monthYearValue, SysParamUtil.getAppDate()) == 1) {
							throw new WrongValueException(monthYear,
									Labels.getLabel("DATE_NO_FUTURE", new String[] { "Month Year" }));
						}
					}
				}
				monthYearValue.setDate(1);
				bankInfoDetail.setMonthYear(monthYearValue);
			}
			break;
		case "balance":
			if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {
				bankInfoDetail = getDayBalanceList(listitem, "balance", bankInfoDetail);
				break;
			} else {
				Hbox hbox2 = (Hbox) getComponent(listitem, "balance");
				if (hbox2 != null) {
					CurrencyBox balanceValue = (CurrencyBox) hbox2.getLastChild();
					if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
							|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
						balanceValue.setAllowNagativeValues(true);
					}
					Clients.clearWrongValue(balanceValue);
					bankInfoDetail = getDayBalanceList(listitem, "balance", bankInfoDetail);
				}
				break;
			}
		case "debitNo":
			Hbox hbox3 = (Hbox) getComponent(listitem, "debitNo");

			if (hbox3 != null) {
				Intbox debitNo = (Intbox) hbox3.getLastChild();
				Clients.clearWrongValue(debitNo);
				if (!debitNo.isReadonly() && debitNo.getValue() == null) {
					throw new WrongValueException(debitNo,
							Labels.getLabel("FIELD_IS_MAND", new String[] { "Debit No" }));
				} else if (!debitNo.isReadonly() && debitNo.intValue() < 0) {
					throw new WrongValueException(debitNo,
							Labels.getLabel("FIELD_NO_EMPTY_NO_NEG_NO_ZERO", new String[] { "Debit No" }));
				}
				bankInfoDetail.setDebitNo(debitNo.intValue());
			}
			break;

		case "debitAmt":
			BigDecimal debitAmt = BigDecimal.ZERO;
			Hbox hbox4 = (Hbox) getComponent(listitem, "debitAmt");
			if (hbox4 != null) {
				CurrencyBox debitAmtValue = (CurrencyBox) hbox4.getLastChild();
				Clients.clearWrongValue(debitAmtValue);
				if (debitAmtValue.getValidateValue() != null) {
					debitAmt = debitAmtValue.getValidateValue();
				}
				if (!(debitAmtValue.isReadonly()) && (debitAmt.intValue() < 0)) {
					throw new WrongValueException(debitAmtValue,
							Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Debit Amount" }));
				}
				bankInfoDetail.setDebitAmt(PennantApplicationUtil.unFormateAmount(debitAmt, 2));
			}
			break;

		case "creditNo":
			Hbox hbox5 = (Hbox) getComponent(listitem, "creditNo");
			if (hbox5 != null) {
				Intbox creditNo = (Intbox) hbox5.getLastChild();
				Clients.clearWrongValue(creditNo);
				if (!creditNo.isReadonly() && creditNo.getValue() == null) {
					throw new WrongValueException(creditNo,
							Labels.getLabel("FIELD_IS_MAND", new String[] { "Credit No" }));
				} else if (!creditNo.isReadonly() && creditNo.intValue() < 0) {
					throw new WrongValueException(creditNo,
							Labels.getLabel("FIELD_NO_EMPTY_NO_NEG_NO_ZERO", new String[] { "Credit No" }));
				}
				bankInfoDetail.setCreditNo(creditNo.intValue());
			}
			break;

		case "creditAmt":
			BigDecimal creditAmt = BigDecimal.ZERO;
			Hbox hbox6 = (Hbox) getComponent(listitem, "creditAmt");
			if (hbox6 != null) {
				CurrencyBox creditAmtValue = (CurrencyBox) hbox6.getLastChild();
				Clients.clearWrongValue(creditAmtValue);
				if (creditAmtValue.getValidateValue() != null) {
					creditAmt = creditAmtValue.getValidateValue();
				}
				if (!(creditAmtValue.isReadonly()) && (creditAmt.intValue() < 0)) {
					throw new WrongValueException(creditAmtValue,
							Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Credit Amount" }));
				}
				bankInfoDetail.setCreditAmt(PennantApplicationUtil.unFormateAmount(creditAmt, 2));
			}
			break;

		case "bounceInWard":
			BigDecimal bounceInWard = BigDecimal.ZERO;
			Hbox hbox7 = (Hbox) getComponent(listitem, "bounceInWard");
			if (hbox7 != null) {
				CurrencyBox bounceIn = (CurrencyBox) hbox7.getLastChild();
				Clients.clearWrongValue(bounceIn);
				if (bounceIn.getValidateValue() != null) {
					bounceInWard = bounceIn.getValidateValue();
				}
				if (!(bounceIn.isReadonly()) && (bounceInWard.intValue() < 0)) {
					throw new WrongValueException(bounceIn,
							Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Bounce In Ward" }));
				}
				bankInfoDetail.setBounceIn(PennantApplicationUtil.unFormateAmount(bounceInWard, 2));
			}
			break;

		case "bounceOutWard":
			BigDecimal bounceInOut = BigDecimal.ZERO;
			Hbox hbox8 = (Hbox) getComponent(listitem, "bounceOutWard");
			if (hbox8 != null) {
				CurrencyBox bounceOut = (CurrencyBox) hbox8.getLastChild();
				Clients.clearWrongValue(bounceOut);
				if (bounceOut.getValidateValue() != null) {
					bounceInOut = bounceOut.getValidateValue();
				}
				if (!(bounceOut.isReadonly()) && (bounceInOut.intValue() < 0)) {
					throw new WrongValueException(bounceOut,
							Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Bounce Out Ward" }));
				}
				bankInfoDetail.setBounceOut(PennantApplicationUtil.unFormateAmount(bounceInOut, 2));
			}
			break;

		case "closingBal":
			BigDecimal closingBal = BigDecimal.ZERO;
			Hbox hbox9 = (Hbox) getComponent(listitem, "closingBal");
			if (hbox9 != null) {
				CurrencyBox closingBalValue = (CurrencyBox) hbox9.getLastChild();
				Clients.clearWrongValue(closingBalValue);
				if (closingBalValue.getValidateValue() != null) {
					closingBal = closingBalValue.getValidateValue();
				}
				bankInfoDetail.setClosingBal(PennantApplicationUtil.unFormateAmount(closingBal, 2));
			}
			break;
		case "sanctionLimit":
			BigDecimal sanctionLimit = BigDecimal.ZERO;
			Hbox hbox10 = (Hbox) getComponent(listitem, "sanctionLimit");
			if (hbox10 != null) {
				CurrencyBox sanctionLimitValue = (CurrencyBox) hbox10.getLastChild();
				Clients.clearWrongValue(sanctionLimitValue);
				if (sanctionLimitValue.getValidateValue() != null) {
					sanctionLimit = sanctionLimitValue.getValidateValue();
				}
				bankInfoDetail.setSanctionLimit(PennantApplicationUtil.unFormateAmount(sanctionLimit, 2));
			}
			break;
		case "avgutilization":
			BigDecimal avgUtilization = BigDecimal.ZERO;
			Hbox hbox11 = (Hbox) getComponent(listitem, "avgUtilization");
			if (hbox11 != null) {
				CurrencyBox avgUtilizationValue = (CurrencyBox) hbox11.getLastChild();
				Clients.clearWrongValue(avgUtilizationValue);
				if (avgUtilizationValue.getValidateValue() != null) {
					avgUtilization = avgUtilizationValue.getValidateValue();
				}
				bankInfoDetail.setAvgUtilization(PennantApplicationUtil.unFormateAmount(avgUtilization, 2));
			}
			break;

		case "peakUtilizationLevel":
			BigDecimal peakUtilizationLevel = BigDecimal.ZERO;
			Hbox hbox12 = (Hbox) getComponent(listitem, "peakUtilizationLevel");
			if (hbox12 != null) {
				CurrencyBox peakUtilizationLevelValue = (CurrencyBox) hbox12.getLastChild();
				Clients.clearWrongValue(peakUtilizationLevelValue);
				if (peakUtilizationLevelValue.getValidateValue() != null) {
					peakUtilizationLevel = peakUtilizationLevelValue.getValidateValue();
				}
				bankInfoDetail.setPeakUtilizationLevel(PennantApplicationUtil.unFormateAmount(peakUtilizationLevel, 2));
			}
			break;
		case "settlementNo":
			Hbox hbox13 = (Hbox) getComponent(listitem, "settlementNo");

			if (hbox13 != null) {
				Intbox settlementNo = (Intbox) hbox13.getLastChild();
				Clients.clearWrongValue(settlementNo);
				if (settlementNo.getValue() == null) {
					settlementNo.setValue(0);
					if (!settlementNo.isReadonly() && settlementNo.getValue() < 0) {
						throw new WrongValueException(settlementNo,
								Labels.getLabel("FIELD_NO_EMPTY_NO_NEG_NO_ZERO", new String[] { "Settlement No" }));
					}
					bankInfoDetail.setSettlementNo(settlementNo.getValue());
				}
			}
			break;
		case "settlementCredits":
			BigDecimal settlementCredits = BigDecimal.ZERO;
			Hbox hbox14 = (Hbox) getComponent(listitem, "settlementCredits");
			if (hbox14 != null) {
				CurrencyBox settlementCreditsValue = (CurrencyBox) hbox14.getLastChild();
				Clients.clearWrongValue(settlementCreditsValue);
				if (settlementCreditsValue.getValidateValue() != null) {
					settlementCredits = settlementCreditsValue.getValidateValue();
				}
				bankInfoDetail.setSettlementCredits(PennantApplicationUtil.unFormateAmount(settlementCredits, 2));
			}
			break;

		case "odCCLimit":
			BigDecimal odCCLimit = BigDecimal.ZERO;
			Hbox hbox15 = (Hbox) getComponent(listitem, "odCCLimit");
			if (hbox15 != null) {
				CurrencyBox odCCLimitValue = (CurrencyBox) hbox15.getLastChild();
				Clients.clearWrongValue(odCCLimitValue);
				if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
						|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
					odCCLimitValue.setAllowNagativeValues(true);
				}
				if (odCCLimitValue.getValidateValue() != null) {
					odCCLimit = odCCLimitValue.getValidateValue();
				}
				bankInfoDetail.setoDCCLimit(PennantApplicationUtil.unFormateAmount(odCCLimit, 2));
			}
			break;
		case "interest":
			BigDecimal interest = BigDecimal.ZERO;
			Hbox hbox16 = (Hbox) getComponent(listitem, "interest");
			if (hbox16 != null) {
				CurrencyBox interestValue = (CurrencyBox) hbox16.getLastChild();
				Clients.clearWrongValue(interestValue);
				if (interestValue.getValidateValue() != null) {
					interest = interestValue.getValidateValue();
				}
				bankInfoDetail.setInterest(PennantApplicationUtil.unFormateAmount(interest, 2));
			}
			break;
		case "trf":
			BigDecimal trf = BigDecimal.ZERO;
			Hbox hbox17 = (Hbox) getComponent(listitem, "trf");
			if (hbox17 != null) {
				CurrencyBox trfValue = (CurrencyBox) hbox17.getLastChild();
				Clients.clearWrongValue(trfValue);
				if (trfValue.getValidateValue() != null) {
					trf = trfValue.getValidateValue();
				}
				bankInfoDetail.setTrf(PennantApplicationUtil.unFormateAmount(trf, 2));
			}
			break;
		case "totalEmi":
			BigDecimal totalEmi = BigDecimal.ZERO;
			Hbox hbox18 = (Hbox) getComponent(listitem, "totalEmi");
			if (hbox18 != null) {
				CurrencyBox totalEmiValue = (CurrencyBox) hbox18.getLastChild();
				Clients.clearWrongValue(totalEmiValue);
				if (totalEmiValue.getValidateValue() != null) {
					totalEmi = totalEmiValue.getValidateValue();
				}
				bankInfoDetail.setTotalEmi(PennantApplicationUtil.unFormateAmount(totalEmi, 2));
			}
			break;
		case "totalSalary":
			BigDecimal totalSalary = BigDecimal.ZERO;
			Hbox hbox19 = (Hbox) getComponent(listitem, "totalSalary");
			if (hbox19 != null) {
				CurrencyBox totalSalaryValue = (CurrencyBox) hbox19.getLastChild();
				Clients.clearWrongValue(totalSalaryValue);
				if (totalSalaryValue.getValidateValue() != null) {
					totalSalary = totalSalaryValue.getValidateValue();
				}
				bankInfoDetail.setTotalSalary(PennantApplicationUtil.unFormateAmount(totalSalary, 2));
			}
			break;
		case "emiBounce":
			Hbox hbox20 = (Hbox) getComponent(listitem, "emiBounce");
			if (hbox20 != null) {
				Intbox emiBounceValue = (Intbox) hbox20.getLastChild();
				Clients.clearWrongValue(emiBounceValue);
				bankInfoDetail.setEmiBounceNo(emiBounceValue.intValue());
			}
			break;

		default:
			break;
		}
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

	private BankInfoDetail getDayBalanceList(Listitem listitem, String listcellId, BankInfoDetail bankInfoDetail) {

		if (bankInfoDetail != null) {
			List<Listcell> listcels = listitem.getChildren();
			BigDecimal balance = BigDecimal.ZERO;
			List<BankInfoSubDetail> list = bankInfoDetail.getBankInfoSubDetails();
			if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {
				for (Listcell listcell : listcels) {
					String id = StringUtils.trimToNull(listcell.getId());

					if (id == null) {
						continue;
					}

					id = id.replaceAll("\\d", "");
					if (StringUtils.equals(id, listcellId)) {

						int i = 0;
						for (String day : configDay.split(",")) {
							/*
							 * try { Integer.parseInt(StringUtils.trim(day)); } catch (NumberFormatException e) {
							 * logger.error(Literal.EXCEPTION, e); continue; }
							 */
							// Label day = (Label) listcell.getFellowIfAny("day"+i);
							i++;
							CurrencyBox balanceValue = (CurrencyBox) listcell.getFellowIfAny("balance_currency"
									.concat(String.valueOf(bankInfoDetail.getKeyValue())).concat(String.valueOf(i)));
							if (this.accountType.getValue() != null
									&& (StringUtils.equals(this.accountType.getValue(), "CC")
											|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
								balanceValue.setAllowNagativeValues(true);
							}
							Clients.clearWrongValue(balanceValue);
							if (this.accountType.getValue() != null
									&& (StringUtils.equals(this.accountType.getValue(), "CC")
											|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
								balanceValue.setAllowNagativeValues(true);
							}
							if (balanceValue.getValidateValue() != null) {
								balance = balanceValue.getValidateValue();
							}
							if (!(balanceValue.isReadonly()) && (balance.intValue() < 0)
									&& !balanceValue.isAllowNagativeValues()) {
								throw new WrongValueException(balanceValue,
										Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Balance" }));
							}

							BankInfoSubDetail subDetail = null;
							if (CollectionUtils.isNotEmpty(list)) {
								for (BankInfoSubDetail subDtl : list) {
									if (subDtl.getDay() == NumberUtils.toInt(day)) {
										subDtl.setBalance(PennantApplicationUtil.unFormateAmount(balance,
												PennantConstants.defaultCCYDecPos));
										subDetail = subDtl;
									}
								}
							}

							if (subDetail == null) {
								subDetail = new BankInfoSubDetail();
								subDetail.setMonthYear(bankInfoDetail.getMonthYear());
								subDetail.setDay(NumberUtils.toInt(day));
								subDetail.setBalance(PennantApplicationUtil.unFormateAmount(balance,
										PennantConstants.defaultCCYDecPos));
								list.add(subDetail);
							}

						}
					}
				}
			} else {
				list = new ArrayList<BankInfoSubDetail>();
				Hbox hbox2 = (Hbox) getComponent(listitem, "balance");
				if (hbox2 != null) {
					CurrencyBox balanceValue = (CurrencyBox) hbox2.getLastChild();
					if (this.accountType.getValue() != null && (StringUtils.equals(this.accountType.getValue(), "CC")
							|| StringUtils.equals(this.accountType.getValue(), "OD"))) {
						balanceValue.setAllowNagativeValues(true);
					}
					Clients.clearWrongValue(balanceValue);
					if (balanceValue.getValidateValue() != null) {
						balance = balanceValue.getValidateValue();
					}

					if (!(balanceValue.isReadonly()) && (balance.intValue() < 0)
							&& !balanceValue.isAllowNagativeValues()) {
						throw new WrongValueException(balanceValue,
								Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Balance" }));
					}
					BankInfoSubDetail subDetail = new BankInfoSubDetail();

					subDetail.setBalance(
							PennantApplicationUtil.unFormateAmount(balance, PennantConstants.defaultCCYDecPos));
					subDetail.setMonthYear(bankInfoDetail.getMonthYear()); // PSD #153044
					subDetail.setBalance(balance);

					list.add(subDetail);

				}
			}
			bankInfoDetail.setBankInfoSubDetails(list);
		}

		return bankInfoDetail;
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

		if (dataObject instanceof String || dataObject == null) {
			this.bankName.setValue("", "");
			this.bankBranch.setValue("");
			this.bankBranchID.setValue("", "");
			this.accountNumber.setValue("");
		} else {
			BankDetail details = (BankDetail) dataObject;
			this.bankName.setValue(details.getBankCode(), details.getBankName());
			if (StringUtils.isNotBlank(details.getBankCode())) {
				maxAccNoLength = details.getAccNoLength();
				minAccNoLength = details.getMinAccNoLength();
				this.accountNumber.setMaxlength(maxAccNoLength);
			} else {
				this.accountNumber.setMaxlength(LengthConstants.LEN_ACCOUNT);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerBankInfo
	 */
	public void doShowDialog(CustomerBankInfo aCustomerBankInfo) {
		logger.debug("Entering");

		if (isCustomer360) {
			doReadOnly();
			setMonthlyIncomeListBoxProperties();
		} else if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custID.focus();
		} else {
			this.bankName.focus();
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
			doWriteBeanToComponents(aCustomerBankInfo);

			doCheckEnquiry();
			if (this.accountNumber.isReadonly()) {
				this.accountNumber.setTooltiptext(this.accountNumber.getValue());
			}
			if (isNewCustomer()) {
				this.window_CustomerBankInfoDialog.setHeight("90%");
				this.window_CustomerBankInfoDialog.setWidth("90%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerBankInfoDialog.doModal();
			} else {
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
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.custID.setReadonly(true);
			this.custCIF.setReadonly(true);
			this.bankName.setReadonly(true);
			this.accountType.setReadonly(true);
			this.accountNumber.setReadonly(true);
			this.salaryAccount.setDisabled(true);
			this.fromDate.setDisabled(true);
			this.toDate.setDisabled(true);
			this.typeOfBanks.setDisabled(true);
			this.button_CustomerBankInfoDialog_btnAccBehaviour.setDisabled(true);

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
			this.bankBranch.setReadonly(true);
			this.accountHolderName.setReadonly(true);
			this.phoneNumber.setReadonly(true);
			this.fromDate.setReadonly(true);
			this.accountOpeningDate.setReadonly(true);
			this.toDate.setReadonly(true);
			this.repaymentFrom.setReadonly(true);
			this.repaymentFrom.setDisabled(true);
			this.NoOfMonthsBanking.setReadonly(true);
			this.lwowRatio.setReadonly(true);
			this.ccLimit.setReadonly(true);
			this.typeOfBanks.setReadonly(true);

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
			this.accountNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerBankInfoDialog_AccountNumber.value"),
							PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true, minAccNoLength, maxAccNoLength));
		}

		if (!this.creditTranNo.isDisabled()) {
			this.creditTranNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_CreditTranNo.value"), false, false));
		}
		if (!this.creditTranAmt.isDisabled()) {
			this.creditTranAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_CreditTranAmt.value"), 0, false, false));
		}
		if (!this.creditTranAvg.isDisabled()) {
			this.creditTranAvg.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_CreditTranAvg.value"), 0, false, false));
		}
		if (!this.debitTranNo.isDisabled()) {
			this.debitTranNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_DebitTranNo.value"), false, false));
		}
		if (!this.debitTranAmt.isDisabled()) {
			this.debitTranAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_DebitTranAmt.value"), 0, false, false));
		}
		if (!this.cashDepositNo.isDisabled()) {
			this.cashDepositNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_CashDepositNo.value"), false, false));
		}
		if (!this.cashDepositAmt.isDisabled()) {
			this.cashDepositAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_CashDepositAmt.value"), 0, false, false));
		}
		if (!this.cashWithdrawalNo.isDisabled()) {
			this.cashWithdrawalNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_CashWithdrawalNo.value"), false, false));
		}
		if (!this.cashWithdrawalAmt.isDisabled()) {
			this.cashWithdrawalAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_CashWithdrawalAmt.value"), 0, false, false));
		}
		if (!this.chqDepositNo.isDisabled()) {
			this.chqDepositNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_ChqDepositNo.value"), false, false));
		}
		if (!this.chqDepositAmt.isDisabled()) {
			this.chqDepositAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_ChqDepositAmt.value"), 0, false, false));
		}
		if (!this.chqIssueNo.isDisabled()) {
			this.chqIssueNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_ChqIssueNo.value"), false, false));
		}
		if (!this.chqIssueAmt.isDisabled()) {
			this.chqIssueAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_ChqIssueAmt.value"), 0, false, false));
		}
		if (!this.inwardChqBounceNo.isDisabled()) {
			this.inwardChqBounceNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_InwardChqBounceNo.value"), false, false));
		}
		if (!this.outwardChqBounceNo.isDisabled()) {
			this.outwardChqBounceNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_OutwardChqBounceNo.value"), false, false));
		}
		if (!this.eodBalMin.isDisabled()) {
			this.eodBalMin.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_EodBalMin.value"), 0, false, false));
		}
		if (!this.eodBalMax.isDisabled()) {
			this.eodBalMax.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_EodBalMax.value"), 0, false, false));
		}
		if (!this.eodBalAvg.isDisabled()) {
			this.eodBalAvg.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_EodBalAvg.value"), 0, false, false));
		}

		// Bank Branch ID
		if (this.addToBenficiary.isChecked()) {
			if (!this.bankBranchID.isReadonly()) {
				this.bankBranchID.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CustomerBankInfoDialog_BankBranchID.value"), null, true));
			}
		}

		if (!this.accountHolderName.isReadonly()) {
			this.accountHolderName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerBankInfoDialog_AccountHolderName.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, this.addToBenficiary.isChecked()));
		}
		if (!this.phoneNumber.isReadonly()) {
			this.phoneNumber.setConstraint(
					new PTMobileNumberValidator(Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneNumber.value"),
							false, PennantRegularExpressions.REGEX_MOBILE, this.phoneNumber.getMaxlength()));

		}

		if (!this.ccLimit.isReadonly()) {
			this.ccLimit.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerBankInfoDialog_CCLimit.value"), null, this.ccLimit.isMandatory()));
		}

		if (!this.toDate.isDisabled()) {
			this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerBankInfoDialog_ToDate.value"),
					false, this.fromDate.getValue(), null, false));
		}

		if (!this.fromDate.isDisabled()) {
			this.fromDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerBankInfoDialog_FromDate.value"),
							false, null, this.toDate.getValue(), false));
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
		this.bankBranch.setConstraint("");
		this.accountHolderName.setConstraint("");
		this.phoneNumber.setConstraint("");
		this.fromDate.setConstraint("");
		this.accountOpeningDate.setConstraint("");
		this.toDate.setConstraint("");
		this.repaymentFrom.setConstraint("");
		this.NoOfMonthsBanking.setConstraint("");
		this.lwowRatio.setConstraint("");
		this.ccLimit.setConstraint("");
		this.typeOfBanks.setConstraint("");
		this.bankBranchID.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.bankName.setConstraint(new PTStringValidator(
				Labels.getLabel("label_CustomerBankInfoDialog_BankName.value"), null, true, true));
		this.accountType.setConstraint(new PTStringValidator(
				Labels.getLabel("label_CustomerBankInfoDialog_AccountType.value"), null, true, true));
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
		this.bankBranch.setErrorMessage("");
		this.accountHolderName.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.fromDate.setErrorMessage("");
		this.accountOpeningDate.setErrorMessage("");
		this.toDate.setErrorMessage("");
		this.repaymentFrom.setErrorMessage("");
		this.NoOfMonthsBanking.setErrorMessage("");
		this.lwowRatio.setErrorMessage("");
		this.ccLimit.setErrorMessage("");
		this.typeOfBanks.setErrorMessage("");
		this.bankBranchID.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CustomerBankInfo aCustomerBankInfo = new CustomerBankInfo();
		BeanUtils.copyProperties(getCustomerBankInfo(), aCustomerBankInfo);

		final String keyReference = Labels.getLabel("label_CustomerBankInfoDialog_BankName.value") + " : "
				+ aCustomerBankInfo.getBankName() + ","
				+ Labels.getLabel("label_CustomerBankInfoDialog_AccountNumber.value") + " : "
				+ aCustomerBankInfo.getAccountNumber();

		doDelete(keyReference, aCustomerBankInfo);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final CustomerBankInfo aCustomerBankInfo) {
		String tranType = PennantConstants.TRAN_WF;

		if (StringUtils.isBlank(aCustomerBankInfo.getRecordType())) {
			aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion() + 1);
			aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if (!isFinanceProcess && getCustomerDialogCtrl() != null
					&& getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()) {
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
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newFinanceCustomerProcess(aCustomerBankInfo, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerBankInfoDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getCustomerDialogCtrl().doFillCustomerBankInfoDetails(this.CustomerBankInfoList);
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
			} else {
				this.btnSearchPRCustid.setVisible(true);
			}
			this.bankName.setReadonly(isReadOnly("CustomerBankInfoDialog_BankName"));
			this.bankName.setMandatoryStyle(!isReadOnly("CustomerBankInfoDialog_BankName"));
			this.accountNumber.setReadonly(isReadOnly("CustomerBankInfoDialog_AccountNumber"));

		} else {
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
		readOnlyComponent(isReadOnly("CustomerBankInfoDialog_AccountOpeningDate"), this.accountOpeningDate);
		readOnlyComponent(isReadOnly("CustomerBankInfoDialog_BankBranch"), this.bankBranch);
		readOnlyComponent(isReadOnly("CustomerBankInfoDialog_FromDate"), this.fromDate);
		readOnlyComponent(isReadOnly("CustomerBankInfoDialog_ToDate"), this.toDate);
		readOnlyComponent(isReadOnly("CustomerBankInfoDialog_RepaymentFrom"), this.repaymentFrom);
		readOnlyComponent(isReadOnly("CustomerBankInfoDialog_NoOfMonthsBanking"), this.NoOfMonthsBanking);
		readOnlyComponent(isReadOnly("CustomerBankInfoDialog_lwowRatio"), this.lwowRatio);
		readOnlyComponent(isReadOnly("CustomerBankInfoDialog_CCLimit"), this.ccLimit);
		readOnlyComponent(isReadOnly("CustomerBankInfoDialog_TypeOfBanks"), this.typeOfBanks);
		this.phoneNumber.setReadonly(isReadOnly("CustomerBankInfoDialog_PhoneNumber"));
		this.accountHolderName.setReadonly(isReadOnly("CustomerBankInfoDialog_AccountHolderName"));
		setMonthlyIncomeListBoxProperties();

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

	@Override
	public boolean isReadOnly(String componentName) {
		boolean isCustomerWorkflow = false;
		if (getCustomerDialogCtrl() != null) {
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
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
		this.bankBranch.setReadonly(true);
		this.accountHolderName.setReadonly(true);
		this.phoneNumber.setReadonly(true);
		this.fromDate.setDisabled(true);
		this.accountOpeningDate.setDisabled(true);
		this.toDate.setDisabled(true);
		this.repaymentFrom.setDisabled(true);
		this.NoOfMonthsBanking.setReadonly(true);
		this.lwowRatio.setReadonly(true);
		this.ccLimit.setReadonly(true);
		this.typeOfBanks.setDisabled(true);
		this.bankBranchID.setReadonly(true);

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
		this.bankBranch.setValue("");
		this.accountHolderName.setValue("");
		this.phoneNumber.setValue("");
		this.repaymentFrom.setValue("");
		this.NoOfMonthsBanking.setValue(0);
		this.lwowRatio.setValue("");
		this.typeOfBanks.setValue("");
		this.bankBranchID.setValue("");
		this.bankBranchID.setDescription("");

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

		if (!saveBankInfoList(aCustomerBankInfo)) {
			return;
		}

		customerBankInfo.setBankInfoDetails(getBankInfoDetails());
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerBankInfo.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerBankInfo.getRecordType())) {
				aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion() + 1);
				if (isNew) {
					aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerBankInfo.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aCustomerBankInfo.setVersion(1);
					aCustomerBankInfo.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
					if (!isFinanceProcess && StringUtils.isBlank(aCustomerBankInfo.getRecordType())
							&& getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()) {
						aCustomerBankInfo.setNewRecord(true);
					}
				}

				if (StringUtils.isBlank(aCustomerBankInfo.getRecordType())) {
					aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion() + 1);
					aCustomerBankInfo.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aCustomerBankInfo.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aCustomerBankInfo.setVersion(aCustomerBankInfo.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newFinanceCustomerProcess(aCustomerBankInfo, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerBankInfoDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getCustomerDialogCtrl().doFillCustomerBankInfoDetails(this.CustomerBankInfoList);
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFinanceCustomerProcess(CustomerBankInfo aCustomerBankInfo, String tranType) {
		logger.debug("Entering");
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCustomerBankInfo, tranType);
		CustomerBankInfoList = new ArrayList<CustomerBankInfo>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustomerBankInfo.getLovDescCustCIF());
		valueParm[1] = aCustomerBankInfo.getAccountNumber();

		errParm[0] = PennantJavaUtil.getLabel("label_CustomerBankInfoDialog_CustID.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("listheader_CustomerBankInfo_AccountNumber.label") + ":" + valueParm[1];

		if (getCustomerDialogCtrl().getCustomerBankInfoDetailList() != null
				&& getCustomerDialogCtrl().getCustomerBankInfoDetailList().size() > 0) {
			for (int i = 0; i < getCustomerDialogCtrl().getCustomerBankInfoDetailList().size(); i++) {
				CustomerBankInfo customerBankInfo = getCustomerDialogCtrl().getCustomerBankInfoDetailList().get(i);

				if (aCustomerBankInfo.getAccountNumber().equals(customerBankInfo.getAccountNumber())
						&& aCustomerBankInfo.getBankName().equals(customerBankInfo.getBankName())) {
					// Both Current and Existing list rating same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							CustomerBankInfoList.add(aCustomerBankInfo);
						} else if (aCustomerBankInfo.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCustomerBankInfo.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							CustomerBankInfoList.add(aCustomerBankInfo);
						} else if (aCustomerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerBankInfoList()
									.size(); j++) {
								CustomerBankInfo email = getCustomerDialogCtrl().getCustomerDetails()
										.getCustomerBankInfoList().get(j);
								if (email.getCustID() == aCustomerBankInfo.getCustID()
										&& email.getBankName().equals(aCustomerBankInfo.getBankName())) {
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
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		final Customer aCustomer = (Customer) nCustomer;
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

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerBankInfo.getBefImage(), aCustomerBankInfo);
		return new AuditHeader(getReference(), String.valueOf(aCustomerBankInfo.getCustID()), null, null, auditDetail,
				aCustomerBankInfo.getUserDetails(), getOverideMap());
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
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.customerBankInfo);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustomerBankInfo().getCustID() + PennantConstants.KEY_SEPERATOR + getCustomerBankInfo().getBankName();
	}

	private void setMonthlyIncomeListBoxProperties() {
		if (monthlyIncome != null && StringUtils.equals(monthlyIncome, PennantConstants.YES)) {
			this.toolBar_AccBehaviour.setVisible(true);
			this.listBoxAccBehaviour.setVisible(true);
			this.button_CustomerBankInfoDialog_btnAccBehaviour
					.setVisible(!isReadOnly("CustomerBankInfoDialog_EodBalAvg")); // FIXME
			if (isCustomer360) {
				this.button_CustomerBankInfoDialog_btnAccBehaviour.setVisible(false);
				btnSearchPRCustid.setDisabled(true);
			}
			accountBehaviourSumary.setVisible(false);
		} else {
			accountBehaviourSumary.setVisible(true);
		}
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug("Entering");

		Object dataObject = bankBranchID.getObject();

		if (dataObject instanceof String) {
			this.bankBranchID.setValue(dataObject.toString());
		} else {
			BankBranch details = (BankBranch) dataObject;

			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bankName.setValue(details.getBankCode());
				this.bankName.setDescription(details.getBankName());
				this.bankBranch.setValue(details.getBranchDesc());
				this.bankBranchID.setValue(details.getIFSC());
				this.bankName.setButtonDisabled(false);
				if (StringUtils.isNotBlank(details.getBankCode())) {
					bankDetail = bankDetailService.getAccNoLengthByCode(details.getBankCode());
					maxAccNoLength = bankDetail.getAccNoLength();
					minAccNoLength = bankDetail.getMinAccNoLength();
				}
				this.accountNumber.setMaxlength(maxAccNoLength);

			} else {
				this.bankName.setValue("");
				this.bankBranch.setValue("");
				this.bankName.setButtonDisabled(true);
			}
		}
	}

	public void onCheck$addToBenficiary(Event event) {
		if (this.addToBenficiary.isChecked()) {
			this.accountHolderName.setValue(this.custShrtName.getValue());
			this.bankName.setValue("", "");
			this.bankName.setButtonDisabled(true);
			this.bankBranch.setValue("");
			this.bankBranchID.setButtonDisabled(false);
			this.bankBranch.setDisabled(true);
		} else {
			this.accountHolderName.setValue("");
			this.bankName.setButtonDisabled(false);
			this.bankBranchID.setButtonDisabled(true);
			this.bankBranchID.setValue("");
			this.bankBranch.setDisabled(false);
			this.bankName.setValue("", "");
			this.bankBranch.setValue("");

		}
	}

	public void onChange$accountNumber(ForwardEvent event) {

		if (isNewRecord()) {
			if (StringUtils.isNotEmpty(this.accountNumber.getValue()) && this.accountNumber.getValue() != null) {
				this.bankBranchID.setReadonly(false);
				this.accountHolderName.setReadonly(false);
				this.addToBenficiary.setDisabled(false);
			} else {
				this.bankBranchID.setReadonly(true);
				this.accountHolderName.setReadonly(true);
				this.addToBenficiary.setDisabled(true);
				this.addToBenficiary.setChecked(false);
				this.accountHolderName.setValue("");
				this.bankBranch.setValue("");
				this.bankBranchID.setValue("");
				this.bankName.setValue("");
				this.bankName.setDescription("");
			}

		}

	}

	public void onClick$button_CustomerBankInfoDialog_btnPerfiosDocUpload(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		ExternalDocument externalDocument = new ExternalDocument();
		externalDocument.setNewRecord(true);
		map.put("customerBankInfoDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("externalDocument", externalDocument);
		map.put("finReference", finReference);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/ExternalDocumentDialog.zul",
					window_CustomerBankInfoDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings("unchecked")
	public void onClick$button_CustomerBankInfoDialog_btnInitiateperfios(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		if (perfiosService != null) {
			HashMap<String, Object> employerMap = new HashMap<>();
			String empType = "SelfEmployed";
			String empName = "";
			String bankCode = "";
			if (customerBankInfo != null) {
				bankCode = StringUtils.trimToEmpty(customerBankInfo.getBankName());
			}

			Map<String, Object> bankInfoMap = new HashMap<>();
			List<CustomerBankInfo> customerBankInfoList = new ArrayList<CustomerBankInfo>();

			List<ExternalDocument> externalDocuments = getExternalDocumentsList();
			List<ExternalDocument> externalDocumentsList = new ArrayList<>();
			List<ExternalDocument> notSavedDocList = new ArrayList<>();
			List<ExternalDocument> processedDocList = new ArrayList<>();

			if (CollectionUtils.isEmpty(externalDocuments)) {
				MessageUtil.showMessage("Documents are not available to initiate Perfios,Please upload the documents");
				return;
			} else if (CollectionUtils.isNotEmpty(externalDocuments)) {

				ArrayList<Date> fromAndToDates = new ArrayList<>();

				for (ExternalDocument externalDocument : externalDocuments) {

					if (!(externalDocument.getId() <= 0)
							&& !(perfiosService.isDocumentExists(externalDocument.getDocRefId(), ""))) {

						if (StringUtils.isEmpty(externalDocument.getFinReference())) {
							externalDocument.setFinReference(this.finReference);
						}

						if (externalDocument.getDocImage() == null) {
							externalDocument.setDocImage(dmsService.getById(externalDocument.getDocRefId()));
						}

						externalDocumentsList.add(externalDocument);

						fromAndToDates.add(DateUtil.getDatePart(externalDocument.getFromDate()));
						fromAndToDates.add(DateUtil.getDatePart(externalDocument.getToDate()));

					} else if (externalDocument.getId() <= 0) {
						notSavedDocList.add(externalDocument);
					} else {
						processedDocList.add(externalDocument);
					}
				}

				if (CollectionUtils.isEmpty(notSavedDocList) && CollectionUtils.isNotEmpty(externalDocumentsList)) {

					if (getCustomerDialogCtrl() != null) {
						employerMap = getCustomerDialogCtrl().getExtendedFieldDetails();
					}

					if (employerMap.containsKey("empName")) {
						empName = (String) employerMap.get("empName");
					}

					Map<String, Object> map = new HashMap<>();

					if (StringUtils.equals(PennantConstants.EMPLOYMENTTYPE_SALARIED, this.empType)) {
						empType = "Salaried";
					} else if (StringUtils.equals(PennantConstants.EMPLOYMENTTYPE_SEP, this.empType)) {
						empType = "SelfEmployed";
					}

					Date docFromDate = Collections.min(fromAndToDates);
					Date docToDate = Collections.max(fromAndToDates);
					map.put("empType", empType);
					map.put("facility", "NONE");
					map.put("sanLimitType", false);
					map.put("sanLimitFixedAmt", BigDecimal.ZERO);
					map.put("variableAmounts", BigDecimal.ZERO);
					map.put("empName", empName);
					map.put("financeAmount", finAmount);
					map.put("bankName", bankName);
					map.put("bankCode", bankCode);
					if (tenor == 0) {
						tenor = 1;
					}
					map.put("loanDuration", tenor);
					map.put("custCIF", this.customerBankInfo.getLovDescCustCIF());
					map.put("finReference", this.finReference);
					map.put("yearMonthFrom", docFromDate);
					map.put("yearMonthTo", docToDate);
					bankInfoMap = perfiosService.statementUpaload(externalDocumentsList, map);
					customerBankInfoList = (List<CustomerBankInfo>) bankInfoMap.get("custBankInfoList");
				} else if (CollectionUtils.isNotEmpty(notSavedDocList)) {
					MessageUtil.showMessage("Please Save all the documents and proceed with perfios initiation");
					return;
				} else if (CollectionUtils.isNotEmpty(processedDocList)) {
					MessageUtil.showMessage("All Documents are initiated to perfios");
					return;
				}
			}

			if (!bankInfoMap.isEmpty() && bankInfoMap.get("error") != null
					&& StringUtils.isNotEmpty(bankInfoMap.get("error").toString())) {
				MessageUtil.showMessage(bankInfoMap.get("error").toString());
				return;
			}

			if (CollectionUtils.isNotEmpty(customerBankInfoList)) {
				List<BankInfoDetail> bankInfoDetails = new ArrayList<>();

				for (CustomerBankInfo info : customerBankInfoList) {
					bankInfoDetails = info.getBankInfoDetails();
					for (int i = 0; i < bankInfoDetails.size(); i++) {
						bankInfoDetails.get(i).setKeyValue(i + 1);
					}
				}

				setBankInfoDetails(bankInfoDetails);
				doFillBankInfoDetails();
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void doFillExternalDocuments(List<ExternalDocument> externalDocuments) {
		logger.debug(Literal.ENTERING);

		listBoxDocuments.getItems().clear();
		if (CollectionUtils.isNotEmpty(externalDocuments)) {
			int i = 0;
			for (ExternalDocument externalDocument : externalDocuments) {
				i++;
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell();
				lc.setId("DocName".concat(String.valueOf(i)));
				A docLink = new A();
				docLink.setLabel(externalDocument.getDocName());
				docLink.addForward("onClick", self, "onClickDoDownload", externalDocument);
				docLink.setStyle("text-decoration:underline;");
				lc.appendChild(docLink);
				lc.setParent(item);

				lc = new Listcell();
				lc.setIconSclass("FromDate".concat(String.valueOf(i)));
				lc.setLabel(DateUtil.formatToLongDate(externalDocument.getFromDate()));
				lc.setParent(item);

				lc = new Listcell();
				lc.setIconSclass("ToDate".concat(String.valueOf(i)));
				lc.setLabel(DateUtil.formatToLongDate(externalDocument.getToDate()));
				lc.setParent(item);

				if (!externalDocument.isNewRecord()) {
					lc = new Listcell();
					lc.setId("ResDoc".concat(String.valueOf(i)));
					A responseDoc = new A();
					responseDoc.setLabel("Perfios Report");
					responseDoc.addForward("onClick", self, "onClickDownloadPerfiosReport", externalDocument);
					responseDoc.setStyle("text-decoration:underline;");
					responseDoc.setTooltiptext("Downlod the Perfios Report");
					lc.appendChild(responseDoc);
					lc.setParent(item);
				} else {
					lc = new Listcell();
					lc.setParent(item);
				}

				item.setAttribute("data", externalDocument);
				this.listBoxDocuments.appendChild(item);
			}
			setExternalDocumentsList(externalDocuments);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClickDoDownload(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		ExternalDocument externalDocumet = (ExternalDocument) event.getData();

		if (externalDocumet != null && externalDocumet.getDocRefId() != 0
				&& externalDocumet.getDocRefId() != Long.MIN_VALUE && externalDocumet.getDocImage() == null) {
			externalDocumet.setDocImage(dmsService.getById(externalDocumet.getDocRefId()));
		}
		AMedia amedia = null;
		if (externalDocumet != null && externalDocumet.getDocImage() != null) {
			final InputStream data = new ByteArrayInputStream(externalDocumet.getDocImage());
			String docName = externalDocumet.getDocName();
			if (externalDocumet.getDocType().equals(PennantConstants.DOC_TYPE_PDF)) {
				amedia = new AMedia(docName, "pdf", "application/pdf", data);
			} else if (externalDocumet.getDocType().equals(PennantConstants.DOC_TYPE_IMAGE)) {
				amedia = new AMedia(docName, "jpeg", "image/jpeg", data);
			} else if (externalDocumet.getDocType().equals(PennantConstants.DOC_TYPE_WORD)
					|| externalDocumet.getDocType().equals(PennantConstants.DOC_TYPE_MSG)) {
				amedia = new AMedia(docName, "docx", "application/pdf", data);
			} else if (externalDocumet.getDocType().equals(PennantConstants.DOC_TYPE_ZIP)) {
				amedia = new AMedia(docName, "x-zip-compressed", "application/x-zip-compressed", data);
			} else if (externalDocumet.getDocType().equals(PennantConstants.DOC_TYPE_7Z)) {
				amedia = new AMedia(docName, "octet-stream", "application/octet-stream", data);
			} else if (externalDocumet.getDocType().equals(PennantConstants.DOC_TYPE_RAR)) {
				amedia = new AMedia(docName, "x-rar-compressed", "application/x-rar-compressed", data);
			}
			Filedownload.save(amedia);

		} else {
			MessageUtil.showMessage("Document details not available.");
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClickDownloadPerfiosReport(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		if (perfiosService == null) {
			return;
		}

		try {
			ExternalDocument externalDocument = (ExternalDocument) event.getData();
			PerfiosHeader perfiosHeader = perfiosService.getPerfiosReponseDocDetails(externalDocument.getDocRefId(),
					"");

			if (perfiosHeader == null) {
				MessageUtil.showMessage("Perfios Report details does not exist.");
				return;
			}

			AMedia amedia = null;
			final InputStream data;
			Long docRefId = perfiosHeader.getDocRefId();
			if (docRefId != null && docRefId != 0 && docRefId != Long.MIN_VALUE) {
				byte[] docImage = dmsService.getById(docRefId);
				if (docImage != null) {
					data = new ByteArrayInputStream(docImage);
					amedia = new AMedia(perfiosHeader.getDocName(), "xlsx",
							"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data);
					if (amedia != null) {
						Filedownload.save(amedia);
					}
				}
			} else if (StringUtils.equals(perfiosHeader.getStatusCode(), "S")
					&& StringUtils.equals(perfiosHeader.getProcessStage(), "G")) { // Report generated but not
																					// downloaded.

				perfiosHeader = customerDetailsService
						.processPerfiosDocumentAndBankInfoDetails(perfiosHeader.getTransactionId());
				if (perfiosHeader != null && perfiosHeader.getDocImage() != null) {
					data = new ByteArrayInputStream(perfiosHeader.getDocImage());
					amedia = new AMedia(perfiosHeader.getDocName(), "xlsx",
							"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data);
					if (amedia != null) {
						Filedownload.save(amedia);
					}
				} else {
					MessageUtil.showMessage(perfiosHeader.getStatusDesc());
				}
			} else if ("E".equals(perfiosHeader.getStatusCode())) {
				MessageUtil.showMessage(
						"Received Error from Perfios " + StringUtils.trimToEmpty(perfiosHeader.getStatusDesc()));
			} else {
				MessageUtil.showMessage("Perfios Report not yet generated.");
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when clicks on extended combobox "accountType"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void onFulfill$accountType(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = this.accountType.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.ccLimit.setMandatory(false);
			this.ccLimit.setErrorMessage("");
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				if (StringUtils.equals(MasterDefUtil.getAccountTypeCode(AccountType.OD), details.getFieldCodeValue())
						|| StringUtils.equals(MasterDefUtil.getAccountTypeCode(AccountType.CC),
								details.getFieldCodeValue())) {
					this.ccLimit.setMandatory(true);
				}
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
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

	public List<BankInfoDetail> getBankInfoDetails() {
		return bankInfoDetails;
	}

	public void setBankInfoDetails(List<BankInfoDetail> bankInfoDetails) {
		this.bankInfoDetails = bankInfoDetails;
	}

	public List<BankInfoSubDetail> getBankInfoSubDetails() {
		return bankInfoSubDetails;
	}

	public void setBankInfoSubDetails(List<BankInfoSubDetail> bankInfoSubDetails) {
		this.bankInfoSubDetails = bankInfoSubDetails;
	}

	public List<ExternalDocument> getExternalDocumentsList() {
		return externalDocumentsList;
	}

	public void setExternalDocumentsList(List<ExternalDocument> externalDocumentsList) {
		this.externalDocumentsList = externalDocumentsList;
	}

	@Autowired(required = false)
	public void setPerfiosService(PerfiousService perfiosService) {
		this.perfiosService = perfiosService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setDmsService(DMSService dmsService) {
		this.dmsService = dmsService;
	}

}
