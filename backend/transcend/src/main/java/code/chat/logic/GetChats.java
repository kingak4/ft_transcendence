package code.chat.logic;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.UserId;
import code.chat.ports.in.GetChatsUseCase;
import code.chat.ports.out.ChatDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetChats implements GetChatsUseCase {

    private final ChatDao dao;

    @Override
    public List<ChatId> getChatList(UserId userId, int page, int size) {
        return dao.getChatList(userId, page, size);
    }
}
