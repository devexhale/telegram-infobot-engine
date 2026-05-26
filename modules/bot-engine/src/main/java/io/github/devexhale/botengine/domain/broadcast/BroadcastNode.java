package io.github.devexhale.botengine.domain.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.devexhale.botengine.domain.content.ContentNode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record BroadcastNode(
    List<ContentNode> content,
    String message,
    @JsonProperty("return_label") String returnButtonLabel,
    @JsonProperty("start_at") LocalDateTime startAt,
    @JsonProperty("total_sends") Integer totalSends,
    @JsonProperty("interval") Duration interval) {}
