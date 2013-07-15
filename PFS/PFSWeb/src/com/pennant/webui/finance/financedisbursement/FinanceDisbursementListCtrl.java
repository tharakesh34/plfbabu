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
 * FileName    		:  FinanceDisbursementListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financedisbursement;


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
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.service.finance.FinanceDisbursementService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.finance.financedisbursement.model.FinanceDisbursementListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceDisbursement/FinanceDisbursementList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinanceDisbursementListCtrl extends GFCBaseListCtrl<FinanceDisbursement> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinanceDisbursementListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceDisbursementList; // autowired
	protected Borderlayout borderLayout_FinanceDisbursementList; // autowired
	protected Paging pagingFinanceDisbursementList; // autowired
	protected Listbox listBoxFinanceDisbursement; // autowired

	// List headers
	protected Listheader listheader_FinReference; // autowired
	protected Listheader listheader_DisbDate; // autowired
	protected Listheader listheader_DisbDesc; // autowired
	protected Listheader listheader_DisbAmount; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_FinanceDisbursementList_NewFinanceDisbursement; // autowired
	protected Button button_FinanceDisbursementList_FinanceDisbursementSearchDialog; // autowired
	protected Button button_FinanceDisbursementList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceDisbursement> searchObj;
	
	private transient FinanceDisbursementService financeDisbursementService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public FinanceDisbursementListCtrl() {
		super();
	}

	public void onCreate$window_FinanceDisbursementList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinanceDisbursement");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceDisbursement");
			
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
		
		this.borderLayout_FinanceDisbursementList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingFinanceDisbursementList.setPageSize(getListRows());
		this.pagingFinanceDisbursementList.setDetailed(true);

		this.listheader_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("finReference", false));
		this.listheader_DisbDate.setSortAscending(new FieldComparator("disbDate", true));
		this.listheader_DisbDate.setSortDescending(new FieldComparator("disbDate", false));
		this.listheader_DisbDesc.setSortAscending(new FieldComparator("disbDesc", true));
		this.listheader_DisbDesc.setSortDescending(new FieldComparator("disbDesc", false));
		this.listheader_DisbAmount.setSortAscending(new FieldComparator("disbAmount", true));
		this.listheader_DisbAmount.setSortDescending(new FieldComparator("disbAmount", false));
		
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
		this.searchObj = new JdbcSearchObject<FinanceDisbursement>(FinanceDisbursement.class,getListRows());
		this.searchObj.addSort("FinReference", false);
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("FinDisbursementDetails_View");
			if (isFirstTask()) {
				button_FinanceDisbursementList_NewFinanceDisbursement.setVisible(true);
			} else {
				button_FinanceDisbursementList_NewFinanceDisbursement.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("FinDisbursementDetails_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_FinanceDisbursementList_NewFinanceDisbursement.setVisible(false);
			this.button_FinanceDisbursementList_FinanceDisbursementSearchDialog.setVisible(false);
			this.button_FinanceDisbursementList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxFinanceDisbursement,this.pagingFinanceDisbursementList);
			// set the itemRenderer
			this.listBoxFinanceDisbursement.setItemRenderer(new FinanceDisbursementListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceDisbursementList");
		
		this.button_FinanceDisbursementList_NewFinanceDisbursement.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementList_NewFinanceDisbursement"));
		this.button_FinanceDisbursementList_FinanceDisbursementSearchDialog.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementList_FinanceDisbursementFindDialog"));
		this.button_FinanceDisbursementList_PrintList.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.finance.financedisbursement.model.FinanceDisbursementListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onFinanceDisbursementItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected FinanceDisbursement object
		final Listitem item = this.listBoxFinanceDisbursement.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceDisbursement aFinanceDisbursement = (FinanceDisbursement) item.getAttribute("data");
			final FinanceDisbursement financeDisbursement = getFinanceDisbursementService().getFinanceDisbursementById(aFinanceDisbursement.getId(),false);
			
			if(financeDisbursement==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceDisbursement.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ financeDisbursement.getFinReference()+"' AND version=" + financeDisbursement.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "FinanceDisbursement", whereCond, financeDisbursement.getTaskId(), financeDisbursement.getNextTaskId());
					if (userAcces){
						showDetailView(financeDisbursement);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(financeDisbursement);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the FinanceDisbursement dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinanceDisbursementList_NewFinanceDisbursement(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new FinanceDisbursement object, We GET it from the backend.
		final FinanceDisbursement aFinanceDisbursement = getFinanceDisbursementService().getNewFinanceDisbursement(false);
		showDetailView(aFinanceDisbursement);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param FinanceDisbursement (aFinanceDisbursement)
	 * @throws Exception
	 */
	private void showDetailView(FinanceDisbursement aFinanceDisbursement) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aFinanceDisbursement.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceDisbursement.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDisbursement", aFinanceDisbursement);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the FinanceDisbursementListbox from the
		 * dialog when we do a delete, edit or insert a FinanceDisbursement.
		 */
		map.put("financeDisbursementListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceDisbursement/FinanceDisbursementDialog.zul",null,map);
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
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinanceDisbursementList);
		logger.debug("Leaving");
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
		logger.debug(event.toString());
		this.pagingFinanceDisbursementList.setActivePage(0);
		Events.postEvent("onCreate", this.window_FinanceDisbursementList, event);
		this.window_FinanceDisbursementList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the FinanceDisbursement dialog
	 */
	
	public void onClick$button_FinanceDisbursementList_FinanceDisbursementSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our FinanceDisbursementDialog zul-file with parameters. So we can
		 * call them with a object of the selected FinanceDisbursement. For handed over
		 * these parameter only a Map is accepted. So we put the FinanceDisbursement object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDisbursementCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceDisbursement/FinanceDisbursementSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the financeDisbursement print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FinanceDisbursementList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("FinanceDisbursement", getSearchObj());
		logger.debug("Leaving");
	}

	public void setFinanceDisbursementService(FinanceDisbursementService financeDisbursementService) {
		this.financeDisbursementService = financeDisbursementService;
	}

	public FinanceDisbursementService getFinanceDisbursementService() {
		return this.financeDisbursementService;
	}

	public JdbcSearchObject<FinanceDisbursement> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceDisbursement> searchObj) {
		this.searchObj = searchObj;
	}
}