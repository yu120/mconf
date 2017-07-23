package cn.ms.mconf.ui.ctrl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
		Set<String> nodeSet = new HashSet<String>();
		List<DataConf> appList = confService.getApps();
		for (DataConf dc:appList) {
			nodeSet.add(dc.getNode());
		}
		
		Set<String> confSet = new HashSet<String>();
		List<DataConf> confList = confService.getConfs();
		for (DataConf dc:confList) {
			confSet.add(dc.getEnv());
		}
		
		request.setAttribute("appNum", appList.size());
		request.setAttribute("confNum", confList.size());
		request.setAttribute("nodeNum", nodeSet.size());
		request.setAttribute("envNum", confSet.size());
		
		return "main";
	}

	/**
	 * 应用列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "apps")
	public String apps(HttpServletRequest request) {
		List<DataConf> appList = confService.getApps();
		request.setAttribute("apps", appList);
		return "apps";
	}

	/**
	 * 配置分析
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "confs")
	public String confs(HttpServletRequest request) {
		List<DataConf> confList = confService.getConfs();
		request.setAttribute("confs", confList);
		return "confs";
	}

	/**
	 * 数据中心
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "datas")
	public String datas(String keywords, HttpServletRequest request) {
		List<DataConf> dataList = new ArrayList<DataConf>();
		List<DataConf> tempDataList = confService.getDatas();
		if(StringUtils.isNotBlank(keywords)){
			for (DataConf data:tempDataList) {
				if(data.toString().contains(keywords)){
					dataList.add(data);
				}
			}
			request.setAttribute("keywords", keywords);
		} else {
			dataList.addAll(tempDataList);
		}
		
		request.setAttribute("datas", dataList);
		return "datas";
	}

	/**
	 * 数据ID列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "dataids")
	public String dataids(HttpServletRequest request) {
		return "dataids";
	}
	
}
