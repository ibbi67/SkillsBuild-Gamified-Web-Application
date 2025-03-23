import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Course } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface RecommendedCoursesResponse {
    message: string;
    data: Course[];
}

export const useRecommendedCourses = () => {
    return useQuery<RecommendedCoursesResponse, AxiosError<RecommendedCoursesResponse>>({
        queryFn: () => axiosInstance.get("/courses/recommend").then((res) => res.data),
        queryKey: ["recommendedCourses"],
    });
};
