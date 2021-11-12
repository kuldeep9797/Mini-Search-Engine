package features;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler 
{
	private Set<String> pagesCrawledList = new HashSet<String>();
	private List<Document> webPageList = new ArrayList<Document>();

	public List<Document> getWebPageList() 
	{
		return webPageList;
	}

	public void setWebPageList(List<Document> webPageList) 
	{
		this.webPageList = webPageList;
	}

	private int maxDepth = 3;

	public void crawl() 
	{	
		String[] urls = { "https://medium.com/nyc-design/how-to-make-ui-shadows-that-dont-suck-53827f2f2cb",
						"https://beebom.com/microsoft-xbox-series-x-mini-fridges" };
		
		System.out.println("[ Starting the crawling ]");
		System.out.println();
		for (String url : urls) 
		{
			startCrawler(url, 0);
		}
		System.out.println();
		System.out.println("[ Finished crawling ]");
	}

	public void startCrawler(String url, int depth) 
	{
		if (depth <= maxDepth) 
		{
			try 
			{
				Document document = Jsoup.connect(url).get();
				Parser.saveDoc(document);
				webPageList.add(document);
				depth++;
				if (depth < maxDepth) 
				{
					Elements links = document.select("a[href]");
					for (Element page : links) 
					{
						if (shouldCrawlUrl(page.attr("abs:href"))) 
						{
							System.out.println(webPageList.size() + ": " + page.attr("abs:href"));
							startCrawler(page.attr("abs:href"), depth);
							pagesCrawledList.add(page.attr("abs:href"));
						}
					}
				}
			} 
			catch (Exception e) 
			{
				System.out.println("Error fetching url - " + url);
			}
		}
	}

	private boolean shouldCrawlUrl(String nextUrl) 
	{
		if (this.pagesCrawledList.contains(nextUrl)) 
		{
			return false;
		}
		if (nextUrl.startsWith("javascript:")) 
		{
			return false;
		}
		if (nextUrl.contains("mailto:")) 
		{
			return false;
		}
		if (nextUrl.contains("#") || nextUrl.contains("?")) 
		{
			return false;
		}
		if (nextUrl.endsWith(".swf")) 
		{
			return false;
		}
		if (nextUrl.endsWith(".pdf")) 
		{
			return false;
		}
		if (nextUrl.endsWith(".png")) 
		{
			return false;
		}
		if (nextUrl.endsWith(".gif")) 
		{
			return false;
		}
		if (nextUrl.endsWith(".jpg")) 
		{
			return false;
		}
		if (nextUrl.endsWith(".jpeg")) 
		{
			return false;
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		new Crawler().crawl();
	}
}

