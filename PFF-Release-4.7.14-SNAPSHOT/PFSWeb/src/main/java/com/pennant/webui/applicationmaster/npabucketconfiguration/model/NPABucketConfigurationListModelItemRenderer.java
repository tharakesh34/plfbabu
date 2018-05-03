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
 * FileName    		:  NPABucketConfigurationListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.npabucketconfiguration.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class NPABucketConfigurationListModelItemRenderer implements ListitemRenderer<NPABucketConfiguration>, Serializable {

	private static final long serialVersionUID = 1L;

	public NPABucketConfigurationListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, NPABucketConfiguration nPABucketConfiguration, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(nPABucketConfiguration.getProductCode());
		lc.setParent(item);
	  	lc = new Listcell(nPABucketConfiguration.getBucketCode());
		lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.formateInt(nPABucketConfiguration.getDueDays()));
	  	lc.setParent(item);
	  	lc = new Listcell(nPABucketConfiguration.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(nPABucketConfiguration.getRecordType()));
		lc.setParent(item);
		item.setAttribute("configID", nPABucketConfiguration.getConfigID());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onNPABucketConfigurationItemDoubleClicked");
	}
}