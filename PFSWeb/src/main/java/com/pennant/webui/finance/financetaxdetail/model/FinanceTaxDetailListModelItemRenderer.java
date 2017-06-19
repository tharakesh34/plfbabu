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
 * FileName    		:  FinanceTaxDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-06-2017    														*
 *                                                                  						*
 * Modified Date    :  17-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.finance.financetaxdetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FinanceTaxDetailListModelItemRenderer implements ListitemRenderer<FinanceTaxDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public FinanceTaxDetailListModelItemRenderer() {
		super();
	}
	
	@Override
	public void render(Listitem item, FinanceTaxDetail financeTaxDetail, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(financeTaxDetail.getFinReference());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(financeTaxDetail.getApplicableFor(), PennantStaticListUtil.getTaxApplicableFor()));
	  	lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbTaxExempted = new Checkbox();
		cbTaxExempted.setDisabled(true);
		cbTaxExempted.setChecked(financeTaxDetail.isTaxExempted());
		lc.appendChild(cbTaxExempted);
		lc.setParent(item);
	  	lc = new Listcell(financeTaxDetail.getTaxNumber());
		lc.setParent(item);
	  	lc = new Listcell(financeTaxDetail.getCity());
		lc.setParent(item);
	  	lc = new Listcell(financeTaxDetail.getPinCode());
		lc.setParent(item);
	  	lc = new Listcell(financeTaxDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(financeTaxDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("finReference", financeTaxDetail.getFinReference());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceTaxDetailItemDoubleClicked");
	}
}