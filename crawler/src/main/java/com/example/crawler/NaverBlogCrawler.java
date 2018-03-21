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

public class NaverBlogCrawler {
	
	private final String baseUrl = "https://openapi.naver.com/v1/search";
	private final int MAX_COUNT = 1100;
	private final int RESULT_COUNT_PER_REQUEST = 50;
	
	public List<NaverBlog> searchBlogByKeyword(String keyword, String clientId, String clientSecret, 
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
				String apiURL = String.format("%s/blog.%s?query=%s&start=%d&display=%d&sort=%s", baseUrl, resFormat, text, startPosition, count, sort);
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
					
					JsonParser parser = new JsonParser();
					//json 문서를 읽어서 객체 트리를 구성하고, 루트 객체 반환
					JsonElement doc = parser.parse(new InputStreamReader(con.getInputStream()));
					JsonObject root = doc.getAsJsonObject();
					if (startPosition == 1) {
						int total = root.get("total").getAsInt();
						maxCount = Math.min(total, maxCount);
					}
					JsonArray items = root.get("items").getAsJsonArray();
					List<String> urls = extractBlogUrl(items); //블로그 주소만 추출 
					
					List<NaverBlog> blogs = extractBlog(urls); //주소로 블로그 정보 추출
					 
					list.addAll(blogs);
	
	
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
	
	public List<NaverBlog> extractBlog(List<String> urls) throws IOException {
		ArrayList<NaverBlog> blogInfoList = new ArrayList<>();
		
		for (String sUrl : urls) {
			//System.out.println(sUrl);
			NaverBlog blog = extractBlog(sUrl);
			//System.out.printf("%s 블로그 처리 중\n", sUrl);
			if(blog!=null)
				blogInfoList.add(blog);
			try {Thread.sleep(10);}
			
			
			catch (InterruptedException e) {}
			
		}
		return blogInfoList;
	}


	private NaverBlog extractBlog(String sUrl) throws MalformedURLException,IOException {
		
		NaverBlog blog = null;
		String logNoParam = sUrl.substring(sUrl.lastIndexOf("logNo"));
		URL url = new URL(sUrl);
		//HTML 문서를 읽고 객체 트리로 반환
		Document doc = Jsoup.parse(url, 2000);
		Elements elements = doc.select("#mainFrame");
		
		if(elements  == null || elements.size() == 0) {
			
		}
		else{
			Element mainFrame = elements.first();
			String srcUrl = mainFrame.attr("src");
			//System.out.println(srcUrl);
			
			
			if(srcUrl.contains("&amp;"))
					srcUrl = srcUrl.replaceAll("&amp;",  "&");
			
			
			if(srcUrl.contains("logNo=null"))
				srcUrl = srcUrl.replaceAll("logNo=null",  logNoParam);
			
			
			
			
			
			URL url2 = new URL("https://blog.naver.com"+srcUrl);
			//System.out.println(url2);
			Document contentDoc = Jsoup.parse(url2,2000);
			
			Elements contents = contentDoc.select(".sect_dsc");
			Elements contents2 = contentDoc.select("#postListBody");
			
			if(contents != null && contents.size()>0) {
				blog = new NaverBlog();
				Element content = contents.first();
				blog.setContent(content.text());
				//System.out.println(".sect_dsc"+content.text().substring(0, 20));
				//System.out.println(".sect_dsc : "+content.select(".pcol1").text());
			}
			else {
				System.out.println("Can not find data");
			}
			if(contents2 !=null & contents2.size()>0) {
				Element content = contents2.first();
				//System.out.println("#postListBody : "+content.text().substring(0, 20));
				//System.out.println("#postListBody : "+content.select(".pcol1").text());
			}
			else {
				//System.out.println("Can not find data");
			}
			
		}
		
		return blog;
	}

	public List<String> extractBlogUrl(JsonArray items) throws IOException {

		ArrayList<String> urls = new ArrayList<String>();
		
		for(JsonElement item : items) {
			JsonElement link = item.getAsJsonObject().get("link");
			String linkString = link.getAsString();
			if(linkString.contains("/blog.naver.com/"))
				urls.add(linkString);
		}
			

		return urls;
	}


}
