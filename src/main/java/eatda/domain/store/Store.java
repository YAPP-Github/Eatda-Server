package eatda.domain.store;

import eatda.domain.AuditingEntity;
import eatda.domain.cheer.Cheer;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "store")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_id", unique = true, nullable = false)
    private String kakaoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private StoreCategory category;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "place_url", nullable = false)
    private String placeUrl;

    @Column(name = "road_address", nullable = false)
    private String roadAddress;

    @Column(name = "lot_number_address", nullable = false)
    private String lotNumberAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "district", nullable = false, length = 31)
    private District district;
    @Embedded
    private Coordinates coordinates;

    /*
    현재는 가게당 평균 응원 수가 5개 이하이므로 BatchSize=10이 적절함.
    데이터 증가를 고려하여 IN 쿼리 한 번당 최대 30개 Store의 Cheer를 로딩하도록 설정.
    향후 응원 수가 증가하거나 Store 리스트 조회 규모가 커질 경우
    성능 모니터링 후 BatchSize 조정 및 Fetch 전략 재검토 필요.
    */
    @OneToMany(mappedBy = "store")
    private List<Cheer> cheers = new ArrayList<>();

    @Builder
    private Store(String kakaoId,
                  StoreCategory category,
                  String phoneNumber,
                  String name,
                  String placeUrl,
                  String roadAddress,
                  String lotNumberAddress,
                  District district,
                  Double latitude,
                  Double longitude) {
        this.kakaoId = kakaoId;
        this.category = category;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.placeUrl = placeUrl;
        this.roadAddress = roadAddress;
        this.district = district;
        this.lotNumberAddress = lotNumberAddress;
        this.coordinates = new Coordinates(latitude, longitude);
    }

    public String getAddressDistrict() {
        return district.getName();
    }

    public String getAddressNeighborhood() {
        String[] addressParts = lotNumberAddress.split(" ");
        if (addressParts.length < 3) {
            return "";
        }
        return addressParts[2];
    }

    public List<String> getCheerDescriptions() {
        return cheers.stream()
                .map(Cheer::getDescription)
                .toList();
    }
}
