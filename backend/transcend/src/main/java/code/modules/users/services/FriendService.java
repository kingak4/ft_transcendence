package code.modules.users.services;

import org.springframework.stereotype.Service;
import java.util.UUID;

import code.modules.users.ports.in.ManageFriends;
import code.modules.users.domain.User;

@Service
public class FriendService implements ManageFriends {

    @Override
    public void addFriend(User user, UUID friendId) {
        System.out.println("Adding friend...");
    }

    @Override
    public void removeFriend(User user, UUID friendId) {
        System.out.println("Removing friend...");
    }
}