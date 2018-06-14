package com.pennanttech.pff.mandate;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.mandate.AbstractMandateProcess;

public class MandateProcess extends AbstractMandateProcess {
	
	public MandateProcess() {
		super();
	}

	@Override
	protected void validateMandate(Mandate respMandate, Mandate mandate, StringBuilder remarks) {
		logger.debug(Literal.ENTERING);
		if (!StringUtils.equals(respMandate.getFinReference(), mandate.getFinReference())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Fin Reference Not Matched.");
		}

		if (!StringUtils.equals(mandate.getAccNumber(), respMandate.getAccNumber())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Account Number Not Matched.");
		}
		logger.debug(Literal.LEAVING);
	}

}
