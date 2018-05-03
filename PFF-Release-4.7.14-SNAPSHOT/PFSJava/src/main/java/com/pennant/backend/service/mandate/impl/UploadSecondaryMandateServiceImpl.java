package com.pennant.backend.service.mandate.impl;

import java.util.List;

import com.pennant.backend.dao.mandate.UploadSecondaryMandateDAO;
import com.pennant.backend.model.mandate.UploadSecondaryMandate;
import com.pennant.backend.service.mandate.UploadSecondaryMandateService;

public class UploadSecondaryMandateServiceImpl implements UploadSecondaryMandateService {

	private UploadSecondaryMandateDAO uploadSecondaryMandateDAO;
	
	@Override
	public void save(UploadSecondaryMandate secondaryMandateStatus) {
		uploadSecondaryMandateDAO.save(secondaryMandateStatus);
	}

	@Override
	public boolean fileIsExists(String name) {
		return uploadSecondaryMandateDAO.fileIsExists(name);
	}

	@Override
	public List<UploadSecondaryMandate> getReportData(long headerId, long userId,String module) {
		return uploadSecondaryMandateDAO.getReportData(headerId,userId,module);
	}

	public UploadSecondaryMandateDAO getUploadSecondaryMandateDAO() {
		return uploadSecondaryMandateDAO;
	}

	public void setUploadSecondaryMandateDAO(UploadSecondaryMandateDAO uploadSecondaryMandateDAO) {
		this.uploadSecondaryMandateDAO = uploadSecondaryMandateDAO;
	}

}
