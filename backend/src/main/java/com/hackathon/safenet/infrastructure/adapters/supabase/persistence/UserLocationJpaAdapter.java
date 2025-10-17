package com.hackathon.safenet.infrastructure.adapters.supabase.persistence;

import com.hackathon.safenet.domain.model.UserLocation;
import com.hackathon.safenet.domain.ports.outbound.UserLocationRepositoryPort;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserLocationEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.mapper.EntityMapper;
import com.hackathon.safenet.infrastructure.adapters.supabase.repository.UserLocationJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLocationJpaAdapter implements UserLocationRepositoryPort {

    private final UserLocationJpaRepository jpaRepository;
    private final EntityMapper<UserLocation, UserLocationEntity> userLocationMapper;

    @Override
    public UserLocation save(UserLocation userLocation) {
        log.debug("Saving user location: userId={}", userLocation.userId());
        UserLocationEntity entity = userLocationMapper.toEntity(userLocation);
        UserLocationEntity saved = jpaRepository.save(entity);
        return userLocationMapper.toDomain(saved);
    }

    @Override
    public Optional<UserLocation> findById(UUID id) {
        log.debug("Finding user location by id: {}", id);
        return jpaRepository.findById(id)
                .map(userLocationMapper::toDomain);
    }

    @Override
    public Optional<UserLocation> findLatestByUserId(UUID userId) {
        log.debug("Finding latest location for user: {}", userId);
        return jpaRepository.findByUserId(userId)
                .map(userLocationMapper::toDomain);
    }

    @Override
    public Optional<UserLocation> findLatestVisibleByUserId(UUID userId) {
        log.debug("Finding latest visible location for user: {}", userId);
        return jpaRepository.findVisibleByUserId(userId)
                .map(userLocationMapper::toDomain);
    }

    @Override
    public List<UserLocation> findLatestVisibleLocationsByUserIds(List<UUID> userIds) {
        log.debug("Finding latest visible locations for {} users", userIds.size());
        return jpaRepository.findVisibleLocationsByUserIds(userIds).stream()
                .map(userLocationMapper::toDomain)
                .toList();
    }

    @Override
    public List<UserLocation> findWithinBounds(BigDecimal minLat, BigDecimal maxLat, 
                                              BigDecimal minLon, BigDecimal maxLon) {
        log.debug("Finding locations within bounds: lat[{}, {}], lon[{}, {}]", 
                minLat, maxLat, minLon, maxLon);
        return jpaRepository.findWithinBounds(minLat, maxLat, minLon, maxLon).stream()
                .map(userLocationMapper::toDomain)
                .toList();
    }

    @Override
    public List<UserLocation> findRecentLocations(Instant since) {
        log.debug("Finding recent locations since: {}", since);
        return jpaRepository.findRecentLocations(since).stream()
                .map(userLocationMapper::toDomain)
                .toList();
    }

    @Override
    public List<UserLocation> findStaleLocations(Instant before) {
        log.debug("Finding stale locations before: {}", before);
        return jpaRepository.findStaleLocations(before).stream()
                .map(userLocationMapper::toDomain)
                .toList();
    }

    @Override
    public boolean hasRecentLocation(UUID userId, Instant since) {
        log.debug("Checking if user has recent location since: {}", since);
        return jpaRepository.hasRecentLocation(userId, since);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        log.debug("Deleting all locations for user: {}", userId);
        jpaRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteOldLocations(Instant before) {
        log.debug("Deleting old locations before: {}", before);
        jpaRepository.deleteOldLocations(before);
    }
}