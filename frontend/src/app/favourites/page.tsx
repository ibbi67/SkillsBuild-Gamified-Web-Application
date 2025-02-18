"use client";

import { Course } from "@/types/course";
import { useEffect, useState, useRef } from "react";
import { useApi } from "@/hooks/useApi";
import Link from "next/link";

const FavoritesPage: React.FC = () => {
    const [favourites, setFavourites] = useState<Course[]>([]);
    const hasFetched = useRef(false);

    const {
        isLoading: isFetching,
        isError: isFetchError,
        message: fetchMessage,
        fetchData: fetchFavorites,
        data: fetchedFavorites,
    } = useApi<Course[], void>("favourite", { method: "GET" });

    useEffect(() => {
        if (!hasFetched.current) {
            hasFetched.current = true;
            fetchFavorites();
        }
    }, []);

    useEffect(() => {
        if (fetchedFavorites) {
            setFavourites(fetchedFavorites);
        }
    }, [fetchedFavorites]);

    return (
        <div className="flex min-h-screen items-center justify-center bg-blue-50 p-6">
            <div className="w-full max-w-2xl rounded-2xl bg-white p-6 shadow-lg">
                <h2 className="mb-6 text-center text-3xl font-bold text-blue-600">
                    Your Favorite Courses
                </h2>

                {favourites.length === 0 ? (
                    <p className="text-center text-gray-500">
                        No favorite courses found.
                    </p>
                ) : (
                    <ul className="space-y-3">
                        {favourites.map((course) => (
                            <li
                                key={course.id}
                                className="flex items-center justify-between rounded-lg bg-blue-100 p-4 shadow-sm transition hover:bg-blue-200"
                            >
                                <a
                                    href={course.link}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="font-medium text-blue-700 hover:underline"
                                >
                                    {course.title}
                                </a>
                            </li>
                        ))}
                    </ul>
                )}

                <div className="mt-6 text-center">
                    <Link
                        href="/course"
                        className="inline-block rounded-lg bg-blue-500 px-6 py-3 font-medium text-white shadow-md transition hover:bg-blue-600"
                    >
                        ← Back to Courses
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default FavoritesPage;
