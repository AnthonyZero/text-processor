package com.anthonyzero.pj.textprocessor.impl;

import com.anthonyzero.pj.textprocessor.SensitiveKeywordsFilter;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSensitiveKeywordsFilter implements SensitiveKeywordsFilter {

    private final AtomicReference<List<Pattern>> cache = new AtomicReference<>();

    @Override
    public synchronized void build(@NonNull Collection<String> keywords) {
        Map<String, Pattern> local = new HashMap<>();
        for (String keyword : keywords) {
            local.put(keyword, Pattern.compile(keyword));
        }
        cache.set(new ArrayList<>(local.values()));
    }

    @Override
    public void clear() {
        List<Pattern> last = cache.getAndSet(null);
        if (last != null) {
            last.clear();
        }
    }

    @Override
    public void walking(String text, HitText processor) {
        List<Pattern> patterns = cache.get();
        if (patterns != null) {
            AtomicInteger counter = new AtomicInteger(0);
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    String word = matcher.group(0);
                    processor.hit(matcher.regionStart(), matcher.regionEnd(), word, counter.getAndIncrement());
                }
            }
        }
    }
}
