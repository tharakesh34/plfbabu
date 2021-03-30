package com.pennanttech.pennapps.dms.filesystem;

import com.pennanttech.pennapps.dms.model.DMSQueue;

public interface DocumentFileSystem {

	public String store(DMSQueue dMSQueue);

	public byte[] retrive(String docURI);

	public DMSQueue retriveDMS(String docURI);

}
