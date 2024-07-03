package com.ufrn.nei.almoxarifadoapi.service;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.UserMapper;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserCreateDTO;
import com.ufrn.nei.almoxarifadoapi.entity.RecoveryTokenEntity;
import com.ufrn.nei.almoxarifadoapi.entity.RoleEntity;
import com.ufrn.nei.almoxarifadoapi.entity.UserEntity;
import com.ufrn.nei.almoxarifadoapi.exception.EntityNotFoundException;
import com.ufrn.nei.almoxarifadoapi.exception.InvalidRecoveryTokenException;
import com.ufrn.nei.almoxarifadoapi.exception.PasswordInvalidException;
import com.ufrn.nei.almoxarifadoapi.infra.mail.MailService;
import com.ufrn.nei.almoxarifadoapi.repository.RecoveryTokenRepository;
import com.ufrn.nei.almoxarifadoapi.repository.UserRepository;
import com.ufrn.nei.almoxarifadoapi.repository.projection.UserProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RecoveryTokenRepository tokenRepository;

  @Autowired
  private RoleService roleService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private MailService mailService;

  @Transactional
  public UserEntity save(UserCreateDTO createDTO) {
    UserEntity user = UserMapper.toUser(createDTO);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    RoleEntity role = getRoleUser();

    user.setRole(role);
    user.setRecords(List.of());
    userRepository.save(user);

    mailService.sendMailUserCreatedAsync(user.getEmail(), user.getName());

    return user;
  }

  @Transactional(readOnly = true)
  public UserEntity findById(Long id) {
    return userRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException(String.format("Usuário não encontrado com id='%s'", id)));
  }

  @Transactional(readOnly = true)
  public UserEntity findByEmail(String email) {
    UserEntity user = userRepository.findByEmail(email).orElseThrow(
        () -> new EntityNotFoundException(String.format("Usuário não encontrado com email='%s'", email)));

    return user;
  }

  @Transactional(readOnly = true)
  public Page<UserProjection> findAllPageable(Pageable pageable) {
    return userRepository.findAllPageable(pageable);
  }

  @Transactional(readOnly = true)
  public List<UserEntity> findAllByRole(RoleEntity role) {
    return userRepository.findAllByRole(role);
  }

  @Transactional
  public void updatePassword(Long id, String token, String newPassword, String confirmPassword) throws EntityNotFoundException {
      Optional<RecoveryTokenEntity> userRecToken = this.getRecoveryTokenUser(id);

      RecoveryTokenEntity storedToken = userRecToken.isEmpty() ? null : userRecToken.get();

      if (storedToken == null || storedToken.isUsed())
        throw new EntityNotFoundException("Não há token de recuperação válido para o usuário de id " + id);
      else if (!storedToken.getToken().equals(token)) {
        throw new InvalidRecoveryTokenException("Token de recuperação enviado é diferente do armazenado para o usuário.");
      }

      if (!newPassword.equals(confirmPassword)) {
        throw new PasswordInvalidException("Nova senha não é igual a confirma senha");
      }

      UserEntity user = findById(id);
      user.setPassword(passwordEncoder.encode(newPassword));

      storedToken.setUsed(true);
      storedToken.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
      tokenRepository.save(storedToken);
  }

  @Transactional
  public void updatePassword(String currentPassword, String newPassword, String confirmPassword, Long id) {
    if (!newPassword.equals(confirmPassword)) {
      throw new PasswordInvalidException("Nova senha não é igual a confirma senha");
    }

    UserEntity user = findById(id);

    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
      throw new PasswordInvalidException("Senha atual é inválida");
    }

    user.setPassword(passwordEncoder.encode(newPassword));
  }

  @Transactional
  public void deleteUser(Long id) {
    UserEntity user = findById(id);

    if (!user.getActive()) {
      throw new EntityNotFoundException("O usuário não existe.");
    }

    user.setActive(false);
  }

  @Transactional(readOnly = true)
  public Optional<RecoveryTokenEntity> getRecoveryTokenUser(Long id) {
    var ignored = findById(id);

    Optional<RecoveryTokenEntity> token = tokenRepository.findById(id);

    return token;
  }

  @Transactional
  public RecoveryTokenEntity createRecoveryTokenUser(UserEntity user, String newToken) {
    RecoveryTokenEntity token = new RecoveryTokenEntity(user, newToken, false);
    tokenRepository.save(token);
    return token;
  }

  @Transactional
  public void updateRecoveryTokenUser(RecoveryTokenEntity userToken, String newToken) {
    try {
      userToken.setToken(newToken);
      userToken.setUsed(false);
      userToken.setValidUntil(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30L)));
      userToken.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

      tokenRepository.save(userToken);
    } catch (Exception ex) {
    }
  }

  // Método(s) Auxiliar(es)
  private RoleEntity getRoleUser() {
    final String ROLE_USER = "ROLE_USER";

    return roleService.findByRoleName(ROLE_USER).orElseThrow(
        () -> new EntityNotFoundException("Não foi possível encontrar a role de usuários"));
  }

}
