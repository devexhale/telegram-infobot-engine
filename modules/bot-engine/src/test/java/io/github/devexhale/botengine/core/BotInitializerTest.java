// package io.github.jawisimo.botengine.core;
//
// import static org.junit.jupiter.api.Assertions.assertSame;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
//
// import ch.qos.logback.classic.Level;
// import ch.qos.logback.classic.Logger;
// import ch.qos.logback.classic.spi.ILoggingEvent;
// import ch.qos.logback.core.read.ListAppender;
// import core.bot.io.github.devexhale.botengine.BotInitializer;
// import properties.io.github.devexhale.botengine.BotProperties;
// import command.common.execution.io.github.devexhale.botengine.CommandsInitializer;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.slf4j.LoggerFactory;
//
// @ExtendWith(MockitoExtension.class)
// class BotInitializerTest {
//
//  private static final String BOT_NAME = "MyBot";
//  private static final String READY_LOG = "Telegram bot";
//
//  @Mock private CommandsInitializer commandsInitializer;
//  @Mock private BotProperties properties;
//
//  private BotInitializer botInitializer;
//
//  @BeforeEach
//  void init() {
//    botInitializer = new BotInitializer(commandsInitializer, properties);
//  }
//
//  @Test
//  void onApplicationReady_shouldInitializeCommandsAndLogReadyMessage() {
//    ListAppender<ILoggingEvent> appender = getListAppender();
//    when(properties.name()).thenReturn(BOT_NAME);
//
//    botInitializer.onApplicationReady();
//
//    verify(commandsInitializer).setUpCommands();
//
//    ILoggingEvent event = appender.list.getFirst();
//    assertSame(Level.INFO, event.getLevel());
//    assertTrue(event.getFormattedMessage().contains(READY_LOG));
//    assertTrue(event.getFormattedMessage().contains(BOT_NAME));
//    assertTrue(event.getFormattedMessage().contains("initialized and ready"));
//  }
//
//  private ListAppender<ILoggingEvent> getListAppender() {
//    Logger logger = (Logger) LoggerFactory.getLogger(BotInitializer.class);
//    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
//    listAppender.start();
//    logger.addAppender(listAppender);
//    return listAppender;
//  }
// }
