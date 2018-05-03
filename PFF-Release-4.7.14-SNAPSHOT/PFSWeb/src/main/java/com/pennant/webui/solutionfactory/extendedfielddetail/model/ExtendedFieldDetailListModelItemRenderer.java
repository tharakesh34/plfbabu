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
 * FileName    		:  ExtendedFieldDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.solutionfactory.extendedfielddetail.model;

import java.io.Serializable;
import java.util.List;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 */
public class ExtendedFieldDetailListModelItemRenderer implements ListitemRenderer<ExtendedFieldHeader>, Serializable {

	private static final long serialVersionUID = -7165820550098919114L;
	private List<ValueLabel> modulesList = null;
	public ExtendedFieldDetailListModelItemRenderer(List<ValueLabel> modulesList) {
		this.modulesList = modulesList;
	}

	@Override
	public void render(Listitem item, ExtendedFieldHeader extendedFieldDetail, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(PennantAppUtil.getlabelDesc(extendedFieldDetail.getModuleName(), this.modulesList));
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel("label_ExtendedField_" + extendedFieldDetail.getSubModuleName()));
		lc.setParent(item);
		lc = new Listcell(extendedFieldDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(extendedFieldDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("data", extendedFieldDetail);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onExtendedFieldDetailItemDoubleClicked");
	}
}