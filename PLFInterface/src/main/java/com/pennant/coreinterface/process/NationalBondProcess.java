package com.pennant.coreinterface.process;

import java.math.BigDecimal;

import com.pennant.coreinterface.model.nbc.NationalBondDetail;
import com.pennant.exception.PFFInterfaceException;

public interface NationalBondProcess {
	NationalBondDetail doBondPurchase(String refNumConsumer, BigDecimal amount) throws PFFInterfaceException;

	NationalBondDetail doBondTransfer(NationalBondDetail nationalBondDetail) throws PFFInterfaceException;

	NationalBondDetail cancelBondTransfer(String refNumProvider, String refNumConsumer) throws PFFInterfaceException;

	NationalBondDetail cancelBondPurchase(String refNumProvider, String refNumConsumer) throws PFFInterfaceException;
}
