package io.github.devexhale.botengine.domain.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.devexhale.botengine.domain.content.ContentNode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a broadcast node in the broadcast definition.
 *
 * @param content the list of content elements to be sent
 * @param message the main message text of the broadcast
 * @param returnButtonLabel the label for the button returning to the main dialog
 * @param startAt the scheduled start time for the broadcast
 * @param totalSends the total number of times the broadcast should be sent
 * @param interval the time interval between consecutive sends
 * @since 1.0
 */
public record BroadcastNode(
    List<ContentNode> content,
    String message,
    @JsonProperty("return_label") String returnButtonLabel,
    @JsonProperty("start_at") LocalDateTime startAt,
    @JsonProperty("total_sends") Integer totalSends,
    @JsonProperty("interval") Duration interval) {}
