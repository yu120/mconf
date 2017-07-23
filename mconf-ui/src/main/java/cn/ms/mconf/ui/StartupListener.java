package cn.ms.mconf.ui;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 应用启动监听器
 * 
 * @author lry
 */
public class StartupListener implements ServletContextListener {

	/**
	 * 连接服务
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// 初始化国际化
		//sce.getServletContext().setAttribute(International.MSG_KEY, International.getMSG(null));
	}

	/**
	 * 关闭服务
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}