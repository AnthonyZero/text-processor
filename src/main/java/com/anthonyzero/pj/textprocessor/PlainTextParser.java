package com.anthonyzero.pj.textprocessor;

import com.anthonyzero.pj.textprocessor.models.LabelItem;
import com.anthonyzero.pj.textprocessor.models.TextWithLabels;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;

public class PlainTextParser {

    private final static String[] defaultLegalTags = new String[]{"stocksymbol", "at", "wm"};

    public final static Set<String> LegalTags = new HashSet<>(Arrays.asList(defaultLegalTags));

    @NonNull
    public static TextWithLabels parse(String content) {
        if (!StringUtils.hasText(content)) return TextWithLabels.empty();
        Document doc = Jsoup.parseBodyFragment(content);
        doc.outputSettings().prettyPrint(false);
        List<String> texts = new ArrayList<>();
        List<LabelItem> tags = new ArrayList<>();
        Map<Node, TextNode> replacement = new HashMap<>();
        Element root = doc.body();
        Set<String> legal = new HashSet<>(LegalTags);
        for (String tag : legal) {
            Elements elements = doc.getElementsByTag(tag);
            for (Element ele : elements) {
                // 过滤嵌套标签
                if (!root.equals(ele.parent())) continue;
                LabelItem item;
                tags.add(item = new LabelItem() {{
                    setName(tag);
                }});
                texts.add(ele.outerHtml());
                replacement.put(ele, TextNode.createFromEncoded("{" + (texts.size() - 1) + "}"));
                Attributes attrs = ele.attributes();
                for (Attribute attr : attrs) {
                    if (StringUtils.hasText(attr.getValue())) {
                        item.getAttributes().put(attr.getKey(), attr.getValue());
                    }
                }
            }
        }
        TextWithLabels.Builder builder = TextWithLabels.builder();
        String formatted;
        if (!texts.isEmpty()) {
            List<Node> nodes = doc.body().childNodes();
            for (Node node : nodes) {
                TextNode changed = replacement.get(node);
                if (changed != null) {
                    node.replaceWith(changed);
                } else {
                    String encoded = node.outerHtml()
                            .replace("'", "''")
                            .replace("{", "'{'");
                    node.replaceWith(TextNode.createFromEncoded(encoded));
                }
            }
            String pattern = Entities.escape(doc.body().html());
            formatted = MessageFormat.format(pattern, texts.toArray());
            TextWithLabels labels = builder.pattern(pattern).text(formatted).build();
            labels.getLabels().addAll(tags);
            return labels;
        } else {
            formatted = Entities.escape(doc.body().html());
            return builder.text(formatted).build();
        }
    }
}
