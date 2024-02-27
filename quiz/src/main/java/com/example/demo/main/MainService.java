package com.example.demo.main;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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
	//private int langCnt = 2; 
	//private int typeCnt = 2;
	
	private List<Map<String,Object>> funcList = new ArrayList<Map<String,Object>>();
	
	public MainService() {
		Map<String,Object> settingFunc = new HashMap<String, Object>();
		
		Function<Map<String,Object>, Map<String,Object>> f1 = param -> quiz1(param);
		settingFunc.put("func", f1);
		settingFunc.put("ex", false);
		funcList.add(settingFunc);
		
		settingFunc = new HashMap<String, Object>();
		Function<Map<String,Object>, Map<String,Object>> f2 = param -> quiz2(param);
		settingFunc.put("func", f2);
		settingFunc.put("ex", false);
		funcList.add(settingFunc);
		
	}
	
	
	
	
	public List<Map<String,Object>> getPages(String url, String token) throws IOException {
		if(!url.contains("http"))return null;
		
		
		Document dom = Jsoup.connect(url).get();
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
		URL page = null; 
		try {
			page = new URL("https://raw.githubusercontent.com/"+url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader br = null;
		
		StringBuilder textFile = new StringBuilder();
		String line = null;
		try {
			br =new BufferedReader(new InputStreamReader(page.openStream(),"UTF-8"));
			while((line = br.readLine())!= null) {
				textFile.append(line+",");
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String text = textFile.toString();
		
		List<String> titles = new ArrayList<String>();
		int start = -1;
		while((start = text.indexOf("---,")) != -1) {
			start+=4;
			int end = text.indexOf("===,")-1;
			String title = text.substring(start, end);
			text = text.substring(end+4);
			titles.add(title);
		}
		
		return titles;
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
		
		Document dom = Jsoup.connect(url).get();
		url = url.substring(0,url.indexOf(".io")+3);
		Elements pageList = dom.select(".page__content.e-content"); 
		
		int allQuizCnt = json.size()*funcList.size();
		allQuizCnt = allQuizCnt < cnt ? allQuizCnt : cnt;
		
		
		
		
		Map<Integer,Integer> indexCnt = new HashMap<Integer,Integer>();
		List<Integer> keys = new ArrayList<Integer>();
		
		addRandomQuiz(json.size(), allQuizCnt, indexCnt, keys);
		
		
		
		
		
		Map<Integer,List<Integer>> indexTitle = new HashMap<Integer, List<Integer>>();
		
		for(int i = 0; i < keys.size(); i++) {
			int key = keys.get(i);
			List<Integer> su = new ArrayList<Integer>();
			for(int j = 0; j < funcList.size(); j++) {
				su.add(j);
			}
			indexTitle.put(key, su);
		}
		System.out.println(pageList);
		
		List<Map<String,Object>> quizs = new ArrayList<>();  
		
		
		
		for(int i = 0; i < allQuizCnt; i++) {
			int index = getRandomQuiz(indexCnt, keys);
			Map<String,Object> oneJson = json.get(index);
			
			int ranfuncIndex = RAN(0,indexTitle.get(index).size()-1);
			int ranfunc = indexTitle.get(index).get(ranfuncIndex);
			indexTitle.get(index).remove(ranfuncIndex);
			
			
			Map<String,Object> parameter = new HashMap<String, Object>();
			parameter.put("json", oneJson); 
			
			if((boolean)funcList.get(ranfunc).get("ex")) {
				
				
			}
			
			quizs.add((Map<String,Object>)((Function<Map<String, Object>, Map<String, Object>>)funcList.get(ranfunc).get("func")).apply(parameter));
		
		}
		
		
		return quizs;
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
			else if(indexCnt.get(ran) != funcList.size()) {
				indexCnt.compute(ran, (k,v) -> v= v+1);
			}
			if(indexCnt.get(ran) == funcList.size())
				ranArr.remove(index);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> quiz1(Map<String,Object> parameter){
		Map<String,Object> oneQuiz = new HashMap<String, Object>();
		Map<String,Object> json = (Map<String,Object>)parameter.get("json");
		
		Map<String,Object> answers = new HashMap<String, Object>();
		String[] answersArr = ((String)json.get("kr")).split(",");
		for(int i = 0; i < answersArr.length; i++) {
			answers.put(answersArr[i], 1);
		}
		
		oneQuiz.put("title", json.get("en")+"의 뜻은?");
		oneQuiz.put("answers", answers);
		oneQuiz.put("ex", false);
		return oneQuiz;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> quiz2(Map<String,Object> parameter){
		Map<String,Object> oneQuiz = new HashMap<String, Object>();
		Map<String,Object> json = (Map<String,Object>)parameter.get("json");
		
		Map<String,Object> answers = new HashMap<String, Object>();
		answers.put((String)json.get("en"), 1);
		
		oneQuiz.put("title", json.get("kr")+"의 뜻을 가진 영어 스펠링은?");
		oneQuiz.put("answers", answers);
		oneQuiz.put("ex", false);
		return oneQuiz;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> quiz3(Map<String,Object> parameter){
		Map<String,Object> oneQuiz = new HashMap<String, Object>();
		Map<String,Object> json = (Map<String,Object>)parameter.get("json");
		
		Map<String,Object> answers = new HashMap<String, Object>();
		String[] answersArr = ((String)json.get("kr")).split(",");
		for(int i = 0; i < answersArr.length; i++) {
			answers.put(answersArr[i], 1);
		}
		
		oneQuiz.put("title", json.get("en")+"의 뜻은?");
		oneQuiz.put("answers", answers);
		oneQuiz.put("ex", false);
		return oneQuiz;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> quiz4(Map<String,Object> parameter){
		Map<String,Object> oneQuiz = new HashMap<String, Object>();
		Map<String,Object> json = (Map<String,Object>)parameter.get("json");
		
		Map<String,Object> answers = new HashMap<String, Object>();
		answers.put((String)json.get("en"), 1);
		
		oneQuiz.put("title", json.get("kr")+"의 뜻을 가진 영어 스펠링은?");
		oneQuiz.put("answers", answers);
		oneQuiz.put("ex", false);
		return oneQuiz;
	}
	
	
	
	
	
	
    public  int RAN(int min,int max) { /// 1 10
    	max = max - min +1;
    	return (int)(Math.random()*max)+min;
    }
}
