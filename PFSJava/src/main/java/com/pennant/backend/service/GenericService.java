package com.pennant.backend.service;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.cache.Cache;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public abstract class GenericService<T> {
	protected static Logger log = LogManager.getLogger(GenericService.class.getClass());
	protected Cache<String, T> cache;
	
	public GenericService() {
		super();
	}
	
	public GenericService(boolean cacheRequired) {
		super();
		cache = new Cache<String, T>(this.getClass().getSimpleName());
	}
	
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
						ErrorDetail errorDetail = auditHeader .getOverideMessage().get(i);
						if (auditHeader.getOverideMap().containsKey( errorDetail.getField())) {
							if (!checkDetails( errorDetail, auditHeader.getOverideMap().get( errorDetail.getField()))) {
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

	private boolean checkDetails(ErrorDetail errorDetail,ArrayList<ErrorDetail> errorDetails){
		
		for (int i = 0; i < errorDetails.size(); i++) {
			if(errorDetails.get(i).getCode().equals(errorDetail.getCode()) && errorDetails.get(i).isOveride()){
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
	
	
	protected T getCachedEntity(String key) {
		if (cache == null) {
			throw new IllegalStateException(
					"Cache is not enabled for the module or class " + this.getClass().getSimpleName());
		}
		
		T entity = cache.getEntity(key);
		if (entity == null) {
			entity = getEntity(key);
			
			if (entity != null) {
				setEntity(key, entity);
			}
		}
		return entity;
	}

	protected T getEntity(String code) {
		return null;
	}
	
	protected void setEntity(String key, T entity) {
		cache.setEntity(key, entity);
	}
	
	protected void invalidateEntity(String key) {
		if (cache == null) {
			throw new IllegalStateException(
					"Cache is not enabled for the module or class " + this.getClass().getSimpleName());
		}
		cache.invalidateEntity(key);
	}
	
}



