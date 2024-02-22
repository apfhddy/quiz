package com.example.demo.restController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rest.PageVO;


@RestController
public class MyRestController {
	
	@GetMapping("pages")
	public List<Map<String,Object>> getPages(@RequestParam(name="url") String url, @RequestParam(name="token") String token) throws IOException {
		Document dom = Jsoup.connect(url).get();
		
		Elements pageList = dom.select(".page__content.e-content"); 
		
		List<Map<String,Object>> returnList = new ArrayList<>();
 
		Elements pageChild = pageList.get(0).children();
		for(int i = 0; i < pageChild.size(); i++) {
			//pd.setUrl(e.select("a").attr("href"));
			Map<String,Object> pageChildData = new HashMap<String, Object>();
			pageChildData.put("title", pageChild.get(i).select("a").text());
			returnList.add(pageChildData);
		}
		
		return returnList;
	}
	
}
