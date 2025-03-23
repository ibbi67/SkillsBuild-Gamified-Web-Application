import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Course } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface CourseResponse {
    message: string;
    data: Course;
}

export const useCourse = (id: number) => {
    return useQuery<CourseResponse, AxiosError<CourseResponse>>({
        queryFn: () => axiosInstance.get(`/courses/${id}`).then((res) => res.data),
        queryKey: ["course", id],
    });
};