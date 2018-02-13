package com.pennant.webui.finance.financemain.stepfinance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class StepDetailDialogCtrl extends GFCBaseCtrl<StepPolicyHeader> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(StepDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_StepDetailDialog; 				// autoWired

	//Finance Step Details Tab
	protected Label 		stepDetail_finType; 						// autoWired
	protected Label 		stepDetail_finCcy; 							// autoWired
	protected Label 		stepDetail_scheduleMethod; 					// autoWired
	protected Label 		stepDetail_profitDaysBasis; 				// autoWired
	protected Label 		stepDetail_finReference; 					// autoWired
	protected Label 		stepDetail_grcEndDate; 						// autoWired
	protected Label 		label_StepDetailDialog_FinType; 			// autoWired
	protected Label 		label_StepDetailDialog_GrcEndDate; 			// autoWired

	protected Listbox 		listBoxStepdetails;					        // autoWired	 
	protected Button        btnNew_FinStepPolicy;                       // autoWired
	
	// not auto wired variables
	private FinanceDetail 			financeDetail = null; 			
	private FinScheduleData 		finScheduleData = null;
	private FinanceMain 			financeMain = null;
	
	private Object financeMainDialogCtrl = null;
	private boolean isWIF = false;
	public List<FinanceStepPolicyDetail> finStepPolicyList = null;

	private String roleCode = "";
	private boolean allowedManualSteps = false;
	private int ccyFormatter = 0;
	
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;
	protected Groupbox finBasicdetails;
	

	/**
	 * default constructor.<br>
	 */
	public StepDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "StepDetailDialog";
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
	public void onCreate$window_StepDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_StepDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}

		if (arguments.containsKey("isWIF")) {
			isWIF = (Boolean) arguments.get("isWIF");
		}
		
		if (arguments.containsKey("roleCode")) {
			roleCode = (String) arguments.get("roleCode");
			setRole((String) arguments.get("roleCode"));
			getUserWorkspace().allocateAuthorities("StepDetailDialog", getRole());	
		}
		
		if (arguments.containsKey("alwManualSteps")) {
			setAllowedManualSteps((Boolean) arguments.get("alwManualSteps"));
		}
		
		if (arguments.containsKey("ccyFormatter")) {
			this.ccyFormatter = (Integer)arguments.get("ccyFormatter");
		}

		doShowDialog(this.financeDetail);

		logger.debug("Leaving " + event.toString());
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 * @throws ParseException 
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException, ParseException {
		logger.debug("Entering");

		try {
			// append finance basic details 
			appendFinBasicDetails();
			
			getFinanceMainDialogCtrl().getClass().getMethod("setStepDetailDialogCtrl", 
					this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		
		//Stooping to Enter New Step policies and Allowed only for Maintenance
		if(!isAllowedManualSteps()){
			this.btnNew_FinStepPolicy.setVisible(false);
		}else{
			if(isWIF){
				this.btnNew_FinStepPolicy.setVisible(true);
			}else{
				this.btnNew_FinStepPolicy.setVisible(getUserWorkspace().isAllowed("button_StepDetailDialog_btnNew"));
			}
		}
		

		// fill the components with the data
		doFillStepDetais(getFinScheduleData().getStepPolicyDetails());
		getBorderLayoutHeight();
		
		if(isWIF){
			this.listBoxStepdetails.setHeight(this.borderLayoutHeight- 245 +"px");
			this.window_StepDetailDialog.setHeight(this.borderLayoutHeight-30+"px");
		}else{
			this.listBoxStepdetails.setHeight(this.borderLayoutHeight- 250 +"px");
			this.window_StepDetailDialog.setHeight(this.borderLayoutHeight-80+"px");
		}		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Validate Finance Step Policy Details either Entered manually or fetching from Existing Step Policies
	 * @param totalTerms
	 * @param isAlwManualSteps
	 * @return
	 */
	public List<ErrorDetail> doValidateStepDetails(FinanceMain financeMain, int totalTerms, boolean isAlwManualSteps, int noOfSteps, String stepType){
		logger.debug("Entering");
		
		List<ErrorDetail> errorList = new ArrayList<ErrorDetail>();
		if(this.finStepPolicyList != null && !finStepPolicyList.isEmpty()){
			
			if(isAlwManualSteps && noOfSteps != finStepPolicyList.size()){
				errorList.add(new ErrorDetail("30542",PennantConstants.KEY_SEPERATOR, new String[] {}));
			}else{
				
				int sumInstallments = 0;
				BigDecimal sumTenurePerc = BigDecimal.ZERO;
				
				BigDecimal calTotTenorSplit = BigDecimal.ZERO;
				BigDecimal calTotEmiStepPercent = BigDecimal.ZERO;
				int calTotTerms = 0;
				boolean hadZeroInstStep = false;

				for (int i = 0; i < finStepPolicyList.size(); i++) {
					FinanceStepPolicyDetail stepPolicy = finStepPolicyList.get(i);
					
					if(stepPolicy.getInstallments() > 0 && isAlwManualSteps){
						
						BigDecimal tenurePerc = (new BigDecimal(stepPolicy.getInstallments()).multiply(new BigDecimal(100))).divide(new BigDecimal(totalTerms), 2, RoundingMode.HALF_DOWN);
						stepPolicy.setTenorSplitPerc(tenurePerc);
						sumTenurePerc = sumTenurePerc.add(tenurePerc);
						if(i == (finStepPolicyList.size()-1)){
							if(sumTenurePerc.compareTo(new BigDecimal(100)) != 0){
								stepPolicy.setTenorSplitPerc(stepPolicy.getTenorSplitPerc().add(new BigDecimal(100)).subtract(sumTenurePerc));
							}
						}
						sumInstallments = sumInstallments + stepPolicy.getInstallments();
						
					}else if(stepPolicy.getTenorSplitPerc().compareTo(BigDecimal.ZERO) > 0){
						
						BigDecimal terms =stepPolicy.getTenorSplitPerc().multiply( new BigDecimal(totalTerms)).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
						sumInstallments = sumInstallments + Integer.parseInt(terms.toString());
						stepPolicy.setInstallments(Integer.parseInt(terms.toString()));
						if(i == (finStepPolicyList.size()-1)){
							if(sumInstallments != totalTerms){
								stepPolicy.setInstallments(stepPolicy.getInstallments() + totalTerms - sumInstallments);
							}
						}
						sumTenurePerc = sumTenurePerc.add(stepPolicy.getTenorSplitPerc());
					}
					
					if(stepPolicy.getInstallments() == 0){
						hadZeroInstStep = true;
					}

					calTotTerms = calTotTerms + stepPolicy.getInstallments();
					calTotTenorSplit = calTotTenorSplit.add(stepPolicy.getTenorSplitPerc());
					calTotEmiStepPercent = calTotEmiStepPercent.add(stepPolicy.getEmiSplitPerc());
					
					//Setting Bean Property Field Details
					if(StringUtils.isBlank(stepPolicy.getRecordType())){
						stepPolicy.setVersion(stepPolicy.getVersion()+1);
						stepPolicy.setRecordType(PennantConstants.RCD_ADD);
						stepPolicy.setNewRecord(true);
					}
					stepPolicy.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					stepPolicy.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					stepPolicy.setUserDetails(getUserWorkspace().getLoggedInUser());
				}
				
				doFillStepDetais(finStepPolicyList);

				//If Any Step Policy have Zero installments while on Calculation
				if(hadZeroInstStep){
					errorList.add(new ErrorDetail("30569", PennantConstants.KEY_SEPERATOR, 
							new String[] {Labels.getLabel("label_MinInstallment")," 1 " }));
				}
				
				//Tenor Percentage Validation for Step Policy Details
				if(calTotTerms != totalTerms){
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR, 
							new String[] {Labels.getLabel("label_TotStepInstallments"),Labels.getLabel("label_TotalTerms") }));
				}
				
				//Tenor Percentage Validation for Step Policy Details
				if(calTotTenorSplit.compareTo(new BigDecimal(100)) != 0){
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR, 
							new String[] {Labels.getLabel("label_TenorSplitPerc"), "100.00 %"}));
				}

				//Average EMI Percentage/ Total Percentage based on Step Type Validation for Step Policy Details
				if(StringUtils.equals(stepType, FinanceConstants.STEPTYPE_EMI)){
					BigDecimal emiStepPercAvg = calTotEmiStepPercent.divide(new BigDecimal(finStepPolicyList.size()), 0 , RoundingMode.HALF_DOWN);
					if(emiStepPercAvg.compareTo(new BigDecimal(100)) != 0){
						errorList.add(new ErrorDetail("30540",PennantConstants.KEY_SEPERATOR, new String[] { Labels.getLabel("label_AvgEMISplitPerc"), "100.00 %"}));
					}
				}else if(StringUtils.equals(stepType, FinanceConstants.STEPTYPE_PRIBAL)){
					if(calTotEmiStepPercent.compareTo(new BigDecimal(100)) != 0){
						errorList.add(new ErrorDetail("30540",PennantConstants.KEY_SEPERATOR, new String[] { Labels.getLabel("label_OutStandingPrincipalSplitPerc"), "100.00 %"}));
					}
				}
			}
		}else{
			if(isAlwManualSteps){
				errorList.add(new ErrorDetail("30541",PennantConstants.KEY_SEPERATOR, new String[] {}));
			}
		}
		logger.debug("Leaving");
		return errorList;
	}

	/**
	 * Method for Filling Step Policy Details
	 * @param finStepPolicyDetails
	 */
	@SuppressWarnings("unchecked")
	public void doFillStepDetais(List<FinanceStepPolicyDetail> finStepPolicyDetails){
		logger.debug("Entering ");

		Listitem listItem = null;
		Listcell lc = null;
		
		BigDecimal tenorPerc = BigDecimal.ZERO;
		int totInstallments = 0;
		BigDecimal avgRateMargin = BigDecimal.ZERO;
		BigDecimal avgAplliedRate = BigDecimal.ZERO;
		BigDecimal avgEmiPerc = BigDecimal.ZERO;
		
		this.listBoxStepdetails.getItems().clear();
		if(finStepPolicyDetails != null){
			
			Comparator<Object> comp = new BeanComparator("stepNo");
			Collections.sort(finStepPolicyDetails,comp);
			setFinStepPoliciesList(finStepPolicyDetails);
			
			for(FinanceStepPolicyDetail financeStepPolicyDetail : finStepPolicyDetails){

				listItem = new Listitem();

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getStepNo()));
				lc.setParent(listItem);

				lc = new Listcell();
				lc.setLabel(PennantApplicationUtil.formatRate(financeStepPolicyDetail.getTenorSplitPerc().doubleValue(), 2));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getInstallments()));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(PennantApplicationUtil.formatRate(financeStepPolicyDetail.getRateMargin().doubleValue(), 9));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");
				
				lc = new Listcell();
				BigDecimal appliedRate= financeStepPolicyDetail.getRateMargin();
				if(getFinanceDetail().getFinScheduleData().getFinanceMain().getRepayProfitRate() != null){
					appliedRate = appliedRate.add(getFinanceDetail().getFinScheduleData().getFinanceMain().getRepayProfitRate());
				}
				lc.setLabel(PennantApplicationUtil.formatRate(financeStepPolicyDetail.getRateMargin().doubleValue(), 9));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(PennantApplicationUtil.formatRate(financeStepPolicyDetail.getEmiSplitPerc().doubleValue(), 2));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");
				
				//FIXME Temporary fix for bajaj Release - Bug ID : 16637
				/*lc = new Listcell();
				lc.setLabel(PennantAppUtil.amountFormate(financeStepPolicyDetail.getSteppedEMI(), getFinanceMain().getLovDescFinFormatter()));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");*/
				
				tenorPerc = tenorPerc.add(financeStepPolicyDetail.getTenorSplitPerc());
				totInstallments = totInstallments + financeStepPolicyDetail.getInstallments();
				avgRateMargin = avgRateMargin.add(financeStepPolicyDetail.getRateMargin());
				avgAplliedRate = avgAplliedRate.add(appliedRate);
				avgEmiPerc = avgEmiPerc.add(financeStepPolicyDetail.getEmiSplitPerc());

				listItem.setParent(this.listBoxStepdetails);
				listItem.setAttribute("data", financeStepPolicyDetail);
				ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyItemDoubleClicked");
			}
			
			if(!finStepPolicyDetails.isEmpty()){
				
				String stepType = PennantConstants.List_Select;
				try {
					stepType = (String) getFinanceMainDialogCtrl().getClass().getMethod("getStepType").invoke(getFinanceMainDialogCtrl());
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				
				addFoolter(finStepPolicyDetails.size() , tenorPerc, totInstallments, avgRateMargin, avgAplliedRate, avgEmiPerc, stepType);
			}
			
		}
		logger.debug("Leaving ");
	}
	
	private void addFoolter(int size, BigDecimal tenorPerc ,int totInstallments, BigDecimal avgRateMargin ,
			BigDecimal avgAplliedRate ,BigDecimal avgEmiPerc, String stepType){
		
		Listitem listItem = new Listitem();
		listItem.setStyle("background-color: #C0EBDF;");

		Listcell lc = new Listcell(Labels.getLabel("label_StepDetailsFooter"));
		lc.setStyle("text-align:left;font-weight:bold;");
		lc.setParent(listItem);

		lc = new Listcell(PennantApplicationUtil.formatRate(tenorPerc.doubleValue(), 2)+"%");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);

		lc = new Listcell(String.valueOf(totInstallments));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);

		avgRateMargin = avgRateMargin.divide(new BigDecimal(size), 9, RoundingMode.HALF_DOWN);
		lc = new Listcell(PennantApplicationUtil.formatRate(avgRateMargin.doubleValue(), 9));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);
		
		avgAplliedRate = avgAplliedRate.divide(new BigDecimal(size), 9, RoundingMode.HALF_DOWN);
		lc = new Listcell(PennantApplicationUtil.formatRate(avgAplliedRate.doubleValue(), 9));
		lc.setParent(listItem);
		lc.setStyle("text-align:right;font-weight:bold;");

		if(StringUtils.equals(stepType, FinanceConstants.STEPTYPE_EMI)){
			avgEmiPerc = avgEmiPerc.divide(new BigDecimal(size), 2, RoundingMode.HALF_DOWN);
		}
		lc = new Listcell(PennantApplicationUtil.formatRate(avgEmiPerc.doubleValue(), 2)+"%");
		lc.setParent(listItem);
		lc.setStyle("text-align:right;font-weight:bold;");

		lc = new Listcell("");
		lc.setStyle("text-align:right;");
		lc.setParent(listItem);
		
		this.listBoxStepdetails.appendChild(listItem);
	}

	/**
	 * Double Click on Step Policy Item
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFinStepPolicyItemDoubleClicked(Event event) throws InterruptedException{
		logger.debug("Entering" + event.toString());
		
		// get the selected Academic object
		final Listitem item = this.listBoxStepdetails.getSelectedItem();

		if (item != null) {
			
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceStepPolicyDetail aFinStepPolicy = (FinanceStepPolicyDetail) item.getAttribute("data");
			openFinStepPolicyDetailDialog(aFinStepPolicy, false);
			
		}
		logger.debug("Leaving" + event.toString());
	}

	/*
	 *  onClick Event For btnNew_FinStepPolicy Button
	 */
	public void onClick$btnNew_FinStepPolicy(Event event) throws Exception {
		logger.debug("Entering");
		
		FinanceStepPolicyDetail financeStepPolicyDetail = new FinanceStepPolicyDetail();
		financeStepPolicyDetail.setNewRecord(true);

		openFinStepPolicyDetailDialog(financeStepPolicyDetail, true);
		logger.debug("Leaving");
	}

	public void openFinStepPolicyDetailDialog(FinanceStepPolicyDetail finStepPolicy, boolean isNewRecord) throws InterruptedException{
		try {
			
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeStepPolicyDetail", finStepPolicy);
			map.put("stepDetailDialogCtrl", this);
			map.put("newRecord", isNewRecord);
			map.put("roleCode",roleCode);
			map.put("finStepPoliciesList", getFinStepPoliciesList());
			map.put("alwDeletion", this.allowedManualSteps);
			map.put("alwManualStep", this.allowedManualSteps);
			map.put("ccyFormatter", this.ccyFormatter);
			
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinStepPolicyDetailDialog.zul", window_StepDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this );
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
		} catch (Exception e) {
			logger.debug(e);
		}
		
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		doFillStepDetais(getFinStepPoliciesList());
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
		setFinScheduleData(financeDetail.getFinScheduleData());
		setFinanceMain(this.finScheduleData.getFinanceMain());
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}
	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public List<FinanceStepPolicyDetail> getFinStepPoliciesList() {
		return this.finStepPolicyList;
	}
	public void setFinStepPoliciesList(List<FinanceStepPolicyDetail> finStepPoliciesList) {
		this.finStepPolicyList = finStepPoliciesList;
	}
	
	public boolean isAllowedManualSteps() {
		return allowedManualSteps;
	}
	public void setAllowedManualSteps(boolean allowedManualSteps) {
		this.allowedManualSteps = allowedManualSteps;
		if(this.allowedManualSteps){
			if(isWIF){
				this.btnNew_FinStepPolicy.setVisible(true);
			}else{
				this.btnNew_FinStepPolicy.setVisible(getUserWorkspace().isAllowed("button_StepDetailDialog_btnNew"));
			}
		}else{
			this.btnNew_FinStepPolicy.setVisible(false);
		}
	}
	
	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}
}
