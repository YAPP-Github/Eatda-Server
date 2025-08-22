package eatda.controller.store;

import eatda.domain.store.District;
import java.util.List;
import lombok.Getter;

@Getter
public enum SearchDistrict {

    GANGNAM("강남/역삼/선릉", List.of(District.GANGNAM)),
    KONDAE("건대/성수/서울숲/왕십리", List.of(District.SEONGDONG, District.GWANGJIN)),
    GEUMHO("금호/옥수/신당", List.of(District.JUNG)),
    HAPJEONG("합정/망원/홍대", List.of(District.MAPO)),
    SINCHON("신촌/이대", List.of(District.SEODAEMUN)),
    MYEONGDONG("명동/을지로/충무로", List.of(District.DONGDAEMUN, District.SEONGBUK)),
    SEOCHON("서촌/북촌/삼청", List.of(District.JONGNO)),
    DAECHI("대치/논현/서초", List.of(District.SEOCHO)),
    YONGSAN("용산/이태원/한남", List.of(District.YONGSAN, District.DONGJAK)),
    GEUMCHEON("금천/도봉/노원", List.of(District.GEUMCHEON, District.DOBONG, District.NOWON)),
    YEONGDEUNGPO("영등포/여의도", List.of(District.YEONGDEUNGPO)),
    JAMSIL("잠실/송파", List.of(District.SONGPA)),
    JONGRO("종로/광화문", List.of(District.JONGNO)),
    MAGOK("마곡/목동/강서", List.of(District.GANGSEO, District.YANGCHEON)),
    GURO("구로/서울대입구", List.of(District.GURO, District.GWANAK)),
    ;

    private String displayName;
    private List<District> districts;

    SearchDistrict(String displayName, List<District> districts) {
        this.displayName = displayName;
        this.districts = districts;
    }
}
