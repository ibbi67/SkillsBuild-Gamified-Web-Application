import { Course } from "@/types/databaseTypes";
import { useMe, useEnrollments } from "@/queries";
import { CourseDescription } from "./CourseDescription";
import { CourseActions } from "./CourseActions";

interface CourseCardProps {
    course: Course;
}

export default function CourseCard({ course }: CourseCardProps) {
    const { data: user, isLoading: isUserLoading } = useMe();
    const { data: enrollments, isLoading: isEnrollmentsLoading } = useEnrollments();
    
    const isEnrolled = enrollments?.data.some(
        (enrollment) => enrollment.course.id === course.id
    );

    const enrollmentId = enrollments?.data.find(
        (enrollment) => enrollment.course.id === course.id
    )?.id || -1;

    return (
        <div className="flex flex-col overflow-hidden rounded-lg border border-blue-500 shadow-lg transition hover:border-blue-600 hover:bg-blue-50">
            <h3 className="bg-blue-500 p-4 text-lg font-bold text-white">
                {course.title}
            </h3>

            <CourseDescription description={course.description} />

            <CourseActions 
                courseId={course.id}
                user={user?.data}
                isEnrolled={isEnrolled}
                enrollmentId={enrollmentId}
                isLoading={isUserLoading || isEnrollmentsLoading}
            />
        </div>
    );
}
