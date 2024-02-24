package com.example.demo.main;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

	@RequestMapping("/")
	public String index() {
		return "index";
	}
	
	@Autowired
	private MainService mainService;
	
	@RequestMapping(value="pages", method=RequestMethod.GET)
	@ResponseBody
	public List<Map<String,Object>> getPages(@RequestParam(name="url") String url, @RequestParam(name="token") String token) throws IOException {
		return mainService.getPages(url, token);
	}
	
	@RequestMapping(value="quizjson", method=RequestMethod.POST)
	@ResponseBody
	public List<Map<String,Object>> getQuizeToJson(@RequestBody Map<String,Object> body) throws IOException {
		return mainService.getQuizeToJson(body);
	}

	@RequestMapping(value="quiz", method=RequestMethod.POST)
	@ResponseBody
	public List<Map<String,Object>> getQuize(@RequestBody Map<String,Object> body) throws IOException {
		List<Map<String,Object>> json = (List<Map<String,Object>>)body.get("json");
		return null;
	}
	
}