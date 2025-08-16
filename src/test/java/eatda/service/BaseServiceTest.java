package eatda.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import eatda.DatabaseCleaner;
import eatda.client.map.MapClient;
import eatda.client.oauth.OauthClient;
import eatda.domain.ImageKey;
import eatda.fixture.CheerGenerator;
import eatda.fixture.MemberGenerator;
import eatda.fixture.StoreGenerator;
import eatda.fixture.StoryGenerator;
import eatda.repository.cheer.CheerRepository;
import eatda.repository.cheer.CheerTagRepository;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.StoreRepository;
import eatda.repository.story.StoryRepository;
import eatda.service.auth.AuthService;
import eatda.service.auth.OauthService;
import eatda.service.store.StoreSearchService;
import eatda.storage.image.ExternalImageStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(DatabaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseServiceTest {

    private static final ImageKey MOCKED_IMAGE_KEY = new ImageKey("mocked-image-key");
    private static final String MOCKED_IMAGE_URL = "https://example.com/image.jpg";

    @MockitoBean
    protected OauthClient oauthClient;

    @MockitoBean
    protected MapClient mapClient;

    @MockitoBean
    protected ExternalImageStorage externalImageStorage;

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
    protected StoryGenerator storyGenerator;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected StoreRepository storeRepository;

    @Autowired
    protected StoreSearchService storeSearchService;

    @Autowired
    protected CheerRepository cheerRepository;

    @Autowired
    protected CheerTagRepository cheerTagRepository;

    @Autowired
    protected StoryRepository storyRepository;

    @BeforeEach
    void mockingImageService() {
        doReturn(MOCKED_IMAGE_URL).when(externalImageStorage).getPreSignedUrl(any());
        doReturn(MOCKED_IMAGE_KEY).when(externalImageStorage).upload(any());
    }
}
