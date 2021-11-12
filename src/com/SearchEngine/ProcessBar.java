package com.SearchEngine;

import features.Indexer;
import features.Parser;

public class ProcessBar extends Thread 
{	
	public void run()
	{
		int count = 0;
		while(true)
		{
			System.out.print("|");
			count++;
			
			try 
			{
				Thread.sleep(500);
			} 
			catch (InterruptedException e) 
			{
			}
			
			if (count > 20)
			{
				Indexer indexer = Indexer.getInstance();
				float completed = ((indexer.id + 0.0f) / Parser.getWebPageFilesList().size()) * 100;
				completed = Math.round((completed)*100) / 100;
				
				System.out.println(" -- " + completed + "%");
				count = 0;
			}
		}
	}
}
