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
 * FileName    		:  ChequeHeaderListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017    														*
 *                                                                  						*
 * Modified Date    :  27-11-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-11-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.pdc.chequeheader.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ChequeHeaderListModelItemRenderer implements ListitemRenderer<ChequeHeader>, Serializable {

	private static final long serialVersionUID = 1L;

	public ChequeHeaderListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, ChequeHeader chequeHeader, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(chequeHeader.getFinReference());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(chequeHeader.getChequeType(), PennantStaticListUtil.getChequeTypes()));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.formateInt(chequeHeader.getNoOfCheques()));
	  	lc.setParent(item);
	    lc = new Listcell(PennantAppUtil.amountFormate(chequeHeader.getTotalAmount(),CurrencyUtil.getFormat("INR")));
		lc.setParent(item);
	  	lc = new Listcell(chequeHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(chequeHeader.getRecordType()));
		lc.setParent(item);
		item.setAttribute("headerID", chequeHeader.getHeaderID());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onChequeHeaderItemDoubleClicked");
	}
}