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
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final EntityMapper<User, UserEntity> userMapper;

    @Override
    public Optional<User> findById(UUID id) {
        log.debug("Finding user by id: {}", id);
        return jpaRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public User save(User user) {
        log.debug("Saving user: id={}", user.id());
        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting user by id: {}", id);
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}