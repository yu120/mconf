package cn.ms.mconf.local;

import cn.ms.mconf.annotation.DataId;
import cn.ms.mconf.annotation.MconfEntity;

@MconfEntity(appId = "gateway", confId = "router")
public class GatewayRouter {

	@DataId
	private String id;
	private String rule;
	private String address;
	private boolean enable;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return "GatewayRouter [id=" + id + ", rule=" + rule
				+ ", address=" + address + ", enable=" + enable + "]";
	}

}
