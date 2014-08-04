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
 * FileName    		:  EducationalExpenseListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.educationalexpense.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.lmtmasters.EducationalExpense;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class EducationalExpenseListModelItemRenderer implements ListitemRenderer<EducationalExpense>, Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3355155374056509636L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, EducationalExpense educationalExpense, int count) throws Exception {

		//final EducationalExpense educationalExpense = (EducationalExpense) data;
		Listcell lc;
	  	lc = new Listcell(educationalExpense.getLovDescEduExpDetailName().toUpperCase());
		lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(educationalExpense.getEduExpAmount(),0));
	  	lc.setStyle("text-align:right");
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(educationalExpense.getEduExpDate(), PennantConstants.dateFormate));
	  	lc.setParent(item);
	  	lc = new Listcell(educationalExpense.getRecordStatus());
	  	lc.setParent(item);
	  	lc = new Listcell(PennantJavaUtil.getLabel(educationalExpense.getRecordType()));
	  	lc.setParent(item);
		item.setAttribute("data", educationalExpense);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onEducationalExpenseItemDoubleClicked");
	}
}