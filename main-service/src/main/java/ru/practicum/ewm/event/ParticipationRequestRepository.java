package ru.practicum.ewm.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.ParticipationRequest;
import ru.practicum.ewm.event.model.ParticipationRequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Page<ParticipationRequest> findAllByRequesterId(Long userId, PageRequest page);

    List<ParticipationRequest> findAllByIdInAndStatusNot(
            List<Long> requestIds, ParticipationRequestStatus participationRequestStatus);

    List<ParticipationRequest> findAllByIdInAndStatus(
            List<Long> requestIds, ParticipationRequestStatus participationRequestStatus);

    @Modifying
    @Query("UPDATE ParticipationRequest r SET r.status = ?2 WHERE r.id IN ?1")
    void updateEventRequest(List<Long> requestIds, ParticipationRequestStatus status);

    @Query(value = "SELECT r.id " +
            "FROM requests AS r " +
            "WHERE r.id IN ?1 " +
            "ORDER BY r.created " +
            "LIMIT ?2", nativeQuery = true)
    List<Long> getRequestsToConfirm(List<Long> requestIds, int confirmedNumbers);

    @Query(value = "SELECT r.id " +
            "FROM requests AS r " +
            "WHERE r.id IN ?1 " +
            "ORDER BY r.created " +
            "OFFSET ?2", nativeQuery = true)
    List<Long> getRequestsToReject(List<Long> requestIds, int confirmedNumbers);

    List<ParticipationRequest> findAllByIdIn(List<Long> confirmedRequestIds);

    List<ParticipationRequest> findByEventId(Long eventId);

    boolean existsByRequesterIdAndEventId(Long id, Long id1);


}
