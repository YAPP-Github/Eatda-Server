package eatda.repository.cheer;

import static org.assertj.core.api.Assertions.assertThat;

import eatda.domain.cheer.Cheer;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheerRepositoryTest extends BaseRepositoryTest {

    @Nested
    class BasicOperations {

        @Test
        void 응원을_저장하고_조회할_수_있다() {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            Cheer cheer = cheerGenerator.generateCommon(member, store);

            Cheer foundCheer = cheerRepository.findById(cheer.getId()).orElseThrow();

            assertThat(foundCheer.getId()).isEqualTo(cheer.getId());
            assertThat(foundCheer.getMember().getId()).isEqualTo(member.getId());
            assertThat(foundCheer.getStore().getId()).isEqualTo(store.getId());
        }

        @Test
        void 멤버별_응원_개수를_조회할_수_있다() {
            Member member = memberGenerator.generate("111");
            Store store1 = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            Store store2 = storeGenerator.generate("순대국밥", "서울 강남구 역삼동 123-45");

            cheerGenerator.generateCommon(member, store1);
            cheerGenerator.generateCommon(member, store2);

            long count = cheerRepository.countByMember(member);

            assertThat(count).isEqualTo(2);
        }
    }
}
