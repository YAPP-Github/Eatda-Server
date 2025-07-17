package eatda.domain.store;

import eatda.domain.AuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

    @Embedded
    private Coordinates coordinates;

    @Builder
    private Store(String kakaoId,
                  StoreCategory category,
                  String phoneNumber,
                  String name,
                  String placeUrl,
                  String roadAddress,
                  String lotNumberAddress,
                  Double latitude,
                  Double longitude) {
        this.kakaoId = kakaoId;
        this.category = category;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.placeUrl = placeUrl;
        this.roadAddress = roadAddress;
        this.lotNumberAddress = lotNumberAddress;
        this.coordinates = new Coordinates(latitude, longitude);
    }

    public String getAddressDistrict() {
        return lotNumberAddress.split(" ")[1];
    }

    public String getAddressNeighborhood() {
        return lotNumberAddress.split(" ")[2];
    }
}
