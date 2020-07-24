package com.pennant.backend.service.finance;

import com.pennant.backend.model.finance.InvoiceDetail;

public interface GSTInvoiceTxnService {

	Long feeTaxInvoicePreparation(InvoiceDetail invoiceDetail);

	Long advTaxInvoicePreparation(InvoiceDetail invoiceDetail);

	Long schdDueTaxInovicePrepration(InvoiceDetail invoiceDetail);
}
