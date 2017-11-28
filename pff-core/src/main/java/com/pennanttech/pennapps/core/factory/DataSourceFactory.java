package com.pennanttech.pennapps.core.factory;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory;

import com.pennanttech.pennapps.core.util.EncryptionUtil;

public class DataSourceFactory extends BasicDataSourceFactory {

	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
			throws Exception {
		if (obj instanceof Reference) {
			setUsername((Reference) obj);
			setPassword((Reference) obj);
		}

		return super.getObjectInstance(obj, name, nameCtx, environment);
	}

	private void setPassword(Reference obj) throws Exception {
		int idx = find("password", obj);
		String encrypted = obj.get(idx).getContent().toString();
		String plain = EncryptionUtil.decrypt(encrypted);
		replace(idx, "password", plain, obj);
	}

	private void setUsername(Reference obj) throws Exception {
		int idx = find("username", obj);
		String encrypted = obj.get(idx).getContent().toString();
		String plain = EncryptionUtil.decrypt(encrypted);
		replace(idx, "username", plain, obj);
	
	}

	private int find(String addrType, Reference ref) throws Exception {
		Enumeration<RefAddr> enu = ref.getAll();
		for (int i = 0; enu.hasMoreElements(); i++) {
			RefAddr addr = (RefAddr) enu.nextElement();
			if (addr.getType().compareTo(addrType) == 0) {
				return i;
			}
		}
		return 0;
	}

	private void replace(int idx, String refType, String newValue, Reference ref) throws Exception {
		ref.remove(idx);
		ref.add(idx, new StringRefAddr(refType, newValue));
	}

}