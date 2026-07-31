package code.chat.logic;

import code.chat.domain.model.UserId;
import code.chat.ports.in.GetChatsUseCase;
import code.chat.ports.out.ChatDao;
import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetChats implements GetChatsUseCase {

  private final ChatDao dao;

  @Override
  public Page<ChatSummary> getChatList(UserId userId, int page, int size) {
    return dao.getChatList(userId, page, size);
  }
}
