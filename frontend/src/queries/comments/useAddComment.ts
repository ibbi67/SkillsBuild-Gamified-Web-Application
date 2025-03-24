import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Comment } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface AddCommentRequest {
    content: string;
    courseId: number;
}

interface AddCommentResponse {
    message: string;
    data: Comment;
}

export const useAddComment = () => {
    return useMutation<AddCommentResponse, AxiosError<AddCommentResponse>, AddCommentRequest>({
        mutationFn: (commentData) => axiosInstance.post("/comments", commentData).then((res) => res.data),
    });
};