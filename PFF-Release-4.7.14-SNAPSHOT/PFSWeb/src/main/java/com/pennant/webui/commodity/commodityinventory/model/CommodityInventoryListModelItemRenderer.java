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
 * FileName    		:  CommodityInventoryListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-04-2015    														*
 *                                                                  						*
 * Modified Date    :  23-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-04-2015       Pennant	                 0.1                                            * 
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

package com.pennant.webui.commodity.commodityinventory.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.commodity.CommodityInventory;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CommodityInventoryListModelItemRenderer implements ListitemRenderer<CommodityInventory>, Serializable {

	private static final long serialVersionUID = 1L;

	public CommodityInventoryListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, CommodityInventory commodityInventory, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(commodityInventory.getBrokerCode());
		lc.setParent(item);
		lc = new Listcell(commodityInventory.getHoldCertificateNo());
		lc.setParent(item);
		lc = new Listcell(commodityInventory.getLovDescCommodityDesc());
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(commodityInventory.getPurchaseDate()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(commodityInventory.getFinalSettlementDate()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(commodityInventory.getPurchaseAmount(),CurrencyUtil.getFormat(commodityInventory.getCommodityCcy())));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(commodityInventory.getQuantity()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbBulkPurchase = new Checkbox();
		cbBulkPurchase.setDisabled(true);
		cbBulkPurchase.setChecked(commodityInventory.isBulkPurchase());
		lc.appendChild(cbBulkPurchase);
		lc.setParent(item);
		lc = new Listcell(commodityInventory.getLovDescRequestStage());
		lc.setParent(item);
		lc = new Listcell(commodityInventory.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(commodityInventory.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", commodityInventory.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCommodityInventoryItemDoubleClicked");
	}
}