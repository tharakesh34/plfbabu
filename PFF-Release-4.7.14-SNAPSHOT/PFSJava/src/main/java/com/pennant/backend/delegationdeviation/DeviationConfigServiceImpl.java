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

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : DeviationHeaderServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-06-2015 * *
 * Modified Date : 22-06-2015 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 22-06-2015 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.delegationdeviation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.ProductDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.solutionfactory.DeviationDetailDAO;
import com.pennant.backend.dao.solutionfactory.DeviationHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.solutionfactory.DeviationDetail;
import com.pennant.backend.model.solutionfactory.DeviationHeader;
import com.pennant.backend.delegationdeviation.DeviationConfigService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Service implementation for methods that depends on <b>DeviationHeader</b>.<br>
 * 
 */
public class DeviationConfigServiceImpl implements DeviationConfigService {
	private static final Logger logger = Logger.getLogger(DeviationConfigServiceImpl.class);

	
	private LoggedInUser userDetails;
	
	private AuditHeaderDAO auditHeaderDAO;

	private DeviationHeaderDAO deviationHeaderDAO;

	private DeviationDetailDAO deviationDetailDAO;
	
	private FinanceTypeDAO financeTypeDAO;

	private FinTypeFeesDAO finTypeFeesDAO;
	@Autowired
	private ProductDAO productDAO; 
	
	public DeviationConfigServiceImpl() {
		super();
	}
	
	@Override
	public FinanceType getFinanceType(String fintype) {
	    return getFinanceTypeDAO().getFinanceTypeByFinType(fintype);
    }
	
	@Override
	public List<FinTypeFees> getFeeCodeList(String finType, int moduleId) {
	 return  getFinTypeFeesDAO().getFinTypeFeeCodes(finType, moduleId);
    }
	
	@Override
	public boolean deviationAllowed(String product){
		Product prod = productDAO.getProductByProduct(product);
		if (prod!=null) {
			return prod.isAllowDeviation();
		}
		return false;
	}
	
	@Override
	public List<DeviationHeader> getDeviationsByFinType(String finType) {
		logger.debug(" Entering ");
		
		List<DeviationHeader> list = getDeviationHeaderDAO().getDeviationHeaderByFinType(finType,"");

		for (DeviationHeader deviationHeader : list) {
			List<DeviationDetail> details = getDeviationDetailDAO()
			        .getDeviationDetailsByDeviationId(deviationHeader.getDeviationID(), "");
			deviationHeader.setDeviationDetails(details);
		}
		return list;

	}
	
	@Override
	public List<DeviationHeader> getDeviationsbyModule(String finType,String module) {
		logger.debug(" Entering ");
		
		List<DeviationHeader> list = getDeviationHeaderDAO().getDeviationHeader(finType,module,"");
		List<DeviationDetail> details = getDeviationDetailDAO().getDeviationDetailsByModuleFinType(finType, module, "");
		
		for (DeviationHeader deviationHeader : list) {

			Iterator<DeviationDetail> it = details.iterator();
			while (it.hasNext()) {
				DeviationDetail deviationDetail = (DeviationDetail) it.next();
				if (deviationHeader.getDeviationID() == deviationDetail.getDeviationID()) {
					deviationHeader.getDeviationDetails().add(deviationDetail);
					it.remove();
				}
			}
		}
		logger.debug(" Leaving ");
		return list;
		
	}

