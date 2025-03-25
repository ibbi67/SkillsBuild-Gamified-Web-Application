import { useState } from "react";
import { useCreateEnrollment } from "@/queries";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "react-hot-toast";

interface EnrollButtonProps {
    courseId: number;
    isEnrolled: boolean | undefined;
    disabled: boolean;
}

export function EnrollButton({ courseId, isEnrolled, disabled }: EnrollButtonProps) {
    const [isLoading, setIsLoading] = useState(false);
    const queryClient = useQueryClient();
    const enrollMutation = useCreateEnrollment();

    const handleEnroll = async () => {
        setIsLoading(true);
        try {
            await enrollMutation.mutateAsync({ courseId });
            await queryClient.invalidateQueries({ queryKey: ["enrollments"] });
            toast.success("Successfully enrolled in course");
        } catch (error) {
            toast.error(error instanceof Error ? error.message : "Failed to enroll in course");
        } finally {
            setIsLoading(false);
        }
    };

    if (isEnrolled) {
        return null;
    }

    return (
        <button
            onClick={handleEnroll}
            disabled={disabled || isLoading}
            className={`px-4 py-2 rounded-lg font-medium transition-colors
                ${isLoading ? "bg-gray-300 cursor-not-allowed" : "bg-green-500 text-white hover:bg-green-600"}
                ${disabled ? "opacity-50 cursor-not-allowed" : ""}`}
        >
            {isLoading ? (
                <span className="flex items-center gap-2">
                    <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"/>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                    </svg>
                    Enrolling...
                </span>
            ) : (
                "Enroll"
            )}
        </button>
    );
}