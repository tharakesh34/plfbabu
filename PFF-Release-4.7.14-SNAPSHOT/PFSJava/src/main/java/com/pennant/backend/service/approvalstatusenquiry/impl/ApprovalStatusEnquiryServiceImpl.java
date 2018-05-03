package com.pennant.backend.service.approvalstatusenquiry.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.approvalstatusenquiry.ApprovalStatusEnquiryDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.service.approvalstatusenquiry.ApprovalStatusEnquiryService;

public class ApprovalStatusEnquiryServiceImpl implements ApprovalStatusEnquiryService{
	private static final Logger logger = Logger.getLogger(ApprovalStatusEnquiryServiceImpl.class);

	protected ApprovalStatusEnquiryDAO approvalStatusEnquiryDAO;
	protected ReasonDetailDAO reasonDetailDAO;
	protected NotesDAO      notesDAO;
	private static final String MODULE_FINANCEMAIN = "financeMain";
	private static final String MODULE_FACILITY = "facility";
	
	public ApprovalStatusEnquiryServiceImpl() {
		super();
	}
	
	/**
	 * Get approved Customer Finance Details By ID 
	 */
	public CustomerFinanceDetail getApprovedCustomerFinanceById(String finReference, String moduleDefiner){
		CustomerFinanceDetail customerFinanceDetail = getApprovalStatusEnquiryDAO().getCustomerFinanceMainById(finReference, "_AView",false);
		if(customerFinanceDetail != null){
			customerFinanceDetail.setAuditTransactionsList(getApprovalStatusEnquiryDAO().getFinTransactionsList(finReference, true,false, moduleDefiner));
			customerFinanceDetail.setNotesList(getNotesDAO().getNotesListAsc(getNotes(finReference, MODULE_FINANCEMAIN)));
		}
		return customerFinanceDetail;
	}

	/**
	 * Get Customer Finance Details By ID 
	 */
	public CustomerFinanceDetail getCustomerFinanceById(String finReference, String moduleDefiner) {
		CustomerFinanceDetail customerFinanceDetail = getApprovalStatusEnquiryDAO().getCustomerFinanceMainById(finReference, "_View",false);
		if(customerFinanceDetail != null){
			customerFinanceDetail.setAuditTransactionsList(getApprovalStatusEnquiryDAO().getFinTransactionsList(finReference, false,false, moduleDefiner));
			customerFinanceDetail.setNotesList(getNotesDAO().getNotesListAsc(getNotes(finReference, MODULE_FINANCEMAIN)));
		}
		return customerFinanceDetail;
	}

	
	@Override
    public CustomerFinanceDetail getApprovedCustomerFacilityById(String facilityReference) {
	
		CustomerFinanceDetail customerFinanceDetail = getApprovalStatusEnquiryDAO().getCustomerFinanceMainById(facilityReference, "_AView",true);
		customerFinanceDetail.setAuditTransactionsList(getApprovalStatusEnquiryDAO().getFinTransactionsList(facilityReference, true,true,null));
		customerFinanceDetail.setNotesList(getNotesDAO().getNotesListAsc(getNotes(facilityReference, MODULE_FACILITY)));
		return customerFinanceDetail;
    }
 
	@Override
    public CustomerFinanceDetail getCustomerFacilityById(String facilityReference) {
		CustomerFinanceDetail customerFinanceDetail = getApprovalStatusEnquiryDAO().getCustomerFinanceMainById(facilityReference, "_View",true);
		customerFinanceDetail.setAuditTransactionsList(getApprovalStatusEnquiryDAO().getFinTransactionsList(facilityReference, false,true, null));
		customerFinanceDetail.setNotesList(getNotesDAO().getNotesListAsc(getNotes(facilityReference, MODULE_FACILITY)));
		return customerFinanceDetail;
    }
	
	
	/**
	 * Method for retrieving Notes Details
	 */
	private Notes getNotes(String finReference, String moduleName) {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName(moduleName);
		notes.setReference(finReference);
		notes.setVersion(0);
		logger.debug("Leaving ");
		return notes;
	}

	@Override
	public List<ReasonDetailsLog> getResonDetailsLog(String reference) {
		return this.reasonDetailDAO.getReasonDetailsLog(reference);

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
