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
 * FileName    		:  SecRightListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-07-2011    														*
 *                                                                  						*
 * Modified Date    :  03-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securityright.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
//Upgraded to ZK-6.5.1.1 Casted to SecurityRight
public class SecurityRightListModelItemRenderer implements ListitemRenderer<SecurityRight>, Serializable {

	private static final long serialVersionUID = -6278242513862235773L;

	//Upgraded to ZK-6.5.1.1 Changed the parameter type from object to securityRight	
	@Override
	public void render(Listitem item, SecurityRight securityRight, int count) throws Exception {
 
		//final SecurityRight securityRight = (SecurityRight) data;
		Listcell lc;
		lc = new Listcell(PennantAppUtil.getlabelDesc(String.valueOf(securityRight.getRightType()),
				PennantStaticListUtil.getRightType()));
		lc.setParent(item);
	  	lc = new Listcell(securityRight.getRightName());
		lc.setParent(item);
	  	lc = new Listcell(securityRight.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(securityRight.getRecordType()));
		lc.setParent(item);
		//Upgraded to ZK-6.5.1.1 Changed the object name from data to securityRight
		item.setAttribute("data", securityRight);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityRightItemDoubleClicked");
		
	}
}