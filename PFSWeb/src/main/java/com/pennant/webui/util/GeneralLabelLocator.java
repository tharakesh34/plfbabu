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
	@SuppressWarnings("deprecation")
	public URL locate(Locale locale) throws Exception {

		String menuResFilename = "";

		if (StringUtils.isEmpty(context) || context.equalsIgnoreCase(PennantConstants.default_Language)) {
			Sessions.getCurrent().setAttribute("px_preferred_locale", new Locale(PennantConstants.default_Language.toLowerCase(), PennantConstants.default_Language));
			menuResFilename = MENU_FILE_NAME + MENU_FILE_SUFFIX;
		}else{
			Sessions.getCurrent().setAttribute("px_preferred_locale", new Locale(this.context.toLowerCase(), this.context.toUpperCase()));
			menuResFilename = MENU_FILE_NAME + "_" + context.toUpperCase() + MENU_FILE_SUFFIX;
		}

		
		
		// real path
		String menuResPath = Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/" + menuResFilename);

		// check if the file exists
		File fmr = new File(menuResPath);
		if (!fmr.exists()){
			System.out.println(menuResFilename + "Not Exists");
			menuResFilename = MENU_FILE_NAME + MENU_FILE_SUFFIX;
			fmr = new File(Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/" + menuResFilename));
		}
			

		// return url
		return fmr.toURL();
	}
}
