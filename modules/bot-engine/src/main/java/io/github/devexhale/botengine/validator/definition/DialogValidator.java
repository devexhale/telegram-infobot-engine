package io.github.devexhale.botengine.validator.definition;

import io.github.devexhale.botengine.execution.common.command.commandset.StartCommand;
import io.github.devexhale.botengine.domain.dialog.Button;
import io.github.devexhale.botengine.domain.dialog.ButtonType;
import io.github.devexhale.botengine.domain.dialog.DialogMap;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Performs comprehensive validation of a {@link DialogMap}.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class DialogValidator extends AbstractDefinitionValidator<DialogMap> {

  private final ContentValidator contentValidator;

  @Override
  public Class<DialogMap> targetType() {
    return DialogMap.class;
  }

  @Override
  public void validate(DialogMap dialogMap, String fileName) {
    ValidationContext context = new ValidationContext();

    validateDialogMap(dialogMap, context);

    if (dialogMap != null) {
      dialogMap.nodes().forEach((nodeKey, node) -> validateNode(nodeKey, node, dialogMap, context));
    }

    logWarnings(
        context,
        "Dialog map validation completed with %d warnings for file '%s':"
            .formatted(context.warningCount(), fileName));

    throwIfErrors(
        context,
        "Dialog map loading failed with %d errors for file '%s':"
            .formatted(context.errorCount(), fileName));
  }

  private void validateDialogMap(DialogMap dialogMap, ValidationContext context) {
    if (dialogMap == null) {
      context.addError("Dialog map is null");
      return;
    }

    if (dialogMap.nodes().isEmpty()) {
      context.addError("Dialog map is empty");
    }

    validateStartNode(dialogMap, context);
  }

  private void validateStartNode(DialogMap dialogMap, ValidationContext context) {
    if (!dialogMap.nodes().containsKey(StartCommand.COMMAND_NAME)) {
      context.addError("Dialog map must contain node '%s'".formatted(StartCommand.COMMAND_NAME));
    }
  }

  private void validateNode(
      String nodeKey, DialogNode node, DialogMap dialogMap, ValidationContext context) {
    if (node == null) {
      context.addError("%s is empty".formatted(path(nodeKey)));
      return;
    }

    contentValidator.validate(node.content(), nodeKey + ".content", context);
    validateMessage(node, nodeKey, context);
    validateButtonType(node, nodeKey, context);
    validateButtons(node, nodeKey, dialogMap, context);
  }

  private void validateMessage(DialogNode node, String nodeKey, ValidationContext context) {
    if (isInvalidString(node.message())) {
      context.addError("%s is missing or blank".formatted(path(nodeKey + ".message")));
    }
  }

  private void validateButtonType(DialogNode node, String nodeKey, ValidationContext context) {
    if (node.buttonType() == ButtonType.UNKNOWN) {
      context.addError("%s is not supported".formatted(path(nodeKey + ".button_type")));
    }
  }

  private void validateButtons(
      DialogNode node, String nodeKey, DialogMap dialogMap, ValidationContext context) {
    if (node.buttons() == null || node.buttons().isEmpty()) {
      context.addError("%s is missing or empty".formatted(path(nodeKey + ".buttons")));
      return;
    }

    validateDuplicateButtonLabels(node.buttons(), nodeKey, context);

    for (int i = 0; i < node.buttons().size(); i++) {
      Button button = node.buttons().get(i);
      String buttonPath = nodeKey + ".buttons[" + i + "]";

      validateButton(button, node.buttonType(), buttonPath, context);
      validateMissingNextNode(button, dialogMap, buttonPath, context);
    }
  }

  private void validateButton(
      Button button, ButtonType buttonType, String buttonPath, ValidationContext context) {
    if (button == null) {
      context.addError("%s is empty".formatted(path(buttonPath)));
      return;
    }

    if (isInvalidString(button.label())) {
      context.addError("%s is missing or blank".formatted(path(buttonPath + ".label")));
    }

    boolean hasUrl = !isInvalidString(button.url());
    boolean hasNext = !isInvalidString(button.next());

    if (buttonType == ButtonType.REPLY) {
      if (hasUrl) {
        context.addError(
            "%s must not be present for reply button".formatted(path(buttonPath + ".url")));
      }
      if (!hasNext) {
        context.addError(
            "%s is missing or blank for reply button".formatted(path(buttonPath + ".next")));
      }
      return;
    }

    if (buttonType == null || buttonType == ButtonType.INLINE) {
      if (!hasUrl && !hasNext) {
        context.addError(
            "%s must contain either 'next' or 'url' for inline button".formatted(path(buttonPath)));
      }

      if (hasUrl && hasNext) {
        context.addError(
            "%s cannot contain both 'next' and 'url' for inline button"
                .formatted(path(buttonPath)));
      }
    }
  }

  private void validateDuplicateButtonLabels(
      List<Button> buttons, String nodeKey, ValidationContext context) {
    Set<String> labels = new HashSet<>();

    for (Button button : buttons) {
      if (button == null || isInvalidString(button.label())) {
        continue;
      }

      String normalizedLabel = button.label().trim();

      if (!labels.add(normalizedLabel)) {
        context.addWarning(
            "%s contains duplicate button label '%s'"
                .formatted(path(nodeKey + ".buttons"), normalizedLabel));
      }
    }
  }

  private void validateMissingNextNode(
      Button button, DialogMap dialogMap, String buttonPath, ValidationContext context) {
    if (button != null
        && !isInvalidString(button.next())
        && !dialogMap.nodes().containsKey(button.next())) {
      context.addWarning(
          "%s points to missing node '%s'".formatted(path(buttonPath + ".next"), button.next()));
    }
  }
}
