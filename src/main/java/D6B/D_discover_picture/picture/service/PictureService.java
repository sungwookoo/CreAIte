package D6B.D_discover_picture.picture.service;

import D6B.D_discover_picture.picture.controller.dto.DeleteUserRequest;
import D6B.D_discover_picture.picture.controller.dto.PictureAllDetailResponse;
import D6B.D_discover_picture.picture.controller.dto.PictureDetailResponse;
import D6B.D_discover_picture.picture.controller.dto.PictureSaveRequest;
import D6B.D_discover_picture.picture.domain.*;
import D6B.D_discover_picture.picture.service.dto.LoveCheckAndMakerResponse;
import D6B.D_discover_picture.picture.service.dto.PictureLoveCheckRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static D6B.D_discover_picture.common.ConstValues.*;

@Slf4j
@Service
public class PictureService {
    private final PictureRepository pictureRepository;
    private final PictureTagRepository pictureTagRepository;
    private final TagRepository tagRepository;
    private final WeeklyTopPictureRepository weeklyTopPictureRepository;
    private final MonthlyTopPictureRepository monthlyTopPictureRepository;

    @Autowired
    public PictureService(PictureRepository pictureRepository,
                          PictureTagRepository pictureTagRepository,
                          TagRepository tagRepository,
                          WeeklyTopPictureRepository weeklyTopPictureRepository,
                          MonthlyTopPictureRepository monthlyTopPictureRepository) {
        this.pictureRepository = pictureRepository;
        this.pictureTagRepository = pictureTagRepository;
        this.tagRepository = tagRepository;
        this.weeklyTopPictureRepository = weeklyTopPictureRepository;
        this.monthlyTopPictureRepository = monthlyTopPictureRepository;
    }

    public void savePicture(PictureSaveRequest pictureSaveRequest) {
        // 이미지 저장
        Picture picture = Picture.from(pictureSaveRequest);
        Picture newPicture = pictureRepository.save(picture);
        // 이미지에 달린 태그들.
        List<String> image_tags = pictureSaveRequest.getImageTags();
        for (String tag: image_tags) {
            Optional<Tag> opTag = tagRepository.findByWord(tag);
            Tag itag;
            if (opTag.isPresent()) {
                // 태그가 이미 존재한다면 그냥 들고오기
                itag = opTag.get();
                itag.setTagCount(itag.getTagCount() + 1);
            } else {
                // 없으면 새로 저장하고 가져오기
                Tag ntag = new Tag();
                ntag.setWord(tag);
                ntag.setTagCount(1L);
                itag = tagRepository.save(ntag);
            }

            pictureTagRepository.save(PictureTag
                    .builder()
                    .tag(itag)
                    .picture(newPicture)
                    .build()
            );
        }
    }

    // 이미지 삭제
    public void deletePicture(Long pictureId, String uid) {
        Picture picture = findPictureById(pictureId);
        if (picture.getMakerUid().equals(uid)) {
            Optional<WeeklyTopPicture> wtPicture = weeklyTopPictureRepository.findByPictureId(picture.getId());
            Optional<MonthlyTopPicture> mtPicture = monthlyTopPictureRepository.findByPictureId(picture.getId());
            if (wtPicture.isPresent()) {
                updateWeeklyTop();
            }
            if (mtPicture.isPresent()) {
                updateMonthlyTop();
            }
            picture.setIsAlive(Boolean.FALSE);
            // 이미지의 태그들 TagCount 낮추기
            minusTagCount(picture);
            pictureRepository.save(picture);
            // 해당 이미지의 좋아요 삭제 요청, 알림 삭제 요청 보내야함
            MsaService.deleteLikeRequest(pictureId);
            MsaService.deleteLikeAlarmRequest(pictureId);
        } else {
            throw new IllegalStateException("이미지를 올린 사람이 아닙니다.");
        }
    }

    // 이미지 isPublic 토글
    public void toggleIsPublic(String uid, Long pictureId) {
        Picture picture = findPictureById(pictureId);
        if (picture.getMakerUid().equals(uid)) {
            picture.setIsPublic(!picture.getIsPublic());
            pictureRepository.save(picture);
        } else {
            throw new IllegalStateException("본인이 만든 이미지가 아닙니다.");
        }
    }

    // 랜덤한 태그들 반환
    public List<String> getRandomTags() {
        List<Tag> tags = tagRepository.findByTagCountGreaterThanEqual(10L);
        Collections.shuffle(tags);
        List<String> wordsList = new ArrayList<>();
        if (tags.size() >= 3) {
            for (int i = 0; i < 3; i++) {
                Tag tag = tags.get(i);
                wordsList.add(tag.getWord());
            }
        } else {
            for (int i = 0; i < tags.size(); i++) {
                Tag tag = tags.get(i);
                wordsList.add(tag.getWord());
            }
        }
        return wordsList;
    }

