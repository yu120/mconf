package cn.ms.mconf.entity;

import cn.ms.mconf.entity.support.BaseEntity;

/**
 * The Request Parameter Rule Entity.
 * 
 * @author lry
 */
public class ParameterEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * The parameter key.
	 */
	String key;
	/**
	 * The parameter title.
	 */
	String title;
	/**
	 * The parameter type.
	 */
	String type;
	/**
	 * The parameter length.
	 */
	int length;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public String toString() {
		return "ParameterEntity [key=" + key + ", title=" + title + ", type="
				+ type + ", length=" + length + ", id=" + id + ", status="
				+ status + ", operateTime=" + operateTime + ", remarks="
				+ remarks + "]";
	}

}
