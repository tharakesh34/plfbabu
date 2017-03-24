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
 * FileName    		:  AssetTypeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2016    														*
 *                                                                  						*
 * Modified Date    :  14-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.configuration.assettype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.configuration.AssetType;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AssetTypeListModelItemRenderer implements ListitemRenderer<AssetType>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, AssetType assetType, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(assetType.getAssetType());
		lc.setParent(item);
	  	lc = new Listcell(assetType.getAssetDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox active = new Checkbox();
		active.setDisabled(true);
		active.setChecked(assetType.isActive());
		lc.appendChild(active);
		lc.setParent(item);
	  	lc = new Listcell(assetType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(assetType.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", assetType.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onAssetTypeItemDoubleClicked");
	}
}