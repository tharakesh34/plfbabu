/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceMainDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.service.bmtmasters.RatingCodeService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl.CreditReviewSummaryData;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.webui.delegationdeviation.DeviationExecutionCtrl;
import com.pennant.webui.util.GFCBaseCtrl;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class ScoringDetailDialogCtrl extends GFCBaseCtrl<FinanceScoreDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(ScoringDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ScoringDetailDialog; // autoWired

	protected Button btnScoringGroup; // autoWired
	protected Label label_TotalScore; // autoWired
	protected Label totalCorpScore; // autoWired
	protected Label label_CorpCreditWoth; // autoWired
	protected Label corpCreditWoth; // autoWired
	protected Label label_ScoreSummary; // autoWired
	protected Label label_ScoreSummaryVal; // autoWired
	protected Label label_ClientCalScore; // autoWired
	protected Label label_ClientCalScoreVal; // autoWired

	protected Groupbox finScoreDetailGroup; // autoWired

	protected Listbox listBoxRetailScoRef; // autoWired
	protected Listbox listBoxFinancialScoRef; // autoWired
	protected Listbox listBoxNonFinancialScoRef; // autoWired

	protected Tab finScoreMetricTab; // autoWired
	protected Tab nonFinScoreMetricTab; // autoWired

	protected Decimalbox maxFinTotScore; // autoWired
	protected Decimalbox maxNonFinTotScore; // autoWired
	protected Intbox minScore; // autoWired
	protected Decimalbox calTotScore; // autoWired
	protected Checkbox isOverride; // autoWired
	protected Intbox overrideScore; // autoWired
	protected Row row_finScoreOverride; // autoWired

	private Map<String, BigDecimal> finExecScoreMap = null;
	// private Map<Long, Boolean> retailOverrideCheckMap = null;

	// External Fields usage for Individuals ----> Scoring Details
	private String custCtgType = "";
	private transient boolean scoreExecuted;
	private transient boolean sufficientScore;
	private BigDecimal totalExecScore = BigDecimal.ZERO;
	private BigDecimal totalFinScore = BigDecimal.ZERO;
	private BigDecimal totalNFRuleScore = BigDecimal.ZERO;
	BigDecimal metricMaxScore = BigDecimal.ZERO;
	BigDecimal metricExecScore = BigDecimal.ZERO;
	private transient boolean validationOn;

	// not auto wired variables
	private FinanceDetail financeDetail = null; // over handed per parameters
												// parameters
												// Bean Setters by application Context
	private FinanceDetailService financeDetailService;
	private ScoringDetailService scoringDetailService;
	private CustomerService customerService;
	private RatingCodeService ratingCodeService;
	private CreditReviewSummaryData creditReviewSummaryData;
	private Object financeMainDialogCtrl;
	private BigDecimal[] scores = null;
	String userRole = "";
	private boolean isWIF = false;

	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;

	DeviationExecutionCtrl deviationExecutionCtrl;

	/**
	 * default constructor.<br>
	 */
	public ScoringDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onCreate$window_ScoringDetailDialog(Event event)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScoringDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
		}
		if (arguments.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
		}

		if (arguments.containsKey("isWIF")) {
			isWIF = (Boolean) arguments.get("isWIF");
		}

		if (arguments.containsKey("userRole")) {
			userRole = (String) arguments.get("userRole");
		}

		// set Field Properties
		doShowDialog();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain financeMain
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doShowDialog() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering");

		FinanceMain main = getFinanceDetail().getFinScheduleData().getFinanceMain();
		custCtgType = getFinanceDetail().getCustomerDetails().getCustomer().getCustCtgCode();

		appendFinBasicDetails();

		if (isWIF) {
			doExecuteScoring(false);
		} else {
			deviationExecutionCtrl = (DeviationExecutionCtrl) getFinanceMainDialogCtrl().getClass()
					.getMethod("getDeviationExecutionCtrl").invoke(getFinanceMainDialogCtrl());
			fillFinCustScoring();
		}

		try {
			financeMainDialogCtrl.getClass().getMethod("setScoringDetailDialogCtrl", this.getClass())
					.invoke(financeMainDialogCtrl, this);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		getBorderLayoutHeight();

		this.listBoxRetailScoRef.setHeight(this.borderLayoutHeight - 180 - 52 + "px");
		this.listBoxFinancialScoRef.setHeight(this.borderLayoutHeight - 318 + "px");
		this.listBoxNonFinancialScoRef.setHeight(this.borderLayoutHeight - 318 + "px");
		this.window_ScoringDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");

		logger.debug("Leaving");
	}

	public void fillFinCustScoring() {
		logger.debug("Entering");

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		List<FinanceReferenceDetail> scoringList = getFinanceDetail().getScoringGroupList();

		// Set Customer Data to Calculate the Score
		this.listBoxRetailScoRef.getItems().clear();
		this.listBoxFinancialScoRef.getItems().clear();
		this.listBoxNonFinancialScoRef.getItems().clear();

		// Execute Scoring metrics and Display Total Score
		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(custCtgType)) {
			// Individual customer
			this.listBoxRetailScoRef.setVisible(true);
			this.finScoreDetailGroup.setVisible(false);
			this.label_ScoreSummaryVal.setValue("");
			this.label_ClientCalScoreVal.setValue("");

			doFillRetailScoringListbox(scoringList, this.listBoxRetailScoRef, false);
		} else if (PennantConstants.PFF_CUSTCTG_CORP.equals(custCtgType)
				|| PennantConstants.PFF_CUSTCTG_SME.equals(custCtgType)) {
			// corporate or bank customer
			this.listBoxRetailScoRef.setVisible(false);
			this.finScoreDetailGroup.setVisible(true);

			if (financeMain.isLovDescIsSchdGenerated()) {
				doFillCorpScoringMetricDetails(true);
			} else {
				doFillCorpScoringMetricDetails(false);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Finance Scoring Details List
	 * 
	 * @param event
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnScoringGroup(Event event)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering" + event.toString());
		doExecuteScoring(true);
		logger.debug("Leaving" + event.toString());
	}

	public void doExecuteScoring(boolean isUserAction)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		if (getFinanceDetail() != null) {
			boolean custValidated = true;
			if (!isWIF) {
				custValidated = (Boolean) getFinanceMainDialogCtrl().getClass().getMethod("doCustomerValidation")
						.invoke(getFinanceMainDialogCtrl());
				setFinanceDetail((FinanceDetail) getFinanceMainDialogCtrl().getClass().getMethod("getFinanceDetail")
						.invoke(getFinanceMainDialogCtrl()));
				custValidated = (Boolean) getFinanceMainDialogCtrl().getClass().getMethod("doExtendedDetailsValidation")
						.invoke(getFinanceMainDialogCtrl());
				custValidated = (Boolean) getFinanceMainDialogCtrl().getClass().getMethod("doPSLDetailsValidation")
						.invoke(getFinanceMainDialogCtrl());

				// Prepare Data for Rule Executions
				try {
					Object object = getFinanceMainDialogCtrl().getClass()
							.getMethod("prepareCustElgDetail", Boolean.class)
							.invoke(getFinanceMainDialogCtrl(), !isUserAction);
					if (object != null) {
						setFinanceDetail((FinanceDetail) object);
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			} else {
				try {
					FinanceDetail financeDetail = (FinanceDetail) getFinanceMainDialogCtrl().getClass()
							.getMethod("dofillEligibilityData", Boolean.class)
							.invoke(getFinanceMainDialogCtrl(), isUserAction);
					setFinanceDetail(financeDetail);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}

			if (custValidated) {
				executeScore();
			}
		}
	}

	private void executeScore() {
		logger.debug(" Entering ");
		this.listBoxRetailScoRef.getItems().clear();
		this.listBoxFinancialScoRef.getItems().clear();
		this.listBoxNonFinancialScoRef.getItems().clear();

		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(custCtgType)) {
			this.label_ScoreSummaryVal.setValue("");
			this.label_ClientCalScoreVal.setValue("");
			setSufficientScore(false);
			doFillRetailScoringListbox(getFinanceDetail().getScoringGroupList(), this.listBoxRetailScoRef, true);
		} else if (PennantConstants.PFF_CUSTCTG_CORP.equals(custCtgType)
				|| PennantConstants.PFF_CUSTCTG_SME.equals(custCtgType)) {
			this.listBoxRetailScoRef.setVisible(false);
			this.finScoreDetailGroup.setVisible(true);
			doFillCorpScoringMetricDetails(true);
		}
		logger.debug(" Leaving ");
	}

	/**
	 * Method to fill list box in Scoring Group Tab <br>
	 * 
	 * @param financeReferenceDetail (List<FinanceReferenceDetail>)
	 * @param listbox                (Listbox)
	 * @param execute                (boolean)
	 */
	public void doFillRetailScoringListbox(List<FinanceReferenceDetail> financeReferenceDetail, Listbox listbox,
			boolean isExecute) {
		logger.debug("Entering");

		this.label_TotalScore.setVisible(false);
		this.totalCorpScore.setVisible(false);
		this.label_CorpCreditWoth.setVisible(false);
		this.corpCreditWoth.setVisible(false);
		this.label_ScoreSummary.setVisible(true);
		this.label_ScoreSummaryVal.setVisible(true);
		this.label_ClientCalScore.setVisible(false);
		this.label_ClientCalScoreVal.setVisible(false);
		this.btnScoringGroup.setVisible(false);

		List<FinanceScoreHeader> scoringHeaderList = getFinanceDetail().getFinScoreHeaderList();
		List<FinanceDeviations> scoringDeviations = new ArrayList<FinanceDeviations>();

		if ((financeReferenceDetail != null && !financeReferenceDetail.isEmpty())
				|| (scoringHeaderList != null && !scoringHeaderList.isEmpty())) {

			BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
			BigDecimal totalGrpExecScore = BigDecimal.ZERO;
			BigDecimal totalClientExecScore = BigDecimal.ZERO;

			for (FinanceReferenceDetail finrefdet : financeReferenceDetail) {

				FinanceScoreHeader header = null;
				if (scoringHeaderList != null) {
					for (FinanceScoreHeader financeScoreHeader : scoringHeaderList) {
						if (financeScoreHeader.getGroupId() == finrefdet.getFinRefId()) {
							header = financeScoreHeader;
							this.setScoreExecuted(true);
							break;
						}
					}
				}
				if (isExecute) {
					this.setScoreExecuted(true);
				}

				if (header == null) {
					header = new FinanceScoreHeader();
				}

				addListGroup("", listbox, PennantConstants.PFF_CUSTCTG_INDIV, finrefdet);// , header.isOverride()

				totalGrpMaxScore = BigDecimal.ZERO;
				totalGrpExecScore = BigDecimal.ZERO;

				if (getFinanceDetail().getScoringMetrics().containsKey(finrefdet.getFinRefId())) {
					List<ScoringMetrics> scoringMetricsList = getFinanceDetail().getScoringMetrics()
							.get(finrefdet.getFinRefId());

					if (isExecute) {
						getScoringDetailService().executeScoringMetrics(scoringMetricsList,
								getFinanceDetail().getCustomerEligibilityCheck());
					}

					for (ScoringMetrics scoringMetric : scoringMetricsList) {
						scores = addListItem(scoringMetric, listbox, "I", finrefdet.getFinRefId(), isExecute);
						totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
						totalGrpExecScore = totalGrpExecScore.add(scores[1]);
					}
				}

				addListFooter(totalGrpMaxScore, totalGrpExecScore, listbox, "", "I", finrefdet.getFinRefId());

				if (isExecute && !isWIF) {
					if (totalGrpExecScore.intValue() < finrefdet.getLovDescminScore()) {
						FinanceDeviations scoreDev = deviationExecutionCtrl.checkScoringDeviations(getFinanceDetail(),
								finrefdet.getFinRefId(), finrefdet.getLovDescminScore(), totalGrpExecScore.intValue());
						if (scoreDev != null) {
							scoringDeviations.add(scoreDev);
						}
					}
				}

				if (isWIF) {
					if (totalGrpExecScore.intValue() < finrefdet.getLovDescminScore()) {
						sufficientScore = false;
					} else {
						sufficientScore = true;
					}
				}

				totalClientExecScore = totalClientExecScore.add(totalGrpExecScore);
			}

			// Score Card preparation as Per Calculation Formula
			totalClientExecScore = totalClientExecScore.add(new BigDecimal(206));
			totalClientExecScore = new BigDecimal(100)
					.add(totalClientExecScore.subtract(new BigDecimal(100)).multiply(BigDecimal.valueOf(1.875)));
			this.label_ClientCalScoreVal.setValue(String.valueOf(
					PennantApplicationUtil.amountFormate(totalClientExecScore.multiply(new BigDecimal(100)), 2)));

		}

		if (isExecute && !isWIF) {
			deviationExecutionCtrl.fillDeviationListbox(scoringDeviations, getUserRole(), DeviationConstants.TY_SCORE);
		}

		setScoreSummaryStyle(sufficientScore, false);
		if (!isWIF) {
			Boolean[] temp = isScoreSufficientWithDeviation(getFinanceDetail().getScoringGroupList(),
					getFinanceDetail().getScoringMetrics());
			sufficientScore = temp[0];
			setScoreSummaryStyle(sufficientScore, temp[1]);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Corporate Scoring Details
	 * 
	 * @param isExecute
	 */
	private void doFillCorpScoringMetricDetails(boolean isExecute) {
		logger.debug("Entering");
		BigDecimal obligorScore = BigDecimal.ZERO;
		List<FinanceScoreHeader> finScoreHeaderList = getFinanceDetail().getFinScoreHeaderList();
		List<FinanceReferenceDetail> refDetails = getFinanceDetail().getScoringGroupList();
		List<ScoringMetrics> scoringMetricsList = null;
		Map<String, String> exeMap = new HashMap<String, String>();

		this.label_TotalScore.setVisible(true);
		this.totalCorpScore.setVisible(true);
		this.label_CorpCreditWoth.setVisible(true);
		this.corpCreditWoth.setVisible(true);
		this.label_ScoreSummary.setVisible(true);
		this.label_ScoreSummaryVal.setVisible(true);
		this.label_ClientCalScore.setVisible(false);
		this.label_ClientCalScoreVal.setVisible(false);

		totalExecScore = BigDecimal.ZERO;
		this.btnScoringGroup.setVisible(false);

		if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			this.isOverride.setChecked(finScoreHeaderList.get(0).isOverride());
		}

		if (refDetails != null && !refDetails.isEmpty()) {
			FinanceReferenceDetail scoringGroup = refDetails.get(0);

			this.setScoreExecuted(isExecute);
			this.minScore.setValue(scoringGroup.getLovDescminScore());
			this.overrideScore.setValue(scoringGroup.getLovDescoverrideScore());

			if (!scoringGroup.isLovDescisoverride()) {
				this.row_finScoreOverride.setVisible(false);
				this.listBoxFinancialScoRef.setHeight(this.borderLayoutHeight - 240 + "px");
				this.listBoxNonFinancialScoRef.setHeight(this.borderLayoutHeight - 240 + "px");
			} else {
				this.row_finScoreOverride.setVisible(true);
				this.listBoxFinancialScoRef.setHeight(this.borderLayoutHeight - 240 - 20 + "px");
				this.listBoxNonFinancialScoRef.setHeight(this.borderLayoutHeight - 240 - 20 + "px");
			}

			this.listBoxFinancialScoRef.getItems().clear();
			totalFinScore = BigDecimal.ZERO;

			for (ScoringMetrics scoringMetric : getFinanceDetail().getFinScoringMetricList()) {
				addListGroup(scoringMetric.getLovDescScoringCodeDesc(), this.listBoxFinancialScoRef,
						PennantConstants.PFF_CUSTCTG_CORP, null);

				BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
				BigDecimal totalGrpExecScore = BigDecimal.ZERO;

				if (getFinanceDetail().getScoringMetrics().containsKey(scoringMetric.getScoringId())) {
					scoringMetricsList = getFinanceDetail().getScoringMetrics().get(scoringMetric.getScoringId());
					if (scoringMetricsList != null) {
						for (ScoringMetrics subScoreMetric : scoringMetricsList) {
							boolean executed = false;
							executed = isExecuted(exeMap, subScoreMetric.getScoringId());

							if (StringUtils.contains(scoringGroup.getMandInputInStage(), userRole)) {
								executed = true;
							} else {
								executed = false;
							}

							scores = addListItem(subScoreMetric, this.listBoxFinancialScoRef, "F",
									scoringMetric.getScoringId(), executed);
							totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
							totalGrpExecScore = totalGrpExecScore.add(scores[1]);
						}
					}
					scoringMetric.setLovDescMetricMaxPoints(totalGrpMaxScore);
					totalFinScore = totalFinScore.add(totalGrpMaxScore);
					totalExecScore = totalExecScore.add(totalGrpExecScore);
				}
				addListFooter(totalGrpMaxScore, totalGrpExecScore, this.listBoxFinancialScoRef, "", "F",
						scoringMetric.getScoringId());
			}

			this.maxFinTotScore.setValue(totalFinScore);
			// totalNFRuleScore = BigDecimal.ZERO;
			this.listBoxNonFinancialScoRef.getItems().clear();
			BigDecimal totalNonFinScore = BigDecimal.ZERO;

			for (ScoringMetrics nonFinMetric : getFinanceDetail().getNonFinScoringMetricList()) {
				addListGroup(nonFinMetric.getLovDescScoringCodeDesc(), this.listBoxNonFinancialScoRef,
						PennantConstants.PFF_CUSTCTG_CORP, null);

				BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
				BigDecimal totalGrpExecScore = BigDecimal.ZERO;

				if (getFinanceDetail().getScoringMetrics().containsKey(nonFinMetric.getScoringId())) {
					scoringMetricsList = getFinanceDetail().getScoringMetrics().get(nonFinMetric.getScoringId());

					if (scoringMetricsList != null) {

						for (ScoringMetrics subScoreMetric : scoringMetricsList) {
							boolean executed = false;
							executed = isExecuted(exeMap, subScoreMetric.getScoringId());

							if (StringUtils.contains(scoringGroup.getMandInputInStage(), userRole)) {
								executed = true;
							} else {
								executed = false;
							}

							scores = addListItem(subScoreMetric, this.listBoxNonFinancialScoRef, "N",
									nonFinMetric.getScoringId(), executed);
							totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
							totalGrpExecScore = totalGrpExecScore.add(scores[1]);
						}
					}

					nonFinMetric.setLovDescMetricMaxPoints(totalGrpMaxScore);
					totalNonFinScore = totalNonFinScore.add(totalGrpMaxScore);
					totalExecScore = totalExecScore.add(totalGrpExecScore);
				}
				addListFooter(totalGrpMaxScore, totalGrpExecScore, this.listBoxNonFinancialScoRef, "", "N",
						nonFinMetric.getScoringId());
			}
			this.maxNonFinTotScore.setValue(totalNonFinScore);
			//

			// Set Total Calculated Score Result
			// BigDecimal totScore = totalExecScore.add(totalNFRuleScore);
			BigDecimal totScore = totalExecScore;
			this.calTotScore.setValue(totScore);

			this.totalCorpScore.setValue(String.valueOf(totScore));

			// Credit Worth based on OBLIGOR Risk Grade Score
			BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
			if (totalScore.compareTo(new BigDecimal(0)) > 0) {
				obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100)))
						.divide(totalScore, 2, RoundingMode.HALF_DOWN);
			}
			this.corpCreditWoth.setValue(getScrSlab(scoringGroup.getFinRefId(), obligorScore, "", false));

			if (totScore.intValue() >= this.minScore.intValue()) {
				sufficientScore = true;
				this.isOverride.setDisabled(true);
			} else if (totScore.intValue() >= (this.overrideScore.intValue())) {
				sufficientScore = true;
				this.isOverride.setDisabled(false);
			} else {
				this.isOverride.setDisabled(true);
				sufficientScore = false;
			}

			setScoreSummaryStyle(sufficientScore, false);
		}

		exeMap = null;
		logger.debug("Leaving");
	}

	/**
	 * Method for Adding List Item For scoring details
	 * 
	 * @param scoringMetric
	 * @param listbox
	 * @param engine
	 * @param categoryValue
	 * @param groupId
	 * @param isExecute
	 * @return
	 */
	private BigDecimal[] addListItem(ScoringMetrics scoringMetric, Listbox listbox, String categoryValue, long groupId,
			boolean isExecute) {
		logger.debug("Entering");

		scores = new BigDecimal[2];

		Listitem item = null;
		Listcell lc = null;
		Decimalbox dupIntbox = null;
		Decimalbox orgIntbox = null;
		List<Object> list = null;

		item = new Listitem();

		lc = new Listcell(scoringMetric.getLovDescScoringCode());
		lc.setParent(item);

		lc = new Listcell(scoringMetric.getLovDescScoringCodeDesc());
		lc.setParent(item);

		if (StringUtils.equals("I", categoryValue)) {
			metricMaxScore = getMaxMetricScore(scoringMetric.getLovDescSQLRule());
		} else {
			metricMaxScore = scoringMetric.getLovDescMetricMaxPoints();
		}

		lc = new Listcell(String.valueOf(metricMaxScore));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		String key = PennantJavaUtil.concat(String.valueOf(groupId), "_", String.valueOf(scoringMetric.getScoringId()));

		if (finExecScoreMap == null) {
			finExecScoreMap = new HashMap<String, BigDecimal>();
		}

		if (finExecScoreMap.containsKey(key)) {
			finExecScoreMap.remove(key);
		}

		if (StringUtils.equals("I", categoryValue)) {
			metricExecScore = scoringMetric.getLovDescExecutedScore();
			finExecScoreMap.put(key, metricExecScore);
			lc = new Listcell(PennantApplicationUtil.formatAmount(metricExecScore, 2));
			lc.setStyle("text-align:right;font-weight:bold;color:#ff7300;");

		} else if ("F".equals(categoryValue)) {
			finExecScoreMap.put(key, metricExecScore);
			lc = new Listcell();
			dupIntbox = new Decimalbox(metricExecScore);
			dupIntbox.setVisible(false);
			dupIntbox.setReadonly(true);
			lc.appendChild(dupIntbox);

			orgIntbox = new Decimalbox(metricExecScore);
			orgIntbox.setReadonly(!isExecute);
			orgIntbox.setMaxlength(String.valueOf(metricMaxScore).length());
			orgIntbox.setId(key);

			list = new ArrayList<Object>();
			list.add(0, dupIntbox);
			list.add(1, orgIntbox);
			list.add(2, scoringMetric.getLovDescMetricMaxPoints());
			list.add(3, true);

			orgIntbox.addForward("onChange", window_ScoringDetailDialog, "onChangeNFRuleScore", list);
			orgIntbox.setWidth("60px");
			lc.appendChild(orgIntbox);

		} else if ("N".equals(categoryValue)) {
			finExecScoreMap.put(key, metricExecScore);
			lc = new Listcell();
			dupIntbox = new Decimalbox(metricExecScore);
			dupIntbox.setVisible(false);
			dupIntbox.setReadonly(true);
			lc.appendChild(dupIntbox);

			orgIntbox = new Decimalbox(metricExecScore);
			orgIntbox.setReadonly(!isExecute);
			orgIntbox.setMaxlength(String.valueOf(metricMaxScore).length());
			orgIntbox.setId(key);

			list = new ArrayList<Object>();
			list.add(0, dupIntbox);
			list.add(1, orgIntbox);
			list.add(2, scoringMetric.getLovDescMetricMaxPoints());
			list.add(3, false);

			orgIntbox.addForward("onChange", window_ScoringDetailDialog, "onChangeNFRuleScore", list);
			orgIntbox.setWidth("60px");
			lc.appendChild(orgIntbox);
		}

		lc.setParent(item);
		listbox.appendChild(item);

		logger.debug("Leaving");
		scores[0] = metricMaxScore;
		scores[1] = metricExecScore;
		return scores;
	}

	/**
	 * Method for calculating Total NFRule Score on Changing each rule Score
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public void onChangeNFRuleScore(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		List<FinanceScoreHeader> finScoreHeaderList = getFinanceDetail().getFinScoreHeaderList();

		List<Object> list = (List<Object>) event.getData();
		Decimalbox dupIntbox = (Decimalbox) list.get(0);
		Decimalbox orgIntbox = (Decimalbox) list.get(1);
		BigDecimal ruleMaxScore = (BigDecimal) list.get(2);
		boolean isFinScore = (Boolean) list.get(3);

		orgIntbox.setFormat("#0.00");
		if (orgIntbox.getValue() == null) {
			orgIntbox.setValue(BigDecimal.ZERO);
		}

		if (orgIntbox.getValue().compareTo(ruleMaxScore) > 0) {
			orgIntbox.setValue(dupIntbox.getValue());
			throw new WrongValueException(orgIntbox,
					PennantJavaUtil.concat("Score Value Must Be Less than or Equal to ", String.valueOf(ruleMaxScore)));
		}

		final BigDecimal dupNFRuleScore = dupIntbox.getValue();
		final BigDecimal orgNFRuleScore = orgIntbox.getValue();

		if (finExecScoreMap == null) {
			finExecScoreMap = new HashMap<String, BigDecimal>();
		}

		if (finExecScoreMap.containsKey(orgIntbox.getId())) {
			finExecScoreMap.remove(orgIntbox.getId());
		}

		finExecScoreMap.put(orgIntbox.getId(), orgNFRuleScore);

		if (isFinScore) {
			totalExecScore = totalExecScore.subtract(dupNFRuleScore).add(orgNFRuleScore);
			dupIntbox.setValue(orgNFRuleScore);
		} else {
			totalNFRuleScore = totalNFRuleScore.subtract(dupNFRuleScore).add(orgNFRuleScore);
			dupIntbox.setValue(orgNFRuleScore);
		}

		// Set Total Calculated Score Result
		BigDecimal totScore = totalExecScore.add(totalNFRuleScore);
		this.calTotScore.setValue(totScore);

		this.totalCorpScore.setValue(String.valueOf(totScore));

		// Credit Worth based on OBLIGOR Risk Grade Score
		BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
		BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100)))
				.divide(totalScore, 2, RoundingMode.HALF_DOWN);

		if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			this.corpCreditWoth.setValue(getScrSlab(finScoreHeaderList.get(0).getGroupId(), obligorScore, "", false));
		}

		if (totScore.intValue() >= this.minScore.intValue()) {
			this.isOverride.setDisabled(true);
			sufficientScore = true;
		} else if (this.isOverride.isChecked() && totScore.intValue() >= (this.overrideScore.intValue())) {
			sufficientScore = true;
			this.isOverride.setDisabled(false);
		} else if (totScore.intValue() >= (this.overrideScore.intValue())) {
			sufficientScore = false;
			this.isOverride.setDisabled(false);
		} else {
			this.isOverride.setDisabled(true);
			sufficientScore = false;
		}

		setScoreSummaryStyle(sufficientScore, false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Check Override Checkbox For Corporate Score Details
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$isOverride(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		List<FinanceScoreHeader> finScoreHeaderList = getFinanceDetail().getFinScoreHeaderList();
		// Set Total Calculated Score Result
		BigDecimal totScore = totalExecScore.add(totalNFRuleScore);

		this.totalCorpScore.setValue(String.valueOf(totScore));

		// Credit Worth based on OBLIGOR Risk Grade Score
		BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
		BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100)))
				.divide(totalScore, 2, RoundingMode.HALF_DOWN);

		if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			this.corpCreditWoth.setValue(getScrSlab(finScoreHeaderList.get(0).getGroupId(), obligorScore, "", false));
		}

		if (this.isOverride.isChecked()) {
			if (totScore.compareTo(new BigDecimal(this.minScore.intValue())) < 0) {

				if (totScore.compareTo(new BigDecimal(this.overrideScore.intValue())) >= 0) {
					sufficientScore = true;
				}
			}

			if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
				finScoreHeaderList.get(0).setOverride(true);
			}
		} else if (totScore.compareTo(new BigDecimal(this.minScore.intValue())) < 0) {
			sufficientScore = false;
		}

		setScoreSummaryStyle(sufficientScore, false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Adding List Group to listBox in Corporation
	 * 
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListGroup(String groupCodeDesc, Listbox listbox, String ctgType, Object object) {
		logger.debug("Entering");

		FinanceScoreHeader header = null;
		FinanceReferenceDetail detail = null;
		Listgroup listgroup = new Listgroup();
		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(ctgType)) {
			Listcell lc = null;
			Label label = null;
			Space space = null;

			if (object instanceof FinanceScoreHeader) {
				header = (FinanceScoreHeader) object;

				lc = new Listcell(PennantJavaUtil.concat(header.getGroupCode(), " - ", header.getGroupCodeDesc()));
				lc.setParent(listgroup);

				lc = new Listcell();
				lc.setSpan(3);
				label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_MinScore"), " :",
						String.valueOf(header.getMinScore())));

				lc.appendChild(label);
				space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);

				lc.setParent(listgroup);
			}

			if (object instanceof FinanceReferenceDetail) {
				detail = (FinanceReferenceDetail) object;

				if (!this.btnScoringGroup.isVisible() && detail.getMandInputInStage().contains(userRole)) {
					this.btnScoringGroup.setVisible(true);
				}
				lc = new Listcell(PennantJavaUtil.concat(detail.getLovDescCodelov(), "-", detail.getLovDescNamelov()));
				lc.setParent(listgroup);

				lc = new Listcell();
				lc.setSpan(3);
				label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_MinScore"), " :",
						String.valueOf(detail.getLovDescminScore())));
				lc.appendChild(label);
				space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);

				lc.setParent(listgroup);
			}

		} else {
			listgroup.setLabel(groupCodeDesc);
		}

		listgroup.setOpen(true);
		listbox.appendChild(listgroup);
		logger.debug("Leaving");
	}

	/**
	 * Method for Adding List Group to listBox in Corporation
	 * 
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListFooter(BigDecimal totalMaxGrpScore, BigDecimal totalExecGrpScore, Listbox listbox,
			String creditWorth, String ctgType, long grpId) {
		logger.debug("Entering");

		Listgroupfoot listgroupfoot = new Listgroupfoot();

		Listcell cell = null;

		if ("I".equals(ctgType)) {
			cell = new Listcell(Labels.getLabel("label_Credit_Worth"));
			cell.setStyle("text-align:right;font-weight:normal;");
			listgroupfoot.appendChild(cell);

			cell = new Listcell();
			Label label = new Label(getScrSlab(grpId, totalExecGrpScore, creditWorth, true));
			label.setId(PennantJavaUtil.concat(String.valueOf(grpId), "_CW"));
			label.setStyle("float:left;font-weight:bold;");
			cell.appendChild(label);

			label = new Label(Labels.getLabel("label_Group_Grand_Total"));
			label.setStyle("float:right;");
			cell.appendChild(label);
			listgroupfoot.appendChild(cell);
		} else if ("F".equals(ctgType) || "N".equals(ctgType)) {
			cell = new Listcell(Labels.getLabel("label_Sub_Group_Total"));
			cell.setSpan(2);
			cell.setStyle("font-weight:bold;text-align:right;");
			listgroupfoot.appendChild(cell);
		}

		cell = new Listcell(PennantApplicationUtil.formatAmount(totalMaxGrpScore, 2));
		cell.setStyle("font-weight:bold;text-align:right;");
		listgroupfoot.appendChild(cell);

		/*
		 * if("N".equals(ctgType)){ cell = new Listcell(""); }else{
		 */
		cell = new Listcell();
		Label label = new Label(PennantApplicationUtil.formatAmount(totalExecGrpScore, 2));
		label.setStyle("font-weight:bold;float:right;");
		cell.appendChild(label);
		if ("I".equals(ctgType)) {
			label.setId(grpId + "_TS");
		}
		// }
		listgroupfoot.appendChild(cell);

		listbox.appendChild(listgroupfoot);
		logger.debug("Leaving");
	}

	private BigDecimal getMaxMetricScore(String rule) {
		BigDecimal max = BigDecimal.ZERO;
		String[] codevalue = rule.split("Result");

		for (int i = 0; i < codevalue.length; i++) {
			if (i == 0) {
				continue;
			}

			if (codevalue[i] != null && codevalue[i].contains(";")) {
				String code = codevalue[i].substring(codevalue[i].indexOf('=') + 1, codevalue[i].indexOf(';'));

				if (code.contains("'")) {
					code = code.replace("'", "");
				}

				if (code.contains("?")) {
					String[] fields = code.split("[^a-zA-Z]+");

					Map<String, Object> fieldValuesMap = new HashMap<String, Object>();

					for (int j = 0; j < fields.length; j++) {
						if (!StringUtils.isEmpty(fields[j])) {
							fieldValuesMap.put(fields[j], BigDecimal.ONE);
						}
					}
					code = String.valueOf(RuleExecutionUtil.executeRule("Result = " + code + ";", fieldValuesMap, null,
							RuleReturnType.INTEGER)); // FIXME Code should be checked
				}

				if (new BigDecimal(code.trim()).compareTo(max) > 0) {
					max = new BigDecimal(code.trim());
				}
			}
		}

		return max;
	}

	/**
	 * Method to show the credit worthiness in Scoring Tab *
	 * 
	 * @param refId         (long)
	 * @param grpTotalScore (int)
	 * @return String
	 */
	private String getScrSlab(long refId, BigDecimal grpTotalScore, String execCreditWorth, boolean isRetail) {
		logger.debug("Entering");
		List<ScoringSlab> slabList = getFinanceDetail().getScoringSlabs().get(refId);
		String creditWorth = "None";

		if (slabList != null && !slabList.isEmpty()) {
			for (ScoringSlab slab : slabList) {

				if (isRetail) {
					if (grpTotalScore.compareTo(new BigDecimal(slab.getScoringSlab())) >= 0) {
						creditWorth = slab.getCreditWorthness();
						break;
					}
				} else {
					if (grpTotalScore.compareTo(new BigDecimal(slab.getScoringSlab())) <= 0) {
						creditWorth = slab.getCreditWorthness();
					} else {
						break;
					}
				}
			}
		} else if (StringUtils.isNotBlank(execCreditWorth)) {
			creditWorth = execCreditWorth;
		}

		logger.debug("Leaving");
		return creditWorth;
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws InterruptedException
	 */
	public void doSave_ScoreDetail(FinanceDetail aFinanceDetail) throws InterruptedException {
		logger.debug("Entering ");

		if (!scoreExecuted && this.btnScoringGroup.isVisible()) {
			throw new InterruptedException();
		}

		setFinanceDetail(aFinanceDetail);
		executeScore();
		getFinanceDetail().getScoreDetailListMap().clear();
		List<FinanceScoreHeader> scoreHeaderList = null;
		BigDecimal totalCheckScore = BigDecimal.ZERO;

		List<FinanceScoreHeader> finScoreHeaderList = getFinanceDetail().getFinScoreHeaderList();
		FinanceScoreHeader header = null;

		if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			header = getFinanceDetail().getFinScoreHeaderList().get(0);
		} else {
			header = new FinanceScoreHeader();
		}

		// Execute Scoring metrics and Display Total Score
		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(custCtgType)) {

			scoreHeaderList = new ArrayList<FinanceScoreHeader>();
			for (FinanceReferenceDetail detail : getFinanceDetail().getScoringGroupList()) {

				header.setGroupId(detail.getFinRefId());
				header.setMinScore(detail.getLovDescminScore());
				header.setGroupCodeDesc(detail.getLovDescNamelov());

				if (getFinanceDetail().getScoringMetrics().containsKey(detail.getFinRefId())) {
					List<FinanceScoreDetail> scoreDetails = new ArrayList<FinanceScoreDetail>();
					List<ScoringMetrics> metrics = getFinanceDetail().getScoringMetrics().get(detail.getFinRefId());
					FinanceScoreDetail scoreDetail = null;

					for (ScoringMetrics metric : metrics) {
						scoreDetail = new FinanceScoreDetail();
						scoreDetail.setRuleId(metric.getScoringId());
						scoreDetail.setSubGroupId(0);
						scoreDetail.setRuleCode(metric.getLovDescScoringCode());
						scoreDetail.setRuleCodeDesc(metric.getLovDescScoringCodeDesc());
						scoreDetail.setMaxScore(getMaxMetricScore(metric.getLovDescSQLRule()));

						BigDecimal execScore = BigDecimal.ZERO;

						String key = PennantJavaUtil.concat(String.valueOf(detail.getFinRefId()), "_",
								String.valueOf(metric.getScoringId()));

						if (finExecScoreMap != null && finExecScoreMap.containsKey(key)) {
							execScore = finExecScoreMap.get(key);
						}

						totalCheckScore = totalCheckScore.add(execScore);
						scoreDetail.setExecScore(execScore);
						scoreDetails.add(scoreDetail);
					}

					if (!scoreDetails.isEmpty()) {
						getFinanceDetail().getScoreDetailListMap().put(header.getHeaderId(), scoreDetails);
					}
				}

				header.setCreditWorth("");
				String key = PennantJavaUtil.concat(String.valueOf(detail.getFinRefId()), "_CW");

				if (this.listBoxRetailScoRef.getFellowIfAny(key) != null) {
					Label label = (Label) this.listBoxRetailScoRef.getFellowIfAny(key);
					header.setCreditWorth(label.getValue());
				}

				key = PennantJavaUtil.concat(detail.getLovDescCodelov(), "_CB");

				scoreHeaderList.add(header);
			}

		} else if (PennantConstants.PFF_CUSTCTG_CORP.equals(custCtgType)
				|| PennantConstants.PFF_CUSTCTG_SME.equals(custCtgType)) {
			scoreHeaderList = new ArrayList<FinanceScoreHeader>();

			for (FinanceReferenceDetail detail : getFinanceDetail().getScoringGroupList()) {

				header.setGroupId(detail.getFinRefId());
				header.setMinScore(this.minScore.intValue());
				header.setOverride(this.isOverride.isChecked());
				header.setOverrideScore(0);

				if (header.isOverride()) {
					header.setOverrideScore(this.overrideScore.intValue());
				}

				// Credit Worth based on OBLIGOR Risk Grade Score
				BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
				BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100)))
						.divide(totalScore, 2, RoundingMode.HALF_DOWN);

				header.setCreditWorth(getScrSlab(detail.getFinRefId(), obligorScore, "", false));
				scoreHeaderList.add(header);

				List<FinanceScoreDetail> scoreDetails = new ArrayList<FinanceScoreDetail>();
				FinanceScoreDetail scoreDetail = null;

				for (ScoringMetrics finMetric : getFinanceDetail().getFinScoringMetricList()) {

					if (getFinanceDetail().getScoringMetrics().containsKey(finMetric.getScoringId())) {
						List<ScoringMetrics> metrics = getFinanceDetail().getScoringMetrics()
								.get(finMetric.getScoringId());
						for (ScoringMetrics metric : metrics) {
							scoreDetail = new FinanceScoreDetail();
							scoreDetail.setRuleId(metric.getScoringId());
							scoreDetail.setSubGroupId(finMetric.getScoringId());
							scoreDetail.setMaxScore(metric.getLovDescMetricMaxPoints());// getMaxMetricScore(metric.getLovDescSQLRule())
							scoreDetail.setCategoryType(finMetric.getCategoryType());
							scoreDetail.setSubGrpCodeDesc(finMetric.getLovDescScoringCodeDesc());
							scoreDetail.setRuleCode(metric.getLovDescScoringCode());
							scoreDetail.setRuleCodeDesc(metric.getLovDescScoringCodeDesc());

							BigDecimal execScore = BigDecimal.ZERO;
							String key = PennantJavaUtil.concat(String.valueOf(finMetric.getScoringId()), "_",
									String.valueOf(metric.getScoringId()));

							if (finExecScoreMap != null && finExecScoreMap.containsKey(key)) {
								execScore = finExecScoreMap.get(key);
							}
							totalCheckScore = totalCheckScore.add(execScore);
							scoreDetail.setExecScore(execScore);
							scoreDetails.add(scoreDetail);
						}
					}
				}

				for (ScoringMetrics nonFinMetric : getFinanceDetail().getNonFinScoringMetricList()) {

					if (getFinanceDetail().getScoringMetrics().containsKey(nonFinMetric.getScoringId())) {
						List<ScoringMetrics> metrics = getFinanceDetail().getScoringMetrics()
								.get(nonFinMetric.getScoringId());

						if (metrics != null) {
							for (ScoringMetrics metric : metrics) {
								scoreDetail = new FinanceScoreDetail();
								scoreDetail.setRuleId(metric.getScoringId());
								scoreDetail.setSubGroupId(nonFinMetric.getScoringId());
								scoreDetail.setMaxScore(metric.getLovDescMetricMaxPoints());
								scoreDetail.setCategoryType(nonFinMetric.getCategoryType());
								scoreDetail.setSubGrpCodeDesc(nonFinMetric.getLovDescScoringCodeDesc());
								scoreDetail.setRuleCode(metric.getLovDescScoringCode());
								scoreDetail.setRuleCodeDesc(metric.getLovDescScoringCodeDesc());
								BigDecimal execScore = BigDecimal.ZERO;
								String key = PennantJavaUtil.concat(String.valueOf(nonFinMetric.getScoringId()), "_",
										String.valueOf(metric.getScoringId()));

								if (finExecScoreMap != null && finExecScoreMap.containsKey(key)) {
									execScore = finExecScoreMap.get(key);
								}
								totalCheckScore = totalCheckScore.add(execScore);
								scoreDetail.setExecScore(execScore);
								scoreDetails.add(scoreDetail);
							}
						}
					}
				}

				if (scoreDetails.size() > 0) {
					getFinanceDetail().getScoreDetailListMap().put(header.getHeaderId(), scoreDetails);
				}
			}
		}

		aFinanceDetail.setFinScoreHeaderList(scoreHeaderList);

		processDevaition(scoreHeaderList, getFinanceDetail());
		sufficientScore = checkScoreisSufficientWithDeviation(financeDetail.getFinScoreHeaderList(),
				financeDetail.getScoreDetailListMap());
		aFinanceDetail.setSufficientScore(sufficientScore);

		// Score Card preparation as Per Calculation Formula
		totalCheckScore = totalCheckScore.add(new BigDecimal(206));
		totalCheckScore = new BigDecimal(100)
				.add(totalCheckScore.subtract(new BigDecimal(100)).multiply(BigDecimal.valueOf(1.875)));
		aFinanceDetail.setScore(totalCheckScore);

		logger.debug("Leaving ");

	}

	private void setScoreSummaryStyle(boolean sufficientScore, boolean deviationSuff) {
		getFinanceDetail().setSufficientScore(sufficientScore);
		if (sufficientScore) {
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
			if (deviationSuff) {
				this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr_Deviation.label"));
			} else {
				this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr.label"));
			}
		} else {
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_InSuffScr.label"));
		}
	}

	private boolean isExecuted(Map<String, String> exeMap, long scoringId) {
		metricExecScore = BigDecimal.ZERO;
		metricMaxScore = BigDecimal.ZERO;
		boolean executed = false;
		FinanceScoreHeader header = null;

		if (getFinanceDetail().getFinScoreHeaderList() != null
				&& !getFinanceDetail().getFinScoreHeaderList().isEmpty()) {
			header = getFinanceDetail().getFinScoreHeaderList().get(0);
		}

		if (header != null) {
			List<FinanceScoreDetail> scoreDetailList = getFinanceDetail().getScoreDetailListMap()
					.get(header.getHeaderId());
			if (scoreDetailList != null) {
				for (FinanceScoreDetail financeScoreDetail : scoreDetailList) {
					if (financeScoreDetail.getRuleId() == scoringId) {
						if (exeMap.get(financeScoreDetail.getRuleCodeDesc()) == null) {
							exeMap.put(financeScoreDetail.getRuleCodeDesc(), financeScoreDetail.getRuleCodeDesc());
							metricMaxScore = financeScoreDetail.getMaxScore();
							metricExecScore = financeScoreDetail.getExecScore();
							executed = true;

							if (header.getHeaderId() == financeScoreDetail.getHeaderId()) {
								executed = false;
							}
							break;
						}
					}
				}
			}
		}

		return executed;
	}

	/**
	 * To Check the deviation if score not matched
	 * 
	 * @param scoreHeaderList
	 * @param aFinanceDetail
	 */
	public void processDevaition(List<FinanceScoreHeader> scoreHeaderList, FinanceDetail aFinanceDetail) {
		logger.debug(" Entering ");

		List<FinanceDeviations> scoringDeviations = new ArrayList<FinanceDeviations>();

		for (FinanceScoreHeader financeScoreHeader : scoreHeaderList) {
			int minScore = financeScoreHeader.getMinScore();
			List<FinanceScoreDetail> list = getFinanceDetail().getScoreDetailListMap()
					.get(financeScoreHeader.getHeaderId());
			if (list == null || list.isEmpty()) {
				throw new EmptyResultDataAccessException(1);
			}
			BigDecimal totcalScore = new BigDecimal(0);

			for (FinanceScoreDetail financeScoreDetail : list) {
				totcalScore = totcalScore.add(financeScoreDetail.getExecScore());
			}

			if (totcalScore.intValue() < minScore) {

				FinanceDeviations scoreDev = deviationExecutionCtrl.checkScoringDeviations(aFinanceDetail,
						financeScoreHeader.getGroupId(), minScore, totcalScore.intValue());
				if (scoreDev != null) {
					scoringDeviations.add(scoreDev);
				}
			}

		}

		deviationExecutionCtrl.fillDeviationListbox(scoringDeviations, getUserRole(), DeviationConstants.TY_SCORE);

		logger.debug(" Leaving ");
	}

	/**
	 * @param financeDetail
	 * @return
	 */
	public boolean checkScoreisSufficientWithDeviation(List<FinanceScoreHeader> financeScoreHeaders,
			Map<Long, List<FinanceScoreDetail>> scoreMap) {
		logger.debug(" Entering ");

		boolean scoreOk = true;

		for (FinanceScoreHeader financeScoreHeader : financeScoreHeaders) {

			int minScore = financeScoreHeader.getMinScore();

			List<FinanceScoreDetail> list = scoreMap.get(financeScoreHeader.getHeaderId());
			BigDecimal totcalScore = new BigDecimal(0);

			for (FinanceScoreDetail financeScoreDetail : list) {
				totcalScore = totcalScore.add(financeScoreDetail.getExecScore());
			}

			if (totcalScore.intValue() < minScore) {
				scoreOk = checkDevaitionbyScoreHeader(financeScoreHeader.getGroupId(),
						minScore - totcalScore.intValue());
				if (!scoreOk) {
					return false;
				}
			}
		}

		logger.debug(" Leaving ");
		return scoreOk;
	}

	/**
	 * @param financeDetail
	 * @return
	 */
	public Boolean[] isScoreSufficientWithDeviation(List<FinanceReferenceDetail> financeScoreHeaders,
			Map<Long, List<ScoringMetrics>> scoreMap) {
		logger.debug(" Entering ");
		Boolean[] socres = new Boolean[2];

		boolean scoreOk = true;
		boolean scoreOkWithDeviation = false;
		for (FinanceReferenceDetail financeScoreHeader : financeScoreHeaders) {

			int minScore = financeScoreHeader.getLovDescminScore();

			List<ScoringMetrics> list = scoreMap.get(financeScoreHeader.getFinRefId());
			BigDecimal totcalScore = new BigDecimal(0);

			for (ScoringMetrics financeScoreDetail : list) {
				totcalScore = totcalScore.add(financeScoreDetail.getLovDescExecutedScore());
			}

			if (totcalScore.intValue() < minScore) {
				scoreOk = checkDevaitionbyScoreHeader(financeScoreHeader.getFinRefId(),
						minScore - totcalScore.intValue());
				if (!scoreOk) {
					scoreOk = false;
					break;
				} else {
					scoreOkWithDeviation = true;
				}
			}
		}
		socres[0] = scoreOk;
		socres[1] = scoreOkWithDeviation;

		logger.debug(" Leaving ");
		return socres;
	}

	/**
	 * @param financeScoreHeader
	 * @param devValue
	 * @return
	 */
	public boolean checkDevaitionbyScoreHeader(long id, int devValue) {
		logger.debug(" Entering ");

		List<FinanceDeviations> devList = deviationExecutionCtrl.getFinanceDeviations();

		FinanceDeviations deviation = null;

		// Check in current deviations
		if (devList != null && !devList.isEmpty()) {

			for (FinanceDeviations financeDeviations : devList) {

				if (!financeDeviations.getModule().equals(DeviationConstants.TY_SCORE)) {
					continue;
				}
				if (financeDeviations.getDeviationCode().equals(String.valueOf(id))
						&& financeDeviations.getDeviationValue().equals(String.valueOf(devValue))) {
					deviation = financeDeviations;
					break;
				}
			}
		}

		// Check it is approved deviations

		if (deviation == null) {
			List<FinanceDeviations> approvedList = getFinanceDetail().getApprovedFinanceDeviations();
			if (approvedList != null && !approvedList.isEmpty()) {

				for (FinanceDeviations financeDeviations : approvedList) {
					if (PennantConstants.RCD_STATUS_REJECTED
							.equals(StringUtils.trimToEmpty(financeDeviations.getApprovalStatus()))) {
						continue;
					}
					if (financeDeviations.getDeviationCode().equals(String.valueOf(id))
							&& financeDeviations.getDeviationValue().equals(String.valueOf(devValue))) {
						deviation = financeDeviations;
						break;
					}
				}
			}
		}

		if (deviation == null) {
			logger.debug(" Leaving ");
			return false;
		} else {
			if ("".equals(deviation.getDelegationRole())) {
				logger.debug(" Leaving ");
				return false;
			} else {
				logger.debug(" Leaving ");
				return true;
			}
		}

	}

	/**
	 * append Finance Basic detail header to current window
	 */
	private void appendFinBasicDetails() {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
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

	public boolean isSufficientScore() {
		return sufficientScore;
	}

	public void setSufficientScore(boolean sufficientScore) {
		this.sufficientScore = sufficientScore;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public CreditReviewSummaryData getCreditReviewSummaryData() {
		return creditReviewSummaryData;
	}

	public void setCreditReviewSummaryData(CreditReviewSummaryData creditReviewSummaryData) {
		this.creditReviewSummaryData = creditReviewSummaryData;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setScoringDetailService(ScoringDetailService scoringDetailService) {
		this.scoringDetailService = scoringDetailService;
	}

	public ScoringDetailService getScoringDetailService() {
		return scoringDetailService;
	}

	public RatingCodeService getRatingCodeService() {
		return ratingCodeService;
	}

	public void setRatingCodeService(RatingCodeService ratingCodeService) {
		this.ratingCodeService = ratingCodeService;
	}

	public void setScoreExecuted(boolean scoreExecuted) {
		this.scoreExecuted = scoreExecuted;
	}

	public boolean isScoreExecuted() {
		return scoreExecuted;
	}

	private String getUserRole() {
		try {
			return (String) getFinanceMainDialogCtrl().getClass().getMethod("getUserRole")
					.invoke(getFinanceMainDialogCtrl());
		} catch (Exception e) {
			logger.debug(e);
		}
		return "";
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}
}