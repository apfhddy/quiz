package com.example.demo.main;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class MainService {
	
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
				item.put("kr", e.children().get(0).text());
				
				quizjson.add(item);
			}
		}
		return quizjson;
	}
	
	
	
	
    public  int RAN(int min,int max) { /// 1 10
    	max = max - min +1;
    	return (int)(Math.random()*max)+min;
    }
}
