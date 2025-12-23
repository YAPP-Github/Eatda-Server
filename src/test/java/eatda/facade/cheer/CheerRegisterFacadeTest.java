package eatda.facade.cheer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import eatda.controller.cheer.CheerRegisterRequest;
import eatda.controller.cheer.CheerResponse;
import eatda.domain.ImageDomain;
import eatda.domain.cheer.CheerTagName;
import eatda.domain.store.District;
import eatda.domain.store.StoreCategory;
import eatda.domain.store.StoreSearchResult;
import eatda.facade.BaseFacadeTest;
import eatda.facade.CheerRegisterFacade;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import software.amazon.awssdk.core.exception.SdkException;

class CheerRegisterFacadeTest extends BaseFacadeTest {

    @Autowired
    private CheerRegisterFacade cheerRegisterFacade;

    @Nested
    class RegisterCheer {

        @Test
        void 응원을_등록하면_이미지를_이동하고_최종_응답을_반환한다() {
            var member = memberGenerator.generate("member-1");

            CheerRegisterRequest request = getRegisterRequest();

            StoreSearchResult storeResult = new StoreSearchResult(
                    "kakao-1",
                    StoreCategory.KOREAN,
                    "02-000-0000",
                    "농민백암순대",
                    "http://place.map.kakao.com/1",
                    "서울시 강남구",
                    "서울시 강남구",
                    District.GANGNAM,
                    37.715132,
                    127.269310
            );

            given(fileClient.moveTempFilesToPermanent(
                    eq(ImageDomain.CHEER.getName()),
                    anyLong(),
                    anyList()
            )).willReturn(List.of(
                    "cheer/1/key1.jpg",
                    "cheer/1/key2.jpg"
            ));

            CheerResponse response = cheerRegisterFacade.registerCheer(
                    request,
                    storeResult,
                    member.getId(),
                    ImageDomain.CHEER
            );

            assertThat(response.cheerDescription()).isEqualTo("맛있어요");
            assertThat(response.tags()).containsExactly(CheerTagName.GOOD_FOR_DATING);
            assertThat(response.images()).hasSize(2);

            verify(fileClient)
                    .moveTempFilesToPermanent(
                            eq(ImageDomain.CHEER.getName()),
                            anyLong(),
                            anyList()
                    );
        }

        @Test
        void 이미지_이동_중_실패하면_응원을_삭제한다() {
            var member = memberGenerator.generate("member-1");

            CheerRegisterRequest.UploadedImageDetail image =
                    new CheerRegisterRequest.UploadedImageDetail(
                            "temp/key1.jpg", 1L, "image/jpeg", 1000L
                    );

            CheerRegisterRequest request = new CheerRegisterRequest(
                    "kakao-1",
                    "농민백암순대",
                    "맛있어요",
                    List.of(image),
                    List.of(CheerTagName.GOOD_FOR_DATING)
            );

            StoreSearchResult storeResult = new StoreSearchResult(
                    "kakao-1",
                    StoreCategory.KOREAN,
                    "02-000-0000",
                    "농민백암순대",
                    "http://place.map.kakao.com/1",
                    "서울시 강남구",
                    "서울시 강남구",
                    District.GANGNAM,
                    37.715132,
                    127.269310
            );

            given(fileClient.moveTempFilesToPermanent(
                    anyString(),
                    anyLong(),
                    anyList()
            )).willThrow(
                    SdkException.builder().build()
            );

            assertThrows(SdkException.class, () ->
                    cheerRegisterFacade.registerCheer(
                            request,
                            storeResult,
                            member.getId(),
                            ImageDomain.CHEER
                    )
            );

            assertThat(cheerRepository.count()).isZero();
        }

        @Test
        void 이미지_이동이_부분적으로_성공한_후_실패하면_응원을_삭제한다() {
            var member = memberGenerator.generate("member-1");

            CheerRegisterRequest request = getCheerRegisterRequest();

            StoreSearchResult storeResult = new StoreSearchResult(
                    "kakao-1",
                    StoreCategory.KOREAN,
                    "02-000-0000",
                    "농민백암순대",
                    "http://place.map.kakao.com/1",
                    "서울시 강남구",
                    "서울시 강남구",
                    District.GANGNAM,
                    37.715132,
                    127.269310
            );

            given(fileClient.moveTempFilesToPermanent(
                    eq(ImageDomain.CHEER.getName()),
                    anyLong(),
                    anyList()
            )).willAnswer(invocation -> {
                throw SdkException.builder().build();
            });

            assertThrows(SdkException.class, () ->
                    cheerRegisterFacade.registerCheer(
                            request,
                            storeResult,
                            member.getId(),
                            ImageDomain.CHEER
                    )
            );

            assertThat(cheerRepository.count())
                    .as("부분 성공 후 실패 시 Cheer는 삭제되어야 한다.")
                    .isZero();
        }

