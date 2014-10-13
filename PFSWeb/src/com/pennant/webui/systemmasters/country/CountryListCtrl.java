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
 * FileName    		:  CountryListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.country;

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
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.service.systemmasters.CountryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.country.model.CountryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Country/CountryList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CountryListCtrl extends GFCBaseListCtrl<Country> implements Serializable {

	private static final long serialVersionUID = -2437455376763752382L;
	private final static Logger logger = Logger.getLogger(CountryListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CountryList; 		// autoWired
	protected Borderlayout 	borderLayout_CountryList; 	// autoWired
	protected Paging 		pagingCountryList; 			// autoWired
	protected Listbox 		listBoxCountry; 			// autoWired

	protected Listbox sortOperator_countryCode;
	protected Textbox countryCode;

	protected Listbox sortOperator_countryDesc;
	protected Textbox countryDesc;

	protected Listbox sortOperator_countryParentLimit;
	protected Decimalbox countryParentLimit;

	protected Listbox sortOperator_countryResidenceLimit;
	protected Decimalbox countryResidenceLimit;

	protected Listbox sortOperator_countryRiskLimit;
	protected Decimalbox countryRiskLimit;

	protected Listbox sortOperator_countryIsActive;
	protected Checkbox countryIsActive;

	protected Listbox sortOperator_recordStatus;
	protected Textbox recordStatus;

	protected Listbox sortOperator_recordType;
	protected Listbox recordType;

	// List headers
	protected Listheader listheader_CountryCode; 			// autoWired
	protected Listheader listheader_CountryDesc; 			// autoWired
	protected Listheader listheader_CountryParentLimit; 	// autoWired
	protected Listheader listheader_CountryResidenceLimit; 	// autoWired
	protected Listheader listheader_CountryRiskLimit; 		// autoWired
	protected Listheader listheader_CountryIsActive; 		// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 									// autoWired
	protected Button button_CountryList_NewCountry; 			// autoWired
	protected Button button_CountryList_CountrySearchDialog; 	// autoWired
	protected Button button_CountryList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Country> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient CountryService countryService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CountryListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Country object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CountryList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Country");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Country");

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
		this.sortOperator_countryCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_countryCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_countryDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_countryDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_countryParentLimit.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_countryParentLimit.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_countryResidenceLimit.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_countryResidenceLimit.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_countryRiskLimit.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_countryRiskLimit.setItemRenderer(new SearchOperatorListModelItemRenderer());
		this.sortOperator_countryIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_countryIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		this.borderLayout_CountryList.setHeight(getBorderLayoutHeight());
		this.listBoxCountry.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCountryList.setPageSize(getListRows());
		this.pagingCountryList.setDetailed(true);

		this.listheader_CountryCode.setSortAscending(new FieldComparator("countryCode", true));
		this.listheader_CountryCode.setSortDescending(new FieldComparator("countryCode", false));
		this.listheader_CountryDesc.setSortAscending(new FieldComparator("countryDesc", true));
		this.listheader_CountryDesc.setSortDescending(new FieldComparator("countryDesc", false));
		this.listheader_CountryParentLimit.setSortAscending(new FieldComparator("countryParentLimit",true));
		this.listheader_CountryParentLimit.setSortDescending(new FieldComparator("countryParentLimit",false));
		this.listheader_CountryResidenceLimit.setSortAscending(new FieldComparator("countryResidenceLimit",true));
		this.listheader_CountryResidenceLimit.setSortDescending(new FieldComparator("countryResidenceLimit",false));
		this.listheader_CountryRiskLimit.setSortAscending(new FieldComparator("countryRiskLimit", true));
		this.listheader_CountryRiskLimit.setSortDescending(new FieldComparator("countryRiskLimit", false));
		this.listheader_CountryIsActive.setSortAscending(new FieldComparator("countryIsActive", true));
		this.listheader_CountryIsActive.setSortDescending(new FieldComparator("countryIsActive", false));

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
		this.searchObj = new JdbcSearchObject<Country>((Country.class), getListRows());
		this.searchObj.addSort("CountryCode",false);
		this.searchObj.addField("countryCode");
		this.searchObj.addField("countryDesc");
		this.searchObj.addField("countryParentLimit");
		this.searchObj.addField("countryResidenceLimit");
		this.searchObj.addField("countryRiskLimit");
		this.searchObj.addField("countryIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTCountries_View");
			if (isFirstTask()) {
				button_CountryList_NewCountry.setVisible(true);
			} else {
				button_CountryList_NewCountry.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTCountries_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CountryList_NewCountry.setVisible(false);
			this.button_CountryList_CountrySearchDialog.setVisible(false);
			this.button_CountryList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxCountry.setItemRenderer(new CountryListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("CountryList");

		this.button_CountryList_NewCountry.setVisible(getUserWorkspace().isAllowed("button_CountryList_NewCountry"));
		this.button_CountryList_CountrySearchDialog.setVisible(getUserWorkspace().isAllowed("button_CountryList_CountryFindDialog"));
		this.button_CountryList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CountryList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see:
	 * com.pennant.webui.bmtmasters.country.model.CountryListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCountryItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Country object
		final Listitem item = this.listBoxCountry.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Country aCountry = (Country) item.getAttribute("data");
			final Country country = getCountryService().getCountryById(aCountry.getId());

			if (country == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aCountry.getCountryCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_CountryCode") + ":" + aCountry.getCountryCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND CountryCode='"+ country.getCountryCode() 
				+ "' AND version=" + country.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Country", whereCond, country.getTaskId(),country.getNextTaskId());
					if (userAcces) {
						showDetailView(country);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(country);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Country dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CountryList_NewCountry(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new Country object, We GET it from the back end.
		final Country aCountry = getCountryService().getNewCountry();
		showDetailView(aCountry);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param Country
	 *            (aCountry)
	 * @throws Exception
	 */
	private void showDetailView(Country aCountry) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aCountry.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCountry.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("country", aCountry);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CountryListbox from the dialog
		 * when we do a delete, edit or insert a Country.
		 */
		map.put("countryListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Country/CountryDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_CountryList);
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
		this.sortOperator_countryCode.setSelectedIndex(0);
		this.countryCode.setValue("");
		this.sortOperator_countryDesc.setSelectedIndex(0);
		this.countryDesc.setValue("");
		this.sortOperator_countryResidenceLimit.setSelectedIndex(0);
		this.countryResidenceLimit.setText("");
		this.sortOperator_countryParentLimit.setSelectedIndex(0);
		this.countryParentLimit.setText("");
		this.sortOperator_countryRiskLimit.setSelectedIndex(0);
		this.countryRiskLimit.setText("");
		this.sortOperator_countryIsActive.setSelectedIndex(0);
		this.countryIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clear All Filters
		this.searchObj.clearFilters();

		// Set the ListModel for the articles.
		getPagedListWrapper().init(getSearchObj(), this.listBoxCountry,this.pagingCountryList);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Country dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CountryList_CountrySearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the country print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CountryList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Country", getSearchObj(),this.pagingCountryList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.countryCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_countryCode.getSelectedItem(),this.countryCode.getValue(), "CountryCode");
		}
		if (!StringUtils.trimToEmpty(this.countryDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_countryDesc.getSelectedItem(),this.countryDesc.getValue(), "CountryDesc");
		}

		if (this.countryParentLimit.getValue()!= null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_countryParentLimit.getSelectedItem(),this.countryParentLimit.getValue(), "CountryParentLimit");
		}

		if (this.countryResidenceLimit.getValue()!=null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_countryResidenceLimit.getSelectedItem(),this.countryResidenceLimit.getValue(), "CountryResidenceLimit");
		}

		if (this.countryRiskLimit.getValue()!=null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_countryRiskLimit.getSelectedItem(),this.countryRiskLimit.getValue(), "CountryRiskLimit");
		}
		// Active
		int intActive=0;
		if(this.countryIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_countryIsActive.getSelectedItem(),intActive, "CountryIsActive");

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
		getPagedListWrapper().init(this.searchObj, this.listBoxCountry,
				this.pagingCountryList);

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}
	public CountryService getCountryService() {
		return this.countryService;
	}

	public JdbcSearchObject<Country> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Country> searchObj) {
		this.searchObj = searchObj;
	}
}