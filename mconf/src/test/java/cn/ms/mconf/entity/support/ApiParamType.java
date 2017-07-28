package cn.ms.mconf.entity.support;

import java.io.Serializable;

/**
 * The Service(API) Parameter Type Description.
 * 
 * @author lry
 */
public class ApiParamType implements Serializable {

	private static final long serialVersionUID = 1L;

	String key;
	String name;
	boolean must;
	String defValue;
	String dataType;
	String remarks;

	public ApiParamType() {
	}
	
	public ApiParamType(String key, String name, boolean must, String defValue, String dataType, String remarks) {
		this.key = key;
		this.name = name;
		this.must = must;
		this.defValue = defValue;
		this.dataType = dataType;
		this.remarks = remarks;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMust() {
		return must;
	}

	public void setMust(boolean must) {
		this.must = must;
	}

	public String getDefValue() {
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "ApiParamType [key=" + key + ", name=" + name + ", must=" + must
				+ ", defValue=" + defValue + ", dataType=" + dataType
				+ ", remarks=" + remarks + "]";
	}

}