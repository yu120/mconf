package cn.ms.mconf.entity.support;

import java.io.Serializable;
import java.sql.Timestamp;

public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	

	protected String id;
	/**
	 * The status.
	 */
	protected boolean status;
	/**
	 * The operate time.
	 */
	protected Timestamp operateTime;
	/**
	 * The remarks.
	 */
	protected String remarks;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Timestamp getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Timestamp operateTime) {
		this.operateTime = operateTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "BaseEntity [id=" + id + ", status=" + status
				+ ", operateTime=" + operateTime + ", remarks=" + remarks + "]";
	}

}
