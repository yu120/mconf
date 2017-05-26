package cn.ms.mconf.support;

public class MconfData {
	/**
	 * The Node.
	 */
	protected String node = "default-node";
	/**
	 * The Application.
	 */
	protected String app;
	/**
	 * The Configure.
	 */
	protected String conf;

	/**
	 * The Environment.
	 */
	protected String env;
	/**
	 * The Configure Group.
	 */
	protected String group;
	/**
	 * The Configure Version.
	 */
	protected String version;

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getConf() {
		return conf;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
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

	@Override
	public String toString() {
		return "MconfData [node=" + node + ", app=" + app + ", conf=" + conf
				+ ", env=" + env + ", group=" + group + ", version=" + version
				+ "]";
	}

}
