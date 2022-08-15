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
 * FileName    		:  ClusterHierarcheyListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-11-2018    														*
 *                                                                  						*
 * Modified Date    :  21-11-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-11-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.clusterhierarchey.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ClusterHierarcheyListModelItemRenderer implements ListitemRenderer<ClusterHierarchy>, Serializable {

	private static final long serialVersionUID = 1L;

	public ClusterHierarcheyListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, ClusterHierarchy clusterHierarchey, int count) {

		Listcell lc;
		lc = new Listcell(clusterHierarchey.getEntity());
		lc.setParent(item);
		lc = new Listcell(clusterHierarchey.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(clusterHierarchey.getRecordType()));
		lc.setParent(item);
		item.setAttribute("entity", clusterHierarchey.getEntity());
		item.setAttribute("clusterType", clusterHierarchey.getClusterType());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onClusterHierarcheyItemDoubleClicked");
	}
}