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
 * FileName    		:  CustStatusCodeListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.customerstatuscode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class CustomerStatusCodeListModelItemRenderer implements ListitemRenderer<CustomerStatusCode>, Serializable {

	private static final long serialVersionUID = -2369417870213221054L;

	public CustomerStatusCodeListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, CustomerStatusCode customerStatusCode, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(customerStatusCode.getCustStsCode());
		lc.setParent(item);
		lc = new Listcell(customerStatusCode.getCustStsDescription());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbCustStsIsActive = new Checkbox();
		cbCustStsIsActive.setDisabled(true);
		cbCustStsIsActive.setChecked(customerStatusCode.isCustStsIsActive());
		lc.appendChild(cbCustStsIsActive);
		lc.setParent(item);
		lc = new Listcell(customerStatusCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerStatusCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", customerStatusCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerStatusCodeItemDoubleClicked");
	}
}