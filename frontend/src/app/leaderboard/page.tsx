"use client";

import { useEffect, useState } from "react";
import Navbar from "@/components/Navbar";
import TopCard from "@/components/leaderboard/TopCard";

type LeaderboardEntry = {
    rank: number;
    name: string;
    score: number;
};

const fakeLeaderboardData: LeaderboardEntry[] = [
    { rank: 1, name: "Justin Fung", score: 100 },
    { rank: 2, name: "Jane Doe", score: 95 },
    { rank: 3, name: "John Smith", score: 90 },
    { rank: 4, name: "Alice Johnson", score: 85 },
    { rank: 5, name: "Bob Brown", score: 80 },
];

export default function LeaderboardPage() {
    const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isError, setIsError] = useState(false);

    useEffect(() => {
        try {
            setLeaderboard(fakeLeaderboardData);
            setIsLoading(false);
        } catch (error) {
            console.error(error);
            setIsError(true);
            setIsLoading(false);
        }
    }, []);

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
                        Error loading leaderboard
                    </div>
                ) : (
                    <>
                        <div className="flex justify-around gap-4">
                            {leaderboard.slice(0, 3).map((entry) => (
                                <TopCard
                                    key={entry.rank}
                                    rank={entry.rank}
                                    name={entry.name}
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
                                {leaderboard.slice(3).map((entry) => (
                                    <tr key={entry.rank}>
                                        <td className="border-b border-gray-200 px-4 py-2">
                                            {entry.rank}
                                        </td>
                                        <td className="border-b border-gray-200 px-4 py-2">
                                            {entry.name}
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
        </div>
    );
}