	@Override
	public void processDelegationDeviation(List<DeviationHeader> newHeaderList, String finType, LoggedInUser user) {
		logger.debug(" Entering ");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		userDetails=user;
		//get previous details
		List<DeviationHeader> prevHeaderList = getDeviationsByFinType(finType);

		for (DeviationHeader newHeader : newHeaderList) {
			//check is it already there in database

			DeviationHeader prvHeader = isFoundinList(prevHeaderList, newHeader);

			if (prvHeader == null) {
				// Not found the database
				newHeader.setRecordType(PennantConstants.RCD_ADD);
				if (newHeader.getDeviationDetails() != null) {
					for (DeviationDetail deviationDetail : newHeader.getDeviationDetails()) {
						deviationDetail.setRecordType(PennantConstants.RCD_ADD);
					}
				}
				
				//No configuration specified
				if (newHeader.getDeviationDetails()==null || newHeader.getDeviationDetails().isEmpty()) {
					continue;
				}
				//add new records to database
				List<AuditDetail> newaudit = processDeviationHeader(newHeader);
				auditDetails.addAll(newaudit);
				continue;
			}

			// Update the Header 
			if (!StringUtils.equals(prvHeader.getValueType(), newHeader.getValueType())) {
				
				DeviationHeader befImageHeader=new DeviationHeader();
				BeanUtils.copyProperties(prvHeader, befImageHeader);
				
				prvHeader.setValueType(newHeader.getValueType());
				prvHeader.setRecordType(PennantConstants.RCD_UPD);
				prvHeader.setBefImage(befImageHeader);
            }
	
			
			// Process details
			List<DeviationDetail> listToProcess = new ArrayList<DeviationDetail>();
			List<DeviationDetail> newDetailList = newHeader.getDeviationDetails();
			List<DeviationDetail> prvdetails = prvHeader.getDeviationDetails();

			for (DeviationDetail newdeviationDetail : newDetailList) {
				DeviationDetail prvDetails = isFoundinList(prvdetails, newdeviationDetail);
				if (prvDetails == null) {
					newdeviationDetail.setRecordType(PennantConstants.RCD_ADD);
					listToProcess.add(newdeviationDetail);
					continue;
				}

			//update the previous deviation details if changed 
				if (!StringUtils.equals(prvDetails.getDeviatedValue(), newdeviationDetail.getDeviatedValue())) {
					DeviationDetail befImage=new DeviationDetail();
					BeanUtils.copyProperties(prvDetails, befImage);

					prvDetails.setDeviatedValue(newdeviationDetail.getDeviatedValue());
					prvDetails.setLastMntBy(newdeviationDetail.getLastMntBy());
					prvDetails.setLastMntOn(newdeviationDetail.getLastMntOn());
					prvDetails.setRecordType(PennantConstants.RCD_UPD);
					prvDetails.setBefImage(befImage);
					
					listToProcess.add(prvDetails);
                }
			}

			boolean emptyHeader=true;
			//TO delete Previous deviation details
			for (DeviationDetail prvdeviationDetail : prvdetails) {
				DeviationDetail recordtodelete = isFoundinList(newDetailList, prvdeviationDetail);
				if (recordtodelete == null) {
					//Not found on the new List should be deleted
					prvdeviationDetail.setRecordType(PennantConstants.RCD_DEL);
					listToProcess.add(prvdeviationDetail);
				}
				if (emptyHeader && !StringUtils.trimToEmpty(prvdeviationDetail.getRecordType()).equals(PennantConstants.RCD_DEL)) {
					emptyHeader=false;
				}
			}

			//update or delete the records
			prvHeader.setDeviationDetails(listToProcess);
			if (emptyHeader) {
				prvHeader.setRecordType(PennantConstants.RCD_DEL);	
			}
			List<AuditDetail> updateaudit = processDeviationHeader(prvHeader);
			auditDetails.addAll(updateaudit);
		}

		//TO delete Previous deviation Headers
		for (DeviationHeader prvdeviationHeader : prevHeaderList) {
			DeviationHeader header = isFoundinList(newHeaderList, prvdeviationHeader);
			if (header == null) {
				//Not found on the new List should be deleted
				prvdeviationHeader.setRecordType(PennantConstants.RCD_DEL);
				if (prvdeviationHeader.getDeviationDetails() != null) {
					for (DeviationDetail deviationDetail : prvdeviationHeader.getDeviationDetails()) {
						deviationDetail.setRecordType(PennantConstants.RCD_DEL);
					}
				}
				//delete the records
				List<AuditDetail> deleteaudit = processDeviationHeader(prvdeviationHeader);
				auditDetails.addAll(deleteaudit);
				continue;
			}
		}
		
		AuditHeader auditHeader=getAuditHeader(finType,auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		userDetails=null;

		logger.debug(" Leaving ");
	}

	public AuditHeader getAuditHeader(String finType, List<AuditDetail> auditDetails){
		AuditHeader auditHeader=new AuditHeader();
		auditHeader.setAuditModule("DEVIATION");
		auditHeader.setAuditReference(finType);
		auditHeader.setAuditUsrId(userDetails.getUserId());
		auditHeader.setAuditBranchCode(userDetails.getBranchCode());
		auditHeader.setAuditDeptCode(userDetails.getDepartmentCode());
		auditHeader.setAuditSystemIP(userDetails.getIpAddress());
		auditHeader.setAuditSessionID(userDetails.getSessionId());
		auditHeader.setUsrLanguage(userDetails.getLanguage());
		int count=0;
		for (AuditDetail auditDetail : auditDetails) {
			auditDetail.setAuditSeq(count++);
        }
		auditHeader.setAuditDetails(auditDetails);
		
		return auditHeader;
	}
	
	
	private DeviationHeader isFoundinList(List<DeviationHeader> list,DeviationHeader deviationHeader) {

		if (list == null || list.isEmpty()) {
			return null;
		}

		for (DeviationHeader header : list) {
			if (StringUtils.equals(header.getModuleCode(), deviationHeader.getModuleCode())) {
				return header;
			}
		}

		return null;
	}

	private DeviationDetail isFoundinList(List<DeviationDetail> details,DeviationDetail dev) {

		if (details == null || details.isEmpty()) {
			return null;
		}
		for (DeviationDetail preDeviationDetail : details) {
			if (StringUtils.equals(preDeviationDetail.getUserRole(), dev.getUserRole())) {
				return preDeviationDetail;
			}
		}
		return null;
	}

	private List<AuditDetail> processDeviationHeader(DeviationHeader deviationHeader) {
		logger.debug(" Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String rcdType = StringUtils.trimToEmpty(deviationHeader.getRecordType());
		long deviationId = deviationHeader.getDeviationID();

		if (PennantConstants.RCD_ADD.equals(rcdType)) {
			
			setStatus(deviationHeader);
			deviationId = getDeviationHeaderDAO().save(deviationHeader, "");
			auditDetails.add(getDeviationHeaderAudit(deviationHeader, PennantConstants.TRAN_ADD));
			
		} else if (PennantConstants.RCD_UPD.equals(rcdType)) {
			
			setStatus(deviationHeader);
			getDeviationHeaderDAO().update(deviationHeader, "");
			auditDetails.add(getDeviationHeaderAudit(deviationHeader, PennantConstants.TRAN_UPD));
			
		} else if (PennantConstants.RCD_DEL.equals(rcdType)) {
			
			setStatus(deviationHeader);
			getDeviationHeaderDAO().delete(deviationHeader, "");
			deviationHeader.setBefImage(deviationHeader);
			auditDetails.add(getDeviationHeaderAudit(deviationHeader, PennantConstants.TRAN_DEL));
			
		}
		// process Details
		List<DeviationDetail> list = deviationHeader.getDeviationDetails();
		for (DeviationDetail deviationDetail : list) {
			AuditDetail auditDetail = processDeviationdetails(deviationDetail, deviationId);
			if (auditDetail != null) {
				auditDetails.add(auditDetail);
			}
		}
		logger.debug(" Leaving ");
		return auditDetails;
	}

	private AuditDetail processDeviationdetails(DeviationDetail deviationDetail, long deviationId) {
		logger.debug(" Entering ");

		String rcdType = StringUtils.trimToEmpty(deviationDetail.getRecordType());

		if (PennantConstants.RCD_ADD.equals(rcdType)) {
			deviationDetail.setDeviationID(deviationId);
			setStatus(deviationDetail);
			getDeviationDetailDAO().save(deviationDetail, "");
			return getDeviationDetailAudit(deviationDetail,PennantConstants.TRAN_ADD);
		}

		if (PennantConstants.RCD_UPD.equals(rcdType)) {
			setStatus(deviationDetail);
			getDeviationDetailDAO().update(deviationDetail, "");
			return getDeviationDetailAudit(deviationDetail,PennantConstants.TRAN_UPD);
		}

		if (PennantConstants.RCD_DEL.equals(rcdType)) {
			setStatus(deviationDetail);
			getDeviationDetailDAO().delete(deviationDetail, "");
			deviationDetail.setBefImage(deviationDetail);
			return getDeviationDetailAudit(deviationDetail,PennantConstants.TRAN_DEL);
		}

		logger.debug(" Leaving ");
		return null;

	}

	public AuditDetail getDeviationHeaderAudit(DeviationHeader deviationHeader, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new DeviationHeader(),
		        new DeviationHeader().getExcludeFields());
		return new AuditDetail(transType, 1, fields[0], fields[1], deviationHeader.getBefImage(),
		        deviationHeader);

	}

	public AuditDetail getDeviationDetailAudit(DeviationDetail deviationDetail, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new DeviationDetail(),
		        new DeviationDetail().getExcludeFields());
		return new AuditDetail(transType, 1, fields[0], fields[1], deviationDetail.getBefImage(),
		        deviationDetail);

	}

