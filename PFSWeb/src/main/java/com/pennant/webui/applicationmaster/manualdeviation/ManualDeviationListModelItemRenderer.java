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
 * FileName    		:  ManualDeviationListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2018    														*
 *                                                                  						*
 * Modified Date    :  03-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.manualdeviation;

import java.io.Serializable;
import java.util.List;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.Property;
import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ManualDeviationListModelItemRenderer implements ListitemRenderer<ManualDeviation>, Serializable {
	private static final long serialVersionUID = 1L;
	private List<Property> severities = PennantStaticListUtil.getManualDeviationSeverities();

	public ManualDeviationListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, ManualDeviation manualDeviation, int count) {

		Listcell lc;
		lc = new Listcell(manualDeviation.getCode());
		lc.setParent(item);
		lc = new Listcell(manualDeviation.getDescription());
		lc.setParent(item);
		lc = new Listcell(
				PennantAppUtil.getlabelDesc(manualDeviation.getModule(), PennantStaticListUtil.getWorkFlowModules()));
		lc.setParent(item);
		lc = new Listcell(manualDeviation.getCategorizationName());
		lc.setParent(item);
		lc = new Listcell(PennantStaticListUtil.getPropertyValue(severities, manualDeviation.getSeverity()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(manualDeviation.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(manualDeviation.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(manualDeviation.getRecordType()));
		lc.setParent(item);
		item.setAttribute("deviationID", manualDeviation.getDeviationID());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onManualDeviationItemDoubleClicked");
	}
}