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
 * FileName    		:  InsurancePolicyListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-02-2017    														*
 *                                                                  						*
 * Modified Date    :  06-02-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-02-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.InsurancePolicy.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.InsurancePolicy;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class InsurancePolicyListModelItemRenderer implements ListitemRenderer<InsurancePolicy>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, InsurancePolicy insurancePolicy, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(insurancePolicy.getPolicyCode());
		lc.setParent(item);
		lc = new Listcell(insurancePolicy.getInsuranceType());
		lc.setParent(item);
		lc = new Listcell(insurancePolicy.getInsuranceProvider());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(insurancePolicy.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(insurancePolicy.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(insurancePolicy.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", insurancePolicy.getPolicyCode());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onInsurancePolicyItemDoubleClicked");
	}
}