package goorm.code_challenge.challenge_room.domain;

import goorm.code_challenge.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "CHALLENGE_ROOM"
)
@DynamicInsert
public class ChallengeRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", nullable = false, length = 20)
    private String roomName;

    @JoinColumn(name = "creator_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private RoomDifficulty roomDifficulty;

    @Column(nullable = false, length = 300)
    private String description;

    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    @CreatedDate
    private LocalDateTime createdAt;

    public ChallengeRoom(String roomName, User user, RoomDifficulty roomDifficulty, String description, RoomStatus roomStatus) {
        this.roomName = roomName;
        this.user = user;
        this.roomDifficulty = roomDifficulty;
        this.description = description;
        this.roomStatus = roomStatus;
        this.createdAt = LocalDateTime.now();
    }

    public ChallengeRoom() {

    }

    public static ChallengeRoomBuilder builder() {
        return new ChallengeRoomBuilder();
    }

    public static class ChallengeRoomBuilder {
        private String roomName;
        private User user;
        private RoomDifficulty roomDifficulty;
        private String description;
        private RoomStatus roomStatus;

        ChallengeRoomBuilder() {
        }

        public ChallengeRoomBuilder roomName(final String roomName) {
            this.roomName = roomName;
            return this;
        }

        public ChallengeRoomBuilder user(final User user) {
            this.user = user;
            return this;
        }

        public ChallengeRoomBuilder roomDifficulty(final RoomDifficulty roomDifficulty) {
            this.roomDifficulty = roomDifficulty;
            return this;
        }

        public ChallengeRoomBuilder description(final String description) {
            this.description = description;
            return this;
        }

        public ChallengeRoomBuilder roomStatus(final RoomStatus roomStatus) {
            this.roomStatus = roomStatus;
            return this;
        }

        public ChallengeRoom build() {
            return new ChallengeRoom(this.roomName, this.user, this.roomDifficulty, this.description, this.roomStatus);
        }

        public String toString() {
            return "ChallengeRoom.ChallengeRoomBuilder(roomName=" + this.roomName + ", user=" + this.user + ", roomDifficulty=" + this.roomDifficulty + ", description=" + this.description + ", roomStatus=" + this.roomStatus + ")";
        }
    }
}
