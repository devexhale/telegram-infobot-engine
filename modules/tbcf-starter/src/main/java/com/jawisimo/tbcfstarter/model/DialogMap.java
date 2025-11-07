package com.jawisimo.tbcfstarter.model;

import java.util.Map;

public record DialogMap(Map<String, DialogNode> nodes) {

    public boolean containsNodeKey(String key) {
        return nodes.containsKey(key);
    }
}
