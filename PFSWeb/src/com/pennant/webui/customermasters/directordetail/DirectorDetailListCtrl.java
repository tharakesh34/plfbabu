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
 * FileName    		:  DirectorDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.directordetail;

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
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.directordetail.model.DirectorDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DirectorDetailListCtrl extends GFCBaseListCtrl<DirectorDetail> implements Serializable {

	private static final long serialVersionUID = -5634641691791820344L;
	private final static Logger logger = Logger.getLogger(DirectorDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_DirectorDetailList; // autowired
	protected Borderlayout borderLayout_DirectorDetailList; // autowired
	protected Paging pagingDirectorDetailList; // autowired
	protected Listbox listBoxDirectorDetail; // autowired

	// List headers
	protected Listheader listheader_CustCIF; // autowired
	protected Listheader listheader_FirstName; // autowired
	protected Listheader listheader_ShortName; // autowired
	protected Listheader listheader_CustGenderCode; // autowired
	protected Listheader listheader_CustSalutationCode; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// Search Fields
	protected Textbox custCIF; // autowired
	protected Listbox sortOperator_custCIF; // autowired
	protected Textbox firstName; // autowired
	protected Listbox sortOperator_firstName; // autowired
	protected Textbox shortName; // autowired
	protected Listbox sortOperator_shortName; // autowired
	protected Textbox custGenderCode; // autowired
	protected Listbox sortOperator_custGenderCode; // autowired
	protected Textbox custSalutationCode; // autowired
	protected Listbox sortOperator_custSalutationCode; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType; // autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired

	protected Label label_DirectorDetailSearch_RecordStatus; // autowired
	protected Label label_DirectorDetailSearch_RecordType; // autowired
	protected Label label_DirectorDetailSearchResult; // autowired

	protected Grid searchGrid; // autowired
	protected Textbox moduleType; // autowired
	protected Radio fromApproved;
	protected Radio fromWorkFlow;
	protected Row workFlowFrom;

	private transient boolean approvedList = false;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_DirectorDetailList_NewDirectorDetail; // autowired
	protected Button button_DirectorDetailList_DirectorDetailSearchDialog; // autowired
	protected Button button_DirectorDetailList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<DirectorDetail> searchObj;
	private transient DirectorDetailService directorDetailService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public DirectorDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected DirectorDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DirectorDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("DirectorDetail");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("DirectorDetail");

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

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_custCIF.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custCIF.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_firstName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_firstName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_shortName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_shortName.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custGenderCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custGenderCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custSalutationCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custSalutationCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);

		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.label_DirectorDetailSearch_RecordStatus.setVisible(false);
			this.label_DirectorDetailSearch_RecordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		this.borderLayout_DirectorDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxDirectorDetail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingDirectorDetailList.setPageSize(getListRows());
		this.pagingDirectorDetailList.setDetailed(true);

		this.listheader_CustCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		this.listheader_FirstName.setSortAscending(new FieldComparator("firstName", true));
		this.listheader_FirstName.setSortDescending(new FieldComparator("firstName", false));
		this.listheader_ShortName.setSortAscending(new FieldComparator("shortName", true));
		this.listheader_ShortName.setSortDescending(new FieldComparator("shortName", false));
		this.listheader_CustGenderCode.setSortAscending(new FieldComparator("custGenderCode", true));
		this.listheader_CustGenderCode.setSortDescending(new FieldComparator("custGenderCode", false));
		this.listheader_CustSalutationCode.setSortAscending(new FieldComparator("custSalutationCode",true));
		this.listheader_CustSalutationCode.setSortDescending(new FieldComparator("custSalutationCode",false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// set the itemRenderer
		this.listBoxDirectorDetail.setItemRenderer(new DirectorDetailListModelItemRenderer());

		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_DirectorDetailList_NewDirectorDetail.setVisible(false);
			this.button_DirectorDetailList_DirectorDetailSearchDialog.setVisible(false);
			this.button_DirectorDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
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
		getUserWorkspace().alocateAuthorities("DirectorDetailList");

		this.button_DirectorDetailList_NewDirectorDetail.setVisible(getUserWorkspace()
				.isAllowed("button_DirectorDetailList_NewDirectorDetail"));
		this.button_DirectorDetailList_DirectorDetailSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_DirectorDetailList_DirectorDetailFindDialog"));
		this.button_DirectorDetailList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_DirectorDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.directordetail.model.
	 * DirectorDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDirectorDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected DirectorDetail object
		final Listitem item = this.listBoxDirectorDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DirectorDetail aDirectorDetail = (DirectorDetail) item.getAttribute("data");
			final DirectorDetail directorDetail = getDirectorDetailService().getDirectorDetailById(aDirectorDetail.getDirectorId(),aDirectorDetail.getCustID());

			if (directorDetail == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(aDirectorDetail.getId());
				errParm[0] = PennantJavaUtil.getLabel("label_DirectorId") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				if (isWorkFlowEnabled()) {
					String whereCond = " AND DirectorId="+ directorDetail.getDirectorId() + " AND version="
							+ directorDetail.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"DirectorDetail", whereCond,directorDetail.getTaskId(),directorDetail.getNextTaskId());
					if (userAcces) {
						showDetailView(directorDetail);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(directorDetail);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the DirectorDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_DirectorDetailList_NewDirectorDetail(Event event)
			throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new DirectorDetail object, We GET it from the backEnd.
		final DirectorDetail aDirectorDetail = getDirectorDetailService().getNewDirectorDetail();
		showDetailView(aDirectorDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param DirectorDetail
	 *            (aDirectorDetail)
	 * @throws Exception
	 */
	private void showDetailView(DirectorDetail aDirectorDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aDirectorDetail.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aDirectorDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("directorDetail", aDirectorDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the DirectorDetailListbox from the
		 * dialog when we do a delete, edit or insert a DirectorDetail.
		 */
		map.put("directorDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailDialog.zul",
					null, map);
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
		PTMessageUtils.showHelpWindow(event, window_DirectorDetailList);
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
		logger.debug(event.toString());

		this.sortOperator_custCIF.setSelectedIndex(0);
		this.custCIF.setValue("");
		this.sortOperator_firstName.setSelectedIndex(0);
		this.firstName.setValue("");
		this.sortOperator_shortName.setSelectedIndex(0);
		this.shortName.setValue("");
		this.sortOperator_custSalutationCode.setSelectedIndex(0);
		this.custSalutationCode.setValue("");
		this.sortOperator_custGenderCode.setSelectedIndex(0);
		this.custGenderCode.setValue("");

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();
		logger.debug("Leaving");
	}

	/**
	 * Method for calling the DirectorDetail dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_DirectorDetailList_DirectorDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the directorDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_DirectorDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("DirectorDetail",
				getSearchObj(), this.pagingDirectorDetailList.getTotalSize() + 1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch() {
		logger.debug("Entering");

		this.searchObj = new JdbcSearchObject<DirectorDetail>(DirectorDetail.class, getListRows());
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addSort("lovDescCustCIF", false);
		this.searchObj.addTabelName("CustomerDirectorDetail_View");

		// Workflow
		if (isWorkFlowEnabled()) {
			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("CustomerDirectorDetail_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("CustomerDirectorDetail_AView");
		}

		// Customer CIF
		if (!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custCIF.getSelectedItem(),this.custCIF.getValue(), "lovDescCustCIF");
		}
		// Customer First Name
		if (!StringUtils.trimToEmpty(this.firstName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_firstName.getSelectedItem(),this.firstName.getValue(), "firstName");
		}
		// Customer Short Name
		if (!StringUtils.trimToEmpty(this.shortName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_shortName.getSelectedItem(),this.shortName.getValue(), "shortName");
		}
		// Customer GenderCode
		if (!StringUtils.trimToEmpty(this.custGenderCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custGenderCode.getSelectedItem(),this.custGenderCode.getValue(), "custGenderCode");
		}
		// Customer GenderCode
		if (!StringUtils.trimToEmpty(this.custSalutationCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custSalutationCode.getSelectedItem(),this.custSalutationCode.getValue(), "custSalutationCode");
		}

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),"RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxDirectorDetail,this.pagingDirectorDetailList);

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDirectorDetailService(
			DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}
	public DirectorDetailService getDirectorDetailService() {
		return this.directorDetailService;
	}

	public JdbcSearchObject<DirectorDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<DirectorDetail> searchObj) {
		this.searchObj = searchObj;
	}

}