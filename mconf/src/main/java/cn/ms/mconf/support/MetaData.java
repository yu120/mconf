package cn.ms.mconf.support;

import java.util.Map;

public class MetaData extends Cmd {

	private int subNum;

	private String json;

	@SuppressWarnings("rawtypes")
	private Map body;

	public int getSubNum() {
		return subNum;
	}

	public void setSubNum(int subNum) {
		this.subNum = subNum;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	@SuppressWarnings("rawtypes")
	public Map getBody() {
		return body;
	}

	@SuppressWarnings("rawtypes")
	public void setBody(Map body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "DataConf [subNum=" + subNum + ", json=" + json + ", body="
				+ body + ", root=" + root + ", rootAttrs=" + rootAttrs
				+ ", node=" + node + ", app=" + app + ", appAttrs=" + appAttrs
				+ ", env=" + env + ", conf=" + conf + ", confAttrs="
				+ confAttrs + ", group=" + group + ", version=" + version
				+ ", data=" + data + ", dataAttrs=" + dataAttrs + "]";
	}

}
