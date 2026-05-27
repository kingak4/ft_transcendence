package code.chat.logic;

import code.chat.domain.model.UserId;
import code.chat.ports.in.GetChatsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetChats implements GetChatsUseCase {

    @Override
    public void getChatList(UserId userId) {

    }
}
