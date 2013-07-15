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
 * FileName    		:  FinanceDisbursementListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financedisbursement.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FinanceDisbursementListModelItemRenderer implements ListitemRenderer<FinanceDisbursement>, Serializable {

	private static final long serialVersionUID = 1L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, FinanceDisbursement financeDisbursement, int count) throws Exception {

		//final FinanceDisbursement financeDisbursement = (FinanceDisbursement) data;
		Listcell lc;
	  	lc = new Listcell(financeDisbursement.getFinReference());
		lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(financeDisbursement.getDisbDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	lc = new Listcell(financeDisbursement.getDisbDesc());
		lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(financeDisbursement.getDisbAmount(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(financeDisbursement.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(financeDisbursement.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", financeDisbursement);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceDisbursementItemDoubleClicked");
	}
}