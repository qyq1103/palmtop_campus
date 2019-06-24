package net.JL.admin.file;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.xmlbeans.impl.util.Base64;

public class Encode {

	public String enc(String agent, String filename) {
		try {
			if (agent != null && agent.toLowerCase().indexOf("firefox") != -1) {
				filename = "=?UTF-8?B?" + (new String(Base64.encode(filename.getBytes("UTF-8")))) + "?=";
			} else {
				filename = URLEncoder.encode(filename, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return filename;
	}

}
