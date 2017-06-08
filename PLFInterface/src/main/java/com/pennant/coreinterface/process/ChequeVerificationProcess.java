package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.chequeverification.ChequeVerification;
import com.pennanttech.pff.core.InterfaceException;

public interface ChequeVerificationProcess {

	ChequeVerification sendChequeVerificationReq(ChequeVerification chequeVerification) throws InterfaceException;
}
