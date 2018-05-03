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
 * FileName    		:  ProfitCenterListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.profitcenter.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ProfitCenterListModelItemRenderer implements ListitemRenderer<ProfitCenter>, Serializable {

	private static final long serialVersionUID = 1L;

	public ProfitCenterListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, ProfitCenter profitCenter, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(profitCenter.getProfitCenterCode());
		lc.setParent(item);
	  	lc = new Listcell(profitCenter.getProfitCenterDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(profitCenter.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
	  	lc = new Listcell(profitCenter.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(profitCenter.getRecordType()));
		lc.setParent(item);
		item.setAttribute("profitCenterID", profitCenter.getProfitCenterID());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onProfitCenterItemDoubleClicked");
	}
}