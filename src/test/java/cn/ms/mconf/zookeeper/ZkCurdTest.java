package cn.ms.mconf.zookeeper;

import java.util.Map;

import org.apache.zookeeper.CreateMode;

import cn.ms.mconf.User;
import cn.ms.mconf.support.NotifyMessage;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.ExtensionLoader;

import com.alibaba.fastjson.JSON;

public class ZkCurdTest {

	public static void main(String[] args) {
		URL url = URL.valueOf("zookeeper://127.0.0.1:2181?zkType=curator&timeout=15000&session=60000");
		ZkCrud zkCrud = ExtensionLoader.getExtensionLoader(ZkCrud.class).getExtension("curator");//zkclient、curator
		zkCrud.connection(url);
		
		String path="/mconf/test01/ssa";
		User user = new User(1000l, "zhangsan", 22);
		String data = JSON.toJSONString(user);
		
		//新增一条记录
		zkCrud.addData(path, data, CreateMode.PERSISTENT);
		
		//获取一条记录
		String queryUser=zkCrud.getData(path);
		System.out.println(queryUser);
		
		//订阅数据
		zkCrud.subscribeChildNodeData("/mconf/test01", new NotifyMessage<Map<String,Object>>() {
			@Override
			public void notify(Map<String, Object> data) {
				System.out.println("notify: "+data.toString());
			}
		});
		
		//修改一条记录
		user.setName("lisi");
		zkCrud.setData(path, JSON.toJSONString(user));
		String setResultUser=zkCrud.getData(path);
		System.out.println(setResultUser);
		
		//删除一条记录
		//zkCrud.deleteData(path);

		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
