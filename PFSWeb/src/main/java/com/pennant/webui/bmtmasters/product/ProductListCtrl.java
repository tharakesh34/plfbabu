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

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.service.bmtmasters.ProductService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.webui.bmtmasters.product.model.ProductListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/Product/ProductList.zul file.
 */
public class ProductListCtrl extends GFCBaseListCtrl<Product> {
	private static final long serialVersionUID = -6951358943287040101L;
	private static final Logger logger = Logger.getLogger(ProductListCtrl.class);

	protected Window window_ProductList;
	protected Borderlayout borderLayout_ProductList;
	protected Paging pagingProductList;
	protected Listbox listBoxProduct;

	protected Listheader listheader_ProductCode;
	protected Listheader listheader_ProductDesc;

	protected Button button_ProductList_NewProduct;
	protected Button button_ProductList_ProductSearchDialog;

	protected Textbox productCode;
	protected Textbox productDesc;
	protected Listbox sortOperator_productDesc;
	protected Listbox sortOperator_productCode;

	private transient ProductService productService;

	/**
	 * default constructor.<br>
	 */
	public ProductListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Product";
		super.pageRightName = "ProductList";
		super.tableName = "BMTProduct_AView";
		super.queueTableName = "BMTProduct_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		//FIXME: Changed by Pradeep. Not sure about the previous condition correctness. Remove the comment after testing.
		if (ImplementationConstants.IMPLEMENTATION_CONVENTIONAL) {
			Filter[] filters = new Filter[3];
			filters[0] = new Filter("ProductCategory", FinanceConstants.PRODUCT_CONVENTIONAL, Filter.OP_EQUAL);
			filters[1] = new Filter("ProductCategory", FinanceConstants.PRODUCT_DISCOUNT, Filter.OP_EQUAL);
			filters[2] = new Filter("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY, Filter.OP_EQUAL);
			
			this.searchObject.addFilterOr(filters);
		}else{
			this.searchObject.addFilterNotEqual("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY);
			this.searchObject.addFilterNotEqual("ProductCategory", FinanceConstants.PRODUCT_CONVENTIONAL);
		}
	}
	
	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ProductList(Event event) {
		// Set the page level components.
		setPageComponents(window_ProductList, borderLayout_ProductList, listBoxProduct, pagingProductList);
		setItemRender(new ProductListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ProductList_NewProduct, "button_ProductList_NewProduct", true);
		registerButton(button_ProductList_ProductSearchDialog);

		registerField("productCode", listheader_ProductCode, SortOrder.ASC, productCode, sortOperator_productCode,
				Operators.STRING);
		registerField("productDesc", listheader_ProductDesc, SortOrder.NONE, productDesc, sortOperator_productDesc,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ProductList_ProductSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onProductItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxProduct.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		String code = (String) selectedItem.getAttribute("code");

		Product product = productService.getProductById(id, code);

		if (product == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ProductCode='" + product.getProductCode() + "' AND version=" + product.getVersion()
				+ " ";

		if (doCheckAuthority(product, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && product.getWorkflowId() == 0) {
				product.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(product);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Call the Product dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ProductList_NewProduct(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Product aProduct = new Product();
		aProduct.setNewRecord(true);
		aProduct.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aProduct);

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aProduct
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Product aProduct) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("product", aProduct);
		arg.put("productListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/Product/ProductDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
}