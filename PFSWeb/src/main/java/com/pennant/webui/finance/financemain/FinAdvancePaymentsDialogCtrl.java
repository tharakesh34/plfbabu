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
 * * FileName : FinAdvancePaymentsDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.East;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.pennydrop.PennyDropDAO;
import com.pennant.backend.dao.systemmasters.BuilderProjcetDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.pennydrop.PennyDropService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.applicationmaster.customerPaymentTransactions.CustomerPaymentTxnsDialogCtrl;
import com.pennant.webui.applicationmaster.customerPaymentTransactions.CustomerPaymentTxnsListCtrl;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennant.webui.finance.payorderissue.PayOrderIssueDialogCtrl;
import com.pennant.webui.finance.payorderissue.PayOrderIssueListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.external.ExternalDocumentManager;
import com.pennanttech.pff.external.BankAccountValidationService;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinAdvancePaymentsDialog.zul file.
 */
public class FinAdvancePaymentsDialogCtrl extends GFCBaseCtrl<FinAdvancePayments> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinAdvancePaymentsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinAdvancePaymentsDialog;

	protected Combobox disbDate;
	protected Decimalbox disbDateAmount;
	protected Intbox disbSeq;
	protected Intbox paymentSequence;
	protected Combobox paymentDetail;
	protected CurrencyBox amtToBeReleased;
	protected Textbox liabilityHoldName;
	protected Textbox beneficiaryName;
	protected Label label_FinAdvancePaymentsDialog_BeneficiaryName;
	protected Textbox beneficiaryAccNo;
	protected Label label_FinAdvancePaymentsDialog_BeneficiaryAccNo;
	protected Textbox description;
	protected Combobox paymentType;
	protected Textbox llReferenceNo;
	protected Datebox llDate;
	protected CurrencyBox custContribution;
	protected CurrencyBox sellerContribution;
	protected CurrencyBox totalDisbursementAmount;
	protected CurrencyBox otherExpenses;
	protected CurrencyBox disbursementAddedAmount;
	protected CurrencyBox netAmount;
	protected Textbox remarks;
	protected Textbox transactionRef;
	protected ExtendedCombobox bankCode;
	protected Textbox payableLoc;
	protected ExtendedCombobox printingLoc;
	protected Space printLoc;
	protected Datebox valueDate;
	protected ExtendedCombobox bankBranchID;
	protected ExtendedCombobox partnerBankID;
	protected Textbox bank;
	protected Textbox branch;
	protected Textbox city;
	protected Space contactNumber;
	protected Space leiNum;
	// protected Textbox phoneCountryCode;
	// protected Textbox phoneAreaCode;
	protected Textbox phoneNumber;
	protected East eastDocument;

	protected Iframe disbDoc;
	protected Button btnUploadDoc;
	protected Textbox documentName;
	private byte[] imagebyte;

	protected Label label_liabilityHoldName;
	protected Hbox hbox_liabilityHoldName;
	protected Label label_llReferenceNo;
	protected Hbox hbox_llReferenceNo;
	protected Label label_llDate;
	protected Hbox hbox_llDate;
	protected Label label_custContribution;
	protected Hbox hbox_custContribution;
	protected Label label_sellerContribution;
	protected Hbox hbox_sellerContribution;

	protected Row row_ReEnterBenfAccNo;
	protected Space space_ReEnterAccNo;
	protected Textbox reEnterBeneficiaryAccNo;
	private boolean reEntrBenfAccNo = false;

	protected Label recordType;
	protected Groupbox gb_statusDetails;
	protected Groupbox gb_ChequeDetails;
	protected Groupbox gb_NeftDetails;
	private boolean enqModule = false;
	protected Button btnGetCustBeneficiary;
	protected Caption caption_FinAdvancePaymentsDialog_NeftDetails;
	protected Caption caption_FinAdvancePaymentsDialog_ChequeDetails;

	protected Row row_HoldDisbursement;
	protected Checkbox holdDisbursement;
	protected Textbox leiNumber;

	// not auto wired vars
	private FinAdvancePayments finAdvancePayments; // over handed per param

	private transient boolean newFinance;

	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;

	private Object financeMainDialogCtrl;
	private FinAdvancePaymentsListCtrl finAdvancePaymentsListCtrl;
	private transient PayOrderIssueListCtrl payOrderIssueListCtrl;
	private transient PayOrderIssueDialogCtrl payOrderIssueDialogCtrl;
	private transient CustomerPaymentTxnsListCtrl customerPaymentTxnsListCtrl;
	private transient CustomerPaymentTxnsDialogCtrl customerPaymentTxnsDialogCtrl;
	private boolean newRecord = false;
	private boolean newCustomer = false;
	private int ccyFormatter = 0;
	private long custID;
	private String custCIF = "";
	private String finCcy;

	private String moduleType = "";

	private List<FinAdvancePayments> finAdvancePaymentsDetails;

	private boolean poIssued = false;
	private boolean allowMultyparty = false;
	private List<FinanceDisbursement> financeDisbursement = null;
	private List<FinanceDisbursement> approvedDisbursments;

	protected int maxAccNoLength;
	protected int minAccNoLength;
	private transient BankDetailService bankDetailService;

	private transient PartnerBankService partnerBankService;
	private FinanceMain financeMain;
	private DocumentDetails documentDetails;
	@Autowired
	private ExternalDocumentManager externalDocumentManager = null;

	protected Textbox pennyDropResult;
	protected Textbox txnDetails;
	protected Button btnPennyDropResult;
	protected Button btnReverse;

	BankAccountValidationService bankAccountValidationService;
	private transient PennyDropService pennyDropService;
	private PennyDropDAO pennyDropDAO;
	private BankAccountValidation bankAccountValidations;
	private String moduleDefiner = null;

	// VAS FrontEnd Functionality
	protected Row row_DisbDate;
	protected Row row_vasReference;
	protected Combobox vasReference;
	protected Decimalbox vasAmount;
	Map<String, BigDecimal> vasAmountsMAP = null;
	private List<VASRecording> vasRecordingList = null;

	protected Row row_disbdetails;
	protected Row row_expensedetails;
	private BankDetail bankdetail;
	private DocumentDetailsDAO documentDetailsDAO;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private BuilderProjcetDAO builderProjcetDAO;
	private BankBranchDAO bankBranchDAO;
	protected CollateralAssignmentDAO collateralAssignmentDAO;
	private boolean populateBenfiryDetails = false;
	private transient FinTypePartnerBankService finTypePartnerBankService;
	private transient ClusterService clusterService;
	private boolean leiMandatory = false;

	/**
	 * default constructor.<br>
	 */
	public FinAdvancePaymentsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinAdvancePaymentsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinAdvancePaymentsDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinAdvancePaymentsDialog(Event event) {
		logger.debug("Entering");
		// Set the page level components.
		setPageComponents(window_FinAdvancePaymentsDialog);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			if (arguments.containsKey("multiParty")) {
				allowMultyparty = (Boolean) arguments.get("multiParty");
			} else {
				allowMultyparty = false;
			}

			if (arguments.containsKey("financeDisbursement")) {
				financeDisbursement = (List<FinanceDisbursement>) arguments.get("financeDisbursement");
			} else {
				financeDisbursement = null;
			}

			if (arguments.containsKey("documentDetails")) {
				documentDetails = (DocumentDetails) arguments.get("documentDetails");
			} else {
				documentDetails = null;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("finAdvancePayments")) {
				this.finAdvancePayments = (FinAdvancePayments) arguments.get("finAdvancePayments");
				FinAdvancePayments befImage = new FinAdvancePayments();
				BeanUtils.copyProperties(this.finAdvancePayments, befImage);
				this.finAdvancePayments.setBefImage(befImage);

				setFinAdvancePayments(this.finAdvancePayments);
				poIssued = this.finAdvancePayments.ispOIssued();
			} else {
				setFinAdvancePayments(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			// moduleDefiner
			if (arguments.containsKey("moduleDefiner")) {
				this.moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("approvedDisbursments")) {
				approvedDisbursments = (List<FinanceDisbursement>) arguments.get("approvedDisbursments");
			}
			if (arguments.containsKey("financeMain")) {
				financeMain = (FinanceMain) arguments.get("financeMain");
			}

			if (this.finAdvancePayments.isNewRecord()) {
				setNewRecord(true);
			}
			if (arguments.containsKey("finAdvancePaymentsListCtrl")) {
				setFinAdvancePaymentsListCtrl((FinAdvancePaymentsListCtrl) arguments.get("finAdvancePaymentsListCtrl"));
			}
			if (arguments.containsKey("financeMainDialogCtrl")) {

				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("ccyFormatter")) {
					ccyFormatter = (Integer) arguments.get("ccyFormatter");
				}

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				setNewFinance(true);
				this.finAdvancePayments.setWorkflowId(0);
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}

			if (arguments.containsKey("customerPaymentTxnsListCtrl")) {
				setCustomerPaymentTxnsListCtrl(
						(CustomerPaymentTxnsListCtrl) arguments.get("customerPaymentTxnsListCtrl"));
			}

			if (arguments.containsKey("customerPaymentTxnsDialogCtrl")) {
				setCustomerPaymentTxnsDialogCtrl(
						(CustomerPaymentTxnsDialogCtrl) arguments.get("customerPaymentTxnsDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("ccyFormatter")) {
					ccyFormatter = (Integer) arguments.get("ccyFormatter");
				}

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				setNewFinance(true);
				this.finAdvancePayments.setWorkflowId(0);
			}

			if (arguments.containsKey("payOrderIssueListCtrl")) {
				setPayOrderIssueListCtrl((PayOrderIssueListCtrl) arguments.get("payOrderIssueListCtrl"));
			}

			if (arguments.containsKey("payOrderIssueDialogCtrl")) {
				setPayOrderIssueDialogCtrl((PayOrderIssueDialogCtrl) arguments.get("payOrderIssueDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("ccyFormatter")) {
					ccyFormatter = (Integer) arguments.get("ccyFormatter");
				}

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				setNewFinance(true);
				this.finAdvancePayments.setWorkflowId(0);
			}
			if (arguments.containsKey("custID")) {
				custID = (long) arguments.get("custID");
			}

			if (arguments.containsKey("finCcy")) {
				finCcy = (String) arguments.get("finCcy");
			}

			if (arguments.containsKey("VasRecordingList")) {
				vasRecordingList = (List<VASRecording>) arguments.get("VasRecordingList");
			} else {
				vasRecordingList = new ArrayList<>(1);
			}
			if (StringUtils.isEmpty(moduleDefiner) && !ImplementationConstants.VAS_INST_EDITABLE
					&& DisbursementConstants.PAYMENT_DETAIL_VAS.equals(this.finAdvancePayments.getPaymentDetail())) {
				enqModule = true;
				this.btnPennyDropResult.setVisible(false);
			}

			doLoadWorkFlow(this.finAdvancePayments.isWorkflow(), this.finAdvancePayments.getWorkflowId(),
					this.finAdvancePayments.getNextTaskId());

			if (isWorkFlowEnabled() && !isNewFinance()) {
				this.userAction = setListRecordStatus(this.userAction);
			}

			getUserWorkspace().allocateAuthorities("FinAdvancePaymentsDialog", getRole());

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(this.finAdvancePayments);
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
	 * when the "btnReverse" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnReverse(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		final String msg = Labels.getLabel("label_FinAdvancePaymentsDialog_Reverse_Status") + "\n"
				+ Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentSequence.value") + " : "
				+ this.finAdvancePayments.getPaymentSeq();

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				doSave();
			}
		});

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnCancel(Event event) throws InterruptedException {
		doDisbCancel();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_FinAdvancePaymentsDialog);
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
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.finAdvancePayments);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finAdvancePayments.getFinReference());
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinAdvancePaymentsDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinAdvancePayments aFinAdvancePayments) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinAdvancePayments.isNewRecord()) {

			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.paymentDetail.focus();
		} else {
			this.paymentDetail.focus();
			if (isNewFinance()) {
				if (enqModule) {
					doReadOnly();
				} else {
					doEdit();
				}
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
			if (aFinAdvancePayments != null) {
				bankAccountValidations = getPennyDropService().getPennyDropStatusDataByAcc(
						aFinAdvancePayments.getBeneficiaryAccNo(), aFinAdvancePayments.getiFSC());
			}
			doWriteBeanToComponents(aFinAdvancePayments);
			if (poIssued) {
				doReadOnly();
				this.btnSave.setVisible(true);
				this.btnGetCustBeneficiary.setVisible(false);
				readOnlyComponent(isReadOnly("FinAdvancePaymentsDialog_holdDisbIsActive"), this.holdDisbursement);
				if (ImplementationConstants.DISB_PAID_CANCELLATION_REQ
						&& DisbursementConstants.STATUS_PAID.equals(aFinAdvancePayments.getStatus())) {
					this.btnDelete.setVisible(false);
					this.btnSave.setVisible(false);
					this.btnCancel.setVisible(true);
				}
			}
			if (enqModule) {
				doReadOnly();
			}
			if (!enqModule && (StringUtils.equals(DisbursementConstants.STATUS_PAID, aFinAdvancePayments.getStatus())
					|| StringUtils.equals(DisbursementConstants.STATUS_REALIZED, aFinAdvancePayments.getStatus()))) {
				doReadOnly();
				this.btnNew.setVisible(false);
				this.btnEdit.setVisible(false);
				this.btnDelete.setVisible(false);
				this.btnSave.setVisible(false);
				this.btnReverse.setVisible(getUserWorkspace().isAllowed("button_FinAdvancePaymentsDialog_btnReverse"));
				this.btnGetCustBeneficiary.setDisabled(true);
			} else {
				this.btnReverse.setVisible(false);
			}
			this.gb_statusDetails.setVisible(false);
			this.window_FinAdvancePaymentsDialog.doModal();

		} catch (Exception e) {
			this.window_FinAdvancePaymentsDialog.onClose();
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (this.finAdvancePayments.isNewRecord()) {
			this.paymentDetail.setDisabled(isReadOnly("FinAdvancePaymentsDialog_paymentDetail"));
		} else {
			this.paymentDetail.setDisabled(true);
		}
		this.disbDate.setDisabled(isReadOnly("FinAdvancePaymentsDialog_llDate"));
		this.amtToBeReleased.setDisabled(isReadOnly("FinAdvancePaymentsDialog_amtToBeReleased"));
		this.llDate.setDisabled(isReadOnly("FinAdvancePaymentsDialog_llDate"));
		this.paymentType.setDisabled(isReadOnly("FinAdvancePaymentsDialog_paymentType"));
		this.remarks.setReadonly(isReadOnly("FinAdvancePaymentsDialog_remarks"));
		readOnlyComponent(isReadOnly("FinAdvancePaymentsDialog_holdDisbIsActive"), this.holdDisbursement);
		this.transactionRef.setReadonly(true);
		this.leiNumber.setReadonly(isReadOnly("FinAdvancePaymentsDialog_leiNumber"));
		// 2
		this.bankBranchID.setReadonly(isReadOnly("FinAdvancePaymentsDialog_bankBranchID"));
		this.beneficiaryAccNo.setReadonly(isReadOnly("FinAdvancePaymentsDialog_beneficiaryAccNo"));
		this.beneficiaryName.setReadonly(isReadOnly("FinAdvancePaymentsDialog_beneficiaryName"));
		this.phoneNumber.setReadonly(isReadOnly("FinAdvancePaymentsDialog_contactNumber"));
		// 3
		this.bankCode.setReadonly(isReadOnly("FinAdvancePaymentsDialog_bankCode"));
		this.liabilityHoldName.setReadonly(isReadOnly("FinAdvancePaymentsDialog_liabilityHoldName"));
		this.llReferenceNo.setReadonly(isReadOnly("FinAdvancePaymentsDialog_llReferenceNo"));
		this.payableLoc.setReadonly(isReadOnly("FinAdvancePaymentsDialog_payableLoc"));
		this.printingLoc.setReadonly(isReadOnly("FinAdvancePaymentsDialog_printingLoc"));
		this.valueDate.setDisabled(isReadOnly("FinAdvancePaymentsDialog_valueDate"));
		this.description.setReadonly(isReadOnly("FinAdvancePaymentsDialog_description"));
		this.custContribution.setDisabled(isReadOnly("FinAdvancePaymentsDialog_custContribution"));
		this.sellerContribution.setDisabled(isReadOnly("FinAdvancePaymentsDialog_sellerContribution"));
		this.partnerBankID.setReadonly(isReadOnly("FinAdvancePaymentsDialog_partnerBankID"));
		this.pennyDropResult.setReadonly(isReadOnly("FinAdvancePaymentsDialog_PennyDropResult"));
		this.txnDetails.setReadonly(isReadOnly("FinAdvancePaymentsDialog_TxnDetails"));
		this.btnPennyDropResult.setVisible(!isReadOnly("button_FinAdvancePaymentsDialog_btnPennyDropResult"));
		this.vasReference.setDisabled(isReadOnly("FinAdvancePaymentsDialog_vasReference"));

		// Added Masking for ReEnter Account Number field in Disbursement at Loan Approval stage
		if (SysParamUtil.isAllowed(SMTParameterConstants.DISB_ACCNO_MASKING)
				&& (!isReadOnly("FinAdvancePaymentsDialog_ReEnterBeneficiaryAccNo") || StringUtils
						.equals(this.finAdvancePayments.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED))) {
			this.beneficiaryAccNo.setType("password");
		}

		// Added in QDP changes
		if (finAdvancePayments.isNewRecord() || DisbursementConstants.STATUS_NEW.equals(finAdvancePayments.getStatus())
				|| DisbursementConstants.STATUS_APPROVED.equals(finAdvancePayments.getStatus())
				|| DisbursementConstants.STATUS_HOLD.equals(finAdvancePayments.getStatus())) {
			readOnlyComponent(isReadOnly("FinAdvancePaymentsDialog_holdDisbIsActive"), this.holdDisbursement); // TODO
																												// create
																												// right
																												// name
		} else {
			readOnlyComponent(true, this.holdDisbursement);
		}

		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.paymentSequence.setReadonly(true);
		this.paymentDetail.setDisabled(true);
		this.disbDate.setDisabled(true);
		this.liabilityHoldName.setReadonly(true);
		this.description.setReadonly(true);
		this.amtToBeReleased.setDisabled(true);
		this.beneficiaryName.setReadonly(true);
		this.beneficiaryAccNo.setReadonly(true);
		this.paymentType.setDisabled(true);
		this.llReferenceNo.setReadonly(false);
		this.llDate.setDisabled(true);
		this.custContribution.setDisabled(true);
		this.sellerContribution.setDisabled(true);
		this.remarks.setReadonly(true);
		this.bankCode.setReadonly(true);
		this.bankBranchID.setReadonly(true);
		this.payableLoc.setDisabled(true);
		this.printingLoc.setReadonly(true);
		this.valueDate.setDisabled(true);
		this.phoneNumber.setReadonly(true);
		this.partnerBankID.setReadonly(true);
		this.transactionRef.setReadonly(true);
		this.leiNumber.setReadonly(true);
		readOnlyComponent(true, holdDisbursement);
		this.vasReference.setDisabled(true);
		if (enqModule) {
			this.btnPennyDropResult.setVisible(false);
		}

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

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinAdvancePaymentsDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinAdvancePaymentsDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinAdvancePaymentsDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinAdvancePaymentsDialog_btnSave"));
			this.btnReverse.setVisible(getUserWorkspace().isAllowed("button_FinAdvancePaymentsDialog_btnReverse"));
		} else {
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
			this.btnReverse.setVisible(false);
		}
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.paymentSequence.setReadonly(true);
		this.liabilityHoldName.setMaxlength(200);
		this.beneficiaryName.setMaxlength(100);
		this.description.setMaxlength(500);
		this.llReferenceNo.setMaxlength(50);
		this.remarks.setMaxlength(500);
		this.transactionRef.setReadonly(true);

		this.totalDisbursementAmount.setReadonly(true);
		this.totalDisbursementAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.totalDisbursementAmount.setScale(ccyFormatter);
		this.totalDisbursementAmount.setTextBoxWidth(150);

		this.otherExpenses.setReadonly(true);
		this.otherExpenses.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.otherExpenses.setScale(ccyFormatter);
		this.otherExpenses.setTextBoxWidth(150);

		this.disbursementAddedAmount.setReadonly(true);
		this.disbursementAddedAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.disbursementAddedAmount.setScale(ccyFormatter);
		this.disbursementAddedAmount.setTextBoxWidth(150);

		this.netAmount.setReadonly(true);
		this.netAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.netAmount.setScale(ccyFormatter);
		this.netAmount.setTextBoxWidth(150);

		this.amtToBeReleased.setMandatory(true);
		this.amtToBeReleased.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.amtToBeReleased.setScale(ccyFormatter);
		this.amtToBeReleased.setTextBoxWidth(150);

		this.disbDateAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.disbDateAmount.setScale(ccyFormatter);

		this.llDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.custContribution.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.custContribution.setScale(ccyFormatter);
		this.custContribution.setTextBoxWidth(150);

		this.sellerContribution.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.sellerContribution.setScale(ccyFormatter);
		this.sellerContribution.setTextBoxWidth(150);

		this.liabilityHoldName.setWidth("150px");
		this.beneficiaryName.setWidth("155px");
		this.llDate.setWidth("150px");
		this.valueDate.setWidth("150px");
		this.llReferenceNo.setWidth("150px");
		this.description.setWidth("150px");
		this.remarks.setWidth("730px");

		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setMandatoryStyle(true);
		this.bankCode.setValueColumn("BankCode");
		this.bankCode.setDescColumn("BankName");
		this.bankCode.setDisplayStyle(2);
		this.bankCode.setValidateColumns(new String[] { "BankCode" });

		this.printingLoc.setModuleName("BankBranch");
		this.printingLoc.setValueColumn("BranchCode");
		this.printingLoc.setDescColumn("BranchDesc");
		this.printingLoc.setValidateColumns(new String[] { "BranchCode" });

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("IFSC");
		this.bankBranchID.setDescColumn("");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "IFSC" });
		this.bankBranchID.setFilterColumns(new String[] { "IFSC", "MICR" });

		this.partnerBankID.setButtonDisabled(true);
		this.partnerBankID.setReadonly(true);
		this.partnerBankID.setModuleName("FinTypePartner");
		this.partnerBankID.setMandatoryStyle(true);
		this.partnerBankID.setValueColumn("PartnerBankCode");
		this.partnerBankID.setDescColumn("PartnerBankName");
		this.partnerBankID.setMaxlength(8);
		this.partnerBankID.setValidateColumns(new String[] { "PartnerBankCode" });

		this.phoneNumber.setMaxlength(10);
		this.phoneNumber.setWidth("180px");

		if (StringUtils.isNotBlank(this.finAdvancePayments.getBranchBankCode())) {
			bankdetail = bankDetailService.getAccNoLengthByCode(this.finAdvancePayments.getBranchBankCode());
			this.maxAccNoLength = this.bankdetail.getAccNoLength();
			this.minAccNoLength = this.bankdetail.getMinAccNoLength();
		}
		this.beneficiaryAccNo.setMaxlength(maxAccNoLength);
		// Added Masking for ReEnter Account Number field in Disbursement at Loan Approval stage
		if (SysParamUtil.isAllowed(SMTParameterConstants.DISB_ACCNO_MASKING)
				&& !isReadOnly("FinAdvancePaymentsDialog_ReEnterBeneficiaryAccNo")) {
			this.btnSave.setVisible(true);
			this.row_ReEnterBenfAccNo.setVisible(true);
			this.space_ReEnterAccNo.setSclass(PennantConstants.mandateSclass);
			this.reEnterBeneficiaryAccNo.setMaxlength(LengthConstants.LEN_ACCOUNT);
			reEntrBenfAccNo = true;

			if (SysParamUtil.isAllowed(SMTParameterConstants.DISB_ACCNO_MASKING)) {
				this.beneficiaryAccNo.setType("password");
			}
		}

		String benificiaryActLen = SysParamUtil.getValueAsString(SMTParameterConstants.BEN_ACTNAME_LENGTH);
		if (benificiaryActLen != null) {
			this.beneficiaryName.setMaxlength(40);
			this.beneficiaryAccNo.setMaxlength(20);
		}

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Cancel a FinAdvancePaymentsDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDisbCancel() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final FinAdvancePayments aFinAdvancePayments = new FinAdvancePayments();
		BeanUtils.copyProperties(this.finAdvancePayments, aFinAdvancePayments);

		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_cancel_this_record") + "\n"
				+ Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentSequence.value") + " : "
				+ aFinAdvancePayments.getPaymentSeq();

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				String tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aFinAdvancePayments.getRecordType())) {
					aFinAdvancePayments.setVersion(aFinAdvancePayments.getVersion() + 1);
					aFinAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_DEL);

					if (isWorkFlowEnabled()) {
						aFinAdvancePayments.setRecordStatus(userAction.getSelectedItem().getValue().toString());
						aFinAdvancePayments.setNewRecord(true);
						tranType = PennantConstants.TRAN_WF;
						getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFinAdvancePayments.getNextTaskId(),
								aFinAdvancePayments);
					} else {
						tranType = PennantConstants.TRAN_DEL;
					}
				}

				try {
					if (isNewCustomer()) {
						tranType = PennantConstants.TRAN_DEL;
						AuditHeader auditHeader = newFinAdvancePaymentsProcess(aFinAdvancePayments, tranType);
						auditHeader = ErrorControl.showErrorDetails(this.window_FinAdvancePaymentsDialog, auditHeader);
						int retValue = auditHeader.getProcessStatus();
						if (retValue == PennantConstants.porcessCONTINUE
								|| retValue == PennantConstants.porcessOVERIDE) {
							if (!ImplementationConstants.VAS_INST_ON_DISB) {
								getPayOrderIssueDialogCtrl()
										.doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails, null);
							} else {
								getPayOrderIssueDialogCtrl().doFillFinAdvancePaymentsDetails(
										this.finAdvancePaymentsDetails,
										getPayOrderIssueDialogCtrl().payOrderIssueHeader.getvASRecordings());
							}

							closeDialog();
						}
					}

				} catch (DataAccessException e) {
					MessageUtil.showError(e);
				}
			}
		});

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinAdvancePayments FinAdvancePaymentsDetail
	 */
	public void doWriteBeanToComponents(FinAdvancePayments aFinAdvnancePayments) {
		logger.debug("Entering");

		if (isNewRecord()) {
			aFinAdvnancePayments.setStatus(DisbursementConstants.STATUS_NEW);
		}

		if (aFinAdvnancePayments.isNewRecord() && StringUtils.isEmpty(aFinAdvnancePayments.getPaymentDetail())) {
			aFinAdvnancePayments.setPaymentDetail(DisbursementConstants.PAYMENT_DETAIL_CUSTOMER);
		}
		List<String> list = new ArrayList<>(2);
		if (!allowMultyparty) {
			list.add(DisbursementConstants.PAYMENT_DETAIL_THIRDPARTY);
			list.add(DisbursementConstants.PAYMENT_DETAIL_VENDOR);
		}

		if (StringUtils.equalsIgnoreCase(moduleDefiner, FinServiceEvent.ADDDISB)
				&& !StringUtils.equalsIgnoreCase(aFinAdvnancePayments.getPaymentDetail(), FinServiceEvent.ADDDISB)) {
			list.add(DisbursementConstants.PAYMENT_DETAIL_VAS);
		}

		this.paymentSequence.setValue(aFinAdvnancePayments.getPaymentSeq());
		this.disbSeq.setValue(aFinAdvnancePayments.getDisbSeq());

		this.holdDisbursement.setChecked(aFinAdvnancePayments.isHoldDisbursement());
		this.leiNumber.setValue(aFinAdvnancePayments.getLei());

		fillComboBox(this.paymentDetail, aFinAdvnancePayments.getPaymentDetail(),
				PennantStaticListUtil.getPaymentDetails(), list);
		if (financeDisbursement != null) {
			int seq = aFinAdvnancePayments.getDisbSeq();
			if (financeDisbursement.size() == 1 && aFinAdvnancePayments.isNewRecord()) {
				seq = financeDisbursement.get(0).getDisbSeq();
			}
			fillComboBox(this.disbDate, seq, financeDisbursement, isNewRecord());
		}
		setDisbursmentAmount();

		this.amtToBeReleased.setValue(formate(aFinAdvnancePayments.getAmtToBeReleased()));
		if (aFinAdvnancePayments.isNewRecord() && aFinAdvnancePayments.getLlDate() == null) {
			if (!("#".equals(this.disbDate.getSelectedItem().getValue()))) {
				String[] disDate;
				if (this.disbDate.getValue().contains(",")) {
					disDate = this.disbDate.getValue().split(",");
					this.llDate.setValue(new Date(disDate[0]));
				} else {
					this.llDate.setValue(new Date(this.disbDate.getValue()));
				}

			}
		} else {
			this.llDate.setValue(aFinAdvnancePayments.getLlDate());
		}
		String excludeField = "";
		if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_INTERNAL_SETTLEMENTS)) {
			excludeField = "," + DisbursementConstants.PAYMENT_TYPE_IST + ",";
		}
		fillComboBox(this.paymentType, aFinAdvnancePayments.getPaymentType(),
				PennantStaticListUtil.getPaymentTypesWithIST(), excludeField);
		this.remarks.setValue(aFinAdvnancePayments.getRemarks());
		// banking
		if (aFinAdvnancePayments.getBankBranchID() != Long.MIN_VALUE && aFinAdvnancePayments.getBankBranchID() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", aFinAdvnancePayments.getBankBranchID());
			this.bankBranchID.setValue(StringUtils.trimToEmpty(aFinAdvnancePayments.getiFSC()));
		}
		if (aFinAdvnancePayments.getPartnerBankID() != Long.MIN_VALUE && aFinAdvnancePayments.getPartnerBankID() != 0) {
			this.partnerBankID.getButton().setDisabled(isReadOnly("FinAdvancePaymentsDialog_partnerBankID"));
			this.partnerBankID.setAttribute("partnerBankId", aFinAdvnancePayments.getPartnerBankID());
			this.partnerBankID.setValue(aFinAdvnancePayments.getPartnerbankCode(),
					aFinAdvnancePayments.getPartnerBankName());
		}

		this.bank.setValue(StringUtils.trimToEmpty(aFinAdvnancePayments.getBranchBankName()));
		this.branch.setValue(aFinAdvnancePayments.getBranchDesc());
		this.city.setValue(StringUtils.trimToEmpty(aFinAdvnancePayments.getCity()));
		this.beneficiaryAccNo.setValue(aFinAdvnancePayments.getBeneficiaryAccNo());
		this.reEnterBeneficiaryAccNo.setValue(aFinAdvnancePayments.getReEnterBeneficiaryAccNo());
		this.beneficiaryName.setValue(aFinAdvnancePayments.getBeneficiaryName());
		this.transactionRef.setValue(aFinAdvnancePayments.getTransactionRef());

		// Adding 3 fields for Disbursement Tab in loan queue
		BigDecimal disbAmount = BigDecimal.ZERO;
		BigDecimal advancePayAmount = BigDecimal.ZERO;
		BigDecimal otherExp = BigDecimal.ZERO;

		// Total Disbursed Amount
		if (financeDisbursement != null) {
			for (FinanceDisbursement curDisb : financeDisbursement) {
				if (StringUtils.equals(curDisb.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
					continue;
				}
				// exclude instruction based schedules
				if (curDisb.getLinkedDisbId() != 0) {
					continue;
				}

				disbAmount = disbAmount.add(curDisb.getDisbAmount());

				if (curDisb.getDisbDate().getTime() == financeMain.getFinStartDate().getTime()
						&& curDisb.getDisbSeq() == 1) {
					otherExp = otherExp.add(financeMain.getDeductFeeDisb());

					disbAmount = disbAmount.subtract(financeMain.getDownPayment());
					disbAmount = disbAmount.subtract(financeMain.getDeductFeeDisb());
					if (StringUtils.trimToEmpty(financeMain.getBpiTreatment())
							.equals(FinanceConstants.BPI_DISBURSMENT)) {
						disbAmount = disbAmount.subtract(financeMain.getBpiAmount());
						otherExp = otherExp.add(financeMain.getBpiAmount());
					}
				} else if (curDisb.getDisbSeq() > 1) {
					otherExp = otherExp.add(curDisb.getDeductFeeDisb());
					disbAmount = disbAmount.subtract(curDisb.getDeductFeeDisb());
				}
			}
		}

		/*
		 * if (approvedDisbursments != null && !approvedDisbursments.isEmpty()) { for (FinanceDisbursement curDisb :
		 * approvedDisbursments) { if (StringUtils.equals(curDisb.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL))
		 * { continue; } disbAmount = disbAmount.add(curDisb.getDisbAmount()); } }
		 */

		// Total amount released except current Instruction
		List<FinAdvancePayments> advPayList = null;
		if (this.moduleType.equals("LOAN")) {
			advPayList = getFinAdvancePaymentsListCtrl().getFinAdvancePaymentsList();
		} else if (this.moduleType.equals("CUSTPMTTXN")) {
			advPayList = getCustomerPaymentTxnsDialogCtrl().getFinAdvancePaymentsList();
		} else {
			advPayList = getPayOrderIssueDialogCtrl().getFinAdvancePaymentsList();
		}

		if (advPayList != null && !advPayList.isEmpty()) {
			for (FinAdvancePayments advPay : advPayList) {
				String status = advPay.getStatus();
				if (DisbursementConstants.STATUS_CANCEL.equals(status)
						|| DisbursementConstants.STATUS_REJECTED.equals(status)
						|| DisbursementConstants.STATUS_PAID_BUT_CANCELLED.equals(status)
						|| PennantConstants.RCD_DEL.equals(advPay.getRecordType())) {
					continue;
				}
				if (StringUtils.equalsIgnoreCase(advPay.getPaymentDetail(), DisbursementConstants.PAYMENT_DETAIL_VAS)) {
					continue;
				}
				advancePayAmount = advancePayAmount.add(advPay.getAmtToBeReleased());
			}
		}

		advancePayAmount = advancePayAmount.subtract(finAdvancePayments.getAmtToBeReleased());

		// Display Parameters for Summary
		this.totalDisbursementAmount.setValue(formate(disbAmount));
		this.otherExpenses.setValue(formate(otherExp));
		this.disbursementAddedAmount.setValue(formate(advancePayAmount));
		this.netAmount.setValue(formate(disbAmount.subtract(advancePayAmount)));

		// String finDisbursement ="";

		this.phoneNumber.setValue(aFinAdvnancePayments.getPhoneNumber());
		// other
		this.bankCode.setAttribute("bankCode", aFinAdvnancePayments.getBankCode());
		this.bankCode.setValue(StringUtils.trimToEmpty(aFinAdvnancePayments.getBankCode()),
				StringUtils.trimToEmpty(aFinAdvnancePayments.getBankName()));
		this.liabilityHoldName.setValue(aFinAdvnancePayments.getLiabilityHoldName());
		this.llReferenceNo.setValue(aFinAdvnancePayments.getLlReferenceNo());
		this.payableLoc.setValue(aFinAdvnancePayments.getPayableLoc());
		this.printingLoc.setValue(aFinAdvnancePayments.getPrintingLoc());
		this.printingLoc.setDescription(aFinAdvnancePayments.getPrintingLocDesc());
		this.valueDate.setValue(aFinAdvnancePayments.getValueDate());
		// unused
		this.custContribution.setValue(formate(aFinAdvnancePayments.getCustContribution()));
		this.sellerContribution.setValue(formate(aFinAdvnancePayments.getSellerContribution()));

		this.description.setValue(aFinAdvnancePayments.getDescription());
		checkPaymentType(aFinAdvnancePayments.getPaymentType());
		this.recordStatus.setValue(aFinAdvnancePayments.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aFinAdvnancePayments.getRecordType()));

		if (bankAccountValidations != null) {
			this.pennyDropResult.setValue(bankAccountValidations.isStatus() ? "Success" : "Fail");
		} else {
			this.pennyDropResult.setValue("");
		}

		if (disbAmount.compareTo(FinanceConstants.LEI_NUM_LIMIT) > 0) {
			this.leiNum.setSclass("mandatory");
			leiMandatory = true;
		}

		setDisbDocument(aFinAdvnancePayments);

		logger.debug("Leaving");
	}

	private void setDisbDocument(FinAdvancePayments aFinAdvnancePayments) {
		if (aFinAdvnancePayments.getDocImage() == null) {
			/// eastDocument
			AMedia media = externalDocumentManager.getAMedia(documentDetails);
			if (media != null) {
				this.disbDoc.setContent(media);
				eastDocument.setVisible(true);
				eastDocument.setTitle(documentDetails.getDocName());
				if (ImplementationConstants.FA_CANCEL_CHEQUE_AUTO_OPEN) {
					eastDocument.setOpen(true);
					eastDocument.setCollapsible(false);
				}
			} else {
				String referenceId = String.valueOf(aFinAdvnancePayments.getPaymentId());
				DocumentDetails documentDetails = documentDetailsDAO.getDocumentDetails(referenceId,
						DisbursementConstants.DISB_DOC_TYPE, DisbursementConstants.DISB_MODULE,
						TableType.MAIN_TAB.getSuffix());
				if (documentDetails != null) {
					AMedia amedia = externalDocumentManager.getAMedia(documentDetails);
					eastDocument.setVisible(true);
					imagebyte = amedia.getByteData();
					this.disbDoc.setContent(amedia);
					this.documentName.setValue(documentDetails.getDocName());
				}
			}
		}
		AMedia amedia = null;
		if (aFinAdvnancePayments.getDocImage() != null) {
			amedia = new AMedia(aFinAdvnancePayments.getDocumentName(), null, null, aFinAdvnancePayments.getDocImage());
			imagebyte = aFinAdvnancePayments.getDocImage();
			this.disbDoc.setContent(amedia);
			this.documentName.setValue(aFinAdvnancePayments.getDocumentName());
			eastDocument.setVisible(true);
		}

		List<ValueLabel> vasReferenceList = getVasReferences(aFinAdvnancePayments.getVasReference(),
				aFinAdvnancePayments.getAmtToBeReleased());
		fillComboBox(this.vasReference, aFinAdvnancePayments.getVasReference(), vasReferenceList, "");
		doChangePaymentDetails(aFinAdvnancePayments.getPaymentDetail());
		if (DisbursementConstants.PAYMENT_DETAIL_VAS.equals(aFinAdvnancePayments.getPaymentDetail())) {
			this.vasAmount.setValue(
					CurrencyUtil.parse(vasAmountsMAP.get(aFinAdvnancePayments.getVasReference()), ccyFormatter));
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to fill the combobox with given list of values and will exclude the the values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillComboBox(Combobox combobox, int seq, List<FinanceDisbursement> list, boolean execuledApprove) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (FinanceDisbursement disbursement : list) {
			if (execuledApprove && isContainsInAppList(disbursement)) {
				continue;
			}
			// cancelled disbursement should not be allowed to process
			if (StringUtils.trimToEmpty(disbursement.getDisbStatus()).equals(FinanceConstants.DISB_STATUS_CANCEL)) {
				continue;
			}

			comboitem = new Comboitem();
			String label = DateUtil.formatToLongDate(disbursement.getDisbDate());
			label = label.concat(" , ") + disbursement.getDisbSeq();
			comboitem.setLabel(label);
			comboitem.setValue(disbursement.getDisbDate());
			comboitem.setAttribute("data", disbursement);
			combobox.appendChild(comboitem);
			if (seq == disbursement.getDisbSeq()) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillComboBox()");
	}

	private boolean isContainsInAppList(FinanceDisbursement disbursement) {
		if (approvedDisbursments != null && !approvedDisbursments.isEmpty()) {
			for (FinanceDisbursement financeDisbursement : approvedDisbursments) {
				if (disbursement.getDisbDate().getTime() == financeDisbursement.getDisbDate().getTime()
						&& disbursement.getDisbSeq() == financeDisbursement.getDisbSeq()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * To avoid length of the line
	 * 
	 * @param amt
	 * @return
	 */
	private BigDecimal formate(BigDecimal amt) {
		return PennantApplicationUtil.formateAmount(amt, ccyFormatter);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinAdvancePayments
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(FinAdvancePayments aFinAdvancePayments) throws InterruptedException {
		logger.debug("Entering");
		String paymentType = "";

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		if (this.btnReverse.isVisible()) {
			aFinAdvancePayments.setStatus(DisbursementConstants.STATUS_REVERSED);
			aFinAdvancePayments.setReversedDate(SysParamUtil.getAppDate());
		}

		try {
			aFinAdvancePayments.setPaymentSeq(this.paymentSequence.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if ("#".equals(getComboboxValue(this.disbDate))) {
				throw new WrongValueException(this.disbDate, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinAdvancePaymentsDialog_DisbDate.value") }));

			} else {
				Comboitem select = this.disbDate.getSelectedItem();
				FinanceDisbursement disbursement = (FinanceDisbursement) select.getAttribute("data");
				aFinAdvancePayments.setDisbSeq(disbursement.getDisbSeq());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.paymentDetail))) {
				if (this.paymentDetail.isVisible() && !this.paymentDetail.isDisabled()) {
					throw new WrongValueException(this.paymentDetail, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentDetail.value") }));
				} else {
					aFinAdvancePayments.setPaymentDetail(null);
				}
			} else {
				aFinAdvancePayments.setPaymentDetail(getComboboxValue(this.paymentDetail));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.paymentDetail))) {
				if (this.paymentDetail.isVisible() && !this.paymentDetail.isDisabled()) {
					throw new WrongValueException(this.paymentDetail, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentDetail.value") }));
				} else {
					aFinAdvancePayments.setPaymentDetail(null);
				}
			} else {
				aFinAdvancePayments.setPaymentDetail(getComboboxValue(this.paymentDetail));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setLiabilityHoldName(this.liabilityHoldName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setAmtToBeReleased(PennantApplicationUtil
					.unFormateAmount(this.amtToBeReleased.isReadonly() ? this.amtToBeReleased.getActualValue()
							: this.amtToBeReleased.getValidateValue(), ccyFormatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setCustContribution(
					PennantApplicationUtil.unFormateAmount(this.custContribution.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setSellerContribution(
					PennantApplicationUtil.unFormateAmount(this.sellerContribution.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setBeneficiaryAccNo(this.beneficiaryAccNo.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (gb_NeftDetails.isVisible()) {
				aFinAdvancePayments.setReEnterBeneficiaryAccNo(this.reEnterBeneficiaryAccNo.getValue());
				if (reEntrBenfAccNo && !StringUtils.equals(this.beneficiaryAccNo.getValue(),
						this.reEnterBeneficiaryAccNo.getValue())) {
					throw new WrongValueException(this.reEnterBeneficiaryAccNo,
							Labels.getLabel("FIELD_NOT_MATCHED", new String[] {
									Labels.getLabel("label_FinAdvancePaymentsDialog_BeneficiaryAccNo.value"),
									Labels.getLabel("label_FinAdvancePaymentsDialog_ReEnterBeneficiaryAccNo.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			paymentType = getComboboxValue(this.paymentType);
			if ("#".equals(paymentType)) {
				if (this.paymentType.isVisible() && !this.paymentType.isDisabled()) {
					throw new WrongValueException(this.paymentType, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentType.value") }));
				} else {
					aFinAdvancePayments.setPaymentType(null);
				}
			} else {
				aFinAdvancePayments.setPaymentType(paymentType);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Comboitem item = this.disbDate.getSelectedItem();
			if (item != null) {
				Object data = item.getAttribute("data");
				if (data != null) {
					FinanceDisbursement disbursement = (FinanceDisbursement) data;
					if (this.llDate.getValue() != null) {
						if (financeMain != null && financeMain.getMaturityDate() != null) {
							if (this.llDate.getValue().before(disbursement.getDisbDate())
									|| this.llDate.getValue().after(financeMain.getMaturityDate())) {

								String maturityDate = DateUtil.formatToLongDate(financeMain.getMaturityDate());
								String disbDate = DateUtil.formatToLongDate(disbursement.getDisbDate());

								throw new WrongValueException(this.llDate,
										Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL",
												new String[] {
														Labels.getLabel("label_FinAdvancePaymentsDialog_LLDate.value"),
														disbDate, maturityDate }));
							}
						}
						aFinAdvancePayments.setLLDate(this.llDate.getValue());
					}
					if (!DisbursementConstants.PAYMENT_DETAIL_VAS.equals(aFinAdvancePayments.getPaymentDetail())) {
						BigDecimal disAmt = BigDecimal.ZERO;
						disAmt = DisbursementInstCtrl.getTotalByDisbursment(disbursement, financeMain);
						BigDecimal insAmt = getAdjustedAmount(disbursement);
						insAmt = insAmt.add(aFinAdvancePayments.getAmtToBeReleased());
						if (insAmt.compareTo(disAmt) > 0) {
							throw new WrongValueException(this.amtToBeReleased,
									Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] {
											Labels.getLabel("label_FinAdvancePaymentsDialog_AmtToBeReleased.value"),
											Labels.getLabel("label_FinAdvancePaymentsDialog_DisbAmount.value") }));
						} else {

							if (financeMain.isInstBasedSchd()
									&& StringUtils.equals(FinServiceEvent.ADDDISB, moduleDefiner)
									&& insAmt.compareTo(disAmt) != 0) {
								throw new WrongValueException(this.amtToBeReleased,
										Labels.getLabel("NUMBER_EQ", new String[] {
												Labels.getLabel("label_FinAdvancePaymentsDialog_AmtToBeReleased.value"),
												Labels.getLabel("label_FinAdvancePaymentsDialog_DisbAmount.value") }));
							}
						}
					}

				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setBeneficiaryName(this.beneficiaryName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setLLReferenceNo(this.llReferenceNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setHoldDisbursement(this.holdDisbursement.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setLei(this.leiNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.bankCode.getValidatedValue();
			Object obj = this.bankCode.getAttribute("bankCode");
			if (obj != null) {
				aFinAdvancePayments.setBankCode(String.valueOf(obj));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAdvancePayments.setPayableLoc(this.payableLoc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			boolean mandatory = false;
			if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
					|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {
				mandatory = true;
			}

			this.printingLoc.clearErrorMessage();

			this.printingLoc.setErrorMessage("");

			if (!this.printingLoc.isReadonly()) {
				this.printingLoc.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinAdvancePaymentsDialog_PrintingLoc.value"), null, mandatory));
			}
			aFinAdvancePayments.setPrintingLoc(this.printingLoc.getValue());
			aFinAdvancePayments.setPrintingLocDesc(this.printingLoc.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.valueDate.getValue() != null) {
				aFinAdvancePayments.setValueDate(this.valueDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.bankBranchID.getValidatedValue();
			Object obj = this.bankBranchID.getAttribute("bankBranchID");
			if (obj != null) {
				aFinAdvancePayments.setBankBranchID(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			boolean mandatory = false;
			if (DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)) {
				mandatory = true;
			}

			this.phoneNumber.clearErrorMessage();

			this.phoneNumber.setErrorMessage("");

			if (!this.phoneNumber.isReadonly()) {
				this.phoneNumber.setConstraint(new PTMobileNumberValidator(
						Labels.getLabel("label_FinAdvancePaymentsDialog_PhoneNumber.value"), mandatory));
			}

			aFinAdvancePayments.setPhoneNumber(this.phoneNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAdvancePayments.setDisbCCy(finCcy);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.partnerBankID.getValidatedValue();
			Object obj = this.partnerBankID.getAttribute("partnerBankId");
			if (obj != null) {
				aFinAdvancePayments.setPartnerBankID(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setTransactionRef(this.transactionRef.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// VAS Front End Functionality
		if (DisbursementConstants.PAYMENT_DETAIL_VAS.equals(aFinAdvancePayments.getPaymentDetail())) {
			try {
				aFinAdvancePayments.setVasReference(getComboboxValue(this.vasReference));
				if (this.row_vasReference.isVisible()) {
					if (StringUtils.isBlank(aFinAdvancePayments.getVasReference())
							|| PennantConstants.List_Select.equals(aFinAdvancePayments.getVasReference())) {
						if (!this.vasReference.isDisabled()) {
							throw new WrongValueException(this.vasReference,
									Labels.getLabel("FIELD_IS_MAND", new String[] {
											Labels.getLabel("label_FinAdvancePaymentsDialog_VasReference.value") }));
						}
						if (this.vasReference.isDisabled() && !this.paymentDetail.isDisabled()) {
							throw new WrongValueException(this.paymentDetail,
									Labels.getLabel("STATIC_INVALID_DISBPARTY", new String[] {
											Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentDetail.value") }));
						}
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (StringUtils.isNotBlank(aFinAdvancePayments.getVasReference())
						&& !PennantConstants.List_Select.equals(aFinAdvancePayments.getVasReference())) {
					if (vasAmountsMAP.get(aFinAdvancePayments.getVasReference())
							.compareTo(aFinAdvancePayments.getAmtToBeReleased()) != 0) {
						throw new WrongValueException(this.amtToBeReleased,
								Labels.getLabel("NUMBER_EQUAL", new String[] {
										Labels.getLabel("label_FinAdvancePaymentsDialog_AmtToBeReleased.value"),
										Labels.getLabel("label_FinAdvancePaymentsDialog_VasReferenceAmount.value") }));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			if (aFinAdvancePayments.isNewRecord()
					&& isDuplicateVasInstruvtions(aFinAdvancePayments.getVasReference())) {
				try {
					if (!this.vasReference.isDisabled()) {
						throw new WrongValueException(this.vasReference,
								Labels.getLabel("VAS_INSTRUCTION_ALREADY_EXISTE",
										new String[] { aFinAdvancePayments.getVasReference() }));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}

		} else {
			aFinAdvancePayments.setVasReference(null);
		}

		// DocumentName
		try {
			aFinAdvancePayments.setDocumentName(this.documentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Document Image
		try {
			aFinAdvancePayments.setDocImage(this.imagebyte);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		Object obj = null;
		obj = this.partnerBankID.getAttribute("partnerBankAc");
		if (obj != null) {
			aFinAdvancePayments.setPartnerBankAc(String.valueOf(obj));
		}

		obj = this.partnerBankID.getAttribute("partnerBankAcType");
		if (obj != null) {
			aFinAdvancePayments.setPartnerBankAcType(String.valueOf(obj));
		}

		aFinAdvancePayments.setLinkedTranId(0);
		doRemoveValidation();
		doClearMessage();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aFinAdvancePayments.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Change the partnerBankID for the Account on changing the finance Branch
	 * 
	 * @param event
	 */
	public void onFulfill$partnerBankID(Event event) {
		logger.debug("Entering");
		Object dataObject = partnerBankID.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.partnerBankID.setValue(dataObject.toString());
				this.partnerBankID.setDescription("");
			}

		} else {
			FinTypePartnerBank partnerBank = (FinTypePartnerBank) dataObject;
			if (partnerBank != null) {
				this.partnerBankID.setAttribute("partnerBankId", partnerBank.getPartnerBankID());
				this.finAdvancePayments.setPartnerbankCode(partnerBank.getPartnerBankCode());
				this.finAdvancePayments.setPartnerBankName(partnerBank.getPartnerBankName());
				this.partnerBankID.setAttribute("partnerBankAc", partnerBank.getAccountNo());
				this.partnerBankID.setAttribute("partnerBankAcType", partnerBank.getAccountType());
				// Van Related Code Removed
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.paymentDetail.isDisabled()) {
			this.paymentDetail.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentDetail.value"), null, true));
		}
		if (!this.amtToBeReleased.isDisabled()) {
			this.amtToBeReleased.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_AmtToBeReleased.value"),
							ccyFormatter, true, false));
		}
		String payType = getComboboxValue(paymentType);
		if (!this.paymentType.isDisabled()) {
			this.paymentType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentType.value"), null, true));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_Description.value"), null, false));
		}
		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_Remarks.value"), null, false));
		}
		if (this.hbox_llReferenceNo.isVisible() && !this.llReferenceNo.isReadonly()) {
			this.llReferenceNo.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_LLReferenceNo.value"), null, false));
		}
		if (this.hbox_llDate.isVisible() && !this.llDate.isDisabled()) {
			this.llDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_LLDate.value"), true));
		}
		if (this.hbox_custContribution.isVisible() && !this.custContribution.isDisabled()) {
			this.custContribution.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_CustContribution.value"),
							ccyFormatter, false, false));
		}
		if (this.hbox_sellerContribution.isVisible() && !this.sellerContribution.isDisabled()) {
			this.sellerContribution.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_SellerContribution.value"),
							ccyFormatter, false, false));
		}

		if (!this.partnerBankID.isReadonly()) {
			this.partnerBankID.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_PartnerbankId.value"), null, true));
		}

		if (gb_ChequeDetails.isVisible()) {
			if (this.hbox_liabilityHoldName.isVisible() && !this.liabilityHoldName.isReadonly()) {
				this.liabilityHoldName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_LiabilityHoldName.value"),
								PennantRegularExpressions.REGEX_FAVOURING_NAME, true));
			}
			if (!this.bankCode.isReadonly()) {
				this.bankCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinAdvancePaymentsDialog_BankCode.value"), null, true));
			}
			if (!this.payableLoc.isReadonly()) {
				this.payableLoc.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_PayableLoc.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}
			/*
			 * if (!this.printingLoc.isReadonly()) { this.printingLoc.setConstraint( new
			 * PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_PrintingLoc.value"),
			 * PennantRegularExpressions.REGEX_ADDRESS, false)); }
			 */
			if (!this.valueDate.isDisabled()) {
				Date todate = DateUtil.addMonths(SysParamUtil.getAppDate(), 6);
				this.valueDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_ValueDate.value"), true,
								SysParamUtil.getAppDate(), todate, true));
			}
			if (ImplementationConstants.CHEQUENO_MANDATORY_DISB_INS && this.hbox_llReferenceNo.isVisible()
					&& !this.llReferenceNo.isReadonly()) {
				this.llReferenceNo.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_LLReferenceNo.value"),
								PennantRegularExpressions.NUMERIC_FL6_REGEX, true));
			}
		} else {
			if (!StringUtils.equals(payType, DisbursementConstants.PAYMENT_TYPE_IST)) {
				if (!this.bankBranchID.isReadonly()) {
					this.bankBranchID.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinAdvancePaymentsDialog_BankBranchID.value"), null, true));
				}
				if (!this.beneficiaryName.isReadonly()) {
					this.beneficiaryName.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinAdvancePaymentsDialog_BeneficiaryName.value"),
							PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME, true));
				}
				if (!this.beneficiaryAccNo.isReadonly()) {
					this.beneficiaryAccNo.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinAdvancePaymentsDialog_BeneficiaryAccNo.value"),
							PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true, minAccNoLength, maxAccNoLength));
				}
			}
		}
		if (this.row_ReEnterBenfAccNo.isVisible() && !this.reEnterBeneficiaryAccNo.isReadonly()) {
			this.reEnterBeneficiaryAccNo.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_ReEnterBeneficiaryAccNo.value"),
					PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true));
		}

		if (this.leiNumber.isVisible() && !this.leiNumber.getValue().isEmpty()) {
			this.leiNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_LEI.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, false));
		}

		if (leiMandatory && this.leiNumber.getValue().isEmpty()) {
			this.leiNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_LEI.value"), null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.paymentDetail.setConstraint("");
		this.liabilityHoldName.setConstraint("");
		this.amtToBeReleased.setConstraint("");
		this.beneficiaryAccNo.setConstraint("");
		this.paymentType.setConstraint("");
		this.beneficiaryName.setConstraint("");
		this.leiNumber.setConstraint("");
		this.description.setConstraint("");
		this.llReferenceNo.setConstraint("");
		this.llDate.setConstraint("");
		this.custContribution.setConstraint("");
		this.sellerContribution.setConstraint("");
		this.remarks.setConstraint("");
		this.bankCode.setConstraint("");
		this.payableLoc.setConstraint("");
		this.printingLoc.setConstraint("");
		this.valueDate.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.partnerBankID.setConstraint("");
		this.phoneNumber.setConstraint("");
		this.transactionRef.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.paymentDetail.setErrorMessage("");
		this.liabilityHoldName.setErrorMessage("");
		this.amtToBeReleased.setErrorMessage("");
		this.beneficiaryAccNo.setErrorMessage("");
		this.paymentType.setErrorMessage("");
		this.beneficiaryName.setErrorMessage("");
		this.description.setErrorMessage("");
		this.llReferenceNo.setErrorMessage("");
		this.llDate.setErrorMessage("");
		this.custContribution.setErrorMessage("");
		this.sellerContribution.setErrorMessage("");
		this.bankCode.setErrorMessage("");
		this.payableLoc.setErrorMessage("");
		this.printingLoc.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.bankBranchID.setErrorMessage("");
		this.partnerBankID.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.transactionRef.setErrorMessage("");
		logger.debug("Leaving");
	}

	protected boolean doCustomDelete(final FinAdvancePayments aFinAdvancePayments, String tranType) {
		if (isNewCustomer()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newFinAdvancePaymentsProcess(aFinAdvancePayments, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FinAdvancePaymentsDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (this.moduleType.equals("LOAN")) {
					getFinAdvancePaymentsListCtrl().doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails,
							vasRecordingList);
				} else {
					if (!ImplementationConstants.VAS_INST_ON_DISB) {
						getPayOrderIssueDialogCtrl().doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails,
								null);
					} else {
						if (this.moduleType.equals("POISSUE")) {
							getPayOrderIssueDialogCtrl().doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails,
									null);
						} else {
							getPayOrderIssueDialogCtrl().doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails,
									getPayOrderIssueDialogCtrl().payOrderIssueHeader.getvASRecordings());
						}
					}

				}
				return true;
			}
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FinAdvancePayments aFinAdvancePayments = new FinAdvancePayments();
		BeanUtils.copyProperties(this.finAdvancePayments, aFinAdvancePayments);

		final String keyReference = Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentSequence.value") + " : "
				+ aFinAdvancePayments.getPaymentSeq();

		doDelete(keyReference, aFinAdvancePayments);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.paymentDetail.setValue("");
		this.liabilityHoldName.setValue("");
		this.beneficiaryName.setValue("");
		this.description.setValue("");
		this.amtToBeReleased.setValue("");
		this.paymentType.setValue("");
		this.llReferenceNo.setValue("");
		this.llDate.setText("");
		this.custContribution.setValue("");
		this.sellerContribution.setValue("");
		this.remarks.setValue("");
		this.bankCode.setValue("");
		this.bankBranchID.setValue("");
		this.payableLoc.setValue("");
		this.printingLoc.setValue("");
		this.bank.setValue("");
		this.branch.setValue("");
		this.city.setValue("");
		this.transactionRef.setValue("");
		this.valueDate.setText("");
		this.documentName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinAdvancePayments aFinAdvancePayments = new FinAdvancePayments();
		BeanUtils.copyProperties(this.finAdvancePayments, aFinAdvancePayments);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aFinAdvancePayments.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFinAdvancePayments.getNextTaskId(),
					aFinAdvancePayments);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aFinAdvancePayments.getRecordType()) && isValidation()) {
			doClearMessage();
			doSetValidation();
			// fill the FinAdvancePaymentsDetail object with the components data
			doWriteComponentsToBean(aFinAdvancePayments);
		}

		isNew = aFinAdvancePayments.isNewRecord();

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinAdvancePayments.getRecordType())) {
				aFinAdvancePayments.setVersion(aFinAdvancePayments.getVersion() + 1);
				if (isNew) {
					aFinAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinAdvancePayments.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aFinAdvancePayments.setVersion(1);
					aFinAdvancePayments.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aFinAdvancePayments.getRecordType())) {
					aFinAdvancePayments.setVersion(aFinAdvancePayments.getVersion() + 1);
					aFinAdvancePayments.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aFinAdvancePayments.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aFinAdvancePayments.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aFinAdvancePayments.setVersion(aFinAdvancePayments.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {

			if (isNewCustomer()) {
				AuditHeader auditHeader = newFinAdvancePaymentsProcess(aFinAdvancePayments, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinAdvancePaymentsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (this.moduleType.equals("LOAN")) {
						getFinAdvancePaymentsListCtrl().doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails,
								vasRecordingList);
					} else {
						if (!ImplementationConstants.VAS_INST_ON_DISB) {
							getPayOrderIssueDialogCtrl().doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails,
									null);
						} else {
							if (this.moduleType.equals("POISSUE")) {
								getPayOrderIssueDialogCtrl()
										.doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails, null);
							} else {
								getPayOrderIssueDialogCtrl().doFillFinAdvancePaymentsDetails(
										this.finAdvancePaymentsDetails,
										getPayOrderIssueDialogCtrl().payOrderIssueHeader.getvASRecordings());
							}
						}
					}
					closeDialog();
				}
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private ArrayList<ErrorDetail> validate(List<FinAdvancePayments> list, FinAdvancePayments advancePayments) {

		ArrayList<ErrorDetail> errors = new ArrayList<ErrorDetail>();
		if (list != null && !list.isEmpty()) {
			String validateAcNumber = StringUtils.trimToEmpty(advancePayments.getBeneficiaryAccNo());
			if (StringUtils.isEmpty(validateAcNumber)) {
				return errors;
			}
		}
		return errors;
	}

	private AuditHeader newFinAdvancePaymentsProcess(FinAdvancePayments afinAdvancePayments, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(afinAdvancePayments, tranType);
		finAdvancePaymentsDetails = new ArrayList<FinAdvancePayments>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(afinAdvancePayments.getFinReference());
		valueParm[1] = Integer.toString(afinAdvancePayments.getPaymentSeq());

		errParm[0] = PennantJavaUtil.getLabel("FinAdvancePayments_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("FinAdvancePayments_PaymentSeq") + ":" + valueParm[1];

		List<FinAdvancePayments> listAdvance = null;
		if (this.moduleType.equals("LOAN")) {
			listAdvance = getFinAdvancePaymentsListCtrl().getFinAdvancePaymentsList();
		} else {
			listAdvance = getPayOrderIssueDialogCtrl().getFinAdvancePaymentsList();
		}
		ArrayList<ErrorDetail> erroe = validate(listAdvance, afinAdvancePayments);
		if (!erroe.isEmpty()) {
			auditHeader.setErrorList(ErrorUtil.getErrorDetails(erroe, getUserWorkspace().getUserLanguage()));
			return auditHeader;
		}

		if (listAdvance != null && listAdvance.size() > 0) {
			for (int i = 0; i < listAdvance.size(); i++) {
				FinAdvancePayments loanDetail = listAdvance.get(i);

				if (afinAdvancePayments.getPaymentSeq() == loanDetail.getPaymentSeq()) { // Both Current and Existing
																							// list rating same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(afinAdvancePayments.getRecordType())) {
							afinAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finAdvancePaymentsDetails.add(afinAdvancePayments);
						} else if (PennantConstants.RCD_ADD.equals(afinAdvancePayments.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(afinAdvancePayments.getRecordType())) {
							afinAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finAdvancePaymentsDetails.add(afinAdvancePayments);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(afinAdvancePayments.getRecordType())) {
							recordAdded = true;
							List<FinAdvancePayments> listAdvanceApproved = null;
							if ("LOAN".equals(this.moduleType)) {
								listAdvanceApproved = getFinAdvancePaymentsListCtrl().getFinancedetail()
										.getAdvancePaymentsList();
							} else {
								listAdvanceApproved = getPayOrderIssueDialogCtrl().getPayOrderIssueHeader()
										.getFinAdvancePaymentsList();
							}
							for (int j = 0; j < listAdvanceApproved.size(); j++) {
								FinAdvancePayments detail = listAdvanceApproved.get(j);
								if (detail.getFinReference() == afinAdvancePayments.getFinReference()
										&& detail.getPaymentSeq() == afinAdvancePayments.getPaymentSeq()) {
									finAdvancePaymentsDetails.add(detail);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							finAdvancePaymentsDetails.add(loanDetail);
						}
					}
				} else {
					finAdvancePaymentsDetails.add(loanDetail);
				}
			}
		}

		if (!recordAdded) {
			finAdvancePaymentsDetails.add(afinAdvancePayments);
		}
		return auditHeader;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FinAdvancePayments aFinAdvancePayments, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinAdvancePayments.getBefImage(), aFinAdvancePayments);
		return new AuditHeader(aFinAdvancePayments.getFinReference(), null, null, null, auditDetail,
				aFinAdvancePayments.getUserDetails(), getOverideMap());
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = bankBranchID.getObject();

		if (dataObject instanceof String) {
			this.bankBranchID.setValue(dataObject.toString());
			this.bank.setValue("");
			this.city.setValue("");
			this.branch.setValue("");
			this.finAdvancePayments.setBranchBankCode("");
		} else {
			BankBranch details = (BankBranch) dataObject;

			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bank.setValue(details.getBankName());
				this.finAdvancePayments.setCity(details.getCity());
				this.finAdvancePayments.setBranchBankCode(details.getBankCode());
				this.finAdvancePayments.setBranchBankName(details.getBankName());
				this.finAdvancePayments.setBranchDesc(details.getBranchDesc());
				this.finAdvancePayments.setiFSC(details.getIFSC());
				this.city.setValue(details.getCity());
				this.branch.setValue(details.getBranchDesc());
				this.bankBranchID.setValue(details.getIFSC());
				if (StringUtils.isNotBlank(details.getBankCode())) {
					bankdetail = bankDetailService.getAccNoLengthByCode(details.getBankCode());
					this.maxAccNoLength = this.bankdetail.getAccNoLength();
					this.minAccNoLength = this.bankdetail.getMinAccNoLength();
				}
				this.beneficiaryAccNo.setMaxlength(maxAccNoLength);

				String benificiaryActLen = SysParamUtil.getValueAsString(SMTParameterConstants.BEN_ACTNAME_LENGTH);
				if (benificiaryActLen != null) {
					this.beneficiaryName.setMaxlength(40);
					this.beneficiaryAccNo.setMaxlength(20);
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$bankCode(Event event) {
		logger.debug("Entering" + event.toString());
		this.printingLoc.setValue("");
		Object dataObject = bankCode.getObject();
		String paymentType = getComboboxValue(this.paymentType);
		if (dataObject instanceof String) {
			this.bankCode.setValue(dataObject.toString());
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankCode.setAttribute("bankCode", details.getBankCode());
				this.finAdvancePayments.setBankName(details.getBankName());
				Filter[] filters = new Filter[2];
				filters[0] = new Filter("BankCode", ((BankDetail) dataObject).getBankCode(), Filter.OP_EQUAL);
				if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)) {
					filters[1] = new Filter("Cheque", true, Filter.OP_EQUAL);
				}
				if (DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {
					filters[1] = new Filter("DD", true, Filter.OP_EQUAL);
				}
				this.printingLoc.setFilters(filters);
			}
		}
	}

	public void onChange$paymentType(Event event) {
		logger.debug(Literal.ENTERING);
		String dType = this.paymentType.getSelectedItem().getValue().toString();
		String disbursementParty = this.paymentDetail.getSelectedItem().getValue().toString();

		if (DisbursementConstants.PAYMENT_DETAIL_BUILDER.equals(disbursementParty)) {
			doFillBenificaryDetails(true);
		} else {
			doFillBenificaryDetails(false);
		}

		this.partnerBankID.setButtonDisabled(false);
		this.partnerBankID.setReadonly(false);

		doLoadPartnerbankData();

		Filter[] filtersPrintLoc = new Filter[1];
		if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(dType)) {
			filtersPrintLoc[0] = new Filter("Cheque", true, Filter.OP_EQUAL);
			this.printingLoc.setValue("");
			this.bankCode.setValue("");
			this.printingLoc.setFilters(filtersPrintLoc);
		}
		if (DisbursementConstants.PAYMENT_TYPE_DD.equals(dType)) {
			filtersPrintLoc[0] = new Filter("DD", true, Filter.OP_EQUAL);
			this.printingLoc.setValue("");
			this.bankCode.setValue("");
			this.printingLoc.setFilters(filtersPrintLoc);
		}

		checkPaymentType(dType);
		logger.debug(Literal.LEAVING);
	}

	private void doLoadPartnerbankData() {
		String paymentMode = this.paymentType.getSelectedItem().getValue().toString();

		if (!PartnerBankExtension.BRANCH_WISE_MAPPING) {
			Filter[] filters = new Filter[4];
			filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
			filters[1] = new Filter("Purpose", "D", Filter.OP_EQUAL);
			filters[2] = new Filter("PaymentMode", paymentMode, Filter.OP_EQUAL);
			filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);

			this.partnerBankID.setFilters(filters);

			this.partnerBankID.setButtonDisabled(false);
			this.partnerBankID.setReadonly(false);
			this.partnerBankID.setValue("");
			this.partnerBankID.setDescription("");

			return;
		}

		String finBranch = financeMain.getFinBranch();
		Long clusterId = null;

		Filter[] filters = new Filter[4];
		filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
		filters[1] = new Filter("Purpose", "D", Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", paymentMode, Filter.OP_EQUAL);

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			filters[3] = new Filter("BranchCode", finBranch, Filter.OP_EQUAL);

		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			clusterId = clusterService.getClustersFilter(finBranch);
			filters[3] = new Filter("ClusterId", clusterId, Filter.OP_EQUAL);
		}

		FinTypePartnerBank fpb = new FinTypePartnerBank();
		fpb.setFinType(financeMain.getFinType());
		fpb.setPurpose("D");
		fpb.setPaymentMode(paymentMode);
		fpb.setBranchCode(finBranch);
		fpb.setClusterId(clusterId);

		List<FinTypePartnerBank> list = finTypePartnerBankService.getFinTypePartnerBanks(fpb);

		if (list.size() == 1) {
			fpb = list.get(0);
			this.partnerBankID.setAttribute("partnerBankId", fpb.getPartnerBankID());
			this.finAdvancePayments.setPartnerbankCode(fpb.getPartnerBankCode());
			this.finAdvancePayments.setPartnerBankName(fpb.getPartnerBankName());
			this.partnerBankID.setAttribute("partnerBankAc", fpb.getAccountNo());
			this.partnerBankID.setAttribute("partnerBankAcType", fpb.getAccountType());
			this.partnerBankID.setValue(fpb.getPartnerBankCode(), fpb.getPartnerBankName());
		}

		this.partnerBankID.setFilters(filters);

		this.partnerBankID.setButtonDisabled(false);
		this.partnerBankID.setReadonly(false);
		this.partnerBankID.setValue("");
		this.partnerBankID.setDescription("");
	}

	public void onChange$paymentDetail(Event event) {
		logger.debug(Literal.ENTERING);
		String disbursementType = StringUtils.trimToNull(getComboboxValue(this.paymentType));
		String disbursementParty = StringUtils.trimToNull(this.paymentDetail.getSelectedItem().getValue().toString());

		if (PennantConstants.List_Select.equals(disbursementType)
				|| PennantConstants.List_Select.equals(disbursementParty)) {
			return;
		}

		/*
		 * if (!PennantConstants.List_Select.equals(disbursementType) &&
		 * DisbursementConstants.PAYMENT_DETAIL_BUILDER.equals(disbursementParty)) { this.bankBranchID.setValue("");
		 * this.finAdvancePayments.setCity(""); this.finAdvancePayments.setBranchBankName("");
		 * this.finAdvancePayments.setBranchBankCode(""); this.finAdvancePayments.setBranchDesc("");
		 * this.finAdvancePayments.setiFSC(""); this.bank.setValue(""); this.branch.setValue("");
		 * this.beneficiaryAccNo.setValue(""); this.beneficiaryName.setValue(""); this.city.setValue("");
		 * this.phoneNumber.setValue("");
		 * 
		 * return; }
		 */

		if (DisbursementConstants.PAYMENT_DETAIL_BUILDER.equals(disbursementParty)) {
			populateBenfiryDetails = true;
			doFillBenificaryDetails(populateBenfiryDetails);
		} else {
			doChangePaymentDetails(disbursementParty);
			this.bankBranchID.setValue("");
			this.finAdvancePayments.setCity("");
			this.finAdvancePayments.setBranchBankName("");
			this.finAdvancePayments.setBranchBankCode("");
			this.finAdvancePayments.setBranchDesc("");
			this.finAdvancePayments.setiFSC("");
			this.bank.setValue("");
			this.branch.setValue("");
			this.beneficiaryAccNo.setValue("");
			this.beneficiaryName.setValue("");
			this.city.setValue("");
			this.phoneNumber.setValue("");
		}

		logger.debug(Literal.LEAVING);
	}

	public void doFillBenificaryDetails(boolean populateBenfiryDetails) {
		logger.debug(Literal.ENTERING);

		if (populateBenfiryDetails) {
			List<CollateralAssignment> collateralAsgmntList = null;

			if (finAdvancePaymentsListCtrl != null && finAdvancePaymentsListCtrl.getFinancedetail() != null) {
				collateralAsgmntList = finAdvancePaymentsListCtrl.getFinancedetail().getCollateralAssignmentList();
			} else {
				collateralAsgmntList = collateralAssignmentDAO.getCollateralAssignmentByFinRef(
						financeMain.getFinReference(), FinanceConstants.MODULE_NAME, "_AView");
			}

			if (CollectionUtils.isEmpty(collateralAsgmntList)) {
				logger.debug(Literal.LEAVING);
				return;
			}

			String colateralType = collateralAsgmntList.get(0).getCollateralType();
			String collaterlref = collateralAsgmntList.get(0).getCollateralRef();

			String builderID = null;
			if (colateralType != null && collaterlref != null) {
				StringBuilder tableName = new StringBuilder();
				tableName.append(CollateralConstants.MODULE_NAME);
				tableName.append("_");
				tableName.append(colateralType);
				tableName.append("_ED");

				Map<String, Object> finalMap = new HashMap<>();
				Map<String, Object> originalMap = extendedFieldRenderDAO.getCollateralMap(collaterlref,
						tableName.toString(), "_View");

				if (MapUtils.isNotEmpty(originalMap)) {
					for (Entry<String, Object> object : originalMap.entrySet()) {
						finalMap.put(object.getKey().toLowerCase(), object.getValue());
					}
				}

				builderID = (String) finalMap.get("projectname");
			}

			BuilderProjcet builderProjcet = null;
			if (StringUtils.isNotBlank(builderID)) {
				builderProjcet = builderProjcetDAO.getBuilderProjcet(NumberUtils.toLong(builderID), "");
			}

			if (builderProjcet == null) {
				logger.debug(Literal.LEAVING);
				return;
			}

			BankBranch bankBranch = bankBranchDAO.getBankBranchById(builderProjcet.getBankBranchID(), "_View");

			if (bankBranch == null) {
				logger.debug(Literal.LEAVING);
				return;
			}

			String dType = this.paymentType.getSelectedItem().getValue().toString();

			if (DisbursementConstants.PAYMENT_TYPE_DD.equals(dType)
					|| DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(dType)) {
				this.liabilityHoldName.setValue(builderProjcet.getBeneficiaryName());
				logger.debug(Literal.LEAVING);
				return;
			}

			this.bankBranchID.setAttribute("bankBranchID", bankBranch.getBankBranchID());
			this.bankBranchID.setValue(bankBranch.getIFSC());
			this.finAdvancePayments.setCity(bankBranch.getCity());
			this.finAdvancePayments.setBranchBankName(bankBranch.getBankName());
			this.finAdvancePayments.setBranchBankCode(bankBranch.getBankCode());
			this.finAdvancePayments.setBranchDesc(bankBranch.getBranchDesc());
			this.finAdvancePayments.setiFSC(bankBranch.getIFSC());
			this.bank.setValue(bankBranch.getBankName());
			this.branch.setValue(bankBranch.getBranchDesc());

			if (StringUtils.isNotBlank(bankBranch.getBankCode())) {
				BankDetail bankDetails = bankDetailService.getAccNoLengthByCode(bankBranch.getBankCode());
				minAccNoLength = bankDetails.getMinAccNoLength();
				maxAccNoLength = bankDetails.getAccNoLength();
			}

			if (maxAccNoLength != 0) {
				this.beneficiaryAccNo.setMaxlength(maxAccNoLength);
			} else {
				this.beneficiaryAccNo.setMaxlength(LengthConstants.LEN_ACCOUNT);
			}

			this.beneficiaryAccNo.setValue(builderProjcet.getAccountNo());
			this.beneficiaryName.setValue(builderProjcet.getBeneficiaryName());
			this.city.setValue(bankBranch.getCity());

		} else {
			this.bankBranchID.setValue("");
			this.finAdvancePayments.setCity("");
			this.finAdvancePayments.setBranchBankName("");
			this.finAdvancePayments.setBranchBankCode("");
			this.finAdvancePayments.setBranchDesc("");
			this.finAdvancePayments.setiFSC("");
			this.bank.setValue("");
			this.branch.setValue("");
			this.beneficiaryAccNo.setValue("");
			this.beneficiaryName.setValue("");
			this.city.setValue("");
			this.phoneNumber.setValue("");
		}

		logger.debug(Literal.LEAVING);
	}

	public void checkPaymentType(String str) {
		if (StringUtils.isEmpty(str) || StringUtils.equals(str, PennantConstants.List_Select)) {
			gb_ChequeDetails.setVisible(false);
			gb_NeftDetails.setVisible(false);
			this.btnGetCustBeneficiary.setVisible(false);
			this.partnerBankID.setReadonly(true);
			this.partnerBankID.setValue("");
			this.partnerBankID.setDescription("");

			return;
		} else if (str.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE)
				|| str.equals(DisbursementConstants.PAYMENT_TYPE_DD)) {
			doAddFilter(str);
			caption_FinAdvancePaymentsDialog_ChequeDetails.setLabel(this.paymentType.getSelectedItem().getLabel());
			gb_ChequeDetails.setVisible(true);
			gb_NeftDetails.setVisible(false);
			this.bankBranchID.setValue("");
			this.bankBranchID.setDescription("");
			this.bank.setValue("");
			this.city.setValue("");
			this.branch.setValue("");
			this.beneficiaryAccNo.setValue("");
			this.beneficiaryName.setValue("");
			this.phoneNumber.setValue("");
			if (str.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE)
					|| str.equals(DisbursementConstants.PAYMENT_TYPE_DD)) {
				readOnlyComponent(isReadOnly("FinAdvancePaymentsDialog_printingLoc"), this.printingLoc);
				this.printingLoc.setMandatoryStyle(true);

			} else {
				/*
				 * this.printingLoc.setValue(""); readOnlyComponent(true, this.printingLoc);
				 */
				this.printingLoc.setSclass("");
			}

			this.btnGetCustBeneficiary.setVisible(!isReadOnly("button_FinAdvancePaymentsDialog_btnGetCustBeneficiary"));
		} else if (str.equals(DisbursementConstants.PAYMENT_TYPE_IST)) {
			doAddFilter(str);
			gb_NeftDetails.setVisible(false);
			gb_ChequeDetails.setVisible(false);
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
			this.liabilityHoldName.setValue("");
			this.payableLoc.setValue("");
			this.printingLoc.setValue("");
			this.valueDate.setText("");
			this.llReferenceNo.setValue("");

			this.bankBranchID.setValue("");
			this.bankBranchID.setDescription("");
			this.bank.setValue("");
			this.city.setValue("");
			this.branch.setValue("");
			this.beneficiaryAccNo.setValue("");
			this.beneficiaryName.setValue("");
			this.bankBranchID.setMandatoryStyle(false);
			this.phoneNumber.setValue("");
			this.printingLoc.setSclass("");
			this.btnGetCustBeneficiary.setVisible(false);
		} else {
			doAddFilter(str);
			caption_FinAdvancePaymentsDialog_NeftDetails.setLabel(this.paymentType.getSelectedItem().getLabel());
			gb_NeftDetails.setVisible(true);
			gb_ChequeDetails.setVisible(false);
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
			this.liabilityHoldName.setValue("");
			this.payableLoc.setValue("");
			this.printingLoc.setValue("");
			this.valueDate.setText("");
			this.llReferenceNo.setValue("");
			if (str.equals(DisbursementConstants.PAYMENT_TYPE_IMPS)) {
				this.contactNumber.setSclass("mandatory");
			} else {
				this.contactNumber.setSclass("");
			}
			this.btnGetCustBeneficiary.setVisible(!isReadOnly("button_FinAdvancePaymentsDialog_btnGetCustBeneficiary"));
		}
	}

	public void doAddFilter(String paymentMode) {
		if (!PartnerBankExtension.BRANCH_WISE_MAPPING) {
			Filter[] filters = new Filter[4];
			filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
			filters[1] = new Filter("Purpose", "D", Filter.OP_EQUAL);
			filters[2] = new Filter("PaymentMode", paymentMode, Filter.OP_EQUAL);
			filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);

			this.partnerBankID.setFilters(filters);

			return;
		}

		String finBranch = financeMain.getFinBranch();
		Long clusterId = null;

		Filter[] filters = new Filter[5];
		filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
		filters[1] = new Filter("Purpose", "D", Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", paymentMode, Filter.OP_EQUAL);
		filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			filters[4] = new Filter("BranchCode", finBranch, Filter.OP_EQUAL);
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			clusterId = clusterService.getClustersFilter(finBranch);
			filters[4] = new Filter("ClusterId", clusterId, Filter.OP_EQUAL);
		}

		FinTypePartnerBank fpb = new FinTypePartnerBank();
		fpb.setFinType(financeMain.getFinType());
		fpb.setPurpose("D");
		fpb.setPaymentMode(paymentMode);
		fpb.setBranchCode(finBranch);
		fpb.setClusterId(clusterId);

		List<FinTypePartnerBank> list = finTypePartnerBankService.getFinTypePartnerBanks(fpb);
		if (list.size() == 1) {
			fpb = list.get(0);
			this.partnerBankID.setAttribute("partnerBankId", fpb.getPartnerBankID());
			this.finAdvancePayments.setPartnerbankCode(fpb.getPartnerBankCode());
			this.finAdvancePayments.setPartnerBankName(fpb.getPartnerBankName());
			this.partnerBankID.setAttribute("partnerBankAc", fpb.getAccountNo());
			this.partnerBankID.setAttribute("partnerBankAcType", fpb.getAccountType());
			this.partnerBankID.setValue(fpb.getPartnerBankCode(), fpb.getPartnerBankName());
		}

		this.partnerBankID.setFilters(filters);
	}

	public void onClick$btnGetCustBeneficiary(Event event) {
		logger.debug("Entering");
		Filter filter[] = new Filter[2];
		if (!ImplementationConstants.DISBURSEMENT_ALLOW_CO_APP) {
			filter[0] = new Filter("CustId", custID, Filter.OP_EQUAL);
			filter[1] = new Filter("BeneficiaryActive", 1, Filter.OP_EQUAL);
			Object dataObject = ExtendedSearchListBox.show(this.window_FinAdvancePaymentsDialog, "BeneficiaryEnquiry",
					filter, "");
			doFillBeneficiaryDetails(dataObject);
		} else {
			Map<String, Object> aruments = new HashMap<>();

			// getting the customer cif's of applicant and coapp's for Customer CIF filter in Beneficiary Select
			aruments.put("custCIFs", getCustomerCIFs());

			// passing primary cif to display in search window
			aruments.put("custCIF", custCIF);

			// preparing filters to render only primary beneficiary while loading
			filter[0] = new Filter("CustCIF", custCIF, Filter.OP_EQUAL);
			filter[1] = new Filter("BeneficiaryActive", 1, Filter.OP_EQUAL);

			aruments.put("filtersList", Arrays.asList(filter));

			aruments.put("DialogCtrl", this);
			Executions.createComponents("/WEB-INF/pages/Beneficiary/BeneficiarySelect.zul", null, aruments);
		}

		logger.debug("Leaving");
	}

	/**
	 * This method will fill the beneficiary details
	 */
	public void doFillBeneficiaryDetails(Object dataObject) {
		if (dataObject instanceof Beneficiary) {
			Beneficiary details = (Beneficiary) dataObject;
			if (details != null) {
				String disbursementType = paymentType.getSelectedItem().getValue().toString();

				if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(disbursementType)
						|| DisbursementConstants.PAYMENT_TYPE_DD.equals(disbursementType)) {
					bankCode.setAttribute("bankCode", details.getBankCode());
					bankCode.setValue(StringUtils.trimToEmpty(details.getBankCode()),
							StringUtils.trimToEmpty(details.getBankName()));
					this.liabilityHoldName.setValue(StringUtils.trimToEmpty(details.getAccHolderName()).concat(", ")
							.concat(details.getBankName()).concat(" A/C No: ").concat(details.getAccNumber()));
					this.payableLoc.setValue(details.getBranchDesc());
				} else {
					this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
					this.bankBranchID.setValue(details.getiFSC());
					this.finAdvancePayments.setCity(details.getCity());
					this.finAdvancePayments.setBranchBankName(details.getBankName());
					this.finAdvancePayments.setBranchBankCode(details.getBankCode());
					this.finAdvancePayments.setBranchDesc(details.getBranchDesc());
					this.finAdvancePayments.setiFSC(details.getiFSC());
					this.bank.setValue(details.getBankName());
					this.branch.setValue(details.getBranchDesc());
					this.beneficiaryAccNo.setValue(details.getAccNumber());
					this.beneficiaryName.setValue(details.getAccHolderName());
					this.city.setValue(details.getCity());
					this.phoneNumber.setValue(details.getPhoneNumber());
					if (StringUtils.isNotBlank(details.getBankCode())) {
						bankdetail = bankDetailService.getAccNoLengthByCode(details.getBankCode());
						this.maxAccNoLength = this.bankdetail.getAccNoLength();
						this.minAccNoLength = this.bankdetail.getMinAccNoLength();
					}
					this.beneficiaryAccNo.setMaxlength(maxAccNoLength);

					if (this.pennyDropResult.isVisible()) {
						BankAccountValidation bankAccountValidations = null;
						bankAccountValidations = pennyDropService.getPennyDropStatusDataByAcc(details.getAccNumber(),
								details.getiFSC());

						if (bankAccountValidations == null) {
							this.pennyDropResult.setValue("");
						} else {
							this.pennyDropResult.setValue(bankAccountValidations.isStatus() ? "Success" : "Fail");
						}
					}
				}
			}
		}
	}

	public void onSelect$disbDate(ForwardEvent event) {
		setDisbursmentAmount();
		setDisbursmentDate();
	}

	private void setDisbursmentDate() {
		Comboitem item = this.disbDate.getSelectedItem();
		if (!("#".equals(this.disbDate.getSelectedItem().getValue()))) {
			if (item != null && item.getValue() != null) {
				this.llDate.setValue(item.getValue());
			}
		} else {
			this.llDate.setValue(null);
		}

	}

	private void setDisbursmentAmount() {
		Comboitem item = this.disbDate.getSelectedItem();
		if (item != null && item.getValue() != null) {
			FinanceDisbursement disbursement = (FinanceDisbursement) item.getAttribute("data");
			if (disbursement != null) {
				BigDecimal disAmt = DisbursementInstCtrl.getTotalByDisbursment(disbursement, financeMain);
				disAmt = disAmt.subtract(getAdjustedAmount(disbursement));

				this.disbDateAmount.setValue(PennantApplicationUtil.formateAmount(disAmt, ccyFormatter));
			}
		} else {
			this.disbDateAmount.setValue(BigDecimal.ZERO);
		}
	}

	public BigDecimal getAdjustedAmount(FinanceDisbursement disbursement) {
		BigDecimal adjustedAmount = BigDecimal.ZERO;

		List<FinAdvancePayments> list = null;
		if (this.moduleType.equals("LOAN")) {
			list = getFinAdvancePaymentsListCtrl().getFinAdvancePaymentsList();
		} else {
			if (getPayOrderIssueDialogCtrl() != null) {
				list = getPayOrderIssueDialogCtrl().getFinAdvancePaymentsList();
			}
		}
		if (list == null || list.isEmpty()) {
			return adjustedAmount;
		}

		for (FinAdvancePayments finAdvPayments : list) {
			if (DisbursementConstants.PAYMENT_DETAIL_VAS.equals(finAdvPayments.getPaymentDetail())) {
				continue;
			}
			if (finAdvPayments.getDisbSeq() == disbursement.getDisbSeq()) {
				if (this.paymentSequence.intValue() == finAdvPayments.getPaymentSeq()) {
					continue;
				}
				if (DisbursementInstCtrl.isDeleteRecord(finAdvPayments)) {
					continue;
				}
				adjustedAmount = adjustedAmount.add(finAdvPayments.getAmtToBeReleased());
			}

		}
		return adjustedAmount;

	}

	public void onClick$btnPennyDropResult(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// Interface Calling
		doSetValidation();
		BankAccountValidation bankAccountValidations = new BankAccountValidation();
		bankAccountValidations.setInitiateReference(finAdvancePayments.getFinReference());
		bankAccountValidations.setUserDetails(getUserWorkspace().getLoggedInUser());

		try {
			if (this.beneficiaryAccNo.getValue() != null) {
				bankAccountValidations
						.setAcctNum(PennantApplicationUtil.unFormatAccountNumber(this.beneficiaryAccNo.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.bankBranchID.getValue() != null) {
				bankAccountValidations.setiFSC(this.bankBranchID.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		int count = getPennyDropService().getPennyDropCount(bankAccountValidations.getAcctNum(),
				bankAccountValidations.getiFSC());
		if (count > 0) {
			MessageUtil.showMessage("Penny Drop Verified for this AccountNumber");
			return;
		} else {
			try {
				boolean status = false;
				if (bankAccountValidationService != null) {
					status = bankAccountValidationService.validateBankAccount(bankAccountValidations);
				}
				if (status) {
					this.pennyDropResult.setValue("Sucess");
				} else {
					this.pennyDropResult.setValue("Fail");
				}
				bankAccountValidations.setStatus(status);
				bankAccountValidations.setInitiateType("D");
				getPennyDropService().savePennyDropSts(bankAccountValidations);
			} catch (Exception e) {
				MessageUtil.showMessage(e.getMessage());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for VAS Reference OnChange.
	 * 
	 * @param event
	 */
	public void onChange$vasReference(Event event) {
		logger.debug(Literal.ENTERING);
		String vasReference = this.vasReference.getSelectedItem().getValue().toString();
		if (StringUtils.isNotBlank(vasReference) && !PennantConstants.List_Select.equals(vasReference)) {
			vasAmount.setValue(CurrencyUtil.parse(vasAmountsMAP.get(vasReference), ccyFormatter));
		} else {
			vasAmount.setValue(BigDecimal.ZERO);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for on changes event of paymentDetail.
	 * 
	 * @param paymentFor
	 */
	private void doChangePaymentDetails(String paymentFor) {
		logger.debug(Literal.ENTERING);
		if (DisbursementConstants.PAYMENT_DETAIL_VAS.equals(paymentFor)) {
			this.row_DisbDate.setVisible(false);
			this.row_vasReference.setVisible(true);
			this.row_disbdetails.setVisible(false);
			this.row_expensedetails.setVisible(false);
			// this.vasReference.setDisabled(true);
			this.btnDelete.setVisible(false);
		} else {
			this.row_DisbDate.setVisible(true);
			this.row_vasReference.setVisible(false);
			this.row_disbdetails.setVisible(true);
			this.row_expensedetails.setVisible(true);
			this.vasAmount.setValue(BigDecimal.ZERO);

			if (CollectionUtils.isNotEmpty(vasRecordingList)) {
				List<ValueLabel> list = getVasReferences(null, BigDecimal.ZERO);
				fillComboBox(this.vasReference, PennantConstants.List_Select, list, "");
			}

		}
		logger.debug(Literal.LEAVING);
	}

	private List<ValueLabel> getVasReferences(String vasReference, BigDecimal vasAmount) {
		logger.debug(Literal.ENTERING);
		List<ValueLabel> vasReferences = new ArrayList<>();
		vasAmountsMAP = new HashMap<>(1);

		boolean isDeletedVAS = true;
		for (VASRecording vasRecording : vasRecordingList) {
			String vasRef = vasRecording.getVasReference();
			vasReferences.add(new ValueLabel(vasRef, vasRef));
			vasAmountsMAP.put(vasRef, vasRecording.getFee());
			if (StringUtils.equals(vasRef, vasReference)) {
				isDeletedVAS = false;
			}

		}
		// if the VAS Instruction is added initial and delete the vas later
		if (StringUtils.isNotBlank(vasReference) && !PennantConstants.List_Select.equals(vasReference)
				&& isDeletedVAS) {
			vasReferences.add(new ValueLabel(vasReference, vasReference));
			vasAmountsMAP.put(vasReference, vasAmount);
		}
		logger.debug(Literal.LEAVING);
		return vasReferences;
	}

	/**
	 * Method for checking given VAS Instruction is duplicate or not.
	 */
	private boolean isDuplicateVasInstruvtions(String vasReference) {
		boolean isDuplicate = false;

		List<FinAdvancePayments> finAdvancePaymentsDetails;
		if ("LOAN".equals(this.moduleType)) {
			finAdvancePaymentsDetails = getFinAdvancePaymentsListCtrl().getFinAdvancePaymentsList();
		} else {
			finAdvancePaymentsDetails = getPayOrderIssueDialogCtrl().getFinAdvancePaymentsList();
		}
		if (CollectionUtils.isNotEmpty(finAdvancePaymentsDetails)) {
			int count = 0;
			for (FinAdvancePayments finAdvPayments : finAdvancePaymentsDetails) {
				if (!DisbursementConstants.PAYMENT_DETAIL_VAS.equals(finAdvPayments.getPaymentDetail())) {
					continue;
				}
				if (DisbursementInstCtrl.isDeleteRecord(finAdvPayments)) {
					continue;
				}
				if (StringUtils.equalsIgnoreCase(vasReference, finAdvPayments.getVasReference())) {
					count++;
				}
			}
			if (count > 0) {
				isDuplicate = true;
			}
		}
		return isDuplicate;
	}

	// Process for Document uploading
	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering");

		Media media = event.getMedia();

		List<DocType> documents = new ArrayList<>();
		documents.add(DocType.PDF);
		documents.add(DocType.JPEG);
		documents.add(DocType.JPG);
		documents.add(DocType.PNG);

		if (!MediaUtil.isValid(media, documents)) {
			MessageUtil.showError(
					Labels.getLabel("upload_document_invalid", new String[] { "pdf or image(.jpeg/jpg/png)" }));
			return;
		}

		browseDoc(media, this.documentName);
		eastDocument.setVisible(true);
		logger.debug("Leaving");
	}

	// Browse for Document uploading
	private void browseDoc(Media media, Textbox textbox) throws InterruptedException {
		logger.debug("Entering");

		try {
			String docType = "";
			if (MediaUtil.isPdf(media)) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if (MediaUtil.isImage(media)) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			}

			// Process for Correct Format Document uploading
			String fileName = media.getName();
			byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
			// Data Fill by QR Bar Code Reader
			if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
				this.disbDoc.setContent(
						new AMedia("document.pdf", "pdf", "application/pdf", new ByteArrayInputStream(ddaImageData)));

			} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
				this.disbDoc.setContent(media);
			}
			this.disbDoc.setVisible(true);
			textbox.setValue(fileName);
			setImagebyte(media.getByteData());
			finAdvancePayments.setDocumentName(fileName);
			finAdvancePayments.setDocImage(getImagebyte());
			finAdvancePayments.setDocType(docType);
		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinAdvancePayments(FinAdvancePayments finAdvancePayments) {
		this.finAdvancePayments = finAdvancePayments;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public FinAdvancePaymentsListCtrl getFinAdvancePaymentsListCtrl() {
		return finAdvancePaymentsListCtrl;
	}

	public void setFinAdvancePaymentsListCtrl(FinAdvancePaymentsListCtrl finAdvancePaymentsListCtrl) {
		this.finAdvancePaymentsListCtrl = finAdvancePaymentsListCtrl;
	}

	public void setFinAdvancePaymentsDetails(List<FinAdvancePayments> finAdvancePaymentsDetails) {
		this.finAdvancePaymentsDetails = finAdvancePaymentsDetails;
	}

	public List<FinAdvancePayments> getFinAdvancePaymentsDetails() {
		return finAdvancePaymentsDetails;
	}

	public PayOrderIssueListCtrl getPayOrderIssueListCtrl() {
		return payOrderIssueListCtrl;
	}

	public void setPayOrderIssueListCtrl(PayOrderIssueListCtrl payOrderIssueListCtrl) {
		this.payOrderIssueListCtrl = payOrderIssueListCtrl;
	}

	public PayOrderIssueDialogCtrl getPayOrderIssueDialogCtrl() {
		return payOrderIssueDialogCtrl;
	}

	public void setPayOrderIssueDialogCtrl(PayOrderIssueDialogCtrl payOrderIssueDialogCtrl) {
		this.payOrderIssueDialogCtrl = payOrderIssueDialogCtrl;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public PartnerBankService getPartnerBankService() {
		return partnerBankService;
	}

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	public CustomerPaymentTxnsListCtrl getCustomerPaymentTxnsListCtrl() {
		return customerPaymentTxnsListCtrl;
	}

	public void setCustomerPaymentTxnsListCtrl(CustomerPaymentTxnsListCtrl customerPaymentTxnsListCtrl) {
		this.customerPaymentTxnsListCtrl = customerPaymentTxnsListCtrl;
	}

	public CustomerPaymentTxnsDialogCtrl getCustomerPaymentTxnsDialogCtrl() {
		return customerPaymentTxnsDialogCtrl;
	}

	public void setCustomerPaymentTxnsDialogCtrl(CustomerPaymentTxnsDialogCtrl customerPaymentTxnsDialogCtrl) {
		this.customerPaymentTxnsDialogCtrl = customerPaymentTxnsDialogCtrl;
	}

	public PennyDropService getPennyDropService() {
		return pennyDropService;
	}

	public void setPennyDropService(PennyDropService pennyDropService) {
		this.pennyDropService = pennyDropService;
	}

	public PennyDropDAO getPennyDropDAO() {
		return pennyDropDAO;
	}

	public void setPennyDropDAO(PennyDropDAO pennyDropDAO) {
		this.pennyDropDAO = pennyDropDAO;
	}

	@Autowired(required = false)
	@Qualifier(value = "bankAccountValidationService")
	public void setBankAccountValidationService(BankAccountValidationService bankAccountValidationService) {
		this.bankAccountValidationService = bankAccountValidationService;
	}

	/**
	 * This method will return the list of customer cif's
	 * 
	 * @return List<String>
	 */
	private List<String> getCustomerCIFs() {
		ArrayList<String> custCIFs = new ArrayList<>(2);
		CustomerDetails customerDetails = null;

		// Inside loan queue
		if (getFinanceMainDialogCtrl() != null) {
			// Get Primary Customer CIF
			FinanceMainBaseCtrl financeMainDialogCtrl = (FinanceMainBaseCtrl) getFinanceMainDialogCtrl();
			CustomerDialogCtrl customerDialogCtrl = financeMainDialogCtrl.getCustomerDialogCtrl();

			if (customerDialogCtrl != null) {
				customerDetails = customerDialogCtrl.getCustomerDetails();
			}

			// From Add Disbursement menu
			FinanceDetail financeDetail = financeMainDialogCtrl.getFinanceDetail();
			// From Add Disbursement menu
			if (financeDetail != null) {
				customerDetails = financeDetail.getCustomerDetails();
			}

			// Setting the Primary Customer CIF
			if (customerDetails != null) {
				custCIF = customerDetails.getCustomer().getCustCIF();
			}

			// Co-applicant details from Add disbursement menu where co Applicants tab not available
			List<JointAccountDetail> jointAccountDetailList = financeDetail.getJointAccountDetailList();
			getCoAppList(custCIFs, jointAccountDetailList);

			// Get Co-applicant CIF's in side the loan queue from Co Applicants tab
			JointAccountDetailDialogCtrl financeJointAccountDetailDialogCtrl = financeMainDialogCtrl
					.getJointAccountDetailDialogCtrl();
			if (financeJointAccountDetailDialogCtrl != null) {
				List<Customer> jointAccountCustomers = financeJointAccountDetailDialogCtrl.getJointAccountCustomers();
				for (Customer customer : jointAccountCustomers) {
					custCIFs.add(customer.getCustCIF());
				}
			}
		} else if (payOrderIssueDialogCtrl != null) {// From Disbursement Instructions menu in loan management
			PayOrderIssueHeader payOrderIssueHeader = payOrderIssueDialogCtrl.getPayOrderIssueHeader();
			if (payOrderIssueHeader != null) {
				// Primary CIF
				custCIF = payOrderIssueHeader.getCustCIF();
				getCoAppList(custCIFs, payOrderIssueHeader.getJointAccountDetails());
			}
		}
		// Primary CIF
		custCIFs.add(custCIF);
		return custCIFs;
	}

	/**
	 * 
	 * @param custCIFs
	 * @param jointAccountDetailList
	 */
	private void getCoAppList(ArrayList<String> custCIFs, List<JointAccountDetail> jointAccountDetailList) {
		if (!CollectionUtils.isEmpty(jointAccountDetailList)) {
			for (JointAccountDetail accountDetail : jointAccountDetailList) {
				custCIFs.add(accountDetail.getCustCIF());
			}
		}
	}

	public byte[] getImagebyte() {
		return imagebyte;
	}

	public void setImagebyte(byte[] imagebyte) {
		this.imagebyte = imagebyte;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	public void setBuilderProjcetDAO(BuilderProjcetDAO builderProjcetDAO) {
		this.builderProjcetDAO = builderProjcetDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}

	public void setClusterService(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

}
