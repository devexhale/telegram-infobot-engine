package com.github.jawisimo.tbcfstarter.repository;

class InMemoryUserStateRepositoryTest extends AbstractUserStateRepositoryTest {

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
