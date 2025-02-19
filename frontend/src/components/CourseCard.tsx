import { useState, useEffect } from "react";
import { Course } from "@/types/course";
import { Heart } from "lucide-react";
import { useApi } from "@/hooks/useApi";
import { FavouriteRequest } from "@/types/favourite";
import toast, { Toaster } from "react-hot-toast";

interface CourseCardProps {
    course: Course;
    favourites: Course[];
    setFavourites: (favourites: Course[]) => void;
}

export default function CourseCard({
    course,
    favourites,
    setFavourites,
}: CourseCardProps) {
    const [isFavourite, setIsFavourite] = useState(false);
    const [isExpanded, setIsExpanded] = useState(false);

    const {
        isLoading: isAddingFavourite,
        isError: isAddFavouriteError,
        message: addMessage,
        fetchData: addFavourite,
    } = useApi<void, FavouriteRequest>("favourite/add", {
        method: "POST",
    });

    const {
        isLoading: isRemovingFavourite,
        isError: isRemoveFavouriteError,
        message: removeMessage,
        fetchData: removeFavourite,
    } = useApi<void, FavouriteRequest>("favourite/remove", {
        method: "DELETE",
    });

    useEffect(() => {
        setIsFavourite(favourites.some((fav) => fav.id === course.id));
    }, [favourites, course.id]);

    useEffect(() => {
        if (isAddFavouriteError) {
            toast.error(addMessage);
        }
        if (isRemoveFavouriteError) {
            toast.error(removeMessage);
        }
    }, [
        isAddFavouriteError,
        isRemoveFavouriteError,
        addMessage,
        removeMessage,
    ]);

    const toggleFavourite = async () => {
        if (isFavourite) {
            await removeFavourite({ courseId: course.id });
            setFavourites(favourites.filter((fav) => fav.id !== course.id));
        } else {
            await addFavourite({ courseId: course.id });
            setFavourites([...favourites, course]);
        }
    };

    const toggleDescription = () => {
        setIsExpanded(!isExpanded);
    };

    const renderDescription = () => {
        const lines = course.description.split("\n");
        if (isExpanded) {
            return lines.map((line, index) => <p key={index}>{line}</p>);
        } else {
            return lines
                .slice(0, 3)
                .map((line, index) => <p key={index}>{line}</p>);
        }
    };

    const openCourseLink = () => {
        window.open(course.link, "_blank", "noopener,noreferrer");
    };

    return (
        <div className="flex cursor-pointer flex-col overflow-hidden rounded-lg border border-blue-500 shadow-lg transition hover:border-blue-600 hover:bg-blue-50">
            <h3 className="bg-blue-500 p-4 text-lg font-bold text-white">
                {course.title}
            </h3>
            <div className="p-4 text-sm">{renderDescription()}</div>

            <button
                onClick={toggleDescription}
                className="mr-4 self-end text-blue-500 hover:underline"
            >
                {isExpanded ? "Read Less" : "Read More"}
            </button>

            <div className="align-center flex gap-4 p-4 text-white">
                <button
                    onClick={openCourseLink}
                    className="rounded bg-blue-500 px-4 py-2"
                >
                    Go to Course
                </button>

                <button
                    onClick={toggleFavourite}
                    className="align-center flex items-center gap-2 rounded px-4 py-2 text-blue-500 outline outline-blue-500"
                    disabled={isAddingFavourite || isRemovingFavourite}
                >
                    {isFavourite ? "Remove from Favorites" : "Add to Favorites"}
                    <Heart
                        className={`h-5 w-5 transition ${
                            isFavourite
                                ? "fill-red-500 text-red-500"
                                : "text-blue-500"
                        }`}
                    />
                </button>
            </div>
            <Toaster />
        </div>
    );
}
