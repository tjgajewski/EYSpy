package infrastructure.xpath;

import infrastructure.thread.ThreadManager;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xpath.parser.CommandList;
import xpath.parser.Queryable;
import xpath.parser.Xpath;

import java.util.List;

public class DocXpath {

    public static Elements findAllByXpath(Element doc, String xpath){
        Xpath xpath2 = new Xpath(new DocumentElmentQueryable(doc, xpath));
        CommandList commandList = xpath2.compile(xpath);
        List<Queryable> queryableList=commandList.execute();
        Elements searchedForElements = new Elements();
        for(Queryable queryable:queryableList){
            searchedForElements.add(((DocumentElmentQueryable) queryable).ele);
        }
        return searchedForElements;
    }
}
