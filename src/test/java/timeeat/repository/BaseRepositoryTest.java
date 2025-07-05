package timeeat.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import timeeat.fixture.MemberGenerator;
import timeeat.repository.member.MemberRepository;

@DataJpaTest
public abstract class BaseRepositoryTest {

    @Autowired
    protected MemberGenerator memberGenerator;

    @Autowired
    protected MemberRepository memberRepository;
}
