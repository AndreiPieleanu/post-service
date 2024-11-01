package s6.postservice.datalayer.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kwex_relationships")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendsRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Exclude
    private Integer id;
    @Column(nullable = false)
    private Integer senderId;
    @Column(nullable = false)
    private Integer receiverId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;
}
