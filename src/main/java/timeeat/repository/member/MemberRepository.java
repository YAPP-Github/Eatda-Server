package timeeat.repository.member;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import timeeat.domain.member.Member;

public interface MemberRepository extends Repository<Member, Long> {

    Member save(Member member);

    Optional<Member> findBySocialId(String socialId);
}
