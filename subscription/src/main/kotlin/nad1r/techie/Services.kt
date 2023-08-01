package nad1r.techie

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "user", configuration = [Auth2TokenConfiguration::class])
interface UserService {
    @GetMapping("internal/exists/{id}")
    fun existById(@PathVariable id: Long): Boolean
}

interface FollowerService {
    fun follow(followingId:  Long)
    fun unfollow(followingId: Long)
    fun getUserConnections(userId: Long): List<Long>
}
@Service
class FollowerServiceImpl(
    private val followerRepository: FollowerRepository,
    private val userService: UserService
) : FollowerService {
    override fun follow(followingId: Long) {
     followersExist(followingId)
      val follower = followerRepository.findByFollowerIdAndFollowingId(userId(), followingId)

        follower?.let {
            it.deleted = false
            followerRepository.save(it)
        } ?: run {
            followerRepository.save(Follower(userId(), followingId))
        }
    }

    override fun unfollow(followingId: Long) {
        followersExist(followingId)
        val follower = followerRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(userId(), followingId)
        follower?.let {
            it.deleted = true
            followerRepository.save(it)
        }?: throw UnfollowNotPossibleException(userId(), followingId)
    }

    override fun getUserConnections(userId: Long): List<Long> {
        return followerRepository.findAllByFollowerIdOrFollowerIdAndDeletedFalse(userId)
    }

    private fun followersExist(followingId: Long) {
        (userId() == followingId).runIfTrue { throw FollowerCannotFollowSelfException(userId()) }
        userService.existById(followingId).runIfFalse { throw UserNotFoundException(followingId) }
    }
}