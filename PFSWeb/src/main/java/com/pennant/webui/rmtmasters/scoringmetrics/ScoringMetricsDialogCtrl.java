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
 * FileName    		:  ScoringMetricsDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.rmtmasters.scoringmetrics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rulefactory.CorpScoreGroupDetail;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.rmtmasters.scoringgroup.ScoringGroupDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/RulesFactory/ScoringMetrics/scoringMetricsDialog.zul file.
 */
public class ScoringMetricsDialogCtrl extends GFCBaseCtrl<ScoringGroup> {
	private static final long serialVersionUID = 6462101709968848897L;
	private static final Logger logger = Logger.getLogger(ScoringMetricsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window  window_ScoringMetricsDialog;      // autoWired
	protected Longbox scoringId;                        // autoWired
	protected Decimalbox  metricMaxPoints;                  // autoWired
	protected Textbox metricTotPercentage;              // autoWired
	protected Textbox lovDescScoringCode;               // autoWired
	protected Textbox lovDescScoringName;               // autoWired
	

	// not auto wired variables
	private ScoringMetrics scoringMetrics; // over handed per parameter

	private transient boolean validationOn;
	
	protected Button  btnSearchScoringCode;
	protected Button  btnSearchFinScoringCode;
	protected Button  btnSearchNFScoringCode;

	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private transient ScoringGroup scoringGroup =null;
	private ScoringGroupDialogCtrl scoringGroupDialogCtrl;
	private List<ScoringMetrics>   scoringMetricsList;
	private List<ScoringMetrics>   originalScoringMetricsList;
	
	private RuleService ruleService;
	 
	private String categoryValue = "";
	private String categoryType = "";

	/**
	 * default constructor.<br>
	 */
	public ScoringMetricsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ScoringMetricsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ScoringMetrics object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ScoringMetricsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScoringMetricsDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("scoringMetrics")) {
			this.scoringMetrics = (ScoringMetrics) arguments.get("scoringMetrics");
			ScoringMetrics befImage =new ScoringMetrics();
			BeanUtils.copyProperties(this.scoringMetrics, befImage);
			this.scoringMetrics.setBefImage(befImage);
			setScoringMetrics(this.scoringMetrics);
		} else {
			setScoringMetrics(null);
		}
		
		if (arguments.containsKey("scoringGroup")) {
			this.scoringGroup = (ScoringGroup) arguments.get("scoringGroup");
			setScoringGroup(this.scoringGroup);
		}
		
		if (arguments.containsKey("originalScoringMetricsList")) {
			this.setOriginalScoringMetricsList((List<ScoringMetrics>) arguments.get("originalScoringMetricsList"));
			setOriginalScoringMetricsList(this.originalScoringMetricsList);
		}
		
		if (arguments.containsKey("categoryValue")) {
			this.categoryValue = (String) arguments.get("categoryValue");
		}
		
		if (arguments.containsKey("categoryType")) {
			this.categoryType = (String) arguments.get("categoryType");
		}
		
		if (arguments.containsKey("scoringGroupDialogCtrl")) {
			this.scoringGroupDialogCtrl = (ScoringGroupDialogCtrl) arguments.get("scoringGroupDialogCtrl");
			setScoringGroupDialogCtrl(this.scoringGroupDialogCtrl);
		} else {
			setScoringGroupDialogCtrl(null);
		}
		
