package goorm.code_challenge.room.domain;

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
    private Integer averageDifficulty;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus roomStatus;

    @ElementCollection
    @CollectionTable(name = "room_problems", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "question")
    private List<Long> problems = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @Builder
    public Room(String roomTitle, int duration, Integer averageDifficulty, String description, User host, RoomStatus roomStatus, List<Long> problems) {
        this.roomTitle = roomTitle;
        this.duration = duration;
        this.averageDifficulty = averageDifficulty;
        this.description = description;
        this.host = host;
        this.roomStatus = roomStatus;
        this.problems = problems;
    }

    public boolean isParticipant(User user) {
        return this.participants.stream().anyMatch(participant -> participant.getUser().equals(user));
    }

    public void addParticipant(User user) {
        if (isParticipant(user)) {
            throw new RuntimeException("User is already a participant in the room");
        }
        Participant participant = new Participant(this, user);
        this.participants.add(participant);
    }

    public void removeParticipant(User user) {
        this.participants.removeIf(participant -> participant.getUser().equals(user));

        if (this.host.equals(user) && !this.participants.isEmpty()) {
            this.host = this.participants.get(0).getUser();
        }
    }

    public boolean allParticipantsReady() {
        return this.participants.stream().allMatch(participant -> participant.getStatus() == ParticipantStatus.READY);
    }

    public void updateParticipantStatus(User user, ParticipantStatus status) {
        this.participants.stream()
                .filter(participant -> participant.getUser().equals(user))
                .findFirst()
                .ifPresent(participant -> participant.setStatus(status));
    }
}
