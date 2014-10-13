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
 * FileName    		:  EMailTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.emailtype;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.service.systemmasters.EMailTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.emailtype.model.EMailTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/EMailType/EMailTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class EMailTypeListCtrl extends GFCBaseListCtrl<EMailType> implements Serializable {

	private static final long serialVersionUID = 2308954215935933494L;
	private final static Logger logger = Logger.getLogger(EMailTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_EMailTypeList; 		// autoWired
	protected Borderlayout 	borderLayout_EMailTypeList; // autoWired
	protected Paging 		pagingEMailTypeList; 		// autoWired
	protected Listbox 		listBoxEMailType; 			// autoWired

	// List headers
	protected Listheader listheader_EmailTypeCode; 		// autoWired
	protected Listheader listheader_EmailTypeDesc; 		// autoWired
	protected Listheader listheader_EmailTypePriority; 	// autoWired
	protected Listheader listheader_EmailTypeIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	protected Textbox 	emailTypeCode; 						// autoWired
	protected Listbox 	sortOperator_emailTypeCode; 		// autoWired
	protected Textbox 	emailTypeDesc; 						// autoWired
	protected Listbox 	sortOperator_emailTypeDesc; 		// autoWired
	protected Intbox 	emailTypePriority; 					// autoWired
	protected Listbox 	sortOperator_emailTypePriority; 	// autoWired
	protected Checkbox 	emailTypeIsActive; 					// autoWired
	protected Listbox 	sortOperator_emailTypeIsActive; 	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	// checkRights
	protected Button btnHelp; 										// autoWired
	protected Button button_EMailTypeList_NewEMailType; 			// autoWired
	protected Button button_EMailTypeList_EMailTypeSearchDialog; 	// autoWired
	protected Button button_EMailTypeList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<EMailType> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient EMailTypeService eMailTypeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public EMailTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected EMailType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EMailTypeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("EMailType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("EMailType");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}
		this.sortOperator_emailTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_emailTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_emailTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_emailTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_emailTypePriority.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_emailTypePriority.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_emailTypeIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_emailTypeIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = setRecordType(this.recordType);
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		} else {
			this.row_AlwWorkflow.setVisible(false);
			
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_EMailTypeList.setHeight(getBorderLayoutHeight());
		this.listBoxEMailType.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingEMailTypeList.setPageSize(getListRows());
		this.pagingEMailTypeList.setDetailed(true);

		this.listheader_EmailTypeCode.setSortAscending(new FieldComparator("emailTypeCode", true));
		this.listheader_EmailTypeCode.setSortDescending(new FieldComparator("emailTypeCode", false));
		this.listheader_EmailTypeDesc.setSortAscending(new FieldComparator("emailTypeDesc", true));
		this.listheader_EmailTypeDesc.setSortDescending(new FieldComparator("emailTypeDesc", false));
		this.listheader_EmailTypePriority.setSortAscending(new FieldComparator("emailTypePriority", true));
		this.listheader_EmailTypePriority.setSortDescending(new FieldComparator("emailTypePriority", false));
		this.listheader_EmailTypeIsActive.setSortAscending(new FieldComparator("emailTypeIsActive", true));
		this.listheader_EmailTypeIsActive.setSortDescending(new FieldComparator("emailTypeIsActive", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj =new JdbcSearchObject<EMailType>(EMailType.class, getListRows());
		this.searchObj.addSort("EmailTypeCode",false);
		this.searchObj.addField("emailTypeCode");
		this.searchObj.addField("emailTypeDesc");
		this.searchObj.addField("emailTypePriority");
		this.searchObj.addField("emailTypeIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTEMailTypes_View");
			if (isFirstTask()) {
				button_EMailTypeList_NewEMailType.setVisible(true);
			} else {
				button_EMailTypeList_NewEMailType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTEMailTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_EMailTypeList_NewEMailType.setVisible(false);
			this.button_EMailTypeList_EMailTypeSearchDialog.setVisible(false);
			this.button_EMailTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxEMailType.setItemRenderer(new EMailTypeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("EMailTypeList");
		this.button_EMailTypeList_NewEMailType.setVisible(getUserWorkspace()
				.isAllowed("button_EMailTypeList_NewEMailType"));
		this.button_EMailTypeList_EMailTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_EMailTypeList_EMailTypeFindDialog"));
		this.button_EMailTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_EMailTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.emailtype.model.
	 * EMailTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onEMailTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected EMailType object
		final Listitem item = this.listBoxEMailType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final EMailType aEMailType = (EMailType) item.getAttribute("data");
			final EMailType eMailType = getEMailTypeService().getEMailTypeById(aEMailType.getId());
			if (eMailType == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aEMailType.getEmailTypeCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_EmailTypeCode") + ":" + aEMailType.getEmailTypeCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND EmailTypeCode='" + eMailType.getEmailTypeCode() 
				+ "' AND version=" + eMailType.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"EMailType", whereCond, eMailType.getTaskId(), eMailType.getNextTaskId());
					if (userAcces) {
						showDetailView(eMailType);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(eMailType);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the EMailType dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_EMailTypeList_NewEMailType(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new EMailType object, We GET it from the back end.
		final EMailType aEMailType = getEMailTypeService().getNewEMailType();
		showDetailView(aEMailType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param EMailType
	 *            (aEMailType)
	 * @throws Exception
	 */
	private void showDetailView(EMailType aEMailType) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aEMailType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aEMailType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("eMailType", aEMailType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the EMailTypeListbox from the
		 * dialog when we do a delete, edit or insert a EMailType.
		 */
		map.put("eMailTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/EMailType/EMailTypeDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_EMailTypeList);
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

		this.sortOperator_emailTypeCode.setSelectedIndex(0);
		this.emailTypeCode.setValue("");
		this.sortOperator_emailTypeDesc.setSelectedIndex(0);
		this.emailTypeDesc.setValue("");
		this.sortOperator_emailTypePriority.setSelectedIndex(0);
		this.emailTypePriority.setValue(null);
		this.sortOperator_emailTypeIsActive.setSelectedIndex(0);
		this.emailTypeIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clears the Filter
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxEMailType,this.pagingEMailTypeList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the EMailType dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_EMailTypeList_EMailTypeSearchDialog(Event event)	throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the eMailType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_EMailTypeList_PrintList(Event event)	throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("EMailType", getSearchObj(),this.pagingEMailTypeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();
		
		if (!StringUtils.trimToEmpty(this.emailTypeCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_emailTypeCode.getSelectedItem(),
					this.emailTypeCode.getValue(), "EmailTypeCode");
		}
		if (!StringUtils.trimToEmpty(this.emailTypeDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_emailTypeDesc.getSelectedItem(),
					this.emailTypeDesc.getValue(), "EmailTypeDesc");
		}
		if (this.emailTypePriority.getValue()!= null) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_emailTypePriority.getSelectedItem(),
					this.emailTypePriority.getValue(), "EmailTypePriority");
		}

		// Active
		int intActive=0;
		if(this.emailTypeIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_emailTypeIsActive.getSelectedItem(),intActive, "EmailTypeIsActive");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_recordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType
						.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_recordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),
					"RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "
						+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}


		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxEMailType,
				this.pagingEMailTypeList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setEMailTypeService(EMailTypeService eMailTypeService) {
		this.eMailTypeService = eMailTypeService;
	}
	public EMailTypeService getEMailTypeService() {
		return this.eMailTypeService;
	}

	public JdbcSearchObject<EMailType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<EMailType> searchObj) {
		this.searchObj = searchObj;
	}
}