import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Enrollment } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface EnrollmentsResponse {
    message: string;
    data: Enrollment[];
}

export const useEnrollments = () => {
    return useQuery<EnrollmentsResponse, AxiosError<EnrollmentsResponse>>({
        queryFn: () => axiosInstance.get("/enrollments").then((res) => res.data),
        queryKey: ["enrollments"],
    });
};