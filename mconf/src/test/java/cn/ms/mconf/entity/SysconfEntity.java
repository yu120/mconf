package cn.ms.mconf.entity;

import cn.ms.mconf.annotation.MconfEntity;
import cn.ms.mconf.entity.support.BaseEntity;

/**
 * The System Conf Entity.
 * 
 * @author lry
 */
@MconfEntity("sysconf")
public class SysconfEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 黑名单清单 **/
	public static final String BALCK_LIST = "balck_list";
	/** 白名单清单 **/
	public static final String WHITE_LIST = "white_list";
	/** 分组路由参数维度:routers **/
	public static final String ROUTERS = "routers";
	/** 故障重试错误码:404=>3,406=>3,590=>1 **/
	public static final String FAIL_RETRY_CODE = "fail_retry_code";

	/**
	 * The System Parameter Title.
	 */
	String title;
	/**
	 * The System Parameter KEY.
	 */
	String key;
	/**
	 * The System Parameter Value.
	 */
	Object value;
	/**
	 * The System Parameter Group.
	 */
	String group;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "SysconfEntity [title=" + title + ", key=" + key + ", value="
				+ value + ", group=" + group + ", id=" + id + ", status="
				+ status + ", operateTime=" + operateTime + ", remarks="
				+ remarks + "]";
	}

}
