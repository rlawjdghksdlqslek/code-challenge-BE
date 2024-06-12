package goorm.code_challenge.challenge_room.domain;

import goorm.code_challenge.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "challenge_participants")
public class ChallengeParticipant {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private ChallengeRoom challengeRoom;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean isCreator;

    @Getter
    @Setter
    @Column(nullable = false)
    private String userStatus; // 유저 대기 상태

    // Constructors, getters and setters

    public ChallengeParticipant() {}

    public ChallengeParticipant(ChallengeRoom challengeRoom, User user, boolean isCreator, String userStatus) {
        this.challengeRoom = challengeRoom;
        this.user = user;
        this.isCreator = isCreator;
        this.userStatus = userStatus;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

}
