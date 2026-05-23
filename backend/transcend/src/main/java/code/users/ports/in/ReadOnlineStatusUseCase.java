package code.users.ports.in;

import code.users.domain.model.UserId;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;

@AsyncPublisher(
    operation =
        @AsyncOperation(
            channelName = "/topic/user/{userId}/presence",
            description =
                "Get real-time online presence status for a user. Secured with JWT bearer token authentication."))
public interface ReadOnlineStatusUseCase {

  boolean isOnline(UserId userId);
}
