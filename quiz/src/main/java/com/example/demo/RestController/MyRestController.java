package com.example.demo.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PageVO;


@RestController
public class MyRestController {
	
	@GetMapping("pages")
	public List<PageVO> getPages(@RequestParam(name="url") String url, @RequestParam(name="token") String token) throws IOException {
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
		
		List<PageVO> returnList = new ArrayList<>();

		Elements pageChild = pageList.get(0).children();
		for(Element e : pageChild) {
			PageVO pd = new PageVO();
			pd.setTitle(e.select("a").text());
			pd.setUrl(e.select("a").attr("href"));
			returnList.add(pd);
		}
		
		return returnList;
	}
	
}