		if(arguments.containsKey("roleCode")){
			getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "ScoringMetricsDialog");
		}
		
		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "ScoringMetricsDialog");
		}
		
		// READ OVERHANDED parameters !
		// we get the scoringMetricsListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete scoringMetrics here.

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getScoringMetrics());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.metricMaxPoints.setMaxlength(4);
		this.metricTotPercentage.setMaxlength(10);

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

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ScoringMetricsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ScoringMetricsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ScoringMetricsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ScoringMetricsDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
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
		MessageUtil.showHelpWindow(event, window_ScoringMetricsDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.scoringMetrics.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aScoringMetrics
	 *            ScoringMetrics
	 */
	public void doWriteBeanToComponents(ScoringMetrics aScoringMetrics) {
		logger.debug("Entering") ;
		this.scoringId.setValue(aScoringMetrics.getScoringId());
		this.lovDescScoringCode.setValue(aScoringMetrics.getLovDescScoringCode());
		this.lovDescScoringName.setValue(aScoringMetrics.getLovDescScoringCodeDesc());
		this.metricMaxPoints.setValue(aScoringMetrics.getLovDescMetricMaxPoints());
		this.metricTotPercentage.setValue(aScoringMetrics.getLovDescMetricTotPerc());

		this.recordStatus.setValue(aScoringMetrics.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aScoringMetrics
	 */
	public void doWriteComponentsToBean(ScoringMetrics aScoringMetrics) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aScoringMetrics.setScoringId(this.scoringId.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aScoringMetrics.setLovDescScoringCode(this.lovDescScoringCode.getValue());
			aScoringMetrics.setLovDescScoringCodeDesc(this.lovDescScoringName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aScoringMetrics.setLovDescMetricMaxPoints(this.metricMaxPoints.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		aScoringMetrics.setCategoryType(this.categoryValue);

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aScoringMetrics.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aScoringMetrics
	 * @throws InterruptedException
	 */
	public void doShowDialog(ScoringMetrics aScoringMetrics) throws InterruptedException {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aScoringMetrics.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			//this.aScoringMetrics.focus();
		} else {
			this.btnCtrl.setInitEdit();
			doReadOnly();
			btnCancel.setVisible(false);
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aScoringMetrics);

			this.window_ScoringMetricsDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving") ;
	}
	
	// Search Button Component Events
	
	public void onClick$btnSearchScoringCode(Event event){
		logger.debug("Entering" + event.toString());
		
		Filter[] filters=new Filter[2];
		filters[0]=new Filter("RuleModule",RuleConstants.MODULE_SCORES,Filter.OP_EQUAL);
		filters[1]=new Filter("RuleEvent",RuleConstants.EVENT_RSCORE,Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_ScoringMetricsDialog,"Rule",filters);
		if (dataObject instanceof String){
			this.scoringId.setValue(Long.valueOf(0));
			this.lovDescScoringCode.setValue("");
		}else{
			Rule details= (Rule) dataObject;
			if (details != null) {
				this.scoringId.setValue(details.getRuleId());
				this.lovDescScoringCode.setValue(details.getRuleCode());
				this.lovDescScoringName.setValue(details.getRuleCodeDesc());
				this.metricMaxPoints.setValue(GetMax(details.getSQLRule()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onClick$btnSearchFinScoringCode(Event event){
		logger.debug("Entering" + event.toString());
		
		Filter[] filters=new Filter[2];
		filters[0]=new Filter("CategoryType","F",Filter.OP_EQUAL);
		filters[1]=new Filter("CustCategory",this.categoryType,Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_ScoringMetricsDialog,"CorpScoreGroupDetail",filters);
		if (dataObject instanceof String){
			this.scoringId.setValue(Long.valueOf(0));
			this.lovDescScoringCode.setValue("");
		}else{
			CorpScoreGroupDetail details= (CorpScoreGroupDetail) dataObject;
			if (details != null) {
				
				this.scoringId.setValue(details.getGroupId());
				this.lovDescScoringCode.setValue(details.getGroupDesc());
				this.lovDescScoringName.setValue(details.getGroupDesc());
				
				/*List<Rule> ruleList = getRuleService().getRulesByGroupId(details.getGroupId(),
				 *  PennantRuleConstants.MODULE_SCORES, PennantRuleConstants.EVENT_FSCORE);
				List<ScoringMetrics> subMetricList = new ArrayList<ScoringMetrics>();
				ScoringMetrics metric = null;
				BigDecimal totalSubGroupScore = BigDecimal.ZERO;
				
				for (Rule rule : ruleList) {
					metric = new ScoringMetrics();
					metric.setLovDescScoringCode(rule.getRuleCode());
					metric.setLovDescScoringCodeDesc(rule.getRuleCodeDesc());
					BigDecimal maxRuleScore = GetMax(rule.getSQLRule());
					metric.setLovDescMetricMaxPoints(maxRuleScore);
					totalSubGroupScore = totalSubGroupScore.add(maxRuleScore);
					subMetricList.add(metric);
				}*/
				
				List<NFScoreRuleDetail> ruleList = getRuleService().getNFRulesByGroupId(details.getGroupId());
				List<ScoringMetrics> subMetricList = new ArrayList<ScoringMetrics>();
				ScoringMetrics metric = null;
				BigDecimal totalSubGroupScore = BigDecimal.ZERO;
				
				for (NFScoreRuleDetail rule : ruleList) {
					metric = new ScoringMetrics();
					metric.setLovDescScoringCode(String.valueOf(rule.getNFRuleId()));
					metric.setLovDescScoringCodeDesc(rule.getNFRuleDesc());
					metric.setLovDescMetricMaxPoints(rule.getMaxScore());
					totalSubGroupScore = totalSubGroupScore.add(rule.getMaxScore());
					subMetricList.add(metric);
				}
				
				this.metricMaxPoints.setValue(totalSubGroupScore);
				if(getScoringGroupDialogCtrl().getFinScoreMap().containsKey(details.getGroupId())){
					getScoringGroupDialogCtrl().getFinScoreMap().remove(details.getGroupId());
				}
				getScoringGroupDialogCtrl().getFinScoreMap().put(details.getGroupId(), subMetricList);
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onClick$btnSearchNFScoringCode(Event event){
		logger.debug("Entering" + event.toString());
		
		Filter[] filters=new Filter[2];
		filters[0]=new Filter("CategoryType","N",Filter.OP_EQUAL);
		filters[1]=new Filter("CustCategory",this.categoryType,Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_ScoringMetricsDialog,"CorpScoreGroupDetail",filters);
		if (dataObject instanceof String){
			this.scoringId.setValue(Long.valueOf(0));
			this.lovDescScoringCode.setValue("");
		}else{
			CorpScoreGroupDetail details= (CorpScoreGroupDetail) dataObject;
			if (details != null) {
				
				this.scoringId.setValue(details.getGroupId());
				this.lovDescScoringCode.setValue(details.getGroupDesc());
				this.lovDescScoringName.setValue(details.getGroupDesc());
				
				List<NFScoreRuleDetail> ruleList = getRuleService().getNFRulesByGroupId(details.getGroupId());
				List<ScoringMetrics> subMetricList = new ArrayList<ScoringMetrics>();
				ScoringMetrics metric = null;
				BigDecimal totalSubGroupScore = BigDecimal.ZERO;
				
				for (NFScoreRuleDetail rule : ruleList) {
					metric = new ScoringMetrics();
					metric.setLovDescScoringCode(String.valueOf(rule.getNFRuleId()));
					metric.setLovDescScoringCodeDesc(rule.getNFRuleDesc());
					metric.setLovDescMetricMaxPoints(rule.getMaxScore());
					totalSubGroupScore = totalSubGroupScore.add(rule.getMaxScore());
					subMetricList.add(metric);
				}
				this.metricMaxPoints.setValue(totalSubGroupScore);
				if(getScoringGroupDialogCtrl().getFinScoreMap().containsKey(details.getGroupId())){
					getScoringGroupDialogCtrl().getFinScoreMap().remove(details.getGroupId());
				}
				getScoringGroupDialogCtrl().getFinScoreMap().put(details.getGroupId(), subMetricList);
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public BigDecimal GetMax(String rule) {
		BigDecimal max = BigDecimal.ZERO;
		String[] codevalue = rule.split("Result");
		for (int i = 0; i < codevalue.length; i++) {
			if (i == 0) {
				continue;
			}
			if (codevalue[i].contains(";")) {
				String code = codevalue[i].substring(codevalue[i].indexOf('=') + 1, codevalue[i].indexOf(';'));
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
		return max;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		setValidationOn(true);
	}
	private void doSetLOVValidation() {
		logger.debug("Entering ");
		this.lovDescScoringCode.setConstraint(new PTStringValidator(Labels.getLabel("label_ScoringMetricsDialog_ScoringCode.value"),null,true));
		logger.debug("Leaving ");

	}
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");
		this.lovDescScoringCode.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
	}
	
	/**
	 * Method for remove Error Messages
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.scoringId.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a ScoringMetrics object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final ScoringMetrics aScoringMetrics = new ScoringMetrics();
		BeanUtils.copyProperties(getScoringMetrics(), aScoringMetrics);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
								+ Labels.getLabel("listheader_ScoringMetricsCode.label")+":"+ aScoringMetrics.getLovDescScoringCode();
	
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aScoringMetrics.getRecordType())){
				aScoringMetrics.setVersion(aScoringMetrics.getVersion()+1);
				aScoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()){
					aScoringMetrics.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}else if (StringUtils.trimToEmpty(aScoringMetrics.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aScoringMetrics.setVersion(aScoringMetrics.getVersion() + 1);
				aScoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newScoringMetricsProcess(aScoringMetrics, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_ScoringMetricsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
	
					getScoringGroupDialogCtrl().doFillScoringMetrics(doCalMetricPercentage(this.scoringMetricsList),this.categoryValue);
					this.window_ScoringMetricsDialog.onClose();
				}
			}catch (DataAccessException e){
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if("R".equals(this.categoryValue)){
			this.btnSearchScoringCode.setVisible(true);
		}else if("F".equals(this.categoryValue)){
			this.btnSearchFinScoringCode.setVisible(true);
		}else if("N".equals(this.categoryValue)){
			this.btnSearchNFScoringCode.setVisible(true);
		}

		if (getScoringMetrics().isNewRecord()){
			this.btnSearchScoringCode.setDisabled(false);
			this.btnSearchFinScoringCode.setDisabled(false);
			this.btnSearchNFScoringCode.setDisabled(false);
			this.btnCancel.setVisible(false);
		}else{
			this.btnSearchScoringCode.setDisabled(true);
			this.btnSearchFinScoringCode.setDisabled(true);
			this.btnSearchNFScoringCode.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		this.scoringId.setReadonly(isReadOnly("ScoringMetricsDialog_scoringId"));
		this.metricMaxPoints.setReadonly(isReadOnly("ScoringMetricsDialog_metricMaxPoints"));
		this.metricTotPercentage.setReadonly(isReadOnly("ScoringMetricsDialog_metricTotPercentage"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.scoringMetrics.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			if (this.scoringMetrics.isNewRecord()){
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setBtnStatus_Edit();
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.scoringId.setReadonly(true);
		this.metricMaxPoints.setReadonly(true);
		this.metricTotPercentage.setReadonly(true);
		this.btnSearchScoringCode.setDisabled(true);
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
		this.scoringId.setText("");
		this.metricMaxPoints.setText("");
		this.metricTotPercentage.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final ScoringMetrics aScoringMetrics = new ScoringMetrics();
		BeanUtils.copyProperties(getScoringMetrics(), aScoringMetrics);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the ScoringMetrics object with the components data
		doWriteComponentsToBean(aScoringMetrics);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aScoringMetrics.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aScoringMetrics.getRecordType())){
				aScoringMetrics.setVersion(aScoringMetrics.getVersion()+1);
				if(isNew){
					aScoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aScoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aScoringMetrics.setNewRecord(true);
				}
			}
		}else{
			/*set the tranType according to RecordType*/
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
				aScoringMetrics.setVersion(1);
				aScoringMetrics.setRecordType(PennantConstants.RCD_ADD);
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}

			if(StringUtils.isBlank(aScoringMetrics.getRecordType())){
				tranType =PennantConstants.TRAN_UPD;
				aScoringMetrics.setRecordType(PennantConstants.RCD_UPD);
			}
			if(PennantConstants.RCD_ADD.equals(aScoringMetrics.getRecordType()) && isNew){
				tranType =PennantConstants.TRAN_ADD;
			} else if(PennantConstants.RECORD_TYPE_NEW.equals(aScoringMetrics.getRecordType())){
				tranType =PennantConstants.TRAN_UPD;
			} 
		}
		try {
			AuditHeader auditHeader =  newScoringMetricsProcess(aScoringMetrics,tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_ScoringMetricsDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getScoringGroupDialogCtrl().doFillScoringMetrics(doCalMetricPercentage(this.scoringMetricsList),this.categoryValue);
				this.window_ScoringMetricsDialog.onClose();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method calculate the percentage of each metric scoring point of
	 * total metric scoring points
	 * 
	 * @param scoringMetricsList
	 */
	public List<ScoringMetrics>  doCalMetricPercentage(List<ScoringMetrics> scoringMetricsList){
		logger.debug("Entering ");
		BigDecimal totMetricScoringPoints = BigDecimal.ZERO; //total metric points
		for(int i=0;i<scoringMetricsList.size();i++){
			if(!(scoringMetricsList.get(i).getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)
					||scoringMetricsList.get(i).getRecordType().equals(PennantConstants.RECORD_TYPE_CAN))){

				totMetricScoringPoints=totMetricScoringPoints.add(scoringMetricsList.get(i).getLovDescMetricMaxPoints());
			}
		}
		if("R".equals(categoryValue)){
			getScoringGroupDialogCtrl().getScoringGroup().setLovDescTotRetailScorPoints(totMetricScoringPoints.longValue());
		}else if("F".equals(categoryValue)){
			getScoringGroupDialogCtrl().getScoringGroup().setLovDescTotFinScorPoints(totMetricScoringPoints.longValue());
		}else if("N".equals(categoryValue)){
			getScoringGroupDialogCtrl().getScoringGroup().setLovDescTotNFScorPoints(totMetricScoringPoints.longValue());
		}
		
		if(totMetricScoringPoints.compareTo(BigDecimal.ZERO)!=0 ){
			for(ScoringMetrics scoringMetrics:scoringMetricsList){

				if(!(scoringMetrics.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)
						||scoringMetrics.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN))){

					BigDecimal metricPerCentage=scoringMetrics.getLovDescMetricMaxPoints()
								.multiply(new BigDecimal(100)).divide(totMetricScoringPoints, 2, RoundingMode.HALF_UP);

					scoringMetrics.setLovDescMetricTotPerc(metricPerCentage.toString());

					if(StringUtils.isEmpty(scoringMetrics.getRecordType())){
						scoringMetrics.setRecordType(PennantConstants.RCD_UPD);
					}
				}else{
					scoringMetrics.setLovDescMetricTotPerc("0");
				}
			}
		}else{
			for(ScoringMetrics scoringMetrics:scoringMetricsList){
				scoringMetrics.setLovDescMetricTotPerc("0");
				if(StringUtils.isEmpty(scoringMetrics.getRecordType())){
					scoringMetrics.setRecordType(PennantConstants.RCD_UPD);
				}
			}
		}
		logger.debug("Leaving ");
		return scoringMetricsList;
	}
	
	/**
	 * Method for Checking list of Existing Data
	 * @param aScoringMetrics
	 * @param tranType
	 * @return
	 */
	private AuditHeader newScoringMetricsProcess(ScoringMetrics aScoringMetrics,String tranType){
		logger.debug("Entering ");
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aScoringMetrics, tranType);
		scoringMetricsList= new ArrayList<ScoringMetrics>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aScoringMetrics.getLovDescScoringCode());
		errParm[0] = Labels.getLabel("label_ScoringMetricsDialog_ScoringMetricCode.value") + ":"+valueParm[0];

		if(getOriginalScoringMetricsList()!=null 
				&& getOriginalScoringMetricsList().size()>0){
			for (int i = 0; i < getOriginalScoringMetricsList().size(); i++) {
				ScoringMetrics scoringMetrics = getOriginalScoringMetricsList().get(i);

				if( aScoringMetrics.getScoringId()== scoringMetrics.getScoringId()){ // Both Current and Existing list metric code same

					/*if same ScoringSlab added twice set error detail*/
					if(aScoringMetrics.isNew()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm)
								, getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(PennantConstants.TRAN_DEL.equals(tranType)){
						if(PennantConstants.RECORD_TYPE_UPD.equals(aScoringMetrics.getRecordType())){
							aScoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							scoringMetricsList.add(aScoringMetrics);
						}else if(PennantConstants.RCD_ADD.equals(aScoringMetrics.getRecordType())){
							recordAdded=true;
						}else if(PennantConstants.RECORD_TYPE_NEW.equals(aScoringMetrics.getRecordType())){
							aScoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							scoringMetricsList.add(aScoringMetrics);
						}else if(PennantConstants.RECORD_TYPE_CAN.equals(aScoringMetrics.getRecordType())){
							recordAdded=true;
							for (int j = 0; j < getOriginalScoringMetricsList().size(); j++) {
								ScoringMetrics scorMetrics  = getOriginalScoringMetricsList().get(j);
								if( aScoringMetrics.getScoringId()== scorMetrics.getScoringId()){
									scoringMetricsList.add(scorMetrics);
								}
							}
						}else if(PennantConstants.RECORD_TYPE_DEL.equals(aScoringMetrics.getRecordType())){
							aScoringMetrics.setNewRecord(true);
						}
					}else{
						if(!PennantConstants.TRAN_UPD.equals(tranType) ){
							scoringMetricsList.add(scoringMetrics);
						}
					}
				}else{
					scoringMetricsList.add(scoringMetrics);
				}
			}
		}
		if(!recordAdded){
			scoringMetricsList.add(aScoringMetrics);
		}
		logger.debug("Leaving");
		return auditHeader;
	} 
	
	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ScoringMetrics aScoringMetrics, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aScoringMetrics.getBefImage(), aScoringMetrics);   
		return new AuditHeader(String.valueOf(aScoringMetrics.getScoreGroupId())
				,null,null,null,auditDetail,aScoringMetrics.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_ScoringMetricsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("ScoringMetrics");
		notes.setReference("");
		notes.setVersion(getScoringMetrics().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public ScoringMetrics getScoringMetrics() {
		return this.scoringMetrics;
	}
	public void setScoringMetrics(ScoringMetrics scoringMetrics) {
		this.scoringMetrics = scoringMetrics;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public ScoringGroup getScoringGroup() {
		return scoringGroup;
	}
	public void setScoringGroup(ScoringGroup scoringGroup) {
		this.scoringGroup = scoringGroup;
	}

	public ScoringGroupDialogCtrl getScoringGroupDialogCtrl() {
		return scoringGroupDialogCtrl;
	}
	public void setScoringGroupDialogCtrl(
			ScoringGroupDialogCtrl scoringGroupDialogCtrl) {
		this.scoringGroupDialogCtrl = scoringGroupDialogCtrl;
	}

	public void setScoringMetricsList(List<ScoringMetrics> scoringMetricsList) {
		this.scoringMetricsList = scoringMetricsList;
	}
	public List<ScoringMetrics> getScoringMetricsList() {
		return scoringMetricsList;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setOriginalScoringMetricsList(
			List<ScoringMetrics> originalScoringMetricsList) {
		this.originalScoringMetricsList = originalScoringMetricsList;
	}

	public List<ScoringMetrics> getOriginalScoringMetricsList() {
		return originalScoringMetricsList;
	}

}
