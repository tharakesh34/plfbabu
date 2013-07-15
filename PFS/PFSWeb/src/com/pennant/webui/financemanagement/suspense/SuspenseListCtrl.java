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
 * FileName    		:  SuspenseListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.suspense;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.financemanagement.suspense.model.SuspenseListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/FinanceManagement/Suspense/SuspenseList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SuspenseListCtrl extends GFCBaseListCtrl<FinanceSuspHead> implements Serializable {

	private static final long serialVersionUID = 4481377123949925578L;
	private final static Logger logger = Logger.getLogger(SuspenseListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SuspenseList; 		// autowired
	protected Borderlayout 	borderLayout_SuspenseList; // autowired
	protected Paging 		pagingSuspenseList; 		// autowired
	protected Listbox 		listBoxSuspense; 			// autowired

	// List headers
	protected Listheader listheader_FinReference; 	// autowired
	protected Listheader listheader_CustID; 		// autowired
	protected Listheader listheader_FinIsInSusp; 	// autowired
	protected Listheader listheader_ManualSusp; 	// autowired
	protected Listheader listheader_FinSuspAmt;		// autowired
	protected Listheader listheader_FinCurSuspAmt;	// autowired

	// checkRights
	protected Button btnHelp; 										// autowired
	protected Button button_SuspenseList_SuspenseSearchDialog; 	// autowired
	protected Button button_SuspenseList_PrintList; 				// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceSuspHead> searchObj;
	private transient SuspenseService suspenseService;
	protected Textbox moduleName;
	private String module = "";

	/**
	 * default constructor.<br>
	 */
	public SuspenseListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Suspense object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SuspenseList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if(moduleName.getValue().equals("SUSPHEAD")){
			this.module="Suspense";
		}else if(moduleName.getValue().equals("SUSPENQ")){
			this.module="SuspenseEnquiry";
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_SuspenseList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingSuspenseList.setPageSize(getListRows());
		this.pagingSuspenseList.setDetailed(true);

		this.listheader_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("finReference", false));
		this.listheader_CustID.setSortAscending(new FieldComparator("custID", true));
		this.listheader_CustID.setSortDescending(new FieldComparator("custID", false));
		this.listheader_FinIsInSusp.setSortAscending(new FieldComparator("finIsInSusp", true));
		this.listheader_FinIsInSusp.setSortDescending(new FieldComparator("finIsInSusp", false));
		this.listheader_ManualSusp.setSortAscending(new FieldComparator("manualSusp", true));
		this.listheader_ManualSusp.setSortDescending(new FieldComparator("manualSusp", false));
		this.listheader_FinSuspAmt.setSortAscending(new FieldComparator("finSuspAmt", true));
		this.listheader_FinSuspAmt.setSortDescending(new FieldComparator("finSuspAmt", false));
		this.listheader_FinCurSuspAmt.setSortAscending(new FieldComparator("finCurSuspAmt", true));
		this.listheader_FinCurSuspAmt.setSortDescending(new FieldComparator("finCurSuspAmt", false));

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceSuspHead>(FinanceSuspHead.class,getListRows());
		this.searchObj.addSort("FinReference", false);
		this.searchObj.addTabelName("FinSuspHead_View");
		setSearchObj(this.searchObj);
		
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxSuspense,this.pagingSuspenseList);
		// set the itemRenderer
		this.listBoxSuspense.setItemRenderer(new SuspenseListModelItemRenderer());
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SuspenseList");

		this.button_SuspenseList_SuspenseSearchDialog.setVisible(true);
		this.button_SuspenseList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SuspenseList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.provision.provision.model.SuspenseListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSuspenseItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Suspense object
		final Listitem item = this.listBoxSuspense.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceSuspHead aSuspHead = (FinanceSuspHead) item.getAttribute("data");

			boolean isEnquiry = true;
			if(this.module.equals("Suspense")){
				isEnquiry = false;
			}
			final FinanceSuspHead suspHead = getSuspenseService().getFinanceSuspHeadById(aSuspHead.getFinReference(),isEnquiry);

			if(suspHead==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aSuspHead.getFinReference();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				showDetailView(suspHead);
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param Suspense (aSuspense)
	 * @throws Exception
	 */
	private void showDetailView(FinanceSuspHead aSuspHead) throws Exception {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("suspHead", aSuspHead);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the SuspenseListbox from the
		 * dialog when we do a delete, edit or insert a Suspense.
		 */
		map.put("suspenseListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			if(this.module.equals("Suspense")){
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Suspense/SuspenseDialog.zul",
						null,map);
			}else{
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/SuspenseDetail/SuspenseDetailEnquiryDialog.zul",
						null,map);
			}
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_SuspenseList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		this.pagingSuspenseList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SuspenseList, event);
		this.window_SuspenseList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Suspense dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SuspenseList_SuspenseSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our SuspenseDialog zul-file with parameters. So we can
		 * call them with a object of the selected Suspense. For handed over
		 * these parameter only a Map is accepted. So we put the Suspense object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("suspenseListCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Suspense/SuspenseSearchDialog.zul",
					null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the provision print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SuspenseList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("Suspense", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSuspenseService(SuspenseService suspenseService) {
		this.suspenseService = suspenseService;
	}
	public SuspenseService getSuspenseService() {
		return this.suspenseService;
	}

	public JdbcSearchObject<FinanceSuspHead> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<FinanceSuspHead> searchObj) {
		this.searchObj = searchObj;
	}
}