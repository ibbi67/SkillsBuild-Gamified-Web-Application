import { useCourses } from "@/queries/courses/useCourses";
import CourseCard from "@/component/course/CourseCard";

interface TabProps {
    query?: string;
}

export function AllCoursesTab({ query = "" }: TabProps) {
    const { data: courses, isLoading: isFetchingCourses, isError: isFetchCoursesError } = useCourses();
    const filteredCourses = courses?.data.filter((course) => course.title.toLowerCase().includes(query.toLowerCase())) || [];

    if (isFetchingCourses) return <p>Loading courses...</p>;
    if (isFetchCoursesError) return <p>Error fetching courses.</p>;
    if (filteredCourses.length === 0) return <p>No courses available at the moment.</p>;

    return (
        <div className="grid grid-cols-2 gap-2">
            {filteredCourses.map((course) => (
                <CourseCard
                    key={course.id}
                    course={course}
                />
            ))}
        </div>
    );
}