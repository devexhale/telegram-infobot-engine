package com.github.jawisimo.tbcfstarter.parser;

import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogMap;

import java.io.InputStream;

public interface DialogParser {

  boolean canParse(String fileName);

  DialogMap parse(InputStream is);
}
