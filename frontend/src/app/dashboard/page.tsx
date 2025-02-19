"use client";

import { useApi } from "@/hooks/useApi";
import { MeRequest } from "@/types/apiCall";
import { useEffect, useRef } from "react";
import Navbar from "@/components/Navbar";
import CourseCard from "@/components/course/CourseCard";
import { User } from "@/types/dbTypes";

export default function DashboardPage() {
    const { isLoading, isError, message, fetchData, data } = useApi<
        User,
        MeRequest
    >("auth/me", { method: "GET" });

    const hasFetched = useRef(false);

    useEffect(() => {
        if (!hasFetched.current) {
            hasFetched.current = true;
            fetchData();
        }
    }, [fetchData]);

    return (
        <div className="flex flex-col">
            <div className="m-4 grow">
                <Navbar />
            </div>
            <div className="m-4 mx-auto w-4/5 rounded-lg bg-white p-4 shadow-lg">
                <h1 className="text-left font-bold md:text-2xl">
                    Welcome Back {data?.username}
                </h1>
                {isLoading ? (
                    <div>Loading...</div>
                ) : isError ? (
                    <div>{message}</div>
                ) : (
                    <div className="mt-4 grid gap-4 md:grid-cols-2 lg:grid-cols-1">
                        <div className="rounded-lg border p-4 shadow">
                            <h2 className="mb-2 font-bold">Your Streak</h2>
                            <div className="flex items-center justify-center gap-2">
                                <p className="text-xl font-bold text-green-500">
                                    {data?.streak.streak}
                                </p>
                                <p className="text-xl font-bold">Days</p>
                            </div>
                        </div>
                        <div className="rounded-lg border p-4 shadow">
                            <h2 className="mb-2 font-bold">Profile</h2>
                            <div className="flex flex-col gap-2">
                                <div>Id: {data?.id}</div>
                                <div>Username: {data?.username}</div>
                                {data?.roles && data?.roles.length > 0 && (
                                    <>
                                        <div>Roles:</div>
                                        {data?.roles.map((role) => (
                                            <div key={role}>{role}</div>
                                        ))}
                                    </>
                                )}
                                {data?.authorities &&
                                    data?.authorities.length > 0 && (
                                        <>
                                            <div>Authorities:</div>
                                            {data?.authorities.map(
                                                (authority) => (
                                                    <div key={authority}>
                                                        {authority}
                                                    </div>
                                                )
                                            )}
                                        </>
                                    )}
                            </div>
                        </div>
                        <div className="rounded-lg border p-4 shadow">
                            <h2 className="mb-2 font-bold">
                                Your Favourite Courses
                            </h2>
                            <div className="grid grid-cols-2 gap-2">
                                {data?.favouriteCourses.length === 0 ? (
                                    <p>No favourite courses yet.</p>
                                ) : (
                                    data?.favouriteCourses.map((course) => (
                                        <CourseCard
                                            key={course.id}
                                            course={course}
                                            favourites={data?.favouriteCourses}
                                            setFavourites={() => {}}
                                        />
                                    ))
                                )}
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
