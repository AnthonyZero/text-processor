package com.anthonyzero.pj.textprocessor.chains;

import com.anthonyzero.pj.textprocessor.models.TextContext;
import com.anthonyzero.pj.textprocessor.models.common.ActionType;
import com.anthonyzero.pj.textprocessor.models.common.LabelType;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.Collection;

public class ImageAlterProcess implements Process{

    private final static String ATTR_HASH = "data-hash";
    private final Collection<String> imageHash;
    private final ActionType action;
    private final String replaceValue;

    public ImageAlterProcess(Collection<String> imageHash) {
        this.imageHash = imageHash;
        this.action = ActionType.replace;
        this.replaceValue = Strings.EMPTY;
    }

    public ImageAlterProcess(Collection<String> imageHash, String srcValue) {
        this.imageHash = imageHash;
        this.action = ActionType.replace;
        this.replaceValue = srcValue;
    }

    public ImageAlterProcess(Collection<String> imageHash, ActionType action) {
        this.imageHash = imageHash;
        this.action = action;
        this.replaceValue = Strings.EMPTY;
    }

    @Override
    public TextContext doProcess(String text) {
        if (!StringUtils.hasText(text)) return new TextContext(text);
        Document doc = Jsoup.parseBodyFragment(text);
        doc.outputSettings().prettyPrint(false);

        Element root = doc.body();
        Elements elements = root.getElementsByTag(LabelType.img.name());
        for(Element element : elements) {
            String attrHash = element.attr(ATTR_HASH);
            if(!StringUtils.hasText(attrHash) || !imageHash.contains(attrHash)) continue;
            if(ActionType.replace == action) {
                element.attr("src", this.replaceValue);
            } else if(ActionType.remove == action) {
                element.remove();
            }
        }
        TextContext context = new TextContext(text);
        context.setTarget(root.html());
        return context;
    }
}
