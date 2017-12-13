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
 * FileName    		:  PathUtil.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  05-08-2015															*
 *                                                                  
 * Modified Date    :  																		*
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
package com.pennant.app.util;

import com.pennanttech.pennapps.core.App;

public class PathUtil {
	
	// Mail Attachment & Reports Download path
	public static final String	DOWNLOAD					= "Downloads";
	public static final String	MAIL_ATTACHMENT_DOWNLOAD	= "Downloads/Mail";
	public static final String	REPORTS_EOMDOWNLOAD_FOLDER	= "Downloads/EndOfMonth";
	public static final String	EOD_FILE_FOLDER				= "Downloads/EOD";
	public static final String	EOD_FILE_HISTORY			= "Downloads/EOD/History";
	public static final String	ECMS_ARCHIVEDOC_LOCATION	= "Downloads/EOD/ECMSArchiveDocs/";
	public static final String	SAS_EXTRACTS_LOCATION		= "Downloads/EOD/SASExtracts/";

	// Agreement Detail Paths
	public static final String	FINANCE_AGREEMENTS			= "Agreements";
	public static final String	MMA_AGREEMENTS				= "Agreements/MMAgreements";

	// Report Detail paths
	public static final String	REPORTS_CHECKS				= "Reports/Checks";
	public static final String	REPORTS_AUDIT				= "Reports/Audit";
	public static final String	REPORTS_ENDOFMONTH			= "Reports/EndOfMonth";
	public static final String	REPORTS_FINANCE				= "Reports/Finance";
	public static final String	REPORTS_LIST				= "Reports/List";
	public static final String	REPORTS_ORGANIZATION		= "Reports/Client";

	// Images
	public static final String	REPORTS_IMAGE_CLIENT		= "Reports/images/OrgLogo.png";
	public static final String	REPORTS_IMAGE_PRODUCT		= "Reports/images/ProductLogo.jpg";
	public static final String	MAIL_ATTACHMENT_AGGREMENT	= "Downloads/Mail/MailAttachments/Aggrements/";
	public static final String	MAIL_ATTACHMENT_REPORT		= "Downloads/Mail/Attachments/Reports/";
	public static final String	MAIL_BODY					= "Downloads/Mail/body/";

	/**
	 * Method for Fetch the application Configuration's path
	 * 
	 * @param requetedPath
	 * @return
	 */
	public static String getPath(String requetedPath) {
		return App.getResourcePath(requetedPath);
	}

}
