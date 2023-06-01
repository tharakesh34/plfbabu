/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : PathUtil.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 05-08-2015 *
 * 
 * Modified Date : *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.util;

import com.pennanttech.pennapps.core.App;

public class PathUtil {

	private PathUtil() {
		//
	}

	public static String APP_ROOT_PATH = "";

	// Mail Attachment & Reports Download path
	public static final String DOWNLOAD = "Downloads";
	public static final String MAIL_ATTACHMENT_DOWNLOAD = "Downloads/Mail";
	public static final String REPORTS_EOMDOWNLOAD_FOLDER = "Downloads/EndOfMonth";
	public static final String EOD_FILE_FOLDER = "Downloads/EOD";
	public static final String EOD_FILE_HISTORY = "Downloads/EOD/History";
	public static final String ECMS_ARCHIVEDOC_LOCATION = "Downloads/EOD/ECMSArchiveDocs/";
	public static final String SAS_EXTRACTS_LOCATION = "Downloads/EOD/SASExtracts/";

	// Agreement Detail Paths
	public static final String FINANCE_AGREEMENTS = "Agreements";
	public static final String FINANCE_INTERESTCERTIFICATE = "Agreements/InterestCertificate";
	public static final String FINANCE_LOANCLOSURE = "Agreements/LoanClosureLetter";
	public static final String COVENANT_STATUS_REPORT = "Agreements/LOD";
	public static final String BALANCE_CONFIRMATION = "Agreements/BalanceConfirmation";
	public static final String NOC_LETTER = "Agreements/NocLetter";
	public static final String CLOUSER_LETTER = "Agreements/LoanClosureLetter";
	public static final String CANCELLED_LETTER = "Agreements/LoanCancelationLetter";

	// Report Detail paths
	public static final String REPORTS_CHECKS = "Reports/Checks";
	public static final String REPORTS_AUDIT = "Reports/Audit";
	public static final String REPORTS_ENDOFMONTH = "Reports/EndOfMonth";
	public static final String REPORTS_FINANCE = "Reports/Finance";
	public static final String REPORTS_LIST = "Reports/List";
	public static final String REPORTS_ORGANIZATION = "Reports/Client";
	public static final String REPORTS_FONT = "/Reports/Fonts/";

	// Images
	public static final String REPORTS_IMAGE_CLIENT = "/Reports/images/OrgLogo.png";
	public static final String REPORTS_IMAGE_CLIENT_WATERMARK = "/Reports/images/Watermark.png";
	public static final String REPORTS_IMAGE_CLIENT_PATH = "/Reports/images";
	public static final String REPORTS_IMAGE_CLIENT_IMAGE = "/OrgLogo";
	public static final String REPORTS_IMAGE_PNG_FORMAT = ".png";
	public static final String REPORTS_IMAGE_SIGN = "/Reports/images/signimage.png";
	public static final String REPORTS_IMAGE_SOA = "/Reports/images/SOAOrgLogo.png";
	public static final String PAYMENT_SCHEDULE_IMAGE_CLIENT = "/Reports/images/PaymentSchedule.jpg";

	public static final String REPORTS_IMAGE_CLIENT_DIGITAL = "/Reports/images/OrgLogo1.png";

	public static final String REPORTS_IMAGE_PRODUCT = "/Reports/images/ProductLogo.jpg";
	public static final String MAIL_ATTACHMENT_AGGREMENT = "/Downloads/Mail/MailAttachments/Aggrements/";
	public static final String MAIL_ATTACHMENT_REPORT = "/Downloads/Mail/Attachments/Reports/";
	public static final String MAIL_BODY = "/Downloads/Mail/body/";
	public static final String CUSTOMER_FAQ = "/Customer360/FAQ.pdf";
	public static final String CUSTOMER_BALIC_CLAIM_FORM_FOR_CRITICAL_ILLNESS = "/Customer360/BAGIC Claim for Critical Illness.pdf";
	public static final String CUSTOMER_DEALTH_CLAIM_FORM = "/Customer360/Death Claim Form.pdf";
	public static final String CUSTOMER_FGI_CI_CLAIM_FORM = "/Customer360/FGL Death Cliam Form.pdf";
	public static final String CUSTOMER_FUTURE_GENERAL_NEW_CLAIM_FORM = "/Customer360/FGL Claim form.pdf";
	public static final String CUSTOMER_HDFC_CLAIM_FORM = "/Customer360/HDFC Claim form.pdf";
	public static final String CUSTOMER_CHECKLIST_FOR_DEALTHCRITICAL_FORM = "/Customer360/Checklist for Death - Critical Illness.xls";
	public static final String TEMPLATES = "Templates";
	public static final String FILE_UPLOADS_PATH = "FileUploads";
	public static final String MANUAL_SCHEDULES = "ManualSchedules";
	public static final String REPORTS_FINANCIALS_SPREADSHEETS = "Financials/SpreadSheets"; // For Credit Review Details
	public static final String EXT_LIABILITY = "ExtLiability";
	public static final String LOAN_REPORT = "LoanReport";
	public static final String UNAUTHORIZED_TRANSACTION = "UnAuthorizedTransactions";

	/**
	 * Method for Set Environment Variable to Root Path Location
	 * 
	 * @param envVarPath
	 */
	public static void setRootPath(String envVarPath) {
		APP_ROOT_PATH = envVarPath;
	}

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
