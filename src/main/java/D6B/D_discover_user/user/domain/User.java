package D6B.D_discover_user.user.domain;

import com.google.firebase.auth.FirebaseToken;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Unique
    @Column(name = "uid", nullable = false)
    private String uid;

    @Column(name= "fcm_token", nullable = false)
    private String fcmToken;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profle_img")
    private String profileImg;

    @Column(name = "gender")
    private String gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "created_at")
    private Instant createdAt;

    // 탈퇴한 유저면 false가 된다.
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    @Builder.Default
    private Set<Love> loves = new LinkedHashSet<>();

    public User(String fcmToken, FirebaseToken decodedToken) {
        this.uid = decodedToken.getUid();
        this.name = decodedToken.getName();
        this.fcmToken = fcmToken;
        this.email = decodedToken.getEmail();
        this.profileImg = decodedToken.getPicture();
        this.createdAt = Instant.now().plusSeconds(60 * 60 * 9);
        this.isActive = true;
    }
}
