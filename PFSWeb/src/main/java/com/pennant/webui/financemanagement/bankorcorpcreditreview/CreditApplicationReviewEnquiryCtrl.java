package com.pennant.webui.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditApplicationReviewDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.model.reports.CreditReviewMainCtgDetails;
import com.pennant.backend.model.reports.CreditReviewSubCtgDetails;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerBankInfoService;
import com.pennant.backend.service.customermasters.CustomerExtLiabilityService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl.CreditReviewSummaryData;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.incomeexpensedetail.dao.IncomeExpenseDetailDAO;
import com.pennanttech.pff.notifications.service.NotificationService;

public class CreditApplicationReviewEnquiryCtrl extends GFCBaseCtrl<FinCreditReviewDetails> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(CreditApplicationReviewEnquiryCtrl.class);

	protected Window window_CreditApplicationReviewDialog;
	protected Borderlayout borderlayout_CreditApplicationReview;
	protected Grid creditApplicationReviewGrid;
	protected Longbox custID;
	protected Intbox toYear;
	protected Textbox custCIF;
	protected Label custShrtName;
	protected Groupbox gb_CreditReviwDetails;
	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Button btnSearch;
	protected Button btnPrint;

	// Customer Details
	protected Textbox bankName;
	protected Textbox auditors;
	protected Radiogroup conSolOrUnConsol;
	protected Radio conSolidated;
	protected Radio unConsolidated;
	protected Textbox location;
	protected Textbox auditedYear;
	protected Datebox auditedDate;
	protected Decimalbox conversionRate;
	protected Longbox noOfShares;
	protected CurrencyBox marketPrice;
	protected Combobox auditPeriod;
	protected Combobox auditType;
	protected Radiogroup qualifiedUnQualified;
	protected Radio qualRadio;
	protected Radio unQualRadio;
	protected Textbox lovDescFinCcyName;
	protected ExtendedCombobox currencyType;

	protected Label label_CreditApplicationReviewDialog_NoOfYearsToDisplay;
	protected Row row1;
	protected Row row2;
	protected Row row3;
	protected Row row4;
	protected Row row5;
	protected Row row6;
	protected Row row7;
	protected Row row8;

	protected Label label_CreditApplicationReviewDialog_RecordStatus;
	protected Groupbox gb_CustDetails;
	protected Listbox listBoxCust;

	protected Button btnSearchPRCustid;
	private JdbcSearchObject<Customer> newSearchObject;

	private List<CreditReviewSubCtgDetails> creditReviewSubtgDetailsList = new ArrayList<CreditReviewSubCtgDetails>();
	private FinCreditReviewDetails finCreditReviewDetails = null;
	private transient WorkFlowDetails workFlowDetails = null;
	public List<Notes> notesList = new ArrayList<Notes>();
	private CreditApplicationReviewListCtrl creditApplicationReviewListCtrl = null;

	private List<FinCreditRevCategory> listOfFinCreditRevCategory = new ArrayList<>();
	private int noOfYears = SysParamUtil.getValueAsInt("NO_OF_YEARS_TOSHOW");
	private int currFormatter;
	private Map<String, String> dataMap = new HashMap<>();
	private List<Filter> filterList = null;
	private int year;
	private boolean ratioFlag = true;
	private String custCtgCode = null;

	private boolean isEnquiry = true;

	private Map<String, FinCreditReviewDetails> creditReviewDetailsMap;
	BigDecimal totAsstValue0 = BigDecimal.ZERO;
	BigDecimal totLibNetWorthValue0 = BigDecimal.ZERO;
	BigDecimal totAsstValue1 = BigDecimal.ZERO;
	BigDecimal totLibNetWorthValue1 = BigDecimal.ZERO;
	BigDecimal totAsstValue2 = BigDecimal.ZERO;
	BigDecimal totLibNetWorthValue2 = BigDecimal.ZERO;

	protected Div div_CmdBtntoolbar;
	protected Div div_SearchBtntoolbar;
	protected Div divDel;
	protected Groupbox gb_basicDetails;
	Date appldate = SysParamUtil.getAppDate();
	Date appDftStrtDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
	String maxAuditYear = null;
	boolean showCurrentYear;
	int notesEnteredCount;
	int noOfRecords;
	private BigDecimal firstRepay = BigDecimal.ZERO;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal finAssetValue = BigDecimal.ZERO;
	private BigDecimal repayProfitRate = BigDecimal.ZERO;
	private int roundingTarget = 0;
	private int numberOfTerms = 0;
	private Map<String, String> extDataMap = new HashMap<String, String>();
	int finFormatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

	private FinanceDetail financeDetail;
	Set<Long> custIds = new HashSet<>();
	private List<FinCreditReviewDetails> auditYears;
	private List<JointAccountDetail> coAppIds = new ArrayList<>();
	CustomerBankInfo custBankInfo = null;
	BigDecimal sumOfEMI = BigDecimal.ZERO;
	BigDecimal totalsumOfEMI = BigDecimal.ZERO;
	BigDecimal sumCreditAmt = BigDecimal.ZERO;

	private List<Map<String, Object>> schlDataMap = new ArrayList<>();
	private boolean fromLoan = false;
	private String eligibilityMethods = "";

	// Spread Sheet changes
	protected CreditReviewDetails creditReviewDetails;
	protected CreditReviewData creditReviewData;
	protected List<Object> finBasicDetails;
	private FinanceMainBaseCtrl financeMainBaseCtrl = null;
	List<CustomerExtLiability> extLiabilities = new ArrayList<>();

	private transient CreditApplicationReviewService creditApplicationReviewService;
	private CreditReviewSummaryData creditReviewSummaryData;
	private transient NotificationService notificationService;
	private transient CustomerBankInfoService customerBankInfoService;
	private transient CustomerExtLiabilityService customerExtLiabilityService;
	private transient CreditApplicationReviewDAO creditApplicationReviewDAO;
	private transient IncomeExpenseDetailDAO incomeExpenseDetailDAO;

	/**
	 * default constructor.<br>
	 */
	public CreditApplicationReviewEnquiryCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CreditApplicationReviewDialog";
	}

	private String unFormat(BigDecimal amount) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}

		return PennantApplicationUtil.formateAmount(amount, finFormatter).toString();
	}

	private String unFormat(Integer amount) {
		if (amount == null) {
			amount = 0;
		}

		return String.valueOf(amount);
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CreditApplicationReviewDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CreditApplicationReviewDialog);

		try {

			if (arguments.containsKey("custCIF") && arguments.containsKey("custID")
					&& arguments.containsKey("custCtgType")) {
				this.custID.setValue((Long) arguments.get("custID"));

				showCurrentYear = true;
				isEnquiry = false;
				this.custCIF.setValue((String) arguments.get("custCIF"));
				this.custCtgCode = (String) arguments.get("custCtgType");
				this.fromLoan = (boolean) arguments.get("fromLoan");

				// based on loan type configuration
				if (fromLoan) {
					List<Long> eligibilityIdsList = new ArrayList<>();
					this.eligibilityMethods = (String) arguments.get("eligibilityMethods");
					if (this.eligibilityMethods != null && !this.eligibilityMethods.isEmpty()) {
						eligibilityIdsList = Arrays.asList(eligibilityMethods.split(",")).stream()
								.map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
						this.listOfFinCreditRevCategory = this.creditApplicationReviewService
								.getCreditRevCategoryByCreditRevCodeAndEligibilityIds(this.custCtgCode,
										eligibilityIdsList);
					} else {
						eligibilityIdsList.add(Long.valueOf(-1));
						this.listOfFinCreditRevCategory = this.creditApplicationReviewService
								.getCreditRevCategoryByCreditRevCodeAndEligibilityIds(this.custCtgCode,
										eligibilityIdsList);
					}
				} else {
					this.listOfFinCreditRevCategory = this.creditApplicationReviewService
							.getCreditRevCategoryByCreditRevCode(this.custCtgCode);
				}

				this.maxAuditYear = creditApplicationReviewService.getMaxAuditYearByCustomerId(this.custID.longValue(),
						"_VIEW");

				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");

				if (financeDetail != null && financeDetail.getJointAccountDetailList() != null
						&& financeDetail.getJointAccountDetailList().size() > 0) {
					for (JointAccountDetail jointAccountDetail : financeDetail.getJointAccountDetailList()) {
						if (!StringUtils.equals(jointAccountDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)
								&& !StringUtils.equals(jointAccountDetail.getRecordType(),
										PennantConstants.RECORD_TYPE_CAN)) {
							coAppIds.add(jointAccountDetail);
						}
					}
				}

				// getting co-applicant id's
				// coAppIds = jointAccountDetailDAO.getCustIdsByFinnRef(finReference);
				custIds.add(this.custID.getValue());

				// Adding co-applicant id's
				if (coAppIds != null && coAppIds.size() > 0) {
					for (JointAccountDetail jointAccountDetail : coAppIds) {
						custIds.add(jointAccountDetail.getCustID());
					}
				}

				// getting audit years from credit review details
				auditYears = creditApplicationReviewDAO.getAuditYearsByCustId(custIds);
				custBankInfo = customerBankInfoService.getSumOfAmtsCustomerBankInfoByCustId(custIds);
				sumOfEMI = customerExtLiabilityService.getSumAmtCustomerExtLiabilityById(custIds);
				sumCreditAmt = customerExtLiabilityService.getSumCredtAmtCustomerBankInfoById(custIds);

				totalsumOfEMI = customerExtLiabilityService.getSumAmtCustomerInternalLiabilityById(custIds);
				totalsumOfEMI = sumOfEMI.add(totalsumOfEMI);
				custIds.remove(this.custID.getValue());
				// Fill Customer details from co-applicants
				doFillCustomerDetails(auditYears);

				this.toYear.setValue(Integer.parseInt(maxAuditYear));
				year = this.toYear.getValue();
				if (arguments.containsKey("facility")) {
					isEnquiry = true;
				}

				if (custBankInfo != null) {
					extDataMap.put("EXT_CREDITTRANNO", unFormat(custBankInfo.getCreditTranNo()));
					extDataMap.put("EXT_CREDITTRANAMT", unFormat(sumCreditAmt));
					extDataMap.put("EXT_CREDITTRANAVG", unFormat(custBankInfo.getCreditTranAvg()));
					extDataMap.put("EXT_DEBITTRANNO", unFormat(custBankInfo.getDebitTranNo()));
					extDataMap.put("EXT_DEBITTRANAMT", unFormat(custBankInfo.getDebitTranAmt()));
					extDataMap.put("EXT_CASHDEPOSITNO", unFormat(custBankInfo.getCashDepositNo()));
					extDataMap.put("EXT_CASHDEPOSITAMT", unFormat(custBankInfo.getCashDepositAmt()));
					extDataMap.put("EXT_CASHWITHDRAWALNO", unFormat(custBankInfo.getCashWithdrawalNo()));
					extDataMap.put("EXT_CASHWITHDRAWALAMT", unFormat(custBankInfo.getCashWithdrawalAmt()));
					extDataMap.put("EXT_CHQDEPOSITNO", unFormat(custBankInfo.getChqDepositNo()));
					extDataMap.put("EXT_CHQDEPOSITAMT", unFormat(custBankInfo.getChqDepositAmt()));
					extDataMap.put("EXT_CHQISSUENO", unFormat(custBankInfo.getChqIssueNo()));
					extDataMap.put("EXT_CHQISSUEAMT", unFormat(custBankInfo.getChqIssueAmt()));
					extDataMap.put("EXT_INWARDCHQBOUNCENO", unFormat(custBankInfo.getInwardChqBounceNo()));
					extDataMap.put("EXT_OUTWARDCHQBOUNCENO", unFormat(custBankInfo.getOutwardChqBounceNo()));
					extDataMap.put("EXT_EODBALAVG", unFormat(custBankInfo.getEodBalAvg()));
					extDataMap.put("EXT_EODBALMAX", unFormat(custBankInfo.getEodBalMax()));
					extDataMap.put("EXT_EODBALMIN", unFormat(custBankInfo.getEodBalMin()));

				}

				extDataMap.put("EXT_OBLIGATION", unFormat(sumOfEMI));

				extDataMap.put("EXT_OBLIGATION_ALL", unFormat(totalsumOfEMI));
				if (arguments.containsKey("numberOfTerms")) {
					numberOfTerms = (int) arguments.get("numberOfTerms");
					extDataMap.put("EXT_NUMBEROFTERMS", String.valueOf(numberOfTerms));
				}
				if (arguments.containsKey("repayProfitRate")) {
					repayProfitRate = (BigDecimal) arguments.get("repayProfitRate");
					extDataMap.put("EXT_REPAYPROFITRATE", String.valueOf(repayProfitRate));
				}
				if (arguments.containsKey("roundingTarget")) {
					roundingTarget = (int) arguments.get("roundingTarget");
					extDataMap.put("EXT_ROUNDINGTARGET", String.valueOf(roundingTarget));
				}
				if (arguments.containsKey("finAssetValue")) {
					finAssetValue = (BigDecimal) arguments.get("finAssetValue");
					extDataMap.put("EXT_FINASSETVALUE", unFormat(finAssetValue));
				}
				if (arguments.containsKey("finAmount")) {
					finAmount = (BigDecimal) arguments.get("finAmount");
					extDataMap.put("EXT_FINAMOUNT", unFormat(finAmount));
				}
				if (arguments.containsKey("firstRepay")) {
					firstRepay = (BigDecimal) arguments.get("firstRepay");
					extDataMap.put("EXT_FIRSTREPAY", unFormat(firstRepay));
				}

				if (arguments.containsKey("creditReviewDetails")) {
					creditReviewDetails = (CreditReviewDetails) arguments.get("creditReviewDetails");
				}
				if (arguments.containsKey("creditReviewData")) {
					creditReviewData = (CreditReviewData) arguments.get("creditReviewData");
				}

				if (arguments.containsKey("financeMainBaseCtrl")) {
					this.financeMainBaseCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");
				}

				if (arguments.containsKey("externalLiabilities")) {
					extLiabilities = (List<CustomerExtLiability>) arguments.get("externalLiabilities");
				}

				this.finBasicDetails = (List<Object>) arguments.get("finHeaderList");

				// School Funding Extended fields data setting
				if (this.toYear.getValue() > 0 && this.custID.getValue() > 0) {
					schlDataMap = incomeExpenseDetailDAO.getTotal(this.custID.getValue(), this.toYear.getValue());
				}

				BigDecimal coreIncome = BigDecimal.ZERO;
				BigDecimal nonCoreIncome = BigDecimal.ZERO;
				// BigDecimal expenses = BigDecimal.ZERO;

				for (Map<String, Object> map : schlDataMap) {
					if (map.get("incomeexpensetype").equals("CORE_INCOME")) {
						coreIncome = (BigDecimal) map.get("sum");
					} else if (map.get("incomeexpensetype").equals("NONCORE_INCOME")) {
						nonCoreIncome = (BigDecimal) map.get("sum");
					} else {
						// BigDecimal expenses = (BigDecimal) map.get("sum");
					}
				}
				extDataMap.put("EXT_TOT_TUT_FEE", unFormat(coreIncome));
				extDataMap.put("EXT_TOT_NON_COR_INC", unFormat(nonCoreIncome));

				setTabs(isEnquiry);

				this.div_CmdBtntoolbar.setVisible(false);
				this.div_SearchBtntoolbar.setVisible(false);
				this.gb_basicDetails.setVisible(false);
				if (arguments.containsKey("facility")) {
					this.window_CreditApplicationReviewDialog.setHeight(this.borderLayoutHeight - 80 + "px");
				}
			} else {
				setDialog(DialogType.EMBEDDED);
				showCurrentYear = false;
			}

			// For Workflow
			if (!arguments.containsKey("creditReviewDetails")) {
				if (arguments.containsKey("creditApplicationReviewListCtrl")) {
					creditApplicationReviewListCtrl = (CreditApplicationReviewListCtrl) arguments
							.get("creditApplicationReviewListCtrl");
				}
				finCreditReviewDetails = (FinCreditReviewDetails) arguments.get("creditReviewDetails");

				String moduleMapCode = null;
				if (finCreditReviewDetails != null
						&& FacilityConstants.CREDIT_DIVISION_COMMERCIAL.equals(finCreditReviewDetails.getDivision())) {
					moduleMapCode = "CommCreditAppReview";
				} else if (finCreditReviewDetails != null
						&& FacilityConstants.CREDIT_DIVISION_CORPORATE.equals(finCreditReviewDetails.getDivision())) {
					moduleMapCode = "CorpCreditAppReview";
				}

				ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(moduleMapCode);
				isEnquiry = false;

				if (finCreditReviewDetails != null) {
					maxAuditYear = creditApplicationReviewService
							.getMaxAuditYearByCustomerId(finCreditReviewDetails.getCustomerId(), "_VIEW");
					creditReviewDetailsMap = this.creditApplicationReviewService.getListCreditReviewDetailsByCustId(
							finCreditReviewDetails.getCustomerId(), noOfYears, Integer.parseInt(maxAuditYear));
					finCreditReviewDetails = creditReviewDetailsMap.get(maxAuditYear);
					doWriteBeanToComponents(finCreditReviewDetails);
					this.custCIF.setValue(finCreditReviewDetails.getLovDescCustCIF());
					this.custID.setValue(finCreditReviewDetails.getCustomerId());
					this.custCtgCode = finCreditReviewDetails.getCreditRevCode();
					this.listOfFinCreditRevCategory = this.creditApplicationReviewService
							.getCreditRevCategoryByCreditRevCode(this.custCtgCode);
					this.toYear.setValue(Integer.parseInt(finCreditReviewDetails.getAuditYear()));
					getSearch(isEnquiry);
					this.creditApplicationReviewGrid.setVisible(true);
					this.groupboxWf.setVisible(true);
					this.userAction.setVisible(true);
					this.div_SearchBtntoolbar.setVisible(false);
				} else {
					this.creditApplicationReviewGrid.setVisible(true);
					this.groupboxWf.setVisible(false);
					this.userAction.setVisible(false);
					this.div_SearchBtntoolbar.setVisible(true);
				}

				readOnlyComponent(true, this.btnSearchPRCustid);
				this.custCIF.setReadonly(true);
				this.toYear.setReadonly(true);
				if (moduleMapping.getWorkflowType() != null) {
					workFlowDetails = WorkFlowUtil.getWorkFlowDetails(moduleMapCode);
					if (workFlowDetails == null) {
						setWorkFlowEnabled(false);
					} else {
						setWorkFlowEnabled(true);
						setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
						setWorkFlowId(workFlowDetails.getId());
						this.finCreditReviewDetails.setWorkflowId(workFlowDetails.getId());
					}
				}
				doCheckRights();
				doLoadWorkFlow(this.finCreditReviewDetails.isWorkflow(), this.finCreditReviewDetails.getWorkflowId(),
						finCreditReviewDetails.getNextTaskId());
				if (isWorkFlowEnabled()) {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().allocateRoleAuthorities(getRole(), "CreditApplicationReviewDialog");
					this.btnNotes.setVisible(true);
				}
			}

			if (isEnquiry) {
				this.toYear.setVisible(true);
				this.auditedYear.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.btnSave.setVisible(false);
				this.row2.setVisible(false);
				this.row3.setVisible(false);
				this.row4.setVisible(false);
				this.row5.setVisible(false);
				this.row6.setVisible(false);
				this.row7.setVisible(false);
				this.row8.setVisible(false);
			} else {
				this.label_CreditApplicationReviewDialog_NoOfYearsToDisplay
						.setValue(Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value"));
				this.toYear.setVisible(false);
				this.auditedYear.setVisible(true);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CreditApplicationReviewDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCreditReviewDetails (FinCreditReviewDetails)
	 */
	public void doWriteBeanToComponents(FinCreditReviewDetails aCreditReviewDetails) {
		logger.debug("Entering");
		this.custID.setValue(aCreditReviewDetails.getCustomerId());
		this.custCIF.setValue(aCreditReviewDetails.getLovDescCustCIF() != null
				? StringUtils.trimToEmpty(aCreditReviewDetails.getLovDescCustCIF())
				: "");
		this.custCIF.setTooltiptext(aCreditReviewDetails.getLovDescCustCIF() != null
				? StringUtils.trimToEmpty(aCreditReviewDetails.getLovDescCustCIF())
				: "");
		this.custShrtName.setValue(aCreditReviewDetails.getLovDescCustShrtName());
		this.bankName.setValue(aCreditReviewDetails.getBankName());
		this.auditedDate.setValue(aCreditReviewDetails.getAuditedDate());
		this.auditedYear.setValue(aCreditReviewDetails.getAuditYear());
		this.currencyType.setValue(aCreditReviewDetails.getCurrency());

		this.currFormatter = CurrencyUtil.getFormat(aCreditReviewDetails.getCurrency());
		this.conversionRate.setFormat(PennantApplicationUtil.getAmountFormate(currFormatter));
		if (aCreditReviewDetails.getConversionRate() == null) {
			BigDecimal converstnRate = CurrencyUtil.parse(CalculationUtil.getConvertedAmount(
					this.currencyType.getValue(), AccountConstants.CURRENCY_USD, new BigDecimal(1000)), currFormatter);
			this.conversionRate.setValue(converstnRate);
		} else {
			this.conversionRate.setValue(aCreditReviewDetails.getConversionRate());
		}

		if (aCreditReviewDetails.isConsolidated()) {
			this.conSolOrUnConsol.setSelectedIndex(0);
		} else {
			this.conSolOrUnConsol.setSelectedIndex(1);
		}

		this.auditors.setValue(aCreditReviewDetails.getAuditors());
		this.location.setValue(aCreditReviewDetails.getLocation());
		this.noOfShares.setValue(aCreditReviewDetails.getNoOfShares());
		this.marketPrice.setFormat(
				PennantApplicationUtil.getAmountFormate(CurrencyUtil.getFormat(aCreditReviewDetails.getCurrency())));
		this.marketPrice.setScale(CurrencyUtil.getFormat(aCreditReviewDetails.getCurrency()));
		this.marketPrice.setValue(aCreditReviewDetails.getMarketPrice() == null ? BigDecimal.ZERO
				: aCreditReviewDetails.getMarketPrice());

		if (aCreditReviewDetails.getAuditPeriod() != 0) {
			fillComboBox(this.auditPeriod, String.valueOf(aCreditReviewDetails.getAuditPeriod()),
					PennantStaticListUtil.getPeriodList(), "");
		}
		fillComboBox(this.auditType, StringUtils.trimToEmpty(aCreditReviewDetails.getAuditType()),
				PennantStaticListUtil.getCreditReviewAuditTypesList(), "");
		if (aCreditReviewDetails.isQualified()) {
			this.qualifiedUnQualified.setSelectedIndex(0);
		} else {
			this.qualifiedUnQualified.setSelectedIndex(1);
		}
		doReadOnly();
		this.recordStatus.setValue(aCreditReviewDetails.getRecordStatus());
		logger.debug("Leaving");
	}

	public void doReadOnly() {
		logger.debug(" Entering ");
		this.bankName.setReadonly(true);
		this.auditors.setReadonly(true);
		this.conSolidated.setDisabled(true);
		this.unConsolidated.setDisabled(true);
		this.location.setDisabled(true);
		this.auditedYear.setReadonly(true);
		readOnlyComponent(true, this.auditPeriod);
		this.auditedDate.setDisabled(true);
		this.conversionRate.setDisabled(true);
		this.noOfShares.setReadonly(true);
		readOnlyComponent(true, this.marketPrice);
		this.auditPeriod.setReadonly(true);
		readOnlyComponent(true, this.auditType);
		this.qualRadio.setDisabled(true);
		this.unQualRadio.setDisabled(true);
		// this.lovDescFinCcyName.setReadonly(true);
		readOnlyComponent(true, this.currencyType);
		logger.debug(" Leaving ");
	}

	public void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().allocateAuthorities("CreditApplicationReviewDialog", getRole());
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnSave"));
		logger.debug("Leaving ");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void doSave() {
		logger.debug("Entering ");
		boolean isNew = false;
		String tranType = "";
		// int approvedRecordsCount = 0;
		notesEnteredCount = 0;
		Map<String, List<FinCreditReviewSummary>> creditReviewSummaryMap;
		creditReviewSummaryMap = this.creditApplicationReviewService
				.getListCreditReviewSummaryByCustId(this.custID.getValue(), noOfYears, year, "_View");
		if (creditReviewDetailsMap == null) {
			creditReviewDetailsMap = this.creditApplicationReviewService
					.getListCreditReviewDetailsByCustId(this.custID.getValue(), noOfYears, year);
		}

		List<FinCreditReviewDetails> listOfFinCreditReviewDetails = new ArrayList<FinCreditReviewDetails>();
		for (int i = 0; i < noOfYears; i++) {
			if (!"Saved".equalsIgnoreCase(userAction.getSelectedItem().getValue().toString())) {
				int yearCount = i;
				switch (yearCount) {
				case 2:
					if (totAsstValue0.compareTo(totLibNetWorthValue0) != 0) {
						MessageUtil.showError(getMessage(year - 2, totAsstValue0, totLibNetWorthValue0));
						return;
					}
					break;
				case 1:
					if (totAsstValue1.compareTo(totLibNetWorthValue1) != 0) {
						MessageUtil.showError(getMessage(year - 1, totAsstValue1, totLibNetWorthValue1));
						return;
					}
					break;
				case 0:
					if (totAsstValue2.compareTo(totLibNetWorthValue2) != 0) {
						MessageUtil.showError(getMessage(year - 0, totAsstValue2, totLibNetWorthValue2));
						return;
					}
					break;
				}
			}
			if (creditReviewDetailsMap != null && creditReviewDetailsMap.get(String.valueOf(year - i)) != null) {
				FinCreditReviewDetails aFinCreditReviewDetails = (FinCreditReviewDetails) creditReviewDetailsMap
						.get(String.valueOf(year - i));
				if (creditReviewSummaryMap.get(String.valueOf(year - i)) != null) {
					aFinCreditReviewDetails
							.setCreditReviewSummaryEntries(creditReviewSummaryMap.get(String.valueOf(year - i)));
					listOfFinCreditReviewDetails.add(aFinCreditReviewDetails);
				}
			}
		}

		// String ltstYrRcdStatus = finCreditReviewDetails.getRecordStatus();
		FinCreditReviewDetails ltstFinCreditReviewDetails = new FinCreditReviewDetails();
		BeanUtils.copyProperties(this.finCreditReviewDetails, ltstFinCreditReviewDetails);
		// noOfRecords = listOfFinCreditReviewDetails.size();
		for (FinCreditReviewDetails aCreditReviewDetails : listOfFinCreditReviewDetails) {
			if (!"Approved".equalsIgnoreCase(aCreditReviewDetails.getRecordStatus())) {
				noOfRecords++;
			}
		}

		int proRecordCount = 0;
		for (FinCreditReviewDetails aCreditReviewDetails : listOfFinCreditReviewDetails) {

			if (!"Approved".equalsIgnoreCase(aCreditReviewDetails.getRecordStatus())) {
				// for cancellation we are processing latest credit review
				// details only
				if ("Cancelled".equalsIgnoreCase(userAction.getSelectedItem().getValue().toString())) {
					if (!aCreditReviewDetails.getAuditYear().equals(ltstFinCreditReviewDetails.getAuditYear())) {
						continue;
					}
				}
				/*
				 * if(!StringUtils.trimToEmpty(aCreditReviewDetails. getRecordStatus()).equals("") &&
				 * !aCreditReviewDetails.getRecordStatus().equals( ltstYrRcdStatus)){
				 * MessageUtil.showErrorMessage(aCreditReviewDetails.
				 * getAuditYear()+" Record  in "+aCreditReviewDetails. getRecordStatus()
				 * +" State Please Process It To "+ltstYrRcdStatus+" State"); return; }
				 */
				isNew = aCreditReviewDetails.isNewRecord();
				if (isWorkFlowEnabled()) {
					tranType = PennantConstants.TRAN_WF;
					if (StringUtils.isBlank(aCreditReviewDetails.getRecordType())) {
						aCreditReviewDetails.setVersion(aCreditReviewDetails.getVersion() + 1);
						if (isNew) {
							aCreditReviewDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						} else {
							aCreditReviewDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							aCreditReviewDetails.setNewRecord(true);
						}
					}
					if (creditReviewSummaryMap.get(aCreditReviewDetails.getAuditYear()) != null) {
						List<FinCreditReviewSummary> finCreditReviewSummaryList = creditReviewSummaryMap
								.get(aCreditReviewDetails.getAuditYear());
						for (FinCreditReviewSummary finCreditReviewSummary : finCreditReviewSummaryList) {
							finCreditReviewSummary.setRecordType(ltstFinCreditReviewDetails.getRecordType());
							finCreditReviewSummary.setNewRecord(ltstFinCreditReviewDetails.isNewRecord());
							finCreditReviewSummary.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
							finCreditReviewSummary.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							finCreditReviewSummary.setRecordStatus(ltstFinCreditReviewDetails.getRecordStatus());
							finCreditReviewSummary.setWorkflowId(ltstFinCreditReviewDetails.getWorkflowId());
						}
					}
				} else {
					aCreditReviewDetails.setVersion(aCreditReviewDetails.getVersion() + 1);
					if (isNew) {
						tranType = PennantConstants.TRAN_ADD;
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}
				}

				// save it to database
				try {
					if (doProcess(aCreditReviewDetails, tranType)) {
						proRecordCount++;
						// do Close the Dialog window
						closeDialog();
						creditApplicationReviewListCtrl.doReset();
					}
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}

		if (listOfFinCreditReviewDetails != null && listOfFinCreditReviewDetails.size() > 0
				&& noOfRecords == proRecordCount) {
			// Mail Alert Notification for User
			notificationService.sendNotifications(NotificationConstants.MAIL_MODULE_CREDIT,
					listOfFinCreditReviewDetails.get(0));

			FinCreditReviewDetails creditReviewDetails = listOfFinCreditReviewDetails.get(0);
			String msg = getSavingStatus(creditReviewDetails.getRoleCode(), creditReviewDetails.getNextRoleCode(),
					creditReviewDetails.getCustomerId(), "Credit Review", creditReviewDetails.getRecordStatus());
			Clients.showNotification(msg, "info", null, null, -1);
		}

		if (proRecordCount != 0) {
			closeDialog();
			creditApplicationReviewListCtrl.doReset();
		}
		logger.debug("Leaving");
	}

	/*
	 * for preparing message
	 */
	public String getMessage(int year, BigDecimal totLaiblts, BigDecimal totNetWrth) {
		return "Total Assets and Total Liabilities & Net Worth not Matched for The Year " + year + " Difference is : "
				+ totLaiblts.subtract(totNetWrth);
	}

	protected boolean doProcess(FinCreditReviewDetails aCreditReviewDetails, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCreditReviewDetails.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCreditReviewDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCreditReviewDetails.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCreditReviewDetails.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCreditReviewDetails.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCreditReviewDetails);
				}

				if (isNotesMandatory(taskId, aCreditReviewDetails)) {
					notesEnteredCount++;
					if (!notesEntered) {
						if (notesEnteredCount == noOfRecords) {
							MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						}
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aCreditReviewDetails.setTaskId(taskId);
			aCreditReviewDetails.setNextTaskId(nextTaskId);
			aCreditReviewDetails.setRoleCode(getRole());
			aCreditReviewDetails.setNextRoleCode(nextRoleCode);
			aCreditReviewDetails.setNotesList(this.notesList);
			auditHeader = getAuditHeader(aCreditReviewDetails, tranType);

			String operationRefs = getServiceOperations(taskId, aCreditReviewDetails);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCreditReviewDetails, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCreditReviewDetails, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinCreditReviewDetails aCreditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail()
				.getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = creditApplicationReviewService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = creditApplicationReviewService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = creditApplicationReviewService.doApprove(auditHeader);

					if (aCreditReviewDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = creditApplicationReviewService.doReject(auditHeader);
					if (aCreditReviewDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CreditApplicationReviewDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_CreditApplicationReviewDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.finCreditReviewDetails), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finCreditReviewDetails.getDetailId());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinCreditReviewDetails aCreditReviewDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCreditReviewDetails.getBefImage(),
				aCreditReviewDetails);
		return new AuditHeader(String.valueOf(aCreditReviewDetails.getDetailId()), null, null, null, auditDetail,
				aCreditReviewDetails.getUserDetails(), getOverideMap());
	}

	/**
	 * This method for setting the list of the tabs.
	 */
	public void setTabs(boolean isEnquiry) {
		logger.debug("Entering");

		if (isEnquiry) {
			this.dataMap = this.creditReviewSummaryData.setDataMap(this.custID.getValue(), custIds,
					this.toYear.getValue(), noOfYears, this.custCtgCode, true, isEnquiry, extDataMap,
					listOfFinCreditRevCategory, new HashMap<>());
		} else if (maxAuditYear != null) {
			custIds.add(custID.longValue());
			this.dataMap = this.creditReviewSummaryData.setDataMap(this.custID.getValue(), custIds,
					Integer.parseInt(maxAuditYear), noOfYears, this.custCtgCode, true, isEnquiry, null,
					listOfFinCreditRevCategory, new HashMap<>());
		}
		if (this.dataMap.containsKey("lovDescCcyEditField")) {
			currFormatter = Integer.parseInt(this.dataMap.get("lovDescCcyEditField"));
		}
		for (FinCreditRevCategory fcrc : listOfFinCreditRevCategory) {
			long categoryId = fcrc.getCategoryId();
			String categoryDesc = fcrc.getCategoryDesc();
			CreditReviewSubCtgDetails creditReviewSubCtgDetails = new CreditReviewSubCtgDetails();
			creditReviewSubCtgDetails.setMainGroup("T");
			creditReviewSubCtgDetails.setMainGroupDesc(categoryDesc);
			creditReviewSubtgDetailsList.add(creditReviewSubCtgDetails);

			if (FacilityConstants.CREDITREVIEW_REMARKS.equals(fcrc.getRemarks())) {
				this.ratioFlag = false;
			} else {
				this.ratioFlag = true;
			}
			Tab tab = new Tab();
			tab.setId("tab_" + categoryId);
			tab.setLabel(categoryDesc);
			tab.setParent(this.tabsIndexCenter);
			Tabpanel tabPanel = new Tabpanel();
			tabPanel.setId("tabPanel_" + categoryId);
			tabPanel.setParent(this.tabpanelsBoxIndexCenter);
			render(fcrc, setListToTab("tabPanel_" + categoryId, tabPanel, fcrc));
		}
		// Code for Excel sheet on tab
		Tab tab = new Tab();
		tab.setId("tab_" + 5);// categoryid fix me
		tab.setLabel("Banking");
		tab.setParent(this.tabsIndexCenter);
		Tabpanel tabPanel = new Tabpanel();
		tabPanel.setId("tabPanel_" + 5);
		tabPanel.setParent(this.tabpanelsBoxIndexCenter);
		appendCreditReviewDetailTab(false, tabPanel);

		logger.debug("Leaving");
	}

	public void appendCreditReviewDetailTab(boolean onLoad, Tabpanel tabPanel) {
		logger.debug(Literal.ENTERING);

		final Map<String, Object> map = new HashMap<String, Object>();
		final Map<String, Object> btMap = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", financeMainBaseCtrl);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("isFinanceProcess", true);
		if (onLoad) {
			map.put("ccyFormatter",
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
		}
		map.put("creditReviewDetails", creditReviewDetails);
		map.put("creditReviewData", creditReviewData);
		map.put("externalLiabilities", extLiabilities);
		btMap.put("EXT_MNTHLY_OBL", this.dataMap.get("EXT_EMI_CNSRD"));

		ArrayList<Long> custIdsList = new ArrayList<>(custIds);
		custIdsList.add(0, this.custID.getValue());
		custIds = new LinkedHashSet<>(custIdsList);

		if (MapUtils.isNotEmpty(this.dataMap)) {
			btMap.put("DSCR_PBDIT",
					dataMap.containsKey("Y2_DEPRITIATION")
							? PennantApplicationUtil.amountFormate(new BigDecimal(this.dataMap.get("Y2_DEPRITIATION")),
									4)
							: "0");
			btMap.put("DSCR_PBDIT", dataMap.containsKey("Y2_DEPRITIATION") ? format(this.dataMap.get("Y2_DEPRITIATION"))
					: BigDecimal.ZERO);
			btMap.put("TOTAL_REVENUE",
					dataMap.containsKey("Y2_TOT_REV") ? this.dataMap.get("Y2_TOT_REV") : BigDecimal.ZERO);

			btMap.put("DSCR_GF",
					dataMap.containsKey("Y2_DSCR_GF") ? format(this.dataMap.get("Y2_DSCR_GF")) : BigDecimal.ZERO);
			btMap.put("CRNTRATIO",
					dataMap.containsKey("Y2_CRNT_RATIO") ? format(this.dataMap.get("Y2_CRNT_RATIO")) : BigDecimal.ZERO);
			btMap.put("DSCR_PBDIT",
					dataMap.containsKey("Y2_DSCR_PBDIT") ? format(this.dataMap.get("Y2_DSCR_PBDIT")).replace(",", "")
							: BigDecimal.ZERO);

			btMap.put("DEBTEQUITY", dataMap.containsKey("Y2_DEBT_EQUITY") ? format(this.dataMap.get("Y2_DEBT_EQUITY"))
					: BigDecimal.ZERO);
			btMap.put("ANNUAL_TURNOVER",
					dataMap.containsKey("Y2_SALES_OTHER_INCOME")
							? unFormat(new BigDecimal(this.dataMap.get("Y2_SALES_OTHER_INCOME")))
							: BigDecimal.ZERO);
			btMap.put("EMI_ALL_LOANS",
					dataMap.containsKey("Y2_EMI_12_ALL_LOANS") ? format(dataMap.get("Y2_EMI_12_ALL_LOANS"))
							: BigDecimal.ZERO);
			btMap.put("MARGINI",
					dataMap.containsKey("Y2_SM_MARGIN") ? unFormat(new BigDecimal(this.dataMap.get("Y2_SM_MARGIN")))
							: BigDecimal.ZERO);
		}
		custIds.remove(this.custID.getValue());
		map.put("btMap", btMap);

		map.put("userRole", getRole());
		map.put("isEditable", isReadOnly("FinanceMainDialog_EligibilitySal"));
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Spreadsheet.zul", tabPanel, map);

		logger.debug(Literal.LEAVING);
	}

	private String format(String data) {
		BigDecimal amount = BigDecimal.ZERO;
		if (StringUtils.isNotEmpty(data)) {
			amount = new BigDecimal(data);
		}

		return PennantApplicationUtil.formatAmount(amount, 2);
	}

	/**
	 * This Method for rendering with data
	 * 
	 * @param categoryId
	 * @param listbox
	 */
	public void render(FinCreditRevCategory finCreditRevCategory, Listbox listbox) {
		logger.debug("Entering");
		long categoryId = finCreditRevCategory.getCategoryId();
		Listitem item = null;
		Listcell lc = null;
		Listgroup lg = null;
		String mainCategory = "";

		String totAsst = "";
		String totLibNetWorth = "";
		if (!isEnquiry) {

			if (PennantConstants.PFF_CUSTCTG_SME.startsWith(finCreditReviewDetails.getCreditRevCode())) {
				totAsst = FacilityConstants.CREDITREVIEW_BANK_TOTASST;
				totLibNetWorth = FacilityConstants.CREDITREVIEW_BANK_TOTLIBNETWRTH;
			} else if (PennantConstants.PFF_CUSTCTG_CORP.startsWith(finCreditReviewDetails.getCreditRevCode())) {
				totAsst = FacilityConstants.CREDITREVIEW_CORP_TOTASST;
				totLibNetWorth = FacilityConstants.CREDITREVIEW_CORP_TOTLIBNETWRTH;
			}
		}

		List<FinCreditRevSubCategory> listOfFinCreditRevSubCategory = this.creditApplicationReviewService
				.getFinCreditRevSubCategoryByCategoryId(categoryId);
		for (int i = 0; i < listOfFinCreditRevSubCategory.size(); i++) {

			FinCreditRevSubCategory finCreditRevSubCategory = listOfFinCreditRevSubCategory.get(i);
			String subCategoryCode = finCreditRevSubCategory.getSubCategoryCode();
			String subCategoryItemType = finCreditRevSubCategory.getSubCategoryItemType();
			item = new Listitem();
			item.setStyle("background: none repeat scroll 0 0 #FFFFFF; font-size: 12px;");

			item.setId(String.valueOf("li" + subCategoryCode));

			CreditReviewSubCtgDetails creditReviewSubCtgDetails = new CreditReviewSubCtgDetails();

			if (!this.ratioFlag && !mainCategory.equals(finCreditRevSubCategory.getMainSubCategoryCode())) {
				mainCategory = finCreditRevSubCategory.getMainSubCategoryCode();
				lg = new Listgroup();
				lg.setId(mainCategory);
				if (!listbox.hasFellow(mainCategory)) {
					lg.setLabel(mainCategory);
					lg.setOpen(true);
					lg.setParent(listbox);
					lg.setStyle("font-weight:bold;font-weight:bold;background-color: #ADD8E6; font-size: 12px;");
				}
			}
			creditReviewSubCtgDetails.setTabDesc(finCreditRevCategory.getCategoryDesc());
			creditReviewSubCtgDetails.setMainGroupDesc(mainCategory);
			lc = new Listcell();
			lc.setStyle("border: 1px inset snow; font-size: 12px;");
			Label label1 = new Label();
			label1.setStyle("font-weight:bold; font-size: 12px;");
			if ("Calc".equals(subCategoryItemType) && this.ratioFlag) {
				creditReviewSubCtgDetails.setCalC("C");
				label1.setStyle("font-weight:bold; color:#000000; font-size: 12px;");
			}

			label1.setValue(String.valueOf(finCreditRevSubCategory.getSubCategoryDesc()));
			creditReviewSubCtgDetails.setSubCategoryDesc(String.valueOf(finCreditRevSubCategory.getSubCategoryDesc()));
			if (categoryId == 4 || categoryId == 7) {
				creditReviewSubCtgDetails.setRemarks(FacilityConstants.CREDITREVIEW_REMARKS);
				finCreditRevSubCategory.setRemarks(FacilityConstants.CREDITREVIEW_REMARKS);
			}
			label1.setParent(lc);
			lc.setParent(item);
			int noOfYears = finCreditRevCategory.getNoOfyears();
			for (int j = noOfYears; j >= 1; j--) {
				lc = new Listcell();
				lc.setStyle("text-align:right;border: 1px inset snow; font-size: 11px;");
				lc.setId("lcdb" + subCategoryCode + String.valueOf(year - j));
				Label valueLabel = new Label();
				valueLabel.setStyle("font-size: 11px;");
				if ("Calc".equals(subCategoryItemType) && this.ratioFlag) {
					valueLabel.setStyle("font-weight:bold; color:#000000; font-size: 11px;");
					if (finCreditRevSubCategory.isGrand()) {
						item.setStyle("background-color: #CCFF99; font-size: 11px;");
					} else {
						item.setStyle("background-color: #ADD8E6; font-size: 11px;");
					}
				}
				valueLabel.setId("db" + subCategoryCode + String.valueOf(year - j));

				int yearCount = noOfYears - j;
				if (subCategoryCode.equals(totAsst)) {
					switch (yearCount) {
					case 0:
						totAsstValue0 = new BigDecimal(dataMap.get("Y" + (noOfYears - j) + "_" + subCategoryCode));
						break;
					case 1:
						totAsstValue1 = new BigDecimal(dataMap.get("Y" + (noOfYears - j) + "_" + subCategoryCode));
						break;
					case 2:
						totAsstValue2 = new BigDecimal(dataMap.get("Y" + (noOfYears - j) + "_" + subCategoryCode));
						break;
					}
				} else if (subCategoryCode.equals(totLibNetWorth)) {
					switch (yearCount) {
					case 0:
						totLibNetWorthValue0 = new BigDecimal(
								dataMap.get("Y" + (noOfYears - j) + "_" + subCategoryCode));
						break;
					case 1:
						totLibNetWorthValue1 = new BigDecimal(
								dataMap.get("Y" + (noOfYears - j) + "_" + subCategoryCode));
						break;
					case 2:
						totLibNetWorthValue2 = new BigDecimal(
								dataMap.get("Y" + (noOfYears - j) + "_" + subCategoryCode));
						break;
					}
				}
				String value = "0";
				if (noOfYears == 3) {
					value = this.dataMap.get("Y" + (noOfYears - j) + "_" + subCategoryCode);
				} else {
					value = this.dataMap.get("Y" + (2) + "_" + subCategoryCode);
				}

				BigDecimal convrsnPrice = BigDecimal.ZERO;
				BigDecimal tempValue = new BigDecimal(value == null ? "0" : value);
				if (tempValue.compareTo(BigDecimal.ZERO) != 0) {
					if (this.conversionRate.getValue() != null) {
						convrsnPrice = PennantApplicationUtil.formateAmount(tempValue, this.currFormatter).divide(
								this.conversionRate.getValue(), FacilityConstants.CREDIT_REVIEW_USD_SCALE,
								RoundingMode.HALF_DOWN);
					} else if (isEnquiry) {
						if (creditReviewDetailsMap != null
								&& creditReviewDetailsMap.get(String.valueOf(year)) != null) {
							FinCreditReviewDetails finCreditReviewDetails = creditReviewDetailsMap
									.get(String.valueOf(year));
							convrsnPrice = PennantApplicationUtil.formateAmount(tempValue, this.currFormatter).divide(
									finCreditReviewDetails.getConversionRate(),
									FacilityConstants.CREDIT_REVIEW_USD_SCALE, RoundingMode.HALF_DOWN);
						}
					}
				}
				if (j == 3) {
					creditReviewSubCtgDetails
							.setYear1USDConvstn(getUsdConVersionValue(finCreditRevSubCategory, convrsnPrice));
				} else if (j == 2) {
					creditReviewSubCtgDetails
							.setYear2USDConvstn(getUsdConVersionValue(finCreditRevSubCategory, convrsnPrice));
				} else if (j == 1) {
					creditReviewSubCtgDetails
							.setYear3USDConvstn(getUsdConVersionValue(finCreditRevSubCategory, convrsnPrice));
				}
				try {

					if ("--".equals(value) || value == null
					// || !StringUtils.isNumeric(value)
					) {
						value = "--";
					} else if (finCreditRevSubCategory.isFormat()) {
						value = PennantApplicationUtil.amountFormate(new BigDecimal(value), this.currFormatter);
					} else if (finCreditRevSubCategory.isPercentCategory()) {
						value = PennantApplicationUtil.formatAmount(new BigDecimal(value).multiply(new BigDecimal(100)),
								2);
						value = value + " %";
					} else {
						value = PennantApplicationUtil.formatAmount(new BigDecimal(value), 2);
					}
				} catch (Exception e) {
					value = "--";
				}
				if (value.contains("-")) {
					valueLabel.setStyle("font-weight:bold;color:#f71111; font-size: 11px;");
				}
				valueLabel.setValue(value);

				if (j == 3) {
					creditReviewSubCtgDetails.setYera1AuditValue(value);
				} else if (j == 2) {
					creditReviewSubCtgDetails.setYera2AuditValue(value);
				} else if (j == 1) {
					creditReviewSubCtgDetails.setYera3AuditValue(value);
				}

				valueLabel.setParent(lc);
				lc.setParent(item);

				lc = new Listcell();
				lc.setStyle("text-align:right;border: 1px inset snow; font-size: 11px;");
				lc.setId("lcra" + subCategoryCode + String.valueOf(year - j));
				Label rLabel = new Label();
				rLabel.setStyle("font-size: 11px;");
				if ("Calc".equals(subCategoryItemType) && this.ratioFlag) {
					creditReviewSubCtgDetails.setCalC("C");
					rLabel.setStyle("font-weight:bold; color:#000000; font-size: 11px;");
				}
				rLabel.setId("rLabel" + subCategoryCode + String.valueOf(year - j));
				if (this.ratioFlag) {
					value = this.dataMap.get("RY" + (noOfYears - j) + "_" + subCategoryCode);
					if ("--".equals(value) || value == null) {
						value = "--";
					} else {
						value = PennantApplicationUtil.formatRate(Double.parseDouble(value), 2);
						value = value + " %";
					}
					if (value.contains("-")) {
						rLabel.setStyle("font-weight:bold;color:#f71111; font-size: 11px;");
					}
					rLabel.setValue(value);
					if (j == 3) {
						creditReviewSubCtgDetails.setYera1BreakDown(value);
					} else if (j == 2) {
						creditReviewSubCtgDetails.setYera2BreakDown(value);
					} else if (j == 1) {
						creditReviewSubCtgDetails.setYera3BreakDown(value);
					}
				} else {
					rLabel.setValue("0");
					if (j == 3) {
						creditReviewSubCtgDetails.setYera1BreakDown("0");
					} else if (j == 2) {
						creditReviewSubCtgDetails.setYera2BreakDown("0");
					} else if (j == 1) {
						creditReviewSubCtgDetails.setYera3BreakDown("0");
					}
				}
				rLabel.setParent(lc);
				lc.setParent(item);

				if (j != noOfYears) {
					lc = new Listcell();
					lc.setStyle("text-align:right;border: 1px inset snow; font-size: 11px;");
					lc.setId("lcdiff" + subCategoryCode + String.valueOf(year - j));
					Label diffLabel = new Label();
					diffLabel.setStyle("font-size: 10px;");
					if ("Calc".equals(subCategoryItemType) && this.ratioFlag) {
						creditReviewSubCtgDetails.setCalC("C");
						diffLabel.setStyle("font-weight:bold;color:#000000; font-size: 11px;");
					}
					diffLabel.setId("diffLabel" + subCategoryCode + String.valueOf(year - j));
					value = this.dataMap.get("CY" + (noOfYears - j) + "_" + subCategoryCode);
					if ("--".equals(value) || value == null) {
						value = "--";
						if (j == 2) {
							creditReviewSubCtgDetails.setYera12PerChange(value);
						} else if (j == 1) {
							creditReviewSubCtgDetails.setYera23PerChange(value);
						}
					} else {
						try {
							value = PennantApplicationUtil.formatRate(Double.parseDouble(value), 2);
							value = value + " %";
							if (j == 2) {
								creditReviewSubCtgDetails.setYera12PerChange(value);
							} else if (j == 1) {
								creditReviewSubCtgDetails.setYera23PerChange(value);
							}
						} catch (Exception e) {
							logger.error("Exception: ", e);
						}
					}
					if (value.contains("-")) {
						diffLabel.setStyle("font-weight:bold;color:#f71111; font-size: 11px;");
					}
					diffLabel.setValue(value);
					diffLabel.setParent(lc);
					lc.setParent(item);
				}

			}
			item.setAttribute("finData", finCreditRevSubCategory);
			item.setParent(listbox);
			creditReviewSubtgDetailsList.add(creditReviewSubCtgDetails);
		}
		listbox.setAttribute("ratio", ratioFlag);
		logger.debug("Leaving");
	}

	/**
	 * Method for USD Conversion
	 * 
	 * @param finCreditRevSubCategory
	 * @param convrsnPrice
	 * @return
	 */
	public String getUsdConVersionValue(FinCreditRevSubCategory finCreditRevSubCategory, BigDecimal convrsnPrice) {
		if (StringUtils.trimToEmpty(finCreditRevSubCategory.getRemarks())
				.equals(FacilityConstants.CREDITREVIEW_REMARKS)) {
			String subCategoryCode = StringUtils.trimToEmpty(finCreditRevSubCategory.getSubCategoryCode());
			if (subCategoryCode.equals(FacilityConstants.CORP_CRDTRVW_RATIOS_WRKCAP)
					|| subCategoryCode.equals(FacilityConstants.CORP_CRDTRVW_RATIOS_EBITDA4)
					|| subCategoryCode.equals(FacilityConstants.CORP_CRDTRVW_RATIOS_FCF)) {
				return PennantApplicationUtil.formatAmount(convrsnPrice, FacilityConstants.CREDIT_REVIEW_USD_SCALE);
			} else {
				return "";
			}
		} else {
			return PennantApplicationUtil.formatAmount(convrsnPrice, FacilityConstants.CREDIT_REVIEW_USD_SCALE);
		}
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_CreditApplicationReviewDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method for building the listbox with dynamic headers.<br>
	 * 
	 */
	public Listbox setListToTab(String tabId, Tabpanel tabPanel, FinCreditRevCategory fcrc) {
		logger.debug("Entering");
		long categoryId = fcrc.getCategoryId();
		String borderLayoutHt = getBorderLayoutHeight();
		Div div = new Div();
		div.setId("div_" + categoryId);
		div.setHeight(
				Integer.parseInt(borderLayoutHt.substring(0, borderLayoutHt.indexOf("px"))) - 100 - 40 - 20 + "px");
		Listbox listbox = new Listbox();
		listbox.setSpan(true);
		listbox.setHeight(
				Integer.parseInt(borderLayoutHt.substring(0, borderLayoutHt.indexOf("px"))) - 100 - 40 - 20 + "px");
		listbox.setId("lb_" + categoryId);

		Auxhead auxHead = new Auxhead();
		auxHead.setId("auxHead_" + categoryId);
		auxHead.setDraggable("true");

		Auxheader auxHeader_bankName = new Auxheader("");
		auxHeader_bankName.setColspan(1);
		auxHeader_bankName.setStyle("font-size: 14px");
		auxHeader_bankName.setParent(auxHead);
		auxHeader_bankName.setAlign("center");

		Listhead listHead = new Listhead();
		listHead.setId("listHead_" + categoryId);
		listHead.setStyle("background:#447294;color:white;");
		listHead.setSizable(true);

		Listheader listheader_bankName = new Listheader();
		// listheader_bankName.setLabel(Labels.getLabel("listheader_bankName.value",new
		// String[]{"Albaraka"}));
		listheader_bankName.setStyle("font-size: 12px");
		listheader_bankName.setHflex("min");
		listheader_bankName.setParent(listHead);

		CreditReviewSubCtgDetails creditReviewSubCtgDetailsHeader = new CreditReviewSubCtgDetails();
		int noOfYears = fcrc.getNoOfyears();
		for (int j = noOfYears; j >= 1; j--) {
			int prevAuditPeriod = creditApplicationReviewService.getCreditReviewAuditPeriodByAuditYear(
					this.custID.longValue(), String.valueOf(year - j + 1), 0, true, "_VIew");
			String prevPeriodLabel = "-" + String.valueOf(prevAuditPeriod) + FacilityConstants.MONTH;

			Auxheader auxHeader_audYearAndPeriod = new Auxheader();
			// setting colspan for AuxHead
			if (j == noOfYears) {
				auxHeader_audYearAndPeriod.setColspan(2);
			} else {
				auxHeader_audYearAndPeriod.setColspan(3);
			}

			if (categoryId == 3 || categoryId == 4 || categoryId == 7) {
				if (j == noOfYears) {
					auxHeader_audYearAndPeriod.setColspan(1);
				} else {
					auxHeader_audYearAndPeriod.setColspan(2);
				}
			}

			auxHeader_audYearAndPeriod.setStyle("font-size: 14px");
			auxHeader_audYearAndPeriod.setAlign("center");
			Listheader listheader_audAmt = new Listheader();
			switch (j) {
			case 3:
				creditReviewSubCtgDetailsHeader.setYera1AuditValueHeader(
						getAuditTypeLabel(String.valueOf(year - j + 1), prevPeriodLabel, true));
				listheader_audAmt.setLabel(getAuditTypeLabel(String.valueOf(year - j + 1), prevPeriodLabel, false));
				auxHeader_audYearAndPeriod.setLabel(String.valueOf(String.valueOf(year - j + 1) + prevPeriodLabel));
				creditReviewSubCtgDetailsHeader.setYera1BreakDownHeader(Labels.getLabel("listheader_breakDown.value"));
				break;
			case 2:
				creditReviewSubCtgDetailsHeader.setYera2AuditValueHeader(
						getAuditTypeLabel(String.valueOf(year - j + 1), prevPeriodLabel, true));
				listheader_audAmt.setLabel(getAuditTypeLabel(String.valueOf(year - j + 1), prevPeriodLabel, false));
				auxHeader_audYearAndPeriod.setLabel(String.valueOf(String.valueOf(year - j + 1) + prevPeriodLabel));
				creditReviewSubCtgDetailsHeader.setYera2BreakDownHeader(Labels.getLabel("listheader_breakDown.value"));
				break;
			case 1:
				creditReviewSubCtgDetailsHeader.setYera3AuditValueHeader(
						getAuditTypeLabel(String.valueOf(year - j + 1), prevPeriodLabel, true));
				listheader_audAmt.setLabel(getAuditTypeLabel(String.valueOf(year - j + 1), prevPeriodLabel, false));
				auxHeader_audYearAndPeriod.setLabel(String.valueOf(String.valueOf(year - j + 1) + prevPeriodLabel));
				creditReviewSubCtgDetailsHeader.setYera3BreakDownHeader(Labels.getLabel("listheader_breakDown.value"));
				break;
			}

			// FinCreditReviewDetails finCreditReviewDetails1 =
			// this.creditApplicationReviewService.getCreditReviewDetailsById(id);
			listheader_audAmt.setHflex("min");

			listheader_audAmt.setParent(listHead);
			auxHeader_audYearAndPeriod.setParent(auxHead);

			Listheader listheader_breakDown = new Listheader();
			listheader_breakDown.setLabel(Labels.getLabel("listheader_breakDown.value"));
			listheader_breakDown.setStyle("font-size: 12px");
			listheader_breakDown.setHflex("min");
			listheader_breakDown.setVisible(fcrc.isBrkdowndsply());
			listheader_breakDown.setParent(listHead);
			if (j != noOfYears) {
				Listheader listheader_diff = new Listheader();
				listheader_diff.setLabel(Labels.getLabel("listheader_diff.value"));
				listheader_diff.setHflex("min");

				if (j == 2) {
					creditReviewSubCtgDetailsHeader.setYera12PerChangeHeader(Labels.getLabel("listheader_diff.value",
							new String[] { String.valueOf(year - j - 1 + 1) + String.valueOf("/" + (year - j + 1)) }));
				} else if (j == 1) {
					creditReviewSubCtgDetailsHeader.setYera23PerChangeHeader(Labels.getLabel("listheader_diff.value",
							new String[] { String.valueOf(year - j - 1 + 1) + String.valueOf("/" + (year - j + 1)) }));
				}
				listheader_diff.setStyle("font-size: 12px");
				listheader_diff.setVisible(fcrc.isChangedsply());
				listheader_diff.setParent(listHead);

			}
		}

		auxHead.setParent(listbox);
		listHead.setParent(listbox);
		for (CreditReviewSubCtgDetails creditReviewSubCtgDetails : creditReviewSubtgDetailsList) {
			creditReviewSubCtgDetails.setCurrencyConvertion(AccountConstants.CURRENCY_USD);
			if ("T".equals(creditReviewSubCtgDetails.getMainGroup())) {

				creditReviewSubCtgDetails
						.setYera1AuditValueHeader(creditReviewSubCtgDetailsHeader.getYera1AuditValueHeader());
				creditReviewSubCtgDetails
						.setYera1BreakDownHeader(creditReviewSubCtgDetailsHeader.getYera1BreakDownHeader());

				creditReviewSubCtgDetails
						.setYera2AuditValueHeader(creditReviewSubCtgDetailsHeader.getYera2AuditValueHeader());
				creditReviewSubCtgDetails
						.setYera2BreakDownHeader(creditReviewSubCtgDetailsHeader.getYera2BreakDownHeader());

				creditReviewSubCtgDetails
						.setYera3AuditValueHeader(creditReviewSubCtgDetailsHeader.getYera3AuditValueHeader());
				creditReviewSubCtgDetails
						.setYera3BreakDownHeader(creditReviewSubCtgDetailsHeader.getYera3BreakDownHeader());

				creditReviewSubCtgDetails
						.setYera12PerChangeHeader(creditReviewSubCtgDetailsHeader.getYera12PerChangeHeader());
				creditReviewSubCtgDetails
						.setYera23PerChangeHeader(creditReviewSubCtgDetailsHeader.getYera23PerChangeHeader());

			}
		}

		// creditReviewSubtgDetailsList.add(creditReviewSubCtgDetailsHeader);
		// }
		listbox.setParent(div);
		div.setParent(tabPanel);
		logger.debug("Leaving");
		return listbox;

	}

	public String getAuditTypeLabel(String auditYear, String prevAudLabel, boolean isForReport) {
		logger.debug("Entering");

		FinCreditReviewDetails finCreditReviewDetails = this.creditApplicationReviewService
				.getCreditReviewDetailsByCustIdAndYear(this.custID.getValue(), auditYear,
						isEnquiry ? "_AVIEW" : "_VIEW");

		if (isEnquiry && finCreditReviewDetails != null) {
			if (creditReviewDetailsMap == null) {
				creditReviewDetailsMap = new HashMap<String, FinCreditReviewDetails>();
			}
			if (creditReviewDetailsMap.get(StringUtils.trimToEmpty(finCreditReviewDetails.getAuditYear())) == null) {
				creditReviewDetailsMap.put(StringUtils.trimToEmpty(finCreditReviewDetails.getAuditYear()),
						finCreditReviewDetails);
			}
		}

		String auditTypeLabel = "";
		if (isForReport) {
			auditTypeLabel = "AUD/Qual " + auditYear + "-0 Months";
			if (finCreditReviewDetails != null) {
				String qualOrUnQual = finCreditReviewDetails.isQualified() ? FacilityConstants.CREDITREVIEW_QUALIFIED
						: FacilityConstants.CREDITREVIEW_UNQUALIFIED;
				auditTypeLabel = finCreditReviewDetails.getAuditType() + "/" + qualOrUnQual + " " + auditYear
						+ prevAudLabel;
				logger.debug("Leaving");
			}
		} else {
			auditTypeLabel = "AUD/Qual ";
			if (finCreditReviewDetails != null) {
				String qualOrUnQual = finCreditReviewDetails.isQualified() ? FacilityConstants.CREDITREVIEW_QUALIFIED
						: FacilityConstants.CREDITREVIEW_UNQUALIFIED;
				auditTypeLabel = finCreditReviewDetails.getAuditType() + "/" + qualOrUnQual;
				logger.debug("Leaving");
			}
		}
		return auditTypeLabel;
	}

	/**
	 * onChange get the customer Details
	 * 
	 * @param event
	 */
	public void onChange$custCIF(Event event) {
		logger.debug("Entering" + event.toString());
		this.custCIF.clearErrorMessage();

		Customer customer = (Customer) PennantAppUtil.getCustomerObject(this.custCIF.getValue(), getFilterList());
		creditReviewSubtgDetailsList.clear();
		if (customer == null) {
			this.custShrtName.setValue("");
			this.custID.setValue(Long.valueOf(0));
			if (this.tabpanelsBoxIndexCenter.getChildren() != null) {
				this.tabpanelsBoxIndexCenter.getChildren().clear();
			}
			if (this.tabsIndexCenter.getChildren() != null) {
				this.tabsIndexCenter.getChildren().clear();
			}
			doClearMessage();
			if (StringUtils.isNotBlank(this.custCIF.getValue())) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value") }));
			}
		} else {
			doSetCustomer(customer, null);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Calling list Of existed Customers
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();

		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("filtersList", getFilterList());
		map.put("searchObject", this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * To fill Customer Details if Co-applicants is available
	 * 
	 * @param coAppIds
	 */
	public void doFillCustomerDetails(List<FinCreditReviewDetails> coAppIds) {
		logger.debug("Entering");

		Map<String, List<String>> map = new LinkedHashMap<>();

		// Separate Customer CIF wise audit years
		for (FinCreditReviewDetails finCreditReviewDetails : coAppIds) {
			List<String> list = new ArrayList<>();
			if (map.containsKey(finCreditReviewDetails.getLovDescCustCIF())) {
				list.addAll(map.get(finCreditReviewDetails.getLovDescCustCIF()));
				if (!list.contains(finCreditReviewDetails.getAuditYear())) {
					list.add(finCreditReviewDetails.getAuditYear());
					map.put(finCreditReviewDetails.getLovDescCustCIF(), list);
				}
			} else {
				list.add(finCreditReviewDetails.getAuditYear());
				map.put(finCreditReviewDetails.getLovDescCustCIF(), list);
			}
		}

		this.listBoxCust.getItems().clear();
		for (String custCIF : map.keySet()) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(custCIF);
			lc.setParent(item);
			String year = "";
			for (String str : map.get(custCIF)) {
				// Filtering un-used audit years
				if ((Integer.valueOf(maxAuditYear) - 2 <= Integer.valueOf(str))) {
					if (StringUtils.isNotBlank(year)) {
						year = year + ", " + str;
					} else {
						year = str;
					}
				}
			}
			lc = new Listcell(year);
			lc.setParent(item);
			this.listBoxCust.appendChild(item);
		}

		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) {
		logger.debug("Entering");
		final Customer aCustomer = (Customer) nCustomer;
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF());
		this.custCIF.setTooltiptext(aCustomer.getCustCIF().trim() + "-" + aCustomer.getCustShrtName());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		this.custCtgCode = aCustomer.getLovDescCustCtgType();
		this.listOfFinCreditRevCategory = this.creditApplicationReviewService
				.getCreditRevCategoryByCreditRevCode(this.custCtgCode);
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		logger.debug("Entering" + event.toString());
		if (isEnquiry) {
			closeDialog();
			final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
			final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
					.getFellow("tabBoxIndexCenter");
			tabbox.getSelectedTab().close();
		} else {
			closeDialog();
			creditApplicationReviewListCtrl.doReset();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method for selecting customer id from lov and after that setting sheet on bases of the customer type.<BR>
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		logger.debug("Entering" + event.toString());
		getSearch(true);
		logger.debug("Leaving" + event.toString());
	}

	public void getSearch(boolean isEnquiry) {
		ratioFlag = true;
		if (this.tabpanelsBoxIndexCenter.getChildren().size() > 0) {
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if (this.tabsIndexCenter.getChildren().size() > 0) {
			this.tabsIndexCenter.getChildren().clear();
		}
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (this.toYear.getValue() == null) {
				throw new WrongValueException(this.toYear, Labels.getLabel("FIELD_IS_MAND", new String[] {
						Labels.getLabel("label_CreditApplicationReviewDialog_NoOfYearsToDisplay.value") }));
			}
			if (this.toYear.getValue() > DateUtil.getYear(appldate)) {
				throw new WrongValueException(this.toYear, Labels.getLabel("DATE_NO_FUTURE"));
			}
			if (this.toYear.getValue() < DateUtil.getYear(DateUtil.addDays(appDftStrtDate, 1))) {
				throw new WrongValueException(this.toYear, Labels.getLabel("label_CreditReviewNotValidYear"));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custID.getValue() == null || this.custID.getValue() == 0) {
				if (StringUtils.isNotBlank(this.custCIF.getValue())) {
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
							new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value") }));
				}
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			logger.debug("Leaving");
			throw new WrongValuesException(wvea);
		}
		if (wve.size() == 0) {
			this.btnPrint.setVisible(true);
			this.year = this.toYear.getValue();
			setTabs(isEnquiry);
		}
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.toYear.clearErrorMessage();
		logger.debug("Leaving");
	}

	public String replaceYear(String formula, int year) {
		String formatedFormula = formula;
		for (int i = 0; i < this.noOfYears; i++) {
			if (i == 0) {
				formatedFormula = formatedFormula.replace("YN.", "Y" + year);
			} else {
				formatedFormula = formatedFormula.replace("YN-" + i + ".", "Y" + (year - i));
			}
		}
		return formatedFormula;
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.finCreditReviewDetails);
	}

	/*
	 * OnClick event For Button Print
	 */
	public void onClick$btnPrint(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doClearMessage();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (this.toYear.getValue() == null) {
				throw new WrongValueException(this.toYear, Labels.getLabel("FIELD_IS_MAND", new String[] {
						Labels.getLabel("label_CreditApplicationReviewDialog_NoOfYearsToDisplay.value") }));
			}
			if (this.toYear.getValue() > DateUtil.getYear(appldate)) {
				throw new WrongValueException(this.toYear, Labels.getLabel("DATE_NO_FUTURE"));
			}
			if (this.toYear.getValue() < DateUtil.getYear(DateUtil.addDays(appDftStrtDate, 1))) {
				throw new WrongValueException(this.toYear, Labels.getLabel("label_CreditReviewNotValidYear"));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custID.getValue() == null || this.custID.getValue() == 0) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			logger.debug("Leaving");
			throw new WrongValuesException(wvea);
		} else {
			CreditReviewMainCtgDetails creditReviewMainCtgDetails = new CreditReviewMainCtgDetails();
			creditReviewMainCtgDetails
					.setCustCIF(this.custCIF.getValue() + " - " + StringUtils.trimToEmpty(custShrtName.getValue()));
			creditReviewMainCtgDetails.setToYear(String.valueOf(this.toYear.getValue()));

			List<Object> list = new ArrayList<Object>();
			list.add(creditReviewSubtgDetailsList);

			String userName = getUserWorkspace().getLoggedInUser().getUserName();
			ReportsUtil.generateExcel("CreditApplication_Review_Enquiry", creditReviewMainCtgDetails, list, userName);

		}

		logger.debug("Leaving" + event.toString());
	}

	private List<Filter> getFilterList() {
		filterList = new ArrayList<Filter>();
		filterList.add(new Filter("lovDescCustCtgType",
				new String[] { PennantConstants.PFF_CUSTCTG_CORP, PennantConstants.PFF_CUSTCTG_SME }, Filter.OP_IN));
		return filterList;
	}

	public String getSavingStatus(String roleCode, String nextRoleCode, long custId, String moduleCode,
			String recordStatus) {
		String roleCodeDesc = "";
		if (StringUtils.isBlank(nextRoleCode) || roleCode.equals(nextRoleCode)
				|| StringUtils.trimToEmpty(recordStatus).equalsIgnoreCase(PennantConstants.RCD_STATUS_SAVED)) {
			return moduleCode + " with Customer ID: " + custId + " " + recordStatus + " Successfully.";
		} else {
			JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
			if (nextRoleCode.contains(",")) {
				String roleCodes[] = nextRoleCode.split(",");
				searchObject.addFilterIn("RoleCd", (Object) roleCodes);
			} else {
				searchObject.addFilterEqual("RoleCd", nextRoleCode);
			}
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<SecurityRole> rolesList = pagedListService.getBySearchObject(searchObject);
			if (rolesList != null && !rolesList.isEmpty()) {
				for (SecurityRole securityRole : rolesList) {
					if ("".equals(roleCodeDesc)) {
						roleCodeDesc = securityRole.getRoleDesc();
					} else {
						roleCodeDesc = roleCodeDesc + " And " + securityRole.getRoleDesc();
					}
				}
			}
			return moduleCode + " with Customer ID: " + custId + " Moved to "
					+ (StringUtils.isBlank(roleCodeDesc) ? "" : roleCodeDesc) + " Successfully.";
		}
	}

	public void setFinCreditReviewDetails(FinCreditReviewDetails finCreditReviewDetails) {
		this.finCreditReviewDetails = finCreditReviewDetails;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public List<Object> getFinBasicDetails() {
		return finBasicDetails;
	}

	public void setFinBasicDetails(ArrayList<Object> finBasicDetails) {
		this.finBasicDetails = finBasicDetails;
	}

	@Autowired
	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	@Autowired
	public void setCreditReviewSummaryData(CreditReviewSummaryData creditReviewSummaryData) {
		this.creditReviewSummaryData = creditReviewSummaryData;
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Autowired
	public void setCustomerBankInfoService(CustomerBankInfoService customerBankInfoService) {
		this.customerBankInfoService = customerBankInfoService;
	}

	@Autowired
	public void setCustomerExtLiabilityService(CustomerExtLiabilityService customerExtLiabilityService) {
		this.customerExtLiabilityService = customerExtLiabilityService;
	}

	@Autowired
	public void setCreditApplicationReviewDAO(CreditApplicationReviewDAO creditApplicationReviewDAO) {
		this.creditApplicationReviewDAO = creditApplicationReviewDAO;
	}

	@Autowired
	public void setIncomeExpenseDetailDAO(IncomeExpenseDetailDAO incomeExpenseDetailDAO) {
		this.incomeExpenseDetailDAO = incomeExpenseDetailDAO;
	}

}