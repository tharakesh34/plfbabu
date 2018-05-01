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
 * FileName    		:  ManualAdviseListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.finance.manualadvise.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ManualAdviseListModelItemRenderer implements ListitemRenderer<ManualAdvise>, Serializable {

	private static final long serialVersionUID = 1L;

	public ManualAdviseListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, ManualAdvise manualAdvise, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(PennantStaticListUtil.getlabelDesc(String.valueOf(manualAdvise.getAdviseType()),
				PennantStaticListUtil.getManualAdviseTypes()));
	  	lc.setParent(item);
	  	lc = new Listcell(manualAdvise.getFinReference());
		lc.setParent(item);
	  	lc = new Listcell(manualAdvise.getFeeTypeDesc());
		lc.setParent(item);
	  	lc = new Listcell(manualAdvise.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(manualAdvise.getRecordType()));
		lc.setParent(item);
		item.setAttribute("adviseID", manualAdvise.getAdviseID());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onManualAdviseItemDoubleClicked");
	}
}