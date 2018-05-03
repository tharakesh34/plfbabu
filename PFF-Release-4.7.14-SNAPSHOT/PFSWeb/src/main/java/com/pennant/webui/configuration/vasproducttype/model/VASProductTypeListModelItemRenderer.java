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
 * FileName    		:  VASProductTypeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-01-2017    														*
 *                                                                  						*
 * Modified Date    :  09-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.configuration.vasproducttype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.vasproducttype.VASProductType;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class VASProductTypeListModelItemRenderer implements ListitemRenderer<VASProductType>, Serializable {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void render(Listitem item, VASProductType vASProductType, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(vASProductType.getProductType());
		lc.setParent(item);
		lc = new Listcell(vASProductType.getProductTypeDesc());
		lc.setParent(item);
		lc = new Listcell(vASProductType.getProductCtg());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox active = new Checkbox();
		active.setDisabled(true);
		active.setChecked(vASProductType.isActive());
		lc.appendChild(active);
		lc.setParent(item);
		lc = new Listcell(vASProductType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(vASProductType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", vASProductType.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onVASProductTypeItemDoubleClicked");
	}
}