package D6B.D_discover_picture.picture.service;

import D6B.D_discover_picture.picture.controller.dto.PictureSaveRequest;
import D6B.D_discover_picture.picture.domain.Picture;
import D6B.D_discover_picture.picture.domain.PictureRepository;
import D6B.D_discover_picture.picture.domain.PictureTagRepository;
import D6B.D_discover_picture.picture.domain.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureService {
    private final PictureRepository pictureRepository;
    private final PictureTagRepository pictureTagRepository;
    private final TagRepository tagRepository;

    @Autowired
    public PictureService(PictureRepository pictureRepository,
                          PictureTagRepository pictureTagRepository,
                          TagRepository tagRepository) {
        this.pictureRepository = pictureRepository;
        this.pictureTagRepository = pictureTagRepository;
        this.tagRepository = tagRepository;
    }

    public void savePicture(PictureSaveRequest pictureSaveRequest, Long userId) {
        Picture picture = Picture.from(pictureSaveRequest, userId);
        System.out.println(picture);
        Picture newPicture = pictureRepository.save(picture);
        // 이미지에 달린 태그들. 추후 저장 작업
        List<String> image_tags = pictureSaveRequest.getImage_tags();
    }
}
