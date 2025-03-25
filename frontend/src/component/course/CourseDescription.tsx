import { useState } from "react";

interface CourseDescriptionProps {
    description: string;
}

export function CourseDescription({ description }: CourseDescriptionProps) {
    const [isExpanded, setIsExpanded] = useState(false);

    const displayText = isExpanded
        ? description
        : description.split("\n").slice(0, 3).join("\n");

    return (
        <div className="flex-grow p-4">
            <div
                dangerouslySetInnerHTML={{
                    __html: displayText.replace(/\n/g, "<br />"),
                }}
                className={
                    "text-gray-600 " +
                    (isExpanded ? "line-clamp-none" : "line-clamp-3")
                }
            />
            {description.split("\n").length > 3 && (
                <button
                    onClick={() => setIsExpanded(!isExpanded)}
                    className="ml-2 text-blue-500 hover:text-blue-600 focus:outline-none"
                >
                    {isExpanded ? "Show less" : "Read more"}
                </button>
            )}
        </div>
    );
}

