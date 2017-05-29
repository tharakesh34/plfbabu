package com.pennant.Interface.service;

import com.pennant.coreinterface.model.chequeverification.ChequeVerification;
import com.pennant.exception.PFFInterfaceException;

public interface ChequeVerifyInterfaceService {

	ChequeVerification verifySecurityCheque(ChequeVerification chequeVerification) throws PFFInterfaceException;
}
