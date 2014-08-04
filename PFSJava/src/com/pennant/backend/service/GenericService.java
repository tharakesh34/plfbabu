package com.pennant.backend.service;

import java.util.ArrayList;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;

public abstract class GenericService<T> {
	
	/**
	 * nextProcess method do the following steps. if errorMessage List or
	 * OverideMessage size is more than 0 then return False else return true.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return boolean
	 */
	
	public AuditHeader nextProcess(AuditHeader auditHeader) {
		boolean nextProcess=true;
		
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0 && !auditHeader.isOveride()) {
			nextProcess = false;
		}

		if (nextProcess) {
			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				nextProcess = true;
				if (auditHeader.getOverideMap() == null) {
					nextProcess = false;
				} else {
					for (int i = 0; i < auditHeader.getOverideMessage().size(); i++) {
						ErrorDetails errorDetail = auditHeader .getOverideMessage().get(i);
						if (auditHeader.getOverideMap().containsKey( errorDetail.getErrorField())) {
							if (!checkDetails( errorDetail, auditHeader.getOverideMap().get( errorDetail.getErrorField()))) {
								nextProcess = false;
							}
						} else {
							nextProcess = false;
						}
					}
					if (nextProcess) {
						auditHeader.setOveride(true);
					}
				}
			}
		}
		auditHeader.setNextProcess(nextProcess);
		return auditHeader;
	}

	private boolean checkDetails(ErrorDetails errorDetail,ArrayList<ErrorDetails> errorDetails){
		
		for (int i = 0; i < errorDetails.size(); i++) {
			if(errorDetails.get(i).getErrorCode().equals(errorDetail.getErrorCode()) && errorDetails.get(i).isErrorOveride()){
				return true;
			}
		}
		
		return false;
	}
	
	public String getAuditTranType(String rcdType) {
		String auditTranType;
		if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
			auditTranType = PennantConstants.TRAN_ADD;
		} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
		        || rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
			auditTranType = PennantConstants.TRAN_DEL;
		} else {
			auditTranType = PennantConstants.TRAN_UPD;
		}
		return auditTranType;
	}
	
}



