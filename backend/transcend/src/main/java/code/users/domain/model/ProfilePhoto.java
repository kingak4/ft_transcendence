package code.users.domain.model;

import lombok.Value;
import lombok.With;

@Value
@With
public class ProfilePhoto {
  String url;

  public static ProfilePhoto defaultPhoto() {
    return new ProfilePhoto("/avatars/default.png");
  }
}
