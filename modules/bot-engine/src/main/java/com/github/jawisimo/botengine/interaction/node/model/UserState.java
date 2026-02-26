package com.github.jawisimo.botengine.interaction.node.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserState {
  @Id private String chatId;
  private String nodeId;
}
