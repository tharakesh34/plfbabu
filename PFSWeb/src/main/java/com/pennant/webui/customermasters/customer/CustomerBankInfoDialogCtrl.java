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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
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
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
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
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerBankInfo/customerBankInfoDialog.zul file.
 */
public class CustomerBankInfoDialogCtrl extends GFCBaseCtrl<CustomerBankInfo> {
	private static final long serialVersionUID = -7522534300621535097L;
	private static final Logger logger = Logger.getLogger(CustomerBankInfoDialogCtrl.class);

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
	protected Row row_salaryAccount;

	//####_0.2
	protected Row row_creditTranNo;
	protected Row row_creditTranAmt;
	protected Row row_creditTranAvg;
	protected Row row_debitTranNo;
	protected Row row_debitTranAmt;
	protected Row row_cashDepositNo;
	protected Row row_cashDepositAmt;
	protected Row row_cashWithdrawalNo;
	protected Row row_cashWithdrawalAmt;
	protected Row row_chqDepositNo;
	protected Row row_chqDepositAmt;
	protected Row row_chqIssueNo;
	protected Row row_chqIssueAmt;
	protected Row row_inwardChqBounceNo;
	protected Row row_outwardChqBounceNo;
	protected Row row_eodBalMin;
	protected Row row_eodBalMax;
	protected Row row_eodBalAvg;
	protected Row row_bankBranch;
	protected Row row_fromDate;
	protected Row row_toDate;
	protected Row row_repaymentFrom;
	protected Row row_noOfMonthsBanking;
	protected Row row_lwowRatio;
	protected Row row_ccLimit;
	protected Row row_typeOfBanks;

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
	protected Textbox ccLimit;
	protected Combobox typeOfBanks;
	protected Datebox accountOpeningDate;

	//BHFL
	protected Button button_CustomerBankInfoDialog_btnAccBehaviour;
	protected Toolbar toolBar_AccBehaviour;
	protected Listbox listBoxAccBehaviour;
	protected Listheader listHead_AccBehaviour;

	private int finFormatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

	// not auto wired variables
	private CustomerBankInfo customerBankInfo; // overHanded per parameter
	private transient boolean validationOn;
	protected Button btnSearchPRCustid;
	private boolean newRecord = false;
	private boolean newCustomer = false;
	private List<CustomerBankInfo> CustomerBankInfoList;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType = "";
	private String userRole = "";
	private boolean isFinanceProcess = false;
	protected int accNoLength;
	private transient BankDetailService bankDetailService;

	private List<BankInfoDetail> bankInfoDetails = new ArrayList<>();
	private List<BankInfoSubDetail> bankInfoSubDetails = new ArrayList<>();
	private String monthlyIncome = SysParamUtil.getValueAsString(SMTParameterConstants.MONTHLY_INCOME_REQ); // FIXME
	private String configDay = SysParamUtil.getValueAsString(SMTParameterConstants.BANKINFO_DAYS); // FIXME

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
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerBankInfoDialog(Event event) throws Exception {
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
					this.row_salaryAccount.setVisible(false);
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

		fillComboBox(repaymentFrom, "", PennantStaticListUtil.getYesNo(), "");
		fillComboBox(typeOfBanks, "", PennantStaticListUtil.getTypeOfBanks(), "");
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("FieldCode", "ACC_TYPE", Filter.OP_EQUAL);
		this.accountType.setFilters(filter);

		this.accountType.setMaxlength(8);

		if (StringUtils.isNotBlank(this.customerBankInfo.getBankCode())) {
			accNoLength = bankDetailService.getAccNoLengthByCode(this.customerBankInfo.getBankCode());
		}

		if (accNoLength != 0) {
			this.accountNumber.setMaxlength(accNoLength);
		} else {
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

		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.accountOpeningDate.setFormat(DateFormat.SHORT_DATE.getPattern());

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
		getUserWorkspace().allocateAuthorities("CustomerBankInfoDialog", userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerBankInfoDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
				if (bankInfo.getKeyValue() > keyValue) {
					keyValue = bankInfo.getKeyValue();
				}
			}
		}
		bankInfoDetail.setKeyValue(keyValue + 1);

		renderItem(bankInfoDetail);
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
		logger.debug(Literal.LEAVING);
	}

