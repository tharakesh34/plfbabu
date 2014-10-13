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
 * FileName    		:  IndustryListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.industry;

import java.io.Serializable;
import java.math.BigDecimal;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
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
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.service.systemmasters.IndustryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.industry.model.IndustryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Industry/IndustryList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class IndustryListCtrl extends GFCBaseListCtrl<Industry> implements Serializable {

	private static final long serialVersionUID = -5021713706640652581L;
	private final static Logger logger = Logger.getLogger(IndustryListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_IndustryList; 		// autoWired
	protected Borderlayout 	borderLayout_IndustryList; 	// autoWired
	protected Paging 		pagingIndustryList; 		// autoWired
	protected Listbox		listBoxIndustry; 			// autoWired

	protected Textbox 		industryCode; 					// autoWired
	protected Listbox 		sortOperator_industryCode; 		// autoWired
	protected Textbox 		industryDesc; 					// autoWired
	protected Listbox 		sortOperator_industryDesc; 		// autoWired
	protected Decimalbox 	industryLimit; 					// autoWired
	protected Listbox 		sortOperator_industryLimit; 	// autoWired
	protected Checkbox 		industryIsActive; 				// autoWired
	protected Listbox 		sortOperator_industryIsActive; 	// autoWired
	protected Textbox 		recordStatus; 					// autoWired
	protected Listbox 		recordType; 					// autoWired
	protected Listbox 		sortOperator_recordStatus; 		// autoWired
	protected Listbox 		sortOperator_recordType; 		// autoWired

	// List headers
	protected Listheader listheader_IndustryCode; 		// autoWired
	protected Listheader listheader_IndustryDesc; 		// autoWired
	protected Listheader listheader_IndustryLimit; 		// autoWired
	protected Listheader listheader_IndustryIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 									// autoWired
	protected Button button_IndustryList_NewIndustry; 			// autoWired
	protected Button button_IndustryList_IndustrySearchDialog; 	// autoWired
	protected Button button_IndustryList_PrintList; 			// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Industry> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;
	
	private transient IndustryService industryService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public IndustryListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Industry object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_IndustryList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Industry");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Industry");

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
		this.sortOperator_industryCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_industryCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_industryDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_industryDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_industryLimit.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_industryLimit.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_industryIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_industryIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		 * currentDesktopHeight from a hidden IntBox from the index.zul that
		 * are filled by onClientInfo() in the indexCtroller
		 */

		this.borderLayout_IndustryList.setHeight(getBorderLayoutHeight());
		this.listBoxIndustry.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingIndustryList.setPageSize(getListRows());
		this.pagingIndustryList.setDetailed(true);

		this.listheader_IndustryCode.setSortAscending(new FieldComparator("industryCode", true));
		this.listheader_IndustryCode.setSortDescending(new FieldComparator("industryCode", false));
		this.listheader_IndustryDesc.setSortAscending(new FieldComparator("industryDesc", true));
		this.listheader_IndustryDesc.setSortDescending(new FieldComparator("industryDesc", false));
		this.listheader_IndustryLimit.setSortAscending(new FieldComparator("industryLimit", true));
		this.listheader_IndustryLimit.setSortDescending(new FieldComparator("industryLimit", false));
		this.listheader_IndustryIsActive.setSortAscending(new FieldComparator("industryIsActive", true));
		this.listheader_IndustryIsActive.setSortDescending(new FieldComparator("industryIsActive", false));

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
		this.searchObj = new JdbcSearchObject<Industry>(Industry.class,	getListRows());
		this.searchObj.addSort("IndustryCode",false);
		this.searchObj.addField("industryCode");
		this.searchObj.addField("industryDesc");
		this.searchObj.addField("industryLimit");
		this.searchObj.addField("industryIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTIndustries_View");
			if (isFirstTask()) {
				button_IndustryList_NewIndustry.setVisible(true);
			} else {
				button_IndustryList_NewIndustry.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTIndustries_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_IndustryList_NewIndustry.setVisible(false);
			this.button_IndustryList_IndustrySearchDialog.setVisible(false);
			this.button_IndustryList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxIndustry.setItemRenderer(new IndustryListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("IndustryList");
		this.button_IndustryList_NewIndustry.setVisible(getUserWorkspace()
				.isAllowed("button_IndustryList_NewIndustry"));
		this.button_IndustryList_IndustrySearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_IndustryList_IndustryFindDialog"));
		this.button_IndustryList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_IndustryList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.industry.model.
	 * IndustryListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onIndustryItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Industry object
		final Listitem item = this.listBoxIndustry.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Industry aIndustry = (Industry) item.getAttribute("data");
			final Industry industry = getIndustryService().getIndustryById(aIndustry.getId());

			if (industry == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aIndustry.getIndustryCode();
				valueParm[1] = aIndustry.getSubSectorCode();

				errParm[0] = PennantJavaUtil.getLabel("label_IndustryCode") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_Industry_SubSectorCode") + ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND IndustryCode='" + industry.getIndustryCode() 
				+ "' AND version=" + industry.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Industry", whereCond, industry.getTaskId(),industry.getNextTaskId());
					if (userAcces) {
						showDetailView(industry);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(industry);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Industry dialog with a new empty entry. <br>
	 */
	public void onClick$button_IndustryList_NewIndustry(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new Industry object, We GET it from the back end.
		final Industry aIndustry = getIndustryService().getNewIndustry();
		aIndustry.setIndustryLimit(BigDecimal.ZERO); // initialization
		showDetailView(aIndustry);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param Industry
	 *            (aIndustry)
	 * @throws Exception
	 */
	private void showDetailView(Industry aIndustry) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aIndustry.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aIndustry.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("industry", aIndustry);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the IndustryListbox from the
		 * dialog when we do a delete, edit or insert a Industry.
		 */
		map.put("industryListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Industry/IndustryDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_IndustryList);
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

		this.sortOperator_industryCode.setSelectedIndex(0);
		this.industryCode.setValue("");
		this.sortOperator_industryDesc.setSelectedIndex(0);
		this.industryDesc.setValue("");
		this.sortOperator_industryLimit.setSelectedIndex(0);
		this.industryLimit.setText("");
		this.sortOperator_industryIsActive.setSelectedIndex(0);
		this.industryIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		// Clears the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxIndustry, this.pagingIndustryList);

		logger.debug("Leaving" + event.toString());
	}

	/*
	 * call the Industry dialog
	 */
	public void onClick$button_IndustryList_IndustrySearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the industry print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_IndustryList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Industry", getSearchObj(),this.pagingIndustryList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();
		
		if (!StringUtils.trimToEmpty(this.industryCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_industryCode.getSelectedItem(),this.industryCode.getValue(), "IndustryCode");
		}
		if (!StringUtils.trimToEmpty(this.industryDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_industryDesc.getSelectedItem(),this.industryDesc.getValue(), "IndustryDesc");
		}
		if (this.industryLimit.getValue()!=null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_industryLimit.getSelectedItem(),this.industryLimit.getValue(), "IndustryLimit");
		}

		// Active
		int intActive=0;
		if(this.industryIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_industryIsActive.getSelectedItem(),intActive, "IndustryIsActive");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),this.recordType.getSelectedItem().getValue().toString(),"RecordType");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxIndustry,
				this.pagingIndustryList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setIndustryService(IndustryService industryService) {
		this.industryService = industryService;
	}
	public IndustryService getIndustryService() {
		return this.industryService;
	}

	public JdbcSearchObject<Industry> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Industry> searchObj) {
		this.searchObj = searchObj;
	}
}