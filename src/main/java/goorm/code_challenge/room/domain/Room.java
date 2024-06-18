package goorm.code_challenge.room.domain;

import goorm.code_challenge.chat.domain.ChatMessage;
import goorm.code_challenge.codes.domain.CodeEntity;
import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, length = 100)
    private String roomTitle;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private Double averageDifficulty;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus roomStatus;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodeEntity> codes;

    @ElementCollection
    @CollectionTable(name = "room_problems", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "question")
    private List<Long> problems;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @Builder
    public Room(String roomTitle, int duration, Double averageDifficulty, String description, User host, RoomStatus roomStatus, List<Long> problems) {
        this.roomTitle = roomTitle;
        this.duration = duration;
        this.averageDifficulty = averageDifficulty;
        this.description = description;
        this.host = host;
        this.roomStatus = roomStatus;
        this.problems = problems;
    }

    // 참가자 수에 따라 방 상태 업데이트
    public void updateRoomStatus() {
        if (this.participants.size() >= 8) {
            this.roomStatus = RoomStatus.FULL;
        } else {
            this.roomStatus = RoomStatus.WAITING;
        }
    }

    public void addParticipant(User user) {
        if (this.participants.size() >= 8) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "방에 참여할 수 있는 최대 인원을 초과했습니다.");
        }
        this.participants.add(new Participant(this, user));
        updateRoomStatus(); // 상태 업데이트
    }

    public void removeParticipant(User user) {
        this.participants.removeIf(p -> p.getUser().equals(user));
        updateRoomStatus(); // 상태 업데이트
    }

    // 참가자 상태 변경
    public void updateParticipantStatus(User user, ParticipantStatus status) {
        this.participants.stream()
                .filter(p -> p.getUser().equals(user))
                .findFirst()
                .ifPresent(p -> p.setStatus(status));
    }

    // 모든 참가자가 준비되었는지 확인
    public boolean allParticipantsReady() {
        return this.participants.stream()
                .allMatch(p -> p.getStatus() == ParticipantStatus.READY);
    }
}
