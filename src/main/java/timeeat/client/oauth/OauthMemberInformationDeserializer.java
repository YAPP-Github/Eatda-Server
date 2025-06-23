package timeeat.client.oauth;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class OauthMemberInformationDeserializer extends JsonDeserializer<OauthMemberInformation> {

    @Override
    public OauthMemberInformation deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode root = p.getCodec().readTree(p);

        long id = root.path("id").asLong();
        String nickname = root
                .path("kakao_account")
                .path("profile")
                .path("nickname")
                .asText(null);
        return new OauthMemberInformation(id, nickname);
    }
}
