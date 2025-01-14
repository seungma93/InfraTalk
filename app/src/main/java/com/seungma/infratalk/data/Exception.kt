package com.seungma.infratalk.data

class InvalidEmailException(
    private val _message: String,
    throwable: Throwable
) : Exception(throwable.message, throwable)

class WrongPasswordException(
    private val _message: String,
    throwable: Throwable
) : Exception(throwable.message, throwable)

class NotExistEmailException(
    private val _message: String,
    throwable: Throwable
) : Exception(throwable.message, throwable)

class ExistEmailException(
    private val _message: String,
    throwable: Throwable
) : Exception(throwable.message, throwable)

class InvalidPasswordException(
    private val _message: String,
    throwable: Throwable
) : Exception(throwable.message, throwable)

class BlockedRequestException(
    private val _message: String,
    throwable: Throwable
) : Exception(throwable.message, throwable)

class UnKnownException(
    private val _message: String
) : Exception()

class FailFirebaseSignupException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailUserDBInsertException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class VerifiedEmailException(
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

class FailGetUserMeException(
    val _message: String
) : Exception(_message)

class FailFirebaseLoginException(
    val _message: String
) : Exception(_message)

class NotExistDBUserInfo(
    val _message: String
) : Exception(_message)