package goorm.code_challenge.room.domain;

import goorm.code_challenge.chat.domain.ChatMessage;
import goorm.code_challenge.codes.domain.CodeEntity;
import goorm.code_challenge.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
