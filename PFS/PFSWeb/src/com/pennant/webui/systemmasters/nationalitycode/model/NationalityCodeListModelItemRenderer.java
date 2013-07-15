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
 * FileName    		:  NationalityCodesListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.nationalitycode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class NationalityCodeListModelItemRenderer implements ListitemRenderer<NationalityCode>,
		Serializable {

	private static final long serialVersionUID = -2115424367644659335L;

	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, NationalityCode nationalityCode, int count) throws Exception {
		
		//final NationalityCode nationalityCode = (NationalityCode) data;
		Listcell lc;
		lc = new Listcell(nationalityCode.getNationalityCode());
		lc.setParent(item);
		lc = new Listcell(nationalityCode.getNationalityDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbNationalityIsActive = new Checkbox();
		cbNationalityIsActive.setDisabled(true);
		cbNationalityIsActive.setChecked(nationalityCode.isNationalityIsActive());
		lc.appendChild(cbNationalityIsActive);
		lc.setParent(item);
		lc = new Listcell(nationalityCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(nationalityCode.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", nationalityCode);
		ComponentsCtrl.applyForward(item,"onDoubleClick=onNationalityCodeItemDoubleClicked");
	}
}