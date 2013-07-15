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
 * FileName    		:  ProvisionMovementListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.provisionmovement.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ProvisionMovementListModelItemRenderer implements ListitemRenderer<ProvisionMovement>, Serializable {

	private static final long serialVersionUID = -4343497695244309847L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, ProvisionMovement provisionMovement, int count) throws Exception {

		//final ProvisionMovement provisionMovement = (ProvisionMovement) data;
		Listcell lc;
	  	lc = new Listcell(PennantAppUtil.formateDate(provisionMovement.getProvMovementDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateInt(provisionMovement.getProvMovementSeq()));
	  	lc.setStyle("text-align:right;");
	  	lc.setParent(item);
	  	/*lc = new Listcell(PennantAppUtil.formateDate(provisionMovement.getProvCalDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(provisionMovement.getProvisionedAmt(),0));
	  	lc.setStyle("text-align:right;");
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(provisionMovement.getProvisionAmtCal(),0));
	  	lc.setStyle("text-align:right;");
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(provisionMovement.getProvisionDue(),0));
	  	lc.setStyle("text-align:right;");
	  	lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbProvisionPostSts = new Checkbox();
		cbProvisionPostSts.setDisabled(true);
		cbProvisionPostSts.setChecked(provisionMovement.getProvisionPostSts().equals("S")? true:false);
		lc.appendChild(cbProvisionPostSts);
		lc.setParent(item);*/
	  	lc = new Listcell(PennantAppUtil.amountFormate(provisionMovement.getNonFormulaProv(),3));
		lc.setStyle("text-align:right;");
	  	lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbUseNFProv = new Checkbox();
		cbUseNFProv.setDisabled(true);
		cbUseNFProv.setChecked(provisionMovement.isUseNFProv());
		lc.appendChild(cbUseNFProv);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbAutoReleaseNFP = new Checkbox();
		cbAutoReleaseNFP.setDisabled(true);
		cbAutoReleaseNFP.setChecked(provisionMovement.isAutoReleaseNFP());
		lc.appendChild(cbAutoReleaseNFP);
		lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(provisionMovement.getPrincipalDue(),3));
	  	lc.setStyle("text-align:right;");
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(provisionMovement.getProfitDue(),3));
	  	lc.setStyle("text-align:right;");
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(provisionMovement.getDueFromDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(provisionMovement.getLastFullyPaidDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	/*lc = new Listcell(PennantAppUtil.formateLong(provisionMovement.getLinkedTranId()));
	  	lc.setParent(item);
	  	lc = new Listcell(provisionMovement.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(provisionMovement.getRecordType()));
		lc.setParent(item);*/
		item.setAttribute("data", provisionMovement);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onProvisionMovementItemDoubleClicked");
	}
}