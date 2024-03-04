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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


@Service
public class MainService {
	
	@Autowired
	private SubMethods subMethods;
	
	
	private List<Map<String,Object>> funcList = new ArrayList<Map<String,Object>>();
	
	public MainService() {
		Map<String,Object> settingFunc = new HashMap<String, Object>();
		
		Function<Map<String,Object>, Map<String,Object>> f1 = param -> subMethods.quiz1(param);
		settingFunc.put("func", f1);
		settingFunc.put("ex", false);
		funcList.add(settingFunc);
		
		settingFunc = new HashMap<String, Object>();
		Function<Map<String,Object>, Map<String,Object>> f2 = param -> subMethods.quiz2(param);
		settingFunc.put("func", f2);
		settingFunc.put("ex", false);
		funcList.add(settingFunc);
		
	}
	
	
	
	
	public List<Map<String,Object>> getPages(String url, String token)  {
		if(!url.contains("http"))return null;
		
		
		Document dom = null;
		try {
			dom = Jsoup.connect(url).get();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Elements pageList = dom.select(".react-directory-row.undefined"); 
		
		List<Map<String,Object>> returnList = new ArrayList<>();
		
		for(int i = pageList.size()-1; i > -1; i--) {
			Map<String,Object> onePage = new HashMap<String, Object>();
			Element e = pageList.get(i).select("a").get(1);
			onePage.put("title", e.attr("title").replace(".txt", ""));
			onePage.put("url", e.attr("href").replace("/blob", ""));
			returnList.add(onePage); 
		}
		 
		return returnList;
	}
	
	
	public List<String> getChilds(String url) {
		
		String text = subMethods.getGitTextFile(url);
		
		List<String> titles = new ArrayList<String>();
		int start = -1;
		while((start = text.indexOf("---\r\n")) != -1) {
			start+=5;
			int end = text.indexOf("\r\n===")+2; 
			String title = text.substring(start, end).replaceAll("\r\n",""); 
			text = text.substring(end+6); 
			titles.add(title);
		}
		
		return titles;
	} 
	
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String,String>> getQuizeToJson(Map<String,Object> body) throws IOException {
		
		Map<String,Object> checkList =  (Map<String,Object>)body.get("checkList");
		
		
		List<Map<String,String>> quizjson = new ArrayList<Map<String,String>>();
		
		for(String pageIndex : checkList.keySet()) {
			Map<String,Object> pageData = (Map<String,Object>)checkList.get(pageIndex);
			String text = subMethods.getGitTextFile((String)pageData.get("url"));
			Map<String,String> childIdMap = subMethods.textToMap(text);
			
			
			for(String childId : ((Map<String,Object>)pageData.get("childs")).keySet()) {
				
				String[] values = childIdMap.get(childId).split("\r\n");
				for(String value : values) {
					Map<String,String> oneMap = new HashMap<String, String>();
					String[] v = value.split(":");
					oneMap.put("lang2", v[1]);
					oneMap.put("lang1", v[0]);
					quizjson.add(oneMap);
				}
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
		
		Document dom = Jsoup.connect(url).get();
		Elements exPageList = dom.select(".react-directory-row.undefined"); 
		
		int allQuizCnt = json.size()*funcList.size();
		allQuizCnt = allQuizCnt < cnt ? allQuizCnt : cnt;
		
		
		if(exPageList.size() == 1) {
			String checkUrl = exPageList.get(0).select("a").attr("href").replace("/blob", ""); 
			if(!subMethods.getExsCheck(checkUrl)) {
				
			}
		}
		
		
		
		Map<Integer,Integer> indexCnt = new HashMap<Integer,Integer>();
		List<Integer> keys = new ArrayList<Integer>();
		
		subMethods.addRandomQuiz(json.size(), allQuizCnt, indexCnt, keys,funcList.size());
		
		
		
		
		
		Map<Integer,List<Integer>> indexTitle = new HashMap<Integer, List<Integer>>();
		
		for(int i = 0; i < keys.size(); i++) {
			int key = keys.get(i);
			List<Integer> su = new ArrayList<Integer>();
			for(int j = 0; j < funcList.size(); j++) {
				su.add(j);
			}
			indexTitle.put(key, su);
		}
		
		List<Map<String,Object>> quizs = new ArrayList<>();  
		
		//예시문제를 계속 텍스트에서 map으로 변환하면 손해 여서 저장
		Map<Integer,Map<String,String>> rememverMap = new HashMap<Integer, Map<String,String>>();
		
		for(int i = 0; i < allQuizCnt; i++) {
			int index = subMethods.getRandomQuiz(indexCnt, keys);
			Map<String,Object> oneJson = json.get(index);
			
			int ranfuncIndex = subMethods.RAN(0,indexTitle.get(index).size()-1);
			int ranfunc = indexTitle.get(index).get(ranfuncIndex);
			indexTitle.get(index).remove(ranfuncIndex);
			
			
			Map<String,Object> parameter = new HashMap<String, Object>();
			parameter.put("json", oneJson); 
			
			
			subMethods.getExs(exPageList,1,(String)oneJson.get("lang1"),rememverMap);
			
			if((boolean)funcList.get(ranfunc).get("ex")) {
				
				
			}
			
			quizs.add((Map<String,Object>)((Function<Map<String, Object>, Map<String, Object>>)funcList.get(ranfunc).get("func")).apply(parameter));
		
		}
		
		
		return quizs;
	}
	
	
	

}
