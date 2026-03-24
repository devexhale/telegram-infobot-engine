package io.github.jawisimo.botsengine.model;

import java.time.LocalDateTime;
import java.util.List;

public record BroadcastNode(
    String id,
    List<ContentNode> content,
    String message,
    Button back,
    LocalDateTime startTime,
    int sendCount,
    int repeatInterval) {}
