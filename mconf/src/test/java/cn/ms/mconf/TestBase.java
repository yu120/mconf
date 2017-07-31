package cn.ms.mconf;

import cn.ms.micro.common.URL;

public class TestBase {
	
	public static final URL mconfURL = URL.valueOf("zookeeper://127.0.0.1:2181/mconf?timeout=15000&session=60000&app=node&conf=env&data=group,version");
//	public static final URL mconfURL = URL.valueOf("redis://127.0.0.1:6379/mconf?app=node&conf=env&data=group,version");
	
}
