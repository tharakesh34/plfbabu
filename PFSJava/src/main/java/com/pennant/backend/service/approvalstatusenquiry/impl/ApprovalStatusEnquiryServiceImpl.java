package com.pennant.backend.service.approvalstatusenquiry.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.approvalstatusenquiry.ApprovalStatusEnquiryDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.finance.AuditTransaction;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.service.approvalstatusenquiry.ApprovalStatusEnquiryService;

public class ApprovalStatusEnquiryServiceImpl implements ApprovalStatusEnquiryService {
	protected ApprovalStatusEnquiryDAO approvalStatusEnquiryDAO;
	protected ReasonDetailDAO reasonDetailDAO;
	protected NotesDAO notesDAO;
	private static final String MODULE_FINANCEMAIN = "financeMain";
	private static final String MODULE_FACILITY = "facility";

	public ApprovalStatusEnquiryServiceImpl() {
		super();
	}

	/**
	 * Get approved Customer Finance Details By ID
	 */
	public CustomerFinanceDetail getApprovedCustomerFinanceById(String finReference, String moduleDefiner) {
		CustomerFinanceDetail customerFinanceDetail = approvalStatusEnquiryDAO.getCustomerFinanceMainById(finReference,
				"_AView", false);

		if (customerFinanceDetail == null) {
			return null;
		}

		List<String> finReferences = new ArrayList<>();
		finReferences.add(finReference);

		if (CollectionUtils.isEmpty(finReferences)) {
			return customerFinanceDetail;
		}

		customerFinanceDetail.setAuditTransactionsList(
				getApprovalStatusEnquiryDAO().getFinTransactionsList(finReferences, true, false, moduleDefiner));
		customerFinanceDetail.setNotesList(getNotesDAO().getNotesListAsc(finReferences, MODULE_FINANCEMAIN));
		return customerFinanceDetail;
	}

	/**
	 * Get Customer Finance Details By ID
	 */
	public CustomerFinanceDetail getCustomerFinanceById(String finReference, String moduleDefiner) {
		CustomerFinanceDetail customerFinanceDetail = approvalStatusEnquiryDAO.getCustomerFinanceMainById(finReference,
				"_View", false);

		if (customerFinanceDetail == null) {
			return null;
		}

		List<String> finReferences = new ArrayList<>();
		finReferences.add(finReference);

		if (CollectionUtils.isEmpty(finReferences)) {
			return customerFinanceDetail;
		}

		customerFinanceDetail.setAuditTransactionsList(
				getApprovalStatusEnquiryDAO().getFinTransactionsList(finReferences, false, false, moduleDefiner));
		customerFinanceDetail.setNotesList(getNotesDAO().getNotesListAsc(finReferences, MODULE_FINANCEMAIN));
		return customerFinanceDetail;
	}

	@Override
	public CustomerFinanceDetail getApprovedCustomerFacilityById(String facilityReference) {
		CustomerFinanceDetail customerFinanceDetail = approvalStatusEnquiryDAO
				.getCustomerFinanceMainById(facilityReference, "_AView", true);

		if (customerFinanceDetail == null) {
			return null;
		}

		List<String> finReferences = new ArrayList<>();
		finReferences.add(facilityReference);

		if (CollectionUtils.isEmpty(finReferences)) {
			return customerFinanceDetail;
		}

		customerFinanceDetail.setAuditTransactionsList(
				getApprovalStatusEnquiryDAO().getFinTransactionsList(finReferences, true, true, null));
		customerFinanceDetail.setNotesList(getNotesDAO().getNotesListAsc(finReferences, MODULE_FACILITY));
		return customerFinanceDetail;
	}

	@Override
	public CustomerFinanceDetail getCustomerFacilityById(String facilityReference) {
		CustomerFinanceDetail customerFinanceDetail = approvalStatusEnquiryDAO
				.getCustomerFinanceMainById(facilityReference, "_View", true);

		if (customerFinanceDetail == null) {
			return null;
		}

		List<String> finReferences = new ArrayList<>();
		finReferences.add(facilityReference);

		if (CollectionUtils.isEmpty(finReferences)) {
			return customerFinanceDetail;
		}
		customerFinanceDetail.setAuditTransactionsList(
				getApprovalStatusEnquiryDAO().getFinTransactionsList(finReferences, false, true, null));
		customerFinanceDetail.setNotesList(getNotesDAO().getNotesListAsc(finReferences, MODULE_FACILITY));
		return customerFinanceDetail;
	}

	@Override
	public List<ReasonDetailsLog> getResonDetailsLog(String reference) {
		return this.reasonDetailDAO.getReasonDetailsLog(reference);
	}

	@Override
	public List<CustomerFinanceDetail> getListOfCustomerFinanceById(long custID, String moduleDefiner) {
		List<CustomerFinanceDetail> existingLoans = approvalStatusEnquiryDAO.getListOfCustomerFinanceDetailById(custID,
				"_View", false);

		List<String> finReferences = new ArrayList<>();

		existingLoans.forEach(el -> finReferences.add(el.getFinReference()));

		if (CollectionUtils.isEmpty(finReferences)) {
			return existingLoans;
		}

		List<AuditTransaction> auditTxnList = approvalStatusEnquiryDAO.getFinTransactionsList(finReferences, false,
				false, moduleDefiner);

		List<Notes> notes = notesDAO.getNotesListAsc(finReferences, MODULE_FINANCEMAIN);

		for (CustomerFinanceDetail cfd : existingLoans) {
			String finReference = cfd.getFinReference();

			for (AuditTransaction auditTransaction : auditTxnList) {
				if (auditTransaction.getAuditReference().equals(finReference)) {
					cfd.getAuditTransactionsList().add(auditTransaction);
				}
			}

			for (Notes item : notes) {
				if (item.getReference().equals(finReference)) {
					cfd.getNotesList().add(item);
				}
			}
		}
		return existingLoans;
	}

	public ApprovalStatusEnquiryDAO getApprovalStatusEnquiryDAO() {
		return approvalStatusEnquiryDAO;
	}

	public void setApprovalStatusEnquiryDAO(ApprovalStatusEnquiryDAO approvalStatusEnquiryDAO) {
		this.approvalStatusEnquiryDAO = approvalStatusEnquiryDAO;
	}

	public NotesDAO getNotesDAO() {
		return notesDAO;
	}

	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

	public ReasonDetailDAO getReasonDetailDAO() {
		return reasonDetailDAO;
	}

	public void setReasonDetailDAO(ReasonDetailDAO reasonDetailDAO) {
		this.reasonDetailDAO = reasonDetailDAO;
	}

}
