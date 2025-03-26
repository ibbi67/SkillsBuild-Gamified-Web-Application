"use client";

import { useEffect, useRef } from "react";
import CourseCard from "@/component/course/CourseCard";
import { useMe } from "@/queries/auth/useMe";
import { useStreak } from "@/queries/streak/useStreak";
import { useFavourites } from "@/queries/favourites/useFavourites";
import { useGoalDashboard } from "@/queries/goals/useGoalDashboard";
import toast from "react-hot-toast";
import ProfileUpdateForm from "@/component/profile/ProfileUpdateForm";
import { EnrolledCoursesSection } from "@/component/dashboard/EnrolledCoursesSection";
import { BadgeSection } from "@/component/badge/BadgeSection";

export default function DashboardPage() {
    const { data: user, isLoading: isLoadingUser, isError: isErrorUser, error: errorUser } = useMe();
    const { data: streak, isLoading: isLoadingStreak, isError: isErrorStreak, error: errorStreak } = useStreak();
    const { data: favouriteCourses, isLoading: isLoadingFavourites, isError: isErrorFavourites, error: errorFavourites } = useFavourites();
    const { data: goals, isLoading: isLoadingGoals, isError: isErrorGoals, error: errorGoals } = useGoalDashboard();

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
        if (isErrorGoals) {
            toast.error(errorGoals?.response?.data?.message || "Error fetching goals data");
        }
    }, [isErrorUser, isErrorStreak, isErrorFavourites, isErrorGoals, errorUser, errorStreak, errorFavourites, errorGoals]);

    if (isLoadingFavourites || isLoadingStreak || isLoadingUser || isLoadingGoals) {
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
                <EnrolledCoursesSection />
                <BadgeSection />
            </div>
        </div>
    );
}