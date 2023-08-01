package nad1r.techie

import org.springframework.security.core.context.SecurityContextHolder

fun userId() = SecurityContextHolder.getContext().getUserId()!!
