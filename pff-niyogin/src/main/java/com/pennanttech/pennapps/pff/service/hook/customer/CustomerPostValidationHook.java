package com.pennanttech.pennapps.pff.service.hook.customer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.pff.service.hook.PostValidationHook;

@Component(value="customerPostValidationHook")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CustomerPostValidationHook implements PostValidationHook {

	@Override
	public List<ErrorDetail> validation(AuditHeader auditHeader) {
		List<ErrorDetail> errorDetails = new ArrayList<>();
		
		
		return errorDetails;
	}

}
