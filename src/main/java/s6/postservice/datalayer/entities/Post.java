package s6.postservice.datalayer.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
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
    @Column(updatable = false)
    private Date createdAt;
    @Column
    private Integer userId;
    @Column
    private Boolean isBlocked = false;
}
