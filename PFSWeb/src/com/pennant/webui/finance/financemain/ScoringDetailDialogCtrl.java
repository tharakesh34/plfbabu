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
package com.pennant.webui.finance.financemain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
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

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerScoringCheck;
import com.pennant.backend.model.finance.FinanceDetail;
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
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ScoringDetailDialogCtrl extends GFCBaseListCtrl<FinanceScoreDetail> implements Serializable {
	
	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(ScoringDetailDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ScoringDetailDialog; 					// autoWired
	
	//Finance Schedule Details Tab
	protected Label 		score_finType; 							// autoWired
	protected Label 		score_finCcy; 							// autoWired
	protected Label 		score_scheduleMethod; 					// autoWired
	protected Label 		score_profitDaysBasis; 					// autoWired
	protected Label 		score_finReference; 					// autoWired
	protected Label 		score_grcEndDate; 						// autoWired	
	
	protected Button btnScoringGroup; 								// autoWired
	protected Label label_TotalScore; 								// autoWired
	protected Label totalCorpScore; 								// autoWired
	protected Label label_CorpCreditWoth; 							// autoWired
	protected Label corpCreditWoth; 								// autoWired
	protected Label label_ScoreSummary; 							// autoWired
	protected Label label_ScoreSummaryVal; 							// autoWired
	
	protected Groupbox finScoreDetailGroup; 						// autoWired
	
	protected Listbox listBoxRetailScoRef; 							// autoWired
	protected Listbox listBoxFinancialScoRef; 						// autoWired
	protected Listbox listBoxNonFinancialScoRef; 					// autoWired
	
	protected Tab finScoreMetricTab; 								// autoWired
	protected Tab nonFinScoreMetricTab; 							// autoWired
	
	protected Decimalbox maxFinTotScore; 							// autoWired
	protected Decimalbox maxNonFinTotScore;							// autoWired
	protected Intbox minScore; 										// autoWired
	protected Decimalbox calTotScore; 								// autoWired
	protected Checkbox isOverride; 									// autoWired
	protected Intbox overrideScore; 								// autoWired
	protected Row row_finScoreOverride; 							// autoWired
	
	private Map<String, BigDecimal> finExecScoreMap = null;
	// private Map<Long, Boolean> retailOverrideCheckMap = null;
	
	// External Fields usage for Individuals ----> Scoring Details
	private String custCtgType = "";
	private transient boolean 	scoreExecuted;
	private transient boolean sufficientScore = true;
	private BigDecimal totalExecScore = BigDecimal.ZERO;
	private BigDecimal totalFinScore = BigDecimal.ZERO;
	private BigDecimal totalNFRuleScore = BigDecimal.ZERO;
	BigDecimal metricMaxScore = BigDecimal.ZERO;
	BigDecimal metricExecScore = BigDecimal.ZERO;
	private transient boolean validationOn;
	
	// not auto wired variables
	private FinanceDetail financeDetail = null; 			// over handed per parameters
															// parameters
	// Bean Setters by application Context
	private FinanceDetailService financeDetailService;
	private ScoringDetailService scoringDetailService;
	private CustomerService customerService;
	private RatingCodeService ratingCodeService;
	private CreditReviewSummaryData creditReviewSummaryData;
	
	private Object financeMainDialogCtrl;
	private List<ValueLabel> profitDaysBasisList = null; 
	private List<ValueLabel> schMethodList = null; 
	private BigDecimal[] scores = null;
	String userRole = "";
	private boolean isWIF = false;
	
	/**
	 * default constructor.<br>
	 */
	public ScoringDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ScoringDetailDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
		}
		if (args.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl(args.get("financeMainDialogCtrl"));
		}
		
		if (args.containsKey("profitDaysBasisList")) {
			profitDaysBasisList = (List<ValueLabel>) args.get("profitDaysBasisList");
		}
		
		if (args.containsKey("schMethodList")) {
			schMethodList = (List<ValueLabel>) args.get("schMethodList");
		}
		
		if (args.containsKey("isWIF")) {
			isWIF = (Boolean) args.get("isWIF");
		}
		
		if (args.containsKey("userRole")) {
			userRole = (String)args.get("userRole");
		}
		
		// set Field Properties
		doShowDialog();
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
		
		FinanceMain aFinanceMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		custCtgType = aFinanceMain.getLovDescCustCtgTypeName();
		
		this.score_finType.setValue(StringUtils.trimToEmpty(aFinanceMain.getLovDescFinTypeName()));
		this.score_finCcy.setValue(StringUtils.trimToEmpty(aFinanceMain.getLovDescFinCcyName()));
		this.score_scheduleMethod.setValue(PennantAppUtil.getlabelDesc(aFinanceMain.getScheduleMethod(), schMethodList));
		this.score_profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(aFinanceMain.getProfitDaysBasis(), profitDaysBasisList));
		this.score_finReference.setValue(StringUtils.trimToEmpty(aFinanceMain.getFinReference()));
		this.score_grcEndDate.setValue(DateUtility.formatDate(aFinanceMain.getGrcPeriodEndDate(), PennantConstants.dateFormate)) ;
		
		fillFinCustScoring();
		
		try {
			financeMainDialogCtrl.getClass().getMethod("setScoringDetailDialogCtrl", this.getClass()).invoke(financeMainDialogCtrl, this);
		} catch (Exception e) {
			logger.error(e);
		}
		
		if(isWIF){
			doExecuteScoring();
		}
			
		getBorderLayoutHeight();
		
		this.listBoxRetailScoRef.setHeight(this.borderLayoutHeight - 180 - 52 + "px");
		this.listBoxFinancialScoRef.setHeight(this.borderLayoutHeight - 318 + "px");
		this.listBoxNonFinancialScoRef.setHeight(this.borderLayoutHeight - 318 + "px");
		this.window_ScoringDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		
		logger.debug("Leaving");
	}

	public void fillFinCustScoring() throws InterruptedException {
		logger.debug("Entering");
		
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		List<FinanceReferenceDetail> scoringList = getFinanceDetail().getScoringGroupList();
		
		// Set Customer Data to Calculate the Score
		this.listBoxRetailScoRef.getItems().clear();
		this.listBoxFinancialScoRef.getItems().clear();
		this.listBoxNonFinancialScoRef.getItems().clear();
		
		// Execute Scoring metrics and Display Total Score
		if (PennantConstants.CUST_CAT_INDIVIDUAL.equals(custCtgType)) {
			// Individual customer
			this.listBoxRetailScoRef.setVisible(true);
			this.finScoreDetailGroup.setVisible(false);
			this.label_ScoreSummaryVal.setValue("");
			
			doFillRetailScoringListbox(scoringList, this.listBoxRetailScoRef, false);
		} else if (PennantConstants.CUST_CAT_CORPORATE.equals(custCtgType) || PennantConstants.CUST_CAT_BANK.equals(custCtgType)) {
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
	 * @throws Exception
	 */
	public void onClick$btnScoringGroup(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doExecuteScoring();
		logger.debug("Leaving" + event.toString());
	}

	public void doExecuteScoring() throws Exception{
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		
		if (getFinanceDetail() != null) {
			
			if(!isWIF){
				try {
					getFinanceMainDialogCtrl().getClass().getMethod("doCustomerValidation").invoke(getFinanceMainDialogCtrl());
				} catch (Exception e) {
					if(e.getCause().getClass().equals(WrongValuesException.class)){
						throw e;	
					}
					logger.error(e);
				}
				if (getFinanceDetail().getCustomerEligibilityCheck() == null) {
					long custId = 0;
					Customer aCustomer = null;

					if (!StringUtils.trimToEmpty(financeMain.getLovDescCustCIF()).equals("")) {
						custId = financeMain.getCustID();
						// Set Customer Data to Calculate the Score
						aCustomer = getCustomerService().getApprovedCustomerById(custId);
					}

					// Set Customer Data to check the eligibility
					getFinanceDetail().setCustomerEligibilityCheck(getCustomerEligibility(financeMain, aCustomer));

				}
			}else{
				try {
					FinanceDetail financeDetail = (FinanceDetail) getFinanceMainDialogCtrl().getClass().getMethod("dofillEligibilityData").invoke(getFinanceMainDialogCtrl());
					setFinanceDetail(financeDetail);
				} catch (Exception e) {
					logger.error(e);
				}
			}

			setCustomerScoringData();

			this.listBoxRetailScoRef.getItems().clear();
			this.listBoxFinancialScoRef.getItems().clear();
			this.listBoxNonFinancialScoRef.getItems().clear();

			if (PennantConstants.CUST_CAT_INDIVIDUAL.equals(custCtgType)) {
				this.label_ScoreSummaryVal.setValue("");
				setSufficientScore(false);
				doFillRetailScoringListbox(getFinanceDetail().getScoringGroupList(), this.listBoxRetailScoRef, true);
			} else if (PennantConstants.CUST_CAT_CORPORATE.equals(custCtgType) || PennantConstants.CUST_CAT_BANK.equals(custCtgType)) {
				this.listBoxRetailScoRef.setVisible(false);
				this.finScoreDetailGroup.setVisible(true);
				doFillCorpScoringMetricDetails(true);
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
	public void doFillRetailScoringListbox(List<FinanceReferenceDetail> financeReferenceDetail, Listbox listbox, boolean isExecute) throws InterruptedException {
		logger.debug("Entering");

		this.label_TotalScore.setVisible(false);
		this.totalCorpScore.setVisible(false);
		this.label_CorpCreditWoth.setVisible(false);
		this.corpCreditWoth.setVisible(false);
		this.label_ScoreSummary.setVisible(true);
		this.label_ScoreSummaryVal.setVisible(true);
		this.btnScoringGroup.setVisible(false);
		
		Checkbox  cb = null;
		String key  = "";
		
		List<FinanceScoreHeader> scoringHeaderList = getFinanceDetail().getFinScoreHeaderList();
		if ((financeReferenceDetail != null && !financeReferenceDetail.isEmpty()) || (scoringHeaderList !=null && !scoringHeaderList.isEmpty())) {
			BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
			BigDecimal totalGrpExecScore = BigDecimal.ZERO;

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
				if(isExecute){
					this.setScoreExecuted(true);
				}
				
				if (header == null) {
					header = new FinanceScoreHeader();
				}
				
				addListGroup("", listbox, PennantConstants.CUST_CAT_INDIVIDUAL, finrefdet, header.isOverride());

				totalGrpMaxScore = BigDecimal.ZERO;
				totalGrpExecScore = BigDecimal.ZERO;

				if (getFinanceDetail().getScoringMetrics().containsKey(finrefdet.getFinRefId())) {
					List<ScoringMetrics> scoringMetricsList = getFinanceDetail().getScoringMetrics().get(finrefdet.getFinRefId());

					if (isExecute) {
						getScoringDetailService().executeScoringMetrics(scoringMetricsList, getFinanceDetail().getCustomerScoringCheck());
						// reset previous user override to  false 
						header.setOverride(false); // TODO
					}

					for (ScoringMetrics scoringMetric : scoringMetricsList) {						
						scores = addListItem(scoringMetric, listbox, PennantConstants.CUST_CAT_INDIVIDUAL, finrefdet.getFinRefId(), isExecute);
						totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
						totalGrpExecScore = totalGrpExecScore.add(scores[1]);

					}
				}

				addListFooter(totalGrpMaxScore, totalGrpExecScore, listbox, "", PennantConstants.CUST_CAT_INDIVIDUAL, finrefdet.getFinRefId());

				for (FinanceReferenceDetail item : financeReferenceDetail) {
					key = PennantJavaUtil.concat(item.getLovDescCodelov(), "_CB");
					cb = (Checkbox)listbox.getAttribute(key);
				}
				
				if (isExecute) {
					if (cb != null) {
						cb.setChecked(false);
					}
				}
				
				if (totalGrpExecScore.intValue() >= finrefdet.getLovDescminScore()) {	
					if(cb != null) {
						cb.setDisabled(true);
					}
					sufficientScore = true;
				} else if(header.isOverride() && (totalGrpExecScore.intValue() >= (finrefdet.getLovDescoverrideScore()))) {
					sufficientScore = true;
					if(cb != null) {
						cb.setDisabled(false);
					}
				} else if(totalGrpExecScore.intValue() >= (finrefdet.getLovDescoverrideScore())) {
					sufficientScore = false;
					if(cb != null) {
						cb.setDisabled(false);
					}
				} else {
					if(cb != null) {
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
	 * Method for Filling Corporate Scoring Details
	 * @param isExecute
	 * @throws InterruptedException 
	 */
	private void doFillCorpScoringMetricDetails(boolean isExecute) throws InterruptedException{
		logger.debug("Entering");
		List<FinanceScoreHeader>  finScoreHeaderList = getFinanceDetail().getFinScoreHeaderList();
		List<FinanceReferenceDetail> refDetails = getFinanceDetail().getScoringGroupList();
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
		
		if(finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			this.isOverride.setChecked(finScoreHeaderList.get(0).isOverride());
		}
		

		if (refDetails != null && !refDetails.isEmpty()) {
			FinanceReferenceDetail scoringGroup = refDetails.get(0);

			if(isExecute){
				this.setScoreExecuted(true);

				/*fieldsandvalues = new HashMap<String, Object>();
				engine = new ScriptEngineManager().getEngineByName("JavaScript");
				if(getFinanceDetail().getCustomerScoringCheck() != null){
					fieldsandvalues = getFinanceDetail().getCustomerScoringCheck().getDeclaredFieldValues();
				}

				//Corporate Financial Scoring Details
				long custId = getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID();//TODO == set year by confirmation
				fieldsandvalues.putAll(getCreditReviewSummaryData().setDataMap(custId, 2012, custCtgType, true));

				ArrayList<String> keyset = new ArrayList<String>(fieldsandvalues.keySet());
				for (int i = 0; i < keyset.size(); i++) {
					Object var=fieldsandvalues.get(keyset.get(i));
					if (var instanceof String) {
						var=var.toString().trim();
						if("--".equals(var)){
							var = String.valueOf(BigDecimal.ZERO);
						}
					}
					engine.put(keyset.get(i),var );
				}*/
			}else{
				this.setScoreExecuted(false);
			}

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
				addListGroup(scoringMetric.getLovDescScoringCodeDesc(), this.listBoxFinancialScoRef, PennantConstants.CUST_CAT_CORPORATE, null, false);

				BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
				BigDecimal totalGrpExecScore = BigDecimal.ZERO;

				if(getFinanceDetail().getScoringMetrics().containsKey(scoringMetric.getScoringId())){
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
							
							scores = addListItem(subScoreMetric, this.listBoxFinancialScoRef, "F", scoringMetric.getScoringId(), executed);
							totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
							totalGrpExecScore = totalGrpExecScore.add(scores[1]);
						}
					}
					scoringMetric.setLovDescMetricMaxPoints(totalGrpMaxScore);
					totalFinScore = totalFinScore.add(totalGrpMaxScore);
					totalExecScore = totalExecScore.add(totalGrpExecScore);
				}
				addListFooter(totalGrpMaxScore, totalGrpExecScore, this.listBoxFinancialScoRef,"", "F", scoringMetric.getScoringId());
			}

			this.maxFinTotScore.setValue(totalFinScore);
			//totalNFRuleScore = BigDecimal.ZERO;
			this.listBoxNonFinancialScoRef.getItems().clear();
			BigDecimal totalNonFinScore = BigDecimal.ZERO;

			for (ScoringMetrics nonFinMetric : getFinanceDetail().getNonFinScoringMetricList()) {
				addListGroup(nonFinMetric.getLovDescScoringCodeDesc(), this.listBoxNonFinancialScoRef, PennantConstants.CUST_CAT_CORPORATE, null, false);

				BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
				BigDecimal totalGrpExecScore = BigDecimal.ZERO;


				if(getFinanceDetail().getScoringMetrics().containsKey(nonFinMetric.getScoringId())){
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

			//Set Total Calculated Score Result
			// BigDecimal totScore = totalExecScore.add(totalNFRuleScore);
			BigDecimal totScore = totalExecScore;
			this.calTotScore.setValue(totScore);

			this.totalCorpScore.setValue(String.valueOf(totScore));
			
			//Credit Worth based on OBLIGOR Risk Grade Score
			BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
			BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100))).divide(totalScore, 2, RoundingMode.HALF_DOWN);
			
			this.corpCreditWoth.setValue(getScrSlab(scoringGroup.getFinRefId(),obligorScore,"",false));

			if (totScore.intValue() >= this.minScore.intValue()) {	
				sufficientScore = true;
				this.isOverride.setDisabled(true);
			} else if(totScore.intValue() >= (this.overrideScore.intValue())) {
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
	 * Method for Adding List Item For scoring details
	 * @param scoringMetric
	 * @param listbox
	 * @param engine
	 * @param categoryValue
	 * @param groupId
	 * @param isExecute
	 * @return
	 */
	private BigDecimal[] addListItem(ScoringMetrics scoringMetric, Listbox listbox, String categoryValue, long groupId, boolean isExecute){
		logger.debug("Entering");

		scores = new BigDecimal[2];

		Listitem item  = null;
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

		String key  = PennantJavaUtil.concat(String.valueOf(groupId), "_", String.valueOf(scoringMetric.getScoringId()));
		
		if(finExecScoreMap == null) {
			finExecScoreMap = new HashMap<String, BigDecimal>();
		}
		
		if(finExecScoreMap.containsKey(key)){
			finExecScoreMap.remove(key);
		}
		
		if("I".equals(categoryValue)){	
			metricExecScore = scoringMetric.getLovDescExecutedScore();
			finExecScoreMap.put(key, metricExecScore);

			lc = new Listcell(String.valueOf(metricExecScore));
			lc.setStyle("text-align:right;font-weight:bold;color:#ff7300;");
			
		

		} else if("F".equals(categoryValue)){	
			/*if(isExecute){
				//Rule Execution of Financial Score
				List<GlobalVariable> globalVariableList = SystemParameterDetails.getGlobaVariableList();
				metricExecScore = getRuleScore(scoringMetric.getLovDescSQLRule(), engine, globalVariableList);

				if(finExecScoreMap.containsKey(groupId+"_"+scoringMetric.getScoringId())){
					finExecScoreMap.remove(groupId+"_"+scoringMetric.getScoringId());
				}
				finExecScoreMap.put(groupId+"_"+scoringMetric.getScoringId(), metricExecScore);

				lc = new Listcell(String.valueOf(metricExecScore));
				lc.setStyle("text-align:right;font-weight:bold;color:#ff7300;");
			}else{
				lc = new Listcell("");
			}*/
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

		} else if("N".equals(categoryValue)){
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
	 * @param event
	 * @throws InterruptedException 
	 */
	@SuppressWarnings("unchecked")
	public void onChangeNFRuleScore(ForwardEvent event) throws InterruptedException{
		logger.debug("Entering" + event.toString());
	
		List<FinanceScoreHeader>  finScoreHeaderList = getFinanceDetail().getFinScoreHeaderList();
		
		List<Object> list = (List<Object>) event.getData();
		Decimalbox dupIntbox = (Decimalbox) list.get(0);
		Decimalbox orgIntbox = (Decimalbox) list.get(1);
		BigDecimal ruleMaxScore = (BigDecimal) list.get(2);	
		boolean isFinScore = (Boolean) list.get(3);
		
		orgIntbox.setFormat("#0.00");
		if( orgIntbox.getValue() == null) {
			orgIntbox.setValue(BigDecimal.ZERO);
		}

		if(orgIntbox.getValue().compareTo(ruleMaxScore) > 0){
			orgIntbox.setValue(dupIntbox.getValue());
			throw new WrongValueException(orgIntbox, PennantJavaUtil.concat("Score Value Must Be Less than or Equal to ", String.valueOf(ruleMaxScore)));
		}

		final BigDecimal dupNFRuleScore = dupIntbox.getValue();
		final BigDecimal orgNFRuleScore = orgIntbox.getValue();

		if(finExecScoreMap == null) {
			finExecScoreMap = new HashMap<String, BigDecimal>();
		}
		
		if(finExecScoreMap.containsKey(orgIntbox.getId())){
			finExecScoreMap.remove(orgIntbox.getId());
		}
		
		finExecScoreMap.put(orgIntbox.getId(), orgNFRuleScore);

		if(isFinScore){
			totalExecScore = totalExecScore.subtract(dupNFRuleScore).add(orgNFRuleScore) ;
			dupIntbox.setValue(orgNFRuleScore);
		}else{
			totalNFRuleScore = totalNFRuleScore.subtract(dupNFRuleScore).add(orgNFRuleScore) ;
			dupIntbox.setValue(orgNFRuleScore);
		}

		//Set Total Calculated Score Result
		BigDecimal totScore = totalExecScore.add(totalNFRuleScore);
		this.calTotScore.setValue(totScore);

		this.totalCorpScore.setValue(String.valueOf(totScore));
		
		//Credit Worth based on OBLIGOR Risk Grade Score
		BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
		BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100))).divide(totalScore, 2, RoundingMode.HALF_DOWN);
		
		if(finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			this.corpCreditWoth.setValue(getScrSlab(finScoreHeaderList.get(0).getGroupId(), obligorScore, "",false));
		}
				
		if (totScore.intValue() >= this.minScore.intValue()) {	
			this.isOverride.setDisabled(true);
			sufficientScore = true;
		} else if(this.isOverride.isChecked() && totScore.intValue() >= (this.overrideScore.intValue())) {
			sufficientScore = true;
			this.isOverride.setDisabled(false);
		} else if(totScore.intValue() >= (this.overrideScore.intValue())) {
			sufficientScore = false;
			this.isOverride.setDisabled(false);
		} else {
			this.isOverride.setDisabled(true);
			sufficientScore = false;
		}
	
		setScoreSummaryStyle(sufficientScore);
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * Method for Check Override Checkbox For Corporate Score Details
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onCheck$isOverride(Event event) throws InterruptedException{
		logger.debug("Entering" + event.toString());
		
		List<FinanceScoreHeader>  finScoreHeaderList = getFinanceDetail().getFinScoreHeaderList();
		//Set Total Calculated Score Result
		BigDecimal totScore = totalExecScore.add(totalNFRuleScore);
		
		this.totalCorpScore.setValue(String.valueOf(totScore));
		
		//Credit Worth based on OBLIGOR Risk Grade Score
		BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
		BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100))).divide(totalScore, 2, RoundingMode.HALF_DOWN);
				
		if(finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			this.corpCreditWoth.setValue(getScrSlab(finScoreHeaderList.get(0).getGroupId(), obligorScore, "", false));
		}
		
		
		if (this.isOverride.isChecked()) {
			if (totScore.compareTo(new BigDecimal(this.minScore.intValue())) < 0) {

				if (totScore.compareTo(new BigDecimal(this.overrideScore.intValue())) >= 0) {
					sufficientScore = true;
				}
			}
			
			if(finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
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
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListGroup(String groupCodeDesc, Listbox listbox, String ctgType, Object object, boolean isOveride){
		logger.debug("Entering");		

		FinanceScoreHeader header = null;
		FinanceReferenceDetail detail = null;
		Listgroup listgroup = new Listgroup();
		String key = "";
		if(PennantConstants.CUST_CAT_INDIVIDUAL.equals(ctgType)){
			Listcell lc = null;
			Label label = null;
			Space space = null;
			Checkbox checkbox = null;
			
			if(object instanceof FinanceScoreHeader){
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
				
				if(header.isOverride()){
					label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_OverrideScore"), " :", String.valueOf(header.getOverrideScore())));
					label.setStyle("float:right;");
					lc.appendChild(label);
				}
				
				lc.setParent(listgroup);
			}

			if(object instanceof FinanceReferenceDetail){
				detail = (FinanceReferenceDetail) object;
				
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
				
				if(detail.isLovDescisoverride()){
					label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_Isoverride"), " :"));
					lc.appendChild(label);

					checkbox = new Checkbox();
					checkbox.setChecked(isOveride);					
					setScoreSummaryStyle(isOveride);					
					checkbox.setDisabled(!detail.isLovDescisoverride());
					key = PennantJavaUtil.concat(detail.getLovDescCodelov(), "_CB");
					listbox.setAttribute(key, checkbox);
					List<Object> overrideList = new ArrayList<Object>();
					overrideList.add(detail.getFinRefId());				//1. Group Id
					overrideList.add(checkbox);							//2. Overrided CheckBox
					overrideList.add(detail.getLovDescminScore());		//3. Min Group Score
					overrideList.add(detail.getLovDescoverrideScore());	//4. Group Overriden Score

					checkbox.addForward("onCheck", window_ScoringDetailDialog, "onRetailOverrideChecked", overrideList);
				//	retailOverrideCheckMap.put(detail.getFinRefId(), false);
					lc.appendChild(checkbox);

					space = new Space();
					space.setWidth("100px");
					lc.appendChild(space);

					label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_OverrideScore"), " :", String.valueOf(detail.getLovDescoverrideScore())));
					lc.appendChild(label);
				}
				lc.setParent(listgroup);
			}
			

		}else{
			listgroup.setLabel(groupCodeDesc);
		}
		
		listgroup.setOpen(true);
		listbox.appendChild(listgroup);
		logger.debug("Leaving");
	}

	/**
	 * Method for Adding List Group to listBox in Corporation
	 * @param scoringMetric
	 * @param listbox
	 * @throws InterruptedException 
	 */
	private void addListFooter(BigDecimal totalMaxGrpScore, BigDecimal totalExecGrpScore, Listbox listbox,
			String creditWorth, String ctgType, long grpId) throws InterruptedException{
		logger.debug("Entering");

		Listgroupfoot listgroupfoot = new Listgroupfoot();

		Listcell cell = null;

		if("I".equals(ctgType)){
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

		/*if("N".equals(ctgType)){
			cell = new Listcell("");
		}else{*/
			cell = new Listcell();
			Label label = new Label(String.valueOf(totalExecGrpScore));
			label.setStyle("font-weight:bold;float:right;");
			cell.appendChild(label);
			if("I".equals(ctgType)){
				label.setId(grpId+"_TS");
			}
	//	}
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
		if(this.listBoxRetailScoRef.getFellowIfAny(key) != null){
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
		
		for (FinanceScoreHeader item : getFinanceDetail().getFinScoreHeaderList()) {
			if(grpId == item.getGroupId()) {
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
		List<ScoringSlab> slabList = getFinanceDetail().getScoringSlabs().get(refId);
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
		} else if (!StringUtils.trimToEmpty(execCreditWorth).equals("")) {
			creditWorth = execCreditWorth;
		}
		
		if(!isRetail && !creditWorth.equals("None")){
			RatingCode ratingCode = getRatingCodeService().getApprovedRatingCodeById(PennantConstants.DEFAULT_RATE_TYPE, creditWorth);
			if(ratingCode != null){
				creditWorth = ratingCode.getRatingCode() +"-"+ratingCode.getRatingCodeDesc();
			}else{
				PTMessageUtils.showErrorMessage("Rating Code Details Not Defined Properly.");
			}
		}
		logger.debug("Leaving");
		return creditWorth;
	}



	/**
	 * Method to prepare data required for scoring check
	 * 
	 * @return CustomerScoringCheck
	 */
	public void setCustomerScoringData() {
		logger.debug("Entering");
		CustomerScoringCheck customerScoringCheck = new CustomerScoringCheck();
		if(getFinanceDetail().getCustomerEligibilityCheck() != null){
			BeanUtils.copyProperties(getFinanceDetail().getCustomerEligibilityCheck(), customerScoringCheck);
			getFinanceDetail().setCustomerScoringCheck(customerScoringCheck);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws InterruptedException 
	 */
	public void doSave_ScoreDetail(FinanceDetail aFinanceDetail) throws InterruptedException {
		logger.debug("Entering ");

		setFinanceDetail(aFinanceDetail);
		getFinanceDetail().getScoreDetailListMap().clear();
		List<FinanceScoreHeader> scoreHeaderList = null;
		BigDecimal totalCheckScore = BigDecimal.ZERO;
		
		List<FinanceScoreHeader>  finScoreHeaderList = getFinanceDetail().getFinScoreHeaderList();
		FinanceScoreHeader header = null;
		
		if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			header = getFinanceDetail().getFinScoreHeaderList().get(0);
		} else {
			header = new FinanceScoreHeader();
		}
		
		// Execute Scoring metrics and Display Total Score
		if(PennantConstants.CUST_CAT_INDIVIDUAL.equals(custCtgType)){
			
			scoreHeaderList = new ArrayList<FinanceScoreHeader>();
			for (FinanceReferenceDetail detail : getFinanceDetail().getScoringGroupList()) {
		
				header.setGroupId(detail.getFinRefId());
				header.setMinScore(detail.getLovDescminScore());
				header.setOverrideScore(detail.getLovDescoverrideScore());
				header.setGroupCodeDesc(detail.getLovDescNamelov());

				if(getFinanceDetail().getScoringMetrics().containsKey(detail.getFinRefId())){
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
						
						String key = PennantJavaUtil.concat(String.valueOf(detail.getFinRefId()), "_", String.valueOf(metric.getScoringId()));
						
						if(finExecScoreMap != null && finExecScoreMap.containsKey(key)){
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
				Checkbox cb = (Checkbox)listBoxRetailScoRef.getAttribute(key);
				
				if(cb != null) {
					header.setOverride(cb.isChecked());
				}
				scoreHeaderList.add(header);
			}

		}else if(PennantConstants.CUST_CAT_CORPORATE.equals(custCtgType) || PennantConstants.CUST_CAT_BANK.equals(custCtgType)){
			scoreHeaderList = new ArrayList<FinanceScoreHeader>();
	
			for (FinanceReferenceDetail detail : getFinanceDetail().getScoringGroupList()) {
				
				header.setGroupId(detail.getFinRefId());
				header.setMinScore(this.minScore.intValue());
				header.setOverride(this.isOverride.isChecked());
				header.setOverrideScore(0);
 				
				if(header.isOverride()){
					header.setOverrideScore(this.overrideScore.intValue());
				}
				
				//Credit Worth based on OBLIGOR Risk Grade Score
				BigDecimal totalScore = this.maxFinTotScore.getValue().add(this.maxNonFinTotScore.getValue());
				BigDecimal obligorScore = (new BigDecimal(this.totalCorpScore.getValue()).multiply(new BigDecimal(100))).divide(totalScore, 2, RoundingMode.HALF_DOWN);
				
				header.setCreditWorth(getScrSlab(detail.getFinRefId(), obligorScore,"", false));
				scoreHeaderList.add(header);

				List<FinanceScoreDetail> scoreDetails = new ArrayList<FinanceScoreDetail>();
				FinanceScoreDetail scoreDetail = null;
				
				for (ScoringMetrics finMetric : getFinanceDetail().getFinScoringMetricList()) {

					if(getFinanceDetail().getScoringMetrics().containsKey(finMetric.getScoringId())){
						List<ScoringMetrics> metrics = getFinanceDetail().getScoringMetrics().get(finMetric.getScoringId());
						for (ScoringMetrics metric : metrics) {
							scoreDetail = new FinanceScoreDetail();
							scoreDetail.setRuleId(metric.getScoringId());
							scoreDetail.setSubGroupId(finMetric.getScoringId());
							scoreDetail.setMaxScore(metric.getLovDescMetricMaxPoints());//getMaxMetricScore(metric.getLovDescSQLRule())
							scoreDetail.setCategoryType(finMetric.getCategoryType());
							scoreDetail.setSubGrpCodeDesc(finMetric.getLovDescScoringCodeDesc());
							scoreDetail.setRuleCode(metric.getLovDescScoringCode());
							scoreDetail.setRuleCodeDesc(metric.getLovDescScoringCodeDesc());
							
							
							BigDecimal execScore = BigDecimal.ZERO;
							String key = PennantJavaUtil.concat(String.valueOf(finMetric.getScoringId()), "_", String.valueOf(metric.getScoringId()));
							
							if(finExecScoreMap != null && finExecScoreMap.containsKey(key)){
								execScore = finExecScoreMap.get(key);
							}
							totalCheckScore = totalCheckScore.add(execScore);
							scoreDetail.setExecScore(execScore);
							scoreDetails.add(scoreDetail);
						}
					}
				}

				for (ScoringMetrics nonFinMetric : getFinanceDetail().getNonFinScoringMetricList()) {

					if(getFinanceDetail().getScoringMetrics().containsKey(nonFinMetric.getScoringId())){
						List<ScoringMetrics> metrics = getFinanceDetail().getScoringMetrics().get(nonFinMetric.getScoringId());
						
						if(metrics != null) {
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
								String key = PennantJavaUtil.concat(String.valueOf(nonFinMetric.getScoringId()), "_", String.valueOf(metric.getScoringId()));

								if(finExecScoreMap != null && finExecScoreMap.containsKey(key)){
									execScore = finExecScoreMap.get(key);
								}
								totalCheckScore = totalCheckScore.add(execScore);
								scoreDetail.setExecScore(execScore);
								scoreDetails.add(scoreDetail);
							}
						}
					}
				}
				
				if(scoreDetails.size() > 0){
					getFinanceDetail().getScoreDetailListMap().put(header.getHeaderId(), scoreDetails);
				}
			}
		}
		
		aFinanceDetail.setFinScoreHeaderList(scoreHeaderList);
		aFinanceDetail.setSufficientScore(sufficientScore);
		aFinanceDetail.setScore(totalCheckScore);

		logger.debug("Leaving ");

	}
	
	// Current Finance Monthly Installment Calculation
	private CustomerEligibilityCheck getCustomerEligibility(FinanceMain financeMain, Customer aCustomer) {
		String productName = getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName();
		BigDecimal finAmount = financeDetail.getFinScheduleData().getFinanceMain().getFinAmount();
		Date finStartDate = financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();
		Date finMaturityDate =  financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate();
		
		BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();				
		int installmentMnts = DateUtility.getMonthsBetween(finStartDate, finMaturityDate, true);
		BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		int months = DateUtility.getMonthsBetween(finStartDate, finMaturityDate);
		
		//Get Customer Employee Designation
		String custEmpDesg = "";
		if(getFinanceDetail().getCustomerDetails() != null && getFinanceDetail().getCustomerDetails().getCustEmployeeDetail() != null){
			custEmpDesg = StringUtils.trimToEmpty(getFinanceDetail().getCustomerDetails().getCustEmployeeDetail().getEmpDesg());
		}

		return getFinanceDetailService().getCustEligibilityDetail(aCustomer, productName, financeMain.getFinCcy(), 
				curFinRepayAmt, months, finAmount, financeMain.getCustDSR(), custEmpDesg);

	}
	
	private void setScoreSummaryStyle(boolean sufficientScore) {
		getFinanceDetail().setSufficientScore(sufficientScore);
		if (sufficientScore) {	
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr.label"));
		} else {
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_InSuffScr.label"));
		}
	}
	
	private boolean isExecuted(Map<String, String> exeMap, long scoringId) {
		metricExecScore = BigDecimal.ZERO;
		metricMaxScore = BigDecimal.ZERO;
		boolean executed = false;
		FinanceScoreHeader  header = null;
		
		if(getFinanceDetail().getFinScoreHeaderList() != null && !getFinanceDetail().getFinScoreHeaderList().isEmpty()) {
			header = getFinanceDetail().getFinScoreHeaderList().get(0);
		}
		
		if (header != null) {
			List<FinanceScoreDetail> scoreDetailList = getFinanceDetail().getScoreDetailListMap().get(header.getHeaderId());
			if (scoreDetailList != null) {
				for (FinanceScoreDetail financeScoreDetail : scoreDetailList) {
					if (financeScoreDetail.getRuleId() == scoringId) {
						if(exeMap.get(financeScoreDetail.getRuleCodeDesc()) == null) {
							exeMap.put(financeScoreDetail.getRuleCodeDesc(), financeScoreDetail.getRuleCodeDesc());
							metricMaxScore = financeScoreDetail.getMaxScore();
							metricExecScore = financeScoreDetail.getExecScore();
							executed = true;

							if(header.getHeaderId() == financeScoreDetail.getHeaderId()) {
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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
}