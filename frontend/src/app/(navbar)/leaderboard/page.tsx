"use client";

import TopCard from "@/component/leaderboard/TopCard";
import { useLeaderboard } from "@/queries/leaderboard/useLeaderboard";
import { useEffect } from "react";
import toast from "react-hot-toast";

export default function LeaderboardPage() {
    const { isLoading, isError, data, error } = useLeaderboard();

    useEffect(() => {
        if (isError) {
            toast.error(error?.response?.data?.message || "Error fetching leaderboard data");
        }
    }, [isError, error]);

    return (
        <div className="m-4 flex grow flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
            <h1 className="text-center font-bold md:text-2xl">
                Leaderboard
            </h1>
            {isLoading ? (
                <div className="text-center">Loading...</div>
            ) : isError ? (
                <div className="text-center text-red-500">
                    Error loading leaderboard
                </div>
            ) : (
                <>
                    <div className="flex justify-around gap-4">
                        {data?.data.slice(0, 3).map((entry, index) => (
                            <TopCard
                                key={index + 1}
                                rank={index + 1}
                                name={entry.username}
                                score={entry.score}
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
                            {data?.data.slice(3).map((entry, index) => (
                                <tr key={index + 3}>
                                    <td className="border-b border-gray-200 px-4 py-2">
                                        {index + 3}
                                    </td>
                                    <td className="border-b border-gray-200 px-4 py-2">
                                        {entry.username}
                                    </td>
                                    <td className="border-b border-gray-200 px-4 py-2">
                                        {entry.score}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </>
            )}
        </div>
    );
}
