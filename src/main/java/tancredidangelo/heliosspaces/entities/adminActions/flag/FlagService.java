package tancredidangelo.heliosspaces.entities.adminActions.flag;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tancredidangelo.heliosspaces.entities.adminActions.flag.flagDTO.CreateFlagRequestDTO;
import tancredidangelo.heliosspaces.entities.adminActions.flag.flagDTO.FlagResponseDTO;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.user.stack.User;
import tancredidangelo.heliosspaces.entities.person.user.stack.UserService;
import tancredidangelo.heliosspaces.exceptions.NotFoundException;
import java.util.UUID;

@Service
@Slf4j
public class FlagService {

    /// dependency injection
    private final FlagRepository flagRepository;
    private final UserService userService;

    public FlagService(FlagRepository flagRepository, UserService userService) {
        this.flagRepository = flagRepository;
        this.userService = userService;
    }


    // methods


    /// CREATE FLAG
    @Transactional
    public FlagResponseDTO createFlag(Account admin, CreateFlagRequestDTO payload) {

        User user = userService.findById(payload.userId());

        Flag flag = new Flag(user, admin, payload.reason());
        Flag saved = flagRepository.save(flag);

        log.info("Flag ID {} created. Flagged User ID {} by Admin ID {}", saved.getId(), user.getId(), admin.getId());

        return FlagResponseDTO.fromEntity(saved);
    }



    /// GET FLAG BY USER
    public Page<FlagResponseDTO> getFlagsByUser(UUID userId, Pageable pageable) {
        return flagRepository.findByUserId(userId, pageable).map(FlagResponseDTO::fromEntity);
    }



    /// GET FLAG BY ID
    public FlagResponseDTO getFlagById(Long id) {
        Flag flag = flagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flag record not found"));
        return FlagResponseDTO.fromEntity(flag);
    }


}