    // 하나의 Picture detail
    public PictureAllDetailResponse getPictureDetail(Long pictureId, String uid) {
        Picture picture = findPictureById(pictureId);
        List<String> tagWords = getTagWords(picture);
        List<PictureLoveCheckRequest> checkList = new ArrayList<>();
        PictureLoveCheckRequest pictureLoveCheckRequest = PictureLoveCheckRequest.from(picture, uid);
        checkList.add(pictureLoveCheckRequest);
        List<LoveCheckAndMakerResponse> checkedList = MsaService.checkLoveAndGetName(checkList);
        PictureAllDetailResponse pADR = PictureAllDetailResponse.from(picture, tagWords, checkedList.get(0).getLoveCheck(), checkedList.get(0).getMakerName());
        return pADR;
    }

    public PictureAllDetailResponse getPictureDetail(Long pictureId) {
        Picture picture = findPictureById(pictureId);
        List<String> tagWords = getTagWords(picture);
        List<String> checkList = new ArrayList<>();
        checkList.add(picture.getMakerUid());
        List<String> checkedList = MsaService.checkMakerName(checkList);
        PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(picture, tagWords, false, checkedList.get(0));
        return pictureAllDetailResponse;
    }

    // 검색한 이미지들 또는 태그별 이미지들 찾기(로그인 한 사용자)
    public List<PictureAllDetailResponse> getSearchListWithLogin(String uid, String keyword) {
        Optional<Tag> opTag = tagRepository.findByWord(keyword);
        List<PictureAllDetailResponse> detailList = new ArrayList<>();
        if (opTag.isPresent()) {
            Tag tag = opTag.get();
            Set<PictureTag> pictureTags = tag.getPictureTags();
            List<PictureLoveCheckRequest> checkList = new ArrayList<>();
            for (PictureTag pTag: pictureTags) {
                Picture picture = pTag.getPicture();
                if (picture.getIsAlive() == Boolean.TRUE) {
                    if (picture.getIsPublic() == Boolean.TRUE) {
                        PictureLoveCheckRequest pictureLoveCheckRequest = PictureLoveCheckRequest.from(picture, uid);
                        checkList.add(pictureLoveCheckRequest);
                    } else {
                        if (picture.getMakerUid().equals(uid)) {
                            PictureLoveCheckRequest pictureLoveCheckRequest = PictureLoveCheckRequest.from(picture, uid);
                            checkList.add(pictureLoveCheckRequest);
                        }
                    }
                }
            }
            List<LoveCheckAndMakerResponse> checkedList = MsaService.checkLoveAndGetName(checkList);
            for (int i = 0; i < checkList.size(); i++) {
                Picture picture = findPictureById(checkList.get(i).getPictureId());
                List<String> tagWords = getTagWords(picture);
                PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(picture, tagWords, checkedList.get(i).getLoveCheck(), checkedList.get(i).getMakerName());
                detailList.add(pictureAllDetailResponse);
            }
        }
        return detailList;
    }

    public List<PictureAllDetailResponse> getSearchListWithoutLogin(String keyword) {
        Optional<Tag> opTag = tagRepository.findByWord(keyword);
        List<PictureAllDetailResponse> detailList = new ArrayList<>();
        if (opTag.isPresent()) {
            Tag tag = opTag.get();
            Set<PictureTag> pictureTags = tag.getPictureTags();
            List<String> checkList = new ArrayList<>();
            List<Picture> picList = new ArrayList<>();
            for (PictureTag pTag : pictureTags) {
                Picture picture = pTag.getPicture();
                if (picture.getIsAlive() == Boolean.TRUE && picture.getIsPublic() == Boolean.TRUE) {
                    checkList.add(picture.getMakerUid());
                    picList.add(picture);
                }
            }
            List<String> checkedList = MsaService.checkMakerName(checkList);

            for (int i = 0; i < checkedList.size(); i++) {
                Picture picture = picList.get(i);
                List<String> tagWords = getTagWords(picture);
                PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(picture, tagWords, false, checkedList.get(i));
                detailList.add(pictureAllDetailResponse);
            }
        }
        return detailList;
    }

