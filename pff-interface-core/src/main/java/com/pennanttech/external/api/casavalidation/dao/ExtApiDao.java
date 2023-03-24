package com.pennanttech.external.api.casavalidation.dao;

public interface ExtApiDao {

	public long insertReqData(String xmlReq);

	public void logResponseById(long id, String xmlResp);
}
