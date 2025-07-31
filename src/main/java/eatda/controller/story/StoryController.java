package eatda.controller.story;

import eatda.controller.web.auth.LoginMember;
import eatda.service.story.StoryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @PostMapping("/api/stories")
    public ResponseEntity<StoryRegisterResponse> registerStory(
            @RequestPart("request") StoryRegisterRequest request,
            @RequestPart("image") MultipartFile image,
            LoginMember member
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storyService.registerStory(request, image, member.id()));
    }

    @GetMapping("api/stories")
    public ResponseEntity<StoriesResponse> getStories(@RequestParam(defaultValue = "5") @Min(1) @Max(50) int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(storyService.getPagedStoryPreviews(size));
    }

    @GetMapping("/api/stories/{storyId}")
    public ResponseEntity<StoryResponse> getStory(@PathVariable long storyId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(storyService.getStory(storyId));
    }

    @GetMapping("/api/stories/kakao/{kakaoId}")
    public ResponseEntity<StoriesDetailResponse> getStoriesByKakaoId(
            @PathVariable String kakaoId,
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int size
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(storyService.getPagedStoryDetails(kakaoId, size));
    }
}
