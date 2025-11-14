package com.jawisimo.tbcfstarter.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jawisimo.tbcfstarter.config.BotProperties;
import com.jawisimo.tbcfstarter.loader.DialogLoader;
import com.jawisimo.tbcfstarter.model.DialogMap;
import com.jawisimo.tbcfstarter.model.DialogNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CaffeineDialogRepository implements DialogRepository {
    private final BotProperties properties;
    private final DialogLoader dialogLoader;

    private final Cache<String, DialogMap> dialogCache = Caffeine.newBuilder()
            .maximumSize(1)
            .build();

    @Override
    public DialogNode getDialogNode(String nodeId) {
        return getCachedDialogMap().nodes().get(nodeId);
    }

    private DialogMap getCachedDialogMap() {
        return dialogCache.get(properties.dialogFileName(), dialogLoader::loadDialog);
    }
}
