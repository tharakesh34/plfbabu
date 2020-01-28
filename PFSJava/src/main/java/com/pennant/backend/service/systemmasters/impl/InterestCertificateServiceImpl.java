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
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.dao.systemmasters.InterestCertificateDAO;
import com.pennant.backend.model.agreement.InterestCertificate;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.InterestCertificateService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.service.extended.fields.ExtendedFieldService;

/**
 * Service implementation for methods that depends on <b>InterestCertficate</b>.<br>
 * 
 */
public class InterestCertificateServiceImpl extends GenericService<InterestCertificate>
		implements InterestCertificateService {
	private static Logger logger = Logger.getLogger(InterestCertificateServiceImpl.class);

	private ExtendedFieldService extendedFieldService;
	private InterestCertificateDAO interestCertificateDAO;

	public InterestCertificateServiceImpl() {
		super();
	}

	@Override
	public InterestCertificate getInterestCertificateDetails(String finReference, String startDate, String endDate,
			boolean isProvCert) throws ParseException {
		logger.debug(Literal.ENTERING);

		InterestCertificate intCert = interestCertificateDAO.getInterestCertificateDetails(finReference);

		if (intCert == null) {
			return null;
		}

		InterestCertificate certificate = null;
		if (isProvCert) {
			certificate = interestCertificateDAO.getSumOfPrinicipalAndProfitAmount(finReference, startDate, endDate);
		} else {
			certificate = interestCertificateDAO.getSumOfPrinicipalAndProfitAmountPaid(finReference, startDate,
					endDate);
		}
		// Get Co-Applicants

		List<String> coApplicantList = interestCertificateDAO.getCoApplicantNames(finReference);
		StringBuilder coapplicant = new StringBuilder();
		for (String object : coApplicantList) {
			coapplicant.append(object).append(" , ");
		}
		intCert.setCoApplicant(coapplicant.toString());
		// set Loan ExtendedFields
		if (extendedFieldService != null) {
			extendedFieldService.setExtendedFields(intCert);
		}

		int format = CurrencyUtil.getFormat(intCert.getFinCcy());
		InterestCertificate summary = interestCertificateDAO.getSumOfPrinicipalAndProfitAmount(finReference, startDate,
				endDate);
		if (summary != null && summary.getFinSchdPftPaid() != null && summary.getFinSchdPriPaid() != null) {
			String finSchdPftPaid = PennantApplicationUtil.amountFormate(summary.getFinSchdPftPaid(), format);
			finSchdPftPaid = finSchdPftPaid.replace(",", "");
			String finSchdPriPaid = PennantApplicationUtil.amountFormate(summary.getFinSchdPriPaid(), format);
			finSchdPriPaid = finSchdPriPaid.replace(",", "");
			intCert.setFinSchdPftPaid(new BigDecimal(finSchdPftPaid));
			intCert.setFinSchdPriPaid(new BigDecimal(finSchdPriPaid));
			intCert.setSchdPftPaid(PennantApplicationUtil.amountFormate(summary.getFinSchdPftPaid(), format));
			intCert.setSchdPriPaid(PennantApplicationUtil.amountFormate(summary.getFinSchdPriPaid(), format));
			intCert.setTotalPaid(PennantApplicationUtil
					.amountFormate(summary.getFinSchdPriPaid().add(summary.getFinSchdPftPaid()), format));
		} else {
			intCert.setFinSchdPftPaid(new BigDecimal(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, format)));
			intCert.setFinSchdPriPaid(new BigDecimal(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, format)));
			intCert.setSchdPftPaid(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, format));
			intCert.setSchdPriPaid(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, format));
			intCert.setTotalPaid(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, format));
		}

		intCert.setFinAmount(PennantApplicationUtil.amountFormate(new BigDecimal(intCert.getFinAmount()), format));

		// collateral address setup
		String collateralRef = interestCertificateDAO.getCollateralRef(intCert.getFinReference());
		if (collateralRef != null) {
			String collateralType = interestCertificateDAO.getCollateralType(collateralRef);
			if (collateralType != null) {
				for (int j = 1; j <= 5; j++) {
					String ColumnField = interestCertificateDAO.getCollateralTypeField("PROVISIONALCERTIFICATE",
							"COLLATERAL_" + collateralType + "_ED", "Addresstype" + j);
					if (ColumnField != null) {
						String ColumnValue = interestCertificateDAO.getCollateralTypeValue(
								"COLLATERAL_" + collateralType + "_ED", ColumnField, collateralRef);
						if (ColumnValue != null) {
							try {
								intCert.getClass().getMethod("setAddressType" + j, new Class[] { String.class })
										.invoke(intCert, ColumnValue);
							} catch (Exception e) {
								logger.error(Literal.EXCEPTION, e);
							}
						}
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return intCert;

	}

	public void setInterestCertificateDAO(InterestCertificateDAO interestCertificateDAO) {
		this.interestCertificateDAO = interestCertificateDAO;
	}

	@Autowired(required = false)
	public void setExtendedFieldService(ExtendedFieldService extendedFieldService) {
		this.extendedFieldService = extendedFieldService;
	}

}