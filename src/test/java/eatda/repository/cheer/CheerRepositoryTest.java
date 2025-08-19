package eatda.repository.cheer;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Nested
    class FindAllByConditions {

        @Test
        void 카테고리로_필터링하여_조회할_수_있다() {
            Member member1 = memberGenerator.generateRegisteredMember("커찬", "ac@kakao.com", "123", "01012341235");
            Member member2 = memberGenerator.generateRegisteredMember("지민", "ad@kakao.com", "124", "01012341236");
            LocalDateTime startAt = LocalDateTime.of(2023, 10, 1, 12, 0);
            Store store1 = storeGenerator.generate("1235", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);
            Store store2 = storeGenerator.generate("1236", "서울시 강남구 역삼동 123-45", StoreCategory.WESTERN, startAt);
            Store store3 = storeGenerator.generate("1237", "서울시 강남구 역삼동 123-45", StoreCategory.KOREAN, startAt);
            Cheer cheer1_1 = cheerGenerator.generateCommon(member1, store1);
            Cheer cheer2_1 = cheerGenerator.generateCommon(member1, store2);
            Cheer cheer2_2 = cheerGenerator.generateCommon(member2, store2);
            Cheer cheer3_2 = cheerGenerator.generateCommon(member2, store3);

            List<Cheer> actual = cheerRepository.findAllByConditions(
                    StoreCategory.KOREAN, List.of(), List.of(), Pageable.unpaged());

            assertThat(actual).map(Cheer::getId)
                    .containsExactlyInAnyOrder(cheer1_1.getId(), cheer3_2.getId());
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

            List<Cheer> actual = cheerRepository.findAllByConditions(null,
                    List.of(CheerTagName.INSTAGRAMMABLE, CheerTagName.CLEAN_RESTROOM), List.of(), Pageable.unpaged());

            assertThat(actual)
                    .map(Cheer::getId)
                    .containsExactlyInAnyOrder(cheer1_1.getId(), cheer2_1.getId(), cheer2_2.getId());
        }

        @Test
        void 지역구를_필터링하여_조회할_수_있다() {
            Member member1 = memberGenerator.generateRegisteredMember("커찬", "ac@kakao.com", "123", "01012341235");
            Member member2 = memberGenerator.generateRegisteredMember("지민", "ad@kakao.com", "124", "01012341236");
            Store store1 = storeGenerator.generate("1235", "서울시 강남구 역삼동 123-45", District.GANGNAM);
            Store store2 = storeGenerator.generate("1236", "서울시 강남구 역삼동 123-45", District.GANGNAM);
            Store store3 = storeGenerator.generate("1237", "서울시 성북구 석관동 123-45", District.SEONGBUK);
            Cheer cheer1_1 = cheerGenerator.generateCommon(member1, store1);
            Cheer cheer2_1 = cheerGenerator.generateCommon(member1, store2);
            Cheer cheer2_2 = cheerGenerator.generateCommon(member2, store2);
            Cheer cheer3_2 = cheerGenerator.generateCommon(member2, store3);

            List<Cheer> actual = cheerRepository.findAllByConditions(
                    null, List.of(), List.of(District.GANGNAM), Pageable.unpaged());

            assertThat(actual)
                    .map(Cheer::getId)
                    .containsExactlyInAnyOrder(cheer1_1.getId(), cheer2_1.getId(), cheer2_2.getId());
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

            List<Cheer> actual = cheerRepository.findAllByConditions(StoreCategory.KOREAN,
                    List.of(CheerTagName.CLEAN_RESTROOM), List.of(District.GANGNAM), Pageable.unpaged());

            assertThat(actual)
                    .map(Cheer::getId)
                    .containsExactlyInAnyOrder(cheer3_2.getId(), cheer5_2.getId());
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

            List<Cheer> actual = cheerRepository.findAllByConditions(null, List.of(), List.of(), Pageable.unpaged());

            assertThat(actual)
                    .map(Cheer::getId)
                    .containsExactlyInAnyOrder(cheer1_1.getId(), cheer2_1.getId(), cheer2_2.getId(), cheer3_1.getId());
        }
    }
}
