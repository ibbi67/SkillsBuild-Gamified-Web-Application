import { Course } from "@/types/databaseTypes";
import { useMe, useEnrollments } from "@/queries";
import { CourseDescription } from "./CourseDescription";
import { CourseActions } from "./CourseActions";
import {Eye} from "lucide-react";

interface CourseCardProps {
    course: Course ;
    trendingRank?: number;
}

export default function CourseCard({ course, trendingRank }: CourseCardProps) {
    const { data: user, isLoading: isUserLoading } = useMe();
    const { data: enrollments, isLoading: isEnrollmentsLoading } = useEnrollments();
    
    const isEnrolled = enrollments?.data.some(
        (enrollment) => enrollment.course.id === course.id
    );

    const enrollmentId = enrollments?.data.find(
        (enrollment) => enrollment.course.id === course.id
    )?.id || -1;

    const formatViews = (views?: number) => {
        if (!views) return '0';
        if (views >= 1000) {
            return `${(views / 1000).toFixed(1)}M`;
        }
        if (views >= 1000) {
            return `${(views / 1000).toFixed(1)}K`;
        }
        return views.toString();
    };


    const getTrendingEmoji = (rank: number) => {
        if (rank === 1) return "🔥";
        if (rank === 2) return "⚡";
        if (rank === 3) return "🚀";
        return "📈";
    };


    return (
        <div
            className="relative flex flex-col overflow-hidden rounded-lg border border-blue-500 shadow-lg transition hover:border-blue-600 hover:bg-blue-50">

            {trendingRank && trendingRank <= 10 && (
                <div
                    className="absolute right-0 top-0 m-2 flex items-center rounded-full bg-red-500 px-5 py-3 text-sm font-bold text-white z-10">
                    {getTrendingEmoji(trendingRank)} #{trendingRank} Trending
                </div>
            )}


            <div className="flex justify-between items-center bg-blue-500 p-4">
                <h3 className="text-lg font-bold text-white">
                    {course.title}
                </h3>
                {course.views !== undefined && (
                    <div className="flex items-center text-white mr-16">
                        <Eye className="mr-1 w-5 h-5"/>
                        <span className="text-sm">{formatViews(course.views)}</span>
                    </div>
                )}
            </div>

            <CourseDescription description={course.description}/>

            <CourseActions
                courseId={course.id}
                user={user?.data}
                isEnrolled={isEnrolled}
                enrollmentId={enrollmentId}
                isLoading={isUserLoading || isEnrollmentsLoading}
                courseLink={course.link}
            />
        </div>
    );
}


