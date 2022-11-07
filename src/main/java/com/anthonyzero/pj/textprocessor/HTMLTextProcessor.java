package com.anthonyzero.pj.textprocessor;

import com.anthonyzero.pj.textprocessor.chains.*;
import com.anthonyzero.pj.textprocessor.chains.Process;
import com.anthonyzero.pj.textprocessor.models.TextContext;
import com.anthonyzero.pj.textprocessor.models.common.ActionType;
import lombok.Getter;

import java.util.*;

@Getter
public class HTMLTextProcessor {
    final TextContext context;

    public HTMLTextProcessor(TextContext context) {
        this.context = context;
    }

    public static final class Builder{
        String text;
        List<Process> chains;
        public Builder(String text) {
            this.text = text;
            this.chains = new ArrayList<>();
        }
        public HTMLTextProcessor.Builder wash() {
            this.chains.add(new LabelWashProcess());
            return this;
        }
        public HTMLTextProcessor.Builder wrap() {
            this.chains.add(new TextWrapProcess());
            return this;
        }
        public HTMLTextProcessor.Builder extract() {
            this.chains.add(new ContentExtractProcess());
            return this;
        }
        public HTMLTextProcessor.Builder alterImg(Collection<String> hash, String replaceValue) {
            this.chains.add(new ImageAlterProcess(new HashSet<>(hash), replaceValue));
            return this;
        }
        public HTMLTextProcessor.Builder removeImg(Collection<String> hash) {
            this.chains.add(new ImageAlterProcess(new HashSet<>(hash), ActionType.remove));
            return this;
        }
        public HTMLTextProcessor.Builder assemble(String translation) {
            this.chains.add(new TranslationAssembleProcess(translation));
            return this;
        }
        public HTMLTextProcessor.Builder text() {
            this.chains.add(new FullTextProcess());
            return this;
        }
        public HTMLTextProcessor build() {
            TextContext context = null;
            for(Process process: chains){
                if(Objects.isNull(context)) {
                    context = process.doProcess(this.text);
                } else {
                    context = process.doProcess(context.getTarget());
                }
            }
            context.setOrigin(this.text);
            return new HTMLTextProcessor(context);
        }
    }
}
