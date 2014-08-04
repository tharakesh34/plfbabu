package com.pennant.webui.finance.enquiry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;


public class FinanceProfitEnquiryDialogCtrl extends GFCBaseListCtrl<FinanceSummary> implements Serializable {
	
	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(FinanceProfitEnquiryDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	
	protected Window 		window_FinProfitEnquiryDialog; 		// autoWired
	protected Borderlayout	borderlayoutFinProfitEnquiryDialog;	// autoWired
	protected Groupbox 		gb_basicDetails; 					// autoWired
	protected Groupbox 		gb_gracePeriodDetails; 				// autoWired
	protected Groupbox      gb_installmentDetails;				// autoWired
	
	protected Grid			grid_BasicDetails;
	protected Grid			grid_GrcDetails;
	protected Grid			grid_Installments;
	
	//Basic Details
	protected Textbox 		finReference; 						// autoWired
	protected Textbox 		finStatus; 							// autoWired
	protected Textbox 		finType; 							// autoWired
	protected Textbox 		finCcy; 							// autoWired
	protected Textbox 		finBranch;		 					// autoWired
	protected Textbox 		custID; 							// autoWired
	protected Datebox 		finStartDate; 						// autoWired
	protected Datebox 		maturityDate; 						// autoWired
	protected Datebox 		maturityDate_two; 					// autoWired
	protected Decimalbox 	finRate;	 						// autoWired
	protected Datebox 		finLastRepayDate;	 				// autoWired
	
	// Profit Details
	protected Label 	totalPriSchd;	 						// autoWired
	protected Label 	totalPftSchd;	 						// autoWired
	protected Label 	totalOriginal;	 						// autoWired
	
	protected Label 	outStandPrincipal;	 					// autoWired
	protected Label 	outStandProfit;	 						// autoWired
	protected Label 	totalOutStanding;	 					// autoWired
	
	protected Label 	schdPftPaid;	 						// autoWired
	protected Label 	schdPriPaid;	 						// autoWired
	protected Label 	totalPaid;	 							// autoWired
	
	protected Label 	unPaidPrincipal;	 					// autoWired
	protected Label 	unPaidProfit;	 						// autoWired
	protected Label 	totalUnPaid;	 						// autoWired
	
	protected Label 	overDuePrincipal;	 					// autoWired
	protected Label 	overDueProfit;	 						// autoWired
	protected Label 	totalOverDue;	 						// autoWired
	
	protected Label 	earnedPrincipal;	 					// autoWired
	protected Label 	earnedProfit;	 						// autoWired
	protected Label 	totalEarned;	 						// autoWired
	
	protected Label 	unEarnedPrincipal;	 					// autoWired
	protected Label 	unEarnedProfit;	 						// autoWired
	protected Label 	totalUnEarned;	 						// autoWired
	
	protected Label 	payOffPrincipal;	 					// autoWired
	protected Label 	payOffProfit;	 						// autoWired
	protected Label 	totalPayOff;	 						// autoWired
	
	protected Label 	overDueInstlments;	 					// autoWired
	protected Label 	overDueInstlementPft;	 				// autoWired
	protected Label 	finProfitrate;	 				// autoWired
	
	protected Label 	paidInstlments;	 						// autoWired
	protected Label 	paidInstlementPft;	 					// autoWired
	
	// Installments
	protected Label 	unPaidInstlments;	 					// autoWired
	protected Label 	unPaidInstlementPft;	 				// autoWired
	
	protected Button     	btnPrint;	        				// autoWired
	protected Button     	btnClose;	        				// autoWired
	protected Button		btnHelp;							// autoWired
	
	private FinanceSummary financeSummary;
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private Tabpanel tabPanel_dialogWindow;
	protected Div div_toolbar;
	
	/**
	 * default constructor.<br>
	 */
	public FinanceProfitEnquiryDialogCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceProfitEnquiry object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinProfitEnquiryDialog(Event event)throws Exception {
		logger.debug("Entering");
		
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		if (event != null && event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}
		
		if(args.containsKey("financeSummary")) {
			this.financeSummary = (FinanceSummary) args.get("financeSummary");
			setFinanceSummary(financeSummary);
		} else {
			setFinanceSummary(null);
		}
		
		if (args.containsKey("financeEnquiryHeaderDialogCtrl")) {
			setFinanceEnquiryHeaderDialogCtrl((FinanceEnquiryHeaderDialogCtrl) args.get("financeEnquiryHeaderDialogCtrl"));
		} else {
			setFinanceEnquiryHeaderDialogCtrl(null);
		}
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(this.financeSummary);

		logger.debug("Leaving");
	}
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			closeDialog(this.window_FinProfitEnquiryDialog, "FinanceProfitEnquiry");
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinProfitEnquiryDialog);
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "PrintButton" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPrint(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		List<Object> list =  new ArrayList<Object>();
		
		if(this.financeSummary!= null) {
			
			list.add(financeSummary);
			ReportGenerationUtil.generateReport("FinanceDetail", financeSummary,
					list,true, 1, getUserWorkspace().getUserDetails().getUsername(), window_FinProfitEnquiryDialog);
		}
		logger.debug("Leaving"+event.toString());
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param FinanceProfitEnquiry
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceSummary financeSummary) throws InterruptedException {
		logger.debug("Entering");
		doReadOnly();
		try {
			// fill the components with the data
			doWriteBeanToComponents(financeSummary);
			
			if (tabPanel_dialogWindow != null) {
				
				this.financeEnquiryHeaderDialogCtrl.grid_BasicDetails.setVisible(false);
				this.div_toolbar.setVisible(false);
				getBorderLayoutHeight();
				this.window_FinProfitEnquiryDialog.setHeight(this.borderLayoutHeight+"px");
				tabPanel_dialogWindow.appendChild(this.window_FinProfitEnquiryDialog);
				
			}
			
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param FinanceProfitEnquiry
	 * @throws InterruptedException 
	 */
	public void doWriteBeanToComponents(FinanceSummary finSummary)throws InterruptedException {
		logger.debug("Entering");
		
		this.finReference.setValue(finSummary.getFinReference());
		this.finType.setValue(finSummary.getFinType()+"-"+finSummary.getLovDescFinTypeName());
		this.finBranch.setValue(finSummary.getFinBranch()+"-"+finSummary.getLovDescFinBranchName());
		this.finCcy.setValue(finSummary.getFinCcy()+"-"+finSummary.getLovDescFinCcyName());
		this.custID.setValue(finSummary.getCustID()+"-"+finSummary.getLovDescCustShrtName());
		this.finStatus.setValue(finSummary.getFinStatus());
		if (finSummary.getFinStartDate() != null) {
			this.finStartDate.setValue(finSummary.getFinStartDate());
		} 
		this.maturityDate_two.setValue(finSummary.getMaturityDate());
		this.finRate.setValue(finSummary.getFinRate());
		if(finSummary.getFinLastRepayDate() != null) {
			this.finLastRepayDate.setValue(finSummary.getFinLastRepayDate());
		}
		
		this.totalPriSchd.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPriSchd(),
				finSummary.getCcyEditField()));
		this.totalPriSchd.setStyle("text-align:right");
		this.totalPftSchd.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPftSchd(), 
				finSummary.getCcyEditField()));
		this.totalPftSchd.setStyle("text-align:right");
		this.totalOriginal.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOriginal(), 
				finSummary.getCcyEditField()));
		this.totalOriginal.setStyle("text-align:right");
		
		this.outStandPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getOutStandPrincipal(), 
				finSummary.getCcyEditField()));
		this.outStandProfit.setValue(PennantAppUtil.amountFormate(finSummary.getOutStandProfit(), 
				finSummary.getCcyEditField()));
		this.totalOutStanding.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOutStanding(), 
				finSummary.getCcyEditField()));
		
		this.schdPriPaid.setValue(PennantAppUtil.amountFormate(finSummary.getSchdPriPaid(), 
				finSummary.getCcyEditField()));
		this.schdPftPaid.setValue(PennantAppUtil.amountFormate(finSummary.getSchdPftPaid(), 
				finSummary.getCcyEditField()));
		this.totalPaid.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPaid(), 
				finSummary.getCcyEditField()));
		
		this.unPaidPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getUnPaidPrincipal() , 
				finSummary.getCcyEditField()));
		this.unPaidProfit.setValue(PennantAppUtil.amountFormate(finSummary.getUnPaidProfit(), 
				finSummary.getCcyEditField()));
		this.totalUnPaid.setValue(PennantAppUtil.amountFormate(finSummary.getTotalUnPaid(), 
				finSummary.getCcyEditField()));
		
		this.overDuePrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getOverDuePrincipal(), 
				finSummary.getCcyEditField()));
		this.overDueProfit.setValue(PennantAppUtil.amountFormate(finSummary.getOverDueProfit(), 
				finSummary.getCcyEditField()));
		this.totalOverDue.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOverDue(), 
				finSummary.getCcyEditField()));
		
		this.earnedPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getEarnedPrincipal(), 
				finSummary.getCcyEditField()));
		this.earnedProfit.setValue(PennantAppUtil.amountFormate(finSummary.getEarnedProfit(), 
				finSummary.getCcyEditField()));
		this.totalEarned.setValue(PennantAppUtil.amountFormate(finSummary.getTotalEarned(), 
				finSummary.getCcyEditField()));
		
		this.unEarnedPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getUnEarnedPrincipal(), 
				finSummary.getCcyEditField()));
		this.unEarnedProfit.setValue(PennantAppUtil.amountFormate(finSummary.getUnEarnedProfit(), 
				finSummary.getCcyEditField()));
		this.totalUnEarned.setValue(PennantAppUtil.amountFormate(finSummary.getTotalUnEarned(), 
				finSummary.getCcyEditField()));
		
		this.payOffPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getPayOffPrincipal(), 
				finSummary.getCcyEditField()));
		this.payOffProfit.setValue(PennantAppUtil.amountFormate(finSummary.getPayOffProfit(), 
				finSummary.getCcyEditField()));
		this.totalPayOff.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPayOff(), 
				finSummary.getCcyEditField()));
		
		this.overDueInstlments.setValue(String.valueOf(finSummary.getOverDueInstlments()));
		this.overDueInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getOverDueInstlementPft(), 
				finSummary.getCcyEditField()));
		this.finProfitrate.setValue(PennantApplicationUtil.formatRate(finSummary.getFinRate().doubleValue(), 2));
		
		this.paidInstlments.setValue(String.valueOf(finSummary.getPaidInstlments()));
		this.paidInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getPaidInstlementPft(), 
				finSummary.getCcyEditField()));
		
		this.unPaidInstlments.setValue(String.valueOf(finSummary.getNumberOfTerms() - finSummary.getPaidInstlments()));
		this.unPaidInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getUnPaidInstlementPft(), 
				finSummary.getCcyEditField()));
		
		logger.debug("Leaving");
	}
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
		this.finStatus.setReadonly(true);
		this.maturityDate.setDisabled(true);
		this.finType.setReadonly(true);
		this.finCcy.setReadonly(true);
		this.custID.setReadonly(true);
		this.finStartDate.setDisabled(true);
		this.finRate.setReadonly(true);
		this.finLastRepayDate.setDisabled(true);
		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Setters And Getters ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	public FinanceSummary getFinanceSummary() {
		return financeSummary;
	}
	public FinanceEnquiryHeaderDialogCtrl getFinanceEnquiryHeaderDialogCtrl() {
		return financeEnquiryHeaderDialogCtrl;
	}

	public void setFinanceEnquiryHeaderDialogCtrl(FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl) {
		this.financeEnquiryHeaderDialogCtrl = financeEnquiryHeaderDialogCtrl;
	}
	public void setFinanceSummary(FinanceSummary financeSummary) {
		this.financeSummary = financeSummary;
	}
}
