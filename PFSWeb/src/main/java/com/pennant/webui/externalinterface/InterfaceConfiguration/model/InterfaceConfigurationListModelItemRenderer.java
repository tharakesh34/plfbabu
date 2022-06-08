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
 * FileName    		:  InterfaceConfigurationListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-08-2019    														*
 *                                                                  						*
 * Modified Date    :  10-08-2019    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2019       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.externalinterface.InterfaceConfiguration.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.externalinterface.InterfaceConfiguration;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class InterfaceConfigurationListModelItemRenderer
		implements ListitemRenderer<InterfaceConfiguration>, Serializable {

	private static final long serialVersionUID = 1L;

	public InterfaceConfigurationListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, InterfaceConfiguration interfaceConfiguration, int count) {

		Listcell lc;
		lc = new Listcell(interfaceConfiguration.getCode());
		lc.setParent(item);
		lc = new Listcell(interfaceConfiguration.getDescription());
		lc.setParent(item);
		lc = new Listcell(interfaceConfiguration.getType());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.formateInt(interfaceConfiguration.getNotificationType()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbactive = new Checkbox();
		cbactive.setDisabled(true);
		cbactive.setChecked(interfaceConfiguration.isActive());
		lc.appendChild(cbactive);
		lc.setParent(item);
		lc = new Listcell(interfaceConfiguration.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(interfaceConfiguration.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", interfaceConfiguration.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onInterfaceConfigurationItemDoubleClicked");
	}
}