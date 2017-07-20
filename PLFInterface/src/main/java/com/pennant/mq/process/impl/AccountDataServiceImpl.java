package com.pennant.mq.process.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.AccountBalance;
import com.pennant.coreinterface.model.account.InterfaceAccount;
import com.pennant.coreinterface.model.collateral.CollateralMark;
import com.pennant.coreinterface.process.AccountDataProcess;
import com.pennant.mq.processutil.AddOrRemoveHoldProcess;
import com.pennant.mq.processutil.CollateralMarkProcess;
import com.pennant.mq.processutil.CreateAccountProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountDataServiceImpl implements AccountDataProcess {

	private static final Logger logger = Logger.getLogger(AccountDataServiceImpl.class);

	private AddOrRemoveHoldProcess addOrRemoveHoldProcess;
	private CreateAccountProcess createAccountProcess;
	private CollateralMarkProcess collateralMarkProcess;

	public AccountDataServiceImpl() {

	}
	
	
	/**
	 * Remove the Hold from Customer Account<br>
	 * 
	 * removeAccountHold method do the following steps.<br>
	 * 1) Send RemoveHold Request to MQ<br>
	 * 2) Receive Response from MQ
	 * 
	 * @return RemoveHoldReply
	 */
	@Override
	public int removeAccountHolds() throws InterfaceException {
		logger.debug("Enering");
		/*RemoveHoldReply removeAccHold = removeHoldProcess.sendRemoveHoldRequest(removeHold, 
				InterfaceMasterConfigUtil.REMOVE_HOLD);

		return removeAccHold;*/
		logger.debug("Leaving");
		return 0;
	}

	/**
	 * Hold the Customer Account<br>
	 * 
	 * addAccountHold method do the following steps.<br>
	 * 1) Send AddHold Request to MQ<br>
	 * 2) Receive Response from MQ
	 * 
	 * @return AddHoldDetail
	 */
	@Override
	public List<AccountBalance> addAccountHolds(List<AccountBalance> accountslist, String holdType)
			throws InterfaceException {
		logger.debug("Enering");

		/*AddHoldReply accHoldStatus = addHoldProcess.sendAddHoldRequest(holdStatus,
				InterfaceMasterConfigUtil.ADD_HOLD);

		return accHoldStatus;*/
		logger.debug("Leaving");
		return null;
	}

	/**
	 * This method is used to create new account in core banking<br>
	 * 
	 * createAccount method do the following steps.<br>
	 * 1) Send create Account Request to core bank through MQ<br>
	 * 2) Receive Response from core bank through MQ
	 * 
	 * @return CoreBankAccountDetail
	 */
	@Override
	public InterfaceAccount createAccount(InterfaceAccount accountdetail) throws InterfaceException {
		logger.debug("Entering");

		InterfaceAccount interfaceAccount = getCreateAccountProcess().createAccount(accountdetail, 
				InterfaceMasterConfigUtil.CREATE_ACCOUNT);

		logger.debug("Leaving");

		return interfaceAccount;
	}

	/********* Excess Interface Connection calls ********/

	/**
	 * This method is used to Mark the collateral in core banking<br>
	 * 
	 * collateralMarking method do the following steps.<br>
	 * 1) Send Mark collateral Request to core bank through MQ<br>
	 * 2) Receive Response from core bank through MQ
	 * 
	 */
	@Override
	public CollateralMark collateralMarking(CollateralMark collateralMarking) throws InterfaceException {
		logger.debug("Entering");

		CollateralMark coolateralMarkReply = getCollateralMarkProcess().markCollateral(collateralMarking, 
				InterfaceMasterConfigUtil.COLLATERAL_MARKING);

		logger.debug("Leaving");

		return coolateralMarkReply;

	}

	/**
	 * This method is used to DeMark the collateral in core banking<br>
	 * 
	 * collateralDeMarking method do the following steps.<br>
	 * 1) Send DeMark collateral Request to core bank through MQ<br>
	 * 2) Receive Response from core bank through MQ
	 * 
	 */
	@Override
	public CollateralMark collateralDeMarking(CollateralMark collateralDeMarking)	
			throws InterfaceException {
		logger.debug("Entering");

		CollateralMark collateralDeMarkReply = getCollateralMarkProcess().markCollateral(collateralDeMarking, 
				InterfaceMasterConfigUtil.COLLATERAL_DEMARKING);

		logger.debug("Leaving");

		return collateralDeMarkReply;
	}
	
	public AddOrRemoveHoldProcess getAddOrRemoveHoldProcess() {
		return addOrRemoveHoldProcess;
	}

	public void setAddOrRemoveHoldProcess(AddOrRemoveHoldProcess addOrRemoveHoldProcess) {
		this.addOrRemoveHoldProcess = addOrRemoveHoldProcess;
	}

	public CollateralMarkProcess getCollateralMarkProcess() {
		return collateralMarkProcess;
	}

	public void setCollateralMarkProcess(CollateralMarkProcess collateralMarkProcess) {
		this.collateralMarkProcess = collateralMarkProcess;
	}
	public CreateAccountProcess getCreateAccountProcess() {
		return createAccountProcess;
	}
	public void setCreateAccountProcess(CreateAccountProcess createAccountProcess) {
		this.createAccountProcess = createAccountProcess;
	}

}
