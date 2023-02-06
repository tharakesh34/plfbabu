package com.pennant.pff.settlement.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.settlement.model.FinSettlementHeader;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectSettlementDialogCtrl extends GFCBaseCtrl<FinSettlementHeader> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectSettlementDialogCtrl.class);

	protected Window windowSelectSettlement;
	protected ExtendedCombobox finReference;
	protected Button btnProceed;

	private FinanceMain finMain;
	private transient WorkFlowDetails workFlowDetails;

	private String moduleDefiner;
	private String workflowCode;
	private String eventCode;
	private String menuItemRightName;
	private List<String> roleList = new ArrayList<>();
	private String module = "";
	private FinSettlementHeader settlement;

	private transient SettlementListCtrl settlementListCtrl;
	private transient FinanceDetailService financeDetailService;
	private transient FeeWaiverHeaderService feeWaiverHeaderService;
	private transient ManualAdviseService manualAdviseService;
	private transient PaymentHeaderService paymentHeaderService;

	public SelectSettlementDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SelectSettlementDialog";
	}

	public void onCreate$windowSelectSettlement(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(this.windowSelectSettlement);

		if (arguments.containsKey("settlement")) {
			this.settlement = (FinSettlementHeader) arguments.get("settlement");
			FinSettlementHeader befImage = new FinSettlementHeader();
			BeanUtils.copyProperties(this.settlement, befImage);
			this.settlement.setBefImage(befImage);
			setSettlement(this.settlement);
		} else {
			setSettlement(null);
		}

		if (arguments.containsKey("SettlementListCtrl")) {
			this.settlementListCtrl = (SettlementListCtrl) arguments.get("SettlementListCtrl");
		}

		if (arguments.containsKey("module")) {
			this.module = (String) arguments.get("module");
		}

		try {
			doSetFieldProperties();
			showSelectPaymentHeaderDialog();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void showSelectPaymentHeaderDialog() {
		logger.debug(Literal.ENTERING);

		try {
			this.windowSelectSettlement.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("SettlementFinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);

		this.finReference.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$finReference(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Clients.clearWrongValue(this.finReference);
		Object dataObject = this.finReference.getObject();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			this.finReference.setDescription("");
		} else {
			finMain = (FinanceMain) dataObject;
			if (finMain != null) {
				this.finReference.setValue(finMain.getFinReference());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doFieldValidation();

		setWorkflowDetails(finMain.getFinType(), StringUtils.isNotEmpty(finMain.getLovDescFinProduct()));

		long finID = ComponentUtil.getFinID(this.finReference);

		this.settlement.setFinID(finID);

		String userRole = finMain.getNextRoleCode();

		final FinanceDetail financeDetail = financeDetailService.getServicingFinance(finID, eventCode, null, userRole);
		financeDetail.setModuleDefiner(moduleDefiner);

		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();

		if (fm.isWriteoffLoan()) {
			MessageUtil.showError(PennantJavaUtil.getLabel("label_Writeoff_Loan"));
			return;
		}

		if (fm.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			fm.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		if (manualAdviseService.isunAdjustablePayables(fm.getFinID())) {
			MessageUtil.showError("Loan having Pending Payables, unable to proceed.");
			return;
		}

		List<FinExcessAmount> excessAmt = paymentHeaderService.getfinExcessAmount(fm.getFinID());
		
		for (FinExcessAmount fea : excessAmt) {
			if (fea.getBalanceAmt().compareTo(BigDecimal.ZERO) > 0
					|| fea.getReservedAmt().compareTo(BigDecimal.ZERO) > 0) {
				MessageUtil.showError("Loan having Balance amount in Excess, unable to proceed.");
				return;
			}
		}

		String nextroleCode = fm.getNextRoleCode();
		if (nextroleCode == null) {
			List<FeeWaiverHeader> waivers = feeWaiverHeaderService.getFeeWaiverHeaderByFinReference(finID, "");
			for (FeeWaiverHeader fwh : waivers) {
				if (fwh.isAlwCondWaiver() && "P".equals(fwh.getStatus())) {
					MessageUtil.showError("Loan is under Conditional Waiver");

					logger.debug(Literal.LEAVING);
					return;
				}
			}
		} else if (StringUtils.isNotBlank(nextroleCode) && !nextroleCode.equals(userRole)) {
			MessageUtil.showError("Not allowed to initiate the settlement as LAN is in maintanance.");

			logger.debug(Literal.LEAVING);
			return;
		}

		Map<String, Object> map = new HashMap<>();
		map.put("financeDetail", financeDetail);
		map.put("moduleDefiner", moduleDefiner);
		map.put("workflowCode", workflowCode);
		map.put("eventCode", eventCode);
		map.put("menuItemRightName", menuItemRightName);
		map.put("role", roleList);
		map.put("settlement", settlement);
		map.put("SettlementListCtrl", settlementListCtrl);
		map.put("module", this.module);
		try {
			Executions.createComponents("/WEB-INF/pages/Settlement/SettlementDialog.zul", null, map);
			this.windowSelectSettlement.onClose();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void setWorkflowDetails(String finType, boolean isPromotion) {
		if (StringUtils.isNotEmpty(workflowCode)) {
			String workflowTye = financeWorkFlowService.getFinanceWorkFlowType(finType, workflowCode,
					isPromotion ? PennantConstants.WORFLOW_MODULE_PROMOTION : PennantConstants.WORFLOW_MODULE_FINANCE);
			if (workflowTye != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(workflowTye);
			}
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	}

	private void doFieldValidation() {
		logger.debug(Literal.ENTERING);

		doClearMessage();

		List<WrongValueException> wve = new ArrayList<>();
		try {
			if (StringUtils.trimToNull(this.finReference.getValue()) == null) {
				throw new WrongValueException(this.finReference, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectPaymentHeaderDialog_FinaType.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		doRemoveValidation();
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		this.finReference.setErrorMessage("");
	}

	private void doRemoveValidation() {
		this.finReference.setConstraint("");
	}

	public void setFinMain(FinanceMain finMain) {
		this.finMain = finMain;
	}

	public void setSettlement(FinSettlementHeader settlement) {
		this.settlement = settlement;
	}

	@Autowired
	public void setSettlementListCtrl(SettlementListCtrl settlementListCtrl) {
		this.settlementListCtrl = settlementListCtrl;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	@Autowired
	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

	@Autowired
	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	@Autowired
	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

}