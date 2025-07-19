package eatda.service.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import eatda.client.map.StoreSearchResult;
import eatda.controller.store.CheerRegisterRequest;
import eatda.controller.store.CheerResponse;
import eatda.controller.store.CheersResponse;
import eatda.domain.member.Member;
import eatda.domain.store.Cheer;
import eatda.domain.store.Store;
import eatda.service.BaseServiceTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class CheerServiceTest extends BaseServiceTest {

    @Autowired
    private CheerService cheerService;

    @Nested
    class RegisterCheer {

        @BeforeEach
        void mockingImage() {
            List<StoreSearchResult> searchResults = List.of(
                    new StoreSearchResult("123", "FD6", "음식점 > 한식 > 국밥", "010-1234-1234", "농민백암순대 본점",
                            "https://yapp.co.kr",
                            "서울시 강남구 역삼동 123-45", "서울 강남구 선릉로86길 40-4", 37.5d, 127.0d),
                    new StoreSearchResult("456", "FD6", "음식점 > 한식 > 국밥", "010-1234-1234", "농민백암순대 시청점",
                            "http://yapp.kr",
                            "서울 중구 북창동 19-4", null, 37.5d, 127.0d)
            );
            doReturn(searchResults).when(mapClient).searchShops(anyString());
        }

        @Test
        void 해당_응원의_가게가_저장되어_있지_않다면_가게와_응원을_저장한다() {
            Member member = memberGenerator.generate("123");
            CheerRegisterRequest request = new CheerRegisterRequest("123", "농민백암순대 본점", "맛있어요!");
            MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "image content".getBytes());

            CheerResponse response = cheerService.registerCheer(request, image, member.getId());

            Store foundStore = storeRepository.findByKakaoId("123").orElseThrow();
            assertAll(
                    () -> assertThat(response.storeId()).isEqualTo(foundStore.getId()),
                    () -> assertThat(response.cheerDescription()).isEqualTo("맛있어요!"),
                    () -> assertThat(response.imageUrl()).isNotNull()
            );
        }

        @Test
        void 해당_응원의_가게가_저장되어_있다면_응원만_저장한다() {
            Member member = memberGenerator.generate("123");
            Store store = storeGenerator.generate("123", "서울시 강남구 역삼동 123-45");
            CheerRegisterRequest request = new CheerRegisterRequest("123", "농민백암순대 본점", "맛있어요!");
            MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "image content".getBytes());

            CheerResponse response = cheerService.registerCheer(request, image, member.getId());

            Store foundStore = storeRepository.findByKakaoId("123").orElseThrow();
            assertAll(
                    () -> assertThat(foundStore.getId()).isEqualTo(store.getId()),
                    () -> assertThat(response.storeId()).isEqualTo(foundStore.getId()),
                    () -> assertThat(response.cheerDescription()).isEqualTo("맛있어요!"),
                    () -> assertThat(response.imageUrl()).isNotNull()
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
            Cheer cheer1 = cheerGenerator.generateAdmin(member, store1);
            Cheer cheer2 = cheerGenerator.generateAdmin(member, store1);
            Cheer cheer3 = cheerGenerator.generateAdmin(member, store2);

            CheersResponse response = cheerService.getCheers(2);

            assertAll(
                    () -> assertThat(response.cheers()).hasSize(2),
                    () -> assertThat(response.cheers().get(0).cheerId()).isEqualTo(cheer3.getId()),
                    () -> assertThat(response.cheers().get(1).cheerId()).isEqualTo(cheer2.getId())
            );
        }
    }
}
