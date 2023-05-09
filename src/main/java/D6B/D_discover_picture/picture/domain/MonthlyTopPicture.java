package D6B.D_discover_picture.picture.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "monthly_top_picture")
public class MonthlyTopPicture {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "picture_id")
    private Long pictureId;
}
