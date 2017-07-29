package cn.ms.mconf.ui.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.ms.mconf.Mconf;
import cn.ms.mconf.MconfFactory;
import cn.ms.mconf.support.DataConf;
import cn.ms.mconf.ui.service.ConfService;
import cn.ms.micro.common.URL;

@Service
public class ConfServiceImpl implements ConfService {

	MconfFactory mf = MconfFactory.MCONF;
	Mconf mconf = null;

	public ConfServiceImpl() {
//		URL mconfURL = URL.valueOf("zookeeper://127.0.0.1:2181/mconf?timeout=15000&session=60000&app=node&conf=env&data=group,version");
		URL mconfURL = URL.valueOf("redis://127.0.0.1:6379/mconf?app=node&conf=env&data=group,version");
		mf.start(mconfURL);
		mconf = mf.getMconf();
	}

	@Override
	public List<DataConf> getApps() {
		return mconf.getApps();
	}

	@Override
	public List<DataConf> getConfs() {
		return mconf.getConfs();
	}

	@Override
	public List<DataConf> getDatas() {
		return mconf.getDataBodys();
	}
	
	@Override
	public List<DataConf> getDataids() {
		// TODO Auto-generated method stub
		return null;
	}

}
