package eatda.service.cheer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.controller.cheer.CheerRegisterRequest;
import eatda.controller.cheer.CheerResponse;
import eatda.controller.cheer.CheersInStoreResponse;
import eatda.controller.cheer.CheersResponse;
import eatda.domain.ImageKey;
import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTagName;
import eatda.domain.member.Member;
import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.domain.store.StoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.service.BaseServiceTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CheerServiceTest extends BaseServiceTest {

    @Autowired
    private CheerService cheerService;

    @Nested
    class RegisterCheer {

        @Test
        void 응원_개수가_최대_개수를_초과하면_예외가_발생한다() {
            Member member = memberGenerator.generate("123");
            Store store1 = storeGenerator.generate("124", "서울시 강남구 역삼동 123-45");
            Store store2 = storeGenerator.generate("125", "서울시 강남구 역삼동 123-45");
            Store store3 = storeGenerator.generate("126", "서울시 강남구 역삼동 123-45");
            cheerGenerator.generateCommon(member, store1);
            cheerGenerator.generateCommon(member, store2);
            cheerGenerator.generateCommon(member, store3);

            CheerRegisterRequest request = new CheerRegisterRequest("123", "농민백암순대 본점", "추가 응원",
                    List.of(CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM));
            StoreSearchResult result = new StoreSearchResult(
                    "123", StoreCategory.KOREAN, "02-755-5232", "농민백암순대 본점", "http://place.map.kakao.com/123",
                    "서울시 강남구 역삼동 123-45", "서울시 강남구 역삼동 123-45", District.GANGNAM, 37.5665, 126.9780);
            ImageKey imageKey = new ImageKey("image-key");

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> cheerService.registerCheer(request, result, imageKey, member.getId()));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.FULL_CHEER_SIZE_PER_MEMBER);
        }

        @Test
        void 이미_응원한_가게에_대해_응원하면_예외가_발생한다() {
            Member member = memberGenerator.generate("123");
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            cheerGenerator.generateCommon(member, store);

            CheerRegisterRequest request = new CheerRegisterRequest("123", "농민백암순대 본점", "추가 응원",
                    List.of(CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM));
            StoreSearchResult result = new StoreSearchResult(
                    "123", StoreCategory.KOREAN, "02-755-5232", "농민백암순대 본점", "http://place.map.kakao.com/123",
                    "서울시 강남구 역삼동 123-45", "서울시 강남구 역삼동 123-45", District.GANGNAM, 37.5665, 126.9780);
            ImageKey imageKey = new ImageKey("image-key");

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> cheerService.registerCheer(request, result, imageKey, member.getId()));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.ALREADY_CHEERED);
        }

        @Test
        void 해당_응원의_가게가_저장되어_있지_않다면_가게와_응원을_저장한다() {
            Member member = memberGenerator.generate("123");

            CheerRegisterRequest request = new CheerRegisterRequest("123", "농민백암순대 본점", "맛있어요!",
                    List.of(CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM));
            StoreSearchResult result = new StoreSearchResult(
                    "123", StoreCategory.KOREAN, "02-755-5232", "농민백암순대 본점", "http://place.map.kakao.com/123",
                    "서울시 강남구 역삼동 123-45", "서울시 강남구 역삼동 123-45", District.GANGNAM, 37.5665, 126.9780);
            ImageKey imageKey = new ImageKey("image-key");

            CheerResponse response = cheerService.registerCheer(request, result, imageKey, member.getId());

            Store foundStore = storeRepository.findByKakaoId("123").orElseThrow();
            assertAll(
                    () -> assertThat(response.storeId()).isEqualTo(foundStore.getId()),
                    () -> assertThat(response.cheerDescription()).isEqualTo("맛있어요!"),
                    () -> assertThat(response.imageUrl()).isNotNull(),
                    () -> assertThat(response.tags()).containsExactlyInAnyOrder(
                            CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM)
            );
        }

        @Test
        void 해당_응원의_가게가_저장되어_있다면_응원만_저장한다() {
            Member member = memberGenerator.generate("123");
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");

            CheerRegisterRequest request = new CheerRegisterRequest("123", "농민백암순대 본점", "맛있어요!",
                    List.of(CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM));
            StoreSearchResult result = new StoreSearchResult(
                    "123", StoreCategory.KOREAN, "02-755-5232", "농민백암순대 본점", "http://place.map.kakao.com/123",
                    "서울시 강남구 역삼동 123-45", "서울시 강남구 역삼동 123-45", District.GANGNAM, 37.5665, 126.9780);
            ImageKey imageKey = new ImageKey("image-key");

            CheerResponse response = cheerService.registerCheer(request, result, imageKey, member.getId());

            Store foundStore = storeRepository.findByKakaoId("123").orElseThrow();
            assertAll(
                    () -> assertThat(foundStore.getId()).isEqualTo(store.getId()),
                    () -> assertThat(response.storeId()).isEqualTo(foundStore.getId()),
                    () -> assertThat(response.cheerDescription()).isEqualTo("맛있어요!"),
                    () -> assertThat(response.imageUrl()).isNotNull(),
                    () -> assertThat(response.tags()).containsExactlyInAnyOrder(
                            CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM)
            );
        }

        @Test
        void 해당_응원의_이미지가_비어있어도_응원을_저장할_수_있다() {
            Member member = memberGenerator.generate("123");

            CheerRegisterRequest request = new CheerRegisterRequest("123", "농민백암순대 본점", "맛있어요!",
                    List.of(CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM));
            StoreSearchResult result = new StoreSearchResult(
                    "123", StoreCategory.KOREAN, "02-755-5232", "농민백암순대 본점", "http://place.map.kakao.com/123",
                    "서울시 강남구 역삼동 123-45", "서울시 강남구 역삼동 123-45", District.GANGNAM, 37.5665, 126.9780);
            ImageKey imageKey = new ImageKey(null);

            CheerResponse response = cheerService.registerCheer(request, result, imageKey, member.getId());

            Store foundStore = storeRepository.findByKakaoId("123").orElseThrow();
            assertAll(
                    () -> assertThat(response.storeId()).isEqualTo(foundStore.getId()),
                    () -> assertThat(response.cheerDescription()).isEqualTo("맛있어요!"),
                    () -> assertThat(response.imageUrl()).isNull(),
                    () -> assertThat(response.tags()).containsExactlyInAnyOrder(
                            CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM)
            );
        }
    }

    @Nested
    class GetCheers {

        @Test
        void 요청한_응원_개수만큼_응원을_최신순으로_반환한다() {
            Member member = memberGenerator.generate("123");
            Store store1 = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            Store store2 = storeGenerator.generate("456", "서울시 성북구 석관동 123-45");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Cheer cheer1 = cheerGenerator.generateAdmin(member, store1, startAt);
            Cheer cheer2 = cheerGenerator.generateAdmin(member, store1, startAt.plusHours(1));
            Cheer cheer3 = cheerGenerator.generateAdmin(member, store2, startAt.plusHours(2));
            int page = 0;
            int size = 2;

            CheersResponse response = cheerService.getCheers(page, size);

            assertAll(
                    () -> assertThat(response.cheers()).hasSize(2),
                    () -> assertThat(response.cheers().get(0).cheerId()).isEqualTo(cheer3.getId()),
                    () -> assertThat(response.cheers().get(1).cheerId()).isEqualTo(cheer2.getId())
            );
        }

        @Test
        void 요청한_응원을_페이지네이션하여_응원을_최신순으로_반환한다() {
            Member member = memberGenerator.generate("123");
            Store store1 = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            Store store2 = storeGenerator.generate("456", "서울시 성북구 석관동 123-45");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Cheer cheer1 = cheerGenerator.generateAdmin(member, store1, startAt);
            Cheer cheer2 = cheerGenerator.generateAdmin(member, store1, startAt.plusHours(1));
            Cheer cheer3 = cheerGenerator.generateAdmin(member, store2, startAt.plusHours(2));
            int page = 1;
            int size = 2;

            CheersResponse response = cheerService.getCheers(page, size);

            assertAll(
                    () -> assertThat(response.cheers()).hasSize(1),
                    () -> assertThat(response.cheers().get(0).cheerId()).isEqualTo(cheer1.getId())
            );
        }
    }

    @Nested
    class GetCheersByStoreId {

        @Test
        void 요청한_가게의_응원을_최신순으로_반환한다() throws InterruptedException {
            Member member1 = memberGenerator.generateRegisteredMember("123", "a@gmail.com", "1234", "01012341234");
            Member member2 = memberGenerator.generateRegisteredMember("124", "b@gmail.com", "1235", "01012341235");
            Member member3 = memberGenerator.generateRegisteredMember("125", "c@gmail.com", "1236", "01012341236");
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            Cheer cheer1 = cheerGenerator.generateCommon(member1, store);
            Thread.sleep(5);
            Cheer cheer2 = cheerGenerator.generateCommon(member2, store);
            Thread.sleep(5);
            Cheer cheer3 = cheerGenerator.generateCommon(member3, store);
            int page = 0;
            int size = 2;

            CheersInStoreResponse response = cheerService.getCheersByStoreId(store.getId(), page, size);

            assertAll(
                    () -> assertThat(response.cheers()).hasSize(2),
                    () -> assertThat(response.cheers().get(0).id()).isEqualTo(cheer3.getId()),
                    () -> assertThat(response.cheers().get(1).id()).isEqualTo(cheer2.getId())
            );
        }

        @Test
        void 요청한_가게의_응원을_페이지네이션하여_최신순으로_반환한다() throws InterruptedException {
            Member member1 = memberGenerator.generateRegisteredMember("123", "a@gmail.com", "1234", "01012341234");
            Member member2 = memberGenerator.generateRegisteredMember("124", "b@gmail.com", "1235", "01012341235");
            Member member3 = memberGenerator.generateRegisteredMember("125", "c@gmail.com", "1236", "01012341236");
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            Cheer cheer1 = cheerGenerator.generateCommon(member1, store);
            Thread.sleep(5);
            Cheer cheer2 = cheerGenerator.generateCommon(member2, store);
            Thread.sleep(5);
            Cheer cheer3 = cheerGenerator.generateCommon(member3, store);
            int page = 1;
            int size = 2;

            CheersInStoreResponse response = cheerService.getCheersByStoreId(store.getId(), page, size);

            assertAll(
                    () -> assertThat(response.cheers()).hasSize(1),
                    () -> assertThat(response.cheers().get(0).id()).isEqualTo(cheer1.getId())
            );
        }
    }
}
