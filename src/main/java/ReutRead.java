import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReutRead
{
    /** This method extracts data from a given file, transforms the data and then loads the data as
     * key,value pairs in a MongoDb database
     *
     * @param file is the file from which data is tp be extracted and transformed
     */
    public void ETL(String file)
    {
        //read the content of a file line by line
        try(BufferedReader bufferedReader=new BufferedReader(new FileReader(file)))
        {
            String line;
            StringBuilder stringBuilder=new StringBuilder();
            //append each line to a string builder object to transform the file content into a string
            while((line=bufferedReader.readLine())!=null)
            {
                stringBuilder.append(line);
            }
            //transforming the data by replacing the newline with space
            String fileContent= stringBuilder.toString().replace("\n"," ");
            //extracting the data using regular expression
            String reGex="<TITLE>(.*?)<\\/TITLE>\\s*<DATELINE>(.*?)<\\/DATELINE><BODY>(.*?)<\\/BODY>";
            Pattern pattern=Pattern.compile(reGex);
            Matcher matcher=pattern.matcher(fileContent);
            List<String> listOfTitles=new ArrayList<>();
            List<String> listOfBodies=new ArrayList<>();
            List<String> listOfDateLines=new ArrayList<>();
            //storing list of titles, datelines and bodies in a news article
            while(matcher.find())
            {
               listOfTitles.add(matcher.group(1));
               listOfDateLines.add(matcher.group(2));
               listOfBodies.add(matcher.group(3));
            }
            //creating connection with MongoDb to load the data
            String uri ="mongodb+srv://sainimisushank:Mongodb13%23@cluster0.qo0raya.mongodb.net/?retryWrites=true&w=majority";
            MongoClient mongoClient= MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase("reuterDb");
            MongoCollection<Document> collection = database.getCollection("newsArticles");
            //insert a nested document in the mongodb database
            for(int i=0;i<= listOfDateLines.size()-1;i++)
            {
                Document mainDocument = new Document();
                Document textDocument = new Document();
                textDocument.append("dateline",listOfDateLines.get(i))
                            .append("body",listOfBodies.get(i));
                mainDocument.append("title",listOfTitles.get(i))
                            .append("text",textDocument);
                collection.insertOne(mainDocument);
            }
        }
        catch(IOException e)
        {
            e.getMessage();
        }
        catch(MongoException me)
        {
            System.out.println(me.getCause()+"\n"+me.getMessage());
        }
    }

}
