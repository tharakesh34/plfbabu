package com.pennant.backend.service;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.legal.LegalDocument;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.cache.Cache;
import com.pennanttech.pennapps.core.cache.CacheManager;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.web.util.MessageUtil;

public abstract class GenericService<T> {
	protected static Logger log = LogManager.getLogger(GenericService.class.getClass());
	protected Cache<String, T> cache;
	protected DMSService dMSService;

	public GenericService() {
		super();
	}

	public GenericService(boolean cacheRequired, String moduleName) {
		super();
		cache = new Cache<String, T>(moduleName);
	}

	/**
	 * nextProcess method do the following steps. if errorMessage List or OverideMessage size is more than 0 then return
	 * False else return true.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return boolean
	 */

	public AuditHeader nextProcess(AuditHeader auditHeader) {
		boolean nextProcess = true;

		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0
				&& !auditHeader.isOveride()) {
			nextProcess = false;
		}

		if (nextProcess) {
			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				nextProcess = true;
				if (auditHeader.getOverideMap() == null) {
					nextProcess = false;
				} else {
					for (int i = 0; i < auditHeader.getOverideMessage().size(); i++) {
						ErrorDetail errorDetail = auditHeader.getOverideMessage().get(i);
						if (auditHeader.getOverideMap().containsKey(errorDetail.getField())) {
							if (!checkDetails(errorDetail, auditHeader.getOverideMap().get(errorDetail.getField()))) {
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

	private boolean checkDetails(ErrorDetail errorDetail, List<ErrorDetail> errorDetails) {

		for (int i = 0; i < errorDetails.size(); i++) {
			if (errorDetails.get(i).getCode().equals(errorDetail.getCode()) && errorDetails.get(i).isOveride()) {
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
		if (!CacheManager.isEnabled()) {
			return getEntity(key);
		}

		if (cache == null) {
			throw new IllegalStateException(
					String.format("Cache is not enabled for the module or class %s", this.getClass().getSimpleName()));
		}

		T entity = cache.getEntity(key);
		if (entity == null) {
			entity = getEntity(key);

			if (entity != null) {
				try {
					setEntity(key, entity);
				} catch (AppException e) {
					//
				}
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
					String.format("Cache is not enabled for the module or class %s", this.getClass().getSimpleName()));
		}
		cache.invalidateEntity(key);
	}

	protected void saveDocument(DMSModule dm, DMSModule dsm, DocumentDetails dd) {
		if (!ImplementationConstants.UPDATE_METADATA_IN_DMS && dd.getDocRefId() != null && dd.getDocRefId() > 0) {
			return;
		}

		if (dd.getCustId() == null || dd.getCustId() <= 0) {
			if (dm == DMSModule.FINANCE && dsm == DMSModule.COLLATERAL) {
				String collateralRef = dd.getReferenceId();
				if ((collateralRef != null)) {
					dd.setCustId(dMSService.getCustomerIdByCollateral(collateralRef));
				}
			} else if (dm == DMSModule.FINANCE) {
				String finReference = dd.getFinReference();
				if ((finReference != null)) {
					dd.setCustId(dMSService.getCustomerIdByFin(finReference));
				}
			}
		}

		DMSQueue dmsQueue = new DMSQueue();
		dmsQueue.setModule(dm);
		dmsQueue.setSubModule(dsm);
		if (dsm == DMSModule.COVENANT) {
			dmsQueue.setFinReference(dd.getReferenceId());
		} else {
			dmsQueue.setFinReference(dd.getFinReference());
		}
		dmsQueue.setApplicationNo(dd.getApplicationNo());
		dmsQueue.setOfferId(dd.getLeadId());
		dmsQueue.setCustId(dd.getCustId());
		dmsQueue.setCustCif(dd.getCustomerCif());
		dmsQueue.setReference(dd.getReferenceId());
		dmsQueue.setDocumentId(dd.getDocId());
		dmsQueue.setDocName(dd.getDocName());
		dmsQueue.setDocCategory(dd.getDocCategory());
		dmsQueue.setDocType(dd.getDoctype());
		dmsQueue.setDocExt(FilenameUtils.getExtension(dd.getDocName()));
		dmsQueue.setDocImage(dd.getDocImage());
		dmsQueue.setCreatedOn(dd.getLastMntOn());
		dmsQueue.setAuxiloryFields1(dd.getRemarks());
		dmsQueue.setDocUri(dd.getDocUri());
		if (dd.getDocRefId() != null) {
			dmsQueue.setDocManagerID(dd.getDocRefId());
		}

		if (SessionUserDetails.getLogiedInUser() != null) {
			dmsQueue.setCreatedBy(SessionUserDetails.getLogiedInUser().getUserId());
		} else {
			dmsQueue.setCreatedBy(1000);
		}

		dmsQueue = getLeadId(dmsQueue);

		if (validate(dmsQueue)) {
			dd.setDocRefId(dMSService.save(dmsQueue));
		}
	}

	protected void saveDocument(DMSModule dm, DMSModule dsm, CustomerDocument cd) {
		if (!ImplementationConstants.UPDATE_METADATA_IN_DMS && cd.getDocRefId() != null && cd.getDocRefId() > 0) {
			return;
		}
		if (cd.getCustID() == 0 || cd.getCustID() == Long.MIN_VALUE) {
			if (dm == DMSModule.CUSTOMER) {
				String custCIF = cd.getLovDescCustCIF();

				cd.setCustID(dMSService.getCustomerIdByCIF(custCIF));
			}
		}

		DMSQueue dmsQueue = new DMSQueue();
		dmsQueue.setModule(DMSModule.CUSTOMER);
		dmsQueue.setSubModule(DMSModule.CUSTOMER);
		dmsQueue.setFinReference(cd.getFinReference());
		dmsQueue.setCustId(cd.getCustID());
		dmsQueue.setCustCif(cd.getLovDescCustCIF());
		dmsQueue.setReference("");
		dmsQueue.setDocumentId(cd.getCustID());
		dmsQueue.setDocName(cd.getCustDocName());
		dmsQueue.setDocCategory(cd.getCustDocCategory());
		dmsQueue.setDocType(cd.getCustDocType());
		dmsQueue.setDocExt(FilenameUtils.getExtension(cd.getCustDocName()));
		dmsQueue.setDocImage(cd.getCustDocImage());
		dmsQueue.setCreatedOn(cd.getLastMntOn());
		dmsQueue.setApplicationNo(cd.getApplicationNo());
		dmsQueue.setOfferId(cd.getOfferId());
		dmsQueue.setAuxiloryFields1(cd.getRemarks());
		dmsQueue.setDocUri(cd.getDocUri());
		if (cd.getDocRefId() != null) {
			dmsQueue.setDocManagerID(cd.getDocRefId());
		}

		if (SessionUserDetails.getLogiedInUser() != null) {
			dmsQueue.setCreatedBy(SessionUserDetails.getLogiedInUser().getUserId());
		} else {
			dmsQueue.setCreatedBy(1000);
		}

		dmsQueue = getLeadId(dmsQueue);

		if (validate(dmsQueue)) {
			cd.setDocRefId(dMSService.save(dmsQueue));
		}
	}

	private DMSQueue getLeadId(DMSQueue dmsQueue) {
		if (SysParamUtil.isAllowed("LEAD_ID_IS_MANDATORY")) {

			if (StringUtils.isEmpty(dmsQueue.getFinReference())) {
				throw new AppException(
						"To store documents in DMS system FinReference is mandatory, please contact system administrator");
			}
			if (StringUtils.isEmpty(dmsQueue.getOfferId()) && StringUtils.isEmpty(dmsQueue.getApplicationNo())) {
				dmsQueue = dMSService.getOfferIdByFin(dmsQueue);
			}
		}
		return dmsQueue;
	}

	protected void saveDocument(DMSModule dm, DMSModule dsm, LegalDocument ld) {
		if ((ld.getDocumentReference() != null && ld.getDocumentReference() > 0) || ld.getDocImage() == null) {
			return;
		}
		String finReference = ld.getFinReference();
		Long custId = ld.getCustId();
		if ((finReference != null) && (custId == null || custId == Long.MIN_VALUE)) {
			ld.setCustId(dMSService.getCustomerIdByFin(finReference));
		}

		DMSQueue dmsQueue = new DMSQueue();
		dmsQueue.setModule(DMSModule.FINANCE);
		dmsQueue.setSubModule(DMSModule.LEGAL);
		dmsQueue.setFinReference(ld.getFinReference());
		dmsQueue.setCustId(ld.getCustId());
		dmsQueue.setReference(String.valueOf(ld.getLegalId()));
		dmsQueue.setDocumentId(ld.getLegalDocumentId());
		dmsQueue.setDocName(ld.getDocumentName());
		dmsQueue.setDocCategory(ld.getDocumentCategory());
		dmsQueue.setDocType(ld.getDocumentType());
		dmsQueue.setDocExt(FilenameUtils.getExtension(ld.getDocumentName()));
		dmsQueue.setDocImage(ld.getDocImage());
		ld.setDocumentReference(dMSService.save(dmsQueue));
		dmsQueue.setCreatedOn(ld.getLastMntOn());

		if (SessionUserDetails.getLogiedInUser() != null) {
			dmsQueue.setCreatedBy(SessionUserDetails.getLogiedInUser().getUserId());
		} else {
			dmsQueue.setCreatedBy(1000);
		}

		dmsQueue = getLeadId(dmsQueue);

		if (dmsQueue != null && validate(dmsQueue)) {
			ld.setDocumentReference(dMSService.save(dmsQueue));
		}

	}

	private boolean validate(DMSQueue dmsQueue) {
		boolean flag = true;
		if ((dmsQueue.getCustId() == null || dmsQueue.getCustId() <= 0)
				&& (dmsQueue.getCustCif() == null && dmsQueue.getFinReference() == null)
				&& (dmsQueue.getSubModule().equals(DMSModule.COVENANT) && dmsQueue.getReference() == null)) {

			flag = false;
			MessageUtil.showError(
					"Either Customer Id/Customer CIF/FinReference should be avilable for DMS structure, please contact administrator.");

		} else if (StringUtils.equals(dmsQueue.getSubModule().name(), DMSModule.QUERY_MGMT.name())) {
			if ((dmsQueue.getCustId() == null || dmsQueue.getCustId() <= 0) && (dmsQueue.getFinReference() == null)
					&& (dmsQueue.getSubModule().equals(DMSModule.COVENANT) && dmsQueue.getReference() == null)) {
				flag = false;
				MessageUtil.showError(
						"Either Customer Id/Customer CIF/FinReference should be avilable in QUERY_MANAGEMENT  for DMS structure, please contact administrator.");
			}

		}
		return flag;
	}

	protected ErrorDetail getError(String errorCode, String... parms) {
		return ErrorUtil.getError(errorCode, parms);
	}

	protected DocumentManager getDocumentManager(long docID) {
		return dMSService.getDocumentManager(docID);
	}

	protected byte[] getDocumentImage(long docID) {
		return dMSService.getById(docID);
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

}
