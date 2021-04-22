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
 * FileName    		:  CommodityDetailListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.finance.commodity.commoditydetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class CommodityDetailListModelItemRenderer implements ListitemRenderer<CommodityDetail>, Serializable {

	private static final long serialVersionUID = 2519982162130114598L;

	public CommodityDetailListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, CommodityDetail commodityDetail, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(commodityDetail.getCommodityCode());
		lc.setParent(item);
		lc = new Listcell(commodityDetail.getCommodityName());
		lc.setParent(item);
		lc = new Listcell(commodityDetail.getCommodityUnitCode());
		lc.setParent(item);
		lc = new Listcell(commodityDetail.getCommodityUnitName());
		lc.setParent(item);
		lc = new Listcell(commodityDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(commodityDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", commodityDetail.getId());
		item.setAttribute("commodityUnitCode", commodityDetail.getCommodityUnitCode());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCommodityDetailItemDoubleClicked");
	}
}