package s6.postservice.datalayer.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Entity
@Table(name = "kwex_posts")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Exclude
    @Setter
    private Integer id;
    @Column(name = "text")
    @Length(max = 500)
    @NotNull
    private String text;
    @CreationTimestamp
    @Column(name="createdAt", updatable = false)
    private Date createdAt;
    @Column(name = "userId")
    private Integer userId;
    @Column(name = "isBlocked")
    private Boolean isBlocked = false;
}
