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
 * FileName    		:  AssetClassificationHeaderListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-05-2020    														*
 *                                                                  						*
 * Modified Date    :  04-05-2020    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-05-2020       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.assetclassificationheader.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.AssetClassificationHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AssetClassificationHeaderListModelItemRenderer
		implements ListitemRenderer<AssetClassificationHeader>, Serializable {

	private static final long serialVersionUID = 1L;

	public AssetClassificationHeaderListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, AssetClassificationHeader assetClassificationHeader, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(assetClassificationHeader.getCode());
		lc.setParent(item);
		lc = new Listcell(assetClassificationHeader.getDescription());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(assetClassificationHeader.getStageOrder()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox active = new Checkbox();
		active.setDisabled(true);
		active.setChecked(assetClassificationHeader.isActive());
		lc.appendChild(active);
		lc.setParent(item);
		lc = new Listcell(assetClassificationHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(assetClassificationHeader.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", assetClassificationHeader.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAssetClassificationHeaderItemDoubleClicked");
	}
}