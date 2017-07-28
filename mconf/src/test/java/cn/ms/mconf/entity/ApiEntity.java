package cn.ms.mconf.entity;

import java.util.Map;

import cn.ms.mconf.annotation.MconfEntity;
import cn.ms.mconf.entity.support.ApiParamType;
import cn.ms.mconf.entity.support.BaseEntity;

/**
 * The Service(API) Entity.
 * 
 * @author lry
 */
@MconfEntity("api")
public class ApiEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * The service(API) ID.
	 */
	String service;
	/**
	 * The service(API) Group.
	 */
	String group;
	/**
	 * The service(API) Version.
	 */
	String version;
	/**
	 * The service(API) title.
	 */
	String title;

	/**
	 * The request header parameters.
	 */
	Map<String, ApiParamType> reqHeaders;
	/**
	 * The request message format.
	 */
	String reqData;

	/**
	 * The response header parameters.
	 */
	Map<String, ApiParamType> resHeaders;
	/**
	 * The response message format.
	 */
	String resData;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, ApiParamType> getReqHeaders() {
		return reqHeaders;
	}

	public void setReqHeaders(Map<String, ApiParamType> reqHeaders) {
		this.reqHeaders = reqHeaders;
	}

	public String getReqData() {
		return reqData;
	}

	public void setReqData(String reqData) {
		this.reqData = reqData;
	}

	public Map<String, ApiParamType> getResHeaders() {
		return resHeaders;
	}

	public void setResHeaders(Map<String, ApiParamType> resHeaders) {
		this.resHeaders = resHeaders;
	}

	public String getResData() {
		return resData;
	}

	public void setResData(String resData) {
		this.resData = resData;
	}

	@Override
	public String toString() {
		return "ApiEntity [service=" + service + ", group=" + group
				+ ", version=" + version + ", title=" + title + ", reqHeaders="
				+ reqHeaders + ", reqData=" + reqData + ", resHeaders="
				+ resHeaders + ", resData=" + resData + ", id=" + id
				+ ", status=" + status + ", operateTime=" + operateTime
				+ ", remarks=" + remarks + "]";
	}

}
