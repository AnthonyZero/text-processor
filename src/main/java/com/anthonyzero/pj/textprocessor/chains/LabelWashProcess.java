package com.anthonyzero.pj.textprocessor.chains;

import com.anthonyzero.pj.textprocessor.models.TextContext;
import com.anthonyzero.pj.textprocessor.models.common.LabelType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.springframework.util.StringUtils;

import javax.swing.text.html.HTML;
import java.util.*;
import java.util.stream.Collectors;

public class LabelWashProcess implements Process {

    private final String ATTR_KEY = "data-tag";
    private final String ATTR_VALUE_UNSUPPORT = "unsupport";
    private final String ATTR_VALUE_MUTEX = "mutex";

    private final Set<String> mutex = new HashSet<>(Arrays.asList(  //互斥 父子互斥情况
            LabelType.h1.name(),LabelType.h2.name(),LabelType.h3.name(),
            LabelType.h4.name(),LabelType.h5.name(),LabelType.h6.name(),
            LabelType.p.name(),LabelType.ul.name(),LabelType.ol.name()
    ));
    private final Set<String> lables = Arrays.stream(LabelType.values())
            .map(Enum::name).collect(Collectors.toSet());

    @Override
    public TextContext doProcess(String text) {
        if (!StringUtils.hasText(text)) return new TextContext(text);
        Document doc = Jsoup.parseBodyFragment(text);
        doc.outputSettings().prettyPrint(false);

        Elements elements = doc.body().children();
        for(Element element : elements) {
            ToMarkNodeVisitor toMarkNodeVisitor = new ToMarkNodeVisitor(element);
            NodeTraversor.traverse(toMarkNodeVisitor, element);
        }

        elements = doc.body().getElementsByAttribute(ATTR_KEY);
        for(Element element : elements) {
            if(HTML.Tag.SCRIPT.toString().equals(element.tagName())) {
                element.remove();
            }else {
                List<Node> nodes = element.childNodes();
                if(nodes.size() == 0) {
                    element.remove();
                } else if(nodes.size() == 1 && nodes.get(0) instanceof TextNode) {
                    TextNode textNode = (TextNode) nodes.get(0);
                    element.replaceWith(new TextNode(textNode.text()));
                } else {
                    Element parent = element.parent();
                    int index = element.siblingIndex();
                    parent.insertChildren(index, nodes);
                    element.remove(); //nodes已经不存在
                }
            }
        }
        TextContext context = new TextContext(text);
        context.setTarget(doc.body().html());
        return context;
    }
    //mark标记移除的node
    private class ToMarkNodeVisitor implements NodeVisitor {
        private Element root;

        public ToMarkNodeVisitor(Element root) {
            this.root = root;
        }

        //script (数据)type = dataNode
        @Override
        public void head(Node node, int depth) {
            if(node == root) {
                Element element = (Element) node;
                if(!lables.contains(element.tagName())) {
                    element.attr(ATTR_KEY, ATTR_VALUE_UNSUPPORT);
                }
            } else {
                if(node instanceof Element) {
                    Element element = (Element) node;
                    if(!lables.contains(element.tagName())) {
                        element.attr(ATTR_KEY, ATTR_VALUE_UNSUPPORT);
                    } else if(mutex.contains(element.tagName()) &&
                            mutex.contains(element.parent().tagName())) {
                        element.attr(ATTR_KEY, ATTR_VALUE_MUTEX);
                    }
                }
            }
        }

        @Override
        public void tail(Node node, int depth) {

        }
    }
}
