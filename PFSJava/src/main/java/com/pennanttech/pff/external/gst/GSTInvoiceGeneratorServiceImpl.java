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
			String invoiceNo = null;
			for (GSTInvoiceTxn gstInvoiceTxn : gstInvoiceList) {
				SeqGSTInvoice seqGSTInvoice = new SeqGSTInvoice();
				seqGSTInvoice.setEntity(gstInvoiceTxn.getCompanyCode());
				seqGSTInvoice.setTransactionType(gstInvoiceTxn.getInvoiceType());
				seqGSTInvoice.setFromState(gstInvoiceTxn.getCompany_State_Code());
				long seqNo = this.gstInvoiceTxnDAO.getSeqNoFromSeqGSTInvoice(seqGSTInvoice);
				seqNo++;
				if (StringUtils.isNotBlank(gstInvoiceTxn.getCompany_State_Code())
						&& StringUtils.isNotBlank(gstInvoiceTxn.getCompanyCode())
						&& StringUtils.isNotBlank(gstInvoiceTxn.getInvoiceType())) {
					invoiceNo = gstInvoiceTxn.getCompany_State_Code() + gstInvoiceTxn.getCompanyCode() + gstInvoiceTxn.getInvoiceType() + seqNo;
				} else {
					continue;
				}
				
				gstInvoiceTxn.setInvoiceNo(invoiceNo);
				this.gstInvoiceTxnDAO.updateGSTInvoiceNo(gstInvoiceTxn);
				seqGSTInvoice.setSeqNo(seqNo);
				this.gstInvoiceTxnDAO.updateSeqGSTInvoice(seqGSTInvoice);
			}
		}
	}

	public void setGstInvoiceTxnDAO(GSTInvoiceTxnDAO gstInvoiceTxnDAO) {
		this.gstInvoiceTxnDAO = gstInvoiceTxnDAO;
	}

}