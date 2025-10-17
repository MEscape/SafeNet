package com.hackathon.safenet.application.service;

import com.hackathon.safenet.domain.enums.NotificationType;
import com.hackathon.safenet.domain.model.FriendRequest;
import com.hackathon.safenet.domain.model.Friendship;
import com.hackathon.safenet.domain.model.NotificationMessage;
import com.hackathon.safenet.domain.ports.inbound.FriendRequestPort;
import com.hackathon.safenet.domain.ports.outbound.FriendRequestRepositoryPort;
import com.hackathon.safenet.domain.ports.outbound.FriendshipRepositoryPort;
import com.hackathon.safenet.domain.ports.outbound.NotificationPort;
import com.hackathon.safenet.domain.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FriendRequestService implements FriendRequestPort {

    private final FriendRequestRepositoryPort friendRequestRepository;
    private final FriendshipRepositoryPort friendshipRepository;
    private final UserRepositoryPort userRepository;
    private final NotificationPort notificationPort;

    @Override
    public FriendRequest sendFriendRequest(UUID requesterId, UUID requestedId) {
        log.debug("Sending friend request from {} to {}", requesterId, requestedId);

        // Validate users exist
        if (!userRepository.existsById(requesterId)) {
            throw new IllegalArgumentException("Requester user not found: " + requesterId);
        }
        if (!userRepository.existsById(requestedId)) {
            throw new IllegalArgumentException("Requested user not found: " + requestedId);
        }

        validateFriendRequest(requesterId, requestedId);

        FriendRequest friendRequest = FriendRequest.create(requesterId, requestedId);
        FriendRequest saved = friendRequestRepository.save(friendRequest);

        notifyFriendRequestReceived(saved);

        log.info("Friend request sent: {}", saved.id());
        return saved;
    }

    @Override
    public FriendRequest acceptFriendRequest(UUID requestId, UUID userId) {
        log.debug("Accepting friend request {} by user {}", requestId, userId);

        FriendRequest request = getFriendRequest(requestId);
        validateCanAccept(request, userId);

        FriendRequest acceptedRequest = request.accept();
        FriendRequest saved = friendRequestRepository.save(acceptedRequest);

        Friendship friendship = Friendship.create(request.requesterId(), request.requestedId());
        friendshipRepository.save(friendship);

        notifyFriendRequestAccepted(saved);

        log.info("Friend request accepted: {}", saved.id());
        return saved;
    }

    @Override
    public FriendRequest rejectFriendRequest(UUID requestId, UUID userId) {
        log.debug("Rejecting friend request {} by user {}", requestId, userId);

        FriendRequest request = getFriendRequest(requestId);
        validateCanReject(request, userId);

        FriendRequest rejectedRequest = request.reject();
        FriendRequest saved = friendRequestRepository.save(rejectedRequest);

        log.info("Friend request rejected: {}", saved.id());
        return saved;
    }

    @Override
    public void cancelFriendRequest(UUID requestId, UUID userId) {
        log.debug("Canceling friend request {} by user {}", requestId, userId);

        FriendRequest request = getFriendRequest(requestId);
        validateCanCancel(request, userId);

        friendRequestRepository.deleteById(requestId);

        log.info("Friend request canceled: {}", requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequest> getPendingReceivedRequests(UUID userId) {
        return friendRequestRepository.findByRequestedId(userId).stream()
                .filter(r -> r.status() == FriendRequest.FriendRequestStatus.PENDING)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequest> getPendingSentRequests(UUID userId) {
        return friendRequestRepository.findByRequesterId(userId).stream()
                .filter(r -> r.status() == FriendRequest.FriendRequestStatus.PENDING)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getPendingReceivedRequestsCount(UUID userId) {
        return friendRequestRepository.countPendingReceivedRequests(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getPendingSentRequestsCount(UUID userId) {
        return friendRequestRepository.countPendingSentRequests(userId);
    }

    // ========== Validation Methods ==========

    private void validateFriendRequest(UUID requesterId, UUID requestedId) {
        if (requesterId.equals(requestedId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        if (friendshipRepository.existsBetweenUsers(requesterId, requestedId)) {
            throw new IllegalArgumentException("Users are already friends");
        }

        if (friendRequestRepository.existsBetweenUsers(requesterId, requestedId)) {
            throw new IllegalArgumentException("Friend request already exists");
        }
    }

    private void validateCanAccept(FriendRequest request, UUID userId) {
        if (!request.requestedId().equals(userId)) {
            throw new IllegalArgumentException("Only requested user can accept");
        }
        validatePending(request);
    }

    private void validateCanReject(FriendRequest request, UUID userId) {
        if (!request.requestedId().equals(userId)) {
            throw new IllegalArgumentException("Only requested user can reject");
        }
        validatePending(request);
    }

    private void validateCanCancel(FriendRequest request, UUID userId) {
        if (!request.requesterId().equals(userId)) {
            throw new IllegalArgumentException("Only requester can cancel");
        }
        validatePending(request);
    }

    private void validatePending(FriendRequest request) {
        if (request.status() != FriendRequest.FriendRequestStatus.PENDING) {
            throw new IllegalArgumentException("Friend request is not pending");
        }
    }

    // ========== Helper Methods ==========

    private FriendRequest getFriendRequest(UUID requestId) {
        return friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));
    }

    // ========== Notification Methods ==========

    private void notifyFriendRequestReceived(FriendRequest request) {
        try {
            NotificationMessage notification = NotificationMessage.create(
                    NotificationType.FRIEND_REQUEST_RECEIVED,
                    request.requesterId().toString(),
                    request.requestedId().toString(),
                    null
            );

            notificationPort.send(notification);
            log.debug("Sent friend request notification");

        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
            // Don't fail the operation
        }
    }

    private void notifyFriendRequestAccepted(FriendRequest request) {
        try {
            NotificationMessage notification = NotificationMessage.create(
                    NotificationType.FRIEND_REQUEST_ACCEPTED,
                    request.requestedId().toString(),
                    request.requesterId().toString(),
                    null
            );

            notificationPort.send(notification);
            log.debug("Sent friend request accepted notification");

        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
            // Don't fail the operation
        }
    }
}