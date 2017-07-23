package cn.ms.mconf.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class International {

	private static final Logger logger = LoggerFactory.getLogger(International.class);

	public static final String MSG_KEY = "msg";
	public static final String MSGTYPE_KEY = "msgType";
	public static final String MSG_ZH_CN = "msg_zh_CN";
	public static final String MSG_EN_US = "msg_en_US";
	public static final String FILE_TYPE = ".properties";

	public static Map<String, Object> getMSG(String msgType) {
		Map<String, Object> msgMap = new HashMap<String, Object>();

		try {
			PropertiesConfiguration conf = new PropertiesConfiguration();
			FileHandler handler = new FileHandler(conf);
			handler.setFileName((StringUtils.isBlank(msgType) ? MSG_ZH_CN : msgType) + FILE_TYPE);
			handler.load();

			Iterator<String> iterator = conf.getKeys();
			while (iterator.hasNext()) {
				String key = iterator.next();
				msgMap.put(key, conf.getProperty(key));
			}

			logger.info("The msg data:{}", msgMap.toString());

			return msgMap;
		} catch (Exception e) {
			logger.error("The read msg properties is exception.", e);
		}

		return msgMap;
	}

}
