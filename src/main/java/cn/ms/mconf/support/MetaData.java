package cn.ms.mconf.support;

import org.apache.commons.lang3.StringUtils;

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
public class MetaData extends MconfData {

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
		boolean isGroup = StringUtils.isNotBlank(group);
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
			sb.append("group=").append(group);
			if (isVersion) {
				sb.append("&");
			}
		}
		if (isVersion) {
			sb.append("version=").append(version);
		}

		return sb.toString();
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
		return "MetaData [data=" + data + ", body=" + body + ", attachment="
				+ attachment + ", node=" + node + ", app=" + app + ", conf="
				+ conf + ", env=" + env + ", group=" + group + ", version="
				+ version + "]";
	}

}
