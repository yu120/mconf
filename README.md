#配置中心(简称“微配置”,英文“Micro Conf”)

## 开源产品介绍（微服务基础设施<font color="red">QQ交流群：191958521</font>）
+ 配置中心(mconf)

1. GITHUB：https://github.com/yu120/mconf
2. 码云：https://git.oschina.net/yu120/mconf

+ 微核心(micro)

1. GITHUB：https://github.com/yu120/micro
2. 码云：https://git.oschina.net/yu120/micro

+ 微服务神经元(neural)

1. GITHUB：https://github.com/yu120/neural
2. 码云：https://git.oschina.net/yu120/neural

+ 微序列(sequence)

1. https://git.oschina.net/yu120/sequence


##1 概述
在分布式微服务架构中,当应用数量和各个应用部署实例的数量较多时,如果还是手动去实现配置信息的修改或数据的迁移等,其效率是很低的。且认为手动操作的也有可能出现错误的情况,从而引发应用发布错地方、启动不了、发不通等情况。

为了解决以上问题,开发了基于Zookeeper的配置中心(微服务配置中心:mconf),用于解决以上问题。同时新引入了微服务配置中心也为架构带来了运维成本和故障风险。因此建议不要强制依赖mconf,即没有mconf也能正常使用,当然有了mconf更好,可以为我们解决很多繁琐的事情。mconf依赖的Zookeeper可以靠集群来实现高可用,但mconf本身的问题也是可能存在的,所以使用请慎重。

##2 配置接口
+ void connection(ZkConnMsg zkConnMsg):连接配置中心
+ boolean isAvailable():查看配置中心是否存活
+ <T> void addConf(MetaData<T> metaData):添加一条配置信息
+ <T> void deleteConf(MetaData<T> metaData):删除一条配置信息
+ <T> void setConf(MetaData<T> metaData):设置一条配置信息
+ <T> T getConf(MetaData<T> metaData, Class<T> clazz):获取一条配置信息
+ <T> Map<String, T> getConfs(MetaData<T> metaData, Class<T> clazz):获取指定配置的所有配置信息
+ <T> void subscribeConf(MetaData<T> metaData, Class<T> clazz, MconfListener<T> mconfListener):订阅一条配置信息
+ <T> void unsubscribeConf(MetaData<T> metaData):取消订阅配置

##3 数据结构
格式：/[配置中心根节点]/[应用ID节点]/[配置文件名称节点]/[配置信息ID]{配置数据}

如：/mconf/gateway/routeRule/10001{测试数据},其中第四级的节点名称为“10001”,第四级节点数据为“测试数据”,且“测试数据”为序列化后的JSON字符串。

+ 数据结构：支持Object、Map、List等数据结构
+ 使用原则：第四级节点和节点数据必须同时共存,即要不都存在,要不都不存在,不能出现节点名称存在而节点数据不存在的情况。

##4 功能范围
+ mconf不支持多版本(version)、多场景(group)和多环境(env)。
+ mconf暂不支持本地缓存配置未离线文件,后期会考虑将拉下来的配置信息缓存到离线文件,解决对Zookeeper的强依赖问题。

##5 CRUD
配置中心目前已经支持zkclient和curator对Zookeeper对增删改查(CRUD)。

##6 Zookeeper连接与实现方式配置信息
+ confSpace:默认为mconf
+ connAddrs:ZK连接地址,默认值为127.0.0.1：2181。集群地址配置方式如：127.0.0.1:2181,127.0.0.1:2182
+ zkType:默认为curator
+ timeout:连接超时时间,默认值为15*1000ms
+ session:默认值为60*1000ms

##7 环境分类
7.1 测试环境
    提供测试人员使用，代码分支除了可以使用master分支外，其他的分支也是可以的。

7.2 回归环境
    如果同时有好几个人参与同一个项目，那么基于master分支可能拉出非常多的开发分支，那么当这些分支合并到master上后，master上的功能可能受到影响，这种情况下，会使用一个回归环境，部署master分支的代码。

7.3 预发布环境
    这个环境中，一般会连接生产环境的数据库，使用生产环境的数据来进行测试。

7.4 灰度发布
    预发布环境过后，就是灰度发布了。由于一个项目，一般会部署到多台机器，所以灰度1台至三台，看看新功能是否ok，如果失败则只需要回滚几台，比价方便。注意，由于是灰度发布几种几台，所以一般会使用跳板机，然后进行域名绑定，这样才可以保证只访问有最新代码的服务器。

7.5 生产发布
    所有服务器上的代码都已经是最新的了。


