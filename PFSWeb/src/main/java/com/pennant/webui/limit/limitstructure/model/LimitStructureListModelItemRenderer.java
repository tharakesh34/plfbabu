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
 * FileName    		:  LimitStructureListModelItemRenderer.java                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.limit.limitstructure.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class LimitStructureListModelItemRenderer implements ListitemRenderer<LimitStructure>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, LimitStructure limitStructure, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(limitStructure.getStructureCode());
		lc.setParent(item);
	  	lc = new Listcell(limitStructure.getStructureName());
		lc.setParent(item);
				
		lc = new Listcell();
		Checkbox ckActive= new Checkbox();
		ckActive.setChecked(limitStructure.isActive());
		ckActive.setDisabled(true);
		ckActive.setParent(lc);
		lc.setParent(item);
		
	  	lc = new Listcell(limitStructure.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(limitStructure.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", limitStructure.getStructureCode());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onLimitStructureItemDoubleClicked");
	}
}