package com.pennant.webui.settlement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
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
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.applicationmaster.SettlementTypeDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.settlement.FinSettlementHeader;
import com.pennant.backend.model.settlement.SettlementAllocationDetail;
import com.pennant.backend.model.settlement.SettlementSchedule;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.settlement.SettlementService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.rits.cloning.Cloner;

public class SettlementDialogCtrl extends GFCBaseCtrl<FinSettlementHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SettlementDialogCtrl.class);

	protected Window window_SettlementDialog;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected String selectMethodName = "onSelectTab";

	protected ExtendedCombobox settlementType;
	protected Combobox settlementStatus;
	protected Datebox startDate;
	protected Datebox otsDate;
	protected ExtendedCombobox settlementReason;
	protected Datebox endDate;
	protected Textbox finReference;
	protected Longbox finID;
	protected CurrencyBox settlementAmount;
	protected Datebox settlementEndAfterGrace;
	protected Longbox noOfGraceDays;
	protected ExtendedCombobox cancelReasonCode;
	protected Textbox cancelRemarks;

	protected Tab settlementDetails;
	protected Tab loanBasicDetails;

	private Listbox listBoxSettlementScheduleInlineEdit;
	private SettlementScheduleInlineEditCtrl settlementScheduleInlineEditCtrl;

	protected Button btnNew_SettlementSchedule;
	protected Listbox listBoxSettlementSchedule;
	protected Listbox listBoxPastdues;
	private List<SettlementSchedule> settlementScheduleDetailList = new ArrayList<SettlementSchedule>();
	protected Listheader listheader_EscrowTran_RecordStatus;
	protected Listheader listheader_EscrowTran_RecordType;
	private List<SettlementSchedule> settlementScheduleList = new ArrayList<SettlementSchedule>();

	private transient SettlementListCtrl settlementListCtrl;

	protected Groupbox gb_Settlement; // autowired
	protected Groupbox gb_SettlementSchedule; // autowired

	protected Tabpanel tp_SettlementDetails;
	protected Tabpanel tp_LoanBasicDetails;

	protected Groupbox gb_Action;
	protected Groupbox gb_statusDetails;
	String parms[] = new String[4];

	private FinSettlementHeader settlement;
	private FinanceDetail financeDetail;
	private transient SettlementService settlementService;
	private BigDecimal totSetAmount = BigDecimal.ZERO;
	private Row cancellationReason;
	private Label window_SettlementDialog_title;
	private String module = "";
	private transient FinanceDetailService financeDetailService;
	private int formatter = 2;
	private FinReceiptData receiptData = null;
	private BigDecimal totalDue = BigDecimal.ZERO;
	private Button btnSearchFinreference;

	/**
	 * default constructor.<br>
	 */
	public SettlementDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SettlementDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.settlement.getID()));
		return referenceBuffer.toString();
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SettlementDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_SettlementDialog);

		try {

			if (arguments.containsKey("settlement")) {
				this.settlement = (FinSettlementHeader) arguments.get("settlement");
				// Store the before image.
				FinSettlementHeader befImage = new FinSettlementHeader();
				BeanUtils.copyProperties(this.settlement, befImage);
				this.settlement.setBefImage(befImage);
				setSettlement(this.settlement);
			} else {
				setSettlement(null);
			}

			if (arguments.containsKey("financeDetail")) {
				financeDetail = (FinanceDetail) arguments.get("financeDetail");
			}

			if (arguments.containsKey("SettlementListCtrl")) {
				this.settlementListCtrl = (SettlementListCtrl) arguments.get("SettlementListCtrl");
			}

			if (this.settlement == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("isEnqProcess")) {
				enqiryModule = true;
			}

			if (arguments.containsKey("module")) {
				this.module = (String) arguments.get("module");
				if (StringUtils.equals(this.module, "SETTLEMENT_CANCEL")) {
					this.cancellationReason.setVisible(true);
					this.window_SettlementDialog_title.setValue(Labels.getLabel("window_SettlementCancelDialog.title"));
				} else {
					this.cancellationReason.setVisible(false);
				}
			}

			// Render the page and display the data.
			doLoadWorkFlow(this.settlement.isWorkflow(), this.settlement.getWorkflowId(),
					this.settlement.getNextTaskId());

			doSetFieldProperties();

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			if (CollectionUtils.isNotEmpty(settlement.getSettlementAllocationDetails())) {
				for (SettlementAllocationDetail sad : settlement.getSettlementAllocationDetails()) {
					sad.setBalance(sad.getTotalDue().subtract(sad.getWaivedAmount()));
					if (sad.getAllocationTo() == 0 || Allocation.BOUNCE.equalsIgnoreCase(sad.getAllocationType())) {
						String param = "label_RecceiptDialog_AllocationType_" + sad.getAllocationType();
						sad.setTypeDesc(Labels.getLabel(param.trim()));
					}
				}
				doFillAllocationDetail(settlement.getSettlementAllocationDetails());
			}
			doCheckRights();
			doShowDialog(this.settlement);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.settlementType.setModuleName("SettlementTypeDetail");
		this.settlementType.setMandatoryStyle(true);
		this.settlementType.setValueColumn("id");
		this.settlementType.setDescColumn("settlementCode");
		this.settlementType.setDisplayStyle(2);
		this.settlementType.setValidateColumns(new String[] { "id" });

		this.cancelReasonCode.setModuleName("SettlementCancelReasons");
		this.cancelReasonCode.setValueColumn("Code");
		this.cancelReasonCode.setMandatoryStyle(true);
		this.cancelReasonCode.setDescColumn("Description");
		this.cancelReasonCode.setDisplayStyle(2);
		this.cancelReasonCode.setValidateColumns(new String[] { "Code" });

		this.settlementReason.setModuleName("ReasonCode");
		this.settlementReason.setValueColumn("Code");
		this.settlementReason.setDescColumn("Description");
		this.settlementReason.setMandatoryStyle(true);
		this.settlementReason.setDisplayStyle(2);
		this.settlementReason.setValidateColumns(new String[] { "Code" });
		this.settlementReason.setMandatoryStyle(true);

		this.settlementAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.settlementAmount.setWidth("200px");
		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.otsDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.endDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.settlementEndAfterGrace.setFormat(DateFormat.SHORT_DATE.getPattern());

		// Settlement Schedule Inline Edit
		this.listBoxSettlementSchedule.setVisible(false);
		this.listBoxSettlementScheduleInlineEdit.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SettlementDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SettlementDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SettlementDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SettlementDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnNew_SettlementSchedule.setVisible(getUserWorkspace().isAllowed("button_SettlementDialog_btnNew"));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void onClick$btnCancel(Event event) throws ParseException, InterruptedException {
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
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.settlement);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		settlementListCtrl.search();
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	private void doCancel() throws ParseException, InterruptedException {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.settlement.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$settlementType(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = settlementType.getObject();

		if (dataObject instanceof String) {
			this.settlementType.setValue(dataObject.toString());
		} else {
			SettlementTypeDetail details = (SettlementTypeDetail) dataObject;
			if (details != null) {
				this.settlementType.setAttribute("settlementType", details.getId());
				this.settlementType.setValue(String.valueOf(details.getId()), details.getSettlementCode());
				if (details.isAlwGracePeriod()) {
					this.noOfGraceDays.setDisabled(false);
				} else {
					this.noOfGraceDays.setDisabled(true);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$settlementReason(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = settlementReason.getObject();

		if (dataObject instanceof String) {
			this.settlementReason.setValue(dataObject.toString());
		} else {
			ReasonCode details = (ReasonCode) dataObject;
			if (details != null) {
				this.settlementReason.setAttribute("settlementReason", details.getId());
				this.settlementReason.setValue(details.getCode());
				this.settlementReason.setDescription(details.getDescription());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$cancelReasonCode(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = cancelReasonCode.getObject();

		if (dataObject instanceof String) {
			this.cancelReasonCode.setValue(dataObject.toString());
		} else {
			ReasonCode details = (ReasonCode) dataObject;
			if (details != null) {
				this.cancelReasonCode.setAttribute("cancelReasonCode", details.getId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$endDate(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Date endDate = this.endDate.getValue();
		Date settlGraceDate = DateUtil.addDays(endDate, this.noOfGraceDays.getValue().intValue());
		this.settlementEndAfterGrace.setValue(settlGraceDate);
		logger.debug(Literal.LEAVING);
	}

	public void onChange$otsDate(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Date otsDate = this.otsDate.getValue();
		receiptData = settlementService.getDues(finReference.getValue(), otsDate);
		formatter = CurrencyUtil
				.getFormat(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		List<SettlementAllocationDetail> settlementAllocationList = new ArrayList<SettlementAllocationDetail>();
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocationsSummary();
		for (ReceiptAllocationDetail receiptAllocationDetail : allocationList) {
			SettlementAllocationDetail settlementAllocationDetail = new SettlementAllocationDetail();
			settlementAllocationDetail.setID(receiptAllocationDetail.getAllocationID());
			settlementAllocationDetail.setAllocationType(receiptAllocationDetail.getAllocationType());
			settlementAllocationDetail.setAllocationTo(receiptAllocationDetail.getAllocationTo());
			settlementAllocationDetail.setTotalDue(receiptAllocationDetail.getTotalDue());
			settlementAllocationDetail.setPaidAmount(receiptAllocationDetail.getPaidAmount());
			settlementAllocationDetail.setPaidGST(receiptAllocationDetail.getPaidGST());
			settlementAllocationDetail.setWaivedAmount(receiptAllocationDetail.getWaivedAmount());
			settlementAllocationDetail.setWaiverAccepted(receiptAllocationDetail.getWaiverAccepted());
			settlementAllocationDetail.setWaivedGST(receiptAllocationDetail.getWaivedGST());
			settlementAllocationDetail.setTdsDue(receiptAllocationDetail.getTdsDue());
			settlementAllocationDetail.setTdsPaid(receiptAllocationDetail.getTdsPaid());
			settlementAllocationDetail.setTdsWaived(receiptAllocationDetail.getTdsWaived());
			settlementAllocationDetail.setTypeDesc(receiptAllocationDetail.getTypeDesc());
			settlementAllocationDetail.setSubListAvailable(receiptAllocationDetail.isSubListAvailable());
			settlementAllocationDetail.setBalance(receiptAllocationDetail.getBalance());
			settlementAllocationList.add(settlementAllocationDetail);
		}
		settlement.setSettlementAllocationDetails(settlementAllocationList);
		doFillAllocationDetail(settlementAllocationList);
		this.btnSearchFinreference.setVisible(true);
		logger.debug(Literal.LEAVING);
	}

	public void onAllocateWaivedChange(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		int idx = (int) event.getData();
		String id = "AllocateWaived_" + idx;

		SettlementAllocationDetail allocate = settlement.getSettlementAllocationDetails().get(idx);

		CurrencyBox allocationWaived = (CurrencyBox) this.listBoxPastdues.getFellow(id);
		BigDecimal waivedAmount = PennantApplicationUtil.unFormateAmount(allocationWaived.getValidateValue(),
				formatter);

		BigDecimal dueAmount = allocate.getTotalDue();
		if (waivedAmount.compareTo(dueAmount) > 0) {
			waivedAmount = dueAmount;
		}
		allocate.setWaivedAmount(waivedAmount);
		allocate.setBalance(dueAmount.subtract(waivedAmount));
		BigDecimal priWaived = BigDecimal.ZERO;
		BigDecimal pftWaived = BigDecimal.ZERO;
		if (Allocation.PRI.equals(allocate.getAllocationType())
				|| Allocation.PFT.equals(allocate.getAllocationType())) {
			for (SettlementAllocationDetail sad : settlement.getSettlementAllocationDetails()) {
				if (Allocation.PRI.equals(sad.getAllocationType())) {
					priWaived = sad.getWaivedAmount();
				}
				if (Allocation.PFT.equals(sad.getAllocationType())) {
					pftWaived = sad.getWaivedAmount();
				}
			}
		}

		if ((priWaived.add(pftWaived)).compareTo(BigDecimal.ZERO) > 0) {
			for (SettlementAllocationDetail sad : settlement.getSettlementAllocationDetails()) {
				if (Allocation.EMI.equals(sad.getAllocationType())) {
					sad.setWaivedAmount(priWaived.add(pftWaived));
					sad.setBalance(sad.getTotalDue().subtract(priWaived.add(pftWaived)));
					break;
				}

			}
		}
		doFillAllocationDetail(settlement.getSettlementAllocationDetails());
		logger.debug(Literal.LEAVING);
	}

	public void doFillAllocationDetail(List<SettlementAllocationDetail> settlementAllocationDetail) {
		logger.debug(Literal.ENTERING);
		// List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocationsSummary();
		this.listBoxPastdues.getItems().clear();

		// Get Receipt Purpose to Make Waiver amount Editable
		String label = Labels.getLabel("label_RecceiptDialog_AllocationType_");

		for (int i = 0; i < settlementAllocationDetail.size(); i++) {
			createAllocateItem(settlementAllocationDetail.get(i), label, i);
		}

		addDueFooter();

		logger.debug(Literal.LEAVING);
	}

	private void addDueFooter() {
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);
		BigDecimal totDue = BigDecimal.ZERO;
		BigDecimal waived = BigDecimal.ZERO;

		List<SettlementAllocationDetail> allocList = settlement.getSettlementAllocationDetails();

		for (SettlementAllocationDetail allocate : allocList) {

			if (!Allocation.EMI.equals(allocate.getAllocationType().trim())
					&& !Allocation.NPFT.equals(allocate.getAllocationType())
					&& !Allocation.FUT_NPFT.equals(allocate.getAllocationType())) {
				totDue = totDue.add(allocate.getTotalDue());
				waived = waived.add(allocate.getWaivedAmount());
			}

		}
		addAmountCell(item, totDue, null, true);
		addAmountCell(item, waived, null, true);
		addAmountCell(item, totDue.subtract(waived), null, true);
		totalDue = totDue.subtract(waived);
		this.listBoxPastdues.appendChild(item);
	}

	private void createAllocateItem(SettlementAllocationDetail allocate, String desc, int idx) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.equals(Allocation.NPFT, allocate.getAllocationType())
				|| StringUtils.equals(Allocation.FUT_NPFT, allocate.getAllocationType())) {
			return;
		}

		Listitem item = new Listitem();
		Listcell lc = null;
		addBoldTextCell(item, allocate.getTypeDesc(), allocate.isSubListAvailable(), idx);

		addAmountCell(item, allocate.getTotalDue(), ("AllocateCurDue_" + idx), true);

		lc = new Listcell();
		CurrencyBox allocationWaived = new CurrencyBox();
		allocationWaived.setStyle("text-align:right;");
		allocationWaived.setBalUnvisible(true, true);
		setProps(allocationWaived, false, formatter, 120);
		allocationWaived.setId("AllocateWaived_" + idx);
		allocationWaived.setValue(PennantApplicationUtil.formateAmount(allocate.getWaivedAmount(), formatter));
		allocationWaived.addForward("onFulfill", this.window_SettlementDialog, "onAllocateWaivedChange", idx);

		allocationWaived.setReadonly(!getUserWorkspace().isAllowed("SettlementDialog_WaivedAmount"));
		if (Allocation.EMI.equals(allocate.getAllocationType())) {
			allocationWaived.setReadonly(true);
		}
		lc.appendChild(allocationWaived);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		// Balance Due AMount
		addAmountCell(item, allocate.getBalance(), ("AllocateBalDue_" + idx), true);

		// if (allocate.isEditable()){
		this.listBoxPastdues.appendChild(item);
		// }

		logger.debug(Literal.LEAVING);
	}

	public void addBoldTextCell(Listitem item, String value, boolean hasChild, int buttonId) {
		Listcell lc = new Listcell(value);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		if (hasChild) {
			Button button = new Button("Details");
			button.setId(String.valueOf(buttonId));
			button.addForward("onClick", window_SettlementDialog, "onDetailsClick", button.getId());
			lc.appendChild(button);
		}
		lc.setParent(item);
	}

	public void addAmountCell(Listitem item, BigDecimal value, String cellID, boolean isBold) {
		Listcell lc = new Listcell(PennantApplicationUtil.amountFormate(value, formatter));

		if (isBold) {
			lc.setStyle("text-align:right;font-weight:bold;");
		} else {
			lc.setStyle("text-align:right;");
		}

		if (!StringUtils.isBlank(cellID)) {
			lc.setId(cellID);
		}

		lc.setParent(item);
	}

	public void onChange$noOfGraceDays(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Date endDate = this.endDate.getValue();
		if (endDate == null) {
			MessageUtil.showError(Labels.getLabel("label_validationForNoOfGraceDays"));
			this.noOfGraceDays.setValue((long) 0);
		} else {
			Date settlGraceDate = DateUtil.addDays(endDate, this.noOfGraceDays.getValue().intValue());
			this.settlementEndAfterGrace.setValue(settlGraceDate);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param escrowAccount
	 * @throws InterruptedException
	 * @throws ParseException
	 * 
	 */
	public void doWriteBeanToComponents(FinSettlementHeader settlement) throws ParseException, InterruptedException {
		logger.debug(Literal.ENTERING);

		if (settlement.getSettlementType() != Long.MIN_VALUE && settlement.getSettlementType() != 0) {
			this.settlementType.setAttribute("settlementType", settlement.getSettlementType());
			this.settlementType.setValue(String.valueOf(settlement.getSettlementType()),
					settlement.getSettlementCode());
		}

		if (settlement.getSettlementStatus() == null) {
			fillComboBox(this.settlementStatus, RepayConstants.SETTLEMENT_STATUS_INITIATED,
					PennantStaticListUtil.getEnquiryReceiptModeStatus(), "");
		} else {
			fillComboBox(this.settlementStatus, settlement.getSettlementStatus(),
					PennantStaticListUtil.getEnquirySettlementStatus(), "");
		}
		this.startDate.setValue(settlement.getStartDate());
		this.otsDate.setValue(settlement.getOtsDate());
		if (settlement.getSettlementReasonId() > 0) {
			this.settlementReason.setAttribute("settlementReason", settlement.getSettlementReasonId());
		} else {
			this.settlementReason.setAttribute("settlementReason", 0);
		}
		this.settlementReason.setValue(settlement.getSettlementReason());
		this.settlementReason.setDescription(settlement.getSettlementReasonDesc());
		this.cancelReasonCode.setValue(settlement.getCancelReasonCode());
		this.cancelRemarks.setValue(settlement.getCancelRemarks());
		this.endDate.setValue(settlement.getEndDate());
		this.noOfGraceDays.setValue(settlement.getNoOfGraceDays());
		this.settlementEndAfterGrace.setValue(settlement.getSettlementEndAfterGrace());

		if (financeDetail != null) {
			this.finReference.setValue(financeDetail.getFinScheduleData().getFinReference());
			this.finID.setValue(financeDetail.getFinScheduleData().getFinID());
		} else {
			this.finReference.setValue(settlement.getFinReference());
			this.finID.setValue(settlement.getFinID());
		}

		this.settlementAmount.setValue(PennantApplicationUtil.formateAmount(settlement.getSettlementAmount(),
				PennantConstants.defaultCCYDecPos));

		if (CollectionUtils.isNotEmpty(settlement.getSettlementScheduleList())) {
			doFillSettlementSchDetails(settlement.getSettlementScheduleList());
			setSettlementScheduleDetailList(settlement.getSettlementScheduleList());
		}

		this.recordStatus.setValue(settlement.getRecordStatus());
		if (settlement.isNewRecord() || enqiryModule) {
			this.btnSearchFinreference.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Showing Finance details on Clicking Finance View Button
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchFinreference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Date otsDate = this.otsDate.getValue();
		receiptData = settlementService.getDues(finReference.getValue(), otsDate);
		// Preparation of Finance Enquiry Data
		FinReceiptHeader finReceiptHeader = receiptData.getReceiptHeader();
		FinanceMain fm = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceEnquiry aFinanceEnq = new FinanceEnquiry();
		aFinanceEnq.setFinID(finReceiptHeader.getFinID());
		aFinanceEnq.setFinReference(finReceiptHeader.getReference());
		aFinanceEnq.setFinID(finReceiptHeader.getFinID());
		aFinanceEnq.setFinType(finReceiptHeader.getFinType());
		aFinanceEnq.setLovDescFinTypeName(fm.getLovDescFinTypeName());
		aFinanceEnq.setFinCcy(finReceiptHeader.getFinCcy());
		aFinanceEnq.setScheduleMethod(finReceiptHeader.getScheduleMethod());
		aFinanceEnq.setProfitDaysBasis(finReceiptHeader.getPftDaysBasis());
		aFinanceEnq.setFinBranch(finReceiptHeader.getFinBranch());
		aFinanceEnq.setLovDescFinBranchName(finReceiptHeader.getFinBranchDesc());
		aFinanceEnq.setLovDescCustCIF(finReceiptHeader.getCustCIF());
		aFinanceEnq
				.setFinIsActive(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().isFinIsActive());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("moduleCode", this.module);
		map.put("fromApproved", true);
		map.put("childDialog", true);
		map.put("financeEnquiry", aFinanceEnq);
		map.put("ReceiptDialog", this);
		map.put("isModelWindow", true);
		map.put("enquiryType", "FINENQ");
		Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
				this.window_SettlementDialog, map);

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void createTab(String moduleID, boolean tabVisible) {
		logger.trace(Literal.ENTERING);

		String tabName = "";
		if (StringUtils.equals(AssetConstants.UNIQUE_ID_JOINTGUARANTOR, moduleID)) {
			tabName = Labels.getLabel("tab_Co-borrower&Gurantors");
		} else if (StringUtils.equals(AssetConstants.UNIQUE_ID_ADDITIONALFIELDS, moduleID)) {
			tabName = getFinanceDetail().getExtendedFieldHeader().getTabHeading();
		} else {
			tabName = Labels.getLabel("tab_label_" + moduleID);
		}
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));

		logger.trace(Literal.LEAVING);
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEscrowAccount
	 */
	public void doWriteComponentsToBean(FinSettlementHeader settlement) {
		logger.debug(Literal.ENTERING);
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Settlement Type
		try {
			if (this.settlementType.getValue() != null) {
				settlement.setSettlementType(Long.parseLong(this.settlementType.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setCancelReasonCode(this.cancelReasonCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setSettlementStatus(getComboboxValue(settlementStatus));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setStartDate(this.startDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setOtsDate(this.otsDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setEndDate(this.endDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setCancelRemarks(this.cancelRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setFinID(this.finID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setSettlementReason(this.settlementReason.getValue());
			settlement.setSettlementReasonDesc(this.settlementReason.getDescription());
			Object object = this.settlementReason.getAttribute("settlementReason");
			if (object != null) {
				settlement.setSettlementReasonId(Long.parseLong(object.toString()));
			} else {
				settlement.setSettlementReasonId(0L);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setNoOfGraceDays(this.noOfGraceDays.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			settlement.setSettlementEndAfterGrace(this.settlementEndAfterGrace.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			BigDecimal settlementAmount = PennantApplicationUtil.unFormateAmount(this.settlementAmount.getActualValue(),
					PennantConstants.defaultCCYDecPos);

			settlement.setSettlementAmount(settlementAmount);

		} catch (WrongValueException we) {
			wve.add(we);
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

		@SuppressWarnings("rawtypes")
		Map<String, List> settlementSchDetails = settlementScheduleInlineEditCtrl.preparesettlementScheduleData(
				this.listBoxSettlementScheduleInlineEdit, settlement.getSettlementScheduleList(), settlement.getID());

		if (settlementSchDetails.get("errorList") != null) {
			@SuppressWarnings("unchecked")
			List<WrongValueException> errorlist = (List<WrongValueException>) settlementSchDetails.get("errorList");
			showErrorDetails(errorlist, null, settlementDetails);
		}
		if (settlementSchDetails.get("settlementSchedule") != null) {
			@SuppressWarnings("unchecked")
			List<SettlementSchedule> settlementScheduleList = settlementSchDetails.get("settlementSchedule");
			setSettlementScheduleDetailList(settlementScheduleList);
		}

		// Set Escrow Transaction Details
		Cloner cloner = new Cloner();
		settlement.setSettlementScheduleList(cloner.deepClone(this.settlementScheduleDetailList));

		settlement.setRecordStatus(this.recordStatus.getValue());

		logger.debug(Literal.LEAVING);
	}

	private void showErrorDetails(List<WrongValueException> wve, Tab parentTab, Tab childTab) {
		logger.debug(Literal.ENTERING);
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (parentTab != null) {
				parentTab.setSelected(true);
			}
			childTab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				Clients.scrollIntoView(wvea[i].getComponent());
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param EscrowAccount The entity that need to be render.
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void doShowDialog(FinSettlementHeader settlement) throws ParseException, InterruptedException {
		logger.debug(Literal.ENTERING);

		if (settlement.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.settlementType.focus();
		} else {
			this.settlementType.setReadonly(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(settlement.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			readOnlyComponent(true, this.settlementType);
			readOnlyComponent(true, this.settlementStatus);
			readOnlyComponent(true, this.startDate);
			readOnlyComponent(true, this.otsDate);
			readOnlyComponent(true, this.settlementReason);
			readOnlyComponent(true, this.endDate);
			readOnlyComponent(true, this.finReference);
			readOnlyComponent(true, this.cancelRemarks);
			readOnlyComponent(true, this.settlementAmount);
			readOnlyComponent(true, this.settlementEndAfterGrace);
			readOnlyComponent(true, this.noOfGraceDays);
			readOnlyComponent(true, this.finID);
		}

		if (StringUtils.equals(this.module, "SETTLEMENT_CANCEL")) {
			readOnlyComponent(true, this.settlementType);
			readOnlyComponent(true, this.settlementStatus);
			readOnlyComponent(true, this.startDate);
			readOnlyComponent(true, this.otsDate);
			readOnlyComponent(true, this.settlementReason);
			readOnlyComponent(true, this.endDate);
			readOnlyComponent(true, this.finReference);
			readOnlyComponent(true, this.settlementAmount);
			readOnlyComponent(true, this.settlementEndAfterGrace);
			readOnlyComponent(true, this.noOfGraceDays);
			readOnlyComponent(true, this.finID);
			this.btnNew_SettlementSchedule.setVisible(false);
		}

		doWriteBeanToComponents(settlement);

		if (this.enqiryModule) {
			this.window_SettlementDialog.doModal();
		} else {
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		// Settlement Type
		if (!this.settlementType.isReadonly()) {
			this.settlementType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_SettlementDialog_SettlementType.value"), null, true));
		}

		// settlement Amount
		if (!this.settlementAmount.isReadonly()) {
			this.settlementAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_SettlementDialog_SettlementAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false));

		}

		if (this.cancellationReason.isVisible() && !this.cancelReasonCode.isReadonly()) {
			if (!this.cancelReasonCode.isReadonly()) {
				this.cancelReasonCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_SettlementDialog_CancellationReasonCode.value"), null, true));
			}
			if (!this.cancelRemarks.isReadonly()) {
				this.cancelRemarks.setConstraint(
						new PTStringValidator(Labels.getLabel("label_SettlementDialog_CancellationRemarks.value"),
								PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL, true));
			}
		}

		if (!this.settlementReason.isReadonly()) {
			this.settlementReason.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SettlementDialog_SettlementReason.value"), null, true));
		}

		if (!this.otsDate.isReadonly()) {
			this.otsDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SettlementDialog_OTSDate.value"),
					true, this.startDate.getValue(), null, false));
		}

		if (!this.endDate.isReadonly()) {
			this.endDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SettlementDialog_EndDate.value"),
					true, this.otsDate.getValue(), null, false));
		}

		if (!this.startDate.isReadonly()) {
			this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SettlementDialog_StartDate.value"),
					true, true, null, false));
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean isValidData() {
		boolean isValid = true;
		if (this.settlement.isNewRecord() && !this.settlementAmount.isReadonly()
				&& PennantApplicationUtil
						.unFormateAmount(this.settlementAmount.getValidateValue(), PennantConstants.defaultCCYDecPos)
						.compareTo(totalDue) != 0) {
			String[] args = new String[2];

			args[0] = this.settlementAmount.getValidateValue().toString();
			args[1] = PennantApplicationUtil.formateAmount(totalDue, formatter).toString();
			MessageUtil.showError(Labels.getLabel("label_Settlement_equals_due", args));
			return false;
		}

		return isValid;
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.settlementType.setConstraint("");
		this.cancelReasonCode.setConstraint("");
		this.settlementStatus.setConstraint("");
		this.startDate.setConstraint("");
		this.otsDate.setConstraint("");
		this.settlementReason.setConstraint("");
		this.endDate.setConstraint("");
		this.finReference.setConstraint("");
		this.cancelRemarks.setConstraint("");
		this.settlementAmount.setConstraint("");
		this.settlementEndAfterGrace.setConstraint("");
		this.noOfGraceDays.setConstraint("");
		this.finID.setConstraint("");
		logger.debug(Literal.LEAVING);
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
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.settlementType.setErrorMessage("");
		this.cancelReasonCode.setErrorMessage("");
		this.settlementStatus.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.otsDate.setErrorMessage("");
		this.settlementReason.setErrorMessage("");
		this.endDate.setErrorMessage("");
		this.finReference.setErrorMessage("");
		this.cancelRemarks.setErrorMessage("");
		this.settlementAmount.setErrorMessage("");
		this.settlementEndAfterGrace.setErrorMessage("");
		this.noOfGraceDays.setErrorMessage("");
		this.finID.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final FinSettlementHeader aSettlement = new FinSettlementHeader();
		BeanUtils.copyProperties(this.settlement, aSettlement);

		doDelete(String.valueOf(aSettlement.getSettlementType()), aSettlement);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);
		if (this.settlement.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.settlementType);
		} else {
			readOnlyComponent(true, this.settlementType);
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("SettlementDialog_StartDate"), this.startDate);
		readOnlyComponent(isReadOnly("SettlementDialog_OtsDate"), this.otsDate);
		readOnlyComponent(isReadOnly("SettlementDialog_SettlementReason"), this.settlementReason);
		readOnlyComponent(isReadOnly("SettlementDialog_EndDate"), this.endDate);
		readOnlyComponent(isReadOnly("SettlementDialog_SettlementAmount"), this.settlementAmount);
		readOnlyComponent(isReadOnly("SettlementDialog_NoOfGraceDays"), this.noOfGraceDays);
		readOnlyComponent(isReadOnly("SettlementDialog_FinID"), this.finID);
		readOnlyComponent(isReadOnly("SettlementDialog_CancelReasonCode"), this.cancelReasonCode);
		readOnlyComponent(isReadOnly("SettlementDialog_CancelRemarks"), this.cancelRemarks);
		// readOnlyComponent(isReadOnly("SettlementDialog_FinReference"), this.finReference);
		// readOnlyComponent(isReadOnly("SettlementDialog_SettlementStatus"), this.settlementStatus);
		// readOnlyComponent(isReadOnly("SettlementDialog_SettlementEndAfterGrace"), this.settlementEndAfterGrace);
		this.finReference.setReadonly(true);
		this.settlementStatus.setDisabled(true);
		this.settlementEndAfterGrace.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.settlement.isNewRecord()) {
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {

		readOnlyComponent(true, this.settlementType);
		readOnlyComponent(true, this.cancelReasonCode);
		readOnlyComponent(true, this.settlementStatus);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.otsDate);
		readOnlyComponent(true, this.settlementReason);
		readOnlyComponent(true, this.endDate);
		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.cancelRemarks);
		readOnlyComponent(true, this.settlementAmount);
		readOnlyComponent(true, this.settlementEndAfterGrace);
		readOnlyComponent(true, this.noOfGraceDays);
		readOnlyComponent(true, this.finID);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
	}

	/**
	 * Clear's the components values.
	 */
	public void doClear() {
		this.settlementType.setValue("");
		this.settlementType.setDescription("");
		this.cancelReasonCode.setValue("");
		this.cancelReasonCode.setDescription("");
		this.settlementStatus.setValue("");
		this.startDate.setValue(null);
		this.otsDate.setValue(null);
		this.settlementReason.setValue("");
		this.endDate.setValue(null);
		this.finReference.setValue("");
		this.cancelRemarks.setValue("");
		this.settlementAmount.setValue(BigDecimal.ZERO);
		this.settlementEndAfterGrace.setValue(null);
		this.noOfGraceDays.setValue((long) 0);
		this.finID.setValue((long) 0);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final FinSettlementHeader aSettlement = this.settlement;
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aSettlement);

		isNew = aSettlement.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSettlement.getRecordType())) {
				aSettlement.setVersion(aSettlement.getVersion() + 1);
				if (isNew) {
					aSettlement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSettlement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSettlement.setNewRecord(true);
				}
			}
		} else {
			aSettlement.setVersion(aSettlement.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (isValidData()) {
				if (doProcess(aSettlement, tranType)) {
					refreshList();
					closeDialog();
				}

			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(FinSettlementHeader aSettlement, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSettlement.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSettlement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSettlement.setUserDetails(getUserWorkspace().getLoggedInUser());

		// Finance Asset Type
		List<SettlementAllocationDetail> settlementAllocationDetails = settlement.getSettlementAllocationDetails();
		if (CollectionUtils.isNotEmpty(settlementAllocationDetails)) {
			for (SettlementAllocationDetail settlementAllocationDetail : settlementAllocationDetails) {
				settlementAllocationDetail.setHeaderID(aSettlement.getID());
				settlementAllocationDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				settlementAllocationDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				settlementAllocationDetail.setRecordStatus(aSettlement.getRecordStatus());
				settlementAllocationDetail.setWorkflowId(aSettlement.getWorkflowId());
				settlementAllocationDetail.setTaskId(taskId);
				settlementAllocationDetail.setNextTaskId(nextTaskId);
				settlementAllocationDetail.setRoleCode(getRole());
				settlementAllocationDetail.setNextRoleCode(nextRoleCode);
				if (PennantConstants.RECORD_TYPE_DEL.equals(aSettlement.getRecordType())) {
					if (StringUtils.trimToNull(settlementAllocationDetail.getRecordType()) == null) {
						settlementAllocationDetail.setRecordType(aSettlement.getRecordType());
						settlementAllocationDetail.setNewRecord(true);
					}
				}
			}
		}

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSettlement.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSettlement.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSettlement);
				}

				if (isNotesMandatory(taskId, aSettlement)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aSettlement.setTaskId(taskId);
			aSettlement.setNextTaskId(nextTaskId);
			aSettlement.setRoleCode(getRole());
			aSettlement.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSettlement, tranType);
			String operationRefs = getServiceOperations(taskId, aSettlement);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSettlement, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSettlement, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinSettlementHeader aSettlement = (FinSettlementHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = settlementService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = settlementService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = settlementService.doApprove(auditHeader);

						if (aSettlement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = settlementService.doReject(auditHeader);
						if (aSettlement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SettlementDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SettlementDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.settlement), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (AppException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FinSettlementHeader aSettlement, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSettlement.getBefImage(), aSettlement);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aSettlement.getUserDetails(),
				getOverideMap());
	}

	// ********************************************************************//
	// ** New Button Events for Settlement Schedule List **//
	// ********************************************************************//
	public void onClick$btnNew_SettlementSchedule(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		SettlementSchedule settlementSchedule = new SettlementSchedule();
		settlementSchedule.setNewRecord(true);
		settlementSchedule.setWorkflowId(0);
		settlementSchedule.setSettlementHeaderID(getSettlement().getID());
		settlementSchedule.setRecordType(PennantConstants.RCD_ADD);
		this.settlementScheduleDetailList.add(settlementSchedule);
		settlementScheduleInlineEditCtrl.doFillSettlementSchedule(settlementSchedule,
				this.listBoxSettlementScheduleInlineEdit, this.listBoxSettlementScheduleInlineEdit.getItemCount() + 1);

		logger.debug(Literal.LEAVING);
	}

	public void onClickSettlementScheduleButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		settlementScheduleInlineEditCtrl.doDelete(this.listBoxSettlementScheduleInlineEdit, item);
		logger.debug(Literal.LEAVING);
	}

	public void doFillSettlementSchDetails(List<SettlementSchedule> settlementScheduleList) {
		logger.debug(Literal.ENTERING);
		this.listBoxSettlementSchedule.getItems().clear();
		totSetAmount = BigDecimal.ZERO;
		for (SettlementSchedule setsch : settlementScheduleList) {
			totSetAmount = totSetAmount.add(setsch.getSettlementAmount());
		}
		settlementScheduleInlineEditCtrl.doRenderSettlementSchList(settlementScheduleList,
				listBoxSettlementScheduleInlineEdit);
		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings("unchecked")
	public void onChangesettlementInstalDate(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Datebox date = (Datebox) event.getOrigin().getTarget();
		SettlementSchedule sd = (SettlementSchedule) date.getAttribute("data");
		sd.setSettlementInstalDate(date.getValue());
		Listcell cellRecordType = (Listcell) date.getAttribute("cellRecordType");
		if (cellRecordType != null) {
			cellRecordType.setLabel(PennantJavaUtil.getLabel(PennantConstants.RCD_EDT));
		}
		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings("unchecked")
	public void onFulfillsettlementAmount(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		CurrencyBox setAmount = (CurrencyBox) event.getOrigin().getTarget();
		Listcell cellRecordType = (Listcell) setAmount.getAttribute("cellRecordType");
		SettlementSchedule settlementSchedule = (SettlementSchedule) setAmount.getAttribute("data");
		settlementSchedule.setSettlementAmount(setAmount.getActualValue());
		if (cellRecordType != null) {
			cellRecordType.setLabel(PennantJavaUtil.getLabel(PennantConstants.RCD_EDT));
		}

		logger.debug(Literal.LEAVING);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public SettlementScheduleInlineEditCtrl getSettlementScheduleInlineEditCtrl() {
		return settlementScheduleInlineEditCtrl;
	}

	public void setSettlementScheduleInlineEditCtrl(SettlementScheduleInlineEditCtrl settlementScheduleInlineEditCtrl) {
		this.settlementScheduleInlineEditCtrl = settlementScheduleInlineEditCtrl;
	}

	public List<SettlementSchedule> getSettlementScheduleDetailList() {
		return settlementScheduleDetailList;
	}

	public void setSettlementScheduleDetailList(List<SettlementSchedule> settlementScheduleDetailList) {
		this.settlementScheduleDetailList = settlementScheduleDetailList;
	}

	public List<SettlementSchedule> getSettlementScheduleList() {
		return settlementScheduleList;
	}

	public void setSettlementScheduleList(List<SettlementSchedule> settlementScheduleList) {
		this.settlementScheduleList = settlementScheduleList;
	}

	public SettlementListCtrl getSettlementListCtrl() {
		return settlementListCtrl;
	}

	public void setSettlementListCtrl(SettlementListCtrl settlementListCtrl) {
		this.settlementListCtrl = settlementListCtrl;
	}

	public FinSettlementHeader getSettlement() {
		return settlement;
	}

	public void setSettlement(FinSettlementHeader settlement) {
		this.settlement = settlement;
	}

	public SettlementService getSettlementService() {
		return settlementService;
	}

	public void setSettlementService(SettlementService settlementService) {
		this.settlementService = settlementService;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}
