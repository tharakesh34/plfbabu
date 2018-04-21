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
 * FileName    		:  BounceReasonListModelItemRenderer.java                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.bouncereason.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class BounceReasonListModelItemRenderer implements ListitemRenderer<BounceReason>, Serializable {

	private static final long serialVersionUID = 1L;

	public BounceReasonListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, BounceReason bounceReason, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(bounceReason.getBounceCode());
		lc.setParent(item);
		lc = new Listcell(PennantStaticListUtil.getPropertyValue(PennantStaticListUtil.getReasonType(),
				bounceReason.getReasonType()));
	  	lc.setParent(item);
		lc = new Listcell(PennantStaticListUtil.getPropertyValue(PennantStaticListUtil.getCategoryType(),
				bounceReason.getCategory()));
	  	lc.setParent(item);
	  	lc = new Listcell(bounceReason.getReturnCode());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(bounceReason.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
	  	lc = new Listcell(bounceReason.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(bounceReason.getRecordType()));
		lc.setParent(item);
		item.setAttribute("bounceID", bounceReason.getBounceID());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBounceReasonItemDoubleClicked");
	}
}