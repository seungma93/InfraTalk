package com.seungma.infratalk.data.remote

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.seungma.infratalk.data.datasource.remote.FirebaseUserRemoteDataSourceImpl
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.sign.form.SignUpForm
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class FirebaseUserRemoteDataSourceImplTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var authResult: AuthResult
    private lateinit var database: FirebaseFirestore
    private lateinit var refernece: DocumentReference
    private lateinit var dataSource: FirebaseUserRemoteDataSourceImpl

    @Before
    fun setUp() {
        auth = mockk()
        database = mockk()
        every { auth.currentUser } returns mockk()
        authResult = mockk()
        refernece = mockk()
        dataSource = FirebaseUserRemoteDataSourceImpl(auth, database)
    }

    @Test
    fun `signUp should return a valid user response`() = runTest {
        // given
        val mockUserEntity = mockk<UserEntity>()
        val mockSignUpForm = mockk<SignUpForm>()
        val mockCreateAuthResult = mockk<AuthResult>()
        val mockDocumentReference = mockk<DocumentReference>()
        val mockUser = mockk<FirebaseUser>()

        every { mockSignUpForm.email } returns "example@example.com"
        every { mockSignUpForm.nickname } returns "Example Nickname"

        every { mockUserEntity.email } returns "example@example.com"
        every { mockUserEntity.nickname } returns "Example Nickname"

        //coEvery { dataSource.insertData(mockUserEntity) } returns mockDocumentReference
        //coEvery { dataSource.createAuth(mockSignUpForm) } returns mockCreateAuthResult
        every { mockCreateAuthResult.user } returns mockUser
        every { mockUser.email } returns "example@example.com"

        // when
        val result = dataSource.signUp(mockSignUpForm)

        // then
        assertEquals("example@example.com", result.email)
        assertEquals("Example Nickname", result.nickname)
    }
}

/*

    @Test
    suspend fun `logIn test`() {
        val logInForm = LogInForm("seungma93@naver.com", "123456")

        `when`(auth.signInWithEmailAndPassword(logInForm.email, logInForm.password).await()).thenReturn(authResult)

        `when`(authResult.user).thenReturn(firebaseUser)

        `when`(firebaseUser.email).thenReturn(logInForm.email)

        // When
        val result = runBlocking { dataSource.logIn(logInForm) }

        // Then
        assertEquals(result.email, logInForm.email)
    }


 */


