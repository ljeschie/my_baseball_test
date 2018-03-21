package com.example.crawler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

public class App {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		String naverClientId = "WsfQx6AeNAbGr904WBZe";
		String naverSecretId = "6iniFJi6zA";
		int maxCount = 100;
		String resFormat = "json";
		String sort = "sim";
		Boolean mode = false;
		
		
		if(mode) {
			NaverBlogCrawler crawler = new NaverBlogCrawler();
			List<NaverBlog> blogs =crawler.searchBlogByKeyword("OOTP", naverClientId, naverSecretId, maxCount, resFormat, sort);
			
			FileOutputStream fos = new FileOutputStream("blong-serach-result.txt");
			PrintStream ps = new PrintStream(fos);
			
			for(NaverBlog blog : blogs) {
				ps.println(blog==null ? "비어있음" : blog.getContent());
				ps.println("==============================================");
				
			}
			System.out.println("end of Crawling");
		}
		else {
			CafeCrawler crawler = new CafeCrawler();
			crawler.searchCafeByKeyword("고양이", naverClientId, naverSecretId, maxCount, resFormat, sort);
			
		}
		
		
		
		
	}

}
