package com.pennant.webui.settlement;

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
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.settlement.FinSettlementHeader;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectSettlementDialogCtrl extends GFCBaseCtrl<FinSettlementHeader> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectSettlementDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SelectSettlementDialog; // autoWired
	protected ExtendedCombobox finReference; // autoWired
	protected Button btnProceed; // autoWired

	private FinanceMain finMain;
	private transient WorkFlowDetails workFlowDetails = null;
	private transient FinanceDetailService financeDetailService;
	private transient FinanceWorkFlowService financeWorkFlowService;
	private transient FeeWaiverHeaderService feeWaiverHeaderService;

	private String moduleDefiner;
	private String workflowCode;
	private String eventCode;
	private String menuItemRightName;
	private List<String> roleList = new ArrayList<String>();
	private String module = "";

	private FinSettlementHeader settlement;
	private SettlementListCtrl settlementListCtrl;

	/**
	 * default constructor.<br>
	 */
	public SelectSettlementDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SelectSettlementDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectSettlementDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(this.window_SelectSettlementDialog);

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

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Opens the SelectPaymentHeaderDialog window modal.
	 */
	private void showSelectPaymentHeaderDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			this.window_SelectSettlementDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
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
		// filters[1] = new Filter("RepayFrq", "D0000", Filter.OP_NOT_EQUAL);
		// filters[2] = new Filter("StepFinance", 1, Filter.OP_NOT_EQUAL);
		this.finReference.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$finReference(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
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

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		if (!doFieldValidation()) {
			logger.debug(Literal.LEAVING + event.toString());
			return;
		}

		// Validate Workflow Details
		setWorkflowDetails(finMain.getFinType(), StringUtils.isNotEmpty(finMain.getLovDescFinProduct()));
		/*
		 * if (workFlowDetails == null) { MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
		 * return; }
		 */

		long finID = ComponentUtil.getFinID(this.finReference);

		// Validate Loan is MATURED or INPROGRESS in any Other Servicing option or NOT ?
		FinanceMain financeMain = financeDetailService.getFinanceMain(finID, TableType.VIEW);

		Date maturityDate = financeMain.getMaturityDate();
		Date appDate = SysParamUtil.getAppDate();

		if (financeMain.isWriteoffLoan()) {
			MessageUtil.showError(PennantJavaUtil.getLabel("label_Writeoff_Loan"));
			return;
		}

		String rcdMaintainSts = financeMain.getRcdMaintainSts();
		/*
		 * if (StringUtils.isNotEmpty(rcdMaintainSts)) {
		 * MessageUtil.showError(PennantJavaUtil.getLabel("Finance_Inprogresss_" + rcdMaintainSts)); return; }
		 */

		String userRole = finMain.getNextRoleCode();
		/*
		 * if (StringUtils.isEmpty(userRole)) { userRole = workFlowDetails.getFirstTaskOwner(); }
		 */

		// Getting FinanceDetail Data
		final FinanceDetail financeDetail = financeDetailService.getServicingFinance(finID, eventCode, null, userRole);
		financeDetail.setModuleDefiner(moduleDefiner);

		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		if (fm.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			fm.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		financeDetail.getFinScheduleData().setFinanceMain(fm);

		// Role Code State Checking
		String nextroleCode = fm.getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());

			logger.debug(Literal.LEAVING + event.toString());
			return;
		} else if (nextroleCode == null) {
			List<FeeWaiverHeader> waiverList = feeWaiverHeaderService.getFeeWaiverHeaderByFinReference(finID, "");
			if (CollectionUtils.isNotEmpty(waiverList)) {
				for (FeeWaiverHeader feeWaiverHeader : waiverList) {
					if (feeWaiverHeader.isAlwCondWaiver() && StringUtils.equals(feeWaiverHeader.getStatus(), "P")) {
						MessageUtil.showError("Loan is under Conditional Waiver");

						logger.debug(Literal.LEAVING + event.toString());
						return;
					}
				}
			}
		}

		String maintainSts = "";
		if (fm != null) {
			maintainSts = StringUtils.trimToEmpty(fm.getRcdMaintainSts());
		}

		/*
		 * if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) { String[] errParm = new
		 * String[1]; String[] valueParm = new String[1]; valueParm[0] = fm.getFinReference(); errParm[0] =
		 * PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		 * 
		 * ErrorDetail errorDetails = ErrorUtil.getErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD, "41005",
		 * errParm, valueParm), getUserWorkspace().getUserLanguage()); MessageUtil.showError(errorDetails.getError());
		 * return; }
		 */

		Map<String, Object> map = new HashMap<String, Object>();
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
			this.window_SelectSettlementDialog.onClose();
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	private void setWorkflowDetails(String finType, boolean isPromotion) {
		// Finance Maintenance Workflow Check & Assignment
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

	/**
	 * Method for checking /validating fields before proceed.
	 * 
	 * @return
	 */
	private boolean doFieldValidation() {
		logger.debug(Literal.ENTERING);

		doClearMessage();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.trimToNull(this.finReference.getValue()) == null) {
				throw new WrongValueException(this.finReference, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectPaymentHeaderDialog_FinaType.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
		return true;
	}

	@Override
	protected void doClearMessage() {
		this.finReference.setErrorMessage("");
	}

	private void doRemoveValidation() {
		this.finReference.setConstraint("");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public FinanceMain getFinMain() {
		return finMain;
	}

	public void setFinMain(FinanceMain finMain) {
		this.finMain = finMain;
	}

	public void setSettlement(FinSettlementHeader settlement) {
		this.settlement = settlement;
	}

	public void setSettlementListCtrl(SettlementListCtrl settlementListCtrl) {
		this.settlementListCtrl = settlementListCtrl;
	}

	public FeeWaiverHeaderService getFeeWaiverHeaderService() {
		return feeWaiverHeaderService;
	}

	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

}