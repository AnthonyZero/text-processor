package com.anthonyzero.pj.textprocessor.chains;

import com.anthonyzero.pj.textprocessor.models.TextContext;

public interface Process {

    TextContext doProcess(String text);
}
