package eatda.controller.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.controller.BaseControllerTest;
import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTagName;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class StoreControllerTest extends BaseControllerTest {

    @Nested
    class GetStore {

        @Test
        void 음식점_정보를_조회한다() {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            cheerGenerator.generateCommon(member, store);

            StoreResponse response = given()
                    .pathParam("storeId", store.getId())
                    .when()
                    .get("/api/shops/{storeId}")
                    .then()
                    .statusCode(200)
                    .extract().as(StoreResponse.class);

            assertAll(
                    () -> assertThat(response.id()).isEqualTo(store.getId()),
                    () -> assertThat(response.name()).isEqualTo(store.getName()),
                    () -> assertThat(response.district()).isEqualTo("강남구"),
                    () -> assertThat(response.neighborhood()).isEqualTo("대치동")
            );
        }
    }

    @Nested
    class GetStores {

        @Test
        void 모든_카테고리의_음식점_목록을_최신순으로_조회한다() {
            Member member = memberGenerator.generate("111");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Store store1 = storeGenerator.generate("112", "서울 강남구 대치동 896-33", StoreCategory.KOREAN, startAt);
            Store store2 = storeGenerator.generate("113", "서울 성북구 석관동 123-45", StoreCategory.OTHER,
                    startAt.plusHours(1));
            Store store3 = storeGenerator.generate("114", "서울 강남구 역삼동 678-90", StoreCategory.KOREAN,
                    startAt.plusHours(2));
            cheerGenerator.generateCommon(member, store1);
            cheerGenerator.generateCommon(member, store2);
            cheerGenerator.generateCommon(member, store3);

            StoresResponse response = given()
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get("/api/shops")
                    .then()
                    .statusCode(200)
                    .extract().as(StoresResponse.class);

            assertAll(
                    () -> assertThat(response.stores()).hasSize(2),
                    () -> assertThat(response.stores().get(0).id()).isEqualTo(store3.getId()),
                    () -> assertThat(response.stores().get(1).id()).isEqualTo(store2.getId())
            );
        }

        @Test
        void 음식점_목록을_필터링하여_최신순으로_조회한다() {
            Member member = memberGenerator.generate("111");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            Store store1 = storeGenerator.generate("112", "서울 강남구 대치동 896-33", StoreCategory.CAFE,
                    startAt);
            Store store2 = storeGenerator.generate("113", "서울 성북구 석관동 123-45", StoreCategory.OTHER,
                    startAt.plusHours(1));
            Store store3 = storeGenerator.generate("114", "서울 강남구 역삼동 678-90", StoreCategory.CAFE,
                    startAt.plusHours(2));
            Cheer cheer1 = cheerGenerator.generateCommon(member, store1);
            Cheer cheer2 = cheerGenerator.generateCommon(member, store2);
            Cheer cheer3 = cheerGenerator.generateCommon(member, store3);
            cheerTagGenerator.generate(cheer1, List.of(CheerTagName.INSTAGRAMMABLE, CheerTagName.ENERGETIC));
            cheerTagGenerator.generate(cheer3, List.of(CheerTagName.INSTAGRAMMABLE, CheerTagName.CLEAN_RESTROOM));

            StoresResponse response = given()
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .queryParam("category", StoreCategory.CAFE)
                    .queryParam("tag", CheerTagName.INSTAGRAMMABLE)
                    .queryParam("location", "")
                    .when()
                    .get("/api/shops")
                    .then()
                    .statusCode(200)
                    .extract().as(StoresResponse.class);

            assertAll(
                    () -> assertThat(response.stores()).hasSize(2),
                    () -> assertThat(response.stores().get(0).id()).isEqualTo(store3.getId()),
                    () -> assertThat(response.stores().get(1).id()).isEqualTo(store1.getId())
            );
        }
    }

    @Nested
    class GetStoreImages {

        @Test
        void 음식점_이미지들을_조회한다() {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            Cheer cheer = cheerGenerator.generateCommon(member, store);

            cheerImageGenerator.generate(cheer, "image1.png", 1L);
            cheerImageGenerator.generate(cheer, "image2.png", 2L);
            cheerImageGenerator.generate(cheer, "image3.png", 3L);

            ImagesResponse response = given()
                    .when()
                    .get("/api/shops/{storeId}/images", store.getId())
                    .then()
                    .statusCode(200)
                    .extract().as(ImagesResponse.class);

            assertThat(response.imageUrls()).hasSize(3);
        }

        @Test
        void 음식점_이미지가_없다면_빈_리스트를_반환한다() {
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");

            ImagesResponse response = given()
                    .when()
                    .get("/api/shops/{storeId}/images", store.getId())
                    .then()
                    .statusCode(200)
                    .extract().as(ImagesResponse.class);

            assertThat(response.imageUrls()).isEmpty();
        }
    }


    @Nested
    class GetStoreTags {

        @Test
        void 음식점_태그들을_조회한다() {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            Cheer cheer = cheerGenerator.generateCommon(member, store);
            cheerTagGenerator.generate(cheer, List.of(CheerTagName.INSTAGRAMMABLE, CheerTagName.CLEAN_RESTROOM));

            TagsResponse response = given()
                    .when()
                    .contentType(ContentType.JSON)
                    .get("/api/shops/{storeId}/tags", store.getId())
                    .then()
                    .statusCode(200)
                    .extract().as(TagsResponse.class);

            assertThat(response.tags())
                    .containsExactlyInAnyOrder(CheerTagName.INSTAGRAMMABLE, CheerTagName.CLEAN_RESTROOM);
        }
    }

    @Nested
    class GetStoresByCheeredMember {

        @Test
        void 회원이_응원한_음식점_목록을_조회한다() {
            Member member = memberGenerator.generate("111");
            Store store1 = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            Store store2 = storeGenerator.generate("홍콩반점", "서울 강남구 역삼동 123-45");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 26, 1, 0, 0);
            cheerGenerator.generate(member, store1, startAt);
            cheerGenerator.generate(member, store2, startAt.plusHours(1));

            StoresInMemberResponse response = given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken(member))
                    .when()
                    .get("/api/shops/cheered-member")
                    .then()
                    .statusCode(200)
                    .extract().as(StoresInMemberResponse.class);

            assertAll(
                    () -> assertThat(response.stores()).hasSize(2),
                    () -> assertThat(response.stores().get(0).id()).isEqualTo(store2.getId()),
                    () -> assertThat(response.stores().get(1).id()).isEqualTo(store1.getId())
            );
        }
    }

    @Nested
    class SearchStores {

        @Test
        void 음식점_검색_결과를_반환한다() {
            String query = "농민백암순대";

            StoreSearchResponses responses = given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("query", query)
                    .when()
                    .get("/api/shop/search")
                    .then()
                    .statusCode(200)
                    .extract().as(StoreSearchResponses.class);

            assertThat(responses.stores()).isNotEmpty();
        }
    }

}
