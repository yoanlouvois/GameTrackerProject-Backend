package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.DailyGameSessionDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.DailyGameSession;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.DailyGameSessionRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.DailyGameSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DailyGameSessionServiceImpl implements DailyGameSessionService {
    private static final Logger log = LoggerFactory.getLogger(DailyGameSessionServiceImpl.class);
    private final DailyGameSessionRepository dailyGameSessionRepository;
    private final UserRepository userRepository;

    public DailyGameSessionServiceImpl(DailyGameSessionRepository dailyGameSessionRepository, UserRepository userRepository) {
        this.dailyGameSessionRepository = dailyGameSessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DailyGameSessionDto createSession(DailyGameSessionDto sessionDto) {
        if (sessionDto == null) {
            log.error("La session à créer est null");
            throw new IllegalArgumentException("La session ne peut être null");
        }
        DailyGameSession session = DailyGameSessionDto.toEntity(sessionDto);
        // On peut initialiser certains champs ici si nécessaire (par exemple la date)
        if (session.getDate() == null) {
            session.setDate(java.time.Instant.now());
        }
        session = dailyGameSessionRepository.save(session);
        return DailyGameSessionDto.fromEntity(session);
    }

    @Override
    public DailyGameSessionDto updateSession(Integer id, DailyGameSessionDto sessionDto) {
        if (id == null) {
            log.error("L'ID de la session à mettre à jour est null");
            throw new IllegalArgumentException("L'ID de la session ne peut être null");
        }
        if (sessionDto == null) {
            log.error("Les données de mise à jour de la session sont null");
            throw new IllegalArgumentException("Les données de mise à jour ne peuvent être null");
        }

        // Mise à jour des champs de la session existante à partir du DTO
        // Ici, on choisit de remplacer les valeurs existantes par celles fournies dans le DTO.
        DailyGameSession updatedSession = DailyGameSessionDto.toEntity(sessionDto);
        updatedSession.setId(id); // S'assurer de conserver le même ID
        updatedSession = dailyGameSessionRepository.save(updatedSession);
        return DailyGameSessionDto.fromEntity(updatedSession);
    }

    @Override
    public void deleteSession(Integer id) {
        if (id == null) {
            log.error("L'ID de la session à supprimer est null");
            throw new IllegalArgumentException("L'ID de la session ne peut être null");
        }
        DailyGameSession session = dailyGameSessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucune session trouvée avec l'ID " + id,
                        ErrorCodes.DAILY_GAME_SESSION_NOT_FOUND
                ));

        // Vérification si la session est utilisée par un utilisateur
        Optional<User> users = userRepository.findByDailyGameSessionId(id);
        if (users.isPresent()) {
            throw new InvalidOperationException("L'avatar est déjà utilisé par un utilisateur",
                    ErrorCodes.DAILY_GAME_SESSION_ALREADY_IN_USE);
        }

        dailyGameSessionRepository.delete(session);
    }



    @Override
    public DailyGameSessionDto getSessionById(Integer id) {
        if (id == null) {
            log.error("L'ID de la session est null");
            throw new IllegalArgumentException("L'ID de la session ne peut être null");
        }
        Optional<DailyGameSession> sessionOpt = dailyGameSessionRepository.findById(id);
        return sessionOpt
                .map(DailyGameSessionDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucune session trouvée avec l'ID " + id,
                        ErrorCodes.DAILY_GAME_SESSION_NOT_FOUND
                ));
    }

    @Override
    public List<DailyGameSessionDto> getSessionByDate(Instant date) {
        if (date == null) {
            log.error("La date de la session est null");
            throw new IllegalArgumentException("La date ne peut être null");
        }

        List<DailyGameSession> sessions = dailyGameSessionRepository.findByDate(date);

        if (sessions.isEmpty()) {
            log.warn("Aucune session trouvée pour la date " + date);
            throw new EntityNotFoundException(
                    "Aucune session trouvée pour la date " + date,
                    ErrorCodes.DAILY_GAME_SESSION_NOT_FOUND
            );
        }

        return sessions.stream()
                .map(DailyGameSessionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyGameSessionDto> getSessionsForUser(Integer userId) {
        if (userId == null) {
            log.error("L'ID utilisateur est null");
            throw new IllegalArgumentException("L'ID utilisateur ne peut être null");
        }

        List<DailyGameSession> sessions = dailyGameSessionRepository.findByUser(userId);
        if (sessions.isEmpty()) {
            log.warn("Aucune session trouvée pour l'utilisateur : " + userId);
            throw new EntityNotFoundException(
                    "Aucune session trouvée pour l'utilisateur " + userId,
                    ErrorCodes.DAILY_GAME_SESSION_NOT_FOUND
            );
        }

        return sessions.stream()
                .map(DailyGameSessionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public DailyGameSessionDto getSessionByUserAndDate(Integer userId, Instant date) {
        if (userId == null || date == null) {
            log.error("L'ID utilisateur ou la date sont null");
            throw new IllegalArgumentException("L'ID utilisateur et la date doivent être fournis");
        }

        Optional<DailyGameSession> session = dailyGameSessionRepository.findByUserAndDate(userId, date);
        return session
                .map(DailyGameSessionDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucune session trouvée pour l'utilisateur " + userId+ " à la date " + date,
                        ErrorCodes.DAILY_GAME_SESSION_NOT_FOUND
                ));
    }

    @Override
    public List<DailyGameSessionDto> getSessionsForUserBetweenDates(Integer userId, Instant start, Instant end) {
        if (userId == null || start == null || end == null) {
            log.error("Paramètres invalides : userId=" + userId + ", start=" + start + ", end=" + end);
            throw new IllegalArgumentException("L'ID utilisateur, la date de début et la date de fin doivent être fournis");
        }

        List<DailyGameSession> sessions = dailyGameSessionRepository.findByUserBetweenDates(userId, start, end);
        if (sessions.isEmpty()) {
            log.warn("Aucune session trouvée pour l'utilisateur " + userId + " entre " + start + " et " + end);
            throw new EntityNotFoundException(
                    "Aucune session trouvée pour l'utilisateur " + userId + " entre " + start + " et " + end,
                    ErrorCodes.DAILY_GAME_SESSION_NOT_FOUND
            );
        }

        return sessions.stream()
                .map(DailyGameSessionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public DailyGameSessionDto getLongestSessionForUser(Integer userId) {
        if (userId == null) {
            log.error("L'utilisateur est null");
            throw new IllegalArgumentException("L'utilisateur ne peut être null");
        }

        List<DailyGameSession> sessions = dailyGameSessionRepository.findLongestSessionsByUser(userId);
        if (sessions.isEmpty()) {
            log.warn("Aucune session trouvée pour l'utilisateur " + userId);
            throw new EntityNotFoundException("Aucune session trouvée pour l'utilisateur " + userId,
                    ErrorCodes.DAILY_GAME_SESSION_NOT_FOUND);
        }

        // The list is sorted in descending order by totalTimePlayed; take the first one.
        DailyGameSession longestSession = sessions.getFirst();
        return DailyGameSessionDto.fromEntity(longestSession);
    }

    @Override
    public List<DailyGameSessionDto> getRecentSessionsForUser(Integer userId, int limit) {
        if (userId == null) {
            log.error("L'utilisateur est null");
            throw new IllegalArgumentException("L'utilisateur ne peut être null");
        }

        List<DailyGameSession> sessions = dailyGameSessionRepository.findRecentSessionsByUser(userId);
        if (sessions.isEmpty()) {
            log.warn("Aucune session récente trouvée pour l'utilisateur " + userId);
            throw new EntityNotFoundException("Aucune session récente trouvée pour l'utilisateur " + userId,
                    ErrorCodes.DAILY_GAME_SESSION_NOT_FOUND);
        }

        // Limit the result list to 'limit' entries
        List<DailyGameSession> limitedSessions = sessions.stream()
                .limit(limit)
                .toList();

        return limitedSessions.stream()
                .map(DailyGameSessionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Map<UserDto, Long> getMostActiveUsers() {
        List<Object[]> results = dailyGameSessionRepository.findMostActiveUsers();
        if (results.isEmpty()) {
            log.warn("Aucun utilisateur actif trouvé");
            throw new EntityNotFoundException("Aucun utilisateur actif trouvé", ErrorCodes.USER_NOT_FOUND);
        }

        return results.stream()
                .collect(Collectors.toMap(
                        row -> UserDto.fromEntity((User) row[0]),
                        row -> (Long) row[1]
                ));
    }


    //=================================CALCUL ===============================
    @Override
    public Integer calculateTotalPlaytimeByUser(Integer userId) {
        if (userId == null) {
            log.error("L'utilisateur est null");
            throw new IllegalArgumentException("L'utilisateur ne peut être null");
        }
        Integer totalPlaytime = dailyGameSessionRepository.calculateTotalPlaytimeByUser(userId);
        log.info("Temps de jeu total pour l'utilisateur " + userId + " : " + totalPlaytime);
        return totalPlaytime;
    }

    @Override
    public Integer calculatePlaytimeByUserInPeriod(Integer userId, Instant startDate, Instant endDate) {
        if (userId == null) {
            log.error("L'utilisateur est null");
            throw new IllegalArgumentException("L'utilisateur ne peut être null");
        }
        if (startDate == null || endDate == null) {
            log.error("Les dates de début ou de fin sont null");
            throw new IllegalArgumentException("La date de début et la date de fin doivent être fournies");
        }

        Integer totalPlaytime = dailyGameSessionRepository.calculatePlaytimeByUserInPeriod(userId, startDate, endDate);
        // Ensure totalPlaytime is not null. If no sessions exist, return 0.
        if (totalPlaytime == null) {
            totalPlaytime = 0;
        }

        log.info("Temps de jeu total pour l'utilisateur {} entre {} et {}: {}",
                userId, startDate, endDate, totalPlaytime);
        return totalPlaytime;
    }

    @Override
    public Long countSessionsByUser(Integer userId) {
        if (userId == null) {
            log.error("L'utilisateur est null");
            throw new IllegalArgumentException("L'utilisateur ne peut être null");
        }

        Long count = dailyGameSessionRepository.countSessionsByUser(userId);
        if (count == null) {
            count = 0L;
        }

        log.info("Nombre total de sessions pour l'utilisateur {}: {}", userId, count);
        return count;
    }

    @Override
    public Integer countGamesPlayedByUser(Integer userId) {
        if (userId == null) {
            log.error("L'utilisateur est null");
            throw new IllegalArgumentException("L'utilisateur ne peut être null");
        }

        Integer totalGamesPlayed = dailyGameSessionRepository.countGamesPlayedByUser(userId);
        if (totalGamesPlayed == null) {
            totalGamesPlayed = 0;
        }

        log.info("Nombre total de jeux joués pour l'utilisateur {}: {}", userId, totalGamesPlayed);
        return totalGamesPlayed;
    }

    @Override
    public Double calculateAveragePlaytimeByUser(Integer userId) {
        if (userId == null) {
            log.error("L'utilisateur est null");
            throw new IllegalArgumentException("L'utilisateur ne peut être null");
        }

        Double averagePlaytime = dailyGameSessionRepository.calculateAveragePlaytimeByUser(userId);
        if (averagePlaytime == null) {
            averagePlaytime = 0.0;
        }

        log.info("Moyenne quotidienne de jeu pour l'utilisateur {} : {}", userId, averagePlaytime);
        return averagePlaytime;
    }


    @Override
    public Instant getLastPlayedDate(Integer userId) {
        if (userId == null) {
            log.error("L'ID utilisateur est null");
            throw new IllegalArgumentException("L'ID utilisateur ne peut être null");
        }

        Instant lastDate = dailyGameSessionRepository.findLastPlayedDateByUserId(userId);
        if (lastDate == null) {
            log.warn("Aucune date de dernière session trouvée pour l'utilisateur : " + userId);
            throw new EntityNotFoundException(
                    "Aucune date de dernière session trouvée pour l'utilisateur " + userId,
                    ErrorCodes.DAILY_GAME_SESSION_NOT_FOUND
            );
        }

        log.info("Date de dernière session pour l'utilisateur {}: {}", userId, lastDate);
        return lastDate;
    }





}
