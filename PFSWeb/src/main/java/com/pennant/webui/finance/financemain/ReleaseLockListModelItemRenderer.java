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
 * FileName    		:  ReleaseLockListModelItemRenderer.java                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-11-2018    														*
 *                                                                  						*
 * Modified Date    :  30-11-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-11-2018       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the listBox.
 */
public class ReleaseLockListModelItemRenderer implements ListitemRenderer<FinanceMain>, Serializable {
	private static final long serialVersionUID = 3736186724610414895L;

	public ReleaseLockListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, FinanceMain financeMain, int count) {
		Listcell listcell;

		listcell = new Listcell(financeMain.getFinReference());
		listcell.setParent(item);

		listcell = new Listcell(financeMain.getNextUsrName());
		listcell.setParent(item);

		listcell = new Listcell(financeMain.getLovValue());
		listcell.setParent(item);

		listcell = new Listcell(financeMain.getRecordStatus());
		listcell.setParent(item);

		listcell = new Listcell(PennantJavaUtil.getLabel(financeMain.getRecordType()));
		listcell.setParent(item);

		item.setAttribute("id", financeMain.getFinReference());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onReleaseLockItemDoubleClicked");
	}
}
