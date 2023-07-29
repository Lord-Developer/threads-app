package nad1r.techie

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): Page<T>
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>, entityManager: EntityManager,
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {

    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }

    override fun findByIdAndDeletedFalse(id: Long) = findByIdOrNull(id)?.run { if (deleted) null else this }

    @Transactional
    override fun trash(id: Long): T? = findByIdOrNull(id)?.run {
        deleted = true
        save(this)
    }

    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)
    override fun findAllNotDeleted(pageable: Pageable): Page<T> = findAll(isNotDeletedSpecification, pageable)

    @Transactional
    override fun trashList(ids: List<Long>): List<T?> = ids.map { trash(it) }
}

interface ThreadRepository : BaseRepository<Thread>{
    fun existsByIdAndDeletedFalse(id: Long): Boolean

    @Query(value = "SELECT * FROM thread_cloud.thread_post.th_thread t\n" +
            "WHERE t.author_id IN :authorIds  AND t.deleted= false\n" +
            "  AND  t.id NOT IN (SELECT utr.thread_id FROM thread_post.th_user_thread_read utr " +
            "WHERE utr.user_id = :userId)",
         nativeQuery = true)
    fun findAllUnreadTweet(@Param("authorIds") authorIds: List<Long>, @Param("userId")userId: Long, pageable: Pageable): Page<Thread>
}
interface ReplyRepository : BaseRepository<Reply>{
    fun existsByIdAndDeletedFalse(id: Long): Boolean
    fun findAllByThreadIdAndDeletedFalse(threadId: Long,  pageable: Pageable): Page<Reply>
}
interface LikeRepository : BaseRepository<Like>{
    fun findByAuthorIdAndAndToLikeIdAndLikeType(authorId: Long, toLikeId: Long,  likeType: LikeType): Like?
    fun countByToLikeIdAndLikeTypeAndDeletedFalse(toLikeId: Long, likeType: LikeType): Int
}

interface UserThreadReadRepository : BaseRepository<UserThreadRead>{

}
