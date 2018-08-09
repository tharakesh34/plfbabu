package com.pennant.webui.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
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
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditApplicationReviewDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
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
import com.pennant.util.ReportGenerationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CreditApplicationReviewEnquiryCtrl extends GFCBaseCtrl<FinCreditReviewDetails> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(CreditApplicationReviewEnquiryCtrl.class);

	protected Window 			window_CreditApplicationReviewDialog;  // autowired
	protected Borderlayout		borderlayout_CreditApplicationReview;  // autowired
	protected Grid 				creditApplicationReviewGrid;           // autowired
	protected Longbox 		 	custID; 						   	   // autowired
	protected Intbox  			toYear;                                // autowired
	protected Textbox 	 		custCIF;							   // autowired
	protected Label 			custShrtName;						   // autowired
	protected Groupbox 			gb_CreditReviwDetails;                 // autowired
	protected Tabbox 			tabBoxIndexCenter;                     // autowired
	protected Tabs 				tabsIndexCenter;                       // autowired
	protected Tabpanels 		tabpanelsBoxIndexCenter;               // autowired
	protected Button 			btnSearch;                             // autowired
	protected Button            btnPrint;                              // autowired
	
	// Customer Details
	protected Textbox 			bankName;                           // autowired
	protected Textbox 			auditors;                           // autowired
	protected Radiogroup 		conSolOrUnConsol;                   // autowired
	protected Radio		        conSolidated;                       // autowired
	protected Radio 		    unConsolidated;                     // autowired
	protected Textbox 			location;                           // autowired
	protected Textbox 			auditedYear;                        // autowired
	protected Datebox 			auditedDate;                        // autowired
	protected Decimalbox 		conversionRate;                     // autowired
	protected Longbox 	 		noOfShares;							// autowired
	protected CurrencyBox 	 	marketPrice;						// autowired
    protected Combobox          auditPeriod;                        // autowired
	protected Combobox          auditType;                          // autowired
	protected Radiogroup        qualifiedUnQualified;               // autowired
	protected Radio             qualRadio;                          // autowired
	protected Radio             unQualRadio;                        // autowired
	protected Textbox           lovDescFinCcyName;                  // autowired
	protected ExtendedCombobox  currencyType;                       // autowired
	
	protected Label label_CreditApplicationReviewDialog_NoOfYearsToDisplay; // autowired
	protected Row row1;   // autowired
	protected Row row2;   // autowired
	protected Row row3;   // autowired
	protected Row row4;   // autowired
	protected Row row5;   // autowired
	protected Row row6;   // autowired
	protected Row row7;   // autowired
	protected Row row8;   // autowired
	
    protected Label             label_CreditApplicationReviewDialog_RecordStatus;
    protected Groupbox gb_CustDetails;  // autowired
    protected Listbox listBoxCust;  // autowired
  

	protected Button btnSearchPRCustid; 			
	private JdbcSearchObject<Customer> newSearchObject ;
	private transient CreditApplicationReviewService creditApplicationReviewService;
	private transient CreditReviewSummaryData creditReviewSummaryData;
	private List<CreditReviewSubCtgDetails> creditReviewSubtgDetailsList = new ArrayList<CreditReviewSubCtgDetails>();
	private FinCreditReviewDetails            finCreditReviewDetails = null;
	private transient WorkFlowDetails	      workFlowDetails	= null;
	public  List<Notes>     notesList = new ArrayList<Notes>();
	private MailUtil mailUtil;
	private CreditApplicationReviewListCtrl creditApplicationReviewListCtrl = null;

	private List<FinCreditRevCategory> listOfFinCreditRevCategory = null;
	private int noOfYears = SysParamUtil.getValueAsInt("NO_OF_YEARS_TOSHOW");
	private int currFormatter;
	private Map<String,String> dataMap = null;
	private List<Filter> filterList = null;
	private int year;
	private boolean ratioFlag= true;
	private String custCtgCode = null;
	
	private boolean isEnquiry = true;
	
	private Map<String ,FinCreditReviewDetails> creditReviewDetailsMap;
	BigDecimal totAsstValue0 = BigDecimal.ZERO;
	BigDecimal totLibNetWorthValue0 = BigDecimal.ZERO;
	BigDecimal totAsstValue1 = BigDecimal.ZERO;
	BigDecimal totLibNetWorthValue1 = BigDecimal.ZERO;
	BigDecimal totAsstValue2 = BigDecimal.ZERO;
	BigDecimal totLibNetWorthValue2 = BigDecimal.ZERO;
	
	// create a script engine manager
	ScriptEngineManager factory = new ScriptEngineManager();
	// create a JavaScript engine
	ScriptEngine engine = factory.getEngineByName("JavaScript");
	
	protected Div 				div_CmdBtntoolbar;
	protected Div 				div_SearchBtntoolbar;
	protected Div 				divDel;
	protected Groupbox 			gb_basicDetails;
	Date appldate = DateUtility.getAppDate();
	Date appDftStrtDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
	String maxAuditYear=null;
	boolean showCurrentYear;
	int notesEnteredCount;
	int noOfRecords;
	private BigDecimal firstRepay = BigDecimal.ZERO;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal finAssetValue = BigDecimal.ZERO;
	private BigDecimal repayProfitRate = BigDecimal.ZERO;
	private int	roundingTarget = 0;
	private int numberOfTerms = 0;
	HashMap<String, String> extendedDataMap = new HashMap<String, String>();
	int finFormatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	
	private CustomerBankInfoService customerBankInfoService;
	private CustomerExtLiabilityService customerExtLiabilityService;
	
	private JountAccountDetailDAO jountAccountDetailDAO;
	private CreditApplicationReviewDAO creditApplicationReviewDAO;
	private String finReference;
	Set<Long> custIds = new HashSet<>();
	private List<FinCreditReviewDetails> auditYears;
	private List<JointAccountDetail> coAppIds =  new ArrayList<>();
	CustomerBankInfo customerBankInfo = null;
	BigDecimal sumOfEMI = BigDecimal.ZERO;
	
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

	private String unFormat(String amount){
		return PennantAppUtil.formateAmount(new BigDecimal(amount), finFormatter).toString();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CreditApplicationReviewDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CreditApplicationReviewDialog);

		try{

			if (arguments.containsKey("custCIF") && arguments.containsKey("custID") && arguments.containsKey("custCtgType")) {
				this.custID.setValue((Long) arguments.get("custID"));
				
				showCurrentYear = true;
				isEnquiry = false;
				this.custCIF.setValue((String) arguments.get("custCIF"));
				this.custCtgCode = (String) arguments.get("custCtgType");
				this.listOfFinCreditRevCategory = this.creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(this.custCtgCode);
				this.finReference = (String) arguments.get("finReference");
				this.maxAuditYear = getCreditApplicationReviewService().getMaxAuditYearByCustomerId(this.custID.longValue(), "_VIEW");
				
				//getting co-applicant id's
				coAppIds = jountAccountDetailDAO.getCustIdsByFinnRef(finReference);
				custIds.add(this.custID.getValue());
				
				//Adding co-applicant id's
				if (coAppIds != null && coAppIds.size() > 0) {
					for (JointAccountDetail jointAccountDetail : coAppIds) {
						custIds.add(jointAccountDetail.getCustID());
					}
				}
				
				//getting audit years from credit review details 
				auditYears = creditApplicationReviewDAO.getAuditYearsByCustId(custIds);
				customerBankInfo = customerBankInfoService.getSumOfAmtsCustomerBankInfoByCustId(custIds);
				sumOfEMI = customerExtLiabilityService.getSumAmtCustomerExtLiabilityById(custIds);
				custIds.remove(this.custID.getValue());
				
				//Fill Customer details from co-applicants 
				doFillCustomerDetails(auditYears);
				
				this.toYear.setValue(Integer.parseInt(maxAuditYear));
				year = this.toYear.getValue();
				if(arguments.containsKey("facility")){
					isEnquiry = true;
				}
				
				if(customerBankInfo != null){
					extendedDataMap.put("EXT_CREDITTRANNO",String.valueOf(customerBankInfo.getCreditTranNo()));
					extendedDataMap.put("EXT_CREDITTRANAMT",customerBankInfo.getCreditTranAmt().toString());
					extendedDataMap.put("EXT_CREDITTRANAVG",customerBankInfo.getCreditTranAvg().toString());
					extendedDataMap.put("EXT_DEBITTRANNO",String.valueOf(customerBankInfo.getDebitTranNo()));
					extendedDataMap.put("EXT_DEBITTRANAMT",customerBankInfo.getDebitTranAmt().toString());
					extendedDataMap.put("EXT_CASHDEPOSITNO",String.valueOf(customerBankInfo.getCashDepositNo()));
					extendedDataMap.put("EXT_CASHDEPOSITAMT",customerBankInfo.getCashDepositAmt().toString());
					extendedDataMap.put("EXT_CASHWITHDRAWALNO",String.valueOf(customerBankInfo.getCashWithdrawalNo()));
					extendedDataMap.put("EXT_CASHWITHDRAWALAMT",customerBankInfo.getCashWithdrawalAmt().toString());
					extendedDataMap.put("EXT_CHQDEPOSITNO",String.valueOf(customerBankInfo.getChqDepositNo()));
					extendedDataMap.put("EXT_CHQDEPOSITAMT",customerBankInfo.getChqDepositAmt().toString());
					extendedDataMap.put("EXT_CHQISSUENO",String.valueOf(customerBankInfo.getChqIssueNo()));
					extendedDataMap.put("EXT_CHQISSUEAMT",customerBankInfo.getChqIssueAmt().toString());
					extendedDataMap.put("EXT_INWARDCHQBOUNCENO",String.valueOf(customerBankInfo.getInwardChqBounceNo()));
					extendedDataMap.put("EXT_OUTWARDCHQBOUNCENO",String.valueOf(customerBankInfo.getOutwardChqBounceNo()));
					extendedDataMap.put("EXT_EODBALAVG",customerBankInfo.getEodBalAvg().toString());
					extendedDataMap.put("EXT_EODBALMAX",customerBankInfo.getEodBalMax().toString());
					extendedDataMap.put("EXT_EODBALMIN",customerBankInfo.getEodBalMin().toString());
					
				}
				
				extendedDataMap.put("EXT_OBLIGATION",unFormat(sumOfEMI == null ? "0" : sumOfEMI.toString()));
				if(arguments.containsKey("numberOfTerms")) {
					numberOfTerms = (int)arguments.get("numberOfTerms");
					extendedDataMap.put("EXT_NUMBEROFTERMS",String.valueOf(numberOfTerms));
				}
				if(arguments.containsKey("repayProfitRate")) {
					repayProfitRate = (BigDecimal)arguments.get("repayProfitRate");
					extendedDataMap.put("EXT_REPAYPROFITRATE",String.valueOf(repayProfitRate));
				}
				if(arguments.containsKey("roundingTarget")) {
					roundingTarget = (int)arguments.get("roundingTarget");
					extendedDataMap.put("EXT_ROUNDINGTARGET",String.valueOf(roundingTarget));
				}
				if(arguments.containsKey("finAssetValue")) {
					finAssetValue = (BigDecimal)arguments.get("finAssetValue");
					extendedDataMap.put("EXT_FINASSETVALUE",unFormat(finAssetValue == null ? "0" : finAssetValue.toString()));
				}
				if(arguments.containsKey("finAmount")) {
					finAmount = (BigDecimal)arguments.get("finAmount");
					extendedDataMap.put("EXT_FINAMOUNT",unFormat(finAmount == null ? "0" : finAmount.toString()));
				}
				if(arguments.containsKey("firstRepay")) {
					firstRepay = (BigDecimal)arguments.get("firstRepay");
					extendedDataMap.put("EXT_FIRSTREPAY",unFormat(firstRepay == null ? "0" : firstRepay.toString()));
				}
				setTabs(isEnquiry);
				getBorderLayoutHeight();
				this.div_CmdBtntoolbar.setVisible(false);
				this.div_SearchBtntoolbar.setVisible(false);
				this.gb_basicDetails.setVisible(false);
				if(arguments.containsKey("facility")){
					this.window_CreditApplicationReviewDialog.setHeight(this.borderLayoutHeight - 80 + "px");
				}
			} else {
				setDialog(DialogType.EMBEDDED);
				showCurrentYear = false;
			}
			
			// For Workflow
			if(arguments.containsKey("creditReviewDetails")) {
				if(arguments.containsKey("creditApplicationReviewListCtrl")){
					creditApplicationReviewListCtrl = (CreditApplicationReviewListCtrl) arguments.get("creditApplicationReviewListCtrl");
				}
				finCreditReviewDetails = (FinCreditReviewDetails) arguments.get("creditReviewDetails");
				
				String moduleMapCode = null;
				if(finCreditReviewDetails != null && 
						FacilityConstants.CREDIT_DIVISION_COMMERCIAL.equals(finCreditReviewDetails.getDivision())){
					moduleMapCode = "CommCreditAppReview";
				} else if(finCreditReviewDetails != null && 
						FacilityConstants.CREDIT_DIVISION_CORPORATE.equals(finCreditReviewDetails.getDivision())){
					moduleMapCode = "CorpCreditAppReview";
				}
				
				ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(moduleMapCode);
				isEnquiry = false;
				
				if(finCreditReviewDetails != null){
					maxAuditYear = getCreditApplicationReviewService().getMaxAuditYearByCustomerId(finCreditReviewDetails.getCustomerId(), "_VIEW");
					creditReviewDetailsMap = this.creditApplicationReviewService.getListCreditReviewDetailsByCustId(finCreditReviewDetails.getCustomerId(), 
							                          noOfYears,Integer.parseInt(maxAuditYear));
					finCreditReviewDetails = creditReviewDetailsMap.get(maxAuditYear);
					doWriteBeanToComponents(finCreditReviewDetails);
					this.custCIF.setValue(finCreditReviewDetails.getLovDescCustCIF());
					this.custID.setValue(finCreditReviewDetails.getCustomerId());
					this.custCtgCode = finCreditReviewDetails.getCreditRevCode();
					this.listOfFinCreditRevCategory = this.creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(this.custCtgCode);
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
				doLoadWorkFlow(this.finCreditReviewDetails.isWorkflow(), 
						this.finCreditReviewDetails.getWorkflowId(), finCreditReviewDetails.getNextTaskId());
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
				this.label_CreditApplicationReviewDialog_NoOfYearsToDisplay.setValue(Labels.getLabel("label_CreditApplicationReviewDialog_AuditedYear.value"));
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
	 * @param aCreditReviewDetails
	 *            (FinCreditReviewDetails)
	 * @throws Exception 
	 */
	
	
	public void doWriteBeanToComponents(FinCreditReviewDetails aCreditReviewDetails) throws Exception {
		logger.debug("Entering");
		this.custID.setValue(aCreditReviewDetails.getCustomerId());
		this.custCIF.setValue(aCreditReviewDetails.getLovDescCustCIF()!=null ? 	StringUtils.trimToEmpty(aCreditReviewDetails.getLovDescCustCIF()):"");
		this.custCIF.setTooltiptext(aCreditReviewDetails.getLovDescCustCIF()!=null ? 
				StringUtils.trimToEmpty(aCreditReviewDetails.getLovDescCustCIF()):"");
		this.custShrtName.setValue(aCreditReviewDetails.getLovDescCustShrtName());
		this.bankName.setValue(aCreditReviewDetails.getBankName());
		this.auditedDate.setValue(aCreditReviewDetails.getAuditedDate());
		this.auditedYear.setValue(aCreditReviewDetails.getAuditYear());
		this.currencyType.setValue(aCreditReviewDetails.getCurrency());
		
		this.currFormatter = CurrencyUtil.getFormat(aCreditReviewDetails.getCurrency());
		this.conversionRate.setFormat(PennantApplicationUtil.getAmountFormate(currFormatter));
		if(aCreditReviewDetails.getConversionRate() == null){
			BigDecimal converstnRate = PennantAppUtil.formateAmount(CalculationUtil.getConvertedAmount(this.currencyType.getValue(), AccountConstants.CURRENCY_USD,
					                     new BigDecimal(1000)), currFormatter);
			this.conversionRate.setValue(converstnRate);
		} else {
			this.conversionRate.setValue(aCreditReviewDetails.getConversionRate());
		}
		
		if(aCreditReviewDetails.isConsolidated()){
			this.conSolOrUnConsol.setSelectedIndex(0);
		} else  {
			this.conSolOrUnConsol.setSelectedIndex(1);
		}
		
		this.auditors.setValue(aCreditReviewDetails.getAuditors());
		this.location.setValue(aCreditReviewDetails.getLocation());	
		this.noOfShares.setValue(aCreditReviewDetails.getNoOfShares());
		this.marketPrice.setFormat(PennantApplicationUtil.getAmountFormate(CurrencyUtil.getFormat(aCreditReviewDetails.getCurrency())));
		this.marketPrice.setScale(CurrencyUtil.getFormat(aCreditReviewDetails.getCurrency()));
 		this.marketPrice.setValue(aCreditReviewDetails.getMarketPrice() == null ? BigDecimal.ZERO : aCreditReviewDetails.getMarketPrice());
		
		if(aCreditReviewDetails.getAuditPeriod() != 0){
			fillComboBox(this.auditPeriod, String.valueOf(aCreditReviewDetails.getAuditPeriod()), PennantStaticListUtil.getPeriodList(), "");
		}
		fillComboBox(this.auditType, StringUtils.trimToEmpty(aCreditReviewDetails.getAuditType()), 
				                       PennantStaticListUtil.getCreditReviewAuditTypesList(), "");
		if(aCreditReviewDetails.isQualified()){
			this.qualifiedUnQualified.setSelectedIndex(0);
		} else  {
			this.qualifiedUnQualified.setSelectedIndex(1);
		}
		doReadOnly();
		this.recordStatus.setValue(aCreditReviewDetails.getRecordStatus());
		logger.debug("Leaving");
	}
	
	public void doReadOnly(){
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
		//this.lovDescFinCcyName.setReadonly(true);     
		readOnlyComponent(true, this.currencyType);    
		logger.debug(" Leaving ");
	}
	
	public void doCheckRights(){
		logger.debug("Entering " );
		getUserWorkspace().allocateAuthorities("CreditApplicationReviewDialog", getRole());
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CreditApplicationReviewDialog_btnSave"));
		logger.debug("Leaving ");
	}
	
	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}
	
	public void doSave() throws Exception{
		logger.debug("Entering ");
		boolean isNew = false;
		String tranType = "";
        //int approvedRecordsCount = 0;
        notesEnteredCount = 0;
		Map<String ,List<FinCreditReviewSummary>> creditReviewSummaryMap;
		creditReviewSummaryMap = this.creditApplicationReviewService.getListCreditReviewSummaryByCustId(this.custID.getValue(), noOfYears,year, "_View");
		if(creditReviewDetailsMap == null){
			creditReviewDetailsMap = this.creditApplicationReviewService.getListCreditReviewDetailsByCustId(this.custID.getValue(), noOfYears,year);
		}
         
		List<FinCreditReviewDetails> listOfFinCreditReviewDetails = new  ArrayList<FinCreditReviewDetails>();
		for(int i=0;i<noOfYears;i++){
			if(!"Saved".equalsIgnoreCase(userAction.getSelectedItem().getValue().toString())){
				int yearCount = i;
				switch(yearCount){
				case 2 : if(totAsstValue0.compareTo(totLibNetWorthValue0) != 0){
						MessageUtil.showError(getMessage(year - 2, totAsstValue0, totLibNetWorthValue0));
					       return;
				         } 
				        break;
				case 1 : if(totAsstValue1.compareTo(totLibNetWorthValue1) != 0){
						MessageUtil.showError(getMessage(year - 1, totAsstValue1, totLibNetWorthValue1));
					        return;
				           } 
				        break;
				case 0 : if(totAsstValue2.compareTo(totLibNetWorthValue2) != 0){
						MessageUtil.showError(getMessage(year - 0, totAsstValue2, totLibNetWorthValue2));
					        return;
				         }
				        break;
				}
			}
			if(creditReviewDetailsMap != null && creditReviewDetailsMap.get(String.valueOf(year-i)) != null){
				FinCreditReviewDetails aFinCreditReviewDetails = (FinCreditReviewDetails) creditReviewDetailsMap.get(String.valueOf(year-i));
				if(creditReviewSummaryMap.get(String.valueOf(year-i)) != null){
					aFinCreditReviewDetails.setCreditReviewSummaryEntries(creditReviewSummaryMap.get(String.valueOf(year-i)));
					listOfFinCreditReviewDetails.add(aFinCreditReviewDetails);
				}
			}
		}

		//String ltstYrRcdStatus = finCreditReviewDetails.getRecordStatus();
		FinCreditReviewDetails ltstFinCreditReviewDetails = new FinCreditReviewDetails();
		BeanUtils.copyProperties(this.finCreditReviewDetails, ltstFinCreditReviewDetails);
		//noOfRecords = listOfFinCreditReviewDetails.size();
		for(FinCreditReviewDetails aCreditReviewDetails : listOfFinCreditReviewDetails){
			if(!"Approved".equalsIgnoreCase(aCreditReviewDetails.getRecordStatus())){
				noOfRecords++;
			}
		}
		
		int proRecordCount = 0;
		for(FinCreditReviewDetails aCreditReviewDetails : listOfFinCreditReviewDetails){
			
			if(!"Approved".equalsIgnoreCase(aCreditReviewDetails.getRecordStatus())){
				// for cancellation we are processing latest credit review details only
				if("Cancelled".equalsIgnoreCase(userAction.getSelectedItem().getValue().toString())){
					if(!aCreditReviewDetails.getAuditYear().equals(ltstFinCreditReviewDetails.getAuditYear())){
						continue;
					}
				}
				/*if(!StringUtils.trimToEmpty(aCreditReviewDetails.getRecordStatus()).equals("") 
						&& !aCreditReviewDetails.getRecordStatus().equals(ltstYrRcdStatus)){
					MessageUtil.showErrorMessage(aCreditReviewDetails.getAuditYear()+" Record  in "+aCreditReviewDetails.getRecordStatus()
							+" State Please Process It To "+ltstYrRcdStatus+" State");
					return;
				}*/
				isNew = aCreditReviewDetails.isNew();
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
					if(creditReviewSummaryMap.get(aCreditReviewDetails.getAuditYear()) != null){
						List<FinCreditReviewSummary> finCreditReviewSummaryList = creditReviewSummaryMap.get(aCreditReviewDetails.getAuditYear());
						for(FinCreditReviewSummary finCreditReviewSummary : finCreditReviewSummaryList){
							finCreditReviewSummary.setRecordType(ltstFinCreditReviewDetails.getRecordType());
							finCreditReviewSummary.setNewRecord(ltstFinCreditReviewDetails.isNew());
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
		
		if(listOfFinCreditReviewDetails != null && listOfFinCreditReviewDetails.size() > 0
				&& noOfRecords == proRecordCount){
			//Mail Alert Notification for User
			mailUtil.sendNotifications(NotificationConstants.MAIL_MODULE_CREDIT, listOfFinCreditReviewDetails.get(0));
			
			FinCreditReviewDetails creditReviewDetails = listOfFinCreditReviewDetails.get(0);
			String msg = getSavingStatus(creditReviewDetails.getRoleCode(), creditReviewDetails.getNextRoleCode(), creditReviewDetails.getCustomerId(),
					"Credit Review", creditReviewDetails.getRecordStatus());
			Clients.showNotification(msg,  "info", null, null, -1);
		}
		
		if(proRecordCount != 0){	
		    closeDialog();
			creditApplicationReviewListCtrl.doReset();
		}
		logger.debug("Leaving");
	}
	
	/*
	 * for preparing message 
	 */
	public String getMessage(int year, BigDecimal totLaiblts, BigDecimal totNetWrth){
		return "Total Assets and Total Liabilities & Net Worth not Matched for The Year "
	           +year+" Difference is : "+totLaiblts.subtract(totNetWrth);   
	}
	
	private boolean doProcess(FinCreditReviewDetails aCreditReviewDetails, String tranType) {
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
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinCreditReviewDetails aCreditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCreditApplicationReviewService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCreditApplicationReviewService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCreditApplicationReviewService().doApprove(auditHeader);

						if (aCreditReviewDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCreditApplicationReviewService().doReject(auditHeader);
						if (aCreditReviewDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"), null));
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
		@Override
		protected String getReference() {
			return String.valueOf(this.finCreditReviewDetails.getDetailId());
		}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CreditApplicationReviewDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinCreditReviewDetails aCreditReviewDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCreditReviewDetails.getBefImage(), aCreditReviewDetails);
		return new AuditHeader(String.valueOf(aCreditReviewDetails.getDetailId()), 
				null, null, null, auditDetail, aCreditReviewDetails.getUserDetails(), getOverideMap());
	}
	

	/**
	 * This method for setting the list of the tabs.<br>
	 * @throws Exception
	 */
	public void setTabs(boolean isEnquiry) throws Exception{
		logger.debug("Entering");
		
		if(isEnquiry){
			this.dataMap = this.creditReviewSummaryData.setDataMap(this.custID.getValue(), custIds, this.toYear.getValue(), noOfYears, this.custCtgCode, true, isEnquiry, extendedDataMap, listOfFinCreditRevCategory);
		} else if(maxAuditYear != null){
			custIds.add(custID.longValue());
			this.dataMap = this.creditReviewSummaryData.setDataMap(this.custID.getValue(), custIds, Integer.parseInt(maxAuditYear), noOfYears, this.custCtgCode, true, isEnquiry, null, listOfFinCreditRevCategory);
		}
		if(this.dataMap.containsKey("lovDescCcyEditField")){
			currFormatter = Integer.parseInt(this.dataMap.get("lovDescCcyEditField"));
		}
		for(FinCreditRevCategory fcrc:listOfFinCreditRevCategory){
			CreditReviewSubCtgDetails creditReviewSubCtgDetails = new CreditReviewSubCtgDetails();
			creditReviewSubCtgDetails.setMainGroup("T");
			creditReviewSubCtgDetails.setMainGroupDesc(fcrc.getCategoryDesc());
			creditReviewSubtgDetailsList.add(creditReviewSubCtgDetails);
			
			if(FacilityConstants.CREDITREVIEW_REMARKS.equals(fcrc.getRemarks())){
				this.ratioFlag = false;
			}else{
				this.ratioFlag = true;
			}
			Tab tab = new Tab();
			tab.setId("tab_"+fcrc.getCategoryId());
			tab.setLabel(fcrc.getCategoryDesc());
			tab.setParent(this.tabsIndexCenter);
			Tabpanel tabPanel = new Tabpanel();	
			tabPanel.setId("tabPanel_"+fcrc.getCategoryId());
			tabPanel.setParent(this.tabpanelsBoxIndexCenter);
			render(fcrc,setListToTab("tabPanel_"+fcrc.getCategoryId(), tabPanel, fcrc));
		}
		logger.debug("Leaving");
	}
 
	/**
	 * This Method for rendering with data
	 * @param categoryId
	 * @param listbox
	 * @throws Exception
	 */
	public void render(FinCreditRevCategory finCreditRevCategory,Listbox listbox) throws Exception {
		logger.debug("Entering");
		long categoryId = finCreditRevCategory.getCategoryId();
		Listitem item = null;
		Listcell lc = null;
		Listgroup lg = null;
		String mainCategory = "";
		
		String totAsst = "";
		String totLibNetWorth = "";
		if(!isEnquiry){

			if(PennantConstants.PFF_CUSTCTG_SME.startsWith(finCreditReviewDetails.getCreditRevCode())){
				totAsst = FacilityConstants.CREDITREVIEW_BANK_TOTASST;
				totLibNetWorth = FacilityConstants.CREDITREVIEW_BANK_TOTLIBNETWRTH; 
			} else if(PennantConstants.PFF_CUSTCTG_CORP.startsWith(finCreditReviewDetails.getCreditRevCode())){
				totAsst = FacilityConstants.CREDITREVIEW_CORP_TOTASST;
				totLibNetWorth = FacilityConstants.CREDITREVIEW_CORP_TOTLIBNETWRTH; 	
			}
		}
		
		List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategory= this.creditApplicationReviewService.
		getFinCreditRevSubCategoryByCategoryId(categoryId);
		for(int i =0 ;i<listOfFinCreditRevSubCategory.size();i++){
			
			FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(i);
			if(finCreditRevSubCategory.getSubCategoryCode().equals("TOT_OPR_INM_RTO") || 
					finCreditRevSubCategory.getSubCategoryCode().equals("GRS_PRFT_TOI_PER_RTO")){
				System.out.println("TOT_OPR_INM_RTO");
			}
			item = new Listitem();
			item.setStyle("background: none repeat scroll 0 0 #FFFFFF; font-size: 12px;");

			item.setId(String.valueOf("li"+finCreditRevSubCategory.getSubCategoryCode()));

			CreditReviewSubCtgDetails creditReviewSubCtgDetails = new CreditReviewSubCtgDetails();
			
			if(!this.ratioFlag && !mainCategory.equals(finCreditRevSubCategory.getMainSubCategoryCode())){
				mainCategory = finCreditRevSubCategory.getMainSubCategoryCode();
				lg =  new Listgroup();
				lg.setId(mainCategory);
				if(!listbox.hasFellow(mainCategory)){
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
			Label label1  = new Label();
			label1.setStyle("font-weight:bold; font-size: 12px;");
			if("Calc".equals(finCreditRevSubCategory.getSubCategoryItemType()) && this.ratioFlag){
				creditReviewSubCtgDetails.setCalC("C");
				label1.setStyle("font-weight:bold; color:#000000; font-size: 12px;");
			}

			label1.setValue(String.valueOf(finCreditRevSubCategory.getSubCategoryDesc()));
			creditReviewSubCtgDetails.setSubCategoryDesc(String.valueOf(finCreditRevSubCategory.getSubCategoryDesc()));
			if(categoryId == 4 || categoryId == 7){
				creditReviewSubCtgDetails.setRemarks(FacilityConstants.CREDITREVIEW_REMARKS);
				finCreditRevSubCategory.setRemarks(FacilityConstants.CREDITREVIEW_REMARKS);
			}
			label1.setParent(lc);
			lc.setParent(item);
			for(int j=noOfYears;j>=1;j--){				
				lc = new Listcell();
				lc.setStyle("text-align:right;border: 1px inset snow; font-size: 11px;");
				lc.setId("lcdb"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
				Label valueLabel= new Label();
				valueLabel.setStyle("font-size: 11px;");
				if("Calc".equals(finCreditRevSubCategory.getSubCategoryItemType()) && this.ratioFlag){
					valueLabel.setStyle("font-weight:bold; color:#000000; font-size: 11px;");
					if(finCreditRevSubCategory.isGrand()){
						item.setStyle("background-color: #CCFF99; font-size: 11px;");
					} else{
						item.setStyle("background-color: #ADD8E6; font-size: 11px;");
					}
				}
				valueLabel.setId("db"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
				
				int yearCount = noOfYears-j;
				if(finCreditRevSubCategory.getSubCategoryCode().equals(totAsst)){
					switch(yearCount){
					case 0 : totAsstValue0 = new BigDecimal(dataMap.get("Y"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
					break;
					case 1 : totAsstValue1 = new BigDecimal(dataMap.get("Y"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
					break;
					case 2 : totAsstValue2 = new BigDecimal(dataMap.get("Y"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
					break;
					}
				} else if(finCreditRevSubCategory.getSubCategoryCode().equals(totLibNetWorth)){
					switch(yearCount){
					case 0 : totLibNetWorthValue0 = new BigDecimal(dataMap.get("Y"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
					break;
					case 1 : totLibNetWorthValue1 = new BigDecimal(dataMap.get("Y"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
					break;
					case 2 : totLibNetWorthValue2 = new BigDecimal(dataMap.get("Y"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
					break;
					}
				}
				String value = this.dataMap.get("Y"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode());
				
				BigDecimal convrsnPrice = BigDecimal.ZERO;
				BigDecimal tempValue = new BigDecimal(value == null ? "0" : value);
				if(tempValue.compareTo(BigDecimal.ZERO) != 0){
					if(this.conversionRate.getValue() != null){
						convrsnPrice = PennantAppUtil.formateAmount(tempValue,this.currFormatter)
								.divide(this.conversionRate.getValue(), FacilityConstants.CREDIT_REVIEW_USD_SCALE, RoundingMode.HALF_DOWN);
					}else if(isEnquiry){
						if(creditReviewDetailsMap != null && creditReviewDetailsMap.get(String.valueOf(year)) != null){
							FinCreditReviewDetails finCreditReviewDetails = creditReviewDetailsMap.get(String.valueOf(year));
								convrsnPrice = PennantAppUtil.formateAmount(tempValue,this.currFormatter)
										.divide(finCreditReviewDetails.getConversionRate(), FacilityConstants.CREDIT_REVIEW_USD_SCALE, RoundingMode.HALF_DOWN);
						}
					}
				}
				if(j==3){
					creditReviewSubCtgDetails.setYear1USDConvstn(getUsdConVersionValue(finCreditRevSubCategory,convrsnPrice));
				}else if(j==2) {
					creditReviewSubCtgDetails.setYear2USDConvstn(getUsdConVersionValue(finCreditRevSubCategory,convrsnPrice));
				}else if(j==1){
					creditReviewSubCtgDetails.setYear3USDConvstn(getUsdConVersionValue(finCreditRevSubCategory,convrsnPrice));
				}
			try{
            	
				if("--".equals(value) || value == null 
						//|| !StringUtils.isNumeric(value)
						) {
					value = "--";
				}else if(finCreditRevSubCategory.isFormat()){
					value = PennantAppUtil.amountFormate(new BigDecimal(value), this.currFormatter);
				}else if(finCreditRevSubCategory.isPercentCategory()) {
					value = PennantAppUtil.formatAmount(new BigDecimal(value).multiply(new BigDecimal(100)), 2,false);
					value = value + " %";
				}else{
					value =  PennantAppUtil.formatAmount(new BigDecimal(value), 2,false);
				}
             }catch (Exception e) {
         	     value = "--";
             } 
				valueLabel.setValue(value);
				
				if(j==3){
					creditReviewSubCtgDetails.setYera1AuditValue(value);
				} else if(j==2) {
					creditReviewSubCtgDetails.setYera2AuditValue(value);
				} else if(j==1){
					creditReviewSubCtgDetails.setYera3AuditValue(value);
				}
				
				valueLabel.setParent(lc);
				lc.setParent(item);

				lc = new Listcell();
				lc.setStyle("text-align:right;border: 1px inset snow; font-size: 11px;");
				lc.setId("lcra"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
				Label rLabel = new Label(); 
				rLabel.setStyle("font-size: 11px;");
				if("Calc".equals(finCreditRevSubCategory.getSubCategoryItemType()) && this.ratioFlag){
					creditReviewSubCtgDetails.setCalC("C");
					rLabel.setStyle("font-weight:bold; color:#000000; font-size: 11px;");
				}
				rLabel.setId("rLabel"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
				if(this.ratioFlag){
					value = this.dataMap.get("RY"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode());
					if("--".equals(value) || value == null) {
						value = "--";
					}else {
						value = PennantApplicationUtil.formatRate(Double.parseDouble(value), 2);
						value = value + " %";
					}	
					rLabel.setValue(value);
					if(j==3){
						creditReviewSubCtgDetails.setYera1BreakDown(value);
					} else if(j==2) {
						creditReviewSubCtgDetails.setYera2BreakDown(value);
					} else if(j==1){
						creditReviewSubCtgDetails.setYera3BreakDown(value);
					}
				}else{
					rLabel.setValue("0");
					if(j==3){
						creditReviewSubCtgDetails.setYera1BreakDown("0");
					} else if(j==2) {
						creditReviewSubCtgDetails.setYera2BreakDown("0");
					} else if(j==1){
						creditReviewSubCtgDetails.setYera3BreakDown("0");
					}
				}
				rLabel.setParent(lc);
				lc.setParent(item);

				if(j != noOfYears){
					lc = new Listcell();
					lc.setStyle("text-align:right;border: 1px inset snow; font-size: 11px;");
					lc.setId("lcdiff"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
					Label diffLabel = new Label(); 
					diffLabel.setStyle("font-size: 10px;");
					if("Calc".equals(finCreditRevSubCategory.getSubCategoryItemType()) && this.ratioFlag){
						creditReviewSubCtgDetails.setCalC("C");
						diffLabel.setStyle("font-weight:bold;color:#000000; font-size: 11px;");
					}
					diffLabel.setId("diffLabel"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
					value = this.dataMap.get("CY"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode());
					if("--".equals(value) || value == null) {
						value = "--";
						if(j==2){
							creditReviewSubCtgDetails.setYera12PerChange(value);
						} else if(j ==1){
							creditReviewSubCtgDetails.setYera23PerChange(value);
						}
					}else {
						try{
						value = PennantApplicationUtil.formatRate(Double.parseDouble(value), 2);
						value = value + " %";
						if(j==2){
							creditReviewSubCtgDetails.setYera12PerChange(value);
						} else if(j ==1){
							creditReviewSubCtgDetails.setYera23PerChange(value);
						}
						} catch(Exception e){
							logger.error("Exception: ", e);
						}
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

	/** Method for USD Conversion	
	 * @param finCreditRevSubCategory
	 * @param convrsnPrice
	 * @return
	 */
	public String getUsdConVersionValue(FinCreditRevSubCategory finCreditRevSubCategory, BigDecimal convrsnPrice){
		if(StringUtils.trimToEmpty(finCreditRevSubCategory.getRemarks()).equals(FacilityConstants.CREDITREVIEW_REMARKS)){
			String subCategoryCode = StringUtils.trimToEmpty(finCreditRevSubCategory.getSubCategoryCode());
			if(subCategoryCode.equals(FacilityConstants.CORP_CRDTRVW_RATIOS_WRKCAP) ||
					subCategoryCode.equals(FacilityConstants.CORP_CRDTRVW_RATIOS_EBITDA4) ||
					subCategoryCode.equals(FacilityConstants.CORP_CRDTRVW_RATIOS_FCF)){
				return PennantAppUtil.formatAmount(convrsnPrice, FacilityConstants.CREDIT_REVIEW_USD_SCALE,false);
			} else {
				return "";
			}
		} else {
			return PennantAppUtil.formatAmount(convrsnPrice, FacilityConstants.CREDIT_REVIEW_USD_SCALE,false);
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
	public Listbox setListToTab(String tabId,Tabpanel tabPanel,FinCreditRevCategory fcrc){
		logger.debug("Entering");
		Div div = new Div();
		div.setId("div_"+fcrc.getCategoryId());
		div.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 100 - 40-20 + "px");
		Listbox listbox = new Listbox();
		listbox.setSpan(true);
		listbox.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 100 - 40-20 + "px");
		listbox.setId("lb_"+fcrc.getCategoryId());
		
		Auxhead auxHead = new Auxhead();
		auxHead.setId("auxHead_"+fcrc.getCategoryId());
		auxHead.setDraggable("true");
		
		Auxheader auxHeader_bankName = new Auxheader("");
		auxHeader_bankName.setColspan(1);
		auxHeader_bankName.setStyle("font-size: 14px");
		auxHeader_bankName.setParent(auxHead);
		auxHeader_bankName.setAlign("center");
		
		Listhead listHead = new Listhead();
		listHead.setId("listHead_"+fcrc.getCategoryId());
		listHead.setStyle("background:#447294;color:white;");
		listHead.setSizable(true);
		
		Listheader listheader_bankName = new Listheader();
		//listheader_bankName.setLabel(Labels.getLabel("listheader_bankName.value",new String[]{"Albaraka"}));
		listheader_bankName.setStyle("font-size: 12px");
		listheader_bankName.setHflex("min");
		listheader_bankName.setParent(listHead);

		CreditReviewSubCtgDetails creditReviewSubCtgDetailsHeader = new CreditReviewSubCtgDetails();

		for(int j=noOfYears;j>=1;j--){
			int prevAuditPeriod =getCreditApplicationReviewService().getCreditReviewAuditPeriodByAuditYear(this.custID.longValue(), 
					String.valueOf(year-j+1), 0, true, "_VIew");
			String prevPeriodLabel = "-"+String.valueOf(prevAuditPeriod)+FacilityConstants.MONTH;

			Auxheader auxHeader_audYearAndPeriod = new Auxheader();
			// setting colspan for AuxHead
			if(j == noOfYears){
				auxHeader_audYearAndPeriod.setColspan(2);
			} else {
				auxHeader_audYearAndPeriod.setColspan(3);
			}

			if(fcrc.getCategoryId() == 3 || fcrc.getCategoryId() == 4 || fcrc.getCategoryId() == 7){
				if(j == noOfYears){
					auxHeader_audYearAndPeriod.setColspan(1);
				} else {
					auxHeader_audYearAndPeriod.setColspan(2);
				}
			}
			
			auxHeader_audYearAndPeriod.setStyle("font-size: 14px");
			auxHeader_audYearAndPeriod.setAlign("center");
			Listheader listheader_audAmt = new Listheader();
			switch(j){
			    case 3 : creditReviewSubCtgDetailsHeader.setYera1AuditValueHeader(getAuditTypeLabel(String.valueOf(year-j+1) ,prevPeriodLabel, true));
				         listheader_audAmt.setLabel(getAuditTypeLabel(String.valueOf(year-j+1) ,prevPeriodLabel, false));
				         auxHeader_audYearAndPeriod.setLabel(String.valueOf(String.valueOf(year-j+1)+prevPeriodLabel));
				         creditReviewSubCtgDetailsHeader.setYera1BreakDownHeader(Labels.getLabel("listheader_breakDown.value"));
				         break;
			    case 2: creditReviewSubCtgDetailsHeader.setYera2AuditValueHeader(getAuditTypeLabel(String.valueOf(year-j+1),prevPeriodLabel, true));
				        listheader_audAmt.setLabel(getAuditTypeLabel(String.valueOf(year-j+1) ,prevPeriodLabel, false));
				        auxHeader_audYearAndPeriod.setLabel(String.valueOf(String.valueOf(year-j+1)+prevPeriodLabel));
				        creditReviewSubCtgDetailsHeader.setYera2BreakDownHeader(Labels.getLabel("listheader_breakDown.value"));
				        break;
			    case 1: creditReviewSubCtgDetailsHeader.setYera3AuditValueHeader(getAuditTypeLabel(String.valueOf(year-j+1),prevPeriodLabel, true));
				        listheader_audAmt.setLabel(getAuditTypeLabel(String.valueOf(year-j+1) ,prevPeriodLabel, false));
				        auxHeader_audYearAndPeriod.setLabel(String.valueOf(String.valueOf(year-j+1)+prevPeriodLabel));
				        creditReviewSubCtgDetailsHeader.setYera3BreakDownHeader(Labels.getLabel("listheader_breakDown.value"));
				        break;
			      }

			//FinCreditReviewDetails finCreditReviewDetails1 = this.creditApplicationReviewService.getCreditReviewDetailsById(id);
			listheader_audAmt.setHflex("min");

			listheader_audAmt.setParent(listHead);
			auxHeader_audYearAndPeriod.setParent(auxHead);

			Listheader listheader_breakDown= new Listheader();
			listheader_breakDown.setLabel(Labels.getLabel("listheader_breakDown.value"));
			listheader_breakDown.setStyle("font-size: 12px");
			listheader_breakDown.setHflex("min");
			listheader_breakDown.setVisible(fcrc.isBrkdowndsply());
			listheader_breakDown.setParent(listHead);
			if(j!= noOfYears){
				Listheader listheader_diff= new Listheader();
				listheader_diff.setLabel(Labels.getLabel("listheader_diff.value"));
				listheader_diff.setHflex("min");

				if(j==2){
					creditReviewSubCtgDetailsHeader.setYera12PerChangeHeader(Labels.getLabel("listheader_diff.value",
							new String[]{String.valueOf(year-j-1+1)+String.valueOf("/"+(year-j+1))}));
				} else if(j ==1){
					creditReviewSubCtgDetailsHeader.setYera23PerChangeHeader(Labels.getLabel("listheader_diff.value",
							new String[]{String.valueOf(year-j-1+1)+String.valueOf("/"+(year-j+1))}));
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
			if("T".equals(creditReviewSubCtgDetails.getMainGroup())){
				
				creditReviewSubCtgDetails.setYera1AuditValueHeader(creditReviewSubCtgDetailsHeader.getYera1AuditValueHeader());
				creditReviewSubCtgDetails.setYera1BreakDownHeader(creditReviewSubCtgDetailsHeader.getYera1BreakDownHeader());
				
				creditReviewSubCtgDetails.setYera2AuditValueHeader(creditReviewSubCtgDetailsHeader.getYera2AuditValueHeader());
				creditReviewSubCtgDetails.setYera2BreakDownHeader(creditReviewSubCtgDetailsHeader.getYera2BreakDownHeader());
				
				creditReviewSubCtgDetails.setYera3AuditValueHeader(creditReviewSubCtgDetailsHeader.getYera3AuditValueHeader());
				creditReviewSubCtgDetails.setYera3BreakDownHeader(creditReviewSubCtgDetailsHeader.getYera3BreakDownHeader());
				
				creditReviewSubCtgDetails.setYera12PerChangeHeader(creditReviewSubCtgDetailsHeader.getYera12PerChangeHeader());
				creditReviewSubCtgDetails.setYera23PerChangeHeader(creditReviewSubCtgDetailsHeader.getYera23PerChangeHeader());
				
			}
		}
		
		//creditReviewSubtgDetailsList.add(creditReviewSubCtgDetailsHeader);
		//}
		listbox.setParent(div);
		div.setParent(tabPanel);
		logger.debug("Leaving");
		return listbox;

	}
	
	
	public String  getAuditTypeLabel(String auditYear, String prevAudLabel, boolean isForReport){
		logger.debug("Entering");

		FinCreditReviewDetails finCreditReviewDetails = this.creditApplicationReviewService.getCreditReviewDetailsByCustIdAndYear(this.custID.getValue(), auditYear, isEnquiry ? "_AVIEW" : "_VIEW");
		
		if(isEnquiry && finCreditReviewDetails!= null){
			if(creditReviewDetailsMap == null){
				creditReviewDetailsMap = new HashMap<String,FinCreditReviewDetails>();
			}
			if(creditReviewDetailsMap.get(StringUtils.trimToEmpty(finCreditReviewDetails.getAuditYear())) == null){
				creditReviewDetailsMap.put(StringUtils.trimToEmpty(finCreditReviewDetails.getAuditYear()),finCreditReviewDetails);
			}
		}
		
		String auditTypeLabel = "";
		if(isForReport){
			auditTypeLabel = "AUD/Qual "+auditYear+"-0 Months";
			if (finCreditReviewDetails != null) {
				String qualOrUnQual = finCreditReviewDetails.isQualified() ? FacilityConstants.CREDITREVIEW_QUALIFIED : FacilityConstants.CREDITREVIEW_UNQUALIFIED;
				auditTypeLabel = finCreditReviewDetails.getAuditType() + "/" + qualOrUnQual + " " + auditYear + prevAudLabel;
				logger.debug("Leaving");
			}
		} else {
			auditTypeLabel = "AUD/Qual ";
			if (finCreditReviewDetails != null) {
				String qualOrUnQual = finCreditReviewDetails.isQualified() ? FacilityConstants.CREDITREVIEW_QUALIFIED : FacilityConstants.CREDITREVIEW_UNQUALIFIED;
				auditTypeLabel = finCreditReviewDetails.getAuditType() + "/" + qualOrUnQual ;
				logger.debug("Leaving");
			}
		}
		return auditTypeLabel;
	}
	/**
	 * onChange get the customer Details
	 * @param event
	 * @throws Exception 
	 */
	public void onChange$custCIF(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		this.custCIF.clearErrorMessage();
	
		Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.custCIF.getValue(), getFilterList());
		creditReviewSubtgDetailsList.clear();
		if(customer == null) {		
			this.custShrtName.setValue("");
			this.custID.setValue(Long.valueOf(0));
			if(this.tabpanelsBoxIndexCenter.getChildren() != null){
				this.tabpanelsBoxIndexCenter.getChildren().clear();
			}
			if(this.tabsIndexCenter.getChildren() != null){
				this.tabsIndexCenter.getChildren().clear();
			}
			doClearMessage();
			if(StringUtils.isNotBlank(this.custCIF.getValue())){
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value") }));
			}
		} else {
			doSetCustomer(customer, null);
		}

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();	

		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("filtersList", getFilterList());
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null,map);
		logger.debug("Leaving");
	}

	/**
	 * To fill Customer Details if Co-applicants is available
	 * @param coAppIds
	 */
	public void doFillCustomerDetails(List<FinCreditReviewDetails> coAppIds) {
		logger.debug("Entering");
		
		Map<String, List<String>> map = new LinkedHashMap<>();

		//Separate Customer CIF wise audit years 
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
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF());
		this.custCIF.setTooltiptext(aCustomer.getCustCIF().trim() +"-"+aCustomer.getCustShrtName());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		this.custCtgCode = aCustomer.getLovDescCustCtgType();
		this.listOfFinCreditRevCategory = this.creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(this.custCtgCode);
		logger.debug("Leaving");
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setCreditApplicationReviewService(
			CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if(isEnquiry) {
			closeDialog();
			final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");  
			final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
			tabbox.getSelectedTab().close();
		} else {
			closeDialog();
			creditApplicationReviewListCtrl.doReset();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method for selecting customer id from lov and after that setting sheet on bases of the customer type.<BR>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		getSearch(true);
		logger.debug("Leaving" + event.toString());
	}


	
	public void getSearch(boolean isEnquiry) throws Exception{
		ratioFlag= true;
		if(this.tabpanelsBoxIndexCenter.getChildren().size()>0){
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if(this.tabsIndexCenter.getChildren().size()>0){
			this.tabsIndexCenter.getChildren().clear();
		}		
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try{
			if (this.toYear.getValue()==null){
				throw new WrongValueException(this.toYear, Labels.getLabel("FIELD_IS_MAND",new String[]{
						Labels.getLabel("label_CreditApplicationReviewDialog_NoOfYearsToDisplay.value")}));
			} 
			if (this.toYear.getValue() > DateUtility.getYear(appldate)){
				throw new WrongValueException(this.toYear, Labels.getLabel("DATE_NO_FUTURE"));
			} 
			if(this.toYear.getValue() < DateUtility.getYear(DateUtility.addDays(appDftStrtDate, 1))){
				throw new WrongValueException(this.toYear, Labels.getLabel("label_CreditReviewNotValidYear"));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}	
		try{
			if (this.custID.getValue() == null || this.custID.getValue() == 0){
				if(StringUtils.isNotBlank(this.custCIF.getValue())){
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value") }));
				}
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_IS_MAND",new String[]{
						Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value")}));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}	

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			logger.debug("Leaving");
			throw new WrongValuesException(wvea);
		}
		if(wve.size() == 0){
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
	public String replaceYear(String formula,int year){
		String formatedFormula= formula;
		for(int i= 0;i<this.noOfYears;i++){
			if(i==0){
				formatedFormula = formatedFormula.replace("YN.","Y"+year);
			}else{
				formatedFormula = formatedFormula.replace("YN-"+i+".","Y"+(year-i));
			}
		}
		return formatedFormula;
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
		doShowNotes(this.finCreditReviewDetails);
	}

	/*
	 *  OnClick event For Button Print
	 */
	public void onClick$btnPrint(Event event) throws InterruptedException{
		logger.debug("Entering" + event.toString());
		
		
		doClearMessage();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try{
			if (this.toYear.getValue()==null){
				throw new WrongValueException(this.toYear, Labels.getLabel("FIELD_IS_MAND",new String[]{
						Labels.getLabel("label_CreditApplicationReviewDialog_NoOfYearsToDisplay.value")}));
			} 
			if (this.toYear.getValue() > DateUtility.getYear(appldate)){
				throw new WrongValueException(this.toYear, Labels.getLabel("DATE_NO_FUTURE"));
			}
			if(this.toYear.getValue() < DateUtility.getYear(DateUtility.addDays(appDftStrtDate, 1))){
				throw new WrongValueException(this.toYear, Labels.getLabel("label_CreditReviewNotValidYear"));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}	
		try{
			if (this.custID.getValue() == null || this.custID.getValue() == 0){
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_IS_MAND",new String[]{
						Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value")}));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}	

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			logger.debug("Leaving");
			throw new WrongValuesException(wvea);
		   } else {
			CreditReviewMainCtgDetails creditReviewMainCtgDetails = new CreditReviewMainCtgDetails();
			creditReviewMainCtgDetails.setCustCIF(this.custCIF.getValue()+" - "+StringUtils.trimToEmpty(custShrtName.getValue()));
			creditReviewMainCtgDetails.setToYear(String.valueOf(this.toYear.getValue()));

			List<Object> list = new ArrayList<Object>();
			list.add(creditReviewSubtgDetailsList);
   
			ReportGenerationUtil.generateReport("CreditApplication_Review_Enquiry", creditReviewMainCtgDetails, list, true, 1,
					getUserWorkspace().getLoggedInUser().getUserName(),  this.window_CreditApplicationReviewDialog,true);	

		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	public void setCreditReviewSummaryData(CreditReviewSummaryData creditReviewSummaryData) {
		this.creditReviewSummaryData = creditReviewSummaryData;
	}

	public CreditReviewSummaryData getCreditReviewSummaryData() {
		return creditReviewSummaryData;
	}
	
	private List<Filter> getFilterList() {
		filterList = new ArrayList<Filter>();		
		filterList.add(new Filter("lovDescCustCtgType", new String[]{PennantConstants.PFF_CUSTCTG_CORP,
				PennantConstants.PFF_CUSTCTG_SME}, Filter.OP_IN));
		return filterList;
	}
	
	public  String getSavingStatus(String roleCode,String nextRoleCode, long custId, String moduleCode, String recordStatus){
		String roleCodeDesc = "";
		if(StringUtils.isBlank(nextRoleCode) || roleCode.equals(nextRoleCode) || StringUtils.trimToEmpty(recordStatus).equalsIgnoreCase(PennantConstants.RCD_STATUS_SAVED)){
			return moduleCode + " with Customer ID: " + custId +" "+ recordStatus + " Successfully.";
		}else{
			JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
			if (nextRoleCode.contains(",")) {
	            String roleCodes[]=nextRoleCode.split(",");
	        	searchObject.addFilterIn("RoleCd", (Object)roleCodes);
            }else{
            	searchObject.addFilterEqual("RoleCd", nextRoleCode);
            }
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<SecurityRole> rolesList = pagedListService.getBySearchObject(searchObject);
			if (rolesList!=null && !rolesList.isEmpty()) {
				for (SecurityRole securityRole : rolesList) {
					if ("".equals(roleCodeDesc)) {
						roleCodeDesc = securityRole.getRoleDesc();
                    }else{
                    	roleCodeDesc=roleCodeDesc+" And "+securityRole.getRoleDesc();
                    }
                }
            }
			return moduleCode + " with Customer ID: " + custId + " Moved to " +  (StringUtils.isBlank(roleCodeDesc) ? "" : roleCodeDesc) + " Successfully.";
		}
 	}
	
	public void setFinCreditReviewDetails(FinCreditReviewDetails finCreditReviewDetails) {
		this.finCreditReviewDetails = finCreditReviewDetails;
	}
	
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
	}

	public void setCreditApplicationReviewDAO(CreditApplicationReviewDAO creditApplicationReviewDAO) {
		this.creditApplicationReviewDAO = creditApplicationReviewDAO;
	}

	public void setCustomerBankInfoService(CustomerBankInfoService customerBankInfoService) {
		this.customerBankInfoService = customerBankInfoService;
	}

	public void setCustomerExtLiabilityService(CustomerExtLiabilityService customerExtLiabilityService) {
		this.customerExtLiabilityService = customerExtLiabilityService;
	}
	
}