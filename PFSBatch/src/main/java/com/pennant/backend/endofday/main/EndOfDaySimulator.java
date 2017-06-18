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
 * FileName    		:  PFSEndOfDayJob.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
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
package com.pennant.backend.endofday.main;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EndOfDaySimulator {
	private static final Logger logger = Logger.getLogger(EndOfDaySimulator.class);
	
	public static void main(String[] args) throws Exception {

		try {
			  
			@SuppressWarnings({ "resource" })
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"launch-context.xml","eod-batch-config-db.xml","eod-batch-config-beans.xml",
					"eod-batch-config-service.xml","log4j.xml"});
/*			 EodTrigger eodTrigger =(EodTrigger) context.getBean("eodTrigger");
			 eodTrigger.run();*/
			
//			Eod eod = (Eod) context.getBean("eod");
//			// Post EOD service
//			eod.getPostEodService().doProcess();
		

		} catch (Exception e) {
			logger.warn("Exception: ", e);
		}

	}

}
