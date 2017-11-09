package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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

public class CustomizeFinanceDataValidation {
	private CustomerDAO					customerDAO;
	private ExtendedFieldDetailsService	extendedFieldDetailsService;

	public FinScheduleData financeDataValidation(String vldGroup, FinanceDetail financeDetail, boolean apiFlag) {
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
		
		return finScheduleData;
	}
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}
	
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}
}
