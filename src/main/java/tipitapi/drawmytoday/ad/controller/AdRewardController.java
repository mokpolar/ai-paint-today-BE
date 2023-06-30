package tipitapi.drawmytoday.ad.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.ad.dto.CreateAdRewardRequest;
import tipitapi.drawmytoday.ad.service.AdRewardService;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;

@RestController
@RequestMapping("/ad")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdRewardController {

    private final AdRewardService adRewardService;

    @Operation(summary = "광고 기록 생성", description = "사용자가 광고를 시청한 후에 광고 기록을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "성공적으로 광고 기록을 등록함"),
    })
    @PostMapping()
    public ResponseEntity<Void> createDiary(
        @RequestBody @Valid CreateAdRewardRequest createAdRewardRequest,
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        adRewardService.createAdReward(tokenInfo.getUserId(), createAdRewardRequest.getAdType());
        return ResponseEntity.noContent().build();
    }
}
