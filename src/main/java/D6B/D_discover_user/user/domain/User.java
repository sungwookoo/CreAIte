package D6B.D_discover_user.user.domain;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String nickname;
    private String profImgUrl;
    private String gender;
    private String role;
    private String provider;
    private String token;
    private Integer age;

    @OneToMany
    @ToString.Exclude
    private Set<UserSearch> userSearches = new LinkedHashSet<>();
}
