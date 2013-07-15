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
 * FileName    		:  IncomeTypeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.incometype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class IncomeTypeListModelItemRenderer implements ListitemRenderer<IncomeType>,
		Serializable {

	private static final long serialVersionUID = -385475759409018331L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, IncomeType incomeType, int count) throws Exception {
		
	//	final IncomeType incomeType = (IncomeType) data;
		Listcell lc;
		lc = new Listcell(incomeType.getIncomeTypeCode());
		lc.setParent(item);
		lc = new Listcell(incomeType.getIncomeTypeDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbIncomeTypeIsActive = new Checkbox();
		cbIncomeTypeIsActive.setDisabled(true);
		cbIncomeTypeIsActive.setChecked(incomeType.isIncomeTypeIsActive());
		lc.appendChild(cbIncomeTypeIsActive);
		lc.setParent(item);
		lc = new Listcell(incomeType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(incomeType.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", incomeType);
		ComponentsCtrl.applyForward(item,"onDoubleClick=onIncomeTypeItemDoubleClicked");
	}
}