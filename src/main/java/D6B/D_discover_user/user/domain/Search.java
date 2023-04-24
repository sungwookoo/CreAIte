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
@Table(name = "search")
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "search_word")
    private String searchWord;

    @OneToMany(mappedBy = "search")
    @ToString.Exclude
    @Builder.Default
    private Set<UserSearch> userSearches = new LinkedHashSet<>();
}
