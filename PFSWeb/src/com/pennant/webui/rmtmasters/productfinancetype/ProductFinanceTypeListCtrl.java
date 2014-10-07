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
 * FileName    		:  ProductFinanceTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-08-2011    														*
 *                                                                  						*
 * Modified Date    :  13-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.productfinancetype;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.ProductFinanceType;
import com.pennant.backend.service.rmtmasters.ProductFinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.rmtmasters.productfinancetype.model.ProductFinanceTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/ProductFinanceType/ProductFinanceTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ProductFinanceTypeListCtrl extends
GFCBaseListCtrl<ProductFinanceType> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger
	.getLogger(ProductFinanceTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ProductFinanceTypeList; // auto wired
	protected Borderlayout borderLayout_ProductFinanceTypeList; // auto wired
	protected Paging pagingProductFinanceTypeList; // auto wired
	protected Listbox listBoxProductFinanceType; // auto wired

	// List headers
	protected Listheader listheader_ProductCode; // auto wired
	protected Listheader listheader_FinType; // auto wired
	protected Listheader listheader_RecordStatus; // auto wired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // auto wired
	protected Button button_ProductFinanceTypeList_NewProductFinanceType; // auto wired
	protected Button button_ProductFinanceTypeList_ProductFinanceTypeSearchDialog; // auto wired
	protected Button button_ProductFinanceTypeList_PrintList; // auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ProductFinanceType> searchObj;

	private transient ProductFinanceTypeService productFinanceTypeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public ProductFinanceTypeListCtrl() {
		super();
	}

	public void onCreate$window_ProductFinanceTypeList(Event event)
	throws Exception {
		logger.debug("Enterring");

		ModuleMapping moduleMapping = PennantJavaUtil
		.getModuleMap("ProductFinanceType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil
			.getWorkFlowDetails("ProductFinanceType");

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

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_ProductFinanceTypeList
		.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingProductFinanceTypeList.setPageSize(getListRows());
		this.pagingProductFinanceTypeList.setDetailed(true);

		this.listheader_ProductCode.setSortAscending(new FieldComparator(
				"productCode", true));
		this.listheader_ProductCode.setSortDescending(new FieldComparator(
				"productCode", false));
		this.listheader_FinType.setSortAscending(new FieldComparator("finType",
				true));
		this.listheader_FinType.setSortDescending(new FieldComparator(
				"finType", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator(
					"recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator(
					"recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator(
					"recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator(
					"recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<ProductFinanceType>(
				ProductFinanceType.class, getListRows());
		this.searchObj.addSort("PrdFinId", false);

		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTProductFinanceTypes_View");
			if (isFirstTask()) {
				button_ProductFinanceTypeList_NewProductFinanceType
				.setVisible(true);
			} else {
				button_ProductFinanceTypeList_NewProductFinanceType
				.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace()
					.getUserRoles(), isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTProductFinanceTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_ProductFinanceTypeList_NewProductFinanceType
			.setVisible(false);
			this.button_ProductFinanceTypeList_ProductFinanceTypeSearchDialog
			.setVisible(false);
			this.button_ProductFinanceTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil
					.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxProductFinanceType,
					this.pagingProductFinanceTypeList);
			// set the itemRenderer
			this.listBoxProductFinanceType
			.setItemRenderer(new ProductFinanceTypeListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Enterring");
		getUserWorkspace().alocateAuthorities("ProductFinanceTypeList");

		this.button_ProductFinanceTypeList_NewProductFinanceType
		.setVisible(getUserWorkspace().isAllowed(
				"button_ProductFinanceTypeList_NewProductFinanceType"));
		this.button_ProductFinanceTypeList_ProductFinanceTypeSearchDialog
		.setVisible(getUserWorkspace()
				.isAllowed(
						"button_ProductFinanceTypeList_ProductFinanceTypeFindDialog"));
		this.button_ProductFinanceTypeList_PrintList
		.setVisible(getUserWorkspace().isAllowed(
				"button_ProductFinanceTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.productfinancetype.model.
	 * ProductFinanceTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onProductFinanceTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected ProductFinanceType object
		final Listitem item = this.listBoxProductFinanceType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ProductFinanceType aProductFinanceType = (ProductFinanceType) item
					.getAttribute("data");
			final ProductFinanceType productFinanceType = getProductFinanceTypeService()
					.getProductFinanceTypeById(aProductFinanceType.getId());

			if (productFinanceType == null) {
				String[] valueParm = new String[1];
				String[] errParm= new String[1];
				
				valueParm[0] = String.valueOf(aProductFinanceType.getPrdFinId());
				errParm[0] = PennantJavaUtil.getLabel("label_PrdFinId")+ ":"+ valueParm[0];
				
				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				if (isWorkFlowEnabled()) {
					String whereCond = " AND PrdFinId="
						+ productFinanceType.getPrdFinId()
						+ " AND version=" + productFinanceType.getVersion()
						+ " ";

					boolean userAcces = validateUserAccess(
							workFlowDetails.getId(), getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(),
							"ProductFinanceType", whereCond,
							productFinanceType.getTaskId(),
							productFinanceType.getNextTaskId());
					if (userAcces) {
						showDetailView(productFinanceType);
					} else {
						PTMessageUtils.showErrorMessage(Labels
								.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(productFinanceType);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the ProductFinanceType dialog with a new empty entry. <br>
	 */
	public void onClick$button_ProductFinanceTypeList_NewProductFinanceType(
			Event event) throws Exception {
		logger.debug(event.toString());
		// create a new ProductFinanceType object, We GET it from the backEnd.
		final ProductFinanceType aProductFinanceType = getProductFinanceTypeService()
					.getNewProductFinanceType();
		showDetailView(aProductFinanceType);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param ProductFinanceType
	 *            (aProductFinanceType)
	 * @throws Exception
	 */
	private void showDetailView(ProductFinanceType aProductFinanceType)
	throws Exception {
		logger.debug("Enterring");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aProductFinanceType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aProductFinanceType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("productFinanceType", aProductFinanceType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the ProductFinanceTypeListbox from
		 * the dialog when we do a delete, edit or insert a ProductFinanceType.
		 */
		map.put("productFinanceTypeListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/ProductFinanceType/ProductFinanceTypeDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_ProductFinanceTypeList);
		logger.debug("Leaving");
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
		this.pagingProductFinanceTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ProductFinanceTypeList, event);
		this.window_ProductFinanceTypeList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the ProductFinanceType dialog
	 */

	public void onClick$button_ProductFinanceTypeList_ProductFinanceTypeSearchDialog(
			Event event) throws Exception {
		logger.debug("Enterring");
		logger.debug(event.toString());
		/*
		 * we can call our ProductFinanceTypeDialog zul-file with parameters. So
		 * we can call them with a object of the selected ProductFinanceType.
		 * For handed over these parameter only a Map is accepted. So we put the
		 * ProductFinanceType object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("productFinanceTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/ProductFinanceType/ProductFinanceTypeSearchDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the productFinanceType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ProductFinanceTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Enterring");
		logger.debug(event.toString());
		new PTListReportUtils("ProductFinanceType", getSearchObj(),this.pagingProductFinanceTypeList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setProductFinanceTypeService(
			ProductFinanceTypeService productFinanceTypeService) {
		this.productFinanceTypeService = productFinanceTypeService;
	}
	public ProductFinanceTypeService getProductFinanceTypeService() {
		return this.productFinanceTypeService;
	}

	public JdbcSearchObject<ProductFinanceType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ProductFinanceType> searchObj) {
		this.searchObj = searchObj;
	}
}