import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Goal } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface GoalsResponse {
    message: string;
    data: Goal[];
}

export const useGoals = () => {
    return useQuery<GoalsResponse, AxiosError<GoalsResponse>>({
        queryFn: () => axiosInstance.get("/goals").then((res) => res.data),
        queryKey: ["goals"],
    });
};