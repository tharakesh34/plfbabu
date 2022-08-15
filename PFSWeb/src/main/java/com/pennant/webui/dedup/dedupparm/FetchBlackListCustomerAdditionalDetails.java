package com.pennant.webui.dedup.dedupparm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class FetchBlackListCustomerAdditionalDetails {
	private static final Logger logger = LogManager.getLogger(FetchBlackListCustomerAdditionalDetails.class);

	/**
	 * Prepare Black List Customer Object Data
	 * 
	 * @param blackListCustomer
	 * @param customer
	 * @return blackListCustomer
	 */
	public static BlackListCustomers doSetCustDataToBlackList(Customer customer, BlackListCustomers blackListCustomer) {
		logger.debug(Literal.ENTERING);
		if (customer != null) {
			// Black list customer dedupe for directors
			if (PennantConstants.PFF_CUSTCTG_INDIV.equals(customer.getCustCtgCode())) {
				blackListCustomer.setDirector1FirstName(customer.getCustFName());
				blackListCustomer.setDirector1LastName(customer.getCustLName());
				blackListCustomer.setDirector2FirstName(customer.getCustFName());
				blackListCustomer.setDirector2LastName(customer.getCustLName());
				blackListCustomer.setDirector3FirstName(customer.getCustFName());
				blackListCustomer.setDirector3LastName(customer.getCustLName());
				blackListCustomer.setDirector4FirstName(customer.getCustFName());
				blackListCustomer.setDirector4LastName(customer.getCustLName());
				blackListCustomer.setDirector5FirstName(customer.getCustFName());
				blackListCustomer.setDirector5LastName(customer.getCustLName());
				blackListCustomer.setDirector6FirstName(customer.getCustFName());
				blackListCustomer.setDirector6LastName(customer.getCustLName());
				blackListCustomer.setDirector7FirstName(customer.getCustFName());
				blackListCustomer.setDirector7LastName(customer.getCustLName());
				blackListCustomer.setDirector8FirstName(customer.getCustFName());
				blackListCustomer.setDirector8LastName(customer.getCustLName());
				blackListCustomer.setDirector9FirstName(customer.getCustFName());
				blackListCustomer.setDirector9LastName(customer.getCustLName());
				blackListCustomer.setDirector10FirstName(customer.getCustFName());
				blackListCustomer.setDirector10LastName(customer.getCustLName());
				// Associate or Related Concern
				blackListCustomer.setAssOrRelConcernFName(customer.getCustFName());
				blackListCustomer.setAssOrRelConcernLName(customer.getCustLName());
				// OtherSourceFirstName or OtherSourceLastName
				blackListCustomer.setOtherSourceFirstName(customer.getCustFName());
				blackListCustomer.setOtherSourceLastName(customer.getCustLName());

				// setting the customer F/L Name to Company filed for Retail
				blackListCustomer.setCustCompName(blackListCustomer.getCustFName());
				blackListCustomer.setCustCompName2(blackListCustomer.getCustLName());

				// Like Operator
				blackListCustomer.setLikeDirector1FirstName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeDirector1LastName(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
				blackListCustomer.setLikeDirector2FirstName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeDirector2LastName(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
				blackListCustomer.setLikeDirector3FirstName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeDirector3LastName(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
				blackListCustomer.setLikeDirector4FirstName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeDirector4LastName(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
				blackListCustomer.setLikeDirector5FirstName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeDirector5LastName(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
				blackListCustomer.setLikeDirector6FirstName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeDirector6LastName(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
				blackListCustomer.setLikeDirector7FirstName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeDirector7LastName(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
				blackListCustomer.setLikeDirector8FirstName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeDirector8LastName(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
				blackListCustomer.setLikeDirector9FirstName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeDirector9LastName(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
				blackListCustomer.setLikeDirector10FirstName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeDirector10LastName(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
				// setting the customer F/L Name to Company filed for Retail
				blackListCustomer.setLikeCustCompName(
						blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
				blackListCustomer.setLikeCustCompName2(
						blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");

			} else {
				blackListCustomer.setDirector1Name(customer.getCustShrtName());
				blackListCustomer.setDirector2Name(customer.getCustShrtName());
				blackListCustomer.setDirector3Name(customer.getCustShrtName());
				blackListCustomer.setDirector4Name(customer.getCustShrtName());
				blackListCustomer.setDirector5Name(customer.getCustShrtName());
				blackListCustomer.setDirector6Name(customer.getCustShrtName());
				blackListCustomer.setDirector7Name(customer.getCustShrtName());
				blackListCustomer.setDirector8Name(customer.getCustShrtName());
				blackListCustomer.setDirector9Name(customer.getCustShrtName());
				blackListCustomer.setDirector10Name(customer.getCustShrtName());
				blackListCustomer.setAssOrRelConcern(customer.getCustShrtName());
				blackListCustomer.setOtherSource(customer.getCustShrtName());
				// Like Operator
				blackListCustomer.setLikeDirector1Name(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeDirector2Name(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeDirector3Name(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeDirector4Name(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeDirector5Name(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeDirector6Name(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeDirector7Name(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeDirector8Name(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeDirector9Name(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeDirector10Name(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeAssOrRelConcern(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				blackListCustomer.setLikeOtherSource(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
				// for CORP Customer Name to Comapny Name
				blackListCustomer.setCustCompName(customer.getCustShrtName());
				blackListCustomer.setLikeCustCompName(
						blackListCustomer.getCustShrtName() != null ? "%" + blackListCustomer.getCustShrtName() + "%"
								: "");
			}
			// setting customer short name to remarks
			blackListCustomer.setRemarks(customer.getCustShrtName());
			logger.debug(Literal.LEAVING);
			return blackListCustomer;
		}
		return blackListCustomer;
	}
}
