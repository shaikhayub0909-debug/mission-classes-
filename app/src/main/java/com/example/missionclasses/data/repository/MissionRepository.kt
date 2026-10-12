package com.example.missionclasses.data.repository

import android.content.Context
import com.example.R
import com.example.missionclasses.data.error.OperationType
import com.example.missionclasses.data.error.handleFirestoreError
import com.example.missionclasses.data.model.AiDraft
import com.example.missionclasses.data.model.Assignment
import com.example.missionclasses.data.model.AttendanceRecord
import com.example.missionclasses.data.model.ExamResult
import com.example.missionclasses.data.model.FeeInvoice
import com.example.missionclasses.data.model.LearningResource
import com.example.missionclasses.data.model.LeaveRequest
import com.example.missionclasses.data.model.Notice
import com.example.missionclasses.data.model.Student
import com.example.missionclasses.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class MissionRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- User Profile ---
    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    } else null
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    throw error
                }
        )
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        return try {
            val toSave = if (profile.createdAt == null) {
                profile.copy(userId = uid, createdAt = null, updatedAt = null)
            } else {
                profile.copy(userId = uid, updatedAt = null)
            }
            docRef.set(toSave).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Students ---
    fun observeStudents(userId: String): Flow<List<Student>> = flow {
        val path = "students"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Student::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createStudent(student: Student): Result<String> {
        val uid = requireUserId()
        val collection = db.collection("students")
        val docRef = if (student.id.isNotBlank()) collection.document(student.id) else collection.document()
        return try {
            val payload = student.copy(
                id = docRef.id,
                userId = uid,
                createdAt = null,
                updatedAt = null
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(studentId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection("students").document(studentId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Attendance ---
    fun observeAttendance(userId: String): Flow<List<AttendanceRecord>> = flow {
        val path = "attendance"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(AttendanceRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun markAttendance(record: AttendanceRecord): Result<String> {
        val uid = requireUserId()
        val collection = db.collection("attendance")
        val docRef = if (record.id.isNotBlank()) collection.document(record.id) else collection.document()
        return try {
            val payload = record.copy(
                id = docRef.id,
                userId = uid,
                createdAt = null,
                updatedAt = null
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Fees ---
    fun observeFees(userId: String): Flow<List<FeeInvoice>> = flow {
        val path = "fees"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FeeInvoice::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createFee(invoice: FeeInvoice): Result<String> {
        val uid = requireUserId()
        val collection = db.collection("fees")
        val docRef = if (invoice.id.isNotBlank()) collection.document(invoice.id) else collection.document()
        return try {
            val payload = invoice.copy(
                id = docRef.id,
                userId = uid,
                createdAt = null,
                updatedAt = null
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateFee(invoice: FeeInvoice): Result<Unit> {
        val uid = requireUserId()
        require(invoice.id.isNotBlank()) { "Fee ID cannot be empty" }
        requireNotNull(invoice.createdAt) { "createdAt cannot be null for update" }
        val docRef = db.collection("fees").document(invoice.id)
        return try {
            val payload = invoice.copy(userId = uid, updatedAt = null)
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Exams ---
    fun observeExams(userId: String): Flow<List<ExamResult>> = flow {
        val path = "exams"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(ExamResult::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createExam(exam: ExamResult): Result<String> {
        val uid = requireUserId()
        val collection = db.collection("exams")
        val docRef = if (exam.id.isNotBlank()) collection.document(exam.id) else collection.document()
        return try {
            val payload = exam.copy(
                id = docRef.id,
                userId = uid,
                createdAt = null,
                updatedAt = null
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Assignments ---
    fun observeAssignments(userId: String): Flow<List<Assignment>> = flow {
        val path = "assignments"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Assignment::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createAssignment(assignment: Assignment): Result<String> {
        val uid = requireUserId()
        val collection = db.collection("assignments")
        val docRef = if (assignment.id.isNotBlank()) collection.document(assignment.id) else collection.document()
        return try {
            val payload = assignment.copy(
                id = docRef.id,
                userId = uid,
                createdAt = null,
                updatedAt = null
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Notices ---
    fun observeNotices(userId: String): Flow<List<Notice>> = flow {
        val path = "notices"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Notice::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createNotice(notice: Notice): Result<String> {
        val uid = requireUserId()
        val collection = db.collection("notices")
        val docRef = if (notice.id.isNotBlank()) collection.document(notice.id) else collection.document()
        return try {
            val payload = notice.copy(
                id = docRef.id,
                userId = uid,
                createdAt = null,
                updatedAt = null
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Learning Resources ---
    fun observeLearningResources(userId: String): Flow<List<LearningResource>> = flow {
        val path = "learning_resources"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(LearningResource::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createLearningResource(resource: LearningResource): Result<String> {
        val uid = requireUserId()
        val collection = db.collection("learning_resources")
        val docRef = if (resource.id.isNotBlank()) collection.document(resource.id) else collection.document()
        return try {
            val payload = resource.copy(
                id = docRef.id,
                userId = uid,
                createdAt = null,
                updatedAt = null
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Leave Requests ---
    fun observeLeaveRequests(userId: String): Flow<List<LeaveRequest>> = flow {
        val path = "leave_requests"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(LeaveRequest::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createLeaveRequest(leave: LeaveRequest): Result<String> {
        val uid = requireUserId()
        val collection = db.collection("leave_requests")
        val docRef = if (leave.id.isNotBlank()) collection.document(leave.id) else collection.document()
        return try {
            val payload = leave.copy(
                id = docRef.id,
                userId = uid,
                createdAt = null,
                updatedAt = null
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateLeaveStatus(leave: LeaveRequest, newStatus: String, remarks: String): Result<Unit> {
        val uid = requireUserId()
        require(leave.id.isNotBlank()) { "Leave ID cannot be blank" }
        requireNotNull(leave.createdAt) { "createdAt cannot be null for update" }
        val docRef = db.collection("leave_requests").document(leave.id)
        return try {
            val payload = leave.copy(
                userId = uid,
                status = newStatus,
                adminRemarks = remarks,
                updatedAt = null
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- AI Drafts ---
    fun observeAiDrafts(userId: String): Flow<List<AiDraft>> = flow {
        val path = "ai_drafts"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(AiDraft::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun saveAiDraft(draft: AiDraft): Result<String> {
        val uid = requireUserId()
        val collection = db.collection("ai_drafts")
        val docRef = if (draft.id.isNotBlank()) collection.document(draft.id) else collection.document()
        return try {
            val payload = draft.copy(
                id = docRef.id,
                userId = uid,
                createdAt = null,
                updatedAt = null
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }
}
