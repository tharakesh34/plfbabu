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
 * FileName    		:  IRRCodeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2017    														*
 *                                                                  						*
 * Modified Date    :  21-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.irrcode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.IRRCode;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class IRRCodeListModelItemRenderer implements ListitemRenderer<IRRCode>, Serializable {

	private static final long serialVersionUID = 1L;

	public IRRCodeListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, IRRCode iRRCode, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(iRRCode.getIRRCode());
		lc.setParent(item);
	  	lc = new Listcell(iRRCode.getIRRCodeDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(iRRCode.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
	  	lc = new Listcell(iRRCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(iRRCode.getRecordType()));
		lc.setParent(item);
		item.setAttribute("iRRID", iRRCode.getIRRID());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onIRRCodeItemDoubleClicked");
	}
}