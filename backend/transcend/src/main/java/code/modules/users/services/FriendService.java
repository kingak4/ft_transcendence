import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class FriendService implements ManageFriends
{

    @Override
    public void addFriend(User user, UUID friendId) {
        System.out.println("Adding friend...");
    }

    @Override
    public void removeFriend(User user, UUID friendId) {
        System.out.println("Removing friend...");
    }
}