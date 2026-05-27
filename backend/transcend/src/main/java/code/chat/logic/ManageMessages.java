package code.chat.logic;

import code.chat.ports.in.ManageMessagesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageMessages implements ManageMessagesUseCase {
    @Override
    public void sendMessage(SendMessageCommand command) {

    }

    @Override
    public void deleteMessage(DeleteMessageCommand command) {

    }
}
