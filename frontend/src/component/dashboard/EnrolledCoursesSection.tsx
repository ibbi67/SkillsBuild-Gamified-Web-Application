"use client"

import { useEnrollments } from "@/queries";
import CourseCard from "../course/CourseCard";

export function EnrolledCoursesSection() {
    const { data: enrollments } = useEnrollments();

    if (!enrollments?.data || enrollments.data.length === 0) {
        return (
            <div className="rounded-lg border p-6">
                <h2 className="text-xl font-medium">Enrolled Courses</h2>
                <p className="mt-4 text-gray-500">You haven't enrolled in any courses yet.</p>
            </div>
        );
    }

    return (
        <div className="rounded-lg border p-6">
            <h2 className="text-xl font-medium">Enrolled Courses</h2>
            <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                {enrollments.data.map((enrollment) => (
                    <CourseCard key={enrollment.id} course={enrollment.course} />
                ))}
            </div>
        </div>
    );
}