package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.ChangePasswordCommand;
import com.cleanarch.application.port.in.ChangePasswordUseCasePort;
import com.cleanarch.application.port.out.PasswordHasherPort;
import com.cleanarch.application.port.out.SessionRepositoryPort;
import com.cleanarch.application.port.out.UserRepositoryPort;
import com.cleanarch.domain.exception.InvalidPasswordException;
import com.cleanarch.domain.exception.InvalidTokenException;
import com.cleanarch.domain.exception.PasswordPolicyViolationException;
import com.cleanarch.domain.model.User;
import jakarta.transaction.Transactional;

@Transactional
public class ChangePasswordUseCase implements ChangePasswordUseCasePort {

    private final UserRepositoryPort userRepository;
    private final SessionRepositoryPort sessionRepository;
    private final PasswordHasherPort passwordHasher;


    public ChangePasswordUseCase(UserRepositoryPort userRepository, SessionRepositoryPort sessionRepository, PasswordHasherPort passwordHasher) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordHasher = passwordHasher;
    }


    @Override
    public void execute(ChangePasswordCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(InvalidTokenException::new);

        if(!passwordHasher.matches(command.currentPassword(), user.getPasswordHash()))
            throw new InvalidPasswordException();

        if(command.currentPassword().equals(command.newPassword()))
            throw new PasswordPolicyViolationException("Current and New Password cannot be same");

        String newPassHash = passwordHasher.hash(command.newPassword());
        user.changePasswordHash(newPassHash);

        userRepository.save(user);
        sessionRepository.revokeAllByUserId(command.userId());

    }
}
