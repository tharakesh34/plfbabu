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
 * FileName    		:  ReasonCategoryListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  19-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.reasoncategory.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.ReasonCategory;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ReasonCategoryListModelItemRenderer implements ListitemRenderer<ReasonCategory>, Serializable {

	private static final long serialVersionUID = 1L;

	public ReasonCategoryListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, ReasonCategory reasonCategory, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(reasonCategory.getCode());
		lc.setParent(item);
	  	lc = new Listcell(reasonCategory.getDescription());
		lc.setParent(item);
	  	lc = new Listcell(reasonCategory.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(reasonCategory.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", reasonCategory.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onReasonCategoryItemDoubleClicked");
	}
}