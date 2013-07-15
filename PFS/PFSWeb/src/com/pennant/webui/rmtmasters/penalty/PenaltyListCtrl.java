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
 * FileName    		:  PenaltyListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.penalty;

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
import com.pennant.backend.model.rmtmasters.Penalty;
import com.pennant.backend.service.rmtmasters.PenaltyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.rmtmasters.penalty.model.PenaltyListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/RMTMasters/Penalty/PenaltyList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class PenaltyListCtrl extends GFCBaseListCtrl<Penalty> implements Serializable {

	private static final long serialVersionUID = 2389453417566334108L;
	private final static Logger logger = Logger.getLogger(PenaltyListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_PenaltyList; 		// autowired
	protected Borderlayout 	borderLayout_PenaltyList; 	// autowired
	protected Paging 		pagingPenaltyList; 			// autowired
	protected Listbox 		listBoxPenalty; 			// autowired

	// List headers
	protected Listheader listheader_PenaltyType; 		// autowired
	protected Listheader listheader_PenaltyEffDate; 	// autowired
	protected Listheader listheader_ODueGraceDays; 		// autowired
	protected Listheader listheader_PenaltyIsActive; 	// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 								// autowired
	protected Button button_PenaltyList_NewPenalty; 		// autowired
	protected Button button_PenaltyList_PenaltySearchDialog;// autowired
	protected Button button_PenaltyList_PrintList; 			// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Penalty> searchObj;

	private transient PenaltyService penaltyService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public PenaltyListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Penalty object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PenaltyList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Penalty");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Penalty");

			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_PenaltyList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingPenaltyList.setPageSize(getListRows());
		this.pagingPenaltyList.setDetailed(true);

		//Apply sorting for getting List in the ListBox 
		this.listheader_PenaltyType.setSortAscending(
				new FieldComparator("penaltyType", true));
		this.listheader_PenaltyType.setSortDescending(
				new FieldComparator("penaltyType", false));
		this.listheader_PenaltyEffDate.setSortAscending(
				new FieldComparator("penaltyEffDate", true));
		this.listheader_PenaltyEffDate.setSortDescending(
				new FieldComparator("penaltyEffDate", false));
		this.listheader_ODueGraceDays.setSortAscending(
				new FieldComparator("oDueGraceDays", true));
		this.listheader_ODueGraceDays.setSortDescending(
				new FieldComparator("oDueGraceDays", false));
		this.listheader_PenaltyIsActive.setSortAscending(
				new FieldComparator("penaltyIsActive", true));
		this.listheader_PenaltyIsActive.setSortDescending(
				new FieldComparator("penaltyIsActive", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(
					new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(
					new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(
					new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(
					new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<Penalty>(Penalty.class,getListRows());
		this.searchObj.addSort("PenaltyType", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTPenalties_View");
			if (isFirstTask()) {
				button_PenaltyList_NewPenalty.setVisible(true);
			} else {
				button_PenaltyList_NewPenalty.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTPenalties_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_PenaltyList_NewPenalty.setVisible(false);
			this.button_PenaltyList_PenaltySearchDialog.setVisible(false);
			this.button_PenaltyList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(
					PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxPenalty,this.pagingPenaltyList);
			// set the itemRenderer
			this.listBoxPenalty.setItemRenderer(new PenaltyListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("PenaltyList");

		this.button_PenaltyList_NewPenalty.setVisible(getUserWorkspace()
				.isAllowed("button_PenaltyList_NewPenalty"));
		this.button_PenaltyList_PenaltySearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_PenaltyList_PenaltyFindDialog"));
		this.button_PenaltyList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_PenaltyList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see:
	 * com.pennant.webui.rmtmasters.penalty.model.PenaltyListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onPenaltyItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Penalty object
		final Listitem item = this.listBoxPenalty.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Penalty aPenalty = (Penalty) item.getAttribute("data");
			final Penalty penalty = getPenaltyService().getPenaltyById(aPenalty.getId());
			if(penalty==null){

				String[] valueParm = new String[2];
				String[] errParm= new String[2];

				valueParm[0] = aPenalty.getPenaltyType();
				valueParm[1] = aPenalty.getPenaltyEffDate().toString();

				errParm[0] = PennantJavaUtil.getLabel("label_PenaltyType") + ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_PenaltyEffDate") + ":"+valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND PenaltyType='"+ penalty.getPenaltyType()+
				"' AND version=" + penalty.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "Penalty",
							whereCond, penalty.getTaskId(), penalty.getNextTaskId());
					if (userAcces){
						showDetailView(penalty);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(penalty);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Penalty dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_PenaltyList_NewPenalty(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new Penalty object, We GET it from the backEnd.
		final Penalty aPenalty = getPenaltyService().getNewPenalty();
		showDetailView(aPenalty);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param Penalty (aPenalty)
	 * @throws Exception
	 */
	private void showDetailView(Penalty aPenalty) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aPenalty.getWorkflowId()==0 && isWorkFlowEnabled()){
			aPenalty.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("penalty", aPenalty);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the PenaltyListbox from the
		 * dialog when we do a delete, edit or insert a Penalty.
		 */
		map.put("penaltyListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/Penalty/PenaltyDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_PenaltyList);
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
		this.pagingPenaltyList.setActivePage(0);
		Events.postEvent("onCreate", this.window_PenaltyList, event);
		this.window_PenaltyList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Penalty dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_PenaltyList_PenaltySearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our PenaltyDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Penalty. For handed over
		 * these parameter only a Map is accepted. So we put the Penalty object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("penaltyCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RMTMasters/Penalty/PenaltySearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the penalty print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_PenaltyList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("Penalty", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setPenaltyService(PenaltyService penaltyService) {
		this.penaltyService = penaltyService;
	}
	public PenaltyService getPenaltyService() {
		return this.penaltyService;
	}

	public JdbcSearchObject<Penalty> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Penalty> searchObj) {
		this.searchObj = searchObj;
	}

}