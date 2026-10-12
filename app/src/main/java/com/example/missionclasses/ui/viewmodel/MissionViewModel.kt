package com.example.missionclasses.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.missionclasses.data.ai.GeminiAiService
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
import com.example.missionclasses.data.repository.MissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class MissionViewModel(
    private val repository: MissionRepository,
    val currentUserId: String
) : ViewModel() {

    private val _currentRole = MutableStateFlow("SUPER_ADMIN")
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _actionStatus = MutableStateFlow<String?>(null)
    val actionStatus: StateFlow<String?> = _actionStatus.asStateFlow()

    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    // Realtime observed streams
    val userProfile: StateFlow<UiState<UserProfile?>> = repository.observeUserProfile(currentUserId)
        .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
        .catch {
            Log.w("MissionVM", "Error observing user profile", it)
            emit(UiState.Error(it.message ?: "Failed to load profile"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val students: StateFlow<UiState<List<Student>>> = repository.observeStudents(currentUserId)
        .map<List<Student>, UiState<List<Student>>> { list ->
            if (list.isEmpty()) {
                seedInitialDataIfEmpty()
            }
            UiState.Success(list)
        }
        .catch {
            Log.w("MissionVM", "Error observing students", it)
            emit(UiState.Error(it.message ?: "Failed to load students"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val attendance: StateFlow<UiState<List<AttendanceRecord>>> = repository.observeAttendance(currentUserId)
        .map<List<AttendanceRecord>, UiState<List<AttendanceRecord>>> { UiState.Success(it) }
        .catch {
            Log.w("MissionVM", "Error observing attendance", it)
            emit(UiState.Error(it.message ?: "Failed to load attendance"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val fees: StateFlow<UiState<List<FeeInvoice>>> = repository.observeFees(currentUserId)
        .map<List<FeeInvoice>, UiState<List<FeeInvoice>>> { UiState.Success(it) }
        .catch {
            Log.w("MissionVM", "Error observing fees", it)
            emit(UiState.Error(it.message ?: "Failed to load fees"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val exams: StateFlow<UiState<List<ExamResult>>> = repository.observeExams(currentUserId)
        .map<List<ExamResult>, UiState<List<ExamResult>>> { UiState.Success(it) }
        .catch {
            Log.w("MissionVM", "Error observing exams", it)
            emit(UiState.Error(it.message ?: "Failed to load exams"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val assignments: StateFlow<UiState<List<Assignment>>> = repository.observeAssignments(currentUserId)
        .map<List<Assignment>, UiState<List<Assignment>>> { UiState.Success(it) }
        .catch {
            Log.w("MissionVM", "Error observing assignments", it)
            emit(UiState.Error(it.message ?: "Failed to load assignments"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val notices: StateFlow<UiState<List<Notice>>> = repository.observeNotices(currentUserId)
        .map<List<Notice>, UiState<List<Notice>>> { UiState.Success(it) }
        .catch {
            Log.w("MissionVM", "Error observing notices", it)
            emit(UiState.Error(it.message ?: "Failed to load notices"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val learningResources: StateFlow<UiState<List<LearningResource>>> = repository.observeLearningResources(currentUserId)
        .map<List<LearningResource>, UiState<List<LearningResource>>> { UiState.Success(it) }
        .catch {
            Log.w("MissionVM", "Error observing resources", it)
            emit(UiState.Error(it.message ?: "Failed to load resources"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val leaveRequests: StateFlow<UiState<List<LeaveRequest>>> = repository.observeLeaveRequests(currentUserId)
        .map<List<LeaveRequest>, UiState<List<LeaveRequest>>> { UiState.Success(it) }
        .catch {
            Log.w("MissionVM", "Error observing leaves", it)
            emit(UiState.Error(it.message ?: "Failed to load leave requests"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val aiDrafts: StateFlow<UiState<List<AiDraft>>> = repository.observeAiDrafts(currentUserId)
        .map<List<AiDraft>, UiState<List<AiDraft>>> { UiState.Success(it) }
        .catch {
            Log.w("MissionVM", "Error observing AI drafts", it)
            emit(UiState.Error(it.message ?: "Failed to load AI drafts"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    private var hasSeeded = false
    private fun seedInitialDataIfEmpty() {
        if (hasSeeded) return
        hasSeeded = true
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Default profile
            repository.saveUserProfile(
                UserProfile(
                    userId = currentUserId,
                    email = "principal@missionclasses.com",
                    displayName = "Dr. Ayub Shaikh",
                    role = "SUPER_ADMIN",
                    branch = "Main Campus (Kota Center)"
                )
            )

            // Seed initial students
            val initialStudents = listOf(
                Student(enrollmentNo = "MC-2026-101", fullName = "Aarav Patel", gradeClass = "Class 12th", batch = "JEE Advanced Batch", guardianName = "Mahesh Patel", guardianPhone = "+91 9823456789", status = "ACTIVE"),
                Student(enrollmentNo = "MC-2026-102", fullName = "Ananya Sen", gradeClass = "Class 12th", batch = "NEET Target Batch", guardianName = "Subhash Sen", guardianPhone = "+91 9812345678", status = "ACTIVE"),
                Student(enrollmentNo = "MC-2026-103", fullName = "Rohan Kulkarni", gradeClass = "Class 10th", batch = "Board Champions", guardianName = "Prakash Kulkarni", guardianPhone = "+91 9876541230", status = "ACTIVE"),
                Student(enrollmentNo = "MC-2026-104", fullName = "Zoya Khan", gradeClass = "Class 11th", batch = "Foundation Batch", guardianName = "Tariq Khan", guardianPhone = "+91 9898765432", status = "ACTIVE")
            )
            initialStudents.forEach { repository.createStudent(it) }

            // Seed attendance
            initialStudents.forEach { s ->
                repository.markAttendance(
                    AttendanceRecord(
                        studentName = s.fullName,
                        date = today,
                        gradeClass = s.gradeClass,
                        batch = s.batch,
                        status = "PRESENT"
                    )
                )
            }

            // Seed fees
            repository.createFee(
                FeeInvoice(
                    studentName = "Aarav Patel",
                    invoiceNo = "INV-2026-001",
                    title = "Term 1 JEE Physics & Chemistry",
                    amount = 45000.0,
                    paidAmount = 45000.0,
                    dueDate = "2026-10-25",
                    status = "PAID"
                )
            )
            repository.createFee(
                FeeInvoice(
                    studentName = "Ananya Sen",
                    invoiceNo = "INV-2026-002",
                    title = "Term 1 NEET Biology Special",
                    amount = 42000.0,
                    paidAmount = 25000.0,
                    dueDate = "2026-10-30",
                    status = "PARTIAL"
                )
            )

            // Seed exams
            repository.createExam(
                ExamResult(
                    studentName = "Aarav Patel",
                    examTitle = "Major Test 01 (JEE Pattern)",
                    subject = "Physics & Mathematics",
                    maxMarks = 300.0,
                    obtainedMarks = 278.0,
                    grade = "A+",
                    examDate = today
                )
            )
            repository.createExam(
                ExamResult(
                    studentName = "Ananya Sen",
                    examTitle = "All India Mock NEET 01",
                    subject = "Biology & Chemistry",
                    maxMarks = 720.0,
                    obtainedMarks = 665.0,
                    grade = "A+",
                    examDate = today
                )
            )

            // Seed notices
            repository.createNotice(
                Notice(
                    title = "Mission Classes National Scholarship Test (MCNST) Announced",
                    content = "Registration is open for up to 100% tuition waiver for 2026-27 session batches. Hall tickets will be issued online.",
                    category = "GENERAL",
                    targetAudience = "ALL"
                )
            )
            repository.createNotice(
                Notice(
                    title = "Diwali Break & Extra Doubt Clearing Sessions",
                    content = "Doubt counters will remain active 9 AM to 2 PM daily. Online practice tests will continue on the portal.",
                    category = "HOLIDAY",
                    targetAudience = "STUDENTS"
                )
            )

            // Seed assignments
            repository.createAssignment(
                Assignment(
                    title = "Electromagnetic Induction DPP 04",
                    subject = "Physics",
                    gradeClass = "Class 12th",
                    description = "Solve 25 advanced numericals on Faraday's Law and Lenz's Law. Submit answers with step-by-step vector notations.",
                    dueDate = "2026-10-18",
                    maxMarks = 50.0
                )
            )

            // Seed learning resource
            repository.createLearningResource(
                LearningResource(
                    title = "Rotational Dynamics Formula Sheet & Mind Map",
                    subject = "Physics",
                    chapter = "Chapter 5: Rigid Body Mechanics",
                    resourceType = "NOTES",
                    contentSummary = "Comprehensive summary of Moment of Inertia for standard geometries, Parallel & Perpendicular Axes theorems, and rolling motion kinetics."
                )
            )
        }
    }

    fun setRole(role: String) {
        _currentRole.value = role
    }

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun clearActionStatus() {
        _actionStatus.value = null
    }

    fun addStudent(student: Student) {
        viewModelScope.launch {
            val result = repository.createStudent(student)
            _actionStatus.value = if (result.isSuccess) "Student ${student.fullName} registered successfully!" else "Failed to register student"
        }
    }

    fun deleteStudent(id: String) {
        viewModelScope.launch {
            val result = repository.deleteStudent(id)
            _actionStatus.value = if (result.isSuccess) "Student record removed." else "Failed to remove student"
        }
    }

    fun markAttendance(record: AttendanceRecord) {
        viewModelScope.launch {
            val result = repository.markAttendance(record)
            _actionStatus.value = if (result.isSuccess) "Attendance recorded for ${record.studentName}" else "Failed to save attendance"
        }
    }

    fun markAllPresent(studentsList: List<Student>, date: String) {
        viewModelScope.launch {
            studentsList.forEach { s ->
                repository.markAttendance(
                    AttendanceRecord(
                        studentName = s.fullName,
                        date = date,
                        gradeClass = s.gradeClass,
                        batch = s.batch,
                        status = "PRESENT"
                    )
                )
            }
            _actionStatus.value = "All ${studentsList.size} students marked Present!"
        }
    }

    fun createFee(invoice: FeeInvoice) {
        viewModelScope.launch {
            val result = repository.createFee(invoice)
            _actionStatus.value = if (result.isSuccess) "Invoice ${invoice.invoiceNo} created!" else "Failed to create invoice"
        }
    }

    fun recordFeePayment(invoice: FeeInvoice, paymentAmount: Double) {
        viewModelScope.launch {
            val newPaid = (invoice.paidAmount + paymentAmount).coerceAtMost(invoice.amount)
            val newStatus = if (newPaid >= invoice.amount) "PAID" else "PARTIAL"
            val updated = invoice.copy(paidAmount = newPaid, status = newStatus)
            val result = repository.updateFee(updated)
            _actionStatus.value = if (result.isSuccess) "Payment recorded: ₹${paymentAmount.toInt()}" else "Failed to record payment"
        }
    }

    fun createExam(exam: ExamResult) {
        viewModelScope.launch {
            val result = repository.createExam(exam)
            _actionStatus.value = if (result.isSuccess) "Exam score recorded for ${exam.studentName}" else "Failed to record marks"
        }
    }

    fun createAssignment(assignment: Assignment) {
        viewModelScope.launch {
            val result = repository.createAssignment(assignment)
            _actionStatus.value = if (result.isSuccess) "Assignment '${assignment.title}' assigned!" else "Failed to create assignment"
        }
    }

    fun createNotice(notice: Notice) {
        viewModelScope.launch {
            val result = repository.createNotice(notice)
            _actionStatus.value = if (result.isSuccess) "Announcement published!" else "Failed to publish notice"
        }
    }

    fun createLearningResource(resource: LearningResource) {
        viewModelScope.launch {
            val result = repository.createLearningResource(resource)
            _actionStatus.value = if (result.isSuccess) "Resource '${resource.title}' uploaded!" else "Failed to upload resource"
        }
    }

    fun submitLeaveRequest(request: LeaveRequest) {
        viewModelScope.launch {
            val result = repository.createLeaveRequest(request)
            _actionStatus.value = if (result.isSuccess) "Leave application submitted." else "Failed to submit leave"
        }
    }

    fun updateLeaveStatus(leave: LeaveRequest, newStatus: String, remarks: String) {
        viewModelScope.launch {
            val result = repository.updateLeaveStatus(leave, newStatus, remarks)
            _actionStatus.value = if (result.isSuccess) "Leave marked as $newStatus." else "Failed to update leave"
        }
    }

    fun generateAiLessonPlan(title: String, prompt: String, category: String) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val aiResult = GeminiAiService.generateAcademicContent(
                prompt = "Topic: $title. Category: $category. User Requirements: $prompt"
            )
            val generatedText = aiResult.getOrDefault("Lesson outline and questions compiled.")
            repository.saveAiDraft(
                AiDraft(
                    title = title,
                    prompt = prompt,
                    generatedContent = generatedText,
                    category = category
                )
            )
            _isGeneratingAi.value = false
            _actionStatus.value = "AI draft for '$title' created!"
        }
    }
}
