package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomizeFinanceDataValidation {

	private static final Logger logger = Logger.getLogger(CustomizeFinanceDataValidation.class);

	private CustomerDAO					customerDAO;
	private ExtendedFieldDetailsService	extendedFieldDetailsService;


	public FinScheduleData financeDataValidation(String vldGroup, FinanceDetail financeDetail, boolean apiFlag) {
		logger.debug(Literal.ENTERING);
		
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceType financeType = finScheduleData.getFinanceType();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<ErrorDetails> errorDetails = new ArrayList<>();

		if (StringUtils.isNotBlank(finMain.getLovDescCustCIF())) {
			Customer customer = customerDAO.getCustomerByCIF(finMain.getLovDescCustCIF(), "");
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getLovDescCustCIF();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90101", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			} else {
				finScheduleData.getFinanceMain().setCustID(customer.getCustID());
				financeDetail.getCustomerDetails().setCustomer(customer);
			}
		}
		if (finMain.getNumberOfTerms() <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "numberOfTerms";
			valueParm[1] = "0";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("91121", valueParm)));
			finScheduleData.setErrorDetails(errorDetails);
		}
		//Net Loan Amount
		BigDecimal netLoanAmount = finMain.getFinAmount().subtract(finMain.getDownPayment());
		if (netLoanAmount.compareTo(financeType.getFinMinAmount()) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(financeType.getFinMinAmount());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90132", valueParm)));
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		if (financeType.getFinMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
			if (netLoanAmount.compareTo(financeType.getFinMaxAmount()) > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(financeType.getFinMaxAmount());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90133", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}
		//ExtendedFieldDetails Validation
		String subModule = financeDetail.getFinScheduleData().getFinanceMain().getFinCategory();
		errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(financeDetail.getExtendedDetails(),
				ExtendedFieldConstants.MODULE_LOAN, subModule);
		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		logger.debug(Literal.LEAVING);
		
		return finScheduleData;
	}

	/**
	 * Method for validating Finance Schedule details
	 * 
	 * @param vldCrtSchd
	 * @param finScheduleData
	 * @param apiFlag
	 */
	public void financeDataValidation(String vldCrtSchd, FinScheduleData finScheduleData, boolean apiFlag) {
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.setFinScheduleData(finScheduleData);
		financeDataValidation(vldCrtSchd, financeDetail, apiFlag);
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}
}
