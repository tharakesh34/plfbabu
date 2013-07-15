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

package com.pennant.webui.coremasters.collateralassignment.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.coremasters.CollateralAssignment;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CollateralAssignmentListModelItemRenderer implements ListitemRenderer<CollateralAssignment>, Serializable {

	private static final long serialVersionUID = 2274326782681085785L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, CollateralAssignment assignment, int count) throws Exception {

		//final CollateralAssignment assignment = (CollateralAssignment) data;
		Listcell lc;
		lc = new Listcell(assignment.getDealType());
		lc.setParent(item);
		lc = new Listcell(assignment.getReference());
		lc.setParent(item);
		lc = new Listcell(assignment.getBranch());
		lc.setParent(item);
	  	lc = new Listcell(assignment.getAccNumber());
		lc.setParent(item);
	  	lc = new Listcell(assignment.getCommitRef());
		lc.setParent(item);
		item.setAttribute("data", assignment);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCollateralAssignmentItemDoubleClicked");
	}
}