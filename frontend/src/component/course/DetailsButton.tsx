"use client";

import { useRouter } from "next/navigation";

interface DefailsButtonProps {
    courseId: number;
}

export default function DetailsButton({ courseId }: DefailsButtonProps) {
    const router = useRouter();

    return <button
        onClick={() => router.push(`/course/${courseId}`)}
        className="rounded bg-purple-500 px-4 py-2 cursor-pointer text-white"
    >
        View Details
    </button>
}