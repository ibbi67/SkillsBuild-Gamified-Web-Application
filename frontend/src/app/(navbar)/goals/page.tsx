"use client";

import { GoalsTab } from "@/component/goals/GoalsTab";

export default function GoalsPage() {
    return (
        <div className="m-4 mx-auto w-4/5 rounded-lg bg-white p-4 shadow-lg">
            <h1 className="text-left font-bold md:text-2xl mb-4">
                Your Learning Goals
            </h1>
            <GoalsTab />
        </div>
    );
}