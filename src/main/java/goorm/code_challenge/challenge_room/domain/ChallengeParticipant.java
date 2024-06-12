package goorm.code_challenge.challenge_room.domain;

import goorm.code_challenge.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "challenge_participants")
public class ChallengeParticipant {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;


    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private ChallengeRoom challengeRoom;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean isCreator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatus participantStatus; // 유저 대기 상태

    public ChallengeParticipant() {}

    public ChallengeParticipant(ChallengeRoom challengeRoom, User user, boolean isCreator, ParticipantStatus participantStatus) {
        this.challengeRoom = challengeRoom;
        this.user = user;
        this.isCreator = isCreator;
        this.participantStatus = participantStatus;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    public enum UserStatus {
        WAITING,
        READY
    }

}
