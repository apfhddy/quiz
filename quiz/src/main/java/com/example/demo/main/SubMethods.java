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

import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class SubMethods {
	
	//랜덤으로 선택하는 함수 한문제 당 최대 언어수 * 문제 형태로 가질수 있다
	public void addRandomQuiz(int jsonSize, int allQuizCnt, Map<Integer,Integer> indexCnt, List<Integer> keys,int functionSize) { 
		List<Integer> ranArr = new ArrayList<Integer>();
		
		for(int i = 0; i < jsonSize; i++) {
			ranArr.add(i);
		}
		
		for(int i = 0; i < allQuizCnt; i++) {
			int index = getPercentageIndex(functionSize,ranArr,indexCnt);
			int ran = ranArr.get(index); //raArr의 해당 인덱스의 값
			
			if(indexCnt.get(ran) == null) {
				indexCnt.put(ran, 1);
				keys.add(ran);
			}
			else if(indexCnt.get(ran) != functionSize) {
				indexCnt.compute(ran, (k,v) -> v= v+1);
			}
			if(indexCnt.get(ran) == functionSize) 
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
	
	
	public String getGitTextFile(String url) {
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
				textFile.append(line+"\r\n");
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return textFile.toString(); 
	}
	
	public Map<String,String> textToMap(String text) {
		Map<String,String> returnMap = new HashMap<String, String>();
		
		String[] textArr = text.split("/--\r\n");
		for(int i = 0; i < textArr.length; i++) {
			int keyStart = textArr[i].indexOf("---\r\n")+5;
			int keyEnd = textArr[i].indexOf("\r\n===")+2; 
			String key = textArr[i].substring(keyStart, keyEnd).replaceAll("\r\n","");
			
			String value = textArr[i].substring(keyEnd+5, textArr[i].length()-7);
			returnMap.put(key, value);
		}
		return returnMap;
	}	
	
	
	
	
	
	
	
	
	
	public List<String> getExs(Elements pages,int lang,String target,Map<Integer,Map<String,String>> rememverMap){
		Map<String,Integer> exs = new HashMap<>();
		
		List<String> returnList = new ArrayList<String>();
		
		for(int i = 0; exs.size() != 4; i++) {
			if(i == 30)break;
			int pageRan = RAN(0, pages.size()-1);
			Map<String,String> childIdMap = rememverMap.get(pageRan);
			
			if(childIdMap == null) {
				String text = getGitTextFile(pages.get(pageRan).select("a").attr("href").replace("/blob",""));
				childIdMap = textToMap(text);
				rememverMap.put(pageRan, childIdMap);
			}
			
			List<String> keys = new ArrayList<>(childIdMap.keySet()); 
			
			String keyRan = keys.get(RAN(0, keys.size()-1));
			
			String[] values = childIdMap.get(keyRan).split("\r\n");
			
			int valueRan = RAN(0, values.length-1);
			
			String[] value = values[valueRan].split(":");
			
			if(!value[lang-1].equals("target"))
				returnList.add(value[lang-1]);
		}
		
		System.out.println(returnList);
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> quiz1(Map<String,Object> parameter){
		Map<String,Object> oneQuiz = new HashMap<String, Object>();
		Map<String,Object> json = (Map<String,Object>)parameter.get("json");
		
		Map<String,Object> answers = new HashMap<String, Object>();
		String[] answersArr = ((String)json.get("lang2")).split(",");
		for(int i = 0; i < answersArr.length; i++) {
			answers.put(answersArr[i], 1);
		}
		
		oneQuiz.put("title", json.get("lang1")+"의 뜻은?");
		oneQuiz.put("answers", answers);
		oneQuiz.put("ex", false);
		return oneQuiz;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> quiz2(Map<String,Object> parameter){
		Map<String,Object> oneQuiz = new HashMap<String, Object>();
		Map<String,Object> json = (Map<String,Object>)parameter.get("json");
		
		Map<String,Object> answers = new HashMap<String, Object>();
		answers.put((String)json.get("lang1"), 1);
		
		oneQuiz.put("title", json.get("lang2")+"의 뜻을 가진 영어 스펠링은?");
		oneQuiz.put("answers", answers);
		oneQuiz.put("ex", false);
		return oneQuiz;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> quiz3(Map<String,Object> parameter){
		Map<String,Object> oneQuiz = new HashMap<String, Object>();
		Map<String,Object> json = (Map<String,Object>)parameter.get("json");
		
		Map<String,Object> answers = new HashMap<String, Object>();
		String[] answersArr = ((String)json.get("lang2")).split(",");
		for(int i = 0; i < answersArr.length; i++) {
			answers.put(answersArr[i], 1);
		}
		
		oneQuiz.put("title", json.get("lang1")+"의 뜻은?");
		oneQuiz.put("answers", answers);
		oneQuiz.put("ex", false);
		return oneQuiz;
	}
	
	
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> quiz4(Map<String,Object> parameter){
		Map<String,Object> oneQuiz = new HashMap<String, Object>();
		Map<String,Object> json = (Map<String,Object>)parameter.get("json");
		
		Map<String,Object> answers = new HashMap<String, Object>();
		answers.put((String)json.get("lang1"), 1);
		
		oneQuiz.put("title", json.get("lang2")+"의 뜻을 가진 영어 스펠링은?");
		oneQuiz.put("answers", answers);
		oneQuiz.put("ex", false);
		return oneQuiz;
	}
	
	public int getPercentageIndex(int allQuizCnt,List<Integer> ranArr,Map<Integer,Integer> indexCnt) {
		int total = 0;
		for(int index : ranArr) {
			Integer num = indexCnt.get(index);
			if(num == null)num = 0;
			total += allQuizCnt - num;
		}
		
		double[] percentageArr = new double[ranArr.size()];
		double[] sysArr = new double[ranArr.size()];
		
		double totalPercentage = 0;
		for(int i = 0; i < percentageArr.length; i++) {
			Integer num = indexCnt.get(ranArr.get(i));
			if(num == null)num = 0;
			totalPercentage += (double)(allQuizCnt-num)/total;
			
			sysArr[i] = (double)(allQuizCnt-num)/total;
			percentageArr[i] = totalPercentage;
		}
		
		double ran = Math.random();
		
		int targetIndex = 0;
		
		for(int i = 0; i < percentageArr.length; i++) {
			if(percentageArr[i] >= ran) {
				targetIndex = i;
				break;
			}
		}
		
		return targetIndex;
	}
	
	public boolean getExsCheck(String url){
		String text = getGitTextFile(url);
		Map<String,String> childIdMap = textToMap(text);
		
		int totalLength = 0;
		
		for(String key : childIdMap.keySet()) {
			totalLength += ((String)childIdMap.get(key)).split("\r\n").length;
		}
		
		return totalLength > 5;
	}
	
	
	
	
	
    public  int RAN(int min,int max) { /// 1 10
    	max = max - min +1;
    	return (int)(Math.random()*max)+min;
    }
	
}
