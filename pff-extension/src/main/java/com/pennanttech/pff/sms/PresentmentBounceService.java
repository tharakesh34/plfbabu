package com.pennanttech.pff.sms;

import java.math.BigDecimal;

public interface PresentmentBounceService {

	public String getTemplateCode(String templateCode);

	public BigDecimal getLimitAmount(long custId);
}
