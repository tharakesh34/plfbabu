package com.pennant.corebanking.process.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.CoreDocumentDetails;
import com.pennant.coreinterface.model.nbc.BondPurchaseDetail;
import com.pennant.coreinterface.model.nbc.BondTransferDetail;
import com.pennant.coreinterface.model.nbc.NationalBondDetail;
import com.pennant.coreinterface.process.NationalBondProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class NationalBondProcessImpl implements NationalBondProcess {
	
	private static final Logger logger = Logger.getLogger(NationalBondProcessImpl.class);

	private InterfaceDAO interfaceDAO;
	
	public NationalBondProcessImpl() {
		super();
	}
	
	@Override
	public NationalBondDetail doBondPurchase(String refNumConsumer,	BigDecimal amount) throws InterfaceException {
		logger.debug("Entering");

		NationalBondDetail detail = new NationalBondDetail();
		
		BondPurchaseDetail purchaseDetail = new BondPurchaseDetail();
		purchaseDetail.setPurchaseReceiptNo("966201248");
		purchaseDetail.setUnitStart("12345");
		purchaseDetail.setUnitEnd("65412");
		
		// fetch Bank Title Certificate from database
		List<CoreDocumentDetails> docDetailList = getInterfaceDAO().getDocumentDetailsByRef("FINPUR1");
		for(CoreDocumentDetails docDetail:docDetailList) {
			Base64 base64 = new Base64();
			byte[] enCodedData = base64.encode(docDetail.getDocImage());
			purchaseDetail.setBankTitleCertifcate(enCodedData);
		}
		
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

		NationalBondDetail bondDetail = new NationalBondDetail();

		BondTransferDetail transDetail = new BondTransferDetail();
		transDetail.setUnitStart("12345");
		transDetail.setUnitEnd("65412");
		if(StringUtils.equals(nationalBondDetail.getTransferLevel(), "TRANS_MAKER")) {
			if(!StringUtils.equals("966201248", nationalBondDetail.getRefNumProvider())) {
				throw new InterfaceException("PTI9001", "Inavlid Provider Reference number");
			}
			transDetail.setSukukNo(96662012);
			transDetail.setPurchaseReceiptNo("8125780");
		} else {
			if(!StringUtils.equals("8125780", nationalBondDetail.getRefNumProvider())) {
				throw new InterfaceException("PTI9002", "Inavlid Provider Reference number");
			}
			transDetail.setSukukNo(8985957);
			transDetail.setPurchaseReceiptNo("8985957");
			
			// fetch Bank Title Certificate from database
			List<CoreDocumentDetails> docDetailList = getInterfaceDAO().getDocumentDetailsByRef("FINTRANS1");
			for(CoreDocumentDetails docDetail:docDetailList) {
				Base64 base64 = new Base64();
				byte[] enCodedData = base64.encode(docDetail.getDocImage());
				transDetail.setTitleCertificate(enCodedData);
			}
		}

		List<BondTransferDetail> detailList = new ArrayList<BondTransferDetail>();
		detailList.add(transDetail);

		bondDetail.setTransferDetailList(detailList);
		bondDetail.setReturnCode("0000");
		bondDetail.setReturnText("SUCCESS");

		logger.debug("Leaving");
		return bondDetail;	
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
	
	public InterfaceDAO getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}
}