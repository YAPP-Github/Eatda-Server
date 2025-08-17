package eatda.repository.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTagName;
import eatda.domain.member.Member;
import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.repository.BaseRepositoryTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

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

    @Nested
    class FindAllByConditions {

        @Test
        void 카테고리로_필터링하여_조회할_수_있다() {
            LocalDateTime startAt = LocalDateTime.of(2023, 10, 1, 12, 0);
            Store store1 = storeGenerator.generate("1235", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);
            Store store2 = storeGenerator.generate("1236", "서울시 강남구 역삼동 123-45", StoreCategory.WESTERN, startAt);
            Store store3 = storeGenerator.generate("1237", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);

            List<Store> actual = storeRepository.findAllByConditions(
                    StoreCategory.KOREAN, List.of(), List.of(), Pageable.unpaged());

            assertThat(actual).map(Store::getId)
                    .containsExactlyInAnyOrder(store1.getId(), store3.getId());
        }

        @Test
        void 응원_태그를_필터링하여_조회할_수_있다() {
            Member member1 = memberGenerator.generateRegisteredMember("커찬", "ac@kakao.com", "123", "01012341235");
            Member member2 = memberGenerator.generateRegisteredMember("지민", "ad@kakao.com", "124", "01012341236");
            LocalDateTime startAt = LocalDateTime.of(2023, 10, 1, 12, 0);
            Store store1 = storeGenerator.generate("1235", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);
            Store store2 = storeGenerator.generate("1236", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);
            Store store3 = storeGenerator.generate("1237", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);
            Store store4 = storeGenerator.generate("1238", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);
            Cheer cheer1_1 = cheerGenerator.generate(member1, store1, startAt);
            Cheer cheer2_1 = cheerGenerator.generate(member1, store2, startAt);
            Cheer cheer2_2 = cheerGenerator.generate(member2, store2, startAt);
            Cheer cheer3_1 = cheerGenerator.generate(member1, store3, startAt);
            Cheer cheer4_2 = cheerGenerator.generate(member2, store4, startAt);
            cheerTagGenerator.generate(cheer1_1, List.of(CheerTagName.INSTAGRAMMABLE, CheerTagName.ENERGETIC));
            cheerTagGenerator.generate(cheer2_1, List.of(CheerTagName.CLEAN_RESTROOM));
            cheerTagGenerator.generate(cheer2_2, List.of(CheerTagName.CLEAN_RESTROOM));
            cheerTagGenerator.generate(cheer3_1, List.of(CheerTagName.ENERGETIC, CheerTagName.QUIET));

            List<Store> actual = storeRepository.findAllByConditions(null,
                    List.of(CheerTagName.INSTAGRAMMABLE, CheerTagName.CLEAN_RESTROOM), List.of(), Pageable.unpaged());

            assertThat(actual).map(Store::getId)
                    .containsExactlyInAnyOrder(store1.getId(), store2.getId());
        }

        @Test
        void 지역구를_필터링하여_조회할_수_있다() {
            LocalDateTime startAt = LocalDateTime.of(2023, 10, 1, 12, 0);
            Store store1 = storeGenerator.generate("1235", "서울시 강남구 역삼동 123-45", District.GANGNAM);
            Store store2 = storeGenerator.generate("1236", "서울시 강남구 역삼동 123-45", District.GANGNAM);
            Store store3 = storeGenerator.generate("1237", "서울시 성북구 석관동 123-45", District.SEONGBUK);

            List<Store> actual = storeRepository.findAllByConditions(
                    null, List.of(), List.of(District.GANGNAM), Pageable.unpaged());

            assertThat(actual).map(Store::getId)
                    .containsExactlyInAnyOrder(store1.getId(), store2.getId());
        }

        @Test
        void 여러_조건을_조합하여_조회할_수_있다() {
            Member member1 = memberGenerator.generateRegisteredMember("커찬", "ac@kakao.com", "123", "01012341235");
            Member member2 = memberGenerator.generateRegisteredMember("지민", "ad@kakao.com", "124", "01012341236");
            Store store1 = storeGenerator.generate("1235", "서울시 성북구 석관동 123-41", District.SEONGBUK,
                    StoreCategory.KOREAN);
            Store store2 = storeGenerator.generate("1236", "서울시 강남구 역삼동 123-42", District.GANGNAM,
                    StoreCategory.WESTERN);
            Store store3 = storeGenerator.generate("1237", "서울시 강남구 역삼동 123-43", District.GANGNAM,
                    StoreCategory.KOREAN);
            Store store4 = storeGenerator.generate("1238", "서울시 강남구 역삼동 123-44", District.GANGNAM,
                    StoreCategory.KOREAN);
            Store store5 = storeGenerator.generate("1239", "서울시 강남구 역삼동 123-45", District.GANGNAM,
                    StoreCategory.KOREAN);
            Cheer cheer1_1 = cheerGenerator.generateCommon(member1, store1);
            Cheer cheer2_1 = cheerGenerator.generateCommon(member1, store2);
            Cheer cheer3_1 = cheerGenerator.generateCommon(member1, store3);
            Cheer cheer3_2 = cheerGenerator.generateCommon(member2, store3);
            Cheer cheer4_2 = cheerGenerator.generateCommon(member2, store4);
            Cheer cheer5_2 = cheerGenerator.generateCommon(member2, store5);
            cheerTagGenerator.generate(cheer1_1, List.of(CheerTagName.INSTAGRAMMABLE, CheerTagName.ENERGETIC));
            cheerTagGenerator.generate(cheer2_1, List.of(CheerTagName.CLEAN_RESTROOM));
            cheerTagGenerator.generate(cheer3_1, List.of(CheerTagName.ENERGETIC, CheerTagName.QUIET));
            cheerTagGenerator.generate(cheer3_2, List.of(CheerTagName.CLEAN_RESTROOM));
            cheerTagGenerator.generate(cheer4_2, List.of(CheerTagName.INSTAGRAMMABLE));
            cheerTagGenerator.generate(cheer5_2, List.of(CheerTagName.CLEAN_RESTROOM, CheerTagName.ENERGETIC));

            List<Store> actual = storeRepository.findAllByConditions(StoreCategory.KOREAN,
                    List.of(CheerTagName.CLEAN_RESTROOM), List.of(District.GANGNAM), Pageable.unpaged());

            assertThat(actual).map(Store::getId)
                    .containsExactlyInAnyOrder(store3.getId(), store5.getId());
        }

        @Test
        void 조건없이_모든_가게를_조회할_수_있다() {
            Member member1 = memberGenerator.generateRegisteredMember("커찬", "ac@kakao.com", "123", "01012341235");
            Member member2 = memberGenerator.generateRegisteredMember("지민", "ad@kakao.com", "124", "01012341236");
            LocalDateTime startAt = LocalDateTime.of(2023, 10, 1, 12, 0);
            Store store1 = storeGenerator.generate("1235", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);
            Store store2 = storeGenerator.generate("1236", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);
            Store store3 = storeGenerator.generate("1237", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);
            Cheer cheer1_1 = cheerGenerator.generate(member1, store1, startAt);
            Cheer cheer2_1 = cheerGenerator.generate(member1, store2, startAt);
            Cheer cheer2_2 = cheerGenerator.generate(member2, store2, startAt);
            Cheer cheer3_1 = cheerGenerator.generate(member1, store3, startAt);
            cheerTagGenerator.generate(cheer1_1, List.of(CheerTagName.INSTAGRAMMABLE, CheerTagName.ENERGETIC));
            cheerTagGenerator.generate(cheer2_1, List.of(CheerTagName.CLEAN_RESTROOM));
            cheerTagGenerator.generate(cheer2_2, List.of(CheerTagName.CLEAN_RESTROOM));

            List<Store> actual = storeRepository.findAllByConditions(null, List.of(), List.of(), Pageable.unpaged());

            assertThat(actual).map(Store::getId)
                    .containsExactlyInAnyOrder(store1.getId(), store2.getId(), store3.getId());
        }
    }
}
