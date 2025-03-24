import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Comment } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface CommentsResponse {
    message: string;
    data: Comment[];
}

export const useComments = (courseId: number) => {
    return useQuery<CommentsResponse, AxiosError<CommentsResponse>>({
        queryFn: () => axiosInstance.get(`/comments/course/${courseId}`).then((res) => res.data),
        queryKey: ["comments", courseId],
    });
};