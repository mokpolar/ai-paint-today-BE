package tipitapi.drawmytoday.diary.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.common.utils.DateUtils;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Prompt;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.dto.GetLastCreationResponse;
import tipitapi.drawmytoday.diary.dto.GetMonthlyDiariesResponse;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.diary.exception.NotOwnerOfDiaryException;
import tipitapi.drawmytoday.diary.exception.PromptNotFoundException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.s3.service.S3PreSignedService;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final PromptService promptService;
    private final DiaryRepository diaryRepository;
    private final ImageService imageService;
    private final ValidateUserService validateUserService;
    private final S3PreSignedService s3PreSignedService;

    public GetDiaryResponse getDiary(Long userId, Long diaryId) {
        User user = validateUserService.validateUserById(userId);

        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(DiaryNotFoundException::new);
        ownedByUser(diary, user);

        String imageUrl = s3PreSignedService.getPreSignedUrlForShare(
            imageService.getImage(diary).getImageUrl(), 30);

        Prompt prompt = Prompt.create("프롬프트 조회를 실패했습니다.", false);
        try {
            prompt = promptService.getOnePromptByDiaryId(diaryId);
        } catch (PromptNotFoundException e) {
            log.info("프롬프트 조회를 실패했습니다.", e);
        }

        return GetDiaryResponse.of(diary, imageUrl, diary.getEmotion(), prompt);
    }

    public List<GetMonthlyDiariesResponse> getMonthlyDiaries(Long userId, int year, int month) {
        User user = validateUserService.validateUserById(userId);
        LocalDateTime startMonth = DateUtils.getStartDate(year, month);
        LocalDateTime endMonth = DateUtils.getEndDate(year, month);
        List<Diary> getDiaryList = diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
            user.getUserId(), startMonth, endMonth);
        return convertDiariesToResponse(getDiaryList);
    }

    public GetLastCreationResponse getLastCreation(Long userId) {
        validateUserService.validateUserById(userId);
        return new GetLastCreationResponse(
            diaryRepository.findFirstByUserUserIdOrderByCreatedAtDesc(userId)
                .map(BaseEntity::getCreatedAt)
                .orElse(null));
    }

    @Transactional
    public void updateDiaryNotes(Long userId, Long diaryId, String notes) {
        User user = validateUserService.validateUserById(userId);
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(DiaryNotFoundException::new);
        ownedByUser(diary, user);

        diary.setNotes(notes);
    }

    private List<GetMonthlyDiariesResponse> convertDiariesToResponse(List<Diary> getDiaryList) {
        return getDiaryList.stream()
            .filter(diary -> {
                if (diary.getImageList().isEmpty()) {
                    throw new ImageNotFoundException();
                }
                return true;
            })
            .map(GetMonthlyDiariesResponse::of)
            .collect(Collectors.toList());
    }

    private void ownedByUser(Diary diary, User user) {
        if (diary.getUser() != user) {
            throw new NotOwnerOfDiaryException();
        }
    }
}
