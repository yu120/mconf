package cn.ms.mconf.support;

import java.util.List;
import java.util.Map;

/**
 * The Configuration center query
 * 
 * @author lry
 */
public interface MconfQuery {

	/**
	 * Query all configuration lists
	 * 
	 * @return
	 */
	List<MetaData> getConfs();

	/**
	 * Query all application lists
	 * 
	 * @return
	 */
	List<String> getAppIds();

	/**
	 * Query the configuration list for the specified application
	 * 
	 * @param metaData Must enter appId
	 * @return
	 */
	List<String> getConfIds(MetaData metaData);

	/**
	 * Query the specified application, specify the configuration item list
	 * 
	 * @param metaData Must enter appId/confId
	 * @return
	 */
	List<String> getDataIds(MetaData metaData);

	/**
	 * Specifies the configuration data for the specified application, the specified configuration, and the configuration item
	 * 
	 * @param metaData Must enter appId/confId/dataId
	 * @return
	 */
	String getData(MetaData metaData);
	
	/**
	 * Specifies the list of dependent parameters for the specified application, the specified configuration, and the configuration item
	 * 
	 * @param metaData Must enter appId/confId/dataId
	 * @return
	 */
	Map<String, Object> getAttachments(MetaData metaData);

}
