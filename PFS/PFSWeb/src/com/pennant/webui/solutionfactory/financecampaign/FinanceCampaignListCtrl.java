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
 * FileName    		:  FinanceCampaignListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-12-2011    														*
 *                                                                  						*
 * Modified Date    :  30-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.solutionfactory.financecampaign;


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
import com.pennant.backend.model.solutionfactory.FinanceCampaign;
import com.pennant.backend.service.solutionfactory.FinanceCampaignService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.solutionfactory.financecampaign.model.FinanceCampaignListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/FinanceCampaign/FinanceCampaignList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinanceCampaignListCtrl extends GFCBaseListCtrl<FinanceCampaign> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinanceCampaignListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceCampaignList; // autowired
	protected Borderlayout borderLayout_FinanceCampaignList; // autowired
	protected Paging pagingFinanceCampaignList; // autowired
	protected Listbox listBoxFinanceCampaign; // autowired

	// List headers
	protected Listheader listheader_FCCode; // autowired
	protected Listheader listheader_FCDesc; // autowired
	protected Listheader listheader_FCFinType; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_FinanceCampaignList_NewFinanceCampaign; // autowired
	protected Button button_FinanceCampaignList_FinanceCampaignSearchDialog; // autowired
	protected Button button_FinanceCampaignList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceCampaign> searchObj;
	
	private transient FinanceCampaignService financeCampaignService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public FinanceCampaignListCtrl() {
		super();
	}

	public void onCreate$window_FinanceCampaignList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinanceCampaign");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceCampaign");
			
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
		
		this.borderLayout_FinanceCampaignList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingFinanceCampaignList.setPageSize(getListRows());
		this.pagingFinanceCampaignList.setDetailed(true);

		this.listheader_FCCode.setSortAscending(new FieldComparator("fCCode", true));
		this.listheader_FCCode.setSortDescending(new FieldComparator("fCCode", false));
		this.listheader_FCDesc.setSortAscending(new FieldComparator("fCDesc", true));
		this.listheader_FCDesc.setSortDescending(new FieldComparator("fCDesc", false));
		this.listheader_FCFinType.setSortAscending(new FieldComparator("fCFinType", true));
		this.listheader_FCFinType.setSortDescending(new FieldComparator("fCFinType", false));
		
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
		this.searchObj = new JdbcSearchObject<FinanceCampaign>(FinanceCampaign.class,getListRows());
		this.searchObj.addSort("FCCode", false);
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("FinanceCampaign_View");
			if (isFirstTask()) {
				button_FinanceCampaignList_NewFinanceCampaign.setVisible(true);
			} else {
				button_FinanceCampaignList_NewFinanceCampaign.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("FinanceCampaign_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_FinanceCampaignList_NewFinanceCampaign.setVisible(false);
			this.button_FinanceCampaignList_FinanceCampaignSearchDialog.setVisible(false);
			this.button_FinanceCampaignList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxFinanceCampaign,this.pagingFinanceCampaignList);
			// set the itemRenderer
			this.listBoxFinanceCampaign.setItemRenderer(new FinanceCampaignListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceCampaignList");
		
		this.button_FinanceCampaignList_NewFinanceCampaign.setVisible(getUserWorkspace().isAllowed("button_FinanceCampaignList_NewFinanceCampaign"));
		this.button_FinanceCampaignList_FinanceCampaignSearchDialog.setVisible(getUserWorkspace().isAllowed("button_FinanceCampaignList_FinanceCampaignFindDialog"));
		this.button_FinanceCampaignList_PrintList.setVisible(getUserWorkspace().isAllowed("button_FinanceCampaignList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.solutionfactory.financecampaign.model.FinanceCampaignListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onFinanceCampaignItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected FinanceCampaign object
		final Listitem item = this.listBoxFinanceCampaign.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceCampaign aFinanceCampaign = (FinanceCampaign) item.getAttribute("data");
			final FinanceCampaign financeCampaign = getFinanceCampaignService().getFinanceCampaignById(aFinanceCampaign.getId());
			
			if(financeCampaign==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceCampaign.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FCCode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FCCode='"+ financeCampaign.getFCCode()+"' AND version=" + financeCampaign.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "FinanceCampaign", whereCond, financeCampaign.getTaskId(), financeCampaign.getNextTaskId());
					if (userAcces){
						showDetailView(financeCampaign);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(financeCampaign);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the FinanceCampaign dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinanceCampaignList_NewFinanceCampaign(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new FinanceCampaign object, We GET it from the backend.
		final FinanceCampaign aFinanceCampaign = getFinanceCampaignService().getNewFinanceCampaign();
		showDetailView(aFinanceCampaign);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param FinanceCampaign (aFinanceCampaign)
	 * @throws Exception
	 */
	private void showDetailView(FinanceCampaign aFinanceCampaign) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aFinanceCampaign.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceCampaign.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeCampaign", aFinanceCampaign);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the FinanceCampaignListbox from the
		 * dialog when we do a delete, edit or insert a FinanceCampaign.
		 */
		map.put("financeCampaignListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceCampaign/FinanceCampaignDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_FinanceCampaignList);
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
		this.pagingFinanceCampaignList.setActivePage(0);
		Events.postEvent("onCreate", this.window_FinanceCampaignList, event);
		this.window_FinanceCampaignList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the FinanceCampaign dialog
	 */
	
	public void onClick$button_FinanceCampaignList_FinanceCampaignSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our FinanceCampaignDialog zul-file with parameters. So we can
		 * call them with a object of the selected FinanceCampaign. For handed over
		 * these parameter only a Map is accepted. So we put the FinanceCampaign object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeCampaignCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceCampaign/FinanceCampaignSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the financeCampaign print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FinanceCampaignList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("FinanceCampaign", getSearchObj());
		logger.debug("Leaving");
	}

	public void setFinanceCampaignService(FinanceCampaignService financeCampaignService) {
		this.financeCampaignService = financeCampaignService;
	}

	public FinanceCampaignService getFinanceCampaignService() {
		return this.financeCampaignService;
	}

	public JdbcSearchObject<FinanceCampaign> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceCampaign> searchObj) {
		this.searchObj = searchObj;
	}
}