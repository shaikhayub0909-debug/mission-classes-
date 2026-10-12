package com.example.missionclasses

import com.example.base.FirestoreEmulatorTestBase
import com.example.missionclasses.data.model.Student
import com.example.missionclasses.data.repository.MissionRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class MissionClassesRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createStudent_authenticatedUser_succeedsAndPersists(): Unit = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = MissionRepository(firestore)

        val student = Student(
            enrollmentNo = "MC-2026-001",
            fullName = "Rahul Sharma",
            gradeClass = "Class 10th",
            batch = "Batch Alpha",
            guardianName = "Suresh Sharma",
            guardianPhone = "+91 9876543210",
            status = "ACTIVE"
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createStudent(student)
        }

        assertTrue(result.isSuccess)
        val studentId = result.getOrThrow()
        assertTrue(studentId.isNotEmpty())

        val list = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.observeStudents(uid).first()
        }
        assertTrue(list.any { it.fullName == "Rahul Sharma" })
    }

    @Test
    fun observeStudents_unauthenticatedUser_failsWithPermissionDenied(): Unit = runBlocking {
        auth.signOut()
        val repository = MissionRepository(firestore)

        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                repository.observeStudents("any_user_id").first()
            }
            fail("Expected PERMISSION_DENIED for unauthenticated user")
        } catch (e: Exception) {
            val firestoreException = (e as? FirebaseFirestoreException)
                ?: (e.cause as? FirebaseFirestoreException)
                ?: (e.cause?.cause as? FirebaseFirestoreException)
            assertNotNull("Expected FirebaseFirestoreException in cause chain: $e", firestoreException)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
        }
    }

    @Test
    fun crossUserAccess_bobCannotQueryAliceStudents(): Unit = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = MissionRepository(firestore)
        val student = Student(
            enrollmentNo = "MC-ALICE-1",
            fullName = "Alice Student",
            gradeClass = "Class 12th",
            batch = "Science",
            status = "ACTIVE"
        )
        aliceRepo.createStudent(student).getOrThrow()

        // Bob signs in and tries to query Alice's collection
        signInTestUser(BOB_EMAIL)
        val bobRepo = MissionRepository(firestore)

        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                bobRepo.observeStudents(aliceUid).first()
            }
            fail("Expected PERMISSION_DENIED for cross user access")
        } catch (e: Exception) {
            val firestoreException = (e as? FirebaseFirestoreException)
                ?: (e.cause as? FirebaseFirestoreException)
                ?: (e.cause?.cause as? FirebaseFirestoreException)
            assertNotNull("Expected FirebaseFirestoreException in cause chain: $e", firestoreException)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice@missionclasses.com"
        const val BOB_EMAIL = "bob@missionclasses.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
    }
}
