package com.pennant.webui.finance.enquiry;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.webui.applicationmaster.checklist.model.CheckListDetailEnquiryListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CheckListEnquiryDialogCtrl extends GFCBaseCtrl<FinanceCheckListReference> {
	private static final long serialVersionUID = 2338460659547934642L;
	private static final Logger logger = Logger.getLogger(CheckListEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_CheckListEnquiryDialog; 		
	protected Listbox 		listBoxCheckList;					
	protected Borderlayout 	borderlayoutCheckListEnquiry; 		

	// not auto wired variables
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinanceCheckListReference> financeCheckListReference;
	private Tabpanel 		tabpanel_ChkDetails;

	/**
	 * default constructor.<br>
	 */
	public CheckListEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}
                                           
	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CheckListEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CheckListEnquiryDialog);

		if(event.getTarget().getParent().getParent() != null){
			tabpanel_ChkDetails = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("FinanceCheckListReference")) {
			setFinanceCheckListReference((List<FinanceCheckListReference>) arguments.get("FinanceCheckListReference"));
		} else {
			setFinanceCheckListReference(null);
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments.get("financeEnquiryHeaderDialogCtrl");
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
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			
			this.listBoxCheckList.setItemRenderer(new CheckListDetailEnquiryListItemRenderer()); 
			getPagedListWrapper().initList(getFinanceCheckListReference(), listBoxCheckList, new Paging());
			
			if(tabpanel_ChkDetails != null){

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				this.listBoxCheckList.setHeight(this.borderLayoutHeight-rowsHeight-90+"px");
				this.window_CheckListEnquiryDialog.setHeight(this.borderLayoutHeight-rowsHeight-45+"px");
				tabpanel_ChkDetails.appendChild(this.window_CheckListEnquiryDialog);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public List<FinanceCheckListReference> getFinanceCheckListReference() {
		return financeCheckListReference;
	}
	public void setFinanceCheckListReference(List<FinanceCheckListReference> financeCheckListReference) {
		this.financeCheckListReference = financeCheckListReference;
	}
}
