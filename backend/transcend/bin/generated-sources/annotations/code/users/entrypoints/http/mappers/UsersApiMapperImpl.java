package code.users.entrypoints.http.mappers;

import code.users.domain.model.UserDetails;
import code.users.entrypoints.http.LoginController;
import code.users.entrypoints.http.RegisterController;
import code.users.entrypoints.http.UserDetailsController;
import code.users.ports.in.LoginUseCase;
import code.users.ports.in.RegisterUseCase;
import code.users.ports.in.UpdateDisplayNameUseCase;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UsersApiMapperImpl implements UsersApiMapper {

    @Override
    public LoginUseCase.LoginCommand toCommand(LoginController.LoginRequest request) {
        if ( request == null ) {
            return null;
        }

        String rawPassword = null;
        String email = null;

        rawPassword = request.password();
        email = request.email();

        LoginUseCase.LoginCommand loginCommand = new LoginUseCase.LoginCommand( email, rawPassword );

        return loginCommand;
    }

    @Override
    public LoginController.LoginResponse toResponse(LoginUseCase.LoginResult result) {
        if ( result == null ) {
            return null;
        }

        String accessToken = null;
        String tokenType = null;
        String userId = null;

        accessToken = result.accessToken();
        tokenType = result.tokenType();
        userId = result.userId();

        LoginController.LoginResponse loginResponse = new LoginController.LoginResponse( accessToken, tokenType, userId );

        return loginResponse;
    }

    @Override
    public RegisterUseCase.RegisterCommand toCommand(RegisterController.RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        String rawPassword = null;
        String email = null;

        rawPassword = request.password();
        email = request.email();

        RegisterUseCase.RegisterCommand registerCommand = new RegisterUseCase.RegisterCommand( email, rawPassword );

        return registerCommand;
    }

    @Override
    public RegisterController.RegisterResponse toResponse(RegisterUseCase.RegisteredUser result) {
        if ( result == null ) {
            return null;
        }

        UUID id = null;

        id = map( result.id() );

        RegisterController.RegisterResponse registerResponse = new RegisterController.RegisterResponse( id );

        return registerResponse;
    }

    @Override
    public UpdateDisplayNameUseCase.UpdateDisplayNameCommand toCommand(UserDetailsController.UpdateDisplayNameRequest request) {
        if ( request == null ) {
            return null;
        }

        String displayName = null;

        displayName = request.displayName();

        UpdateDisplayNameUseCase.UpdateDisplayNameCommand updateDisplayNameCommand = new UpdateDisplayNameUseCase.UpdateDisplayNameCommand( displayName );

        return updateDisplayNameCommand;
    }

    @Override
    public UserDetailsController.GetUserDetailsResponse toResponse(UserDetails details) {
        if ( details == null ) {
            return null;
        }

        String displayName = null;
        String avatarUrl = null;

        displayName = details.getDisplayName();
        avatarUrl = details.getAvatarUrl();

        UserDetailsController.GetUserDetailsResponse getUserDetailsResponse = new UserDetailsController.GetUserDetailsResponse( displayName, avatarUrl );

        return getUserDetailsResponse;
    }
}
