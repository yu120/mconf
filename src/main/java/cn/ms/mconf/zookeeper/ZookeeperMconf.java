package cn.ms.mconf.zookeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;

import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.MconfParamType;
import cn.ms.mconf.support.MconfQuery;
import cn.ms.mconf.support.MetaData;
import cn.ms.mconf.support.NotifyMessage;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.ExtensionLoader;
import cn.ms.micro.extension.SpiMeta;

/**
 * The Micro service configuration center based on Zookeeper.<br>
 * <br>
 * The data structure：/[rootPath]/[appId]/[confId]/[dataId]{data}<br>
 * <br>
 * @author lry
 */
@SpiMeta(name = "zookeeper")
public class ZookeeperMconf extends AbstractMconf {

	public ZkCrud zkCrud;
	private MconfQuery mconfQuery;

	@Override
	public void connection(URL url) {
		super.connection(url);
		String zkType = this.url.getParameter(MconfParamType.ZKTYPE.getName(), MconfParamType.ZKTYPE.getValue());
		
		zkCrud = ExtensionLoader.getExtensionLoader(ZkCrud.class).getExtension(zkType);
		zkCrud.connection(this.url);
	}

	@Override
	public boolean isAvailable() {
		if (zkCrud == null) {
			return false;
		}
		
		return zkCrud.isAvailable();
	}
	
	@Override
	public <T> void addConf(T data) {
		MetaData metaData = this.obj2Mconf(data);
		String path = this.metaData2Path(metaData);
		zkCrud.addData(path, metaData.getData(), CreateMode.PERSISTENT);// Persistent node data
	}
	
	@Override
	public <T> void delConf(T data) {
		MetaData metaData = this.obj2Mconf(data);
		zkCrud.delData(this.metaData2Path(metaData));
	}
	
	@Override
	public <T> void setConf(T data) {
		MetaData metaData = this.obj2Mconf(data);
		zkCrud.setData(this.metaData2Path(metaData), metaData.getData());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getConf(T data) {
		MetaData metaData = this.obj2Mconf(data);
		
		String path = this.metaData2Path(metaData);
		String json = zkCrud.getData(path);
		return (T)json2Obj(json, data.getClass());
	}
	
	/**
	 * Query all nodes and node data under /mconf/[appId]/[confId]
	 * 
	 * @return Map<version:group, data>
	 */
	@Override
	public <T> List<T> getConfs(T data) {
		MetaData metaData = this.obj2Mconf(data);
		List<T> list = new ArrayList<T>();
		metaData.setDataId(null);// Force setting dataId to Nulls
		
		//Query all dataId lists
		List<String> childNodeList= zkCrud.getChildNodes(this.metaData2Path(metaData));
		if(childNodeList!=null){
			for (String childNode:childNodeList) {
				String dataId = decode(childNode);
				if(StringUtils.isBlank(dataId)){
					throw new RuntimeException("Invalid data, dataId=="+dataId);
				}
				metaData.setDataId(dataId);
				
				String path = this.metaData2Path(metaData);
				String json = zkCrud.getData(path);
				@SuppressWarnings("unchecked")
				T t = (T)json2Obj(json, data.getClass());
				if(t!=null){
					list.add(t);
				}
			}
		}
		
		return list;
	}
	
	@Override
	public <T> void subscribe(final T data, final NotifyMessage<List<T>> notifyMessage) {
		MetaData metaData = this.obj2Mconf(data);
		zkCrud.subscribeChildNodeData(this.metaData2Path(metaData), new NotifyMessage<Map<String, Object>>() {
			@SuppressWarnings("unchecked")
			@Override
			public void notify(Map<String, Object> dataMap) {
				List<T> objs = new ArrayList<T>();
				for (Map.Entry<String, Object> entry:dataMap.entrySet()) {
					String[] dataPathArray = entry.getKey().split("\\/");// ["", mconf, appId, confId, dataId]
					if(dataPathArray.length!=5){
						throw new RuntimeException("llegal PATH structure.");
					}
					
					T obj = (T)json2Obj(String.valueOf(entry.getValue()), data.getClass());
					objs.add(obj);
				}
				notifyMessage.notify(objs);
			}
		});

		// Solve the first subscription without notice
		List<T> objs = this.getConfs(data);
		notifyMessage.notify(objs);
	}
	
	@Override
	public <T> void unsubscribe(T data) {
		MetaData metaData = this.obj2Mconf(data);
		zkCrud.unsubscribeChildNodeData(this.metaData2Path(metaData));
	}
	
	@Override
	public MconfQuery query() {
		if(mconfQuery != null) {
			return mconfQuery;
		}
		
		return mconfQuery = new ZookeeperMconfQuery(this);
	}
	
}
