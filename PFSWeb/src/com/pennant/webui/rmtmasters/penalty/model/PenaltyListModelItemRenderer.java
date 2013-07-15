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
 * FileName    		:  PenaltyListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.penalty.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.Penalty;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class PenaltyListModelItemRenderer implements ListitemRenderer<Penalty>, Serializable {

	private static final long serialVersionUID = -9202451412801564659L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, Penalty penalty, int count) throws Exception {

		//final Penalty penalty = (Penalty) data;
		Listcell lc;
		lc = new Listcell(penalty.getPenaltyType()+"-"+penalty.getLovDescPenaltyTypeName());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.formateDate(penalty.getPenaltyEffDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.formateInt(penalty.getODueGraceDays()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbPenaltyIsActive = new Checkbox();
		cbPenaltyIsActive.setDisabled(true);
		cbPenaltyIsActive.setChecked(penalty.isPenaltyIsActive());
		lc.appendChild(cbPenaltyIsActive);
		lc.setParent(item);
		lc = new Listcell(penalty.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(penalty.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", penalty);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onPenaltyItemDoubleClicked");
	}
}