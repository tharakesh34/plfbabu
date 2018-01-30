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
 * FileName    		:  ManagerChequeDialogCtrl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  12-01-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                    														* 
 * 12-01-2016       Satya	                 0.2         1. PSD - Ticket: 124096       		*
 * 															Nostro Account will populate 	*
 * 															automatically by default and it	*
 * 															can be editable.  				*
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.managercheque;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.ManagerCheque;
import com.pennant.backend.model.rmtmasters.TransactionDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.financemanagement.ManagerChequeService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.financemanagement.managercheque.ManagerChequeListCtrl.MCType;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/FinanceManagement/ManagerCheque/managerChequeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ManagerChequeDialogCtrl extends GFCBaseCtrl<ManagerCheque> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ManagerChequeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ManagerChequeDialog;
	protected Label label_ManagerChequeDialog_title;
	protected Row row0;
	protected Label label_ChqPurposeCode;
	protected Hlayout hlayout_ChqPurposeCode;
	protected Space space_ChqPurposeCode;

	protected Combobox chqPurposeCode;
	protected Label label_ChequeRef;
	protected Label label_branchCode;
	protected ExtendedCombobox branchCode;

	protected Hlayout hlayout_ChequeRef;
	protected Space space_ChequeRef;

	protected Textbox chequeRef;
	protected Row row1;
	protected Label label_ChequeNo;
	protected Hlayout hlayout_ChequeNo;
	protected Space space_ChequeNo;

	protected Textbox chequeNo;
	protected Label label_BeneficiaryName;
	protected Hlayout hlayout_BeneficiaryName;
	protected Space space_BeneficiaryName;

	protected Textbox beneficiaryName;
	protected Row row2;
	protected Label label_CustCIF;
	protected Hlayout hlayout_CustCIF;
	protected Space space_CustCIF;

	protected Textbox custCIF;
	protected Label label_DraftCcy;

	protected ExtendedCombobox draftCcy;
	protected Row row3;
	protected Label label_FundingCcy;

	protected ExtendedCombobox fundingCcy;
	protected Label label_FundingAccount;
	protected Hlayout hlayout_FundingAccount;

	protected AccountSelectionBox fundingAccount;
	protected Row row4;
	protected Label label_NostroAccount;
	protected Hlayout hlayout_NostroAccount;

	protected AccountSelectionBox nostroAccount;
	protected Label label_NostroFullName;
	protected Hlayout hlayout_NostroFullName;
	protected Space space_NostroFullName;

	protected Textbox nostroFullName;
	protected Row row5;
	protected Label label_ChequeAmount;

	protected CurrencyBox chequeAmount;
	protected Label label_ValueDate;

	protected CurrencyBox availableAmt;
	protected Label label_AvailableAmt;

	protected Hlayout hlayout_ValueDate;
	protected Space space_ValueDate;

	protected CurrencyBox disbursedAmt;
	protected Label label_DisbursedAmt;

	protected Datebox valueDate;
	protected Row row6;
	protected Label label_Narration1;
	protected Hlayout hlayout_Narration1;
	protected Space space_Narration1;

	protected Textbox narration1;
	protected Label label_Narration2;
	protected Hlayout hlayout_Narration2;
	protected Space space_Narration2;

	protected Textbox narration2;
	protected Row row7;
	protected Groupbox gb_basicDetails;

	protected Label label_ManagerChequeDialog_FinType;
	protected Label managerChq_finType;
	protected Label label_ManagerChequeDialog_FinReference;
	protected Label managerChq_finReference;
	protected Label label_ManagerChequeDialog_CustomerCIF;
	protected Label managerChq_CustCIF;
	protected Label label_ManagerChequeDialog_Currency;
	protected Label managerChq_Currency;
	protected Label label_ManagerChequeDialog_FinBranch;
	protected Label managerChq_FinBranch;
	protected Label label_ManagerChequeDialog_GrcEndDate;
	protected Label managerChq_grcEndDate;
	protected Label label_ManagerChequeDialog_StartDate;
	protected Label managerChq_startDate;
	protected Label label_ManagerChequeDialog_MaturityDate;
	protected Label managerChq_maturityDate;
	protected Label label_ManagerChequeDialog_FinAmount;
	protected Label label_ManagerChequeDialog_DownPayments;
	protected Label label_ManagerChequeDialog_Fees;
	protected Label label_ManagerChequeDialog_DisbursementAmt;
	protected Label label_ManagerChequeDialog_TotalProfitAmt;
	protected Label label_ManagerChequeDialog_NetEffectiveRate;
	protected Label managerChq_NetEffectiveRate;

	protected Decimalbox managerChq_FinAmount;
	protected Decimalbox managerChq_DownPayments;
	protected Decimalbox managerChq_Fees;
	protected Decimalbox managerChq_TotalProfitAmt;
	protected Decimalbox managerChq_DisbursementAmt;
	protected Tabpanel documnetDetailsTabPanel;
	protected Listbox listBoxDocumentDetails;
	protected Listbox listBoxDocumentEnquiry;
	protected Label custShrtName;
	protected Tab managerChequeDetailsTab;

	protected Label label_OldChequeNo;
	protected Label oldChequeNo;

	protected Row row8;
	protected Label label_ChargeAmount;
	protected CurrencyBox chargeAmount;
	protected Label label_FundingAmount;
	protected CurrencyBox fundingAmount;

	protected Row row9;
	protected Label label_IssueDate;
	protected Hlayout hlayout_IssueDate;
	protected Space space_IssueDate;
	protected Datebox issueDate;

	protected Row row10;
	protected Label label_AddressLine1;
	protected Hlayout hlayout_AddressLine1;
	protected Space space_AddressLine1;
	protected Textbox addressLine1;
	protected Label label_AddressLine2;
	protected Hlayout hlayout_AddressLine2;
	protected Space space_AddressLine2;
	protected Textbox addressLine2;

	protected Row row11;
	protected Label label_AddressLine3;
	protected Hlayout hlayout_AddressLine3;
	protected Space space_AddressLine3;
	protected Textbox addressLine3;
	protected Label label_AddressLine4;
	protected Hlayout hlayout_AddressLine4;
	protected Space space_AddressLine4;
	protected Textbox addressLine4;

	protected Row row12;
	protected Label label_AddressLine5;
	protected Hlayout hlayout_AddressLine5;
	protected Space space_AddressLine5;
	protected Textbox addressLine5;

	protected Row row_StopOrderRef;
	protected Label label_StopOrderRef;

	// Accounting Set Details Tab

	protected Tabpanel accountingTabPanel;

	protected Button btnAccounting; // autoWired
	protected Label label_AccountingDisbCrVal; // autoWired
	protected Label label_AccountingDisbDrVal; // autoWired
	protected Label label_AccountingSummaryVal; // autoWired
	protected Button btnPrintAccounting; // autowired
	protected Listbox listBoxFinAccountings; // autowired

	protected Groupbox gb_AccDetails;
	protected Label acc_ManagerChq_finType;
	protected Label acc_ManagerChq_Currency;
	protected Label acc_ManagerChq_RepaymantSchMethod;
	protected Label acc_ManagerChq_ProfitDaysBasis;
	protected Label acc_ManagerChq_FinReference;
	protected Label acc_ManagerChq_GrcEndDate;

	// Recommendations Tab
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;

	// not auto wired vars
	private ManagerCheque managerCheque; // overhanded per param
	private transient ManagerChequeListCtrl managerChequeListCtrl; // overhanded per param

	
	protected Button btnPrintCancelChq;

	protected Button btnNew_DocumentDetails;

	
	

	// ServiceDAOs / Domain Classes
	private transient ManagerChequeService managerChequeService;
	private transient PagedListService pagedListService;
	private List<ValueLabel> listChqPurposeCodes = PennantAppUtil.getChqPurposeCodes(true);
	protected Map<String, DocumentDetails> docDetailMap = null;

	private FinanceMain financeMain;
	// private boolean managerChequeType = false;

	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();

	private CustomerDetailsService customerDetailsService = null;
	private CustomerService customerService = null;
	// private PFFCustomerPreparation pffCustomerPreparation = null;//AHB
	private FinanceDetailService financeDetailService = null;

	private boolean isReprint = false;
	private boolean isCancel = false;
	private boolean isManagerCheque = false;
	private MCType mcType = null;

	private final String crTranCode = "943";
	private final String drTranCode = "443";
	private final String labeldef = "label_";

	private final String managerChequeRptName = "ManagerChq_Draft";
	private final String cancelChequeRptName = "ManagerChq_Cancel";

	private boolean isRecordSave = true;
	private final int chqNoMinlength = 8;

	private int ccyEditField = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);// AHB
	private Date postDate = DateUtility.getAppDate();

	/**
	 * default constructor.<br>
	 */
	public ManagerChequeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ManagerChequeDialog";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected ManagerCheque object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ManagerChequeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ManagerChequeDialog);
		try {
			
			this.managerCheque = (ManagerCheque) arguments.get("managerCheque");
			this.financeMain = (FinanceMain) arguments.get("financeMain");
			this.managerChequeListCtrl = (ManagerChequeListCtrl) arguments.get("managerChequeListCtrl");
			this.enqiryModule = (Boolean) arguments.get("enqiryModule");
			this.mcType = (MCType) arguments.get("mcType");


			ManagerCheque befImage = new ManagerCheque();
			BeanUtils.copyProperties(this.managerCheque, befImage);
			this.managerCheque.setBefImage(befImage);
			
			mcType = (MCType) arguments.get("mcType");
			this.label_ManagerChequeDialog_title.setValue(mcType.getTitle());

			if (MCType.MC == mcType || (enqiryModule && !managerCheque.isReprint() && !managerCheque.isCancel())) {
				isManagerCheque = true;
			} else if (MCType.RPMC == mcType || (enqiryModule && managerCheque.isReprint())) {
				isReprint = true;
			} else if (MCType.CMC == mcType || (enqiryModule && managerCheque.isCancel())) {
				isCancel = true;
			}

			doLoadWorkFlow(this.managerCheque.isWorkflow(), this.managerCheque.getWorkflowId(),
					this.managerCheque.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(managerCheque);
			
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		displayComponents(ScreenCTL.SCRN_GNEDT);
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
	public void onClick$btnCancel(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doWriteBeanToComponents(this.managerCheque.getBefImage());
		displayComponents(ScreenCTL.SCRN_GNINT);
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
		MessageUtil.showHelpWindow(event, window_ManagerChequeDialog);
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
					getNotes("ManagerCheque", String.valueOf(managerCheque.getChequeID()), managerCheque
							.getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$print(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		String reportName = "";
		if (isManagerCheque) {
			reportName = managerChequeRptName;
		} else if (isReprint) {
			reportName = managerChequeRptName;
		} else if (isCancel) {
			reportName = cancelChequeRptName;
		}
		doPrintManagerCheque(reportName, getManagerChequeForPrint());

		logger.debug("Leaving" + event.toString());
	}

	private ManagerCheque getManagerChequeForPrint() throws InterruptedException {

		doSetValidation();
		final ManagerCheque printManagerCheque = new ManagerCheque();
		BeanUtils.copyProperties(managerCheque, printManagerCheque);
		doWriteComponentsToBean(printManagerCheque);
		return printManagerCheque;
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintCancelChq(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final ManagerCheque printManagerCheque = getManagerChequeForPrint();
		printManagerCheque.setChequeNo(this.oldChequeNo.getValue());

		doPrintManagerCheque(cancelChequeRptName, printManagerCheque);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @param event
	 */
	public void onFulfill$fundingCcy(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = fundingCcy.getObject();
		if (dataObject instanceof String) {
			this.fundingCcy.setValue(dataObject.toString());
			this.fundingCcy.setDescription("");
			// this.fundingAccount.setFinanceDetails(AccountSelectionBox.NOTAPPLICABLE,
			// AccountSelectionBox.MGRCHQ_DR_FIN_EVENT, AccountSelectionBox.NOTAPPLICABLE);//AHB
			// this.nostroAccount.setFinanceDetails(AccountSelectionBox.NOTAPPLICABLE,
			// AccountSelectionBox.MGRCHQ_CR_FIN_EVENT, AccountSelectionBox.NOTAPPLICABLE);//AHB
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				ccyEditField = details.getCcyEditField();
				// this.fundingAccount.setFinanceDetails(AccountSelectionBox.NOTAPPLICABLE,
				// AccountSelectionBox.MGRCHQ_DR_FIN_EVENT, details.getCcyCode());//AHB
				// this.nostroAccount.setFinanceDetails(AccountSelectionBox.NOTAPPLICABLE,
				// AccountSelectionBox.MGRCHQ_CR_FIN_EVENT, details.getCcyCode());//AHB
			}
		}
		this.nostroAccount.setFormatter(ccyEditField);
		this.fundingAccount.setFormatter(ccyEditField);
		this.chequeAmount.setScale(ccyEditField);
		this.chargeAmount.setScale(ccyEditField);
		this.fundingAmount.setScale(ccyEditField);
		this.chequeAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.chargeAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.fundingAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnNew_DocumentDetails(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		createNewDocument(null, "");
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onChange$custCIF(Event event) {
		logger.debug("Entering" + event.toString());

		Customer customer = null;
		CustomerDetails customerDetails = null;
		// Reset all CustCIF depended values
		doResetValues();
		String customerCIF = StringUtils.trimToEmpty(this.custCIF.getValue());
		if (StringUtils.isNotEmpty(customerCIF)) {
			// Get the data of Customer from Data Base
			customer = (Customer) PennantAppUtil.getCustomerObject(customerCIF, null);
			if (customer == null) {
				// Get the data of Customer from Core Banking Customer
				customerDetails = doGetCustomerDetails(customerCIF);
				if (customerDetails == null) {
					this.custCIF.setValue("");
				} else {
					this.custShrtName.setValue(customerDetails.getCustomer().getCustShrtName());
					// this.beneficiaryName.setValue(customerDetails.getCustomer().getCustFName());
					this.draftCcy.setValue(customerDetails.getCustomer().getCustBaseCcy());
					this.draftCcy.setDescription(CurrencyUtil.getCcyDesc(customerDetails.getCustomer().getCustBaseCcy()));
					this.fundingCcy.setValue(customerDetails.getCustomer().getCustBaseCcy());
					this.fundingCcy.setDescription(CurrencyUtil.getCcyDesc(customerDetails.getCustomer().getCustBaseCcy()));
					this.branchCode.setValue(customerDetails.getCustomer().getCustDftBranch(), customerDetails
							.getCustomer().getLovDescCustDftBranchName());
					this.fundingAccount.setCustCIF(customerDetails.getCustomer().getCustCIF());
					this.fundingAccount.setBranchCode(customerDetails.getCustomer().getCustDftBranch());
					this.nostroAccount.setCustCIF(customerDetails.getCustomer().getCustCIF());
					this.nostroAccount.setBranchCode(customerDetails.getCustomer().getCustDftBranch());
					this.narration1.setValue(customerDetails.getCustomer().getCustCIF() + " - "
							+ customerDetails.getCustomer().getCustShrtName());
				}
			} else {
				this.custShrtName.setValue(customer.getCustShrtName());
				// this.beneficiaryName.setValue(customer.getCustFName());
				this.draftCcy.setValue(customer.getCustBaseCcy());
				this.draftCcy.setDescription(CurrencyUtil.getCcyDesc(customer.getCustBaseCcy()));
				this.fundingCcy.setValue(customer.getCustBaseCcy());
				this.fundingCcy.setDescription(CurrencyUtil.getCcyDesc(customer.getCustBaseCcy()));
				this.branchCode.setValue(customer.getCustDftBranch(), customer.getLovDescCustDftBranchName());
				this.fundingAccount.setCustCIF(customer.getCustCIF());
				this.fundingAccount.setBranchCode(customer.getCustDftBranch());
				this.nostroAccount.setCustCIF(customer.getCustCIF());
				this.nostroAccount.setBranchCode(customer.getCustDftBranch());
				this.narration1.setValue(customer.getCustCIF() + " - " + customer.getCustShrtName());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Get Customer Details from Equation
	 * 
	 * @throws CustomerNotFoundException
	 */
	private CustomerDetails doGetCustomerDetails(String customerCIF) {
		logger.debug("Entering");

		CustomerDetails customerDetails = null;
		try {
			// customerDetails = getPffCustomerPreparation().getCustomerByInterface(customerCIF, "");//AHB
		} catch (Exception e) { // AHB
			logger.error("Exception: ", e);
			this.custCIF.setValue("");
			this.custShrtName.setValue("");
			// this.beneficiaryName.setValue("");
			MessageUtil.showError(Labels.getLabel("Cust_NotFound"));
		}
		logger.debug("Leaving");

		return customerDetails;
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$branchCode(Event event) throws InterruptedException {
		logger.debug("Entering");
		Object dataObject = branchCode.getObject();
		if (!(dataObject instanceof String)) {
			Branch details = (Branch) dataObject;
			if (details != null) {
				// Specific to AIB TODO
				/*
				 * if(details.getBranchCode().equals("2010")){ this.cmtCcy.setValue("USD"); }else{
				 * this.cmtCcy.setValue("BHD"); }
				 */
				// this.cmtCcy.getValidatedValue();
				this.fundingAccount.setBranchCode(details.getBranchCode());
				this.nostroAccount.setBranchCode(details.getBranchCode());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * @param event
	 */
	public void onFulfill$nostroAccount(Event event) {
		logger.debug("Entering" + event.toString());

		doSetNostroFullName();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the Nostro Account Full Name
	 */
	private void doSetNostroFullName() {
		logger.debug("Entering");

		if (this.nostroAccount.getSelectedAccount() != null) {
			this.nostroFullName.setValue(this.nostroAccount.getSelectedAccount().getAcShortName());
		}

		logger.debug("Leaving");
	}

	/**
	 * @param event
	 */
	public void onSelect$accountingTab(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		ManagerCheque accManagerCheque = new ManagerCheque();
		doSetValidation();
		doWriteComponentsToBean(accManagerCheque);

		accountingListRenderer(getPostingDataSets());

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnPrintAccounting" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintAccounting(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		String usrName = getUserWorkspace().getUserDetails().getUsername();
		List<Object> list = null;

		list = new ArrayList<Object>();
		List<TransactionDetail> accountingDetails = new ArrayList<TransactionDetail>();
		for (ReturnDataSet dataSet : getPostingDataSets()) {
			TransactionDetail detail = new TransactionDetail();
			detail.setEventCode(dataSet.getFinEvent());
			detail.setEventDesc(dataSet.getLovDescEventCodeName());
			detail.setTranType("C".equals(dataSet.getDrOrCr()) ? "Credit" : "Debit");
			detail.setTransactionCode(dataSet.getTranCode());
			detail.setTransDesc(dataSet.getTranDesc());
			detail.setCcy(dataSet.getAcCcy());
			detail.setAccount(PennantApplicationUtil.formatAccountNumber(dataSet.getAccount()));
			detail.setPostAmount(PennantAppUtil.amountFormate(dataSet.getPostAmount(), dataSet.getFormatter()));
			accountingDetails.add(detail);
		}

		if (!accountingDetails.isEmpty()) {
			list.add(accountingDetails);
		}

		final ManagerCheque acManagerCheque = new ManagerCheque();

		doSetValidation();
		doWriteComponentsToBean(acManagerCheque);

		if (financeMain != null) {
			acManagerCheque.setCustCIF(financeMain.getLovDescCustCIF());
			acManagerCheque.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
			acManagerCheque.setBranchCode(financeMain.getFinBranch());
			acManagerCheque.setLovDescBranchDesc(financeMain.getLovDescFinBranchName());
			acManagerCheque.setFinReference(financeMain.getFinReference());
			acManagerCheque.setFinType(financeMain.getFinType());
			acManagerCheque.setLovDescFinTypeName(financeMain.getLovDescFinTypeName());
		}

		ReportGenerationUtil.generateReport("MGRCHQ_PostingDetail", acManagerCheque, list, true, 1, usrName,
				this.window_ManagerChequeDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * @param event
	 */
	public void onChange$chequeNo(Event event) {
		logger.debug("Entering" + event.toString());

		String chequeNo = this.chequeNo.getValue().trim();
		if (StringUtils.isNotEmpty(chequeNo) && chequeNo.length() < chqNoMinlength) {
			while (chequeNo.length() < chqNoMinlength) {
				chequeNo = 0 + chequeNo;
			}
			this.chequeNo.setValue(chequeNo);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * @param event
	 */
	public void onFulfill$chequeAmount(Event event) {
		logger.debug("Entering" + event.toString());

		doCalculateFundingAmt();

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$chargeAmount(Event event) {
		logger.debug("Entering" + event.toString());

		doCalculateFundingAmt();

		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aManagerCheque
	 * @throws InterruptedException
	 */
	public void doShowDialog(ManagerCheque aManagerCheque) throws InterruptedException {
		logger.debug("Entering");

		// if aManagerCheque == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aManagerCheque == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aManagerCheque = getManagerChequeService().getNewManagerCheque();

			this.managerCheque = aManagerCheque;
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(aManagerCheque);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqiryModule, isWorkFlowEnabled(), aManagerCheque.isNewRecord()));

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode) {
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.custCIF, this.chequeNo));

		if (financeMain != null) {
			if (isManagerCheque && !enqiryModule) {
				setComponentAccessType("ManagerChequeDialog_CustCIF", true, this.custCIF, null, this.label_CustCIF,
						this.hlayout_CustCIF, null);
				setComponentAccessType("ManagerChequeDialog_ChqPurposeCode", true, this.chqPurposeCode,
						this.space_ChqPurposeCode, this.label_ChqPurposeCode, this.hlayout_ChqPurposeCode, null);
				setExtAccess("ManagerChequeDialog_DraftCcy", true, this.draftCcy, this.row2);
				setExtAccess("ManagerChequeDialog_FundingCcy", true, this.fundingCcy, this.row3);
				setComponentAccessType("ManagerChequeDialog_NostroFullName", true, this.nostroFullName,
						this.space_NostroFullName, this.label_NostroFullName, this.hlayout_NostroFullName, null);
				this.nostroAccount.setReadonly(true);
				// this.fundingAccount.setReadonly(true);
			}
			this.row0.setVisible(false);
		} else {
			this.availableAmt.setVisible(false);
			this.label_AvailableAmt.setVisible(false);
			this.disbursedAmt.setVisible(false);
			this.label_DisbursedAmt.setVisible(false);
		}
		this.btnDelete.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly = readOnly;
		if (readOnly){
			tempReadOnly = true;
		}else if (PennantConstants.RECORD_TYPE_DEL.equals(this.managerCheque.getRecordType())) {
			tempReadOnly = true;
		}
		if (isReprint || isCancel || enqiryModule) {
			doSetReadOnly(true);
			this.chequeAmount.setReadonly(true);
			this.chargeAmount.setReadonly(true);
			this.fundingAccount.setReadonly(true);
			this.nostroAccount.setReadonly(true);
		} else {
			doSetReadOnly(tempReadOnly);
			this.fundingAccount.setReadonly(isReadOnly("ManagerChequeDialog_FundingAccount"));
			this.nostroAccount.setReadonly(isReadOnly("ManagerChequeDialog_NostroAccount"));
			this.chequeAmount.setReadonly(isReadOnly("ManagerChequeDialog_ChequeAmount"));
			this.chargeAmount.setReadonly(isReadOnly("ManagerChequeDialog_ChargeAmount"));
		}
		setComponentAccessType("ManagerChequeDialog_IssueDate", true, this.issueDate, null, this.label_IssueDate,
				this.hlayout_IssueDate, null);
		this.availableAmt.setReadonly(true);
		this.disbursedAmt.setReadonly(true);
		this.fundingAmount.setReadonly(true);

		if (isCancel || enqiryModule) {
			setComponentAccessType("ManagerChequeDialog_ChequeNo", true, this.chequeNo, this.space_ChequeNo,
					this.label_ChequeNo, this.hlayout_ChequeNo, null);
			setComponentAccessType("ManagerChequeDialog_AddressLine1", tempReadOnly, this.addressLine1, null,
					this.label_AddressLine1, this.hlayout_AddressLine1, null);
			setComponentAccessType("ManagerChequeDialog_AddressLine2", tempReadOnly, this.addressLine2, null,
					this.label_AddressLine2, this.hlayout_AddressLine2, null);
			setComponentAccessType("ManagerChequeDialog_AddressLine3", tempReadOnly, this.addressLine3, null,
					this.label_AddressLine3, this.hlayout_AddressLine3, null);
			setComponentAccessType("ManagerChequeDialog_AddressLine4", tempReadOnly, this.addressLine4, null,
					this.label_AddressLine4, this.hlayout_AddressLine4, null);
			setComponentAccessType("ManagerChequeDialog_AddressLine5", tempReadOnly, this.addressLine5, null,
					this.label_AddressLine5, this.hlayout_AddressLine5, null);
		} else {
			setComponentAccessType("ManagerChequeDialog_ChequeNo", tempReadOnly, this.chequeNo, this.space_ChequeNo,
					this.label_ChequeNo, this.hlayout_ChequeNo, null);
		}
		if (this.chequeAmount.isReadonly() || this.chequeAmount.isDisabled()) {
			this.chequeAmount.setMandatory(false);
		} else {
			this.chequeAmount.setMandatory(true);
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	private void doSetReadOnly(boolean readOnly) {
		logger.debug("Entering");

		// setComponentAccessType("ManagerChequeDialog_ChequeRef", tempReadOnly, this.chequeRef, this.space_ChequeRef,
		// this.label_ChequeRef, this.hlayout_ChequeRef,null);
		// setRowInvisible(this.row, this.hlayout_ChequeRef, null);

		setComponentAccessType("ManagerChequeDialog_CustCIF", readOnly, this.custCIF, null, this.label_CustCIF,
				this.hlayout_CustCIF, null);
		setExtAccess("ManagerChequeDialog_BranchCode", readOnly, this.branchCode, this.row0);

		setComponentAccessType("ManagerChequeDialog_BeneficiaryName", readOnly, this.beneficiaryName,
				this.space_BeneficiaryName, this.label_BeneficiaryName, this.hlayout_BeneficiaryName, null);
		setComponentAccessType("ManagerChequeDialog_ChqPurposeCode", readOnly, this.chqPurposeCode,
				this.space_ChqPurposeCode, this.label_ChqPurposeCode, this.hlayout_ChqPurposeCode, null);
		setRowInvisible(this.row1, this.hlayout_BeneficiaryName, this.hlayout_ChqPurposeCode);

		setExtAccess("ManagerChequeDialog_DraftCcy", readOnly, this.draftCcy, this.row2);
		setExtAccess("ManagerChequeDialog_FundingCcy", readOnly, this.fundingCcy, this.row3);
		setRowInvisible(this.row3, null, null);// AHB

		setComponentAccessType("ManagerChequeDialog_ValueDate", readOnly, this.valueDate, this.space_ValueDate,
				this.label_ValueDate, this.hlayout_ValueDate, null);
		setRowInvisible(this.row5, this.hlayout_ValueDate, null);

		setComponentAccessType("ManagerChequeDialog_NostroFullName", readOnly, this.nostroFullName,
				this.space_NostroFullName, this.label_NostroFullName, this.hlayout_NostroFullName, null);
		setRowInvisible(this.row6, null, this.hlayout_NostroFullName);// AHB

		setComponentAccessType("ManagerChequeDialog_Narration1", readOnly, this.narration1, null,
				this.label_Narration1, this.hlayout_Narration1, null);
		setComponentAccessType("ManagerChequeDialog_Narration2", readOnly, this.narration2, null,
				this.label_Narration2, this.hlayout_Narration2, null);
		setRowInvisible(this.row7, this.hlayout_Narration1, this.hlayout_Narration2);

		logger.debug("Leaving");
	}

	/**
	 * Method for Filling the Basic Finance Details
	 */
	private void doFillFinanceBasicDetails() {
		logger.debug("Entering");
		int format=CurrencyUtil.getFormat(this.financeMain.getFinCcy());

		// Label Names
		this.label_ManagerChequeDialog_FinType.setValue(Labels.getLabel("label_ManagerChequeDialog_FinType.value"));
		this.label_ManagerChequeDialog_FinReference.setValue(Labels
				.getLabel("label_ManagerChequeDialog_FinReference.value"));
		this.label_ManagerChequeDialog_CustomerCIF.setValue(Labels
				.getLabel("label_ManagerChequeDialog_CustomerCIF.value"));
		this.label_ManagerChequeDialog_Currency.setValue(Labels.getLabel("label_ManagerChequeDialog_Currency.value"));
		this.label_ManagerChequeDialog_FinBranch.setValue(Labels.getLabel("label_ManagerChequeDialog_FinBranch.value"));
		this.label_ManagerChequeDialog_GrcEndDate.setValue(Labels
				.getLabel("label_ManagerChequeDialog_GrcEndDate.value"));
		this.label_ManagerChequeDialog_StartDate.setValue(Labels.getLabel("label_ManagerChequeDialog_StartDate.value"));
		this.label_ManagerChequeDialog_MaturityDate.setValue(Labels
				.getLabel("label_ManagerChequeDialog_MaturityDate.value"));
		this.label_ManagerChequeDialog_FinAmount.setValue(Labels.getLabel("label_ManagerChequeDialog_FinAmount.value"));
		this.label_ManagerChequeDialog_DownPayments.setValue(Labels
				.getLabel("label_ManagerChequeDialog_DownPayments.value"));
		this.label_ManagerChequeDialog_Fees.setValue(Labels.getLabel("label_ManagerChequeDialog_Fees.value"));
		this.label_ManagerChequeDialog_TotalProfitAmt.setValue(Labels
				.getLabel("label_ManagerChequeDialog_TotalProfitAmt.value"));
		this.label_ManagerChequeDialog_DisbursementAmt.setValue(Labels
				.getLabel("label_ManagerChequeDialog_DisbursementAmt.value"));
		this.label_ManagerChequeDialog_NetEffectiveRate.setValue(Labels
				.getLabel("label_ManagerChequeDialog_NetEffectiveRate.value"));

		// Label Values
		this.managerChq_finType.setValue(this.financeMain.getFinType() + " - "
				+ this.financeMain.getLovDescFinTypeName());
		this.managerChq_finReference.setValue(this.financeMain.getFinReference());
		this.managerChq_CustCIF.setValue(this.financeMain.getLovDescCustCIF() + " - "
				+ this.financeMain.getLovDescCustShrtName());
		this.managerChq_Currency.setValue(this.financeMain.getFinCcy());
		this.managerChq_FinBranch.setValue(this.financeMain.getFinBranch() + " - "
				+ this.financeMain.getLovDescFinBranchName());
		this.managerChq_grcEndDate.setValue(DateUtility.formatToLongDate(this.financeMain.getGrcPeriodEndDate()));
		this.managerChq_startDate.setValue(DateUtility.formatToLongDate(this.financeMain.getFinStartDate()));
		this.managerChq_maturityDate.setValue(DateUtility.formatToLongDate(this.financeMain.getMaturityDate()));
		this.managerChq_FinAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.managerChq_FinAmount.setValue(PennantAppUtil.formateAmount(this.financeMain.getFinAmount(),
				format));
		this.managerChq_DownPayments.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.managerChq_DownPayments.setValue(PennantAppUtil.formateAmount(this.financeMain.getDownPayment(),
				format));
		this.managerChq_Fees.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.managerChq_Fees.setValue(PennantAppUtil.formateAmount(this.financeMain.getFeeChargeAmt(),
				format));
		this.managerChq_TotalProfitAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.managerChq_TotalProfitAmt.setValue(PennantAppUtil.formateAmount(this.financeMain.getTotalProfit(),
				format));

		// this.managerChq_DisbursementAmt.setValue(financeMain.getFinAmount() - financeMain.getDownPayment() +
		// financeMain.getFeeChargeAmt());
		this.managerChq_DisbursementAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.managerChq_DisbursementAmt.setValue(PennantAppUtil.formateAmount(
				this.financeMain.getLovDescFinancingAmount(), format));

		if (this.financeMain.getEffectiveRateOfReturn() == null) {
			this.financeMain.setEffectiveRateOfReturn(BigDecimal.ZERO);
		}
		this.managerChq_NetEffectiveRate.setValue(PennantApplicationUtil.formatRate(this.financeMain
				.getEffectiveRateOfReturn().doubleValue(), PennantConstants.rateFormate)
				+ "%");

		logger.debug("Leaving");
	}

	/**
	 * Method for Filling the Basic ManagerCheque Details
	 */
	private void doFillManagerChequeDetails(Boolean newRecord) {
		logger.debug("Entering");
		int format=CurrencyUtil.getFormat( this.financeMain.getFinCcy());
		BigDecimal disbursedAmt = getManagerChequeService().getTotalChqAmtByFinReference(
				this.financeMain.getFinReference());
		if (disbursedAmt == null) {
			disbursedAmt = BigDecimal.ZERO;
		}
		if (newRecord) {
			// this.custCIF.setValue(Long.toString(this.financeMain.getCustID()));
			this.custCIF.setValue(this.financeMain.getLovDescCustCIF());
			this.branchCode.setValue(this.financeMain.getFinBranch());
			this.branchCode.setDescription(this.financeMain.getLovDescFinBranchName());
			this.beneficiaryName.setValue(StringUtils.trimToEmpty(this.financeMain.getLovDescCustFName())
					+ StringUtils.trimToEmpty(this.financeMain.getLovDescCustLName()));
			this.draftCcy.setValue(this.financeMain.getFinCcy(), CurrencyUtil.getCcyDesc(this.financeMain.getFinCcy()));
			this.fundingCcy.setValue(this.financeMain.getFinCcy(), CurrencyUtil.getCcyDesc(this.financeMain.getFinCcy()));
			this.fundingAccount.setValue(this.financeMain.getDisbAccountId());
			this.fundingAccount.setCustCIF(this.financeMain.getLovDescCustCIF());
			this.fundingAccount.setBranchCode(this.financeMain.getFinBranch());
			this.nostroAccount.setCustCIF(this.financeMain.getLovDescCustCIF());
			this.nostroAccount.setBranchCode(this.financeMain.getFinBranch());
			this.nostroAccount.setValue(SysParamUtil.getValueAsString("FIN_MGRCHQ_NOSTROACC")); // ### 12-01-2016 Ticket
																								// ID : 124096
			this.narration1.setValue(this.financeMain.getLovDescCustCIF() + " - "
					+ this.financeMain.getLovDescCustShrtName());
			this.narration2.setValue(this.financeMain.getFinReference());
			this.chequeAmount.setValue(PennantAppUtil.formateAmount(
					this.financeMain.getFinAmount().subtract(disbursedAmt), format));
		}
		this.availableAmt.setScale(ccyEditField);
		this.disbursedAmt.setScale(ccyEditField);
		this.availableAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.disbursedAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.availableAmt.setValue(PennantAppUtil.formateAmount(this.financeMain.getFinAmount().subtract(disbursedAmt),
				format));
		this.disbursedAmt
				.setValue(PennantAppUtil.formateAmount(disbursedAmt, format));

		logger.debug("Leaving");
	}

	/**
	 * Method for Filling the Basic ManagerCheque Details
	 */
	private void doFillAccountingBasicDetails() {
		logger.debug("Entering");

		this.acc_ManagerChq_finType.setValue(this.financeMain.getFinType() + " - "
				+ this.financeMain.getLovDescFinTypeName());
		this.acc_ManagerChq_Currency.setValue(this.financeMain.getFinCcy());
		this.acc_ManagerChq_RepaymantSchMethod.setValue(this.financeMain.getScheduleMethod());
		this.acc_ManagerChq_ProfitDaysBasis.setValue(this.financeMain.getProfitDaysBasis());
		this.acc_ManagerChq_FinReference.setValue(this.financeMain.getFinReference());
		this.acc_ManagerChq_GrcEndDate.setValue(DateUtility.formatToLongDate(this.financeMain.getGrcPeriodEndDate()));

		logger.debug("Leaving");

	}

	/**
	 * Method for Append Recommend Details Tab
	 * 
	 * @throws InterruptedException
	 */
	private void appendRecommendDetailTab() throws InterruptedException {
		logger.debug("Entering");

		// Memo Tab Details -- Comments or Recommendations
		// this.btnNotes.setVisible(false);

		Tab tab = new Tab("Recommendations");
		tab.setId("memoDetailTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("memoDetailTabPanel");
		tabpanel.setHeight(this.borderLayoutHeight + "px");
		tabpanel.setStyle("overflow:auto");
		tabpanel.setParent(tabpanelsBoxIndexCenter);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("isFinanceNotes", true); // TODO
		// map.put("isRecommendMand", true);
		map.put("notes", getNotes(this.managerCheque));
		map.put("control", this);

		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", tabpanel, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get all the Transaction/Posting Data sets
	 * 
	 * @return List<ReturnDataSet>
	 */
	public List<ReturnDataSet> getPostingDataSets() {
		logger.debug("Entering");
		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>();

		if (isManagerCheque) {
			returnDataSets.add(doPreparePostings("MGRCHQ", true, "D", 1));
			returnDataSets.add(doPreparePostings("MGRCHQ", false, "C", 2));
		} else if (isCancel) {
			returnDataSets.add(doPreparePostings("CANCHQ", true, "C", 1));
			returnDataSets.add(doPreparePostings("CANCHQ", false, "D", 2));
		} else if (isReprint) {
			returnDataSets.add(doPreparePostings("CANCHQ", true, "C", 1));
			returnDataSets.add(doPreparePostings("CANCHQ", false, "D", 2));
			returnDataSets.add(doPreparePostings("MGRCHQ", true, "D", 3));
			returnDataSets.add(doPreparePostings("MGRCHQ", false, "C", 4));
		}
		logger.debug("Leaving");
		return returnDataSets;
	}

	/**
	 * Prepare the postings details
	 * 
	 * @param finEvent
	 * @param isfundingAccount
	 * @param drOrCr
	 * @param tranOrder
	 * @return ReturnDataSet
	 */
	private ReturnDataSet doPreparePostings(String finEvent, boolean isfundingAccount, String drOrCr, int tranOrder) {
		logger.debug("Entering");
		ReturnDataSet postings = new ReturnDataSet();

		postings.setFinEvent(finEvent);
		postings.setTransOrder(tranOrder);
		postings.setTranOrderId(String.valueOf(tranOrder));
		int decPos = 0;

		if (isfundingAccount) {
			postings.setAccountType("DISB");
			postings.setTranDesc(Labels.getLabel("label_ManagerChequeDialog_FundingAccount.value"));
			postings.setAccount(PennantApplicationUtil.unFormatAccountNumber(this.fundingAccount.getValue()));
			postings.setAcCcy(this.fundingCcy.getValidatedValue());
			decPos = this.fundingCcy.getObject() == null ? 3 : ((Currency) this.fundingCcy.getObject())
					.getCcyEditField();
		} else {
			postings.setAccountType("MNGR");
			postings.setTranDesc(Labels.getLabel("label_ManagerChequeDialog_NostroAccount.value"));
			postings.setAccount(PennantApplicationUtil.unFormatAccountNumber(this.nostroAccount.getValue()));
			postings.setAcCcy(this.draftCcy.getValidatedValue());
			decPos = this.fundingCcy.getObject() == null ? 3 : ((Currency) this.fundingCcy.getObject())
					.getCcyEditField();
		}
		BigDecimal postAmount = PennantApplicationUtil.unFormateAmount(this.chequeAmount.getActualValue(), decPos);
		postings.setPostAmount(postAmount);

		postings.setDrOrCr(drOrCr);
		if ("D".equals(drOrCr)) {
			postings.setTranCode(drTranCode);
			postings.setRevTranCode(crTranCode);
		} else {
			postings.setTranCode(crTranCode);
			postings.setRevTranCode(drTranCode);
		}

		postings.setCustCIF(this.custCIF.getValue());
		postings.setPostref(this.chequeNo.getValue());
		postings.setFinReference(this.chequeNo.getValue());
		postings.setPostBranch(this.branchCode.getValidatedValue());
		postings.setPostDate(postDate);
		postings.setValueDate(this.valueDate.getValue());

		postings.setAmountType("D");
		postings.setFlagCreateIfNF(false);
		postings.setFlagCreateNew(false);
		postings.setInternalAc(false);
		postings.setShadowPosting(false);
		postings.setPostStatus("");
		postings.setErrorId("");
		postings.setErrorMsg("");

		logger.debug("Leaving");
		return postings;
	}

	/**
	 * Render the Accounting Details
	 * 
	 * @param returnDataSets
	 */
	private void accountingListRenderer(List<ReturnDataSet> returnDataSets) {
		logger.debug("Entering");

		String eventCode = null;
		this.listBoxFinAccountings.getItems().clear();
		for (ReturnDataSet returnDataSet : returnDataSets) {
			if (!returnDataSet.getFinEvent().equals(eventCode)) {
				eventCode = returnDataSet.getFinEvent();
				Listgroup listGroup = new Listgroup(Labels.getLabel(labeldef + returnDataSet.getFinEvent()));
				listGroup.setParent(listBoxFinAccountings);

			}

			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell("D".equals(returnDataSet.getDrOrCr()) ? "Debit" : "Credit");
			lc.setParent(item);
			lc = new Listcell(
					"DISB".equals(returnDataSet.getAccountType()) ? Labels
							.getLabel("label_ManagerChequeDialog_FundingAccount.value") : Labels
							.getLabel("label_ManagerChequeDialog_NostroAccount.value"));
			lc.setParent(item);
			lc = new Listcell(returnDataSet.getTranCode());
			lc.setParent(item);
			lc = new Listcell(returnDataSet.getAccountType());
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.formatAccountNumber(returnDataSet.getAccount()));
			lc.setParent(item);
			lc = new Listcell(returnDataSet.getAcCcy());
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(returnDataSet.getPostAmount(), ccyEditField));
			lc.setStyle("font-weight:bold;text-align:right;");
			lc.setParent(item);

			lc = new Listcell("");
			lc.setParent(item);
			this.listBoxFinAccountings.appendChild(item);

		}
		logger.debug("Leaving");
	}

	public void createNewDocument(Listitem listitem, Object docType) throws InterruptedException {
		logger.debug("Entering");

		DocumentDetails documentDetails = new DocumentDetails();
		documentDetails.setDocModule("ManagerCheques");
		documentDetails.setNewRecord(true);
		documentDetails.setWorkflowId(0);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finDocumentDetail", documentDetails);
		map.put("DocumentDialogCtrl", this);
		map.put("financeMain", this.financeMain);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("listitem", listitem);
		map.put("docType", docType);
		map.put("documentType", getDefaultDocumentType());

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/ManagerCheque/DocumentDialog.zul",
					window_ManagerChequeDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Calculate Funding Amount. <br>
	 */
	private void doCalculateFundingAmt() {
		logger.debug("Entering");

		BigDecimal chequeAmt = this.chequeAmount.getActualValue() == null ? BigDecimal.ZERO : this.chequeAmount
				.getActualValue();
		BigDecimal chargeAmt = this.chargeAmount.getActualValue() == null ? BigDecimal.ZERO : this.chargeAmount
				.getActualValue();
		this.fundingAmount.setValue(chequeAmt.add(chargeAmt));

		// String fundingAmt;
		// fundingAmt = chequeAmt.add(chargeAmt).toString();
		// this.fundingAmount.setValue(fundingAmt.substring(0, fundingAmt.lastIndexOf('.')));
		// this.fundingAmount.setValue(chequeAmt.add(chargeAmt).stripTrailingZeros());

		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	private void doResetValues() {
		logger.debug("Entering");

		// Clear the values
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();
		this.custShrtName.setValue("");
		// this.beneficiaryName.setValue("");
		this.draftCcy.setValue("", "");
		this.fundingCcy.setValue("", "");
		this.branchCode.setValue("", "");
		this.fundingAccount.setValue("");
		this.fundingAccount.setBranchCode("");
		this.fundingAccount.setCustCIF("");
		this.narration1.setValue("");

		// ### 12-01-2016 - Start - Ticket ID : 124096
		// this.nostroAccount.setValue("");
		// this.nostroAccount.setBranchCode("");
		// this.nostroAccount.setCustCIF("");
		// this.nostroFullName.setValue("");
		// ### 12-01-2016 - End

		logger.debug("Leaving");
	}

	/**
	 * Method for Printing the Cheque Details
	 * 
	 * @throws Exception
	 */
	private void doPrintManagerCheque(String reportName, ManagerCheque printManagerCheque) throws Exception {
		logger.debug("Entering");

		SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
		String usrName = (securityUser.getUsrFName().trim() + " " + securityUser.getUsrMName().trim() + " " + securityUser
				.getUsrLName()).trim();

		printManagerCheque.setIssueDate(DateUtility.getAppDate());
		printManagerCheque.setTodayDate(DateUtility.formatDate(this.valueDate.getValue(), PennantConstants.dateFormat)
				.replace("/", ""));
		printManagerCheque.setFundingAccount(this.fundingAccount.getValue());
		printManagerCheque.setNostroAccount(this.nostroAccount.getValue());
		printManagerCheque.setLoginUsrName(usrName);
		printManagerCheque.setAmtInLocalCcy(PennantAppUtil.unFormateAmount(
				CalculationUtil.getConvertedAmount(this.fundingCcy.getValue(),
						SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY), this.chequeAmount.getActualValue()),
				ccyEditField)); // TODO
		printManagerCheque.setAmtInWords(NumberToEnglishWords.getAmountInText(
				PennantAppUtil.formateAmount(printManagerCheque.getAmtInLocalCcy(), ccyEditField),
				SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY)));// AHB

		ReportGenerationUtil.generateReport(reportName, printManagerCheque, new ArrayList<Object>(), true, 1, usrName,
				this.window_ManagerChequeDialog);

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
		getUserWorkspace().allocateAuthorities("ManagerChequeDialog", getRole());
		if (!enqiryModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ManagerChequeDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ManagerChequeDialog_btnEdit"));
			// this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ManagerChequeDialog_btnDelete"));
			//this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ManagerChequeDialog_btnSave"));
			this.btnNew_DocumentDetails.setVisible(getUserWorkspace().isAllowed(
					"button_ManagerChequeDialog_btnNew_Documents"));
			this.print.setVisible(getUserWorkspace().isAllowed("button_ManagerChequeDialog_btnPrint"));
			if (isReprint) {
				this.btnPrintCancelChq.setVisible(getUserWorkspace().isAllowed("button_ManagerChequeDialog_btnPrint"));
			}
		} else {
			// Application should allow the users to reprint the ManagerCheques with the same serial number using the
			// menu option ManagerCheque Enquiry.
			if (isManagerCheque) {
				this.print.setVisible(getUserWorkspace().isAllowed("button_ManagerChequeDialog_btnPrint"));
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.chequeRef.setMaxlength(20);
		this.chequeNo.setMaxlength(50);
		this.beneficiaryName.setMaxlength(65);
		this.custCIF.setMaxlength(12);

		this.branchCode.setMaxlength(12);
		this.branchCode.setTextBoxWidth(120);
		this.branchCode.setMandatoryStyle(true);
		this.branchCode.setModuleName("Branch");
		this.branchCode.setValueColumn("BranchCode");
		this.branchCode.setDescColumn("BranchDesc");
		this.branchCode.setValidateColumns(new String[] { "BranchCode" });

		this.draftCcy.setMaxlength(LengthConstants.LEN_CURRENCY);// AHB
		this.draftCcy.setMandatoryStyle(true);
		this.draftCcy.setTextBoxWidth(45);
		this.draftCcy.setModuleName("Currency");
		this.draftCcy.setValueColumn("CcyCode");
		this.draftCcy.setDescColumn("CcyDesc");
		this.draftCcy.setValidateColumns(new String[] { "CcyCode" });

		this.fundingCcy.setMaxlength(LengthConstants.LEN_CURRENCY);// ABH
		this.fundingCcy.setMandatoryStyle(true);
		this.fundingCcy.setTextBoxWidth(45);
		this.fundingCcy.setModuleName("Currency");
		this.fundingCcy.setValueColumn("CcyCode");
		this.fundingCcy.setDescColumn("CcyDesc");
		this.fundingCcy.setValidateColumns(new String[] { "CcyCode" });

		this.fundingAccount.setMandatoryStyle(true);
		// this.fundingAccount.setFinanceDetails(AccountSelectionBox.NOTAPPLICABLE,
		// AccountSelectionBox.MGRCHQ_DR_FIN_EVENT, AccountSelectionBox.NOTAPPLICABLE);//AHB
		this.nostroAccount.setMandatoryStyle(true);
		this.nostroFullName.setMaxlength(50);
		// this.nostroAccount.setFinanceDetails(AccountSelectionBox.NOTAPPLICABLE,
		// AccountSelectionBox.MGRCHQ_CR_FIN_EVENT, AccountSelectionBox.NOTAPPLICABLE);//AHB

		this.chequeAmount.setMandatory(true);
		this.chequeAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.chequeAmount.setScale(ccyEditField);

		this.availableAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.availableAmt.setScale(ccyEditField);

		this.disbursedAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.disbursedAmt.setScale(ccyEditField);

		this.fundingAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.fundingAmount.setScale(ccyEditField);

		this.chargeAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.chargeAmount.setScale(ccyEditField);

		// this.chargeAmount.setMaxlength(18);//AHB
		this.valueDate.setFormat(PennantConstants.dateFormat);
		this.issueDate.setFormat(PennantConstants.dateFormat);

		this.narration1.setMaxlength(50);
		this.narration2.setMaxlength(50);
		this.addressLine1.setMaxlength(50);
		this.addressLine2.setMaxlength(50);
		this.addressLine3.setMaxlength(50);
		this.addressLine4.setMaxlength(50);
		this.addressLine5.setMaxlength(50);

		// setStatusDetails(groupboxWf,groupboxWf,south,enqModule);
		if (isWorkFlowEnabled() && !enqiryModule) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0%");
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aManagerCheque
	 *            ManagerCheque
	 */
	public void doWriteBeanToComponents(ManagerCheque aManagerCheque) throws InterruptedException {
		logger.debug("Entering");

		this.chequeRef.setValue(aManagerCheque.getChequeRef());

		// Display the Old Cheque Number in case of Reprint ManagerCheque.
		if (isReprint || (enqiryModule && aManagerCheque.isReprint())) {
			if (aManagerCheque.getReprintManagerCheque() != null) {
				this.chequeNo.setValue(aManagerCheque.getReprintManagerCheque().getChequeNo());
			} else {
				this.chequeNo.setValue(aManagerCheque.getChequeNo());
			}
			this.label_OldChequeNo.setVisible(true);
			this.oldChequeNo.setValue(aManagerCheque.getChequeNo());
		} else {
			this.label_OldChequeNo.setVisible(false);
			this.oldChequeNo.setValue("");
			this.chequeNo.setValue(aManagerCheque.getChequeNo());
		}

		// Display the Stop Order Reference in Enquiry for Reprint & Cancel ManagerCheques
		if (enqiryModule && StringUtils.isNotEmpty(aManagerCheque.getStopOrderRef())
				&& (aManagerCheque.isReprint() || aManagerCheque.isCancel())) {
			this.row_StopOrderRef.setVisible(true);
			this.label_StopOrderRef.setValue(aManagerCheque.getStopOrderRef());
		}

		if (this.managerCheque.isNewRecord()) { // New Record
			this.valueDate.setValue(DateUtility.getAppDate());
			this.issueDate.setValue(DateUtility.getAppDate());

			if (financeMain != null) { // Finance ManagerCheque
				
				ccyEditField =CurrencyUtil.getFormat(this.financeMain.getFinCcy());;
				this.gb_basicDetails.setVisible(true);
				this.gb_AccDetails.setVisible(true);
				fillComboBox(this.chqPurposeCode, PennantConstants.FIN_MGRCHQ__CHQPURPOSECODE, listChqPurposeCodes, "");// AHB
				// this.chqPurposeCode.setValue(PennantStaticListUtil.getlabelDesc(PennantConstants.FIN_MGRCHQ__CHQPURPOSECODE,
				// PennantAppUtil.getChqPurposeCodes(true)));

				doFillFinanceBasicDetails();
				doFillManagerChequeDetails(true);
				doFillAccountingBasicDetails();

			} else { // Non Finance ManagerCheque
				this.gb_basicDetails.setVisible(false);
				this.gb_AccDetails.setVisible(false);
				this.custCIF.setValue(aManagerCheque.getCustCIF());
				this.branchCode.setValue(aManagerCheque.getBranchCode());
				this.branchCode.setDescription("");
				fillComboBox(this.chqPurposeCode, aManagerCheque.getChqPurposeCode(), listChqPurposeCodes, ","
						+ PennantConstants.FIN_MGRCHQ__CHQPURPOSECODE + ",");
				this.nostroAccount.setValue(SysParamUtil.getValueAsString("NONFIN_MGRCHQ_NOSTROACC")); // ### 12-01-2016
																										// Ticket ID :
																										// 124096
			}
			doSetNostroFullName(); // ### 12-01-2016 Ticket ID : 124096
		} else { // Existing Record
			fillComboBox(this.chqPurposeCode, aManagerCheque.getChqPurposeCode(), listChqPurposeCodes, "");
			ccyEditField =  CurrencyUtil.getFormat(aManagerCheque.getFundingCcy());
			this.custCIF.setValue(aManagerCheque.getCustCIF());

			if (StringUtils.isNotBlank(aManagerCheque.getCustCIF())
					&& StringUtils.isBlank(aManagerCheque.getLovDescCustShrtName())) { // Get the data of Customer from
																						// Core Banking Customer
				CustomerDetails customerDetails = doGetCustomerDetails(aManagerCheque.getCustCIF());
				if (customerDetails == null) {
					this.custCIF.setValue("");
					this.custShrtName.setValue("");
				} else {
					this.custShrtName.setValue(customerDetails.getCustomer().getCustShrtName());
				}
			} else { // Get the data of Customer from Data Base
				this.custShrtName.setValue(aManagerCheque.getLovDescCustShrtName());
			}

			this.beneficiaryName.setValue(aManagerCheque.getBeneficiaryName());
			this.valueDate.setValue(aManagerCheque.getValueDate());
			this.issueDate.setValue(aManagerCheque.getIssueDate());

			this.branchCode.setValue(aManagerCheque.getBranchCode());
			this.branchCode.setDescription(StringUtils.isBlank(aManagerCheque.getLovDescBranchDesc()) ? "" : aManagerCheque.getLovDescBranchDesc());

			
			String draftCcy =  CurrencyUtil.getCcyDesc(aManagerCheque.getDraftCcy());
			String fundCcy =  CurrencyUtil.getCcyDesc(aManagerCheque.getFundingCcy());
			this.draftCcy.setValue(aManagerCheque.getDraftCcy());
			this.draftCcy.setDescription(StringUtils.isBlank(draftCcy) ? "" : draftCcy);

			this.fundingCcy.setValue(aManagerCheque.getFundingCcy());
			this.fundingCcy.setDescription(StringUtils.isBlank(fundCcy) ? "" : fundCcy);

			this.fundingAccount.setValue(aManagerCheque.getFundingAccount());
			this.fundingAccount.setCustCIF(aManagerCheque.getCustCIF());

			this.nostroAccount.setValue(aManagerCheque.getNostroAccount());
			this.nostroFullName.setValue(aManagerCheque.getNostroFullName());
			this.nostroAccount.setCustCIF(aManagerCheque.getCustCIF());

			this.narration1.setValue(aManagerCheque.getNarration1());
			this.narration2.setValue(aManagerCheque.getNarration2());

			this.addressLine1.setValue(aManagerCheque.getAddressLine1());
			this.addressLine2.setValue(aManagerCheque.getAddressLine2());
			this.addressLine3.setValue(aManagerCheque.getAddressLine3());
			this.addressLine4.setValue(aManagerCheque.getAddressLine4());
			this.addressLine5.setValue(aManagerCheque.getAddressLine5());

			if (financeMain != null && StringUtils.isNotEmpty(aManagerCheque.getChequeRef())) { // Finance
																										// ManagerCheque
				this.gb_basicDetails.setVisible(true);
				this.gb_AccDetails.setVisible(true);

				doFillFinanceBasicDetails();
				doFillManagerChequeDetails(false);
				doFillAccountingBasicDetails();

			} else { // Non Finance ManagerCheque
				this.gb_basicDetails.setVisible(false);
				this.gb_AccDetails.setVisible(false);
			}
		}
		if (isCancel || aManagerCheque.isCancel()) {
			this.row10.setVisible(true);
			this.row11.setVisible(true); // In case of Cancel MangerCheque only Address Line rows are visible other wise
											// not visible
			this.row12.setVisible(true);
		}
		this.nostroAccount.setFormatter(ccyEditField);
		this.fundingAccount.setFormatter(ccyEditField);

		this.chequeAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.chequeAmount.setScale(ccyEditField);
		this.chequeAmount.setValue(PennantAppUtil.formateAmount(aManagerCheque.getChequeAmount(), ccyEditField));

		this.chargeAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.chargeAmount.setScale(ccyEditField);
		this.chargeAmount.setValue(PennantAppUtil.formateAmount(aManagerCheque.getChargeAmount(), ccyEditField));

		this.fundingAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyEditField));
		this.fundingAmount.setScale(ccyEditField);
		this.fundingAmount.setValue(PennantAppUtil.formateAmount(aManagerCheque.getFundingAmount(), ccyEditField));

		if (enqiryModule || isReprint || isCancel) { // For Enquiry removes 'Active' filter while getting ChqPurposeCodes
			fillComboBox(this.chqPurposeCode, aManagerCheque.getChqPurposeCode(),
					PennantAppUtil.getChqPurposeCodes(false), "");
		}

		if (!enqiryModule) {
			// Document Details Tab
			doFillDocumentDetails(aManagerCheque.getDocumentDetailsList());

			// Recommend & Comments Details Tab Addition
			appendRecommendDetailTab();
		} else {
			// Enquiry Document Details Tab
			this.listBoxDocumentEnquiry.setVisible(true);
			this.listBoxDocumentDetails.setVisible(false);
			doFillEnqDocumentDetails(aManagerCheque.getDocumentDetailsList());
		}

		this.recordStatus.setValue(aManagerCheque.getRecordStatus());
		// this.recordType.setValue(PennantJavaUtil.getLabel(aManagerCheque.getRecordType()));

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aManagerCheque
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(ManagerCheque aManagerCheque) throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Cheque Purpose
		try {
			String strChqPurposeCode = null;
			if (this.chqPurposeCode.getSelectedItem() != null) {
				strChqPurposeCode = this.chqPurposeCode.getSelectedItem().getValue().toString();
			}
			if (strChqPurposeCode != null && !PennantConstants.List_Select.equals(strChqPurposeCode)) {
				aManagerCheque.setChqPurposeCode(strChqPurposeCode);
				aManagerCheque.setChqPurposeCodeName(PennantStaticListUtil.getlabelDesc(strChqPurposeCode,
						PennantAppUtil.getChqPurposeCodes(false)));
			} else {
				aManagerCheque.setChqPurposeCode(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cheque Reference
		try {
			if (this.financeMain != null) {
				aManagerCheque.setChequeRef(this.managerChq_finReference.getValue());
			} else {
				aManagerCheque.setChequeRef("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Branch
		try {
			aManagerCheque.setLovDescBranchDesc(this.branchCode.getDescription());
			if (StringUtils.isEmpty(this.branchCode.getValue())) {
				wve.add(new WrongValueException(this.branchCode, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_ManagerChequeDialog_branchCode.value") })));
			} else {
				aManagerCheque.setBranchCode(this.branchCode.getValidatedValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cheque No
		try {
			if (isReprint && isRecordSave && !enqiryModule) {
				// Reprint ManagerCheque basic Validation
				if (StringUtils.trimToEmpty(this.chequeNo.getValue().trim()).equals(
						StringUtils.trimToEmpty(this.oldChequeNo.getValue()))) {
					wve.add(new WrongValueException(this.chequeNo, Labels.getLabel("ChequeNo_Validation",
							new String[] { Labels.getLabel("label_ManagerChequeDialog_ChequeNo.value") })));
				} else {
					aManagerCheque.setChequeNo(this.chequeNo.getValue().trim());
				}
			} else {
				aManagerCheque.setChequeNo(this.chequeNo.getValue().trim());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Beneficiary Name
		try {
			aManagerCheque.setBeneficiaryName(this.beneficiaryName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Customer CIF
		try {
			aManagerCheque.setCustCIF(this.custCIF.getValue());
			if (StringUtils.isNotBlank(this.custCIF.getValue())) {
				aManagerCheque.setLovDescCustShrtName(this.custShrtName.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Draft Currency
		try {
			if (StringUtils.isEmpty(this.draftCcy.getValue())) {
				wve.add(new WrongValueException(this.draftCcy, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_ManagerChequeDialog_DraftCcy.value") })));
			} else {
				aManagerCheque.setDraftCcy(this.draftCcy.getValidatedValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Funding Currency
		try {
			if (StringUtils.isEmpty(this.fundingCcy.getValue())) {
				wve.add(new WrongValueException(this.fundingCcy, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_ManagerChequeDialog_FundingCcy.value") })));
			} else {
				aManagerCheque.setFundingCcy(this.fundingCcy.getValidatedValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Funding Account
		try {
			if (isRecordSave && !enqiryModule) {
				aManagerCheque.setFundingAccount(PennantApplicationUtil.unFormatAccountNumber(this.fundingAccount
						.getValidatedValue()));
			} else {
				aManagerCheque.setFundingAccount(PennantApplicationUtil.unFormatAccountNumber(this.fundingAccount
						.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Nostro Account
		try {
			if (isRecordSave && !enqiryModule) {
				aManagerCheque.setNostroAccount(PennantApplicationUtil.unFormatAccountNumber(this.nostroAccount
						.getValidatedValue()));
			} else {
				aManagerCheque.setNostroAccount(PennantApplicationUtil.unFormatAccountNumber(this.nostroAccount
						.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Nostro Full Name
		try {
			aManagerCheque.setNostroFullName(this.nostroFullName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cheque Amount
		try {
			if (this.chequeAmount.getActualValue() != null) {
				aManagerCheque.setChequeAmount(PennantAppUtil.unFormateAmount(this.chequeAmount.getActualValue(),
						ccyEditField));
			}
			if (this.financeMain != null && isRecordSave && isManagerCheque && !enqiryModule) {
				// Comparing ChequeAmount and Available Amounts
				if (this.chequeAmount.getActualValue().compareTo(this.availableAmt.getActualValue()) > 0) {
					throw new WrongValueException(this.chequeAmount, Labels.getLabel(
							"FIELD_IS_EQUAL_OR_LESSER",
							new String[] { Labels.getLabel("label_ManagerChequeDialog_ChequeAmount.value"),
									Labels.getLabel("label_ManagerChequeDialog_AvailableAmt.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Charge Amount
		try {
			if (this.chargeAmount.getActualValue() != null) {
				aManagerCheque.setChargeAmount(PennantAppUtil.unFormateAmount(this.chargeAmount.getActualValue(),
						ccyEditField));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Funding Amount
		try {
			if (this.chequeAmount.getActualValue() != null && this.chargeAmount.getActualValue() != null) {
				// defaultCCYDecPos = doGetCcyEditField(this.fundingCcy.getValue());
				aManagerCheque.setFundingAmount(PennantAppUtil.unFormateAmount(
						this.chequeAmount.getActualValue().add(this.chargeAmount.getActualValue()), ccyEditField));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Value Date
		try {
			if (this.valueDate.getValue() != null) {
				aManagerCheque.setValueDate(new Timestamp(this.valueDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Issue Date
		try {
			if (this.issueDate.getValue() != null) {
				aManagerCheque.setIssueDate(new Timestamp(this.issueDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Narration 1
		try {
			aManagerCheque.setNarration1(this.narration1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Narration 2
		try {
			aManagerCheque.setNarration2(this.narration2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line1
		try {
			aManagerCheque.setAddressLine1(this.addressLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line2
		try {
			aManagerCheque.setAddressLine2(this.addressLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line3
		try {
			aManagerCheque.setAddressLine3(this.addressLine3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line4
		try {
			aManagerCheque.setAddressLine4(this.addressLine4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line1
		try {
			aManagerCheque.setAddressLine5(this.addressLine5.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cancel ManagerCheque
		if (isCancel) {
			aManagerCheque.setCancel(true);
		}
		aManagerCheque.setStopOrderRef("");

		if (wve.size() > 0) {
			this.managerChequeDetailsTab.setSelected(true);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// Cheque Purpose
		if (!this.chqPurposeCode.isDisabled()) {
			this.chqPurposeCode.setConstraint(new StaticListValidator(listChqPurposeCodes, Labels
					.getLabel("label_ManagerChequeDialog_ChqPurposeCode.value")));
		}
		// Cheque Reference
		if (!this.chequeRef.isReadonly()) {
			this.chequeRef
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ManagerChequeDialog_ChequeRef.value"),
							PennantRegularExpressions.REGEX_NAME, false));
		}
		// Cheque No
		if (!this.chequeNo.isReadonly()) {
			this.chequeNo.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_ChequeNo.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}
		// Beneficiary Name
		if (!this.beneficiaryName.isReadonly()) {
			this.beneficiaryName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_BeneficiaryName.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, true));
		}
		// Customer CIF
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ManagerChequeDialog_CustCIF.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, false));
		}
		// Funding Account
		if (isRecordSave && !this.fundingAccount.isReadonly()) {
			this.fundingAccount.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_ManagerChequeDialog_FundingAccount.value") }));
		}
		// Nostro Account
		if (isRecordSave && !this.nostroAccount.isReadonly()) {
			this.nostroAccount.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_ManagerChequeDialog_NostroAccount.value") }));
		}
		// Nostro Full Name
		if (!this.nostroFullName.isReadonly()) {
			this.nostroFullName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_NostroFullName.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		// Cheque Amount
		if (!this.chequeAmount.isReadonly()) {
			this.chequeAmount.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_ManagerChequeDialog_ChequeAmount.value"), ccyEditField, true, false, 0));
		}
		// Value Date
		if (!this.valueDate.isReadonly()) {
			this.valueDate.setConstraint(new PTDateValidator(Labels
					.getLabel("label_ManagerChequeDialog_ValueDate.value"), true));
		}
		// Narration 1
		if (!this.narration1.isReadonly()) {
			this.narration1.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_Narration1.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		// Narration 2
		if (!this.narration2.isReadonly()) {
			this.narration2.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_Narration2.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		// Branch
		if (!branchCode.isReadonly()) {
			this.branchCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_branchCode.value"), null, true));
		}
		// draftCcy
		if (!this.draftCcy.isReadonly()) {
			this.draftCcy.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_DraftCcy.value"), null, true));
		}
		// fundingCcy
		if (!this.fundingCcy.isReadonly()) {
			this.fundingCcy.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_FundingCcy.value"), null, true));
		}

		// Charge Amount
		if (!this.chargeAmount.isReadonly()) {
			this.chargeAmount.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_ManagerChequeDialog_ChargeAmount.value"), ccyEditField, false, false, 0));
		}
		// Address Line 1
		if (!this.addressLine1.isReadonly()) {
			this.addressLine1.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_AddressLine1.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		// Address Line 2
		if (!this.addressLine2.isReadonly()) {
			this.addressLine2.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_AddressLine2.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		// Address Line 3
		if (!this.addressLine3.isReadonly()) {
			this.addressLine3.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_AddressLine3.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		// Address Line 4
		if (!this.addressLine4.isReadonly()) {
			this.addressLine4.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_AddressLine4.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		// Address Line 5
		if (!this.addressLine5.isReadonly()) {
			this.addressLine5.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ManagerChequeDialog_AddressLine5.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.chqPurposeCode.setConstraint("");
		this.branchCode.setConstraint("");
		this.chequeRef.setConstraint("");
		this.chequeNo.setConstraint("");
		this.beneficiaryName.setConstraint("");
		this.custCIF.setConstraint("");
		this.draftCcy.setConstraint("");
		this.fundingCcy.setConstraint("");
		this.fundingAccount.setConstraint("");
		this.nostroAccount.setConstraint("");
		this.nostroFullName.setConstraint("");
		this.chequeAmount.setConstraint("");
		this.valueDate.setConstraint("");
		this.narration1.setConstraint("");
		this.narration2.setConstraint("");
		Clients.clearWrongValue(this.chequeAmount);
		this.chargeAmount.setConstraint("");
		this.addressLine1.setConstraint("");
		this.addressLine2.setConstraint("");
		this.addressLine3.setConstraint("");
		this.addressLine4.setConstraint("");
		this.addressLine5.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.chqPurposeCode.setErrorMessage("");
		this.chequeRef.setErrorMessage("");
		this.chequeNo.setErrorMessage("");
		this.branchCode.setErrorMessage("");
		this.beneficiaryName.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		this.draftCcy.setErrorMessage("");
		this.fundingCcy.setErrorMessage("");
		this.fundingAccount.setErrorMessage("");
		this.nostroAccount.setErrorMessage("");
		this.nostroFullName.setErrorMessage("");
		this.chequeAmount.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.narration1.setErrorMessage("");
		this.narration2.setErrorMessage("");
		Clients.clearWrongValue(this.chequeAmount);
		this.chargeAmount.setErrorMessage("");
		this.addressLine1.setErrorMessage("");
		this.addressLine2.setErrorMessage("");
		this.addressLine3.setErrorMessage("");
		this.addressLine4.setErrorMessage("");
		this.addressLine5.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		this.managerChequeListCtrl.search();
	}

	/**
	 * Deletes a ManagerCheque object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final ManagerCheque aManagerCheque = new ManagerCheque();
		BeanUtils.copyProperties(managerCheque, aManagerCheque);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aManagerCheque.getChequeID();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aManagerCheque.getRecordType())) {
				aManagerCheque.setVersion(aManagerCheque.getVersion() + 1);
				aManagerCheque.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aManagerCheque.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aManagerCheque.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aManagerCheque.getNextTaskId(),
							aManagerCheque);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aManagerCheque, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
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

		this.chqPurposeCode.setSelectedIndex(0);
		this.chequeRef.setValue("");
		this.chequeNo.setValue("");
		this.branchCode.setDescription("");
		this.beneficiaryName.setValue("");
		this.custCIF.setValue("");
		this.draftCcy.setValue("");
		this.draftCcy.setDescription("");
		this.fundingCcy.setValue("");
		this.fundingCcy.setDescription("");
		this.fundingAccount.setValue("");
		this.nostroAccount.setValue("");
		this.nostroFullName.setValue("");
		this.chequeAmount.setValue("");
		this.valueDate.setText("");
		this.narration1.setValue("");
		this.narration2.setValue("");
		this.chargeAmount.setValue("");
		this.addressLine1.setValue("");
		this.addressLine2.setValue("");
		this.addressLine3.setValue("");
		this.addressLine4.setValue("");
		this.addressLine5.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final ManagerCheque aManagerCheque = new ManagerCheque();
		ManagerCheque reprintManagerCheque = null;

		BeanUtils.copyProperties(managerCheque, aManagerCheque);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aManagerCheque.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aManagerCheque.getNextTaskId(), aManagerCheque);
		}

		if (PennantConstants.RCD_STATUS_CANCELLED.equalsIgnoreCase(aManagerCheque.getRecordStatus())
				|| aManagerCheque.getRecordStatus().contains(PennantConstants.RCD_STATUS_RESUBMITTED)
				|| aManagerCheque.getRecordStatus().contains(PennantConstants.RCD_STATUS_REJECTED)
				|| PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(aManagerCheque.getRecordType())) {
			isRecordSave = false;
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		String tranType = "";

		if (!PennantConstants.RECORD_TYPE_DEL.equals(aManagerCheque.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the ManagerCheque object with the components data
			if (isReprint) {
				reprintManagerCheque = getManagerChequeService().getNewManagerCheque();
				if (managerCheque.getReprintManagerCheque() != null) {
					BeanUtils.copyProperties(managerCheque.getReprintManagerCheque(), reprintManagerCheque);
				}
				doWriteComponentsToBean(reprintManagerCheque);
			} else {
				doWriteComponentsToBean(aManagerCheque);
			}

			if (isReprint) {
				aManagerCheque.setReprint(true);
				isNew = reprintManagerCheque.isNew();

				reprintManagerCheque.setOldChequeID(aManagerCheque.getChequeID());

				if (isWorkFlowEnabled()) {
					tranType = PennantConstants.TRAN_WF;
					if (StringUtils.isBlank(reprintManagerCheque.getRecordType())) {
						reprintManagerCheque.setVersion(reprintManagerCheque.getVersion() + 1);
						if (isNew) {
							reprintManagerCheque.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						} else {
							reprintManagerCheque.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							reprintManagerCheque.setNewRecord(true);
						}
					}
				} else {
					reprintManagerCheque.setVersion(reprintManagerCheque.getVersion() + 1);
					if (isNew) {
						tranType = PennantConstants.TRAN_ADD;
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}
				}

				aManagerCheque.setReprintManagerCheque(reprintManagerCheque);

			}
		}

		// Document Details Saving
		aManagerCheque.setDocumentDetailsList(documentDetailsList);

		// Accounting Preparation
		aManagerCheque.setReturnDataSetList(getPostingDataSets());

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aManagerCheque.isNew();

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aManagerCheque.getRecordType())) {
				aManagerCheque.setVersion(aManagerCheque.getVersion() + 1);
				if (isNew) {
					aManagerCheque.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aManagerCheque.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aManagerCheque.setNewRecord(true);
				}
			}
		} else {
			aManagerCheque.setVersion(aManagerCheque.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aManagerCheque, tranType)) {
				// doWriteBeanToComponents(aManagerCheque);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
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

	private boolean doProcess(ManagerCheque aManagerCheque, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aManagerCheque.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aManagerCheque.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aManagerCheque.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aManagerCheque.setTaskId(getTaskId());
			aManagerCheque.setNextTaskId(getNextTaskId());
			aManagerCheque.setRoleCode(getRole());
			aManagerCheque.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aManagerCheque, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aManagerCheque, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aManagerCheque, tranType), null);
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

		ManagerCheque aManagerCheque = (ManagerCheque) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getManagerChequeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getManagerChequeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getManagerChequeService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aManagerCheque.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getManagerChequeService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aManagerCheque.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ManagerChequeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ManagerChequeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(
								getNotes("ManagerCheque", String.valueOf(aManagerCheque.getChequeID()),
										aManagerCheque.getVersion()), true);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ManagerCheque aManagerCheque, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManagerCheque.getBefImage(), aManagerCheque);
		return new AuditHeader(String.valueOf(aManagerCheque.getChequeID()), null, null, null, auditDetail,
				aManagerCheque.getUserDetails(), getOverideMap());
	}

	public void doFillDocumentDetails(List<DocumentDetails> documentDetails) {
		logger.debug("Entering");

		docDetailMap = new HashMap<String, DocumentDetails>();
		this.listBoxDocumentDetails.getItems().clear();
		setDocumentDetailsList(documentDetails);
		for (DocumentDetails documentDetail : documentDetails) {
			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell(PennantAppUtil.getlabelDesc(documentDetail.getDocCategory(),
					PennantAppUtil.getDocumentTypes()));
			listitem.appendChild(listcell);
			listcell = new Listcell(documentDetail.getDocName());
			listitem.appendChild(listcell);
			listcell = new Listcell(documentDetail.getRecordType());
			listitem.appendChild(listcell);
			listitem.setAttribute("data", documentDetail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick = onManagerDocumentItemDoubleClicked");
			this.listBoxDocumentDetails.appendChild(listitem);
			docDetailMap.put(documentDetail.getDocCategory(), documentDetail);
		}
		logger.debug("Leaving");
	}

	public void onManagerDocumentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxDocumentDetails.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			DocumentDetails managerDocumentDetail = (DocumentDetails) item.getAttribute("data");
			if (StringUtils.trimToEmpty(managerDocumentDetail.getRecordType()).equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				boolean viewProcess = false;
				if (!StringUtils.trimToEmpty(managerDocumentDetail.getRecordType()).equalsIgnoreCase(
						PennantConstants.RCD_ADD)) {
					viewProcess = true;
				}
				updateExistingDocument(managerDocumentDetail, viewProcess);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void updateExistingDocument(DocumentDetails finDocumentDetail, boolean viewProcess)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finDocumentDetail", finDocumentDetail);
		map.put("DocumentDialogCtrl", this);
		map.put("roleCode", getRole());
		map.put("moduleType", "");
		map.put("viewProcess", viewProcess);
		map.put("customerDialogCtrl", this);

		map.put("documentType", getDefaultDocumentType());
		// map.put("newRecord", "true");

		finDocumentDetail = getManagerChequeService().getManagerChequeDocDetailByDocId(finDocumentDetail.getDocId());
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/ManagerCheque/DocumentDialog.zul",
					window_ManagerChequeDialog, map);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the default document type
	 * 
	 * @return
	 */
	private String getDefaultDocumentType() {
		String documentType = null;
		if (isReprint) {
			documentType = "RPMGRCHQ";
		} else if (isCancel) {
			documentType = "CANMGRCHQ";
		} else {
			documentType = "MGRCHQ";

		}
		return documentType;
	}

	/**
	 * Method to fill the Finance Document Details List
	 * 
	 * @param docDetails
	 */
	public void doFillEnqDocumentDetails(List<DocumentDetails> docDetails) {
		logger.debug("Entering");
		Listitem listitem = null;
		Listcell lc = null;
		for (DocumentDetails doc : docDetails) {

			listitem = new Listitem();
			lc = new Listcell(String.valueOf(doc.getDocId()));
			listitem.appendChild(lc);

			lc = new Listcell(doc.getDocCategory());
			listitem.appendChild(lc);

			lc = new Listcell(doc.getDoctype());
			listitem.appendChild(lc);

			lc = new Listcell(doc.getDocName());
			listitem.appendChild(lc);

			lc = new Listcell();
			Button viewBtn = new Button("View");
			if (StringUtils.trimToEmpty(doc.getDoctype()).equals(PennantConstants.DOC_TYPE_WORD)) {
				viewBtn.setLabel("Download");
			}
			viewBtn.addForward("onClick", window_ManagerChequeDialog, "onDocViewButtonClicked", doc.getDocId());
			lc.appendChild(viewBtn);
			viewBtn.setStyle("font-weight:bold;");
			listitem.appendChild(lc);

			this.listBoxDocumentEnquiry.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	public void onDocViewButtonClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		long docId = Long.parseLong(event.getData().toString());
		DocumentDetails detail = getFinanceDetailService().getFinDocDetailByDocId(docId);

		if (StringUtils.isNotBlank(detail.getDocName()) && StringUtils.isNotBlank(detail.getDocImage().toString())) {

			try {
				if (StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_WORD)) {
					Filedownload.save(detail.getDocImage(), "application/msword", detail.getDocName());
				} else {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("FinDocumentDetail", detail);
					Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
				}
			} catch (Exception e) {
				logger.debug(e);
			}
		} else {
			MessageUtil.showError("Document Details not Found.");
		}
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * Recommendations Notes Method for get the ChequeID to be stored in Notes table as reference
	 * 
	 * @return chequeID
	 */
	public String getReference() {
		if (managerCheque.getId() == Long.MIN_VALUE) {
			managerCheque.setId(getManagerChequeService().getNextId());
			logger.debug("get NextID:" + managerCheque.getId());
			return String.valueOf(managerCheque.getId());
		} else {
			return String.valueOf(managerCheque.getId());
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setManagerChequeService(ManagerChequeService managerChequeService) {
		this.managerChequeService = managerChequeService;
	}

	public ManagerChequeService getManagerChequeService() {
		return this.managerChequeService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	
	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	// AHB
	/*
	 * public void setPffCustomerPreparation(PFFCustomerPreparation pffCustomerPreparation) {
	 * this.pffCustomerPreparation = pffCustomerPreparation; }
	 * 
	 * public PFFCustomerPreparation getPffCustomerPreparation() { return pffCustomerPreparation; }
	 */

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
}
