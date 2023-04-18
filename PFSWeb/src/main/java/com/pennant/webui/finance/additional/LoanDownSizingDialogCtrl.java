package com.pennant.webui.finance.additional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinAssetAmtMovement;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.LoanDownSizingService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennapps.core.util.ObjectUtil;

public class LoanDownSizingDialogCtrl extends GFCBaseCtrl<FinScheduleData> {

	private static final long serialVersionUID = 454600127282110738L;
	private static final Logger logger = LogManager.getLogger(LoanDownSizingDialogCtrl.class);

	protected Window window_LoanDownSizing;

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tab tab_BasicDetailsTab;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Div basicDetailDiv;

	protected Decimalbox totSanctionedAmt;
	protected Decimalbox disbursedAmt;
	protected Decimalbox availableAmt;
	protected Decimalbox revisedSanctionedAmt;
	protected CurrencyBox downSizingAmt;

	protected Textbox finReference;
	protected Textbox finBranch;
	protected Textbox custCIF;
	protected Textbox custShrtName;
	protected Textbox finType;
	protected Textbox startDate;
	protected Textbox maturityDate;
	protected Textbox currency;
	protected Button btnValidate;
	protected Row row_cpzIntAmt;
	protected CurrencyBox cpzIntAmt;

	protected transient BigDecimal oldVar_DownSizingAmt;

	private FinanceMain financeMain;
	private FinScheduleData finScheduleData;
	private transient FinanceDetail financeDetail;
	protected transient FinanceSelectCtrl financeSelectCtrl = null;

	protected LoanDownSizingService loanDownSizingService;

	// append tab details
	int formatter = 0;
	protected String eventCode = "";
	private String moduleDefiner = "";
	private String workflowCode = "";
	private String menuItemRightName = null;

	private BigDecimal finAssetValue = BigDecimal.ZERO;
	private BigDecimal finCurrAssetValue = BigDecimal.ZERO;
	private BigDecimal finAvailableAmt = BigDecimal.ZERO;

	private transient boolean validationOn;
	private boolean isSchdBuildReq = false;
	private boolean isFullDisb = false;
	private boolean recSave = false;
	boolean isDownsizeError = false;

	// Accounting detail Tab
	protected String selectMethodName = "onSelectTab";
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;

	// Sanctioned Amount Movements
	private List<FinAssetAmtMovement> assetAmtMvntList = null;
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	FinScheduleData newFinSchdData = null;

