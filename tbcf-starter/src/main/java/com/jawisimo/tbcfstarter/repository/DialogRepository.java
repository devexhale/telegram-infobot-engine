package com.jawisimo.tbcfstarter.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jawisimo.tbcfstarter.config.BotProperties;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.support.DialogLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DialogRepository {
    private final BotProperties properties;
    private final DialogLoader dialogLoader;

    private final Cache<String, Map<String, DialogNode>> dialogCache = Caffeine.newBuilder()
            .maximumSize(1)
            .build();

    public DialogNode getDialogNode(String nodeId) {
       return getCachedDialogMap().get(nodeId);
    }

    private Map<String, DialogNode> getCachedDialogMap() {
        return dialogCache.get(properties.dialogFileName(), dialogLoader::load);
    }

}
