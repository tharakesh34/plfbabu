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
 * FileName    		:  InterestCertficateServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.systemmasters.impl;

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.log4j.Logger;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.dao.systemmasters.InterestCertificateDAO;
import com.pennant.backend.model.agreement.InterestCertificate;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.InterestCertificateService;
import com.pennant.backend.util.PennantApplicationUtil;

/**
 * Service implementation for methods that depends on <b>InterestCertficate</b>.<br>
 * 
 */
public class InterestCertificateServiceImpl extends GenericService<InterestCertificate>
		implements InterestCertificateService {

	private static Logger logger = Logger.getLogger(InterestCertificateServiceImpl.class);

	private InterestCertificateDAO interestCertificateDAO;

	public InterestCertificateServiceImpl() {
		super();
	}

	@Override
	public InterestCertificate getInterestCertificateDetails(String finReference, String startDate, String endDate) throws ParseException {
		logger.debug("Entering");
		
		InterestCertificate interestCertificate=getInterestCertificateDAO().getInterestCertificateDetails(finReference);
		if(interestCertificate!=null){
			int format=CurrencyUtil.getFormat(interestCertificate.getFinCcy());
			InterestCertificate certificate=getInterestCertificateDAO().getSumOfPrinicipalAndProfitAmount(finReference,startDate, endDate);
			if(certificate!=null && certificate.getFinSchdPftPaid()!=null && certificate.getFinSchdPriPaid()!=null){
				String finSchdPftPaid=PennantApplicationUtil.amountFormate(certificate.getFinSchdPftPaid(),format);
				finSchdPftPaid=finSchdPftPaid.replace(",", "");
				String finSchdPriPaid=PennantApplicationUtil.amountFormate(certificate.getFinSchdPriPaid(),format);
				finSchdPriPaid=finSchdPriPaid.replace(",", "");
				interestCertificate.setFinSchdPftPaid(new BigDecimal(finSchdPftPaid));
				interestCertificate.setFinSchdPriPaid(new BigDecimal(finSchdPriPaid));
				interestCertificate.setSchdPftPaid(PennantApplicationUtil.amountFormate(certificate.getFinSchdPftPaid(),format));
				interestCertificate.setSchdPriPaid(PennantApplicationUtil.amountFormate(certificate.getFinSchdPriPaid(),format));
				interestCertificate.setTotalPaid(PennantApplicationUtil.amountFormate(certificate.getFinSchdPriPaid().add(certificate.getFinSchdPftPaid()),format));
			}else{
				interestCertificate.setFinSchdPftPaid(new BigDecimal(PennantApplicationUtil.amountFormate(BigDecimal.ZERO,format)));
				interestCertificate.setFinSchdPriPaid(new BigDecimal(PennantApplicationUtil.amountFormate(BigDecimal.ZERO,format)));
				interestCertificate.setSchdPftPaid(PennantApplicationUtil.amountFormate(BigDecimal.ZERO,format));
				interestCertificate.setSchdPriPaid(PennantApplicationUtil.amountFormate(BigDecimal.ZERO,format));
				interestCertificate.setTotalPaid(PennantApplicationUtil.amountFormate(BigDecimal.ZERO,format));
			}
			
			interestCertificate.setFinAmount(PennantApplicationUtil.amountFormate(new BigDecimal(interestCertificate.getFinAmount()),format));
			
			//collateral address setup
			String collateralRef=getInterestCertificateDAO().getCollateralRef(interestCertificate.getFinReference());
			if(collateralRef!=null){
				String collateralType=getInterestCertificateDAO().getCollateralType(collateralRef);
				if(collateralType!=null){
					for(int j=1;j<=5;j++){
						String ColumnField=getInterestCertificateDAO().getCollateralTypeField("PROVISIONALCERTIFICATE","COLLATERAL_"+collateralType+"_ED","Addresstype"+j);
						if (ColumnField != null) {
						String ColumnValue=getInterestCertificateDAO().getCollateralTypeValue("COLLATERAL_"+collateralType+"_ED",ColumnField,collateralRef);
							if (ColumnValue != null) {
								try {
									interestCertificate.getClass().getMethod("setAddressType" + j, new Class[] { String.class }).invoke(interestCertificate, ColumnValue);
								} catch (Exception e) {
									logger.error("Exception: ", e);
								}
							}
						
					    }
					}
				}
			}
			logger.debug("Leaving");
			return interestCertificate;
		}else{
			logger.debug("Leaving");
			return null;			
		}
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public InterestCertificateDAO getInterestCertificateDAO() {
		return interestCertificateDAO;
	}

	public void setInterestCertificateDAO(InterestCertificateDAO interestCertificateDAO) {
		this.interestCertificateDAO = interestCertificateDAO;
	}


}