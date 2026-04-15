package code.modules.users.ports.in;

import code.modules.users.domain.User;

public interface UpdateInformation
{

    void changePassword(User user);

    void changeAvatar(User user);
}