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
 *
 * FileName    		:  GeneralLabelLocator.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
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

package com.pennant.webui.util;

import java.io.File;
import java.net.URL;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.LabelLocator;
import org.zkoss.zk.ui.Sessions;

import com.pennant.backend.util.PennantConstants;

public class GeneralLabelLocator implements LabelLocator {

	private static final String MENU_FILE_NAME = "i3-label";
	private static final String MENU_FILE_SUFFIX = ".properties";
	private String context;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            wam context
	 */
	public GeneralLabelLocator(String context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.util.resource.LabelLocator#locate(java.util.Locale)
	 */
	public URL locate(Locale locale) throws Exception {

		String menu_res_filename = "";

		if (StringUtils.isEmpty(context) || context.equalsIgnoreCase(PennantConstants.default_Language)) {
			Sessions.getCurrent().setAttribute("px_preferred_locale", new Locale(PennantConstants.default_Language.toLowerCase(), PennantConstants.default_Language));
			menu_res_filename = MENU_FILE_NAME + MENU_FILE_SUFFIX;
		}else{
			Sessions.getCurrent().setAttribute("px_preferred_locale", new Locale(this.context.toLowerCase(), this.context.toUpperCase()));
			menu_res_filename = MENU_FILE_NAME + "_" + context.toUpperCase() + MENU_FILE_SUFFIX;
		}

		
		
		// real path
		String menu_res_path = Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/" + menu_res_filename);

		// check if the file exists
		File fmr = new File(menu_res_path);
		if (!fmr.exists()){
			System.out.println(menu_res_filename + "Not Exists");
			menu_res_filename = MENU_FILE_NAME + MENU_FILE_SUFFIX;
			fmr = new File(Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/" + menu_res_filename));
		}
			

		// return url
		return fmr.toURL();
	}
}
