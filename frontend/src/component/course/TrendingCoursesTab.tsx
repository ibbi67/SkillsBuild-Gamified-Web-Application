import { useTrendingCourses } from "@/queries/courses/useTrendingCourses";
import CourseCard from "@/component/course/CourseCard";

export function TrendingCoursesTab() {
    const { data: trendingCourses, isLoading: isFetchingTrending, isError: isFetchTrendingError } = useTrendingCourses();

    if (isFetchingTrending) return <p>Loading trending courses...</p>;
    if (isFetchTrendingError) return <p>Error fetching trending courses.</p>;
    if (trendingCourses?.data.length === 0) return <p>No trending courses available at the moment.</p>;

    return (
        <div className="grid grid-cols-2 gap-2">
            {trendingCourses?.data.map((course, index) => (
                <CourseCard
                    key={course.id}
                    course={course}
                    trendingRank={index + 1}
                />
            ))}
        </div>
    );
}