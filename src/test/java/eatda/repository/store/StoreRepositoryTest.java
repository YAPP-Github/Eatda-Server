package eatda.repository.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.repository.BaseRepositoryTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StoreRepositoryTest extends BaseRepositoryTest {

    @Nested
    class FindAllByCheeredMemberId {

        @Test
        void 멤버가_응원한_가게를_조회할_수_있다() {
            Member member = memberGenerator.generateRegisteredMember("커찬", "abc@kakao.com", "123", "01012341235");
            Store store1 = storeGenerator.generate("1235", "서울시 강남구 역삼동 123-45");
            Store store2 = storeGenerator.generate("1236", "서울시 강남구 역삼동 123-45");
            Store store3 = storeGenerator.generate("1237", "서울시 강남구 역삼동 123-45");
            LocalDateTime startAt = LocalDateTime.of(2023, 10, 1, 12, 0);
            cheerGenerator.generate(member, store1, startAt);
            cheerGenerator.generate(member, store3, startAt.plusHours(1));

            List<Store> actual = storeRepository.findAllByCheeredMemberId(member.getId());

            assertAll(
                    () -> assertThat(actual).hasSize(2),
                    () -> assertThat(actual.get(0).getId()).isEqualTo(store3.getId()),
                    () -> assertThat(actual.get(1).getId()).isEqualTo(store1.getId())
            );
        }

        @Test
        void 멤버가_응원한_가게가_없으면_빈_리스트를_반환한다() {
            Member member = memberGenerator.generateRegisteredMember("커찬", "abc@kakao.com", "123", "01012341235");
            storeGenerator.generate("1235", "서울시 강남구 역삼동 123-45");

            List<Store> actual = storeRepository.findAllByCheeredMemberId(member.getId());

            assertThat(actual).isEmpty();
        }
    }
}
