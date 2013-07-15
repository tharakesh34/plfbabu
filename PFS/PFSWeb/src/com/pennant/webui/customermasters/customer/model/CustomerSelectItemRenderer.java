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
 * FileName    		:  CustomerListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customer.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.Customer;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerSelectItemRenderer implements ListitemRenderer<Customer>, Serializable {

	private static final long serialVersionUID = 1552059797117039294L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, Customer customer, int count) throws Exception {

		//final Customer customer = (Customer) data;
		Listcell lc;
	  	lc = new Listcell(customer.getCustCIF());
		lc.setParent(item);
	  	lc = new Listcell(customer.getCustCoreBank());
		lc.setParent(item);
	  	lc = new Listcell(customer.getCustCtgCode()+"-"+customer.getLovDescCustCtgCodeName());
		lc.setParent(item);
	  	lc = new Listcell(customer.getCustFName());
		lc.setParent(item);
		lc = new Listcell(customer.getCustLName());
		lc.setParent(item);
		item.setAttribute("data", customer);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerItemDoubleClicked");
	}
}