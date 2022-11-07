package com.anthonyzero.pj.textprocessor.chains;

import com.anthonyzero.pj.textprocessor.models.TextContext;
import com.anthonyzero.pj.textprocessor.models.common.LabelType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TranslationAssembleProcess implements Process {

    private final String translation;
    private final Set<String> tags = new HashSet<>(Arrays.asList(
            LabelType.h1.name(),LabelType.h2.name(),LabelType.h3.name(),
            LabelType.h4.name(),LabelType.h5.name(),LabelType.h6.name(),
            LabelType.p.name(),LabelType.ul.name(),LabelType.ol.name(),LabelType.blockquote.name()
    ));

    public TranslationAssembleProcess(String translation) {
        this.translation = translation;
    }

    @Override
    public TextContext doProcess(String text) {
        TextContext context = new TextContext(text);
        if (!StringUtils.hasText(text)) {
            return context;
        }
        if(!StringUtils.hasText(translation)) {
            context.setTarget(text);
            return context;
        }
        Document origin = Jsoup.parseBodyFragment(text);
        Document translateDoc = Jsoup.parseBodyFragment(translation);
        origin.outputSettings().prettyPrint(false);
        translateDoc.outputSettings().prettyPrint(false);

        Elements elements = origin.body().children();
        for(Element element : elements) {
            String tagName = element.tag().getName();
            if(!tags.contains(tagName)) {
                continue;
            }
            String hashValue = element.attr("data-hash");
            if(StringUtils.hasText(hashValue)) {
                Elements targets = translateDoc.body().getElementsByAttributeValue("data-hash", hashValue);
                for(Element target : targets) {
                    if(target.tagName().equals(tagName)) {
                        Node node = element.previousSibling();
                        if(node instanceof TextNode) {
                            TextNode textNode = (TextNode) node;
                            String str = textNode.toString();
                            if("\r\n".equals(str) || "\n".equals(str) || "\r".equals(str)) {
                                TextNode line = new TextNode(str);
//                                Element parent = element.parent();
//                                int index = element.siblingIndex();
//                                parent.insertChildren(index + 1, line);
//                                parent.insertChildren(index + 2, target);
                                element.after(target).after(line);
                            } else {
                                element.after(target);
                            }
                        }else {
                            element.after(target);
                        }
                        break;
                    }
                }
            }
        }
        context.setTarget(origin.body().html());
        return context;
    }
}
