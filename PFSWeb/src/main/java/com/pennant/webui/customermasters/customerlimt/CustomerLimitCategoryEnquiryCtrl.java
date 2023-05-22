/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : AccountingSetDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2011 * *
 * Modified Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customerlimt;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.customermasters.CustLimitCategoryBreakdown;
import com.pennant.backend.model.customermasters.CustomerLimit;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerLimit/CustomerLimitEnquiry.zul file.
 */
public class CustomerLimitCategoryEnquiryCtrl extends GFCBaseCtrl<CustLimitCategoryBreakdown> {
	private static final long serialVersionUID = 8602015982512929710L;
	private static final Logger logger = LogManager.getLogger(CustomerLimitCategoryEnquiryCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerLimitCategoryEnquiry;

	protected Label custCIF;
	protected Label custShortName;
	protected Label country;
	protected Label groupName;
	protected Label limitCategory;

	protected Listbox listBoxCustomerLimit;
	protected Grid grid_enquiryDetails;

	private CustomerLimit customerLimit;

	/**
	 * default constructor.<br>
	 */
	public CustomerLimitCategoryEnquiryCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected AccountingSet object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_AcademicDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerLimitCategoryEnquiry);

		if (arguments.containsKey("customerLimit")) {
			setCustomerLimit((CustomerLimit) arguments.get("customerLimit"));
		} else {
			setCustomerLimit(null);
		}

		getBorderLayoutHeight();
		int dialogHeight = grid_enquiryDetails.getRows().getVisibleItemCount() * 20 + 400;
		int listboxHeight = borderLayoutHeight - dialogHeight;
		listBoxCustomerLimit.setHeight(listboxHeight + "px");

		// set Field Properties
		doShowDialog(getCustomerLimit());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_CustomerLimitCategoryEnquiry);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccountingSet (AccountingSet)
	 */
	public void doWriteBeanToComponents(CustomerLimit customerLimit) {
		logger.debug("Entering");

		this.custCIF.setValue(customerLimit.getCustCIF());
		this.custShortName.setValue(customerLimit.getCustShortName());
		this.country.setValue("");
		this.groupName.setValue("");
		this.limitCategory.setValue(customerLimit.getLimitCategory());

		doFilllistbox(null);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerLimit
	 */
	public void doShowDialog(CustomerLimit aCustomerLimit) {
		logger.debug("Entering");

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerLimit);

			// stores the initial data for comparing if they are changed
			// during user action.
			this.window_CustomerLimitCategoryEnquiry.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param custlimitCategoryList
	 */
	public void doFilllistbox(List<CustLimitCategoryBreakdown> categoryBreakdowns) {
		logger.debug("Entering");

		if (categoryBreakdowns != null) {

			Listitem item = null;
			Listcell lc = null;
			for (CustLimitCategoryBreakdown breakdown : categoryBreakdowns) {

				item = new Listitem();
				lc = new Listcell(breakdown.getAccountNum());
				lc.setParent(item);
				lc = new Listcell(breakdown.getCurrency());
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(breakdown.getEquivalent(), 3));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(
						breakdown.getEndDate() == null ? "" : DateUtil.formatToLongDate(breakdown.getEndDate()));
				lc.setParent(item);
				lc = new Listcell(breakdown.getType());
				lc.setParent(item);
				lc = new Listcell(breakdown.getComments());
				lc.setParent(item);
				this.listBoxCustomerLimit.appendChild(item);
			}

		}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerLimit getCustomerLimit() {
		return customerLimit;
	}

	public void setCustomerLimit(CustomerLimit customerLimit) {
		this.customerLimit = customerLimit;
	}

}
