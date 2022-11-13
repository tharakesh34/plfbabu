package com.pennant.pff.presentment.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.pff.presentment.model.DueExtractionConfig;
import com.pennant.pff.presentment.model.DueExtractionHeader;
import com.pennant.pff.presentment.model.InstrumentTypes;
import com.pennanttech.pff.core.TableType;

public interface DueExtractionConfigDAO {

	long save(InstrumentTypes instrType, TableType tableType);

	boolean isConfigExists();

	long getHeaderID();

	void saveHeader(List<DueExtractionHeader> list, TableType tableType);

	void updateHeader(List<DueExtractionHeader> list, TableType tableType);

	void save(List<DueExtractionConfig> preExtCon, TableType tableType);

	void update(List<DueExtractionConfig> preExtCon, TableType tableType);

	void delete(InstrumentTypes instrType, TableType tableType);

	void deleteConfig(Date extractionDate);

	void delete(DueExtractionHeader header, TableType tableType);

	List<InstrumentTypes> getInstrumentTypes();

	List<DueExtractionConfig> getConfig(long instrumentID);

	List<InstrumentTypes> getInstrumentHeader();

	boolean getExtractionDays(int extDys);

	Map<String, Date> getDueDates(Date extractionDate);

	long getNextValue();

	List<DueExtractionHeader> getDueExtractionHeaders();

	List<DueExtractionConfig> getDueExtractionConfig(long monthID);

	Map<Long, InstrumentTypes> getInstrumentTypesMap();

}
