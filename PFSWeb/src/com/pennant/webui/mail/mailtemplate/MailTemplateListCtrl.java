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
 * FileName    		:  MailTemplateListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2012    														*
 *                                                                  						*
 * Modified Date    :  04-10-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.mail.mailtemplate;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.mail.mailtemplate.model.MailTemplateListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Mail/MailTemplate/MailTemplateList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class MailTemplateListCtrl extends GFCBaseListCtrl<MailTemplate> implements Serializable {

	private static final long serialVersionUID = 7079846100434942353L;
	private final static Logger logger = Logger.getLogger(MailTemplateListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_MailTemplateList; 		// autowired
	protected Borderlayout 	borderLayout_MailTemplateList; 	// autowired
	protected Paging 		pagingMailTemplateList; 		// autowired
	protected Listbox 		listBoxMailTemplate; 			// autowired

	// List headers
	protected Listheader listheader_TemplateCode; 		// autowired
	protected Listheader listheader_TemplateForSMS;		// autowired
	protected Listheader listheader_TemplateForEMail; 	// autowired
	protected Listheader listheader_TemplateActive; 	// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;			// autowired

	// checkRights
	protected Button btnHelp; 										// autowired
	protected Button button_MailTemplateList_NewMailTemplate; 		// autowired
	protected Button button_MailTemplateList_MailTemplateSearch; 	// autowired
	protected Button button_MailTemplateList_PrintList; 			// autowired
	protected Label  label_MailTemplateList_RecordStatus; 			// autoWired
	protected Label  label_MailTemplateList_RecordType; 			// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<MailTemplate> searchObj;

	private transient MailTemplateService mailTemplateService;
	private transient WorkFlowDetails workFlowDetails=null;

	protected Textbox 	templateCode; 				// autowired
	protected Listbox 	sortOperator_templateCode; 	// autowired
	protected Textbox 	templateName; 				// autowired
	protected Listbox 	sortOperator_templateName; 	// autowired
	protected Textbox 	templateType; 				// autowired
	protected Listbox 	sortOperator_templateType; 	// autowired
	protected Checkbox 	active; 					// autowired
	protected Listbox 	sortOperator_active; 		// autowired
	protected Textbox 	recordStatus; 				// autowired
	protected Listbox 	recordType;					// autowired
	protected Listbox 	sortOperator_recordStatus; 	// autowired
	protected Listbox 	sortOperator_recordType; 	// autowired

	private Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Textbox 		templateFor; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;

	private transient boolean  approvedList=false;

	/**
	 * default constructor.<br>
	 */
	public MailTemplateListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_MailTemplateList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("MailTemplate");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("MailTemplate");

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

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_templateCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_templateCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_templateName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_templateName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_templateType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_templateType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_active.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_active.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.label_MailTemplateList_RecordStatus.setVisible(false);
			this.label_MailTemplateList_RecordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_MailTemplateList.setHeight(getBorderLayoutHeight());
		this.listBoxMailTemplate.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 

		// set the paging parameters
		this.pagingMailTemplateList.setPageSize(getListRows());
		this.pagingMailTemplateList.setDetailed(true);

		this.listheader_TemplateCode.setSortAscending(new FieldComparator("templateCode", true));
		this.listheader_TemplateCode.setSortDescending(new FieldComparator("templateCode", false));

		this.listheader_TemplateForSMS.setSortAscending(new FieldComparator("smsTemplate", true));
		this.listheader_TemplateForSMS.setSortDescending(new FieldComparator("smsTemplate", false));

		this.listheader_TemplateForEMail.setSortAscending(new FieldComparator("emailTemplate", true));
		this.listheader_TemplateForEMail.setSortDescending(new FieldComparator("emailTemplate", false));

		this.listheader_TemplateActive.setSortAscending(new FieldComparator("active", true));
		this.listheader_TemplateActive.setSortDescending(new FieldComparator("active", false));

		// set the itemRenderer
		this.listBoxMailTemplate.setItemRenderer(new MailTemplateListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_MailTemplateList_NewMailTemplate.setVisible(false);
			this.button_MailTemplateList_MailTemplateSearch.setVisible(false);
			this.button_MailTemplateList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("MailTemplateList");

		if(moduleType==null){
			this.button_MailTemplateList_NewMailTemplate.setVisible(getUserWorkspace()
					.isAllowed("button_MailTemplateList_NewMailTemplate"));
		}else{
			this.button_MailTemplateList_NewMailTemplate.setVisible(false);
		}	
		this.button_MailTemplateList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_MailTemplateList_PrintList"));
		
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.mail.mailtemplate.model.MailTemplateListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onMailTemplateItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected MailTemplate object
		final Listitem item = this.listBoxMailTemplate.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final MailTemplate aMailTemplate = (MailTemplate) item.getAttribute("data");
			MailTemplate mailTemplate = null;
			if(approvedList){
				mailTemplate = getMailTemplateService().getApprovedMailTemplateById(aMailTemplate.getId(),
						aMailTemplate.getTemplateFor());
			}else{
				mailTemplate = getMailTemplateService().getMailTemplateById(aMailTemplate.getId(), 
						aMailTemplate.getTemplateFor());
			}

			if(mailTemplate==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aMailTemplate.getTemplateCode();
				errParm[0]=PennantJavaUtil.getLabel("label_TemplateCode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
						"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					StringBuilder whereCond = new StringBuilder();

					whereCond.append(" AND TemplateCode='");
					whereCond.append(mailTemplate.getTemplateCode());
					whereCond.append("' AND version = ");
					whereCond.append(mailTemplate.getVersion());
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "MailTemplate", 
							whereCond.toString(), mailTemplate.getTaskId(), mailTemplate.getNextTaskId());

					if (userAcces){
						showDetailView(mailTemplate);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(mailTemplate);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the MailTemplate dialog with a new empty entry. <br>
	 */
	public void onClick$button_MailTemplateList_NewMailTemplate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new MailTemplate object, We GET it from the backend.
		final MailTemplate aMailTemplate = getMailTemplateService().getNewMailTemplate();
		aMailTemplate.setEmailTemplate(true);
		aMailTemplate.setActive(true);  
		showDetailView(aMailTemplate);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param MailTemplate (aMailTemplate)
	 * @throws Exception
	 */
	private void showDetailView(MailTemplate aMailTemplate) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aMailTemplate.getWorkflowId()==0 && isWorkFlowEnabled()){
			aMailTemplate.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("mailTemplate", aMailTemplate);
		if(moduleType!=null){
			map.put("enqModule", true);
		}else{
			map.put("enqModule", false);
		}
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the MailTemplateListbox from the
		 * dialog when we do a delete, edit or insert a MailTemplate.
		 */
		map.put("mailTemplateListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Mail/MailTemplate/MailTemplateDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_MailTemplateList);
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

		this.sortOperator_templateCode.setSelectedIndex(0);
		this.templateCode.setValue("");
		this.sortOperator_templateName.setSelectedIndex(0);
		this.templateName.setValue("");
		this.sortOperator_templateType.setSelectedIndex(0);
		this.templateType.setValue("");
		this.sortOperator_active.setSelectedIndex(0);
		this.active.setChecked(false);

		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$button_MailTemplateList_MailTemplateSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 

	public void doSearch() {
		logger.debug("Entering");
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<MailTemplate>(MailTemplate.class,getListRows());

		// Defualt Sort on the table
		this.searchObj.addSort("TemplateCode", false);
		
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("Templates_View");

			if(this.moduleType==null){
				/*if(this.templateFor != null){
					this.searchObj.addFilter(new Filter("templateFor", PennantConstants.TEMPLATE_FOR_AE));
				}else {
					this.searchObj.addFilter(new Filter("templateFor", PennantConstants.TEMPLATE_FOR_CN));
				}*/
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
				
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		
		if(approvedList){
			this.searchObj.addTabelName("Templates_AView");
		}else{
			this.searchObj.addTabelName("Templates_View");
		}
		
		if (StringUtils.isNotEmpty(this.templateCode.getValue())) {

			// get the search operator
			final Listitem item_TemplateCode = this.sortOperator_templateCode.getSelectedItem();

			if (item_TemplateCode != null) {
				final int searchOpId = ((SearchOperators) item_TemplateCode.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("templateCode", "%" + this.templateCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId != -1) {
					this.searchObj.addFilter(new Filter("templateCode", this.templateCode.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.templateName.getValue())) {

			// get the search operator
			final Listitem item_TemplateName = this.sortOperator_templateName.getSelectedItem();

			if (item_TemplateName != null) {
				final int searchOpId = ((SearchOperators) item_TemplateName.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("templateName", "%" + this.templateName.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId != -1) {
					this.searchObj.addFilter(new Filter("templateName", this.templateName.getValue(), searchOpId));
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.templateType.getValue())) {

			// get the search operator
			final Listitem item_TemplateType = this.sortOperator_templateType.getSelectedItem();

			if (item_TemplateType != null) {
				final int searchOpId = ((SearchOperators) item_TemplateType.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("templateType", "%" + this.templateType.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId != -1) {
					this.searchObj.addFilter(new Filter("templateType", this.templateType.getValue(), searchOpId));
				}
			}
		}
		
		// get the search operatorxxx
		final Listitem item_Active = this.sortOperator_active.getSelectedItem();

		if (item_Active != null) {
			final int searchOpId = ((SearchOperators) item_Active.getAttribute("data")).getSearchOperatorId();
			if (searchOpId != -1) {
				if(this.active.isChecked()){
					this.searchObj.addFilter(new Filter("active",1, searchOpId));
				}else{
					this.searchObj.addFilter(new Filter("active",0, searchOpId));	
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("recordStatus", "%" + this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId != -1) {
					this.searchObj.addFilter(new Filter("recordStatus", this.recordStatus.getValue(), searchOpId));
				}
			}
		}

		String selectedValue="";
		if (this.recordType.getSelectedItem()!=null){
			selectedValue =this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType!= null) {
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute("data")).getSearchOperatorId();
				if (searchOpId == Filter.OP_LIKE) {
					this.searchObj.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() + "%", searchOpId));
				} else if (searchOpId != -1) {
					this.searchObj.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxMailTemplate,this.pagingMailTemplateList);

		logger.debug("Leaving");
	}
	/**
	 * When the mailTemplate print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_MailTemplateList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("MailTemplate", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}
	public MailTemplateService getMailTemplateService() {
		return this.mailTemplateService;
	}

	public JdbcSearchObject<MailTemplate> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<MailTemplate> searchObj) {
		this.searchObj = searchObj;
	}
}