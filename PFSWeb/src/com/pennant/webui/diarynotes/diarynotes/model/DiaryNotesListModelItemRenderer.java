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
 * FileName    		:  DiaryNotesListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2011    														*
 *                                                                  						*
 * Modified Date    :  20-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.diarynotes.diarynotes.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.model.FrequencyDetails;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.diarynotes.DiaryNotes;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class DiaryNotesListModelItemRenderer implements ListitemRenderer<DiaryNotes>, Serializable {

	private static final long serialVersionUID = 1L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, DiaryNotes diaryNotes, int count) throws Exception {

		//final DiaryNotes diaryNotes = (DiaryNotes) data;
		Listcell lc;
		lc = new Listcell(PennantAppUtil.getlabelDesc(diaryNotes.getDnType(),PennantAppUtil.getNotesType()));
	  	lc.setParent(item);
	  	lc = new Listcell(diaryNotes.getDnCreatedNo());
		lc.setParent(item);
	  	lc = new Listcell(diaryNotes.getDnCreatedName());
		lc.setParent(item);
	  	//lc = new Listcell(diaryNotes.getFrqCode()); Modified to add the List Frequency Codes
		
		FrequencyDetails frequencyDetails =  FrequencyUtil.getFrequencyDetail(diaryNotes.getFrqCode());				
		if(frequencyDetails.getErrorDetails() != null){
			lc = new Listcell("");	  	
			lc.setParent(item);		
		}else{
			lc = new Listcell(frequencyDetails.getFrequencyDescription());	  	
			lc.setParent(item);		
		}
		
	  	lc = new Listcell(PennantAppUtil.formateDate(diaryNotes.getNextActionDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(diaryNotes.getFinalActionDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbSuspend = new Checkbox();
		cbSuspend.setDisabled(true);
		cbSuspend.setChecked(diaryNotes.isSuspend());
		lc.appendChild(cbSuspend);
		lc.setParent(item);
	  	lc = new Listcell(diaryNotes.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(diaryNotes.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", diaryNotes);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDiaryNotesItemDoubleClicked");
	}
}