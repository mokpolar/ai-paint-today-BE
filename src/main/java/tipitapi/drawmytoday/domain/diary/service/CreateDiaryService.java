package tipitapi.drawmytoday.domain.diary.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.utils.Encryptor;
import tipitapi.drawmytoday.domain.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.domain.dalle.exception.ImageInputStreamFailException;
import tipitapi.drawmytoday.domain.dalle.service.DallEService;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.emotion.service.ValidateEmotionService;
import tipitapi.drawmytoday.domain.ticket.service.ValidateTicketService;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateDiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageService imageService;
    private final ValidateUserService validateUserService;
    private final ValidateEmotionService validateEmotionService;
    private final ValidateDiaryService validateDiaryService;
    private final ValidateTicketService validateTicketService;
    private final DallEService dallEService;
    private final PromptService promptService;
    private final PromptTextService promptTextService;
    private final Encryptor encryptor;
    private final String DUMMY_IMAGE_PATH = "test/dummy.png";

    @Transactional(
        noRollbackFor = {DallERequestFailException.class, DallERequestFailException.class,
            ImageInputStreamFailException.class})
    public CreateDiaryResponse createDiary(Long userId, Long emotionId, String keyword,
        String notes, LocalDate diaryDate, LocalTime userTime)
        throws DallERequestFailException, ImageInputStreamFailException {
        // TODO: 이미지 여러 개로 요청할 경우의 핸들링 필요
        User user = validateUserService.validateUserById(userId);
        validateDiaryService.validateExistsByDate(userId, diaryDate);
        validateTicketService.findAndUseTicket(userId);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        String prompt = promptTextService.createPromptText(emotion, keyword);
        LocalDateTime diaryDateTime = diaryDate.atTime(userTime);

        try {
            byte[] dallEImage = dallEService.getImageAsUrl(prompt);

            Diary diary = saveDiary(notes, user, emotion, diaryDateTime, false);
            promptService.createPrompt(diary, prompt, true);
            imageService.uploadAndCreateImage(diary, dallEImage, true);

            return new CreateDiaryResponse(diary.getDiaryId());
        } catch (DallERequestFailException | ImageInputStreamFailException e) {
            promptService.createPrompt(prompt, false);
            throw e;
        }
    }

    @Transactional
    public CreateDiaryResponse createTestDiary(Long userId, Long emotionId, String keyword,
        String notes, LocalDate diaryDate, LocalTime userTime) {
        User user = validateUserService.validateAdminUserById(userId);
        validateDiaryService.validateExistsByDate(userId, diaryDate);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        LocalDateTime diaryDateTime = diaryDate.atTime(userTime);

        Diary diary = saveDiary(notes, user, emotion, diaryDateTime, true);
        String prompt = promptTextService.createPromptText(emotion, keyword);
        promptService.createPrompt(diary, prompt, true);
        imageService.createImage(diary, DUMMY_IMAGE_PATH, true);

        return new CreateDiaryResponse(diary.getDiaryId());
    }

    private Diary saveDiary(String notes, User user, Emotion emotion, LocalDateTime diaryDate,
        boolean testDiary) {
        String encryptedNotes = encryptor.encrypt(notes);
        user.setLastDiaryDate(LocalDateTime.now());

        if (testDiary) {
            return diaryRepository.save(Diary.ofTest(user, emotion, diaryDate, encryptedNotes));
        } else {
            return diaryRepository.save(Diary.of(user, emotion, diaryDate, encryptedNotes));
        }
    }
}
