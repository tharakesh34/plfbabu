
package com.pennant.backend.service.licencefile;

import com.pennant.backend.licencefile.LicenceFile;

/**
 * Service Declaration for methods that depends on <b>LicenceFileUpload</b>.<br>
 * 
 */
public interface LicenceFileService {

	/**
	 * @param licenceFile
	 * @return
	 */
	boolean saveOrUpdate(LicenceFile licenceFile);
	/**
	 * @param active
	 * @return
	 */
	LicenceFile getActiveLicenceFile();
}