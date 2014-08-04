
package com.pennant.webui.finance.enquiry;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.webui.applicationmaster.checklist.model.CheckListDetailEnquiryListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class CheckListEnquiryDialogCtrl  extends GFCBaseListCtrl<FinanceCheckListReference> implements Serializable {

	private static final long serialVersionUID = 2338460659547934642L;
	private final static Logger logger = Logger.getLogger(CheckListEnquiryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CheckListEnquiryDialog; 		// autoWired
	protected Listbox 		listBoxCheckList;					// autoWired
	protected Borderlayout 	borderlayoutCheckListEnquiry; 		// autoWired

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
	public void onCreate$window_CheckListEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());

		if(event != null && event.getTarget().getParent().getParent() != null){
			tabpanel_ChkDetails = (Tabpanel) event.getTarget().getParent().getParent();
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("FinanceCheckListReference")) {
			setFinanceCheckListReference((List<FinanceCheckListReference>) args.get("FinanceCheckListReference"));
		} else {
			setFinanceCheckListReference(null);
		}

		if (args.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) args.get("financeEnquiryHeaderDialogCtrl");
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
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public List<FinanceCheckListReference> getFinanceCheckListReference() {
		return financeCheckListReference;
	}
	public void setFinanceCheckListReference(List<FinanceCheckListReference> financeCheckListReference) {
		this.financeCheckListReference = financeCheckListReference;
	}
}
