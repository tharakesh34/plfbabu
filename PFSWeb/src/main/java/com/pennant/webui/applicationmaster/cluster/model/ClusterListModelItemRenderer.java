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
 * FileName    		:  ClusterListModelItemRenderer.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.cluster.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ClusterListModelItemRenderer implements ListitemRenderer<Cluster>, Serializable {

	private static final long serialVersionUID = 1L;

	public ClusterListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, Cluster cluster, int count) {

		Listcell lc;
		lc = new Listcell(cluster.getEntity() + " - " + cluster.getEntityDesc());
		lc.setParent(item);

		lc = new Listcell(cluster.getClusterType());
		lc.setParent(item);

		lc = new Listcell(cluster.getCode() + " - " + cluster.getName());
		lc.setParent(item);

		if (StringUtils.equals(cluster.getClusterType(), cluster.getParentType())) {

			lc = new Listcell(cluster.getClusterType());

		} else {
			lc = new Listcell(cluster.getParentCode() + " - " + cluster.getParentName());
		}
		lc.setParent(item);

		lc = new Listcell(cluster.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(cluster.getRecordType()));
		lc.setParent(item);

		item.setAttribute("clusterId", cluster.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onClusterItemDoubleClicked");
	}
}