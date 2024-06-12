package goorm.code_challenge.challenge_room.service;

import goorm.code_challenge.challenge_room.domain.ChallengeRoom;
import goorm.code_challenge.challenge_room.dto.request.CreateChallengeRoomRequest;
import goorm.code_challenge.challenge_room.repository.ChallengeRoomRepository;
import goorm.code_challenge.global.utils.CurrentUserGetter;
import goorm.code_challenge.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        readOnly = true
)
public class ChallengeRoomService {
    private final CurrentUserGetter currentUserGetter;
    private final ChallengeRoomRepository challengeRoomRepository;

    public void createChallengeRoom(CreateChallengeRoomRequest request) {
        User user = this.currentUserGetter.getCurrentMember();
        ChallengeRoom challengeRoom = request.toEntity(user);
        this.challengeRoomRepository.save(challengeRoom);
    }

    public ChallengeRoomService(final CurrentUserGetter currentUserGetter, final ChallengeRoomRepository challengeRoomRepository) {
        this.currentUserGetter = currentUserGetter;
        this.challengeRoomRepository = challengeRoomRepository;
    }
}
