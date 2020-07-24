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
 * FileName    		:  BusinessVerticalListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2018    														*
 *                                                                  						*
 * Modified Date    :  14-12-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.businessvertical.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.BusinessVertical;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class BusinessVerticalListModelItemRenderer implements ListitemRenderer<BusinessVertical>, Serializable {

	private static final long serialVersionUID = 1L;

	public BusinessVerticalListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, BusinessVertical businessVertical, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(businessVertical.getCode());
		lc.setParent(item);
		lc = new Listcell(businessVertical.getDescription());
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox act = new Checkbox();

		act.setChecked(businessVertical.isActive());
		lc.appendChild(act);
		lc.setParent(item);
		lc = new Listcell(businessVertical.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(businessVertical.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", businessVertical.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onBusinessVerticalItemDoubleClicked");
	}
}