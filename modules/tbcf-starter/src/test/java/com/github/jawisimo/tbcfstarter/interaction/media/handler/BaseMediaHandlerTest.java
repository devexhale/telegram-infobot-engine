package com.github.jawisimo.tbcfstarter.interaction.media.handler;

import static org.mockito.Mockito.lenient;

import com.github.jawisimo.tbcfstarter.interaction.node.model.ContentNode;
import com.github.jawisimo.tbcfstarter.loader.MediaFileLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
abstract class BaseMediaHandlerTest<T extends MediaHandler> {

  protected static final String CHAT_ID = "123456789";
  protected static final String FILE_NAME = "test-file.mp3";
  protected static final String CAPTION = "Test Caption";
  protected static final InputFile MEDIA_FILE = new InputFile("testFile");
  protected static final String EXCEPTION_MSG = "Something went wrong...";

  @Mock protected TelegramClient telegramClient;
  @Mock protected MediaFileLoader mediaFileLoader;

  protected T handler;

  protected abstract T createHandler(TelegramClient client, MediaFileLoader mediaFileLoader);

  protected abstract ContentNode createContentNodeWithMedia(String caption);

  @BeforeEach
  void setUp() {
    lenient().when(mediaFileLoader.load(FILE_NAME)).thenReturn(MEDIA_FILE);
    handler = createHandler(telegramClient, mediaFileLoader);
  }
}
