package swing;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import javax.swing.tree.DefaultMutableTreeNode;

public class ElementMutableTreeNode extends DefaultMutableTreeNode {

    public ElementMutableTreeNode(Element element){
        super(element);
    }

    public String absXpath;

    @Override
    public String toString(){
        return fullElementTag((Element) userObject);
    }

    public String fullElementTag(Element element){
        String outerHtlml = element.outerHtml();
        String endTag = "</"+element.tagName()+">";
        StringBuilder sb = new StringBuilder("<html><body style='width: %1spx'><span color=\"#00ff00\">"+"&lt;"+element.tagName()+"</span>");
        for(Attribute attribute : element.attributes()){
            sb.append("<span color=\"#00ffff\"> "+attribute.getKey()+"</span><span color=\"#808080\">=&quot;</span><span color=\"#ff9900\">"+attribute.getValue()+"</span><span color=\"#808080\">&quot;</span>");
        }
        sb.append("<span color=\"#00ff00\">&gt;</span>");
        if(element.children().size()==0){
            sb.append("<span color=\"#ffffff\">"+element.html()+"</span>");
        }
        else{
            sb.append("...");
        }
        String endTag2 = "<span color=\"#00ff00\">&lt;/"+element.tagName()+"&gt;</span>";
        if(outerHtlml.trim().endsWith(endTag)){
            sb.append(endTag2);
        }
        sb.append("</body></html>");



        return sb.toString();
      //  return "<html><p color=\"#00ff00\">&lt;"+element.tagName()+"</p></html>";
    }

    public String getElementText(){
        Element element = (Element) userObject;
        if(element.children().size()==0){
                  return element.outerHtml();
        }
        String outerHtlml = element.outerHtml();
        int lastChar = outerHtlml.indexOf('>')+1;
        String s = outerHtlml.substring(0,lastChar);
        String endTag = "</"+element.tagName()+">";
        if(outerHtlml.trim().endsWith(endTag)){
            s=s+"..."+endTag;
        }
        return s;
    }
}
