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
 * FileName    		:  VASConfigurationListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.configuration.vasconfiguration.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class VASConfigurationListModelItemRenderer implements ListitemRenderer<VASConfiguration>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, VASConfiguration vASConfiguration, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(vASConfiguration.getProductCode());
		lc.setParent(item);
		
	  	lc = new Listcell(vASConfiguration.getProductDesc());
		lc.setParent(item);
		
		lc = new Listcell(vASConfiguration.getProductType());
		lc.setParent(item);
		
		lc = new Listcell(vASConfiguration.getProductCategory());
		lc.setParent(item);
		
		lc = new Listcell(PennantAppUtil.getlabelDesc(vASConfiguration.getRecAgainst(), PennantStaticListUtil.getRecAgainstTypes()));
	  	lc.setParent(item);
	  	
	  	lc = new Listcell(vASConfiguration.getManufacturerName());
	  	lc.setParent(item);
	  	
	  	lc = new Listcell(vASConfiguration.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(vASConfiguration.getRecordType()));
		lc.setParent(item);
		item.setAttribute("productCode", vASConfiguration.getProductCode());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onVASConfigurationItemDoubleClicked");
	}
}