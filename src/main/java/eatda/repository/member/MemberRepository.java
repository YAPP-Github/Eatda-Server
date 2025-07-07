package eatda.repository.member;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import eatda.domain.member.Member;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;

public interface MemberRepository extends Repository<Member, Long> {

    Member save(Member member);

    Optional<Member> findById(Long id);

    default Member getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.INVALID_MEMBER_ID));
    }

    Optional<Member> findBySocialId(String socialId);

    boolean existsByNickname(String nickname);

    boolean existsByMobilePhoneNumberValue(String phoneNumber);
}
