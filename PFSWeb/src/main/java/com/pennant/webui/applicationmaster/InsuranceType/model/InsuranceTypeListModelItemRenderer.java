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
 * FileName    		:  InsuranceTypeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2016    														*
 *                                                                  						*
 * Modified Date    :  19-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.InsuranceType.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.InsuranceType;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class InsuranceTypeListModelItemRenderer implements ListitemRenderer<InsuranceType>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, InsuranceType insuranceType, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(insuranceType.getInsuranceType());
		lc.setParent(item);
	  	lc = new Listcell(insuranceType.getInsuranceTypeDesc());
		lc.setParent(item);
	  	lc = new Listcell(insuranceType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(insuranceType.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", insuranceType.getInsuranceType());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onInsuranceTypeItemDoubleClicked");
	}
}