package D6B.D_discover_picture.picture.domain;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "word")
    private String word;

    @OneToMany(mappedBy = "tag")
    @ToString.Exclude
    @Builder.Default
    private Set<PictureTag> pictureTags = new LinkedHashSet<>();

}