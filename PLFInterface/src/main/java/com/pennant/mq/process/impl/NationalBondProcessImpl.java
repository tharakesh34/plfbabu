package com.pennant.mq.process.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.nbc.NationalBondDetail;
import com.pennant.coreinterface.process.NationalBondProcess;
import com.pennant.mq.processutil.BondDetailProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class NationalBondProcessImpl implements NationalBondProcess {
	private static final Logger logger = Logger.getLogger(NationalBondProcessImpl.class);

	public NationalBondProcessImpl() {
		super();
	}

	private BondDetailProcess bondDetailProcess;

	/**
	 * Method for purchase National bonds by sending request to middleware
	 * 
	 * @param refNumConsumer
	 * @param amount
	 * @return NationalBondDetail
	 * 
	 * @throws InterfaceException
	 */
	@Override
	public NationalBondDetail doBondPurchase(String refNumConsumer,	BigDecimal amount) throws InterfaceException {
		logger.debug("Entering");

		NationalBondDetail detail = new NationalBondDetail();
		detail.setRefNumConsumer(refNumConsumer);
		detail.setAmount(amount);
		
		NationalBondDetail bondDetail = new NationalBondDetail() ;
		try {
			bondDetail = getBondDetailProcess().doNationalBondProcess(detail, InterfaceMasterConfigUtil.BOND_PURCHASE_INSTANT);
		} catch (JaxenException e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI9009", e.getMessage());
		}
		
		logger.debug("Leaving");
		return bondDetail;
	}

	/**
	 * Method for transfer National bonds by sending request to middleware<br>
	 * The below are the transfer types<br>
	 * 1. Maker 2. Checker
	 * 
	 * @param nationalBondDetail
	 * @param type
	 * @return NationalBondDetail
	 * 
	 * @throws InterfaceException
	 */
	@Override
	public NationalBondDetail doBondTransfer(NationalBondDetail nationalBondDetail)	throws InterfaceException {
		logger.debug("Entering");

		NationalBondDetail response;
		try {
			String transType = InterfaceMasterConfigUtil.BOND_TRANSFER_MAKER;
			if(StringUtils.equals(nationalBondDetail.getTransferLevel(), "TRANS_CHECKER")) {
				transType = InterfaceMasterConfigUtil.BOND_TRANSFER_CHECKER;
			}
			response = getBondDetailProcess().doNationalBondProcess(nationalBondDetail, transType);
		} catch (JaxenException e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI9009", e.getMessage());
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for cancel National bond transfer by sending request to middleware
	 * 
	 * @param refNumProvider
	 * @param refNumConsumer
	 * @return NationalBondDetail
	 * 
	 * @throws InterfaceException
	 */
	@Override
	public NationalBondDetail cancelBondTransfer(String refNumProvider,	String refNumConsumer) 
			throws InterfaceException {
		logger.debug("Entering");

		NationalBondDetail detail = new NationalBondDetail();
		detail.setRefNumProvider(refNumProvider);
		detail.setRefNumConsumer(refNumConsumer);
		NationalBondDetail response;
		try {
			response = getBondDetailProcess().doNationalBondProcess(detail, 
					InterfaceMasterConfigUtil.BOND_CANCEL_TRANSFER);
		} catch (JaxenException e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI9009", e.getMessage());
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for cancel National bond purchase by sending request to middleware
	 * 
	 * @param refNumProvider
	 * @param refNumConsumer
	 * @return NationalBondDetail
	 * 
	 * @throws InterfaceException
	 */
	@Override
	public NationalBondDetail cancelBondPurchase(String refNumProvider,	String refNumConsumer) 
			throws InterfaceException {
		logger.debug("Entering");

		NationalBondDetail detail = new NationalBondDetail();
		detail.setRefNumProvider(refNumProvider);
		detail.setRefNumConsumer(refNumConsumer);
		NationalBondDetail response = null;
		try {
			response = getBondDetailProcess().doNationalBondProcess(detail, InterfaceMasterConfigUtil.BOND_CANCEL_PURCHASE);
		} catch (JaxenException e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI9009", e.getMessage());
		}
		logger.debug("Leaving");
		return response;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public BondDetailProcess getBondDetailProcess() {
		return bondDetailProcess;
	}

	public void setBondDetailProcess(BondDetailProcess bondDetailProcess) {
		this.bondDetailProcess = bondDetailProcess;
	}
}