package eatda.controller.cheer;

import eatda.domain.cheer.CheerTagName;
import java.util.Collections;
import java.util.List;

public record CheerRegisterRequest(
        String storeKakaoId,
        String storeName,
        String description,
        List<UploadedImageDetail> images,
        List<CheerTagName> tags
) {
    @Override
    public List<CheerTagName> tags() { // TODO : 클라이언트 태그 구현 완료 시 삭제
        if (tags == null) {
            return Collections.emptyList();
        }
        return tags;
    }

    public record UploadedImageDetail(
            String imageKey,
            long orderIndex,
            String contentType,
            long fileSize
    ) {
    }
}
