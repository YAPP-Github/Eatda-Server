package eatda.domain.store;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DistrictTest {

    @Nested
    class FromName {

        @CsvSource({"강남구, GANGNAM", "강동구, GANGDONG", "강북구, GANGBUK", "강서구, GANGSEO", "관악구, GWANAK",
                "광진구, GWANGJIN", "구로구, GURO", "금천구, GEUMCHEON", "노원구, NOWON", "도봉구, DOBONG",
                "동대문구, DONGDAEMUN", "동작구, DONGJAK", "마포구, MAPO", "서대문구, SEODAEMUN", "서초구, SEOCHO",
                "성동구, SEONGDONG", "성북구, SEONGBUK", "송파구, SONGPA", "양천구, YANGCHEON", "영등포구, YEONGDEUNGPO",
                "용산구, YONGSAN", "은평구, EUNPYEONG", "종로구, JONGNO", "중구, JUNG", "중랑구, JUNGNANG"})
        @ParameterizedTest
        void 구_이름을_통해_해당_구를_반환한다(String name, District expected) {
            District actual = District.fromName(name);

            assertThat(actual).isEqualTo(expected);
        }

        @ValueSource(strings = {"서구", "사사구", "기구", ""})
        @ParameterizedTest
        void 구_이름이_존재하지_않으면_ETC를_반환한다(String name) {
            District actual = District.fromName(name);

            assertThat(actual).isEqualTo(District.ETC);
        }
    }
}
