package cn.ms.mconf.ui.service;

import java.util.List;

import cn.ms.mconf.support.MetaData;

public interface ConfService {

	List<MetaData> getApps();

	List<MetaData> getConfs();

	List<MetaData> getDatas();
	
	List<MetaData> getDataids();

}
