import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Course } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface CoursesResponse {
    message: string;
    data: Course[];
}

export const useCourses = () => {
    return useQuery<CoursesResponse, AxiosError<CoursesResponse>>({
        queryFn: () => axiosInstance.get("/courses").then((res) => res.data),
        queryKey: ["courses"],
    });
};