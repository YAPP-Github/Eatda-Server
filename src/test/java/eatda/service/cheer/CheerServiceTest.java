package eatda.service.cheer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import eatda.client.file.FileClient;
import eatda.controller.cheer.CheerImageResponse;
import eatda.controller.cheer.CheerRegisterRequest;
import eatda.controller.cheer.CheerRegisterRequest.UploadedImageDetail;
import eatda.controller.cheer.CheerResponse;
import eatda.controller.cheer.CheersInStoreResponse;
import eatda.controller.cheer.CheersResponse;
import eatda.domain.ImageDomain;
import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerImage;
import eatda.domain.member.Member;
import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.domain.store.StoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.fixture.CheerImageGenerator;
import eatda.repository.cheer.CheerRepository;
import eatda.service.BaseServiceTest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class CheerServiceTest extends BaseServiceTest {

    @Autowired
    private CheerService cheerService;

    @Autowired
    private CheerRepository cheerRepository;

    @Autowired
    private CheerImageGenerator cheerImageGenerator;

    @MockBean
    private FileClient fileClient;

    @BeforeEach
    void setUp() {
        cheerRepository.deleteAll();
    }

    @Nested
    class RegisterCheer {

        private Member member;
        private StoreSearchResult storeSearchResult;

        @BeforeEach
        void setUp() {
            member = memberGenerator.generate("123");
            storeSearchResult = new StoreSearchResult(
                    "123", StoreCategory.KOREAN, "02-755-5232", "농민백암순대 본점", "http://place.map.kakao.com/123",
                    "서울시 강남구 역삼동 123-45",
                    "서울시 강남구 사사로 3길 12-24",
                    District.GANGNAM, 37.5665, 126.9780);
        }

        @Test
        void 응원_개수가_최대_개수를_초과하면_예외가_발생한다() {
            Store store1 = storeGenerator.generate("124", "서울시 강남구 역삼동 123-45");
            Store store2 = storeGenerator.generate("125", "서울시 강남구 역삼동 123-45");
            Store store3 = storeGenerator.generate("126", "서울시 강남구 역삼동 123-45");
            cheerGenerator.generateCommon(member, store1);
            cheerGenerator.generateCommon(member, store2);
            cheerGenerator.generateCommon(member, store3);
            CheerRegisterRequest request = new CheerRegisterRequest("농민백암순대 본점", "123", "추가 응원", List.of());

            assertThatThrownBy(() -> cheerService.registerCheer(request, storeSearchResult, member.getId(), ImageDomain.CHEER))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(BusinessErrorCode.FULL_CHEER_SIZE_PER_MEMBER.getMessage());
        }

        @Test
        void 이미_응원한_가게에_대해_응원하면_예외가_발생한다() {
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            cheerGenerator.generateCommon(member, store);
            CheerRegisterRequest request = new CheerRegisterRequest("농민백암순대 본점", "123", "추가 응원", List.of());

            assertThatThrownBy(() -> cheerService.registerCheer(request, storeSearchResult, member.getId(), ImageDomain.CHEER))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(BusinessErrorCode.ALREADY_CHEERED.getMessage());
        }

        @Test
        void 해당_응원의_가게가_저장되어_있지_않다면_가게와_응원을_저장한다() {
            CheerRegisterRequest request = new CheerRegisterRequest("농민백암순대 본점", "123", "맛있어요!", List.of());

            CheerResponse response = cheerService.registerCheer(request, storeSearchResult, member.getId(), ImageDomain.CHEER);

            Store foundStore = storeRepository.findByKakaoId("123").orElseThrow();
            assertAll(
                    () -> assertThat(response.storeId()).isEqualTo(foundStore.getId()),
                    () -> assertThat(response.cheerDescription()).isEqualTo("맛있어요!"),
                    () -> assertThat(cheerRepository.count()).isEqualTo(1)
            );
        }

        @Test
        void 해당_응원의_가게가_저장되어_있다면_응원만_저장한다() {
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            long initialStoreCount = storeRepository.count();
            CheerRegisterRequest request = new CheerRegisterRequest("농민백암순대 본점", "123", "맛있어요!", List.of());

            CheerResponse response = cheerService.registerCheer(request, storeSearchResult, member.getId(), ImageDomain.CHEER);

            assertAll(
                    () -> assertThat(storeRepository.count()).isEqualTo(initialStoreCount),
                    () -> assertThat(response.storeId()).isEqualTo(store.getId()),
                    () -> assertThat(response.cheerDescription()).isEqualTo("맛있어요!"),
                    () -> assertThat(cheerRepository.count()).isEqualTo(1)
            );
        }

        @Test
        @Transactional
        void 이미지를_포함한_응원을_등록할_수_있다() {
            UploadedImageDetail image2 = new UploadedImageDetail("temp-key-2", 2L, "image/jpeg", 2000L);
            UploadedImageDetail image1 = new UploadedImageDetail("temp-key-1", 1L, "image/jpeg", 1000L);
            CheerRegisterRequest request = new CheerRegisterRequest("농민백암순대 본점", "123", "맛있어요!", List.of(image2, image1));
            List<String> permanentKeys = List.of("permanent/path/1", "permanent/path/2");
            given(fileClient.moveTempFilesToPermanent(any(String.class), anyLong(), anyList()))
                    .willReturn(permanentKeys);

            CheerResponse response = cheerService.registerCheer(request, storeSearchResult, member.getId(), ImageDomain.CHEER);

            Cheer savedCheer = cheerRepository.findById(response.cheerId()).orElseThrow();
            assertAll(
                    () -> assertThat(response.images()).hasSize(2),
                    () -> assertThat(response.images()).isSortedAccordingTo(Comparator.comparingLong(CheerImageResponse::orderIndex)),
                    () -> assertThat(savedCheer.getImages()).extracting(CheerImage::getImageKey).containsExactlyElementsOf(permanentKeys)
            );
        }
    }

    @Nested
    class GetCheers {

        @Test
        void 요청한_응원을_페이지네이션하여_응원을_최신순으로_반환한다() {
            Member member = memberGenerator.generate("123");
            Store store1 = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            Store store2 = storeGenerator.generate("456", "서울시 성북구 석관동 123-45");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Cheer cheer1 = cheerGenerator.generateAdmin(member, store1, startAt);
            cheerGenerator.generateAdmin(member, store1, startAt.plusHours(1));
            cheerGenerator.generateAdmin(member, store2, startAt.plusHours(2));

            CheersResponse response = cheerService.getCheers(1, 2);

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

            CheersResponse response = cheerService.getCheers(0, 1);

            assertThat(response.cheers()).hasSize(1);
            assertThat(response.cheers().get(0).images()).hasSize(2)
                    .isSortedAccordingTo(Comparator.comparingLong(CheerImageResponse::orderIndex));
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

            CheersInStoreResponse response = cheerService.getCheersByStoreId(store.getId(), 1, 2);

            assertAll(
                    () -> assertThat(response.cheers()).hasSize(1),
                    () -> assertThat(response.cheers().get(0).id()).isEqualTo(cheer1.getId())
            );
        }
    }
}
