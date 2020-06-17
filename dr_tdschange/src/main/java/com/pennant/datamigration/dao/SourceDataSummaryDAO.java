package com.pennant.datamigration.dao;

import com.pennant.datamigration.model.SourceDataSummary;
import com.pennant.datamigration.model.SourceStatus;
import com.pennant.datamigration.model.StatusCount;
import java.util.List;

public interface SourceDataSummaryDAO {
   void saveSummary(SourceDataSummary var1);

   void deleteSummary(SourceDataSummary var1);

   List<SourceStatus> getSummaryStatus();

   void saveStatusCount(List<StatusCount> var1);
}
    