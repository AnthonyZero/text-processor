package com.anthonyzero.pj.textprocessor.models;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class LabelItem {
    /**
     * 标签名称
     */
    private String name;
    /**
     * 属性
     */
    private final Map<String, String> attributes = new HashMap<>();
}
