package com.pennanttech.pff.external;

import com.pennanttech.pennapps.core.model.LoggedInUser;

public interface AadharVaultService {

	/**
	 * Fetch aadharVault Reference Number for the aadhar number.
	 * 
	 * @param aadharNumber aadharIdnumber .
	 * @param LoggedInUser loggedInUser logged in user details.
	 * @param userID       Logged in User.
	 * @return aadharVault Number.
	 */
	String getReferenceKeyByUID(String aadharNumber, LoggedInUser loggedInUser);

	/**
	 * Fetch aadhar Number for the aadharVault Reference Number
	 * 
	 * @param aadharVault  aadharIdnumber .
	 * @param LoggedInUser loggedInUser logged in user details.
	 * @return aadhar Number.
	 */
	String retrieveAadhaarVault(String aadharVault, LoggedInUser loggedInUser);

	/**
	 * Store the aadhar Number and return aadharVault Number
	 * 
	 * @param aadharNumber aadharNumber .
	 * @param LoggedInUser loggedInUser logged in user details.
	 * @return aadharVault Number.
	 */
	String storeAadharVault(String aadharNumber, LoggedInUser loggedInUser);

}
