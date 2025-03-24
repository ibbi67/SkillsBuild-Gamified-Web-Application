"use client";

import { useEffect, useRef } from "react";
import CourseCard from "@/component/course/CourseCard";
import { useMe } from "@/queries/auth/useMe";
import { useStreak } from "@/queries/streak/useStreak";
import { useFavourites } from "@/queries/favourites/useFavourites";
import toast from "react-hot-toast";
import ProfileUpdateForm from "@/component/profile/ProfileUpdateForm";

export default function DashboardPage() {
    const { data: user, isLoading: isLoadingUser, isError: isErrorUser, error: errorUser } = useMe();
    const { data: streak, isLoading: isLoadingStreak, isError: isErrorStreak, error: errorStreak } = useStreak();
    const { data: favouriteCourses, isLoading: isLoadingFavourites, isError: isErrorFavourites, error: errorFavourites } = useFavourites();

    const hasFetched = useRef(false);

    useEffect(() => {
        if (!hasFetched.current) {
            hasFetched.current = true;
        }
    }, []);

    useEffect(() => {
        if (isErrorUser) {
            toast.error(errorUser?.response?.data?.message || "Error fetching user data");
        }
        if (isErrorStreak) {
            toast.error(errorStreak?.response?.data?.message || "Error fetching streak data");
        }
        if (isErrorFavourites) {
            toast.error(errorFavourites?.response?.data?.message || "Error fetching favourite courses");
        }
    }, [isErrorUser, isErrorStreak, isErrorFavourites, errorUser, errorStreak, errorFavourites]);

    if (isLoadingFavourites || isLoadingStreak || isLoadingUser) {
        return <div>Loading...</div>;
    }

    return (
        <div className="m-4 mx-auto w-4/5 rounded-lg bg-white p-4 shadow-lg">
            <h1 className="text-left font-bold md:text-2xl">
                Welcome Back {user?.data.username}
            </h1>
            <div className="mt-4 grid gap-4 md:grid-cols-2 lg:grid-cols-1">
                <div className="rounded-lg border p-4 shadow">
                    <h2 className="mb-2 font-bold">Your Streak</h2>
                    <div className="flex items-center justify-center gap-2">
                        <p className="text-xl font-bold text-green-500">
                            {streak?.data}
                        </p>
                        <p className="text-xl font-bold">Days</p>
                    </div>
                </div>
                <ProfileUpdateForm />
                <div className="rounded-lg border p-4 shadow">
                    <h2 className="mb-2 font-bold">
                        Your Favourite Courses
                    </h2>
                    <div className="grid grid-cols-2 gap-2">
                        {favouriteCourses?.data.length === 0 ? (
                            <p>No favourite courses yet.</p>
                        ) : (
                            favouriteCourses?.data.map((course) => <CourseCard key={course.id} course={course} />)
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}