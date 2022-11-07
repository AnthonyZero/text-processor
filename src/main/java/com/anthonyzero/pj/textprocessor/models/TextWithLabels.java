package com.anthonyzero.pj.textprocessor.models;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(builderClassName = "Builder")
public class TextWithLabels {
    /**
     * 格式化后文本
     */
    private String text;
    /**
     * 模板文本
     */
    private String pattern;
    /**
     * 标签
     */
    private final List<LabelItem> labels = new ArrayList<>();

    public static TextWithLabels empty() {
        return TextWithLabels.builder()
                .text("")
                .pattern("")
                .build();
    }
}
