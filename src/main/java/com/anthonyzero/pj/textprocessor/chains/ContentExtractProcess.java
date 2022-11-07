package com.anthonyzero.pj.textprocessor.chains;

import com.anthonyzero.pj.textprocessor.models.LabelItem;
import com.anthonyzero.pj.textprocessor.models.TextContext;
import com.anthonyzero.pj.textprocessor.models.common.LabelType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.*;

public class ContentExtractProcess implements Process {
    private final static String[] targetTags = new String[]{
            LabelType.stocksymbol.name(),
            LabelType.img.name(),
            LabelType.at.name(),
            LabelType.wm.name(),
    };

    @Override
    public TextContext doProcess(String text) {
        if (!StringUtils.hasText(text)) return new TextContext(text);
        Document doc = Jsoup.parseBodyFragment(text);
        doc.outputSettings().prettyPrint(false);

        Element root = doc.body();
        List<LabelItem> labels = new ArrayList<>();
        for (String tag : targetTags) {
            Elements elements = root.getElementsByTag(tag);
            if(elements.size() == 0) continue;
            for(Element element : elements) {
                LabelItem labelItem = new LabelItem(){{
                    setName(tag);
                }};
                Attributes attrs = element.attributes();
                for (Attribute attr : attrs) {
                    if (StringUtils.hasText(attr.getValue())) {
                        labelItem.getAttributes().put(attr.getKey(), attr.getValue());
                    }
                }
                labels.add(labelItem);
            }
        }
        TextContext context = new TextContext(text);
        context.setTarget(root.html());
        context.setLabels(labels);
        return context;
    }
}
