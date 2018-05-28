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
 * FileName    		:  ShortMessageServiceImpl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-11-2017    														*
 *                                                                  						*
 * Modified Date    :  22-11-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date			Author				Version	Comments                                        *
 ********************************************************************************************
 * 04-10-2012   Pennant	            0.1                                                     * 
 *                                                                                          * 
 * 28-05-2018   Sai Krishna         0.2     bugs #389 Skip the external e-Mail and SMS      * 
 *                                          services if the implementation for the same is  * 
 *                                          not available.	                                * 
 *                                                        			                        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.sms.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.sms.ShortMessageService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.SMSService;

public class ShortMessageServiceImpl implements ShortMessageService {
    
	private static final Logger logger = Logger.getLogger(ShortMessageServiceImpl.class);

	@Autowired(required = false)
	private SMSService sMSService; 

	/**
	 * Method for call the ExternalServiceTask to send SMS.
	 * 
	 * @param custPhoneNoList
	 * @param smsContent
	 * @return 
	 */

	@Override
	public void sendMessage(List<MailTemplate> smsList, String finReference) {
		logger.debug(Literal.ENTERING);

		// bugs #389 Skip the external e-Mail and SMS services if the implementation for the same is not available.
		if (sMSService != null) {
			sMSService.sendSms(smsList, finReference);
		}

		logger.debug(Literal.LEAVING);
	}
}
