package org.udemy.SpringBlog.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.udemy.SpringBlog.Models.Account;
import org.udemy.SpringBlog.services.AccountService;
import org.udemy.SpringBlog.services.EmailService;
import org.udemy.SpringBlog.util.constants.AppUtil;
import org.udemy.SpringBlog.util.constants.email.EmailDetails;

import jakarta.validation.Valid;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    @Value("${site.domain}")
    private String siteDomain;

    @Value("${spring.mvc.static-path-pattern}")
    private String imagePrefixPath;

    @Value("${password.token.reset.timeout.minutes}")
    private int password_reset_time;

    @GetMapping("/register")
    public String register(Model model) {
        Account account = new Account();
        model.addAttribute("account", account);
        return "account_views/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute Account account, BindingResult result) {
        if (result.hasErrors()) {
            return "account_views/register";
        }
        accountService.save(account);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "account_views/login";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model, Principal principal) {
        String authUser = "email";

        if (principal != null) {
            authUser = principal.getName();
        }

        Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            model.addAttribute("account", account);
            model.addAttribute("photo", account.getPhoto());
            return "account_views/profile";
        } else {
            return "redirect:/?error";
        }
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String postProfile(@ModelAttribute Account account, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "account_views/profile";
        }

        String authUser = "email";

        if (principal != null) {
            authUser = principal.getName();
        }
        Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);

        if (optionalAccount.isPresent()) {
            Account updatedAccount = accountService.findOneByID(account.getID()).get();
            updatedAccount.setAge(account.getAge());
            updatedAccount.setDateOfBirth(account.getDateOfBirth());
            updatedAccount.setFirstName(account.getFirstName());
            updatedAccount.setLastName(account.getLastName());
            updatedAccount.setGender(account.getGender());
            updatedAccount.setPassword(account.getPassword());

            accountService.save(updatedAccount);
            SecurityContextHolder.clearContext();
            return "redirect:/";
        } else {
            return "redirect:/?error";
        }
    }

    @PostMapping("/update_photo")
    @PreAuthorize("isAuthenticated()")
    public String updatePhoto(@RequestParam("file") MultipartFile file, RedirectAttributes attributes,
            Principal principal) {
        if (file.isEmpty()) {
            attributes.addFlashAttribute("error", "No file Uploaded");
            return "redirect:/profile";
        } else {
            String fileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());

            try {
                int length = 10;
                boolean useLetters = true;
                boolean useNumbers = true;
                String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
                String finalPhotoName = generatedString + fileName;
                String absoluteFileLocation = AppUtil.getUploadPath(finalPhotoName);

                Path path = Paths.get(absoluteFileLocation);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                attributes.addFlashAttribute("message", "Photo Uploaded Succesfully");

                String authUser = "email";
                if (principal != null) {
                    authUser = principal.getName();
                }

                Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);
                if (optionalAccount.isPresent()) {
                    Account account = optionalAccount.get();
                    Account accountByID = accountService.findOneByID(account.getID()).get();

                    String relativeFileLocation = imagePrefixPath.replace("**", "uploads/" + finalPhotoName);
                    accountByID.setPhoto(relativeFileLocation);
                    accountService.save(accountByID);

                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "redirect:/profile";

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return "redirect:/profile?error";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        return "account_views/forgot_password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String mEmail, RedirectAttributes attribute, Model model) {
        Optional<Account> optionalAccount = accountService.findOneByEmail(mEmail);
        if (optionalAccount.isPresent()) {
            Account tempAccount = accountService.findOneByID(optionalAccount.get().getID()).get();
            String resetToken = UUID.randomUUID().toString();
            tempAccount.setToken(resetToken);
            tempAccount.setResetPasswordTokenTime(LocalDateTime.now().plusMinutes(password_reset_time));
            accountService.save(tempAccount);
            String resetMessage = "This is the reset password link: " + siteDomain + "change-password?token=" + resetToken;
            EmailDetails emailDetails = new EmailDetails(tempAccount.getEmail(), resetMessage,
                    "Reset Password Udemy demo");
            if (emailService.sendSimpleEmail(emailDetails) == false) {
                attribute.addFlashAttribute("error", "Error while sending email, contact admin");
                return "redirect:/forgot-password";
            }
            attribute.addFlashAttribute("message", "Password Reset Email sent");
            return "redirect:/login";
        } else {
            attribute.addFlashAttribute("error", "No user found with the email entered");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/test")
    public String test(Model model) {
        return "account_views/test";
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, @RequestParam("token") String token, RedirectAttributes attributes) {
        if (token.equals("")){
            attributes.addFlashAttribute("error", "Invalid Token");
            return "redirect:/forgot-password";     
        }
        Optional<Account> optionalAccount = accountService.findByToken(token);
        if (optionalAccount.isPresent()){
            Account account = accountService.findOneByID(optionalAccount.get().getID()).get();

            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(optionalAccount.get().getResetPasswordTokenTime())){
                attributes.addFlashAttribute("error", "Token Expired");
                return "redirect:/forgot-password";
            }
            
            model.addAttribute("account", account);
            return "account_views/change_password";
        }

        attributes.addFlashAttribute("error", "Invalid Token");
        return "redirect:/forgot-password";
    }

    @PostMapping("/change-password")
    public String postChangePassword(@ModelAttribute Account account, RedirectAttributes attributes){
        Account accountByID = accountService.findOneByID(account.getID()).get();
        accountByID.setPassword(account.getPassword());
        accountByID.setToken("");
        accountService.save(accountByID);
        attributes.addFlashAttribute("message", "Password Updated");
        return "redirect:/login";
    }

}
