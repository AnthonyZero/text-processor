package com.anthonyzero.pj.textprocessor;

import com.anthonyzero.pj.textprocessor.impl.RegexSensitiveKeywordsFilter;
import com.anthonyzero.pj.textprocessor.impl.TrieSensitiveKeywordsFilter;
import com.anthonyzero.pj.textprocessor.models.TextContext;
import com.anthonyzero.pj.textprocessor.models.TextWithLabels;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SpringBootTest(classes = {
        PlainTextParser.class,
        SensitiveKeywordsFilter.class,
        TrieSensitiveKeywordsFilter.class,
        RegexSensitiveKeywordsFilter.class
})
class TextApplicationTests {


    @Test
    void regexTests() {
        SensitiveKeywordsFilter filter = new RegexSensitiveKeywordsFilter();
        filter.build(Collections.singleton("^(?:[\\uD83C\\uDF00-\\uD83D\\uDDFF]|[\\uD83E\\uDD00-\\uD83E\\uDDFF]|[\\uD83D\\uDE00-\\uD83D\\uDE4F]|[\\uD83D\\uDE80-\\uD83D\\uDEFF]|[\\u2600-\\u26FF]\\uFE0F?|[\\u2700-\\u27BF]\\uFE0F?|\\u24C2\\uFE0F?|[\\uD83C\\uDDE6-\\uD83C\\uDDFF]{1,2}|[\\uD83C\\uDD70\\uD83C\\uDD71\\uD83C\\uDD7E\\uD83C\\uDD7F\\uD83C\\uDD8E\\uD83C\\uDD91-\\uD83C\\uDD9A]\\uFE0F?|[\\u0023\\u002A\\u0030-\\u0039]\\uFE0F?\\u20E3|[\\u2194-\\u2199\\u21A9-\\u21AA]\\uFE0F?|[\\u2B05-\\u2B07\\u2B1B\\u2B1C\\u2B50\\u2B55]\\uFE0F?|[\\u2934\\u2935]\\uFE0F?|[\\u3030\\u303D]\\uFE0F?|[\\u3297\\u3299]\\uFE0F?|[\\uD83C\\uDE01\\uD83C\\uDE02\\uD83C\\uDE1A\\uD83C\\uDE2F\\uD83C\\uDE32-\\uD83C\\uDE3A\\uD83C\\uDE50\\uD83C\\uDE51]\\uFE0F?|[\\u203C\\u2049]\\uFE0F?|[\\u25AA\\u25AB\\u25B6\\u25C0\\u25FB-\\u25FE]\\uFE0F?|[\\u00A9\\u00AE]\\uFE0F?|[\\u2122\\u2139]\\uFE0F?|\\uD83C\\uDC04\\uFE0F?|\\uD83C\\uDCCF\\uFE0F?|[\\u231A\\u231B\\u2328\\u23CF\\u23E9-\\u23F3\\u23F8-\\u23FA]\\uFE0F?)$"));
        System.out.println("\uD83D\uDE00: ");
        System.out.println(filter.isMatch("\uD83D\uDE00"));
    }

    @Test
    void trieTests(){
        SensitiveKeywordsFilter filter = new TrieSensitiveKeywordsFilter();
    }


    @Test
    void contextLoads() {
        String content = "哈哈'{哈}<stocksymbol symbol='BABA.US'>$阿里巴巴(BABA.US)$</stocksymbol>123445<stocksymbol symbol='FAMI.US'>$农米良品(FAMI.US)$</stocksymbol>FDSF<stocksymbol symbol='00031.HK'>$航天控股(00031.HK)$</stocksymbol>GFDGD<stocksymbol symbol='00700.HK'>$腾讯控股(00700.HK)$</stocksymbol>腾讯控股有限公司是一家主要提供增值服务及网络广告服务的投资控股公司。该公司通过三大分部运营。增值服务分部主要包括互联网及移动平台提供的网络╱手机游戏、社区增值服务及应用。网络广告分部主要包括效果广告及展示广告。其他分部主要包括支付相关服务、云服务及其他服务<at user=\"928660500038946816\" account=\"One-Min_Recap\">@One-Min Recap</at>";
        TextWithLabels twl = PlainTextParser.parse(content);
        System.out.println(twl.getPattern());
        System.out.println(twl.getText());
        System.out.println(twl.getLabels());
    }

    @Test
    void htmlParserTests() {
        StringBuffer sb = new StringBuffer();
        try {
            Resource resource = new ClassPathResource("text.html");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            char[] chars = new char[]{1024};
            while(reader.read(chars) != -1) {
                sb.append(chars);
            }
        } catch (IOException e) {
            System.err.println(e.getStackTrace());
        }
        HTMLTextProcessor parser = new HTMLTextProcessor.Builder(sb.toString())
                .wash()
                .wrap()
                .alterImg(new ArrayList<>(Arrays.asList("1441481267")), "www.baidu.com")
                .extract()
                .build();
        TextContext context = parser.getContext();
        System.out.println(context.getTarget());
        System.out.println(context.getLabels());
    }
}
