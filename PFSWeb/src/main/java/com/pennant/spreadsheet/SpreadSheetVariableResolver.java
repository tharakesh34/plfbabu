package com.pennant.spreadsheet;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Sessions;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
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
}
