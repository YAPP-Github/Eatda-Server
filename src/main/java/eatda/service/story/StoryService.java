package eatda.service.story;

import eatda.controller.story.StoryRegisterRequest;
import eatda.repository.member.MemberRepository;
import eatda.repository.story.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public void saveStory(StoryRegisterRequest request) {

        storyRepository.save();
    }
}
