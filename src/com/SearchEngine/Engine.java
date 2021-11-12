package com.SearchEngine;

import java.io.IOException;
import java.util.Scanner;

import features.Indexer;
import features.SpellCorrector;
import HelperClasses.DocRank;
import HelperClasses.SearchResults;

public class Engine
{
	static Indexer indexer = Indexer.getInstance();
	static SpellCorrector spellChecker = new SpellCorrector();
	
//	Get results for the corrected search term
	private static SearchResults getSearchResults(String term) throws IOException 
	{
		SearchResults searchResults = new SearchResults();
		
		searchResults.setQuery(term.trim());
		
		double startTime = System.nanoTime();
		searchResults.setResults(indexer.getFilteredDocuments(term));
		searchResults.setTimeTaken((System.nanoTime() - startTime) / 1000000.0);
		
		return searchResults;
	}
	
	public static void main(String[] agrs) throws Exception
	{	
		System.out.println();
		System.out.println("------------------------------------------------------------------------------------------------------------");
		System.out.println("\tHey from Team 15,\n\tIt’s great to serve you with our search engine! Let’s have a wonderful time together!");
		System.out.println("\tTeam Members:");
		System.out.println("\t\tBhardvaj Lukhi - 110044760\n"
							+ "\t\tKuldeep Padhiyar - 110049895\n"
							+ "\t\tDarsh Parikh - 110041405");
		System.out.println("\tSubmited to:");
		System.out.println("\t\tDr. Ikjot Saini");
		System.out.println("------------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		ProcessBar bar = new ProcessBar();
		bar.start();
		
//		Starting indexing web files
		indexer.startIndexer();
//		Indexing complete
		
		bar.stop();
		
//		Creating console input stream
		Scanner input = new Scanner(System.in);
		
		while(true)
		{
			System.out.println();
			System.out.print("Enter the Search term (or exit) : ");
			String searchTerm = input.nextLine();
			if (searchTerm.equalsIgnoreCase("exit"))
			{
				System.out.println();
				System.out.println("---------------------------------------------------");
				System.out.println(" It's hard to say goodbye, See you soon again ^_^");
				System.out.println("---------------------------------------------------");
				System.out.println();
				break;
			}
			
//			Checking if the spelling of term is right or not
			String correctedTerm = spellChecker.spellCheck(searchTerm);
			
//			Getting Search results 
			SearchResults result = getSearchResults(correctedTerm);
			if (!searchTerm.equalsIgnoreCase(result.getQuery())) 
			{
				System.out.println();
				System.out.println("Showing results for '" + result.getQuery() + "'");
				System.out.println("Instead for '"+ searchTerm + "'");
			}
			
//			Show time and number of results found for the search term
			System.out.println("\nAbout " + result.getResults().size() + " results ("+ String.format("%.5f", result.getTimeTaken() / 1000.0) + " seconds)");
			if (result.getResults().size() > 15)
			{
				System.out.println("\nTop 10 results...");
			}
			
//			Printing top 10 results for the search term
			int count = 0;
			int partition = 10;
			int additionalResults = 10;
			for (DocRank res : result.getResults()) 
			{	
				System.out.println("[" +count + "]" + "\t" + res.getDocTitle());
				System.out.println("\t" + res.getDocLink());
				System.out.println("\tTFIDF: " + res.getTfIdf());
				System.out.println();
				
				if (++count >= partition)
				{
					System.out.print("More "+ additionalResults + " results... (y/n): ");
					searchTerm = input.nextLine();
					
					if(searchTerm.equalsIgnoreCase("n"))
					{
						break;
					}
					else
					{
						partition += additionalResults;
					}
				}
			}
			
		}
		
//		Closing input stream
		input.close();
	}
}
