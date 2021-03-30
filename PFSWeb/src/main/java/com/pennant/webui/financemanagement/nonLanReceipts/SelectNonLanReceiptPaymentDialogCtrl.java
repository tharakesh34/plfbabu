package com.pennant.webui.financemanagement.nonLanReceipts;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectNonLanReceiptPaymentDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {

	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = Logger.getLogger(SelectNonLanReceiptPaymentDialogCtrl.class);

	protected Window window_SelectNonLanReceiptPaymentDialog;
	protected ExtendedCombobox division;
	protected ExtendedCombobox entityCode;
	protected Combobox receiptMode;
	protected Combobox receiptChannel;
	protected Combobox subReceiptMode;
	protected Combobox receivedFrom;
	protected Datebox receiptDate;
	protected Uppercasebox extReference;
	protected Combobox receiptSource;
	protected CurrencyBox receiptAmount;
	protected ExtendedCombobox tranBranch;
	protected ExtendedCombobox customer;

	protected Button btnProceed;

	protected Row row_CustId;
	protected Row row_subReceiptMode;
	protected Row row_ReceiptMode;
	protected Row row_ReceiptChannel;
	protected Label ReceiptPayment;

	protected Label label_title;

	protected NonLanReceiptListCtrl nonLanReceiptListCtrl;

	@Autowired
	public transient ReceiptService receiptService;
	public transient SecurityUserDAO securityUserDAO;

	protected FinReceiptData receiptData = new FinReceiptData();
	protected FinReceiptHeader finReceiptHeader = new FinReceiptHeader();
	private transient WorkFlowDetails workFlowDetails = null;
	private transient FinanceWorkFlowService financeWorkFlowService;

	private FinanceEnquiry financeEnquiry;

	// private DueData dueData;
	private String module;
	private int formatter = 2;
	Date appDate = DateUtility.getAppDate();

	private List<ValueLabel> receiptModeList = PennantAppUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_MODE);
	private List<ValueLabel> receiptChannelList = PennantAppUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_CHANNEL);
	private List<ValueLabel> subReceiptModeList = PennantAppUtil
			.getActiveFieldCodeList(RepayConstants.SUB_RECEIPT_MODE);
	private List<ValueLabel> receiptSourceList = PennantAppUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_SOURCE);

	/**
	 * default constructor.<br>
	 */
	public SelectNonLanReceiptPaymentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";

	}

	public void onCreate$window_SelectNonLanReceiptPaymentDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// Set the page level components.
		setPageComponents(this.window_SelectNonLanReceiptPaymentDialog);
		doSetFieldProperties();
		if (arguments.containsKey("nonLanReceiptListCtrl")) {
			this.nonLanReceiptListCtrl = (NonLanReceiptListCtrl) arguments.get("nonLanReceiptListCtrl");
			setNonLanReceiptListCtrl(this.nonLanReceiptListCtrl);
		} else {
			setNonLanReceiptListCtrl(null);
		}
		setTranBranch();
		this.window_SelectNonLanReceiptPaymentDialog.doModal();

		logger.debug("Leaving " + event.toString());
	}

	private void setTranBranch() {
		logger.debug(Literal.ENTERING);

		if (getUserWorkspace().getUserDetails().getSecurityUser().isAccessToAllBranches()) {
			this.tranBranch.setReadonly(false);
		} else {
			this.tranBranch.setReadonly(true);
		}
		this.tranBranch.setValue(getUserWorkspace().getLoggedInUser().getBranchCode());
		this.tranBranch
				.setDescription(getUserWorkspace().getUserDetails().getSecurityUser().getLovDescUsrBranchCodeName());

		logger.debug(Literal.LEAVING);

	}

	/**
	 * 
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.tranBranch.setMandatoryStyle(true);
		this.tranBranch.setTextBoxWidth(155);
		this.tranBranch.setReadonly(false);
		this.tranBranch.setModuleName("Branch");
		this.tranBranch.setValueColumn("BranchCode");
		this.tranBranch.setDescColumn("BranchDesc");
		this.tranBranch.setValidateColumns(new String[] { "BranchCode" });

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.receiptDate.setValue(appDate);
		this.receiptAmount.setProperties(true, formatter);
		this.extReference.setMaxlength(20);

		fillComboBox(this.receiptMode, "", receiptModeList, "");
		fillComboBox(this.receiptChannel, "", receiptChannelList, "POR");
		fillComboBox(this.subReceiptMode, "", subReceiptModeList, "");
		fillComboBox(this.receiptSource, "", receiptSourceList, "");
		//adding new in nonselectlan
		fillComboBox(this.receivedFrom, "", PennantStaticListUtil.getNonLoanReceivedFrom(), "");
		this.division.setModuleName("DivisionDetail");
		this.division.setMandatoryStyle(true);
		this.division.setValueColumn("DivisionCode");
		this.division.setDescColumn("DivisionCodeDesc");
		this.division.setDisplayStyle(2);
		this.division.setValidateColumns(new String[] { "DivisionCode" });

		this.customer.setModuleName("Customer");
		this.customer.setMandatoryStyle(true);
		this.customer.setValueColumn("CustCIF");
		this.customer.setDescColumn("CustShrtName");
		this.customer.setDisplayStyle(2);
		this.customer.setValidateColumns(new String[] { "CustCIF" });

		this.entityCode.setModuleName("Entity");
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });
		Filter[] fieldCode = new Filter[1];
		fieldCode[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.entityCode.setFilters(fieldCode);

		this.module = getArgument("module");
		if (StringUtils.equals(this.module, FinanceConstants.KNOCKOFF_MAKER)) {
			this.row_ReceiptMode.setVisible(false);
			this.row_subReceiptMode.setVisible(false);
			this.row_ReceiptChannel.setVisible(false);
		} else if (StringUtils.equals(this.module, FinanceConstants.CLOSURE_APPROVER)
				|| StringUtils.equals(this.module, FinanceConstants.CLOSURE_MAKER)) {
			this.label_title.setValue("Loan Closure");
			this.row_subReceiptMode.setVisible(false);
			this.row_ReceiptChannel.setVisible(false);
			this.row_ReceiptMode.setVisible(false);
		} else {
			fillComboBox(this.receiptMode, "", receiptModeList, "");
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user changes textbox "custCIF"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$receiptMode(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		this.row_subReceiptMode.setVisible(false);
		this.row_ReceiptChannel.setVisible(false);

		String receiptMode = this.receiptMode.getSelectedItem().getValue().toString();
		int channelIdx = 0;

		if (StringUtils.equals(receiptMode, DisbursementConstants.PAYMENT_TYPE_ONLINE)) {
			this.row_subReceiptMode.setVisible(true);
		} else {
			fillComboBox(subReceiptMode, "", subReceiptModeList, "");
		}
		if (!StringUtils.equals(receiptMode, DisbursementConstants.PAYMENT_TYPE_ONLINE)
				&& !StringUtils.equals(receiptMode, PennantConstants.List_Select)) {
			this.row_ReceiptChannel.setVisible(true);
			channelIdx = this.receiptChannel.getSelectedIndex();
			if (channelIdx > 0) {
				this.receiptChannel.setSelectedIndex(channelIdx);
			} else {
				this.receiptChannel.setSelectedIndex(2);
			}

		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$receivedFrom(Event event) throws Exception {
		logger.debug("Entering");
		String receivedFrom = this.receivedFrom.getSelectedItem().getValue().toString();
		this.row_CustId.setVisible(false);

		//if receivedFrom is Non_Loan then extendedCombobox will be disabled.

		if (StringUtils.equalsIgnoreCase(receivedFrom, Labels.getLabel("label_Receipt_ReceivedFrom_Customer"))) {
			this.row_CustId.setVisible(true);
		} else {
			this.row_CustId.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		receiptData.setEnquiry(true);
		doSetValidation();
		doWriteComponentsToBean();
		doShowDialog();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.receiptMode.setErrorMessage("");
		this.subReceiptMode.setErrorMessage("");
		this.receiptChannel.setErrorMessage("");
		this.receiptDate.setErrorMessage("");
		this.receivedFrom.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());
		this.window_SelectNonLanReceiptPaymentDialog.onClose();
		logger.debug("Leaving" + event.toString());
	}

	private void doShowDialog() {
		logger.debug("Entering ");

		final HashMap<String, Object> map = new HashMap<String, Object>();

		// set new record true
		setWorkflowDetails();
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		// setting workflow
		finReceiptHeader.setWorkflowId(getWorkFlowId());
		finReceiptHeader.setNewRecord(true);
		finReceiptHeader.setWorkflowId(getWorkFlowId());
		receiptData.setReceiptHeader(finReceiptHeader);

		map.put("module", this.module);
		map.put("receiptData", this.receiptData);
		map.put("nonLanReceiptListCtrl", getNonLanReceiptListCtrl());
		Executions.createComponents("/WEB-INF/pages/FinanceManagement/NonLanReceipt/NonLanReceiptDialog.zul", null,
				map);

		this.window_SelectNonLanReceiptPaymentDialog.onClose();

		logger.debug("Leaving ");

	}

	private void setWorkflowDetails() {

		// Finance Maintenance Workflow Check & Assignment
		if (StringUtils.isNotEmpty(FinanceConstants.FINSER_EVENT_RECEIPT)) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("NonLanReceipt");

		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

	}

	public void doWriteComponentsToBean() throws Exception {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			finReceiptHeader.setCashierBranch(this.tranBranch.getValue());
			finReceiptHeader.setPostBranch(this.tranBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			//finReceiptHeader.setReference(this.extReference.getValue());
			Customer cust = (Customer) this.customer.getObject();
			if (cust != null && cust.getId() != 0) {
				finReceiptHeader.setCustCIF(this.customer.getValue());
				finReceiptHeader.setCustID(cust.getCustID());
				finReceiptHeader.setReference(String.valueOf(cust.getCustID()));
				finReceiptHeader.setExtReference(this.extReference.getValue());
				finReceiptHeader.setCustShrtName(cust.getCustShrtName());
			} else {
				finReceiptHeader.setReference(this.extReference.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finReceiptHeader.setReceiptAmount(PennantApplicationUtil
					.unFormateAmount(this.receiptAmount.getActualValue(), PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finReceiptHeader.setFinDivision(this.division.getValue());
			finReceiptHeader.setFinDivisionDesc(this.division.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finReceiptHeader.setEntityCode(this.entityCode.getValue());
			finReceiptHeader.setEntityDesc(this.entityCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

		} catch (WrongValueException we) {

			wve.add(we);
		}
		try {
			finReceiptHeader.setReceiptDate(this.receiptDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equalsIgnoreCase(getComboboxValue(this.receivedFrom))) {
				throw new WrongValueException(this.receivedFrom, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_ReceiptDialog_ReceivedFrom1.value") }));
			}

			finReceiptHeader.setReceivedFrom(this.receivedFrom.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.receiptSource))) {
				throw new WrongValueException(this.receiptSource, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_ReceiptPayment_ReceiptSource.value") }));
			}
			finReceiptHeader.setReceiptSource(getComboboxValue(this.receiptSource));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (row_ReceiptMode.isVisible()) {
				if ("#".equals(getComboboxValue(this.receiptMode))) {
					throw new WrongValueException(this.receiptMode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ReceiptPayment_ReceiptMode.value") }));
				}
				finReceiptHeader.setReceiptMode(getComboboxValue(this.receiptMode));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_subReceiptMode.isVisible()) {
				if ("#".equals(getComboboxValue(this.subReceiptMode))) {
					throw new WrongValueException(this.subReceiptMode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ReceiptPayment_SubReceiptMode.value") }));
				}
				finReceiptHeader.setSubReceiptMode(getComboboxValue(this.subReceiptMode));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_ReceiptChannel.isVisible()) {
				if ("#".equals(getComboboxValue(this.receiptChannel))) {
					throw new WrongValueException(this.receiptChannel, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ReceiptPayment_ReceiptChannel.value") }));
				}
				finReceiptHeader.setReceiptChannel(getComboboxValue(this.receiptChannel));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		validateBasicReceiptDate();

		doRemoveValidation();
		doClearMessage();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving ");
	}

	private void validateBasicReceiptDate() {
		logger.debug(Literal.ENTERING);

		if (receiptDate.getValue() == null) {
			receiptDate.setValue(appDate);
		}

		if (receiptDate.getValue().compareTo(appDate) == 0) {
			return;
		}

		ArrayList<WrongValueException> wve = new ArrayList<>();

		// Back Value checking will be with Application Date
		try {
			if (this.receiptDate.getValue().compareTo(appDate) > 0) {
				throw new WrongValueException(this.receiptDate, Labels.getLabel("DATE_ALLOWED_ON_BEFORE", new String[] {
						Labels.getLabel("label_SchedulePayment_ReceiptDate.value"), appDate.toString() }));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		this.entityCode.setConstraint(
				new PTStringValidator(Labels.getLabel("label_ReceiptPayment_EntityCode.value"), null, true, true));

		this.tranBranch.setConstraint(new PTStringValidator(
				Labels.getLabel("label_ReceiptPayment_TransactionBranch.value"), null, true, true));

		this.extReference.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptList_ExtReference.value"),
				PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));

		this.receiptAmount.setConstraint(new PTDecimalValidator(
				Labels.getLabel("label_ReceiptList_ReceiptAmount.value"), formatter, true, false));
		if (this.row_CustId.isVisible()) {

			this.customer.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerRatingSearch_CustCIF.value"), null, true, true));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.receivedFrom.setConstraint("");
		this.subReceiptMode.setConstraint("");
		this.receiptMode.setConstraint("");
		this.receiptDate.setConstraint("");
		this.receiptChannel.setConstraint("");
		this.division.setConstraint("");
		this.entityCode.setConstraint("");
		this.extReference.setConstraint("");
		this.receiptSource.setConstraint("");
		this.customer.setConstraint("");
		logger.debug("Leaving");
	}

	public void resetDefaults(FinanceMain financeMain) {

		formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		FinanceType financeType = receiptService.getFinanceType(financeMain.getFinType());

		if (financeType.isDeveloperFinance()) {
			fillComboBox(subReceiptMode, "", subReceiptModeList, "");
		} else {
			fillComboBox(subReceiptMode, "", subReceiptModeList, ",ESCROW,");
		}
	}

	public FinanceEnquiry getFinanceEnquiry() {
		return financeEnquiry;
	}

	public void setFinanceEnquiry(FinanceEnquiry financeEnquiry) {
		this.financeEnquiry = financeEnquiry;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public SecurityUserDAO getSecurityUserDAO() {
		return securityUserDAO;
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	public NonLanReceiptListCtrl getNonLanReceiptListCtrl() {
		return nonLanReceiptListCtrl;
	}

	public void setNonLanReceiptListCtrl(NonLanReceiptListCtrl nonLanReceiptListCtrl) {
		this.nonLanReceiptListCtrl = nonLanReceiptListCtrl;
	}

}