package com.anthonyzero.pj.textprocessor.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TextContext {
    //解析前
    private String origin;
    //解析后
    private String target;
    //标签列表
    private List<LabelItem> labels = new ArrayList<>();

    public TextContext(String origin) {
        this.origin = origin;
    }
}
