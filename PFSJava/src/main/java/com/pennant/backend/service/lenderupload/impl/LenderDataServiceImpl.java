package com.pennant.backend.service.lenderupload.impl;

import com.pennant.backend.dao.lenderupload.LenderDataDAO;
import com.pennant.backend.model.lenderdataupload.LenderDataUpload;
import com.pennant.backend.service.lenderupload.LenderDataService;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class LenderDataServiceImpl extends BasicDao<LenderDataUpload> implements LenderDataService {

	private LenderDataDAO lenderDataDAO;

	@Override
	public int update(LenderDataUpload lenderDataUpload, String tableName, String type) {
		return lenderDataDAO.update(lenderDataUpload, tableName, type);
	}

	@Override
	public boolean isLenderExist(String finReference, String tableName, String type) {
		return lenderDataDAO.isLenderExist(finReference, tableName, type);
	}

	public void setLenderDataDAO(LenderDataDAO lenderDataDAO) {
		this.lenderDataDAO = lenderDataDAO;
	}

}
