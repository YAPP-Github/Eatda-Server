package eatda.fixture;

import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.repository.store.StoreRepository;
import eatda.util.DomainUtils;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class StoreGenerator {

    private static final StoreCategory DEFAULT_CATEGORY = StoreCategory.OTHER;
    private static final String DEFAULT_PHONE_NUMBER = "010-1234-5678";
    private static final String DEFAULT_NAME = "가게 이름";
    private static final String DEFAULT_PLACE_URL = "https://place.kakao.com/123456789";
    private static final String DEFAULT_ROAD_ADDRESS = "";
    private static final District DEFAULT_DISTRICT = District.GANGNAM;

    private static final double DEFAULT_LATITUDE = 37.5665; // Default latitude for Seoul
    private static final double DEFAULT_LONGITUDE = 126.978; // Default longitude for Seoul

    private final StoreRepository storeRepository;

    public StoreGenerator(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Store generate(String kakaoId, String lotNumberAddress) {
        Store store = create(kakaoId, lotNumberAddress, DEFAULT_DISTRICT, DEFAULT_CATEGORY);
        return storeRepository.save(store);
    }

    public Store generate(String kakaoId, String lotNumberAddress, District district) {
        Store store = create(kakaoId, lotNumberAddress, district, DEFAULT_CATEGORY);
        return storeRepository.save(store);
    }

    public Store generate(String kakaoId, String lotNumberAddress, LocalDateTime createdAt) {
        Store store = create(kakaoId, lotNumberAddress, DEFAULT_DISTRICT, DEFAULT_CATEGORY);
        DomainUtils.setCreatedAt(store, createdAt);
        return storeRepository.save(store);
    }

    public Store generate(String kakaoId, String lotNumberAddress, StoreCategory category, LocalDateTime createdAt) {
        Store store = create(kakaoId, lotNumberAddress, DEFAULT_DISTRICT, category);
        DomainUtils.setCreatedAt(store, createdAt);
        return storeRepository.save(store);
    }

    private Store create(String kakaoId, String lotNumberAddress, District district, StoreCategory category) {
        return Store.builder()
                .kakaoId(kakaoId)
                .category(category)
                .phoneNumber(DEFAULT_PHONE_NUMBER)
                .name(DEFAULT_NAME)
                .placeUrl(DEFAULT_PLACE_URL)
                .roadAddress(DEFAULT_ROAD_ADDRESS)
                .lotNumberAddress(lotNumberAddress)
                .district(district)
                .latitude(DEFAULT_LATITUDE)
                .longitude(DEFAULT_LONGITUDE)
                .build();
    }
}
