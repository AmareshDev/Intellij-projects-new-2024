package com.exam.in.service.impl;

import com.exam.in.dto.UserDto;
import com.exam.in.entity.Role;
import com.exam.in.entity.User;
import com.exam.in.payload.AppConstants;
import com.exam.in.repo.RoleRepository;
import com.exam.in.repo.UserRepository;
import com.exam.in.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
@Component
public class UserServiceImpl implements UserService {


    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private UserRepository userrepo;
    @Autowired
    private RoleRepository rolerepo;
    @Autowired
    PasswordEncoder passwordEncoder;

   @Autowired
   ModelMapper modelMapper;


    @Override
    public void deleteUser(String username) {
        User user1= userrepo.findByUserName(username);
        userrepo.delete(user1);
    }

    @Override
    public User RegisterUser(UserDto userDto) throws RoleNotFoundException, Exception {
        User user = this.modelMapper.map(userDto, User.class);
        user.setVerified(false);
        String rawPassword = user.getPassword();

        // Check if the password is not null
        if (rawPassword != null) {
            user.setPassword(this.passwordEncoder.encode(rawPassword));

            // Use Optional for findById
            Optional<Role> optionalRole = this.rolerepo.findById(AppConstants.NORMAL_USER);

            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                user.getRoles().add(role);
            } else {
                // Handle the case where the role is not found
                throw new RoleNotFoundException("Role with ID " + AppConstants.NORMAL_USER + " not found");
            }

            User existingUser = userrepo.findByEmail(user.getEmail());
            if (existingUser != null) {
                throw new Exception("user already registered");
            }

            String otp = generateOTP();
            user.setOtp(otp);

            User registeredUser = this.userrepo.save(user);
            sendVerficationMail(registeredUser.getEmail(), otp);


            return registeredUser;
        } else {
            // Handle the case where the password is null
            throw new IllegalArgumentException("Password cannot be null");
        }
    }

    @Override
    public List<User> getAllUser() {
       List<User> allUser= this.userrepo.findAll();
       return  allUser;
    }

    @Override
    public String verifyUser(String email, String otp) throws Exception {
        User user = userrepo.findByEmail(email);
        if (user == null) {
            throw new Exception("user already registered");
        }
        else if (!otp.equals(user.getOtp())) {
            throw new Exception("Wrong otp");
        }
        else {
            user.setVerified(true);
            userrepo.save(user);
            return "otp verified successsfully";
        }

    }


    private String generateOTP(){
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }


    private void sendEmail(String to, String subject, String body) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendVerficationMail(String email, String otp) {
        String subject = "Email verfication";
        String body = "your varification OTP is :" + otp;
        sendEmail(email, subject, body);
    }
}