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
 * FileName    		:  AddressTypeListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.addresstype;

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
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.service.systemmasters.AddressTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.addresstype.model.AddressTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/AddressType/AddressTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class AddressTypeListCtrl extends GFCBaseListCtrl<AddressType> implements Serializable {

	private static final long serialVersionUID = 1817958653208633892L;
	private final static Logger logger = Logger.getLogger(AddressTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_AddressTypeList; 		// autoWired
	protected Borderlayout 	borderLayout_AddressTypeList; 	// autoWired
	protected Paging 		pagingAddressTypeList; 			// autoWired
	protected Listbox 		listBoxAddressType; 			// autoWired

	protected Textbox addrTypeCode;
	protected Listbox sortOperator_addrTypeCode;

	protected Textbox addrTypeDesc;
	protected Listbox sortOperator_addrTypeDesc;

	protected Intbox addrTypePriority;
	protected Listbox sortOperator_addrTypePriority;

	protected Checkbox addrTypeIsActive;
	protected Listbox sortOperator_addrTypeIsActive;

	protected Textbox recordStatus;
	protected Listbox sortOperator_recordStatus;

	protected Listbox recordType;
	protected Listbox sortOperator_recordType;

	// List headers
	protected Listheader listheader_AddrTypeCode; 		// autoWired
	protected Listheader listheader_AddrTypeDesc; 		// autoWired
	protected Listheader listheader_AddrTypePriority; 	// autoWired
	protected Listheader listheader_AddrTypeIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;			// autoWired

	// checkRights
	protected Button btnHelp; 											// autoWired
	protected Button button_AddressTypeList_NewAddressType; 			// autoWired
	protected Button button_AddressTypeList_AddressTypeSearchDialog; 	// autoWired
	protected Button button_AddressTypeList_PrintList;		 			// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<AddressType> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient AddressTypeService addressTypeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public AddressTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected AddressType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AddressTypeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("AddressType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AddressType");

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

		this.sortOperator_addrTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_addrTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_addrTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_addrTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_addrTypePriority.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_addrTypePriority.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_addrTypeIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_addrTypeIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		this.borderLayout_AddressTypeList.setHeight(getBorderLayoutHeight());
		this.listBoxAddressType.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingAddressTypeList.setPageSize(getListRows());
		this.pagingAddressTypeList.setDetailed(true);

		this.listheader_AddrTypeCode.setSortAscending(new FieldComparator("addrTypeCode", true));
		this.listheader_AddrTypeCode.setSortDescending(new FieldComparator("addrTypeCode", false));
		this.listheader_AddrTypeDesc.setSortAscending(new FieldComparator("addrTypeDesc", true));
		this.listheader_AddrTypeDesc.setSortDescending(new FieldComparator("addrTypeDesc", false));
		this.listheader_AddrTypePriority.setSortAscending(new FieldComparator("addrTypePriority", true));
		this.listheader_AddrTypePriority.setSortDescending(new FieldComparator("addrTypePriority", false));
		this.listheader_AddrTypeIsActive.setSortAscending(new FieldComparator("addrTypeIsActive", true));
		this.listheader_AddrTypeIsActive.setSortDescending(new FieldComparator("addrTypeIsActive", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<AddressType>(AddressType.class,getListRows());
		this.searchObj.addSort("AddrTypeCode",false);
		this.searchObj.addField("addrTypeCode");
		this.searchObj.addField("addrTypeDesc");
		this.searchObj.addField("addrTypePriority");
		this.searchObj.addField("addrTypeIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_AddressTypeList_NewAddressType.setVisible(true);
			} else {
				button_AddressTypeList_NewAddressType.setVisible(false);
			}
			this.searchObj.addTabelName("BMTAddressTypes_View");
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		} else{
			this.searchObj.addTabelName("BMTAddressTypes_AView");
		}
		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_AddressTypeList_NewAddressType.setVisible(false);
			this.button_AddressTypeList_AddressTypeSearchDialog.setVisible(false);
			this.button_AddressTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();              
			// set the itemRenderer
			this.listBoxAddressType.setItemRenderer(new AddressTypeListModelItemRenderer());
		}
		this.listBoxAddressType.setHeight(getBorderLayoutHeight());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("AddressTypeList");

		this.button_AddressTypeList_NewAddressType.setVisible(getUserWorkspace()
				.isAllowed("button_AddressTypeList_NewAddressType"));
		this.button_AddressTypeList_AddressTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_AddressTypeList_AddressTypeFindDialog"));
		this.button_AddressTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_AddressTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.addresstype.model.
	 * AddressTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onAddressTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected AddressType object
		final Listitem item = this.listBoxAddressType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final AddressType aAddressType = (AddressType) item.getAttribute("data");
			final AddressType addressType = getAddressTypeService().getAddressTypeById(aAddressType.getId());

			if (addressType == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aAddressType.getAddrTypeCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_AddrTypeCode") + ":" + aAddressType.getAddrTypeCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND AddrTypeCode='" + addressType.getAddrTypeCode() 
				+ "' AND version=" + addressType.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"AddressType", whereCond, addressType.getTaskId(), addressType.getNextTaskId());
					if (userAcces) {
						showDetailView(addressType);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(addressType);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the AddressType dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AddressTypeList_NewAddressType(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new AddressType object, We GET it from the back end.
		final AddressType aAddressType = getAddressTypeService().getNewAddressType();
		aAddressType.setAddrTypePriority(0);
		showDetailView(aAddressType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param AddressType
	 *            (aAddressType)
	 * @throws Exception
	 */
	private void showDetailView(AddressType aAddressType) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aAddressType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aAddressType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("addressType", aAddressType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the AddressTypeListbox from the
		 * dialog when we do a delete, edit or insert a AddressType.
		 */
		map.put("addressTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/AddressType/AddressTypeDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_AddressTypeList);
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
		this.sortOperator_addrTypeCode.setSelectedIndex(0);
		this.addrTypeCode.setValue("");
		this.sortOperator_addrTypeDesc.setSelectedIndex(0);
		this.addrTypeDesc.setValue("");
		this.sortOperator_addrTypePriority.setSelectedIndex(0);
		this.addrTypePriority.setValue(null);
		this.sortOperator_addrTypeIsActive.setSelectedIndex(0);
		this.addrTypeIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clear All Filters
		this.searchObj.clearFilters();

		// Set the ListModel for the articles.
		getPagedListWrapper().init(getSearchObj(), this.listBoxAddressType,this.pagingAddressTypeList);

		logger.debug("Leaving" + event.toString());
	}

	/*
	 * call the AddressType dialog
	 */
	public void onClick$button_AddressTypeList_AddressTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.addrTypeCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_addrTypeCode.getSelectedItem(),this.addrTypeCode.getValue(), "AddrTypeCode");
		}
		if (!StringUtils.trimToEmpty(this.addrTypeDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_addrTypeDesc.getSelectedItem(),this.addrTypeDesc.getValue(), "AddrTypeDesc");
		}
		if (this.addrTypePriority.getValue()!= null) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_addrTypePriority.getSelectedItem(),this.addrTypePriority.getValue(), "AddrTypePriority");
		}
		// Active
		int intActive=0;
		if(this.addrTypeIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_addrTypeIsActive.getSelectedItem(),intActive, "AddrTypeIsActive");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null&& !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())) {
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
		getPagedListWrapper().init(this.searchObj, this.listBoxAddressType,this.pagingAddressTypeList);
		logger.debug("Leaving");
	}
	/**
	 * When the addressType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_AddressTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("AddressType", getSearchObj(),this.pagingAddressTypeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setAddressTypeService(AddressTypeService addressTypeService) {
		this.addressTypeService = addressTypeService;
	}
	public AddressTypeService getAddressTypeService() {
		return this.addressTypeService;
	}

	public JdbcSearchObject<AddressType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<AddressType> searchObj) {
		this.searchObj = searchObj;
	}

}