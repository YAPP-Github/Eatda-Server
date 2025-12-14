package eatda.service.cheer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.controller.cheer.CheerRegisterRequest;
import eatda.controller.cheer.CheerSearchParameters;
import eatda.controller.store.SearchDistrict;
import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTagName;
import eatda.domain.member.Member;
import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.domain.store.StoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.facade.CheerCreationResult;
import eatda.service.BaseServiceTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CheerServiceTest extends BaseServiceTest {

    @Autowired
    private CheerService cheerService;

    @Nested
    class RegisterCheer {

        @Test
        @Disabled("현재 한시적으로 MAX_CHEER_SIZE=10000 으로 운영 중")
        void 응원_개수가_최대_개수를_초과하면_예외가_발생한다() {
            Member member = memberGenerator.generate("123");
            Store store1 = storeGenerator.generate("124", "서울시 강남구 역삼동 123-45");
            Store store2 = storeGenerator.generate("125", "서울시 강남구 역삼동 123-45");
            Store store3 = storeGenerator.generate("126", "서울시 강남구 역삼동 123-45");
            cheerGenerator.generateCommon(member, store1);
            cheerGenerator.generateCommon(member, store2);
            cheerGenerator.generateCommon(member, store3);

            CheerRegisterRequest request = new CheerRegisterRequest(
                    "123",
                    "농민백암순대 본점",
                    "추가 응원",
                    List.of(),
                    List.of(CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM)
            );

            StoreSearchResult result = storeSearchResult("123");

            assertThatThrownBy(() ->
                    cheerService.createCheer(request, result, member.getId())
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(BusinessErrorCode.FULL_CHEER_SIZE_PER_MEMBER.getMessage());
        }

        @Test
        void 이미_응원한_가게에_대해_응원하면_예외가_발생한다() {
            Member member = memberGenerator.generate("123");
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            cheerGenerator.generateCommon(member, store);

            CheerRegisterRequest request = new CheerRegisterRequest(
                    "123",
                    "농민백암순대 본점",
                    "추가 응원",
                    List.of(),
                    List.of(CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM)
            );

            StoreSearchResult result = storeSearchResult("123");

            assertThatThrownBy(() ->
                    cheerService.createCheer(request, result, member.getId())
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(BusinessErrorCode.ALREADY_CHEERED.getMessage());
        }

        @Test
        void 해당_응원의_가게가_저장되어_있지_않다면_가게와_응원을_저장한다() {
            Member member = memberGenerator.generate("123");

            CheerRegisterRequest request = new CheerRegisterRequest(
                    "123",
                    "농민백암순대 본점",
                    "맛있어요!",
                    List.of(),
                    List.of(CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM)
            );

            StoreSearchResult result = storeSearchResult("123");

            CheerCreationResult creationResult =
                    cheerService.createCheer(request, result, member.getId());

            Cheer cheer = creationResult.cheer();
            Store store = creationResult.store();
            Store foundStore = storeRepository.findByKakaoId("123").orElseThrow();

            assertAll(
                    () -> assertThat(store.getId()).isEqualTo(foundStore.getId()),
                    () -> assertThat(cheer.getDescription()).isEqualTo("맛있어요!"),
                    () -> assertThat(cheerRepository.count()).isEqualTo(1),
                    () -> assertThat(
                            cheer.getCheerTags().getNames()
                    ).containsExactlyInAnyOrder(
                            CheerTagName.GOOD_FOR_DATING,
                            CheerTagName.CLEAN_RESTROOM
                    )
            );
        }

        @Test
        void 해당_응원의_가게가_저장되어_있다면_응원만_저장한다() {
            Member member = memberGenerator.generate("123");
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");

            CheerRegisterRequest request = new CheerRegisterRequest(
                    "123",
                    "농민백암순대 본점",
                    "맛있어요!",
                    List.of(),
                    List.of(CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM)
            );

            StoreSearchResult result = storeSearchResult("123");

            CheerCreationResult creationResult =
                    cheerService.createCheer(request, result, member.getId());

            Cheer cheer = creationResult.cheer();

            assertAll(
                    () -> assertThat(cheer.getStore().getId()).isEqualTo(store.getId()),
                    () -> assertThat(cheerRepository.count()).isEqualTo(1)
            );
        }

        @Test
        void 해당_응원의_이미지가_비어있어도_응원을_저장할_수_있다() {
            Member member = memberGenerator.generate("123");

            CheerRegisterRequest request = new CheerRegisterRequest(
                    "123",
                    "농민백암순대 본점",
                    "맛있어요!",
                    List.of(),
                    List.of(CheerTagName.GOOD_FOR_DATING, CheerTagName.CLEAN_RESTROOM)
            );

            StoreSearchResult result = storeSearchResult("123");

            CheerCreationResult creationResult =
                    cheerService.createCheer(request, result, member.getId());

            assertThat(creationResult.cheer().getDescription()).isEqualTo("맛있어요!");
        }

        @Test
        void 해당_응원의_응원_태그가_비어있어도_응원을_저장할_수_있다() {
            Member member = memberGenerator.generate("123");

            CheerRegisterRequest request = new CheerRegisterRequest(
                    "123",
                    "농민백암순대 본점",
                    "맛있어요!",
                    List.of(),
                    List.of()
            );

            StoreSearchResult result = storeSearchResult("123");

            CheerCreationResult creationResult =
                    cheerService.createCheer(request, result, member.getId());

            assertAll(
                    () -> assertThat(creationResult.cheer().getDescription()).isEqualTo("맛있어요!"),
                    () -> assertThat(creationResult.cheer().getCheerTags().getNames()).isEmpty()
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

            var response = cheerService.getCheers(
                    new CheerSearchParameters(0, 2, null, null, null)
            );

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
            cheerGenerator.generateAdmin(member, store2, startAt.plusHours(2));

            var response = cheerService.getCheers(
                    new CheerSearchParameters(1, 2, null, null, null)
            );

            assertAll(
                    () -> assertThat(response.cheers()).hasSize(1),
                    () -> assertThat(response.cheers().get(0).cheerId()).isEqualTo(cheer1.getId())
            );
        }

        @Test
        void 이미지가_포함된_응원_목록을_조회할_수_있다() {
            Member member = memberGenerator.generate("123");
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            Cheer cheer = cheerGenerator.generateCommon(member, store);
            cheerImageGenerator.generate(cheer, "key2", 2L);
            cheerImageGenerator.generate(cheer, "key1", 1L);

            var response = cheerService.getCheers(
                    new CheerSearchParameters(0, 1, null, null, null)
            );

            assertThat(response.cheers().get(0).images()).hasSize(2);
        }

        @Test
        void 요청한_응원을_지역으로_필터링하여_최신순으로_반환한다() {
            Member member = memberGenerator.generate("123");
            Store store1 = storeGenerator.generate("123", "강남", District.GANGNAM);
            Store store2 = storeGenerator.generate("456", "성북", District.SEONGBUK);
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Cheer cheer1 = cheerGenerator.generateAdmin(member, store1, startAt);
            Cheer cheer2 = cheerGenerator.generateAdmin(member, store1, startAt.plusHours(1));
            cheerGenerator.generateAdmin(member, store2, startAt.plusHours(2));

            var response = cheerService.getCheers(
                    new CheerSearchParameters(
                            0, 2, null, null, List.of(SearchDistrict.GANGNAM)
                    )
            );

            assertAll(
                    () -> assertThat(response.cheers()).hasSize(2),
                    () -> assertThat(response.cheers().get(0).cheerId()).isEqualTo(cheer2.getId()),
                    () -> assertThat(response.cheers().get(1).cheerId()).isEqualTo(cheer1.getId())
            );
        }
    }

    @Nested
    class GetCheersByStoreId {

        @Test
        void 요청한_가게의_응원을_페이지네이션하여_최신순으로_반환한다() {
            Member member = memberGenerator.generate("123");
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);

            Cheer cheer1 = cheerGenerator.generateCommon(member, store, startAt);
            cheerGenerator.generateCommon(member, store, startAt.plusHours(1));
            cheerGenerator.generateCommon(member, store, startAt.plusHours(2));

            var response = cheerService.getCheersByStoreId(store.getId(), 1, 2);

            assertAll(
                    () -> assertThat(response.cheers()).hasSize(1),
                    () -> assertThat(response.cheers().get(0).id()).isEqualTo(cheer1.getId())
            );
        }
    }

    private StoreSearchResult storeSearchResult(String kakaoId) {
        return new StoreSearchResult(
                kakaoId,
                StoreCategory.KOREAN,
                "02-755-5232",
                "농민백암순대 본점",
                "http://place.map.kakao.com/123",
                "서울시 강남구",
                "서울시 강남구",
                District.GANGNAM,
                37.5665,
                126.9780
        );
    }
}
