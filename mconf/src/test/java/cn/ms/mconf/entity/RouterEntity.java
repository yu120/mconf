package cn.ms.mconf.entity;

import cn.ms.mconf.entity.support.BaseEntity;

/**
 * The Router Rule Entity.
 * 
 * @author lry
 */
public class RouterEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * The allow consumers ID.
	 */
	String appkey;
	/**
	 * The allow consumers service(API) ID.
	 */
	String apiId;

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	@Override
	public String toString() {
		return "RouterEntity [appkey=" + appkey + ", apiId=" + apiId + ", id="
				+ id + ", status=" + status + ", operateTime=" + operateTime
				+ ", remarks=" + remarks + "]";
	}

}