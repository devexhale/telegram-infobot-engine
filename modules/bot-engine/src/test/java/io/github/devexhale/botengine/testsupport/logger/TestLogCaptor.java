package io.github.devexhale.botengine.testsupport.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.slf4j.LoggerFactory;

public final class TestLogCaptor implements AutoCloseable {

  private final Logger logger;
  private final ListAppender<ILoggingEvent> appender;

  public TestLogCaptor(Class<?> clazz) {
    logger = (Logger) LoggerFactory.getLogger(clazz);
    appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
  }

  public List<ILoggingEvent> events() {
    return List.copyOf(appender.list);
  }

  @Override
  public void close() {
    logger.detachAppender(appender);
    appender.stop();
  }
}
