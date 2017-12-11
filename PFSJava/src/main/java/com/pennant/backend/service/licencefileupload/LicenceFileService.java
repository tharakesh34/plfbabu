
package com.pennant.backend.service.licencefileupload;

import com.pennant.backend.licencefileupload.LicenceFile;

/**
 * Service Declaration for methods that depends on <b>LicenceFileUpload</b>.<br>
 * 
 */
public interface LicenceFileService {

	/**
	 * @param licenceFileUpload
	 * @return
	 */
	boolean saveOrUpdate(LicenceFile licenceFileUpload);
	/**
	 * @param active
	 * @return
	 */
	LicenceFile getActiveLicenceFileUpload(boolean active);
}