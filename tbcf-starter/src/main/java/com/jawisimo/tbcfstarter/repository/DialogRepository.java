package com.jawisimo.tbcfstarter.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jawisimo.tbcfstarter.config.BotProperties;
import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.parser.DialogParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DialogRepository {
    private final BotProperties properties;

    private final List<DialogParser> parsers;

    private final Cache<String, Map<String, DialogNode>> dialogCache = Caffeine.newBuilder()
            .maximumSize(1)
            .build();

    public DialogNode getDialogNode(String nodeId) {
       return getCachedDialogMap().get(nodeId);
    }

    private Map<String, DialogNode> getCachedDialogMap() {
        return dialogCache.get(properties.dialogFileName(), fileName -> {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
                if (is == null) {
                    throw new DialogLoadingException("Dialog file not found: " + fileName);
                }

                List<DialogParser> matchingParsers = parsers.stream()
                        .filter(s -> s.supports(fileName))
                        .toList();

                if (matchingParsers.isEmpty()) {
                    throw new DialogLoadingException("No suitable parser found for file: " + fileName);
                }
                if (matchingParsers.size() > 1) {
                    throw new DialogLoadingException(
                            "Multiple parsers found for file: " + fileName +
                                    " -> " + matchingParsers.stream()
                                    .map(p -> p.getClass().getSimpleName())
                                    .toList()
                    );
                }

                DialogParser strategy = matchingParsers.getFirst();

                log.info("Parsing '{}' using {}", fileName, strategy.getClass().getSimpleName());

                Map<String, DialogNode> dialogMap = strategy.parse(is);

                if (!dialogMap.containsKey("start")) {
                    throw new DialogLoadingException("Dialog must contain 'start' node in file: " + fileName);
                }

                return dialogMap;

            } catch (Exception e) {
                throw new DialogLoadingException("Failed to load dialog file: " + fileName, e);
            }
        });
    }

}
