package cn.ms.mconf.ui.ctrl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("web")
public class WebCtrl {

	@RequestMapping(value = "index")
	public String index(HttpServletRequest request) {
		return "index";
	}
	
	@RequestMapping(value = "main")
	public String main(HttpServletRequest request) {
		return "main";
	}

}
