package com.pennant.corebanking.process.impl;

import java.util.List;

import com.pennant.coreinterface.model.AccountBalance;
import com.pennant.coreinterface.model.account.InterfaceAccount;
import com.pennant.coreinterface.model.collateral.CollateralMark;
import com.pennant.coreinterface.process.AccountDataProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountDataProcessImpl extends GenericProcess implements AccountDataProcess{

	public AccountDataProcessImpl() {
		super();
	}
	
	@Override
	public int removeAccountHolds() throws InterfaceException {
		return 0;
	}

	@Override
	public List<AccountBalance> addAccountHolds(List<AccountBalance> accountslIst, String holdType)
			throws InterfaceException {
		return null;
	}
	
	/**
	 * Method to create customer Account in corebank system
	 * 
	 */
	@Override
	public InterfaceAccount createAccount(InterfaceAccount accountdetail) throws InterfaceException {
		InterfaceAccount interfaceAccount = new InterfaceAccount();
		interfaceAccount.setAccountNumber("123456789789");
		return interfaceAccount;
	}
	@Override
	public CollateralMark collateralMarking(CollateralMark collateralMark) throws InterfaceException {
		CollateralMark collateralMarkRes = new CollateralMark();
		collateralMarkRes.setReferenceNum("12365478");
		collateralMarkRes.setReturnCode("0000");
		return collateralMarkRes;
	}

	@Override
	public CollateralMark collateralDeMarking(CollateralMark collateralMark) throws InterfaceException {
		CollateralMark collateralMarkRes = new CollateralMark();
		collateralMarkRes.setReferenceNum("12365478");
		collateralMarkRes.setReturnCode("0000");
		return collateralMarkRes;
	}
}
