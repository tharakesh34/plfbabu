package com.pennant.webui.finance.enquiry;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.webui.finance.enquiry.model.RepayEnquiryComparator;
import com.pennant.webui.finance.enquiry.model.RepayEnquiryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class RepayEnquiryDialogCtrl  extends GFCBaseListCtrl<FinanceRepayments> implements Serializable {

	private static final long serialVersionUID = 2338460659547934642L;
	private final static Logger logger = Logger.getLogger(RepayEnquiryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_RepayEnquiryDialog; 		// autoWired
	protected Listbox 		listBox_RepayEnquiry;			// autoWired
	protected Borderlayout 	borderlayout_RepayEnquiry; 		// autoWired
	
	// List headers
	protected Listheader listheader_RepayDate; 			// autoWired
	protected Listheader listheader_FinRepayFor; 		// autoWired
	protected Listheader listheader_SchdPft; 			// autoWired
	protected Listheader listheader_FinSchdPri; 		// autoWired
	protected Listheader listheader_TotalSchd; 			// autoWired
	protected Listheader listheader_Balance;			// autoWired
	
	// not auto wired variables
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinanceRepayments> finRepayments;
	private Tabpanel 		tabPanel_dialogWindow;
	
	private int formatter;
	/**
	 * default constructor.<br>
	 */
	public RepayEnquiryDialogCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_RepayEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		if(event != null && event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}
		
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED parameters !
		if (args.containsKey("financeRepayments")) {
			this.finRepayments = (List<FinanceRepayments>) args.get("financeRepayments");
		} else {
			this.finRepayments = null;
		}
		
		if (args.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) args.get("financeEnquiryHeaderDialogCtrl");
		}
		if (args.containsKey("finAmountformatter")) {
			this.formatter =  (Integer) args.get("finAmountformatter");
		}
		
		//Render List in Listbox
		doShowDialog();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		
		try {
			
			//Fill Repayment Details
			if(finRepayments != null){
				this.listBox_RepayEnquiry.setModel(new GroupsModelArray(
						finRepayments.toArray(),new RepayEnquiryComparator()));		
				this.listBox_RepayEnquiry.setItemRenderer(new RepayEnquiryListModelItemRenderer(formatter));
			}
			if(tabPanel_dialogWindow != null){

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				this.listBox_RepayEnquiry.setHeight(this.borderLayoutHeight-rowsHeight-90+"px");
				this.window_RepayEnquiryDialog.setHeight(this.borderLayoutHeight-rowsHeight-45+"px");
				tabPanel_dialogWindow.appendChild(this.window_RepayEnquiryDialog);

			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public List<FinanceRepayments> getFinRepayments() {
		return finRepayments;
	}
	public void setFinRepayments(List<FinanceRepayments> finRepayments) {
		this.finRepayments = finRepayments;
	}
}
