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
 * FileName    		:  ReligionListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-01-2018    														*
 *                                                                  						*
 * Modified Date    :  24-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-01-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.religion.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Religion;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ReligionListModelItemRenderer implements ListitemRenderer<Religion>, Serializable {

	private static final long serialVersionUID = 1L;

	public ReligionListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, Religion religion, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(religion.getReligionCode());
		lc.setParent(item);
	  	lc = new Listcell(religion.getReligionDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(religion.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
	  	lc = new Listcell(religion.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(religion.getRecordType()));
		lc.setParent(item);
		item.setAttribute("religionId", religion.getReligionId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onReligionItemDoubleClicked");
	}
}