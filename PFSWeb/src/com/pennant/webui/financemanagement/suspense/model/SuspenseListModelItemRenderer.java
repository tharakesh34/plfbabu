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
 * FileName    		:  ProvisionListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.suspense.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class SuspenseListModelItemRenderer implements ListitemRenderer<FinanceSuspHead>, Serializable {

	private static final long serialVersionUID = -4554647022945989420L;

	@Override
	public void render(Listitem item, FinanceSuspHead suspHead, int count) throws Exception {

		//final FinanceSuspHead suspHead = (FinanceSuspHead) data;
		Listcell lc;
		lc = new Listcell(suspHead.getFinReference());
		lc.setParent(item);
		lc = new Listcell(suspHead.getLovDescCustCIFName());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox finIsInSusp = new Checkbox();
		finIsInSusp.setDisabled(true);
		finIsInSusp.setChecked(suspHead.isFinIsInSusp());
		lc.appendChild(finIsInSusp);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox manualSusp = new Checkbox();
		manualSusp.setDisabled(true);
		manualSusp.setChecked(suspHead.isManualSusp());
		lc.appendChild(manualSusp);
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(suspHead.getFinSuspAmt(), suspHead.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(suspHead.getFinCurSuspAmt(), suspHead.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(suspHead.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(suspHead.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", suspHead);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onSuspenseItemDoubleClicked");
	}
}