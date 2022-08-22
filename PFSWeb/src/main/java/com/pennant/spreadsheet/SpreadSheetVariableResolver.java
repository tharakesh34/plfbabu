package com.pennant.spreadsheet;

import org.apache.commons.collections.CollectionUtils;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Sessions;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.spreadsheet.SpreadSheet;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;

public class SpreadSheetVariableResolver implements VariableResolver {

	int finFormatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

	public SpreadSheetVariableResolver() {
		super();
	}

	public CustomerService getCustomerService() {
		if (customerService == null) {
			customerService = (CustomerService) SpringBeanUtil.getBean("customerService");
		}
		return customerService;
	}

	private CustomerService customerService;

	@Override
	public Object resolveVariable(String name) throws XelException {

		if (!"ss".equals(name)) {
			return null;
		}
		if ("ss".equals(name)) {
			SpreadSheet ss = (SpreadSheet) Sessions.getCurrent().getAttribute("ss");
			return ss;
		}

		return null;
	}

	private void setCoApplicantData(SpreadSheet spreadSheet, FinanceDetail financeDetail) {
		if (CollectionUtils.isNotEmpty(financeDetail.getJointAccountDetailList())) {
			if (financeDetail.getJointAccountDetailList().get(0) != null) {
				spreadSheet.setCu1(getCustomerService().getCustomerDetailForFinancials(
						financeDetail.getJointAccountDetailList().get(0).getCustCIF(), "_View"));
				setCustomerName(spreadSheet, spreadSheet.getCu1());

			}

			if (financeDetail.getJointAccountDetailList().size() > 1
					&& financeDetail.getJointAccountDetailList().get(1) != null) {
				spreadSheet.setCu2(getCustomerService().getCustomerDetailForFinancials(
						financeDetail.getJointAccountDetailList().get(1).getCustCIF(), "_View"));
				setCustomerName(spreadSheet, spreadSheet.getCu2());

			}

			if (financeDetail.getJointAccountDetailList().size() > 2
					&& financeDetail.getJointAccountDetailList().get(2) != null) {
				spreadSheet.setCu3(getCustomerService().getCustomerDetailForFinancials(
						financeDetail.getJointAccountDetailList().get(2).getCustCIF(), "_View"));
				setCustomerName(spreadSheet, spreadSheet.getCu3());

			}

			if (financeDetail.getJointAccountDetailList().size() > 3
					&& financeDetail.getJointAccountDetailList().get(3) != null) {
				spreadSheet.setCu4(getCustomerService().getCustomerDetailForFinancials(
						financeDetail.getJointAccountDetailList().get(3).getCustCIF(), "_View"));
				setCustomerName(spreadSheet, spreadSheet.getCu4());

			}

			if (financeDetail.getJointAccountDetailList().size() > 4
					&& financeDetail.getJointAccountDetailList().get(4) != null) {
				spreadSheet.setCu5(getCustomerService().getCustomerDetailForFinancials(
						financeDetail.getJointAccountDetailList().get(4).getCustCIF(), "_View"));
				setCustomerName(spreadSheet, spreadSheet.getCu5());

			}
		}

	}

	private void setCustomerName(SpreadSheet spreadSheet, Customer customer) {

		if (customer.getCustCtgCode().equals("SME") || customer.getCustCtgCode().equals("CORP")) {
			customer.setCustomerFullName(customer.getCustShrtName());
		} else {
			customer.setCustomerFullName(
					customer.getCustFName().concat(customer.getCustMName().concat(customer.getCustLName())));

		}

	}

}
