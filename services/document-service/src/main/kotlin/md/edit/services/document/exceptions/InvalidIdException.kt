package md.edit.services.document.exceptions

import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = org.springframework.http.HttpStatus.BAD_REQUEST, reason = "Invalid id")
class InvalidIdException : RuntimeException {
    override var message: String = "Invalid id"

    constructor() : super()

    constructor(cause: Throwable) : super(cause) {
        this.message = cause.message ?: "Invalid id"
    }
}