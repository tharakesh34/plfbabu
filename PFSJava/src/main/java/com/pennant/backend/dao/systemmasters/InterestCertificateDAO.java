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
 * FileName    		:  InterestCertificateDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.systemmasters;
import java.text.ParseException;

import com.pennant.backend.model.agreement.InterestCertificate;

/**
 * DAO methods declaration for the <b>InterestCertificate model</b> class.<br>
 * 
 */
public interface InterestCertificateDAO {

	InterestCertificate getInterestCertificateDetails(String finReference ) throws ParseException;

	InterestCertificate getSumOfPrinicipalAndProfitAmount(String finReference, String startDate, String endDate) throws ParseException;

	String getCollateralRef(String finReference);

	String getCollateralType(String collateralRef);

	String getCollateralTypeField(String interfaceType, String table, String field);

	String getCollateralTypeValue(String table, String columnField,String reference);


}