package eatda.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import eatda.fixture.MemberGenerator;
import eatda.repository.member.MemberRepository;

@DataJpaTest
public abstract class BaseRepositoryTest {

    @Autowired
    protected MemberGenerator memberGenerator;

    @Autowired
    protected MemberRepository memberRepository;
}
