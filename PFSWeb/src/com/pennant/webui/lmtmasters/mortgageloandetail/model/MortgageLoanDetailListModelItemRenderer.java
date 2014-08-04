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
 * FileName    		:  MortgageLoanDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-10-2011    														*
 *                                                                  						*
 * Modified Date    :  14-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.mortgageloandetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class MortgageLoanDetailListModelItemRenderer implements ListitemRenderer<MortgageLoanDetail>, Serializable {

	private static final long serialVersionUID = -7369262422361298349L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, MortgageLoanDetail mortgageLoanDetail, int count) throws Exception {

		//final MortgageLoanDetail mortgageLoanDetail = (MortgageLoanDetail) data;
		Listcell lc;
		lc = new Listcell(mortgageLoanDetail.getLoanRefNumber());
		lc.setParent(item);
		lc = new Listcell(mortgageLoanDetail.getLovDescMortgPropertyName());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(mortgageLoanDetail.getMortgCurrentValue(),0));
		lc.setParent(item);
		lc = new Listcell(mortgageLoanDetail.getMortgPurposeOfLoan());
		lc.setParent(item);
		lc = new Listcell(mortgageLoanDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(mortgageLoanDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", mortgageLoanDetail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onMortgageLoanDetailItemDoubleClicked");
	}
}