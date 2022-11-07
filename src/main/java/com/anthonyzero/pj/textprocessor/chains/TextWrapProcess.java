package com.anthonyzero.pj.textprocessor.chains;

import com.anthonyzero.pj.textprocessor.models.TextContext;
import com.anthonyzero.pj.textprocessor.models.common.LabelType;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.springframework.util.StringUtils;

import javax.swing.text.html.HTML;

public class TextWrapProcess implements Process{
    private final String ATTR_HASH = "data-hash";
    @Override
    public TextContext doProcess(String text) {
        if (!StringUtils.hasText(text)) return new TextContext(text);
        Document doc = Jsoup.parseBodyFragment(text);
        doc.outputSettings().prettyPrint(false);

        ToWrapNodeVisitor wrapNodeVisitor = new ToWrapNodeVisitor();
        NodeTraversor.traverse(wrapNodeVisitor, doc.body());
        TextContext context = new TextContext(text);
        context.setTarget(doc.body().html());
        return context;
    }

    private class ToWrapNodeVisitor implements NodeVisitor {
        @Override
        public void head(Node node, int depth) {
            if(node instanceof Element) {
                Element element = (Element) node;
                String tagName = element.tagName();
                if(tagName.equals(LabelType.stocksymbol.name())) {
                    return;
                } else if(tagName.equals(HTML.Tag.IMG.toString())) {
                    String src = element.attr("src");
                    if(StringUtils.hasText(src)) {
                        element.attr(ATTR_HASH, bkdrHash(src) + Strings.EMPTY);
                    }
                } else {
                    String text = element.text();
                    if(StringUtils.hasText(text)) {
                        element.attr(ATTR_HASH, bkdrHash(text) + Strings.EMPTY);
                    }
                }
            }
        }

        @Override
        public void tail(Node node, int depth) {
            //<ul data-hash="1350979919">
            //    <li data-hash="1350979919">关于奖金支付：</li>
            //</ul>
            //修改为不同hash 父节点包含tag算进去
        }
    }


    private int bkdrHash(String str) {
        int seed = 131;
        int hash = 0;

        for(int i = 0; i < str.length(); ++i) {
            hash = hash * seed + str.charAt(i);
        }

        return hash & 2147483647;
    }
}
