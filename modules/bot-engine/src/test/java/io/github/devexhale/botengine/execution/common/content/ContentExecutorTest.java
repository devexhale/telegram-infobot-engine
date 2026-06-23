package io.github.devexhale.botengine.execution.common.content;

import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.domain.content.ContentType;
import io.github.devexhale.botengine.execution.common.content.handler.ContentHandler;
import io.github.devexhale.botengine.execution.common.content.handler.ContentHandlerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentExecutorTest {

  private static final String CHAT_ID = "123456789";

  @Mock private ContentHandlerRegistry registry;
  @Mock private ContentHandler textHandler;
  @Mock private ContentHandler audioHandler;
  @Mock private Message textMessage;
  @Mock private Message audioMessage;

  @InjectMocks private ContentExecutor contentExecutor;

  private ContentNode textNode;
  private ContentNode audioNode;

  @BeforeEach
  void setUp() {
    textNode = new ContentNode(ContentType.TEXT, "Hello", null, null);
    audioNode = new ContentNode(ContentType.AUDIO, null, "test.mp3", "Caption");
  }

  @Test
  void execute_shouldReturnEmptyList_whenContentIsNull() {
    List<Message> result = contentExecutor.execute(null, CHAT_ID);
    assertIterableEquals(List.of(), result);
  }

  @Test
  void execute_shouldReturnEmptyList_whenContentIsEmpty() {
    List<Message> result = contentExecutor.execute(List.of(), CHAT_ID);
    assertIterableEquals(List.of(), result);
  }

  @Test
  void execute_shouldExecuteHandlersAndReturnMessages_whenContentIsValid() {
    List<ContentNode> contentList = List.of(textNode, audioNode);

    when(registry.get(ContentType.TEXT)).thenReturn(Optional.of(textHandler));
    when(registry.get(ContentType.AUDIO)).thenReturn(Optional.of(audioHandler));
    when(textHandler.handle(textNode, CHAT_ID)).thenReturn(textMessage);
    when(audioHandler.handle(audioNode, CHAT_ID)).thenReturn(audioMessage);

    List<Message> result = contentExecutor.execute(contentList, CHAT_ID);

    assertIterableEquals(List.of(textMessage, audioMessage), result);
  }
}
