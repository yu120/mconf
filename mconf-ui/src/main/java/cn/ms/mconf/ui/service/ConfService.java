package cn.ms.mconf.ui.service;

import java.util.List;

import cn.ms.mconf.support.DataConf;

public interface ConfService {

	List<DataConf> getApps();

	List<DataConf> getBlocks();

	List<String> getItems();

	List<DataConf> getDatasources();

}
