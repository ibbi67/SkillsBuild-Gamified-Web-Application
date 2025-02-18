"use client";

import React, { useEffect, useRef, useState } from "react";
import { Heart } from "lucide-react";
import { useApi } from "@/hooks/useApi";
import { Course } from "@/types/course";
import { FavouriteRequest } from "@/types/favourite";

const CourseList: React.FC = () => {
    const [courses, setCourses] = useState<Course[]>([]);
    const [favourites, setFavourites] = useState<Course[]>([]);
    const [courseId, setCourseId] = useState<number>(-1);
    const hasFetched = useRef(false);

    const {
        isLoading: isFetchingCourses,
        isError: isFetchCoursesError,
        fetchData: fetchCourses,
        data: fetchedCourses,
    } = useApi<Course[], void>("courses", {
        method: "GET",
    });

    const {
        isLoading: isFetchingFavourites,
        isError: isFetchfavouritesError,
        fetchData: fetchfavourites,
        data: fetchedfavourites,
    } = useApi<Course[], void>("favourite", { method: "GET" });

    const {
        isLoading: isAddingFavourite,
        isError: isAddFavouriteError,
        fetchData: addFavourite,
    } = useApi<void, FavouriteRequest>("favourite/add", {
        method: "POST",
        data: { courseId },
    });

    const {
        isLoading: isRemovingFavourite,
        isError: isRemoveFavouriteError,
        fetchData: removeFavourite,
    } = useApi<void, FavouriteRequest>("favourite/remove", {
        method: "DELETE",
        data: { courseId },
    });

    useEffect(() => {
        if (!hasFetched.current) {
            hasFetched.current = true;
            fetchCourses();
            fetchfavourites();
        }
    }, []);

    useEffect(() => {
        if (fetchedCourses) {
            setCourses(fetchedCourses);
        }
    }, [fetchedCourses]);

    useEffect(() => {
        if (fetchedfavourites) {
            setFavourites(fetchedfavourites);
        }
    }, [fetchedfavourites]);

    const toggleFavourite = async (course: Course) => {
        const isFavourite = favourites.some((fav) => fav.id === course.id);
        setCourseId(course.id);

        if (isFavourite) {
            await removeFavourite();
            setFavourites(favourites.filter((fav) => fav.id !== course.id));
        } else {
            await addFavourite();
            setFavourites([...favourites, course]);
        }
    };

    return (
        <div className="flex w-full flex-col space-y-6 p-6">
            <h2 className="text-2xl font-bold text-blue-600">
                Available Courses
            </h2>

            <div className="flex flex-col items-start space-y-4">
                {courses.length === 0 ? (
                    <p>No courses available at the moment.</p>
                ) : (
                    courses.map((course) => (
                        <div
                            key={course.id}
                            className="flex h-16 w-64 cursor-pointer items-center justify-between rounded-lg bg-blue-500 px-4 font-semibold text-white shadow-lg transition hover:bg-blue-600"
                        >
                            <button
                                className="flex-1 text-left"
                                onClick={() =>
                                    window.open(
                                        course.link,
                                        "_blank",
                                        "noopener,noreferrer"
                                    )
                                }
                            >
                                {course.title}
                            </button>

                            <button onClick={() => toggleFavourite(course)}>
                                <Heart
                                    className={`h-5 w-5 transition ${
                                        favourites.some(
                                            (fav) => fav.id === course.id
                                        )
                                            ? "fill-red-500 text-red-500"
                                            : "text-white"
                                    }`}
                                />
                            </button>
                        </div>
                    ))
                )}
            </div>

            <div className="mt-6">
                <a
                    href="/favourites"
                    className="text-blue-600 hover:text-blue-800"
                >
                    View Favourite Courses
                </a>
            </div>
        </div>
    );
};

export default CourseList;
