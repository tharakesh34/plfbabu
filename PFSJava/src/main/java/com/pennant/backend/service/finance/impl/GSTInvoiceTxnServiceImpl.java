package com.pennant.backend.service.finance.impl;

import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.EntityService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.systemmasters.ProvinceService;

public class GSTInvoiceTxnServiceImpl implements GSTInvoiceTxnService {
	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;
	private EntityService entityService;
	private ProvinceService	provinceService;

	public GSTInvoiceTxnServiceImpl() {
		super();
	}

	@Override
	public long save(GSTInvoiceTxn gstInvoiceTxn) {
		return this.gstInvoiceTxnDAO.save(gstInvoiceTxn);
	}
	
	@Override
	public Entity getEntity(String entityCode) {
		return this.entityService.getApprovedEntity(entityCode);
	}
	
	@Override
	public Entity getEntityByFinDivision(String divisionCode, String type) {
		return entityService.getEntityByFinDivision(divisionCode, type);
	}
	
	@Override
	public Province getApprovedProvince(String cPCountry, String cPProvince) {
		return provinceService.getApprovedProvinceById(cPCountry, cPProvince);
	}

	public void setGstInvoiceTxnDAO(GSTInvoiceTxnDAO gstInvoiceTxnDAO) {
		this.gstInvoiceTxnDAO = gstInvoiceTxnDAO;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public void setProvinceService(ProvinceService provinceService) {
		this.provinceService = provinceService;
	}


}
