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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.service.rmtmasters.ScoringGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.rmtmasters.scoringgroup.model.ScoringGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/RulesFactory/ScoringGroup/ScoringGroupList.zul file.
 */
public class ScoringGroupListCtrl extends GFCBaseListCtrl<ScoringGroup> {
	private static final long serialVersionUID = -2983341258706724321L;
	private static final Logger logger = Logger.getLogger(ScoringGroupListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ScoringGroupList;
	protected Borderlayout borderLayout_ScoringGroupList;
	protected Paging pagingScoringGroupList;
	protected Listbox listBoxScoringGroup;

	protected Listheader listheader_ScoreGroupCode;
	protected Listheader listheader_ScoreGroupName;
	protected Listheader listheader_CategoryType;
	protected Listheader listheader_MinScore;
	protected Listheader listheader_Isoverride;
	protected Listheader listheader_OverrideScore;

	protected Button button_ScoringGroupList_NewScoringGroup;
	protected Button button_ScoringGroupList_ScoringGroupSearchDialog;

	protected Longbox scoreGroupId;
	protected Textbox scoreGroupCode;
	protected Textbox scoreGroupName;
	protected Intbox minScore;
	protected Checkbox isoverride;
	protected Intbox overrideScore;
	protected Combobox categoryType;

	protected Listbox sortOperator_scoreGroupId;
	protected Listbox sortOperator_scoreGroupCode;
	protected Listbox sortOperator_scoreGroupName;
	protected Listbox sortOperator_minScore;
	protected Listbox sortOperator_isoverride;
	protected Listbox sortOperator_overrideScore;
	protected Listbox sortOperator_CategoryType;

	private transient ScoringGroupService scoringGroupService;

	/**
	 * default constructor.<br>
	 */
	public ScoringGroupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ScoringGroup";
		super.pageRightName = "ScoringGroupList";
		super.tableName = "RMTScoringGroup_AView";
		super.queueTableName = "RMTScoringGroup_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ScoringGroupList(Event event) {
		// Set the page level components.
		setPageComponents(window_ScoringGroupList, borderLayout_ScoringGroupList, listBoxScoringGroup,
				pagingScoringGroupList);
		setItemRender(new ScoringGroupListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ScoringGroupList_NewScoringGroup, "button_ScoringGroupList_NewScoringGroup", true);
		registerButton(button_ScoringGroupList_ScoringGroupSearchDialog);

		fillComboBox(this.categoryType, "", PennantAppUtil.getcustCtgCodeList(), "");

		registerField("ScoreGroupId", scoreGroupId, SortOrder.ASC, sortOperator_scoreGroupId, Operators.NUMERIC);
		registerField("scoreGroupCode", listheader_ScoreGroupCode, SortOrder.NONE, scoreGroupCode,
				sortOperator_scoreGroupCode, Operators.STRING);
		registerField("scoreGroupName", listheader_ScoreGroupName, SortOrder.NONE, scoreGroupName,
				sortOperator_scoreGroupName, Operators.STRING);
		registerField("CategoryType", listheader_CategoryType, SortOrder.NONE, categoryType, sortOperator_CategoryType,
				Operators.STRING);
		registerField("minScore", listheader_MinScore, SortOrder.NONE, minScore, sortOperator_minScore,
				Operators.NUMERIC);
		registerField("isOverride", listheader_Isoverride, SortOrder.NONE, isoverride, sortOperator_isoverride,
				Operators.BOOLEAN);
		registerField("overrideScore", listheader_OverrideScore, SortOrder.NONE, overrideScore,
				sortOperator_overrideScore, Operators.NUMERIC);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ScoringGroupList_ScoringGroupSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ScoringGroupList_NewScoringGroup(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ScoringGroup scoringGroup = new ScoringGroup();
		scoringGroup.setNewRecord(true);
		scoringGroup.setWorkflowId(getWorkFlowId());

		if (event.getData() != null) {
			ScoringGroup newScoringGroup = (ScoringGroup) event.getData();
			setObjectData(scoringGroup, newScoringGroup);
		}

		// Display the dialog page.
		doShowDialogPage(scoringGroup);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onScoringGroupItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxScoringGroup.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		ScoringGroup scoringGroup = scoringGroupService.getScoringGroupById(id);

		if (scoringGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ScoreGroupId=" + scoringGroup.getScoreGroupId() + " AND version="
				+ scoringGroup.getVersion() + " ";

		if (doCheckAuthority(scoringGroup, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && scoringGroup.getWorkflowId() == 0) {
				scoringGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(scoringGroup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Method for set the list into new Object in the process of COPY
	 * 
	 * @param destScoGroup
	 * @param sourceScoGroup
	 * @return
	 */
	private ScoringGroup setObjectData(ScoringGroup destScoGroup, ScoringGroup sourceScoGroup) {
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
		if (sourceScoGroup.getScoringMetricsList() != null) {
			for (int i = 0; i < sourceScoGroup.getScoringMetricsList().size(); i++) {

				ScoringMetrics scoringMetrics = new ScoringMetrics();
				ScoringMetrics scoMetrics = sourceScoGroup.getScoringMetricsList().get(i);
				scoringMetrics.setScoringId(scoMetrics.getScoringId());
				scoringMetrics.setLovDescScoringCode(scoMetrics.getLovDescScoringCode());
				scoringMetrics.setLovDescScoringCodeDesc(scoMetrics.getLovDescScoringCodeDesc());
				scoringMetrics.setLovDescMetricMaxPoints(scoMetrics.getLovDescMetricMaxPoints());
				scoringMetrics.setLovDescMetricTotPerc(scoMetrics.getLovDescMetricTotPerc());
				scoringMetrics.setLovDescSQLRule(scoMetrics.getLovDescSQLRule());
				scoringMetrics.setRecordType(PennantConstants.RCD_ADD);
				listScoringMetricsSlab.add(scoringMetrics);
			}
		}
		destScoGroup.setScoringMetricsList(listScoringMetricsSlab);
		logger.debug("Leaving");
		return destScoGroup;
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aScoringGroup
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ScoringGroup aScoringGroup) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("scoringGroup", aScoringGroup);
		arg.put("scoringGroupListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/ScoringGroup/ScoringGroupDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setScoringGroupService(ScoringGroupService scoringGroupService) {
		this.scoringGroupService = scoringGroupService;
	}
}