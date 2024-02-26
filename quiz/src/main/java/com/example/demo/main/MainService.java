package com.example.demo.main;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class MainService {
	private int langCnt = 2; 
	private int typeCnt = 2;
	
	private Map<String,Object> settingFunc = new HashMap<String, Object>();
	
	public MainService() {
		Function<Integer, Map<String,Object>> f1 = s -> quiz1(s);
		
		settingFunc.put("func", f1);
		settingFunc.put("func", f1);
		System.out.println(settingFunc.get("func").getClass()); 
	}
	
	
	
	
	public List<Map<String,Object>> getPages(String url, String token) throws IOException {
		if(!url.contains("http"))return null;
		
		
		Document dom = Jsoup.connect(url).get();
		url = url.substring(0,url.indexOf(".io")+3);
		Elements pageList = dom.select(".page__content.e-content"); 
		
		List<Map<String,Object>> returnList = new ArrayList<>();
 
		Elements pageChild = pageList.get(0).children();
		for(int i = 0; i < pageChild.size(); i++) {
			Map<String,Object> pageChildData = new HashMap<String, Object>();
			
			Document childDom = Jsoup.connect(url+pageChild.get(i).select("a").attr("href")).get();
			Elements mainDom = childDom.select(".page__content.e-content > ul");
			
			Elements tables = mainDom.get(0).children();
			List<String> childs = new ArrayList<String>();
			for(Element e : tables) {
				childs.add(e.select("h1").text());
			}
			pageChildData.put("title", pageChild.get(i).select("a").text());
			pageChildData.put("url", pageChild.get(i).select("a").attr("href"));
			pageChildData.put("childs", childs);
			returnList.add(pageChildData);
		}
		
		return returnList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getQuizeToJson(Map<String,Object> body) throws IOException {
		Map<String,Object> checkList =  (Map<String,Object>)body.get("checkList");
		String url = (String)body.get("url");
		url = url.substring(0,url.indexOf(".io")+3);
		
		
		List<Elements> trs = new ArrayList<Elements>();
		
		for(String pageIndex : checkList.keySet()) {
			Map<String,Object> pageData = (Map<String,Object>)checkList.get(pageIndex);
			
			Document dom = Jsoup.connect(url+pageData.get("url")).get();
			
			for(String childTitle : ((Map<String,Object>)pageData.get("childs")).keySet()) {
				trs.add((dom.getElementById(childTitle).parent().children().get(1).children().get(0).children()));
			}
		}
		
		List<Map<String,Object>> quizjson = new ArrayList<Map<String,Object>>();
		
		for(Elements es : trs) {
			for(Element e : es) {
				Map<String,Object> item = new HashMap<String, Object>();
				
				item.put("en", e.children().get(0).text());
				item.put("kr", e.children().get(1).text());
				
				quizjson.add(item);
			}
		}
		return quizjson;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getQuize(@RequestBody Map<String,Object> body) throws IOException {
		
		List<Map<String,Object>> json = (List<Map<String,Object>>)body.get("json");
		int cnt = (int)body.get("cnt");
		int type = (int)body.get("type");
		String url = (String)body.get("url");
		
		int allQuizCnt = json.size()*langCnt*typeCnt;
		allQuizCnt = allQuizCnt < cnt ? allQuizCnt : cnt;
		
		
		
		
		Map<String,Object> quizs = new HashMap<>();  
		
		Map<Integer,Integer> indexCnt = new HashMap<Integer,Integer>();
		List<Integer> keys = new ArrayList<Integer>();
		
		addRandomQuiz(json.size(), allQuizCnt, indexCnt, keys);
		System.out.println(indexCnt);
		
		
		
		
		
		Map<Integer,List<Integer>> indexTitle = new HashMap<Integer, List<Integer>>();
		
		for(int i = 0; i < keys.size(); i++) {
			int key = keys.get(i);
			List<Integer> su = new ArrayList<Integer>();
			for(int j = 0; j < langCnt*typeCnt; j++) {
				su.add(j);
			}
			indexTitle.put(key, su);
		}
		
		
		
		
		for(int i = 0; i < allQuizCnt; i++) {
			int index = getRandomQuiz(indexCnt, keys);
			Map<String,Object> oneJson = json.get(index);
			
			
			
		
		}
		
		
		return null;
	}
	
	
	
	
	
	
	//랜덤으로 선택하는 함수 한문제 당 최대 언어수 * 문제 형태로 가질수 있다
	public void addRandomQuiz(int jsonSize, int allQuizCnt, Map<Integer,Integer> indexCnt, List<Integer> keys) {
		List<Integer> ranArr = new ArrayList<Integer>();
		
		for(int i = 0; i < jsonSize; i++) {
			ranArr.add(i);
		}
			
		for(int i = 0; i < allQuizCnt; i++) {
			int index = RAN(0, ranArr.size()-1); //ranArr의 무작위 인덱스값
			int ran = ranArr.get(index); //raArr의 해당 인덱스의 값
			
			if(indexCnt.get(ran) == null) {
				indexCnt.put(ran, 1);
				keys.add(ran);
			}
			else if(indexCnt.get(ran) != langCnt*typeCnt) {
				indexCnt.compute(ran, (k,v) -> v= v+1);
				if(indexCnt.get(ran) == langCnt*typeCnt)
					ranArr.remove(index);
			}
		}
	}
	
	public int getRandomQuiz(Map<Integer,Integer> indexCnt, List<Integer> keys) {
		int key = RAN(0, keys.size()-1);
		int index = keys.get(key);
		
		//index하나 가져와서 빼는 작업
		indexCnt.compute(index, (k,v) -> v= v-1);
		if(indexCnt.get(index) == 0) {
			indexCnt.remove(index);
			keys.remove(key);
		}
		return index;
	}
	
	
	public Map<String,Object> quiz1(int a){
		return null;
	}
	
	
	
	
	
	
	
	
	
    public  int RAN(int min,int max) { /// 1 10
    	max = max - min +1;
    	return (int)(Math.random()*max)+min;
    }
}
