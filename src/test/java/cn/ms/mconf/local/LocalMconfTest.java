package cn.ms.mconf.local;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import cn.ms.mconf.Mconf;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.ExtensionLoader;

public class LocalMconfTest {

	URL url = URL.valueOf("local://0.0.0.0:0?confName=mconf-test.properties");
	Mconf mconf = null;
	public LocalMconfTest() {
		mconf = ExtensionLoader.getExtensionLoader(Mconf.class).getExtension(url.getProtocol());
		mconf.connect(url);
	}

	@Test
	public void testGetConfs() {
		List<GatewayRouter> list = mconf.pulls(new GatewayRouter());
		Assert.assertEquals(2, list.size());
	}

	@Test
	public void testGetConf() {
		GatewayRouter queryGatewayRouter = new GatewayRouter();
		queryGatewayRouter.setId("RR01");
		GatewayRouter gatewayRouter = mconf.pull(queryGatewayRouter);
		Assert.assertEquals("(beijing),(weixin0[1-5])", gatewayRouter.getRule());
	}

}
