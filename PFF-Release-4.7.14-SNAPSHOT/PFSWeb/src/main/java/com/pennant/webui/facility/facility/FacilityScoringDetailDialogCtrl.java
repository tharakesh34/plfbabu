/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceMainDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.facility.facility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
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

import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.service.bmtmasters.RatingCodeService;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl.CreditReviewSummaryData;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class FacilityScoringDetailDialogCtrl extends GFCBaseCtrl<FinanceScoreDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(FacilityScoringDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ScoringDetailDialog; 
	protected Button btnScoringGroup; 
	protected Label label_TotalScore; 
	protected Label totalCorpScore; 
	protected Label label_CorpCreditWoth; 
	protected Label corpCreditWoth; 
	protected Label label_ScoreSummary; 
	protected Label label_ScoreSummaryVal; 
	protected Groupbox finScoreDetailGroup; 
	protected Listbox listBoxRetailScoRef; 
	protected Listbox listBoxFinancialScoRef; 
	protected Listbox listBoxNonFinancialScoRef; 
	protected Tab finScoreMetricTab; 
	protected Tab nonFinScoreMetricTab; 
	protected Decimalbox maxFinTotScore; 
	protected Decimalbox maxNonFinTotScore; 
	protected Intbox minScore; 
	protected Decimalbox calTotScore; 
	protected Checkbox isOverride; 
	protected Intbox overrideScore; 
	protected Row row_finScoreOverride; 
	private Map<String, BigDecimal> finExecScoreMap = null;
	// private Map<Long, Boolean> retailOverrideCheckMap = null;
	// External Fields usage for Individuals ----> Scoring Details
	private String custCtgType = "";
	private transient boolean sufficientScore = true;
	private BigDecimal totalExecScore = BigDecimal.ZERO;
	private BigDecimal totalFinScore = BigDecimal.ZERO;
	private BigDecimal totalNFRuleScore = BigDecimal.ZERO;
	BigDecimal metricMaxScore = BigDecimal.ZERO;
	BigDecimal metricExecScore = BigDecimal.ZERO;
	private transient boolean validationOn;
	// not auto wired variables
	private Facility facility = null; // over handed per parameters
										// parameters
	// Bean Setters by application Context
	private ScoringDetailService scoringDetailService;
	private CreditReviewSummaryData creditReviewSummaryData;
	private RatingCodeService ratingCodeService;
	private Object ctrlObject;
	private BigDecimal[] scores = null;
	String userRole = "";
	private boolean isWIF = false;
	private boolean enqModule = false;

	/**
	 * default constructor.<br>
	 */
	public FacilityScoringDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScoringDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScoringDetailDialog);

		try {
			if (arguments.containsKey("facility")) {
				this.facility = (Facility) arguments.get("facility");
			}
			if (arguments.containsKey("control")) {
				setCtrlObject(arguments.get("control"));
			}
			if (arguments.containsKey("custCtgType")) {
				custCtgType = (String) arguments.get("custCtgType");
			}
			if (arguments.containsKey("isWIF")) {
				isWIF = (Boolean) arguments.get("isWIF");
			}
			if (arguments.containsKey("userRole")) {
				userRole = (String) arguments.get("userRole");
			}
			if (arguments.containsKey("enqModule")) {
				enqModule = true;
			} else {
				enqModule = false;
			}

			// set Field Properties
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ScoringDetailDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws Exception 
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering");
		fillFinCustScoring();
		try {
			ctrlObject.getClass().getMethod("setFacilityScoringDetailDialogCtrl", this.getClass()).invoke(ctrlObject, this);
		
		if (isWIF) {
			doExecuteScoring();
		}
		getBorderLayoutHeight();
	
		this.listBoxRetailScoRef.setHeight(this.borderLayoutHeight - 200 + "px");

		this.listBoxFinancialScoRef.setHeight(this.borderLayoutHeight - 250 + "px");
		this.listBoxNonFinancialScoRef.setHeight(this.borderLayoutHeight - 250 + "px");

		this.window_ScoringDetailDialog.setHeight(this.borderLayoutHeight - 75 + "px");
	} catch (UiException e) {
		logger.error("Exception: ", e);
		this.window_ScoringDetailDialog.onClose();
	} catch (Exception e) {
		throw e;
	}
		logger.debug("Leaving");
	}

	public void fillFinCustScoring() throws InterruptedException {
		logger.debug("Entering");
		// Set Customer Data to Calculate the Score
		this.listBoxRetailScoRef.getItems().clear();
		this.listBoxFinancialScoRef.getItems().clear();
		this.listBoxNonFinancialScoRef.getItems().clear();
		// Execute Scoring metrics and Display Total Score
		if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, custCtgType)) {
			// Individual customer
			this.listBoxRetailScoRef.setVisible(true);
			this.finScoreDetailGroup.setVisible(false);
			this.label_ScoreSummaryVal.setValue("");
			doFillRetailScoringListbox(getFacility().getScoringGroupList(), this.listBoxRetailScoRef, false);
		} else if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_CORP, custCtgType) || 
				StringUtils.equals(PennantConstants.PFF_CUSTCTG_SME, custCtgType)) {
			// corporate or bank customer
			this.listBoxRetailScoRef.setVisible(false);
			this.finScoreDetailGroup.setVisible(true);
			doFillCorpScoringMetricDetails();
		}
		logger.debug("Leaving");
	}
	/**
	 * Method for Filling Corporate Scoring Details
	 * 
	 * @param isExecute
	 * @throws InterruptedException 
	 */
	private void doFillCorpScoringMetricDetails() throws InterruptedException {
		logger.debug("Entering");
		List<FinanceScoreHeader> finScoreHeaderList = getFacility().getFinScoreHeaderList();
		List<FacilityReferenceDetail> refDetails = getFacility().getScoringGroupList();
		List<ScoringMetrics> scoringMetricsList = null;
		Map<String, String> exeMap = new HashMap<String, String>();
		this.label_TotalScore.setVisible(true);
		this.totalCorpScore.setVisible(true);
		this.label_CorpCreditWoth.setVisible(true);
		this.corpCreditWoth.setVisible(true);
		this.label_ScoreSummary.setVisible(true);
		this.label_ScoreSummaryVal.setVisible(true);
		totalExecScore = BigDecimal.ZERO;
		this.btnScoringGroup.setVisible(false);
		if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			this.isOverride.setChecked(finScoreHeaderList.get(0).isOverride());
		}
		if (refDetails != null && !refDetails.isEmpty()) {
			FacilityReferenceDetail scoringGroup = refDetails.get(0);
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
			for (ScoringMetrics scoringMetric : getFacility().getFinScoringMetricList()) {
				addListGroup(scoringMetric.getLovDescScoringCodeDesc(), this.listBoxFinancialScoRef, PennantConstants.PFF_CUSTCTG_CORP, null, false);
				BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
				BigDecimal totalGrpExecScore = BigDecimal.ZERO;
				if (getFacility().getScoringMetrics().containsKey(scoringMetric.getScoringId())) {
					scoringMetricsList = getFacility().getScoringMetrics().get(scoringMetric.getScoringId());
					if (scoringMetricsList != null) {
						for (ScoringMetrics subScoreMetric : scoringMetricsList) {
							boolean executed = false;
							executed = isExecuted(exeMap, subScoreMetric.getScoringId());
							if (StringUtils.contains(scoringGroup.getMandInputInStage(), userRole)) {
								executed = true;
							} else {
								executed = false;
							}
							if (enqModule) {
								executed = false;
							}
							scores = addListItem(subScoreMetric, this.listBoxFinancialScoRef, "F", scoringMetric.getScoringId(), executed);
							totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
							totalGrpExecScore = totalGrpExecScore.add(scores[1]);
						}
					}
					scoringMetric.setLovDescMetricMaxPoints(totalGrpMaxScore);
					totalFinScore = totalFinScore.add(totalGrpMaxScore);
					totalExecScore = totalExecScore.add(totalGrpExecScore);
				}
				addListFooter(totalGrpMaxScore, totalGrpExecScore, this.listBoxFinancialScoRef, "", "F", scoringMetric.getScoringId());
			}
			this.maxFinTotScore.setValue(totalFinScore);
			// totalNFRuleScore = BigDecimal.ZERO;
			this.listBoxNonFinancialScoRef.getItems().clear();
			BigDecimal totalNonFinScore = BigDecimal.ZERO;
			for (ScoringMetrics nonFinMetric : getFacility().getNonFinScoringMetricList()) {
				addListGroup(nonFinMetric.getLovDescScoringCodeDesc(), this.listBoxNonFinancialScoRef, PennantConstants.PFF_CUSTCTG_CORP, null, false);
				BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
				BigDecimal totalGrpExecScore = BigDecimal.ZERO;
				if (getFacility().getScoringMetrics().containsKey(nonFinMetric.getScoringId())) {
					scoringMetricsList = getFacility().getScoringMetrics().get(nonFinMetric.getScoringId());
					if (scoringMetricsList != null) {
						for (ScoringMetrics subScoreMetric : scoringMetricsList) {
							boolean executed = false;
							executed = isExecuted(exeMap, subScoreMetric.getScoringId());
							if (StringUtils.contains(scoringGroup.getMandInputInStage(), userRole)) {
								executed = true;
							} else {
								executed = false;
							}
							if (enqModule) {
								executed = false;
							}
							scores = addListItem(subScoreMetric, this.listBoxNonFinancialScoRef, "N", nonFinMetric.getScoringId(), executed);
							totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
							totalGrpExecScore = totalGrpExecScore.add(scores[1]);
						}
					}
					nonFinMetric.setLovDescMetricMaxPoints(totalGrpMaxScore);
					totalNonFinScore = totalNonFinScore.add(totalGrpMaxScore);
					totalExecScore = totalExecScore.add(totalGrpExecScore);
				}
				addListFooter(totalGrpMaxScore, totalGrpExecScore, this.listBoxNonFinancialScoRef, "", "N", nonFinMetric.getScoringId());
			}
			this.maxNonFinTotScore.setValue(totalNonFinScore);
			//
			// Set Total Calculated Score Result
			// BigDecimal totScore = totalExecScore.add(totalNFRuleScore);
			BigDecimal totScore = totalExecScore;
			this.calTotScore.setValue(totScore);
			this.totalCorpScore.setValue(String.valueOf(totScore));
			
			//Credit Worth based on OBLIGOR Risk Grade Score
			BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
			BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100))).divide(totalScore, 2, RoundingMode.HALF_DOWN);
			
			this.corpCreditWoth.setValue(getScrSlab(scoringGroup.getFinRefId(),obligorScore, "", false));
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
			setScoreSummaryStyle(sufficientScore);
		}
		exeMap = null;
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Finance Scoring Details List
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnScoringGroup(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doExecuteScoring();
		logger.debug("Leaving" + event.toString());
	}

	public void doExecuteScoring() throws InterruptedException {
		if (getFacility() != null) {
			this.listBoxRetailScoRef.getItems().clear();
			this.listBoxFinancialScoRef.getItems().clear();
			this.listBoxNonFinancialScoRef.getItems().clear();
			if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, custCtgType)) {
				this.label_ScoreSummaryVal.setValue("");
				setSufficientScore(false);
				doFillRetailScoringListbox(getFacility().getScoringGroupList(), this.listBoxRetailScoRef, true);
			} else if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_CORP, custCtgType) || 
					StringUtils.equals(PennantConstants.PFF_CUSTCTG_SME, custCtgType)) {
				this.listBoxRetailScoRef.setVisible(false);
				this.finScoreDetailGroup.setVisible(true);
				doFillCorpScoringMetricDetails();
			}
		}
	}

	/**
	 * Method to fill list box in Scoring Group Tab <br>
	 * 
	 * @param financeReferenceDetail
	 *            (List<FinanceReferenceDetail>)
	 * @param listbox
	 *            (Listbox)
	 * @param execute
	 *            (boolean)
	 * @throws InterruptedException 
	 */
	public void doFillRetailScoringListbox(List<FacilityReferenceDetail> financeReferenceDetail, Listbox listbox, boolean isExecute) throws InterruptedException {
		logger.debug("Entering");
		this.label_TotalScore.setVisible(false);
		this.totalCorpScore.setVisible(false);
		this.label_CorpCreditWoth.setVisible(false);
		this.corpCreditWoth.setVisible(false);
		this.label_ScoreSummary.setVisible(true);
		this.label_ScoreSummaryVal.setVisible(true);
		this.btnScoringGroup.setVisible(false);
		Checkbox cb = null;
		String key = "";
		List<FinanceScoreHeader> scoringHeaderList = getFacility().getFinScoreHeaderList();
		if ((financeReferenceDetail != null && !financeReferenceDetail.isEmpty()) || (scoringHeaderList != null && !scoringHeaderList.isEmpty())) {
			BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
			BigDecimal totalGrpExecScore = BigDecimal.ZERO;
			for (FacilityReferenceDetail finrefdet : financeReferenceDetail) {
				FinanceScoreHeader header = null;
				for (FinanceScoreHeader financeScoreHeader : scoringHeaderList) {
					if (financeScoreHeader.getGroupId() == finrefdet.getFinRefId()) {
						header = financeScoreHeader;
						break;
					}
				}
				if (header == null) {
					header = new FinanceScoreHeader();
				}
				addListGroup("", listbox, PennantConstants.PFF_CUSTCTG_INDIV, finrefdet, header.isOverride());
				totalGrpMaxScore = BigDecimal.ZERO;
				totalGrpExecScore = BigDecimal.ZERO;
				if (getFacility().getScoringMetrics().containsKey(finrefdet.getFinRefId())) {
					List<ScoringMetrics> scoringMetricsList = getFacility().getScoringMetrics().get(finrefdet.getFinRefId());
					if (isExecute) {
						getScoringDetailService().executeScoringMetrics(scoringMetricsList, getFacility().getCustomerEligibilityCheck());
						// reset previous user override to false
						header.setOverride(false); 
					}
					for (ScoringMetrics scoringMetric : scoringMetricsList) {
						scores = addListItem(scoringMetric, listbox, "I", finrefdet.getFinRefId(), isExecute);
						totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
						totalGrpExecScore = totalGrpExecScore.add(scores[1]);
					}
				}
				addListFooter(totalGrpMaxScore, totalGrpExecScore, listbox, "", "I", finrefdet.getFinRefId());
				for (FacilityReferenceDetail item : financeReferenceDetail) {
					key = PennantJavaUtil.concat(item.getLovDescCodelov(), "_CB");
					cb = (Checkbox) listbox.getAttribute(key);
				}
				if (isExecute) {
					if (cb != null) {
						cb.setChecked(false);
					}
				}
				if (totalGrpExecScore.intValue() >= finrefdet.getLovDescminScore()) {
					if (cb != null) {
						cb.setDisabled(true);
					}
					sufficientScore = true;
				} else if (header.isOverride() && (totalGrpExecScore.intValue() >= (finrefdet.getLovDescoverrideScore()))) {
					sufficientScore = true;
					if (cb != null) {
						cb.setDisabled(false);
					}
				} else if (totalGrpExecScore.intValue() >= (finrefdet.getLovDescoverrideScore())) {
					sufficientScore = false;
					if (cb != null) {
						cb.setDisabled(false);
					}
				} else {
					if (cb != null) {
						cb.setDisabled(true);
					}
					sufficientScore = false;
				}
			}
		}
		setScoreSummaryStyle(sufficientScore);
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
	private BigDecimal[] addListItem(ScoringMetrics scoringMetric, Listbox listbox, String categoryValue, long groupId, boolean isExecute) {
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
		if ("I".equals(categoryValue)) {
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
		if ("I".equals(categoryValue)) {
			metricExecScore = scoringMetric.getLovDescExecutedScore();
			finExecScoreMap.put(key, metricExecScore);
			lc = new Listcell(String.valueOf(metricExecScore));
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
			list.add(4, groupId);
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
			list.add(4, groupId);
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
			List<Object> list = (List<Object>) event.getData();
			Decimalbox dupIntbox = (Decimalbox) list.get(0);
			Decimalbox orgIntbox = (Decimalbox) list.get(1);
			BigDecimal ruleMaxScore = (BigDecimal) list.get(2);
			boolean isFinScore = (Boolean) list.get(3);
			long grpId = (Long) list.get(4);
			orgIntbox.setFormat("#0.00");
			if (orgIntbox.getValue() == null) {
				orgIntbox.setValue(BigDecimal.ZERO);
			}
			if (orgIntbox.getValue().compareTo(ruleMaxScore) > 0) {
				orgIntbox.setValue(dupIntbox.getValue());
				throw new WrongValueException(orgIntbox, PennantJavaUtil.concat("Score Value Must Be Less than or Equal to ", String.valueOf(ruleMaxScore)));
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
			List<FacilityReferenceDetail> scGrplist = getFacility().getScoringGroupList();
			
			//Credit Worth based on OBLIGOR Risk Grade Score
			BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
			BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100))).divide(totalScore, 2, RoundingMode.HALF_DOWN);
			
			if (scGrplist != null && !scGrplist.isEmpty()) {
				this.corpCreditWoth.setValue(getScrSlab(scGrplist.get(0).getFinRefId(), obligorScore, "", false));
			}
			//To Calculate Group Total
			Label label = (Label) this.window_ScoringDetailDialog.getFellowIfAny(grpId + "_TS");
			BigDecimal grpTotal = BigDecimal.ZERO;
			for (String string : finExecScoreMap.keySet()) {
				if (string.startsWith(grpId + "_")) {
					grpTotal = grpTotal.add(finExecScoreMap.get(string));
				}
			}
			label.setValue(String.valueOf(grpTotal));
			if (totScore.intValue() >= (this.overrideScore.intValue())) {
				sufficientScore = false;
				this.isOverride.setDisabled(false);
				if (totScore.intValue() >= this.minScore.intValue()) {
					this.isOverride.setDisabled(true);
					this.isOverride.setChecked(false);
					sufficientScore = true;
				} else if (this.isOverride.isChecked() && totScore.intValue() >= (this.overrideScore.intValue())) {
					sufficientScore = true;
					this.isOverride.setDisabled(false);
				}
			} else {
				this.isOverride.setDisabled(true);
				this.isOverride.setChecked(false);
				sufficientScore = false;
			}
			setScoreSummaryStyle(sufficientScore);
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
		List<FinanceScoreHeader> finScoreHeaderList = getFacility().getFinScoreHeaderList();
		// Set Total Calculated Score Result
		BigDecimal totScore = totalExecScore.add(totalNFRuleScore);
		this.totalCorpScore.setValue(String.valueOf(totScore));
		
		//Credit Worth based on OBLIGOR Risk Grade Score
		BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
		BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100))).divide(totalScore, 2, RoundingMode.HALF_DOWN);
				
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
		setScoreSummaryStyle(sufficientScore);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Adding List Group to listBox in Corporation
	 * 
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListGroup(String groupCodeDesc, Listbox listbox, String ctgType, Object object, boolean isOveride) {
		logger.debug("Entering");
		FinanceScoreHeader header = null;
		FacilityReferenceDetail detail = null;
		Listgroup listgroup = new Listgroup();
		String key = "";
		if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, ctgType)) {
			Listcell lc = null;
			Label label = null;
			Space space = null;
			Checkbox checkbox = null;
			if (object instanceof FinanceScoreHeader) {
				header = (FinanceScoreHeader) object;
				lc = new Listcell(PennantJavaUtil.concat(header.getGroupCode(), " - ", header.getGroupCodeDesc()));
				lc.setParent(listgroup);
				lc = new Listcell();
				lc.setSpan(3);
				label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_MinScore"), " :", String.valueOf(header.getMinScore())));
				lc.appendChild(label);
				space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);
				label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_Isoverride"), " :"));
				label.setStyle("float:center;");
				lc.appendChild(label);
				checkbox = new Checkbox();
				checkbox.setDisabled(!header.isOverride());
				checkbox.setStyle("float:center;");
				checkbox.setChecked(header.isOverride());
				lc.appendChild(checkbox);
				space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);
				if (header.isOverride()) {
					label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_OverrideScore"), " :", String.valueOf(header.getOverrideScore())));
					label.setStyle("float:right;");
					lc.appendChild(label);
				}
				lc.setParent(listgroup);
			}
			if (object instanceof FacilityReferenceDetail) {
				detail = (FacilityReferenceDetail) object;
				if (!this.btnScoringGroup.isVisible() && detail.getMandInputInStage().contains(userRole)) {
					this.btnScoringGroup.setVisible(true);
				}
				lc = new Listcell(PennantJavaUtil.concat(detail.getLovDescCodelov(), "-", detail.getLovDescNamelov()));
				lc.setParent(listgroup);
				lc = new Listcell();
				lc.setSpan(3);
				label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_MinScore"), " :", String.valueOf(detail.getLovDescminScore())));
				lc.appendChild(label);
				space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);
				if (detail.isLovDescisoverride()) {
					label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_Isoverride"), " :"));
					lc.appendChild(label);
					checkbox = new Checkbox();
					checkbox.setChecked(isOveride);
					setScoreSummaryStyle(isOveride);
					checkbox.setDisabled(!detail.isLovDescisoverride());
					key = PennantJavaUtil.concat(detail.getLovDescCodelov(), "_CB");
					listbox.setAttribute(key, checkbox);
					List<Object> overrideList = new ArrayList<Object>();
					overrideList.add(detail.getFinRefId()); // 1. Group Id
					overrideList.add(checkbox); // 2. Overrided CheckBox
					overrideList.add(detail.getLovDescminScore()); // 3. Min
																	// Group
																	// Score
					overrideList.add(detail.getLovDescoverrideScore()); // 4.
																		// Group
																		// Overriden
																		// Score
					checkbox.addForward("onCheck", window_ScoringDetailDialog, "onRetailOverrideChecked", overrideList);
					// retailOverrideCheckMap.put(detail.getFinRefId(), false);
					lc.appendChild(checkbox);
					space = new Space();
					space.setWidth("100px");
					lc.appendChild(space);
					label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_OverrideScore"), " :", String.valueOf(detail.getLovDescoverrideScore())));
					lc.appendChild(label);
				}
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
	 * @throws InterruptedException 
	 */
	private void addListFooter(BigDecimal totalMaxGrpScore, BigDecimal totalExecGrpScore, Listbox listbox, String creditWorth, String ctgType, long grpId) throws InterruptedException {
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
		cell = new Listcell(String.valueOf(totalMaxGrpScore));
		cell.setStyle("font-weight:bold;text-align:right;");
		listgroupfoot.appendChild(cell);

		cell = new Listcell();
		Label label = new Label(String.valueOf(totalExecGrpScore));
		label.setStyle("font-weight:bold;float:right;");
		cell.appendChild(label);
		label.setId(grpId + "_TS");
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
				if (new BigDecimal(code.trim()).compareTo(max) > 0) {
					max = new BigDecimal(code.trim());
				}
			}
		}
		return max;
	}

	/**
	 * Method to capture event when scoring list item clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onRetailOverrideChecked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		List<Object> overridenData = (List<Object>) event.getData();
		long grpId = (Long) overridenData.get(0);
		Checkbox overrideCB = (Checkbox) overridenData.get(1);
		int minScore = (Integer) overridenData.get(2);
		int overrideScore = (Integer) overridenData.get(3);
		BigDecimal totalGrpExcScore = BigDecimal.ZERO;
		String key = PennantJavaUtil.concat(String.valueOf(grpId), "_TS");
		if (this.listBoxRetailScoRef.getFellowIfAny(key) != null) {
			Label label = (Label) this.listBoxRetailScoRef.getFellowIfAny(key);
			totalGrpExcScore = new BigDecimal(label.getValue());
		}
		if (overrideCB.isChecked()) {
			if (totalGrpExcScore.compareTo(new BigDecimal(minScore)) < 0) {
				if (totalGrpExcScore.compareTo(new BigDecimal(overrideScore)) >= 0) {
					sufficientScore = true;
				} else {
					sufficientScore = false;
				}
			}
		} else if (totalGrpExcScore.compareTo(new BigDecimal(minScore)) < 0) {
			sufficientScore = false;
			overrideCB.setChecked(false);
		}
		for (FinanceScoreHeader item : getFacility().getFinScoreHeaderList()) {
			if (grpId == item.getGroupId()) {
				if (overrideCB.isChecked()) {
					item.setOverride(true);
				} else {
					item.setOverride(false);
				}
			}
		}
		setScoreSummaryStyle(sufficientScore);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to show the credit worthiness in Scoring Tab *
	 * 
	 * @param refId
	 *            (long)
	 * @param grpTotalScore
	 *            (int)
	 * @return String
	 * @throws InterruptedException 
	 */
	private String getScrSlab(long refId, BigDecimal grpTotalScore, String execCreditWorth, boolean isRetail) throws InterruptedException {
		logger.debug("Entering");
		List<ScoringSlab> slabList = getFacility().getScoringSlabs().get(refId);
		String creditWorth = "None";
		if (slabList != null && !slabList.isEmpty()) {
			for (ScoringSlab slab : slabList) {
				
				if(isRetail){
					if (grpTotalScore.compareTo(new BigDecimal(slab.getScoringSlab())) >= 0) {
						creditWorth = slab.getCreditWorthness();
						break;
					}
				}else{
					if (grpTotalScore.compareTo(new BigDecimal(slab.getScoringSlab())) <= 0) {
						creditWorth = slab.getCreditWorthness();
					}else{
						break;
					}
				}
			}
		} else if (StringUtils.isNotBlank(execCreditWorth)) {
			creditWorth = execCreditWorth;
		}
		
		if(!isRetail && !"None".equals(creditWorth)){
			RatingCode ratingCode = getRatingCodeService().getApprovedRatingCodeById(PennantConstants.DEFAULT_RATE_TYPE, creditWorth);
			if(ratingCode != null){
				creditWorth = ratingCode.getRatingCode() +"-"+ratingCode.getRatingCodeDesc();
			}else{
				MessageUtil.showError("Rating Code Details Not Defined Properly.");
			}
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
	public void doSave_ScoreDetail(Facility aFinanceDetail) throws InterruptedException {
		logger.debug("Entering ");
		setFacility(aFinanceDetail);
		getFacility().getScoreDetailListMap().clear();
		List<FinanceScoreHeader> scoreHeaderList = null;
		List<FinanceScoreHeader> finScoreHeaderList = getFacility().getFinScoreHeaderList();
		FinanceScoreHeader header = null;
		if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			header = getFacility().getFinScoreHeaderList().get(0);
		} else {
			header = new FinanceScoreHeader();
		}
		// Execute Scoring metrics and Display Total Score
		if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, custCtgType)) {
			scoreHeaderList = new ArrayList<FinanceScoreHeader>();
			for (FacilityReferenceDetail detail : getFacility().getScoringGroupList()) {
				header.setGroupId(detail.getFinRefId());
				header.setMinScore(detail.getLovDescminScore());
				header.setOverrideScore(detail.getLovDescoverrideScore());
				header.setGroupCodeDesc(detail.getLovDescNamelov());
				if (getFacility().getScoringMetrics().containsKey(detail.getFinRefId())) {
					List<FinanceScoreDetail> scoreDetails = new ArrayList<FinanceScoreDetail>();
					List<ScoringMetrics> metrics = getFacility().getScoringMetrics().get(detail.getFinRefId());
					FinanceScoreDetail scoreDetail = null;
					for (ScoringMetrics metric : metrics) {
						scoreDetail = new FinanceScoreDetail();
						scoreDetail.setRuleId(metric.getScoringId());
						scoreDetail.setSubGroupId(0);
						scoreDetail.setRuleCode(metric.getLovDescScoringCode());
						scoreDetail.setRuleCodeDesc(metric.getLovDescScoringCodeDesc());
						scoreDetail.setMaxScore(getMaxMetricScore(metric.getLovDescSQLRule()));
						BigDecimal execScore = BigDecimal.ZERO;
						String key = PennantJavaUtil.concat(String.valueOf(detail.getFinRefId()), "_", String.valueOf(metric.getScoringId()));
						if (finExecScoreMap != null && finExecScoreMap.containsKey(key)) {
							execScore = finExecScoreMap.get(key);
						}
						scoreDetail.setExecScore(execScore);
						scoreDetails.add(scoreDetail);
					}
					if (!scoreDetails.isEmpty()) {
						getFacility().getScoreDetailListMap().put(detail.getFinRefId(), scoreDetails);
					}
				}
				header.setCreditWorth("");
				String key = PennantJavaUtil.concat(String.valueOf(detail.getFinRefId()), "_CW");
				if (this.listBoxRetailScoRef.getFellowIfAny(key) != null) {
					Label label = (Label) this.listBoxRetailScoRef.getFellowIfAny(key);
					header.setCreditWorth(label.getValue());
				}
				key = PennantJavaUtil.concat(detail.getLovDescCodelov(), "_CB");
				Checkbox cb = (Checkbox) listBoxRetailScoRef.getAttribute(key);
				if (cb != null) {
					header.setOverride(cb.isChecked());
				}
				scoreHeaderList.add(header);
			}
		} else if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_CORP, custCtgType) || 
				StringUtils.equals(PennantConstants.PFF_CUSTCTG_SME, custCtgType)) {
			scoreHeaderList = new ArrayList<FinanceScoreHeader>();
			for (FacilityReferenceDetail detail : getFacility().getScoringGroupList()) {
				header.setGroupId(detail.getFinRefId());
				header.setMinScore(this.minScore.intValue());
				header.setOverride(this.isOverride.isChecked());
				header.setOverrideScore(0);
				if (header.isOverride()) {
					header.setOverrideScore(this.overrideScore.intValue());
				}
				
				//Credit Worth based on OBLIGOR Risk Grade Score
				BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
				BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100))).divide(totalScore, 2, RoundingMode.HALF_DOWN);
				
				header.setCreditWorth(getScrSlab(detail.getFinRefId(), obligorScore, "", false));
				scoreHeaderList.add(header);
				List<FinanceScoreDetail> scoreDetails = new ArrayList<FinanceScoreDetail>();
				FinanceScoreDetail scoreDetail = null;
				for (ScoringMetrics finMetric : getFacility().getFinScoringMetricList()) {
					if (getFacility().getScoringMetrics().containsKey(finMetric.getScoringId())) {
						List<ScoringMetrics> metrics = getFacility().getScoringMetrics().get(finMetric.getScoringId());
						for (ScoringMetrics metric : metrics) {
							scoreDetail = new FinanceScoreDetail();
							scoreDetail.setCategoryType(finMetric.getCategoryType());
							scoreDetail.setSubGrpCodeDesc(finMetric.getLovDescScoringCodeDesc());
							scoreDetail.setRuleCode(metric.getLovDescScoringCode());
							scoreDetail.setRuleCodeDesc(metric.getLovDescScoringCodeDesc());
							scoreDetail.setRuleId(metric.getScoringId());
							scoreDetail.setSubGroupId(finMetric.getScoringId());
							scoreDetail.setMaxScore(metric.getLovDescMetricMaxPoints());// getMaxMetricScore(metric.getLovDescSQLRule())
							BigDecimal execScore = BigDecimal.ZERO;
							String key = PennantJavaUtil.concat(String.valueOf(finMetric.getScoringId()), "_", String.valueOf(metric.getScoringId()));
							if (finExecScoreMap != null && finExecScoreMap.containsKey(key)) {
								execScore = finExecScoreMap.get(key);
							}
							scoreDetail.setExecScore(execScore);
							scoreDetails.add(scoreDetail);
						}
					}
				}
				for (ScoringMetrics nonFinMetric : getFacility().getNonFinScoringMetricList()) {
					if (getFacility().getScoringMetrics().containsKey(nonFinMetric.getScoringId())) {
						List<ScoringMetrics> metrics = getFacility().getScoringMetrics().get(nonFinMetric.getScoringId());
						if (metrics != null) {
							for (ScoringMetrics metric : metrics) {
								scoreDetail = new FinanceScoreDetail();
								scoreDetail.setCategoryType(nonFinMetric.getCategoryType());
								scoreDetail.setSubGrpCodeDesc(nonFinMetric.getLovDescScoringCodeDesc());
								scoreDetail.setRuleCode(metric.getLovDescScoringCode());
								scoreDetail.setRuleCodeDesc(metric.getLovDescScoringCodeDesc());
								scoreDetail.setRuleId(metric.getScoringId());
								scoreDetail.setSubGroupId(nonFinMetric.getScoringId());
								scoreDetail.setMaxScore(metric.getLovDescMetricMaxPoints());
								BigDecimal execScore = BigDecimal.ZERO;
								String key = PennantJavaUtil.concat(String.valueOf(nonFinMetric.getScoringId()), "_", String.valueOf(metric.getScoringId()));
								if (finExecScoreMap != null && finExecScoreMap.containsKey(key)) {
									execScore = finExecScoreMap.get(key);
								}
								scoreDetail.setExecScore(execScore);
								scoreDetails.add(scoreDetail);
							}
						}
					}
				}
				if (scoreDetails.size() > 0) {
					getFacility().getScoreDetailListMap().put(scoreHeaderList.get(0).getHeaderId(), scoreDetails);
				}
			}
		}
		aFinanceDetail.setFinScoreHeaderList(scoreHeaderList);
		aFinanceDetail.setSufficientScore(sufficientScore);
		logger.debug("Leaving ");
	}


	private void setScoreSummaryStyle(boolean sufficientScore) {
		getFacility().setSufficientScore(sufficientScore);
		if (sufficientScore) {
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr.label"));
		} else {
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_InSuffScr.label"));
		}
	}

	private boolean isExecuted(Map<String, String> exeMap, long scoringID) {
		metricExecScore = BigDecimal.ZERO;
		metricMaxScore = BigDecimal.ZERO;
		boolean executed = false;
		FinanceScoreHeader header = null;
		if (getFacility().getFinScoreHeaderList() != null && !getFacility().getFinScoreHeaderList().isEmpty()) {
			header = getFacility().getFinScoreHeaderList().get(0);
		}
		if (header != null) {
			List<FinanceScoreDetail> scoreDetailList = getFacility().getScoreDetailListMap().get(header.getHeaderId());
			if (scoreDetailList != null) {
				for (FinanceScoreDetail financeScoreDetail : scoreDetailList) {
					if (financeScoreDetail.getRuleId() == scoringID) {
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public boolean isSufficientScore() {
		return sufficientScore;
	}

	public void setSufficientScore(boolean sufficientScore) {
		this.sufficientScore = sufficientScore;
	}



	public CreditReviewSummaryData getCreditReviewSummaryData() {
		return creditReviewSummaryData;
	}

	public void setCreditReviewSummaryData(CreditReviewSummaryData creditReviewSummaryData) {
		this.creditReviewSummaryData = creditReviewSummaryData;
	}

	public void setCtrlObject(Object object) {
		this.ctrlObject = object;
	}

	public Object getCtrlObject() {
		return ctrlObject;
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
}