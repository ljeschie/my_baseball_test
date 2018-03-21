package com.example.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CafeCrawler {
	
	private final String baseUrl = "https://openapi.naver.com/v1/search";
	private final int MAX_COUNT = 1100;
	private final int RESULT_COUNT_PER_REQUEST = 50;
	
	public List<NaverBlog> searchCafeByKeyword(String keyword, String clientId, String clientSecret, 
			int maxCount, String resFormat, String sort) {
		
		if (maxCount > MAX_COUNT) {
			throw new RuntimeException("최대 검색량 한도(1100건)를 초과했습니다.");
		}
		
		// 검색 결과를 저장하는 컬렉션
		List<NaverBlog> list = new ArrayList<NaverBlog>();
		
		try {
			int startPosition = 1;
			String text = URLEncoder.encode(keyword, "UTF-8");

			while (startPosition <= maxCount) {
				int count = Math.min(RESULT_COUNT_PER_REQUEST, maxCount - startPosition);
				String apiURL = String.format("%s/cafearticle.%s?query=%s&start=%d&display=%d&sort=%s", baseUrl, resFormat, text, startPosition, count, sort);
				URL url = new URL(apiURL);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("X-Naver-Client-Id", clientId);
				con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
				int responseCode = con.getResponseCode();
				
				if (responseCode == 200) {
					
//					InputStreamReader reader = new InputStreamReader(con.getInputStream());
//					BufferedReader breader = new BufferedReader(reader);
//					while(true) {
//						String line = breader.readLine();
//						if(line == null) break;
//						System.out.println(line);
//					}
					// 전략, 링크 가져와서 제대로 응답한 경우만 파싱
					
					
					
					JsonParser parser = new JsonParser();
					//json 문서를 읽어서 객체 트리를 구성하고, 루트 객체 반환
					JsonElement doc = parser.parse(new InputStreamReader(con.getInputStream()));
					JsonObject root = doc.getAsJsonObject();
					if (startPosition == 1) {
						int total = root.get("total").getAsInt();
						maxCount = Math.min(total, maxCount);
					}
					
					JsonArray items = root.get("items").getAsJsonArray();
					List<String> urls = extractCafeUrl(items); //블로그 주소만 추출 
					for(String url2 : urls)
						System.out.println(url2);
					
	
				} else {
					System.out.println("Error : " + responseCode);
				}
				startPosition += RESULT_COUNT_PER_REQUEST;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
 
	public List<String> extractCafeUrl(JsonArray items) throws IOException {

		ArrayList<String> urls = new ArrayList<String>();
		 
		for(JsonElement item : items) {
			JsonElement link = item.getAsJsonObject().get("link");
			String linkString = link.getAsString();
			if(linkString!=null && linkString.contains("/cafe.naver.com/"))
				urls.add(linkString);
			
		}
			

		return urls;
	}


}
