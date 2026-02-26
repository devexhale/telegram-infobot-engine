package com.github.jawisimo.botengine.parser;

import com.github.jawisimo.botengine.interaction.node.model.DialogMap;

import java.io.InputStream;

public interface DialogParser {

  boolean canParse(String fileName);

  DialogMap parse(InputStream is);
}
