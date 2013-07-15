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
 * FileName    		:  ExtendedFieldHeaderListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.extendedfieldheader.model;

import java.io.Serializable;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 */
public class ExtendedFieldHeaderListModelItemRenderer implements ListitemRenderer<ExtendedFieldHeader>, Serializable {

	private static final long serialVersionUID = -2172710250317016081L;

	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, ExtendedFieldHeader extendedFieldHeader, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(Labels.getLabel("label_ExtendedField_"+extendedFieldHeader.getModuleName()));
		lc.setParent(item);
	  	lc = new Listcell(Labels.getLabel("label_ExtendedField_"+extendedFieldHeader.getSubModuleName()));
		lc.setParent(item);
	  	lc = new Listcell(extendedFieldHeader.getTabHeading());
		lc.setParent(item);
	  	lc = new Listcell(extendedFieldHeader.getNumberOfColumns());
	  	lc.setParent(item);
	  	lc = new Listcell(extendedFieldHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(extendedFieldHeader.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", extendedFieldHeader);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onExtendedFieldHeaderItemDoubleClicked");
	}
}