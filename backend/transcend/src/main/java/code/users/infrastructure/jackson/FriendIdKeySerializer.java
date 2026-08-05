package code.users.infrastructure.jackson;

import code.users.domain.model.FriendId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class FriendIdKeySerializer extends JsonSerializer<FriendId> {
  @Override
  public void serialize(FriendId id, JsonGenerator gen, SerializerProvider sp) throws IOException {
    gen.writeFieldName(id.val().toString());
  }
}
