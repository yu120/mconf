package cn.ms.mconf.support;

/**
 * The Configure Category.
 * 
 * @author lry
 */
public class Category {

	/**
	 * The Node.
	 */
	protected String node;
	/**
	 * The Application.
	 */
	protected String app;

	/**
	 * mconf://0.0.0.0:0/data?env=[env]&category=[category]&version=[version]
	 * The Environment.
	 */
	protected String env;
	/**
	 * The Configure Category.
	 */
	protected String category;
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

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "GroupData [node=" + node + ", app=" + app + ", env=" + env
				+ ", category=" + category + ", version=" + version + "]";
	}

}
