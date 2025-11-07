package com.jawisimo.tbcfstarter.model;

import java.util.Map;

public record DialogMap(Map<String, DialogNode> nodes) {

    public DialogMap {
        nodes = nodes == null ? Map.of() : Map.copyOf(nodes);
    }

    public boolean containsKey(String key) {
        return nodes.containsKey(key);
    }
}
