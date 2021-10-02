package com.pennant.backend.service.extendedfieldsExtension;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennanttech.pff.core.TableType;

public interface ExtendedFieldExtensionService {
	List<AuditDetail> processingExtendedFieldExtList(List<AuditDetail> details, FinReceiptData rceiptData,
			long serviceUID, TableType tableType);

	List<AuditDetail> delete(List<AuditDetail> details, String tranType, TableType tableType);

	List<AuditDetail> vaildateDetails(List<AuditDetail> details, String usrLanguage);

	List<AuditDetail> setExtendedFieldExtAuditData(ExtendedFieldExtension efe, String tranType, String method);

	ExtendedFieldExtension getExtendedFieldExtension(String externalRef, String modeStatus, String finEvent,
			TableType tableType);
}