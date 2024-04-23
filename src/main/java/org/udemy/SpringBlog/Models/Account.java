package org.udemy.SpringBlog.Models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long ID;

    @Email(message = "Invalid Email")
    @NotEmpty(message = "Missing Email")
    private String email;

    @NotEmpty(message = "Missing password")
    private String password;

    @NotEmpty(message = "Missing firstName")
    private String firstName;

    @NotEmpty(message = "Missing lastName")
    private String lastName;

    private String gender;

    @Min(value = 18)
    @Max(value = 99)
    private int age;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String photo;

    private String role;

    @Column(name = "token")
    private String token;

    private LocalDateTime resetPasswordTokenTime;

    @OneToMany(mappedBy = "account")
    private List<Post> posts;

    @ManyToMany
    @JoinTable(name = "account_authority", joinColumns = {
            @JoinColumn(name = "account_id", referencedColumnName = "ID") }, inverseJoinColumns = {
                    @JoinColumn(name = "authority_id", referencedColumnName = "ID") })
    private Set<Authority> authorities = new HashSet<>();

}
