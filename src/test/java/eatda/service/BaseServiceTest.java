package eatda.service;

import eatda.DatabaseCleaner;
import eatda.client.map.MapClient;
import eatda.client.oauth.OauthClient;
import eatda.fixture.MemberGenerator;
import eatda.repository.member.MemberRepository;
import eatda.repository.story.StoryRepository;
import eatda.service.common.ImageService;
import eatda.service.store.StoreService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(DatabaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @MockitoBean
    protected OauthClient oauthClient;

    @MockitoBean
    protected MapClient mapClient;

    @Autowired
    protected MemberGenerator memberGenerator;

    @Autowired
    protected MemberRepository memberRepository;

    @MockitoBean
    protected StoreService storeService;

    @MockitoBean
    protected ImageService imageService;

    @Autowired
    protected StoryRepository storyRepository;
}
