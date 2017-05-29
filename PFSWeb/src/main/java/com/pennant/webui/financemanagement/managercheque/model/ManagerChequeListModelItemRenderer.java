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
 * FileName    		:  ManagerChequeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.managercheque.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.financemanagement.ManagerCheque;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ManagerChequeListModelItemRenderer implements ListitemRenderer<ManagerCheque>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, ManagerCheque managerCheque, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(managerCheque.getChqPurposeCodeName());
		lc.setParent(item);
		lc = new Listcell(managerCheque.getChequeRef());
		lc.setParent(item);
		lc = new Listcell(managerCheque.getChequeNo());
		lc.setParent(item);
		lc = new Listcell(managerCheque.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(managerCheque.getBeneficiaryName());
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(managerCheque.getValueDate()));
		lc.setParent(item);
		lc = new Listcell(managerCheque.getDraftCcy() + "-" + CurrencyUtil.getCcyDesc(managerCheque.getDraftCcy()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(PennantApplicationUtil.amountFormate(managerCheque.getChequeAmount(),  CurrencyUtil.getFormat(managerCheque.getFundingCcy()))));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(managerCheque.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(managerCheque.getRecordType()));
		lc.setParent(item);
		
		item.setAttribute("id", managerCheque.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onManagerChequeItemDoubleClicked");
	}
}