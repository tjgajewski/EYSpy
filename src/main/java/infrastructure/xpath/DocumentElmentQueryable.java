package infrastructure.xpath;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xpath.parser.Queryable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class DocumentElmentQueryable implements Queryable {
    public Element ele;
    String xpath;
    public DocumentElmentQueryable(Element ele, String xpath){
        this.ele=ele;
        this.xpath=xpath;
    }

    @Override
    public List<Queryable> findAllChildren(String s, List<HashMap<String, String>> list, int indexToReturn) {
        List<Queryable> matchingEles = new ArrayList<>();
        Elements children = ele.children();
        for(int j =0;j<children.size();j++){
            Element child = children.get(j);
            if(child.tagName().equals(s)){
                matchingEles.add(new DocumentElmentQueryable(child, xpath));
            }
        }
        if(matchingEles.size()==0||matchingEles.size()<=indexToReturn-1){
            System.out.println("Unable to find an element using By.xpath: "+xpath);
            return new ArrayList<>();
     //       throw new NoSuchElementException("Unable to find an element using By.xpath: "+xpath);
        }
        if(indexToReturn!=-1){
            indexToReturn=indexToReturn-1;
            List<Queryable> results = new ArrayList<>();
            results.add(matchingEles.get(indexToReturn));
            return results;
        }
        return matchingEles;
    }

    @Override
    public List<Queryable> findParent() {
        return null;
    }

    @Override
    public List<Queryable> findAllDescendents(String s, List<HashMap<String, String>> list, int i) {
        return null;
    }

    @Override
    public List<Queryable> findAllAncestors(String s, List<HashMap<String, String>> list, int i) {
        return null;
    }

    @Override
    public List<Queryable> findAllFollowing(String s, List<HashMap<String, String>> list, int i) {
        return null;
    }

    @Override
    public List<Queryable> findAllFollowingSibling(String s, List<HashMap<String, String>> list, int i) {
        return null;
    }

    @Override
    public List<Queryable> findAllPreceding(String s, List<HashMap<String, String>> list, int i) {
        return null;
    }

    @Override
    public List<Queryable> findAllPrecedingSibling(String s, List<HashMap<String, String>> list, int i) {
        return null;
    }

    @Override
    public List<Queryable> findAllFollowingSiblingAll(String s, List<HashMap<String, String>> list, int i) {
        return null;
    }

    @Override
    public List<Queryable> findAllFollowingAll(String s, List<HashMap<String, String>> list, int i) {
        return null;
    }

    @Override
    public List<Queryable> findAllPrecedingAll(String s, List<HashMap<String, String>> list, int i) {
        return null;
    }

    @Override
    public List<Queryable> findAllPrecedingSiblingAll(String s, List<HashMap<String, String>> list, int i) {
        return null;
    }
}
