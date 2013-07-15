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
 * FileName    		:  CustomerPRelationListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerPRelationListModelItemRenderer implements ListitemRenderer<CustomerPRelation>, Serializable {

	private static final long serialVersionUID = 1537997798690146761L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, CustomerPRelation customerPRelation, int count) throws Exception {

		//final CustomerPRelation customerPRelation = (CustomerPRelation) data;
		Listcell lc;
		lc = new Listcell(customerPRelation.getPRRelationCode());
		lc.setParent(item);
	  	lc = new Listcell(customerPRelation.getPRRelationCustID());
	  	lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerPRelation.getPRSName()));
		lc.setParent(item);
		lc = new Listcell(customerPRelation.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerPRelation.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", customerPRelation);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerPRelationItemDoubleClicked");
	}
}