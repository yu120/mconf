package cn.ms.mconf.support;

import java.util.Map;

public class DataConf {

	private String root;

	private String node;
	private String app;

	private String env;
	private String conf;

	private String group;
	private String version;
	private String data;

	private int subNum;
	private Map<String, String> attributes;

	private String json;
	@SuppressWarnings("rawtypes")
	private Map kvdata;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getConf() {
		return conf;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getSubNum() {
		return subNum;
	}

	public void setSubNum(int subNum) {
		this.subNum = subNum;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	@SuppressWarnings("rawtypes")
	public Map getKvdata() {
		return kvdata;
	}

	@SuppressWarnings("rawtypes")
	public void setKvdata(Map kvdata) {
		this.kvdata = kvdata;
	}

	@Override
	public String toString() {
		return "DataConf [root=" + root + ", node=" + node + ", app=" + app
				+ ", env=" + env + ", conf=" + conf + ", group=" + group
				+ ", version=" + version + ", data=" + data + ", subNum="
				+ subNum + ", attributes=" + attributes + ", json=" + json
				+ ", kvdata=" + kvdata + "]";
	}

}
