package cn.ms.mconf;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import cn.ms.mconf.entity.ApiEntity;
import cn.ms.mconf.entity.ConsumerEntity;
import cn.ms.mconf.entity.ParameterEntity;
import cn.ms.mconf.entity.RouterEntity;
import cn.ms.mconf.entity.SysconfEntity;
import cn.ms.mconf.entity.support.ApiParamType;
import cn.ms.mconf.entity.support.BaseEntity;
import cn.ms.mconf.support.Cmd;

public class MconfTest {

	Mconf mconf;
	Integer id = 1;

	public MconfTest() {
		MconfFactory.MCONF.start(TestBase.mconfURL);
		mconf = MconfFactory.MCONF.getMconf();
	}
	
	private void wrapperBaseEntity(BaseEntity baseEntity) {
		baseEntity.setId(String.valueOf(id++));
		baseEntity.setStatus(true);
		baseEntity.setOperateTime(new Timestamp(System.currentTimeMillis()));
	}

	@Test
	public void testGetParameter() {
		Cmd cmd = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "parameter");
		List<ParameterEntity> list = mconf.pulls(cmd, ParameterEntity.class);
		System.out.println(list);
	}
	
	@Test
	public void testParameter() {
		ParameterEntity parameterEntity1 = new ParameterEntity();
		this.wrapperBaseEntity(parameterEntity1);
		parameterEntity1.setKey("channelId");
		parameterEntity1.setTitle("渠道ID");
		parameterEntity1.setType("String");
		Cmd command1 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "parameter").buildData(parameterEntity1.getId());
		mconf.addConf(command1, parameterEntity1);

		ParameterEntity parameterEntity2 = new ParameterEntity();
		this.wrapperBaseEntity(parameterEntity2);
		parameterEntity2.setKey("consumerId");
		parameterEntity2.setTitle("消费服务ID");
		parameterEntity2.setType("String");
		Cmd command2 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "parameter").buildData(parameterEntity2.getId());
		mconf.addConf(command2, parameterEntity2);

		ParameterEntity parameterEntity3 = new ParameterEntity();
		this.wrapperBaseEntity(parameterEntity3);
		parameterEntity3.setKey("areaId");
		parameterEntity3.setTitle("地区ID");
		parameterEntity3.setType("String");
		Cmd command3 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "parameter").buildData(parameterEntity3.getId());
		mconf.addConf(command3, parameterEntity3);
	}

	@Test
	public void testRouter() {
		RouterEntity routerEntity1 = new RouterEntity();
		this.wrapperBaseEntity(routerEntity1);
		routerEntity1.setAppkey("850d5a93");
		routerEntity1.setApiId("1");
		Cmd command1 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "router").buildData(routerEntity1.getId());
		mconf.addConf(command1, routerEntity1);
		
		RouterEntity routerEntity2 = new RouterEntity();
		this.wrapperBaseEntity(routerEntity2);
		routerEntity2.setAppkey("850d5a93");
		routerEntity2.setApiId("2");
		Cmd command2 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "router").buildData(routerEntity2.getId());
		mconf.addConf(command2, routerEntity2);
		
		RouterEntity routerEntity3 = new RouterEntity();
		this.wrapperBaseEntity(routerEntity3);
		routerEntity3.setAppkey("850d5a93");
		routerEntity3.setApiId("3");
		Cmd command3 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "router").buildData(routerEntity3.getId());
		mconf.addConf(command3, routerEntity3);
		
		RouterEntity routerEntity4 = new RouterEntity();
		this.wrapperBaseEntity(routerEntity4);
		routerEntity4.setAppkey("714b4aaa");
		routerEntity4.setApiId("1");
		Cmd command4 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "router").buildData(routerEntity4.getId());
		mconf.addConf(command4, routerEntity4);
		
		RouterEntity routerEntity5 = new RouterEntity();
		this.wrapperBaseEntity(routerEntity5);
		routerEntity5.setAppkey("f74b334a");
		routerEntity5.setApiId("3");
		Cmd command5 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "router").buildData(routerEntity5.getId());
		mconf.addConf(command5, routerEntity5);
	}

	@Test
	public void testSysconf() {
		// 白名单清单
		SysconfEntity sysconfEntity1 = new SysconfEntity();
		this.wrapperBaseEntity(sysconfEntity1);
		sysconfEntity1.setKey(SysconfEntity.WHITE_LIST);
		sysconfEntity1.setValue("127.0.0.1;10.22.*.*");
		sysconfEntity1.setGroup("BALCKWHITE");
		sysconfEntity1.setTitle("白名单清单");
		Cmd command1 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "sysconf").buildData(sysconfEntity1.getId());
		mconf.addConf(command1, sysconfEntity1);

		// 黑名单清单
		SysconfEntity sysconfEntity2 = new SysconfEntity();
		this.wrapperBaseEntity(sysconfEntity2);
		sysconfEntity2.setKey(SysconfEntity.BALCK_LIST);
		sysconfEntity2.setValue("192.168.1.*");
		sysconfEntity2.setGroup("BALCKWHITE");
		sysconfEntity2.setTitle("黑名单清单");
		Cmd command2 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "sysconf").buildData(sysconfEntity2.getId());
		mconf.addConf(command2, sysconfEntity2);

		// 分组路由维度
		SysconfEntity sysconfEntity3 = new SysconfEntity();
		this.wrapperBaseEntity(sysconfEntity3);
		sysconfEntity3.setKey(SysconfEntity.ROUTERS);
		sysconfEntity3.setValue("areaId,channelId");
		sysconfEntity3.setGroup("ROUTERS");
		sysconfEntity3.setTitle("分组路由维度");
		Cmd command3 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "sysconf").buildData(sysconfEntity3.getId());
		mconf.addConf(command3, sysconfEntity3);

		// 故障重试错误码
		SysconfEntity sysconfEntity4 = new SysconfEntity();
		this.wrapperBaseEntity(sysconfEntity4);
		sysconfEntity4.setKey(SysconfEntity.FAIL_RETRY_CODE);
		sysconfEntity4.setValue("404=>3,405=>5,406=>2,409=>1");
		sysconfEntity4.setGroup("RETRY_ENABLE");
		sysconfEntity4.setTitle("故障重试错误码");
		Cmd command4 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "sysconf").buildData(sysconfEntity4.getId());
		mconf.addConf(command4, sysconfEntity4);
	}

	@Test
	public void testConsumer() {
		ConsumerEntity consumerEntity1 = new ConsumerEntity();
		this.wrapperBaseEntity(consumerEntity1);
		consumerEntity1.setAppsecret(UUID.randomUUID().toString().replace("-", ""));
		consumerEntity1.setAppkey("850d5a93");
		Map<String, Object> categories1 = new HashMap<String, Object>();
		categories1.put("channelId", "weixin07");
		categories1.put("areaId", "shenzheng");
		consumerEntity1.setCategories(categories1);
		Cmd command1 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "consumer").buildData(consumerEntity1.getId());
		mconf.addConf(command1, consumerEntity1);
		
		ConsumerEntity consumerEntity2 = new ConsumerEntity();
		this.wrapperBaseEntity(consumerEntity2);
		consumerEntity2.setAppsecret(UUID.randomUUID().toString().replace("-", ""));
		consumerEntity2.setAppkey("714b4aaa");
		Map<String, Object> categories2 = new HashMap<String, Object>();
		categories2.put("channelId", "weixin07");
		categories2.put("areaId", "shenzheng");
		consumerEntity2.setCategories(categories2);
		Cmd command2 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "consumer").buildData(consumerEntity2.getId());
		mconf.addConf(command2, consumerEntity2);
		
		ConsumerEntity consumerEntity3 = new ConsumerEntity();
		this.wrapperBaseEntity(consumerEntity3);
		consumerEntity3.setAppsecret(UUID.randomUUID().toString().replace("-", ""));
		consumerEntity3.setAppkey("f74b334a");
		Map<String, Object> categories3 = new HashMap<String, Object>();
		categories3.put("channelId", "weixin06");
		categories3.put("areaId", "beijing");
		consumerEntity3.setCategories(categories3);
		Cmd command3 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "consumer").buildData(consumerEntity3.getId());
		mconf.addConf(command3, consumerEntity3);
	}
	
	@Test
	public void testApi() {
		ApiEntity apiEntity1 = new ApiEntity();
		this.wrapperBaseEntity(apiEntity1);
		apiEntity1.setService("UnionPay_core");
		apiEntity1.setGroup("S01");
		apiEntity1.setVersion("1.0");
		apiEntity1.setTitle("The UnionPay Core");
		Map<String, ApiParamType> reqHeaders1 = new HashMap<String, ApiParamType>();
		reqHeaders1.put("1", new ApiParamType("consumerId", "消费者ID", true, null, "String", null));
		reqHeaders1.put("2", new ApiParamType("areaId", "地区ID", true, null, "String", null));
		reqHeaders1.put("3", new ApiParamType("channelId", "渠道ID", true, null, "String", null));
		apiEntity1.setReqHeaders(reqHeaders1);
		Map<String, ApiParamType> resHeaders1 = new HashMap<String, ApiParamType>();
		resHeaders1.put("1", new ApiParamType("code", "通讯状态码", true, null, "String", null));
		resHeaders1.put("2", new ApiParamType("title", "状态标题", true, null, "String", null));
		resHeaders1.put("3", new ApiParamType("msg", "状态码描述", true, null, "String", null));
		apiEntity1.setResHeaders(resHeaders1);
		Cmd command1 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "api").buildData(apiEntity1.getId());
		mconf.addConf(command1, apiEntity1);
		
		ApiEntity apiEntity2 = new ApiEntity();
		this.wrapperBaseEntity(apiEntity2);
		apiEntity2.setService("test");
		apiEntity2.setGroup("S01");
		apiEntity2.setVersion("1.0");
		apiEntity2.setTitle("The test Test");
		Map<String, ApiParamType> reqHeaders2 = new HashMap<String, ApiParamType>();
		reqHeaders2.put("1", new ApiParamType("consumerId", "消费者ID", true, null, "String", null));
		reqHeaders2.put("2", new ApiParamType("areaId", "地区ID", true, null, "String", null));
		reqHeaders2.put("3", new ApiParamType("channelId", "渠道ID", true, null, "String", null));
		apiEntity2.setReqHeaders(reqHeaders2);
		Map<String, ApiParamType> resHeaders2 = new HashMap<String, ApiParamType>();
		resHeaders2.put("1", new ApiParamType("code", "通讯状态码", true, null, "String", null));
		resHeaders2.put("2", new ApiParamType("title", "状态标题", true, null, "String", null));
		resHeaders2.put("3", new ApiParamType("msg", "状态码描述", true, null, "String", null));
		apiEntity2.setResHeaders(resHeaders2);
		Cmd command2 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "api").buildData(apiEntity2.getId());
		mconf.addConf(command2, apiEntity2);
		
		ApiEntity apiEntity3 = new ApiEntity();
		this.wrapperBaseEntity(apiEntity3);
		apiEntity3.setService("CustInfo");
		apiEntity3.setGroup("S02");
		apiEntity3.setVersion("1.0");
		apiEntity3.setTitle("The CustInfo");
		Map<String, ApiParamType> reqHeaders3 = new HashMap<String, ApiParamType>();
		reqHeaders3.put("1", new ApiParamType("consumerId", "消费者ID", true, null, "String", null));
		reqHeaders3.put("2", new ApiParamType("areaId", "地区ID", true, null, "String", null));
		reqHeaders3.put("3", new ApiParamType("channelId", "渠道ID", true, null, "String", null));
		apiEntity3.setReqHeaders(reqHeaders3);
		Map<String, ApiParamType> resHeaders3 = new HashMap<String, ApiParamType>();
		resHeaders3.put("1", new ApiParamType("code", "通讯状态码", true, null, "String", null));
		resHeaders3.put("2", new ApiParamType("title", "状态标题", true, null, "String", null));
		resHeaders3.put("3", new ApiParamType("msg", "状态码描述", true, null, "String", null));
		apiEntity3.setResHeaders(resHeaders3);
		Cmd command3 = new Cmd().buildApp("node01", "ms-gateway").buildConf("test", "S01", "1.0", "api").buildData(apiEntity3.getId());
		mconf.addConf(command3, apiEntity3);
	}

}
