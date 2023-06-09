/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.pennanttech.pff.external.creditInformation;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.net.SocketClient;
import org.apache.commons.net.time.TimeUDPClient;

import com.pennanttech.pff.InterfaceConstants;

/***
 * The TimeTCPClient class is a TCP implementation of a client for the Time protocol described in RFC 868. To use the
 * class, merely establish a connection with {@link org.apache.commons.net.SocketClient#connect connect } and call
 * either {@link #getTime getTime() } or {@link #getDate getDate() } to retrieve the time, then call
 * {@link org.apache.commons.net.SocketClient#disconnect disconnect } to close the connection properly.
 * <p>
 * <p>
 * 
 * @author Daniel F. Savarese
 * @see TimeUDPClient
 ***/

public final class CibilClient extends SocketClient {
	/*** The default time port. It is set to 37 according to RFC 868. ***/
	/***
	 * The default TimeTCPClient constructor. It merely sets the default port to <code> DEFAULT_PORT </code>.
	 ***/
	public CibilClient() {
		setDefaultPort(InterfaceConstants.DEFAULT_PORT);
	}

	@SuppressWarnings("unused")
	public String getData() throws IOException {
		SocketClient socket;
		return readAll();
	}

	public boolean sendData(String output) throws IOException {
		_output_.write(output.getBytes());
		return true;
	}

	public String readAll() throws IOException {
		try (Reader r = new InputStreamReader(new DataInputStream(_input_))) {
			try (BufferedReader reader = new BufferedReader(r)) {
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null)
					sb.append(line).append("\n");
				return sb.toString();
			}
		}
	}

}