	/**
	 * default constructor.<br>
	 */
	public LoanDownSizingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceMainDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_LoanDownSizing(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(this.window_LoanDownSizing);

		try {

			if (arguments.containsKey("financeDetail")) {

				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
				setFinScheduleData(getFinanceDetail().getFinScheduleData());

				this.financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

				FinanceMain befImage = new FinanceMain();
				BeanUtils.copyProperties(this.financeMain, befImage);
				this.financeMain.setBefImage(befImage);

				this.formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
				setFinanceMain(this.financeMain);
			}

			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
			}
			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}
			if (arguments.containsKey("workflowCode")) {
				workflowCode = (String) arguments.get("workflowCode");
			}
			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			doLoadWorkFlow(this.financeMain.isWorkflow(), this.financeMain.getWorkflowId(),
					this.financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			doSetFieldProperties();
			doShowDialog(getFinanceMain());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LoanDownSizing.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		if (financeMain != null) {
			finAssetValue = PennantApplicationUtil.formateAmount(financeMain.getFinAssetValue(), formatter);
			finCurrAssetValue = PennantApplicationUtil.formateAmount(financeMain.getFinCurrAssetValue(), formatter);
			if (financeMain.isStepFinance()
					&& StringUtils.equals(financeMain.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)) {
				this.row_cpzIntAmt.setVisible(true);
				this.cpzIntAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				this.cpzIntAmt.setScale(formatter);
				BigDecimal tdCPZAmount = BigDecimal.ZERO;
				if (getFinanceDetail() != null) {
					List<FinanceScheduleDetail> fsdList = getFinanceDetail().getFinScheduleData()
							.getFinanceScheduleDetails();
					Date maturityDate = financeMain.getMaturityDate();
					for (FinanceScheduleDetail fsd : fsdList) {
						if (fsd.isCpzOnSchDate() && fsd.getSchDate().compareTo(maturityDate) <= 0) {
							tdCPZAmount = tdCPZAmount.add(fsd.getCpzAmount());
						} else if (fsd.isCpzOnSchDate()
								&& (StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)
										|| StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI))) {
							tdCPZAmount = tdCPZAmount.add(fsd.getCpzAmount());
						}
					}
				}
				tdCPZAmount = PennantApplicationUtil.formateAmount(tdCPZAmount, formatter);
				finAvailableAmt = finAssetValue.subtract(finCurrAssetValue);
				this.cpzIntAmt.setValue(tdCPZAmount);
			} else {
				finAvailableAmt = finAssetValue.subtract(finCurrAssetValue);
			}
		}

		this.totSanctionedAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totSanctionedAmt.setScale(formatter);

		this.disbursedAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.disbursedAmt.setScale(formatter);

		this.availableAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.availableAmt.setScale(formatter);

		this.revisedSanctionedAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.revisedSanctionedAmt.setScale(formatter);

		this.downSizingAmt.setProperties(true, formatter);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Button Rights
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnSave"));

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 */
	private void doShowDialog(FinanceMain aFinanceMain) {
		logger.debug(Literal.ENTERING);

		this.downSizingAmt.focus();

		// set Read only mode accordingly if the object is new or not.
		if (aFinanceMain.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aFinanceMain.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceMain);

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LoanDownSizing.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	private void doWriteBeanToComponents(FinanceMain aFinanceMain) {
		logger.debug(Literal.ENTERING);

		getFinanceDetail().setModuleDefiner(moduleDefiner);
		CustomerDetails custDetails = getFinanceDetail().getCustomerDetails();
		FinScheduleData finSchdData = getFinanceDetail().getFinScheduleData();
		List<FinServiceInstruction> finServInstList = finSchdData.getFinServiceInstructions();

		FinServiceInstruction finServInst = finServInstList.get(0);
		BigDecimal downSizingAmount = PennantApplicationUtil.formateAmount(finServInst.getAmount(), formatter);

		// Schedule Re Build In case of No Availability
		if (aFinanceMain.isAllowGrcPeriod() && finAvailableAmt.compareTo(downSizingAmount) == 0) {
			isFullDisb = true;
			isSchdBuildReq = true;
		}

		// Basic Details
		this.custCIF.setValue(custDetails.getCustomer().getCustCIF());
		this.custShrtName.setValue(custDetails.getCustomer().getCustShrtName());
		this.finReference.setValue(aFinanceMain.getFinReference());
		this.finBranch.setValue(aFinanceMain.getFinBranch());
		this.currency.setValue(aFinanceMain.getFinCcy());
		this.finType.setValue(aFinanceMain.getFinType());
		this.recordStatus.setValue(aFinanceMain.getRecordStatus());
		this.startDate.setValue(DateUtil.formatToLongDate(aFinanceMain.getFinStartDate()));
		this.maturityDate.setValue(DateUtil.formatToLongDate(aFinanceMain.getMaturityDate()));

		// DownSizing Details
		this.totSanctionedAmt.setValue(finAssetValue);
		this.disbursedAmt.setValue(finCurrAssetValue);
		this.downSizingAmt.setValue(downSizingAmount);
		this.availableAmt.setValue(finAvailableAmt.subtract(downSizingAmount));
		this.revisedSanctionedAmt.setValue(finAssetValue.subtract(downSizingAmount));

		// Filling Child Window Details Tabs
		doFillTabs(getFinanceDetail(), true, true);
		appendExtendedFieldDetails(this.moduleDefiner);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param aFinanceDetail
	 * @param onLoad
	 * @param isReqToLoad
	 */
	protected void doFillTabs(FinanceDetail aFinanceDetail, boolean onLoad, boolean isReqToLoad) {
		logger.debug(Literal.ENTERING);

		// Schedule Details Tab Adding
		if (!isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
			appendScheduleDetailTab(aFinanceDetail, false);
		}

		// Show Accounting Tab Details Based upon Role Condition using Work flow
		if (isFullDisb && isReqToLoad && "Accounting".equals(getTaskTabs(getTaskId(getRole())))) {

			// Accounting Details Tab Addition
			appendAccountingDetailTab(aFinanceDetail, onLoad);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * ReadOnly Components
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.downSizingAmt.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.financeMain.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_DownSizingAmount"), this.downSizingAmt);
		this.downSizingAmt.setReadonly(isReadOnly("FinanceMainDialog_DownSizingAmount"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeMain.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		btnDelete.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
		if (extendedFieldCtrl != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}
	}

	/**
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());

		doSave();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(getFinanceMain());
	}

	@Override
	public Notes getNotes(AbstractWorkflowEntity entity) {
		Notes notes = new Notes();
		notes.setModuleName(FinServiceEvent.LOANDOWNSIZING);
		notes.setReference(getFinanceMain().getFinReference());
		notes.setVersion(entity.getVersion());
		return notes;
	}

	/**
	 * 
	 * @param event
	 */
	public void onClick$btnAssetMvnt(Event event) {
		logger.debug("Entering" + event.toString());

		if (assetAmtMvntList == null) {
			assetAmtMvntList = loanDownSizingService.getFinAssetAmtMovements(getFinanceMain().getFinID(),
					FinanceConstants.MOVEMENTTYPE_DOWNSIZING);
		}

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("assetAmtMvntList", assetAmtMvntList);
		map.put("formatter", formatter);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/LoanDownSizingMovement.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @param event
	 */
	public void onFulfill$downSizingAmt(Event event) {
		logger.debug("Entering" + event.toString());

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		this.downSizingAmt.setConstraint("");
		this.downSizingAmt.setErrorMessage("");

		BigDecimal curAvlAmt = BigDecimal.ZERO;
		BigDecimal prvAvlAmt = this.availableAmt.getValue();
		BigDecimal curDownSizingAmt = this.downSizingAmt.getActualValue();

		if (curDownSizingAmt != null) {

			curAvlAmt = finAvailableAmt.subtract(curDownSizingAmt);
			BigDecimal revisedSanAmt = finAssetValue.subtract(curDownSizingAmt);

			if (curAvlAmt.compareTo(BigDecimal.ZERO) >= 0) {
				this.availableAmt.setValue(curAvlAmt);
				this.revisedSanctionedAmt.setValue(revisedSanAmt);
			} else {
				this.availableAmt.setValue(finAvailableAmt);
				this.revisedSanctionedAmt.setValue(BigDecimal.ZERO);

				this.tab_BasicDetailsTab.setSelected(true);
				throw new WrongValueException(this.downSizingAmt.getCcyTextBox(),
						Labels.getLabel("NUMBER_MAXVALUE_EQ",
								new String[] { Labels.getLabel("label_LoanDownSizing_DownSizingAmount.value"),
										Labels.getLabel("label_LoanDownSizing_AvailableAmount.value") }));

			}
		}

		if (financeMain.isAllowGrcPeriod()
				|| (!financeMain.isStepFinance() && financeMain.isEndGrcPeriodAftrFullDisb())) {
			if ((prvAvlAmt.compareTo(BigDecimal.ZERO) == 0 && curAvlAmt.compareTo(BigDecimal.ZERO) > 0)
					|| (prvAvlAmt.compareTo(BigDecimal.ZERO) > 0 && curAvlAmt.compareTo(BigDecimal.ZERO) == 0)) {

				isSchdBuildReq = true;

				if (curAvlAmt.compareTo(BigDecimal.ZERO) == 0) {

					Tab tab = getTab(AssetConstants.UNIQUE_ID_SCHEDULE);
					if (tab != null) {
						Events.sendEvent(Events.ON_SELECT, tab, null);
						tab.setSelected(true);
					}
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	protected void doStoreDftSchdValues() {

		doClearMessage();
		this.oldVar_DownSizingAmt = this.downSizingAmt.getActualValue();
	}

	/**
	 * Checks, if data are changed since the last call of doStoreInitData() . <br>
	 * 
	 * Reflection Method in ScheduleDetailDialogCtrl
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	public boolean isSchdlRegenerate() {

		BigDecimal oldFinAmount = PennantApplicationUtil.unFormateAmount(this.oldVar_DownSizingAmt, formatter);
		BigDecimal newFinAmount = PennantApplicationUtil.unFormateAmount(this.downSizingAmt.getActualValue(),
				formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			return true;
		}

		return false;
	}

	/**
	 * Method for Reset Schedule Details after Schedule Calculation
	 */
	public void resetScheduleTerms(FinScheduleData scheduleData) {

		// TODO : Reflection Method in ScheduleDetailDialogCtrl
	}

	/**
	 * Saves the components to table.
	 */
	protected void doSave() {
		logger.debug(Literal.ENTERING);

		recSave = false;
		boolean isNew = false;

		if (this.userAction.getSelectedItem() != null) {
			if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")
					|| this.userAction.getSelectedItem().getLabel().contains("Hold")) {

				recSave = true;
			}
		}

		// Accounting Validation
		if (!recSave && isFullDisb && "Accounting".equals(getTaskTabs(getTaskId(getRole())))) {

			// check if accounting rules executed or not
			if (accountingDetailDialogCtrl == null || (!accountingDetailDialogCtrl.isAccountingsExecuted())) {
				if (ImplementationConstants.ACCOUNTING_VALIDATION) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
					return;
				}
			} else {
				if (accountingDetailDialogCtrl.getDisbCrSum()
						.compareTo(accountingDetailDialogCtrl.getDisbDrSum()) != 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			}
		}

		FinanceDetail aFinanceDetail = ObjectUtil.clone(getFinanceDetail());
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// Finance Accounting Details Tab
		aFinanceDetail.setModuleDefiner(moduleDefiner);
		aFinanceDetail.setAccountingEventCode(AccountingEvent.SCDCHG);

		// Validations and fill the FinServiceInstruction object with the components data
		doSetValidation();
		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());

		if (aFinanceDetail.getExtendedFieldHeader() != null && extendedFieldCtrl != null) {
			aFinanceDetail.setExtendedFieldRender(extendedFieldCtrl.save(true));
		}

		if (isDownsizeError) {
			return;
		}

		String tranType = "";
		isNew = aFinanceMain.isNewRecord();
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceMain.getRecordType())) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				if (isNew) {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceMain.setNewRecord(true);
				}
			}
		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aFinanceDetail, tranType)) {
				refreshList();
				closeDialog();

				// Notification for Role Identification
				if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Loan ",
						aFinanceMain.getRecordStatus(), false);

				Clients.showNotification(msg, "info", null, null, -1);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		if (extendedFieldCtrl != null && aFinanceDetail.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getFinanceSelectCtrl().doSearch(true);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAcademic (Academic)
	 * 
	 * @param tranType  (String)
	 * 
	 * @return boolean
	 */
	protected boolean doProcess(FinanceDetail aFinanceDetail, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		FinScheduleData finScheduleData = aFinanceDetail.getFinScheduleData();
		FinanceMain aFinanceMain = finScheduleData.getFinanceMain();

		aFinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aFinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceMain.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinanceMain);
				}

				if (isNotesMandatory(taskId, aFinanceMain)) {
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

			aFinanceMain.setTaskId(taskId);
			aFinanceMain.setNextTaskId(nextTaskId);
			aFinanceMain.setRoleCode(getRole());
			aFinanceMain.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinanceDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aFinanceMain);
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);
					auditHeader.getAuditDetail().setModelData(aFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinanceDetail financeDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, financeDetail.getBefImage(), financeDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, financeDetail.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean deleteNotes = false;
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		if (fd.getExtendedFieldRender() != null) {
			int seqNo = 0;
			ExtendedFieldRender details = fd.getExtendedFieldRender();
			details.setReference(fm.getFinReference());
			details.setSeqNo(++seqNo);
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(fm.getRecordStatus());
			details.setVersion(fm.getVersion());
			details.setWorkflowId(fm.getWorkflowId());
			details.setTaskId(fm.getTaskId());
			details.setNextTaskId(fm.getNextTaskId());
			details.setRoleCode(fm.getRoleCode());
			details.setNextRoleCode(fm.getNextRoleCode());
			details.setNewRecord(fm.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(fm.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(fm.getRecordType());
					details.setNewRecord(true);
				}
			} else if (details.isNewRecord()) {
				details.setRecordType(PennantConstants.RCD_ADD);
			}
		}

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					// auditHeader = loanDownSizingService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = loanDownSizingService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = loanDownSizingService.doApprove(auditHeader);

					if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = loanDownSizingService.doReject(auditHeader);

					if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_LoanDownSizing, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_LoanDownSizing, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.financeMain), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(true);
		if (!this.downSizingAmt.isReadonly()) {
			this.downSizingAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_LoanDownSizing_DownSizingAmount.value"), formatter, true, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		setValidationOn(false);
		this.downSizingAmt.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.downSizingAmt.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinScheduleData
	 */
	private void doWriteComponentsToBean(FinScheduleData aFinScheduleData) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		FinanceMain aFinanceMain = aFinScheduleData.getFinanceMain();
		List<FinServiceInstruction> finServInstList = aFinScheduleData.getFinServiceInstructions();
		FinServiceInstruction finServiceInstruction = finServInstList.get(0);

		finServiceInstruction.setFromDate(SysParamUtil.getAppDate());
		finServiceInstruction.setFinID(aFinanceMain.getFinID());
		finServiceInstruction.setFinReference(aFinanceMain.getFinReference());
		finServiceInstruction.setSchdMethod(aFinanceMain.getScheduleMethod());
		finServiceInstruction.setFinEvent(FinServiceEvent.LOANDOWNSIZING);

		// DownSizing Amount
		try {
			if (!this.downSizingAmt.isReadonly() && this.downSizingAmt.getActualValue() != null) {

				if (this.downSizingAmt.getActualValue().compareTo(finAvailableAmt) > 0) {
					throw new WrongValueException(this.downSizingAmt.getCcyTextBox(),
							Labels.getLabel("NUMBER_MAXVALUE_EQ",
									new String[] { Labels.getLabel("label_LoanDownSizing_DownSizingAmount.value"),
											Labels.getLabel("label_LoanDownSizing_AvailableAmount.value") }));

				}
			}

			finServiceInstruction.setAmount(PennantApplicationUtil
					.unFormateAmount(this.downSizingAmt.isReadonly() ? this.downSizingAmt.getActualValue()
							: this.downSizingAmt.getValidateValue(), formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			this.tab_BasicDetailsTab.setSelected(true);

			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		isDownsizeError = false;
		if (!recSave && finAvailableAmt.compareTo(this.downSizingAmt.getActualValue()) == 0
				&& aFinanceMain.isAllowGrcPeriod()
				|| (!financeMain.isStepFinance() && aFinanceMain.isEndGrcPeriodAftrFullDisb())) {

			// For Under Construction, End Grace / PRE EMI Period After Full Disbursement
			aFinScheduleData = getLoanDownSizingService().changeGraceEndAfterFullDisb(aFinScheduleData);

			// Show Error Details in Schedule Maintenance
			aFinScheduleData.getFinanceMain().resetRecalculationFields();
			if (aFinScheduleData.getErrorDetails() == null || aFinScheduleData.getErrorDetails().isEmpty()) {

				aFinanceMain.setScheduleChange(true);
				aFinanceMain.setScheduleRegenerated(true);
				aFinScheduleData.setSchduleGenerated(true);

				aFinScheduleData.setFinServiceInstructions(finServInstList);
				setFinScheduleData(aFinScheduleData);

				newFinSchdData = ObjectUtil.clone(getFinScheduleData());

			} else {

				Tab tab = getTab(AssetConstants.UNIQUE_ID_SCHEDULE);
				if (tab != null) {
					tab.setSelected(true);
				}

				isDownsizeError = true;
				MessageUtil.showError(aFinScheduleData.getErrorDetails().get(0));
				aFinScheduleData.getErrorDetails().clear();
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "Verify" button is clicked. <br>
	 * 
	 * Schedule recalculated based on New Grace End Date.
	 * 
	 * @param event
	 */
	public void onClick$btnValidate(Event event) {
		logger.debug("Entering" + event.toString());

		getScheduleDetails(getFinanceDetail().getFinScheduleData());

		Tab tab = getTab(AssetConstants.UNIQUE_ID_SCHEDULE);
		if (tab != null) {
			tab.setSelected(true);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Recalculate Schedule Based On New Schedule Date
	 */
	private void getScheduleDetails(FinScheduleData finScheduleData) {
		logger.debug(Literal.ENTERING);

		isSchdBuildReq = false;
		isDownsizeError = false;

		// Actual Schedules
		FinScheduleData scheduleData = ObjectUtil.clone(finScheduleData);

		if (finAvailableAmt.compareTo(this.downSizingAmt.getActualValue()) == 0) {

			// new change Grace End Schedules
			if (newFinSchdData == null) {
				doWriteComponentsToBean(scheduleData);
			} else {
				scheduleData = ObjectUtil.clone(newFinSchdData);
			}
		}

		// Change Grace End / Actual Schedule Rendering
		if (scheduleDetailDialogCtrl != null && !isDownsizeError) {
			scheduleDetailDialogCtrl.doFillScheduleList(scheduleData);
		}

		scheduleData = null;

		logger.debug(Literal.LEAVING);
	}

	// TODO : APPEND TABS : SHOUBLD BE AT COMMON TAB CONTROLLER NOT IN EVERY CTRL

	// 1. -----> Schedule Details Tab

	/**
	 * Method for Rendering Schedule Details Data in finance
	 * 
	 * @param financeDetail
	 * @param onLoadProcess
	 * @param isFeeRender
	 */
	public void appendScheduleDetailTab(FinanceDetail financeDetail, Boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);

		// 1. ---> TAB Creation
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_SCHEDULE) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_SCHEDULE, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_SCHEDULE);
		}

		// 2. ---> Schedule Rendering
		if (!onLoadProcess) {

			FinanceDetail aFinanceDetail = ObjectUtil.clone(getFinanceDetail());

			// TODO : Render the Schedule Details While selecting TAB, NOT ON OPENING THE FINANCE
			if (isSchdBuildReq) {
				doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());
				setFinScheduleData(aFinanceDetail.getFinScheduleData());
				isSchdBuildReq = false;
			}

			final Map<String, Object> map = getDefaultArguments();
			map.put("financeDetail", aFinanceDetail);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_SCHEDULE), map);

			Tab tab = getTab(AssetConstants.UNIQUE_ID_SCHEDULE);
			if (tab != null) {
				tab.setVisible(true);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	// 2. -----> Accounting Details Tab

	/**
	 * Method for Rendering Schedule Details Data in finance
	 * 
	 * @param financeDetail
	 * @param onLoadProcess
	 */
	protected void appendAccountingDetailTab(FinanceDetail financeDetail, boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}

		if (!onLoadProcess) {

			eventCode = AccountingEvent.SCDCHG;
			financeDetail.setAccountingEventCode(eventCode);

			Long acSetID = AccountingEngine.getAccountSetID(finMain, eventCode);

			final Map<String, Object> map = getDefaultArguments();

			map.put("acSetID", acSetID);
			map.put("financeDetail", financeDetail);
			map.put("ccyFormatter", CurrencyUtil.getFormat(getFinanceMain().getFinCcy()));

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);

			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param onLoadProcess
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		accountingDetailDialogCtrl.getLabel_AccountingDisbCrVal().setValue("");
		accountingDetailDialogCtrl.getLabel_AccountingDisbDrVal().setValue("");

		if (isOverdraft) {
			if (getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails().size() <= 0
					&& getFinanceDetail().getFinScheduleData().getFinanceType().isDroplineOD()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
				return;
			}
		} else {
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
				return;
			}
		}

		// Finance Accounting Details Execution
		executeAccounting(onLoadProcess);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Executing Accounting tab Rules
	 */
	private void executeAccounting(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);

		// TODO : VALIDATE Execute Accounting
		if (newFinSchdData == null) {
			prepareAccountingData(onLoadProcess);
		}
		AEEvent aeEvent = getLoanDownSizingService().getChangeGrcEndPostings(getFinScheduleData());

		if (aeEvent != null) {

			// PftChg is the POST AMOUNT in Posting entries
			List<FinServiceInstruction> finServInstList = getFinScheduleData().getFinServiceInstructions();
			if (!finServInstList.isEmpty()) {
				finServInstList.get(0).setPftChg(getFinScheduleData().getPftChg());
			}
			getFinanceDetail().setFinScheduleData(getFinScheduleData());
			getFinanceDetail().setReturnDataSetList(aeEvent.getReturnDataSet());

			if (accountingDetailDialogCtrl != null) {
				accountingDetailDialogCtrl.doFillAccounting(aeEvent.getReturnDataSet());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Prepare Accounting Data
	 */
	private void prepareAccountingData(boolean onLoadProcess) {

		if (onLoadProcess) {
			doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
		}
	}

	/**
	 * Default Arguments
	 * 
	 * @return
	 */
	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();

		map.put("isEnquiry", false);
		map.put("roleCode", getRole());
		map.put("isFinanceProcess", true);
		map.put("financeMainDialogCtrl", this);
		map.put("workflowCode", workflowCode);
		map.put("moduleDefiner", moduleDefiner);
		map.put("menuItemRightName", menuItemRightName);
		map.put("finHeaderList", getFinBasicDetails());

		return map;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails() {

		ArrayList<Object> arrayList = new ArrayList<Object>();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		arrayList.add(0, financeMain.getFinType());
		arrayList.add(1, financeMain.getFinCcy());

		if (StringUtils.equals(financeMain.getScheduleMethod(), PennantConstants.List_Select)) {
			arrayList.add(2, "");
		} else {
			arrayList.add(2, financeMain.getScheduleMethod());
		}
		arrayList.add(3, financeMain.getFinReference());
		arrayList.add(4, financeMain.getProfitDaysBasis());
		arrayList.add(5, financeMain.getGrcPeriodEndDate());
		arrayList.add(6, financeMain.isAllowGrcPeriod());

		if (StringUtils.isNotEmpty(financeType.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, financeType.getFinCategory());

		String custShrtName = "";
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			custShrtName = getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName();
		}

		arrayList.add(9, custShrtName);
		arrayList.add(10, getFinanceMain().isNewRecord());
		arrayList.add(11, moduleDefiner);
		// arrayList.add(12, getFinanceMain().getFlexiType());

		return arrayList;
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug(Literal.ENTERING);

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
		tabpanel.setHeight("100%");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setParent(tabpanelsBoxIndexCenter);

		// Forward Event setting
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param event
	 */
	public void onSelectTab(ForwardEvent event) {

		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + "Entering");

		String module = getIDbyTab(tab.getId());
		switch (module) {

		case AssetConstants.UNIQUE_ID_SCHEDULE:

			if (isSchdBuildReq) {
				getScheduleDetails(getFinanceDetail().getFinScheduleData());
			}
			break;

		case AssetConstants.UNIQUE_ID_ACCOUNTING:

			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			appendAccountingDetailTab(getFinanceDetail(), false);

			if (accountingDetailDialogCtrl != null) {
				accountingDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;

		default:
			break;
		}

		logger.debug(tab.getId() + " --> " + "Leaving");
	}

	// Tab related methods
	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
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

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	private void appendExtendedFieldDetails(String finEvent) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldRender extendedFieldRender = null;

		try {
			FinScheduleData schdData = financeDetail.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();

			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = this.extendedFieldCtrl
					.getExtendedFieldHeader(ExtendedFieldConstants.MODULE_LOAN, fm.getFinCategory(), finEvent);
			if (extendedFieldHeader == null) {
				return;
			}

			extendedFieldCtrl.setAppendActivityLog(true);
			extendedFieldCtrl.setFinBasicDetails(getFinBasicDetails());

			extendedFieldRender = extendedFieldCtrl.getExtendedFieldRender(fm.getFinReference());

			extendedFieldCtrl.createTab(tabsIndexCenter, tabpanelsBoxIndexCenter);
			financeDetail.setExtendedFieldHeader(extendedFieldHeader);
			financeDetail.setExtendedFieldRender(extendedFieldRender);

			if (financeDetail.getBefImage() != null) {
				financeDetail.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				financeDetail.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(fm.getFinCcy()));
			extendedFieldCtrl.setReadOnly(false);
			extendedFieldCtrl.setWindow(window_LoanDownSizing);
			extendedFieldCtrl.setTabHeight(this.borderLayoutHeight - 100);
			extendedFieldCtrl.setUserWorkspace(getUserWorkspace());
			extendedFieldCtrl.setUserRole(getRole());
			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error(Labels.getLabel("message.error.Invalid_Extended_Field_Config"), e);
			MessageUtil.showError(Labels.getLabel("message.error.Invalid_Extended_Field_Config"));
		}

		logger.debug(Literal.LEAVING);

	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public LoanDownSizingService getLoanDownSizingService() {
		return loanDownSizingService;
	}

	public void setLoanDownSizingService(LoanDownSizingService loanDownSizingService) {
		this.loanDownSizingService = loanDownSizingService;
	}

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}

	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}

	public void setScheduleDetailDialogCtrl(ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}
}
