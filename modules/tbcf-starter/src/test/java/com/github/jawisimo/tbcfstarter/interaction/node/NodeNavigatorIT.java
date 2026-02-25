package com.github.jawisimo.tbcfstarter.interaction.node;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.jawisimo.tbcfstarter.config.BotProperties;
import com.github.jawisimo.tbcfstarter.interaction.node.model.Button;
import com.github.jawisimo.tbcfstarter.interaction.node.model.ButtonType;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogNode;
import com.github.jawisimo.tbcfstarter.repository.CaffeineDialogRepository;
import com.github.jawisimo.tbcfstarter.repository.DialogRepository;
import com.github.jawisimo.tbcfstarter.service.UserStateService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = {NodeNavigator.class, CaffeineDialogRepository.class})
@Import(NodeNavigatorIT.TestConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"telegram.bot.user-state-persistent=false"})
class NodeNavigatorIT {

  private static final String CHAT_ID = "123456789";
  private static final String START_NODE = "/start";
  private static final String HISTORY_Q1 = "history_q1";
  private static final String SCIENCE_Q1 = "science_q1";
  private static final String GEOGRAPHY_Q1 = "geography_q1";
  private static final String CINEMA_Q1 = "cinema_q1";
  private static final String GITHUB_URL = "https://github.com/jawisimo/tg-bot-config-framework";

  private static final String BUTTON_LABEL_HISTORY = "🏛️ Історія";
  private static final String BUTTON_LABEL_SCIENCE = "🔬 Наука";
  private static final String BUTTON_LABEL_GEOGRAPHY = "🌎 Географія";
  private static final String BUTTON_LABEL_CINEMA = "🎬 Кіно";
  private static final String BUTTON_LABEL_GITHUB = "🔗 GitHub";

  private static final String USER_INPUT = "some input";
  private static final String NON_EXISTENT_NODE = "non-existent-node";

  @Autowired private NodeNavigator nodeNavigator;
  @Autowired private DialogRepository dialogRepository;

  @MockitoBean private UserStateService userStateService;
  @MockitoBean private NodeExecutor nodeExecutor;

  @BeforeEach
  void setUp() {
    doReturn(START_NODE).when(userStateService).getUserStateOrDefault(eq(CHAT_ID), anyString());
  }

  @Test
  void navigateToNode_shouldExecuteNodeAndReturnTrue_whenNodeExists() {
    boolean result = nodeNavigator.navigateToNode(CHAT_ID, START_NODE);

    assertTrue(result);
    verify(nodeExecutor).execute(any(DialogNode.class), eq(CHAT_ID));
  }

  @Test
  void navigateToNode_shouldReturnFalse_whenNodeNotFound() {
    boolean result = nodeNavigator.navigateToNode(CHAT_ID, NON_EXISTENT_NODE);

    assertFalse(result);
    verify(nodeExecutor, never()).execute(any(), anyString());
  }

  @Test
  void navigateToNode_shouldReturnFalse_whenNodeKeyIsNull() {
    boolean result = nodeNavigator.navigateToNode(CHAT_ID, null);

    assertFalse(result);
    verify(nodeExecutor, never()).execute(any(), anyString());
  }

  @Test
  void getNextNodeKey_shouldReturnUserInput_whenCurrentNodeHasInlineButtons() {
    DialogNode startNode = dialogRepository.getNode(START_NODE);

    assertNotNull(startNode);
    assertEquals(ButtonType.INLINE, startNode.buttonType());

    String result = nodeNavigator.getNextNodeKey(CHAT_ID, USER_INPUT);

    assertEquals(USER_INPUT, result);
  }

  @Test
  void startNode_shouldHaveCorrectStructure_whenLoadedFromTestDialog() {
    DialogNode startNode = dialogRepository.getNode(START_NODE);
    List<Button> buttons = startNode.buttons();

    assertNotNull(startNode);
    assertEquals("Почнемо нашу подорож. Оберіть тему: ", startNode.message());
    assertEquals(ButtonType.INLINE, startNode.buttonType());
    assertNotNull(buttons);
    assertEquals(5, buttons.size());
    assertEquals(BUTTON_LABEL_HISTORY, buttons.getFirst().label());
    assertEquals(HISTORY_Q1, buttons.get(0).next());
    assertNull(buttons.get(0).url());
    assertEquals(BUTTON_LABEL_SCIENCE, buttons.get(1).label());
    assertEquals(SCIENCE_Q1, buttons.get(1).next());
    assertNull(buttons.get(1).url());
    assertEquals(BUTTON_LABEL_GEOGRAPHY, buttons.get(2).label());
    assertEquals(GEOGRAPHY_Q1, buttons.get(2).next());
    assertNull(buttons.get(2).url());
    assertEquals(BUTTON_LABEL_CINEMA, buttons.get(3).label());
    assertEquals(CINEMA_Q1, buttons.get(3).next());
    assertNull(buttons.get(3).url());
    assertEquals(BUTTON_LABEL_GITHUB, buttons.get(4).label());
    assertNull(buttons.get(4).next());
    assertEquals(GITHUB_URL, buttons.get(4).url());
  }

  @Configuration
  @EnableConfigurationProperties(BotProperties.class)
  @ComponentScan(
      basePackageClasses = {
        com.github.jawisimo.tbcfstarter.loader.DialogLoader.class,
        com.github.jawisimo.tbcfstarter.parser.DialogParserProvider.class,
        com.github.jawisimo.tbcfstarter.validator.DialogValidator.class
      })
  static class TestConfig {}
}