    public List<PictureAllDetailResponse> getTodayPickWithLogin(String uid) {
        List<PictureAllDetailResponse> list = new ArrayList<>();
        List<Picture> picList = pictureRepository.findRandPictures();
        List<PictureLoveCheckRequest> checkList = new ArrayList<>();
        for (Picture picture : picList) {
            PictureLoveCheckRequest pictureLoveCheckRequest = PictureLoveCheckRequest.from(picture, uid);
            checkList.add(pictureLoveCheckRequest);
        }
        List<LoveCheckAndMakerResponse> checkedList = MsaService.checkLoveAndGetName(checkList);
        for (int i = 0; i < picList.size(); i++) {
            Picture picture = picList.get(i);
            LoveCheckAndMakerResponse checking = checkedList.get(i);
            List<String> tagWords = getTagWords(picture);
            PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(picture, tagWords, checking.getLoveCheck(), checking.getMakerName());
            list.add(pictureAllDetailResponse);
        }
        return list;
    }

    public List<PictureAllDetailResponse> getTodayPickWithoutLogin() {
        List<PictureAllDetailResponse> list = new ArrayList<>();
        List<Picture> picList = pictureRepository.findRandPictures();
        List<String> checkList = new ArrayList<>();
        for (Picture picture : picList) {
            checkList.add(picture.getMakerUid());
        }
        List<String> checkedList = MsaService.checkMakerName(checkList);
        for (int i = 0; i < picList.size(); i++) {
            Picture picture = picList.get(i);
            List<String> tagWords = getTagWords(picture);
            PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(picture, tagWords, false, checkedList.get(i));
            list.add(pictureAllDetailResponse);
        }
        return list;
    }

    public List<PictureAllDetailResponse> getWeeklyTopListWithLogin(String uid) {
        List<WeeklyTopPicture> weeklyList = weeklyTopPictureRepository.findAll();
        List<PictureLoveCheckRequest> checkList = new ArrayList<>();
        for (WeeklyTopPicture weeklyTopPicture : weeklyList) {
            Picture picture = findPictureById(weeklyTopPicture.getPictureId());
            PictureLoveCheckRequest pictureLoveCheckRequest = PictureLoveCheckRequest.from(picture, uid);
            checkList.add(pictureLoveCheckRequest);
        }
        List<LoveCheckAndMakerResponse> checkedList = MsaService.checkLoveAndGetName(checkList);
        List<PictureAllDetailResponse> detailList = new ArrayList<>();
        for (int i = 0; i < checkedList.size(); i++) {
            WeeklyTopPicture wtPicture = weeklyList.get(i);
            Picture wPic = findPictureById(wtPicture.getPictureId());
            List<String> tagWords = getTagWords(wPic);
            PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(wPic, tagWords, checkedList.get(i).getLoveCheck(), checkedList.get(i).getMakerName());
            detailList.add(pictureAllDetailResponse);
        }
        return detailList;
    }

    public List<PictureAllDetailResponse> getWeeklyTopListWithoutLogin() {
        List<WeeklyTopPicture> weeklyList = weeklyTopPictureRepository.findAll();
        List<String> checkList = new ArrayList<>();
        for (WeeklyTopPicture weeklyTopPicture : weeklyList) {
            Picture picture = findPictureById(weeklyTopPicture.getPictureId());
            checkList.add(picture.getMakerUid());
        }
        List<String> checkedList = MsaService.checkMakerName(checkList);
        List<PictureAllDetailResponse> detailList = new ArrayList<>();
        for (int i = 0; i < checkedList.size(); i++) {
            WeeklyTopPicture wtPicture = weeklyList.get(i);
            Picture wPic = findPictureById(wtPicture.getPictureId());
            List<String> tagWords = getTagWords(wPic);
            PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(wPic, tagWords, false, checkedList.get(i));
            detailList.add(pictureAllDetailResponse);
        }
        return detailList;
    }

    public List<PictureAllDetailResponse> getMonthlyTopListWithLogin(String uid) {
        List<MonthlyTopPicture> monthlyList = monthlyTopPictureRepository.findAll();
        List<PictureLoveCheckRequest> checkList = new ArrayList<>();
        for (MonthlyTopPicture monthlyTopPicture : monthlyList) {
            Picture picture = findPictureById(monthlyTopPicture.getPictureId());
            PictureLoveCheckRequest pictureLoveCheckRequest = PictureLoveCheckRequest.from(picture, uid);
            checkList.add(pictureLoveCheckRequest);
        }
        List<LoveCheckAndMakerResponse> checkedList = MsaService.checkLoveAndGetName(checkList);
        List<PictureAllDetailResponse> detailList = new ArrayList<>();
        for (int i = 0; i < checkedList.size(); i++) {
            MonthlyTopPicture mtPicture = monthlyList.get(i);
            Picture mPic = findPictureById(mtPicture.getPictureId());
            List<String> tagWords = getTagWords(mPic);
            PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(mPic, tagWords, checkedList.get(i).getLoveCheck(), checkedList.get(i).getMakerName());
            detailList.add(pictureAllDetailResponse);
        }
        return detailList;
    }

