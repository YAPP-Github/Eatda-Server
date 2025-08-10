package eatda.service.image;

import eatda.domain.Image;
import eatda.domain.ImageDomain;
import eatda.domain.ImageKey;
import eatda.storage.image.ImageStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageStorage imageStorage;

    public ImageKey uploadImage(ImageDomain domain, MultipartFile imageFile) {
        Image image = new Image(domain, imageFile);
        return imageStorage.upload(image);
    }
}
