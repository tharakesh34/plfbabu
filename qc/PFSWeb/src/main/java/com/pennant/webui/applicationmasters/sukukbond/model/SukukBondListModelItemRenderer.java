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
 * FileName    		:  SukukBondListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-06-2015    														*
 *                                                                  						*
 * Modified Date    :  09-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-06-2015       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmasters.sukukbond.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmasters.SukukBond;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class SukukBondListModelItemRenderer implements ListitemRenderer<SukukBond>, Serializable {

	private static final long serialVersionUID = 1L;

	public SukukBondListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, SukukBond sukukBond, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(sukukBond.getBondCode());
		lc.setParent(item);
		lc = new Listcell(sukukBond.getBondDesc());
		lc.setParent(item);
		lc = new Listcell(sukukBond.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(sukukBond.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", sukukBond.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSukukBondItemDoubleClicked");
	}
}