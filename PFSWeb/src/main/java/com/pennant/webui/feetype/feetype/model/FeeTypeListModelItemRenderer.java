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
 * FileName    		:  FeeTypeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-01-2017    														*
 *                                                                  						*
 * Modified Date    :  03-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.feetype.feetype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FeeTypeListModelItemRenderer implements ListitemRenderer<FeeType>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, FeeType feeType, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(feeType.getFeeTypeCode());
		lc.setParent(item);
	  	lc = new Listcell(feeType.getFeeTypeDesc());
		lc.setParent(item);
		lc = new Listcell();
		Checkbox checkbox = new Checkbox();
		checkbox.setChecked(feeType.isActive());
		checkbox.setDisabled(true);
		checkbox.setParent(lc);
		lc.setParent(item);
	  	lc = new Listcell(feeType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(feeType.getRecordType()));
		lc.setParent(item);
		
		item.setAttribute("id", feeType.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFeeTypeItemDoubleClicked");
	}
}