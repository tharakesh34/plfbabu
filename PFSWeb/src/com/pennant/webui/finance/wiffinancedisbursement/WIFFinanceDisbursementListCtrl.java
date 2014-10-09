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
 * FileName    		:  WIFFinanceDisbursementListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.wiffinancedisbursement;


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
import com.pennant.backend.service.finance.WIFFinanceDisbursementService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.finance.wiffinancedisbursement.model.WIFFinanceDisbursementListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/WIFFinanceDisbursement/WIFFinanceDisbursementList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class WIFFinanceDisbursementListCtrl extends GFCBaseListCtrl<FinanceDisbursement> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(WIFFinanceDisbursementListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_WIFFinanceDisbursementList; // autowired
	protected Borderlayout borderLayout_WIFFinanceDisbursementList; // autowired
	protected Paging pagingWIFFinanceDisbursementList; // autowired
	protected Listbox listBoxWIFFinanceDisbursement; // autowired

	// List headers
	protected Listheader listheader_FinReference; // autowired
	protected Listheader listheader_DisbDate; // autowired
	protected Listheader listheader_DisbDesc; // autowired
	protected Listheader listheader_DisbAmount; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_WIFFinanceDisbursementList_NewWIFFinanceDisbursement; // autowired
	protected Button button_WIFFinanceDisbursementList_WIFFinanceDisbursementSearchDialog; // autowired
	protected Button button_WIFFinanceDisbursementList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceDisbursement> searchObj;
	
	private transient WIFFinanceDisbursementService wIFFinanceDisbursementService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public WIFFinanceDisbursementListCtrl() {
		super();
	}

	public void onCreate$window_WIFFinanceDisbursementList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("WIFFinanceDisbursement");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("WIFFinanceDisbursement");
			
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
		
		this.borderLayout_WIFFinanceDisbursementList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingWIFFinanceDisbursementList.setPageSize(getListRows());
		this.pagingWIFFinanceDisbursementList.setDetailed(true);

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
			this.searchObj.addTabelName("WIFFinDisbursementDetails_View");
			if (isFirstTask()) {
				button_WIFFinanceDisbursementList_NewWIFFinanceDisbursement.setVisible(true);
			} else {
				button_WIFFinanceDisbursementList_NewWIFFinanceDisbursement.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("WIFFinDisbursementDetails_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_WIFFinanceDisbursementList_NewWIFFinanceDisbursement.setVisible(false);
			this.button_WIFFinanceDisbursementList_WIFFinanceDisbursementSearchDialog.setVisible(false);
			this.button_WIFFinanceDisbursementList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxWIFFinanceDisbursement,this.pagingWIFFinanceDisbursementList);
			// set the itemRenderer
			this.listBoxWIFFinanceDisbursement.setItemRenderer(new WIFFinanceDisbursementListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("WIFFinanceDisbursementList");
		
		this.button_WIFFinanceDisbursementList_NewWIFFinanceDisbursement.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceDisbursementList_NewWIFFinanceDisbursement"));
		this.button_WIFFinanceDisbursementList_WIFFinanceDisbursementSearchDialog.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceDisbursementList_WIFFinanceDisbursementFindDialog"));
		this.button_WIFFinanceDisbursementList_PrintList.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceDisbursementList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.finance.wiffinancedisbursement.model.WIFFinanceDisbursementListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onWIFFinanceDisbursementItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected WIFFinanceDisbursement object
		final Listitem item = this.listBoxWIFFinanceDisbursement.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceDisbursement aWIFFinanceDisbursement = (FinanceDisbursement) item.getAttribute("data");
			final FinanceDisbursement wIFFinanceDisbursement = getWIFFinanceDisbursementService().getWIFFinanceDisbursementById(aWIFFinanceDisbursement.getId());
			
			if(wIFFinanceDisbursement==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aWIFFinanceDisbursement.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ wIFFinanceDisbursement.getFinReference()+"' AND version=" + wIFFinanceDisbursement.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "WIFFinanceDisbursement", whereCond, wIFFinanceDisbursement.getTaskId(), wIFFinanceDisbursement.getNextTaskId());
					if (userAcces){
						showDetailView(wIFFinanceDisbursement);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(wIFFinanceDisbursement);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the WIFFinanceDisbursement dialog with a new empty entry. <br>
	 */
	public void onClick$button_WIFFinanceDisbursementList_NewWIFFinanceDisbursement(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new WIFFinanceDisbursement object, We GET it from the backend.
		final FinanceDisbursement aWIFFinanceDisbursement = getWIFFinanceDisbursementService().getNewWIFFinanceDisbursement();
		showDetailView(aWIFFinanceDisbursement);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param WIFFinanceDisbursement (aWIFFinanceDisbursement)
	 * @throws Exception
	 */
	private void showDetailView(FinanceDisbursement aWIFFinanceDisbursement) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aWIFFinanceDisbursement.getWorkflowId()==0 && isWorkFlowEnabled()){
			aWIFFinanceDisbursement.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("wIFFinanceDisbursement", aWIFFinanceDisbursement);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the WIFFinanceDisbursementListbox from the
		 * dialog when we do a delete, edit or insert a WIFFinanceDisbursement.
		 */
		map.put("wIFFinanceDisbursementListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceDisbursement/WIFFinanceDisbursementDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_WIFFinanceDisbursementList);
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
		this.pagingWIFFinanceDisbursementList.setActivePage(0);
		Events.postEvent("onCreate", this.window_WIFFinanceDisbursementList, event);
		this.window_WIFFinanceDisbursementList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the WIFFinanceDisbursement dialog
	 */
	
	public void onClick$button_WIFFinanceDisbursementList_WIFFinanceDisbursementSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our WIFFinanceDisbursementDialog zul-file with parameters. So we can
		 * call them with a object of the selected WIFFinanceDisbursement. For handed over
		 * these parameter only a Map is accepted. So we put the WIFFinanceDisbursement object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("wIFFinanceDisbursementCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceDisbursement/WIFFinanceDisbursementSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the wIFFinanceDisbursement print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_WIFFinanceDisbursementList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("WIFFinanceDisbursement", getSearchObj(),this.pagingWIFFinanceDisbursementList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setWIFFinanceDisbursementService(WIFFinanceDisbursementService wIFFinanceDisbursementService) {
		this.wIFFinanceDisbursementService = wIFFinanceDisbursementService;
	}

	public WIFFinanceDisbursementService getWIFFinanceDisbursementService() {
		return this.wIFFinanceDisbursementService;
	}

	public JdbcSearchObject<FinanceDisbursement> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceDisbursement> searchObj) {
		this.searchObj = searchObj;
	}
}