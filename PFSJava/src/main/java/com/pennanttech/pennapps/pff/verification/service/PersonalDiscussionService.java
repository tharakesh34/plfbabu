/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

package com.pennanttech.pennapps.pff.verification.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.pennapps.pff.verification.model.PersonalDiscussion;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface PersonalDiscussionService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	PersonalDiscussion getPersonalDiscussion(long id, String type);

	PersonalDiscussion getApprovedPersonalDiscussion(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<PersonalDiscussion> getList(String keyReference);

	List<Long> getPersonalDiscussionIds(List<Verification> verifications, String keyRef);

	void save(CustomerDetails applicant, List<CustomerPhoneNumber> phoneNumbers, Verification item);

	void save(PersonalDiscussion personalDiscussion, TableType tempTab);

	boolean isAddressChanged(Verification verification);

	PersonalDiscussion getVerificationFromRecording(long verificationId);

	boolean isAddressChanged(long verificationId, CustomerAddres customerAddres);

	boolean isAddressChange(CustomerAddres oldAddress, CustomerAddres newAddress);
}