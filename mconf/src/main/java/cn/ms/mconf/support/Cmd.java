package cn.ms.mconf.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Cmd {

	public static final String NODE_KEY = "node";
	public static final String ENV_KEY = "env";
	public static final String GROUP_KEY = "group";
	public static final String VERSION_KEY = "version";

	// 第一层
	private String root;//必须
	private Map<String, Object> rootAttrs = new HashMap<String, Object>();

	// 第二层
	private String node;
	private String app;//必须
	private Map<String, Object> appAttrs = new HashMap<String, Object>();

	// 第三层
	private String env;
	private String conf;//必须
	private Map<String, Object> confAttrs = new HashMap<String, Object>();

	// 第四层
	private String group;
	private String version;
	private String data;//必须
	private Map<String, Object> dataAttrs = new HashMap<String, Object>();

	// build root
	public Cmd buildRoot(String root) {
		return this.buildRoot(root, null);
	}

	public Cmd buildRoot(String root, Map<String, Object> rootAttrs) {
		if (StringUtils.isNotBlank(root)) {
			this.setRoot(root);
		}

		if (rootAttrs != null) {
			if (!rootAttrs.isEmpty()) {
				this.rootAttrs.putAll(rootAttrs);
			}
		}

		return this;
	}

	// build app
	public Cmd buildApp(String node, String app) {
		return this.buildApp(node, app, null);
	}

	public Cmd buildApp(String node, String app, Map<String, Object> appAttrs) {
		if (StringUtils.isNotBlank(node)) {
			this.setNode(node);
		}

		if (StringUtils.isNotBlank(app)) {
			this.setApp(app);
		}

		if (appAttrs != null) {
			if (!appAttrs.isEmpty()) {
				this.appAttrs.putAll(appAttrs);
			}
		}

		return this;
	}

	// build conf
	public Cmd buildConf(String env, String conf) {
		return this.buildConf(env, conf, null);
	}

	public Cmd buildConf(String env, String conf, Map<String, Object> confAttrs) {
		if (StringUtils.isNotBlank(env)) {
			this.setEnv(env);
		}
		if (StringUtils.isNotBlank(conf)) {
			this.setConf(conf);
		}
		if (confAttrs != null) {
			if (!confAttrs.isEmpty()) {
				this.confAttrs.putAll(confAttrs);
			}
		}

		return this;
	}

	// build data
	public Cmd buildData(String group, String version, String data) {
		return this.buildData(group, version, data, null);
	}

	public Cmd buildData(String group, String version, String data,
			Map<String, Object> dataAttrs) {
		if (StringUtils.isNotBlank(group)) {
			this.setGroup(group);
		}
		if (StringUtils.isNotBlank(version)) {
			this.setVersion(version);
		}
		if (StringUtils.isNotBlank(data)) {
			this.setData(data);
		}
		if (dataAttrs != null) {
			if (!dataAttrs.isEmpty()) {
				this.dataAttrs.putAll(dataAttrs);
			}
		}

		return this;
	}

	/**
	 * 数据结构：/[root][?……]/[app][?node=[node]&……]/[conf][?env=[env]&……]/[data][?
	 * group=[group]&version=[version]&……]
	 * 
	 * @return
	 */
	public String buildKey() {
		return this.buildPrefixKey() + this.buildSuffixKey();
	}

	/**
	 * 数据结构：/[root][?……]/[app][?node=[node]&……]/[conf][?env=[env]&……]
	 * 
	 * @return
	 */
	public String buildPrefixKey() {
		if (StringUtils.isBlank(root)) {// check root
			throw new RuntimeException("The must set parameter 'root'.");
		}
		if (StringUtils.isBlank(app)) {// check app
			throw new RuntimeException("The must set parameter 'app'.");
		}
		if (StringUtils.isBlank(conf)) {// check conf
			throw new RuntimeException("The must set parameter 'conf'.");
		}

		Map<String, Object> tempAppAttrs = new HashMap<String, Object>();
		tempAppAttrs.putAll(appAttrs);
		tempAppAttrs.put(NODE_KEY, node);
		Map<String, Object> tempConfAttrs = new HashMap<String, Object>();
		tempConfAttrs.putAll(confAttrs);
		tempConfAttrs.put(ENV_KEY, env);

		StringBuffer sb = new StringBuffer();
		sb.append("/").append(root).append(this.buildAttributes(rootAttrs));
		sb.append("/").append(app).append(this.buildAttributes(tempAppAttrs));
		sb.append("/").append(conf).append(this.buildAttributes(tempConfAttrs));

		return sb.toString();
	}

	/**
	 * 数据结构：/[data][?group=[group]&version=[version]&……]
	 * 
	 * @return
	 */
	public String buildSuffixKey() {
		if (StringUtils.isBlank(data)) {// check data
			throw new RuntimeException("The must set parameter 'data'.");
		}

		Map<String, Object> tempDataAttrs = new HashMap<String, Object>();
		tempDataAttrs.putAll(dataAttrs);
		tempDataAttrs.put(GROUP_KEY, group);
		tempDataAttrs.put(VERSION_KEY, version);

		StringBuffer sb = new StringBuffer();
		sb.append("/").append(data).append(this.buildAttributes(tempDataAttrs));

		return sb.toString();
	}

	public String buildAttributes(Map<String, Object> attributes) {
		if (attributes == null || attributes.size() == 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer("?");
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			if (entry.getValue() != null) {
				String key = this.encode(entry.getKey());
				String value = this.encode(String.valueOf(entry.getValue()));
				sb.append(key).append("=").append(value).append("&");
			}
		}

		String keyAllPath = sb.toString();
		if (keyAllPath.endsWith("&")) {
			return keyAllPath.substring(0, keyAllPath.length() - 1);
		} else {
			return keyAllPath;
		}
	}

	// encode、decode
	public String encode(String data) {
		try {
			return URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Encoding exception", e);
		}
	}

	public String decode(String data) {
		try {
			return URLDecoder.decode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Decoding exception", e);
		}
	}

	// getter、setter
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public Map<String, Object> getRootAttrs() {
		return rootAttrs;
	}

	public void setRootAttrs(Map<String, Object> rootAttrs) {
		this.rootAttrs = rootAttrs;
	}

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

	public Map<String, Object> getAppAttrs() {
		return appAttrs;
	}

	public void setAppAttrs(Map<String, Object> appAttrs) {
		this.appAttrs = appAttrs;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getConf() {
		return conf;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}

	public Map<String, Object> getConfAttrs() {
		return confAttrs;
	}

	public void setConfAttrs(Map<String, Object> confAttrs) {
		this.confAttrs = confAttrs;
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Map<String, Object> getDataAttrs() {
		return dataAttrs;
	}

	public void setDataAttrs(Map<String, Object> dataAttrs) {
		this.dataAttrs = dataAttrs;
	}

	@Override
	public String toString() {
		return "Cmd [root=" + root + ", rootAttrs=" + rootAttrs + ", node="
				+ node + ", app=" + app + ", appAttrs=" + appAttrs + ", env="
				+ env + ", conf=" + conf + ", confAttrs=" + confAttrs
				+ ", group=" + group + ", version=" + version + ", data="
				+ data + ", dataAttrs=" + dataAttrs + "]";
	}

}
