package cn.ms.mconf.support;

/**
 * Configuration data model<br>
 * <br>
 * The data structure：/[rootPath]/[appId]/[confId]/[dataId]{data}<br>
 * <br>
 * 
 * @author lry
 */
public class MetaData {

	/**
	 * Application ID
	 */
	private String appId;

	/**
	 * Configure ID
	 */
	private String confId;

	/**
	 * Data ID
	 */
	private String dataId;

	/**
	 * Data Body
	 */
	private String data;

	/**
	 * Subsidiary parameter
	 */
	private Object obj;

	public MetaData() {
	}

	public MetaData(String appId) {
		this.appId = appId;
	}

	public MetaData(String appId, String confId) {
		this.appId = appId;
		this.confId = confId;
	}

	public MetaData(String appId, String confId, String dataId) {
		this.appId = appId;
		this.confId = confId;
		this.dataId = dataId;
	}

	public MetaData(String appId, String confId, String dataId, String data) {
		this.appId = appId;
		this.confId = confId;
		this.dataId = dataId;
		this.data = data;
	}

	public MetaData(String appId, String confId, String dataId, String data,
			Object obj) {
		this.appId = appId;
		this.confId = confId;
		this.dataId = dataId;
		this.data = data;
		this.obj = obj;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getConfId() {
		return confId;
	}

	public void setConfId(String confId) {
		this.confId = confId;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return "MetaData [appId=" + appId + ", confId=" + confId + ", dataId="
				+ dataId + ", data=" + data + ", obj=" + obj + "]";
	}

}
