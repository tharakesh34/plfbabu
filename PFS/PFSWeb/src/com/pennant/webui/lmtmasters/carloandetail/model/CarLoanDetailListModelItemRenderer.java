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
 * FileName    		:  CarLoanDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.carloandetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CarLoanDetailListModelItemRenderer implements ListitemRenderer<CarLoanDetail>, Serializable {

	private static final long serialVersionUID = -1692748444122173025L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, CarLoanDetail carLoanDetail, int count) throws Exception {

		//final CarLoanDetail carLoanDetail = (CarLoanDetail) data;
		Listcell lc;
	  	lc = new Listcell(String.valueOf(carLoanDetail.getLoanRefNumber()));
	  	lc.setParent(item);
	  	lc = new Listcell(carLoanDetail.getCarLoanFor()+"-"+carLoanDetail.getLovDescLoanForValue());
		lc.setParent(item);
	  	lc = new Listcell(carLoanDetail.getCarVersion()+"-"+carLoanDetail.getLovDescVehicleVersionCode());
		lc.setParent(item);
	  	lc = new Listcell(String.valueOf(carLoanDetail.getCarMakeYear()));
	  	lc.setStyle("text-align:right;");
	  	lc.setParent(item);
	  	lc = new Listcell(carLoanDetail.getCarDealer()+"-"+carLoanDetail.getLovDescCarDealerName());
		lc.setParent(item);
	  	lc = new Listcell(carLoanDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(carLoanDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", carLoanDetail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCarLoanDetailItemDoubleClicked");
	}
}