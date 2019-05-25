package com.pennanttech.pff.core.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.util.ClassUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;

/**
 * <p>
 * A suite of utilities for building the DataMap.
 * </p>
 */
public final class DataMapUtil {
	private DataMapUtil() {
		super();
	}

	public enum FieldPrefix {
		FinanceMain("fm_"),
		Customer("ct_"),
		CustomerAddress("cta_"),
		CustomerEmail("cte_"),
		CustomerPhoneNumber("ctp_"),
		Covenant("cvnt_"),
		Putcall("pc_"),
		CollateralLTVBreachs("cltvb_");

		private String prefix;

		private FieldPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getPrefix() {
			return prefix;

		}
	}

	public static Map<String, String> getDataMap(FinanceDetail financeDetail) {
		Map<String, String> data = new HashMap<>();
		int priority = Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH);

		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();

		Customer cu = null;
		List<CustomerAddres> custAddressList = null;
		List<CustomerEMail> customerEmailList = null;
		List<CustomerPhoneNumber> custMobiles = null;
		Covenant covenant = null;
		FinOption finOption = null;
		CollateralRevaluation collateralRevaluation = null;

		if (customerDetails != null) {
			cu = customerDetails.getCustomer();
			custAddressList = customerDetails.getAddressList();
			customerEmailList = customerDetails.getCustomerEMailList();
			custMobiles = customerDetails.getCustomerPhoneNumList();
		}

		data.putAll(getDataMap(fm, FieldPrefix.FinanceMain.getPrefix()));
		data.putAll(getDataMap(cu, FieldPrefix.Customer.getPrefix()));

		// Customer Address Details
		if (CollectionUtils.isNotEmpty(custAddressList)) {
			for (CustomerAddres customerAddress : custAddressList) {
				if (priority != customerAddress.getCustAddrPriority()) {
					continue;
				}
				data.putAll(getDataMap(customerAddress, FieldPrefix.CustomerAddress.getPrefix()));
				break;
			}
		}

		// Customer Email Details
		if (CollectionUtils.isNotEmpty(customerEmailList)) {
			for (CustomerEMail customerEMail : customerEmailList) {
				if (priority != customerEMail.getCustEMailPriority()) {
					continue;
				}
				data.putAll(getDataMap(customerEMail, FieldPrefix.CustomerEmail.getPrefix()));
				break;
			}
		}

		// Customer Contact Details
		if (CollectionUtils.isNotEmpty(custMobiles)) {
			for (CustomerPhoneNumber customerPhoneNumber : custMobiles) {
				if (priority != customerPhoneNumber.getPhoneTypePriority()) {
					continue;
				}
				data.putAll(getDataMap(customerPhoneNumber, FieldPrefix.CustomerPhoneNumber.getPrefix()));
				break;
			}
		}

		covenant = financeDetail.getCovenant();
		data.putAll(getDataMap(covenant, FieldPrefix.Covenant.getPrefix()));

		finOption = financeDetail.getFinOption();
		data.putAll(getDataMap(finOption, FieldPrefix.Putcall.getPrefix()));

		collateralRevaluation = financeDetail.getCollateralRevaluation();
		data.putAll(getDataMap(collateralRevaluation, FieldPrefix.CollateralLTVBreachs.getPrefix()));

		return data;
	}

	public static Map<String, String> getDataMap(Object obj, String prefix) {
		Map<String, String> data = new HashMap<>();

		if (obj == null) {
			return data;
		}

		Field[] fields = ClassUtil.getAllFields(obj);
		for (Field field : fields) {
			data.put(prefix.concat(field.getName()), getValue(field, obj));
		}
		return data;
	}

	private static String getValue(Field field, Object obj) {
		Object value;
		try {
			field.setAccessible(true);
			value = field.get(obj);

			field.setAccessible(false);
			if (value instanceof Date) {
				return DateUtil.format((Date) value, DateFormat.LONG_DATE);
			} else if (value instanceof BigDecimal) {
				return PennantApplicationUtil.amountFormate((BigDecimal) value, 2);
			} else if (value != null) {
				return value.toString();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {

		}
		return "";
	}
}
