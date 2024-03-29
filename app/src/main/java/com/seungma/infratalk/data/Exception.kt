package com.seungma.infratalk.data

class InvalidEmailException(
    val _message: String
) : Exception(_message)

class VerifiedEmailException(
    val _message: String
) : Exception(_message)

class InvalidPasswordException(
    val _message: String
) : Exception(_message)

class WrongPasswordException(
    val _message: String
) : Exception(_message)

class BlockedRequestException(
    val _message: String
) : Exception(_message)

class NotExistEmailException(
    val _message: String
) : Exception(_message)

class ExistEmailException(
    val _message: String
) : Exception(_message)

class UnKnownException(
    val _message: String
) : Exception(_message)

class FailSendEmailException(
    val _message: String
) : Exception(_message)

class FailInsertException(
    val _message: String
) : Exception(_message)

class FailUpdatetException(
    val _message: String
) : Exception(_message)

class NoImageException(
    val _message: String
) : Exception(_message)

class FailSelectException(
    val _message: String,
    val throwable: Throwable
) : Exception(_message)

class FailDeleteException(
    val _message: String
) : Exception(_message)

class FailLoadBookMarkListException(
    val _message: String
) : Exception(_message)

class FailInsertBookMarkException(
    val _message: String
) : Exception(_message)

class FailLoadBookMarkException(
    val _message: String
) : Exception(_message)

class FailSelectLogInInfoException(
    val _message: String
) : Exception(_message)

class FailInsertLikeException(
    val _message: String
) : Exception(_message)

class FailDeleteLikeException(
    val _message: String
) : Exception(_message)

class FailLoadLikeException(
    val _message: String
) : Exception(_message)

class FailLoadLikeCountException(
    val _message: String
) : Exception(_message)

class FailSelectBoardContentException(
    val _message: String
) : Exception(_message)

class FailUpdateBookMarkException(
    val _message: String
) : Exception(_message)

class FailDeleteBookMarkException(
    val _message: String
) : Exception(_message)

class FailInsertCommentException(
    val _message: String
) : Exception(_message)

class FailSelectCommentsException(
    val _message: String
) : Exception(_message)

class FailDeleteCommentException(
    val _message: String
) : Exception(_message)
