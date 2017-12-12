package com.pennant.webui.finance.enquiry;

import java.util.ArrayList;
import java.util.List;

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

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinanceProfitEnquiryDialogCtrl extends GFCBaseCtrl<FinanceSummary> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(FinanceProfitEnquiryDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_FinProfitEnquiryDialog; 		
	protected Borderlayout	borderlayoutFinProfitEnquiryDialog;	
	protected Groupbox 		gb_basicDetails; 					
	protected Groupbox 		gb_gracePeriodDetails; 				
	protected Groupbox      gb_installmentDetails;				
	
	protected Grid			grid_BasicDetails;
	protected Grid			grid_GrcDetails;
	protected Grid			grid_Installments;
	
	//Basic Details
	protected Textbox 		finReference; 						
	protected Textbox 		finStatus; 							
	protected Textbox 		finType; 							
	protected Textbox 		finCcy; 							
	protected Textbox 		finBranch;		 					
	protected Textbox 		custID; 							
	protected Datebox 		finStartDate; 						
	protected Datebox 		maturityDate; 						
	protected Datebox 		maturityDate_two; 					
	protected Decimalbox 	finRate;	 						
	protected Datebox 		finLastRepayDate;	 				
	
	// Profit Details
	protected Label 	totalPriSchd;	 						
	protected Label 	totalPftSchd;	 						
	protected Label 	totalOriginal;	 						
	
	protected Label 	outStandPrincipal;	 					
	protected Label 	outStandProfit;	 						
	protected Label 	totalOutStanding;	 					
	
	protected Label 	schdPftPaid;	 						
	protected Label 	schdPriPaid;	 						
	protected Label 	totalPaid;	 							
	
	protected Label 	unPaidPrincipal;	 					
	protected Label 	unPaidProfit;	 						
	protected Label 	totalUnPaid;	 						
	
	protected Label 	overDuePrincipal;	 					
	protected Label 	overDueProfit;	 						
	protected Label 	totalOverDue;	 						
	
	protected Label 	earnedPrincipal;	 					
	protected Label 	earnedProfit;	 						
	protected Label 	totalEarned;	 						
	
	protected Label 	unEarnedPrincipal;	 					
	protected Label 	unEarnedProfit;	 						
	protected Label 	totalUnEarned;	 						
	
	protected Label 	payOffPrincipal;	 					
	protected Label 	payOffProfit;	 						
	protected Label 	totalPayOff;	 						
	
	protected Label 	overDueInstlments;	 					
	protected Label 	overDueInstlementPft;	 				
	protected Label 	finProfitrate;	 				
	
	protected Label 	paidInstlments;	 						
	protected Label 	paidInstlementPft;	 					
	
	// Installments
	protected Label 	unPaidInstlments;	 					
	protected Label 	unPaidInstlementPft;	 				
	
	protected Button     	btnPrint;	        				
	protected Button		btnHelp;							
	
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

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceProfitEnquiry";
	}
	
	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceProfitEnquiry object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinProfitEnquiryDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinProfitEnquiryDialog);

		logger.debug("Entering");
		
		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}
		
		if(arguments.containsKey("financeSummary")) {
			this.financeSummary = (FinanceSummary) arguments.get("financeSummary");
			setFinanceSummary(financeSummary);
		} else {
			setFinanceSummary(null);
		}
		
		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			setFinanceEnquiryHeaderDialogCtrl((FinanceEnquiryHeaderDialogCtrl) arguments.get("financeEnquiryHeaderDialogCtrl"));
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
	
	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			closeDialog();
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
		MessageUtil.showHelpWindow(event, window_FinProfitEnquiryDialog);
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
					list,true, 1, getUserWorkspace().getLoggedInUser().getUserName(), window_FinProfitEnquiryDialog);
		}
		logger.debug("Leaving"+event.toString());
	}
	
	// GUI operations
	
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
			
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		this.finType.setValue(finSummary.getFinType());
		this.finBranch.setValue(finSummary.getFinBranch());
		this.finCcy.setValue(finSummary.getFinCcy());
		this.custID.setValue(finSummary.getCustCIF());
		this.finStatus.setValue(finSummary.getFinStatus());
		if (finSummary.getFinStartDate() != null) {
			this.finStartDate.setValue(finSummary.getFinStartDate());
		} 
		this.maturityDate_two.setValue(finSummary.getMaturityDate());
		this.finRate.setValue(finSummary.getFinRate());
		if(finSummary.getFinLastRepayDate() != null) {
			this.finLastRepayDate.setValue(finSummary.getFinLastRepayDate());
		}
	
		int formatter = CurrencyUtil.getFormat(finSummary.getFinCcy());
		
		this.totalPriSchd.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPriSchd(),
				formatter));
		this.totalPriSchd.setStyle("text-align:right");
		this.totalPftSchd.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPftSchd(), 
				formatter));
		this.totalPftSchd.setStyle("text-align:right");
		this.totalOriginal.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOriginal(), 
				formatter));
		this.totalOriginal.setStyle("text-align:right");
		
		this.outStandPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getOutStandPrincipal(), 
				formatter));
		this.outStandProfit.setValue(PennantAppUtil.amountFormate(finSummary.getOutStandProfit(), 
				formatter));
		this.totalOutStanding.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOutStanding(), 
				formatter));
		
		this.schdPriPaid.setValue(PennantAppUtil.amountFormate(finSummary.getSchdPriPaid(), 
				formatter));
		this.schdPftPaid.setValue(PennantAppUtil.amountFormate(finSummary.getSchdPftPaid(), 
				formatter));
		this.totalPaid.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPaid(), 
				formatter));
		
		this.unPaidPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getUnPaidPrincipal() , 
				formatter));
		this.unPaidProfit.setValue(PennantAppUtil.amountFormate(finSummary.getUnPaidProfit(), 
				formatter));
		this.totalUnPaid.setValue(PennantAppUtil.amountFormate(finSummary.getTotalUnPaid(), 
				formatter));
		
		this.overDuePrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getOverDuePrincipal(), 
				formatter));
		this.overDueProfit.setValue(PennantAppUtil.amountFormate(finSummary.getOverDueProfit(), 
				formatter));
		this.totalOverDue.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOverDue(), 
				formatter));
		
		this.earnedPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getEarnedPrincipal(), 
				formatter));
		this.earnedProfit.setValue(PennantAppUtil.amountFormate(finSummary.getEarnedProfit(), 
				formatter));
		this.totalEarned.setValue(PennantAppUtil.amountFormate(finSummary.getTotalEarned(), 
				formatter));
		
		this.unEarnedPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getUnEarnedPrincipal(), 
				formatter));
		this.unEarnedProfit.setValue(PennantAppUtil.amountFormate(finSummary.getUnEarnedProfit(), 
				formatter));
		this.totalUnEarned.setValue(PennantAppUtil.amountFormate(finSummary.getTotalUnEarned(), 
				formatter));
		
		this.payOffPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getPayOffPrincipal(), 
				formatter));
		this.payOffProfit.setValue(PennantAppUtil.amountFormate(finSummary.getPayOffProfit(), 
				formatter));
		this.totalPayOff.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPayOff(), 
				formatter));
		
		this.overDueInstlments.setValue(String.valueOf(finSummary.getOverDueInstlments()));
		this.overDueInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getOverDueInstlementPft(), 
				formatter));
		this.finProfitrate.setValue(PennantApplicationUtil.formatRate(finSummary.getFinRate().doubleValue(), 2));
		
		this.paidInstlments.setValue(String.valueOf(finSummary.getPaidInstlments()));
		this.paidInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getPaidInstlementPft(), 
				formatter));
		
		this.unPaidInstlments.setValue(String.valueOf(finSummary.getNumberOfTerms() - finSummary.getPaidInstlments()));
		this.unPaidInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getUnPaidInstlementPft(), 
				formatter));
		
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
	
	// Setters And Getters
	
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
