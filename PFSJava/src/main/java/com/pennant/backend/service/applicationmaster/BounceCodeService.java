package com.pennant.backend.service.applicationmaster;

import java.util.List;

import com.pennant.backend.model.applicationmaster.BounceCode;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.jdbc.search.ISearch;

public interface BounceCodeService {

	BounceCode getCode(String code);

	List<BounceCode> getBounceCodeById(Long Id);

	List<BounceCode> getResult(ISearch search);

	AuditHeader saveOrUpdate(AuditHeader ah);

	AuditHeader doApprove(AuditHeader ah);

	AuditHeader delete(AuditHeader ah);

	AuditHeader doReject(AuditHeader ah);
}
