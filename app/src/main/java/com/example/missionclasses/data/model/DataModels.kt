package com.example.missionclasses.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "TEACHER",
    val phone: String = "",
    val branch: String = "Main Campus",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Student(
    @DocumentId val id: String = "",
    val userId: String = "",
    val enrollmentNo: String = "",
    val fullName: String = "",
    val gradeClass: String = "Class 10th",
    val batch: String = "Batch A",
    val guardianName: String = "",
    val guardianPhone: String = "",
    val status: String = "ACTIVE",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AttendanceRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val date: String = "",
    val gradeClass: String = "Class 10th",
    val batch: String = "Batch A",
    val status: String = "PRESENT",
    val remarks: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FeeInvoice(
    @DocumentId val id: String = "",
    val userId: String = "",
    val studentName: String = "",
    val invoiceNo: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val dueDate: String = "",
    val status: String = "PENDING",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ExamResult(
    @DocumentId val id: String = "",
    val userId: String = "",
    val studentName: String = "",
    val examTitle: String = "",
    val subject: String = "",
    val maxMarks: Double = 100.0,
    val obtainedMarks: Double = 0.0,
    val grade: String = "A",
    val examDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Assignment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val subject: String = "",
    val gradeClass: String = "Class 10th",
    val description: String = "",
    val dueDate: String = "",
    val maxMarks: Double = 50.0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Notice(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val category: String = "GENERAL",
    val targetAudience: String = "ALL",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class LearningResource(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val subject: String = "",
    val chapter: String = "",
    val resourceType: String = "NOTES",
    val contentSummary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class LeaveRequest(
    @DocumentId val id: String = "",
    val userId: String = "",
    val applicantName: String = "",
    val role: String = "STUDENT",
    val leaveType: String = "Medical Leave",
    val startDate: String = "",
    val endDate: String = "",
    val reason: String = "",
    val status: String = "PENDING",
    val adminRemarks: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AiDraft(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val prompt: String = "",
    val generatedContent: String = "",
    val category: String = "LESSON_PLAN",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
