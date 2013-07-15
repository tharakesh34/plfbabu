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
 * FileName    		:  ProductListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-08-2011    														*
 *                                                                  						*
 * Modified Date    :  12-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.bmtmasters.product;

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
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.service.bmtmasters.ProductService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.bmtmasters.product.model.ProductListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/Product/ProductList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ProductListCtrl extends GFCBaseListCtrl<Product> implements Serializable {

	private static final long serialVersionUID = -6951358943287040101L;
	private final static Logger logger = Logger.getLogger(ProductListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ProductList; 				// autoWired
	protected Borderlayout borderLayout_ProductList; 	// autoWired
	protected Paging pagingProductList; 				// autoWired
	protected Listbox listBoxProduct; 					// autoWired

	// List headers
	protected Listheader listheader_ProductCode; 		// autoWired
	protected Listheader listheader_ProductDesc;		// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 								 // autoWired
	protected Button button_ProductList_NewProduct; 		 // autoWired
	protected Button button_ProductList_ProductSearchDialog; // autoWired
	protected Button button_ProductList_PrintList; 			 // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Product> searchObj;

	private transient ProductService productService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public ProductListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Product object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProductList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Product");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Product");

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

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_ProductList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingProductList.setPageSize(getListRows());
		this.pagingProductList.setDetailed(true);

		this.listheader_ProductCode.setSortAscending(new FieldComparator("productCode", true));
		this.listheader_ProductCode.setSortDescending(new FieldComparator("productCode", false));
		this.listheader_ProductDesc.setSortAscending(new FieldComparator("productDesc", true));
		this.listheader_ProductDesc.setSortDescending(new FieldComparator("productDesc", false));

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
		this.searchObj = new JdbcSearchObject<Product>(Product.class,getListRows());
		this.searchObj.addSort("ProductCode", false);
		this.searchObj.addTabelName("BMTProduct_View");

		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_ProductList_NewProduct.setVisible(false);
			} else {
				button_ProductList_NewProduct.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_ProductList_NewProduct.setVisible(false);
			this.button_ProductList_ProductSearchDialog.setVisible(false);
			this.button_ProductList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxProduct,
					this.pagingProductList);
			// set the itemRenderer
			this.listBoxProduct.setItemRenderer(new ProductListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("ProductList");

		this.button_ProductList_NewProduct.setVisible(false);//getUserWorkspace().isAllowed("button_ProductList_NewProduct")
		this.button_ProductList_ProductSearchDialog.setVisible(getUserWorkspace().isAllowed("button_ProductList_ProductFindDialog"));
		this.button_ProductList_PrintList.setVisible(getUserWorkspace().isAllowed("button_ProductList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see:
	 * com.pennant.webui.bmtmasters.product.model.ProductListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onProductItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Product object
		final Listitem item = this.listBoxProduct.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Product aProduct = (Product) item.getAttribute("data");
			final Product product = getProductService().getProductById(aProduct.getId(), aProduct.getProductCode());

			if (product == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];
				valueParm[0] = aProduct.getProductCode();

				errParm[0] = PennantJavaUtil.getLabel("label_ProductCode")+ ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41005",errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());

			} else {

				String whereCond = " AND ProductCode='"+ product.getProductCode() +
				"' AND version="	+ product.getVersion() + " ";
				if (isWorkFlowEnabled()) {

					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(),"Product", whereCond, product.getTaskId(),product.getNextTaskId());
					if (userAcces) {
						showDetailView(product);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(product);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Product dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ProductList_NewProduct(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new Product object, We GET it from the backEnd.
		final Product aProduct = getProductService().getNewProduct();
		showDetailView(aProduct);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param Product
	 *            (aProduct)
	 * @throws Exception
	 */
	private void showDetailView(Product aProduct) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aProduct.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aProduct.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("product", aProduct);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the ProductListbox from the dialog
		 * when we do a delete, edit or insert a Product.
		 */
		map.put("productListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/Product/ProductDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
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
		PTMessageUtils.showHelpWindow(event, window_ProductList);
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
		this.pagingProductList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ProductList, event);
		this.window_ProductList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Product dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ProductList_ProductSearchDialog(Event event)
	throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our ProductDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Product. For handed over
		 * these parameter only a Map is accepted. So we put the Product object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("productCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/Product/ProductSearchDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the product print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_ProductList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Product", getSearchObj(),this.pagingProductList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
	public ProductService getProductService() {
		return this.productService;
	}

	public JdbcSearchObject<Product> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Product> searchObj) {
		this.searchObj = searchObj;
	}
}