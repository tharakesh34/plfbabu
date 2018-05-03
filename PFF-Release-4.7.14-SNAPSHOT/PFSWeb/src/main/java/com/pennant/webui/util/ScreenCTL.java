package com.pennant.webui.util;
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
 * FileName    		:  ScreenCTL.java														*                           
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                     						*
 * Creation Date    :  01-12-2012															*
 *                                                                     						*
 * Modified Date    :  01-12-2012															*
 *                                                                     						*
 * Description 		:												 						*                                 
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
import java.util.HashMap;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radiogroup;

import com.pennant.backend.model.Notes;

public class ScreenCTL {
	
	public static final int SCRN_GNENQ = 1;
	public static final int SCRN_GNADD = 2;
	public static final int SCRN_GNINT = 3;
	public static final int SCRN_GNEDT = 4;
	public static final int SCRN_WFADD = 5;
	public static final int SCRN_WFEDT = 6;

	
	public static int getMode(boolean enquiry,boolean workFlow,boolean newRecord) {
		if(enquiry){
			return SCRN_GNENQ;
		}
		if(workFlow){
			if (newRecord){
				return SCRN_WFADD;
			}else{
				return SCRN_WFEDT;
			}
		}else{
			if (newRecord){
				return SCRN_GNADD;
			}else{
				return SCRN_GNINT;
			}
		}
	}
	
	public static boolean initButtons(int mode,ButtonStatusCtrl btnCtrl,Button btnNotes,boolean workFlow,boolean firstTask,Radiogroup userAction,HtmlBasedComponent focusField1,HtmlBasedComponent focusField2){
		boolean readOnly=false;
		
		if(focusField2!=null){
			focusField2.focus();
		}
		switch (mode) {
		case SCRN_GNADD:
			btnCtrl.setInitNew();
			if(focusField1!=null){
				focusField1.focus();
			}
			break;
		case SCRN_GNINT:
			readOnly=true;
			btnCtrl.setInitEdit();
			break;
		case SCRN_GNEDT:
			btnCtrl.setBtnStatus_Edit();
			break;
		case SCRN_WFADD:
			btnCtrl.setInitNew();
			if(focusField1!=null){
				focusField1.focus();
			}
			break;
		case SCRN_WFEDT:
			btnCtrl.setWFBtnStatus_Edit(firstTask);
			btnNotes.setVisible(true);
			break;
		default:
			btnCtrl.setBtnStatus_Enquiry();
			btnCtrl.setCloseFocus();
			readOnly=true;
			break;
		}
		
		if(workFlow && userAction!=null){
			
			if(userAction.getItemCount()>0){
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(readOnly);
				}
				userAction.setSelectedIndex(0);
			}
		}
		
		return readOnly;
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public static void displayNotes(Notes notes,Object control) throws Exception {

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", notes);
		map.put("control", control);
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
	}
}
