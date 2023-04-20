package D6B.D_discover_user.user.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class UserLikeImage {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private Long pictureId;
}
