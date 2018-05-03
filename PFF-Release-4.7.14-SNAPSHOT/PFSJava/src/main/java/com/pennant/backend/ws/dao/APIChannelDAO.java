package com.pennant.backend.ws.dao;

import java.util.List;

import com.pennant.backend.model.channeldetails.APIChannel;
import com.pennant.backend.model.channeldetails.APIChannelIP;
import com.pennant.ws.exception.APIException;

public interface APIChannelDAO {

	long save(APIChannel aPIChannel, String tableType);

	void update(APIChannel aPIChannel, String tableType);

	void deleteChannelDetails(APIChannel aPIChannel, String string);

	APIChannel getChannelDetailsById(long id, String string);

	APIChannel getChannelDetails();

	APIChannel getNewChannelDetails();
	
	//ChannelAuthDetails
	long save(APIChannelIP aPIChannelIP, String tableType);

	void update(APIChannelIP aPIChannelIP, String tableType);

	void delete(APIChannelIP aPIChannelIP, String string);

	void deleteChannelAuthDetails(long id, String string);

	List<APIChannelIP> getChannelAuthDetailsByChannelId(long id, String string);

	APIChannelIP getChannelAuthDetails();

	APIChannelIP getNewChannelAuthDetails();
	
	long getChannelId(String channelId, String channelIp) throws APIException;

	APIChannelIP getChannelIpDetail(long channelId, long id);
}
