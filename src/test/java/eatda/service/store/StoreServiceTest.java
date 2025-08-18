package eatda.service.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.controller.store.StoreResponse;
import eatda.controller.store.StoresInMemberResponse;
import eatda.controller.store.StoresResponse;
import eatda.domain.member.Member;
import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.service.BaseServiceTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StoreServiceTest extends BaseServiceTest {

    @Autowired
    private StoreService storeService;

    @Nested
    class GetStore {

        @Test
        void 가게_정보를_조회한다() {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            cheerGenerator.generateCommon(member, store, false);

            StoreResponse response = storeService.getStore(store.getId());

            assertAll(
                    () -> assertThat(response.id()).isEqualTo(store.getId()),
                    () -> assertThat(response.name()).isEqualTo(store.getName()),
                    () -> assertThat(response.category()).isEqualTo(store.getCategory().getCategoryName()),
                    () -> assertThat(response.district()).isEqualTo(store.getDistrict().getName()),
                    () -> assertThat(response.neighborhood()).isEqualTo(store.getAddressNeighborhood())
            );
        }
    }

    @Nested
    class GetStores {

        @Test
        void 음식점_목록을_최신순으로_조회한다() {
            Member member = memberGenerator.generate("111");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Store store1 = storeGenerator.generate("112", "서울 강남구 대치동 896-33", StoreCategory.KOREAN, startAt);
            Store store2 = storeGenerator.generate("113", "서울 성북구 석관동 123-45", StoreCategory.OTHER,
                    startAt.plusHours(1));
            Store store3 = storeGenerator.generate("114", "서울 강남구 역삼동 678-90", StoreCategory.KOREAN,
                    startAt.plusHours(2));
            cheerGenerator.generateCommon(member, store1, false);
            cheerGenerator.generateCommon(member, store2, false);
            cheerGenerator.generateCommon(member, store3, false);

            int page = 0;
            int size = 2;

            StoresResponse response = storeService.getStores(page, size, null);

            assertAll(
                    () -> assertThat(response.stores()).hasSize(size),
                    () -> assertThat(response.stores().get(0).id()).isEqualTo(store3.getId()),
                    () -> assertThat(response.stores().get(1).id()).isEqualTo(store2.getId())
            );
        }

        @Test
        void 특정_카테고리의_음식점_목록을_최신순으로_조회한다() {
            Member member = memberGenerator.generate("111");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Store store1 = storeGenerator.generate("112", "서울 강남구 대치동 896-33", StoreCategory.CAFE, startAt);
            Store store2 = storeGenerator.generate("113", "서울 성북구 석관동 123-45", StoreCategory.OTHER,
                    startAt.plusHours(1));
            Store store3 = storeGenerator.generate("114", "서울 강남구 역삼동 678-90", StoreCategory.CAFE,
                    startAt.plusHours(2));
            cheerGenerator.generateCommon(member, store1, false);
            cheerGenerator.generateCommon(member, store2, false);
            cheerGenerator.generateCommon(member, store3, false);

            int page = 0;
            int size = 2;
            StoreCategory category = StoreCategory.CAFE;

            StoresResponse response = storeService.getStores(page, size, category.getCategoryName());

            assertAll(
                    () -> assertThat(response.stores()).hasSize(size),
                    () -> assertThat(response.stores().get(0).id()).isEqualTo(store3.getId()),
                    () -> assertThat(response.stores().get(1).id()).isEqualTo(store1.getId())
            );
        }
    }

    @Nested
    class GetStoresByCheeredMember {

        @Test
        void 특정_회원이_응원한_음식점_목록을_조회한다() {
            Member member = memberGenerator.generate("1111", "abc@kakao.com", "nickname1");
            Member anotherMember = memberGenerator.generate("1112", "abd@kakao.com", "nickname2");
            Store store1 = storeGenerator.generate("123", "서울 강남구 대치동 896-33", District.GANGNAM);
            Store store2 = storeGenerator.generate("124", "서울 성북구 석관동 123-45", District.SEONGBUK);
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            cheerGenerator.generate(member, store1, startAt);
            cheerGenerator.generate(anotherMember, store1, startAt.plusHours(2));
            cheerGenerator.generate(member, store2, startAt.plusHours(1));

            StoresInMemberResponse response = storeService.getStoresByCheeredMember(member.getId());

            assertAll(
                    () -> assertThat(response.stores()).hasSize(2),
                    () -> assertThat(response.stores().get(0).id()).isEqualTo(store2.getId()),
                    () -> assertThat(response.stores().get(0).district()).isEqualTo("성북구"),
                    () -> assertThat(response.stores().get(0).neighborhood()).isEqualTo("석관동"),
                    () -> assertThat(response.stores().get(0).cheerCount()).isEqualTo(1L),
                    () -> assertThat(response.stores().get(1).id()).isEqualTo(store1.getId()),
                    () -> assertThat(response.stores().get(1).district()).isEqualTo("강남구"),
                    () -> assertThat(response.stores().get(1).neighborhood()).isEqualTo("대치동"),
                    () -> assertThat(response.stores().get(1).cheerCount()).isEqualTo(2L)
            );
        }

        @Test
        void 응원한_음식점이_없으면_빈_목록을_반환한다() {
            Member member = memberGenerator.generate("111");

            var response = storeService.getStoresByCheeredMember(member.getId());

            assertThat(response.stores()).isEmpty();
        }
    }

}
