package com.hackathon.safenet.infrastructure.adapters.supabase.persistence;

import com.hackathon.safenet.domain.model.User;
import com.hackathon.safenet.domain.ports.outbound.UserRepositoryPort;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.mapper.EntityMapper;
import com.hackathon.safenet.infrastructure.adapters.supabase.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final EntityMapper<User, UserEntity> userMapper;

    @Override
    public Optional<User> findByAuthId(String authId) {
        log.debug("Finding user by authId: {}", authId);
        return jpaRepository.findByAuthId(authId)
                .map(userMapper::toDomain);
    }

    @Override
    public User save(User user) {
        log.debug("Saving user: authId={}", user.authId());
        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public void deleteByAuthId(String authId) {
        log.debug("Deleting user by authId: {}", authId);
        jpaRepository.deleteByAuthId(authId);
    }

    @Override
    public boolean existsByAuthId(String authId) {
        return jpaRepository.existsByAuthId(authId);
    }
}