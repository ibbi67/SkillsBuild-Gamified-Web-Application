"use client";

import { useState } from "react";
import { AllCoursesTab } from "@/component/course/AllCoursesTab";
import { FavouriteCoursesTab } from "@/component/course/FavouriteCoursesTab";
import { RecommendedCoursesTab } from "@/component/course/RecommendedCoursesTab";
import CoursePageButton from "@/component/course/CoursePageButton";
import { useMe } from "@/queries";

export default function CoursePage() {
    const [activeTab, setActiveTab] = useState("all");
    const [query, setQuery] = useState("");

    const { data: me } = useMe();

    return (
        <div className="m-4 flex grow flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
            <div className="flex gap-4">
                <CoursePageButton setActiveTab={() => setActiveTab("all")} isActive={activeTab === "all"} text="All Courses" />
                {me && <>
                    <CoursePageButton setActiveTab={() => setActiveTab("favourites")} isActive={activeTab === "favourites"} text="Favourite Courses" />
                    <CoursePageButton setActiveTab={() => setActiveTab("recommended")} isActive={activeTab === "recommended"} text="Recommended Courses" />
                </>}
                <div className="grow" />
                <input
                    type="text"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="Search courses..."
                    className="w-64 rounded border px-3 py-2"
                />
            </div>

            {activeTab === "all" && <AllCoursesTab query={query} />}
            {activeTab === "favourites" && <FavouriteCoursesTab />}
            {activeTab === "recommended" && <RecommendedCoursesTab />}
        </div>
    );
}
