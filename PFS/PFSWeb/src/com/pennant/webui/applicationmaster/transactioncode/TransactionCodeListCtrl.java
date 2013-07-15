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
 * FileName    		:  TransactionCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.transactioncode;


import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
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
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.service.applicationmaster.TransactionCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.transactioncode.model.TransactionCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/TransactionCode/TransactionCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class TransactionCodeListCtrl extends GFCBaseListCtrl<TransactionCode> implements Serializable {

	private static final long serialVersionUID = 8484399111058985206L;
	private final static Logger logger = Logger.getLogger(TransactionCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_TransactionCodeList; 		// autoWired
	protected Borderlayout 	borderLayout_TransactionCodeList; 	// autoWired
	protected Paging 		pagingTransactionCodeList; 			// autoWired
	protected Listbox 		listBoxTransactionCode; 			// autoWired

	// List headers
	protected Listheader listheader_TranCode; 		// autoWired
	protected Listheader listheader_TranDesc; 		// autoWired
	protected Listheader listheader_TranType; 		// autoWired
	protected Listheader listheader_TranIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autoWired
	protected Button button_TransactionCodeList_NewTransactionCode; // autoWired
	protected Button button_TransactionCodeList_TransactionCodeSearchDialog; // autoWired
	protected Button button_TransactionCodeList_PrintList; // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<TransactionCode> searchObj;
	
	private transient TransactionCodeService transactionCodeService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public TransactionCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected TransactionCode object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TransactionCodeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("TransactionCode");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("TransactionCode");
			
			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}
		
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_TransactionCodeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingTransactionCodeList.setPageSize(getListRows());
		this.pagingTransactionCodeList.setDetailed(true);

		this.listheader_TranCode.setSortAscending(new FieldComparator("tranCode", true));
		this.listheader_TranCode.setSortDescending(new FieldComparator("tranCode", false));
		this.listheader_TranDesc.setSortAscending(new FieldComparator("tranDesc", true));
		this.listheader_TranDesc.setSortDescending(new FieldComparator("tranDesc", false));
		this.listheader_TranType.setSortAscending(new FieldComparator("tranType", true));
		this.listheader_TranType.setSortDescending(new FieldComparator("tranType", false));
		this.listheader_TranIsActive.setSortAscending(new FieldComparator("tranIsActive", true));
		this.listheader_TranIsActive.setSortDescending(new FieldComparator("tranIsActive", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<TransactionCode>(TransactionCode.class,getListRows());
		this.searchObj.addSort("TranCode", false);
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTTransactionCode_View");
			if (isFirstTask()) {
				button_TransactionCodeList_NewTransactionCode.setVisible(true);
			} else {
				button_TransactionCodeList_NewTransactionCode.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("BMTTransactionCode_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_TransactionCodeList_NewTransactionCode.setVisible(false);
			this.button_TransactionCodeList_TransactionCodeSearchDialog.setVisible(false);
			this.button_TransactionCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxTransactionCode,
					this.pagingTransactionCodeList);
			// set the itemRenderer
			this.listBoxTransactionCode.setItemRenderer(new TransactionCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("TransactionCodeList");
		
		this.button_TransactionCodeList_NewTransactionCode.setVisible(getUserWorkspace().
				isAllowed("button_TransactionCodeList_NewTransactionCode"));
		this.button_TransactionCodeList_TransactionCodeSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_TransactionCodeList_TransactionCodeFindDialog"));
		this.button_TransactionCodeList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_TransactionCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.transactioncode.model.
	 * TransactionCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onTransactionCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected TransactionCode object
		final Listitem item = this.listBoxTransactionCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final TransactionCode aTransactionCode = (TransactionCode) item.getAttribute("data");
			final TransactionCode transactionCode = getTransactionCodeService().getTransactionCodeById(
					aTransactionCode.getId());
			
			if(transactionCode==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aTransactionCode.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_TranCode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), 
						getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND TranCode='"+ transactionCode.getTranCode()
								+"' AND version=" + transactionCode.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), 
							"TransactionCode", whereCond, transactionCode.getTaskId(), 
							transactionCode.getNextTaskId());
					if (userAcces){
						showDetailView(transactionCode);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(transactionCode);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the TransactionCode dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_TransactionCodeList_NewTransactionCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new TransactionCode object, We GET it from the backEnd.
		final TransactionCode aTransactionCode = getTransactionCodeService().getNewTransactionCode();
		showDetailView(aTransactionCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param TransactionCode (aTransactionCode)
	 * @throws Exception
	 */
	private void showDetailView(TransactionCode aTransactionCode) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */		
		if(aTransactionCode.getWorkflowId()==0 && isWorkFlowEnabled()){
			aTransactionCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("transactionCode", aTransactionCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the TransactionCodeListbox from the
		 * dialog when we do a delete, edit or insert a TransactionCode.
		 */
		map.put("transactionCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/TransactionCode/TransactionCodeDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_TransactionCodeList);
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
		this.pagingTransactionCodeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_TransactionCodeList, event);
		this.window_TransactionCodeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the TransactionCode dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_TransactionCodeList_TransactionCodeSearchDialog(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		
		/*
		 * we can call our TransactionCodeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected TransactionCode. For handed over
		 * these parameter only a Map is accepted. So we put the TransactionCode object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("transactionCodeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
				"/WEB-INF/pages/ApplicationMaster/TransactionCode/TransactionCodeSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the transactionCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_TransactionCodeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("TransactionCode", getSearchObj(),this.pagingTransactionCodeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setTransactionCodeService(TransactionCodeService transactionCodeService) {
		this.transactionCodeService = transactionCodeService;
	}
	public TransactionCodeService getTransactionCodeService() {
		return this.transactionCodeService;
	}

	public JdbcSearchObject<TransactionCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<TransactionCode> searchObj) {
		this.searchObj = searchObj;
	}
}