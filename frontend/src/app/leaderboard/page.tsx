"use client";

import { useEffect, useRef, useState } from "react";
import Navbar from "@/components/Navbar";
import TopCard from "@/components/leaderboard/TopCard";
import { useApi } from "@/hooks/useApi";
import { LeaderboardEntry } from "@/types/dbTypes";

export default function LeaderboardPage() {
    const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
    const { isLoading, isError, message, fetchData, data } = useApi<
        LeaderboardEntry[],
        void
    >("leaderboard", { method: "GET" });
    const hasFetched = useRef(false);

    useEffect(() => {
        if (!hasFetched.current) {
            fetchData();
            hasFetched.current = true;
        }
    }, []);

    useEffect(() => {
        if (data) {
            setLeaderboard(data);
        }
    }, [data]);

    return (
        <div className="flex min-h-screen flex-col bg-gray-100">
            <div className="m-4">
                <Navbar />
            </div>
            <div className="m-4 flex grow flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
                <h1 className="text-center font-bold md:text-2xl">
                    Leaderboard
                </h1>
                {isLoading ? (
                    <div className="text-center">Loading...</div>
                ) : isError ? (
                    <div className="text-center text-red-500">
                        Error loading leaderboard: {message}
                    </div>
                ) : (
                    <>
                        <div className="flex justify-around gap-4">
                            {leaderboard.slice(0, 3).map((entry, index) => (
                                <TopCard
                                    key={index + 1}
                                    rank={index + 1}
                                    name={entry.user.username}
                                    score={entry.points}
                                />
                            ))}
                        </div>
                        <table className="mt-4 w-full border-collapse">
                            <thead>
                                <tr>
                                    <th className="border-b border-gray-200 px-4 py-2 text-left">
                                        Rank
                                    </th>
                                    <th className="border-b border-gray-200 px-4 py-2 text-left">
                                        Name
                                    </th>
                                    <th className="border-b border-gray-200 px-4 py-2 text-left">
                                        Score
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                {leaderboard.slice(3).map((entry, index) => (
                                    <tr key={index + 3}>
                                        <td className="border-b border-gray-200 px-4 py-2">
                                            {index + 3}
                                        </td>
                                        <td className="border-b border-gray-200 px-4 py-2">
                                            {entry.user.username}
                                        </td>
                                        <td className="border-b border-gray-200 px-4 py-2">
                                            {entry.points}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </>
                )}
            </div>
        </div>
    );
}
