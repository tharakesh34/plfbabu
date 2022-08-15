package com.pennant.backend.dao;

import java.util.List;

import com.pennant.backend.model.isradetail.ISRADetail;
import com.pennant.backend.model.isradetail.ISRALiquidDetail;

public interface ISRADetailDAO {

	long save(ISRADetail israDetail, String tableType);

	void update(ISRADetail israDetail, String tableType);

	void delete(String finRef, String tableType);

	ISRADetail getISRADetailsByFinRef(String finRef, String tableType);

	void deleteIsraLiqDetails(long israDetailId, String tableType);

	List<ISRALiquidDetail> getISRALiqDetails(long israDetailId, String tableType);

	void save(ISRALiquidDetail israLiquidDetail, String tableType);

	void update(ISRALiquidDetail liquidDetail, String tableType);

	void delete(ISRALiquidDetail liquidDetail, String tableType);

	boolean isDetailExists(ISRADetail israDetail, String tableType);
}
