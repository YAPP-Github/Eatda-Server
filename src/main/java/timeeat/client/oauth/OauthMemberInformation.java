package timeeat.client.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = OauthMemberInformationDeserializer.class)
public record OauthMemberInformation(long socialId, String nickname) {
}
