package com.pennant.Interface.service;

import com.pennant.coreinterface.model.HostEnquiry;

public interface HostStatusEnquiryService {
	
    HostEnquiry getHostStatus() throws Exception;
}
