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
 * FileName    		:  CommodityBrokerDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.commodity.commoditybrokerdetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for list items in the list box.
 * 
 */
public class CommodityBrokerDetailListModelItemRenderer implements ListitemRenderer<CommodityBrokerDetail>, Serializable {


	private static final long serialVersionUID = -3419874747206428796L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, CommodityBrokerDetail commodityBrokerDetail, int count) throws Exception {

		//final CommodityBrokerDetail commodityBrokerDetail = (CommodityBrokerDetail) data;
		Listcell lc;
	  	lc = new Listcell(commodityBrokerDetail.getBrokerCode());
		lc.setParent(item);
	  	lc = new Listcell(commodityBrokerDetail.getLovDescBrokerCIF());
		lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(commodityBrokerDetail.getBrokerFrom(), PennantConstants.dateFormat));
		lc.setParent(item);
	  	lc = new Listcell(commodityBrokerDetail.getBrokerAddrHNbr());
		lc.setParent(item);
	  	lc = new Listcell(commodityBrokerDetail.getBrokerAddrFlatNbr());
		lc.setParent(item);
	  	lc = new Listcell(commodityBrokerDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(commodityBrokerDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", commodityBrokerDetail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCommodityBrokerDetailItemDoubleClicked");
	}
}