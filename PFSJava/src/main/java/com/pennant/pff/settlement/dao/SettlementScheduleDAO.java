package com.pennant.pff.settlement.dao;

import java.util.List;

import com.pennant.pff.settlement.model.SettlementSchedule;

public interface SettlementScheduleDAO {

	List<SettlementSchedule> getSettlementScheduleDetails(long id, String type);

	String save(SettlementSchedule settlementSchedule, String type);

	void update(SettlementSchedule settlementSchedule, String type);

	void delete(SettlementSchedule settlementSchedule, String type);

}
