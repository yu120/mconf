package cn.ms.mconf.entity;

import java.util.Map;

import cn.ms.mconf.entity.support.BaseEntity;

/**
 * The Consumer Account Entity.
 * 
 * @author lry
 */
public class ConsumerEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * The Account number (one generation cannot be changed).
	 */
	String appkey;
	/**
	 * The secret key (allows the client to reset).
	 */
	String appsecret;

	/**
	 * The traffic category
	 */
	Map<String, Object> categories;

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public Map<String, Object> getCategories() {
		return categories;
	}

	public void setCategories(Map<String, Object> categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return "ConsumerEntity [appkey=" + appkey + ", appsecret=" + appsecret
				+ ", categories=" + categories + ", id=" + id + ", status="
				+ status + ", operateTime=" + operateTime + ", remarks="
				+ remarks + "]";
	}

}
