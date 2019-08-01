package com.pennant.backend.service.customermasters;

import java.util.List;

public interface CustomerDownloadService {

	boolean processDownload(List<Long> custId) throws Exception;

}
