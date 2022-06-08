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
 * FileName    		:  GSTRateListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-05-2019    														*
 *                                                                  						*
 * Modified Date    :  20-05-2019    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-05-2019       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.rmtmasters.gstrate.model;

import java.io.Serializable;
import java.util.List;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.rmtmasters.GSTRate;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class GSTRateListModelItemRenderer implements ListitemRenderer<GSTRate>, Serializable {

	private static final long serialVersionUID = 1L;

	private List<Property> listTaxType = PennantAppUtil.getTaxtTypeList();
	private List<ValueLabel> listCalcOn = PennantStaticListUtil.getCalcOnList();

	public GSTRateListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, GSTRate gSTRate, int count) {

		Listcell lc;
		lc = new Listcell(gSTRate.getFromState() + " - " + gSTRate.getFromStateName());
		lc.setParent(item);
		lc = new Listcell(gSTRate.getToState() + " - " + gSTRate.getToStateName());
		lc.setParent(item);
		lc = new Listcell(PennantStaticListUtil.getPropertyValue(listTaxType, gSTRate.getTaxType()));
		lc.setParent(item);
		lc = new Listcell(String
				.valueOf(PennantApplicationUtil.amountFormate(gSTRate.getAmount(), PennantConstants.defaultCCYDecPos)));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(gSTRate.getPercentage()));
		lc.setParent(item);
		lc.setStyle("text-align:Right;");
		lc = new Listcell(PennantStaticListUtil.getlabelDesc(gSTRate.getCalcOn(), listCalcOn));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(gSTRate.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(gSTRate.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(gSTRate.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", gSTRate.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onGSTRateItemDoubleClicked");
	}
}