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
 * FileName    		:  HomeLoanDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.homeloandetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class HomeLoanDetailListModelItemRenderer implements ListitemRenderer<HomeLoanDetail>, Serializable {

	private static final long serialVersionUID = -8430106928416366499L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, HomeLoanDetail homeLoanDetail, int count) throws Exception {

	//	final HomeLoanDetail homeLoanDetail = (HomeLoanDetail) data;
		Listcell lc;
		lc = new Listcell(homeLoanDetail.getLoanRefNumber());
		lc.setParent(item);
		lc = new Listcell(homeLoanDetail.getHomeDetails()+"-"+homeLoanDetail.getLovDescHomeDetailsName());
		lc.setParent(item);
		lc = new Listcell(homeLoanDetail.getHomeBuilderName());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(homeLoanDetail.getHomeCostPerFlat(),0));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbLoanRefType = new Checkbox();
		cbLoanRefType.setDisabled(true);
		cbLoanRefType.setChecked(homeLoanDetail.isLoanRefType());
		lc.appendChild(cbLoanRefType);
		lc.setParent(item);
		lc = new Listcell(homeLoanDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(homeLoanDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", homeLoanDetail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onHomeLoanDetailItemDoubleClicked");
	}
}