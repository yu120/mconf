package cn.ms.mconf.ui.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.ms.mconf.Conf;
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
		Conf conf = Conf.INSTANCE;
		conf.connection("mconf.properties");
		Object mconfURLStr = conf.getProperty("mconf.url");

		URL mconfURL = URL.valueOf(String.valueOf(mconfURLStr));
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
		return mconf.getBodys();
	}
	
	@Override
	public List<DataConf> getDataids() {
		// TODO Auto-generated method stub
		return null;
	}

}
