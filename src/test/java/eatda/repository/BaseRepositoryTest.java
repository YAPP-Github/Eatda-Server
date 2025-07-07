package eatda.repository;

import eatda.fixture.MemberGenerator;
import eatda.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public abstract class BaseRepositoryTest {

    @Autowired
    protected MemberGenerator memberGenerator;

    @Autowired
    protected MemberRepository memberRepository;
}
