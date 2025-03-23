import { useRecommendedCourses } from "@/queries/courses/useRecommendedCourses";
import CourseCard from "@/component/course/CourseCard";

export function RecommendedCoursesTab() {
    const { data: recommendedCourses, isLoading: isFetchingRecommended, isError: isFetchRecommendedError } = useRecommendedCourses();

    if (isFetchingRecommended) return <p>Loading recommended courses...</p>;
    if (isFetchRecommendedError) return <p>Error fetching recommended courses.</p>;
    if (recommendedCourses?.data.length === 0) return <p>No recommended courses available at the moment.</p>;

    return (
        <div className="grid grid-cols-2 gap-2">
            {recommendedCourses?.data.map((course) => <CourseCard key={course.id} course={course} />)}
        </div>
    );
}