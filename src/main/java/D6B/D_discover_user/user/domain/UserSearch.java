package D6B.D_discover_user.user.domain;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "user_search")
public class UserSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "searh_id")
    private Search search;
}
