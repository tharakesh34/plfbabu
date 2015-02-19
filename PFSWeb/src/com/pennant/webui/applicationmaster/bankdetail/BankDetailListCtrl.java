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
 * FileName    		:  BankDetailListCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.bankdetail;

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
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmaster.bankdetail.model.BankDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/BankDetail/BankDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class BankDetailListCtrl extends	GFCBaseListCtrl<BankDetail> implements Serializable {

	private static final long serialVersionUID = -3571720185247491921L;
	private final static Logger logger = Logger.getLogger(BankDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_BankDetailList; 			// autoWired
	protected Borderlayout 	borderLayout_BankDetailList; 	// autoWired
	protected Paging 		pagingBankDetailList; 			// autoWired
	protected Listbox 		listBoxBankDetail; 				// autoWired

	protected Textbox 	bankCode; 						// autoWired
	protected Listbox 	sortOperator_bankCode; 			// autoWired
	protected Textbox 	bankName; 						// autoWired
	protected Listbox 	sortOperator_bankName; 			// autoWired
	protected Checkbox 	active; 					// autoWired
	protected Listbox 	sortOperator_active; 		// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	// List headers
	protected Listheader listheader_BankCode; 		// autoWired
	protected Listheader listheader_BankName; 		// autoWired
	protected Listheader listheader_Active; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 															   // autoWired
	protected Button button_BankDetailList_NewBankDetail;   	   // autoWired
	protected Button button_BankDetailList_BankDetailSearchDialog; // autoWired
	protected Button button_BankDetailList_PrintList; 						   // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<BankDetail> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient BankDetailService bankDetailService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public BankDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected BankDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BankDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("BankDetail");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BankDetail");

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

		this.sortOperator_bankCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_bankCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_bankName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_bankName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_active.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_active.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		this.borderLayout_BankDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxBankDetail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingBankDetailList.setPageSize(getListRows());
		this.pagingBankDetailList.setDetailed(true);

		this.listheader_BankCode.setSortAscending(new FieldComparator("BankCode", true));
		this.listheader_BankCode.setSortDescending(new FieldComparator("BankCode", false));
		this.listheader_BankName.setSortAscending(new FieldComparator("BankName", true));
		this.listheader_BankName.setSortDescending(new FieldComparator("BankName", false));
		this.listheader_Active.setSortAscending(new FieldComparator("Active", true));
		this.listheader_Active.setSortDescending(new FieldComparator("Active", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("RecordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("RecordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("RecordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("RecordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<BankDetail>(BankDetail.class, getListRows());
		this.searchObj.addSort("BankCode", false);
		this.searchObj.addField("BankCode");
		this.searchObj.addField("BankName");
		this.searchObj.addField("Active");
		this.searchObj.addField("RecordStatus");
		this.searchObj.addField("RecordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTBankDetail_View");
			if (isFirstTask()) {
				button_BankDetailList_NewBankDetail.setVisible(true);
			} else {
				button_BankDetailList_NewBankDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTBankDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_BankDetailList_NewBankDetail.setVisible(false);
			this.button_BankDetailList_BankDetailSearchDialog.setVisible(false);
			this.button_BankDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxBankDetail.setItemRenderer(new BankDetailListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("BankDetailList");
		this.button_BankDetailList_NewBankDetail.setVisible(getUserWorkspace()
				.isAllowed("button_BankDetailList_NewBankDetail"));
		this.button_BankDetailList_BankDetailSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_BankDetailList_BankDetailFindDialog"));
		this.button_BankDetailList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_BankDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.financeapplicationcode.model.
	 * BankDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onBankDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected BankDetail object
		final Listitem item = this.listBoxBankDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final BankDetail aBankDetail = (BankDetail) item.getAttribute("data");
			final BankDetail bankDetail = getBankDetailService()
			.getBankDetailById(aBankDetail.getId());

			if (bankDetail == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aBankDetail.getBankCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_BankCode")	+ ":" + aBankDetail.getBankCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND BankCode='" + bankDetail.getBankCode()
				+ "' AND version=" + bankDetail.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"BankDetail", whereCond,bankDetail.getTaskId(),	bankDetail.getNextTaskId());
					if (userAcces) {
						showDetailView(bankDetail);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(bankDetail);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the BankDetail dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BankDetailList_NewBankDetail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new BankDetail object, We GET it from the back end
		final BankDetail aBankDetail = getBankDetailService().getNewBankDetail();

		showDetailView(aBankDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param BankDetail
	 *            (aBankDetail)
	 * @throws Exception
	 */
	private void showDetailView(BankDetail aBankDetail) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aBankDetail.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aBankDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("bankDetail", aBankDetail);

		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the BankDetailListbox
		 * from the dialog when we do a delete, edit or insert a
		 * BankDetail.
		 */
		map.put("bankDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/BankDetail/BankDetailDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_BankDetailList);
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
		this.sortOperator_bankCode.setSelectedIndex(0);
		this.bankCode.setValue("");
		this.sortOperator_bankName.setSelectedIndex(0);
		this.bankName.setValue("");
		this.sortOperator_active.setSelectedIndex(0);
		this.active.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clears All the Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxBankDetail, this.pagingBankDetailList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the BankDetail dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BankDetailList_BankDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the bankDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_BankDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("BankDetail", getSearchObj(),this.pagingBankDetailList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.bankCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_bankCode.getSelectedItem(),this.bankCode.getValue(), "BankCode");
		}
		if (!StringUtils.trimToEmpty(this.bankName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_bankName.getSelectedItem(),this.bankName.getValue(), "BankName");
		}

		int intActive=0;
		if(this.active.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_active.getSelectedItem(),intActive, "Active");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_recordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxBankDetail,this.pagingBankDetailList);
		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}
	public BankDetailService getBankDetailService() {
		return this.bankDetailService;
	}

	public JdbcSearchObject<BankDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<BankDetail> searchObj) {
		this.searchObj = searchObj;
	}

}