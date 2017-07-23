package cn.ms.mconf.ui.ctrl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.ms.mconf.support.DataConf;
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
	 * 应用中心
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "apps")
	public String apps(HttpServletRequest request) {
		List<DataConf> appConfList = confService.getApps();
		System.out.println(appConfList);
		request.setAttribute("appconfs", appConfList);
		return "apps";
	}

	/**
	 * 配置中心
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "blocks")
	public String blocks(HttpServletRequest request) {
		List<DataConf> confConfList = confService.getBlocks();
		System.out.println(confConfList);
		request.setAttribute("confconfs", confConfList);
		return "blocks";
	}

	/**
	 * 数据清单
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
		List<DataConf> dataConfList = confService.getDatasources();
		System.out.println(dataConfList);
		request.setAttribute("datas", dataConfList);
		return "datasources";
	}

}
