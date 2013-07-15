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
 * FileName    		:  ScoringGroupListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-12-2011    														*
 *                                                                  						*
 * Modified Date    :  05-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.scoringgroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.service.rmtmasters.ScoringGroupService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.rmtmasters.scoringgroup.model.ScoringGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RulesFactory/ScoringGroup/ScoringGroupList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ScoringGroupListCtrl extends GFCBaseListCtrl<ScoringGroup> implements Serializable {

	private static final long serialVersionUID = -2983341258706724321L;
	private final static Logger logger = Logger.getLogger(ScoringGroupListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window           window_ScoringGroupList;                // autoWired
	protected Borderlayout     borderLayout_ScoringGroupList;          // autoWired
	protected Paging           pagingScoringGroupList;                 // autoWired
	protected Listbox          listBoxScoringGroup;                    // autoWired

	// List headers
	protected Listheader       listheader_ScoreGroupCode;              // autoWired
	protected Listheader       listheader_ScoreGroupName;              // autoWired
	protected Listheader       listheader_CategoryType;                // autoWired
	protected Listheader       listheader_MinScore;                    // autoWired
	protected Listheader       listheader_Isoverride;                  // autoWired
	protected Listheader       listheader_OverrideScore;               // autoWired
	protected Listheader       listheader_RecordStatus;                // autoWired
	protected Listheader       listheader_RecordType;                  // autoWired

	// checkRights
	protected Button btnHelp;                                          // autoWired
	protected Button button_ScoringGroupList_NewScoringGroup;          // autoWired
	protected Button button_ScoringGroupList_ScoringGroupSearchDialog; // autoWired
	protected Button button_ScoringGroupList_PrintList;                // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ScoringGroup> searchObj;
	private transient ScoringGroupService scoringGroupService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public ScoringGroupListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ScoringGroup object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScoringGroupList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("ScoringGroup");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ScoringGroup");

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

		this.borderLayout_ScoringGroupList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingScoringGroupList.setPageSize(getListRows());
		this.pagingScoringGroupList.setDetailed(true);

		this.listheader_ScoreGroupCode.setSortAscending(new FieldComparator("scoreGroupCode", true));
		this.listheader_ScoreGroupCode.setSortDescending(new FieldComparator("scoreGroupCode", false));
		this.listheader_ScoreGroupName.setSortAscending(new FieldComparator("scoreGroupName", true));
		this.listheader_ScoreGroupName.setSortDescending(new FieldComparator("scoreGroupName", false));
		this.listheader_CategoryType.setSortAscending(new FieldComparator("CategoryType", true));
		this.listheader_CategoryType.setSortDescending(new FieldComparator("CategoryType", false));
		this.listheader_MinScore.setSortAscending(new FieldComparator("minScore", true));
		this.listheader_MinScore.setSortDescending(new FieldComparator("minScore", false));
		this.listheader_Isoverride.setSortAscending(new FieldComparator("isoverride", true));
		this.listheader_Isoverride.setSortDescending(new FieldComparator("isoverride", false));
		this.listheader_OverrideScore.setSortAscending(new FieldComparator("overrideScore", true));
		this.listheader_OverrideScore.setSortDescending(new FieldComparator("overrideScore", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<ScoringGroup>(ScoringGroup.class,getListRows());
		this.searchObj.addSort("ScoreGroupId", false);
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTScoringGroup_View");
			if (isFirstTask()) {
				button_ScoringGroupList_NewScoringGroup.setVisible(true);
			} else {
				button_ScoringGroupList_NewScoringGroup.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTScoringGroup_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_ScoringGroupList_NewScoringGroup.setVisible(false);
			this.button_ScoringGroupList_ScoringGroupSearchDialog.setVisible(false);
			this.button_ScoringGroupList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxScoringGroup,this.pagingScoringGroupList);
			// set the itemRenderer
			this.listBoxScoringGroup.setItemRenderer(new ScoringGroupListModelItemRenderer());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("ScoringGroupList");

		this.button_ScoringGroupList_NewScoringGroup.setVisible(getUserWorkspace().
				isAllowed("button_ScoringGroupList_NewScoringGroup"));
		this.button_ScoringGroupList_ScoringGroupSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_ScoringGroupList_ScoringGroupFindDialog"));
		this.button_ScoringGroupList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_ScoringGroupList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.scoringgroup.model.ScoringGroupListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onScoringGroupItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the selected ScoringGroup object
		final Listitem item = this.listBoxScoringGroup.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ScoringGroup aScoringGroup = (ScoringGroup) item.getAttribute("data");
			final ScoringGroup scoringGroup = getScoringGroupService().getScoringGroupById(aScoringGroup.getId());

			if(scoringGroup==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aScoringGroup.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_ScoreGroupId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND ScoreGroupId="+ scoringGroup.getScoreGroupId()+
											" AND version=" + scoringGroup.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "ScoringGroup", 
							whereCond, scoringGroup.getTaskId(), scoringGroup.getNextTaskId());
					if (userAcces){
						showDetailView(scoringGroup);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(scoringGroup);
				}
			}	
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Call the ScoringGroup dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ScoringGroupList_NewScoringGroup(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// create a new ScoringGroup object, We GET it from the back end.
		final ScoringGroup aScoringGroup = getScoringGroupService().getNewScoringGroup();
		if(event.getData()!=null){
			ScoringGroup newScoringGroup=(ScoringGroup)event.getData();
			setObjectData(aScoringGroup,newScoringGroup);
		}
		showDetailView(aScoringGroup);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for set the list into new Object in the process of COPY
	 * @param destScoGroup
	 * @param sourceScoGroup
	 * @return
	 */
	private ScoringGroup setObjectData(ScoringGroup destScoGroup,ScoringGroup sourceScoGroup){
		logger.debug("Entering");

		destScoGroup.setScoreGroupCode("");
		destScoGroup.setScoreGroupName("");
		destScoGroup.setCategoryType(PennantConstants.List_Select);
		destScoGroup.setMinScore(sourceScoGroup.getMinScore());
		destScoGroup.setIsOverride(sourceScoGroup.isIsOverride());
		destScoGroup.setOverrideScore(sourceScoGroup.getOverrideScore());

		List<ScoringSlab> listScoringSlab = new ArrayList<ScoringSlab>();
		for (int i = 0; i < sourceScoGroup.getScoringSlabList().size(); i++) {

			ScoringSlab scoringSlab = new ScoringSlab();
			ScoringSlab scoSlab = sourceScoGroup.getScoringSlabList().get(i);
			scoringSlab.setScoringSlab(scoSlab.getScoringSlab());
			scoringSlab.setCreditWorthness(scoSlab.getCreditWorthness());
			scoringSlab.setRecordType(PennantConstants.RCD_ADD);
			listScoringSlab.add(scoringSlab);
		}
		destScoGroup.setScoringSlabList(listScoringSlab);	

		List<ScoringMetrics> listScoringMetricsSlab = new ArrayList<ScoringMetrics>();
		for (int i = 0; i < sourceScoGroup.getScoringMetricsList().size(); i++) {

			ScoringMetrics scoringMetrics = new ScoringMetrics();
			ScoringMetrics scoMetrics = sourceScoGroup.getScoringMetricsList().get(i);
			scoringMetrics.setScoringId(scoMetrics.getScoringId());
			scoringMetrics.setLovDescScoringCode(scoMetrics.getLovDescScoringCode());
			scoringMetrics.setLovDescScoringCodeDesc(scoMetrics.getLovDescScoringCodeDesc());
			scoringMetrics.setLovDescMetricMaxPoints(scoMetrics.getLovDescMetricMaxPoints());
			scoringMetrics.setLovDescMetricTotPerc(scoMetrics.getLovDescMetricTotPerc());
			scoringMetrics.setRecordType(PennantConstants.RCD_ADD);
			listScoringMetricsSlab.add(scoringMetrics);
		}
		destScoGroup.setScoringMetricsList(listScoringMetricsSlab);
		logger.debug("Leaving");
		return destScoGroup;
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param ScoringGroup (aScoringGroup)
	 * @throws Exception
	 */
	private void showDetailView(ScoringGroup aScoringGroup) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aScoringGroup.getWorkflowId()==0 && isWorkFlowEnabled()){
			aScoringGroup.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("scoringGroup", aScoringGroup);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the ScoringGroupListbox from the
		 * dialog when we do a delete, edit or insert a ScoringGroup.
		 */
		map.put("scoringGroupListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RulesFactory/ScoringGroup/ScoringGroupDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_ScoringGroupList);
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
		this.pagingScoringGroupList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ScoringGroupList, event);
		this.window_ScoringGroupList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the ScoringGroup dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ScoringGroupList_ScoringGroupSearchDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		/*
		 * we can call our ScoringGroupDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected ScoringGroup. For handed over
		 * these parameter only a Map is accepted. So we put the ScoringGroup object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("scoringGroupCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RulesFactory/ScoringGroup/ScoringGroupSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the scoringGroup print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ScoringGroupList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTReportUtils.getReport("ScoringGroup", getSearchObj());
		logger.debug("Leaving " + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setScoringGroupService(ScoringGroupService scoringGroupService) {
		this.scoringGroupService = scoringGroupService;
	}
	public ScoringGroupService getScoringGroupService() {
		return this.scoringGroupService;
	}

	public JdbcSearchObject<ScoringGroup> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ScoringGroup> searchObj) {
		this.searchObj = searchObj;
	}
}