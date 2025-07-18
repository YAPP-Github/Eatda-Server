package eatda.controller.story;

public record FilteredSearchResult(
        String kakaoId,
        String name,
        String roadAddress,
        String lotNumberAddress,
        String category
) {
}
