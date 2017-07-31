package cn.ms.mconf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import cn.ms.mconf.entity.RouterEntity;
import cn.ms.mconf.support.Cmd;
import cn.ms.mconf.support.Notify;

public class MconfPushTest {

	Mconf mconf;

	public MconfPushTest() {
		MconfFactory.MCONF.start(TestBase.mconfURL);
		mconf = MconfFactory.MCONF.getMconf();
	}
	
	@Test
	public void pushTest() throws Exception {
		final List<RouterEntity> list = new ArrayList<RouterEntity>();
		
		final CountDownLatch cd = new CountDownLatch(1);
		Cmd cmd = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "router");
		mconf.push(cmd, RouterEntity.class, new Notify<RouterEntity>() {
			@Override
			public void notify(List<RouterEntity> confs) {
				if(confs.size()>0){
					list.addAll(confs);
				}
				
				cd.countDown();
			}
		});
		
		cd.await(10000, TimeUnit.MILLISECONDS);
		Assert.assertTrue(list.size() > 0);
	}
	
	
}
