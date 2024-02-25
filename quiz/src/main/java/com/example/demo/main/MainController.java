package com.example.demo.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
	
	@Autowired
	private MainService mainService;

	@RequestMapping("/")
	public String index() {
		return "index";
	}
	
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
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getQuize(@RequestBody Map<String,Object> body) throws IOException {
		
		int langCnt = 2; 
		int typeCnt = 2;
		
	
		
		List<Map<String,Object>> json = (List<Map<String,Object>>)body.get("json");
		int cnt = (int)body.get("cnt");
		int type = (int)body.get("type");
		String url = (String)body.get("url");
		
		int allQuizCnt = json.size()*langCnt*typeCnt;
		allQuizCnt = allQuizCnt < cnt ? allQuizCnt : cnt;
		
		
		Document dom = Jsoup.connect(url).get();
		url = url.substring(0,url.indexOf(".io")+3);
		Elements pageList = dom.select(".page__content.e-content"); 
		
		
		Map<String,Object> quizs = new HashMap<>();  
		
		Map<Integer,Integer> ranQuiz = new HashMap<Integer,Integer>();
		List<Integer> ranArr = new ArrayList<Integer>();
		List<Integer> keys = new ArrayList<Integer>();
		
		for(int i = 0; i < json.size(); i++) {
			ranArr.add(i);
		}
			
		for(int i = 0; i < allQuizCnt; i++) {
			int index = mainService.RAN(0, ranArr.size()-1); //ranArr의 무작위 인덱스값
			int ran = ranArr.get(index); //raArr의 해당 인덱스의 값
			
			if(ranQuiz.get(ran) == null) {
				ranQuiz.put(ran, 1);
				keys.add(ran);
			}
			else if(ranQuiz.get(ran) != langCnt*typeCnt) {
				ranQuiz.compute(ran, (k,v) -> v= v+1);
				if(ranQuiz.get(ran) == langCnt*typeCnt)
					ranArr.remove(index);
			}
		}
		
		
		
		
		
		for(int i = 0; i < allQuizCnt; i++) {
			int key = mainService.RAN(0, keys.size()-1);
			int index = keys.get(key);
			
			//index하나 가져와서 빼는 작업
			ranQuiz.compute(index, (k,v) -> v= v-1);
			if(ranQuiz.get(index) == 0) {
				ranQuiz.remove(index);
				keys.remove(key);
			}

			
			switch(type) {
			case 1: //객관식
				break;
			case 2: //주관식
				
				break;
			case 3: // 혼합
				
				
				
				
				
				break;
			default:
				break;
			}
		}
		
		
		return null;
	}
	
}
