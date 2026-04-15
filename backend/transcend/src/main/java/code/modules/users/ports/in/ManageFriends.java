import java.util.UUID;

public interface ManageFriends {

    void addFriend(User user, UUID friendId);

    void removeFriend(User user, UUID friendId);
}