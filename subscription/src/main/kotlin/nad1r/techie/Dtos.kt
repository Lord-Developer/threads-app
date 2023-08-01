package nad1r.techie

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseMessage(val code: Int? = null, val message: String? = null)
