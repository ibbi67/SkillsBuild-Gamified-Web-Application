import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";
import { Person } from "@/types/databaseTypes";

interface MeResponse {
    message: string;
    data: Person;
}

export const useMe = () => {
    return useQuery<MeResponse, AxiosError<MeResponse>>({
        queryFn: () => axiosInstance.get("/auth/me").then((res) => res.data),
        queryKey: ["me"],
    });
};