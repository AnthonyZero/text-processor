package com.anthonyzero.pj.textprocessor.chains;

import com.anthonyzero.pj.textprocessor.models.TextContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.StringUtils;

/**
 * 获取全部标签下的所有文本
 */
public class FullTextProcess implements Process {
    
    @Override
    public TextContext doProcess(String text) {
        if (!StringUtils.hasText(text)) return new TextContext(text);
        Document doc = Jsoup.parseBodyFragment(text);
        doc.outputSettings().prettyPrint(false);

        TextContext context = new TextContext(text);
        context.setTarget(doc.body().text());
        return context;
    }
}
