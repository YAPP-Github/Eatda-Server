package eatda.controller.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.controller.BaseControllerTest;
import eatda.domain.member.Member;
import eatda.domain.store.Cheer;
import eatda.domain.store.Store;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheerControllerTest extends BaseControllerTest {

    @Nested
    class GetCheers {

        @Test
        void 요청한_응원_중_최신_응원_N개를_조회한다() {
            Member member = memberGenerator.generateRegisteredMember("nickname", "ac@kakao.com", "123", "01011111111");
            Store store1 = storeGenerator.generate("111", "서울시 노원구 월계3동 123-45");
            Store store2 = storeGenerator.generate("222", "서울시 성북구 석관동 123-45");
            Cheer cheer1 = cheerGenerator.generateAdmin(member, store1);
            Cheer cheer2 = cheerGenerator.generateAdmin(member, store1);
            Cheer cheer3 = cheerGenerator.generateAdmin(member, store2);

            CheersResponse response = given()
                    .when()
                    .queryParam("size", 2)
                    .get("/api/cheer")
                    .then()
                    .statusCode(200)
                    .extract().as(CheersResponse.class);

            CheerPreviewResponse firstResponse = response.cheers().get(0);
            assertAll(
                    () -> assertThat(response.cheers()).hasSize(2),
                    () -> assertThat(firstResponse.storeId()).isEqualTo(store2.getId()),
                    () -> assertThat(firstResponse.storeDistrict()).isEqualTo("성북구"),
                    () -> assertThat(firstResponse.storeNeighborhood()).isEqualTo("석관동"),
                    () -> assertThat(firstResponse.cheerId()).isEqualTo(cheer3.getId())
            );
        }
    }
}
