interface CoursePageButtonProps {
    setActiveTab: () => void
    isActive: boolean;
    text: string;
}

export default function CoursePageButton({ setActiveTab, isActive, text }: CoursePageButtonProps) {
    return (
            <button
                className={`rounded-lg px-4 py-2 cursor-pointer ${isActive ? "bg-blue-500 text-white" : "bg-gray-200"}`}
                onClick={() => setActiveTab()}
            >
                {text}
            </button>
    );
}