	private void renderItem(BankInfoDetail bankInfoDetail) {
		Listitem listItem = new Listitem();
		Listcell listCell;
		Hbox hbox;
		Space space;
		boolean isReadOnly = isReadOnly("CustomerBankInfoDialog_AccountType");

		// Month Year
		listCell = new Listcell();
		Datebox monthYear = new Datebox();
		monthYear.setFormat(PennantConstants.monthYearFormat);
		if (bankInfoDetail.isNew() && StringUtils.isEmpty(bankInfoDetail.getRecordType())) {
			readOnlyComponent(false, monthYear);
		} else {
			readOnlyComponent(true, monthYear);
		}
		monthYear.setReadonly(true); //Intentionally we put as read-only here
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

		// Bank Balance
		listCell = new Listcell(); //FIXME
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
		for (String day : configDay.split(",")) {

			int j = 0;
			try {
				j = Integer.parseInt(StringUtils.trim(day));
			} catch (NumberFormatException e) {
				continue;
			}

			balCount++;
			row = new Row();
			//Day
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
			//Balance
			box = new CurrencyBox();
			box.setId("balance_currency".concat(String.valueOf(bankInfoDetail.getKeyValue()))
					.concat(String.valueOf(balCount)));
			box.setBalUnvisible(true);
			box.setFormat(PennantApplicationUtil.getAmountFormate(2));
			box.setScale(2);
			if (bankInfoDetail.getBankInfoSubDetails().size() > 0) {
				box.setValue(PennantApplicationUtil
						.formateAmount(bankInfoDetail.getBankInfoSubDetails().get(balCount - 1).getBalance(), 0));
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

		// ODCC Limit
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox odCCLimit = new CurrencyBox();
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
		this.listBoxAccBehaviour.appendChild(listItem);
	}
	
	public void onChangeConfigDay(Event event) {
		logger.debug(Literal.ENTERING);

		BigDecimal balanceSum = BigDecimal.ZERO;

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
						Clients.clearWrongValue(balanceValue);
						if (balanceValue.getValidateValue() != null) {
							balanceSum = balanceSum.add(balanceValue.getValidateValue());
							count++;
						}
					}

				}
			}

