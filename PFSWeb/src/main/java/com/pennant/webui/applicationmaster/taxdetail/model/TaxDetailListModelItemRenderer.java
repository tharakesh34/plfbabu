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
 * FileName    		:  TaxDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2017    														*
 *                                                                  						*
 * Modified Date    :  14-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.taxdetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TaxDetailListModelItemRenderer implements ListitemRenderer<TaxDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public TaxDetailListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, TaxDetail taxDetail, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(taxDetail.getCountryName());
		lc.setParent(item);
		lc = new Listcell(taxDetail.getProvinceName());
		lc.setParent(item);
		lc = new Listcell(taxDetail.getEntityDesc());
		lc.setParent(item);
		lc = new Listcell(taxDetail.getTaxCode());
		lc.setParent(item);
		lc = new Listcell(taxDetail.getPinCode());
		lc.setParent(item);
		lc = new Listcell(taxDetail.getCityName());
		lc.setParent(item);
		lc = new Listcell(taxDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(taxDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", taxDetail.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onTaxDetailItemDoubleClicked");
	}
}