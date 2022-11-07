package com.anthonyzero.pj.textprocessor.configs;

import com.anthonyzero.pj.textprocessor.impl.RegexSensitiveKeywordsFilter;
import com.anthonyzero.pj.textprocessor.SensitiveKeywordsFilter;
import com.anthonyzero.pj.textprocessor.impl.TrieSensitiveKeywordsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

@Configuration
public class SensitiveKeywordsConfig {

    @Lazy
    @Primary
    @Bean(destroyMethod = "clear")
    public SensitiveKeywordsFilter trieSKFilter() {
        return new TrieSensitiveKeywordsFilter();
    }

    @Lazy
    @Bean(destroyMethod = "clear")
    public SensitiveKeywordsFilter regexSKFilter() {
        return new RegexSensitiveKeywordsFilter();
    }
}
