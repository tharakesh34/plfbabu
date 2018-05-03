package com.pennant.coreinterface.process;

import java.math.BigDecimal;

import com.pennant.coreinterface.model.nbc.NationalBondDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public interface NationalBondProcess {
	NationalBondDetail doBondPurchase(String refNumConsumer, BigDecimal amount) throws InterfaceException;

	NationalBondDetail doBondTransfer(NationalBondDetail nationalBondDetail) throws InterfaceException;

	NationalBondDetail cancelBondTransfer(String refNumProvider, String refNumConsumer) throws InterfaceException;

	NationalBondDetail cancelBondPurchase(String refNumProvider, String refNumConsumer) throws InterfaceException;
}
