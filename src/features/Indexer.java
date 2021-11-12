package features;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import HelperClasses.DocFrequency;
import HelperClasses.DocInfo;
import HelperClasses.DocRank;
import textprocessing.TST;

// Singleton class for indexing the web documents
public class Indexer 
{
	private TST<List<DocFrequency>> indexedTerms = new TST<List<DocFrequency>>();
	private HashMap<Integer, DocInfo> documentIdNameMap = new HashMap<Integer, DocInfo>();
	String stemmedWord = "";
	public int id = 0;

	private static Indexer indexer;

//	Private constructor
	private Indexer() 
	{
	}

	public static Indexer getInstance() 
	{
		if (indexer == null) 
		{
			indexer = new Indexer();
		}
		return indexer;
	}

	public void startIndexer() throws IOException, InterruptedException 
	{
		List<String> tokenWords;
		int count = 0;
		
		System.out.println("[ Starting the indexing process ]");
		
		for (File file : Parser.getWebPageFilesList()) 
		{
//			System.out.println("Indexing (" + (++count) + "/" + Parser.getWebPageFilesList().size() + ") " + file.getName());
//			if (id % 2 == 0)
//			{
//				System.out.print("/");
//				TimeUnit.SECONDS.sleep(1);
//			}
			
			String title = file.getName().substring(0, file.getName().length() - 4);
			String parentDir = file.getAbsolutePath().replaceAll(Resources.txtDirectoryName, "");
			String link = parentDir.substring(0, parentDir.length() - 4) + ".html";
			String data = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
			double documentLength = data.split("[^a-zA-Z0-9'-]").length;
			
			documentIdNameMap.put(id, new DocInfo(title, link));
			tokenWords = Parser.parse(data);

			tokenWords.stream().filter(word1 -> word1.trim().length() > 1 || word1.length() > 1).forEach(word -> 
			{
				stemmedWord = word;
				if (null == indexedTerms.get(stemmedWord)) 
				{
					indexedTerms.put(stemmedWord, new ArrayList<DocFrequency>());
					indexedTerms.get(stemmedWord).add(new DocFrequency(id, 1, documentLength));
				} 
				else 
				{
					List<DocFrequency> docList = indexedTerms.get(stemmedWord);
					if (docList.contains(new DocFrequency(id))) 
					{
						DocFrequency docFreqObj = docList.get(docList.indexOf((new DocFrequency(id, documentLength))));
						docFreqObj.addOccurrence();
					} 
					else 
					{
						DocFrequency newDoc = new DocFrequency(id, documentLength);
						newDoc.addOccurrence();
						docList.add(newDoc);
					}
				}
			});
			id++;
		}
		System.out.println();
		System.out.println("[ Indexing process complete ]");
	}

	public List<DocRank> tfIdf(String term) 
	{
		List<DocRank> docRankList = new ArrayList<DocRank>();
		int totalDocuments = Parser.getWebPageFilesList().size();
		if (indexedTerms.get(term) != null) 
		{
			double docListLength = indexedTerms.get(term).size();
			for (DocFrequency doc : indexedTerms.get(term)) 
			{
				docRankList.add(new DocRank(doc.getDocumentId(), documentIdNameMap.get(doc.getDocumentId()).getDocTitle(),documentIdNameMap.get(doc.getDocumentId()).getDocLink(),doc.getTermFrequency() * Math.log10(totalDocuments / docListLength)));
			}
		}
		return docRankList;
	}

	public List<DocRank> getFilteredDocuments(String query) 
	{
		String[] queryTokens = query.split(" ");

		List<DocRank> filteredDocumentsList = tfIdf(queryTokens[0]);
		for (int i = 1; i < queryTokens.length; i++) 
		{
			for (DocRank doc : tfIdf(queryTokens[i])) 
			{
				if (filteredDocumentsList.contains(doc)) 
				{
					filteredDocumentsList.get(filteredDocumentsList.indexOf(doc)).addTfIdf(doc.getTfIdf());
				}
			}
		}
		filteredDocumentsList.sort((c1, c2) -> Double.compare(c2.getTfIdf(), c1.getTfIdf()));
		return filteredDocumentsList;
	}

	public TST<List<DocFrequency>> getIndexedTerms() 
	{
		return indexedTerms;
	}

}