	public void setStatus(DeviationHeader deviationHeader) {
		deviationHeader.setRecordType("");
		deviationHeader.setRecordStatus("");
		deviationHeader.setVersion(deviationHeader.getVersion() + 1);
		deviationHeader.setLastMntBy(userDetails.getUserId());
		deviationHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
	}

	public void setStatus(DeviationDetail deviationDetail) {
		deviationDetail.setRecordType("");
		deviationDetail.setRecordStatus("");
		deviationDetail.setVersion(deviationDetail.getVersion() + 1);
		deviationDetail.setLastMntBy(userDetails.getUserId());
		deviationDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
	}
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public DeviationHeaderDAO getDeviationHeaderDAO() {
		return deviationHeaderDAO;
	}

	public void setDeviationHeaderDAO(DeviationHeaderDAO deviationHeaderDAO) {
		this.deviationHeaderDAO = deviationHeaderDAO;
	}

	public DeviationDetailDAO getDeviationDetailDAO() {
		return deviationDetailDAO;
	}

	public void setDeviationDetailDAO(DeviationDetailDAO deviationDetailDAO) {
		this.deviationDetailDAO = deviationDetailDAO;
	}
	
	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinTypeFeesDAO getFinTypeFeesDAO() {
		return finTypeFeesDAO;
	}
	
	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}
	
}