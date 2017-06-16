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
 * FileName    		:  EntityListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-06-2017    														*
 *                                                                  						*
 * Modified Date    :  15-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.entity.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class EntityListModelItemRenderer implements ListitemRenderer<Entity>, Serializable {

	private static final long serialVersionUID = 1L;

	public EntityListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, Entity entity, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(entity.getEntityCode());
		lc.setParent(item);
	  	lc = new Listcell(entity.getEntityDesc());
		lc.setParent(item);
	  	lc = new Listcell(entity.getCountry());
		lc.setParent(item);
	  	lc = new Listcell(entity.getStateCode());
		lc.setParent(item);
	  	lc = new Listcell(entity.getCityCode());
		lc.setParent(item);
		lc = new Listcell(entity.getPinCode());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(entity.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
	  	lc = new Listcell(entity.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(entity.getRecordType()));
		lc.setParent(item);
		item.setAttribute("entityCode", entity.getEntityCode());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onEntityItemDoubleClicked");
	}
}