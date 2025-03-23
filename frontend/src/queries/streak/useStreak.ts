import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface StreakResponse {
    message: string;
    data: number;
}

export const useStreak = () => {
    return useQuery<StreakResponse, AxiosError<StreakResponse>>({
        queryFn: () => axiosInstance.get("/streak").then((res) => res.data),
        queryKey: ["streak"],
    });
};