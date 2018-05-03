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
 * FileName    		:  CollateralStructureListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-11-2016    														*
 *                                                                  						*
 * Modified Date    :  29-11-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-11-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.collateral.collateralstructure.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

public class CollateralStructureListModelItemRenderer implements ListitemRenderer<CollateralStructure>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, CollateralStructure cs, int count) throws Exception {

		Listcell lc;

		lc = new Listcell(cs.getCollateralType());
		lc.setParent(item);
		
		lc = new Listcell(cs.getCollateralDesc());
		lc.setParent(item);

		lc = new Listcell(PennantAppUtil.getlabelDesc(cs.getLtvType(), PennantStaticListUtil.getListLtvTypes()));
		lc.setParent(item);
		
		lc = new Listcell();
		final Checkbox cbMarketableSecurities = new Checkbox();
		cbMarketableSecurities.setDisabled(true);
		cbMarketableSecurities.setChecked(cs.isMarketableSecurities());
		lc.appendChild(cbMarketableSecurities);
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox cbPreValidationReq = new Checkbox();
		cbPreValidationReq.setDisabled(true);
		cbPreValidationReq.setChecked(cs.isPreValidationReq());
		lc.appendChild(cbPreValidationReq);
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox cbPostValidationReq = new Checkbox();
		cbPostValidationReq.setDisabled(true);
		cbPostValidationReq.setChecked(cs.isPostValidationReq());
		lc.appendChild(cbPostValidationReq);
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox active = new Checkbox();
		active.setDisabled(true);
		active.setChecked(cs.isActive());
		lc.appendChild(active);
		lc.setParent(item);

		lc = new Listcell(cs.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(cs.getRecordType()));
		lc.setParent(item);

		item.setAttribute("CollateralType", cs.getCollateralType());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCollateralStructureItemDoubleClicked");
	}
}