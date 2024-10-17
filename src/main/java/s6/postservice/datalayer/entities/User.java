package s6.postservice.datalayer.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kwex_users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Exclude
    private Integer id;

    @Column(unique = true, length = 100, nullable = false)
    private String email;
}
