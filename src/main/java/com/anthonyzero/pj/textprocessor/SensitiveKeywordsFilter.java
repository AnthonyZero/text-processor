package com.anthonyzero.pj.textprocessor;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public interface SensitiveKeywordsFilter {

    /**
     * 是否命中关键词
     *
     * @param text 样本文本
     * @return true表示命中；否则表示不命中
     */
    default boolean isMatch(String text) {
        if (!StringUtils.hasText(text)) return false;
        AtomicBoolean state = new AtomicBoolean(false);
        String txt = text.toLowerCase(Locale.ROOT).trim();
        walking(txt, (begin, end, val, idx) -> state.getAndSet(true));
        return state.get();
    }

    /**
     * 构建关键词索引
     *
     * @param keywords 关键词列表
     */
    void build(Collection<String> keywords);

    /**
     * 清空索引
     */
    void clear();

    /**
     * 遍历文本
     *
     * @param text      文本样本
     * @param processor 处理器
     */
    void walking(String text, HitText processor);

    /**
     * 找出所有命中关键词
     *
     * @param text 文本样本
     * @return 关键词列表
     */
    default List<String> findAll(String text) {
        List<String> all = new ArrayList<>();
        if (!StringUtils.hasText(text)) return all;
        walking(text, (begin, end, val, idx) -> {
            all.add(val);
            return true;
        });
        return all;
    }

    interface HitText {
        /**
         * 命中文本
         *
         * @param begin 字符开始位置
         * @param end   字符结束位置
         * @param value 命中词
         * @param index 命中索引
         * @return 是否继续匹配，true表示继续；否则表示中断
         */
        boolean hit(int begin, int end, String value, int index);
    }
}
