package cn.ms.mconf.support;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.ms.micro.common.URL;

/**
 * The Configuration Data Model.<br>
 * <br>
 * The data structure：<br>
 * <b>Zookeeper：</b><br>
 * Path -->
 * /mconf-[node]/[app]/[conf]/[data]?env=[env]&group=[group]&version=[version]<br>
 * Data --> {body}<br>
 * <br>
 * <br>
 * <b>Redis：</b><br>
 * Key -->mconf-[node]/[app]/[conf] <br>
 * Value -->Map<[data]?env=[env]&group=[group]&version=[version], {body}>><br>
 * <br>
 * 
 * @author lry
 */
public class MetaData extends Category {

	/**
	 * The Configure.
	 */
	protected String conf;

	/**
	 * The Serialize Data.
	 */
	private String data;
	/**
	 * The Data Body.
	 */
	private Object body;
	/**
	 * The Configure Attachment.
	 */
	private Object attachment;

	public MetaData() {
	}

	public String toBuildDataId() {
		StringBuffer sb = new StringBuffer(data);
		boolean isEnv = StringUtils.isNotBlank(env);
		boolean isGroup = StringUtils.isNotBlank(category);
		boolean isVersion = StringUtils.isNotBlank(version);
		if (isEnv || isGroup || isVersion) {
			sb.append("?");
		}

		if (isEnv) {
			sb.append("env=").append(env);
			if (isGroup || isVersion) {
				sb.append("&");
			}
		}
		if (isGroup) {
			sb.append("category=").append(category);
			if (isVersion) {
				sb.append("&");
			}
		}
		if (isVersion) {
			sb.append("version=").append(version);
		}

		return sb.toString();
	}

	public void buildWrapper(String dataId) {
		if (dataId.indexOf("?") > 0) {
			this.setData(dataId.substring(0, dataId.indexOf("?")));
			Map<String, String> map = new URL(null, null, 0)
					.addParameterString(dataId).getParameters();
			this.setEnv(map.get("env"));
			this.setCategory(map.get("group"));
			this.setVersion(map.get("version"));
		}
	}

	public MetaData copyCategory(Category category) {
		this.setNode(category.getNode());
		this.setApp(category.getApp());
		this.setEnv(category.getEnv());
		this.setCategory(category.getCategory());
		this.setVersion(category.getVersion());

		return this;
	}

	public String getConf() {
		return conf;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return "MetaData [conf=" + conf + ", data=" + data + ", body=" + body
				+ ", attachment=" + attachment + ", node=" + node + ", app="
				+ app + ", env=" + env + ", category=" + category
				+ ", version=" + version + "]";
	}

}
