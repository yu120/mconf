package cn.ms.mconf.zookeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.data.Stat;

import cn.ms.mconf.support.MconfQuery;
import cn.ms.mconf.support.MetaData;

import com.alibaba.fastjson.JSON;

/**
 * The Zookeeper based on the realization of the configuration center more inquiries.
 * 
 * @author lry
 */
public class ZookeeperMconfQuery implements MconfQuery {
	
	ZookeeperMconf zookeeperMconf;
	
	public ZookeeperMconfQuery(ZookeeperMconf zookeeperMconf) {
		this.zookeeperMconf = zookeeperMconf;
	}
	
	@Override
	public List<MetaData> getConfs() {
		List<MetaData> metaDataList = new ArrayList<MetaData>();
		
		List<String> appIds = zookeeperMconf.zkCrud.getChildNodes(ZookeeperMconf.PATH_SEQ + zookeeperMconf.group);
		if(appIds == null){
			return metaDataList;
		}
		
		for (String appId:appIds) {
			List<String> confIds = zookeeperMconf.zkCrud.getChildNodes(
					ZookeeperMconf.PATH_SEQ + zookeeperMconf.group + ZookeeperMconf.PATH_SEQ + appId);
			if(confIds == null){
				return metaDataList;
			}
			
			for (String confId:confIds) {
				List<String> dataIds = zookeeperMconf.zkCrud.getChildNodes(ZookeeperMconf.PATH_SEQ + 
						zookeeperMconf.group + ZookeeperMconf.PATH_SEQ + appId + ZookeeperMconf.PATH_SEQ + confId);
				if(dataIds == null){
					return metaDataList;
				}
				
				for (String dataId:dataIds) {
					MetaData metaData = new MetaData();
					metaData.setAppId(appId);
					metaData.setConfId(confId);
					metaData.setDataId(dataId);
					
					String path = ZookeeperMconf.PATH_SEQ + zookeeperMconf.group + ZookeeperMconf.PATH_SEQ
							+ appId + ZookeeperMconf.PATH_SEQ + confId + ZookeeperMconf.PATH_SEQ + dataId;
					metaData.setData(zookeeperMconf.zkCrud.getData(path));
					metaData.setObj(zookeeperMconf.zkCrud.getStat(path));
					
					metaDataList.add(metaData);
				}
			}
		}
		
		return metaDataList;
	}
	
	@Override
	public List<String> getAppIds() {
		return zookeeperMconf.zkCrud.getChildNodes(ZookeeperMconf.PATH_SEQ + zookeeperMconf.group);
	}
	
	@Override
	public List<String> getConfIds(MetaData metaData) {
		MetaData tempMetaData = new MetaData();
		tempMetaData.setAppId(metaData.getAppId());
		String path = zookeeperMconf.metaData2Path(tempMetaData);
		return zookeeperMconf.zkCrud.getChildNodes(path);
	}
	
	@Override
	public List<String> getDataIds(MetaData metaData) {
		MetaData tempMetaData = new MetaData();
		tempMetaData.setAppId(metaData.getAppId());
		tempMetaData.setConfId(metaData.getConfId());
		String path = zookeeperMconf.metaData2Path(tempMetaData);
		return zookeeperMconf.zkCrud.getChildNodes(path);
	}
	
	@Override
	public String getData(MetaData metaData) {
		MetaData tempMetaData = new MetaData();
		tempMetaData.setAppId(metaData.getAppId());
		tempMetaData.setConfId(metaData.getConfId());
		tempMetaData.setDataId(metaData.getDataId());
		String path = zookeeperMconf.metaData2Path(tempMetaData);
		return zookeeperMconf.zkCrud.getData(path);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAttachments(MetaData metaData) {
		MetaData tempMetaData = new MetaData();
		tempMetaData.setAppId(metaData.getAppId());
		tempMetaData.setConfId(metaData.getConfId());
		tempMetaData.setDataId(metaData.getDataId());
		String path = zookeeperMconf.metaData2Path(tempMetaData);
		
		Stat stat = zookeeperMconf.zkCrud.getStat(path);
		return JSON.parseObject(JSON.toJSONString(stat), Map.class);
	}
	
}
