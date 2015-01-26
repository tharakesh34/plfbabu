package com.pennant.webui.finance.enquiry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.ChequeDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class ChequePrintingDialogCtrl  extends GFCBaseListCtrl<FinanceScheduleDetail> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2919106187676267998L;

	private final static Logger logger = Logger.getLogger(ChequePrintingDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	
	protected Window 		window_ChequePrintingDialog; 		// autoWired
	protected Window        window_ChequePrinting;              // autoWired
	protected Listbox 		listBoxSchedule; 					// autoWired
	protected Borderlayout  borderlayoutChequePrinting;		    // autoWired
	private Tabpanel 		tabPanel_dialogWindow;
	protected Tab 			repayGraphTab;
	protected Div           graphDivTabDiv;
	
	protected Combobox       cbPDCPeriod;
	protected Intbox         noOfCheques;
	protected Combobox        startDate;
	protected Button         button_Print;
	protected Iframe         chequeImageView;
	public byte[] buf = null;
	
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private FinanceScheduleDetail financeScheduleDetail; 
	private FinScheduleData finScheduleData;
	private FinScheduleListItemRenderer finRender;
	private transient boolean validationOn;
	private FinanceScheduleDetail prvSchDetail;
	private List<ValueLabel>  listCPPDCPeriod = PennantStaticListUtil.getPDCPeriodList();
	protected Map<Integer,BigDecimal> repayDetailMap = new HashMap<Integer, BigDecimal>();
	
	@SuppressWarnings("unused")
	private int formatter;
	
	/**
	 * default constructor.<br>
	 */
	public ChequePrintingDialogCtrl() {
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
	public void onCreate$window_ChequePrintingDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());

		if(event != null && event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("finScheduleData")) {
			this.finScheduleData = (FinScheduleData) args.get("finScheduleData");
			setFinScheduleData(finScheduleData);
		}else{
			setFinScheduleData(null);
		}
		
		if (args.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) args.get("financeEnquiryHeaderDialogCtrl");
		}
		
		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {

			if(tabPanel_dialogWindow != null){
				
				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				this.listBoxSchedule.setHeight(this.borderLayoutHeight-rowsHeight-200+"px");
				this.window_ChequePrintingDialog.setHeight(this.borderLayoutHeight-rowsHeight-30+"px");
				tabPanel_dialogWindow.appendChild(this.window_ChequePrintingDialog);

				try {
					// fill the components with the data
					doWriteBeanToComponents(finScheduleData);
					
				} catch (final Exception e) {
					logger.error(e);
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	/**
	 * When user clicks on button "button_Print" button
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$button_Print(Event event) throws Exception{
		logger.debug("Entering " + event.toString());
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.cbPDCPeriod.getSelectedItem().getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			isValidComboValue(this.startDate,Labels.getLabel("label_FinanceEnquiryDialog_CPStartDate.value"));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.noOfCheques.intValue();
			if(!this.noOfCheques.isDisabled()){
				if(this.noOfCheques.intValue()==0){
					throw new WrongValueException(this.noOfCheques,
							Labels.getLabel("NUMBER_MINVALUE",new String[]{Labels.getLabel("label_FinanceEnquiryDialog_CPNoOfCheques.value"),
									String.valueOf("Zero")}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		int startIndex = 0;
		int endIndex=0;

		startIndex = this.startDate.getSelectedIndex();
		if(wve.size()== 0 &&  !this.noOfCheques.isDisabled()){
			int allowedNoCheques = (repayDetailMap.size()-startIndex+1)/(Integer.parseInt(this.cbPDCPeriod.getSelectedItem().getValue().toString()));
			if(this.noOfCheques.getValue() > allowedNoCheques){
				throw new WrongValueException(this.noOfCheques,
						Labels.getLabel("NUMBER_MAXVALUE",new String[]{Labels.getLabel("label_FinanceEnquiryDialog_CPNoOfCheques.value"),
										String.valueOf(allowedNoCheques)}));
			}
		}

		doRemoveValidation();
		if(wve.size() > 0){
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		} 
		ChequeDetails chequeDetails = null;
		ArrayList<ChequeDetails> chequeDetailsList = null;
		chequeDetailsList = new ArrayList<ChequeDetails>();

		endIndex =startIndex+(this.noOfCheques.getValue()*(Integer.parseInt(this.cbPDCPeriod.getSelectedItem().getValue().toString())))-1;
		if(endIndex>repayDetailMap.size()){
			endIndex=repayDetailMap.size();
		}
		

		if(this.cbPDCPeriod.getSelectedItem() != null){
			if(this.cbPDCPeriod.getSelectedItem().getValue().equals("0000")){
				chequeDetailsList = new ArrayList<ChequeDetails>();
				chequeDetailsList.add(null);
				chequeDetails = prepareReportObject(getFinScheduleData());
				BigDecimal repayAmt = getRepayDetails(repayDetailMap,startIndex,repayDetailMap.size());
				chequeDetails.setRepayAmount(PennantAppUtil.amountFormate(repayAmt,
						getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				chequeDetails.setRepayAmountinWords(NumberToEnglishWords.getAmountInText(PennantAppUtil.formateAmount(repayAmt,getFinScheduleData().getFinanceMain().getLovDescFinFormatter()),
						getFinScheduleData().getFinanceMain().getFinCcy()).toUpperCase());
				if(!this.startDate.getSelectedItem().getValue().toString().equals("#")){
					chequeDetails.setAppDate(DateUtility.formatUtilDate(DateUtility.getUtilDate(this.startDate.getSelectedItem().getValue().toString(),PennantConstants.DBDateFormat),PennantConstants.dateFormate));
				}
				chequeDetailsList.add(chequeDetails);		
			} else {
				chequeDetailsList = getChequeDetailsList(chequeDetails, startIndex, endIndex);
			}
		}

		ArrayList<Object> list=new ArrayList<Object>();
		list.add(chequeDetailsList);
		ReportGenerationUtil.print(list, PennantConstants.CHEQUE_PRINTING_CHEQUES, 
				getUserWorkspace().getUserDetails().getUsername(),this.financeEnquiryHeaderDialogCtrl.window_FinEnqHeaderDialog);

		doRemoveValidation();
		logger.debug("Leaving " + event.toString());

	}
	
	/**
	 * Get the Chequelist Detals based on the selected frequency 
	 * @param chequeDetails
	 * @param startIndex
	 * @param endIndex
	 * @return
	 * @throws Exception 
	 */
	public ArrayList<ChequeDetails> getChequeDetailsList(ChequeDetails chequeDetails, int startIndex, int endIndex) throws Exception{
		ArrayList<ChequeDetails>	chequeDetailsList = new ArrayList<ChequeDetails>();
		chequeDetailsList.add(null);
		BigDecimal repaymentAmount = null;
		int pDCPeriod = Integer.parseInt(this.cbPDCPeriod.getSelectedItem().getValue().toString());
		for (int j=startIndex;j<=endIndex;j++) {
			if(pDCPeriod == 0 || j%pDCPeriod==0){
				System.out.println(j);
				chequeDetails=prepareReportObject(getFinScheduleData());
				chequeDetails.setAppDate(DateUtility.formatUtilDate(DateUtility.getUtilDate(this.startDate.getItemAtIndex(j).getValue().toString(),PennantConstants.DBDateFormat),PennantConstants.dateFormate));
				if(j==endIndex){
					repaymentAmount = getRepayDetails(repayDetailMap,j-pDCPeriod+1,repayDetailMap.size());
					if(endIndex != repayDetailMap.size()){
						chequeDetails.setAppDate("");
					}
				}else{
					repaymentAmount = getRepayDetails(repayDetailMap,j-pDCPeriod+1,j);
				}	
				chequeDetails.setRepayAmount(PennantAppUtil.amountFormate(repaymentAmount,
						getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				chequeDetails.setRepayAmountinWords(NumberToEnglishWords.getAmountInText(PennantAppUtil.formateAmount(repaymentAmount,getFinScheduleData().getFinanceMain().getLovDescFinFormatter()),
																			getFinScheduleData().getFinanceMain().getFinCcy()).toUpperCase());

				chequeDetailsList.add(chequeDetails);
			} 
		}
		//}
		return chequeDetailsList;
	}
	
	/**
	 * Get the RepayAmount from the schedule based on the selected period
	 * @param repayDetailMap
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	private BigDecimal getRepayDetails(Map<Integer, BigDecimal> repayDetailMap, int startIndex, int endIndex){
		BigDecimal repayAmount=BigDecimal.ZERO;
		for(int i=startIndex;startIndex<=endIndex;startIndex++){
			repayAmount = repayAmount.add((BigDecimal)repayDetailMap.get(i));
			i++;
		}
		return repayAmount;
	}
	
	/**
	 * Prepare the report object
	 * @param finScheduleData
	 * @return
	 */
	private ChequeDetails prepareReportObject(FinScheduleData finScheduleData){
		ChequeDetails chequeDetails = new ChequeDetails();
		chequeDetails.setFinBranchName(Labels.getLabel("label_AlBarakaIslamicBank"));//finScheduleData.getFinanceMain().getLovDescFinBranchName()
		chequeDetails.setAppDate(DateUtility.formatUtilDate(DateUtility.getUtilDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString(),PennantConstants.DBDateFormat),PennantConstants.dateFormate));
		chequeDetails.setCustName(finScheduleData.getFinanceMain().getLovDescCustFName()+" "+StringUtils.trimToEmpty(finScheduleData.getFinanceMain().getLovDescCustLName()));
		chequeDetails.setDisbAccountId(finScheduleData.getFinanceMain().getRepayAccountId());
		chequeDetails.setFinReference(finScheduleData.getFinanceMain().getFinType()+"-"+finScheduleData.getFinanceMain().getFinReference());
		return chequeDetails;
	}
	
	/**
	 * On changing the Period code
	 * @param e
	 */
	public void onChange$cbPDCPeriod(Event e){
		if(!this.cbPDCPeriod.getSelectedItem().getValue().toString().equals("#") &&
				(Integer.parseInt(this.cbPDCPeriod.getSelectedItem().getValue().toString()) == 0)) {
 			this.startDate.setDisabled(false);
			this.startDate.setSelectedIndex(1);
			this.noOfCheques.setValue(1);
			this.noOfCheques.setDisabled(true);
			this.noOfCheques.setConstraint("");			 
		} else{
			this.startDate.setSelectedIndex(1);
			int allowedNoCheques = (repayDetailMap.size()-	this.startDate.getSelectedIndex()+1)/(Integer.parseInt(this.cbPDCPeriod.getSelectedItem().getValue().toString()));
			this.startDate.setText("");
			this.startDate.setDisabled(false);
			this.noOfCheques.setValue(allowedNoCheques);
			this.noOfCheques.setDisabled(false);
			
			
		}
	}
	
	/**
	 * Set Validation to the  components 
	 */
	public void doSetValidation(){
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.cbPDCPeriod.isDisabled()) {
			this.cbPDCPeriod.setConstraint(new StaticListValidator(listCPPDCPeriod,
							Labels.getLabel("label_FinanceEnquiryDialog_CPPDCPeriod.value")));
		}
		if (!this.noOfCheques.isReadonly()){
			this.noOfCheques.setConstraint("NO EMPTY, NO NEGATIVE:" + Labels.getLabel("PERC_NO_LESS_ZERO"
					,new String[] { Labels.getLabel("label_FinanceEnquiryDialog_CPNoOfCheques.value")}));

		}	
		
		logger.debug("Leaving " );
	}
	
	/**
	 * Write the values to the components
	 * @param aFinSchData
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
			// fill the components with the data
		doFillScheduleList(this.finScheduleData);
		fillComboBox(this.cbPDCPeriod,"",listCPPDCPeriod,"");
		fillSchFromDates(this.startDate,aFinSchData.getFinanceScheduleDetails());
		logger.debug("Leaving " );
	}
	
	/**
	 * Fill the dates from the schedule details 
	 * @param dateCombobox
	 * @param financeScheduleDetails
	 */
	public void fillSchFromDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		
		this.startDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			int count = 1;
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				BigDecimal schedulePaid = curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid());
				if ((curSchd.isRepayOnSchDate() || curSchd.isDeferedPay() || 
						(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) && 
						schedulePaid.compareTo(curSchd.getRepayAmount()) != 0 &&
								curSchd.getRepayAmount().compareTo(BigDecimal.ZERO)>0 ) {
					
					repayDetailMap.put(count, curSchd.getRepayAmount());
					comboitem = new Comboitem();
					comboitem.setAttribute("index", count);
					count++;
					comboitem.setLabel(PennantAppUtil.formateDate(
							curSchd.getSchDate(),
							PennantConstants.dateFormate)+" "+curSchd.getSpecifier());
					comboitem.setAttribute("fromSpecifier",curSchd.getSpecifier());
					comboitem.setValue(curSchd.getSchDate());
					dateCombobox.appendChild(comboitem);
					if (getFinanceScheduleDetail() != null) {
						dateCombobox.appendChild(comboitem);
						if(curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
							dateCombobox.setSelectedItem(comboitem);
						}
					}
				}
			}
		}
		logger.debug("Leaving");
	}
	
	private void doRemoveValidation() {
          logger.debug("Entering");
          setValidation(false);
		this.startDate.setConstraint("");
		this.cbPDCPeriod.setConstraint("");
		this.noOfCheques.setConstraint("");
		  
		logger.debug("Leaving");
	}
	
	public void doClearErrorMessage(){
		logger.debug("Entering");
		
		this.startDate.setErrorMessage("");
		this.cbPDCPeriod.setErrorMessage("");
		this.noOfCheques.setErrorMessage("");
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method to fill the ScheduleList
	 * 
	 * @param FinanceDetail
	 *            (aFinanceDetail)
	 */
	public void doFillScheduleList(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		finRender = new FinScheduleListItemRenderer();
		if (finScheduleData.getFinanceScheduleDetails() != null) {
			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			// Clear all the list items in list box
			this.listBoxSchedule.getItems().clear();
			
			Map<Date, ArrayList<FeeRule>> feeChargesMap = null;
			
			if(finScheduleData.getFeeRules() != null && finScheduleData.getFeeRules().size() > 0){
				feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();
				
				for (FeeRule fee : finScheduleData.getFeeRules()) {
					if(feeChargesMap.containsKey(fee.getSchDate())){
						ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
						feeChargeList.add(fee);
						feeChargesMap.put(fee.getSchDate(), feeChargeList);
					}else{
						ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
						feeChargeList.add(fee);
						feeChargesMap.put(fee.getSchDate(), feeChargeList);
					}
				}
			}
			
			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
			if(finScheduleData.getRepayDetails() != null && finScheduleData.getRepayDetails().size() > 0){
				rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

				for (FinanceRepayments rpyDetail : finScheduleData.getRepayDetails()) {
					if(rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())){
						ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}else{
						ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}
				}
			}

			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail aScheduleDetail = finScheduleData.getFinanceScheduleDetails().get(i);
				boolean showRate = false;
				if (i == 0) {
					prvSchDetail = aScheduleDetail;
					showRate = true;
				} else {
					prvSchDetail = finScheduleData.getFinanceScheduleDetails().get(i - 1);
					if(aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate())!=0){
						showRate = true;
					}
				}

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", finScheduleData);
				if(finScheduleData.getDefermentMap().containsKey(aScheduleDetail.getSchDate())) {
					map.put("defermentDetail", finScheduleData.getDefermentMap().get(aScheduleDetail.getSchDate()));
				}else {
					map.put("defermentDetail", null);
				}
				map.put("financeScheduleDetail", aScheduleDetail);
				map.put("paymentDetailsMap", rpyDetailsMap);
				map.put("window", this.window_ChequePrintingDialog);
				finRender.render(map, prvSchDetail, false, false,true, feeChargesMap, showRate, false);

				if(i == sdSize-1){						
					finRender.render(map, prvSchDetail, true, false,true, feeChargesMap, showRate, false);				
					break;
				}
			}
		}
		logger.debug("Leaving");
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
	
	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}
	
	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}
	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}
	
}
