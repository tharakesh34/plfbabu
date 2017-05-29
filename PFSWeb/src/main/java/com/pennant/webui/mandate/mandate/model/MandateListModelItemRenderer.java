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
 * FileName    		:  MandateListModelItemRenderer.java                                    * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.mandate.mandate.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class MandateListModelItemRenderer implements ListitemRenderer<Mandate>, Serializable {

	private static final long serialVersionUID = 1L;
	boolean multiselect=false;

	
	public MandateListModelItemRenderer(boolean multiselect) {
		super();
		this.multiselect = multiselect;
	}


	@Override
	public void render(Listitem item, Mandate mandate, int count) throws Exception {
		if (multiselect) {
			Listbox listbox = (Listbox) item.getParent();
			listbox.setMultiple(true);
			listbox.setCheckmark(true);
		}
		Listcell lc;
		
		lc = new Listcell(String.valueOf(mandate.getMandateID()));
		lc.setParent(item);
		lc = new Listcell(mandate.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(mandate.getMandateType());
		lc.setParent(item);
		lc = new Listcell(mandate.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(mandate.getBankName());
		lc.setParent(item);
		lc = new Listcell(mandate.getAccNumber());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(mandate.getAccType(), PennantStaticListUtil.getAccTypeList()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(mandate.getMaxLimit(),CurrencyUtil.getFormat(mandate.getMandateCcy())));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(mandate.getExpiryDate()));
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(mandate.getStatus(), PennantStaticListUtil.getStatusTypeList()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(mandate.getInputDate()));
		lc.setParent(item);
		lc = new Listcell(mandate.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(mandate.getRecordType()));
		lc.setParent(item);
		
		item.setAttribute("id", mandate.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onMandateItemDoubleClicked");
	}
}