package eatda.controller.story;

import eatda.controller.web.auth.LoginMember;
import eatda.service.story.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @PostMapping("/api/stories")
    public ResponseEntity<Void> registerStory(
            @RequestPart("request") StoryRegisterRequest request,
            @RequestPart("image") MultipartFile image,
            LoginMember member
    ) {
        storyService.registerStory(request, image, member.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
