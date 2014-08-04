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
 * FileName    		:  CustomerGroupListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customergroup.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerGroupListModelItemRenderer implements ListitemRenderer<CustomerGroup>, Serializable {

	private static final long serialVersionUID = -4657544182143939881L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, CustomerGroup customerGroup, int count) throws Exception {

		//final CustomerGroup customerGroup = (CustomerGroup) data;
		Listcell lc;
	  	lc = new Listcell(customerGroup.getCustGrpCode());
		lc.setParent(item);
	  	lc = new Listcell(customerGroup.getCustGrpDesc());
		lc.setParent(item);
		
		String GrpRelation  = "";
		if(!StringUtils.trimToEmpty(customerGroup.getCustGrpRO1()).equals("")){
			if(!StringUtils.trimToEmpty(customerGroup.getLovDescCustGrpRO1Name()).equals("")){
				GrpRelation = customerGroup.getCustGrpRO1()+PennantConstants.KEY_SEPERATOR+customerGroup.getLovDescCustGrpRO1Name();
			}else{
				GrpRelation = customerGroup.getCustGrpRO1();
			}
		}
		lc = new Listcell(GrpRelation);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbCustGrpIsActive = new Checkbox();
		cbCustGrpIsActive.setDisabled(true);
		cbCustGrpIsActive.setChecked(customerGroup.isCustGrpIsActive());
		lc.appendChild(cbCustGrpIsActive);
		lc.setParent(item);
	  	lc = new Listcell(customerGroup.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerGroup.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", customerGroup);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerGroupItemDoubleClicked");
	}
}