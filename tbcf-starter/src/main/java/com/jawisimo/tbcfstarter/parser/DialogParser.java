package com.jawisimo.tbcfstarter.parser;

import com.jawisimo.tbcfstarter.model.DialogNode;

import java.io.InputStream;
import java.util.Map;

public interface DialogParser {
    boolean supports(String fileName);
    Map<String, DialogNode> parse(InputStream is);
}
