package com.pennant.equation.process.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.nbc.BondPurchaseDetail;
import com.pennant.coreinterface.model.nbc.NationalBondDetail;
import com.pennant.coreinterface.process.NationalBondProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class NationalBondProcessImpl implements NationalBondProcess {
	private static final Logger logger = Logger.getLogger(NationalBondProcessImpl.class);

	public NationalBondProcessImpl() {
		super();
	}
	
	@Override
	public NationalBondDetail doBondPurchase(String refNumConsumer,	BigDecimal amount) throws InterfaceException {
		logger.debug("Entering");

		NationalBondDetail detail = new NationalBondDetail();
		
		BondPurchaseDetail purchaseDetail = new BondPurchaseDetail();
		purchaseDetail.setPurchaseReceiptNo("123456789");
		purchaseDetail.setUnitStart("12345");
		purchaseDetail.setUnitEnd("65412");
		
		List<BondPurchaseDetail> detailList = new ArrayList<BondPurchaseDetail>();
		detailList.add(purchaseDetail);
		
		detail.setPurchaseDetailList(detailList);
		detail.setReturnCode("0000");
		detail.setReturnText("SUCCESS");

		logger.debug("Leaving");
		return detail;
	}

	@Override
	public NationalBondDetail doBondTransfer(NationalBondDetail nationalBondDetail) throws InterfaceException {
		logger.debug("Entering");

		NationalBondDetail detail = new NationalBondDetail();
		detail.setReturnCode("0000");
		detail.setReturnText("SUCCESS");

		logger.debug("Leaving");
		return detail;
	}

	@Override
	public NationalBondDetail cancelBondTransfer(String refNumProvider, String refNumConsumer) 
			throws InterfaceException {
		logger.debug("Entering");

		NationalBondDetail detail = new NationalBondDetail();
		detail.setReturnCode("0000");
		detail.setReturnText("SUCCESS");

		logger.debug("Leaving");
		return detail;
	}

	@Override
	public NationalBondDetail cancelBondPurchase(String refNumProvider,	String refNumConsumer)
			throws InterfaceException {
		logger.debug("Entering");

		NationalBondDetail detail = new NationalBondDetail();
		detail.setReturnCode("0000");
		detail.setReturnText("SUCCESS");

		logger.debug("Leaving");
		return detail;
	}
}