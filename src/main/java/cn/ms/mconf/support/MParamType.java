package cn.ms.mconf.support;

/**
 * Configuration Center Parameters.
 * 
 * @author lry
 */
public enum MParamType {

	/** Configuration space, defaults to mconf **/
	GROUP("group", "mconf"),

	/** Connection timeout, defaults to 60s **/
	TIMEOUT("timeout", 60 * 1000),

	/** Expired cleanup time, defaults to 60s **/
	SESSION("session", 60 * 1000),

	/** ZK implementation (zkclient/curator), the default is curator **/
	ZKTYPE("zkType", "curator"),

	confName("confName", "mconf.properties"),
	;

	public final static String DEFAULT_CHARTSET = "UTF-8";
	public final static String DATAID_KEY = "dataId";

	private String name;
	private String value;
	private long longValue;
	private int intValue;
	private boolean boolValue;

	private MParamType(String name, String value) {
		this.name = name;
		this.value = value;
	}

	private MParamType(String name, long longValue) {
		this.name = name;
		this.value = String.valueOf(longValue);
		this.longValue = longValue;
	}

	private MParamType(String name, int intValue) {
		this.name = name;
		this.value = String.valueOf(intValue);
		this.intValue = intValue;
	}

	private MParamType(String name, boolean boolValue) {
		this.name = name;
		this.value = String.valueOf(boolValue);
		this.boolValue = boolValue;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public int getIntValue() {
		return intValue;
	}

	public long getLongValue() {
		return longValue;
	}

	public boolean getBooleanValue() {
		return boolValue;
	}

}
