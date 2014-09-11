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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  CustomerAddressListModelItemRenderer.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  22-04-2011    
 *                                                                  
 * Modified Date    :  22-04-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2011       Pennant	                 0.1                                         * 
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

import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerAddressListModelItemRenderer implements ListitemRenderer<CustomerAddres>, Serializable {

	private static final long serialVersionUID = -5263188939752215836L;

	@Override
	public void render(Listitem item, CustomerAddres customerAddress, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(customerAddress.getLovDescCustAddrTypeName());
		lc.setParent(item);
		lc = new Listcell(customerAddress.getLovDescCustAddrCountryName());
		lc.setParent(item);
		lc = new Listcell(customerAddress.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerAddress.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", customerAddress);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerAddressItemDoubleClicked");
	}
}