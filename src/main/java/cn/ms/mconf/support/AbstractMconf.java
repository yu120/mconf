package cn.ms.mconf.support;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.Mconf;
import cn.ms.mconf.annotation.DataId;
import cn.ms.mconf.annotation.MconfEntity;
import cn.ms.micro.common.URL;

import com.alibaba.fastjson.JSON;

/**
 * The Abstract Mconf Center.
 * 
 * @author lry
 */
public abstract class AbstractMconf implements Mconf {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMconf.class);

	public static final String PATH_SEQ = "/";
	
	public URL url;
	public String group;

	@Override
	public void connection(URL url) {
		this.url = url;
		this.group = this.url.getParameter(MconfParamType.GROUP.getName(), MconfParamType.GROUP.getValue());
	}

	public <T> MetaData obj2Mconf(T data) {
		if (data == null) {
			throw new RuntimeException("data[" + data + "] cannot be empty");
		}
		if (data instanceof MetaData) {
			return (MetaData) data;
		}

		MetaData metaData = new MetaData();
		metaData.setData(this.obj2Json(data));

		//$NON-NLS-Get application ID and configuration name$
		MconfEntity mconfEntity = data.getClass().getAnnotation(MconfEntity.class);
		if (mconfEntity == null) {
			throw new RuntimeException("Configuration entity[" +
					data.getClass() + "] Must contain @MconfEntity annotations.");
		}

		metaData.setAppId(mconfEntity.appId());
		metaData.setConfId(mconfEntity.confId());
		if ("$".equals(metaData.getConfId())) {// If it is the default value, use the SimpleName
			metaData.setConfId(data.getClass().getSimpleName());
		}

		try {
			Field dataIdField = null;
			Field[] fields = data.getClass().getDeclaredFields();
			for (Field field : fields) {
				DataId dataId = field.getAnnotation(DataId.class);
				field.setAccessible(true);
				if (dataId != null) {
					dataIdField = field;
					break;
				}
			}

			if (null == dataIdField) {
				throw new RuntimeException("Configuration entity[" + 
						data.getClass() + "] The member variable must contain a @DataId comment field (Field)");
			}

			Object object = dataIdField.get(data);
			if (object != null) {
				metaData.setDataId(String.valueOf(object));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return metaData;
	}

	@SuppressWarnings("unchecked")
	public <T> T json2Obj(String json, Class<T> clazz) {
		try {
			if (clazz == null) {
				return (T) JSON.parseObject(json);
			} else {
				return JSON.parseObject(json, clazz);
			}
		} catch (Exception e) {
			logger.error("Serialization exception", e);
			throw e;
		}
	}

	public String obj2Json(Object obj) {
		return JSON.toJSONString(obj);
	}

	public String url2Path(URL url) {
		StringBuilder sb = new StringBuilder();
		sb.append(PATH_SEQ).append(this.encode(group));
		
		if (StringUtils.isNotBlank(url.getProtocol())) {
			sb.append(PATH_SEQ).append(this.encode(url.getProtocol()));
		}
		if (StringUtils.isNotBlank(url.getPath())) {
			sb.append(PATH_SEQ).append(this.encode(url.getPath()));
		}
		if (StringUtils.isNotBlank(url.getParameter(MconfParamType.DATAID_KEY))) {
			sb.append(PATH_SEQ).append(this.encode(url.getParameter(MconfParamType.DATAID_KEY)));
		}

		return sb.toString();
	}

	/**
	 * The data structure: /[group]/[appId]/[confId]/[dataId]
	 * 
	 * @param metaData
	 * @return
	 */
	public String metaData2Path(MetaData metaData) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(PATH_SEQ).append(this.encode(group));// Primary node
		
		if (!StringUtils.isBlank(metaData.getAppId())) {
			stringBuffer.append(PATH_SEQ).append(this.encode(metaData.getAppId()));// Two node
		}
		if (!StringUtils.isBlank(metaData.getConfId())) {
			stringBuffer.append(PATH_SEQ).append(this.encode(metaData.getConfId()));// Three node
		}
		if (!StringUtils.isBlank(metaData.getDataId())) {// Four node
			stringBuffer.append(PATH_SEQ).append(this.encode(metaData.getDataId()));
		}

		return stringBuffer.toString();
	}

	/**
	 * Data encoding
	 * 
	 * @param data
	 * @return
	 */
	public String encode(String data) {
		try {
			return URLEncoder.encode(data, MconfParamType.DEFAULT_CHARTSET);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Encoding exception", e);
		}
	}

	/**
	 * Data decoding
	 * 
	 * @param data
	 * @return
	 */
	public String decode(String data) {
		try {
			return URLDecoder.decode(data, MconfParamType.DEFAULT_CHARTSET);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Decoding exception", e);
		}
	}

}
