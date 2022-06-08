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
 * FileName    		:  SecurityOperationListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014    														*
 *                                                                  						*
 * Modified Date    :  10-03-2014
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014       Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.securityoperation.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class SecurityOperationListModelItemRenderer implements ListitemRenderer<SecurityOperation>, Serializable {

	private static final long serialVersionUID = -3424682216721299542L;

	@Override
	public void render(Listitem item, SecurityOperation data, int count) {

		final SecurityOperation securityOperation = (SecurityOperation) data;
		Listcell lc;
		lc = new Listcell(securityOperation.getOprCode());
		lc.setParent(item);
		lc = new Listcell(securityOperation.getOprDesc());
		lc.setParent(item);
		lc = new Listcell(securityOperation.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(securityOperation.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", securityOperation.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityOperationItemDoubleClicked");

	}
}
