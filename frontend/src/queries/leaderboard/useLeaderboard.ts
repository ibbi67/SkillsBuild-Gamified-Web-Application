import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface LeaderboardDTO {
    username: string;
    score: number;
}

interface LeaderboardResponse {
    message: string;
    data: LeaderboardDTO[];
}

export const useLeaderboard = () => {
    return useQuery<LeaderboardResponse, AxiosError<LeaderboardResponse>>({
        queryFn: () => axiosInstance.get("/leaderboard").then((res) => res.data),
        queryKey: ["leaderboard"],
    });
};