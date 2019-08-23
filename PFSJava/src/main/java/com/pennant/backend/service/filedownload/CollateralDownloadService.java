package com.pennant.backend.service.filedownload;

import java.util.List;

public interface CollateralDownloadService {


	boolean processDownload(List<String> collateralRef) throws Exception;
}
