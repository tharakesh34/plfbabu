package com.pennant.webui.systemmasters.pmay;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.MasterDefUtil;
import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennant.app.util.MasterDefUtil.PhoneType;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.PmayEligibilityLog;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.pmay.PmayDetailsService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.service.systemmasters.PMAYService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.model.PMAYDetailsRespData;
import com.pennanttech.pff.model.PMAYRequest;
import com.pennanttech.pff.model.PMAYResponse;
import com.pennanttech.pff.model.PmayDetails;

public class PMAYDialogCtrl extends GFCBaseCtrl<PMAY> {
	private static final Logger logger = LogManager.getLogger(PMAYDialogCtrl.class);
	private static final long serialVersionUID = 1L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PmayDialog;
	protected Textbox finReference;
	protected Radio balanceTransferYes;
	protected Radio balanceTransferNo;
	protected Radio primaryApplicantYes;
	protected Radio primaryApplicantNo;
	protected Radio centralAssistanceYes;
	protected Radio centralAssistanceNo;
	protected Radio ownedHouseYes;
	protected Radio ownedHouseNo;
	protected Decimalbox carpetArea;
	protected CurrencyBox householdAnnIncome;
	protected Combobox transactionFinType;
	protected Radio notifiedTownYes;
	protected Radio notifiedTownNo;
	protected ExtendedCombobox townCode;
	protected Combobox product;
	protected Radio prptyOwnedByWomenYes;
	protected Radio prptyOwnedByWomenNo;
	protected Radio waterSupplyYes;
	protected Radio waterSupplyNo;
	protected Radio drinageYes;
	protected Radio drinageNo;
	protected Radio electricityYes;
	protected Radio electricityNo;
	protected Rows basicDts;

	protected Textbox pmayCategory;
	protected Button btnPMAYElgbty;
	protected Button btnFinalElgbty;
	private PMAY pmay;
	private Label finBasic_finType;
	private Label finBasic_finReference;
	private Label finBasic_custCif;
	private Label finBasic_custShrtName;
	private int ccyFormatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	private FinanceMain financeMain;
	private FinanceMainBaseCtrl financeMainBaseCtrl;
	private boolean fromLoan = false;
	private transient PMAYListCtrl pmayListCtrl; // overhanded per param
	private transient PMAYService pmayService;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private FinanceDetail financeDetail = null;
	Tab parenttab = null;
	private RuleService ruleService;
	private RuleExecutionUtil ruleExecutionUtil;
	private Listbox listBoxfinalEligibility;

	private final List<ValueLabel> productTypeList = PennantStaticListUtil.getProductTypeList();
	private final List<ValueLabel> txFinTypeList = PennantStaticListUtil.getTxFinTypeList();

	private PMAYRequest pmyReq;
	private CustomerDetailsService customerDetailsService;
	private CustomerDialogCtrl customerDialogCtrl;

