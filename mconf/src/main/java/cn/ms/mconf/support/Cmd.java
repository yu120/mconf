package cn.ms.mconf.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.ms.micro.common.URL;

public class Cmd {

	public static final String ROOT_KEY = "root";
	public static final String NODE_KEY = "node";
	public static final String APP_KEY = "app";
	public static final String ENV_KEY = "env";
	public static final String GROUP_KEY = "group";
	public static final String VERSION_KEY = "version";
	public static final String CONF_KEY = "conf";
	public static final String DATA_KEY = "data";

	// 第一层
	String root;// 必须
	Map<String, String> rootAttrs = new HashMap<String, String>();

	// 第二层
	String node;
	String app;// 必须
	Map<String, String> appAttrs = new HashMap<String, String>();

	// 第三层
	String env;
	String group;
	String version;
	String conf;// 必须
	Map<String, String> confAttrs = new HashMap<String, String>();

	// 第四层
	String data;// 必须
	Map<String, String> dataAttrs = new HashMap<String, String>();

	// build root
	public Cmd buildRoot(URL url) {
		String root = url.getPath();
		if (StringUtils.isNotBlank(root)) {
			this.setRoot(root);
		}

		Map<String, String> rootAttrs = url.getMethodParameters(ROOT_KEY);
		if (rootAttrs != null) {
			if (!rootAttrs.isEmpty()) {
				this.rootAttrs.putAll(rootAttrs);
			}
		}

		return this;
	}

	// build app
	public Cmd buildApp(String node, String app, String... pairs) {
		return this.buildApp(node, app, URL.toStringMap(pairs));
	}

	public Cmd buildApp(String node, String app, Map<String, String> appAttrs) {
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
	public Cmd buildConf(String env, String group, String version, String conf,
			String... pairs) {
		return this
				.buildConf(env, group, version, conf, URL.toStringMap(pairs));
	}

	public Cmd buildConf(String env, String group, String version, String conf,
			Map<String, String> confAttrs) {
		if (StringUtils.isNotBlank(env)) {
			this.setEnv(env);
		}
		if (StringUtils.isNotBlank(group)) {
			this.setGroup(group);
		}
		if (StringUtils.isNotBlank(version)) {
			this.setVersion(version);
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
	public Cmd buildData(String data, String... pairs) {
		return this.buildData(data, URL.toStringMap(pairs));
	}

	public Cmd buildData(String data, Map<String, String> dataAttrs) {
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
	 * 数据结构：/[root][?……] / [app][?node=[node]&……] /
	 * [conf][?env=[env]&group=[group]&version=[version]&……] / [data][?……]
	 * 
	 * @return
	 */
	public String getKey() {
		return this.getPrefixKey() + this.getSuffixKey();
	}

	/**
	 * 数据结构：/[root][?……] / [app][?node=[node]&……] /
	 * [conf][?env=[env]&group=[group]&version=[version]&……]
	 * 
	 * @return
	 */
	public String getPrefixKey() {
		if (StringUtils.isBlank(root)) {// check root
			throw new IllegalArgumentException("The must set 'root'.");
		}
		if (StringUtils.isBlank(app)) {// check app
			throw new IllegalArgumentException("The must set 'app'.");
		}
		if (StringUtils.isBlank(conf)) {// check conf
			throw new IllegalArgumentException("The must set 'conf'.");
		}

		Map<String, String> tempAppAttrs = new HashMap<String, String>();
		tempAppAttrs.putAll(appAttrs);
		tempAppAttrs.put(NODE_KEY, node);
		Map<String, String> tempConfAttrs = new HashMap<String, String>();
		tempConfAttrs.putAll(confAttrs);
		tempConfAttrs.put(ENV_KEY, env);
		tempConfAttrs.put(GROUP_KEY, group);
		tempConfAttrs.put(VERSION_KEY, version);

		StringBuffer sb = new StringBuffer();
		sb.append("/").append(root).append(buildAttributes(rootAttrs));
		sb.append("/").append(app).append(buildAttributes(tempAppAttrs));
		sb.append("/").append(conf).append(buildAttributes(tempConfAttrs));

		return sb.toString();
	}

	/**
	 * 数据结构：/[data][?……]
	 * 
	 * @return
	 */
	public String getSuffixKey() {
		if (StringUtils.isBlank(data)) {// check data
			throw new IllegalArgumentException("The must set 'data'.");
		}

		Map<String, String> tempDataAttrs = new HashMap<String, String>();
		tempDataAttrs.putAll(dataAttrs);

		StringBuffer sb = new StringBuffer();
		sb.append("/").append(data).append(buildAttributes(tempDataAttrs));

		return sb.toString();
	}

	/**
	 * 
	 * 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
	 * 
	 * @param paraMap
	 * @return
	 */
	public static String buildAttributes(Map<String, String> attributes) {
		if(attributes == null || attributes.isEmpty()){
			return "";
		}
		
		String buff;
		StringBuilder buf = new StringBuilder("?");
		Map<String, String> tmpMap = attributes;
		try {
			List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
			// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
				@Override
				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
					return (o1.getKey()).toString().compareTo(o2.getKey());
				}
			});
			// 构造URL 键值对的格式
			for (Map.Entry<String, String> item : infoIds) {
				if (StringUtils.isNotBlank(item.getKey())) {
					buf.append(item.getKey() + "=" + item.getValue()).append("&");
				}

			}
			buff = buf.toString();
			if (buff.isEmpty() == false) {
				buff = buff.substring(0, buff.length() - 1);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return buff;
	}

	// encode、decode
	public static String encode(String data) {
		try {
			return URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Encoding exception", e);
		}
	}

	public static String decode(String data) {
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

	public Map<String, String> getRootAttrs() {
		return rootAttrs;
	}

	public void setRootAttrs(Map<String, String> rootAttrs) {
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

	public Map<String, String> getAppAttrs() {
		return appAttrs;
	}

	public void setAppAttrs(Map<String, String> appAttrs) {
		this.appAttrs = appAttrs;
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

	public String getConf() {
		return conf;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}

	public Map<String, String> getConfAttrs() {
		return confAttrs;
	}

	public void setConfAttrs(Map<String, String> confAttrs) {
		this.confAttrs = confAttrs;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Map<String, String> getDataAttrs() {
		return dataAttrs;
	}

	public void setDataAttrs(Map<String, String> dataAttrs) {
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