    public List<PictureAllDetailResponse> getMonthlyTopListWithoutLogin() {
        List<MonthlyTopPicture> monthlyList = monthlyTopPictureRepository.findAll();
        List<String> checkList = new ArrayList<>();
        for (MonthlyTopPicture monthlyTopPicture : monthlyList) {
            Picture picture = findPictureById(monthlyTopPicture.getPictureId());
            checkList.add(picture.getMakerUid());
        }
        List<String> checkedList = MsaService.checkMakerName(checkList);
        List<PictureAllDetailResponse> detailList = new ArrayList<>();
        for (int i = 0; i < checkedList.size(); i++) {
            MonthlyTopPicture mtPicture = monthlyList.get(i);
            Picture mPic = findPictureById(mtPicture.getPictureId());
            List<String> tagWords = getTagWords(mPic);
            PictureAllDetailResponse pictureAllDetailResponse = PictureAllDetailResponse.from(mPic, tagWords, false, checkedList.get(i));
            detailList.add(pictureAllDetailResponse);
        }
        return detailList;
    }

    // Weekly Top 업데이트
    @Scheduled(cron = "5 0 0 * * 2", zone = "Asia/Seoul")
    public void updateWeeklyTop() {
        weeklyTopPictureRepository.deleteAllInBatch();
        // 일주일 전 월요일 KST 기준 시간
        LocalDate oneWeekAgoMonday = LocalDate.now().minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        Instant mondayOneWeekAgoKST = oneWeekAgoMonday.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

        // 일주일 전 일요일 KST 기준 시간
        LocalDate oneWeekAgoSunday = LocalDate.now().minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Instant sundayOneWeekAgoKST = oneWeekAgoSunday.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().plus(Duration.ofDays(1)).minusSeconds(1);

        List<Picture> picList = pictureRepository.findTop50ByIsPublicAndIsAliveAndCreatedAtBetweenOrderByLoveCountDesc(true, true,
                mondayOneWeekAgoKST, sundayOneWeekAgoKST);

        System.out.println("위클리 업데이트");
        System.out.println(picList);

        for (Picture picture : picList) {
            WeeklyTopPicture weeklyTopPicture = new WeeklyTopPicture();
            weeklyTopPicture.setPictureId(picture.getId());
            weeklyTopPictureRepository.save(weeklyTopPicture);
        }
    }

    // Monthly Top 업데이트
    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
    public void updateMonthlyTop() {
        monthlyTopPictureRepository.deleteAllInBatch();

        // 지난 달 1일 KST 기준 시간
        LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        Instant firstDayOfLastMonthKST = firstDayOfLastMonth.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

        // 지난 달 마지막 날짜 KST 기준 시간
        LocalDate lastDayOfLastMonth = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        Instant lastDayOfLastMonthKST = lastDayOfLastMonth.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().plus(Duration.ofDays(1)).minusSeconds(1);

        List<Picture> picList = pictureRepository.findTop50ByIsPublicAndIsAliveAndCreatedAtBetweenOrderByLoveCountDesc(true, true,
                firstDayOfLastMonthKST, lastDayOfLastMonthKST);

        System.out.println(picList);

        for (Picture picture : picList) {
            MonthlyTopPicture monthlyTopPicture = new MonthlyTopPicture();
            monthlyTopPicture.setPictureId(picture.getId());
            monthlyTopPictureRepository.save(monthlyTopPicture);
        }
    }

    public List<String> getTagWords(Picture picture) {
        Set<PictureTag> pTags = picture.getPictureTags();
        List<String> words = new ArrayList<>();
        for (PictureTag pTag : pTags) {
            words.add(pTag.getTag().getWord());
        }
        Collections.sort(words);
        return words;
    }

    // 이미지 삭제할 때 이미지의 태그들의 tag count 낮추기
    public void minusTagCount(Picture picture) {
        Set<PictureTag> pTags = picture.getPictureTags();
        for (PictureTag pTag : pTags) {
            Tag tag = pTag.getTag();
            if (tag.getTagCount() > 0) {
                tag.setTagCount(tag.getTagCount() - 1);
            }
            tagRepository.save(tag);
        }
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
