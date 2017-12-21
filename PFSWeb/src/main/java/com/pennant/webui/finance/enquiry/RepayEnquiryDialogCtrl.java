package com.pennant.webui.finance.enquiry;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.UiException;
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
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class RepayEnquiryDialogCtrl extends GFCBaseCtrl<FinanceRepayments> {
	private static final long serialVersionUID = 2338460659547934642L;
	private static final Logger logger = Logger.getLogger(RepayEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_RepayEnquiryDialog; 		
	protected Listbox 		listBox_RepayEnquiry;			
	protected Borderlayout 	borderlayout_RepayEnquiry; 		
	
	// List headers
	protected Listheader listheader_RepayDate; 			
	protected Listheader listheader_FinRepayFor; 		
	protected Listheader listheader_SchdPft; 			
	protected Listheader listheader_FinSchdPri; 		
	protected Listheader listheader_TotalSchd; 			
	protected Listheader listheader_Balance;			
	
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
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_RepayEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RepayEnquiryDialog);

		try {
			if (event.getTarget().getParent().getParent() != null) {
				tabPanel_dialogWindow = (Tabpanel) event.getTarget()
						.getParent().getParent();
			}

			if (arguments.containsKey("financeRepayments")) {
				this.finRepayments = (List<FinanceRepayments>) arguments
						.get("financeRepayments");
			} else {
				this.finRepayments = null;
			}

			if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
				this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
						.get("financeEnquiryHeaderDialogCtrl");
			}
			if (arguments.containsKey("finAmountformatter")) {
				this.formatter = (Integer) arguments.get("finAmountformatter");
			}

			// Render List in Listbox
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RepayEnquiryDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doShowDialog() throws Exception {
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
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_RepayEnquiryDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public List<FinanceRepayments> getFinRepayments() {
		return finRepayments;
	}
	public void setFinRepayments(List<FinanceRepayments> finRepayments) {
		this.finRepayments = finRepayments;
	}
}
