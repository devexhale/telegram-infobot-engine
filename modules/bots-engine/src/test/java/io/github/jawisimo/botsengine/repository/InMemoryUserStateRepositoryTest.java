package io.github.jawisimo.botsengine.repository;

class InMemoryUserStateRepositoryTest extends BaseUserStateRepositoryTest {

  private InMemoryUserStateRepository repository;

  @Override
  protected UserStateRepository getRepository() {
    return repository;
  }

  @Override
  protected void cleanUp() {
    repository = new InMemoryUserStateRepository();
  }
}
