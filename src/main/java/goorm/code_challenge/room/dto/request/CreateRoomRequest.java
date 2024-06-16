package goorm.code_challenge.room.dto.request;

import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.domain.RoomStatus;
import goorm.code_challenge.user.domain.User;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateRoomRequest {

    @NotBlank(message = "방 제목은 필수 항목입니다.")
    @Size(max = 100, message = "방 제목은 100자 이내여야 합니다.")
    private String roomTitle;

    @NotNull(message = "평균 난이도는 필수 항목입니다.")
    private Double averageDifficulty;

    @NotBlank(message = "방 설명은 필수 항목입니다.")
    @Size(max = 500, message = "방 설명은 500자 이내여야 합니다.")
    private String description;

    @Min(value = 30, message = "풀이 시간은 최소 30분 이상이어야 합니다.")
    @Max(value = 120, message = "풀이 시간은 최대 2시간 이내여야 합니다.")
    private int duration; // 분 단위로 저장

    @NotNull(message = "문제 목록은 필수 항목입니다.")
    @Size(min = 1, message = "적어도 하나의 문제를 선택해야 합니다.")
    private List<String> questions; // 추가된 필드

    public Room toEntity(User host) {
        return Room.builder()
                .roomTitle(this.roomTitle)
                .duration(this.duration)
                .averageDifficulty(this.averageDifficulty)
                .description(this.description)
                .host(host)
                .roomStatus(RoomStatus.WAITING)
                .questions(this.questions)
                .build();
    }
}
