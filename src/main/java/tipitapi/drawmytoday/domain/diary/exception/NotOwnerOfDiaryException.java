package tipitapi.drawmytoday.domain.diary.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class NotOwnerOfDiaryException extends BusinessException {

    public NotOwnerOfDiaryException() {
        super(ErrorCode.IMAGE_NOT_OWNER);
    }
}