	/**
	 * default constructor.<br>
	 */
	public PMAYDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PmayDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PmayDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PmayDialog);

		try {
			// Get the required arguments.
			this.pmay = (PMAY) arguments.get("pmay");
			this.pmayListCtrl = (PMAYListCtrl) arguments.get("pmayListCtrl");

			if (arguments.containsKey("enqiryModule")) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}
			if (arguments.containsKey("tab")) {
				parenttab = (Tab) arguments.get("tab");
			}

			if (this.pmay == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Get the required arguments.
			if (arguments.containsKey("financeDetail")) {
				financeDetail = (FinanceDetail) arguments.get("financeDetail");
				this.financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			}

			if (arguments.containsKey("financeMainBaseCtrl")) {
				this.financeMainBaseCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");
				try {
					this.financeMainBaseCtrl.getClass().getMethod("setPmayDialogCtrl", this.getClass())
							.invoke(getFinanceMainBaseCtrl(), this);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
				this.window_PmayDialog.setTitle("");
				fromLoan = true;
			}
			// Store the before image.
			PMAY pmay = new PMAY();
			BeanUtils.copyProperties(this.pmay, pmay);
			this.pmay.setBefImage(pmay);

			// Render the page and display the data.
			if (!enqiryModule) {
				doLoadWorkFlow(this.pmay.isWorkflow(), this.pmay.getWorkflowId(), this.pmay.getNextTaskId());
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), this.pageRightName);
			} else {
				if (isWorkFlowEnabled()) {
					if (!enqiryModule) {
						this.userAction = setListRecordStatus(this.userAction);
					}
					getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
				} else {
					getUserWorkspace().allocateAuthorities(this.pageRightName, null);
				}
			}
			if (fromLoan) {
				this.userAction.setVisible(false);
			}
			doSetFieldProperties();
			doCheckRights();
			if (getPmay().isNewRecord()) {
				pmay.setFinID(financeMain.getFinID());
				pmay.setFinReference(financeMain.getFinReference());
				doShowDialog(pmay);
			} else {
				doShowDialog(pmay);
			}
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$notifiedTownYes(Event event) {
		logger.debug(Literal.ENTERING);

		this.townCode.setReadonly(false);

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$notifiedTownNo(Event event) {
		logger.debug(Literal.ENTERING);

		this.townCode.setReadonly(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.householdAnnIncome.setProperties(false, ccyFormatter);
		this.pmayCategory.setDisabled(true);
		setStatusDetails();

		this.townCode.setProperties("TownCode", "TownCode", "TownName", false, 10);
		this.townCode.setInputAllowed(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PmayDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PmayDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PmayDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PmayDialog_btnSave"));
		this.btnPMAYElgbty.setDisabled(!getUserWorkspace().isAllowed("button_PmayDialog_btnPMAYElgbty"));
		this.btnFinalElgbty.setDisabled(!getUserWorkspace().isAllowed("button_PmayDialog_btnFinalElgbty"));
		this.btnCancel.setVisible(false);
		if (fromLoan) {
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		pmayListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnPMAYElgbty(Event event) {
		logger.debug(Literal.ENTERING);
		if (fromLoan) {
			CustomerDialogCtrl cdc = this.financeMainBaseCtrl.getCustomerDialogCtrl();
			try {
				ExtendedFieldRender efr = cdc.getExtendedDetails();
				if (efr == null) {
					return;
				}

				Map<String, Object> mapValues = efr.getMapValues();
				String object = (String) mapValues.get("APPLICANTID");
				if (StringUtils.isNotEmpty(object)) {
					MessageUtil.showError("PMAY Benefits Already Used.");
					return;
				}
			} catch (WrongValueException e) {
				ExtendedFieldCtrl efc = cdc.getExtendedFieldCtrl();
				Window window = efc.getWindow();
				if (window != null && efc != null && efc.getExtendedFieldHeader() != null) {
					ExtendedFieldHeader efh = efc.getExtendedFieldHeader();
					Component fellowIfAny = window.getFellowIfAny(efh.getModuleName() + efh.getSubModuleName());
					if (fellowIfAny != null && fellowIfAny instanceof Tab) {
						Tab parTab = (Tab) fellowIfAny;
						parTab.setSelected(true);
					}
				}
				logger.error(Literal.EXCEPTION, e);
			}
		}

		PMAY aPMAY = new PMAY();

		ArrayList<WrongValueException> wve = doWriteComponentsToBean(aPMAY);

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		String finCcy = this.financeMain.getFinCcy();
		aPMAY.setProduct(this.product.getValue());
		aPMAY.setTransactionFinType(this.product.getValue());

		Map<String, Object> fieldsandvalues = new HashMap<>();

		fieldsandvalues.put("NOTIFIEDTOWN", aPMAY.isNotifiedTown() ? "YES" : "NO");
		fieldsandvalues.put("HOUSINGSCHEME", aPMAY.isCentralAssistance() ? "YES" : "NO");
		fieldsandvalues.put("PUCCAHOUSE", aPMAY.isOwnedHouse() ? "YES" : "NO");
		fieldsandvalues.put("CARPETAREA", aPMAY.getCarpetArea());
		BigDecimal annualIncome = PennantApplicationUtil.formateAmount(aPMAY.getHouseholdAnnIncome(),
				CurrencyUtil.getFormat(finCcy));
		fieldsandvalues.put("HOUSEHOLDINCOME", annualIncome);
		fieldsandvalues.put("BALTRANSFER", aPMAY.isBalanceTransfer() ? "YES" : "NO");
		fieldsandvalues.put("ISAPPISINDIVIDUAL", aPMAY.isPrimaryApplicant() ? "YES" : "NO");
		fieldsandvalues.put("TRANTYPEOFLOAN", aPMAY.getTransactionFinType());
		fieldsandvalues.put("PRODUCT", aPMAY.getProduct());
		fieldsandvalues.put("OWNEDPROPERTY", aPMAY.isPrprtyOwnedByWomen() ? "YES" : "NO");
		fieldsandvalues.put("WATERSUPPLY", aPMAY.isWaterSupply() ? "YES" : "NO");
		fieldsandvalues.put("DRAINAGESANITATIO", aPMAY.isDrinage() ? "YES" : "NO");
		fieldsandvalues.put("ELECTRICITY", aPMAY.isElectricity() ? "YES" : "NO");
		fieldsandvalues.put("PMAYCATEGORY", aPMAY.getPmayCategory());

		Map<String, Object> fieldsandvaluesResult = new HashMap<>();

		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();

		for (String key : fieldsandvalues.keySet()) {
			Object val = fieldsandvalues.get(key);
			fieldsandvaluesResult.put("LOAN" + "_" + fm.getFinCategory() + "_" + key, val);
		}

		String ruleModule = RuleConstants.MODULE_ELGRULE;
		Rule pmayEligibilityRule = ruleService.getApprovedRuleById("SMPL", ruleModule, ruleModule);

		if (pmayEligibilityRule == null) {
			MessageUtil.showMessage("PMAY Rule is Not available with RuleCode >> SMPL");
			return;
		}

		Integer pmayResult = (Integer) RuleExecutionUtil.executeRule(pmayEligibilityRule.getSQLRule(),
				fieldsandvaluesResult, finCcy, RuleReturnType.INTEGER);

		String pmayCategory = "";
		String applicableLoanAmount = "";
		boolean pMay = false;

		switch (pmayResult) {
		case 6:
			pmayCategory = "EWS";
			applicableLoanAmount = pmayResult + "00000";
			pMay = true;
			break;
		case 7:
			pmayCategory = "LIG";
			applicableLoanAmount = 6 + "00000";
			pMay = true;
			break;
		case 9:
			pmayCategory = "MIG1";
			applicableLoanAmount = pmayResult + "00000";
			pMay = true;
			break;
		case 12:
			pmayCategory = "MIG2";
			applicableLoanAmount = pmayResult + "00000";
			pMay = true;
			break;
		default:
			MessageUtil.showMessage("PMAY Not Applicable");
			break;
		}

		if (applicableLoanAmount.isEmpty()) {
			applicableLoanAmount = "0";
		}

		Map<String, Object> subSidyResultMap = new HashMap<>();
		fm.setPmay(pMay);

		if (pmayResult != 0) {
			subSidyResultMap.put("SUBSIDYAPPLICABLE", applicableLoanAmount);
			fm.setPmay(pMay);
			this.btnFinalElgbty.setDisabled(!getUserWorkspace().isAllowed("button_PmayDialog_btnFinalElgbty"));
		} else {
			pmayCategory = "NA";
			subSidyResultMap.put("PMAYCATEGORY", pmayCategory);
			subSidyResultMap.put("SUBSIDYAPPLICABLE", 0.00);
			btnFinalElgbty.setDisabled(true);
		}

		this.pmayCategory.setValue(pmayCategory);
		fieldsandvalues.put("PMAYCATEGORY", subSidyResultMap.get("PMAYCATEGORY"));
		fieldsandvalues.put("SUBSIDYAPPLICABLE", subSidyResultMap.get("PMAYCATEGORY"));

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnFinalElgbty(Event event) {
		logger.debug(Literal.ENTERING);

		List<PmayEligibilityLog> eligibilityLog = new ArrayList<>();
		this.pmyReq = new PMAYRequest();
		pmyReq = new PMAYRequest();
		boolean inValidData = ValidateData(this.financeDetail);
		if (inValidData) {
			return;
		}
		long recordId = this.pmayService.generateDocSeq();
		List<PmayDetails> pmayDetailList = pmyReq.getPmayDetails();
		for (PmayDetails pmayDetails : pmayDetailList) {
			pmayDetails.setRECORD_ID(String.valueOf(recordId));
		}

		// String url = "http://localhost:8181/pennapps-simulator/PMAYServlet";
		PmayDetailsService pmayService = new PmayDetailsService();
		PMAYResponse processRes = pmayService.ProcessRequest(pmyReq);
		String reqJson = "";
		String respJson = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			reqJson = mapper.writeValueAsString(pmyReq);
			respJson = mapper.writeValueAsString(processRes);
		} catch (Exception e) {
			logger.error("Exception in json request string" + e);
		}

		if (customerDialogCtrl != null && customerDialogCtrl.getExtendedFieldCtrl() != null) {
			Window window = customerDialogCtrl.getExtendedFieldCtrl().getWindow();
			Groupbox pmaydtsGrpBox = null;
			Textbox pmayLan = null;
			try {
				if (window != null && window.getFellow("PMAYDETAIL") instanceof Groupbox) {
					pmaydtsGrpBox = (Groupbox) window.getFellow("PMAYDETAIL");
				}
				if (pmaydtsGrpBox != null && pmaydtsGrpBox.getFellow("ad_PMAYLAN") instanceof Textbox) {
					pmayLan = (Textbox) window.getFellow("ad_PMAYLAN");
					if (pmayLan != null) {
						pmayLan.setValue(financeMain.getFinReference());
					}
				}
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
		}

		if (processRes != null && processRes.getPmayDetailsRespData() != null) {
			PMAYDetailsRespData pmayDetailsRespData = processRes.getPmayDetailsRespData();
			PmayEligibilityLog pmayEligibilityLog = new PmayEligibilityLog();
			pmayEligibilityLog.setNewRecord(true);
			pmayEligibilityLog.setRecordId(recordId);
			pmayEligibilityLog.setPmayStatus(pmayDetailsRespData.getSTATUS());
			pmayEligibilityLog.setErrorCode("");
			pmayEligibilityLog.setErrorDesc("");
			pmayEligibilityLog.setApplicantId(pmayDetailsRespData.getAPPLICATION_ID());
			pmayEligibilityLog.setReqJson(reqJson);
			pmayEligibilityLog.setRespJson(respJson);
			eligibilityLog.add(pmayEligibilityLog);
			renderItem(eligibilityLog);
			btnFinalElgbty.setDisabled(true);
		}
		logger.debug(Literal.LEAVING);
	}

	public boolean ValidateData(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		boolean inValidData = validateRequestData(customerDetails, finReference, "Applicant", false);

		boolean coApplContainSpouseDts = false;

		if (!inValidData) {
			List<JointAccountDetail> jointAccountDetailList = financeDetail.getJointAccountDetailList();
			if (fromLoan && financeMainBaseCtrl != null) {
				jointAccountDetailList = financeMainBaseCtrl.getJointAccountDetailList();
			}
			for (JointAccountDetail jointAccountDetail : jointAccountDetailList) {
				if (!PennantConstants.RECORD_TYPE_CAN.equals(jointAccountDetail.getRecordType())) {
					if ("Wife".equals(jointAccountDetail.getCatOfcoApplicant())
							|| "Husband".equals(jointAccountDetail.getCatOfcoApplicant())) {
						coApplContainSpouseDts = true;
					}
					CustomerDetails custCoapplicantData = customerDetailsService
							.getCustomerDetails(jointAccountDetail.getCustID(), "_AView", true);
					inValidData = validateRequestData(custCoapplicantData, finReference, "Co-Applicant",
							!coApplContainSpouseDts);
					if (inValidData) {
						return inValidData;
					}
				}
			}
		}

		if (!inValidData) {
			if (!coApplContainSpouseDts) {
				String custMaritalSts = customer.getCustMaritalSts();
				if ("M".equals(custMaritalSts)) {
					PmayDetails spousePmayDetails = new PmayDetails();
					spousePmayDetails.setAPPLICATION_NO(finReference);
					inValidData = validateSpouseDetails(customerDetails, spousePmayDetails, "Applicant Spouse");
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return inValidData;
	}

	private boolean validateRequestData(CustomerDetails customerDetails, String finReference, String custType,
			boolean coApplSpouseReq) {
		logger.debug(Literal.ENTERING);

		Customer customer = customerDetails.getCustomer();
		List<CustomerDocument> customerDocumentsList = customerDetails.getCustomerDocumentsList();
		List<CustomerPhoneNumber> customerPhoneNumberList = customerDetails.getCustomerPhoneNumList();
		List<CustomerEMail> customerEMailList = customerDetails.getCustomerEMailList();

		if ("Applicant".equals(custType) && fromLoan && financeMainBaseCtrl != null) {
			if (financeMainBaseCtrl != null && financeMainBaseCtrl.getCustomerDialogCtrl() != null) {
				customerDialogCtrl = financeMainBaseCtrl.getCustomerDialogCtrl();
				customerDocumentsList = customerDialogCtrl.getCustomerDocumentDetailList();
				customerPhoneNumberList = customerDialogCtrl.getCustomerPhoneNumberDetailList();
				customerEMailList = customerDialogCtrl.getCustomerEmailDetailList();
			}
		}

		PmayDetails pmayDetails = new PmayDetails();
		boolean inValidData = false;
		StringBuilder reason = new StringBuilder();
		boolean aadharFound = false;
		boolean mobileFound = false;
		if (CollectionUtils.isNotEmpty(customerEMailList)) {
			for (CustomerEMail customerEMail : customerEMailList) {
				if (!PennantConstants.RECORD_TYPE_CAN.equals(customerEMail.getRecordType())) {
					if (Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH) == customerEMail
							.getCustEMailPriority()) {
						pmayDetails.setEMAIL_ID(customerEMail.getCustEMail());
						break;
					}
				}
			}
		}

		if (CollectionUtils.isNotEmpty(customerDocumentsList)) {
			for (CustomerDocument customerDocument : customerDocumentsList) {
				if (!PennantConstants.RECORD_TYPE_CAN.equals(customerDocument.getRecordType())) {
					if (StringUtils.equals(MasterDefUtil.getDocCode(DocType.AADHAAR),
							customerDocument.getCustDocCategory())) {
						aadharFound = true;
						pmayDetails.setAADHAAR_NO(customerDocument.getCustDocTitle());
						break;
					}
					if (StringUtils.equals(MasterDefUtil.getDocCode(DocType.ENO), // enrollment number if aadhaar not
																					// available
							customerDocument.getCustDocCategory())) {
						aadharFound = true;
						pmayDetails.setAADHAAR_NO(customerDocument.getCustDocTitle());
						break;
					}
				}
			}

			if (!aadharFound) {
				inValidData = true;
				reason.append("Aadhaar Number or Enrollment Number is Mandatory for " + custType + ". ");
			}
		} else {
			inValidData = true;
			reason.append("Aadhaar Number or Enrollment Number is Mandatory for " + custType + ". ");
		}

		if (CollectionUtils.isNotEmpty(customerPhoneNumberList)) {
			for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumberList) {
				if (!PennantConstants.RECORD_TYPE_CAN.equals(customerPhoneNumber.getRecordType())) {
					if (StringUtils.equals(MasterDefUtil.getPhoneTypeCode(PhoneType.MOBILE),
							customerPhoneNumber.getPhoneNumber())) {
						mobileFound = true;
						pmayDetails.setMOBILE_NO(customerPhoneNumber.getPhoneNumber());
						break;
					}
				}
			}
			if (!mobileFound) {
				inValidData = true;
				reason.append(" Phone Type Code with MOBILE is Mandatory for " + custType + ", ");
			}
		}

		if (reason.length() > 1) {
			reason.append(" for Customer Cif " + customer.getCustCIF() + ". ");
		}

		if (StringUtils.isEmpty(finReference)) {
			inValidData = true;
			reason.append(" Loan Reference is Not Generated .");
		} else {
			pmayDetails.setAPPLICATION_NO(finReference);
		}
		if (StringUtils.isEmpty(customer.getCustGenderCode())) {
			inValidData = true;
			reason.append("select Customer Gender is Mandatory for " + custType + ", ");
		} else {
			pmayDetails.setGENDER(customer.getCustGenderCode());
		}

		if (inValidData) {
			MessageUtil.showError(reason.toString());
		} else {
			this.pmyReq.getPmayDetails().add(pmayDetails);
		}

		pmayDetails.setBORROWER_TYPE(custType);
		pmayDetails.setNAME(customer.getCustShrtName());

		if (coApplSpouseReq && !"Applicant".equals(custType)) {
			if (!inValidData) {
				String custMaritalSts = customer.getCustMaritalSts();
				if ("M".equals(custMaritalSts)) {
					PmayDetails spousePmayDetails = new PmayDetails();
					spousePmayDetails.setAPPLICATION_NO(finReference);
					inValidData = validateSpouseDetails(customerDetails, spousePmayDetails, "Co-Applicant Spouse");
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return inValidData;
	}

	private boolean validateSpouseDetails(CustomerDetails customerDetails, PmayDetails spousePmayDetails,
			String custType) {
		boolean inValidData = false;
		StringBuilder reason = new StringBuilder();
		Map<String, Object> mapValues = new HashMap<String, Object>();
		Customer customer = customerDetails.getCustomer();

		if ("Applicant Spouse".equals(custType)) {
			if (financeMainBaseCtrl != null && financeMainBaseCtrl.getCustomerDialogCtrl() != null) {
				CustomerDialogCtrl customerDialogCtrl = financeMainBaseCtrl.getCustomerDialogCtrl();
				try {
					ExtendedFieldRender extendedDetails = customerDialogCtrl.getExtendedDetails();
					mapValues = extendedDetails.getMapValues();
				} catch (WrongValueException e) {
					Window window = customerDialogCtrl.getExtendedFieldCtrl().getWindow();
					if (window != null && customerDialogCtrl.getExtendedFieldCtrl() != null
							&& customerDialogCtrl.getExtendedFieldCtrl().getExtendedFieldHeader() != null) {

						ExtendedFieldHeader extendedFieldHeader = customerDialogCtrl.getExtendedFieldCtrl()
								.getExtendedFieldHeader();
						Component fellowIfAny = window.getFellowIfAny(
								extendedFieldHeader.getModuleName() + extendedFieldHeader.getSubModuleName());
						if (fellowIfAny != null && fellowIfAny instanceof Tab) {
							Tab parTab = (Tab) fellowIfAny;
							parTab.setSelected(true);
						}
					}
					logger.error(Literal.EXCEPTION, e);
				}
			} else {
				mapValues = customerDetails.getExtendedFieldRender().getMapValues();
			}

		} else if ("Co-Applicant Spouse".equals(custType)) {
			mapValues = customerDetails.getExtendedFieldRender().getMapValues();
		}
		if (mapValues != null) {
			if (mapValues.containsKey("SPOUSENAME")) {
				String spouseName = (String) mapValues.get("SPOUSENAME");
				if (StringUtils.isEmpty(spouseName)) {
					inValidData = true;
					reason.append("Spouse Name  is Mandatory. ");
				} else {
					spousePmayDetails.setNAME(spouseName);
				}
			}

			if (mapValues.containsKey("ISAADHARAVL")) {
				int isAadharVal = 1;
				if (mapValues.get("ISAADHARAVL") == null) {
					isAadharVal = 1;
				} else if (mapValues.get("ISAADHARAVL") instanceof BigDecimal) {
					isAadharVal = ((BigDecimal) mapValues.get("ISAADHARAVL")).intValue();
				} else if (mapValues.get("ISAADHARAVL") instanceof Integer) {
					isAadharVal = ((Integer) mapValues.get("ISAADHARAVL"));
				}
				if (1 == isAadharVal) {
					String aadharNo = (String) mapValues.get("SPOUSEAADHAREN");
					if (StringUtils.isEmpty(aadharNo)) {
						inValidData = true;
						reason.append("Spouse Aadhaar Number  is Mandatory . ");
					} else {
						spousePmayDetails.setAADHAAR_NO(aadharNo);
					}
				} else {
					String enrollmentNo = (String) mapValues.get("SPOUSEAADHAREN");
					if (StringUtils.isEmpty(enrollmentNo)) {
						inValidData = true;
						reason.append("Spouse Enrollment Number is Mandatory . ");
					} else {
						spousePmayDetails.setAADHAAR_NO(enrollmentNo);
					}
				}
			}

			if (mapValues.containsKey("SPOUSEMOBILENO")) {
				String mobileNo = (String) mapValues.get("SPOUSEMOBILENO");
				if (StringUtils.isEmpty(mobileNo)) {
					inValidData = true;
					reason.append("Spouse Mobile Number is Mandatory . ");
				} else {
					spousePmayDetails.setMOBILE_NO(mobileNo);
				}
			}

			if (mapValues.containsKey("SPOUSEGENDER")) {
				String gender = (String) mapValues.get("SPOUSEGENDER");
				if (PennantConstants.List_Select.equals(gender) || StringUtils.isEmpty(gender)) {
					inValidData = true;
					reason.append("SPOUSE GENDER is Mandatory . ");
				} else {
					spousePmayDetails.setGENDER(gender);
				}
			}

			if (mapValues.containsKey("SPOUSEALTERNEMAIL")) {
				spousePmayDetails.setALTERNATE_ID((String) mapValues.get("SPOUSEALTERNEMAIL"));
			}
			if (mapValues.containsKey("SPOUSEEMAILID")) {
				spousePmayDetails.setEMAIL_ID((String) mapValues.get("SPOUSEEMAILID"));
			}

			if (mapValues.containsKey("SPOUSEALTERNATEMOB")) {
				spousePmayDetails.setALTERNATE_NO((String) mapValues.get("SPOUSEALTERNATEMOB"));
			}
		} else {
			inValidData = true;
			reason.append(" Spouse Details are mandatory in customer Additional Details Tab , ");
		}

		if (reason.length() > 1) {
			reason.append(" , for " + custType + " Customer Cif is " + customer.getCustCIF() + ". ");
		}

		if (inValidData) {
			MessageUtil.showError(reason.toString());
		} else {
			this.pmyReq.getPmayDetails().add(spousePmayDetails);
		}

		return inValidData;
	}

	private void renderItem(List<PmayEligibilityLog> eligibilityLog) {

		logger.debug(Literal.ENTERING);
		Listitem listitem;
		if (eligibilityLog != null && !eligibilityLog.isEmpty()) {
			for (PmayEligibilityLog pmayEligibilityLog : eligibilityLog) {
				listitem = new Listitem();
				Listcell lc;

				// Record ID
				lc = new Listcell(String.valueOf(pmayEligibilityLog.getRecordId()));
				lc.setParent(listitem);

				// PMAY STATUS
				lc = new Listcell(pmayEligibilityLog.getPmayStatus());
				lc.setParent(listitem);

				// Error Code
				lc = new Listcell(pmayEligibilityLog.getErrorCode());
				lc.setParent(listitem);

				// Error Description
				lc = new Listcell(pmayEligibilityLog.getErrorDesc());
				lc.setParent(listitem);

				// Applicant ID
				lc = new Listcell(pmayEligibilityLog.getApplicantId());
				lc.setParent(listitem);

				// Applicant ID
				Textbox remarks = new Textbox();
				remarks.addForward("onChange", self, "onChangeRemarks");
				remarks.setAttribute("pmayEligibilityLog", pmayEligibilityLog);
				lc = new Listcell();
				lc.appendChild(remarks);
				lc.setParent(listitem);
				listitem.setAttribute("data", pmayEligibilityLog);
				this.listBoxfinalEligibility.appendChild(listitem);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChangeRemarks(ForwardEvent event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		Textbox remarks = (Textbox) event.getOrigin().getTarget();

		PmayEligibilityLog pmayEligibilityLog = (PmayEligibilityLog) remarks.getAttribute("pmayEligibilityLog");
		pmayEligibilityLog.setRemarks(remarks.getValue());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.pmay.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param pmay
	 * 
	 */
	public void doWriteBeanToComponents(PMAY aPMAY) {
		logger.debug(Literal.ENTERING);

		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		if (!fromLoan) {
			this.basicDts.setVisible(true);
			this.finBasic_finType.setValue(financeMain.getFinType());
			this.finBasic_finReference.setValue(financeMain.getFinReference());
			this.finBasic_custCif.setValue(customer.getCustCIF());
			this.finBasic_custShrtName.setValue(customer.getCustShrtName());
		}
		// notified town
		if (aPMAY.isNotifiedTown()) {
			this.townCode.setReadonly(false);
			this.notifiedTownYes.setSelected(aPMAY.isNotifiedTown());
		} else {
			this.townCode.setReadonly(true);
			this.notifiedTownNo.setSelected(true);
		}

		this.townCode.setValue(String.valueOf(aPMAY.getTownCode() != 0 ? aPMAY.getTownCode() : ""),
				aPMAY.getTownName());

		// Central Asstnce
		if (aPMAY.isCentralAssistance()) {
			this.centralAssistanceYes.setSelected(aPMAY.isCentralAssistance());
		} else {
			this.centralAssistanceNo.setSelected(true);
		}
		// Owned house
		if (aPMAY.isOwnedHouse()) {
			this.ownedHouseYes.setSelected(aPMAY.isOwnedHouse());
		} else {
			this.ownedHouseNo.setSelected(true);
		}
		// carpet Area
		this.carpetArea.setValue(aPMAY.getCarpetArea());

		// annual income
		this.householdAnnIncome
				.setValue(PennantApplicationUtil.formateAmount(aPMAY.getHouseholdAnnIncome(), ccyFormatter));
		// Balance Transfr
		if (aPMAY.isBalanceTransfer()) {
			this.balanceTransferYes.setSelected(aPMAY.isBalanceTransfer());
		} else {
			this.balanceTransferNo.setSelected(true);
		}
		// primary applcnt
		if (aPMAY.isPrimaryApplicant()) {
			this.primaryApplicantYes.setSelected(aPMAY.isPrimaryApplicant());
		} else {
			this.primaryApplicantNo.setSelected(true);
		}

		fillComboBox(this.transactionFinType, aPMAY.getTransactionFinType(), txFinTypeList, "");
		fillComboBox(this.product, aPMAY.getProduct(), productTypeList, "");
		// property owned by women
		if (aPMAY.isPrprtyOwnedByWomen()) {
			this.prptyOwnedByWomenYes.setSelected(aPMAY.isPrprtyOwnedByWomen());
		} else {
			this.prptyOwnedByWomenNo.setSelected(true);
		}
		// water supply
		if (aPMAY.isWaterSupply()) {
			this.waterSupplyYes.setSelected(aPMAY.isWaterSupply());
		} else {
			this.waterSupplyNo.setSelected(true);
		}
		// drinage supply
		if (aPMAY.isDrinage()) {
			this.drinageYes.setSelected(aPMAY.isDrinage());
		} else {
			this.drinageNo.setSelected(true);
		}
		// electricity supply
		if (aPMAY.isElectricity()) {
			this.electricityYes.setSelected(aPMAY.isElectricity());
		} else {
			this.electricityNo.setSelected(true);
		}
		this.pmayCategory.setValue(aPMAY.getPmayCategory());
		renderItem(aPMAY.getPmayEligibilityLogList());

		if (fromLoan
				&& ((StringUtils.isEmpty(aPMAY.getPmayCategory()) || StringUtils.equals(aPMAY.getPmayCategory(), "NA"))
						|| aPMAY.getPmayEligibilityLogList().size() >= 1)) {
			btnFinalElgbty.setDisabled(true);
		}
		for (PmayEligibilityLog pmayEligibilityLog : aPMAY.getPmayEligibilityLogList()) {
			if (StringUtils.isEmpty(pmayEligibilityLog.getApplicantId())
					|| StringUtils.isEmpty(pmayEligibilityLog.getErrorCode())) {
				btnFinalElgbty.setDisabled(true);
				break;
			}
		}
		this.recordStatus.setValue(aPMAY.getRecordStatus());
		logger.debug(Literal.LEAVING);
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPMAY
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean(PMAY aPMAY) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		aPMAY.setFinID(financeMain.getFinID());
		aPMAY.setFinReference(financeMain.getFinReference());
		// notified town

		try {
			if (notifiedTownYes.isSelected()) {
				aPMAY.setNotifiedTown(this.notifiedTownYes.isSelected());
			} else {
				aPMAY.setNotifiedTown(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aPMAY.setTownCode(Long
					.parseLong(this.townCode.getValidatedValue().equals("") ? "0" : this.townCode.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// central asstnce
		try {
			if (centralAssistanceYes.isSelected()) {
				aPMAY.setCentralAssistance(this.centralAssistanceYes.isSelected());
			} else {
				aPMAY.setCentralAssistance(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Owned house
		try {
			if (ownedHouseYes.isSelected()) {
				aPMAY.setOwnedHouse(this.ownedHouseYes.isSelected());
			} else {
				aPMAY.setOwnedHouse(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Carpet Area
		try {
			aPMAY.setCarpetArea(this.carpetArea.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Annual income
		try {
			aPMAY.setHouseholdAnnIncome(
					PennantApplicationUtil.unFormateAmount(this.householdAnnIncome.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Balance Trnsfr
		try {
			if (balanceTransferYes.isSelected()) {
				aPMAY.setBalanceTransfer(this.balanceTransferYes.isSelected());
			} else {
				aPMAY.setBalanceTransfer(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Primary Appcnt
		try {
			if (primaryApplicantYes.isSelected()) {
				aPMAY.setPrimaryApplicant(this.primaryApplicantYes.isSelected());
			} else {
				aPMAY.setPrimaryApplicant(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tx FinType
		try {
			aPMAY.setTransactionFinType(this.transactionFinType.getSelectedItem().getValue().toString());
		} catch (WrongValueException e) {
			// TODO: handle exception
		}
		// Product
		try {
			aPMAY.setProduct(this.product.getSelectedItem().getValue().toString());
		} catch (WrongValueException e) {
			// TODO: handle exception
		}
		// Prprty Owned by women
		try {
			if (prptyOwnedByWomenYes.isSelected()) {
				aPMAY.setPrprtyOwnedByWomen(this.prptyOwnedByWomenYes.isSelected());
			} else {
				aPMAY.setPrprtyOwnedByWomen(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// water supply
		try {
			if (waterSupplyYes.isSelected()) {
				aPMAY.setWaterSupply(this.waterSupplyYes.isSelected());
			} else {
				aPMAY.setWaterSupply(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// drinage
		try {
			if (drinageYes.isSelected()) {
				aPMAY.setDrinage(this.drinageYes.isSelected());
			} else {
				aPMAY.setDrinage(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Electricity
		try {
			if (electricityYes.isSelected()) {
				aPMAY.setElectricity(this.electricityYes.isSelected());
			} else {
				aPMAY.setElectricity(false);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aPMAY.setPmayCategory(this.pmayCategory.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		List<Listitem> items = this.listBoxfinalEligibility.getItems();
		List<PmayEligibilityLog> PmayEligibilityLogList = new ArrayList<>();
		for (Listitem listitem : items) {
			PmayEligibilityLog pmayEligibilityLog = (PmayEligibilityLog) listitem.getAttribute("data");
			PmayEligibilityLogList.add(pmayEligibilityLog);
		}
		aPMAY.setPmayEligibilityLogList(PmayEligibilityLogList);

		doRemoveValidation();
		doRemoveLOVValidation();
		logger.debug(Literal.LEAVING);
		return wve;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param pmay The entity that need to be render.
	 */
	public void doShowDialog(PMAY aPMAY) {
		logger.debug(Literal.ENTERING);

		if (aPMAY.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aPMAY.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else if (fromLoan) {
				this.btnCtrl.setInitEdit();
				this.btnCancel.setVisible(false);
				this.btnClose.setVisible(false);
				doEdit();
			}
		}

		doWriteBeanToComponents(aPMAY);
		if (enqiryModule) {
			doReadOnly();
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}
		if (parenttab != null) {
			this.parenttab.setVisible(true);
		}
		if (!fromLoan) {
			setDialog(DialogType.EMBEDDED);
		} else {
			this.btnCancel.setVisible(false);
			this.btnClose.setVisible(false);
			this.btnSave.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.carpetArea.isDisabled()) {
			this.carpetArea.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_PmayDialog_CarpetArea.value"), 2, false, false));
		}
		if (!this.townCode.isReadonly()) {
			if (notifiedTownYes.isSelected()) {
				this.townCode.setConstraint(
						new PTStringValidator(Labels.getLabel("label_PmayDialog_TownCode.value"), null, true, true));
				this.townCode.setMandatoryStyle(true);
			} else {
				this.townCode.setConstraint("");
				this.townCode.setMandatoryStyle(false);
			}
		} else {
			this.townCode.setConstraint("");
			this.townCode.setMandatoryStyle(false);
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.householdAnnIncome.setConstraint("");
		this.carpetArea.setConstraint("");
		this.transactionFinType.setConstraint("");
		this.product.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.finReference.setErrorMessage("");
		this.householdAnnIncome.setErrorMessage("");
		this.carpetArea.setErrorMessage("");
		this.transactionFinType.setErrorMessage("");
		this.product.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final PMAY aPMAY = new PMAY();
		BeanUtils.copyProperties(this.pmay, aPMAY);

		doDelete(aPMAY.getFinReference(), aPMAY);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);
		readOnlyComponent(isReadOnly("PmayDialog_BalanceTransfer"), this.balanceTransferYes);
		readOnlyComponent(isReadOnly("PmayDialog_BalanceTransfer"), this.balanceTransferNo);
		readOnlyComponent(isReadOnly("PmayDialog_PrimaryApplicant"), this.primaryApplicantYes);
		readOnlyComponent(isReadOnly("PmayDialog_PrimaryApplicant"), this.primaryApplicantNo);
		readOnlyComponent(isReadOnly("PmayDialog_CentralAssistance"), this.centralAssistanceYes);
		readOnlyComponent(isReadOnly("PmayDialog_CentralAssistance"), this.centralAssistanceNo);
		readOnlyComponent(isReadOnly("PmayDialog_OwnedHouse"), this.ownedHouseYes);
		readOnlyComponent(isReadOnly("PmayDialog_OwnedHouse"), this.ownedHouseNo);
		readOnlyComponent(isReadOnly("PmayDialog_CarpetArea"), this.carpetArea);
		readOnlyComponent(isReadOnly("PmayDialog_HouseholdAnnIncome"), this.householdAnnIncome);
		readOnlyComponent(isReadOnly("PmayDialog_TransactionFinType"), this.transactionFinType);
		readOnlyComponent(isReadOnly("PmayDialog_NotifiedTown"), this.notifiedTownYes);
		readOnlyComponent(isReadOnly("PmayDialog_NotifiedTown"), this.notifiedTownNo);
		readOnlyComponent(isReadOnly("PmayDialog_TownCode"), this.townCode);
		readOnlyComponent(isReadOnly("PmayDialog_Product"), this.product);
		readOnlyComponent(isReadOnly("PmayDialog_PrprtyOwnedByWomen"), this.prptyOwnedByWomenYes);
		readOnlyComponent(isReadOnly("PmayDialog_PrprtyOwnedByWomen"), this.prptyOwnedByWomenNo);
		readOnlyComponent(isReadOnly("PmayDialog_WaterSupply"), this.waterSupplyYes);
		readOnlyComponent(isReadOnly("PmayDialog_WaterSupply"), this.waterSupplyNo);
		readOnlyComponent(isReadOnly("PmayDialog_Drinage"), this.drinageYes);
		readOnlyComponent(isReadOnly("PmayDialog_Drinage"), this.drinageNo);
		readOnlyComponent(isReadOnly("PmayDialog_Electricity"), this.electricityYes);
		readOnlyComponent(isReadOnly("PmayDialog_Electricity"), this.electricityNo);
		readOnlyComponent(isReadOnly("PmayDialog_PmayCategory"), this.pmayCategory);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.pmay.isNewRecord()) {
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		readOnlyComponent(true, this.balanceTransferYes);
		readOnlyComponent(true, this.balanceTransferNo);
		readOnlyComponent(true, this.primaryApplicantYes);
		readOnlyComponent(true, this.primaryApplicantNo);
		readOnlyComponent(true, this.centralAssistanceYes);
		readOnlyComponent(true, this.centralAssistanceNo);
		readOnlyComponent(true, this.ownedHouseYes);
		readOnlyComponent(true, this.ownedHouseNo);
		readOnlyComponent(true, this.carpetArea);
		readOnlyComponent(true, this.householdAnnIncome);
		readOnlyComponent(true, this.transactionFinType);
		readOnlyComponent(true, this.notifiedTownYes);
		readOnlyComponent(true, this.notifiedTownNo);
		readOnlyComponent(true, this.townCode);
		readOnlyComponent(true, this.product);
		readOnlyComponent(true, this.prptyOwnedByWomenYes);
		readOnlyComponent(true, this.prptyOwnedByWomenNo);
		readOnlyComponent(true, this.waterSupplyYes);
		readOnlyComponent(true, this.waterSupplyNo);
		readOnlyComponent(true, this.drinageYes);
		readOnlyComponent(true, this.drinageNo);
		readOnlyComponent(true, this.electricityYes);
		readOnlyComponent(true, this.electricityNo);
		readOnlyComponent(true, this.pmayCategory);
		readOnlyComponent(true, this.btnFinalElgbty);
		readOnlyComponent(true, this.btnPMAYElgbty);
		this.btnClose.setVisible(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		this.finReference.setValue("");
		this.householdAnnIncome.setValue("");
		this.carpetArea.setValue("");
		this.transactionFinType.setValue("");
		this.product.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final PMAY aPMAY = new PMAY();
		BeanUtils.copyProperties(this.pmay, aPMAY);
		boolean isNew = false;

		doSetValidation();
		ArrayList<WrongValueException> wve = doWriteComponentsToBean(aPMAY);

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		isNew = aPMAY.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPMAY.getRecordType())) {
				aPMAY.setVersion(aPMAY.getVersion() + 1);
				if (isNew) {
					aPMAY.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPMAY.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPMAY.setNewRecord(true);
				}
			}
		} else {
			aPMAY.setVersion(aPMAY.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aPMAY, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aPMAY    (PMAY)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	public void doSave(FinanceDetail aFinanceDetail, Tab pmayDetailsTab, boolean recSave) {
		logger.debug(Literal.ENTERING);
		doClearMessage();
		if (!recSave) {
			doSetValidation();
		}

		ArrayList<WrongValueException> wve = doWriteComponentsToBean(this.pmay);
		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}
		showErrorDetails(wve, parenttab);

		FinScheduleData aSchdData = aFinanceDetail.getFinScheduleData();
		FinanceMain aFm = aSchdData.getFinanceMain();
		this.pmay.setFinID(aFm.getFinID());
		this.pmay.setFinReference(aFm.getFinReference());

		if (StringUtils.isBlank(this.pmay.getRecordType())) {
			this.pmay.setVersion(this.pmay.getVersion() + 1);
			this.pmay.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			this.pmay.setNewRecord(true);
		}

		this.pmay.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		this.pmay.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		this.pmay.setUserDetails(getUserWorkspace().getLoggedInUser());
		aFinanceDetail.setPmay(this.pmay);
		// Pmay data saving issue in finance main
		if (this.financeDetail != null) {
			boolean ispMay = this.financeDetail.getFinScheduleData().getFinanceMain().isPmay();
			aFm.setPmay(ispMay);
		}
		logger.debug(Literal.LEAVING);
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		if (!wve.isEmpty()) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (tab != null) {
				tab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	protected boolean doProcess(PMAY aPMAY, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPMAY.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aPMAY.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPMAY.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPMAY.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPMAY.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPMAY);
				}

				if (isNotesMandatory(taskId, aPMAY)) {
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

			aPMAY.setTaskId(taskId);
			aPMAY.setNextTaskId(nextTaskId);
			aPMAY.setRoleCode(getRole());
			aPMAY.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPMAY, tranType);

			String operationRefs = getServiceOperations(taskId, aPMAY);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPMAY, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPMAY, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
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
		PMAY aPMAY = (PMAY) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = pmayService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = pmayService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = pmayService.doApprove(auditHeader);

					if (aPMAY.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = pmayService.doReject(auditHeader);
					if (aPMAY.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_PmayDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_PmayDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.pmay), true);
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

	public PMAY getPmay() {
		return pmay;
	}

	public void setPmay(PMAY pmay) {
		this.pmay = pmay;
	}

	private Notes getNotes() {
		logger.debug(Literal.ENTERING);
		Notes notes = new Notes();
		notes.setModuleName("PMAY");
		// notes.setReference(String.valueOf(getPmay().getFinReference()));
		notes.setVersion(getPmay().getVersion());
		logger.debug(Literal.LEAVING);
		return notes;
	}

	/**
	 * @param aPMAY
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(PMAY aPMAY, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPMAY.getBefImage(), aPMAY);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aPMAY.getUserDetails(), getOverideMap());
	}

	public boolean isReadOnly(String componentName) {
		if (enqiryModule) {
			return true;
		} else {
			return getUserWorkspace().isReadOnly(componentName);
		}
	}

	public PMAYService getPmayService() {
		return pmayService;
	}

	public void setPmayService(PMAYService pmayService) {
		this.pmayService = pmayService;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public FinanceMainBaseCtrl getFinanceMainBaseCtrl() {
		return financeMainBaseCtrl;
	}

	public void setFinanceMainBaseCtrl(FinanceMainBaseCtrl financeMainBaseCtrl) {
		this.financeMainBaseCtrl = financeMainBaseCtrl;
	}

}
