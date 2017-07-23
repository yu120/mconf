package cn.ms.mconf.ui.ctrl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.ms.mconf.ui.service.ConfService;

@Controller
@RequestMapping("web")
public class WebCtrl {

	@Resource
	private ConfService confService;

	@RequestMapping(value = "index")
	public String index(HttpServletRequest request) {
		return "index";
	}

	/**
	 * 首页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "main")
	public String main(HttpServletRequest request) {
		return "main";
	}

	/**
	 * 首页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "apps")
	public String apps(HttpServletRequest request) {
		return "apps";
	}

	/**
	 * 配置块
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "blocks")
	public String blocks(HttpServletRequest request) {
		return "blocks";
	}

	/**
	 * 配置项
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "items")
	public String items(HttpServletRequest request) {
		return "items";
	}

	/**
	 * 数据源
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "datasources")
	public String datasources(HttpServletRequest request) {
		return "datasources";
	}

}
