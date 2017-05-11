package cn.ms.mconf;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import cn.ms.mconf.support.NotifyConf;

/**
 * 微服务配置中心 - 功能单元测试<br>
 * <br>
 * 
 * @author lry
 */
public class MconfTest {

	Mconf mconf;

	public MconfTest() {
		MconfFactory.MCONF.start("mconf.properties");
		mconf = MconfFactory.MCONF.getMconf();
	}

	/**
	 * 新增配置-->查看新增结果-->删除配置-->查看删除结果
	 */
	@Test
	public void testAdd2Get2DelConfData() {
		User user = new User(1000l, "zhangsan", 22);

		mconf.addConf(user);// 新增
		User getAddUser = mconf.pull(user);// 查看新增结果
		Assert.assertEquals(user, getAddUser);

		mconf.delConf(user);// 删除
		User getDelUser = mconf.pull(user);// 查看删除结果
		Assert.assertNull(getDelUser);
	}
	
	/**
	 * 获取指定/mconf/gateway/route下所有version:group的列表
	 */
	@Test
	public void testGetList() {
		mconf.addConf(new User(1001l, "zhangsan", 111));
		mconf.addConf(new User(1002l, "zhangsan", 222));
		mconf.addConf(new User(1003l, "zhangsan", 333));
		
		List<User> list = mconf.pulls(new User());
		Assert.assertNotNull(list);
		Assert.assertEquals(3, list.size());
		for (User user:list) {
			mconf.delConf(user);
			User tempUser = mconf.pull(user);
			Assert.assertNull(tempUser);
		}
	}

	/**
	 * 添加、获取、删除List<T>
	 */
	@Test
	public void testSubscribeChildNodeConfData() throws Exception {
		final CountDownLatch cd = new CountDownLatch(3);
		mconf.push(new User(), new NotifyConf<User>() {
			@Override
			public void notify(List<User> message) {
				System.out.println("监听通知："+message.toString());
				cd.countDown();
			}
		});
		
		Thread.sleep(3000);
		
		User metaData1,metaData2,metaData3;
		mconf.addConf(metaData1 = new User(1004l, "zhangsan", 111));
		mconf.addConf(metaData2 = new User(1005l, "zhangsan", 222));
		mconf.addConf(metaData3 = new User(1006l, "zhangsan", 333));
		mconf.setConf(new User(1004l, "zhangsan", 4444));
		
		cd.await(10000, TimeUnit.MILLISECONDS);
		Assert.assertEquals(0, cd.getCount());
		
		mconf.delConf(metaData1);
		mconf.delConf(metaData2);
		mconf.delConf(metaData3);
	}

	@Test
	public void testSubscribe() throws Exception {
		final CountDownLatch cd = new CountDownLatch(3);
		mconf.push(new User(), new NotifyConf<User>() {
			@Override
			public void notify(List<User> messages) {
				System.out.println(messages.size()+"监听通知："+messages.toString());
				cd.countDown();
			}
		});
		
		for (int i = 0; i < 50; i++) {
			int s = i%4;
			if (s==1) {
				int k=i-1;
				User metaData2 = new User(1000l+k, "zhangsan", 111+i);
				mconf.setConf(metaData2);
				System.out.println("变更服务："+metaData2.toString());
			} else if(s==2) {
				int k=i-2;
				User metaData3 = new User(1000l+k, "zhangsan", 111+i);
				mconf.delConf(metaData3);
				System.out.println("删除服务："+metaData3.toString());				
			} else {
				User metaData1 = new User(1000l+i, "zhangsan", 111+i);
				mconf.addConf(metaData1);
				System.out.println("发布服务："+metaData1.toString());				
			}
//			Thread.sleep(3000);
		}
	}
	
	@Test
	public void testSubscribeFirst() throws Exception {
		mconf.push(new User(), new NotifyConf<User>() {
			@Override
			public void notify(List<User> messages) {
				System.out.println(messages.size()+"监听通知："+messages.toString());
			}
		});
		mconf.addConf(new User(1100l, "zhangsan", 222));
		Thread.sleep(10000);
	}
}
