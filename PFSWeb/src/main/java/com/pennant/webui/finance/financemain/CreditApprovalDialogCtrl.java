package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.dashboard.DashboardCreate;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CreditApprovalDialogCtrl extends GFCBaseCtrl<FinanceDetail> {
	private static final long serialVersionUID = 2290501784830847866L;
	private static final Logger logger = LogManager.getLogger(CreditApprovalDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CreditApprovalDialog; // autoWired
	protected Borderlayout borderlayoutDeviationDetail; // autoWired
	protected Div divFianncedetails;
	private int ccyFormatter = 0;
	// Tab 1
	protected Textbox finCustSysref;
	protected Textbox finCustBranch;
	protected Textbox custCIF;
	protected Label custName;
	protected Textbox finCustNationality;
	protected Textbox finCustEducation;
	protected Textbox finCustPrvEmployer;
	protected Textbox finCustPrvYOE;
	protected Textbox finCustFDOB;
	protected Textbox finCustFAge;
	protected Textbox finCustSDOB;
	protected Textbox finCustSAge;
	protected Textbox finCustSector;
	protected Textbox finCustSubSector;
	protected Textbox finCustPhone;
	protected Textbox finCustFax;
	protected Textbox finCustMail;
	// Finance Details
	protected Textbox finType;
	protected Textbox finCcy;
	protected Label finCcyDesc;
	protected Textbox finDivison;
	protected Decimalbox finAmount;
	protected Decimalbox finDownPayBank;
	protected Decimalbox finDownPaySupp;
	protected Decimalbox finProfitRate;
	protected Intbox numberOfterms;
	protected Textbox finPurpose;

	protected Listbox listBoxCustomerFinExposure;
	protected Listbox listBoxFinElgRef;
	protected Listbox listBoxRetailScoRef;
	protected Listbox listBox_CheckList;

	private static final String BOLD = " font-weight: bold;";

	private FinanceDetail financeDetail = null;
	List<DeviationParam> eligibilitiesList = PennantAppUtil.getDeviationParams();

	/**
	 * default constructor.<br>
	 */
	public CreditApprovalDialogCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CreditApprovalDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CreditApprovalDialog);

		try {

			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				setFinanceDetail(financeDetail);
				doShowDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CreditApprovalDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.debug("Entering");
		try {

			this.divFianncedetails.setHeight((borderLayoutHeight - 25) + "px");
			doWriteBeantoComponentsFiannceData(getFinanceDetail());
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * @param financeDetail
	 */
	private void doWriteBeantoComponentsFiannceData(FinanceDetail financeDetail) {
		logger.debug(" Entering ");

		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType type = financeDetail.getFinScheduleData().getFinanceType();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		Date appldate = SysParamUtil.getAppDate();
		ccyFormatter = CurrencyUtil.getFormat(finMain.getFinCcy());

		this.finCustSysref.setValue(finMain.getFinReference());
		this.finCustBranch.setValue(customer.getCustDftBranch() + "-" + customer.getLovDescCustDftBranchName());
		this.custCIF.setValue(customer.getCustCIF() + "-" + customer.getCustShrtName());
		// this.custName.setValue(customer.getCustShrtName());
		this.finCustNationality
				.setValue(customer.getCustNationality() + "-" + customer.getLovDescCustNationalityName());

		if (customer.getCustDOB() != null) {
			this.finCustFDOB.setValue(DateUtil.formatToShortDate(customer.getCustDOB()));
			this.finCustFAge.setValue(String.valueOf(DateUtil.getYearsBetween(customer.getCustDOB(), appldate)));
		}

		// Customer Email
		List<CustomerEMail> emailList = customerDetails.getCustomerEMailList();

		if (emailList != null) {
			for (CustomerEMail customerEMail : emailList) {
				this.finCustMail.setValue(customerEMail.getCustEMail());
			}
		}

		// Customer Phone Number
		List<CustomerPhoneNumber> phoneList = customerDetails.getCustomerPhoneNumList();

		if (phoneList != null && !phoneList.isEmpty()) {
			CustomerPhoneNumber customerPhoneNumber = phoneList.get(0);
			this.finCustPhone
					.setValue(PennantApplicationUtil.formatPhoneNumber(customerPhoneNumber.getPhoneCountryCode(),
							customerPhoneNumber.getPhoneAreaCode(), customerPhoneNumber.getPhoneNumber()));

		}
		this.finCustEducation.setValue("");
		// Finance Details
		this.finType.setValue(type.getFinType() + "-" + type.getFinTypeDesc());
		this.finCcy.setValue(finMain.getFinCcy());
		// this.finCcyDesc.setValue(main.getLovDescFinCcyName());
		this.finDivison.setValue(type.getFinDivision() + " - " + type.getLovDescFinDivisionName());
		this.finAmount.setValue(CurrencyUtil.parse(finMain.getFinAmount(), ccyFormatter));
		this.finDownPayBank.setValue(CurrencyUtil.parse(finMain.getDownPayBank(), ccyFormatter));
		this.finDownPaySupp.setValue(CurrencyUtil.parse(finMain.getDownPaySupl(), ccyFormatter));
		this.finProfitRate.setValue(finMain.getRepayProfitRate());
		this.numberOfterms.setValue(finMain.getNumberOfTerms());
		this.finPurpose.setValue(finMain.getFinPurpose());
		doFillCustFinanceExposureDetails(customerDetails.getCustFinanceExposureList());
		// Set Eligibility based on deviations and rule result
		List<FinanceEligibilityDetail> eligibilityRuleList = financeDetail.getElgRuleList();
		for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityRuleList) {
			setStatusByDevaition(financeEligibilityDetail);
		}

		doFillFInEligibilityDetails(eligibilityRuleList);

		doFillCheckListdetails(financeDetail);

		doFillScoringdetails(financeDetail.getFinScoreHeaderList(), financeDetail.getScoreDetailListMap());

		createDashboards();

		logger.debug(" Entering ");
	}

	/**
	 * @param custFinanceExposureDetails
	 */
	private void doFillCustFinanceExposureDetails(List<FinanceEnquiry> custFinanceExposureDetails) {

		logger.debug(" Entering ");

		this.listBoxCustomerFinExposure.getItems().clear();
		if (custFinanceExposureDetails != null) {
			for (FinanceEnquiry finEnquiry : custFinanceExposureDetails) {
				Listitem item = new Listitem();
				Listcell lc = new Listcell(DateUtil.formatToLongDate(finEnquiry.getFinStartDate()));
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinType());
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinReference());
				lc.setParent(item);
				BigDecimal totAmt = finEnquiry.getFinAmount()
						.subtract(finEnquiry.getDownPayment().add(finEnquiry.getFeeChargeAmt()));
				lc = new Listcell(CurrencyUtil.format(totAmt, CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				BigDecimal instAmt = BigDecimal.ZERO;
				if (finEnquiry.getNumberOfTerms() > 0) {
					instAmt = totAmt.divide(new BigDecimal(finEnquiry.getNumberOfTerms()), 0, RoundingMode.HALF_DOWN);
				}
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(instAmt, CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(totAmt.subtract(finEnquiry.getFinRepaymentAmount()),
						CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinStatus());
				lc.setParent(item);
				this.listBoxCustomerFinExposure.appendChild(item);
			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * Method for Rendering Executed Eligibility Details
	 * 
	 * @param eligibilityDetails
	 */
	private void doFillFInEligibilityDetails(List<FinanceEligibilityDetail> eligibilityDetails) {
		logger.debug("Entering");

		this.listBoxFinElgRef.getItems().clear();

		if (eligibilityDetails != null && !eligibilityDetails.isEmpty()) {
			for (FinanceEligibilityDetail detail : eligibilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;

				// Rule Code
				lc = new Listcell(detail.getLovDescElgRuleCode());
				lc.setParent(item);

				// Rule Code Desc
				lc = new Listcell(detail.getLovDescElgRuleCodeDesc());
				lc.setParent(item);

				// If Rule Not Executed

				if (StringUtils.isEmpty(detail.getRuleResult())) {

					lc = new Listcell("");
					lc.setParent(item);

				} else {

					String labelCode = "";
					String StyleCode = "";

					if (detail.isEligible()) {
						if (detail.isEligibleWithDevaition()) {
							labelCode = Labels.getLabel("common.Eligible_Deviation");
						} else {
							labelCode = Labels.getLabel("common.Eligible");
						}
						StyleCode = "font-weight:bold;color:green;";

					} else {
						labelCode = Labels.getLabel("common.Ineligible");
						StyleCode = "font-weight:bold;color:red;";
					}

					if (RuleConstants.RETURNTYPE_DECIMAL.equals(detail.getRuleResultType())) {

						if (RuleConstants.ELGRULE_DSRCAL.equals(detail.getLovDescElgRuleCode())
								|| RuleConstants.ELGRULE_PDDSRCAL.equals(detail.getLovDescElgRuleCode())) {
							BigDecimal val = new BigDecimal(detail.getRuleResult());
							val = val.setScale(2, RoundingMode.HALF_DOWN);
							labelCode = String.valueOf(val) + "%";
						} else {
							labelCode = CurrencyUtil.format(new BigDecimal(detail.getRuleResult()), ccyFormatter);
						}
						StyleCode = "text-align:right;";
					}

					lc = new Listcell(labelCode);
					lc.setStyle(StyleCode);
					lc.setParent(item);

				}

				listBoxFinElgRef.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * @param finElgDet
	 */
	private void setStatusByDevaition(FinanceEligibilityDetail finElgDet) {
		logger.debug(" Entering ");

		FinanceDeviations deviation = null;

		List<FinanceDeviations> list = getFinanceDetail().getFinanceDeviations();
		if (list != null && !list.isEmpty()) {
			for (FinanceDeviations financeDeviations : list) {
				if (financeDeviations.getDeviationCode().equals(String.valueOf(finElgDet.getElgRuleCode()))) {
					deviation = financeDeviations;
					break;
				}
			}
		}
		if (deviation == null) {
			List<FinanceDeviations> approvedList = getFinanceDetail().getApprovedFinanceDeviations();
			if (approvedList != null && !approvedList.isEmpty()) {
				for (FinanceDeviations financeDeviations : approvedList) {
					if (financeDeviations.getDeviationCode().equals(String.valueOf(finElgDet.getElgRuleCode()))) {
						deviation = financeDeviations;
						break;
					}
				}
			}
		}

		if (deviation != null) {
			if ("".equals(deviation.getDelegationRole())) {
				finElgDet.setEligible(false);
			} else {
				finElgDet.setEligible(true);
				finElgDet.setEligibleWithDevaition(true);
			}

		} else {
			String ruleResult = StringUtils.trimToEmpty(finElgDet.getRuleResult());
			if ("0.0".equals(ruleResult) || "0.00".equals(ruleResult)) {
				finElgDet.setEligible(false);
			} else {
				finElgDet.setEligible(true);
			}

		}

		logger.debug(" Leaving ");
	}

	class CompareCheckList implements Comparator<FinanceCheckListReference> {

		public CompareCheckList() {
		    super();
		}

		@Override
		public int compare(FinanceCheckListReference o1, FinanceCheckListReference o2) {
			return String.valueOf(o1.getQuestionId()).compareTo(String.valueOf(o2.getQuestionId()));
		}
	}

	/**
	 * @param financeDetail
	 */
	private void doFillCheckListdetails(FinanceDetail financeDetail) {
		logger.debug(" Entering ");

		List<FinanceCheckListReference> list = financeDetail.getFinanceCheckList();

		Collections.sort(list, new CompareCheckList());

		long Questionid = 0;
		for (FinanceCheckListReference finRefDetail : list) {
			if (Questionid != finRefDetail.getQuestionId()) {
				Questionid = finRefDetail.getQuestionId();
				Listgroup listgroup = new Listgroup();
				Listcell listcell;
				listcell = new Listcell(finRefDetail.getLovDescQuesDesc());
				listcell.setStyle(BOLD);
				listcell.setParent(listgroup);
				// Deviation Combobox
				FinanceDeviations devation = getDeviationValuesifnay(finRefDetail);

				if (devation != null) {
					String devCode = devation.getDeviationValue();
					int index = devCode.indexOf("_");
					String comboVal = devCode.substring(index);
					Listcell listCell = new Listcell();
					Combobox combobox = new Combobox();
					combobox.setWidth("100px");
					fillComboBox(combobox, StringUtils.trimToEmpty(comboVal),
							PennantStaticListUtil.getCheckListDeviationType(), "");
					combobox.setDisabled(true);
					listCell.appendChild(combobox);
					listCell.appendChild(new Space());
					if (!DeviationConstants.CL_WAIVED.equals(comboVal)) {
						Intbox intbox = new Intbox();
						intbox.setValue(Integer.parseInt(devation.getDeviationValue()));
						intbox.setWidth("50px");
						intbox.setDisabled(true);
						listCell.appendChild(intbox);
					}

					listgroup.appendChild(listCell);
				} else {
					listgroup.appendChild(new Listcell(""));
				}

				listgroup.appendChild(new Listcell(""));

				this.listBox_CheckList.appendChild(listgroup);

				Listitem listitem = new Listitem();
				listcell = new Listcell(finRefDetail.getLovDescAnswerDesc());
				listcell.setParent(listitem);
				listitem.appendChild(new Listcell(""));
				listitem.appendChild(new Listcell(""));
				this.listBox_CheckList.appendChild(listitem);
			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * @param list
	 * @param map
	 */
	private void doFillScoringdetails(List<FinanceScoreHeader> list, Map<Long, List<FinanceScoreDetail>> map) {
		logger.debug(" Entering ");

		this.listBoxRetailScoRef.getItems().clear();

		for (FinanceScoreHeader financeScoreHeader : list) {
			Listgroup listGroup = new Listgroup();
			Listcell listcell;

			listcell = new Listcell(financeScoreHeader.getGroupCode() + "-" + financeScoreHeader.getGroupCodeDesc());
			listcell.setStyle(BOLD);
			listcell.setParent(listGroup);

			listcell = new Listcell(PennantJavaUtil.concat(Labels.getLabel("label_MinScore"), " :",
					String.valueOf(financeScoreHeader.getMinScore())));
			listcell.setStyle(BOLD);
			listcell.setParent(listGroup);

			listcell = new Listcell("");
			listcell.setParent(listGroup);

			listcell = new Listcell("");
			listcell.setParent(listGroup);
			this.listBoxRetailScoRef.appendChild(listGroup);

			List<FinanceScoreDetail> dtlist = map.get(financeScoreHeader.getHeaderId());

			BigDecimal totScore = new BigDecimal(0);
			BigDecimal totExeScore = new BigDecimal(0);

			for (FinanceScoreDetail financeScoreDetail : dtlist) {
				Listitem listitem = new Listitem();

				listcell = new Listcell(financeScoreDetail.getRuleCode());
				listcell.setParent(listitem);

				listcell = new Listcell(financeScoreDetail.getRuleCodeDesc());
				listcell.setParent(listitem);

				totScore = totScore.add(financeScoreDetail.getMaxScore());
				listcell = new Listcell(CurrencyUtil.format(financeScoreDetail.getMaxScore(), 0));
				listcell.setParent(listitem);

				totExeScore = totExeScore.add(financeScoreDetail.getExecScore());
				listcell = new Listcell(CurrencyUtil.format(financeScoreDetail.getExecScore(), 0));
				listcell.setParent(listitem);
				this.listBoxRetailScoRef.appendChild(listitem);
			}
			// Total's
			Listitem listfoot = new Listitem();

			listcell = new Listcell();
			listcell.setParent(listfoot);

			listcell = new Listcell(
					Labels.getLabel("label_Credit_Worth") + " : " + financeScoreHeader.getCreditWorth());
			listcell.setStyle(BOLD);
			listcell.setParent(listfoot);

			listcell = new Listcell(CurrencyUtil.format(totScore, 0));
			listcell.setStyle(BOLD);
			listcell.setParent(listfoot);

			listcell = new Listcell(CurrencyUtil.format(totExeScore, 0));
			listcell.setStyle(BOLD);
			listcell.setParent(listfoot);
			this.listBoxRetailScoRef.appendChild(listfoot);
		}

		logger.debug(" Leaving ");

	}

	/**
	 * @param finRefDetail
	 * @return
	 */
	private FinanceDeviations getDeviationValuesifnay(FinanceCheckListReference finRefDetail) {
		logger.debug(" Entering ");

		List<FinanceDeviations> list = new ArrayList<>();
		list.addAll(getFinanceDetail().getFinanceDeviations());
		list.addAll(getFinanceDetail().getApprovedFinanceDeviations());

		if (!list.isEmpty()) {
			for (FinanceDeviations financeDeviations : list) {
				if (!financeDeviations.getModule().equals(DeviationConstants.TY_CHECKLIST)) {
					continue;
				}

				String devCode = financeDeviations.getDeviationCode();
				int index = devCode.indexOf("_");
				long devRef = Long.parseLong(devCode.substring(0, index));

				if (devRef == finRefDetail.getQuestionId()) {

					logger.debug(" Leaving ");
					return financeDeviations;
				}

			}
		}

		logger.debug(" Leaving ");
		return null;

	}

	public DashboardCreate dashboardCreate;
	public Cell col_html;

	/**
	 * Create DashBoards
	 * 
	 * @param panelchildren
	 * @param info
	 */
	public void createDashboards() {
		JdbcSearchObject<DashboardConfiguration> jdbcSearchObject = new JdbcSearchObject<DashboardConfiguration>(
				DashboardConfiguration.class);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		jdbcSearchObject.addFilterEqual("DashboardCode", "CCBYFINAMT");
		List<DashboardConfiguration> list = pagedListService.getBySearchObject(jdbcSearchObject);
		if (list != null && !list.isEmpty()) {
			jdbcSearchObject.addFilterEqual("DashboardCode", list.get(0));
			ChartDetail chartDetail = dashboardCreate.getChartDetail(list.get(0));
			chartDetail.setChartId(list.get(0).getDashboardCode());
			chartDetail.setChartHeight("75%");
			chartDetail.setChartWidth("100%");
			chartDetail.setiFrameHeight("100%");
			chartDetail.setiFrameWidth("100%");
			dashboardCreate.setChartDetail(chartDetail);

			// new code to display chart by skipping jsps
			String strXML = chartDetail.getStrXML();
			strXML = strXML.replace("\n", "").replaceAll("\\s{2,}", " ");
			strXML = StringEscapeUtils.escapeJavaScript(strXML);
			chartDetail.setStrXML(strXML);

			Executions.createComponents("/Charts/Chart.zul", col_html,
					Collections.singletonMap("chartDetail", chartDetail));
		}
	}

	public DashboardCreate getDashboardCreate() {
		return dashboardCreate;
	}

	public void setDashboardCreate(DashboardCreate dashboardCreate) {
		this.dashboardCreate = dashboardCreate;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}
