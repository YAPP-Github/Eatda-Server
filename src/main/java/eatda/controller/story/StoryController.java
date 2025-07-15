package eatda.controller.story;

import eatda.controller.web.auth.LoginMember;
import eatda.service.story.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public ResponseEntity<Void> registerStory(
            @ModelAttribute StoryRegisterRequest request,
            @RequestPart MultipartFile image,
            LoginMember member
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/stories")
    public ResponseEntity<Void> getStories() {

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/story")
    public ResponseEntity<Void> getStory(@RequestParam Long storyId) {

        return ResponseEntity.noContent().build();
    }
}
