package cn.ms.mconf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.micro.common.URL;
import cn.ms.micro.extension.ExtensionLoader;

/**
 * Configuration center for factory<br>
 * <br>
 * 使用场景:<br>
 * 1.读取普通Key-Value<br>
 * 2.读取Zookeeper中的Bean配置<br>
 * 3.读取本地资源文件中的Bean配置<br>
 * 4.先读取本地支援文件中的Bena配置,再读取Zookeeper中的配置进行内存覆盖<br>
 * 
 * @author lry
 */
public enum MconfFactory {

	MCONF;

	private final static Logger logger = LoggerFactory.getLogger(MconfFactory.class);

	public static final String MCONF_URL_KEY = "mconf.url";

	private Conf conf;
	private Mconf mconf;

	public Conf getConf() {
		return conf;
	}

	public Mconf getMconf() {
		return mconf;
	}

	public void start(String confName) {
		logger.info("Is loading conf and mconf center...");

		conf = Conf.INSTANCE;
		if (!conf.connection(confName)) {
			throw new IllegalStateException("No conf(" + confName + ") connection fail.");
		} else {
			logger.info("The conf connection successed, data is: {}", conf.getPropertys());

			Object mconfURL = conf.getProperty(MCONF_URL_KEY);
			if (mconfURL == null || String.valueOf(mconfURL).length() == 0) {
				throw new IllegalStateException("The conf(" + confName + ") KEY '" + MCONF_URL_KEY + "'='" + mconfURL + "'.");
			} else {
				URL url = URL.valueOf(String.valueOf(mconfURL));
				mconf = ExtensionLoader.getExtensionLoader(Mconf.class).getExtension(url.getProtocol());
				mconf.connect(url);
				if (!mconf.available()) {
					throw new IllegalStateException("No mconf center available: " + url);
				} else {
					logger.info("The mconf center started successed!");
				}
			}
		}
	}

}
