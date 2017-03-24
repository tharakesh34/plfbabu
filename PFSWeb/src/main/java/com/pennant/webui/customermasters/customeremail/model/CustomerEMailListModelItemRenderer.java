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
 * FileName    		:  CustomerEMailListModelItemRenderer.java                                                   * 	  
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
package com.pennant.webui.customermasters.customeremail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerEMailListModelItemRenderer implements ListitemRenderer<CustomerEMail>, Serializable {
	private static final long serialVersionUID = -292319041377555951L;

	public CustomerEMailListModelItemRenderer() {
		//
	}
	
	@Override
	public void render(Listitem item, CustomerEMail customerEMail, int count) throws Exception {

		if (item instanceof Listgroup) { 
			item.appendChild(new Listcell(String.valueOf(customerEMail.getLovDescCustCIF()))); 
		}else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(5);
			item.appendChild(cell); 
		} else { 

			Listcell lc;
			lc = new Listcell(customerEMail.getLovDescCustCIF());
			lc.setParent(item);
			lc = new Listcell(customerEMail.getCustEMailTypeCode());
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.formateInt(customerEMail.getCustEMailPriority()));
			lc.setParent(item);
			lc = new Listcell(customerEMail.getCustEMail());
			lc.setParent(item);
			lc = new Listcell(customerEMail.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(customerEMail.getRecordType()));
			lc.setParent(item);
			
			item.setAttribute("id", customerEMail.getCustID());
			item.setAttribute("typeCode", customerEMail.getCustEMailTypeCode());
			
			ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerEMailItemDoubleClicked");
		}
	}
}