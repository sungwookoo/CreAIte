package D6B.D_discover_picture.picture.service;

import D6B.D_discover_picture.picture.controller.dto.PictureSaveRequest;
import D6B.D_discover_picture.picture.domain.Picture;
import D6B.D_discover_picture.picture.domain.PictureRepository;
import D6B.D_discover_picture.picture.domain.PictureTagRepository;
import D6B.D_discover_picture.picture.domain.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public void savePicture(PictureSaveRequest pictureSaveRequest) {
        Picture picture = Picture.from(pictureSaveRequest);
        Picture newPicture = pictureRepository.save(picture);
        // 이미지에 달린 태그들. 추후 저장 작업
        List<String> image_tags = pictureSaveRequest.getImageTags();
    }

    public void deletePicture(Long pictureId, String uid) {
        Optional<Picture> opPicture = pictureRepository.findById(pictureId);
        if (opPicture.isPresent()) {
            Picture picture = opPicture.get();
            if (picture.getMakerUid().equals(uid)) {
                picture.setIsAlive(Boolean.FALSE);
                pictureRepository.save(picture);
                // 해당 이미지의 좋아요 삭제 요청, 알림 삭제 요청 보내야함
                return;
            } else {
                throw new IllegalStateException("이미지를 올린 사람이 아닙니다.");
            }
        }
        throw new IllegalStateException("없는 이미지 입니다.");
    }

    public Picture findPictureById(Long pictureId) {
        Optional<Picture> opPicture = pictureRepository.findById(pictureId);
        if (opPicture.isPresent()) {
            return opPicture.get();
        } else {
            throw new IllegalStateException("해당 이미지 없음");
        }
    }
}
