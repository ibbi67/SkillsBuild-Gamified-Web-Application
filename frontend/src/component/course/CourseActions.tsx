import { Course, Person } from "@/types/databaseTypes";
import { Timer } from "./Timer";
import { EnrollButton } from "./EnrollButton";
import { FavoriteButton } from "./FavoriteButton";

interface CourseActionsProps {
    courseId: number;
    user: Person | undefined;
    enrollmentId: number;
    isEnrolled: boolean | undefined;
    isLoading: boolean;
}

export function CourseActions({ user, courseId, enrollmentId, isEnrolled, isLoading }: CourseActionsProps) {
    if (isLoading) {
        return (
            <div className="flex justify-between items-center gap-4 p-4 bg-gray-50">
                <div className="h-10 w-24 animate-pulse bg-gray-200 rounded"></div>
                <div className="h-10 w-24 animate-pulse bg-gray-200 rounded"></div>
            </div>
        );
    }

    return (
        <div className="flex gap-4 p-4 bg-gray-50">
            <div className="flex justify-between items-center gap-4">
                <EnrollButton 
                    courseId={courseId}
                    isEnrolled={isEnrolled}
                    disabled={!user}
                />
                <FavoriteButton 
                    courseId={courseId}
                    disabled={!user}
                />
            </div>
            {isEnrolled && <Timer enrollmentId={enrollmentId} />}
        </div>
    );
}