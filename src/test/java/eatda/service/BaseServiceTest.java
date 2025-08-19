package eatda.service;

import eatda.DatabaseCleaner;
import eatda.client.file.FileClient;
import eatda.client.map.MapClient;
import eatda.client.oauth.OauthClient;
import eatda.fixture.CheerGenerator;
import eatda.fixture.CheerImageGenerator;
import eatda.fixture.CheerTagGenerator;
import eatda.fixture.MemberGenerator;
import eatda.fixture.StoreGenerator;
import eatda.fixture.StoryGenerator;
import eatda.fixture.StoryImageGenerator;
import eatda.repository.cheer.CheerRepository;
import eatda.repository.cheer.CheerTagRepository;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.StoreRepository;
import eatda.repository.story.StoryRepository;
import eatda.service.auth.AuthService;
import eatda.service.auth.OauthService;
import eatda.service.store.StoreSearchService;
import eatda.service.story.StoryService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(DatabaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseServiceTest {

    private static final String MOCKED_IMAGE_URL = "https://example.com/image.jpg";

    @MockitoBean
    protected OauthClient oauthClient;

    @MockitoBean
    protected MapClient mapClient;

    @MockitoBean
    protected FileClient fileClient;

    @Autowired
    protected OauthService oauthService;

    @Autowired
    protected AuthService authService;

    @Autowired
    protected MemberGenerator memberGenerator;

    @Autowired
    protected StoreGenerator storeGenerator;

    @Autowired
    protected CheerGenerator cheerGenerator;

    @Autowired
    protected CheerTagGenerator cheerTagGenerator;

    @Autowired
    protected StoryGenerator storyGenerator;

    @Autowired
    protected CheerImageGenerator cheerImageGenerator;

    @Autowired
    protected StoryImageGenerator storyImageGenerator;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected StoreRepository storeRepository;

    @Autowired
    protected StoryService storyService;

    @Autowired
    protected StoreSearchService storeSearchService;

    @Autowired
    protected CheerRepository cheerRepository;

    @Autowired
    protected CheerTagRepository cheerTagRepository;

    @Autowired
    protected StoryRepository storyRepository;

}
