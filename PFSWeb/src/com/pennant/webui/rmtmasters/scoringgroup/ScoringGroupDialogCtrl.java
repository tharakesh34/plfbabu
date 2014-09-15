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
 * FileName    		:  ScoringGroupDialogCtrl.java                                                   * 	  
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.service.rmtmasters.ScoringGroupService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.rmtmasters.scoringslab.model.ScoringSlabListModelItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RulesFactory/ScoringGroup/scoringGroupDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ScoringGroupDialogCtrl extends GFCBaseListCtrl<ScoringGroup> implements Serializable {

	private static final long serialVersionUID = 6496923785082021678L;
	private final static Logger logger = Logger.getLogger(ScoringGroupDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window     window_ScoringGroupDialog;       // autoWired
	protected Textbox    scoreGroupCode;                  // autoWired
	protected Textbox    scoreGroupName;                  // autoWired
	protected Combobox   categoryType;                    // autoWired
	protected Intbox     minScore;                        // autoWired
	protected Checkbox   isoverride;                      // autoWired
	protected Intbox     overrideScore;                   // autoWired

	protected Listbox    listboxScoringSlab;              // autoWired
	protected Paging     pagingScorSlabDetailsList;       // autoWired
	
	protected Listbox    listBoxRetailScoringMetrics;     // autoWired
	protected Paging     pagingRetailScoringMetricsList;  // autoWired
	protected Listbox    listBoxFinScoringMetrics;        // autoWired
	protected Listbox    listBoxNFScoringMetrics;         // autoWired
	
	protected Grid 		 grid_Basicdetails;	    	      // autoWired

	protected Label      recordStatus;                    // autoWired
	protected Radiogroup userAction;
	protected Groupbox   groupboxWf;
	
	// not auto wired variables
	private ScoringGroup scoringGroup;                           // over handed per parameters
	private transient ScoringGroupListCtrl scoringGroupListCtrl; // over handed per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_scoreGroupCode;
	private transient String  		oldVar_scoreGroupName;
	private transient int  			oldVar_categoryType;
	private transient int  		    oldVar_minScore;
	private transient boolean  		oldVar_isoverride;
	private transient int  		    oldVar_overrideScore;
	private transient String        oldVar_recordStatus;
	private List<ScoringMetrics>    oldVar_scoringMetricList;
	private List<ScoringSlab>       oldVar_scoringSlabList;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ScoringGroupDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button     btnNew;       // autoWired
	protected Button     btnEdit;      // autoWired
	protected Button     btnDelete;    // autoWired
	protected Button     btnSave;      // autoWired
	protected Button     btnCancel;    // autoWired
	protected Button     btnClose;     // autoWired
	protected Button     btnHelp;      // autoWired
	protected Button     btnNotes;     // autoWired
	protected Button     btnCopyTo;    // autoWired

	protected Button 	btnNewScoringSlab;
	protected Button 	btnNewRetailScoringMetrics;
	protected Button 	btnNewFinScoringMetrics;
	protected Button 	btnNewNFScoringMetrics;
	
	protected Tab 		retailScoreMetricTab;
	protected Tab 		finScoreMetricTab;
	protected Tab 		nonFinScoreMetricTab;
	
	// ServiceDAOs / Domain Classes
	private transient ScoringGroupService scoringGroupService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private List<ScoringSlab>             scoringSlabList=new ArrayList<ScoringSlab>();
	private PagedListWrapper<ScoringSlab> scoringSlabPagedListWrapper;
	private List<ScoringMetrics>          scoringMetricList=new ArrayList<ScoringMetrics>();
	private PagedListWrapper<ScoringMetrics> scoringMetricsPagedListWrapper;
	
	private List<ScoringMetrics>          finScoringMetricList=new ArrayList<ScoringMetrics>();
	private List<ScoringMetrics>          nonFinScoringMetricList=new ArrayList<ScoringMetrics>();
	protected Map<Long,List<ScoringMetrics>> finScoreMap = new HashMap<Long, List<ScoringMetrics>>();
	
	int listRows;
	/**
	 * default constructor.<br>
	 */
	public ScoringGroupDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ScoringGroup object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScoringGroupDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		setScoringSlabPagedListWrapper();
		setScoringMetricsPagedListWrapper();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("scoringGroup")) {
			this.scoringGroup = (ScoringGroup) args.get("scoringGroup");
			ScoringGroup befImage =new ScoringGroup();
			BeanUtils.copyProperties(this.scoringGroup, befImage);
			this.scoringGroup.setBefImage(befImage);
			setScoringGroup(this.scoringGroup);
		} else {
			setScoringGroup(null);
		}
		
		//Set the Height of Lists according to Current desktop
		getBorderLayoutHeight();
		grid_Basicdetails.getRows().getVisibleItemCount();
		int dialogHeight =  grid_Basicdetails.getRows().getVisibleItemCount()* 20 +200; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listRows = Math.round(listboxHeight/ 24)-1;
		
		listboxScoringSlab.setHeight(listboxHeight+"px");
		pagingScorSlabDetailsList.setPageSize(listRows);
		
		listBoxRetailScoringMetrics.setHeight((listboxHeight-20)+"px");
		pagingRetailScoringMetricsList.setPageSize(listRows);
		
		listBoxFinScoringMetrics.setHeight((listboxHeight-10)+"px");
		listBoxNFScoringMetrics.setHeight((listboxHeight-10)+"px");

		doLoadWorkFlow(this.scoringGroup.isWorkflow(),this.scoringGroup.getWorkflowId()
				,this.scoringGroup.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "ScoringGroupDialog");
		}

		// READ OVERHANDED parameters !
		// we get the scoringGroupListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete scoringGroup here.
		if (args.containsKey("scoringGroupListCtrl")) {
			setScoringGroupListCtrl((ScoringGroupListCtrl) args.get("scoringGroupListCtrl"));
		} else {
			setScoringGroupListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getScoringGroup());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.scoreGroupCode.setMaxlength(8);
		this.scoreGroupName.setMaxlength(50);
		this.minScore.setMaxlength(4);
		this.overrideScore.setMaxlength(4);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering") ;

		getUserWorkspace().alocateAuthorities("ScoringGroupDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ScoringGroupDialog_btnNew"));
		this.btnNewRetailScoringMetrics.setVisible(getUserWorkspace()
				.isAllowed("button_ScoringGroupDialog_btnNewScoringMetrics"));
		this.btnNewFinScoringMetrics.setVisible(getUserWorkspace()
				.isAllowed("button_ScoringGroupDialog_btnNewScoringMetrics"));
		this.btnNewNFScoringMetrics.setVisible(getUserWorkspace()
				.isAllowed("button_ScoringGroupDialog_btnNewScoringMetrics"));
		this.btnNewScoringSlab.setVisible(getUserWorkspace()
				.isAllowed("button_ScoringGroupDialog_btnNewScoringSlab"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ScoringGroupDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ScoringGroupDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ScoringGroupDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnCopyTo.setVisible(getUserWorkspace().isAllowed("button_ScoringGroupDialog_btnCopyTo"));

		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_ScoringGroupDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {
			doClose(null);
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_ScoringGroupDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose(null);
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Creating Duplicate record
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnCopyTo(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		String message="Change done to the existing definition. Do you want to save before copy?";
		doClose(message);
		Events.postEvent("onClick$button_ScoringGroupList_NewScoringGroup"
				, scoringGroupListCtrl.window_ScoringGroupList,getScoringGroup());
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * When user clicks on "btnNewScoringSlab"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNewScoringSlab(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		/*
		 * we can call our ScoringGroupDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected ScoringGroup. For handed over
		 * these parameter only a Map is accepted. So we put the ScoringGroup object
		 * in a HashMap.
		 */
		ScoringSlab scroingSlab=new ScoringSlab();
		scroingSlab.setNewRecord(true);
		scroingSlab.setWorkflowId(0);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("scoringGroupDialogCtrl", this);
		map.put("roleCode", getRole());
		map.put("scroingSlab", scroingSlab);
		map.put("scoringGroup",getScoringGroup());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/ScoringSlab/ScoringSlabDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * When user double clicks "scorings Lab"
	 * @param event
	 * @throws Exception
	 */
	public void onScoringSlabItemDoubleClicked(Event event)throws Exception{
		logger.debug("Entering " + event.toString());
		
		final Listitem item=this.listboxScoringSlab.getSelectedItem();
		if(item!=null){	
			final ScoringSlab scroingSlab=(ScoringSlab)item.getAttribute("data");	
			scroingSlab.setNewRecord(false);
			if (scroingSlab.getRecordType() !=null && 
					(scroingSlab.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) 
							|| scroingSlab.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN))) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Not_Allowed_to_maintain"));
			}else{
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("scoringGroupDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("scroingSlab", scroingSlab);
				map.put("scoringGroup",getScoringGroup());
				
				try {
					Executions.createComponents("/WEB-INF/pages/RMTMasters" +
							"/ScoringSlab/ScoringSlabDialog.zul",null,map);

				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving " + event.toString());	
	}
	
	/**
	 * This method fills expense details list 
	 * @param expenseDetails
	 */
	@SuppressWarnings("unchecked")
	public void doFillScoringSlab(List<ScoringSlab> scoringSlabList){
		logger.debug("Entering ");
		this.setScoringSlabList(scoringSlabList);
		Comparator<Object> comp = new BeanComparator("scoringSlab");
		Collections.sort(scoringSlabList,comp);
		this.pagingScorSlabDetailsList.setDetailed(true);
		getScoringGroup().setScoringSlabList(scoringSlabList);
		getScoringSlabPagedListWrapper().initList(scoringSlabList
				, this.listboxScoringSlab, pagingScorSlabDetailsList);
		this.listboxScoringSlab.setItemRenderer(new ScoringSlabListModelItemRenderer());
		logger.debug("Leaving ");
	}
	
	/**
	 * When user clicks on "btnNewScoringMetrics"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNewRetailScoringMetrics(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		showDialogWindow(getScoringGroup().getScoringMetricsList(),"R");
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * When user clicks on "btnNewFinScoringMetrics"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNewFinScoringMetrics(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		showDialogWindow(getScoringGroup().getFinScoringMetricsList(),"F");
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * When user clicks on "btnNewNFScoringMetrics"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNewNFScoringMetrics(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		showDialogWindow(getScoringGroup().getNonFinScoringMetricsList(),"N");
		logger.debug("Leaving" + event.toString());
	}
	
	private void showDialogWindow(List<ScoringMetrics> scoringMetrics , String categoryValue) throws InterruptedException{
		/*
		 * we can call our ScoringGroupDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected ScoringGroup. For handed over
		 * these parameter only a Map is accepted. So we put the ScoringGroup object
		 * in a HashMap.
		 */
		ScoringMetrics scoreMetric = new ScoringMetrics();
		scoreMetric.setNewRecord(true);
		scoreMetric.setWorkflowId(0);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("scoringGroupDialogCtrl", this);
		map.put("roleCode", getRole());
		map.put("scoringMetrics", scoreMetric);
		map.put("scoringGroup",getScoringGroup());
		map.put("categoryValue",categoryValue);
		map.put("categoryType",this.categoryType.getSelectedItem().getValue().toString());
		map.put("originalScoringMetricsList",scoringMetrics);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/ScoringMetrics/ScoringMetricsDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}
	
	/**
	 * When user double "scoring metrics"
	 * @param event
	 * @throws Exception
	 */
	public void onScoringMetricsItemDoubleClicked(Event event)throws Exception{
		logger.debug("Entering " + event.toString());
		
		final Listitem item=this.listBoxRetailScoringMetrics.getSelectedItem();
		if(item!=null){	
			final ScoringMetrics scoringMetrics=(ScoringMetrics)item.getAttribute("data");	
			scoringMetrics.setNewRecord(false);
			if (scoringMetrics.getRecordType() !=null && 
					(scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| scoringMetrics.getRecordType()
							.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN))) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Not_Allowed_to_maintain"));
			}else{
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("scoringGroupDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("scoringMetrics", scoringMetrics);
				map.put("scoringGroup",getScoringGroup());
				map.put("categoryValue","R");
				map.put("originalScoringMetricsList",getScoringGroup().getScoringMetricsList());
				
				try {
					Executions.createComponents("/WEB-INF/pages/RMTMasters" +
							"/ScoringMetrics/ScoringMetricsDialog.zul",null,map);

				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving " + event.toString());	
	}
	
	/**
	 * When user double "scoring metrics"
	 * @param event
	 * @throws Exception
	 */
	public void onScoringMetricsFinGroupItemDoubleClicked(ForwardEvent event)throws Exception{
		logger.debug("Entering " + event.toString());
		
		if(event.getOrigin() != null && event.getOrigin().getTarget() != null){

			final Listgroup listgroup = (Listgroup) event.getOrigin().getTarget();

			if(listgroup!=null){	
				final ScoringMetrics scoringMetrics=(ScoringMetrics)listgroup.getAttribute("data");	
				scoringMetrics.setNewRecord(false);
				if (scoringMetrics.getRecordType() !=null && 
						(scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
								|| scoringMetrics.getRecordType()
								.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN))) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Not_Allowed_to_maintain"));
				}else{
					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("scoringGroupDialogCtrl", this);
					map.put("roleCode", getRole());
					map.put("scoringMetrics", scoringMetrics);
					map.put("scoringGroup",getScoringGroup());
					map.put("categoryValue","F");
					map.put("originalScoringMetricsList",getScoringGroup().getFinScoringMetricsList());

					try {
						Executions.createComponents("/WEB-INF/pages/RMTMasters" +
								"/ScoringMetrics/ScoringMetricsDialog.zul",null,map);

					} catch (final Exception e) {
						logger.error("onOpenWindow:: error opening window / " + e.getMessage());
						PTMessageUtils.showErrorMessage(e.toString());
					}
				}
			}
		}
		logger.debug("Leaving " + event.toString());	
	}
	
	/**
	 * When user double "scoring metrics"
	 * @param event
	 * @throws Exception
	 */
	public void onScoringMetricsNonFinGroupItemDoubleClicked(ForwardEvent event)throws Exception{
		logger.debug("Entering " + event.toString());
		
		if(event.getOrigin() != null && event.getOrigin().getTarget() != null){

			final Listgroup listgroup = (Listgroup) event.getOrigin().getTarget();

			if(listgroup!=null){	
				final ScoringMetrics scoringMetrics=(ScoringMetrics)listgroup.getAttribute("data");	
				scoringMetrics.setNewRecord(false);
				if (scoringMetrics.getRecordType() !=null && 
						(scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
								|| scoringMetrics.getRecordType()
								.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN))) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Not_Allowed_to_maintain"));
				}else{
					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("scoringGroupDialogCtrl", this);
					map.put("roleCode", getRole());
					map.put("scoringMetrics", scoringMetrics);
					map.put("scoringGroup",getScoringGroup());
					map.put("categoryValue","N");
					map.put("originalScoringMetricsList",getScoringGroup().getNonFinScoringMetricsList());

					try {
						Executions.createComponents("/WEB-INF/pages/RMTMasters" +
								"/ScoringMetrics/ScoringMetricsDialog.zul",null,map);

					} catch (final Exception e) {
						logger.error("onOpenWindow:: error opening window / " + e.getMessage());
						PTMessageUtils.showErrorMessage(e.toString());
					}
				}
			}
		}
		logger.debug("Leaving " + event.toString());	
	}
	
	/**
	 * This method fills expense details list 
	 * @param expenseDetails
	 */
	public void doFillScoringMetrics(List<ScoringMetrics> scoringMetricsList,String categoryValue){
		logger.debug("Entering ");
		
		if("R".equals(categoryValue)){
			
			this.listBoxRetailScoringMetrics.getItems().clear();
			this.pagingRetailScoringMetricsList.setDetailed(true);
			this.pagingRetailScoringMetricsList.setTotalSize(scoringMetricsList.size());
			this.setScoringMetricList(scoringMetricsList);
			getScoringGroup().setScoringMetricsList(scoringMetricsList);
			
			for (ScoringMetrics scoringMetric : scoringMetricsList) {
				addListItem(scoringMetric, true, BigDecimal.ONE , this.listBoxRetailScoringMetrics);
			}
			
		}else if("F".equals(categoryValue)){

			this.listBoxFinScoringMetrics.getItems().clear();
			this.setFinScoringMetricList(scoringMetricsList);
			getScoringGroup().setFinScoringMetricsList(scoringMetricsList);

			for (ScoringMetrics scoringMetric : scoringMetricsList) {
				addListGroup(scoringMetric, this.listBoxFinScoringMetrics, categoryValue);
				if(finScoreMap.containsKey(scoringMetric.getScoringId())){
					List<ScoringMetrics> subMetricList = finScoreMap.get(scoringMetric.getScoringId());
					for (ScoringMetrics subScoreMetric : subMetricList) {
						addListItem(subScoreMetric, false, scoringMetric.getLovDescMetricMaxPoints(),
								this.listBoxFinScoringMetrics);
					}
				}
				addListFooter(scoringMetric, this.listBoxFinScoringMetrics);
			}
		}else if("N".equals(categoryValue)){

			this.listBoxNFScoringMetrics.getItems().clear();
			this.setNonFinScoringMetricList(scoringMetricsList);
			getScoringGroup().setNonFinScoringMetricsList(scoringMetricsList);

			for (ScoringMetrics scoringMetric : scoringMetricsList) {
				addListGroup(scoringMetric, this.listBoxNFScoringMetrics, categoryValue);
				if(finScoreMap.containsKey(scoringMetric.getScoringId())){
					List<ScoringMetrics> subMetricList = finScoreMap.get(scoringMetric.getScoringId());
					for (ScoringMetrics subScoreMetric : subMetricList) {
						addListItem(subScoreMetric, false, scoringMetric.getLovDescMetricMaxPoints(),
								this.listBoxNFScoringMetrics);
					}
				}
				addListFooter(scoringMetric, this.listBoxNFScoringMetrics);
			}
		}
		logger.debug("Leaving ");
	}
	
	private void addListItem(ScoringMetrics scoringMetric, boolean addDoubleClickEvent, BigDecimal subGroupScore, Listbox listbox){
		logger.debug("Entering");
		
		Listitem item  = new Listitem();
		Listcell lc = new Listcell(scoringMetric.getLovDescScoringCode());
		lc.setParent(item);
		lc = new Listcell(scoringMetric.getLovDescScoringCodeDesc());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(scoringMetric.getLovDescMetricMaxPoints()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		String perc = scoringMetric.getLovDescMetricTotPerc();
		if(!addDoubleClickEvent){
			if(subGroupScore != null){
				perc = (scoringMetric.getLovDescMetricMaxPoints()
						.multiply(new BigDecimal(100))).divide(subGroupScore, 2, RoundingMode.HALF_UP).toString();
			}
		}
		lc = new Listcell(perc);
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(scoringMetric.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(scoringMetric.getRecordType()));
		lc.setParent(item);
		if(addDoubleClickEvent){
			item.setAttribute("data", scoringMetric);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onScoringMetricsItemDoubleClicked");
		}
		listbox.appendChild(item);
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Adding List Group to listBox in Corporation
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListGroup(ScoringMetrics scoringMetric, Listbox listbox, String categoryValue){
		logger.debug("Entering");
		
		Listgroup listgroup = new Listgroup(scoringMetric.getLovDescScoringCodeDesc());
		listgroup.setOpen(true);
		listgroup.setAttribute("data", scoringMetric);
		if("F".equals(categoryValue)){
			ComponentsCtrl.applyForward(listgroup, "onDoubleClick=onScoringMetricsFinGroupItemDoubleClicked");
		}else if("N".equals(categoryValue)){
			ComponentsCtrl.applyForward(listgroup, "onDoubleClick=onScoringMetricsNonFinGroupItemDoubleClicked");
		}
		
		listbox.appendChild(listgroup);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Adding List Group to listBox in Corporation
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListFooter(ScoringMetrics scoringMetric, Listbox listbox){
		logger.debug("Entering");
		
		Listgroupfoot listgroupfoot = new Listgroupfoot();
		
		Listcell cell = new Listcell("Sub Total");
		cell.setStyle("font-weight:bold;text-align:right;");
		cell.setSpan(2);
		listgroupfoot.appendChild(cell);
		
		cell = new Listcell(scoringMetric.getLovDescMetricMaxPoints() == null ? "" : String.valueOf(scoringMetric.getLovDescMetricMaxPoints()));
		cell.setStyle("font-weight:bold;text-align:right;");
		listgroupfoot.appendChild(cell);
		
		cell = new Listcell(String.valueOf(scoringMetric.getLovDescMetricTotPerc()));
		cell.setStyle("font-weight:bold;text-align:right;");
		listgroupfoot.appendChild(cell);
		
		cell = new Listcell(scoringMetric.getRecordStatus());
		listgroupfoot.appendChild(cell);
		
		cell = new Listcell(PennantJavaUtil.getLabel(scoringMetric.getRecordType()));
		listgroupfoot.appendChild(cell);
		
		listbox.appendChild(listgroupfoot);
		logger.debug("Leaving");
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ GUI PROCESS +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose(String msg) throws InterruptedException {
		logger.debug("Entering");
		
		boolean close=true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			if(msg == null){
				msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			}	
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
					| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_ScoringGroupDialog, "ScoringGroup");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aScoringGroup
	 *            ScoringGroup
	 */
	public void doWriteBeanToComponents(ScoringGroup aScoringGroup) {
		logger.debug("Entering") ;
		this.scoreGroupCode.setValue(aScoringGroup.getScoreGroupCode());
		this.scoreGroupName.setValue(aScoringGroup.getScoreGroupName());
		fillComboBox(this.categoryType, aScoringGroup.getCategoryType(), PennantStaticListUtil.getCategoryType(), "");
		
		if(!aScoringGroup.isNewRecord()){
			if("C".equals(aScoringGroup.getCategoryType()) || "B".equals(aScoringGroup.getCategoryType())){
				this.finScoreMetricTab.setVisible(true);
				this.nonFinScoreMetricTab.setVisible(true);
			}else{
				this.retailScoreMetricTab.setVisible(true);
			}
		}
		this.minScore.setValue(aScoringGroup.getMinScore());
		this.isoverride.setChecked(aScoringGroup.isIsOverride());
		this.overrideScore.setValue(aScoringGroup.getOverrideScore());
		this.recordStatus.setValue(aScoringGroup.getRecordStatus());
		checkoverride(aScoringGroup.isIsOverride());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aScoringGroup
	 */
	public void doWriteComponentsToBean(ScoringGroup aScoringGroup) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aScoringGroup.setScoreGroupCode(this.scoreGroupCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aScoringGroup.setScoreGroupName(this.scoreGroupName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aScoringGroup.setCategoryType(this.categoryType.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aScoringGroup.setMinScore(this.minScore.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aScoringGroup.setIsOverride(this.isoverride.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aScoringGroup.setOverrideScore(this.overrideScore.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		long totalScorePoints = 0;
		if("C".equals(this.categoryType.getSelectedItem().getValue().toString()) ||
				"B".equals(this.categoryType.getSelectedItem().getValue().toString())){
			totalScorePoints = getScoringGroup().getLovDescTotFinScorPoints() + getScoringGroup().getLovDescTotNFScorPoints();
		}else{
			totalScorePoints = getScoringGroup().getLovDescTotRetailScorPoints() ;
		}

		try {
			if(totalScorePoints<this.minScore.getValue()){
				throw new WrongValueException(this.minScore,Labels.getLabel("FIELD_NO_EMPTY_LESSTHAN"
						,new String[]{Labels.getLabel("label_ScoringGroupDialog_MinScore.value")
								,new String(Labels.getLabel("label_ScoringGroupDialog_MetPoints_Total.value")
										+"-"+String.valueOf(totalScorePoints))}));

			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.isoverride.isChecked()){
				if((this.minScore.getValue()<this.overrideScore.getValue())){
					throw new WrongValueException(this.overrideScore,Labels.getLabel("FIELD_NO_EMPTY_LESSTHAN"
							,new String[]{Labels.getLabel("label_ScoringGroupDialog_OverrideScore.value")
									,new String(Labels.getLabel("label_ScoringGroupDialog_MinScore.value")
											+"-"+this.minScore.getValue().toString())}));
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aScoringGroup.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aScoringGroup
	 * @throws InterruptedException
	 */
	public void doShowDialog(ScoringGroup aScoringGroup) throws InterruptedException {
		logger.debug("Entering") ;

		// if aScoringGroup == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aScoringGroup == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aScoringGroup = getScoringGroupService().getNewScoringGroup();

			setScoringGroup(aScoringGroup);
		} else {
			setScoringGroup(aScoringGroup);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aScoringGroup.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.scoreGroupName.focus();
		} else {
			this.scoreGroupName.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aScoringGroup);
			finScoreMap = aScoringGroup.getLovDescFinScoreMap();
			if(aScoringGroup.getScoringSlabList() != null){
				doFillScoringSlab(aScoringGroup.getScoringSlabList());
			}
			
			if(aScoringGroup.getScoringMetricsList() != null){
				
				//Calculation Of Max Metric Score & Percentage In Total Group Score
				doCalMetricPercentage(aScoringGroup.getScoringMetricsList(),"R");
				
				doFillScoringMetrics(aScoringGroup.getScoringMetricsList(),"R");
			}
			
			if(aScoringGroup.getFinScoringMetricsList() != null){
				
				//Calculation Of Max Metric Score & Percentage In Total Group Score
				doCalMetricPercentage(aScoringGroup.getFinScoringMetricsList(),"F");
				
				doFillScoringMetrics(aScoringGroup.getFinScoringMetricsList(),"F");
			}
			
			if(aScoringGroup.getNonFinScoringMetricsList() != null){
				
				//Calculation Of Max Metric Score & Percentage In Total Group Score
				doCalMetricPercentage(aScoringGroup.getNonFinScoringMetricsList(),"N");
				
				doFillScoringMetrics(aScoringGroup.getNonFinScoringMetricsList(),"N");
			}
			
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_ScoringGroupDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_scoreGroupCode = this.scoreGroupCode.getValue();
		this.oldVar_scoreGroupName = this.scoreGroupName.getValue();
		this.oldVar_categoryType = this.categoryType.getSelectedIndex();
		this.oldVar_minScore = this.minScore.intValue();	
		this.oldVar_isoverride = this.isoverride.isChecked();
		this.oldVar_overrideScore = this.overrideScore.intValue();	
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_scoringSlabList=this.scoringSlabList;
		this.oldVar_scoringMetricList=this.scoringMetricList;
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.scoreGroupCode.setValue(this.oldVar_scoreGroupCode);
		this.scoreGroupName.setValue(this.oldVar_scoreGroupName);
		this.categoryType.setSelectedIndex(this.oldVar_categoryType);
		this.minScore.setValue(this.oldVar_minScore);
		this.isoverride.setChecked(this.oldVar_isoverride);
		this.overrideScore.setValue(this.oldVar_overrideScore);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.scoringMetricList=this.oldVar_scoringMetricList;
		this.scoringSlabList=this.oldVar_scoringSlabList;

		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {

		//To clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_scoreGroupCode != this.scoreGroupCode.getValue()) {
			return true;
		}
		if (this.oldVar_scoreGroupName != this.scoreGroupName.getValue()) {
			return true;
		}
		if (this.oldVar_categoryType != this.categoryType.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_minScore != this.minScore.intValue()) {
			return  true;
		}
		if (this.oldVar_isoverride != this.isoverride.isChecked()) {
			return true;
		}
		if (this.oldVar_overrideScore != this.overrideScore.intValue()) {
			return  true;
		}
		if (this.oldVar_scoringMetricList!= this.scoringMetricList) {
			return  true;
		}
		if (this.oldVar_scoringSlabList!= this.scoringSlabList) {
			return  true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.scoreGroupCode.isReadonly()){
			this.scoreGroupCode.setConstraint(new PTStringValidator(Labels.getLabel("label_ScoringGroupDialog_ScoreGroupCode.value"),PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
		}	
		if (!this.scoreGroupName.isReadonly()){
			this.scoreGroupName.setConstraint(new PTStringValidator(Labels.getLabel("label_ScoringGroupDialog_ScoreGroupName.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}	
		if (!this.categoryType.isDisabled()){
			this.categoryType.setConstraint(new StaticListValidator(PennantStaticListUtil.getCategoryType(), 
					Labels.getLabel("label_ScoringGroupDialog_CategoryType.value")));
		}	
		if (!this.minScore.isReadonly()){
			this.minScore.setConstraint(new IntValidator(5, Labels.getLabel("label_ScoringGroupDialog_MinScore.value"), false));

		}	
		if (!this.overrideScore.isReadonly()){
			if(this.isoverride.isChecked()){
				this.overrideScore.setConstraint(new IntValidator(5, Labels.getLabel("label_ScoringGroupDialog_OverrideScore.value"), false));
			}
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.scoreGroupCode.setConstraint("");
		this.scoreGroupName.setConstraint("");
		this.categoryType.setConstraint("");
		this.minScore.setConstraint("");
		this.overrideScore.setConstraint("");
		logger.debug("Leaving");
	}
	
	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}
	
	/**
	 * Method for Clear the Error Messages
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.scoreGroupCode.setErrorMessage("");
		this.scoreGroupName.setErrorMessage("");
		this.categoryType.setErrorMessage("");
		this.minScore.setErrorMessage("");
		this.overrideScore.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * When user checks isOverRide check box 
	 * @param event
	 */
	public void onCheck$isoverride(Event event){
		logger.debug("Entering " + event.toString());		
		checkoverride(this.isoverride.isChecked());
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * This method Disabled the overrideScore 
	 * @param isOverride
	 */
	private void checkoverride(boolean isOverride){
		if (isOverride) {
			this.overrideScore.setDisabled(false);
		}else{
			this.overrideScore.setDisabled(true);
			this.overrideScore.setValue(0);
		}
	}
	
	/**
	 * Method for Selection Of Category type
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onChange$categoryType(Event event) throws InterruptedException{
		logger.debug("Entering" + event.toString());
		
		if((this.listBoxRetailScoringMetrics.getItemCount() > 0) || 
				(this.listBoxFinScoringMetrics.getItemCount() > 0) ||
				(this.listBoxNFScoringMetrics.getItemCount() > 0)){

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show("Do You want to modify Scoring Metric Details Data ? ", 
					"Confirmation", MultiLineMessageBox.YES
					| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				
				this.listBoxRetailScoringMetrics.getItems().clear();
				this.listBoxFinScoringMetrics.getItems().clear();
				this.listBoxNFScoringMetrics.getItems().clear();
				
				this.finScoreMetricTab.setVisible(false);
				this.nonFinScoreMetricTab.setVisible(false);
				this.retailScoreMetricTab.setVisible(false);
				if(this.categoryType.getSelectedIndex() != 0){
					if("C".equals(this.categoryType.getSelectedItem().getValue().toString()) ||
							"B".equals(this.categoryType.getSelectedItem().getValue().toString())){
						this.finScoreMetricTab.setVisible(true);
						this.nonFinScoreMetricTab.setVisible(true);
					}else{
						this.retailScoreMetricTab.setVisible(true);
					}
				}
			}
		}else{
			this.finScoreMetricTab.setVisible(false);
			this.nonFinScoreMetricTab.setVisible(false);
			this.retailScoreMetricTab.setVisible(false);
			if(this.categoryType.getSelectedIndex() != 0){
				if("C".equals(this.categoryType.getSelectedItem().getValue().toString()) || 
						"B".equals(this.categoryType.getSelectedItem().getValue().toString())){
					this.finScoreMetricTab.setVisible(true);
					this.nonFinScoreMetricTab.setVisible(true);
				}else{
					this.retailScoreMetricTab.setVisible(true);
				}
			}
		}
		
		
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a ScoringGroup object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		
		final ScoringGroup aScoringGroup = new ScoringGroup();
		BeanUtils.copyProperties(getScoringGroup(), aScoringGroup);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
									+ "\n\n --> " + aScoringGroup.getScoreGroupId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aScoringGroup.getRecordType()).equals("")){
				aScoringGroup.setVersion(aScoringGroup.getVersion()+1);
				aScoringGroup.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aScoringGroup.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aScoringGroup,tranType)){
					refreshList();
					//do close the Dialog window
					closeDialog(this.window_ScoringGroupDialog, "ScoringGroup"); 
				}
			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new ScoringGroup object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		// remember the old variables
		doStoreInitValues();
		final ScoringGroup aScoringGroup = getScoringGroupService().getNewScoringGroup();
		setScoringGroup(aScoringGroup);
		doClear();          // clear all components
		doEdit();           // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.scoreGroupName.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getScoringGroup().isNewRecord()){
			this.scoreGroupCode.setReadonly(false);
			this.btnCopyTo.setVisible(false);
			this.btnCancel.setVisible(false);
			this.categoryType.setDisabled(false);
		}else{
			this.scoreGroupCode.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.categoryType.setDisabled(true);
		}

		this.scoreGroupName.setReadonly(isReadOnly("ScoringGroupDialog_scoreGroupName"));
		//this.categoryType.setDisabled(isReadOnly("ScoringGroupDialog_categoryType"));
		this.minScore.setReadonly(isReadOnly("ScoringGroupDialog_minScore"));
		this.isoverride.setDisabled(isReadOnly("ScoringGroupDialog_isoverride"));
		this.overrideScore.setReadonly(isReadOnly("ScoringGroupDialog_overrideScore"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.scoringGroup.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.scoreGroupCode.setReadonly(true);
		this.scoreGroupName.setReadonly(true);
		this.categoryType.setDisabled(true);
		this.minScore.setReadonly(true);
		this.isoverride.setDisabled(true);
		this.overrideScore.setReadonly(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		
		// remove validation, if there are a save before
		this.scoreGroupCode.setValue("");
		this.scoreGroupName.setValue("");
		this.categoryType.setSelectedIndex(0);
		this.minScore.setText("");
		this.isoverride.setChecked(false);
		this.overrideScore.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final ScoringGroup aScoringGroup = new ScoringGroup();
		BeanUtils.copyProperties(getScoringGroup(), aScoringGroup);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doClearMessage();
		doSetValidation();
		// fill the ScoringGroup object with the components data
		doWriteComponentsToBean(aScoringGroup);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aScoringGroup.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aScoringGroup.getRecordType()).equals("")){
				aScoringGroup.setVersion(aScoringGroup.getVersion()+1);
				if(isNew){
					aScoringGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aScoringGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aScoringGroup.setNewRecord(true);
				}
			}
		}else{
			aScoringGroup.setVersion(aScoringGroup.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aScoringGroup,tranType)){
				refreshList();
				//do Close the Exiting dialog
				closeDialog(this.window_ScoringGroupDialog, "ScoringGroup");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}
	/**
	 * Set the workFlow Details List to Object
	 * @param aScoringGroup
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(ScoringGroup aScoringGroup,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aScoringGroup.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aScoringGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aScoringGroup.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
			aScoringGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aScoringGroup.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aScoringGroup);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aScoringGroup))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;

				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aScoringGroup.setTaskId(taskId);
			aScoringGroup.setNextTaskId(nextTaskId);
			aScoringGroup.setRoleCode(getRole());
			aScoringGroup.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aScoringGroup, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aScoringGroup);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aScoringGroup, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aScoringGroup, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		ScoringGroup aScoringGroup = (ScoringGroup) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getScoringGroupService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getScoringGroupService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getScoringGroupService().doApprove(auditHeader);

						if(aScoringGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getScoringGroupService().doReject(auditHeader);
						if(aScoringGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999
								, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ScoringGroupDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_ScoringGroupDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Method for Get Max Score in Scoring Metric
	 * @param rule
	 * @return
	 */
	public BigDecimal getMetricMaxScore(String rule) {
		logger.debug("Entering");
		BigDecimal max = BigDecimal.ZERO;
		String[] codevalue = rule.split("Result");
		for (int i = 0; i < codevalue.length; i++) {
			if (i == 0) {
				continue;
			}
			if (codevalue[i].contains(";")) {
				String code = codevalue[i].substring(codevalue[i].indexOf("=") + 1, codevalue[i].indexOf(";"));
				System.out.println("values " + code);
				if (code.contains("'")) {
				code=code.replace("'", "");
				}
				//System.out.println(Integer.parseInt(code.trim()));
				if (new BigDecimal(code.trim()).compareTo(max) > 0) {
					max = new BigDecimal(code.trim());
				}
			}
		}
		logger.debug("Leaving");
		return max;
	}
	
	/**
	 * This method calculate the percentage of each metric scoring point of
	 * total metric scoring points
	 * 
	 * @param scoringMetricsList
	 */
	public List<ScoringMetrics>  doCalMetricPercentage(List<ScoringMetrics> scoringMetricsList, String categoryType){
		logger.debug("Entering ");
		BigDecimal totMetricScoringPoints = BigDecimal.ZERO; //total metric points
		for(int i=0;i<scoringMetricsList.size();i++){
			if(!(scoringMetricsList.get(i).getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)
					||scoringMetricsList.get(i).getRecordType().equals(PennantConstants.RECORD_TYPE_CAN))){
				
				if("R".equals(categoryType)){
					scoringMetricsList.get(i).setLovDescMetricMaxPoints(
							getMetricMaxScore(scoringMetricsList.get(i).getLovDescSQLRule()));
				}else if("F".equals(categoryType) || "N".equals(categoryType)){
					
					if(this.finScoreMap.containsKey(scoringMetricsList.get(i).getScoringId())){
						List<ScoringMetrics> subMetricList = this.finScoreMap.get(
								scoringMetricsList.get(i).getScoringId());
						BigDecimal subGroupScore = BigDecimal.ZERO;
						for (ScoringMetrics scoreMetric : subMetricList) {
							BigDecimal subRuleScore = BigDecimal.ZERO;
							if("F".equals(categoryType)){
								/*subRuleScore = getMetricMaxScore(scoreMetric.getLovDescSQLRule());
								scoreMetric.setLovDescMetricMaxPoints(subRuleScore);*/
								
								subRuleScore = scoreMetric.getLovDescMetricMaxPoints();
								
							}else if("N".equals(categoryType)){
								subRuleScore = scoreMetric.getLovDescMetricMaxPoints();
							}
							subGroupScore = subGroupScore.add(subRuleScore);
						}
						scoringMetricsList.get(i).setLovDescMetricMaxPoints(subGroupScore);
					}else{
						scoringMetricsList.get(i).setLovDescMetricMaxPoints(BigDecimal.ZERO);
					}
				}

				totMetricScoringPoints=totMetricScoringPoints.add(scoringMetricsList.get(i).getLovDescMetricMaxPoints());
			}
		}
		
		if("R".equals(categoryType)){
			getScoringGroup().setLovDescTotRetailScorPoints(totMetricScoringPoints.longValue());
		}else if("F".equals(categoryType)){
			getScoringGroup().setLovDescTotFinScorPoints(totMetricScoringPoints.longValue());
		}else if("N".equals(categoryType)){
			getScoringGroup().setLovDescTotNFScorPoints(totMetricScoringPoints.longValue());
		}
	
		if(totMetricScoringPoints.compareTo(new BigDecimal(0))!=0 ){
			
			BigDecimal  totalPerc = BigDecimal.ZERO;
			for (int i = 0; i < scoringMetricsList.size(); i++) {
				
				ScoringMetrics scoringMetrics = scoringMetricsList.get(i);
				
				if(!(scoringMetrics.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)
						||scoringMetrics.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN))){

					if(i == scoringMetricsList.size()-1){
						scoringMetrics.setLovDescMetricTotPerc((new BigDecimal(100).subtract(totalPerc)).toString());
					}else{
						
						BigDecimal metricPerCentage=((scoringMetrics.getLovDescMetricMaxPoints()
							.multiply(new BigDecimal(100)).divide(totMetricScoringPoints, 2, RoundingMode.HALF_UP)));
						
						totalPerc = totalPerc.add(metricPerCentage);
						scoringMetrics.setLovDescMetricTotPerc(metricPerCentage.toString());
					}

					if(scoringMetrics.getRecordType().equals("")){
						scoringMetrics.setRecordType(PennantConstants.RCD_UPD);
					}
				}else{
					scoringMetrics.setLovDescMetricTotPerc(new String("0"));
				}
			}
		}else{
			for(ScoringMetrics scoringMetrics:scoringMetricsList){
				scoringMetrics.setLovDescMetricTotPerc(new String("0"));
				if(scoringMetrics.getRecordType().equals("")){
					scoringMetrics.setRecordType(PennantConstants.RCD_UPD);
				}
			}
		}
		logger.debug("Leaving ");
		return scoringMetricsList;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ScoringGroup aScoringGroup, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aScoringGroup.getBefImage(), aScoringGroup);   
		return new AuditHeader(String.valueOf(aScoringGroup.getScoreGroupId()),null,null,null,
				auditDetail,aScoringGroup.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ScoringGroupDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<ScoringGroup> soScoringGroup = getScoringGroupListCtrl().getSearchObj();
		getScoringGroupListCtrl().pagingScoringGroupList.setActivePage(0);
		getScoringGroupListCtrl().getPagedListWrapper().setSearchObject(soScoringGroup);
		if(getScoringGroupListCtrl().listBoxScoringGroup!=null){
			getScoringGroupListCtrl().listBoxScoringGroup.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("ScoringGroup");
		notes.setReference(String.valueOf(getScoringGroup().getScoreGroupId()));
		notes.setVersion(getScoringGroup().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public ScoringGroup getScoringGroup() {
		return this.scoringGroup;
	}
	public void setScoringGroup(ScoringGroup scoringGroup) {
		this.scoringGroup = scoringGroup;
	}

	public void setScoringGroupService(ScoringGroupService scoringGroupService) {
		this.scoringGroupService = scoringGroupService;
	}
	public ScoringGroupService getScoringGroupService() {
		return this.scoringGroupService;
	}

	public void setScoringGroupListCtrl(ScoringGroupListCtrl scoringGroupListCtrl) {
		this.scoringGroupListCtrl = scoringGroupListCtrl;
	}
	public ScoringGroupListCtrl getScoringGroupListCtrl() {
		return this.scoringGroupListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setScoringSlabList(List<ScoringSlab> scoringSlabList) {
		this.scoringSlabList = scoringSlabList;
	}
	public List<ScoringSlab> getScoringSlabList() {
		return scoringSlabList;
	}

	@SuppressWarnings("unchecked")
	public void setScoringSlabPagedListWrapper(){
		if(this.scoringSlabPagedListWrapper == null){
			this.scoringSlabPagedListWrapper = (PagedListWrapper<ScoringSlab>)
			SpringUtil.getBean("pagedListWrapper");;
		}
	}
	public PagedListWrapper<ScoringSlab> getScoringSlabPagedListWrapper() {
		return scoringSlabPagedListWrapper;
	}

	public void setScoringMetricList(List<ScoringMetrics> scoringMetricList) {
		this.scoringMetricList = scoringMetricList;
	}
	public List<ScoringMetrics> getScoringMetricList() {
		return scoringMetricList;
	}
	
	public PagedListWrapper<ScoringMetrics> getScoringMetricsPagedListWrapper() {
		return scoringMetricsPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setScoringMetricsPagedListWrapper(){
		if(this.scoringMetricsPagedListWrapper == null){
			this.scoringMetricsPagedListWrapper = (PagedListWrapper<ScoringMetrics>) 
			SpringUtil.getBean("pagedListWrapper");;
		}
	}

	public void setFinScoringMetricList(List<ScoringMetrics> finScoringMetricList) {
		this.finScoringMetricList = finScoringMetricList;
	}
	public List<ScoringMetrics> getFinScoringMetricList() {
		return finScoringMetricList;
	}
	
	public List<ScoringMetrics> getNonFinScoringMetricList() {
		return nonFinScoringMetricList;
	}
	public void setNonFinScoringMetricList(
			List<ScoringMetrics> nonFinScoringMetricList) {
		this.nonFinScoringMetricList = nonFinScoringMetricList;
	}

	public Map<Long, List<ScoringMetrics>> getFinScoreMap() {
		return finScoreMap;
	}
	public void setFinScoreMap(Map<Long, List<ScoringMetrics>> finScoreMap) {
		this.finScoreMap = finScoreMap;
	}

}
