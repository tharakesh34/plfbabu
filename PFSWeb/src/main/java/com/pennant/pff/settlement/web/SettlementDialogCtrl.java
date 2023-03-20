package com.pennant.pff.settlement.web;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.settlement.model.FinSettlementHeader;
import com.pennant.pff.settlement.model.SettlementAllocationDetail;
import com.pennant.pff.settlement.model.SettlementSchedule;
import com.pennant.pff.settlement.model.SettlementTypeDetail;
import com.pennant.pff.settlement.service.SettlementService;
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

public class SettlementDialogCtrl extends GFCBaseCtrl<FinSettlementHeader> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SettlementDialogCtrl.class);

	protected Window settlementWindow;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected ExtendedCombobox settlementType;
	protected Combobox settlementStatus;
	protected Datebox startDate;
	protected Datebox otsDate;
	protected ExtendedCombobox settlementReason;
	protected Datebox endDate;
	protected Textbox finReference;
	protected CurrencyBox settlementAmount;
	protected Datebox settlementEndAfterGrace;
	protected Longbox noOfGraceDays;
	protected ExtendedCombobox cancelReasonCode;
	protected Textbox cancelRemarks;
	protected Listbox listBoxSettlementScheduleInlineEdit;
	protected Button btnNewSettlementSchedule;
	protected Listbox listBoxSettlementSchedule;
	protected Listbox listBoxPastdues;
	protected Label windowSettlementDialogTitle;
	protected Row cancellationReason;
	protected Row cancellationRemarks;
	protected Button btnSearchFinreference;

	private String module = "";
	private int formatter = 2;
	private FinReceiptData receiptData = null;
	private BigDecimal totalDue = BigDecimal.ZERO;

	private FinSettlementHeader settlement;
	private FinanceDetail financeDetail;
	private List<SettlementSchedule> settlementScheduleDetailList = new ArrayList<>();

	private transient SettlementListCtrl settlementListCtrl;
	private transient SettlementScheduleInlineEditCtrl settlementScheduleInlineEditCtrl;
	private transient SettlementService settlementService;

	public SettlementDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SettlementDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.settlement.getId());
	}

	public void onCreate$settlementWindow(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(settlementWindow);

		try {
			if (arguments.containsKey("settlement")) {
				this.settlement = (FinSettlementHeader) arguments.get("settlement");
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
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("isEnqProcess")) {
				enqiryModule = true;
			}

			if (arguments.containsKey("module")) {
				this.module = (String) arguments.get("module");
				if (StringUtils.equals(this.module, "SETTLEMENT_CANCEL")) {
					this.cancellationReason.setVisible(true);
					this.cancellationRemarks.setVisible(true);
					this.windowSettlementDialogTitle.setValue(Labels.getLabel("window_SettlementCancelDialog.title"));
				} else {
					this.cancellationReason.setVisible(false);
					this.cancellationRemarks.setVisible(false);
				}
			}

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
		} catch (AppException e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.settlementType.setModuleName("SettlementTypeDetail");
		this.settlementType.setMandatoryStyle(true);
		this.settlementType.setValueColumn("id");
		this.settlementType.setDescColumn("settlementCode");
		this.settlementType.setDisplayStyle(2);
		this.settlementType.setValidateColumns(new String[] { "settlementCode" });

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

		this.listBoxSettlementSchedule.setVisible(false);
		this.listBoxSettlementScheduleInlineEdit.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SettlementDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SettlementDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SettlementDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnNewSettlementSchedule.setVisible(getUserWorkspace().isAllowed("button_SettlementDialog_btnNew"));

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doSave();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doEdit();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnDelete(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doDelete();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doCancel();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doShowNotes(this.settlement);
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	@Override
	protected void refreshList() {
		settlementListCtrl.search();
	}

	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.settlement.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$settlementType(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

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
		logger.debug(Literal.ENTERING.concat(event.toString()));

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
		logger.debug(Literal.ENTERING.concat(event.toString()));

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
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Date endDate = this.endDate.getValue();
		Date settlGraceDate = DateUtil.addDays(endDate, this.noOfGraceDays.getValue().intValue());
		this.settlementEndAfterGrace.setValue(settlGraceDate);

		logger.debug(Literal.LEAVING);
	}

	public void onChange$otsDate(ForwardEvent event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Date otsDate = this.otsDate.getValue();

		if (otsDate == null) {
			this.listBoxPastdues.getItems().clear();
			return;
		}
		receiptData = settlementService.getDues(finReference.getValue(), otsDate);
		FinanceMain fm = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		formatter = CurrencyUtil.getFormat(fm.getFinCcy());
		List<SettlementAllocationDetail> allocations = new ArrayList<SettlementAllocationDetail>();
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocationsSummary();

		for (int i = 0; i < allocationList.size(); i++) {
			ReceiptAllocationDetail allocation = allocationList.get(i);
			allocation.setAllocationID(i + 1);
		}

		for (ReceiptAllocationDetail rad : allocationList) {
			SettlementAllocationDetail sad = new SettlementAllocationDetail();
			sad.setId(rad.getAllocationID());
			sad.setAllocationType(rad.getAllocationType());
			sad.setAllocationTo(rad.getAllocationTo());
			sad.setTotalDue(rad.getTotalDue());
			sad.setPaidAmount(rad.getPaidAmount());
			sad.setPaidGST(rad.getPaidGST());
			sad.setWaivedAmount(rad.getWaivedAmount());
			sad.setWaiverAccepted(rad.getWaiverAccepted());
			sad.setWaivedGST(rad.getWaivedGST());
			sad.setTdsDue(rad.getTdsDue());
			sad.setTdsPaid(rad.getTdsPaid());
			sad.setTdsWaived(rad.getTdsWaived());
			sad.setTypeDesc(rad.getTypeDesc());
			sad.setSubListAvailable(rad.isSubListAvailable());
			sad.setBalance(rad.getBalance());
			allocations.add(sad);
		}

		settlement.setSettlementAllocationDetails(allocations);
		doFillAllocationDetail(allocations);
		this.btnSearchFinreference.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	public void onAllocateWaivedChange(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
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

		for (SettlementAllocationDetail sad : settlement.getSettlementAllocationDetails()) {
			if (Allocation.PRI.equals(sad.getAllocationType())) {
				priWaived = sad.getWaivedAmount();
			}
			if (Allocation.PFT.equals(sad.getAllocationType())) {
				pftWaived = sad.getWaivedAmount();
			}
		}

		for (SettlementAllocationDetail sad : settlement.getSettlementAllocationDetails()) {
			if (Allocation.EMI.equals(sad.getAllocationType())) {
				if ((priWaived.add(pftWaived)).compareTo(BigDecimal.ZERO) > 0) {
					sad.setWaivedAmount(priWaived.add(pftWaived));
					sad.setBalance(sad.getTotalDue().subtract(priWaived.add(pftWaived)));
				} else {
					sad.setWaivedAmount(BigDecimal.ZERO);
					sad.setBalance(sad.getTotalDue());
				}
				break;
			}
		}

		doFillAllocationDetail(settlement.getSettlementAllocationDetails());
		logger.debug(Literal.LEAVING);
	}

	public void doFillAllocationDetail(List<SettlementAllocationDetail> settlementAllocationDetail) {
		logger.debug(Literal.ENTERING);
		this.listBoxPastdues.getItems().clear();

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

		String allocType = allocate.getAllocationType();
		if (Allocation.NPFT.equals(allocType) || Allocation.FUT_NPFT.equals(allocType)) {
			return;
		}

		Listitem item = new Listitem();

		addBoldTextCell(item, allocate.getTypeDesc(), allocate.isSubListAvailable(), idx);
		addAmountCell(item, allocate.getTotalDue(), ("AllocateCurDue_" + idx), true);

		Listcell lc = new Listcell();
		CurrencyBox allocationWaived = new CurrencyBox();
		allocationWaived.setStyle("text-align:right;");
		allocationWaived.setBalUnvisible(true, true);
		setProps(allocationWaived, false, formatter, 120);
		allocationWaived.setId("AllocateWaived_" + idx);
		allocationWaived.setValue(PennantApplicationUtil.formateAmount(allocate.getWaivedAmount(), formatter));
		allocationWaived.addForward("onFulfill", this.settlementWindow, "onAllocateWaivedChange", idx);

		allocationWaived.setReadonly(!getUserWorkspace().isAllowed("SettlementDialog_WaivedAmount"));
		if (Allocation.EMI.equals(allocType)) {
			allocationWaived.setReadonly(true);
		}
		lc.appendChild(allocationWaived);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		addAmountCell(item, allocate.getBalance(), ("AllocateBalDue_" + idx), true);

		this.listBoxPastdues.appendChild(item);

		logger.debug(Literal.LEAVING);
	}

	public void addBoldTextCell(Listitem item, String value, boolean hasChild, int buttonId) {
		Listcell lc = new Listcell(value);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		if (hasChild) {
			Button button = new Button("Details");
			button.setId(String.valueOf(buttonId));
			button.addForward("onClick", settlementWindow, "onDetailsClick", button.getId());
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
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Date endDate = this.endDate.getValue();
		if (endDate == null) {
			this.noOfGraceDays.setValue((long) 0);
			MessageUtil.showError(Labels.getLabel("label_validationForNoOfGraceDays"));
		} else {
			this.settlementEndAfterGrace.setValue(DateUtil.addDays(endDate, this.noOfGraceDays.getValue().intValue()));
		}

		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(FinSettlementHeader header) {
		logger.debug(Literal.ENTERING);

		long reasonID = header.getSettlementReasonId();
		long typeID = header.getSettlementType();
		String status = header.getSettlementStatus();
		BigDecimal amount = header.getSettlementAmount();

		if (status == null) {
			status = RepayConstants.SETTLEMENT_STATUS_INITIATED;
		}

		if (typeID > 0) {
			this.settlementType.setAttribute("settlementType", typeID);
			this.settlementType.setValue(String.valueOf(typeID), header.getSettlementCode());
		}

		fillComboBox(this.settlementStatus, status, PennantStaticListUtil.getEnquirySettlementStatus(), "");
		this.startDate.setValue(header.getStartDate());
		this.otsDate.setValue(header.getOtsDate());
		this.settlementReason.setAttribute("settlementReason", reasonID > 0 ? reasonID : 0);
		this.settlementReason.setValue(header.getSettlementReason());
		this.settlementReason.setDescription(header.getSettlementReasonDesc());
		this.cancelReasonCode.setValue(header.getCancelReasonCode());
		this.cancelRemarks.setValue(header.getCancelRemarks());
		this.endDate.setValue(header.getEndDate());
		this.noOfGraceDays.setValue(header.getNoOfGraceDays());
		this.settlementEndAfterGrace.setValue(header.getSettlementEndAfterGrace());
		this.settlementAmount.setValue(PennantApplicationUtil.formateAmount(amount, PennantConstants.defaultCCYDecPos));
		this.finReference.setValue(header.getFinReference());

		if (financeDetail != null) {
			this.finReference.setValue(financeDetail.getFinScheduleData().getFinReference());
		}

		if (CollectionUtils.isNotEmpty(header.getSettlementScheduleList())) {

			for (SettlementSchedule sch : header.getSettlementScheduleList()) {
				sch.setModule(this.moduleCode);
			}

			doFillSettlementSchDetails(header.getSettlementScheduleList());
			setSettlementScheduleDetailList(header.getSettlementScheduleList());
		}

		this.recordStatus.setValue(header.getRecordStatus());
		if (header.isNewRecord() || enqiryModule) {
			this.btnSearchFinreference.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSearchFinreference(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Date otsDate = this.otsDate.getValue();
		receiptData = settlementService.getDues(finReference.getValue(), otsDate);
		FinReceiptHeader header = receiptData.getReceiptHeader();
		FinanceMain fm = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		FinanceEnquiry enquiry = new FinanceEnquiry();
		enquiry.setFinID(header.getFinID());
		enquiry.setFinReference(header.getReference());
		enquiry.setFinID(header.getFinID());
		enquiry.setFinType(header.getFinType());
		enquiry.setLovDescFinTypeName(fm.getLovDescFinTypeName());
		enquiry.setFinCcy(header.getFinCcy());
		enquiry.setScheduleMethod(header.getScheduleMethod());
		enquiry.setProfitDaysBasis(header.getPftDaysBasis());
		enquiry.setFinBranch(header.getFinBranch());
		enquiry.setLovDescFinBranchName(header.getFinBranchDesc());
		enquiry.setLovDescCustCIF(header.getCustCIF());
		enquiry.setFinIsActive(fm.isFinIsActive());

		Map<String, Object> map = new HashMap<>();
		map.put("moduleCode", this.module);
		map.put("fromApproved", true);
		map.put("childDialog", true);
		map.put("financeEnquiry", enquiry);
		map.put("ReceiptDialog", this);
		map.put("isModelWindow", true);
		map.put("enquiryType", "FINENQ");
		Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
				this.settlementWindow, map);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doWriteComponentsToBean(FinSettlementHeader header) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			if (this.settlementType.getValue() != null) {
				header.setSettlementType(Long.parseLong(this.settlementType.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setCancelReasonCode(this.cancelReasonCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setSettlementStatus(getComboboxValue(settlementStatus));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setStartDate(this.startDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setOtsDate(this.otsDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setEndDate(this.endDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setCancelRemarks(this.cancelRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setSettlementReason(this.settlementReason.getValue());
			header.setSettlementReasonDesc(this.settlementReason.getDescription());
			Object object = this.settlementReason.getAttribute("settlementReason");
			if (object != null) {
				header.setSettlementReasonId(Long.parseLong(object.toString()));
			} else {
				header.setSettlementReasonId(0L);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setNoOfGraceDays(this.noOfGraceDays.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setSettlementEndAfterGrace(this.settlementEndAfterGrace.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setSettlementAmount(PennantApplicationUtil.unFormateAmount(this.settlementAmount.getActualValue(),
					PennantConstants.defaultCCYDecPos));
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

		if (RepayConstants.SETTLEMENT_STATUS_INIT_MSG.equals(this.settlementStatus.getValue())
				&& FinanceConstants.SETTLEMENT.equals(this.module)) {

			Map<String, List> settlementSchDetails = settlementScheduleInlineEditCtrl.preparesettlementSchdData(
					this.listBoxSettlementScheduleInlineEdit, header.getSettlementScheduleList(), header.getId());

			if (settlementSchDetails.get("errorList") != null) {
				showErrorDetails(settlementSchDetails.get("errorList"), null);
			}

			if (settlementSchDetails.get("settlementSchedule") != null) {
				setSettlementScheduleDetailList(settlementSchDetails.get("settlementSchedule"));
			}

			header.getSettlementScheduleList().clear();
			for (SettlementSchedule schedule : settlementScheduleDetailList) {
				header.getSettlementScheduleList().add(schedule.copyEntity());
			}
		}

		header.setRecordStatus(this.recordStatus.getValue());

		logger.debug(Literal.LEAVING);
	}

	private void showErrorDetails(List<WrongValueException> wve, Tab parentTab) {
		logger.debug(Literal.ENTERING);
		doRemoveValidation();

		if (!wve.isEmpty()) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (parentTab != null) {
				parentTab.setSelected(true);
			}

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				Clients.scrollIntoView(wvea[i].getComponent());
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(FinSettlementHeader settlement) {
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

			if ((!PennantConstants.RECORD_TYPE_NEW.equals(settlement.getRecordType())
					&& FinanceConstants.SETTLEMENT.equals(this.module))
					|| (!PennantConstants.RECORD_TYPE_NEW.equals(settlement.getRecordType())
							&& FinanceConstants.SETTLEMENT_CANCEL.equals(this.module)
							&& (RepayConstants.SETTLEMENT_STATUS_CANCELLED.equals(settlement.getSettlementStatus())
									&& PennantConstants.RCD_STATUS_APPROVED.equals(settlement.getRecordStatus())))) {
				doSetFieldEdit();
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
			this.btnNewSettlementSchedule.setVisible(false);
		}

		doWriteBeanToComponents(settlement);

		if (this.enqiryModule) {
			this.settlementWindow.doModal();
		} else {
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.settlementType.isReadonly()) {
			this.settlementType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_SettlementDialog_SettlementType.value"), null, true));
		}

		if (!this.settlementAmount.isReadonly()) {
			this.settlementAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_SettlementDialog_SettlementAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false));

		}

		if (this.cancellationReason.isVisible() && !this.cancelReasonCode.isReadonly()
				&& this.cancellationRemarks.isVisible()) {
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
		logger.debug(Literal.LEAVING);
	}

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
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final FinSettlementHeader aSettlement = new FinSettlementHeader();
		BeanUtils.copyProperties(this.settlement, aSettlement);

		doDelete(String.valueOf(aSettlement.getSettlementType()), aSettlement);

		logger.debug(Literal.LEAVING);
	}

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
		readOnlyComponent(isReadOnly("SettlementDialog_CancelReasonCode"), this.cancelReasonCode);
		readOnlyComponent(isReadOnly("SettlementDialog_CancelRemarks"), this.cancelRemarks);

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

		logger.debug(Literal.LEAVING);
	}

	public void doReadOnly() {
		readOnlyComponent(true, this.settlementType);
		readOnlyComponent(true, this.settlementStatus);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.otsDate);
		readOnlyComponent(true, this.settlementReason);
		readOnlyComponent(true, this.endDate);
		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.cancelRemarks);
		readOnlyComponent(true, this.cancelReasonCode);

		readOnlyComponent(true, this.settlementAmount);
		readOnlyComponent(true, this.settlementEndAfterGrace);
		readOnlyComponent(true, this.noOfGraceDays);

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

	public void doSetFieldEdit() {

		this.btnSave.setVisible(false);
		readOnlyComponent(true, this.settlementType);
		readOnlyComponent(true, this.settlementStatus);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.otsDate);
		readOnlyComponent(true, this.settlementReason);
		readOnlyComponent(true, this.endDate);
		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.cancelRemarks);
		readOnlyComponent(true, this.cancelReasonCode);

		readOnlyComponent(true, this.settlementAmount);
		readOnlyComponent(true, this.settlementEndAfterGrace);
		readOnlyComponent(true, this.noOfGraceDays);
		readOnlyComponent(true, this.btnNewSettlementSchedule);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setVisible(false);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
	}

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
	}

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

	protected boolean doProcess(FinSettlementHeader aSettlement, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSettlement.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSettlement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSettlement.setUserDetails(getUserWorkspace().getLoggedInUser());

		List<SettlementAllocationDetail> sad = settlement.getSettlementAllocationDetails();
		if (CollectionUtils.isNotEmpty(sad)) {
			for (SettlementAllocationDetail settlementAllocationDetail : sad) {
				settlementAllocationDetail.setHeaderID(aSettlement.getId());
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

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinSettlementHeader aSettlement = (FinSettlementHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

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
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.settlementWindow, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.settlementWindow, auditHeader);
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

		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private AuditHeader getAuditHeader(FinSettlementHeader header, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, header.getBefImage(), header);
		return new AuditHeader(getReference(), null, null, null, auditDetail, header.getUserDetails(), getOverideMap());
	}

	public void onClick$btnNewSettlementSchedule(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		SettlementSchedule schedule = new SettlementSchedule();
		schedule.setNewRecord(true);
		schedule.setWorkflowId(0);
		schedule.setSettlementHeaderID(settlement.getId());
		schedule.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		schedule.setModule(this.module);
		this.settlementScheduleDetailList.add(schedule);

		settlementScheduleInlineEditCtrl.doFillSettlementSchedule(schedule, this.listBoxSettlementScheduleInlineEdit,
				this.listBoxSettlementScheduleInlineEdit.getItemCount() + 1);

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

		settlementScheduleInlineEditCtrl.doRenderSettlementSchList(settlementScheduleList,
				listBoxSettlementScheduleInlineEdit);
		logger.debug(Literal.LEAVING);
	}

	public void onChangesettlementInstalDate(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Datebox date = (Datebox) event.getOrigin().getTarget();
		SettlementSchedule sd = (SettlementSchedule) date.getAttribute("data");
		sd.setSettlementInstalDate(date.getValue());
		Listitem selectecListItem = (Listitem) event.getOrigin().getTarget().getParent().getParent().getParent();
		Listcell cellRecordType = (Listcell) selectecListItem.getChildren().get(3);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfillsettlementAmount(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		CurrencyBox setAmount = (CurrencyBox) event.getOrigin().getTarget();
		Listitem selectecListItem = (Listitem) event.getOrigin().getTarget().getParent().getParent().getParent();
		Listcell cellRecordType = (Listcell) selectecListItem.getChildren().get(3);
		SettlementSchedule settlementSchedule = (SettlementSchedule) setAmount.getAttribute("data");
		settlementSchedule.setSettlementAmount(setAmount.getActualValue());

		logger.debug(Literal.LEAVING);
	}

	public void onDetailsClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		String buttonId = (String) event.getData();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("details",
				receiptData.getReceiptHeader().getAllocationsSummary().get(Integer.parseInt(buttonId)).getSubList());
		map.put("buttonId", buttonId);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/BounceDetailsDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void setSettlement(FinSettlementHeader settlement) {
		this.settlement = settlement;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setSettlementScheduleDetailList(List<SettlementSchedule> settlementScheduleDetailList) {
		this.settlementScheduleDetailList = settlementScheduleDetailList;
	}

	@Autowired
	public void setSettlementListCtrl(SettlementListCtrl settlementListCtrl) {
		this.settlementListCtrl = settlementListCtrl;
	}

	@Autowired
	public void setSettlementScheduleInlineEditCtrl(SettlementScheduleInlineEditCtrl settlementScheduleInlineEditCtrl) {
		this.settlementScheduleInlineEditCtrl = settlementScheduleInlineEditCtrl;
	}

	@Autowired
	public void setSettlementService(SettlementService settlementService) {
		this.settlementService = settlementService;
	}

}
