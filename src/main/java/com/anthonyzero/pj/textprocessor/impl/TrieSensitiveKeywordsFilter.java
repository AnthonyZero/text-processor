package com.anthonyzero.pj.textprocessor.impl;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import com.anthonyzero.pj.textprocessor.SensitiveKeywordsFilter;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrieSensitiveKeywordsFilter implements SensitiveKeywordsFilter {

    private static final Pattern pattern = Pattern.compile("[a-z1-9]+", Pattern.CASE_INSENSITIVE);
    private static final CountDownLatch initLock = new CountDownLatch(1);
    private final AtomicReference<AhoCorasickDoubleArrayTrie<String>> prefix = new AtomicReference<>();
    private final AtomicReference<AhoCorasickDoubleArrayTrie<String>> exact = new AtomicReference<>();

    @Override
    public boolean isMatch(String text) {
        try {
            initLock.await();
            if (!StringUtils.hasText(text))
                return true;
            String txt = text.toLowerCase(Locale.ROOT).trim();
            if (!exactMatch(txt)) {
                return prefix.get() != null && prefix.get().matches(txt);
            }
            return true;
        } catch (InterruptedException ignored) {
            return false;
        }
    }

    @Override
    public synchronized void build(Collection<String> keywords) {
        if (keywords == null) return;
        AhoCorasickDoubleArrayTrie<String> localPrefix = new AhoCorasickDoubleArrayTrie<>();
        AhoCorasickDoubleArrayTrie<String> localExact = new AhoCorasickDoubleArrayTrie<>();
        Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Map<String, String> words = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String key : keywords) {
            if (!StringUtils.hasText(key)) continue;
            String _key = key.toLowerCase(Locale.ROOT).trim();
            // 单独匹配英文单词，避免命中词缀
            if (Pattern.matches("^[a-z]+$", _key)) {
                words.put(_key, key);
            } else {
                map.put(_key, key);
            }
        }
        if (!map.isEmpty()) {
            localPrefix.build(map);
            prefix.set(localPrefix);
        } else {
            prefix.set(null);
        }
        if (!words.isEmpty()) {
            localExact.build(words);
            exact.set(localExact);
        } else {
            exact.set(null);
        }
        if (initLock.getCount() > 0) {
            initLock.countDown();
        }
    }

    @Override
    public void clear() {
        prefix.set(null);
        exact.set(null);
    }

    @Override
    public void walking(String text, HitText processor) {
        AtomicInteger count = new AtomicInteger(0);
        if (exact.get() != null) {
            AhoCorasickDoubleArrayTrie<String> index = exact.get();
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String word = matcher.group(0);
                if (index.exactMatchSearch(word) >= 0) {
                    processor.hit(matcher.regionStart(), matcher.regionEnd(), word, count.getAndIncrement());
                }
            }
        }
        if (prefix.get() != null) {
            prefix.get().parseText(text, (begin, end, value) -> {
                return processor.hit(begin, end, value, count.getAndIncrement());
            });
        }
    }

    private boolean exactMatch(String text) {
        AhoCorasickDoubleArrayTrie<String> index = exact.get();
        if (index == null) return false;
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String word = matcher.group(0);
            if (index.exactMatchSearch(word) >= 0) {
                return true;
            }
        }
        return false;
    }
}
