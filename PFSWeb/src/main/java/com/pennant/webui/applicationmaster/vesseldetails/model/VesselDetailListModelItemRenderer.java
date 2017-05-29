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
 * FileName    		:  VesselDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-05-2015    														*
 *                                                                  						*
 * Modified Date    :  12-05-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-05-2015       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.vesseldetails.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.VesselDetail;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class VesselDetailListModelItemRenderer implements ListitemRenderer<VesselDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public VesselDetailListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, VesselDetail vesselDetail, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(vesselDetail.getVesselTypeID());
		lc.setParent(item);
		lc = new Listcell(vesselDetail.getVesselTypeName());
		lc.setParent(item);
		lc = new Listcell(vesselDetail.getVesselSubType());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbIsActive = new Checkbox();
		cbIsActive.setDisabled(true);
		cbIsActive.setChecked(vesselDetail.isActive());
		lc.appendChild(cbIsActive);
		lc.setParent(item);
		lc = new Listcell(vesselDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(vesselDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", vesselDetail.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onVesselDetailItemDoubleClicked");
	}
}