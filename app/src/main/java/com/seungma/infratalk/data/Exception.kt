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

class NotExistUpdateInfoException(
    private val _message: String
) : Exception()

class FailUpdateException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class NotExistFirebaseCurrentUserException(
    private val _message: String
) : Exception()

class FailVerifiedEmailException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailDeleteUserException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class NeedVerifiedEmailException(
    val _message: String
) : Exception(_message)

class NotExistFirebaseUserException(
    val _message: String
) : Exception(_message)

class FailFirebaseLoginException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class NotExistDBUserInfoException(
    val _message: String
) : Exception(_message)

class FailResetPasswordException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailFirebaseSelectUserException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailGetUserException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailLogoutException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailGetUserTokenException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailSetUserTokenException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailDeleteUserTokenException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailGetSavedEmailException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailSetSavedEmailException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailDeleteSavedEmailException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailSignupException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailUpdateUserInfoException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class FailSendEmailException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)






class FailInsertException(
    val _message: String
) : Exception(_message)



class NoImageException(
    val _message: String
) : Exception(_message)

class FailSelectException(
    val _message: String,
    val throwable: Throwable
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



