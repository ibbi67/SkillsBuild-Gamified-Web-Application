import React, { useState } from "react";
import { useComments } from "@/queries/comments/useComments";
import { useAddComment } from "@/queries/comments/useAddComment";
import { useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

interface CommentSectionProps {
    courseId: number;
}

const CommentSection: React.FC<CommentSectionProps> = ({ courseId }) => {
    const [newComment, setNewComment] = useState("");
    const queryClient = useQueryClient();

    const { data: commentsData, isLoading, isError } = useComments(courseId);

    const { mutate: addComment, isPending: isAddingComment } = useAddComment();

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (newComment.trim()) {
            addComment(
                {
                    content: newComment,
                    courseId: courseId,
                },
                {
                    onSuccess: () => {
                        setNewComment("");
                        queryClient.invalidateQueries({ queryKey: ["comments", courseId] });
                        toast.success("Comment added successfully!");
                    },
                    onError: (error) => {
                        toast.error(`Error adding comment: ${error.message}`);
                    },
                }
            );
        }
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleString();
    };

    if (isLoading) return <p>Loading comments...</p>;
    if (isError) return <p>Error loading comments.</p>;

    return (
        <div className="mt-6">
            <h3 className="text-xl font-bold mb-4">Comments</h3>

            <div className="space-y-4 mb-6">
                {commentsData?.data.length === 0 ? (
                    <p className="text-gray-500">No comments yet. Be the first to comment!</p>
                ) : (
                    commentsData?.data.map((comment) => (
                        <div key={comment.id} className="bg-gray-50 p-4 rounded-lg">
                            <div className="flex justify-between mb-2">
                                <span className="font-semibold">{comment.person.username}</span>
                                <span className="text-sm text-gray-500">
                                    {formatDate(comment.createdAt)}
                                </span>
                            </div>
                            <p>{comment.content}</p>
                        </div>
                    ))
                )}
            </div>

            <form onSubmit={handleSubmit} className="mt-4">
                <textarea
                    className="w-full p-2 border rounded-lg"
                    rows={3}
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="Write a comment..."
                ></textarea>
                <button
                    type="submit"
                    disabled={isAddingComment || !newComment.trim()}
                    className={`mt-2 px-4 py-2 rounded-lg ${
                        isAddingComment || !newComment.trim()
                            ? "bg-gray-300 cursor-not-allowed"
                            : "bg-blue-500 text-white hover:bg-blue-600"
                    }`}
                >
                    {isAddingComment ? "Posting..." : "Post Comment"}
                </button>
            </form>
        </div>
    );
};

export default CommentSection;