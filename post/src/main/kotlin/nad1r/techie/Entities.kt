package nad1r.techie

import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.Temporal
import java.util.*
import javax.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @CreatedBy var createdBy: String? = null,
    @LastModifiedBy var modifiedBy: String? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false
)


@Entity(name = "th_thread")
class Thread(
    @Column(nullable = false) val text: String,
    @Column(nullable = false) val authorId:  Long
): BaseEntity()


@Entity(name = "th_like")
class Like(
    @Column(nullable = false) val authorId: Long,
    val toLikeId: Long,
    @Enumerated(EnumType.STRING) val likeType: LikeType
): BaseEntity()

@Entity(name = "th_reply")
class Reply(
    var text: String,
    @Column(nullable = false) val authorId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    val thread: Thread
): BaseEntity()

@Entity(name = "th_user_thread_read")
class UserThreadRead(
    val userId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    val thread: Thread
): BaseEntity()