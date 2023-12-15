import com.mongodb.MongoException;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BOWModel
{
    private Table table;
    private List<String> listOfPositiveWords;
    private List<String> listOfNegativeWords;
    BOWModel()
    {
        table=new Table();
        listOfPositiveWords=getPositiveWords();
        listOfNegativeWords=getNegativeWords();
    }
    public void bowSentimentAnalysis()
    {
        try
        {
            List<String> listOfTitles=new ArrayList<>();
            //creating connection with MongoDb to fetch the documents
            String uri = "mongodb+srv://sainimisushank:Mongodb13%23@cluster0.qo0raya.mongodb.net/?retryWrites=true&w=majority";
            MongoClient mongoClient = MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase("reuterDb");
            MongoCollection<Document> collection = database.getCollection("newsArticles");
            //get all documents from the collection newsArticles
            MongoCursor<Document> cursor = collection.find().cursor();
            while (cursor.hasNext())
            {
                Document document = cursor.next();
                listOfTitles.add(document.get("title").toString().toLowerCase());
            }
            int newsNumber=1;
            String data[][]=new String[listOfTitles.size()+1][4];
            for (String title : listOfTitles)
            {
                int polarity=0;
                Map<String,Integer> bagOfWords=new HashMap<>();
                StringBuilder matches=new StringBuilder();
                List<String> wordsInTitle = new ArrayList<>(Arrays.asList(title.split(" ")));
                //creating bag of words for the title in current iteration
                for (String word : wordsInTitle)
                {
                    final int one = 1;
                    if (bagOfWords.containsKey(word))
                    {
                        bagOfWords.replace(word, bagOfWords.get(word) + one);
                    }
                    else
                    {
                        bagOfWords.put(word, one);
                    }
                }
                //comparing words in bag of words with positive and negative words
               for(String word:bagOfWords.keySet())
                {
                    for(String positiveWord: listOfPositiveWords)
                    {
                        if(word.equals(positiveWord))
                        {
                            polarity=polarity+bagOfWords.get(word);
                            matches.append(word).append(" ");
                            break;
                        }
                    }
                    for(String negativeWord:listOfNegativeWords)
                    {
                        if(word.equals(negativeWord))
                        {
                            polarity=polarity-bagOfWords.get(word);
                            matches.append(word).append(" ");
                            break;
                        }
                    }
                }
               if(polarity>0)
               {
                  data[newsNumber][0]= String.valueOf(newsNumber);
                  data[newsNumber][1]=title;
                  data[newsNumber][2]=matches.toString();
                  data[newsNumber][3]="Positive";

               }
               else if(polarity<0)
               {
                   data[newsNumber][0]= String.valueOf(newsNumber);
                   data[newsNumber][1]=title;
                   data[newsNumber][2]=matches.toString();
                   data[newsNumber][3]="Negative";
               }
               else
               {
                   data[newsNumber][0]= String.valueOf(newsNumber);
                   data[newsNumber][1]=title;
                   data[newsNumber][2]=matches.toString();
                   data[newsNumber][3]="Neutral";
               }
               newsNumber++;
            }
            table.createTable(data);
        }
        catch (MongoException e)
        {
            e.getMessage();
        }
    }
    private List<String> getPositiveWords()
    {
        List<String> listOfPositiveWords=new ArrayList<>();
        try(BufferedReader bufferedReader=new BufferedReader(new FileReader("positivewords.txt")))
        {
            String line;
            while((line=bufferedReader.readLine())!=null)
            {
                String positiveWord=line.trim();
                listOfPositiveWords.add(positiveWord);
            }
            return listOfPositiveWords;
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
    private List<String> getNegativeWords()
    {
        List<String> listOfNegativeWords=new ArrayList<>();
        try(BufferedReader bufferedReader=new BufferedReader(new FileReader("negativewords.txt")))
        {
            String line;
            while((line=bufferedReader.readLine())!=null)
            {
                String negativeWord=line.trim();
                listOfNegativeWords.add(negativeWord);
            }
            return listOfNegativeWords;
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
