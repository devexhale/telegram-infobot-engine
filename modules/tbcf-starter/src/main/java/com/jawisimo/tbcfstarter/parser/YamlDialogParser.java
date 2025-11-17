package com.jawisimo.tbcfstarter.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.dialog.node.model.DialogMap;
import com.jawisimo.tbcfstarter.dialog.node.model.DialogNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public class YamlDialogParser implements DialogParser {
    private static final String FORMAT_YAML = ".yaml";
    private static final String FORMAT_YML = ".yml";

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @Override
    public boolean canParse(String fileName) {
        return fileName.endsWith(FORMAT_YAML) || fileName.endsWith(FORMAT_YML);
    }

    @Override
    public DialogMap parse(InputStream is) {
        try {
            TypeReference<Map<String, DialogNode>> typeRef = new TypeReference<>() {
            };
            return new DialogMap(yamlMapper.readValue(is, typeRef));
        } catch (IOException e) {
            throw new DialogLoadingException("Failed to parse YAML dialog", e);
        }
    }
}