			Hbox hbox1 = (Hbox) getComponent(listItem, "odCCLimit");
			CurrencyBox db = (CurrencyBox) hbox1.getLastChild();
			db.setValue(balanceSum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClickAccBehaviourButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		listBoxAccBehaviour.removeItemAt(item.getIndex());
		logger.debug(Literal.LEAVING);
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

		if (aCustomerBankInfo.getCustID() != Long.MIN_VALUE) {
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
		if (CustomerBankInfoList != null) {
			for (CustomerBankInfo customerBankInfo : CustomerBankInfoList) {
				if (customerBankInfo.isSalaryAccount()) {
					this.salaryAccount.setDisabled(true);
				}
			}
		}
		if (aCustomerBankInfo.isSalaryAccount()) {
			this.salaryAccount.setDisabled(false);
		}

		this.creditTranNo.setValue(aCustomerBankInfo.getCreditTranNo());
		this.creditTranAmt.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getCreditTranAmt(), finFormatter));
		this.creditTranAvg.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getCreditTranAvg(), finFormatter));
		this.debitTranNo.setValue(aCustomerBankInfo.getDebitTranNo());
		this.debitTranAmt.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getDebitTranAmt(), finFormatter));
		this.cashDepositNo.setValue(aCustomerBankInfo.getCashDepositNo());
		this.cashDepositAmt.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getCashDepositAmt(), finFormatter));
		this.cashWithdrawalNo.setValue(aCustomerBankInfo.getCashWithdrawalNo());
		this.cashWithdrawalAmt
				.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getCashWithdrawalAmt(), finFormatter));
		this.chqDepositNo.setValue(aCustomerBankInfo.getChqDepositNo());
		this.chqDepositAmt.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getChqDepositAmt(), finFormatter));
		this.chqIssueNo.setValue(aCustomerBankInfo.getChqIssueNo());
		this.chqIssueAmt.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getChqIssueAmt(), finFormatter));
		this.inwardChqBounceNo.setValue(aCustomerBankInfo.getInwardChqBounceNo());
		this.outwardChqBounceNo.setValue(aCustomerBankInfo.getOutwardChqBounceNo());
		this.eodBalMin.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getEodBalMin(), finFormatter));
		this.eodBalMax.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getEodBalMax(), finFormatter));
		this.eodBalAvg.setValue(PennantAppUtil.formateAmount(aCustomerBankInfo.getEodBalAvg(), finFormatter));
		this.bankBranch.setValue(aCustomerBankInfo.getBankBranch());
		this.fromDate.setValue(aCustomerBankInfo.getFromDate());
		this.accountOpeningDate.setValue(aCustomerBankInfo.getAccountOpeningDate());
		this.toDate.setValue(aCustomerBankInfo.getToDate());
		this.repaymentFrom.setValue(aCustomerBankInfo.getRepaymentFrom());
		this.NoOfMonthsBanking.setValue(aCustomerBankInfo.getNoOfMonthsBanking());
		this.lwowRatio.setValue(aCustomerBankInfo.getLwowRatio());
		this.ccLimit.setValue(aCustomerBankInfo.getCcLimit());
		this.typeOfBanks.setValue(aCustomerBankInfo.getTypeOfBanks());

		this.recordStatus.setValue(aCustomerBankInfo.getRecordStatus());

		if (aCustomerBankInfo.getBankInfoDetails().size() > 0) {
			Cloner cloner = new Cloner();
			CustomerBankInfo detail = (CustomerBankInfo) cloner.deepClone(aCustomerBankInfo);
			List<BankInfoDetail> bankInfoDetails = detail.getBankInfoDetails();
			for (int i = 0; i < bankInfoDetails.size(); i++) {
				bankInfoDetails.get(i).setKeyValue(i + 1);
			}
			setBankInfoDetails(bankInfoDetails);
			doFillBankInfoDetails();
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
			aCustomerBankInfo.setBankName(this.bankName.getValidatedValue());//TODO: change it to name and increase the size in db
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
					PennantAppUtil.unFormateAmount(this.creditTranAmt.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setCreditTranAvg(
					PennantAppUtil.unFormateAmount(this.creditTranAvg.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setDebitTranNo(this.debitTranNo.intValue());
		} catch (WrongValueException we) {

		}
		try {
			aCustomerBankInfo.setDebitTranAmt(
					PennantAppUtil.unFormateAmount(this.debitTranAmt.getValidateValue(), finFormatter));
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
					PennantAppUtil.unFormateAmount(this.cashDepositAmt.getValidateValue(), finFormatter));
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
					PennantAppUtil.unFormateAmount(this.cashWithdrawalAmt.getValidateValue(), finFormatter));
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
					PennantAppUtil.unFormateAmount(this.chqDepositAmt.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setChqIssueNo(this.chqIssueNo.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo
					.setChqIssueAmt(PennantAppUtil.unFormateAmount(this.chqIssueAmt.getValidateValue(), finFormatter));
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
			aCustomerBankInfo
					.setEodBalMin(PennantAppUtil.unFormateAmount(this.eodBalMin.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo
					.setEodBalMax(PennantAppUtil.unFormateAmount(this.eodBalMax.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo
					.setEodBalAvg(PennantAppUtil.unFormateAmount(this.eodBalAvg.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setBankBranch((this.bankBranch.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setFromDate((this.fromDate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
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
			aCustomerBankInfo.setRepaymentFrom((this.repaymentFrom.getValue()));
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
			aCustomerBankInfo.setCcLimit((this.ccLimit.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerBankInfo.setTypeOfBanks((this.typeOfBanks.getValue()));
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

		aCustomerBankInfo.setRecordStatus(this.recordStatus.getValue());
		setCustomerBankInfo(aCustomerBankInfo);
		logger.debug("Leaving");
	}

	public boolean saveBankInfoList(CustomerBankInfo customerBankInfo) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		HashMap<Date, BankInfoDetail> hashMap = new HashMap<>();

		List<BankInfoDetail> infoList = customerBankInfo.getBankInfoDetails();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		Set<Date> dateValidatioinSet = new HashSet<>();

		for (Listitem listitem : listBoxAccBehaviour.getItems()) {

			BankInfoDetail bankInfoDetail = (BankInfoDetail) listitem.getAttribute("data");

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
				getCompValuetoBean(listitem, "odCCLimit");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			showErrorDetails(wve);

			boolean isNew = false;
			isNew = bankInfoDetail.isNew();
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
		valueParm[0] = String.valueOf(DateUtility.format(detail.getMonthYear(), PennantConstants.monthYearFormat));
		errParm[0] = "Monthyear" + ":" + valueParm[0];

		bankInfoDetails = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(infoList)) {
			for (int i = 0; i < infoList.size(); i++) {
				if (DateUtility.compare(detail.getMonthYear(), infoList.get(i).getMonthYear()) == 0) {
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
			Datebox monthYear = (Datebox) hbox1.getLastChild();
			Clients.clearWrongValue(monthYear);
			Date monthYearValue = monthYear.getValue();
			if (!monthYear.isDisabled()) {
				if (monthYearValue == null) {
					throw new WrongValueException(monthYear,
							Labels.getLabel("FIELD_IS_MAND", new String[] { "Month Year" }));
				} else {
					monthYearValue.setDate(1);
					if (DateUtility.compare(monthYearValue, DateUtility.getAppDate()) == 1) {
						throw new WrongValueException(monthYear,
								Labels.getLabel("DATE_NO_FUTURE", new String[] { "Month Year" }));
					}
				}
			}
			monthYearValue.setDate(1);
			bankInfoDetail.setMonthYear(monthYearValue);
			break;

		case "balance":
			bankInfoDetail = getDayBalanceList(listitem, "balance", bankInfoDetail);
			break;

		case "debitNo":
			Hbox hbox3 = (Hbox) getComponent(listitem, "debitNo");
			Intbox debitNo = (Intbox) hbox3.getLastChild();
			Clients.clearWrongValue(debitNo);
			if (!debitNo.isReadonly() && debitNo.getValue() == null) {
				throw new WrongValueException(debitNo, Labels.getLabel("FIELD_IS_MAND", new String[] { "Debit No" }));
			} else if (!debitNo.isReadonly() && debitNo.getValue() <= 0) {
				throw new WrongValueException(debitNo,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Debit No" }));
			}
			bankInfoDetail.setDebitNo(debitNo.getValue());
			break;

		case "debitAmt":
			BigDecimal debitAmt = BigDecimal.ZERO;
			Hbox hbox4 = (Hbox) getComponent(listitem, "debitAmt");
			CurrencyBox debitAmtValue = (CurrencyBox) hbox4.getLastChild();
			Clients.clearWrongValue(debitAmtValue);
			if (debitAmtValue.getValidateValue() != null) {
				debitAmt = debitAmtValue.getValidateValue();
			}
			if (!(debitAmtValue.isReadonly()) && (debitAmt.intValue() <= 0)) {
				throw new WrongValueException(debitAmtValue,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Debit Amount" }));
			}
			bankInfoDetail.setDebitAmt(PennantAppUtil.unFormateAmount(debitAmt, 2));
			break;

		case "creditNo":
			Hbox hbox5 = (Hbox) getComponent(listitem, "creditNo");
			Intbox creditNo = (Intbox) hbox5.getLastChild();
			Clients.clearWrongValue(creditNo);
			if (!creditNo.isReadonly() && creditNo.getValue() == null) {
				throw new WrongValueException(creditNo, Labels.getLabel("FIELD_IS_MAND", new String[] { "Credit No" }));
			} else if (!creditNo.isReadonly() && creditNo.getValue() <= 0) {
				throw new WrongValueException(creditNo,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Credit No" }));
			}
			bankInfoDetail.setCreditNo(creditNo.getValue());
			break;

		case "creditAmt":
			BigDecimal creditAmt = BigDecimal.ZERO;
			Hbox hbox6 = (Hbox) getComponent(listitem, "creditAmt");
			CurrencyBox creditAmtValue = (CurrencyBox) hbox6.getLastChild();
			Clients.clearWrongValue(creditAmtValue);
			if (creditAmtValue.getValidateValue() != null) {
				creditAmt = creditAmtValue.getValidateValue();
			}
			if (!(creditAmtValue.isReadonly()) && (creditAmt.intValue() <= 0)) {
				throw new WrongValueException(creditAmtValue,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Credit Amount" }));
			}
			bankInfoDetail.setCreditAmt(PennantAppUtil.unFormateAmount(creditAmt, 2));
			break;

		case "bounceInWard":
			BigDecimal bounceInWard = BigDecimal.ZERO;
			Hbox hbox7 = (Hbox) getComponent(listitem, "bounceInWard");
			CurrencyBox bounceIn = (CurrencyBox) hbox7.getLastChild();
			Clients.clearWrongValue(bounceIn);
			if (bounceIn.getValidateValue() != null) {
				bounceInWard = bounceIn.getValidateValue();
			}
			if (!(bounceIn.isReadonly()) && (bounceInWard.intValue() < 0)) {
				throw new WrongValueException(bounceIn,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Bounce In Ward" }));
			}
			bankInfoDetail.setBounceIn(PennantAppUtil.unFormateAmount(bounceInWard, 2));
			break;

		case "bounceOutWard":
			BigDecimal bounceInOut = BigDecimal.ZERO;
			Hbox hbox8 = (Hbox) getComponent(listitem, "bounceOutWard");
			CurrencyBox bounceOut = (CurrencyBox) hbox8.getLastChild();
			Clients.clearWrongValue(bounceOut);
			if (bounceOut.getValidateValue() != null) {
				bounceInOut = bounceOut.getValidateValue();
			}
			if (!(bounceOut.isReadonly()) && (bounceInOut.intValue() < 0)) {
				throw new WrongValueException(bounceOut,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Bounce Out Ward" }));
			}
			bankInfoDetail.setBounceOut(PennantAppUtil.unFormateAmount(bounceInOut, 2));
			break;

		case "closingBal":
			BigDecimal closingBal = BigDecimal.ZERO;
			Hbox hbox9 = (Hbox) getComponent(listitem, "closingBal");
			CurrencyBox closingBalValue = (CurrencyBox) hbox9.getLastChild();
			Clients.clearWrongValue(closingBalValue);
			if (closingBalValue.getValidateValue() != null) {
				closingBal = closingBalValue.getValidateValue();
			}
			bankInfoDetail.setClosingBal(PennantAppUtil.unFormateAmount(closingBal, 2));
			break;

		case "odCCLimit":
			BigDecimal odCCLimit = BigDecimal.ZERO;
			Hbox hbox10 = (Hbox) getComponent(listitem, "odCCLimit");
			CurrencyBox odCCLimitValue = (CurrencyBox) hbox10.getLastChild();
			Clients.clearWrongValue(odCCLimitValue);
			if (odCCLimitValue.getValidateValue() != null) {
				odCCLimit = odCCLimitValue.getValidateValue();
			}
			bankInfoDetail.setoDCCLimit(PennantAppUtil.unFormateAmount(odCCLimit, 2));
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
		List<Listcell> listcels = listitem.getChildren();
		BigDecimal balance = BigDecimal.ZERO;
		List<BankInfoSubDetail> list = bankInfoDetail.getBankInfoSubDetails();
		for (Listcell listcell : listcels) {
			String id = StringUtils.trimToNull(listcell.getId());

			if (id == null) {
				continue;
			}

			id = id.replaceAll("\\d", "");
			if (StringUtils.equals(id, listcellId)) {

				int i = 0;
				for (String day : configDay.split(",")) {

					try {
						Integer.parseInt(StringUtils.trim(day));
					} catch (NumberFormatException e) {
						logger.error(Literal.EXCEPTION, e);
						continue;
					}
					i++;
					//Label day = (Label) listcell.getFellowIfAny("day"+i);
					CurrencyBox balanceValue = (CurrencyBox) listcell.getFellowIfAny("balance_currency"
							.concat(String.valueOf(bankInfoDetail.getKeyValue())).concat(String.valueOf(i)));
					Clients.clearWrongValue(balanceValue);
					if (balanceValue.getValidateValue() != null) {
						balance = balanceValue.getValidateValue();
					}
					if (!(balanceValue.isReadonly()) && (balance.intValue() < 0)) {
						throw new WrongValueException(balanceValue,
								Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Balance" }));
					}

					BankInfoSubDetail subDetail = null;
					if (list != null && !list.isEmpty()) {
						for (BankInfoSubDetail subDtl : list) {
							if (subDtl.getDay() == i) {
								subDetail = subDtl;
							}
						}
					}

					if (subDetail == null) {
						subDetail = new BankInfoSubDetail();
						subDetail.setMonthYear(bankInfoDetail.getMonthYear());
						subDetail.setDay(i);
						list.add(subDetail);
					}
					subDetail.setBalance(balance);
				}
			}
		}
		bankInfoDetail.setBankInfoSubDetails(list);
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

		if (dataObject instanceof String) {
			this.bankName.setValue(dataObject.toString(), "");
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankName.setValue(details.getBankCode(), details.getBankName());
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
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
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
			this.fromDate.setReadonly(true);
			this.accountOpeningDate.setReadonly(true);
			this.toDate.setReadonly(true);
			this.repaymentFrom.setReadonly(true);
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
							PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true));
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
		this.fromDate.setConstraint("");
		this.accountOpeningDate.setConstraint("");
		this.toDate.setConstraint("");
		this.repaymentFrom.setConstraint("");
		this.NoOfMonthsBanking.setConstraint("");
		this.lwowRatio.setConstraint("");
		this.ccLimit.setConstraint("");
		this.typeOfBanks.setConstraint("");
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
		this.fromDate.setErrorMessage("");
		this.accountOpeningDate.setErrorMessage("");
		this.toDate.setErrorMessage("");
		this.repaymentFrom.setErrorMessage("");
		this.NoOfMonthsBanking.setErrorMessage("");
		this.lwowRatio.setErrorMessage("");
		this.ccLimit.setErrorMessage("");
		this.typeOfBanks.setErrorMessage("");
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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_CustomerBankInfoDialog_BankName.value") + " : "
				+ aCustomerBankInfo.getBankName() + ","
				+ Labels.getLabel("label_CustomerBankInfoDialog_AccountNumber.value") + " : "
				+ aCustomerBankInfo.getAccountNumber();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
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
		logger.debug("Leaving");
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

		/*
		 * this.bankBranch.setReadonly(isReadOnly( "CustomerBankInfoDialog_BankBranch"));
		 * this.fromDate.setReadonly(isReadOnly( "CustomerBankInfoDialog_FromDate"));
		 * this.fromDate.setReadonly(isReadOnly("CustomerBankInfoDialog_ToDate") );
		 * this.repaymentFrom.setReadonly(isReadOnly( "CustomerBankInfoDialog_RepaymentFrom"));
		 * this.NoOfMonthsBanking.setReadonly(isReadOnly( "CustomerBankInfoDialog_NoOfMonthsBanking"));
		 * this.lwowRatio.setReadonly(isReadOnly( "CustomerBankInfoDialog_lwowRatio"));
		 * this.ccLimit.setReadonly(isReadOnly("CustomerBankInfoDialog_CCLimit") );
		 * this.typeOfBanks.setReadonly(isReadOnly( "CustomerBankInfoDialog_TypeOfBanks"));
		 */

		if (monthlyIncome != null && StringUtils.equals(monthlyIncome, PennantConstants.YES)) {
			this.toolBar_AccBehaviour.setVisible(true);
			this.listBoxAccBehaviour.setVisible(true);
			this.button_CustomerBankInfoDialog_btnAccBehaviour
					.setVisible(!isReadOnly("CustomerBankInfoDialog_EodBalAvg")); //FIXME Rightname

			this.row_creditTranNo.setVisible(false);
			this.row_creditTranAmt.setVisible(false);
			this.row_creditTranAvg.setVisible(false);
			this.row_debitTranNo.setVisible(false);
			this.row_debitTranAmt.setVisible(false);
			this.row_cashDepositNo.setVisible(false);
			this.row_cashDepositAmt.setVisible(false);
			this.row_cashWithdrawalNo.setVisible(false);
			this.row_cashWithdrawalAmt.setVisible(false);
			this.row_chqDepositNo.setVisible(false);
			this.row_chqDepositAmt.setVisible(false);
			this.row_chqIssueNo.setVisible(false);
			this.row_chqIssueAmt.setVisible(false);
			this.row_inwardChqBounceNo.setVisible(false);
			this.row_outwardChqBounceNo.setVisible(false);
			this.row_eodBalMin.setVisible(false);
			this.row_eodBalMax.setVisible(false);
			this.row_eodBalAvg.setVisible(false);
			/*
			 * this.row_bankBranch.setVisible(false); this.row_fromDate.setVisible(false);
			 * this.row_toDate.setVisible(false); this.row_repaymentFrom.setVisible(false);
			 * this.row_noOfMonthsBanking.setVisible(false); this.row_lwowRatio.setVisible(false);
			 * this.row_ccLimit.setVisible(false); this.row_typeOfBanks.setVisible(false);
			 */

		}

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

	public boolean isReadOnly(String componentName) {
		boolean isCustomerWorkflow = false;
		if (getCustomerDialogCtrl() != null) {
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
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
		this.fromDate.setReadonly(true);
		this.accountOpeningDate.setReadonly(true);
		this.toDate.setReadonly(true);
		this.repaymentFrom.setReadonly(true);
		this.NoOfMonthsBanking.setReadonly(true);
		this.lwowRatio.setReadonly(true);
		this.ccLimit.setReadonly(true);
		this.typeOfBanks.setReadonly(true);

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
		this.repaymentFrom.setValue("");
		this.NoOfMonthsBanking.setValue(0);
		this.lwowRatio.setValue("");
		this.typeOfBanks.setValue("");

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

		isNew = aCustomerBankInfo.isNew();
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
					if (!isFinanceProcess && getCustomerDialogCtrl() != null
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
	 * @param e
	 *            (Exception)
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
	 * @param event
	 *            (Event)
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
		return getCustomerBankInfo().getCustID() + PennantConstants.KEY_SEPERATOR + getCustomerBankInfo().getBankName();
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
}
