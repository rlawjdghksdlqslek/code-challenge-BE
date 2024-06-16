package goorm.code_challenge.codes.domain;

import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, length = 5000)
    private String codeContent;

    @Column(nullable = false, length = 50)
    private String language = "Java";

    @Column(nullable = false)
    private String submissionTime;
}
