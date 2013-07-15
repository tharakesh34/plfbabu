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
 * FileName    		:  RepaymentMethodListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.repaymentmethod.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.staticparms.RepaymentMethod;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class RepaymentMethodListModelItemRenderer implements ListitemRenderer<RepaymentMethod>, Serializable {

	private static final long serialVersionUID = -2719576264511696189L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, RepaymentMethod repaymentMethod, int count) throws Exception {

		//final RepaymentMethod repaymentMethod = (RepaymentMethod) data;
		Listcell lc;
	  	lc = new Listcell(repaymentMethod.getRepayMethod());
		lc.setParent(item);
	  	lc = new Listcell(repaymentMethod.getRepayMethodDesc());
		lc.setParent(item);
	  	lc = new Listcell(repaymentMethod.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(repaymentMethod.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", repaymentMethod);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onRepaymentMethodItemDoubleClicked");
	}
}