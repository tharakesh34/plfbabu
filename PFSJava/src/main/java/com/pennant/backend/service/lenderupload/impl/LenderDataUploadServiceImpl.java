package com.pennant.backend.service.lenderupload.impl;

import com.pennant.backend.dao.lenderupload.LenderDataUploadDAO;
import com.pennant.backend.model.lenderdataupload.LenderDataUpload;
import com.pennant.backend.service.lenderupload.LenderDataUploadService;

public class LenderDataUploadServiceImpl implements LenderDataUploadService {

	private LenderDataUploadDAO lenderDataUploadDAO;

	@Override
	public void save(LenderDataUpload lenderDataUpload) {
		lenderDataUploadDAO.save(lenderDataUpload);
	}

	public void setLenderDataUploadDAO(LenderDataUploadDAO lenderDataUploadDAO) {
		this.lenderDataUploadDAO = lenderDataUploadDAO;
	}

}
