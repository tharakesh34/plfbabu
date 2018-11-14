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
 * FileName    		:  OverdueChargeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-05-2012    														*
 *                                                                  						*
 * Modified Date    :  10-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rulefactory.overduecharge.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rulefactory.OverdueCharge;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class OverdueChargeListModelItemRenderer implements ListitemRenderer<OverdueCharge>, Serializable {

	private static final long serialVersionUID = 7335935028165679463L;

	public OverdueChargeListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, OverdueCharge overdueCharge, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(overdueCharge.getODCRuleCode());
		lc.setParent(item);
		lc = new Listcell(overdueCharge.getODCPLAccount());
		lc.setParent(item);
		lc = new Listcell(overdueCharge.getODCCharityAccount());
		lc.setParent(item);
		lc = new Listcell(overdueCharge.getODCPLShare().toString());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbODCSweepCharges = new Checkbox();
		cbODCSweepCharges.setDisabled(true);
		cbODCSweepCharges.setChecked(overdueCharge.isODCSweepCharges());
		lc.appendChild(cbODCSweepCharges);
		lc.setParent(item);
		lc = new Listcell(overdueCharge.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(overdueCharge.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", overdueCharge);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onOverdueChargeItemDoubleClicked");
	}
}