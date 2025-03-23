import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Enrollment } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface EnrollmentResponse {
    message: string;
    data: Enrollment;
}

export const useEnrollment = (id: number) => {
    return useQuery<EnrollmentResponse, AxiosError<EnrollmentResponse>>({
        queryFn: () => axiosInstance.get(`/enrollments/${id}`).then((res) => res.data),
        queryKey: ["enrollment", id],
    });
};