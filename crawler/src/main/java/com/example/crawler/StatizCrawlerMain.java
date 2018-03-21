package com.example.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

class StatizCrawler {
	
	static Scanner scan;
	public void run() {
		boolean b = true;
		String input = "";
		scan = new Scanner(System.in);
		
		while(b) {
			
			input="";
			System.out.println("크롤링한 대상 선텍");
			System.out.println("*******************************");
			System.out.println("1. 시즌 기록 (선수)");
			System.out.println("2. 시즌 기록 (팀)");	
			System.out.println("3. 종료");	
			System.out.println("*******************************");
			input = scan.nextLine();
			if(input.equals("1")) {
				getSeasonRecord_player();
			}
			else if(input.equals("2")) {
				
			}
			else if(input.equals("3")) {
				b=false;
			}
			
			scan.nextLine();
			
		}
	}
	
	
	void getSeasonRecord_player(){
		HttpURLConnection conn;
		String urlstring = "http://www.statiz.co.kr/stat.php?ml=2&";
		String input ="";
		System.out.println("1. 타격 / 2. 투구 / 3. 수비 / 그외. 종료");
		input = scan.nextLine();
	 
		if(input.equals("1")) {
			urlstring+="re=0";
		}else if(input.equals("2")) {
			urlstring+="re=1";
		}else if(input.equals("3")) {
			urlstring+="re=2";
		}else{
			return;
		} 
		try {
			URL url = new URL(urlstring);
			conn = (HttpURLConnection)url.openConnection();
			
			
			if(conn.getResponseCode()==200) {
				//System.out.println("잘뜸\n");
				Document doc = Jsoup.connect(urlstring).get(); 
				String title = doc.title();
				System.out.println(title);
				 
				ArrayList<String> colnames = new ArrayList<String>();
				ArrayList<String> rows = new ArrayList<String>();
				
				
				Element e = doc.getElementById("mytable");  
				Element tr1 = e.child(0).child(0);
				Element tr2 = e.child(0).child(1);
				Elements es = tr1.getElementsByTag("th"); 
				Elements es2 = tr2.getElementsByTag("th"); 
				
				int rowcount=0;
				 
				for(int i=1;i<es.size();i++) {
 
					Element node = es.get(i);
					Element tnode = node;
					String trc = node.attr("colspan");
					
					if(i==3) continue;
					
					if(!trc.equals("1") && trc.length()>0) {
						for(int j=1;j<es2.size();j++) { 
							Element snode = es2.get(j); 
							
							while(snode.tagName().equals("span") || snode.tagName().equals("a")) {
								node=node.child(0);
							}
							colnames.add(snode.text());
							//System.out.println(snode.tagName()+"/"+snode.text());
						}
						// 분할 컬럼 리스트, 0번은 정렬값 이니 중복이므로 제외 가능
						// 각 페이지당 분할 컬럼은, 정렬을 제외하고 한개
					}
					else {
						while(tnode.tagName().equals("span") || tnode.tagName().equals("a")) {
							tnode=tnode.child(0);
						}
						colnames.add(node.text());
						//System.out.println(node.tagName()+"/"+node.text()+"/"+trc);
					}
				}
				//컬럼 리스트, 0번은 정렬값이니, 중복이므로 제외함 *이찍힌 컬럼은 정렬컬럼
				//여기까지 컬럼
				
				Element countdiv = doc.getElementsByClass("col-md-12 col-xs-12 col-sm-12 col-lg-12").first().getElementsByClass("row").get(3).child(0).child(0)
						.getElementsByClass("box-body").last().child(0).child(0).getElementsByTag("td").last().child(2);
				
				if(countdiv.text().equals("마지막")) {
					String chref = countdiv.attr("href");
					rowcount = Integer.parseInt(chref.substring(chref.indexOf("&pa=")+4,chref.indexOf("&si")))+30;
				}
				// 카운트 구하기
				
				if(rowcount == 0)
					return;
				
				
				String urlstring2 = urlstring+"&pa=0&sn="+rowcount; 
				
				System.out.println(urlstring2);
				
//				Document doc2 = Jsoup.connect(urlstring2).get(); 
//				
//				Element tbody = doc2.getElementById("mytable").child(0);   
//				
//				System.out.println(tbody.getElementsByTag("tr").size());
				
				
				// 메모리 한계 떄문에 이렇게 함
				HttpURLConnection conn2 = (HttpURLConnection)new URL(urlstring2).openConnection();
				int resCode = conn2.getResponseCode();
				if (resCode == 200) {
					InputStream is = conn2.getInputStream();
					Document doc2 = Jsoup.parse(is, "utf-8", "http://www.statiz.co.kr/");
					Elements trs = doc2.select("#mytable tr");
					//System.out.println("TABLE COUNT : " + trs.size());
					
					
					System.out.println(trs.size());
					for(int i=2;i<trs.size();i++) {
						Elements tds = trs.get(i).select("td");
						String rowString="";
						rowString = rotateTds(tds);
						rows.add(rowString);
						// j는 1부터 시작해서 3은 중복이니 패스
					}
					
					
					is.close();
				}
//				
//				for(String s : colnames) {
//					System.out.print(s+"\t");
//				}
//				System.out.println();
//				for(String s : rows) {
//					System.out.println(s);
//				}
					
				
				// 2번쨰 
				
				
				
				
				
				 
	  
				 
				  
			}else {
				System.out.println("에러");
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	String rotateTds(Elements tds) {
		String rowString ="";
		for(int j=1;j<tds.size();j++) {
			if(j==2) {
				Elements spans = tds.get(j).select("span");
				rowString+=spans.get(2).text()+"\t";
				if(spans.size()>4)
					rowString+=spans.get(3).text()+"\t";
				else
					rowString+="NA\t";
				
			}
			else {
				rowString+=tds.get(j).text()+"\t";
			}
		}
		return rowString;
		
		
		
		
		
	}
	
	void getColName_player() {
		
	}
	
	void getSeasonRecord_team(){
		
	}
}


public class StatizCrawlerMain {
	public static void main(String[] args) {
		StatizCrawler c = new StatizCrawler();
		c.run();
	}
	
}
