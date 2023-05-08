/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ProductAssetListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-11-2011
 * * * Modified Date : 19-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-11-2011 Pennant~ 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.rmtmasters.productAsset.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class ProductAssetListModelItemRenderer implements ListitemRenderer<ProductAsset>, Serializable {

	private static final long serialVersionUID = 5546399736336410891L;

	public ProductAssetListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, ProductAsset productAsset, int count) {
		Listcell lc;
		lc = new Listcell(productAsset.getAssetCode());
		lc.setParent(item);
		lc = new Listcell(productAsset.getAssetDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox assetIsActive = new Checkbox();
		assetIsActive.setDisabled(true);
		assetIsActive.setChecked(productAsset.isAssetIsActive());
		lc.appendChild(assetIsActive);
		lc.setParent(item);
		lc = new Listcell(productAsset.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(productAsset.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", productAsset);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onProductAssetItemDoubleClicked");
	}

}
