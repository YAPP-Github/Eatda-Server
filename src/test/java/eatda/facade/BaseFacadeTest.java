package eatda.facade;

import eatda.DatabaseCleaner;
import eatda.client.file.FileClient;
import eatda.client.map.MapClient;
import eatda.client.oauth.OauthClient;
import eatda.fixture.MemberGenerator;
import eatda.fixture.StoreGenerator;
import eatda.repository.cheer.CheerRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(DatabaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseFacadeTest {

    @MockitoBean
    protected OauthClient oauthClient;

    @MockitoBean
    protected MapClient mapClient;

    @MockitoBean
    protected FileClient fileClient;

    @Autowired
    protected MemberGenerator memberGenerator;

    @Autowired
    protected StoreGenerator storeGenerator;

    @Autowired
    protected CheerRepository cheerRepository;
}
