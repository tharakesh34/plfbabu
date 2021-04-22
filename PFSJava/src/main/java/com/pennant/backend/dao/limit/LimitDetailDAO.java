package com.pennant.backend.dao.limit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.limit.LimitDetails;

public interface LimitDetailDAO {

	List<LimitDetails> getLimitDetailsByHeaderId(long id, String type);

	void deletebyHeaderId(long headerId, String type);

	long save(LimitDetails limitDetail, String type);

	void update(LimitDetails limitDetail, String type);

	int validationCheck(String lmtGrp, String type);

	int limitItemCheck(String lmtItem, String limitCategory, String type);

	int limitStructureCheck(String structureCode, String type);

	int getLimitDetailByStructureId(long structureId, String type);

	void deletebyLimitStructureDetailId(long id, String string);

	List<LimitDetails> getLimitDetailsByLimitLine(long headeId, String type);

	List<LimitDetails> getLimitDetailsByCustID(long headerId);

	void updateReserveUtilise(LimitDetails limitDetail, String type);

	LimitDetails getLimitLineByDetailId(final long id, String type);

	List<LimitDetails> getLimitByLineAndgroup(long headerId, String limitline, List<String> groupcode);

	public List<LimitDetails> getLimitDetails(long headerId);

	public void updateReserveUtiliseList(List<LimitDetails> limitDetailsList, String type);

	public void saveList(List<LimitDetails> limitDetailsList, String type);

	public List<LimitDetails> getLatestLimitExposures(final long id, String type);

	public Map<String, BigDecimal> getOsPriBal(long id);

	public int getLimitHeaderIDByCustId(long custId);

	public BigDecimal getOsPriBal(String finReference);

}
