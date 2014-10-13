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
 * FileName    		:  CityListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.city;

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
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.service.systemmasters.CityService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.city.model.CityListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/City/CityList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CityListCtrl extends GFCBaseListCtrl<City> implements Serializable {

	private static final long serialVersionUID = 485796535935527728L;
	private final static Logger logger = Logger.getLogger(CityListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CityList;		 // autoWired
	protected Panel 		panel_CityList; 		 // autoWired
	protected Borderlayout 	borderLayout_CityList; 	 // autoWired
	protected Paging 		pagingCityList; 		 // autoWired
	protected Listbox 		listBoxCity; 			 // autoWired


	protected Listbox sortOperator_pCCountry;		 // autoWired
	protected Textbox pCCountry;			    	 // autoWired

	protected Listbox  sortOperator_pCProvince;		  // autoWired
	protected Textbox pCProvince;				      // autoWired

	protected Listbox sortOperator_pCCity;		      // autoWired
	protected Textbox pCCity;  				          // autoWired

	protected Listbox sortOperator_pCCityName;	      // autoWired
	protected Textbox pCCityName;				      // autoWired

	protected Listbox sortOperator_recordStatus;      // autoWired
	protected Textbox recordStatus;				  	  // autoWired

	protected Listbox sortOperator_recordType;	  	  // autoWired
	protected Listbox recordType;				      // autoWired

	// List headers
	protected Listheader 	listheader_PCCountry; 		// autoWired
	protected Listheader 	listheader_PCProvince; 		// autoWired
	protected Listheader 	listheader_PCCity; 			// autoWired
	protected Listheader 	listheader_PCCityName; 		// autoWired
	protected Listheader 	listheader_RecordStatus; 	// autoWired
	protected Listheader 	listheader_RecordType;		// autoWired

	// checkRights
	protected Button 		btnHelp; 					// autoWired
	protected Button 		button_CityList_NewCity; 	// autoWired
	protected Button 		button_CityList_CitySearchDialog; // autoWired
	protected Button 		button_CityList_PrintList; 	// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<City> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;
	
	private transient CityService cityService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CityListCtrl() {
		super();
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected City object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CityList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("City");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("City");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}
		this.sortOperator_pCCity.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_pCCity.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_pCCityName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_pCCityName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_pCCountry.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_pCCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_pCProvince.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_pCProvince.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CityList.setHeight(getBorderLayoutHeight());
		this.listBoxCity.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingCityList.setPageSize(getListRows());
		this.pagingCityList.setDetailed(true);
		
		//Apply sorting for getting List in the ListBox 
		this.listheader_PCCountry.setSortAscending(new FieldComparator("pCCountry", true));
		this.listheader_PCCountry.setSortDescending(new FieldComparator("pCCountry", false));
		this.listheader_PCProvince.setSortAscending(new FieldComparator("pCProvince", true));
		this.listheader_PCProvince.setSortDescending(new FieldComparator("pCProvince", false));
		this.listheader_PCCity.setSortAscending(new FieldComparator("pCCity",true));
		this.listheader_PCCity.setSortDescending(new FieldComparator("pCCity",false));
		this.listheader_PCCityName.setSortAscending(new FieldComparator("pCCityName", true));
		this.listheader_PCCityName.setSortDescending(new FieldComparator("pCCityName", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		this.searchObj = new JdbcSearchObject<City>(City.class, getListRows());
		this.searchObj.addSort("PCCountry",false);
		this.searchObj.addField("pCCountry");
		this.searchObj.addField("lovDescPCCountryName");
		this.searchObj.addField("pCProvince");
		this.searchObj.addField("lovDescPCProvinceName");
		this.searchObj.addField("pCCity");
		this.searchObj.addField("pCCityName");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTProvinceVsCity_View");
			if (isFirstTask()) {
				button_CityList_NewCity.setVisible(true);
			} else {
				button_CityList_NewCity.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("RMTProvinceVsCity_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CityList_NewCity.setVisible(false);
			this.button_CityList_CitySearchDialog.setVisible(false);
			this.button_CityList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil
					.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxCity.setItemRenderer(new CityListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("CityList");

		this.button_CityList_NewCity.setVisible(getUserWorkspace()
				.isAllowed("button_CityList_NewCity"));
		this.button_CityList_CitySearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CityList_CityFindDialog"));
		this.button_CityList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CityList_PrintList"));
		logger.debug("Leaving ");

	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see:
	 * com.pennant.webui.rmtmasters.city.model.CityListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCityItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected City object
		final Listitem item = this.listBoxCity.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final City aCity = (City) item.getAttribute("data");
			final City city = getCityService().getCityById(aCity.getPCCountry(),
					aCity.getPCProvince(), aCity.getPCCity());

			if(city==null){
				String[] valueParm = new String[3];
				String[] errParm= new String[3];

				valueParm[0] = aCity.getPCCountry();
				valueParm[1] = aCity.getPCProvince();
				valueParm[2] = aCity.getPCCity();
				
				errParm[0]=PennantJavaUtil.getLabel("label_PCCountry")+":"+valueParm[0];
				errParm[1]=PennantJavaUtil.getLabel("label_PCProvince")+":"+valueParm[1];
				errParm[2]=PennantJavaUtil.getLabel("label_PCCity")+":"+valueParm[2];
				
				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", 
								errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if (isWorkFlowEnabled()) {
					
					String whereCond = " AND PCCountry='" + city.getPCCountry()
					+ "'AND PCProvince='" + city.getPCProvince()
					+ "' AND PCCity='" + city.getPCCity() + "'  AND version="
					+ city.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails()
									.getLoginUsrID(), "City", whereCond,
							city.getTaskId(), city.getNextTaskId());
					if (userAcces) {
						showDetailView(city);
					} else {
						PTMessageUtils.showErrorMessage(Labels
								.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(city);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the City dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CityList_NewCity(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new City object, We GET it from the backEnd.
		final City aCity = getCityService().getNewCity();
		showDetailView(aCity);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param City
	 *            (aCity)
	 * @throws Exception
	 */
	private void showDetailView(City aCity) throws Exception {
		logger.debug("Entering ");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aCity.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCity.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("city", aCity);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the CityListbox from the dialog
		 * when we do a delete, edit or insert a City.
		 */
		map.put("cityListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/City/CityDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CityList);
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
		this.sortOperator_pCCity.setSelectedIndex(0);
		this.pCCity.setValue("");
		this.sortOperator_pCCityName.setSelectedIndex(0);
		this.pCCityName.setValue("");
		this.sortOperator_pCCountry.setSelectedIndex(0);
		this.pCCountry.setValue("");
		this.sortOperator_pCProvince.setSelectedIndex(0);
		this.pCProvince.setValue("");
		if(isWorkFlowEnabled())
		{
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clear All Filters
		this.searchObj.clearFilters();

		// Set the ListModel for the articles.
		getPagedListWrapper().init(getSearchObj(), this.listBoxCity,this.pagingCityList);

		logger.debug("leaving" + event.toString());
	}

	/**
	 * call the City dialog
	 * @param event
	 * @throws Exception
	 */
	
	public void onClick$button_CityList_CitySearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("leaving" + event.toString());
	}

	/**
	 * When the city print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CityList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("City", getSearchObj(),this.pagingCityList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}


	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.pCCountry.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_pCCountry.getSelectedItem(),this.pCCountry.getValue(), "PCCountry");
		}
		if (!StringUtils.trimToEmpty(this.pCProvince.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_pCProvince.getSelectedItem(),this.pCProvince.getValue(), "PCProvince");
		}
		if (!StringUtils.trimToEmpty(this.pCCity.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_pCCity.getSelectedItem(),this.pCCity.getValue(), "PCCity");
		}

		if (!StringUtils.trimToEmpty(this.pCCityName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_pCCityName.getSelectedItem(),this.pCCityName.getValue(), "PCCityName");
		}

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null && !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())) {
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
		getPagedListWrapper().init(this.searchObj, this.listBoxCity,
				this.pagingCityList);

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCityService(CityService cityService) {
		this.cityService = cityService;
	}
	public CityService getCityService() {
		return this.cityService;
	}

	public JdbcSearchObject<City> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<City> searchObj) {
		this.searchObj = searchObj;
	}
	
}