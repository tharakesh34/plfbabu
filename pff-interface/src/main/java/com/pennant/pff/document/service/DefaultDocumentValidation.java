package com.pennant.pff.document.service;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennant.backend.model.PrimaryAccount;
import com.pennant.pff.document.dao.DocVerificationDAO;
import com.pennant.pff.document.model.DocVerificationDetail;
import com.pennant.pff.document.model.DocVerificationHeader;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.pan.service.PANService;

public class DefaultDocumentValidation implements DocumentValidation {
	private static final Logger logger = LogManager.getLogger(DefaultDocumentValidation.class);

	private DocVerificationDAO docVerificationDAO;
	private PANService nsdlPANService;

	public DefaultDocumentValidation() {
		super();
	}

	@Override
	public boolean isVerified(String docNumebr, DocType docType) {
		return docVerificationDAO.isVerified(docNumebr, docType);
	}

	@Override
	public DocVerificationHeader validate(DocType docType, DocVerificationHeader header) {
		logger.debug(Literal.ENTERING);

		try {
			switch (docType) {
			case PAN:
				validatePAN(header);
				break;
			default:
				break;
			}
		} catch (InterfaceException ie) {
			header.setVerified(false);
			header.setDocResponse(ie.toString());
			header.setStatus("FAILED");
			this.docVerificationDAO.saveHeader(header);
			logger.error(ie);
			throw ie;
		} catch (Exception ex) {
			logger.error(ex);
			throw ex;
		}

		logger.debug(Literal.LEAVING);
		return header;
	}

	private void validatePAN(DocVerificationHeader header) {
		header.setDocType(DocType.PAN.name());
		header.setVerifiedOn(new Timestamp(System.currentTimeMillis()));

		DocVerificationDetail docDetails = new DocVerificationDetail();

		PrimaryAccount pa = new PrimaryAccount();
		pa.setPanNumber(header.getDocNumber());
		if (nsdlPANService != null) {
			pa = nsdlPANService.getPANDetails(pa);
		}
		docDetails.setFName(pa.getCustFName());
		docDetails.setMName(pa.getCustMName());
		docDetails.setLName(pa.getCustLName());

		docDetails.setFullName(pa.getCustFName() + " " + pa.getCustMName() + " " + pa.getCustLName());

		header.setVerified(true);
		docDetails.setPanNumber(header.getDocNumber());
		header.setDocVerificationDetail(docDetails);

		DocVerificationHeader oldDocHead = this.docVerificationDAO.getHeader(header.getDocNumber(),
				header.getDocType());

		if (oldDocHead == null) {
			long docHeaderId = this.docVerificationDAO.saveHeader(header);
			docDetails.setHeaderId(docHeaderId);
			this.docVerificationDAO.saveDetail(docDetails);
		} else {
			header.setPrevVerifiedOn(oldDocHead.getVerifiedOn());
			header.setId(oldDocHead.getId());
			docDetails.setHeaderId(oldDocHead.getId());
			this.docVerificationDAO.updateHeader(header);
			this.docVerificationDAO.updateDetail(docDetails);
		}
	}

	@Override
	public DocVerificationHeader validateOTP(DocVerificationHeader dh, String value) {
		// Implement In Extension Layer
		return null;
	}

	@Qualifier("nsdlPANService")
	@Autowired(required = false)
	public void setNsdlPANService(PANService nsdlPANService) {
		this.nsdlPANService = nsdlPANService;
	}

	@Autowired
	public void setDocVerificationDAO(DocVerificationDAO docVerificationDAO) {
		this.docVerificationDAO = docVerificationDAO;
	}
}
