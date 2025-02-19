"use client";

import { useApi } from "@/hooks/useApi";
import { Course } from "@/types/course";
import { useEffect, useRef, useState } from "react";
import Navbar from "@/components/Navbar";
import CourseCard from "@/components/CourseCard";

export default function CoursePage() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [favourites, setFavourites] = useState<Course[]>([]);
    const [recommendedCourses, setRecommendedCourses] = useState<Course[]>([]);
    const [activeTab, setActiveTab] = useState("all");
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

    const {
        isLoading: isFetchingRecommended,
        isError: isFetchRecommendedError,
        message: fetchRecommendedCoursesMessage,
        fetchData: fetchRecommendedCourses,
        data: fetchedRecommendedCourses,
    } = useApi<Course[], void>("courses/recommend", { method: "GET" });

    useEffect(() => {
        if (!hasFetched.current) {
            hasFetched.current = true;
            fetchCourses();
            fetchFavourites();
            fetchRecommendedCourses();
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

    useEffect(() => {
        if (fetchedRecommendedCourses) {
            setRecommendedCourses(fetchedRecommendedCourses);
        }
    }, [fetchedRecommendedCourses]);

    return (
        <div className="flex flex-col">
            <div className="m-4 grow">
                <Navbar />
            </div>
            <div className="m-4 flex grow flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
                <div className="flex gap-4">
                    <button
                        className={`rounded-lg px-4 py-2 ${
                            activeTab === "all"
                                ? "bg-blue-500 text-white"
                                : "bg-gray-200"
                        }`}
                        onClick={() => setActiveTab("all")}
                    >
                        All Courses
                    </button>
                    <button
                        className={`rounded-lg px-4 py-2 ${
                            activeTab === "favourites"
                                ? "bg-blue-500 text-white"
                                : "bg-gray-200"
                        }`}
                        onClick={() => setActiveTab("favourites")}
                    >
                        Favourite Courses
                    </button>
                    <button
                        className={`rounded-lg px-4 py-2 ${
                            activeTab === "recommended"
                                ? "bg-blue-500 text-white"
                                : "bg-gray-200"
                        }`}
                        onClick={() => setActiveTab("recommended")}
                    >
                        Recommended Courses
                    </button>
                </div>

                {activeTab === "all" && (
                    <div className="grow flex-col gap-4 rounded-lg bg-white">
                        {isFetchingCourses ? (
                            <p>Loading courses...</p>
                        ) : isFetchCoursesError ? (
                            <p>Error fetching courses.</p>
                        ) : (
                            <div className="grid grid-cols-2 gap-2">
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
                )}

                {activeTab === "favourites" && (
                    <div className="grow flex-col gap-4 rounded-lg bg-white">
                        {isFetchingFavourites ? (
                            <p>Loading favourite courses...</p>
                        ) : isFetchFavouritesError ? (
                            <p>Error fetching favourite courses.</p>
                        ) : (
                            <div className="grid grid-cols-2 gap-2">
                                {favourites.length === 0 ? (
                                    <p>
                                        No favourite courses available at the
                                        moment.
                                    </p>
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
                )}

                {activeTab === "recommended" && (
                    <div className="grow flex-col gap-4 rounded-lg bg-white">
                        {isFetchingRecommended ? (
                            <p>Loading recommended courses...</p>
                        ) : isFetchRecommendedError ? (
                            fetchRecommendedCoursesMessage ===
                            "No enrollments found" ? (
                                <p>No enrollments found.</p>
                            ) : (
                                <p>Error fetching recommended courses.</p>
                            )
                        ) : (
                            <div className="grid grid-cols-2 gap-2">
                                {recommendedCourses.length === 0 ? (
                                    <p>
                                        No recommended courses available at the
                                        moment.
                                    </p>
                                ) : (
                                    recommendedCourses.map((course) => (
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
                )}
            </div>
        </div>
    );
}
