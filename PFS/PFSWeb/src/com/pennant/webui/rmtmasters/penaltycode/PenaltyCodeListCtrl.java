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
 * FileName    		:  PenaltyCodeListCtrl.java                                                   * 	  
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

package com.pennant.webui.rmtmasters.penaltycode;

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
import com.pennant.backend.model.rmtmasters.PenaltyCode;
import com.pennant.backend.service.rmtmasters.PenaltyCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.rmtmasters.penaltycode.model.PenaltyCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/PenaltyCode/PenaltyCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class PenaltyCodeListCtrl extends GFCBaseListCtrl<PenaltyCode> implements Serializable {

	private static final long serialVersionUID = -3952096795343865287L;
	private final static Logger logger = Logger.getLogger(PenaltyCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_PenaltyCodeList; 		// autowired
	protected Borderlayout 	borderLayout_PenaltyCodeList; 	// autowired
	protected Paging 		pagingPenaltyCodeList; 			// autowired
	protected Listbox 		listBoxPenaltyCode; 			// autowired

	// List headers
	protected Listheader listheader_PenaltyType; 		// autowired
	protected Listheader listheader_PenaltyDesc; 		// autowired
	protected Listheader listheader_PenaltyIsActive; 	// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 											// autowired
	protected Button button_PenaltyCodeList_NewPenaltyCode; 			// autowired
	protected Button button_PenaltyCodeList_PenaltyCodeSearchDialog; 	// autowired
	protected Button button_PenaltyCodeList_PrintList; 					// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<PenaltyCode> searchObj;

	private transient PenaltyCodeService penaltyCodeService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public PenaltyCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected PenaltyCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PenaltyCodeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("PenaltyCode");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("PenaltyCode");

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
		this.borderLayout_PenaltyCodeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingPenaltyCodeList.setPageSize(getListRows());
		this.pagingPenaltyCodeList.setDetailed(true);

		//Apply sorting for getting List in the ListBox 
		this.listheader_PenaltyType.setSortAscending(
				new FieldComparator("penaltyType", true));
		this.listheader_PenaltyType.setSortDescending(
				new FieldComparator("penaltyType", false));
		this.listheader_PenaltyDesc.setSortAscending(
				new FieldComparator("penaltyDesc", true));
		this.listheader_PenaltyDesc.setSortDescending(
				new FieldComparator("penaltyDesc", false));
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
		this.searchObj = new JdbcSearchObject<PenaltyCode>(PenaltyCode.class,getListRows());
		this.searchObj.addSort("PenaltyType", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTPenaltyCodes_View");
			if (isFirstTask()) {
				button_PenaltyCodeList_NewPenaltyCode.setVisible(true);
			} else {
				button_PenaltyCodeList_NewPenaltyCode.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTPenaltyCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_PenaltyCodeList_NewPenaltyCode.setVisible(false);
			this.button_PenaltyCodeList_PenaltyCodeSearchDialog.setVisible(false);
			this.button_PenaltyCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(
					PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxPenaltyCode,this.pagingPenaltyCodeList);
			// set the itemRenderer
			this.listBoxPenaltyCode.setItemRenderer(
					new PenaltyCodeListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("PenaltyCodeList");

		this.button_PenaltyCodeList_NewPenaltyCode.setVisible(getUserWorkspace()
				.isAllowed("button_PenaltyCodeList_NewPenaltyCode"));
		this.button_PenaltyCodeList_PenaltyCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_PenaltyCodeList_PenaltyCodeFindDialog"));
		this.button_PenaltyCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_PenaltyCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.penaltycode.model.
	 * PenaltyCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onPenaltyCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected PenaltyCode object
		final Listitem item = this.listBoxPenaltyCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final PenaltyCode aPenaltyCode = (PenaltyCode) item.getAttribute("data");
			final PenaltyCode penaltyCode = getPenaltyCodeService()
			.getPenaltyCodeById(aPenaltyCode.getId());
			if(penaltyCode==null){

				String[] valueParm = new String[1];
				String[] errParm= new String[1];

				valueParm[0] = aPenaltyCode.getPenaltyType();
				errParm[0] = PennantJavaUtil.getLabel("label_PenaltyType") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND PenaltyType='"+ penaltyCode.getPenaltyType()+
				"' AND version=" + penaltyCode.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "PenaltyCode",
							whereCond, penaltyCode.getTaskId(), penaltyCode.getNextTaskId());
					if (userAcces){
						showDetailView(penaltyCode);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(penaltyCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the PenaltyCode dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_PenaltyCodeList_NewPenaltyCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new PenaltyCode object, We GET it from the backEnd.
		final PenaltyCode aPenaltyCode = getPenaltyCodeService().getNewPenaltyCode();
		showDetailView(aPenaltyCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param PenaltyCode (aPenaltyCode)
	 * @throws Exception
	 */
	private void showDetailView(PenaltyCode aPenaltyCode) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aPenaltyCode.getWorkflowId()==0 && isWorkFlowEnabled()){
			aPenaltyCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("penaltyCode", aPenaltyCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the PenaltyCodeListbox from the
		 * dialog when we do a delete, edit or insert a PenaltyCode.
		 */
		map.put("penaltyCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/PenaltyCode/PenaltyCodeDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_PenaltyCodeList);
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
		this.pagingPenaltyCodeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_PenaltyCodeList, event);
		this.window_PenaltyCodeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the PenaltyCode dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_PenaltyCodeList_PenaltyCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our PenaltyCodeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected PenaltyCode. For handed over
		 * these parameter only a Map is accepted. So we put the PenaltyCode object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("penaltyCodeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/PenaltyCode/PenaltyCodeSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the penaltyCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_PenaltyCodeList_PrintList(Event event)
			throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("PenaltyCode", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setPenaltyCodeService(PenaltyCodeService penaltyCodeService) {
		this.penaltyCodeService = penaltyCodeService;
	}
	public PenaltyCodeService getPenaltyCodeService() {
		return this.penaltyCodeService;
	}

	public JdbcSearchObject<PenaltyCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<PenaltyCode> searchObj) {
		this.searchObj = searchObj;
	}

}