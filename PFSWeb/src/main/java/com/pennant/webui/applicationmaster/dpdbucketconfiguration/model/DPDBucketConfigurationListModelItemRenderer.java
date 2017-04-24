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
 * FileName    		:  DPDBucketConfigurationListModelItemRenderer.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.dpdbucketconfiguration.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class DPDBucketConfigurationListModelItemRenderer implements ListitemRenderer<DPDBucketConfiguration>, Serializable {

	private static final long serialVersionUID = 1L;

	public DPDBucketConfigurationListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, DPDBucketConfiguration dPDBucketConfiguration, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(dPDBucketConfiguration.getProductCode());
		lc.setParent(item);
	  	lc = new Listcell(dPDBucketConfiguration.getBucketCode());
		lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.formateInt(dPDBucketConfiguration.getDueDays()));
	  	lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbSuspendProfit = new Checkbox();
		cbSuspendProfit.setDisabled(true);
		cbSuspendProfit.setChecked(dPDBucketConfiguration.isSuspendProfit());
		lc.appendChild(cbSuspendProfit);
		lc.setParent(item);
	  	lc = new Listcell(dPDBucketConfiguration.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(dPDBucketConfiguration.getRecordType()));
		lc.setParent(item);
		item.setAttribute("configID", dPDBucketConfiguration.getConfigID());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDPDBucketConfigurationItemDoubleClicked");
	}
}