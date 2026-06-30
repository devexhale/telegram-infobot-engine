package io.github.devexhale.botengine.validator.definition;

import static io.github.devexhale.botengine.execution.broadcast.BroadcastDefaults.DEFAULT_TOTAL_SENDS;

import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import io.github.devexhale.botengine.domain.broadcast.BroadcastMap;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Performs comprehensive validation of a {@link BroadcastMap}.
 *
 * @since 1.0
 */
@Component
@ConditionalOnBroadcastEnabled
@RequiredArgsConstructor
public class BroadcastValidator extends AbstractDefinitionValidator<BroadcastMap> {

  private final ContentValidator contentValidator;

  @Override
  public Class<BroadcastMap> targetType() {
    return BroadcastMap.class;
  }

  @Override
  public void validate(BroadcastMap broadcastMap, String fileName) {
    ValidationContext context = new ValidationContext();

    validateBroadcastMap(broadcastMap, context);

    if (broadcastMap != null) {
      broadcastMap.nodes().forEach((nodeKey, node) -> validateNode(nodeKey, node, context));
    }

    logWarnings(
        context,
        "Broadcast map validation completed with %d warnings for file '%s':"
            .formatted(context.warningCount(), fileName));

    throwIfErrors(
        context,
        "Broadcast map loading failed with %d errors for file '%s':"
            .formatted(context.errorCount(), fileName));
  }

  private void validateBroadcastMap(BroadcastMap broadcastMap, ValidationContext context) {
    if (broadcastMap == null) {
      context.addError("Broadcast map is null");
      return;
    }

    if (broadcastMap.nodes().isEmpty()) {
      context.addError("Broadcast map is empty");
    }
  }

  private void validateNode(String nodeKey, BroadcastNode node, ValidationContext context) {
    if (node == null) {
      context.addError("%s is empty".formatted(path(nodeKey)));
      return;
    }

    contentValidator.validate(node.content(), nodeKey + ".content", context);
    validateMessage(node, nodeKey, context);
    validateReturnButtonLabel(node, nodeKey, context);
    validateStartAt(node, nodeKey, context);
    validateTotalSends(node, nodeKey, context);
    validateInterval(node, nodeKey, context);
  }

  private void validateMessage(BroadcastNode node, String nodeKey, ValidationContext context) {
    if (isInvalidString(node.message())) {
      context.addError(formatMissingError(nodeKey + ".message"));
    }
  }

  private void validateReturnButtonLabel(
      BroadcastNode node, String nodeKey, ValidationContext context) {
    if (isInvalidString(node.returnButtonLabel())) {
      context.addError(formatMissingError(nodeKey + ".return_label"));
    }
  }

  private void validateStartAt(BroadcastNode node, String nodeKey, ValidationContext context) {
    if (node.startAt() == null) {
      context.addError(formatMissingError(nodeKey + ".start_at"));
    }
  }

  private void validateTotalSends(BroadcastNode node, String nodeKey, ValidationContext context) {
    if (node.totalSends() != null && node.totalSends() <= 0) {
      context.addError(formatPositiveValueError(nodeKey + ".total_sends"));
    }
  }

  private void validateInterval(BroadcastNode node, String nodeKey, ValidationContext context) {
    if (node.totalSends() != null
        && node.totalSends() != DEFAULT_TOTAL_SENDS
        && node.interval() == null) {
      context.addError(formatMissingError(nodeKey + ".interval"));
      return;
    }

    if ((node.interval() != null) && (node.interval().isNegative() || node.interval().isZero())) {
      context.addError(formatPositiveValueError(nodeKey + ".interval"));
    }
  }

  private String formatMissingError(String fieldPath) {
    return "%s is missing or blank".formatted(path(fieldPath));
  }

  private String formatPositiveValueError(String fieldPath) {
    return "%s must be greater than 0".formatted(path(fieldPath));
  }
}
