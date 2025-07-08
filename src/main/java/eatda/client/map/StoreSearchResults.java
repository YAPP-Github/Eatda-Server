package eatda.client.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StoreSearchResults(@JsonProperty("documents") List<StoreSearchResult> results) {
}
