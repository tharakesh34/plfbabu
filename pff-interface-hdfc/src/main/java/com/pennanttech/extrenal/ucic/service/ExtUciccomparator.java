package com.pennanttech.extrenal.ucic.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.pennanttech.extrenal.ucic.model.ExtUcicCust;

public interface ExtUciccomparator {

	default boolean compareCustomerData(ExtUcicCust newCustomer, ExtUcicCust existingCustomer) {

		return tryValidate(newCustomer.getCreationDate(), existingCustomer.getCreationDate())
				&& tryValidate(newCustomer.getCustCtgCode(), existingCustomer.getCustCtgCode())
				&& tryValidate(newCustomer.getLastMntOn(), existingCustomer.getLastMntOn())
				&& tryValidate(newCustomer.getSubCategory(), existingCustomer.getSubCategory())
				&& tryValidate(newCustomer.getCustShrtName(), existingCustomer.getCustShrtName())
				&& tryValidate(newCustomer.getCustDob(), existingCustomer.getCustDob())
				&& tryValidate(newCustomer.getCloseDate(), existingCustomer.getCloseDate())
				&& tryValidate(newCustomer.getClosingStatus(), existingCustomer.getClosingStatus())
				&& tryValidate(newCustomer.getCustMotherMaiden(), existingCustomer.getCustMotherMaiden())
				&& tryValidate(newCustomer.getAccNumber(), existingCustomer.getAccNumber())
				&& tryValidate(newCustomer.getCompanyName(), existingCustomer.getCompanyName())
				&& tryValidate(newCustomer.getPan(), existingCustomer.getPan())
				&& tryValidate(newCustomer.getAadhaar(), existingCustomer.getAadhaar())
				&& tryValidate(newCustomer.getVoterId(), existingCustomer.getVoterId())
				&& tryValidate(newCustomer.getDrivingLicence(), existingCustomer.getDrivingLicence())
				&& tryValidate(newCustomer.getPassport(), existingCustomer.getPassport())
				&& tryValidate(newCustomer.getEmail1(), existingCustomer.getEmail1())
				&& tryValidate(newCustomer.getEmail2(), existingCustomer.getEmail2())
				&& tryValidate(newCustomer.getEmail3(), existingCustomer.getEmail3())
				&& tryValidate(newCustomer.getMobile1(), existingCustomer.getMobile1())
				&& tryValidate(newCustomer.getMobile2(), existingCustomer.getMobile2())
				&& tryValidate(newCustomer.getMobile3(), existingCustomer.getMobile3())
				&& tryValidate(newCustomer.getLandLine1(), existingCustomer.getLandLine1())
				&& tryValidate(newCustomer.getLandLine2(), existingCustomer.getLandLine2())
				&& tryValidate(newCustomer.getLandLine3(), existingCustomer.getLandLine3())

				&& tryValidate(newCustomer.getAddr1Type(), existingCustomer.getAddr1Type())
				&& tryValidate(newCustomer.getAddr1Line1(), existingCustomer.getAddr1Line1())
				&& tryValidate(newCustomer.getAddr1Line2(), existingCustomer.getAddr1Line2())
				&& tryValidate(newCustomer.getAddr1Line3(), existingCustomer.getAddr1Line3())
				&& tryValidate(newCustomer.getAddr1Line4(), existingCustomer.getAddr1Line4())
				&& tryValidate(newCustomer.getAddr1City(), existingCustomer.getAddr1City())
				&& tryValidate(newCustomer.getAddr1State(), existingCustomer.getAddr1State())
				&& tryValidate(newCustomer.getAddr1Pin(), existingCustomer.getAddr1Pin())

				&& tryValidate(newCustomer.getAddr2Type(), existingCustomer.getAddr2Type())
				&& tryValidate(newCustomer.getAddr2Line1(), existingCustomer.getAddr2Line1())
				&& tryValidate(newCustomer.getAddr2Line2(), existingCustomer.getAddr2Line2())
				&& tryValidate(newCustomer.getAddr2Line3(), existingCustomer.getAddr2Line3())
				&& tryValidate(newCustomer.getAddr2Line4(), existingCustomer.getAddr2Line4())
				&& tryValidate(newCustomer.getAddr2City(), existingCustomer.getAddr2City())
				&& tryValidate(newCustomer.getAddr2State(), existingCustomer.getAddr2State())
				&& tryValidate(newCustomer.getAddr2Pin(), existingCustomer.getAddr2Pin())

				&& tryValidate(newCustomer.getAddr3Type(), existingCustomer.getAddr3Type())
				&& tryValidate(newCustomer.getAddr3Line1(), existingCustomer.getAddr3Line1())
				&& tryValidate(newCustomer.getAddr3Line2(), existingCustomer.getAddr3Line2())
				&& tryValidate(newCustomer.getAddr3Line3(), existingCustomer.getAddr3Line3())
				&& tryValidate(newCustomer.getAddr3Line4(), existingCustomer.getAddr3Line4())
				&& tryValidate(newCustomer.getAddr3City(), existingCustomer.getAddr3City())
				&& tryValidate(newCustomer.getAddr3State(), existingCustomer.getAddr3State())
				&& tryValidate(newCustomer.getAddr3Pin(), existingCustomer.getAddr3Pin());

	}

	default boolean tryValidate(String str1, String str2) {
		return StringUtils.stripToEmpty(str1).equals(StringUtils.stripToEmpty(str2));
	}

	default boolean tryValidate(Date str1, Date str2) {
		if (str1 == null && str2 == null) {
			return true;
		} else {
			if (str1.compareTo(str2) == 0) {
				return true;
			}
		}
		return false;
	}

}
