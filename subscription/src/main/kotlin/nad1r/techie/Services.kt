package nad1r.techie

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "user")
interface UserService {
    @GetMapping("internal/exists/{id}")
    fun existById(@PathVariable id: Long): Boolean
}

interface FollowerService {
    fun follow(dto: FollowerDto)
    fun unfollow(dto: FollowerDto)
    fun getUserConnections(userId: Long): List<Long>
}
@Service
class FollowerServiceImpl(
    private val followerRepository: FollowerRepository,
    private val userService: UserService
) : FollowerService {
    override fun follow(dto: FollowerDto) {
        (dto.followerId == dto.followingId).runIfTrue { throw FollowerCannotFollowSelfException(dto.followerId) }
     followersExist(dto)
      val follower = followerRepository.findByFollowerIdAndFollowingId(dto.followerId, dto.followingId)

        follower?.let {
            it.deleted = false
            followerRepository.save(it)
        } ?: run {
            followerRepository.save(Follower(dto.followerId, dto.followingId))
        }
    }

    override fun unfollow(dto: FollowerDto) {
        (dto.followerId == dto.followingId).runIfTrue { throw FollowerCannotFollowSelfException(dto.followerId) }
        followersExist(dto)
        val follower = followerRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(dto.followerId, dto.followingId)
        follower?.let {
            it.deleted = true
            followerRepository.save(it)
        }?: throw UnfollowNotPossibleException(dto.followerId, dto.followingId)
    }

    override fun getUserConnections(userId: Long): List<Long> {
        return followerRepository.findAllByFollowerIdOrFollowerIdAndDeletedFalse(userId)
    }

    private fun followersExist(dto: FollowerDto) {
        userService.existById(dto.followerId).runIfFalse { throw UserNotFoundException(dto.followerId) }
        userService.existById(dto.followingId).runIfFalse { throw UserNotFoundException(dto.followingId) }
    }
}