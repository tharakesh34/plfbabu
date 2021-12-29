package com.pennanttech.pff.external.gst;

import com.pennanttech.pennapps.core.job.JobExecution;

public interface GSTInvoiceGeneratorService {
	JobExecution generateInvoice();
}