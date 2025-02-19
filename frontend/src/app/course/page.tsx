"use client";

import { useApi } from "@/hooks/useApi";
import { Course } from "@/types/course";
import { useEffect, useRef, useState } from "react";
import Navbar from "@/components/Navbar";
import CourseCard from "@/components/CourseCard";

export default function CoursePage() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [favourites, setFavourites] = useState<Course[]>([]);
    const hasFetched = useRef(false);

    const {
        isLoading: isFetchingCourses,
        isError: isFetchCoursesError,
        fetchData: fetchCourses,
        data: fetchedCourses,
    } = useApi<Course[], void>("/courses", {
        method: "GET",
    });

    const {
        isLoading: isFetchingFavourites,
        isError: isFetchFavouritesError,
        fetchData: fetchFavourites,
        data: fetchedFavourites,
    } = useApi<Course[], void>("favourite", { method: "GET" });

    useEffect(() => {
        if (!hasFetched.current) {
            hasFetched.current = true;
            fetchCourses();
            fetchFavourites();
        }
    }, []);

    useEffect(() => {
        if (fetchedCourses) {
            setCourses(fetchedCourses);
        }
    }, [fetchedCourses]);

    useEffect(() => {
        if (fetchedFavourites) {
            setFavourites(fetchedFavourites);
        }
    }, [fetchedFavourites]);

    return (
        <div className="flex flex-col">
            <div className="m-4 grow">
                <Navbar />
            </div>
            <div className="m-4 flex grow flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
                <h2 className="text-2xl font-bold text-blue-600">
                    Favourite Courses
                </h2>

                {isFetchingCourses || isFetchingFavourites ? (
                    <p>Loading favourite courses...</p>
                ) : isFetchCoursesError || isFetchFavouritesError ? (
                    <p>Error fetching favourite courses.</p>
                ) : (
                    <div className="grid grid-cols-2 items-start space-y-4">
                        {favourites.length === 0 ? (
                            <p>No favourite courses available at the moment.</p>
                        ) : (
                            favourites.map((course) => (
                                <CourseCard
                                    key={course.id}
                                    course={course}
                                    favourites={favourites}
                                    setFavourites={setFavourites}
                                />
                            ))
                        )}
                    </div>
                )}
            </div>
            <div className="m-4 flex grow flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
                <h2 className="text-2xl font-bold text-blue-600">
                    Available Courses
                </h2>

                {isFetchingCourses || isFetchingFavourites ? (
                    <p>Loading courses...</p>
                ) : isFetchCoursesError || isFetchFavouritesError ? (
                    <p>Error fetching courses.</p>
                ) : (
                    <div className="grid grid-cols-2 items-start space-y-4">
                        {courses.length === 0 ? (
                            <p>No courses available at the moment.</p>
                        ) : (
                            fetchedCourses &&
                            fetchedCourses.map((course) => (
                                <CourseCard
                                    key={course.id}
                                    course={course}
                                    favourites={favourites}
                                    setFavourites={setFavourites}
                                />
                            ))
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}
