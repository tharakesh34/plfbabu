package com.pennanttech.pff.external.gst;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.SeqGSTInvoice;

public class GSTInvoiceGeneratorServiceImpl implements GSTInvoiceGeneratorService {

	private GSTInvoiceTxnDAO  gstInvoiceTxnDAO;
	
	/**
	 * generate Invoice number
	 */
	@Override
	public void generateInvoice() {
		
		List<GSTInvoiceTxn> gstInvoiceList = this.gstInvoiceTxnDAO.getGSTInvoiceTxnList();
		
		if (CollectionUtils.isNotEmpty(gstInvoiceList)) {
			
			for (GSTInvoiceTxn gstInvoiceTxn : gstInvoiceList) {
				
				if (StringUtils.isNotBlank(gstInvoiceTxn.getCompany_GSTIN()) && StringUtils.isNotBlank(gstInvoiceTxn.getInvoiceType())) {
					SeqGSTInvoice seqGSTInvoice = new SeqGSTInvoice();
					seqGSTInvoice.setTransactionType(gstInvoiceTxn.getInvoiceType());
					String gstStateCode = gstInvoiceTxn.getCompany_GSTIN().substring(0, 2);
					seqGSTInvoice.setGstStateCode(gstStateCode);
					long seqNo = this.gstInvoiceTxnDAO.getSeqNoFromSeqGSTInvoice(seqGSTInvoice);
					seqNo++;
					String invoiceNo = gstStateCode + gstInvoiceTxn.getInvoiceType() + StringUtils.leftPad(String.valueOf(seqNo), 13, "0");
					gstInvoiceTxn.setInvoiceNo(invoiceNo);
					this.gstInvoiceTxnDAO.updateGSTInvoiceNo(gstInvoiceTxn);
					seqGSTInvoice.setSeqNo(seqNo);
					this.gstInvoiceTxnDAO.updateSeqGSTInvoice(seqGSTInvoice);
				}
			}
		}
	}

	public void setGstInvoiceTxnDAO(GSTInvoiceTxnDAO gstInvoiceTxnDAO) {
		this.gstInvoiceTxnDAO = gstInvoiceTxnDAO;
	}

}