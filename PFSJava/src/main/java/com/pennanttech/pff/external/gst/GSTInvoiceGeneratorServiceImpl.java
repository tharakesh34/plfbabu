package com.pennanttech.pff.external.gst;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.SeqGSTInvoice;
import com.pennant.backend.util.PennantConstants;

public class GSTInvoiceGeneratorServiceImpl implements GSTInvoiceGeneratorService {
	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;

	/**
	 * generate Invoice number
	 */
	@Override
	public void generateInvoice() {

		List<GSTInvoiceTxn> gstInvoiceList = this.gstInvoiceTxnDAO.getGSTInvoiceTxnList();

		if (CollectionUtils.isNotEmpty(gstInvoiceList)) {

			for (GSTInvoiceTxn gstInvoiceTxn : gstInvoiceList) {

				if (StringUtils.isNotBlank(gstInvoiceTxn.getCompany_GSTIN())
						&& StringUtils.isNotBlank(gstInvoiceTxn.getInvoiceType())) {
					SeqGSTInvoice seqGSTInvoice = new SeqGSTInvoice();
					seqGSTInvoice.setTransactionType(gstInvoiceTxn.getInvoiceType());

					String entityCode = "";
					if (StringUtils.isNotBlank(gstInvoiceTxn.getCompanyCode())) {
						if (gstInvoiceTxn.getCompanyCode().length() >= 2) {
							entityCode = gstInvoiceTxn.getCompanyCode().substring(0, 2);
						} else {
							entityCode = gstInvoiceTxn.getCompanyCode();
						}
					}
					String gstStateCode = gstInvoiceTxn.getCompany_GSTIN().substring(0, 2);
					String appDate = DateUtility.getAppDate(PennantConstants.dateFormat);

					String[] dateformat = appDate.split("/");
					String month = dateformat[1];
					String year = dateformat[2].substring(2, 4);

					seqGSTInvoice.setGstStateCode(gstStateCode);
					long seqNo = this.gstInvoiceTxnDAO.getSeqNoFromSeqGSTInvoice(seqGSTInvoice);
					seqNo++;
					String invoiceNo = entityCode + gstStateCode + month + year + gstInvoiceTxn.getInvoiceType()
							+ StringUtils.leftPad(String.valueOf(seqNo), 7, "0");
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