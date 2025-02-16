"use client";

import Navbar from "@/components/Navbar";

export default function Home() {
    return (
        <div className="flex flex-col">
            <div className="m-4 grow">
                <Navbar />
            </div>
            <div className="mx-auto m-4 w-4/5 rounded-lg bg-white p-4 shadow-lg">
                <h1 className="text-left font-bold md:text-2xl">Welcome Back (enter user name here)</h1>
                {/* Add your dashboard content here */}
                <div className="mt-4 grid gap-4 md:grid-cols-2 lg:grid-cols-1">
                    <div className="rounded-lg border p-4 shadow">
                        <h2 className="mb-2 font-bold">Your Streak</h2>
                        <div className="flex items-center justify-center gap-2">
                            <p className="text-xl font-bold text-green-500">5</p>
                            <p className="text-xl font-bold">Days</p>
                        </div>    
                    </div>
                    {/* Example dashboard cards */}
                    <div className="rounded-lg border p-4 shadow">
                        <h2 className="mb-2 font-bold">Statistics</h2>
                        {/* Add statistics content */}
                    </div>
                    <div className="rounded-lg border p-4 shadow">
                        <h2 className="mb-2 font-bold">Recent Activity</h2>
                        {/* Add activity content */}
                    </div>
                    <div className="rounded-lg border p-4 shadow">
                        <h2 className="mb-2 font-bold">Quick Actions</h2>
                        {/* Add action buttons */}
                    </div>
                </div>
            </div>
        </div>
    );
}

