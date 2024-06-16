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
    @CollectionTable(name = "room_questions", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "question")
    private List<String> questions;

    @ManyToMany
    @JoinTable(
            name = "room_users",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();

    @Builder
    public Room(String roomTitle, int duration, Double averageDifficulty, String description, User host, RoomStatus roomStatus, List<String> questions) {
        this.roomTitle = roomTitle;
        this.duration = duration;
        this.averageDifficulty = averageDifficulty;
        this.description = description;
        this.host = host;
        this.roomStatus = roomStatus;
        this.questions = questions;
    }

    public void addParticipant(User user) {
        if (this.participants.size() >= 8) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "방에 참여할 수 있는 최대 인원을 초과했습니다.");
        }
        this.participants.add(user);
    }

    public void removeParticipant(User user) {
        this.participants.remove(user);
    }
}
