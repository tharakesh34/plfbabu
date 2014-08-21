package com.pennant.webui.finance.financemain.stepfinance;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class StepDetailDialogCtrl extends GFCBaseListCtrl<StepPolicyHeader> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(StepDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_StepDetailDialog; 				// autoWired

	//Finance Step Details Tab
	protected Label 		stepDetail_finType; 						// autoWired
	protected Label 		stepDetail_finCcy; 							// autoWired
	protected Label 		stepDetail_scheduleMethod; 					// autoWired
	protected Label 		stepDetail_profitDaysBasis; 				// autoWired
	protected Label 		stepDetail_finReference; 					// autoWired
	protected Label 		stepDetail_grcEndDate; 						// autoWired

	protected Listbox 		listBoxStepdetails;					        // autoWired	 
	protected Button        btnNew_FinStepPolicy;                       // autoWired
	
	// not auto wired variables
	private FinanceDetail 			financeDetail = null; 			
	private FinScheduleData 		finScheduleData = null;
	private FinanceMain 			financeMain = null;
	
	private Object financeMainDialogCtrl = null;
	private boolean isWIF = false;
	public List<FinanceStepPolicyDetail> financeStepPolicyDetailsList = null;

	private List<ValueLabel> profitDaysBasisList = null;
	private List<ValueLabel> schMethodList = null;

	/**
	 * default constructor.<br>
	 */
	public StepDetailDialogCtrl() {
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
	public void onCreate$window_StepDetailDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) args.get("financeDetail"));
		}

		if (args.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
		}

		if (args.containsKey("isWIF")) {
			isWIF = (Boolean) args.get("isWIF");
		}
		if (args.containsKey("profitDaysBasisList")) {
			profitDaysBasisList = (List<ValueLabel>) args.get("profitDaysBasisList");
		}

		if (args.containsKey("schMethodList")) {
			schMethodList = (List<ValueLabel>) args.get("schMethodList");
		}

		doShowDialog(this.financeDetail);

		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws ParseException 
	 */
	public void doWriteBeanToComponents() throws ParseException { 
		logger.debug("Entering");

		this.stepDetail_finType.setValue(StringUtils.trimToEmpty(getFinanceMain().getLovDescFinTypeName()));
		this.stepDetail_finCcy.setValue(StringUtils.trimToEmpty(getFinanceMain().getLovDescFinCcyName()));
		if(schMethodList != null){
			this.stepDetail_scheduleMethod.setValue(PennantAppUtil.getlabelDesc(getFinanceMain().getScheduleMethod(), schMethodList));
		}
		this.stepDetail_profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(getFinanceMain().getProfitDaysBasis(), profitDaysBasisList));
		this.stepDetail_finReference.setValue(StringUtils.trimToEmpty(getFinanceMain().getFinReference()));
		this.stepDetail_grcEndDate.setValue(DateUtility.formatDate(getFinanceMain().getGrcPeriodEndDate(), PennantConstants.dateFormate)) ;

		logger.debug("Leaving");
	}

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
			getFinanceMainDialogCtrl().getClass().getMethod("setStepDetailDialogCtrl", 
					this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
		} catch (Exception e) {
			logger.error(e);
		}

		// fill the components with the data
		doWriteBeanToComponents();
		doFillStepDetais(getFinScheduleData().getStepPolicyDetails());
		if(getFinanceDetail().getFinScheduleData().getFinanceType().isStepFinance() && 
				getFinanceDetail().getFinScheduleData().getFinanceType().isAlwManualSteps()){
			this.btnNew_FinStepPolicy.setVisible(true);
		} else {
			this.btnNew_FinStepPolicy.setVisible(false);
		}

		getBorderLayoutHeight();
		if(isWIF){
			this.listBoxStepdetails.setHeight(this.borderLayoutHeight- 230 +"px");
			this.window_StepDetailDialog.setHeight(this.borderLayoutHeight-30+"px");
		}else{
			this.listBoxStepdetails.setHeight(this.borderLayoutHeight- 305 +"px");
			this.window_StepDetailDialog.setHeight(this.borderLayoutHeight-80+"px");
		}		
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Step Policy Details
	 * @param finStepPolicyDetails
	 */
	public void doFillStepDetais(List<FinanceStepPolicyDetail> finStepPolicyDetails){
		logger.debug("Entering ");

		Listitem listItem = null;
		Listcell lc = null;

		this.listBoxStepdetails.getItems().clear();
		this.financeStepPolicyDetailsList = finStepPolicyDetails;
		if(financeStepPolicyDetailsList != null){
			for(FinanceStepPolicyDetail financeStepPolicyDetail : this.financeStepPolicyDetailsList){

				listItem = new Listitem();

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getStepNo()));
				lc.setParent(listItem);

				lc = new Listcell();
				lc.setLabel(financeStepPolicyDetail.getTenorSplitPerc()+"%");
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getInstallments()));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getRateMargin()));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(financeStepPolicyDetail.getEmiSplitPerc()+"%");
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(PennantAppUtil.amountFormate(financeStepPolicyDetail.getSteppedEMI(), getFinanceMain().getLovDescFinFormatter()));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				listItem.setParent(this.listBoxStepdetails);
				listItem.setAttribute("data", financeStepPolicyDetail);
				ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyItemDoubleClicked");
			}
		}
		logger.debug("Leaving ");
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
			openFinStepPolicyDetailDialog(aFinStepPolicy);
			
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

		openFinStepPolicyDetailDialog(financeStepPolicyDetail);
		logger.debug("Leaving");
	}

	public void openFinStepPolicyDetailDialog(FinanceStepPolicyDetail finStepPolicy) throws InterruptedException{
		try {
			
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeStepPolicyDetail", finStepPolicy);
			map.put("stepDetailDialogCtrl", this);
			map.put("newRecord", finStepPolicy.isNewRecord() == true ?"true" : "false");
			map.put("roleCode", getRole());
			map.put("finStepPoliciesList", getFinStepPoliciesList());
			
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinStepPolicyDetailDialog.zul", window_StepDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
		return this.financeStepPolicyDetailsList;
	}
	public void setFinStepPoliciesList(List<FinanceStepPolicyDetail> finStepPoliciesList) {
		this.financeStepPolicyDetailsList = finStepPoliciesList;
	}

}
