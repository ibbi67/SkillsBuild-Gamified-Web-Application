import { Course, Person } from "@/types/databaseTypes";
import { Timer } from "./Timer";
import { EnrollButton } from "./EnrollButton";
import { FavoriteButton } from "./FavoriteButton";
import DetailsButton from "./DetailsButton";
import axiosInstance from "@/component/axiosInstance";
import { useQueryClient } from '@tanstack/react-query';

interface CourseActionsProps {
    courseId: number;
    user: Person | undefined;
    enrollmentId: number;
    isEnrolled: boolean | undefined;
    isLoading: boolean;
    courseLink?: string;
}

export function CourseActions({
                                  user,
                                  courseId,
                                  enrollmentId,
                                  isEnrolled,
                                  isLoading,
                                  courseLink
                              }: CourseActionsProps) {
    const queryClient = useQueryClient();

    if (!user) return null;


    const openCourseLink = async () => {
        if (!courseLink) return;

        try {

            await axiosInstance.post(`/courses/${courseId}/view`);


            queryClient.invalidateQueries({ queryKey: ['trendingCourses'] });


            window.open(courseLink, "_blank", "noopener,noreferrer");
        } catch (error) {
            console.error("Error recording course view:", error);

            window.open(courseLink, "_blank", "noopener,noreferrer");
        }
    };

    return (
        <div className="flex gap-4 p-4 bg-gray-50">
            <div className="flex justify-between items-center gap-4">
                {courseLink && (
                    <button
                        onClick={openCourseLink}
                        className="rounded bg-blue-500 px-4 py-2 text-white cursor-pointer"
                    >
                        Go to Course
                    </button>
                )}
                <EnrollButton courseId={courseId} isEnrolled={isEnrolled} disabled={!user} />
                <DetailsButton courseId={courseId} />
                <FavoriteButton courseId={courseId} disabled={!user} />
            </div>
            {isEnrolled && <Timer enrollmentId={enrollmentId} />}
        </div>
    );
}