        @Test
        void 이미지_이동은_성공했으나_DB_저장에_실패하면_파일과_응원_모두_삭제한다() {
            var member = memberGenerator.generate("member-1");
            String tooLongContentType = "a".repeat(300);

            CheerRegisterRequest request = new CheerRegisterRequest(
                    "kakao-1", "농민백암순대", "맛있어요",
                    List.of(new CheerRegisterRequest.UploadedImageDetail(
                            "temp/key1.jpg",
                            1L,
                            tooLongContentType,
                            1000L
                    )),
                    List.of(CheerTagName.GOOD_FOR_DATING)
            );

            StoreSearchResult storeResult = new StoreSearchResult(
                    "kakao-1", StoreCategory.KOREAN, "02-000-0000", "농민백암순대",
                    "http://place.map.kakao.com/1", "서울시 강남구", "서울시 강남구",
                    District.GANGNAM, 37.715132, 127.269310
            );

            List<String> movedKeys = List.of("cheer/1/key1.jpg");
            given(fileClient.moveTempFilesToPermanent(anyString(), anyLong(), anyList()))
                    .willReturn(movedKeys);

            assertThrows(Exception.class, () ->
                    cheerRegisterFacade.registerCheer(
                            request,
                            storeResult,
                            member.getId(),
                            ImageDomain.CHEER
                    )
            );

            assertThat(cheerRepository.count())
                    .as("DB 에러(컬럼 길이 초과) 발생 시 응원글은 삭제되어야 한다.")
                    .isZero();

            verify(fileClient).deleteFiles(movedKeys);

        }

        @Test
        void 이미지가_없어도_응원은_정상_등록된다() {
            var member = memberGenerator.generate("member-1");

            CheerRegisterRequest request = new CheerRegisterRequest(
                    "kakao-1",
                    "농민백암순대",
                    "이미지 없음",
                    List.of(),
                    List.of(CheerTagName.GOOD_FOR_DATING)
            );

            StoreSearchResult storeResult = new StoreSearchResult(
                    "kakao-1",
                    StoreCategory.KOREAN,
                    "02-000-0000",
                    "농민백암순대",
                    "http://place.map.kakao.com/1",
                    "서울시 강남구",
                    "서울시 강남구",
                    District.GANGNAM,
                    37.715132,
                    127.269310
            );

            CheerResponse response = cheerRegisterFacade.registerCheer(
                    request,
                    storeResult,
                    member.getId(),
                    ImageDomain.CHEER
            );

            assertThat(response.images()).isEmpty();
            assertThat(cheerRepository.count()).isEqualTo(1);

            verify(fileClient, Mockito.never())
                    .moveTempFilesToPermanent(anyString(), anyLong(), anyList());
        }

        @NonNull
        private CheerRegisterRequest getRegisterRequest() {
            CheerRegisterRequest.UploadedImageDetail image1 =
                    new CheerRegisterRequest.UploadedImageDetail("temp/key1.jpg", 1L, "image/jpeg", 1000L);
            CheerRegisterRequest.UploadedImageDetail image2 =
                    new CheerRegisterRequest.UploadedImageDetail("temp/key2.jpg", 2L, "image/jpeg", 2000L);

            return new CheerRegisterRequest(
                    "kakao-1",
                    "농민백암순대",
                    "맛있어요",
                    List.of(image1, image2),
                    List.of(CheerTagName.GOOD_FOR_DATING)
            );
        }

        @NonNull
        private CheerRegisterRequest getCheerRegisterRequest() {
            CheerRegisterRequest.UploadedImageDetail image1 =
                    new CheerRegisterRequest.UploadedImageDetail(
                            "temp/key1.jpg", 1L, "image/jpeg", 1000L
                    );
            CheerRegisterRequest.UploadedImageDetail image2 =
                    new CheerRegisterRequest.UploadedImageDetail(
                            "temp/key2.jpg", 2L, "image/jpeg", 1000L
                    );
            CheerRegisterRequest.UploadedImageDetail image3 =
                    new CheerRegisterRequest.UploadedImageDetail(
                            "temp/key3.jpg", 3L, "image/jpeg", 1000L
                    );

            return new CheerRegisterRequest(
                    "kakao-1",
                    "농민백암순대",
                    "부분 성공 테스트",
                    List.of(image1, image2, image3),
                    List.of(CheerTagName.GOOD_FOR_DATING)
            );
        }
    }
}
