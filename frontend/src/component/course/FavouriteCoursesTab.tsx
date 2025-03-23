import { useFavourites } from "@/queries/favourites/useFavourites";
import CourseCard from "@/component/course/CourseCard";

export function FavouriteCoursesTab() {
    const { data: favouriteCourses, isLoading: isFetchingFavourites, isError: isFetchFavouritesError } = useFavourites();

    if (isFetchingFavourites) return <p>Loading favourite courses...</p>;
    if (isFetchFavouritesError) return <p>Error fetching favourite courses.</p>;
    if (favouriteCourses?.data.length === 0) return <p>No favourite courses available at the moment.</p>;

    return (
        <div className="grid grid-cols-2 gap-2">
            {favouriteCourses?.data.map((course) => <CourseCard key={course.id} course={course} />)}
        </div>
    );
}