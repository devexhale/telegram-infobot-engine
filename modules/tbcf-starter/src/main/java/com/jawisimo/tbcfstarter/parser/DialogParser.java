package com.jawisimo.tbcfstarter.parser;

import com.jawisimo.tbcfstarter.model.DialogMap;

import java.io.InputStream;

public interface DialogParser {
    boolean supports(String fileName);
    DialogMap parse(InputStream is);
}
