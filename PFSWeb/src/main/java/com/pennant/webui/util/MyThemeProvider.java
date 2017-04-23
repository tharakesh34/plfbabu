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
 * FileName    		:  MyThemeProvider.java													*                           
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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeProvider;

public class MyThemeProvider implements ThemeProvider, Serializable {
	private static final long serialVersionUID = 1L;

	private transient static String _cssPrefix = "~./css/norm";
	private transient static String _fsCookieName = "zkdemotfs";
	private transient static String _skinCookieName = "zkdemoskin";

	@Override
	public Collection<Object> getThemeURIs(Execution exec, List<Object> uris) {

		int size = uris.size();

		for (int i = 0; i < size; i++) {
			String uri = (String) uris.get(i);
			if (uri.startsWith(_cssPrefix)) {
				uri = _cssPrefix + getFontSizeCookie(exec) + uri.substring(_cssPrefix.length());
				uris.set(i, uri);
			}
		}

		if ("silvergray".equals(getSkinCookie(exec))) {
			uris.add("~./silvergray/color.css.dsp");
			uris.add("~./silvergray/img.css.dsp");
		}

		return uris;
	}

	/**
	 * get font size value from cookie. <br>
	 * 
	 * @param exe
	 * @return "lg" for larger font, "sm" for smaler font or "" for normal font.
	 */
	public static String getFontSizeCookie(Execution exe) {
		Cookie[] cookies = ((HttpServletRequest) exe.getNativeRequest()).getCookies();

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (_fsCookieName.equals(cookies[i].getName())) {
					String fs = cookies[i].getValue();
					if ("lg".equals(fs)) {
						return "lg";
					} else if ("sm".equals(fs)) {
						return "sm";
					}
				}
			}
		}

		return "";
	}

	/**
	 * set font size value to cookie
	 * 
	 * @param exe
	 * @param fontSize
	 *            "lg" for larger font, "sm" for smaler font or "" for normal
	 *            font.
	 */
	public static void setFontSizeCookie(Execution exe, String fontSize) {

		String fs = "";
		if ("lg".equals(fontSize)) {
			fs = "lg";
		} else if ("sm".endsWith(fontSize)) {
			fs = "sm";
		}

		Cookie cookie = new Cookie(_fsCookieName, fs);
		cookie.setMaxAge(60 * 60 * 24 * 30); // store 30 days
		String cp = exe.getContextPath();
		cookie.setPath(cp);
		((HttpServletResponse) exe.getNativeResponse()).addCookie(cookie);
	}

	/**
	 * get the skin value from cookie. <br>
	 * 
	 * @param exe
	 * @return
	 */
	public static String getSkinCookie(Execution exe) {

		Cookie[] cookies = ((HttpServletRequest) exe.getNativeRequest()).getCookies();

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (_skinCookieName.equals(cookies[i].getName())) {
					String fs = cookies[i].getValue();
					if (fs != null) {
						return fs;
					}
				}
			}
		}
		return "";
	}

	/**
	 * set skin value to cookie. <br>
	 */
	public static void setSkinCookie(Execution exe, String skin) {

		Cookie cookie = new Cookie(_skinCookieName, skin);
		cookie.setMaxAge(60 * 60 * 24 * 30); // store 30 days
		((HttpServletResponse) exe.getNativeRequest()).addCookie(cookie);

	}

	public void setTheme() {
		// Executions.getCurrent().getDesktop().getWebApp().setAttribute("style",
		// uris.get(0));
	}

	@Override
	public String beforeWCS(Execution exec, String uri) {
		return null;
	}

	@Override
	public String beforeWidgetCSS(Execution exec, String uri) {
		return null;
	}

	@Override
	public int getWCSCacheControl(Execution exec, String uri) {
		return 0;
	}
}
