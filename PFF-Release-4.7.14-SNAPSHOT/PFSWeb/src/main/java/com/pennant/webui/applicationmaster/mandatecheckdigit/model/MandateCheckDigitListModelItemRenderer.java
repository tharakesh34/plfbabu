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
 * FileName    		:  MandateCheckDigitListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-12-2017    														*
 *                                                                  						*
 * Modified Date    :  11-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-12-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.mandatecheckdigit.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class MandateCheckDigitListModelItemRenderer implements ListitemRenderer<MandateCheckDigit>, Serializable {

	private static final long serialVersionUID = 1L;

	public MandateCheckDigitListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, MandateCheckDigit mandateCheckDigit, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(PennantApplicationUtil.formateInt(mandateCheckDigit.getCheckDigitValue()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(mandateCheckDigit.getLookUpValue()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(mandateCheckDigit.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(mandateCheckDigit.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(mandateCheckDigit.getRecordType()));
		lc.setParent(item);
		item.setAttribute("checkDigitValue", mandateCheckDigit.getCheckDigitValue());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onMandateCheckDigitItemDoubleClicked");
	}
}