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

@Entity(name = "th_user")
class User(
    @Column(unique = true, length = 32, nullable = false) var username: String,
    var password: String,
    @ManyToOne var role: Role,
    var name: String,
    @ManyToMany(fetch = FetchType.LAZY) var permissions: MutableSet<Permission>? = mutableSetOf(),
    @Column(columnDefinition = "boolean default true") var active: Boolean = true
) : BaseEntity()

@Entity(name = "roles")
class Role(
    @Enumerated(value = EnumType.STRING) var name: UserRole,
) : BaseEntity()

@Entity(name = "permissions")
class Permission(
    @Column(unique = true) var name: String,
    @ManyToMany var role: MutableSet<Role> = mutableSetOf(),
) : BaseEntity()