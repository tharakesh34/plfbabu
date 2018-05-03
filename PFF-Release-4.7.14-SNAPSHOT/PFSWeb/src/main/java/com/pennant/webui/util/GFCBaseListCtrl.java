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
 *
 * FileName    		:  GFCBAseListCtl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.util;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.framework.web.AbstractListController;
import com.pennanttech.pennapps.jdbc.search.Filter;

/**
 * Extended the GFCBase controller for a pagedListWrapper for a single type.
 * 
 */
public class GFCBaseListCtrl<T> extends AbstractListController<T> {
	private static final long serialVersionUID = -3741197830243792411L;

	protected GFCBaseListCtrl() {
		super();
	}

	/**
	 * FIXME to be changed to ExtendedCombobox
	 * 
	 * @param searchOperator
	 * @param searchField
	 * @param module
	 */
	protected void setSearchValue(Listbox searchOperator, Textbox searchField, String module) {
		Listitem item = searchOperator.getSelectedItem();
		int searchOpId = Integer.parseInt(((ValueLabel) item.getAttribute("data")).getValue());

		if (searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(window, module, searchField.getValue(),
					new Filter[] {});
			if (selectedValues != null) {
				searchField.setValue(selectedValues);
			}

		} else {
			Object dataObject = ExtendedSearchListBox.show(window, module);
			if (dataObject instanceof String) {
				searchField.setValue("");
			} else {
				if ("Currency".equals(module)) {
					Currency details = (Currency) dataObject;
					if (details != null) {
						searchField.setValue(details.getCcyCode());
					}
				} else if ("FinanceFlag".equals(module)) {
					FinanceFlag details = (FinanceFlag) dataObject;
					if (details != null) {
						searchField.setValue(details.getFinReference());
					}
				} else if ("Branch".equals(module)) {
					Branch details = (Branch) dataObject;
					if (details != null) {
						searchField.setValue(details.getBranchCode());
					}
				} else if ("FinanceType".equals(module)) {
					FinanceType details = (FinanceType) dataObject;
					if (details != null) {
						searchField.setValue(details.getFinType());
					}
				} else if ("Customer".equals(module)) {
					Customer details = (Customer) dataObject;
					if (details != null) {
						searchField.setValue(details.getCustCIF());
					}
				} else if ("ScheduleMethod".equals(module)) {
					ScheduleMethod details = (ScheduleMethod) dataObject;
					if (details != null) {
						searchField.setValue(details.getSchdMethod());
					}
				} else if ("InterestRateBasisCode".equals(module)) {
					InterestRateBasisCode details = (InterestRateBasisCode) dataObject;
					if (details != null) {
						searchField.setValue(details.getIntRateBasisCode());
					}
				} else if ("SecurityRoleEnq".equals(module)) {
					SecurityRole details = (SecurityRole) dataObject;
					if (details != null) {
						searchField.setValue(details.getRoleDesc());
					}

				} else if ("DataEngine".equals(module)) {
					BankBranch details = (BankBranch) dataObject;
					if (details != null){
					searchField.setValue(details.getBranchCode());
					}
				}
			}
		}
	}
}
