package com.example.demo.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MyRestController {
	
	@GetMapping("pages")
	public String getPages(@RequestParam(name="url") String url, @RequestParam(name="token") String token) throws IOException {
		URL userMainPage = new URL(url); 
		
		BufferedReader bf = new BufferedReader(new InputStreamReader(userMainPage.openStream(),"UTF-8"));
		StringBuilder html = new StringBuilder();
		
		String line = null;
		while((line = bf.readLine()) != null) {
			html.append(line);
		}
		
		bf.close();
		Document dom = Jsoup.parse(html.toString());
		
		Elements pageList = dom.select(".entries-list");
		
		StringBuilder pageJson = new StringBuilder("[");
		for(Element e : pageList) {
			StringBuilder json = new StringBuilder("{");
				
			json.append("}");
		}
		pageJson.append("]");
		
		return null;
	}
	
}
